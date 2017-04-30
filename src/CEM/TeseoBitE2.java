package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.swing.tree.ExpandVetoException;

import org.omg.IOP.TransactionService;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

public class TeseoBitE2 implements AgentProgram {

	// informacion de control o general del sistema
	private long initTime, endTime;
	private int SEARCH_TRIGGER = 50;
	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { 1, 0, -1, 0 };

	// informacion general de la exploracion
	private int[] pb = { 6, 6, 2, 5 };
	private int time = 0;

	private LinkedList<String> cmd;
	private Map<Point, Long> map;
	private SimpleLanguage language;
	private TeseoSearch search;
	private Map<Integer, Boolean> foodRec;
	// partimos del origen y vamos a construir el mapa de acuerdo a las
	// siguientes normas
	// de donde partimos supongamos que siempre estamos orientados al norte
	// con eso dr nos mide 0 -> norte 1 -> oriente 2 -> sur 3 -> occidnete
	private int px = 0;
	private int py = 0;
	private int dr = 0;
	private int initialEnergy;
	private int previosEnergy;
	private int maxEnergy;

	// representacion en bit
	// visitado paredes/agente
	// bbbb b bb b b bbbb bbbb

	public TeseoBitE2(SimpleLanguage l) {
		language = l;
		cmd = new LinkedList<String>();
		map = new HashMap<Point, Long>();
		search = new TeseoSearch(language, map);
		foodRec = new HashMap<Integer, Boolean>();
		initialEnergy = previosEnergy = -100;
	}

	@Override
	public void init() {
		cmd.clear();
	}

	/**
	 * visita de nodo visita de nodo visita de nodo visita de nodo visita de
	 * nodo visita de nodo visita de nodo visita de nodo
	 */

	private void visit(Percept p) {
		Point position = new Point(px, py);
		initMap(position);

		// cuento el numero de veces que he visitado este punto
		long tVisited = Math.min(128, (map.get(position) >> 24) + 1);

		// System.out.println(tVisited);
		long initValue = 0;
		// reconocimiento de la comida
		if ((Boolean) p.getAttribute(language.getPercept(10))) {
			initValue = (1 << 9) | initValue;
			for (int i = 0; i < 4; ++i) {
				int val = ((Boolean) p.getAttribute(language.getPercept(11 + i))) ? 1 : 0;
				initValue = (val << (10 + i)) | initValue;
			}
		}

		map.put(position, initValue);

		for (int i = 0; i < 4; ++i) {
			// posicion relativa de acuerdo a la direccion actual
			int k = (4 + i - dr) % 4;

			// hay un agente en esa direccion
			boolean cond2 = (Boolean) p.getAttribute(language.getPercept(k + 6));
			if (cond2) {
				cmd.clear();
				time *= SEARCH_TRIGGER;
			}
			// puedo avanzar en esa direccion, no hay pared
			boolean cond1 = !(Boolean) p.getAttribute(language.getPercept(k));

			int kk = (cond1 && !cond2) ? 1 : 0;
			int x = px + dx[i];
			int y = py + dy[i];
			Point nPosition = new Point(x, y);
			initMap(nPosition);
			// construyo la conexion entre mi posicion actual y mi vecino
			long newKey = (kk) << i | map.get(position);
			map.put(position, newKey);
			// construyo la conexion entre mi vecino y mi posicion actual
			newKey = (kk) << ((i + 2) % 4) | map.get(nPosition);
			map.put(nPosition, newKey);
		}
		initValue = (tVisited << 24) | map.get(position);
		map.put(position, initValue);
	}

	private void initMap(Point position) {
		if (!map.containsKey(position)) {
			map.put(position, (long) 0);
		}
	}

	/**
	 * visita de nodo visita de nodo visita de nodo visita de nodo visita de
	 * nodo visita de nodo visita de nodo visita de nodo
	 */

	// la heuristica calcula cuantos vecinos tiene y cuantos ya he visitado
	private int heuristic(Point position, int i, int k) {
		int r = 0;
		// valido la informacion de los vecinos de mi vecino (ve-vecino)
		for (int n = 0; n < 4; ++n) {
			// valido si existe dicho ve-vecino
			boolean cond1 = (1 << n & map.get(position)) != 0;
			boolean cond2 = (1 << (n + 4) & map.get(position)) == 0;
			if (cond1 && cond2) {
				r++;
			}
		}
		return pb[k] + r;
	}

