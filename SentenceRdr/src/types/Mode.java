package types;

public class Mode {
	public String name;
	public Word[][] rep;
	public Mode(String n, Word[][]r) {
		this.name = n;
		this.rep = r;
	}
	
	public String toString() {
		return name;
		
	}
}
