package ChatBot;

import java.util.ArrayList;
import java.util.Arrays;

import types.Modality;
import types.Node;
import types.Noun;
import types.NounPhrase;
import types.PrepPhrase;
import types.Sentence;

public class rObj { //readable object. 
	public String name;
	public int type; //if its a person, place, thing other than these (1,2,3). this will be determined by the statements hopefully
	public ArrayList<Prop> props; //properties.
	
	
	public rObj(String n, Prop[] p) {
		name = n;
		props = new ArrayList<Prop>(Arrays.asList(p));
	}
	public rObj(String n, Prop[] p, int t) {
		name = n;
		props = new ArrayList<Prop>(Arrays.asList(p));
		type = t;
	}
	
	public static int getTypeOfSubject(Sentence s) {
		if(s.getChildSymbolString().contains("z")) {
			Noun subj = null;
			NounPhrase nP = null;
			for(Node n : s.getChildren()) {
				if (n instanceof NounPhrase) {
					nP = (NounPhrase) n;
					subj = nP.getNoun();
					break;
				}
			}
			if (subj == null)return -1;
			
			return getTypeOfNoun(subj, nP); //found a subject and return type
			
		}
		return -1; //no subject phrase
	}
	
	public static int getTypeOfNoun(Noun subj, NounPhrase np) {
		if(subj.val.locateInDef("proper name") != null && Modality.isHumanName(subj.val.getVal()) == true) {
			return 1; //person
		}
		
		else if(subj.val.locateInDef("proper name") != null && Modality.isHumanName(subj.val.getVal()) == false) {
			return 2; //place
		}
		/*else {
			PrepPhrase prepV = np.getPrepPhrase();
			if(prepV != null) {
				String pStr = prepV.getPreposition().val.getVal();
			}
		}*/
		
		return 0; //does not conclude. some object might be a place or a person
	}
	
	public void addProp(Prop p) {
		props.add(p);
	}
	
	public Prop getPropWDesc(String fStr) { //returns first result that matches the description. second argument is the nth match
		for(int i = 0;  i < props.size(); i++) {
			if(props.get(i).descriptor.contains(fStr)) {				
				return props.get(i);
			}				
		}
		return null;
	}
	public Prop getPropWName(String fStr) { //returns first result that matches the description. second argument is the nth match
		for(int i = 0;  i < props.size(); i++) {
			if(props.get(i).name.contains(fStr)) {				
				return props.get(i);
			}
			else if(fStr.contains(props.get(i).name)) {				
				return props.get(i);
			}				
		}
		return null;
	}
}
