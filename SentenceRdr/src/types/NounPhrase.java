package types;
import java.util.ArrayList;

public class NounPhrase extends Phrase{

	public NounPhrase(ArrayList<Node> c) {
		super(c);
		// TODO Auto-generated constructor stub
		symbol = 'z';
	}
	
	public Noun getNoun() {
		for(int i = 0; i < this.children.size(); i++) {
			if(this.children.get(i) instanceof Noun) {
				return (Noun) this.children.get(i);
			}
		}
		return null;
	}
	
	public PrepPhrase getPrepPhrase() {
		for(int i = 0; i < this.children.size(); i++) {
			if(this.children.get(i) instanceof PrepPhrase) {
				return (PrepPhrase) this.children.get(i);
			}
		}
		return null;
	}
	
}
