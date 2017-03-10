package unalcol.agents.examples.labyrinth.teseo.unzero;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

public class TeseoBitA0 implements AgentProgram{
	
	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { -1, 0, 1, 0 };
	private int[] pb = { 7, 9, 5, 9 };
	
	private LinkedList<String> cmd = new LinkedList<String>();

	@Override
	public Action compute(Percept p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
