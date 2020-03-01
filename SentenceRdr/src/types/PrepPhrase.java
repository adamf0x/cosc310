package types;

import java.util.ArrayList;

public class PrepPhrase extends Phrase{

	public PrepPhrase(ArrayList<Node> c) {
		super(c);
		// TODO Auto-generated constructor stub
		symbol = 'x';
	}
	
	public Preposition getPreposition() {
		for(Node n : children) {
			if(n instanceof Preposition)return (Preposition) n;
		}
		return null;
	}

}
