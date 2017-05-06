package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.*;
import unalcol.agents.Action;

import java.util.Random;

import org.w3c.dom.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class Teseo3 implements AgentProgram {

	private int dx[] = {0,1,0,-1};
	private int dy[] = {-1,0,1,0};
	
	private int time = 0;
	protected SimpleLanguage language; 
	protected Vector<String> cmd = new Vector<String>();
	private Map<Integer,Map<Integer,Short>> map = new HashMap<Integer,Map<Integer,Short>>();
	int px = 0;
	int py = 0;
	int dir = 0;
	
	private class Node{
		Vector<String> cmd; 
		int px,py,lvl,prof,dir;
		public Node(int x,int y,int l,int c,int d){
			cmd = new Vector<String>();
			px = x;
			py = y;
			lvl = l;
			prof = c;
			dir = d;
		}
		
		public Node compare( Node b){
			if( this.prof < b.prof ){
				return b;
			}
			if( b.prof  < this.prof ){
				return this;
			}
			return (this.lvl > b.lvl)? this : b; 
		}
		
		/**
		 * Hay que tener cuidado con este metodo dado que esto es, extraño pilas 
		 * 0 es avanzar en su direccion actual, 1 es avanzar a la derecha
		 * 2 es avanzar hacia atras y 3 es avanzar hacia izquierdas
		 * @param i
		 * @return
		 */
		
		public Node cloneWith( int i ){
			Node ret = new Node(px+dx[i],py+dy[i],lvl+1,-1,(dir+i)%4);
			for(int x=0;x<this.cmd.size();++x){
				ret.cmd.add(this.cmd.get(x));
			}
			for(int x=0;x<i;++x){
				ret.cmd.add(language.getAction(3));
			}
			ret.cmd.add(language.getAction(2));
			int s = 0;
			for(int j=0;j<4;++j){
				int x = ret.px + dx[j];
				int y = ret.py + dy[j];
				if( map.get(x) != null && map.get(x).get(y) != null){
					s++;
				}
			}
			ret.prof = s;
			return ret;
		}
	}
	
	
	
	public Teseo3(SimpleLanguage lang){
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
	
	private int aprox(int x,int y,int i){
		if( new Random().nextDouble() > 0.93 ){
			return 4;
		}
		int[] pb = {4,7,2,7};
		if( map.get(x) != null && map.get(x).get(y) != null){
			int r = map.get(x).get(y);
			for(int k=0;k<4;++k){
				r -= ( map.get(x+dx[k]) != null && map.get(x+dx[k]).get(y+dy[k]) != null )? 1:0;
			}
			return r + pb[i];
		}
		return pb[i];
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
		
		//Se mueve hacia adelante siempre que puede
		//TAL VEZ SE PODRIA PONER UNA COLA CON PRIORIDAD PARA QUE SE MUEVA DE ACUERDO A UNA PRIORIDAD, POR EJEMPLO CUANDO TENGAMOS QUE GASTAR
		//ENERGIA DADO QUE MOVERSE A LA DERECHA ES MEJOR QUE MOVERSE A LA IZQUIERDA
		short c = 0;
		for( int i=0;i<4;++i){
			if( ! (Boolean) p.getAttribute(language.getPercept(i))   ){
				int l = aprox(px+dx[i],py+dy[i],i);
				System.out.println(l);
				for(int x=0;x<l;++x){
					pos.add(language.getPercept(i));
				}
				c++;
			}
		}
		map.get(px).put(py, c);

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
	
	
	public Action unzero(Percept p){
		if( cmd.size() > 0 ){
			String s = cmd.get(0);
			cmd.remove(0);
			return new Action(s);
		}
		cmd = dfs(p);
		String s = cmd.get(0);
		cmd.remove(0);
		return new Action(s);
	}
	
	
	
	public Vector<String> dfs(Percept p){
		Map<Integer,Map<Integer,Boolean>> vis = new HashMap<Integer,Map<Integer,Boolean>>();
		int MAX_LEVEL = 10;
		LinkedList<Node> stack = new LinkedList<Node>();
		Node best = new Node(-1,-1,-1,-1,-1);
		stack.add(new Node(px,py,0,0,dir));
		stack.getLast().cmd.add(language.getAction(0));
		vis.put(px, new HashMap<Integer,Boolean>());
		vis.get(px).put(py,true);
				
		while( stack.size() > 0  ){
			Node nx = stack.removeLast();
			if( nx.lvl >= MAX_LEVEL ){
				best = best.compare(nx);
				continue;
			}
			best = best.compare(nx);
			
			for(int j=0;j<4;++j){
				int i = (nx.dir+j)%4;
				int x = nx.px + dx[i];
				int y = nx.py + dy[i];
				if( map.get(x) != null && map.get(x).get(y) != null && vis.get(x) != null && ! vis.get(x).get(y)){
					if( vis.get(x) == null){
						vis.put(x,new HashMap<Integer,Boolean>());
					}
					if( vis.get(x).get(y) == null){
						vis.get(x).put(y, true);
					}
					stack.add( nx.cloneWith(i) );
				}
			}
			
		}
		if( best.cmd.size() > 2){
			best.cmd.remove(0);
		}
		return best.cmd;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Action compute(Percept p) {
		
		if((Boolean)p.getAttribute(language.getPercept(4))){
			return new Action(language.getAction(0));
		}
		// TODO Auto-generated method stub
		map.put(px,new HashMap<Integer,Short>());
		time++;
//;		System.out.println(new Random().nextDouble());
		if( (time*new Random().nextDouble())/200 > 0.95 ){
			System.out.println("KOKONI");
			return unzero(p);
		}
		return explore(p);
	}

	@Override
	public void init() {
		cmd.clear();
	}
	
}
