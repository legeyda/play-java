package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

public abstract class WalkTest {
	protected final TreeNode<Integer> sampleTree = new TreeNode<>(
			10,
			new TreeNode<>(
					5,
					new TreeNode<>(2, null, null),
					new TreeNode<>(7, null, null)),
			new TreeNode<>(
					15,
					new TreeNode<>(12, null, null),
					new TreeNode<>(17, null, null)));

}
