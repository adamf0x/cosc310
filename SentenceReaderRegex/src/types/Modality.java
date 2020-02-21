package types;
//word double array is an array for each word in the mode, the inner array being the possibilities for the person of the subject
import java.util.ArrayList;
import java.util.Arrays;

public class Modality {
	static ArrayList<Mode> mList = new ArrayList<Mode>(Arrays.asList(new Mode[]{new Mode("present", new Word[][] {new Word[] {new Word(3), new Word(4)}}),
																				new Mode("past",new Word[][] {new Word[] {new Word(7)}}),
																				new Mode("present continuous",new Word[][] {new Word[]{Word.getWordObj("is"), Word.getWordObj("are")},new Word[] {new Word(8)}}),
																				new Mode("past continuous",new Word[][] {new Word[]{Word.getWordObj("was"), Word.getWordObj("were")},new Word[] {new Word(8)}}),
																				new Mode("present perfect",new Word[][] {new Word[]{Word.getWordObj("have")},new Word[]{new Word(7)}}),
																				new Mode("past perfect",new Word[][] {new Word[]{Word.getWordObj("had")},new Word[]{new Word(7)}}),
																				new Mode("simple future",new Word[][] {new Word[]{Word.getWordObj("will")},new Word[] {new Word(3), new Word(4)}}),
																				new Mode("conditional present",new Word[][] {new Word[]{Word.getWordObj("would")},new Word[] {new Word(3), new Word(4)}}),
																				new Mode("conditional past",new Word[][] {new Word[]{Word.getWordObj("would"),Word.getWordObj("have")},new Word[] {new Word(7)}}),
																				new Mode("indefinite conditional present",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")},new Word[] {new Word(3), new Word(4)}}),
																				new Mode("indefinite conditional past",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")},new Word[] {Word.getWordObj("have")},new Word[] {new Word(7),new Word(9)}}),
																				new Mode("perfect participle",new Word[][] {new Word[]{Word.getWordObj("having")},new Word[] {new Word(7)}}),
	})); 
	
	
	//I have to pass it the arguments taken from the auxiliary and the verb phrase. there should be one mode per auxiliary or one for any sentence without auxiliary
	
	public static Mode getModality(Sentence sentence) {
		//I will have to advance the words in the sentence in the first loop,along with the wA index. (to match it)
		Mode rVal = null;
		boolean b1 = true;
		int auxLength = 0;
		AuxiliaryPhrase ap = null;
		if(sentence.children.get(0) instanceof Sentence)return null; //has to be called on individual (leaf) sentences
		if((ap=sentence.getAuxPhrase())!=null){auxLength = ap.children.size();}
		Word[] matchThis = new Word[auxLength+1];
		for(int i = 0; i < auxLength; i++) {
			matchThis[i] = ap.children.get(i).val;
		}
		matchThis[auxLength] = ((Phrase) sentence.children.get((auxLength > 0) ? 2 : 1)).children.get(0).val ;
		Word wMatch = null; //this is the current word in the sentence being matched
		for(Mode m : mList) {
			b1 = false; //if a match is made, this will be true at the end
			boolean b2 = true;			
			if(m.rep.length != matchThis.length)continue;
			for(int i = 0; i < matchThis.length;i++) {
				if(match(matchThis[i],m.rep[i])== false)b2 = false; //remains true if one of the words in the array matches
				
			}
			if(b2) {b1 = true;rVal = m; break;}
		}
		if(b1)return rVal;
		return null;
	}
	
	public static boolean match(Word m1, Word[] m2) { //match a single word as first argument with any Word in second argument
		for(Word w2: m2) {
			if(w2.getVal()=="any" && w2.sPart == m1.sPart)return true;
			else if(w2.getVal().compareTo(m1.getVal())==0)return true;
		}
		
		return false;
	}
	
	
}
