package types;

public class Person {
	public Word pronoun;
	public String description;
	public int submode = 0; //1 for possessive, 2 for interrogative
	public int gender = 0; //0 for neutral, 1 for male, 2 for female
	public int id;
	
	public Person(Word p, String d, int id) {
		pronoun = p;
		description = d;
		this.id = id;
	}
	public Person(Word p, String d, int id, int g) {
		pronoun = p;
		description = d;
		this.id = id;
		gender = g;
	}
	
	public Person(Word p, String d, int id, int g, int sm) {
		pronoun = p;
		description = d;
		this.id = id;
		gender = g;
		submode = sm;
	}
	
	public String toString(){
		return description;
	}
	
}
