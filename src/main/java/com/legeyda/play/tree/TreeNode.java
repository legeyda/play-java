package com.legeyda.play.tree;

public class TreeNode<T> {
	public T value;
	public TreeNode<T> left;
	public TreeNode<T> right;

	public TreeNode(T value, TreeNode<T> left, TreeNode<T> right) {
		this.value = value;
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return "TreeNode{" + (value!=null ? value.toString() + '}' : "null}");
	}
}
