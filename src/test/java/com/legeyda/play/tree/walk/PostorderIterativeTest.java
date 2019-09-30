package com.legeyda.play.tree.walk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class PostorderIterativeTest extends WalkTest {
	@Test
	public void test() {
		final ArrayList<Integer> trace = new ArrayList<>(7);
		new PostorderIterative<Integer>().walk(sampleTree, treeNode -> trace.add(treeNode.value));
		Assertions.assertIterableEquals(Arrays.asList(2, 7, 5, 12, 17, 15, 10), trace);
	}
}
