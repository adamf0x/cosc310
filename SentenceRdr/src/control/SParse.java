package control;
import types.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class SParse {	
	public static Scanner scn;	
	public static LinkedList<Word> wList;
	public static Word[] wListA;

	
	public static void main(String[] args) {		
		//getTextFromDB();		
		//writeWordCSV();
		
		readWordCSV();
		Modality.readBabyNames();
		//String testStr = "A man had to go to the park with his dog";//works
		//String testStr = "john will pay him at 1230";
		//String testStr = "Pay him tomorrow";
		//String testStr = "run"; //works
		//String testStr = "hello";
		String testStr = "hi";
		
		//String testStr = "Lively little John drove in a car to the park carelessly but he fell and hurt his hand";//works
		
		Node endVal = getPhraseTreeFromString(testStr.toLowerCase(), 0, true);
		if(!(endVal instanceof Sentence))endVal = new Sentence(new ArrayList<Node>(Arrays.asList(endVal)));
		Mode testMode = null;
		Person testPerson = null;
		Voice testVoice = null;
		//change it so tht instead of returning the modality, etc it just mutates the sentence, so that the function can execute for compound sentences recursively
		if((testMode = Modality.getModality((Sentence)endVal)) != null)System.out.println("\n\nmodality : " + testMode.toString());
		if((testPerson = Modality.getPerson((Sentence)endVal)) != null)System.out.println("\n\nperson : " + testPerson.toString());
		if((testVoice = Modality.getVoice((Sentence)endVal)) != null)System.out.println("\n\nvoice : " + testVoice.toString());
		else { System.out.println("\n" + ((Sentence)endVal).getChildSymbolString());}
		
		int test = 0;
		test ++;
	}
	/*The sentences will have to be translated into events that the "Chatbot" can interpret. a sentence with a compound subject, action or object 
	*might be resolved into 2 or more events, which could be ordered relative to one another temporally. 
	*The voice (inquisitive vs imperative or declarative should be determined in a class like the modality class, along with the person)
	*thus the plural of a noun should be recognized as a certain "person" which can then be expressed using the appropriate pronoun.
	*
	*/
	
	
	public static void init() {
		readWordCSV();
		Modality.readBabyNames();
	}
	
	public static void readWordCSV() {
		try (
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./src/words/objectlist.txt")));
			) {
				wList = new LinkedList<Word>();
				String ln = "";
				Word prevWord = null;
				while((ln = br.readLine())!=null) {	
					String[] wrds = ln.split(",");
					if(wrds.length<3)continue;
					if(prevWord == null || !prevWord.getVal().equals(wrds[0].toLowerCase())) {
						if(prevWord!=null)prevWord.guessPart();
						wList.add(prevWord = new Word(wrds[0].toLowerCase(), wrds[2].toLowerCase(), wrds[1].toLowerCase()));												
					}
					else {
						prevWord.addDef(wrds[2].toLowerCase(), wrds[1].toLowerCase());
					}
				}
				
			}catch (IOException e) {			
				e.printStackTrace();
			}
		wListA = new Word[wList.size()];
		 wList.toArray(wListA);
	}
	
	public static Node getPhraseTreeFromString(String str, int level, boolean verboss) {	
		String[] temp = str.split(" ");
		ArrayList<Node> nList = new ArrayList<Node>(temp.length);
		Word tW = null;
		Node n = null;
		for(String s : temp) {			
			tW = Word.getWordObj(s);
			switch(tW.sPart) {
				case 0: n = new Preposition(tW);break;
				case 1: n = new Noun(tW);break;
				case 2: n = new Adjective(tW);break;
				case 3: n = new Verb(tW);break;
				case 4: n = new Verb(tW);break;
				case 5: n = new Adverb(tW);break;
				case 6: n = new Verb(tW);break;
				case 7: n = new Verb(tW);break;
				case 8: n = new Noun(tW);break;
				case 9: n = new Verb(tW);break;
				case 12: n = new Determiner(tW);break;
				case 13: n = new Interjection(tW);break;
				case 14: n = new Noun(tW);break;
				case 11: n = new Noun(tW);break;
				case 15: n = new Pronoun(tW);break;
				case 16: n = new Conjunction(tW);break;
				case 20: n = new Auxiliary(tW);break;
				case 21: n = new Interrogative(tW);break;
			}
			nList.add(n);
		}
		//need to change any ambiguous words like dog into nouns in the case that they are used after a possessive pronoun
		
		nList = adjustWordType(nList);
		
		
		//hopefully now it wont make phrases out of single adjectives and adverbs.
		// Patterns reversed char-wise, listed in order as follows:
		//interjection,auxiliary,adverb,adjective, preposition,noun,verb,sentence
		String[] pList = {	"o+",
							"b?p+",
							"([bx]?dc+|[bx]dc*|v(lv)+)",
							"([bx]?cd+|[bx]cd*|w(lw)+)",
							"([akzm]b|x(lx)+)",
							"([bx]?[ma][wc]*[ne]?|z(lz)+)",
							"([dv]*[bx]?[akzm]?[f-j]|y(ly)+)",
							"(z|t|u?yz|yu?z|^yz?u?$|s(ls)+)",   				//interrogative, declarative, conditional, compound sentence
							};
		String pStr = new StringBuilder(getRCharStringFromList(nList)).reverse().toString();
		if(verboss)System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t" + pStr);
		Pattern endP = Pattern.compile("^[wxyz]+$");
		Matcher endM = endP.matcher(pStr);
		int count = 0;
		while(!endM.matches() && count++ < 20) {//while anything other than a phrase [w-z] char is found in pStr
			for(int i = 0; i < pList.length; i++) {		
				Pattern ptrn = Pattern.compile(pList[i]); 
				Matcher m = ptrn.matcher(pStr);
				if(m.find()) {
					if(verboss)System.out.print("\n\nfound match: start: " +m.start() + ", end: "+ m.end() + " with pattern#: " + i + "\t");
					int len = m.end() - m.start();
					Phrase phr = null;
					ArrayList<Node> tempList = new ArrayList<Node>(len);
					int lSize = nList.size()-1;
					for(int j = 0; j < len; j++) {
						tempList.add(0,nList.remove(lSize - m.start()-j));
					}
					switch(i) {	case 0: phr = new InterjectionPhrase(tempList);break;
								case 1: phr = new AuxiliaryPhrase(tempList);break;
								case 2:	 phr = new AdverbPhrase(tempList);break;
								case 3:  phr = new AdjectivePhrase(tempList);break;
								case 4:  phr = new PrepPhrase(tempList);break;
								case 5:  phr = new NounPhrase(tempList);break;
								case 6:  phr = new VerbPhrase(tempList);break;	
								case 7:  phr = new Sentence(tempList);break;
					}
					nList.add(nList.size()-m.start(), phr);
					pStr = new StringBuilder(getRCharStringFromList(nList)).reverse().toString();
					if(verboss)System.out.print(Arrays.toString(nList.toArray())+ " :: " + pStr + ", i = " + i + ", count = " + count);
					break;
				}
				
			}
		}
		
		
		return nList.get(0);
		
	}
	
	public static ArrayList<Node> adjustWordType(ArrayList<Node> nList) {
		Node cN=null, pN=null;
		for(int i = 0; i < nList.size();i++) {
			cN = nList.get(i);
			if(pN!=null) {
				if(pN instanceof Determiner || pN.symbol == 'n')
					if(cN.val.getPartWithDef(1)==1) {
						nList.remove(i);
						nList.add(i, new Noun(cN.val));						
					}		
			}
			pN = cN;
		}
		return nList;
	}
	
	
	public static String getRCharStringFromList(ArrayList<Node> nList) {
		String rVal = "";
		for(Node n: nList) {
			rVal += n.symbol;
		}
		return rVal;
	}
	
	
	/*public static Word getWordObj(String str) {
		for(Word w: wList) {
			if(w.getVal().compareTo(str)==0)return w; //change to clone later
		}
		return null;
	}*/

	public static void printNodes() {
		
	}
	
	public static void writeWordFile() {
		try (
				BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./src/words/objectlist.txt")));
			) {
				for(Word w: wList) {
					br.write(w.toString());
				}
				
				
			}catch (IOException e) {			
				e.printStackTrace();
			}		
	}
}


