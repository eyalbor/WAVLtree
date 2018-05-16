import java.util.function.BiConsumer;

/**
 * 
 * @author eyal borovsky(300075728)_rimon shy (302989215)
 * @version 1.0.0
 */

/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree. (Haupler, Sen & Tarajan ‘15)
 *
 */
public class WAVLTree {

	private enum Operation {
		FINISH, PROMOTE, ROTATION, DOUBLE_ROTATION, DEMOTE, NONE
	}

	private enum SIDE {
		LEFT, RIGHT, NONE
	}

	private WAVLNode root;
	private static Operation status;

	/**
	 * CTOR
	 */
	public WAVLTree() {
		root = null;
	}

	/**
	 * public boolean empty()
	 *
	 * @returns true if and only if the tree is empty O(1)
	 */
	public boolean empty() {
		return size() == 0 ? true : false;
	}

	/**
	 * public String search(int k)
	 *
	 * @returns the info of an item with key k if it exists in the tree otherwise
	 *          null O(log n)
	 */
	public String search(int key) {
		WAVLNode node = searchForNode(key);
		if (node != null) {
			if (node.isInnerNode())
				return node.value;
		}
		return null;
	}

	/**
	 *
	 * @param k
	 *            is the key
	 * @return @WAVLNODE the node of the key, if the tree is not empty and if the
	 *         key isn't found return external node
	 */
	private WAVLNode searchForNode(int k) {
		WAVLNode node = root;
		while (node != null) {
			if (k == node.getKey())
				break;
			else if (node.getKey() == WAVLNode.EXTERNAL_NODE_RANK)
				return node;
			else if (k < node.getKey())
				node = node.getLeft();
			else
				node = node.getRight();
		}
		return node;
	}

	/**
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. returns -1 if an item with key
	 * k already exists in the tree.
	 */
	public int insert(int k, String value) {
		int rebalancing = 0;
		WAVLNode searchNode = searchForNode(k);
		if (searchNode == null) {
			// insert first item to the tree, root is a leaf
			root = new WAVLNode(k, value);
			return rebalancing;
		} else if (searchNode.isInnerNode())
			// the key already exists
			return -1;

		// now we have external node (key = -1) in searchNode
		// create leaf
		WAVLNode newNode = new WAVLNode(k, value);
		WAVLNode parent = searchNode.parent;

		// connectNodeToParent
		/****
		 * rimon - you already check it in the code, eyal - where????????
		 ***/
		if (!parent.isLeaf()) {
			// insert to unary node
			status = Operation.FINISH;
		} else {
			// insert to leaf
			status = Operation.PROMOTE;
		}
		if (parent.key < k) {
			parent.right = newNode;
		} else {
			parent.left = newNode;
		}
		// connect to parent
		newNode.parent = parent;

		// start passing on the tree from the node until the tree is valid
		WAVLNode n = newNode;
		SIDE side = SIDE.NONE;
		while (status != Operation.FINISH) {
			// between n to n.parent
			if ((side = checkPromoteCase(n)) != SIDE.NONE) {
				status = Operation.PROMOTE;
				Operation rotateCase = checkRotationCase(n, side);
				if (rotateCase != Operation.NONE) {
					status = rotateCase;
				}
			} else {
				status = Operation.FINISH;
			}

			switch (status) {
			case PROMOTE:
				n.parent.rank++;
				++rebalancing;
				break;
			case ROTATION:
				singleRotation(n, side);
				status = Operation.FINISH;
				rebalancing += 2;
				break;
			case DOUBLE_ROTATION:
				// this is node x from the lecture
				// rimon please fix it to return the node to update the tree subTree and rank
				// until the root
				doubleRotation(n, side);
				rebalancing += 5;
				status = Operation.FINISH;
				break;
			default:
				break;
			}
			n = n.parent;
		}
		updateSubTreeSizeFromNodeToRoot(newNode.parent);
		return rebalancing;
	}

	/**
	 * O(log n)
	 * 
	 * @param node
	 */
	private void updateSubTreeSizeFromNodeToRoot(WAVLNode node) {
		while (node != null) {
			if (node.key == WAVLNode.EXTERNAL_NODE_RANK) {
				node = node.parent;
				continue;
			} else {
				calculateSubTreePairNode(node);
			}
			node = node.parent;
		}
	}

	private void calculateSubTreePairNode(WAVLNode node) {
		node.subTreeSize = 0;
		if (!node.isLeaf())
			if (node.right.isInnerNode())
				node.subTreeSize += node.right.subTreeSize  + 1;
			if(node.left.isInnerNode())
				node.subTreeSize +=  node.left.subTreeSize + 1;
	}


