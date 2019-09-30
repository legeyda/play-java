package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.function.Consumer;

public interface TreeWalker<T> {
	void walk(TreeNode<T> root, Consumer<TreeNode<T>> listener);
}
