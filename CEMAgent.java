package unalcol.agents.examples.games.fourinrow.ISI2017.CEM;

import java.awt.Point;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

public class CEMAgent implements AgentProgram {

	private String color;
	private boolean WARNING;
	

	public CEMAgent(String c) {
		color = c;
		WARNING = false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Action nextAction(Percept p){

		int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
        int i = (int)(n*Math.random());
        int j = (int)(n*Math.random());
        boolean flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
        while( !flag ){
            i = (int)(n*Math.random());
            j = (int)(n*Math.random());
            flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
        }	
        for(int y=0;y<n;++y){
        	System.out.print( findTop(p,y) +" : " );
        	
        }
        System.out.println();
        
        
        return new Action( i+":"+j+":"+color );
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public Action compute(Percept p) {
		//try{
			if(WARNING){ return randomAction(p); }
			try {
				Thread.sleep((long) (20 * Math.random()));
			} catch (Exception e) {}
			if( p.getAttribute(FourInRow.TURN).equals(color) ){
	        	return nextAction(p);
	        }
			return new Action(FourInRow.PASS);
		/*}catch(Exception ex){
			System.out.println("PANIC");
			WARNING = true;
			return randomAction(p);
		}*/
	}
	

	
	/**Solo si el agente falla se juega aleatorio, es mejor a no jugar nada */
	private Action randomAction(Percept p){
		int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
        int i = (int)(n*Math.random());
        int j = (int)(n*Math.random());
        boolean flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
        while( !flag ){
            i = (int)(n*Math.random());
            j = (int)(n*Math.random());
            flag = (i==n-1) || !p.getAttribute((i+1)+":"+j).equals((String)FourInRow.SPACE);
        }	
        return new Action( i+":"+j+":"+color );
	}
	
	
	
	
	
	
	
	
	private int findTop(Percept p, int col){
		int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
		for(int x=0;x<n;++x){
			if( !p.getAttribute(x+":"+col).equals((String)FourInRow.SPACE) ){
				return x-1;
			}
		}
		return n-1;
	}
	
	
	private Point search(Percept p){
		int n = Integer.parseInt((String)p.getAttribute(FourInRow.SIZE));
		
		return null;
	}
	

}
