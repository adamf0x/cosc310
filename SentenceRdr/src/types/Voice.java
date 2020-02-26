package types;

public class Voice {
	public String description;
	public String ptrn; //regex pattern to indicate this is a match
	
	public Voice(String d, String p) {
		description = d;
		ptrn = p;
	}
	
	public String toString() {
		return description;
	}
}
