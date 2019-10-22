package com.legeyda.play.tree;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;


public class BinaryTreeSetTest {

	@Test
	void simpleTest() {
		final Set<Integer> testee = new BinaryTreeSet<>();

		assertThat(testee).isEmpty();


		assertThat(testee.add(100)).isTrue();
		assertThat(testee).hasSize(1).contains(100);

		assertThat(testee.add(100)).isFalse();
		assertThat(testee).hasSize(1).contains(100);


		assertThat(testee.add(50)).isTrue();
		assertThat(testee).hasSize(2).containsExactly(50, 100);

		assertThat(testee.add(50)).isFalse();
		assertThat(testee).hasSize(2).containsExactly(50, 100);


		assertThat(testee.add(150)).isTrue();
		assertThat(testee).hasSize(3).containsExactly(50, 100, 150);

		assertThat(testee.add(150)).isFalse();
		assertThat(testee).hasSize(3).containsExactly(50, 100, 150);


		assertThat(testee.add(75)).isTrue();
		assertThat(testee).hasSize(4).containsExactly(50, 75, 100, 150);

		assertThat(testee.add(75)).isFalse();
		assertThat(testee).hasSize(4).containsExactly(50, 75, 100, 150);


	}

	@Test
	void randomizedTest() {
		final Set<Integer> testee = new BinaryTreeSet<>();
		final Set<Integer> model = new TreeSet<>();
		for(int i=0; i<1000; i++) {
			final Integer value = new Random().nextInt(100);
			assertThat(testee.add(value)).isEqualTo(model.add(value));
			assertThat(testee).hasSize(model.size()).containsAll(model);
		}
	}


	@Test
	void printTest() {
		final BinaryTreeSet<Integer> testee = new BinaryTreeSet<>();
		testee.add(100);
		testee.add(50);
		testee.add(150);
		testee.add(75);
		testee.add(60);
		testee.add(61);

		final StringBuilder builder = new StringBuilder();
		testee.print(str->builder.append(str));

		assertThat(builder.toString()).isEqualTo(
"       100\n" +
"50       150\n" +
"      75\n" +
"  60\n" +
"    61");
	}

}
