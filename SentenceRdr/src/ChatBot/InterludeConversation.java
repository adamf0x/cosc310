package ChatBot;

import java.util.Properties;

import control.TestRun;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

//if the original chatbot doesnt know how to handle a reply, the stanfordAPI is employed to further analyze the user's input, creating a conversation within the conversation that will
//return the point at which it began in the original graph traversal, repeating the question (using the other form, or another if there are more than 2) that raised the exception.
public class InterludeConversation {
	private StateNode sn;
	static StanfordCoreNLP pipeline;
	public InterludeConversation(String argS, StateNode sn) {
		this.sn = sn;
		if(pipeline == null) {
			init();
		}
	    if(argS != null && argS != "") {		    
	    	interpretStatement(argS);
	    }
	    
	    makeStatement(sn.nlpOpeningStmt);
	    
	}	
	
	//ends the conversation and removes it by setting the only reference to it to null
	public void concludeConversation() {
		makeStatement(sn.nlpClosingStmt);
		sn.movingOn(); 
	}
	
	//this is the entry point for text input to this flow of control (conversation)
	public void interpretStatement(String argS) {
		 // make an example document
	    CoreDocument doc = new CoreDocument(argS);
	    // annotate the document
	    pipeline.annotate(doc);
	    //debug code
		if(doc != null && doc.entityMentions() != null) 
		    for (CoreEntityMention em : doc.entityMentions())
		    	System.out.println("\tdetected entity: \t"+em.text()+"\t"+em.entityType());
		
		
	}
	
	public void nextMove(String argS) {
		if(argS.equals("end"))concludeConversation();
		else makeStatement("go on....");
	}
	
	//same as in the chatAI class
	public void makeStatement(String str) {
		TestRun.addTextToWindow("Driver: " + str + "\n");
	}
	
	public static void init() {
		Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
	    // set up pipeline
	    pipeline = new StanfordCoreNLP(props);
	}
}
