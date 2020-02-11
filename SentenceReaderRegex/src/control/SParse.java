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
	static Connection conn;
	
	public static void main(String[] args) {		
		getTextFromDB();
		//SParse.loadTextFromCSV();
		//String testStr = "A man went to the park with his dog";//works
		String testStr = "A man in a car drove to the park";//works
		
		Node endVal = getPhraseTreeFromString(testStr.toLowerCase(), 0);
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
	
	
	public static void loadTextFromCSV() {
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
			
			tW = getWordObj(s);
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
			}
			nList.add(n);
		}
		//I have to add more patterns, to try to match the more restrictive versions of each first, then gradually work down into the less so. Restart every time
		// a match is made? try first without
		
		String[] pList = {	"[bx]?cd*",
							"[akzm]b",
							"[bx]?[ma]c*[ne]?",							
							"d*[bx]?[akzm]?[h-j]",							
							"yz"};
		String pStr = new StringBuilder(getRCharStringFromList(nList)).reverse().toString();
		System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t" + pStr);
		Pattern endP = Pattern.compile("^[wxyz]+$");
		Matcher endM = endP.matcher(pStr);
		int count = 0;
		while(!endM.matches() && count++ < 8) {//while anything other than a phrase [w-z] char is found in pStr
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
					switch(i) {	case 0:  phr = new AdjectivePhrase(tempList);break;
								case 1:  phr = new PrepPhrase(tempList);break;							
								case 2:  phr = new NounPhrase(tempList);break;
								case 3:  phr = new VerbPhrase(tempList);break;								
								case 4:  phr = new Sentence(tempList);break;
					}
					nList.add(nList.size()-m.start(), phr);
					pStr = new StringBuilder(getRCharStringFromList(nList)).reverse().toString();
					System.out.print(Arrays.toString(nList.toArray())+ " :: " + pStr + ", i = " + i + ", count = " + count);
					break;
				}
				
			}
		}
		
		int test = 0;
		test++;
		return n;
		
	}
	
	public static String getRCharStringFromList(ArrayList<Node> nList) {
		String rVal = "";
		for(Node n: nList) {
			rVal += n.symbol;
		}
		return rVal;
	}
	
	public static Word getWordObj(String str) {
		for(Word w: wList) {
			if(w.getVal().compareTo(str)==0)return w; //change to clone later
		}
		return null;
	}

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


