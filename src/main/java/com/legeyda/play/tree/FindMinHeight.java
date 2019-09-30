package com.legeyda.play.tree;

import java.util.LinkedList;
import java.util.Queue;

public class FindMinHeight<T> {

	private class NodeWithDepth {
		int depth;
		TreeNode<T> node;
		public NodeWithDepth(int depth, TreeNode<T> node) {
			this.depth = depth;
			this.node = node;
		}
	}

	public int findMinHeight(TreeNode<T> root) {
		if(null==root) {
			return 1;
		}
		final Queue<NodeWithDepth> queue = new LinkedList<>();
		for(NodeWithDepth node = new NodeWithDepth(1, root); node!=null; node=queue.poll()) {
			if(node.node.left!=null || node.node.right!=null) {
				if (null != node.node.left) {
					queue.offer(new NodeWithDepth(node.depth + 1, node.node.left));
				}
				if (null != node.node.right) {
					queue.offer(new NodeWithDepth(node.depth + 1, node.node.right));
				}
			} else {
				return node.depth;
			}
		}
		return -1;
	}

}
