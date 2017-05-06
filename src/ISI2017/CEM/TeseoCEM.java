package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import javax.security.*;

import unalcol.agents.Action;

public class TeseoCEM implements AgentProgram {
	protected SimpleLanguage language;
	protected Vector<String> cmd = new Vector<String>();
	ArrayList<ArrayList<Integer>> pos = new ArrayList<ArrayList<Integer>>();
	Map<Integer,Map<Integer,Integer>> visited = new HashMap<Integer,Map<Integer,Integer>>();
	
	ArrayList<String> primerOpcion;
	ArrayList<String> segundaOpcion;
	ArrayList<String> tercerOpcion;
	final int derecha = 1;
	final int atras = 2;
	final int izquierda = 3;
	Random random = new Random();
	int x = 0;
	int y = 0;
	int dr = 0;
	private int dx[] = { 0, 1, 0, -1 };
	private int dy[] = { -1, 0, 1, 0 };

	public TeseoCEM(SimpleLanguage _language) {
		language = _language;
	}

	public void init() {
		cmd.clear();
	}

	// esta funci�n revisa si est� en una posici�n con varias opciones de
	// movimiento
	public boolean division(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL) {
		if ((!PF && !PD) || (!PF && !PI) || (!PI && !PD) || (!PF && !PD && !PI)) {
			// System.out.println("PF " +PF+ " PD "+ PD + " PI " +PI);
			return true;
		}
		return false;
	}

	// retorna cuantas veces ha sido visitada la posicion x y
	public int vecesVisitado(int x, int y) {
		if( visited.get(x) == null ){
			visited.put(x, new HashMap<Integer,Integer>());
		}
		if( visited.get(x).get(y) == null){
			visited.get(x).put(y, 0);
		}
		return visited.get(x).get(y);
	}

	// retorna cuantas veces ha sido visitada la posici�n que se puede alcanzar
	// desde el x y actual
	private int vecesDireccionVisitada(String direccion) {

		int nx = 0;
		int ny = 0;
		int innerdr = dr;
		switch (direccion) {
		case "front":
			nx = x + dx[innerdr];
			ny = y + dy[innerdr];
			break;
		case "right":
			for (int i = 0; i < 1; i++) {
				innerdr = (innerdr + 1) % 4;
			}
			nx = x + dx[innerdr];
			ny = y + dy[innerdr];
			break;
		case "left":
			for (int i = 0; i < 3; i++) {
				innerdr = (innerdr + 1) % 4;
			}
			nx = x + dx[innerdr];
			ny = y + dy[innerdr];
			break;

		}

		return vecesVisitado(nx, ny);
	}

	// selecciona el n�mero de giros para moverse
	public int selecNumGiro(String giro) {
		int num = 0;
		switch (giro) {
		case "right":
			num = 1;
			break;

		case "back":
			num = 2;
			break;

		case "left":
			num = 3;
			break;
		default:
			num = 0;
			break;
		}
		return num;
	}

