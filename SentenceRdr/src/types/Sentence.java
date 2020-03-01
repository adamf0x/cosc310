package types;

import java.util.ArrayList;

public class Sentence extends Phrase{
	//public Node Left, Right;
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
	
	public AuxiliaryPhrase getAuxPhrase() {//if I am parsing compound sentences, I will have to parse them individually from the top level, or add a list function
		AuxiliaryPhrase rVal = null;
		if(children.size() > 1 && children.get(1) instanceof AuxiliaryPhrase) rVal = (AuxiliaryPhrase) children.get(1);
		
		
		return rVal;
	}
	
	public NounPhrase getSubject() {
		for(int i = 0; i < this.children.size(); i++) {
			if(this.children.get(i) instanceof NounPhrase) {
				return (NounPhrase) this.children.get(i);
			}
		}
		return null;
	}
	
	public String getChildSymbolString() {
		String rVal = "";
		for(Node n : children) {
			rVal += n.symbol;
		}
		return rVal;
	}
	
	
	public Noun getInnerSubj() {
		for(Node n : children) {
			if (n instanceof NounPhrase) {				
				return ((NounPhrase) n).getNoun();				
			}
		}
		return null;
	}
	
}
