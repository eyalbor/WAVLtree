
public class Main {

	public static void main(String[] args) {
		WAVLTree tree3 = new WAVLTree();
		System.out.println(tree3.toString());
		tree3.insert(10, "value10");
		tree3.insert(20, "value20");
		tree3.insert(30, "value30");
		tree3.insert(25, "value25");
		tree3.insert(1, "value1");
		tree3.insert(2, "value2");
		tree3.insert(3, "value3");
		tree3.insert(4, "value4");
		tree3.insert(15, "value15");
		tree3.insert(15, "value15");
		tree3.insert(16, "value16");
		tree3.delete(16);
		tree3.delete(20);
		//not working - maybe double rotaion
		//tree3.insert(17, "value17");
		//tree3.insert(18, "value18");
		System.out.println(tree3.toString());
		
//		WAVLTree tree = new WAVLTree();
//		tree.insert(10, "value10");
//		tree.insert(20, "value20");
//		tree.insert(30, "value30");
//		tree.insert(25, "value25");
//		tree.insert(1, "value1");
//		tree.insert(2, "value2");
//		tree.insert(3, "value3");
//		tree.insert(4, "value4");
//		tree.insert(15, "value15");
//		tree.insert(15, "value15");
//		tree.insert(16, "value16");
//		//tree.delete(10);
//		//tree.delete(2);
//		//tree.delete(17);
//		//tree.delete(16);
//		//tree.delete(4);
//		tree.insert(17, "value17");
//		tree.insert(18, "value18");
//		System.out.println(tree.toString());

//		WAVLTree treed = new WAVLTree();
//
//		treed.insert(10, "value10");
//		treed.insert(20, "value20");
//		treed.insert(30, "value30");
//		treed.insert(25, "value30");
//		treed.insert(26, "value30");
//		treed.delete(20);
//		
//		//cant delete 25
//		treed.delete(25);
//		treed.delete(30);
//		treed.delete(26);
//		treed.delete(10);
//		System.out.println(treed.toString());
		
	}

}
