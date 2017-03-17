package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.*;
import unalcol.agents.Action;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Teseo2 implements AgentProgram {

	private int dx[] = {0,1,0,-1};
	private int dy[] = {-1,0,1,0};
	
	private int time = 0;
	protected SimpleLanguage language; 
	protected Vector<String> cmd = new Vector<String>();
	private Map<Integer,Map<Integer,Short>> map = new HashMap<Integer,Map<Integer,Short>>();
	int px = 0;
	int py = 0;
	int dir = 0;
	

	public Teseo2(SimpleLanguage lang){
		language = lang;
	}
	
	private void prepross(String s){
		if( s.equals("rotate") ){
			dir = (dir+1)%4;
		}
		if( s.equals("advance") ){
			px = px + dx[dir];
			py = py + dy[dir];
		}
	}
	
	private int aprox(int x,int y){
		if( map.get(x) != null && map.get(x).get(y) != null){
			int r = map.get(x).get(y);
			for(int i=0;i<4;++i){
				r -= ( map.get(x+dx[i]) != null && map.get(x+dx[i]).get(y+dy[i]) != null )? 1:0;
			}
			return r;
		}
		return 0;
	}
	
	private Action explore(Percept p){
		if( cmd.size() != 0){
			String s = cmd.get(0);
			cmd.remove(0);
			//System.out.println(s);
			prepross(s);
			return new Action(s);
		}
		Vector<String> pos = new Vector<String>();
		int[] pb = {4,7,2,7};
		
		//Se mueve hacia adelante siempre que puede
		//TAL VEZ SE PODRIA PONER UNA COLA CON PRIORIDAD PARA QUE SE MUEVA DE ACUERDO A UNA PRIORIDAD, POR EJEMPLO CUANDO TENGAMOS QUE GASTAR
		//ENERGIA DADO QUE MOVERSE A LA DERECHA ES MEJOR QUE MOVERSE A LA IZQUIERDA
		short c = 0;
		for( int i=0;i<4;++i){
			if( ! (Boolean) p.getAttribute(language.getPercept(i))   ){
				int r = aprox(px+dx[i],py+dy[i]);
				int l = pb[i] + r;
				System.out.println(r);
				for(int x=0;x<l;++x){
					pos.add(language.getPercept(i));
				}
				c++;
			}
		}
		map.get(px).put(py, c);
		/*
		if( !(Boolean) p.getAttribute(language.getPercept(1))){
			System.out.println(pb[1]+":"+language.getPercept(1));
			for(int x=0;x<pb[1];++x){
				pos.add(language.getPercept(1));
			}
		}
		if( !(Boolean) p.getAttribute(language.getPercept(2))){
			for(int x=0;x<pb[2];++x){
				pos.add(language.getPercept(2));
			}
		}
		
		if( !(Boolean) p.getAttribute(language.getPercept(3))){
			System.out.println(pb[3]+":"+language.getPercept(3));
			for(int x=0;x<pb[3];++x){
				pos.add(language.getPercept(3));
			}
		}
		*/
		int x = new Random().nextInt(pos.size());
		if( pos.get(x).equals("front")){
			cmd.add(language.getAction(2));
		}else if( pos.get(x).equals("right")){
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(2));
		}else if( pos.get(x).equals("back")){
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(2));
		}else if( pos.get(x).equals("left")){
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(2));
		}

		String s = cmd.get(0);
		cmd.remove(0);
		//System.out.println(s);
		prepross(s);
		return new Action(s);
	}
	
	@Override
	public Action compute(Percept p) {
		
		if((Boolean)p.getAttribute(language.getPercept(4))){
			return new Action(language.getAction(0));
		}
		// TODO Auto-generated method stub
		map.put(px,new HashMap<Integer,Short>());
		time++;
		
		return explore(p);
	}

	@Override
	public void init() {
		cmd.clear();
	}
	
	
	
	
}
