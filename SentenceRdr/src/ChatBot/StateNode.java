package ChatBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import control.TestRun;
import types.Node;

public class StateNode {
	public ArrayList<UserQueue> outgoingLinks;  //each of these links to another node. they should be ordered in the order of priority for the matching algorithm, ie. rarest first.
	public int id;
	public boolean interNode; //some nodes do not queue the user for speech, they join with other paths at the next decision node. this is true in such a case
	public String name;
	public Stmt statement;	
	public boolean isLastNode = false;
	public String nlpOpeningStmt, nlpClosingStmt;
	public int destinationByDefault;
	public InterludeConversation interConv;
	
	public StateNode(int id, String name, boolean interNode, boolean isLastNode) {
		this.id =id;
		this.name = name;
		this.interNode = interNode;
		this.isLastNode = isLastNode;
		statement = new Stmt(name);
		getQueues();
	}
	/*endpoint is 0 based.
	 * 
	 * 
	 * 
	 */
	public void getQueues() { //loads csv and pattern strings from files based on the name of the statement node
		outgoingLinks = new ArrayList<UserQueue>();		
		try (
			Scanner scn = new Scanner(new File("./src/statements/" + name + "_links.txt"));
		) {
			while (scn.hasNextLine()) {
				String temp1 = scn.nextLine();
				String[] temp11 = temp1.split(" ");
				int temp  = Integer.parseInt(temp11[0]);  //type of match:: 0 for csv, 1 for regex, 2 for interNode (no matching)
				int ep =Integer.parseInt(temp11[1]); 	// endpoint: id of node which this links to
				String temp2 = scn.nextLine(); //the match string (csv or regex)
				if(temp == 0) { //csv match
					int[][] csvMatch = new int[3][];  //Mode, Person, Voice. As few as 0 or as many as all of them can match.
					String[] matchList = temp2.split(";");
					for(int i = 0; i < matchList.length; i++) { //should be length 3
						String[] c = matchList[i].split(",");
						csvMatch[i] = new int[c.length];
						for(int j = 0; j < c.length; j++) {
							if(c[j].length() > 0)csvMatch[i][j] = Integer.parseInt(c[j]);
							else csvMatch[i][j] = -1;
						}
					}				
					outgoingLinks.add(new UserQueue(ep,id,null,csvMatch));
				}
				else if(temp == 1) {
					outgoingLinks.add(new UserQueue(ep,id,temp2,null));
				}
				else if(temp == 3) {//this stores the data needed to begin the NLP interlude conversation for this node
					this.destinationByDefault = ep;
					this.nlpOpeningStmt = temp2;
					this.nlpClosingStmt = scn.nextLine();
				}
				else {
					outgoingLinks.add(new UserQueue(ep,id,null,null));
				}
			}
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	//could use an array of int values to test for more complex queues such as the result of the speech being parsed
	public UserQueue testInpForQueues(String input, Node endVal) {
		for(UserQueue uq : outgoingLinks) {
			if(uq.matchesQueue(input, endVal) ) {
				return uq;
			}
		}
		return null;
	}
	
	public void movingOn() {
		this.interConv = null;
		ChatAI AI = TestRun.getAI();
		//after concluding the interlude conversation, a question must be posed to restart the dialogue.
		AI.makeStatement(AI.sList.get(AI.curr).statement.getRandomOpt());
	}
	
	
	
}
