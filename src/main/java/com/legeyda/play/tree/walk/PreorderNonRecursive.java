package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.Stack;
import java.util.function.Consumer;

public class PreorderNonRecursive<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> node, Consumer<TreeNode<T>> listener) {
		final Stack<TreeNode<T>> stack = new Stack<>();
		stack.push(node);
		while(!stack.isEmpty()) {
			node = stack.pop();
			if (null != node) {
				listener.accept(node);
				stack.push(node.right);
				stack.push(node.left);
			}
		}
	}
}
