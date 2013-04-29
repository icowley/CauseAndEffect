package com.example.ceandroid;

import java.util.Stack;

import CEapi.rCause;
import android.content.Context;

/**
 * Uses the tree string stored in a rule to build a tree, fills leaf nodes with
 * rCause objects, and evaluates/returns a result
 * 
 * @author CEandroid SMU
 * 
 */
public class ExpressionTree {
	/**
	 * Nodes for tree building.
	 * 
	 * @author CEandroid SMU
	 * 
	 */
	private static class TreeNode {
		/** Is true if the node is a leaf */
		public boolean leaf;

		/** Either '+' or '&' if not a leaf node, used for evaluation */
		public char operation;

		/** rCause to be evaluated (leaf nodes only), null if operator node */
		public rCause value;

		/** Used for getting the correct rCause from the database for evaluation */
		public String idHolder;

		/** Nodes for tree building */
		@SuppressWarnings("unused")
		public TreeNode left, right, parent;

		/**
		 * Constructor for a node, only called by ExpressionTree
		 * 
		 * @param leaf
		 *            flag that says if the node is a leaf or not
		 * @param operation
		 *            set to + or & if not a leaf
		 * @param value
		 *            rCause associated to a leaf node, null if not leaf
		 * @param idHolder
		 *            holds an rCause id for when its time to fetch rCauses
		 */
		public TreeNode(boolean leaf, char operation, rCause value,
				String idHolder) {
			this.leaf = leaf;
			this.operation = operation;
			this.value = value;
			this.idHolder = idHolder;
			this.left = null;
			this.right = null;
			this.parent = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return leaf ? Integer.toString(this.value.getID()) : Character
					.toString(this.operation);
		}
	}

	// end of node class //

	/** The top node of the tree */
	TreeNode root = null;

	/** Context for using the DatabaseHandler */
	private Context c;

	/** Handler to get rCause objects for evaluation */
	private DatabaseHandler db;

	/**
	 * Constructor for tree creation
	 * 
	 * @param code
	 *            formatted string that the tree will build itself with
	 * @param c
	 *            context for usage of application resources
	 */
	public ExpressionTree(String code, Context c) {
		this.c = c;
		root = build(code);
	}

	/**
	 * Builds the tree based on the stored code.
	 * 
	 * @param code
	 *            string that the tree uses to build itself
	 * @return root node of the tree
	 */
	private TreeNode build(String code) {
		root = null;
		TreeNode lNode = null;
		TreeNode rNode = null;
		TreeNode curNode = null;
		char cur;
		Stack<TreeNode> s = new Stack<TreeNode>();
		String idHolder = "";

		/**
		 * If a code gives an ID (number), the value is pushed to a stack.
		 * Whenever a '+' or '&' is reached, the stack is popped twice and those
		 * nodes become children of the operator node. Commas separate nodes in
		 * the string and a '$' represents end of string.
		 */
		for (int i = 0; i < code.length(); i++) {
			cur = code.charAt(i);

			if (cur == ',' || cur == '$') {
				if (idHolder != "") {
					curNode = new TreeNode(true, '\0', null, idHolder);
					s.push(curNode);
					idHolder = "";
				}

				if (cur == '$') {
					if (!s.isEmpty()) {
						root = s.pop();
					}
					break;
				}
			} else if (cur == '+' || cur == '&') {
				curNode = new TreeNode(false, cur, null, "");

				if (!s.isEmpty()) {
					rNode = s.pop();
				}

				if (!s.isEmpty()) {
					lNode = s.pop();
				}

				if (rNode != null) {
					rNode.parent = curNode;
					curNode.right = rNode;
				}
				if (lNode != null) {
					lNode.parent = curNode;
					curNode.left = lNode;
				}

				s.push(curNode);
				curNode = null;
				idHolder = "";
			} else if (cur != '+' && cur != '&' && cur != ',' && cur != '$') {
				idHolder += cur;
			}
		}

		/** Return the root of the tree for evaluation. */
		return root;
	}

	/**
	 * Debug purposes, prints the tree
	 */
	public void printPreOrder() {
		printPreOrder(root);
		System.out.println();
	}

	/**
	 * Debug purposes - prints the tree using recursive methods
	 * 
	 * @param node
	 *            starts with root and passes next node to print child values
	 */
	private void printPreOrder(TreeNode node) {
		if (node == null)
			return;
		if (node.leaf) {
			System.out.print(node.idHolder + " ");
		} else {
			System.out.print(node.operation + " ");
		}
		printPreOrder(node.left);
		printPreOrder(node.right);
	}

	/**
	 * @return number of nodes in the tree
	 */
	public int getSize() {
		int x = getSize(root, 0);
		return x;
	}

	/**
	 * Recursively counts number of nodes in a tree
	 * 
	 * @param node
	 *            used for recursive calling
	 * @param count
	 *            increments as nodes are visited
	 * @return number of nodes in the tree
	 */
	private int getSize(TreeNode node, int count) {
		if (node == null)
			return count;
		else if (node.left == null && node.right == null)
			count += getSize(node.left, count++);
		count += getSize(node.right, count++);

		return count;
	}

	/**
	 * Evaluates the tree, calls recursive function
	 * 
	 * @param context
	 *            allows access to application resources
	 * @return boolean result
	 */
	public boolean evaluate(Context context) {
		c = context;
		return root == null ? false : evaluate(root);
	}

	/**
	 * Evaluates the tree and returns the result. Fills in the leaf nodes with
	 * rCauses for values and uses their isTrue() method to get a value for
	 * them.
	 * 
	 * @param node
	 *            needed for recursive call
	 * @return boolean result
	 */
	private boolean evaluate(TreeNode node) {
		boolean result = false;

		if (node.leaf) {
			db = new DatabaseHandler(c);
			node.value = db.getRCauseByID(Integer.valueOf(node.idHolder));
			db.close();
			if (node.value != null) {
				result = node.value.isTrue(c);
			} else
				result = false;
		} else {
			boolean left, right;
			char operator = node.operation;

			if (node.left != null) {
				left = evaluate(node.left);
			} else {
				if (operator == '&') {
					left = true;
				} else
					left = false;
			}

			if (node.right != null) {
				right = evaluate(node.right);
			} else {
				if (operator == '&') {
					right = true;
				} else
					right = false;
			}

			switch (operator) {
			case '&':
				result = left & right;
				break;
			case '+':
				result = left | right;
				break;
			}
		}
		return result;
	}
}