  package types;

import java.util.ArrayList;
import java.util.LinkedList;
import types.Def;

public class Word implements Comparable<Word>, Cloneable{
	public static String[] partList = {"prep.","n.","a.","v. t.","v. i.", "adv.","p. pr.", "p. p.","vb. n.",
			"imp.","superl.", "pl.", "definite article.","interj.", "n sing.", "pron.", "conj.","poss. pr.", "obj."};
	private static String[] adjectiveEndings = {"able", "ible", "al", "ful", "ic", "ive", "less", "ous"};
	private ArrayList<Def> defs;
	private String val;
	public int sPart;
	
	public Word(String v) {
		this.defs = new ArrayList<Def>();
		val = v;
	}
	
	public Word(String v,String d , String p) {
		this.defs = new ArrayList<Def>(1); //start with 1 to save memory
		defs.add(new Def(d,p));
		val = v;
		
	}
	public Word(String v, ArrayList<Def> de) {
		val = v;
		defs = de;
	}
	
	public ArrayList<Def> getDefs() {
		return defs;
	}
	
	public Def getDefWithPart(int... p) {
		for(Def d: defs) {
			if(d.hasPart(p))return d;
		}
		
		return null;
	}
	
	public int getPartWithDef(int... p) {
		for(Def d: defs) {
			if(d.hasPart(p)) {
				for(int i : p) {
					if(d.hasPart(i))return i;
				}
			}
		}
		
		return -1;
	}
	
	
	public void guessPart() {
		int temp = -1;
		if((temp=this.getPartWithDef(16))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(9,6,7))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(0))!=-1)sPart = temp;												//conjugated verb
		else if((temp=this.getPartWithDef(3,4))!=-1 && this.getPartWithDef(1)==-1)sPart = temp;				//non conjugated verb
		else if((temp=this.getPartWithDef(15,18))!=-1)sPart = 15;											//pronoun
		else if((temp=this.getPartWithDef(8))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(12))!=-1)sPart = temp;
		
		else if((temp=this.getPartWithDef(2))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(5))!=-1)sPart = temp;
		
		else if((temp=this.getPartWithDef(1))!=-1)sPart = temp;
			
	}
	
	

	public String getVal() {
		return val;
	}	

	public void addDef(String d, String p) {
		defs.add(new Def(d,p));		
	}	
	
	public boolean isVerbType() { //it may be that the transitive and intransitive infinitive forms should be classified differently for phrase purposes
		if(this.getDefWithPart(3,4,6,9) != null) return true;
		return false;
	}
	public boolean isVerbPhraseType() { //it may be that the transitive and intransitive infinitive forms should be classified differently for phrase purposes
		if(this.getDefWithPart(3,4,6,9) != null) return true;
		return false;
	}
	public boolean isNounType() {
		if(this.getDefWithPart(1,8,14,15)!= null)return true;
		return false;
	}
	public boolean isNounPhraseType() {
		if(this.getDefWithPart(1,2,8,12,14,15)!= null)return true;
		return false;
	}
	public boolean assertNounType() { //only answers true if it is a noun/pronoun and no other type
		if(!isVerbType()) {
			if(this.getDefWithPart(1,8,14,15)!= null)return true;
		}
		return false;
	}
	
	public boolean assertNounPhraseTypes() { //only answers true if it is a noun/pronoun or any of the others in Noun phrase
		if(!isVerbType()) {
			if(this.getDefWithPart(1,2,8,12,14,15)!= null)return true;
		}
		return false;
	}
	
	public String toString() {
		String rVal = null;
		if(val.compareTo("") != 0) {
			rVal = "@" + val + ":\n";		
			for(Def d : defs) {
				if(d.def.length() >0)
				rVal += "> " + d.getPartString() + " < : " + d.def +"*\n"; 
			}		
		}
		return rVal;
	}
	
	public Word clone() {
		return new Word(this.val, (ArrayList<Def>) this.defs.clone());
	}
	
	public int compareTo(Word w) {
		return this.val.compareTo(w.val);
	}
	
	//*****************************inner Definition Class
	//*****************************
	
	//*******************************
	//*****************************
}
