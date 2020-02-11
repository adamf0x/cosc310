package control;
import java.util.Arrays;
import types.*;

//the rules for parsing the next word might depend on the word preceding it (or following it)

public class WordFactory {
	public static String[] Pronouns;
	public static String[] Auxiliaries;
	public static String[] Prepositions;
	public static String[] actionVerbs;
	public static String[] specialVerbs;
	public static String[] conjunctions;
	public static String[] determiners;
	
	public WordFactory() {
		Arrays.sort(Pronouns);
		Arrays.sort(Auxiliaries);
		Arrays.sort(Prepositions);
		Arrays.sort(actionVerbs);
		Arrays.sort(specialVerbs);
		
		Arrays.sort(conjunctions);
		Arrays.sort(determiners);

	}
	
	/*public static Node getNode(String word) {
		String tw = word.toLowerCase();
		if(matchWord(Pronouns, tw))return new Pronoun(word);
		else if(matchWord(Auxiliaries, tw))return new Auxiliary(word);
		else if(matchWord(Prepositions, tw))return new Preposition(word);
		else if(matchWord(specialVerbs, tw))return new Verb(word);
		else if(matchWord(conjunctions, tw))return new Conjunction(word);
		else if(matchWord(determiners, tw))return new Determiner(word);

		else {
			for(String s: actionVerbs) {
				if(tw.contains(s))return new Verb(word);
			}
		}
		
		
		//return new Noun(word);
	}
	
	public static boolean matchWord(String[] list, String word) {
		for(String s: list) {
			if(word.equals(s))return true;
		}
		return false;
	}
	*/
}
