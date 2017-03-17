package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Comparator;
import java.util.HashMap;

import unalcol.agents.Action;

public class TeseoBit0 implements AgentProgram {

	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { -1, 0, 1, 0 };
	private int[] pb = { 7, 9, 5, 9 };
	private int MAX_LEVEL = 10;
	private long initTime;
	private long endTime;

	LinkedList<String> cmd = new LinkedList<String>();
	SimpleLanguage language;

	// me sirve para llevar un tiempo dentro del sistema adicional me permite
	// guardar la posicion
	int time = 0;
	int px = 0;
	int py = 0;
	int dr = 0;
	private boolean moved = false;

	// estructura del mapa
	// hago operaciones de bit
	// representacion de bits
	// 2^0 -> arriba
	// 2^1 -> derecha
	// 2^2 -> izquierda
	// 2^3 -> abajo

	Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();

	public TeseoBit0(SimpleLanguage l) {
		language = l;
	}

	public int getHeuristic(int x, int y, int i) {

		int r = 0;
		for (int k = 0; k < 4; ++k) {
			// indica que puedo alcanzar a ese vecino de mi vecino
			boolean c1 = (1 << k & map.get(x).get(y)) != 0;
			// indica que no he visitado ese vecino de mi vecino
			boolean c2 = (1 << (4 + k) & map.get(x).get(y)) == 0;
			if (c1 && c2) {
				r++;
			}
			
			if( (1<<(k+8) & map.get(x).get(y)) != 0){
				r--;
			}
			
		}
		
		return pb[i] + r;
	}

