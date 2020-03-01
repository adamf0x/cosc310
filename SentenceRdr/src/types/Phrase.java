package types;

import java.util.ArrayList;

public class Phrase extends Node{
	ArrayList<Node> children;
	
	public Phrase() {
		
	}
	public Phrase(ArrayList<Node> c) {
		children = c;		
	}
	
	public String toString(){
		String rVal = "{";
		for(Node n : children) {
			rVal += n.toString() + ", ";
		}
		rVal = rVal.substring(0, rVal.length()-2) + "}";
		return rVal;
	}
	
	public ArrayList<Node> getChildren(){
		return this.children;
	}
}
