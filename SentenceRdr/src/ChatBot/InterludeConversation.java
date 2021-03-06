package ChatBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import control.TestRun;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

//if the original chatbot doesnt know how to handle a reply, the stanfordAPI is employed to further analyze the user's input, creating a conversation within the conversation that will
//return the point at which it began in the original graph traversal, repeating the question (using the other form, or another if there are more than 2) that raised the exception.
public class InterludeConversation {
	private StateNode sn;
	static StanfordCoreNLP pipeline;
	static String[] lr = {"%0, ive been there before its beautiful!", "Yeah im familiar with %0 ill take you right there", "Oh, ive never been to %0 but let me know exactly where to turn", "Oh, I've never been to %0"};
	static String[] tr = {"%0, that must be interesting", "I always found the idea of being a %0 daunting", "You're a %0? Good for you."};
	static String[] pr = {"%0, that sounds familiar...\nWhere did I hear that name?","%0, is that a friend of yours?", "%0, now there's a fancy name"};
	static String[] or = {"I've heard good things about %0, what do they do again?","%0, I had a friend who did some work for them", "Seems you can't go a day without reading about %0 online..."};
	static String[] dr = {"%0, That's terrible!", "I am sympathetic. Few things are more devastating than %0", "Those poor people, %0 at a time like this"};
	static String[] nr = { "%0 makes me appreciate foreign culture"};
	static String[] negsentresp = {"Im sorry to hear that", "Well, theres no need to be negative about things", "You'll get over it", "If I had a nickle for every time I heard that..."};
	static String[] possentresp = {"Yeah I also enjoy it", "I agree, it's great!", "Glad to hear it!", "Well there ya go!"};
	static String[] neutsentresp = {"Well im sure youll grow to enjoy it", "Never been much of a fan myself", "What can ya do?"};
	static String[] noMatchResp = {"Fair enough", "Uh-huh", "Oh?", "Right...", "I guess", "Go on..."}; 
	public int[] sequenceOfResponses;
	public boolean matchedQueue = false;
	public int iterationsToDefault = 3;
	public InterludeConversation(String argS, StateNode sn) {
		this.sn = sn;
		if(pipeline == null) {
			init();
		}
		sequenceOfResponses = new int[7];
		sequenceOfResponses[0] = sn.priorityOfResponses;
		int opt = 0;
		for(int i = 1; i < sequenceOfResponses.length;i++) {
			if(opt==sn.priorityOfResponses)sequenceOfResponses[i] = ++opt;
			else sequenceOfResponses[i] = opt;
			opt++;
		}
	    /*if(argS != null && argS != "") {		    
	    	interpretStatement(argS);
	    }*/
	    
	    makeStatement(sn.nlpOpeningStmt,false);
	    
	}	
	
	//ends the conversation and removes it by setting the only reference to it to null
	public void concludeConversation() {
		makeStatement(sn.nlpClosingStmt,false);
		sn.movingOn(); 
	}
	
	//this is the entry point for text input to this flow of control (conversation)
	public boolean interpretStatement(String argS) {
		 // make an example document
	    CoreDocument doc = new CoreDocument(argS);
	    // annotate the document
	    pipeline.annotate(doc);
	    ArrayList<CoreSentence> sentences = (ArrayList<CoreSentence>) doc.sentences(); 
	    Map<Integer, CorefChain> graph = doc.annotation().get(CorefChainAnnotation.class);
	    List <CoreMap> sentCore = doc.annotation().get(SentencesAnnotation.class);
	    boolean matchedSomething = false;
	    //run through the array of priorities, matching each in turn if necessary.
	    for(int i = 0; i < this.sequenceOfResponses.length; i++) {
	    	if(matchedSomething)break;
	    	switch(this.sequenceOfResponses[i]) {
		    	case 6:for(CoreSentence sentence:sentences) { //might have to find some way to aggregate the sentences (if there is more than 1) before responding, just to keep the back and forth pattern intact
							String sentiment = sentence.sentiment();
							if(sentiment.equals("Positive")) {
								this.makeStatement(possentresp[(int)(Math.random()*neutsentresp.length)],false);
								matchedSomething = true; //the function exits if this is set to true;
							}
							if(sentiment.equals("Neutral")) {
								this.makeStatement(neutsentresp[(int)(Math.random()*neutsentresp.length)],false);
								matchedSomething = true; //the function exits if this is set to true;								
							}
							if(sentiment.equals("Negative")) {
								this.makeStatement(negsentresp[(int)(Math.random()*neutsentresp.length)],false);
								matchedSomething = true; //the function exits if this is set to true;								
							}							
						}
		    			break;
		    	case 0:	matchedSomething = chooseResponse("LOCATION",i,doc,sentCore, lr);
						break;
		    	case 2: matchedSomething = chooseResponse("TITLE",i,doc,sentCore, tr);
						break;	
		    	case 3:	matchedSomething = chooseResponse("PERSON",i,doc,sentCore, pr);
		    			break;	
		    	case 4:	matchedSomething = chooseResponse("CAUSE_OF_DEATH",i,doc,sentCore, dr);
		    			break;
		    	case 5:	matchedSomething = chooseResponse("NATIONALITY",i,doc,sentCore, nr);
		    			break;
		    	case 1: matchedSomething = chooseResponse("ORGANIZATION",i,doc,sentCore, or);
		    			break;
				//there is no default case, the loop continues to search in the particular order of priority predetermined for each node (default is sentiment first)
	    	}
	    }
	    if(!matchedSomething) {this.makeStatement(noMatchResp[(int)(Math.random()*neutsentresp.length)],false);}
		return matchedSomething; //this currently doesnt signal anything but might be useful information to pass on
	}
	
