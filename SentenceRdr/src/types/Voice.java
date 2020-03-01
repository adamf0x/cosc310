package types;

public class Voice {
	public String description;
	public String ptrn; //regex pattern to indicate this is a match
	public int id;
	
	public Voice(String d, String p, int id) {
		description = d;
		ptrn = p;
		this.id = id;
	}
	
	public String toString() {
		return description;
	}
}
