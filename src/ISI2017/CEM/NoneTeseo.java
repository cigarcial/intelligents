package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.Language;

public class NoneTeseo implements AgentProgram{

	private Language language;
	
	public NoneTeseo(Language l){
		language = l;
	}
	
	@Override
	public Action compute(Percept p) {
		return new Action(language.getAction(1));
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	

}
