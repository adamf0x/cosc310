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
	public String currInternodeText;
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
		makeStatement(sList.get(0).statement.getRandomOpt(), false);
		sn = sList.get(0);
	}


	public StateNode getNextNode() {
		return null;
	}

	public void makeStatement(String str, boolean interNode) {
		TestRun.addTextToWindow("Driver: " + str + "\n");
        if(!interNode) 
            TestRun.aiOutput.add(str);
        else
            TestRun.aiOutput.set(TestRun.aiOutput.size()-1,TestRun.aiOutput.get(TestRun.aiOutput.size()-1) + "\n" + str);
	}

	public void generateResponse(String inp) {	
		System.out.println("gen response at: " + curr);
		if(curr == -1) { 
			makeStatement("Thanks again for choosing EZ cabs hope to have you again soon!\n\n", true);
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
				System.out.println("gen response at: " + curr);
				sn = sList.get(curr);
				makeStatement(sList.get(curr).statement.getRandomOpt(), false);
				while(curr != -1 && sn.interNode) {
					curr = sn.outgoingLinks.get(0).traverse();
					if(curr != -1) {
						sn = sList.get(curr); //in this case there is only 1 link, so the next node is assumed	
						makeStatement(sList.get(curr).statement.getRandomOpt(), true);
					}
				}
			}
			else if(sn.interNode) {
				currInternodeText = sList.get(curr).statement.getRandomOpt();
			}
			else {		
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
		System.out.println("handling input");
		//the interConv will only exist until it concludes, at which point control is returned to the normal flow.
		if(this.sn.interConv != null) {
			//sn.interConv.interpretStatement(input);
			sn.interConv.iterationsToDefault--; //when this gets to 0, the conversation will conclude.
			sn.interConv.nextMove(input, sn.interConv.matchedQueue);
			
		}
		else {
			System.out.println("generating response");
			generateResponse(input);
		}
		
	}
}
