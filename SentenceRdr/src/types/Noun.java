package types;

public class Noun extends Node{
	boolean subject, object, isNumeral;

	//nouns can be either the subject of an action or the object of it
	public Noun(Word val, boolean... flags) {
		super(val);
		if(flags.length > 0 && flags[0])subject = true;
		else if(flags.length > 0 && flags[1])object = true;	
		
		if(val.getDefWithPart(8)!=null)symbol = 'k';
		else symbol = 'a';
		if(val.getVal().matches(".*[0-9]+.*"))isNumeral = true;
	}

}
