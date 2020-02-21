package control;
import types.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dbconn.mysqlConnHelper;

import java.sql.Statement;
import java.sql.ResultSet;

public class SParse {	public static Scanner scn;
	
	
	public static LinkedList<Word> wList;
	public static Word[] wListA;
	static Connection conn;
	
	public static void main(String[] args) {		
		//getTextFromDB();		
		//writeWordCSV();
		
		readWordCSV();
		//String testStr = "A man had to go to the park with his dog";//works
		String testStr = "John could have paid him tomorrow";
		//String testStr = "Lively little John drove in a car to the park carelessly but he fell and hurt his hand";//works
		
		Node endVal = getPhraseTreeFromString(testStr.toLowerCase(), 0);
		Mode testMode = null;
		if((testMode = Modality.getModality((Sentence)endVal)) != null)System.out.println("\n\nmodality : " + testMode.toString());
		
		int test = 0;
		test ++;
	}
	//it appears that the definitions are not being recorded with multiple parts on the text file
	public static void getTextFromDB() {
		conn = mysqlConnHelper.getConnection();
		//verb phrase is the top level as far as phrases are concerned. these can contain other verb phrases, or noun and prep. phrases
		//noun phrases and prepositional phrases can recursively contain one another
		//sentences must contain a NP and a VP. AUXillary verbs are optional and change the tense, voice, etc
		//
		 wList = new LinkedList<Word>();
		 Word prevWord = null;
		 
		
		try (
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM entries WHERE LENGTH(wordtype) > 0 ORDER BY word ASC, wordtype ASC");
		){
			
			while(rs.next()) {
				if(prevWord == null || !prevWord.getVal().equals(rs.getNString(1).toLowerCase())) {
					if(prevWord!=null)prevWord.guessPart();
					wList.add(prevWord = new Word(rs.getNString(1).toLowerCase(), rs.getNString(3).toLowerCase(), rs.getNString(2).toLowerCase()));					
				}
				else {
					prevWord.addDef(rs.getNString(3).toLowerCase(), rs.getNString(2).toLowerCase());
				}
			}
			
			
		}catch(SQLException ex) {
			 System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());

		}finally {
			
		}
		wListA = new Word[wList.size()];
		 wList.toArray(wListA);
	}
	
	public static void writeWordObjects() { //broken
		//this part writes it to the file
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
	
	public static void writeWordCSV() {
		conn = mysqlConnHelper.getConnection();
		//verb phrase is the top level as far as phrases are concerned. these can contain other verb phrases, or noun and prep. phrases
		//noun phrases and prepositional phrases can recursively contain one another
		//sentences must contain a NP and a VP. AUXillary verbs are optional and change the tense, voice, etc
		//
		 wList = new LinkedList<Word>();
		 Word prevWord = null;
		 
		
		try (
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM entries WHERE LENGTH(wordtype) > 0 ORDER BY word ASC, wordtype ASC");
		){
			try (
					BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./src/words/objectlist.txt")));
				) {
				while(rs.next()) {					
					br.write(rs.getNString(1).toLowerCase().replace('\n', ' ') +","+ rs.getNString(2).toLowerCase().replace('\n', ' ')+","+ rs.getNString(3).toLowerCase().replace('\n', ' ')+"\n");	
			}
			}catch (IOException e) {			
				e.printStackTrace();
			}
		}catch(SQLException ex) {
			 System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());

		}finally {
			
		}
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
	
	
	public static void loadTextFromObjects() { //broken
		try (
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("./src/words/objectlist.txt")));
			) {
				wList = new LinkedList<Word>();
				String ln = "";
				Word prevWord = null;
				while((ln = br.readLine())!=null) {
					if(ln.charAt(0)=='@') {
						wList.add((prevWord= new Word(ln.substring(1,ln.indexOf(':')))));
					}
					else if(ln.charAt(0)=='>') {	
						String dStr = ln;
						while((ln = br.readLine())!=null && ln.charAt(ln.length()-1)!='*') dStr+=ln;
						prevWord.addDef(dStr.substring(dStr.indexOf(':')+2) , dStr.substring(2,dStr.indexOf('<')).trim());
					}
				}
				
				
			}catch (IOException e) {			
				e.printStackTrace();
			}
		
	}
	
	
	
	public static Node getPhraseTreeFromString(String str, int level) {	
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
				case 14: n = new Noun(tW);break;
				case 11: n = new Noun(tW);break;
				case 15: n = new Pronoun(tW);break;
				case 16: n = new Conjunction(tW);break;
				case 20: n = new Auxiliary(tW);break;
			}
			nList.add(n);
		}
		//need to change any ambiguous words like dog into nouns in the case that they are used after a pronoun
		
		nList = adjustWordType(nList);
		
		
		//hopefully now it wont make phrases out of single adjectives and adverbs. tht should be the standard for all types.
		// Patterns reversed char-wise, listed in order as follows:
		//adverb,adjective, preposition,noun,verb,sentence
		String[] pList = {	"b?p+",
							"([bx]?dc+|[bx]dc*|v(lv)+)",
							"([bx]?cd+|[bx]cd*|w(lw)+)",
							"([akzm]b|x(lx)+)",
							"([bx]?[ma][wc]*[ne]?|z(lz)+)",
							"([dv]*[bx]?[akzm]?[f-j]|y(ly)+)",
							"(u?yz|yu?z|yzu?|s(ls)+)",   				//inquisitive, declarative, conditional, compound sentence
							};
		String pStr = new StringBuilder(getRCharStringFromList(nList)).reverse().toString();
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t" + pStr);
		Pattern endP = Pattern.compile("^[wxyz]+$");
		Matcher endM = endP.matcher(pStr);
		int count = 0;
		while(!endM.matches() && count++ < 20) {//while anything other than a phrase [w-z] char is found in pStr
			for(int i = 0; i < pList.length; i++) {		
				Pattern ptrn = Pattern.compile(pList[i]); 
				Matcher m = ptrn.matcher(pStr);
				if(m.find()) {
					System.out.print("\n\nfound match: start: " +m.start() + ", end: "+ m.end() + " with pattern#: " + i + "\t");
					int len = m.end() - m.start();
					Phrase phr = null;
					ArrayList<Node> tempList = new ArrayList<Node>(len);
					int lSize = nList.size()-1;
					for(int j = 0; j < len; j++) {
						tempList.add(0,nList.remove(lSize - m.start()-j));
					}
					switch(i) {	case 0: phr = new AuxiliaryPhrase(tempList);break;
								case 1:	 phr = new AdverbPhrase(tempList);break;
								case 2:  phr = new AdjectivePhrase(tempList);break;
								case 3:  phr = new PrepPhrase(tempList);break;
								case 4:  phr = new NounPhrase(tempList);break;
								case 5:  phr = new VerbPhrase(tempList);break;	
								case 6:  phr = new Sentence(tempList);break;
					}
					nList.add(nList.size()-m.start(), phr);
					pStr = new StringBuilder(getRCharStringFromList(nList)).reverse().toString();
					System.out.print(Arrays.toString(nList.toArray())+ " :: " + pStr + ", i = " + i + ", count = " + count);
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