	public Action explore() {
		if (cmd.size() > 0) {
			return nextAction();
		}

		LinkedList<String> pos = new LinkedList<String>();

		for (int i = 0; i < 4; ++i) {
			if ((1 << i & map.get(px).get(py)) != 0) {
				int k = (i + 4 - dr) % 4;
				int t = getHeuristic(px + dx[i], py + dy[i], k);
				//System.out.println(t);			
				for (int x = 0; x < t; ++x) {
					pos.add(language.getPercept(k));
				}
			}
		}

		int x = new Random().nextInt(pos.size());
		if (pos.get(x).equals("front")) {
			cmd.add(language.getAction(2));
		} else if (pos.get(x).equals("right")) {
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(2));
		} else if (pos.get(x).equals("back")) {
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(2));
		} else if (pos.get(x).equals("left")) {
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(3));
			cmd.add(language.getAction(2));
		}
		return nextAction();
	}

	// ojo estoy usando 8 bits para codificar la informacion
	// distribuidos de la siguiente forma
	// xxxx
	// los 4 primeros me indican cuales de mis vecinos conectan con migo
	// pilas esta funcion hace enhebramiento de un árbol
	public void visit(Percept p) {
		boolean f = initMapIn(px, py);
		/*
		 * if( f && moved ){ moved = false; pb[dr] = Math.min(pb[dr]+1, 8);
		 * }else if( !f && moved ){ pb[dr] = Math.max(pb[dr]-1, 2); }
		 */
		for(int i=0;i<2;++i){
			if( (1<<(i+8) & map.get(px).get(py)) == 0){
				int newKey = map.get(px).get(py) | 1<<(i+8);
				map.get(px).put(py,newKey);
				break;
			}
		}
		for (int i = 0; i < 4; ++i) {
			// reviso si en esa direccion hay vecino
			int k = (i + 4 - dr) % 4;
			if (!(Boolean) p.getAttribute(language.getPercept(k))) {
				int x = px + dx[i];
				int y = py + dy[i];
				initMapIn(x, y);
				int j = (i + 2) % 4;
				int newKey = map.get(x).get(y) | 1 << j;
				map.get(x).put(y, newKey);
				newKey = map.get(px).get(py) | 1 << i;
				map.get(px).put(py, newKey);
			}
		}
	}

	public Action nextAction() {
		String s = cmd.removeFirst();
		if (s.equals("rotate")) {
			dr = (dr + 1) % 4;
		}
		if (s.equals("advance")) {
			// moved = true;
			// marco que mi vecino ya es visitado desde mi posicion
			int newKey = map.get(px).get(py) | 1 << (4 + dr);
			map.get(px).put(py, newKey);

			px = px + dx[dr];
			py = py + dy[dr];

			// marco que mi posicion anterior fue visitada por mi vecino
			newKey = map.get(px).get(py) | 1 << (4 + ((dr + 2) % 4));
			map.get(px).put(py, newKey);
		}
		return new Action(s);
	}

	public boolean initMapIn(int x, int y) {
		boolean f = false;
		if (map.get(x) == null) {
			map.put(x, new HashMap<Integer, Integer>());
			f = true;
		}
		if (map.get(x).get(y) == null) {
			map.get(x).put(y, 0);
			f = true;
		}
		return f;
	}

	@Override
	public void init() {
		cmd.clear();
	}

	@Override
	public Action compute(Percept p) {
		
		if( initTime == 0){
			initTime = System.currentTimeMillis();
		}
		if ((Boolean) p.getAttribute(language.getPercept(4))) {
			if( endTime == 0){
				endTime = System.currentTimeMillis();
			}
			double t = (endTime-initTime)/1000.0;
			System.out.println( t );
			return new Action(language.getAction(0));
		}
		time++;
		if (time % 100 == 0) {
			if( time%500 == 0){
				MAX_LEVEL++;
				time = 0;
			}
			return searchCentinel(0);
		}
		visit(p);
		return explore();
	}

	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS
	// BUSQUEDAS

	public Action searchCentinel(int i) {
		//System.out.println("new search");
		if( new Random().nextDouble() < 2 ){
			cmd = asterisk();
		}else{
			cmd = NonDFS();
		}
		if (cmd.size() == 0) {
			cmd.add(language.getAction(0));
		}
		return nextAction();

	}

	public LinkedList<String> NonDFS() {

		Node best = new Node(-1, -1, -1, -1);
		LinkedList<Node> stack = new LinkedList<Node>();
		Map<Integer, Map<Integer, Boolean>> vis = new HashMap<Integer, Map<Integer, Boolean>>();

		stack.add(new Node(px, py, 0, dr));
		vis.put(px, new HashMap<Integer, Boolean>());
		vis.get(px).put(py, true);

		while (stack.size() > 0) {
			Node nx = stack.removeLast();
			best = best.isBetter(nx);
			if (nx.lvl >= MAX_LEVEL) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {

				int k = (i + 4 - nx.dr) % 4;
				int x = nx.px + dx[i];
				int y = nx.py + dy[i];

				initMapIn(x, y);
				boolean noVis = vis.get(x) == null || vis.get(x).get(y) == null;
				boolean reach = (1 << i & map.get(nx.px).get(nx.py)) != 0;
				if (reach && noVis) {
					if (vis.get(x) == null) {
						vis.put(x, new HashMap<Integer, Boolean>());
					}
					if (vis.get(x).get(y) == null) {
						vis.get(x).put(y, true);
					}
					stack.add(nx.cloneWith(i, k));
				}
			}

		}

		return best.cmd;
	}

	public LinkedList<String> asterisk() {

		Node best = new Node(-1, -1, -1, -1);
		PriorityQueue<Node> stack = new PriorityQueue<Node>();
		Map<Integer, Map<Integer, Boolean>> vis = new HashMap<Integer, Map<Integer, Boolean>>();

		stack.add(new Node(px, py, 0, dr));
		vis.put(px, new HashMap<Integer, Boolean>());
		vis.get(px).put(py, true);

		while (stack.size() > 0) {
			Node nx = stack.poll();
			best = best.isBetter(nx);
			if (nx.lvl >= MAX_LEVEL) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {

				int k = (i + 4 - nx.dr) % 4;
				int x = nx.px + dx[i];
				int y = nx.py + dy[i];

				initMapIn(x, y);
				boolean noVis = vis.get(x) == null || vis.get(x).get(y) == null;
				boolean reach = (1 << i & map.get(nx.px).get(nx.py)) != 0;
				if (reach && noVis) {
					if (vis.get(x) == null) {
						vis.put(x, new HashMap<Integer, Boolean>());
					}
					if (vis.get(x).get(y) == null) {
						vis.get(x).put(y, true);
					}
					stack.offer(nx.cloneWith(i, k));
				}
			}

		}

		return best.cmd;
	}
	
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA
	// CLASE PARA LA BUSQUEDA

	class Node implements Comparable<Node> {

		private int px, py, lvl, dr, profit;
		int st = 0;
		private LinkedList<String> cmd;

		public Node(int x, int y, int l, int d) {
			this.px = x;
			this.py = y;
			this.lvl = l;
			this.dr = d;
			this.cmd = new LinkedList<String>();
		}

		public Node cloneWith(int i, int k) {
			Node ret = new Node(this.px + dx[i], this.py + dy[i], this.lvl + 1, (this.dr + k) % 4);
			ret.st = this.st + k + 1;
			ret.cmd = (LinkedList<String>) this.cmd.clone();
			for (int j = 0; j < 4; ++j) {
				boolean isNeig = (1 << j & map.get(ret.px).get(ret.py)) != 0;
				//ret.profit += ( isNeig )? 1 : 0;
				boolean unvis = (1 << (j + 4) & map.get(ret.px).get(ret.py)) == 0;
				if (isNeig && unvis) {
					ret.profit++;
				}
			}
			for (int x = 0; x < k; ++x) {
				ret.cmd.add(language.getAction(3));
			}
			ret.cmd.add(language.getAction(2));
			return ret;
		}

		public Node isBetter(Node o) {
			if( this.profit != o.profit ){
				if( this.profit > o.profit ){
					return this;
				}
				return o;
			}
			return (this.st < o.st )? this : o;
		}

		@Override
		public int compareTo(Node arg0) {
			Node n2 = (Node)arg0;
			if( this.lvl != n2.lvl ){
				return n2.lvl - this.lvl;
			}
			return this.st - n2.st;
		}

	}

}
