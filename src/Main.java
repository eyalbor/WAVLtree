
public class Main {

	public static void main(String[] args) {
		WAVLTree tree = new WAVLTree();
		tree.insert(10, "value10");
		WAVLTreePrinter.toString(tree);
	}

}
