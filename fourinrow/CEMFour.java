/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.agents.examples.games.fourinrow;

import java.util.ArrayList;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;

/**
 *
 * @author Jonatan
 */
public class CEMFour implements AgentProgram {
	protected String color;

	int[][] tablero = null;

	public CEMFour(String color) {
		this.color = color;
	}

	public int[][] tablero(int n) {
		if (tablero == null) {
			tablero = new int[n][n];
			return tablero;
		} else
			return tablero;
	}

	public void setTablero(Percept p) {
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero.length; j++) {
				if (p.getAttribute((i) + ":" + j).equals((String) FourInRow.SPACE))
					tablero[i][j] = 0;
				else if (p.getAttribute((i) + ":" + j).equals((String) FourInRow.BLACK))
					tablero[i][j] = 1;
				else if (p.getAttribute((i) + ":" + j).equals((String) FourInRow.WHITE))
					tablero[i][j] = 2;
			}
		}
	}

	public void printTablero() {
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero.length; j++) {
				System.out.print(tablero[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("---------------------");
	}

	private ArrayList<int[]> jugadas() {
		ArrayList<int[]> siguientesJugadas = new ArrayList<int[]>();

		/*
		 * If gameover, i.e., no next move if (hasWon(mySeed) ||
		 * hasWon(oppSeed)) { return siguientesJugadas; // return empty list }
		 */

		// Busca casillas vacías
		for (int col = 0; col < tablero.length; col++) {
			for (int row = tablero.length - 1; row >= 0; row--) {
				if (tablero[row][col] == 0) {
					siguientesJugadas.add(new int[] { row, col });
					break;
				}
			}
		}
		return siguientesJugadas;
	}

	private int[] minimax(int depth, String jugada, int alpha, int beta) {
		// Genera posible jugadas
		ArrayList<int[]> siguientesJugadas = jugadas();

		// mySeed is maximizing; while oppSeed is minimizing
		int puntaje;
		int mejorFila = -1;
		int mejorColumna = -1;

		if (siguientesJugadas.isEmpty() || depth == 0) {
			// System.out.println("evaluo");
			// System.out.println("depth 0");
			// Fin del juego o profundidad alcanzada
			puntaje = evaluar();
			return new int[] { puntaje, mejorFila, mejorColumna };
		} else {
			for (int[] movimiento : siguientesJugadas) {
				// probar el movimiento en el tablero
				tablero[movimiento[0]][movimiento[1]] = (jugada.equals("black")) ? 1 : 2;
				// System.out.println(movimiento[0] + " " + movimiento[1] + " "
				// + tablero[movimiento[0]][movimiento[1]]);
				if (jugada.equals(color)) { // yo maximizo
					puntaje = minimax(depth - 1, (color.equals("white")) ? "black" : "white", alpha, beta)[0];
					// System.out.println("color: " + jugada + " puntaje: " +
					// puntaje);
					if (puntaje > alpha) {
						// System.out.println("cambio de alpha");
						alpha = puntaje;
						mejorFila = movimiento[0];
						mejorColumna = movimiento[1];
					}
				} else { // enemigo minimiza
					puntaje = minimax(depth - 1, (color.equals("white")) ? "white" : "black", alpha, beta)[0];
					// System.out.println("color: " + jugada + " puntaje: " +
					// puntaje);
					if (puntaje < beta) {
						// System.out.println("cambio de beta");
						beta = puntaje;
						mejorFila = movimiento[0];
						mejorColumna = movimiento[1];
					}
				}
				// undo move
				tablero[movimiento[0]][movimiento[1]] = 0;
				// cut-off
				if (alpha >= beta)
					break;
			}
			// System.out.println("alpha " + alpha + " beta " + beta);
			return new int[] { (jugada.equals(color)) ? alpha : beta, mejorFila, mejorColumna };
		}
	}

	private int evaluar() {
		int puntaje = 0;
		puntaje += evalFilas();
		puntaje += evalColumnas();
		 puntaje += evalDiagonalAs();
		puntaje += evalDiagonalDes();
		return puntaje;
	}

	private boolean posibleDeCalcularDiaIzq(int i, int j) {
		if(i+4< tablero.length -1 && j-3>=0){
			//System.out.println("dentro");
		return (tablero[i + 1][j] != 0 && tablero[i + 2][j - 1] != 0
				&& tablero[i + 3][j - 2] != 0 && tablero[i + 4][j - 3] != 0);
		}else if(i ==  tablero.length -4 && j-3>=0){
			//System.out.println(" casi dentro");
			return  (tablero[i + 1][j] != 0 && tablero[i + 2][j - 1] != 0
					&& tablero[i + 3][j - 2] != 0);
		}else{
			//System.out.println("paila");
			return false;
		}
	}
	private boolean posibleDeCalcularDiaDer(int i, int j) {
		if(i+4< tablero.length -1 && j+3<tablero.length-1){
			//System.out.println("dentro");
		return (tablero[i + 1][j] != 0 && tablero[i + 2][j + 1] != 0
				&& tablero[i + 3][j + 2] != 0 && tablero[i + 4][j + 3] != 0);
		}else if(i ==  tablero.length -4 && j+3<tablero.length-1){
			//System.out.println(" casi dentro");
			return  (tablero[i + 1][j] != 0 && tablero[i + 2][j + 1] != 0
					&& tablero[i + 3][j + 2] != 0);
		}else{
			//System.out.println("paila");
			return false;
		}
	}
private int evalDiagonalDes() {
		
		int total = 0;
		//printTablero();
		for (int i = 0; i < tablero.length-3; i++) {
			ArrayList<Integer> puntajes = new ArrayList<>();
			for (int j = 0; j < tablero.length-3; j++) {
				int puntaje = 0;
				//int i = k - j;
				//System.out.println(posibleDeCalcularDiaIzq(i, j));
				if (posibleDeCalcularDiaDer(i, j)) {
					//printTablero();
					//System.out.println("i: " + i + " j: " + j);
					if (tablero[i][j] == ((color.equals("black")) ? 1 : 2)) {
						puntaje = 1;
					} else if (tablero[i][j] == ((color.equals("black")) ? 2 : 1)) {
						puntaje = -1;
					} else {
						puntaje = 0;
					}
					if (tablero[i + 1][j + 1] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 1) { // celda1 mia
							puntaje = 10;
						} else if (puntaje < 0) { // celda1 oponente
							puntaje = 0;
						} else { // celda1 vacía
							puntaje = 1;
						}
					} else if (tablero[i + 1][j + 1] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -1) { // celda2 mia
							puntaje = -10;
						} else if (puntaje > 0) { // celda2 oponente
							puntaje = 0;
						} else { // celda2 vacía
							puntaje = -1;
						}
					}
					if (tablero[i + 2][j + 2] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
					} else if (tablero[i + 2][j + 2] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}

					if (tablero[i + 3][j + 3] == ((color.equals("black")) ? 1 : 2)) {
						// System.out.println("posicion 4ta con blanco puntaje
						// actual " + puntaje +";"+ tablero[i][j]+" "+
						// tablero[i][j+1]+" "+ tablero[i][j+2]);
						// System.out.println("posicion 1 con blanco 4 " +
						// tablero[i][j]);
						if (puntaje == 100) { // celda4 mia
							puntaje = 1000;
						} else if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
						// System.out.println("puntaje despues de 4ta posicion "
						// + puntaje);
					} else if (tablero[i + 3][j + 3] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -100) { // celda4 mia
							puntaje = -1000;
						} else if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}

				}
				puntajes.add(puntaje);
				total += puntaje;
			}
			//System.out.println("puntajes " + puntajes);
		}
		return total;
	}

	private int evalDiagonalAs() {
		
		int total = 0;
		//printTablero();
		for (int i = 0; i < tablero.length-3; i++) {
			ArrayList<Integer> puntajes = new ArrayList<>();
			for (int j = 3; j < tablero.length; j++) {
				int puntaje = 0;
				//int i = k - j;
				//System.out.println(posibleDeCalcularDiaIzq(i, j));
				if (posibleDeCalcularDiaIzq(i, j)) {
					//printTablero();
					//System.out.println("i: " + i + " j: " + j);
					if (tablero[i][j] == ((color.equals("black")) ? 1 : 2)) {
						puntaje = 1;
					} else if (tablero[i][j] == ((color.equals("black")) ? 2 : 1)) {
						puntaje = -1;
					} else {
						puntaje = 0;
					}
					if (tablero[i + 1][j - 1] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 1) { // celda1 mia
							puntaje = 10;
						} else if (puntaje < 0) { // celda1 oponente
							puntaje = 0;
						} else { // celda1 vacía
							puntaje = 1;
						}
					} else if (tablero[i + 1][j - 1] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -1) { // celda2 mia
							puntaje = -10;
						} else if (puntaje > 0) { // celda2 oponente
							puntaje = 0;
						} else { // celda2 vacía
							puntaje = -1;
						}
					}
					if (tablero[i + 2][j - 2] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
					} else if (tablero[i + 2][j - 2] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}

					if (tablero[i + 3][j - 3] == ((color.equals("black")) ? 1 : 2)) {
						// System.out.println("posicion 4ta con blanco puntaje
						// actual " + puntaje +";"+ tablero[i][j]+" "+
						// tablero[i][j+1]+" "+ tablero[i][j+2]);
						// System.out.println("posicion 1 con blanco 4 " +
						// tablero[i][j]);
						if (puntaje == 100) { // celda4 mia
							puntaje = 1000;
						} else if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
						// System.out.println("puntaje despues de 4ta posicion "
						// + puntaje);
					} else if (tablero[i + 3][j - 3] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -100) { // celda4 mia
							puntaje = -1000;
						} else if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}

				}
				puntajes.add(puntaje);
				total += puntaje;
			}
			//System.out.println("puntajes " + puntajes);
		}
		
		return total;
	}

	

	private int evalColumnas() {
		// printTablero();
		int total = 0;
		for (int j = 0; j < tablero.length; j++) {
			// ArrayList<Integer> puntajes = new ArrayList<>();
			int puntaje = 0;
			for (int i = tablero.length - 1; i >= 4; i--) {
				if (tablero[i][j] != 0) {
					if (tablero[i][j] == ((color.equals("black")) ? 1 : 2)) {
						puntaje = 1;
					} else if (tablero[i][j] == ((color.equals("black")) ? 2 : 1)) {
						puntaje = -1;
					} else {
						puntaje = 0;
					}
					if (tablero[i - 1][j] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 1) { // celda1 mia
							puntaje = 10;
						} else if (puntaje < 0) { // celda1 oponente
							puntaje = 0;
						} else { // celda1 vacía
							puntaje = 1;
						}
					} else if (tablero[i - 1][j] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -1) { // celda2 mia
							puntaje = -10;
						} else if (puntaje > 0) { // celda2 oponente
							puntaje = 0;
						} else { // celda2 vacía
							puntaje = -1;
						}
					}
					if (tablero[i - 2][j] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
					} else if (tablero[i - 2][j] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}

					if (tablero[i - 3][j] == ((color.equals("black")) ? 1 : 2)) {
						// System.out.println("posicion 4ta con blanco puntaje
						// actual " + puntaje +";"+ tablero[i][j]+" "+
						// tablero[i][j+1]+" "+ tablero[i][j+2]);
						// System.out.println("posicion 1 con blanco 4 " +
						// tablero[i][j]);
						if (puntaje == 100) { // celda4 mia
							puntaje = 1000;
						} else if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
						// System.out.println("puntaje despues de 4ta posicion "
						// + puntaje);
					} else if (tablero[i - 3][j] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -100) { // celda4 mia
							puntaje = -1000;
						} else if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}

				} else {
					break;
				}
				// puntajes.add(puntaje);
				total += puntaje;

			}
			// System.out.println("puntajes " + puntajes);
			// System.out.println("puntaje col " + puntaje+" "+j);
		}
		// System.out.println("total " + total);
		return total;
	}

	public boolean posibleDeCalcular(int i, int j) {
		return (i == tablero.length - 1) || (tablero[i + 1][j] != 0 && tablero[i + 1][j + 1] != 0
				&& tablero[i + 1][j + 2] != 0 && tablero[i + 1][j + 3] != 0);
	}

	private int evalFilas() {
		// printTablero();

		int total = 0;
		for (int i = tablero.length - 1; i >= 0; i--) {
			int puntaje = 0;
			// ArrayList<Integer> puntajes = new ArrayList<>();
			for (int j = 0; j < tablero.length - 3; j++) {
				if (posibleDeCalcular(i, j)) {
					if (tablero[i][j] == ((color.equals("black")) ? 1 : 2)) {
						puntaje = 1;
					} else if (tablero[i][j] == ((color.equals("black")) ? 2 : 1)) {
						puntaje = -1;
					} else {
						puntaje = 0;
					}
					if (tablero[i][j + 1] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 1) { // celda1 mia
							puntaje = 10;
						} else if (puntaje < 0) { // celda1 oponente
							puntaje = 0;
						} else { // celda1 vacía
							puntaje = 1;
						}
					} else if (tablero[i][j + 1] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -1) { // celda2 mia
							puntaje = -10;
						} else if (puntaje > 0) { // celda2 oponente
							puntaje = 0;
						} else { // celda2 vacía
							puntaje = -1;
						}
					}
					if (tablero[i][j + 2] == ((color.equals("black")) ? 1 : 2)) {
						if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
					} else if (tablero[i][j + 2] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}
					if (tablero[i][j + 3] == ((color.equals("black")) ? 1 : 2)) {
						// System.out.println("posicion 4ta con blanco puntaje
						// actual " + puntaje +";"+ tablero[i][j]+" "+
						// tablero[i][j+1]+" "+ tablero[i][j+2]);
						// System.out.println("posicion 1 con blanco 4 " +
						// tablero[i][j]);
						if (puntaje == 100) { // celda4 mia
							puntaje = 1000;
						} else if (puntaje == 10) { // celda3 mia
							puntaje = 100;
						} else if (puntaje == 1) {
							puntaje = 10;
						} else if (puntaje < 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = 1;
						}
						// System.out.println("puntaje despues de 4ta posicion "
						// + puntaje);
					} else if (tablero[i][j + 3] == ((color.equals("black")) ? 2 : 1)) {
						if (puntaje == -100) { // celda4 mia
							puntaje = -1000;
						} else if (puntaje == -10) { // celda3 mia
							puntaje = -100;
						} else if (puntaje == -1) {
							puntaje = -10;
						} else if (puntaje > 0) { // celda3 oponente
							puntaje = 0;
						} else { // celda3 vacía
							puntaje = -1;
						}
					}
				}
				// puntajes.add(puntaje);
				total += puntaje;
			}

			// System.out.println("puntajes " + puntajes);

		}
		// System.out.println("total filas: " + total);
		return total;
	}

	@Override
	public Action compute(Percept p) {

		long time = (long) (20 * Math.random());
		try {
			Thread.sleep(time);
		} catch (Exception e) {
		}
		if (p.getAttribute(FourInRow.TURN).equals(color)) {

			int n = Integer.parseInt((String) p.getAttribute(FourInRow.SIZE));
			tablero = tablero(n);
			setTablero(p);
			// printTablero();
			int[] result = null;
			if (p.getAttribute(n - 1 + ":" + n / 2).equals((String) FourInRow.SPACE)) {
				return new Action(n - 1 + ":" + n / 2 + ":" + color);
			} else {
				result = minimax(6, color, Integer.MIN_VALUE, Integer.MAX_VALUE);
				// System.out.println("result---------------------------------------------");
				// System.out.println(result[0] + " " + result[1] + " " +
				// result[2]);
				// depth, max-turn, alpha, beta
				/*
				 * return new int[] {result[1], result[2]}; // row, col int i =
				 * (int) (n * Math.random()); int j = (int) (n * Math.random());
				 * boolean flag = (i == n - 1) || !p.getAttribute((i + 1) + ":"
				 * + j).equals((String) FourInRow.SPACE); while (!flag) { i =
				 * (int) (n * Math.random()); j = (int) (n * Math.random());
				 * flag = (i == n - 1) || !p.getAttribute((i + 1) + ":" +
				 * j).equals((String) FourInRow.SPACE); }
				 */
			}
			return new Action(result[1] + ":" + result[2] + ":" + color);
		}
		return new Action(FourInRow.PASS);
	}

	@Override
	public void init() {
	}

}