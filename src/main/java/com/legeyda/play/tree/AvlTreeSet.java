package com.legeyda.play.tree;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class AvlTreeSet<T extends Comparable<T>> extends AbstractSet<T> {

	private class TreeNode {
		public T value;
		public TreeNode left;
		public TreeNode right;
		public int height = 0;

		public TreeNode(T value, TreeNode left, TreeNode right) {
			this.value = value;
			this.left = left;
			this.right = right;
		}

		@Override
		public String toString() {
			return "TreeNode{" + (value!=null ? value.toString() + '}' : "null}");
		}
	}


	/**
	 * абстрактный элемент для печати дерева:
	 * может быть либо нодой исходного дерева, либо пробельными промежутками между ними
	 */
	private abstract static class PrintNode implements BiFunction<Consumer<String>, Consumer<PrintNode>, Boolean> {
		/** печатает в printer текущую строку и заполняет очередь следующего уровня
		 *  (как обход дерева в ширину)
		 *  @return true если это нода и у неё есть потомки, false - если просто промежуток пробелов
		 */
		@Override
		abstract public Boolean apply(Consumer<String> printer, Consumer<PrintNode> outputQueue);
	}

	/** печать промежутка между деревьями */
	private static class Whitespace extends PrintNode {
		private final int width;

		public Whitespace(int length) {
			this.width = length;
		}

		@Override
		public Boolean apply(Consumer<String> printer, Consumer<PrintNode> outputQueue) {
			Collections.nCopies(this.width, " ").forEach(printer);
			outputQueue.accept(this);
			return false;
		}
	}

	/** печать горизонтальной линии, под которой промежуток */
	private static class Line extends PrintNode {
		private final int width;

		public Line(int length) {
			this.width = length;
		}

		@Override
		public Boolean apply(Consumer<String> printer, Consumer<PrintNode> outputQueue) {
			Collections.nCopies(this.width, "-").forEach(printer);
			outputQueue.accept(new Whitespace(this.width));
			return true;
		}
	}



	private abstract class AbstractPrintTree extends PrintNode {

		final TreeNode node;
		final PrintTreeEvalResult eval;

		public AbstractPrintTree(TreeNode node) {
			this.node = node;
			this.eval = this.doEval();
		}

		public AbstractPrintTree(TreeNode node, PrintTreeEvalResult eval) {
			this.node = node;
			this.eval = eval;
		}

		private PrintTreeEvalResult doEval() {
			// Здесь мы рекурсивно находим ширину каждого поддерева. Нужно ли её кэшировать?
			// Сравним сложность вычисления ширины каждого поддерева:
			// без кэширования: время O(n*log(n)) место O(1),
			// с кэшированием:  время O(n)        место O(n).
			// Учитывая общую сложность обхода дерева в ширину - время O(n) место O(log^2(n))) -
			// преимущества кэширования неочевидны, поэтому не будем.

			final PrintTreeEvalResult result = new PrintTreeEvalResult();
			if(null==node) {
				return result;
			}

			result.label = node.value.toString();
			result.labelWidth = result.label.length() ;
			if (result.labelWidth == 0) {
				throw new AssertionError("something wrong: empty string");
			}

			// для симметрии, если в надписи чётное число символов, между поддеревьями делаем 2 пробела, иначе 1
			result.subtreeInterval = (0 == result.labelWidth % 2 ? 2 : 1);


			if(this.node.left==null && this.node.right==null) {
				result.leftSubtreeWidth = result.rightSubtreeWidth = (result.labelWidth - result.subtreeInterval) / 2;
				result.leftSubtree = result.rightSubtree = new Whitespace(result.leftSubtreeWidth);
			} else {
				if (node.left != null) {
					final AbstractPrintTree subtree = new PrintLeftSubtree(node.left);
					result.leftSubtree = subtree;
					result.leftSubtreeWidth = subtree.eval.totalWidth;
					result.toBeContinued = true;
				} else {
					result.leftSubtreeWidth = (result.labelWidth - result.subtreeInterval) / 2;
					result.leftSubtree = new Line(result.leftSubtreeWidth);
				}

				if (node.right != null) {
					final AbstractPrintTree subtree = new PrintRightSubtree(node.right);
					result.rightSubtree = subtree;
					result.rightSubtreeWidth = subtree.eval.totalWidth;
					result.toBeContinued = true;
				} else {
					result.rightSubtreeWidth = (result.labelWidth - result.subtreeInterval) / 2;
					result.rightSubtree = new Line(result.rightSubtreeWidth);
				}
			}

			result.totalWidth = result.leftSubtreeWidth + result.subtreeInterval + result.rightSubtreeWidth;

			return result;
		}
	}

	/** деревао печатает свои левое и правое поддеревья */
	private class PrintTree extends AbstractPrintTree {

		public PrintTree(TreeNode node) {
			super(node);
		}

		public PrintTree(TreeNode node, PrintTreeEvalResult eval) {
			super(node, eval);
		}

		@Override
		public Boolean apply(Consumer<String> printer, Consumer<PrintNode> outputQueue) {

			// есть место равное ширине дерева, примерно поседерине печатаем надпись
			int charsWritten = 0;
			for(; charsWritten<eval.leftSubtreeWidth-(eval.labelWidth-1)/2; charsWritten++) {
				printer.accept(" ");
			}
			printer.accept(eval.label);
			charsWritten+=eval.labelWidth;
			while (charsWritten < eval.totalWidth) {
				charsWritten++;
				printer.accept(" ");
			}

			outputQueue.accept(eval.leftSubtree);
			outputQueue.accept(node.left!=null || node.right!=null ? new Line(eval.subtreeInterval) : new Whitespace(eval.subtreeInterval));
			outputQueue.accept(eval.rightSubtree);

			return eval.toBeContinued;
		}
	}

	/** левое поддерево печатает линию направо до конца, и ниже допечатывает себя */
	private class PrintLeftSubtree extends AbstractPrintTree {
		public PrintLeftSubtree(TreeNode node) {
			super(node);
		}

		@Override
		public Boolean apply(Consumer<String> printer, Consumer<PrintNode> outputQueue) {
			int charsWritten = 0;
			for(; charsWritten<eval.leftSubtreeWidth-(eval.labelWidth-1)/2; charsWritten++) {
				printer.accept(" ");
			}
			while (charsWritten < eval.totalWidth) {
				charsWritten++;
				printer.accept("-");
			}

			outputQueue.accept(new PrintTree(node, eval));
			return true;
		}
	}

	/** правое поддерево печатает линию налево до конца, и ниже допечатывает себя */
	private class PrintRightSubtree extends AbstractPrintTree {
		public PrintRightSubtree(TreeNode node) {
			super(node);
		}

		@Override
		public Boolean apply(Consumer<String> printer, Consumer<PrintNode> outputQueue) {
			int charsWritten = 0;
			for(; charsWritten<eval.leftSubtreeWidth-(eval.labelWidth-1)/2 + eval.labelWidth; charsWritten++) {
				printer.accept("-");
			}
			while (charsWritten < eval.totalWidth) {
				charsWritten++;
				printer.accept(" ");
			}

			outputQueue.accept(new PrintTree(node, eval));
			return true;
		}
	}

	private class PrintTreeEvalResult {
		int totalWidth =0, labelWidth =0, subtreeInterval=0, leftSubtreeWidth =0, rightSubtreeWidth =0;
		PrintNode leftSubtree, rightSubtree;
		boolean toBeContinued = false;
		String label = "";
	}

	/** обертка вокруг принтера, умеющая не печатать пробелы в конце строки */
	private class AvoidTrailingSpaces implements Consumer<String> {
		private final Consumer<String> printer;
		private int amountOfSpaces = 0;

		public AvoidTrailingSpaces(Consumer<String> printer) {
			this.printer = printer;
		}

		@Override
		public void accept(String s) {
			// вместо печати пробелов запоминаем их количество и печатаем позже по необходимости
			if(" ".equals(s)) {
				amountOfSpaces++;
			} else {
				if(!"\n".equals(s)) {
					for (int i = 0; i < amountOfSpaces; i++) {
						printer.accept(" ");
					}
				}
				amountOfSpaces = 0;
				printer.accept(s);
			}
		}
	}


	TreeNode root = null;



	@Override
	public boolean add(T value) {
		boolean result = false;
		if(this.root==null) {
			this.root = new TreeNode(value, null, null);
			result = true;
		} else {
			result = add(this.root, value);
			if(result) {
				this.root = rebalance(this.root);
			}
		}
		return result;
	}

	private boolean add(TreeNode node, T value) {
		boolean result = false;
		final int comparisonResult = value.compareTo(node.value);
		if(comparisonResult<0) {
			if(node.left==null) {
				node.left = new TreeNode(value, null, null);
				result = true;
			} else {
				result = add(node.left, value);
			}
			if(result) {
				node.left = rebalance(node.left);
			}
		} else if(comparisonResult>0) {
			if(node.right==null) {
				node.right = new TreeNode(value, null, null);
				result = true;
			} else {
				result = add(node.right, value);
			}
			if(result) {
				node.right = rebalance(node.right);
			}
		}
		return result;
	}

	private TreeNode rebalance(TreeNode node) {
		calculateHeight(node);

		if(balanceFactor(node)==2) {
			if(balanceFactor(node.right) < 0) {
				node.right = rotateRight(node.right);
			}
			return rotateLeft(node);
		}
		if(balanceFactor(node)==-2) {
			if(balanceFactor(node.left) > 0) {
				node.left = rotateLeft(node.left);
			}
			return rotateRight(node);
		}
		return node;
	}

	private void calculateHeight(TreeNode node) {
		node.height = (1 + Math.max(height(node.left), height(node.right)));
	}

	private int height(TreeNode node) {
		return node!=null ? node.height : 0;
	}

	private int balanceFactor(TreeNode node) {
		return node==null ? 0 : height(node.right) - height(node.left);
	}

	private TreeNode rotateRight(TreeNode p) {
		TreeNode q = p.left;
		p.left = q.right;
		q.right = p;
		calculateHeight(p);
		calculateHeight(q);
		return q;
	}

	private TreeNode rotateLeft(TreeNode q) {
		TreeNode p = q.right;
		q.right = p.left;
		p.left = q;
		calculateHeight(q);
		calculateHeight(p);
		return p;
	}



	@Override
	public boolean contains(Object value) {
		return find((T)value, this.root).isPresent();
	}

	private Optional<TreeNode> find(final T value, final TreeNode node) {
		if(node==null) {
			return Optional.empty();
		} else {
			final int comparisonResult = node.value.compareTo(value);
			if(comparisonResult>0) {
				return find(value, node.right);
			} else if(comparisonResult<0) {
				return find(value, node.left);
			} else {
				return Optional.of(node);
			}
		}
	}


	@Override
	public Iterator<T> iterator() {
		return this.inorderIterator();
	}

	public Iterator<T> inorderIterator() {
		return new Iterator<T>() {
			final Stack<TreeNode> stack = new Stack<>();
			TreeNode node = AvlTreeSet.this.root;

			@Override
			public boolean hasNext() {
				return this.node!=null || !stack.isEmpty();
			}

			@Override
			public T next() {
				while(node!=null) {
					stack.push(node);
					node = node.left;
				}
				if(stack.isEmpty()) {
					throw new RuntimeException("iterator exhausted");
				}
				final TreeNode result = stack.pop();
				this.node = result.right;
				return result.value;
			}
		};
	}

	@Override
	public int size() {
		return this.size(this.root);
	}

	private int size(final TreeNode node) {
		return node==null ? 0 : 1 + size(node.left) + size(node.right);
	}




	public String print() {
		final StringBuilder result = new StringBuilder();
		this.print(result::append);
		return result.toString();
	}

	public void print(final Consumer<String> printer) {
		final Consumer<String> internalPrinter = new AvoidTrailingSpaces(printer);

		// обход дерева в ширину с двумя очередями для отслеживания перехода на следующий уровень
		if(this.root==null) {
			return;
		}
		Queue<PrintNode> inputQueue = new LinkedList<>();
		Queue<PrintNode> outputQueue = new LinkedList<>();
		inputQueue.add(new PrintTree(root));
		boolean toBeContinued = false;
		while(true) {
			if(inputQueue.isEmpty()) {
				if(!toBeContinued) {
					break;
				}
				if(outputQueue.isEmpty()) {
					break;
				}
				Queue<PrintNode> temp = inputQueue;
				inputQueue = outputQueue;
				outputQueue = temp;
				toBeContinued = false;
				internalPrinter.accept("\n");
			}
			PrintNode entity = inputQueue.poll();
			if(entity!=null) {
				toBeContinued |= entity.apply(internalPrinter, outputQueue::offer);
			}
		}
	}


}
