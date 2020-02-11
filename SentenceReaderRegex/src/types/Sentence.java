package types;

import java.util.ArrayList;

public class Sentence extends Phrase{
	public Node Left, Right;
	public Sentence(Node l, Node r) {
		Left = l;
		Right = r;
	}
	
	public Sentence(ArrayList<Node> aList) {
		Left = aList.remove(0);
		Right = aList.remove(0);
	}
	
	public String toString() {
		return Left.toString() + " & " + Right.toString();
		
	}
}