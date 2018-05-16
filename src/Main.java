
public class Main {

	public static void main(String[] args) {
		WAVLTree tree = new WAVLTree();
		tree.insert(10, "value10");
		tree.insert(20, "value20");
		tree.insert(30, "value30");
		tree.insert(25, "value25");
		tree.insert(1, "value1");
		tree.insert(2, "value2");
		tree.insert(3, "value3");
		tree.insert(4, "value4");
		tree.insert(15, "value15");
		tree.insert(15, "value15");
		tree.insert(16, "value16");
//		tree.delete(16);
		//tree.delete(20);
		//not working - maybe double rotaion
		tree.insert(17, "value17");
		tree.insert(18, "value18");
		
		WAVLTree tree2 = new WAVLTree();
		for (int i = 0; i < 3; i++) {
			tree2.insert(i, "");
		}
		System.out.println(tree.toString());
	}

}
