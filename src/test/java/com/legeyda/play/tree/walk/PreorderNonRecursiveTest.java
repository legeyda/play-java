package com.legeyda.play.tree.walk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class PreorderNonRecursiveTest extends WalkTest {

	@Test
	public void test() {
		final ArrayList<Integer> trace = new ArrayList<>(7);
		new PreorderNonRecursive<Integer>().walk(sampleTree, treeNode -> trace.add(treeNode.value));
		Assertions.assertIterableEquals(Arrays.asList(10, 5, 2, 7, 15, 12, 17), trace);
	}

}
