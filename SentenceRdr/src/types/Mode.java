package types;

public class Mode {
	public String name;
	public Word[][] rep;
	public int id;
	public Mode(String n, Word[][]r, int id) {
		this.name = n;
		this.rep = r;
		this.id = id;
	}
	
	public String toString() {
		return name;
		
	}
}
