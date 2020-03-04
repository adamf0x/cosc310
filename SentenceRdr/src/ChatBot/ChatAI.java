package ChatBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import control.SParse;
import types.Node;

/*the graph works as follows:
 * the statements are the nodes. the cues observed in the user responses are the edges.
 * at each node it will be looking for different queues. they will have to be pretty vague (reg exp might be of use here)
 * there will have to be a default path, and many of the paths will be parallel routes to the same goal (different branches that meet again at a nexus)
 * 
 */

public class ChatAI {
	public ArrayList<StateNode> sList;
	public Scanner scn;
	int curr;
	
	public ChatAI() {
		SParse.init();
		sList = new ArrayList<StateNode>();
		init();
		this.curr = 0;
		
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
				sList.add(new StateNode(count++,str.split(" ")[0],interNode));
			}
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public StateNode getNextNode() {
		return null;
	}
	
	public String genResponse(String inp) {
		StateNode sn = sList.get(curr);
		String out = sn.statement.getRandomOpt();
		if(!sn.interNode) {	//if the user is queued to make a decision (thus determining the link chosen)
			Node endVal = SParse.getPhraseTreeFromString(inp, 0, false);
			curr = sn.testInpForQueues(inp, endVal).traverse();	
		}
		else {
			curr = sn.outgoingLinks.get(0).traverse(); //in this case there is only 1 link, so the next node is assumed
		}
		
		return out;
		
	}
	
	
}
