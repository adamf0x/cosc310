package ChatBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Stmt { //statements that the bot can make. its like a statement class, to avoid repetition but preserve consistency
	public String name; //the name will correspond to a filename. the options will be listed on each file.
	public ArrayList<String> options;
	public Stmt (String n, String[] o) {
		name = n;
		options = new ArrayList<String>(Arrays.asList(o));
	}
	
	public Stmt (String n) {
		name = n;
		getOptions();
	}
	
	public String getStmtWDesc(String fStr) { //returns first result that matches the description. second argument is the nth match
		for(int i = 0;  i < options.size(); i++) {
			if(options.get(i).contains(fStr)) {				
				return options.get(i);
			}				
		}
		return null;
	}
	
	public String getRandomOpt() {
		int opt = (int) (Math.random()*this.options.size());
		return options.get(opt);
	}
	
	public void getOptions() {
		options = new ArrayList<String>();
		
		try (
			Scanner scn = new Scanner(new File("./src/statements/" + name + ".txt"));
		) {
			while (scn.hasNextLine()) {
				options.add(scn.nextLine());
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
