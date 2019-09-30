package com.legeyda.play.tree.walk;

import com.legeyda.play.tree.TreeNode;

import java.util.Stack;
import java.util.function.Consumer;

public class PostorderIterative<T> implements TreeWalker<T> {
	@Override
	public void walk(TreeNode<T> node, Consumer<TreeNode<T>> listener) {
		final Stack<TreeNode<T>> stack = new Stack<>();
		TreeNode last = null;
		while(true) {
			if(node!=null) {
				stack.push(node);
				node = node.left;
			} else if(!stack.isEmpty()) {
				node = stack.peek();
				if(node.right==null || node.right==last) {
					// visit and forget
					listener.accept(node);
					stack.pop();
					last = node;
					node=null; // take node from stack for the next iteration
				} else {
					node = node.right;
				}
			} else {
				break;
			}
		}
	}
}
