package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.Stack;
import java.util.function.Consumer;

public class InorderNonRecursive<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> node, Consumer<TreeNode<T>> listener) {
		final Stack<TreeNode<T>> stack = new Stack<>();
		while (node != null || !stack.empty()) {
			while (node != null) {
				stack.push(node);
				node = node.left;
			}
			node = stack.pop();
			if (node != null) {
				listener.accept(node);
				node = node.right;
			}
		}
	}
}
