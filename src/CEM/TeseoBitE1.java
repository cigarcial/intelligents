package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;

public class TeseoBitE1 implements AgentProgram{

	private long initTime;
	private long endTime;

	private int SEARCH_TRIGGER = 50;

	private int[] dx = { 0, 1, 0, -1 };
	private int[] dy = { 1, 0, -1, 0 };
	// si pudieramos ir entrenando para que se mueva de acuerdo a donde vaya
	// encontrando mas posibilidades, uufff
	// castigo el devolverme, la idea es ir para atras lo menor posible
	private int[] pb = { 6, 6, 2, 5 };
	int time = 0;

	private LinkedList<String> cmd;
	private Map<Point, Long> map;
	private SimpleLanguage language;
	private TeseoSearch search;

	// partimos del origen y vamos a construir el mapa de acuerdo a las
	// siguientes normas
	// de donde partimos supongamos que siempre estamos orientados al norte
	// con eso dr nos mide 0 -> norte 1 -> oriente 2 -> sur 3 -> occidnete
	int px = 0;
	int py = 0;
	int dr = 0;
	
	// implementacion de la comida, para ello vamos a suponer que partimos de una energia inicial 0 
	// la idea es mantener la energia lo mas alejado de cero (hacia lo positivo)
	int initial_energy = -1;
	
	
	//representacion en bit
	// 			     visitado	    paredes/agente
	// bbbb b bb b b bbbb           bbbb
	
	public TeseoBitE1(SimpleLanguage l) {
		language = l;
		cmd = new LinkedList<String>();
		map = new HashMap<Point, Long>();
		search = new TeseoSearch(language, map);
	}

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
		int[] minMove = {Integer.MAX_VALUE,-1};
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
				//System.out.print(idx+" ");
				int times = (int)((map.get(position))>>24);	
				if( idx == 2 && times < minMove[0] ){
					minMove[0] = times;
					minMove[1] = k;
				}
			}
		}
		//System.out.println();
		// no posible action (?)
		if (pos[2].size() == 0 && pos[1].size() == 0 && pos[0].size() == 0) {
			return new Action(language.getAction(0));
		}
		if( pos[0].size() == 0 && pos[1].size() == 0){
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
		} else if( s.equals(language.getAction(4)) ){
			
		}
		return new Action(s);
	}
	
	private boolean criticalEnergy(Percept p){
		//return true;
		int actual_energy = (Integer)p.getAttribute(language.getPercept(15));
		return ((1<<9) & map.get(new Point(px,py)) ) > 0 && actual_energy - initial_energy < -5 ; 
	}
	
	private LinkedList<String> getFood(){
		LinkedList<String> ret = new LinkedList<String>();
		ret.add(language.getAction(4));
		//food search
		return ret;
	}
		
	private void visit(Percept p) {
		Point position = new Point(px, py);
		initMap(position);

		// cuento el numero de veces que he visitado este punto
		long tVisited = Math.min(128, (map.get(position) >> 24) + 1);

		// System.out.println(tVisited);
		long initValue = 0;
		//reconocimiento de la comida 
		if( (Boolean)p.getAttribute(language.getPercept(10)) ){
			initValue = (1<<9) | initValue;
			for(int i=0;i<4;++i){
				int val = ( (Boolean)p.getAttribute(language.getPercept(11+i)) )? 1 : 0;
				initValue = (val<<(10+i)) | initValue;
				//restore visit information 	
				val = ( (1<<(4+i) & map.get(position)) > 0 )? 1 : 0;
				initValue = (val<<(4+i)) | initValue;
			}
		}
		
		map.put(position, initValue);
		for (int i = 0; i < 4; ++i) {
			// posicion relativa de acuerdo a la direccion actual
			int k = (4 + i - dr) % 4;

			boolean cond2 = (Boolean) p.getAttribute(language.getPercept(k + 6));
			if (cond2) {
				//cmd.clear();
				//time *= SEARCH_TRIGGER;
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
		long timesVisited = (tVisited << 24) | map.get(position);
		//System.out.println(timesVisited);
		map.put(position, timesVisited);

	}
	
	private void initMap(Point position) {
		if (!map.containsKey(position)) {
			map.put(position, (long) 0);
		}
	}
	
	@Override
	public void init() {
		cmd.clear();
	}

	@Override
	public Action compute(Percept p) {
		if(initial_energy == -1){
			initial_energy = (Integer)p.getAttribute(language.getPercept(15));
		}
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
		
		if( criticalEnergy(p) ){
			//System.out.println("He comido");
			cmd = getFood();
			return nextAction();
		}
		
		if (time % SEARCH_TRIGGER == 0) {
			time = 0;
			SEARCH_TRIGGER += 3;
			cmd = search.searchCentinel(new Point(px,py), dr);
			return nextAction();
		}
		return explore();
	}
	
	

}
