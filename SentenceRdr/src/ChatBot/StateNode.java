package ChatBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import types.Node;

public class StateNode {
	public ArrayList<UserQueue> outgoingLinks;  //each of these links to another node. they should be ordered in the order of priority for the matching algorithm, ie. rarest first.
	public int id;
	public boolean interNode; //some nodes do not queue the user for speech, they join with other paths at the next decision node. this is true in such a case
	public String name;
	public Stmt statement;	
	
	
	public StateNode(int id, String name, boolean interNode) {
		this.id =id;
		this.name = name;
		this.interNode = interNode;
		statement = new Stmt(name);
		getQueues();
	}
	
	public void getQueues() { //loads csv and pattern strings from files based on the name of the statement node
		outgoingLinks = new ArrayList<UserQueue>();		
		try (
			Scanner scn = new Scanner(new File("./src/statements/" + name + "_links.txt"));
		) {
			while (scn.hasNextLine()) {
				String temp1 = scn.nextLine();
				String[] temp11 = temp1.split(" ");
				int temp  = Integer.parseInt(temp11[0]);  //type of match:: 0 for csv, 1 for regex, 2 for interNode (no matching)
				int ep =Integer.parseInt(temp11[1]); 	// id of node which this links to
				String temp2 = scn.nextLine(); //the match string (csv or regex)
				if(temp == 0) { //csv match
					int[][] csvMatch = new int[3][];
					String[] matchList = temp2.split(";");
					for(int i = 0; i < matchList.length; i++) { //should be length 3
						String[] c = matchList[i].split(",");
						csvMatch[i] = new int[c.length];
						for(int j = 0; j < c.length; j++) {
							csvMatch[i][j] = Integer.parseInt(c[j]);
						}
					}				
					outgoingLinks.add(new UserQueue(ep,id,null,csvMatch));
				}
				else if(temp == 1) {
					outgoingLinks.add(new UserQueue(ep,id,temp2,null));
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
	
	
	
}
