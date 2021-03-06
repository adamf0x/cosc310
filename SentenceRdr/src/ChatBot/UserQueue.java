package ChatBot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import types.Modality;
import types.Mode;
import types.Node;
import types.Person;
import types.Sentence;
import types.Voice;

//these are one way links. the endpoint is only the endpoint. either has a patternString or a modality match


public class UserQueue {
	private int endpoint, origin;
	public String pStr;
	public int[][] auxOpts;  //[[mode], [person], [voice]]
	
	public UserQueue(int ep, int or, String p, int[][] a) {
		endpoint = ep;
		origin = or;
		pStr = p;
		auxOpts = a;
	}
	public UserQueue(int ep, int or) {
		endpoint = ep;
		origin = or;
	}
	
	
	public int traverse() {
		return endpoint;
	}
	
	public int getOrigin() {
		return origin;
	}
	
	public boolean matchesQueue(String input, Node endVal) {
		boolean matches = true; //if criteria is provided and doesnt match, then it is false
		
		if(pStr == null) {//match with auxOpts
			
			Sentence eV = null;
			if(endVal instanceof Sentence)eV = (Sentence) endVal;
			Mode testMode = null;
			Person testPerson = null;
			Voice testVoice = null;
			int mode, person, voice;
			matches = false;
			//change it so tht instead of returning the modality, etc it just mutates the sentence, so that the function can execute for compound sentences recursively
			if((testMode = Modality.getModality((Sentence)endVal)) != null) {
				mode = Modality.getModality(eV).id;
				if(auxContains(0,mode)) matches = true;
			}
			
			if((testPerson = Modality.getPerson((Sentence)endVal)) != null) {
				person = Modality.getPerson(eV).id;
				if(auxContains(1,person)) matches = true;
			}
			if((testVoice = Modality.getVoice((Sentence)endVal)) != null) {
				voice = Modality.getVoice(eV).id;
				if(auxContains(2,voice)) matches = true;
			}
		}
		else {			//match with pStr (a regex)
			Pattern endP = Pattern.compile(pStr);
			Matcher endM = endP.matcher(input); 
			if(!endM.matches())matches = false;
		}
		
		return matches;
	}
	
	public boolean asrtOpts(int inner) {
		if(auxOpts[inner] != null)return true;
		return false;
	}
	
	public boolean auxContains(int inner, int val) { //should be called conditionSatisfied()
		if(!asrtOpts(inner)) return false; 
		for(int i : auxOpts[inner])
			if(val == i)return true;
		return false;
	}
	
}
