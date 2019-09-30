package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.function.Consumer;

public class PreorderRecursive<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> root, Consumer<TreeNode<T>> listener) {
		if(null==root) {
			return;
		}
		listener.accept(root);
		walk(root.left, listener);
		walk(root.right, listener);
	}
}
