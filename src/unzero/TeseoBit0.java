package unalcol.agents.examples.labyrinth.teseo.unzero;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

import unalcol.agents.Action;

public class TeseoBit0 implements AgentProgram {

	private int dx[] = { 0, 1, 0, -1 };
	private int dy[] = { -1, 0, 1, 0 };

	LinkedList<String> cmd = new LinkedList<String>();
	SimpleLanguage language;

	// me sirve para llevar un tiempo dentro del sistema adicional me permite
	// guardar la posicion
	int time = 0;
	int px = 0;
	int py = 0;
	int dr = 0;

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
		int[] pb = { 4, 5, 2, 5 };
		int r = -1;
		for (int k = 0; k < 4; ++k) {
			//indica que puedo alcanzar a ese vecino de mi vecino
			boolean c1 = ( 1<<k & map.get(x).get(y) ) != 0;
			//indica que no he visitado ese vecino de mi vecino
			boolean c2 = ( 1<<(4+k) & map.get(x).get(y) ) == 0;
			if ( c1 && c2 ) {
				r++;
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
		initMapIn(px, py);
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

	@Override
	public Action compute(Percept p) {

		if ((Boolean) p.getAttribute(language.getPercept(4))) {
			return new Action(language.getAction(0));
		}
		time++;
		visit(p);
		return explore();
	}

	@Override
	public void init() {

	}

	public Action nextAction() {
		String s = cmd.removeFirst();
		if( s.equals("rotate") ){
			dr = (dr+1)%4;
		}
		if( s.equals("advance") ){
			//marco que mi vecino ya es visitado desde mi posicion 
			int newKey = map.get(px).get(py) | 1 << (4+dr);
			map.get(px).put(py, newKey);
			
			px = px + dx[dr];
			py = py + dy[dr];
			
			//marco que mi posicion anterior fue visitada por mi vecino
			newKey = map.get(px).get(py) | 1 << (4+(dr+2)%4);
			map.get(px).put(py, newKey);
		}
		return new Action(s);
	}

	public void initMapIn(int x, int y) {
		if (map.get(x) == null) {
			map.put(x, new HashMap<Integer, Integer>());
		}
		if (map.get(x).get(y) == null) {
			map.get(x).put(y, 0);
		}
	}

}
