package types;

import java.util.ArrayList;

public class Sentence extends Phrase{
	public Node Left, Right;
	public Sentence() {
		symbol = 's';
	}
	
	public Sentence(ArrayList<Node> c) {
		children = c;
		symbol = 's';
	}
	
	public String toString(){
		String rVal = "[";
		for(Node n : children) {
			rVal += n.toString() + ", ";
		}
		rVal = rVal.substring(0, rVal.length()-2) + "]";
		return rVal;
	}
}
