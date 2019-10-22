package com.legeyda.play.tree;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BinaryTreeSet<T extends Comparable<T>> extends AbstractSet<T> {

	/**
	 * абстрактный элемент для печати дерева:
	 * может быть либо нодой исходного дерева, либо пробельными промежутками между ними
	 */
	private interface PrintNode<T> extends BiFunction<Consumer<String>, Queue<PrintNode<T>>, Boolean> {
		/** мега метод для печати дерева:
		 *  печатает в printer текущую строку и заполняет очередь следующего уровня
		 *  (как обход дерева в ширину)
		 *  @return true если это нода и у неё есть потомки, false - если просто промежуток пробелов
		 */
		@Override
		Boolean apply(Consumer<String> printer, Queue<PrintNode<T>> outputQueue);
	}

	/** печать промежутка между деревьями */
	private class Whitespace implements PrintNode<T> {
		private final int width;

		public Whitespace(int length) {
			this.width = length;
		}

		@Override
		public Boolean apply(Consumer<String> printer, Queue<PrintNode<T>> outputQueue) {
			for(int i = 0; i<this.width; i++) {
				printer.accept(" ");
			}
			outputQueue.add(this);
			return false;
		}
	}

	/** для читаемости добавляем линию под нодой */
	private class Pattern implements PrintNode<T> {
		private final Character symbol;
		private final int width;
		private final Collection<PrintNode<T>> children;


		private Pattern(Character symbol, int width, Collection<PrintNode<T>> children) {
			this.symbol = symbol;
			this.width = width;
			this.children = children;
		}

		@Override
		public Boolean apply(Consumer<String> printer, Queue<PrintNode<T>> outputQueue) {
			for(int i=0; i<this.width; i++) {
				printer.accept("-");
			}
			outputQueue.addAll(this.children);
			return true;
		}
	}

	private class LeftBar implements PrintNode<T> {



		@Override
		public Boolean apply(Consumer<String> printer, Queue<PrintNode<T>> outputQueue) {
			return null;
		}

	}



	// todo rightmost
	private class PrintTree implements PrintNode<T> {

		private final TreeNode<T> node;

		public PrintTree(TreeNode<T> node) {
			this.node = node;
		}

		@Override
		public Boolean apply(Consumer<String> printer, Queue<PrintNode<T>> outputQueue) {
			final TreeEvalResult eval = this.eval();

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

			final Collection<PrintNode<T>> children = Arrays.asList(
					eval.leftSubtree,
					new Whitespace(eval.subtreeInterval),
					eval.rightSubtree);

			if(this.node.left!=null || this.node.right!=null) {
				outputQueue.add(new Pattern(eval.totalWidth, children));
			} else {
				outputQueue.addAll(children);
			}


			return eval.toBeContinued;
		}

		private TreeEvalResult eval() {
			// Здесь мы рекурсивно находим ширину каждого поддерева. Нужно ли её кэшировать?
			// Сравним сложность вычисления ширины каждого поддерева:
			// без кэширования: время O(n*log(n)) место O(1),
			// с кэшированием:  время O(n)        место O(n).
			// Учитывая общую сложность обхода дерева в ширину - время O(n) место O(log^2(n))) -
			// преимущества кэширования неочевидны, поэтому не будем.

			final TreeEvalResult result = new TreeEvalResult();
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

			if(node.left!=null) {
				final PrintTree subtree = new PrintTree(node.left);
				result.leftSubtree = subtree;
				result.leftSubtreeWidth = subtree.eval().totalWidth;
				result.toBeContinued = true;
			} else {
				result.leftSubtreeWidth = (result.labelWidth - result.subtreeInterval) / 2;
				result.leftSubtree = new Whitespace(result.leftSubtreeWidth);
			}

			if(node.right!=null) {
				final PrintTree subtree = new PrintTree(node.right);
				result.rightSubtree = subtree;
				result.rightSubtreeWidth = subtree.eval().totalWidth;
				result.toBeContinued = true;
			} else {
				result.rightSubtreeWidth = (result.labelWidth - result.subtreeInterval) / 2;
				result.rightSubtree = new Whitespace(result.rightSubtreeWidth);
			}

			result.totalWidth = result.leftSubtreeWidth + result.subtreeInterval + result.rightSubtreeWidth;

			return result;
		}
	}

	private class TreeEvalResult {
		int totalWidth =0, labelWidth =0, subtreeInterval=0, leftSubtreeWidth =0, rightSubtreeWidth =0;
		PrintNode<T> leftSubtree, rightSubtree;
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



	TreeNode<T> root = null;

	@Override
	public boolean add(T value) {
		if(this.root==null) {
			this.root = new TreeNode<>(value, null, null);
			return true;
		} else {
			return add(this.root, value);
		}
	}

	private boolean add(TreeNode<T> node, T value) {
		final int comparisonResult = value.compareTo(node.value);
		if(comparisonResult<0) {
			if(node.left==null) {
				node.left = new TreeNode<>(value, null, null);
				return true;
			} else {
				return add(node.left, value);
			}
		} else if(comparisonResult>0) {
			if(node.right==null) {
				node.right = new TreeNode<>(value, null, null);
				return true;
			} else {
				return add(node.right, value);
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean contains(Object value) {
		return find((T)value, this.root).isPresent();
	}

	private Optional<TreeNode<T>> find(final T value, final TreeNode<T> node) {
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
			final Stack<TreeNode<T>> stack = new Stack<>();
			TreeNode<T> node = BinaryTreeSet.this.root;

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
				final TreeNode<T> result = stack.pop();
				this.node = result.right;
				return result.value;
			}
		};
	}

	@Override
	public int size() {
		return this.size(this.root);
	}

	private int size(final TreeNode<T> node) {
		return node==null ? 0 : 1 + size(node.left) + size(node.right);
	}







	public void print(final Consumer<String> printer) {
		final Consumer<String> internalPrinter = new AvoidTrailingSpaces(printer);


		// обход дерева в ширину с двумя очередями для отслеживания перехода на следующий уровень
		if(this.root==null) {
			return;
		}
		Queue<PrintNode<T>> inputQueue = new LinkedList<>();
		Queue<PrintNode<T>> outputQueue = new LinkedList<>();
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
				Queue<PrintNode<T>> temp = inputQueue;
				inputQueue = outputQueue;
				outputQueue = temp;
				toBeContinued = false;
				internalPrinter.accept("\n");
			}
			PrintNode<T> entity = inputQueue.poll();
			if(entity!=null) {
				toBeContinued |= entity.apply(internalPrinter, outputQueue);
			}
		}
	}

}
