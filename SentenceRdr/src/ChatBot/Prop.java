package ChatBot;

public class Prop {
	public String name, descriptor;	
	
	public int nParameter; //optional parameter to represent a number
	
	public Prop(String n, String d) {
		name = n;
		descriptor = d;		
	}
	
	public Prop(String n, String d,int np) {
		name = n;
		descriptor = d;		
		nParameter = np;
	}
}
