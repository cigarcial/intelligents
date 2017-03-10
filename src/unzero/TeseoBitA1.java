package unalcol.agents.examples.labyrinth.teseo.unzero;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

public class TeseoBitA1 implements AgentProgram {

	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { 1, 0, -1, 0 };
	// si pudieramos ir entrenando para que se mueva de acuerdo a donde vaya
	// encontrando mas posibilidades, uufff
	// castigo el devolverme, la idea es ir para atras lo menor posible
	private int[] pb = { 5, 5, 2, 5 };

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

	public TeseoBitA1(SimpleLanguage l) {
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
		return pb[k] + r - 1;
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
				System.out.println(t);
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
		visit(p);
		return explore();
	}

}
