package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class BreadFirstNonRecursive<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> root, Consumer<TreeNode<T>> listener) {
		if(null==root) {
			return;
		}
		final Queue<TreeNode<T>> queue = new LinkedList<>();
		for(TreeNode node = root; node!=null; node=queue.poll()) {
			listener.accept(node);
			if(null!=node.left) {
				queue.offer(node.left);
			}
			if(null!=node.right) {
				queue.offer(node.right);
			}
		}
	}
}
