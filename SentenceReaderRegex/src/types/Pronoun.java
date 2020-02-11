package types;

public class Pronoun extends Node{
	public Noun realNoun;
	public Pronoun(Word val) {
		super(val);
		for(Def d : val.getDefs()) {
			if(d.def.toLowerCase().contains("poss"))symbol='n';
			else symbol = 'm';
		}
	}
	public Pronoun(Word val, Noun rN) {
		this(val);
		realNoun = rN;
	}
}