	/**
	 * rimon all this function is not right, what is side here
	 * 
	 * //check if its rotate/double rotate (difference 0,2)
	 * 
	 * @param node
	 * @param side
	 *            of node x to his parent z
	 * @return
	 */
	private Operation checkRotationCase(WAVLNode node, SIDE side) {
		Operation s = Operation.NONE;
		if (side == SIDE.LEFT) {
			if (getRankDiffBySide(node.parent, true) == 2) {
				if (getRankDiffBySide(node, true) == 2) {
					status = Operation.ROTATION;
				} else {
					status = Operation.DOUBLE_ROTATION;
				}
			}

		} else {
			// false==left
			if (getRankDiffBySide(node.parent, false) == 2) {
				if (getRankDiffBySide(node, false) == 2) {
					status = Operation.ROTATION;
				} else {
					status = Operation.DOUBLE_ROTATION;
				}
			}
		}
		return s;
	}

	/**
	 * between current node to the parent
	 * 
	 * @param node
	 * @return
	 */
	private SIDE checkPromoteCase(WAVLNode node) {
		SIDE s = SIDE.NONE;

		if (node.parent == null) {
			return SIDE.NONE;
		}

		if (getRankDiffBySide(node.parent, false) == 0)
			s = SIDE.LEFT;
		else if (getRankDiffBySide(node.parent, true) == 0)
			s = SIDE.RIGHT;
		return s;
	}

	/**
	 * Eyal
	 * 
	 * @param node
	 * @return
	 */
	private void doubleRotation(WAVLNode node, SIDE side) {
		WAVLNode z = node.parent;
		SIDE zSide = this.SideToParent(z.parent, z);
		WAVLNode b, c, d = null;
		if (side == SIDE.LEFT) {
			b = node.right;
			c = b.left;
			d = b.right;
			node.right = c;
			c.parent = node;
			b.parent = z.parent;
			if (zSide == SIDE.LEFT)
				z.parent.left = b;
			else if (zSide == SIDE.RIGHT)
				z.parent.right = b;
			else
				root = b;
			node.parent = b;
			b.left = node;
			z.parent = b;
			b.right = z;
			d.parent = z;
			z.left = d;
		} else {
			b = node.left;
			c = b.right;
			d = b.left;
			node.left = c;
			c.parent = node;
			b.parent = z.parent;
			if (zSide == SIDE.LEFT)
				z.parent.left = b;
			else if (zSide == SIDE.RIGHT)
				z.parent.right = b;
			else
				root = b;
			node.parent = b;
			b.right = node;
			z.parent = b;
			b.left = z;
			d.parent = z;
			z.right = d;
		}
		--node.rank;
		--z.rank;
		++b.rank;
		calculateSubTreePairNode(node);
		calculateSubTreePairNode(z);
		calculateSubTreePairNode(b);

	}

	/**
	 * eyal
	 * 
	 * @param node
	 * @return
	 */
	private void singleRotation(WAVLNode node, SIDE side) {
		WAVLNode z = node.parent;
		WAVLNode b = null;
		SIDE zSide = this.SideToParent(z.parent, z);

		if (side == SIDE.LEFT) {
			b = node.right;
			node.parent = z.parent;
			if (zSide == SIDE.LEFT)
				z.parent.left = node;
			else if (zSide == SIDE.RIGHT)
				z.parent.right = node;
			else // zSide is null
				root = node;
			z.parent = node;
			node.right = z;
			z.left = b;
			b.parent = z;

		} else {
			b = node.left;
			node.parent = z.parent;
			if (zSide == SIDE.LEFT)
				z.parent.left = node;
			else if (zSide == SIDE.RIGHT)
				z.parent.right = node;
			else // zSide is null
				root = node;
			z.parent = node;
			node.left = z;
			z.right = b;
			b.parent = z;
		}
		--z.rank;
		calculateSubTreePairNode(z);
	}

