package ChatBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import control.SParse;
import control.TestRun;
import types.Node;

/*the graph works as follows:
 * the statements are the nodes. the cues observed in the user responses are the edges.
 * at each node it will be looking for different queues. they will have to be pretty vague (reg exp might be of use here)
 * there will have to be a default path, and many of the paths will be parallel routes to the same goal (different branches that meet again at a nexus)
 * 
 */

public class ChatAI {
	public ArrayList<StateNode> sList;
	public int curr =0;
	public StateNode sn;

	public ChatAI() {
		SParse.init();
		sList = new ArrayList<StateNode>();
		init();
		
	}

	public void init() {		

		try (
				Scanner scn = new Scanner(new File("./src/statements/slist.txt"));
				) 
		{
			int count = 0;
			String str = null;
			while (scn.hasNextLine()) {
				str = scn.nextLine();
				boolean interNode = (Integer.parseInt(str.split(" ")[1])==1)?true:false;
				if(str.split(" ")[0] == "destinationreached") {
					System.out.println("initialized end node");
					sList.add(new StateNode(count++,str.split(" ")[0],interNode, true));
				}
				else {
					sList.add(new StateNode(count++,str.split(" ")[0],interNode, false));
				}
			}
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		makeStatement(sList.get(0).statement.getRandomOpt());
		sn = sList.get(0);
	}


	public StateNode getNextNode() {
		return null;
	}

	public void makeStatement(String str) {
		TestRun.addTextToWindow("Driver: " + str + "\n");
		TestRun.aiOutput.add(str );
	}

	public void generateResponse(String inp) {	
		if(curr == -1) { //ISSUE: im not sure how to trigger this upon the "destinationreached" statement 
			makeStatement("Thanks again for choosing EZ cabs hope to have you again soon!\n\n");
			sList.clear();//clear the statement list of possible statements
			curr = 0;//set the current statement to 0
			init();//reinitialize 
			return;//exit the generate response function
		}
		sn = sList.get(curr);
		//if a ride is coming to a close, initialize a new ride instance 
		if(!sn.interNode) {	//if the user is queued to make a decision (thus determining the link chosen)
			Node endVal = SParse.getPhraseTreeFromString(inp, 0, false);
			UserQueue next = sn.testInpForQueues(inp, endVal);
			if(next != null) {				
				curr = sn.testInpForQueues(inp, endVal).traverse();	
				sn = sList.get(curr);
				makeStatement(sList.get(curr).statement.getRandomOpt());
				while(curr != -1 && sn.interNode) {
					curr = sn.outgoingLinks.get(0).traverse();
					if(curr != -1) {
						sn = sList.get(curr); //in this case there is only 1 link, so the next node is assumed	
						makeStatement(sList.get(curr).statement.getRandomOpt());
					}
				}
			}
			else {
				//makeStatement(sList.get(curr).statement.getRandomOpt()); //repeat the question if a response is not entered
				//MarkII:begin Interlude Conversation. It will restart the recursive sequence and end the one that went awry.
				
				//eventually it should return some data which can be used to recall what was discussed if necessary. for now it will just instantiate itself.
				//perhaps the existing interlude conversation data can be passed as an argument to the constructor to prevent repetition and to enable greater 
				//synthesis of statements into an integrated "conversation". This would prevent high coupling (which would occur if simply reading a property for instance)
				
				int prev = curr;
				sn.interConv = new InterludeConversation(inp, sn);
				//sets the current index to whatever is indicated on the links page for the decision node in question. (its the number following 3, followed by 2 lines of text)
				curr = sn.destinationByDefault;
				//makeStatement(sList.get(curr).statement.getRandomOpt());
			}
		}		
		return;		
	}
	
	public void handleInput(String input) {
		//the interConv will only exist until it concludes, at which point control is returned to the normal flow.
		if(this.sn.interConv != null) {
			sn.interConv.interpretStatement(input);
			sn.interConv.nextMove(input);
		}
		else generateResponse(input);
	}
}
