package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.Stack;
import java.util.function.Consumer;

public class InorderNonRecursive2<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> node, Consumer<TreeNode<T>> listener) {
		final Stack<TreeNode<T>> stack = new Stack<>();
		while(true) {
			if(node!=null) {
				stack.push(node);
				node = node.left;
			} else if (!stack.isEmpty()) {
				node = stack.pop();
				listener.accept(node);
				node = node.right;
			} else {
				break;
			}
		}
	}
}
