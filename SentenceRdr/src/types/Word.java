  package types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import control.SParse;
import types.Def;

public class Word implements Comparable<Word>, Cloneable{
	public static String[] partList = {"prep.","n.","a.","v. t.","v. i.", "adv.","p. pr.", "p. p.","vb. n.",
			"imp.","superl.", "pl.", "definite article.","interj.", "n sing.", "pron.", "conj.","poss. pr.", "obj."};
	private static ArrayList<String> auxiliaries = new ArrayList<String>(Arrays.asList(new String[]{"is", "am", "are", "was", "were", "can","could","dare","do", "did",
			"does","have","had","has","may","might","must","need","ought","shall", "should","will", "would", "been","be", "being"}));
	
	private static ArrayList<String> interrogatives = new ArrayList<String>(Arrays.asList(new String[] {"who", "what", "where", "when", "why", "how"}));
	
	private ArrayList<Def> defs;
	private String val;
	public int sPart = 1; //in case it is a noun. 
	
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
	
	public Word(int part) {  //generic constructor for modality
		this.val = "any";
		this.sPart = part;
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
		if(auxiliaries.contains(this.val)) {sPart = 20;}
		else if((temp=this.getPartWithDef(13))!=-1) {
			sPart = temp;
		}
		else if(interrogatives.contains(this.val))sPart = 21;
		else if((temp=this.getPartWithDef(16))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(9,6))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(0))!=-1)sPart = temp;	
		else if((temp=this.getPartWithDef(3,4))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(7))!=-1)sPart = temp;
		//else if((temp=this.getPartWithDef(3,4))!=-1 && this.getPartWithDef(1)==-1)sPart = temp; //I will assume this is a verb, but if it is found after a pronoun or determiner I will change it to a noun
		else if((temp=this.getPartWithDef(15,18))!=-1)sPart = 15;											
		else if((temp=this.getPartWithDef(8))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(12))!=-1)sPart = temp;
		
		else if((temp=this.getPartWithDef(2))!=-1)sPart = temp;
		else if((temp=this.getPartWithDef(5))!=-1)sPart = temp;
		
		else if((temp=this.getPartWithDef(1))!=-1)sPart = temp;
		else sPart = 1; //default to noun (0 is preposition)	
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
	
	public static Word getWordObj(String str) {
		int ind = -1;
		if((ind = Arrays.binarySearch(SParse.wListA,new Word(str)))> -1) {
			return SParse.wListA[ind];
		}
		
		else if(str.matches(".*([0-9]{2}:?[0-9]{2}|[0-9]{1,}(am|AM|pm|PM)).*")) {
			return new Word(str, "n.", "time"); //if its a time. This doesnt appear to work for some reason. attempted a fix (not tested)
			}
		else if(str.matches("[0-9]+")) {	return new Word(str, "a.", "number");}//in case its not
		
		else return new Word(str, "n.", "proper name"); 
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
	
	public Def locateInDef(String fStr) {
		for(Def d: defs) {
			if(d.def.contains(fStr))return d;
		}
		return null;
	}
	
	public Word clone() {
		return new Word(this.val, (ArrayList<Def>) this.defs.clone());
	}
	
	public int compareTo(Word w) {
		return this.val.toLowerCase().compareTo(w.val.toLowerCase());
	}
	
	//*****************************inner Definition Class
	//*****************************
	
	//*******************************
	//*****************************
}