	public void nextMove(String argS, boolean mQ) {
		if(matchedQueue || this.iterationsToDefault == 0) {
			concludeConversation();		
		}
		else interpretStatement(argS);
	}
	
	//same as in the chatAI class
	//simpler version used by sentiment analysis to output text.
	public void makeStatement(String str,boolean interNode) {
        TestRun.addTextToWindow("Driver: " + str + "\n");
        if(!interNode) 
            TestRun.aiOutput.add(str);
        else
            TestRun.aiOutput.set(TestRun.aiOutput.size()-1,TestRun.aiOutput.get(TestRun.aiOutput.size()-1) + "\n" + str);
    }
	
	//this version is used with NER and includes functionality to insert the object of the statement into the response
	//it automatically treats each iteration of interlude banter as a separate piece of dialogue (unlike the internode)	
	
	public void makeStatement(String str, CoreEntityMention em, List <CoreMap> sentences) {
		String[] splitStr = str.split("%0");
		String pString = splitStr[0];
		String iStr = "";		
		//the NP doesnt apply to any individual element of the tree, it is another layer over top.
		 for (CoreMap sentence : sentences) {	
			iStr = "";
			Tree tree = sentence.get(TreeAnnotation.class);		
			if((iStr = findNEinTree(em.text(),tree)) != null)break;
			
		}
		if(iStr != null) { 
			for(int i = 1; i < splitStr.length;i++) {//just in case multiple mentions are added later
				pString += iStr + splitStr[i];
			}
		}
		else pString = "I don't even know what to say...";
		TestRun.addTextToWindow("Driver: " + pString + "\n");        
        TestRun.aiOutput.add(pString);
      
	}
	
	public String findNEinTree(String searchStr, Tree tree) { //expected behavior: return null only if none of the subtrees contains the ner text within a NP (should be never)
		String iStr = "";
		if(tree.value().equals("NP")) {
			for(Word wrd: tree.yieldWords()) {
				iStr += wrd.toString() + " ";
			}
			if(iStr.contains(searchStr))									
				return iStr.trim();
			
		}
		for(Tree subtree : tree) {
			if(!subtree.isLeaf() && !(subtree == tree) && (iStr = findNEinTree(searchStr, subtree)) != null) return iStr;
		}
		return null;
		
	}
	
	//this is a refactoring of the function calls found in the switch statement in interpretStatement() leading to NER related responses
	public boolean chooseResponse(String entType, int index, CoreDocument cDoc, List <CoreMap> sCore, String[] stmt) {
		if(cDoc != null && cDoc.entityMentions() != null) {
			boolean matchedSomething = false;
			int vStr = -1;
		    for (CoreEntityMention em : cDoc.entityMentions())
		    	//if(em.entityType().equals(entType) &&  (vStr = new edu.stanford.nlp.ling.Word(em.text()).value()) != "PRP" && vStr != "PRP$" ) {
		    	if(em.entityType().equals(entType) &&  (vStr = types.Word.getWordObj(em.text().toLowerCase()).sPart) != 15 && vStr != 17 ) {
		    		this.makeStatement(stmt[((int)Math.random())*stmt.length],em,sCore);
		    		if(this.sequenceOfResponses[index] == sn.priorityOfResponses)matchedQueue = true;//the interlude conversation ends if this is set to true
		    		matchedSomething = true; //the function exits if this is set to true;
		    		break;
		    	}
		    return matchedSomething;
		}
		return false;
	}
	
	public static void init() {
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions, parse, dcoref, sentiment");
	    // set up pipeline
	    pipeline = new StanfordCoreNLP(props);
	    
	}
}
