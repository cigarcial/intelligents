package unalcol.agents.examples.labyrinth.multeseo.eater.CEM;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

public class TeseoBitA0 implements AgentProgram {

	private long initTime;
	private long endTime;

	private int MAX_LEVEL = 7;
	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { 1, 0, -1, 0 };
	// si pudieramos ir entrenando para que se mueva de acuerdo a donde vaya
	// encontrando mas posibilidades, uufff
	// castigo el devolverme, la idea es ir para atras lo menor posible
	private int[] pb = { 5, 5, 2, 5 };
	int time = 0;

	private LinkedList<String> cmd;
	private Map<Integer, Map<Integer, Integer>> map;
	private SimpleLanguage language;

	// partimos del origen y vamos a construir el mapa de acuerdo a las
	// siguientes normas
	// de donde partimos supongamos que siempre estamos orientados al norte
	// con eso dr nos mide 0 -> norte 1 -> oriente 2 -> sur 3 -> occidnete
	int px = 0;
	int py = 0;
	int dr = 0;

	public TeseoBitA0(SimpleLanguage l) {
		language = l;
		cmd = new LinkedList<String>();
		map = new HashMap<Integer, Map<Integer, Integer>>();
	}

	// la heuristica calcula cuantos vecinos tiene y cuantos ya he visitado
	private int heuristic(int x, int y, int i, int k) {
		int r = 0;
		// valido la informacion de los vecinos de mi vecino (ve-vecino)
		for (int n = 0; n < 4; ++n) {
			// valido si existe dicho ve-vecino
			boolean cond1 = (1 << n & map.get(x).get(y)) != 0;
			boolean cond2 = (1 << (n + 4) & map.get(x).get(y)) == 0;
			if (cond1 && cond2) {
				r++;
			}
		}
		r -= ((1 << 8 & map.get(x).get(y)) != 0) ? 1 : 0;
		return pb[k] + r;
	}

	private Action explore() {
		if (cmd.size() > 0) {
			return nextAction();
		}
		LinkedList<Integer> pos = new LinkedList<Integer>();
		// por cada posible movimiento valido si me puedo mover y agrego a la
		// lista de movimientos
		for (int i = 0; i < 4; ++i) {
			// encuentro mi posicion relativa
			int k = (4 + i - dr) % 4;
			// valido si me puedo mover
			boolean cond1 = (1 << i & map.get(px).get(py)) != 0;
			if (cond1) {
				int t = heuristic(px + dx[i], py + dy[i], i, k);
				//System.out.println(t);
				for (int n = 0; n < t; ++n) {
					pos.add(k);
				}
			}
		}
		// selecciono un movimiento posible aleatoriamente
		int idx = new Random().nextInt(pos.size());
		for (int n = 0; n < pos.get(idx); ++n) {
			cmd.add(language.getAction(3));
		}
		cmd.add(language.getAction(2));
		return nextAction();
	}

	private void visit(Percept p) {
		initMap(px, py);

		// cuento el numero de veces que he visitado este punto
		map.get(px).put(py, 1 << 8 | map.get(px).get(py));
		/*
		 * int tVisited = Math.min(7, (map.get(px).get(py) >> 24) + 1); int
		 * timesVisited = (tVisited << 24) | ((map.get(px).get(py) << 8) >> 8);
		 * map.get(px).put(py, timesVisited);
		 */

		for (int i = 0; i < 4; ++i) {
			// posicion relativa de acuerdo a la direccion actual
			int k = (4 + i - dr) % 4;
			// puedo avanzar en esa direccion
			if (!(Boolean) p.getAttribute(language.getPercept(k))) {
				int x = px + dx[i];
				int y = py + dy[i];
				initMap(x, y);
				// construyo la conexion entre mi posicion actual y mi vecino
				int newKey = 1 << i | map.get(px).get(py);
				map.get(px).put(py, newKey);
				// construyo la conexion entre mi vecino y mi posicion actual
				newKey = 1 << ((i + 2) % 4) | map.get(x).get(y);
				map.get(x).put(y, newKey);
			}
		}
	}

