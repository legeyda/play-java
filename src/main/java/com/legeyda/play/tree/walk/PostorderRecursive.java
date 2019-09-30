package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.Stack;
import java.util.function.Consumer;

public class PostorderRecursive<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> node, Consumer<TreeNode<T>> listener) {
		if(node!=null) {
			walk(node.left, listener);
			walk(node.right, listener);
			listener.accept(node);
		}
	}
}
