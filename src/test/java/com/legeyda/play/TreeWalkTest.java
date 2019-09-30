package com.legeyda.play;

import com.legeyda.play.tree.TreeNode;
import com.legeyda.play.tree.walk.BreadFirstNonRecursive;
import com.legeyda.play.tree.walk.PreorderNonRecursive;
import com.legeyda.play.tree.walk.InorderNonRecursive;
import com.legeyda.play.tree.walk.InorderNonRecursive2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TreeWalkTest {
	private final TreeNode<Integer> sampleTree = new TreeNode<>(
			10,
			new TreeNode<>(
					5,
					new TreeNode<>(2, null, null),
					new TreeNode<>(7, null, null)),
			new TreeNode<>(
					15,
					new TreeNode<>(12, null, null),
					new TreeNode<>(17, null, null)));

	@Test
	public void testBreadFirstNonRecursive() {
		final ArrayList<Integer> trace = new ArrayList<>(7);
		new BreadFirstNonRecursive<Integer>().walk(sampleTree, treeNode -> trace.add(treeNode.value));
		Assertions.assertIterableEquals(Arrays.asList(10, 5, 15, 2, 7, 12, 17), trace);
	}


	@Test
	public void testDepthFirstRecursive() {
		final ArrayList<Integer> trace = new ArrayList<>(7);
		new PreorderNonRecursive<Integer>().walk(sampleTree, treeNode -> trace.add(treeNode.value));
		Assertions.assertIterableEquals(Arrays.asList(10, 5, 2, 7, 15, 12, 17), trace);
	}

	@Test
	public void testInorderNonRecursive() {
		final ArrayList<Integer> trace = new ArrayList<>(7);
		new InorderNonRecursive<Integer>().walk(sampleTree, treeNode -> trace.add(treeNode.value));
		Assertions.assertIterableEquals(Arrays.asList(2, 5, 7, 10, 12, 15, 17), trace);
	}
	@Test
	public void testInorderNonRecursive2() {
		final ArrayList<Integer> trace = new ArrayList<>(7);
		new InorderNonRecursive2<Integer>().walk(sampleTree, treeNode -> trace.add(treeNode.value));
		Assertions.assertIterableEquals(Arrays.asList(2, 5, 7, 10, 12, 15, 17), trace);
	}
}