	private void initMap(int x, int y) {
		if (map.get(x) == null) {
			map.put(x, new HashMap<Integer, Integer>());
		}
		if (map.get(x).get(y) == null) {
			map.get(x).put(y, 0);
		}
	}

	private Action nextAction() {
		String s = cmd.removeFirst();
		// una rotacion
		if (s.equals(language.getAction(3))) {
			dr = (dr + 1) % 4;
			// un movimiento
		} else if (s.equals(language.getAction(2))) {
			int newKey = (1 << (4 + dr) | map.get(px).get(py));
			map.get(px).put(py, newKey);
			px = px + dx[dr];
			py = py + dy[dr];
			int w = 4 + ((dr + 2) % 4);
			newKey = (1 << w | map.get(px).get(py));
			map.get(px).put(py, newKey);
		}
		return new Action(s);
	}

	@Override
	public void init() {
		cmd.clear();
	}

	@Override
	public Action compute(Percept p) {
		if (initTime == 0) {
			initTime = System.currentTimeMillis();
		}
		if ((Boolean) p.getAttribute(language.getPercept(4))) {
			if (endTime == 0) {
				endTime = System.currentTimeMillis();
				double t = (endTime - initTime) / 1000.0;
				System.out.println(t);
			}
			return new Action(language.getAction(0));
		}
		visit(p);
		time++;
		if (time % 50 == 0) {
			if (time % 200 == 0) {
				time = 0;
				MAX_LEVEL += 3;
			}
			return searchCentinel();
		}
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

	public Action searchCentinel() {
		// System.out.println("new search");
		if (new Random().nextDouble() < 2) {
			cmd = asterisk();
		}
		if (cmd.size() == 0) {
			System.out.println("BAD!!");
			cmd.add(language.getAction(0));
		}
		return nextAction();
	}

	public LinkedList<String> asterisk() {

		Node best = new Node(-1, -1, -1, -1);
		PriorityQueue<Node> structure = new PriorityQueue<Node>();
		Map<Integer, Map<Integer, Boolean>> vis = new HashMap<Integer, Map<Integer, Boolean>>();

		structure.add(new Node(px, py, 0, dr));
		vis.put(px, new HashMap<Integer, Boolean>());
		vis.get(px).put(py, true);

		while (structure.size() > 0) {
			Node nx = structure.poll();
			best = best.getBetter(nx);
			if (nx.lvl >= MAX_LEVEL) {
				continue;
			}
			for (int i = 0; i < 4; ++i) {

				int k = (i + 4 - nx.dr) % 4;
				int x = nx.px + dx[i];
				int y = nx.py + dy[i];

				initMap(x, y);
				boolean noVis = vis.get(x) == null || vis.get(x).get(y) == null;
				boolean reach = (1 << i & map.get(nx.px).get(nx.py)) != 0;
				if (reach && noVis) {
					if (vis.get(x) == null) {
						vis.put(x, new HashMap<Integer, Boolean>());
					}
					if (vis.get(x).get(y) == null) {
						vis.get(x).put(y, true);
					}
					structure.offer(nx.cloneWith(i, k));
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
			ret.cmd = (LinkedList<String>) this.cmd.clone();
			for (int j = 0; j < 4; ++j) {
				boolean isNeig = (1 << j & map.get(ret.px).get(ret.py)) != 0;
				// ret.profit += ( isNeig )? 1 : 0;
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

		@Override
		public int compareTo(Node o) {
			if (this.profit != o.profit) {
				return o.profit - this.profit;
			}
			return this.cmd.size() - o.cmd.size();
		}

		public Node getBetter(Node o) {
			return (this.compareTo(o) < 0) ? this : o;
		}

	}

}
