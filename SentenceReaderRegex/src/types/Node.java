package types;

public abstract class Node {
	public Word val;
	public char symbol;
	
	
	public Node() {
		
	}
	
	public Node(Word v) {
		val = v;
	}	
	
	public String toString(){
		return this.val.getVal();
	}
	
}
