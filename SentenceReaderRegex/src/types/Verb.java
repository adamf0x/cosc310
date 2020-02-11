package types;

public class Verb extends Node{
	boolean refl,trns;
	int tense; //0:present,1:past,2:future, etc.
	public Verb(Word val, boolean... flags) {
		super(val);
		if(val.getDefWithPart(3)!=null)symbol = 'f';
		else if(val.getDefWithPart(4)!=null)symbol = 'g';
		else if(val.getDefWithPart(9)!=null)symbol = 'h';
		else if(val.getDefWithPart(6)!=null)symbol = 'i';
		else if(val.getDefWithPart(7)!=null)symbol = 'j';		
		
	}
}