	/**
	 * execute
	 *
	 * @param perception
	 *            Perception
	 * @return Action[]
	 */
	public Action compute(Percept p) {
		if (cmd.size() == 0) {
			
			
			boolean cond2 = (Boolean) p.getAttribute(language.getPercept(dr+6));
			if( cond2 ){
				cmd.clear();
				//time *= 40;
			}
			
			boolean PF = ((Boolean) p.getAttribute(language.getPercept(0))).booleanValue();
			boolean PD = ((Boolean) p.getAttribute(language.getPercept(1))).booleanValue();
			boolean PA = ((Boolean) p.getAttribute(language.getPercept(2))).booleanValue();
			boolean PI = ((Boolean) p.getAttribute(language.getPercept(3))).booleanValue();
			boolean MT = ((Boolean) p.getAttribute(language.getPercept(4))).booleanValue();
			boolean FAIL = ((Boolean) p.getAttribute(language.getPercept(5))).booleanValue();

			// System.out.println(division(PF, PD, PA, PI, MT, FAIL));
			primerOpcion = new ArrayList<>();
			segundaOpcion = new ArrayList<>();
			tercerOpcion = new ArrayList<>();

			// si llega a la meta muere y se queda quieto :(
			if (MT) {
				cmd.add(language.getAction(0)); // die
			}
			// se revisa si estamos en una division con varios (minimo 2)
			// caminos por tomar
			else if (division(PF, PD, PA, PI, MT, FAIL)) {
				// System.out.println("en division " +x +" "+y);
				// si puede moverse a la izquierda revisa si la posici�n ha sido
				// visitada cero, una
				// o dos o m�s veces, as� con pared derecha y al frente
				if (!PI) {

					if (vecesDireccionVisitada(language.getPercept(3)) == 0) {
						primerOpcion.add(language.getPercept(3));
					}
					if (vecesDireccionVisitada(language.getPercept(3)) == 1) {
						segundaOpcion.add(language.getPercept(3));
					}
					if (vecesDireccionVisitada(language.getPercept(3)) >= 2) {
						tercerOpcion.add(language.getPercept(3));
					}
				}
				if (!PD) {

					if (vecesDireccionVisitada(language.getPercept(1)) == 0) {
						primerOpcion.add(language.getPercept(1));
					}
					if (vecesDireccionVisitada(language.getPercept(1)) == 1) {
						segundaOpcion.add(language.getPercept(1));
					}
					if (vecesDireccionVisitada(language.getPercept(1)) >= 2) {
						tercerOpcion.add(language.getPercept(1));
					}
				}
				if (!PF) {

					if (vecesDireccionVisitada(language.getPercept(0)) == 0) {
						primerOpcion.add(language.getPercept(0));
					}
					if (vecesDireccionVisitada(language.getPercept(0)) == 1) {
						segundaOpcion.add(language.getPercept(0));
					}
					if (vecesDireccionVisitada(language.getPercept(0)) >= 2) {
						tercerOpcion.add(language.getPercept(0));
					}
				}
				// si ambas opciones de movimiento ya han sido visitadas una vez
				// se
				// devuelve por donde vino
				if (primerOpcion.size() == 0 && vecesVisitado(x, y) == 0) {
					for (int i = 0; i < 2; i++) {
						cmd.add(language.getAction(3)); // rotate
						dr = (dr + 1) % 4;
					}
					ArrayList<Integer> newPos = new ArrayList<Integer>() {
						{
							add(x);
							add(y);
						}
					};
					//visited.add(newPos);
					addMap(newPos);
					cmd.add(language.getAction(2)); // advance
					x = x + dx[dr];
					y = y + dy[dr];

				}

			} else {
				// si las 3 opciones hay pared (dead end) se devuelve
				if (PF && PD && PI) {
					for (int i = 0; i < atras; i++) {
						cmd.add(language.getAction(3)); // rotate
						dr = (dr + 1) % 4;
					}
				} else {
					// si solo hay una pared para moverse se dirije ah� y se
					// mueve
					if (!PD) {
						for (int i = 0; i < derecha; i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						}
					}
					if (!PI) {
						for (int i = 0; i < izquierda; i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						}
					}
				}
			}

			// System.out.println(" 1 opcion " + primerOpcion);
			// System.out.println(" 2 opcion " + segundaOpcion);
			// System.out.println(" 3 opcion " + tercerOpcion);

			// si tiene m�s de una opci�n de movimiento con 0 visitas escoge al
			// azar
			if (primerOpcion.size() != 0) {
				int cantidad = primerOpcion.size();
				int selec = random.nextInt(cantidad);
				String giro = primerOpcion.get(selec);
				for (int i = 0; i < selecNumGiro(giro); i++) {
					cmd.add(language.getAction(3)); // rotate
					dr = (dr + 1) % 4;
				}
			} else {
				// si tiene m�s de una opci�n de movimiento con 1 visitas escoge
				// al azar
				if (segundaOpcion.size() != 0) {
					int cantidad = segundaOpcion.size();
					int selec = random.nextInt(cantidad);
					String giro = segundaOpcion.get(selec);
					for (int i = 0; i < selecNumGiro(giro); i++) {
						cmd.add(language.getAction(3)); // rotate
						dr = (dr + 1) % 4;
					}
				}
				// si tiene m�s de una opci�n de movimiento con 2 visitas o m�s
				// escoge
				// la de menos visitas
				// esto hay que revisarlo bien porque tal vez da�a un poco la
				// implementaci�n
				else {
					if (tercerOpcion.size() != 0) {
						/*
						  int cantidad = tercerOpcion.size(); 
						  int selec = random.nextInt(cantidad); 
						  String giro =tercerOpcion.get(selec); 
						  for (int i = 0; i <selecNumGiro(giro); i++) {
						  cmd.add(language.getAction(3)); 
						  // rotate 
						  dr = (dr +1) % 4; 
						  }
						*/ 
						int veces = Integer.MAX_VALUE;
						int currentPos = -1;
						for (int i = 0; i < tercerOpcion.size(); i++) {
							if (vecesDireccionVisitada(tercerOpcion.get(i)) < veces) {
								veces = vecesDireccionVisitada(tercerOpcion.get(i));
								currentPos = i;
							}
						}
						String giro = tercerOpcion.get(currentPos);
						for (int i = 0; i < selecNumGiro(giro); i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						}
						/*
						 * for (int i = 0; i < 2; i++) {
						 * cmd.add(language.getAction(3)); // rotate dr = (dr +
						 * 1) % 4; }
						 */
					}
				}
			}

			ArrayList<Integer> newPos = new ArrayList<Integer>() {
				{
					add(x);
					add(y);
				}
			};
			//visited.add(newPos);
			addMap(newPos);
			// System.out.println("visited "+visited);
			cmd.add(language.getAction(2)); // advance
			x = x + dx[dr];
			y = y + dy[dr];

		}
		String x = cmd.get(0);
		cmd.remove(0);
		return new Action(x);
	}
	
	private void addMap(ArrayList<Integer> newPos){
		int x = newPos.get(0);
		int y = newPos.get(1);
		if( visited.get(x) == null){
			visited.put(x, new HashMap<Integer,Integer>());
		}
		if( visited.get(x).get(y) == null){
			visited.get(x).put(y, 0);
		}
		visited.get(x).put(y, visited.get(x).get(y)+1);
	}

}
