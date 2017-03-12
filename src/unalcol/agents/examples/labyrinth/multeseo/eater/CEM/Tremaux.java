package unalcol.agents.examples.labyrinth.multeseo.eater.CEM;

import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.*;

import java.util.ArrayList;
import java.util.Random;


import unalcol.agents.Action;


public class Tremaux implements AgentProgram {
	protected SimpleLanguage language;
	protected Vector<String> cmd = new Vector<String>();
	ArrayList<ArrayList<Integer>> pos = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
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
	
	public Tremaux() {
	}

	public Tremaux(SimpleLanguage _language) {
		language = _language;
	}

	public void setLanguage(SimpleLanguage _language) {
		language = _language;
	}

	public void init() {
		cmd.clear();
	}

	public boolean division(boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL) {
		if ((!PF && !PD) || (!PF && !PI) || (!PI && !PD) || (!PF && !PD && !PI)) {
//			System.out.println("PF "  +PF+ " PD "+ PD + " PI " +PI);
			
			return true;
		}
		return false;
	}

	public int vecesVisitado(int x, int y) {
		int vecesVisitado = 0;
		ArrayList<Integer> newPos = new ArrayList<Integer>() {
			{
				add(x);
				add(y);
			}
		};
		for (int i = 0; i < visited.size(); i++) {
			if (visited.get(i).equals(newPos))
				vecesVisitado += 1;
		}
		return vecesVisitado;
	}

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
	
	public int selecNumGiro(String giro){
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

			boolean PF = ((Boolean) p.getAttribute(language.getPercept(0))).booleanValue();
			boolean PD = ((Boolean) p.getAttribute(language.getPercept(1))).booleanValue();
			boolean PA = ((Boolean) p.getAttribute(language.getPercept(2))).booleanValue();
			boolean PI = ((Boolean) p.getAttribute(language.getPercept(3))).booleanValue();
			boolean MT = ((Boolean) p.getAttribute(language.getPercept(4))).booleanValue();
			boolean FAIL = ((Boolean) p.getAttribute(language.getPercept(5))).booleanValue();

//			System.out.println(division(PF, PD, PA, PI, MT, FAIL));
			primerOpcion = new ArrayList<>();
			segundaOpcion = new ArrayList<>();
			tercerOpcion = new ArrayList<>();
			// se revisa si estamos en una division con varios (minimo 2)
			// caminos por tomar
			if(MT){
				System.out.println(" llegué hijueputa xd");
				cmd.add(language.getAction(0)); // die
			}
			else if (division(PF, PD, PA, PI, MT, FAIL)) {
//				System.out.println("en division " +x +" "+y);
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
				if (primerOpcion.size() == 0 && vecesVisitado(x, y) == 0){
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
					visited.add(newPos);
					cmd.add(language.getAction(2)); // advance
					x = x + dx[dr];
					y = y + dy[dr];
					
				}

			}else{
				if(PF && PD && PI){
					for (int i = 0; i < atras; i++) {
						cmd.add(language.getAction(3)); // rotate
						dr = (dr + 1) % 4;
					} 
				}else{
					if(!PD){
						for (int i = 0; i < derecha; i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						} 
					}if(!PI){
						for (int i = 0; i < izquierda; i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						} 
					}
				}
			}
			
//			System.out.println(" 1 opcion " + primerOpcion);
//			System.out.println(" 2 opcion " + segundaOpcion);
//			System.out.println(" 3 opcion " + tercerOpcion);
			
			if (primerOpcion.size() != 0){
				int cantidad = primerOpcion.size();
				int selec = random.nextInt(cantidad);
				String giro = primerOpcion.get(selec);
				for (int i = 0; i < selecNumGiro(giro); i++) {
					cmd.add(language.getAction(3)); // rotate
					dr = (dr + 1) % 4;
				}
			}
			else{
				if (segundaOpcion.size() != 0){
					int cantidad = segundaOpcion.size();
					int selec = random.nextInt(cantidad);
					String giro = segundaOpcion.get(selec);
					for (int i = 0; i < selecNumGiro(giro); i++) {
						cmd.add(language.getAction(3)); // rotate
						dr = (dr + 1) % 4;
					}
				}
				else{
					if (tercerOpcion.size() != 0){
						/*int cantidad = tercerOpcion.size();
						int selec = random.nextInt(cantidad);
						String giro = tercerOpcion.get(selec);
						for (int i = 0; i < selecNumGiro(giro); i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						}*/
						int veces=Integer.MAX_VALUE;
						int currentPos =-1;
						for (int i = 0; i < tercerOpcion.size(); i++) {
							if (vecesDireccionVisitada(tercerOpcion.get(i))<veces){
								veces = vecesDireccionVisitada(tercerOpcion.get(i));
								currentPos = i;
							}
						}
						String giro = tercerOpcion.get(currentPos);
						for (int i = 0; i < selecNumGiro(giro); i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						}
						/*for (int i = 0; i < 2; i++) {
							cmd.add(language.getAction(3)); // rotate
							dr = (dr + 1) % 4;
						}*/
					}
				}
			}
			
			ArrayList<Integer> newPos = new ArrayList<Integer>() {
				{
					add(x);
					add(y);
				}
			};
			visited.add(newPos);
//			System.out.println("visited "+visited);
			cmd.add(language.getAction(2)); // advance
			x = x + dx[dr];
			y = y + dy[dr];
			
			/*
			int d = accion(PF, PD, PA, PI, MT, FAIL);
			if (0 <= d && d < 4) {
				for (int i = 1; i <= d; i++) {
					cmd.add(language.getAction(3)); // rotate
					dr = (dr + 1) % 4;
				}
				cmd.add(language.getAction(2)); // advance
				x = x + dx[dr];
				y = y + dy[dr];
			} else {
				cmd.add(language.getAction(0)); // die
			}

			ArrayList<Integer> newPos = new ArrayList<Integer>() {
				{
					add(x);
					add(y);
				}
			};
			if (!pos.contains(newPos))
				pos.add(newPos);
			else
				System.out.println("ya pase por aca");
			System.out.println(pos.get(pos.size() - 1));
			*/
		}
		String x = cmd.get(0);
		cmd.remove(0);
		return new Action(x);
	}

	

}