	private Action explore() {
		if (cmd.size() > 0) {
			return nextAction();
		}
		LinkedList<Integer>[] pos = new LinkedList[3];
		for (int n = 0; n < 3; ++n) {
			pos[n] = new LinkedList<Integer>();
		}
		// por cada posible movimiento valido si me puedo mover y agrego a la
		// lista de movimientos
		int[] minMove = { Integer.MAX_VALUE, -1 };
		for (int i = 0; i < 4; ++i) {
			// encuentro mi posicion relativa
			int k = (4 + i - dr) % 4;
			// valido si me puedo mover
			boolean cond1 = (1 << i & map.get(new Point(px, py))) != 0;
			if (cond1) {
				Point position = new Point(px + dx[i], py + dy[i]);
				int t = heuristic(position, i, k);
				long v = map.get(position) >> 24;
				int idx = (v < 2) ? (int) v : 2;
				for (int n = 0; n < t; ++n) {
					pos[idx].add(k);
				}
				// System.out.print(idx+" ");
				int times = (int) ((map.get(position)) >> 24);
				if (idx == 2 && times < minMove[0]) {
					minMove[0] = times;
					minMove[1] = k;
				}
			}
		}
		// System.out.println();
		// no posible action (?)
		if (pos[2].size() == 0 && pos[1].size() == 0 && pos[0].size() == 0) {
			return new Action(language.getAction(0));
		}
		if (pos[0].size() == 0 && pos[1].size() == 0) {
			for (int n = 0; n < minMove[1]; ++n) {
				cmd.add(language.getAction(3));
			}
			cmd.add(language.getAction(2));
			return nextAction();
		}
		// selecciono un movimiento posible aleatoriamente
		for (int m = 0; m < 2; ++m) {
			if (pos[m].size() > 0) {
				int idx = new Random().nextInt(pos[m].size());
				for (int n = 0; n < pos[m].get(idx); ++n) {
					cmd.add(language.getAction(3));
				}
				cmd.add(language.getAction(2));
				break;
			}
		}
		return nextAction();
	}

	private Action nextAction() {
		String s = cmd.removeFirst();
		// una rotacion
		if (s.equals(language.getAction(3))) {
			dr = (dr + 1) % 4;
			// un movimiento
		} else if (s.equals(language.getAction(2))) {
			Point position = new Point(px, py);
			long newKey = (1 << (4 + dr) | map.get(position));
			map.put(position, newKey);
			px = px + dx[dr];
			py = py + dy[dr];
			position = new Point(px, py);
			int w = 4 + ((dr + 2) % 4);
			newKey = (1 << w | map.get(position));
			map.put(position, newKey);
		}
		return new Action(s);
	}

	@Override
	public Action compute(Percept p) {
		// try {
		// verificaciones previas a la primera accion
		if (initialEnergy == -100) {
			initialEnergy = (Integer) p.getAttribute(language.getPercept(15));

			initTime = System.currentTimeMillis();
		}
		maxEnergy = Math.max(maxEnergy, (Integer) p.getAttribute(language.getPercept(15)));
		//System.out.println(maxEnergy + ", " + (Integer) p.getAttribute(language.getPercept(15)));

		if (previosEnergy != -100) {
			boolean val = ((Integer) p.getAttribute(language.getPercept(15)) - previosEnergy) > -2;
			// System.out.println((Integer)
			// p.getAttribute(language.getPercept(15))+", "+previosEnergy);
			foodRec.put(getFoodCode(), val);
			previosEnergy = -100;
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

		if (criticalEnergy(p)) {
			cmd = getFood();
			return nextAction();
		}

		if (time % SEARCH_TRIGGER == 0) {
			time = 0;
			SEARCH_TRIGGER += 3;
			cmd = search.searchCentinel(new Point(px, py), dr);
			System.out.println(cmd.size());
			if (cmd.size() == 0) {
				cmd.add(language.getAction(0));
				System.out.println("BAD SEARCH!!");
			}
			return nextAction();
		}
		return explore();
		/*
		 * } catch (Exception ex) {
		 * 
		 * } return new Action(language.getAction(0));
		 */
	}

	public int getFoodCode() {
		return (int) ((map.get(new Point(px, py)) >> 4) & (long) 15);
	}

	public boolean criticalEnergy(Percept p) {
		int foodCode = getFoodCode();
		int actualEnergy = (Integer) p.getAttribute(language.getPercept(15));
		boolean c1 = ((1 << 9) & map.get(new Point(px, py))) > 0;
		boolean c2 = actualEnergy - initialEnergy < -5;
		boolean c3 = actualEnergy < (initialEnergy + (Math.abs(maxEnergy - initialEnergy)) / 2);
		if (c1 && Math.random() < 0.1 && foodRec.containsKey(foodCode)) {
			return foodRec.containsKey(foodCode);
		}
		if (foodRec.containsKey(foodCode)) {
			return c1 && c2 && c3 && foodRec.get(foodCode);
		}
		previosEnergy = actualEnergy;
		return c1 & c2 && c3;
	}

	private LinkedList<String> getFood() {
		LinkedList<String> ret = new LinkedList<String>();
		ret.add(language.getAction(4));
		// food search
		return ret;
	}

}