	public SIDE SideToParent(WAVLNode parent, WAVLNode chiled) {
		if (parent == null)
			return SIDE.NONE;
		if (parent.getLeft() == chiled) {
			return SIDE.LEFT;
		} else {
			return SIDE.RIGHT;
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
	 * item with key k was not found in the tree.
	 */
	public int delete(int k) {
		int rebalancing = 0;
		WAVLNode rebalanceNode = null;
		WAVLNode parent = null;
		SIDE sideOfChild;
		SIDE sideToParent;

		WAVLNode searchNode = searchForNode(k);
		if (searchNode == null) {
			return -1;
		}

		parent = searchNode.parent;
		if (parent.getLeft() == searchNode) {
			sideToParent = SIDE.LEFT;
		} else {
			sideToParent = SIDE.RIGHT;
		}

		// if is leaf
		if (searchNode.isLeaf()) {
			WAVLNode externalNode = WAVLNode.createExternalNode(parent);
			switch (sideToParent) {
			case LEFT:
				parent.left = externalNode;
				break;
			case RIGHT:
				parent.right = externalNode;
				break;
			default:
				break;
			}
			parent.subTreeSize--;
			if (parent.isLeaf()) {
				parent.rank = 0;
				rebalancing++;
			}
			rebalanceNode = parent;
		}
		// unary Node
		else if ((sideOfChild = searchNode.isUnary()) != SIDE.NONE) {
			parent.subTreeSize--;
			if (sideOfChild == SIDE.LEFT) {
				if (sideToParent == SIDE.LEFT) {
					parent.left = searchNode.left;
				} else {
					parent.right = searchNode.left;
				}
				searchNode.left.parent = parent;
			} else {
				if (sideToParent == SIDE.LEFT) {
					parent.left = searchNode.right;
				} else {
					parent.right = searchNode.right;
				}
				searchNode.right.parent = parent;
			}
			rebalanceNode = parent;
		}
		// 2 children
		else {
			WAVLNode successor = getSuccessor(searchNode);
			if (successor.parent.left == successor) {
				successor.parent.left = WAVLNode.createExternalNode(successor.parent);
			} else {
				successor.parent.right = WAVLNode.createExternalNode(successor.parent);
			}
			rebalanceNode = successor.parent;
			rebalanceNode.subTreeSize--;
			if (rebalanceNode.isLeaf()) {
				rebalanceNode.rank = 0;
			}
			// TODO what if tree size is 1 or 0
			if (sideToParent == SIDE.LEFT) {
				parent.left = successor;
			} else {
				parent.right = successor;
			}
			if (successor != null) {
				successor.left = searchNode.left;
				successor.right = searchNode.right;
				successor.subTreeSize = searchNode.subTreeSize - 1;
				successor.rank = searchNode.rank;
				successor.parent = parent;
			}
		}
		parent.subTreeSize -= 1;

		searchNode = null;
		// TODO delete before the subtreesize and rank --
		rebalance(rebalanceNode);
		return rebalancing;
	}

	private void rebalance(WAVLNode parent) {
		// TODO Auto-generated method stub

	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty O(log n)
	 */
	public String min() {
		WAVLNode node = getMinNode(root);
		if (node == null) {
			return null;
		}
		return node.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty O(log n)
	 */
	public String max() {
		WAVLNode node = root;
		while (node != null) {
			if (!node.getRight().isInnerNode()) {
				return node.getValue();
			}
			node = node.getRight();
		}
		return null;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty. O(n)
	 */
	public int[] keysToArray() {
		final int[] arr = new int[size()];
		BiConsumer<WAVLNode, Integer> biConsumer = (node, index) -> arr[index] = node.getKey();
		InOrderTree(root, 0, biConsumer);
		return arr;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty. O(n)
	 */
	public String[] infoToArray() {
		final String[] arr = new String[size()];
		BiConsumer<WAVLNode, Integer> biConsumer = (node, index) -> arr[index] = node.getValue();
		InOrderTree(root, 0, biConsumer);
		return arr;
	}

	private void InOrderTree(WAVLNode node, int i, BiConsumer<WAVLNode, Integer> biConsumer) {
		if (node == null) {
			return;
		}
		WAVLNode n = getMinNode(root);
		while (n != null) {
			biConsumer.accept(node, i);
			++i;
			n = getSuccessor(n);
		}
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree. O(1)
	 */
	public int size() {
		if (root != null) {
			return root.getSubTreeSize();
		}
		return 0; // to be replaced by student code
	}

	/**
	 * public WAVLNode getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty O(1)
	 */
	public WAVLNode getRoot() {
		return root;
	}

	/**
	 * public int select(int i)
	 *
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key Example
	 * 2: select(size()) returns the value of the node with maximal key Example 3:
	 * select(2) returns the value 2nd smallest minimal node, i.e the value of the
	 * node minimal node's successor
	 *
	 * O(n) - we can do it better by const
	 */
	public String select(int i) {
		WAVLNode node = getMinNode(root);
		if (node == null || i > size()) {
			return null;
		}
		for (int j = 1; j < i; j++) {
			node = getSuccessor(node);
		}
		return node.getValue();
	}

	/**
	 * 
	 * @param node
	 * @return The successor to the given node, or null if it has no successor.
	 */
	private WAVLNode getSuccessor(WAVLNode node) {
		if (node != null) {
			if (node.getRight().isInnerNode()) {
				return getMinNode(node.getRight());
			}
			WAVLNode y = node.getParent();
			while (y != null && node == y.getRight()) {
				node = y;
				y = node.getParent();
			}
			return y;
		}
		return null;
	}

	private WAVLNode getMinNode(WAVLNode rootNode) {
		WAVLNode node = rootNode;
		while (node != null) {
			if (!node.getLeft().isInnerNode()) {
				return node;
			}
			node = node.getLeft();
		}
		return null;
	}

	@Override
	public String toString() {
		return WAVLTreePrinter.toString(this);
	}

	/**
	 * @return The rank difference between the parent and the child on it's side. if
	 *         side is true rightRankDiff else leftRankDiff
	 */
	private static int getRankDiffBySide(WAVLNode parent, boolean side) {
		return side ? WAVLNode.getRankDiff(parent, parent.right) : WAVLNode.getRankDiff(parent, parent.left);
	}

	/**
	 * public class WAVLNode
	 */

	public static class WAVLNode {
		static final int EXTERNAL_NODE_RANK = -1;
		private int key;
		private String value;
		private int rank;
		private WAVLNode parent;
		private WAVLNode left;
		private WAVLNode right;
		private int subTreeSize;

		/**
		 * public class WAVLNode, create leaf
		 */

		public WAVLNode(int key, String value) {
			this.key = key;
			this.value = value;
			rank = 0;
			subTreeSize = 0;
			this.left = WAVLNode.createExternalNode(this);
			this.right = WAVLNode.createExternalNode(this);
		}

		public SIDE isUnary() {
			if (this.left.key != -1 && this.right.key == -1) {
				return SIDE.LEFT;
			} else if (this.left.key == -1 && this.right.key != -1)
				return SIDE.RIGHT;

			return SIDE.NONE;
		}

		public WAVLNode(int key, String value, WAVLNode left, WAVLNode right) {
			this.key = key;
			this.value = value;
			this.left = left;
			this.right = right;
			this.parent = null;
			this.rank = Math.max(left.getRank(), right.getRank()) + 1;
			this.subTreeSize = left.getSubtreeSize() + right.getSubtreeSize() + 1;
			this.left.setParent(this);
			this.right.setParent(this);
		}

		// external node
		private WAVLNode() {
			key = -1;
			rank = EXTERNAL_NODE_RANK;
			subTreeSize = 0;
		}

		public boolean isLeaf() {
			if (this.left.key == -1 && this.right.key == -1)
				return true;
			return false;
		}

		/**
		 * create external node with key = -1
		 */
		private static WAVLNode createExternalNode(WAVLNode parent) {
			WAVLNode node = new WAVLNode();
			node.parent = parent;
			node.rank = -1;
			node.subTreeSize = 0;
			return node;
		}

		/**
		 * 
		 * @return key, if the node is external node return -1
		 */
		public int getKey() {
			return key;
		}

		/**
		 * 
		 * @return the info of the nod. if the node is external node return null
		 */
		public String getValue() {
			return value;
		}

		public WAVLNode getLeft() {
			return left;
		}

		public WAVLNode getRight() {
			return right;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		public WAVLNode getParent() {
			return parent;
		}

		public void setParent(WAVLNode parent) {
			this.parent = parent;
		}

		public int getSubTreeSize() {
			return subTreeSize;
		}

		public void setSubTreeSize(int subTreeSize) {
			this.subTreeSize = subTreeSize;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void setLeft(WAVLNode left) {
			this.left = left;
		}

		public void setRight(WAVLNode right) {
			this.right = right;
		}

		/**
		 * 
		 * @return true if the node is internal , otherwise false O(1)
		 */
		public boolean isInnerNode() {
			return (key != EXTERNAL_NODE_RANK ? true : false);
		}

		/**
		 * 
		 * @return the number of internal mode in the sub-tree O(1)
		 */
		public int getSubtreeSize() {
			return subTreeSize;
		}

		@Override
		public String toString() {
			return "Node(" + value + ", rank=" + rank + ")";
		}

		/**
		 * @return The rank diff between the node and its child
		 */
		public static int getRankDiff(WAVLNode parent, WAVLNode child) {
			if (parent == null || child == null)
				throw new IllegalStateException("Left node cannot be null");
			return parent.getRank() - child.getRank();
		}
	}
}
