package com.legeyda.play.tree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FindMinHeightTest {

	private final TreeNode<Integer> sampleTree = new TreeNode<>(
			10,
			new TreeNode<>(
					5,
					new TreeNode<>(2, null, null),
					new TreeNode<>(7, null, null)),
			new TreeNode<>(
					15, null, null));

	@Test
	public void test() {
		Assertions.assertEquals(2, new FindMinHeight<Integer>().findMinHeight(sampleTree));
	}


}
