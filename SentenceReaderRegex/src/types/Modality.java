package types;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//word double array is an array for each word in the mode, the inner array being the possibilities for the person of the subject
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//have to figure out how to integrate the word "to" in the "had to" and "have to" modes
//could add a property to verb nodes to indicte that the infinitive is to be used (and thus it should be preceded by "to")
public class Modality {
	static ArrayList<Mode> mList = new ArrayList<Mode>(Arrays.asList(new Mode[]{new Mode("present", new Word[][] {new Word[] {new Word(3), new Word(4)}}),
																				new Mode("past",new Word[][] {new Word[] {new Word(7),new Word(9)}}),
																				new Mode("present continuous",new Word[][] {new Word[]{Word.getWordObj("is"), Word.getWordObj("are")},new Word[] {new Word(8)}}),
																				new Mode("past continuous",new Word[][] {new Word[]{Word.getWordObj("was"), Word.getWordObj("were")},new Word[] {new Word(8)}}),
																				new Mode("present perfect",new Word[][] {new Word[]{Word.getWordObj("have")},new Word[]{Word.getWordObj("to")},new Word[]{new Word(3), new Word(4)}}),
																				new Mode("past perfect",new Word[][] {new Word[]{Word.getWordObj("had")},new Word[]{Word.getWordObj("to")},new Word[]{new Word(3), new Word(4)}}),
																				new Mode("simple future",new Word[][] {new Word[]{Word.getWordObj("will")},new Word[] {new Word(3), new Word(4)}}),
																				new Mode("conditional present",new Word[][] {new Word[]{Word.getWordObj("would")},new Word[] {new Word(3), new Word(4)}}),
																				new Mode("conditional past",new Word[][] {new Word[]{Word.getWordObj("would")},new Word[] {Word.getWordObj("have")},new Word[] {new Word(7),new Word(9)}}),
																				new Mode("indefinite conditional present",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")},new Word[] {new Word(3), new Word(4)}}),
																				new Mode("indefinite conditional past",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")},new Word[] {Word.getWordObj("have")},new Word[] {new Word(7),new Word(9)}}),
																				new Mode("indefinite conditional future",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")},new Word[] {Word.getWordObj("have")}, new Word[] {new Word("to")},new Word[] {new Word(3),new Word(4)}}),
																				new Mode("continuous conditional present",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")}, new Word[] {new Word("be")}, new Word[] {new Word(8)}}),
																				new Mode("continuous conditional past",new Word[][] {new Word[]{Word.getWordObj("could"), Word.getWordObj("might")},new Word[] {Word.getWordObj("have")},new Word[] {new Word("been")},new Word[] {new Word(8)}}),
																				new Mode("perfect participle",new Word[][] {new Word[]{Word.getWordObj("having")},new Word[] {new Word(7)}}),
	})); 
	//can add possessive modes/voices as well. adding 's to indicate possession will have to be taken into account
	static ArrayList<Person> pList = new ArrayList<Person>(Arrays.asList(new Person[]{	new Person(Word.getWordObj("I"), "first person, singular"),
																						new Person(Word.getWordObj("You"), "second person"),
																						new Person(Word.getWordObj("He"), "third person singular (M)", 1),
																						new Person(Word.getWordObj("She"), "third person singular (F)", 2),																						
																						new Person(Word.getWordObj("We"), "first person plural"),
																						new Person(Word.getWordObj("They"), "third person plural"),
																						new Person(Word.getWordObj("It"), "third person singular"),
																						new Person(Word.getWordObj("My"), "first person singular possessive", 0, 1),
																						new Person(Word.getWordObj("Your"), "second person possessive", 0, 1),
																						new Person(Word.getWordObj("His"), "first person possessive (M)", 1,1),
																						new Person(Word.getWordObj("Her"), "first person possessive (F)", 2,1),
																						new Person(Word.getWordObj("Our"), "first person possessive", 0, 1),
																						new Person(Word.getWordObj("Their"), "first person possessive", 0, 1),
																						new Person(Word.getWordObj("Everyone"), "third person universal"),
																						new Person(Word.getWordObj("Someone"), "third person existential"),
																						new Person(Word.getWordObj("Who"), "third person interrogative", 0, 2),
																						new Person(Word.getWordObj("Which"), "nonpersonal interrogative", 0, 2),
																						new Person(Word.getWordObj("Where"), "spatial interrogative", 0, 2),
																						new Person(Word.getWordObj("When"), "temporal interrogative", 0, 2),
																						new Person(Word.getWordObj("Why"), "general interrogative", 0, 2), //this replaces a sentence, not a noun
																						new Person(Word.getWordObj("How"), "descriptive interrogative", 0, 2), //this replaces a sentence, not a noun
	
	}));
	
	
	//the imperative voice is always referring to the second person (singular or plural), and always uses the present tense/ simple mode
	static Voice[] vList = new Voice[] {new Voice("Declarative", "zu?y"),
										new Voice("Interrogative", "qus?"), 
										new Voice("Imperative", "yz?")
	};
	
	public static ArrayList<String> mNames, fNames;
	
	public static boolean readBabyNames() {
		mNames = new ArrayList<String>();
		fNames = new ArrayList<String>();
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("./src/words/babynames_sum.csv")))){
			String inStr = in.readLine();
			String[] prevVals = null;
			while((inStr = in.readLine())!= null) {
				String[] vals = inStr.split(",");
				if(prevVals!=null && prevVals[0].compareTo(vals[0])==0) {
					if(Integer.parseInt(prevVals[1]) > Integer.parseInt(vals[1]))
						fNames.add(prevVals[0].toLowerCase());
					else
						mNames.add(vals[0].toLowerCase());					
				}
				else {
					if(vals[2].compareTo("F")==0)
						fNames.add(vals[0].toLowerCase());
					else
						mNames.add(vals[0].toLowerCase());
				}
				prevVals = vals;
			}
			
			
		}catch(IOException e) {
			return false;
		}
		
		
		return true;
	}
	//I have to pass it the arguments taken from the auxiliary and the verb phrase. there should be one mode per auxiliary or one for any sentence without auxiliary
	
	public static Mode getModality(Sentence sentence) {
		//I will have to advance the words in the sentence in the first loop,along with the wA index. (to match it)
		Mode rVal = null;
		boolean b1 = true;
		int auxLength = 0;
		AuxiliaryPhrase ap = null;
		if(sentence.children.get(0) instanceof Sentence || sentence.children.size() < 2)return null; //has to be called on individual (leaf) sentences
		
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
	
	public static Person getPerson(Sentence sentence) {
		
		Noun n = null;
		if(sentence.getSubject() != null && (n=sentence.getSubject().getNoun())!=null) {
			for(Person p : Modality.pList) {
				if(n.val.compareTo(p.pronoun) == 0)return p; //specific pronoun listed above
			}
			if(n.val.getVal().toLowerCase().compareTo("man")==0 || getSex(n.val.getVal().toLowerCase()) == 1)return pList.get(2);  //he
			else if(n.val.getVal().toLowerCase().compareTo("woman")==0 || getSex(n.val.getVal().toLowerCase()) == 2)return pList.get(3);  //she
			else if(n.val.getVal().endsWith("s")) return pList.get(5);  //they
			else return pList.get(6);	//it
		}	
		return null;
	}
	
	public static Voice getVoice(Sentence sentence) {
		for(Voice v : vList) {
			Pattern endP = Pattern.compile(v.ptrn);
			Matcher endM = endP.matcher(sentence.getChildSymbolString());
			if(endM.matches())return v;
		}
		
		return null;
	}
	
	public static int getSex(String name) { //0 for unknown, 1 for male, 2 for female
		if(mNames.contains(name))return 1;
		else if (fNames.contains(name))return 2;
		return 0;
	}
	
	
	public static boolean match(Word m1, Word[] m2) { //match a single word as first argument with any Word in second argument
		for(Word w2: m2) {
			if(w2.getVal()=="any" && w2.sPart == m1.sPart)return true;
			else if(w2.getVal().compareTo(m1.getVal())==0)return true;
		}
		
		return false;
	}
	
	
}
