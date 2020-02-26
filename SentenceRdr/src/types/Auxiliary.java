package types;

public class Auxiliary extends Node{	
		int voice; //0:
		int tense; //0:present,1:past,2:future, etc.
		public Auxiliary(Word val) {
			super(val);		
			symbol = 'p';
		}
}
