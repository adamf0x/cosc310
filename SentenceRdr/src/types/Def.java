package types;

import java.util.LinkedList;

import types.Word;

public class Def implements Cloneable{
	public String def;
	public LinkedList<Integer> part;
	public Def(String d, String p) {
		part = new LinkedList<Integer>();
		def = d;
		for(int i = 0; i < Word.partList.length; i++) {
				if(i==15 && d.contains("possessive")) {part.add(17);continue;}
				if(p.contains(Word.partList[i]))part.add(i);	
		}
	}
	public Def(String d, LinkedList<Integer> pa) {
		this.def = d;
		this.part = pa;
	}
	
	public String getPartString(){
		String rVal = "";
		for(Integer i: part) {
			rVal += Word.partList[i] + " ";
		}
		return rVal;
	}	
	
	public boolean hasPart(int p) {
		for(Integer i: part) {
			if(p == i)return true;				
		}
		return false;
	}
	
	public boolean hasPart(int[] p) {
		for(Integer i: part) {
			for(Integer j : p) {
				if(j == i)return true;				
			}
		}
		return false;
	}
	
	public Def clone() {
		return new Def(def, (LinkedList<Integer>)part.clone());
	}
}
