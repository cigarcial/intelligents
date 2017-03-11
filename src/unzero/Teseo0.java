package unalcol.agents.examples.labyrinth.multeseo.eater.CEM;

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

public class Teseo0 implements AgentProgram {

	private int dx[] = {0,1,0,-1};
	private int dy[] = {-1,0,1,0};
	
	private SimpleLanguage language; 
	private Vector<String> cmd = new Vector<String>();
	private Map<Integer,Map<Integer,Short>> map = new HashMap<Integer,Map<Integer,Short>>();
	
	private int px = 0;
	private int py = 0;
	private int dir = 0;
	private int time = 0;
	
	private class Node{
		LinkedList<String> cmd = new LinkedList<String>();
		int px,py,lvl,prof,dir;
		
		public Node(int x,int y,int l,int p,int d){
			px = x;
			py = y;
			lvl = l;
			prof = p;
			dir = d;
		}
		
		/**
		 * @param i
		 * @return
		 */
		public Node cloneWith(int i){
			Node ret = new Node(px+dx[i],py+dy[i],lvl+1,-1,(i+dir)%4);
			ret.cmd = (LinkedList<String>)cmd.clone();
			for(int j=0;j<i;++j){
				ret.cmd.add(language.getAction(3));
			}
			ret.cmd.add(language.getAction(2));
			return ret;
		}
		
		
		public Node compare(Node o){
			if( this.lvl < o.lvl ){
				return o;
			}
			return this;
		}
	}
	
	
	
	public Teseo0(SimpleLanguage lang){
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
		int[] pb = {4,5,2,5};
		int r = pb[i];
		if( map.get(x) != null && map.get(x).get(y) != null){
			r += map.get(x).get(y)-1;
		}
		return r;
	}
	
	private Action explore(Percept p){
		if( cmd.size() != 0){
			String s = cmd.get(0);
			cmd.remove(0);
			prepross(s);
			return new Action(s);
		}
		Vector<String> pos = new Vector<String>();
		
		//Se mueve hacia adelante siempre que puede
		//TAL VEZ SE PODRIA PONER UNA COLA CON PRIORIDAD PARA QUE SE MUEVA DE ACUERDO A UNA PRIORIDAD, POR EJEMPLO CUANDO TENGAMOS QUE GASTAR
		//ENERGIA DADO QUE MOVERSE A LA DERECHA ES MEJOR QUE MOVERSE A LA IZQUIERDA
		for( int i=0;i<4;++i){
			if( ! (Boolean) p.getAttribute(language.getPercept(i))   ){
				int l = aprox(px+dx[(i+dir)%4],py+dy[(i+dir)%4],i);
			    System.out.println(l);
				for(int x=0;x<l;++x){
					pos.add(language.getPercept(i));
				}
			}
		}

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
		prepross(s);
		return new Action(s);
	}
	
	public Action unzero(Percept p){
		System.out.println("OK");
		cmd.clear();
		cmd = NonDFS(p);
		String s = cmd.get(0);
		cmd.remove(0);
		time = 0;
		return new Action(s);
	}
	
	public Vector<String> NonDFS(Percept p){
		
		Node best = new Node(-1,-1,-1,-1,-1);

		Map<Integer,Map<Integer,Boolean>> vis = new HashMap<Integer,Map<Integer,Boolean>>();
		LinkedList<Node> stack = new LinkedList<Node>();
		int MAX_LEVEL = 10;
		stack.add(new Node(px,py,0,0,dir));
		
		while( stack.size() > 0 ){
			Node nx = stack.removeLast();
			if( nx.lvl > MAX_LEVEL ){
				best = best.compare(nx);
				break;
			}
			best = best.compare(nx);
			for(int k=0;k<4;++k){
				int i = (k+dir)%2;
				int x = nx.px + dx[i];
				int y = nx.py + dy[i];
				boolean reach = map.get(x) != null && map.get(x).get(y) != null;
				boolean vist = vis.get(x) != null && vis.get(x).get(y) != null;
				if( reach && !vist ){
					stack.add(nx.cloneWith(k));
				}
			}
		}
		if( best.cmd.size() == 0){
			System.out.println("BAD!!");
			best.cmd.add(language.getAction(0));
		}
		Vector<String> ret = new Vector<String>();
		for(int x=0;x<best.cmd.size();++x){
			ret.add(best.cmd.get(x));
		}
		return ret;
	}
	
	
	
	
	
	public void visit(Percept p){
		if( map.get(px) == null){
			map.put(px,new HashMap<Integer,Short>() );
		}
		if( map.get(px).get(py) == null ){
			short r = 0;
			for(int i=0;i<4;++i){
				if( !(Boolean)p.getAttribute(language.getPercept(i)) ){
					r++;
				}
			}
			map.get(px).put(py, r);
		}
	}
	
	@Override
	public Action compute(Percept p) {
		if((Boolean)p.getAttribute(language.getPercept(4))){
			return new Action(language.getAction(0));
		}
		time++;
		visit(p);
		if( time%100 == 0 ){
			return unzero(p);
		}
		return explore(p);
	}

	@Override
	public void init() {
		cmd.clear();
	}
	
}
