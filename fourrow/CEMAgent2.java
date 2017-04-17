package unalcol.agents.examples.games.fourinrow.ISI2017.CEM;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.examples.games.fourinrow.FourInRow;

public class CEMAgent2 implements AgentProgram {

	private String color;
	private boolean WARNING;
	private int SEARCH_DEPTH = 6;

	public CEMAgent2(String c) {
		color = c;
		WARNING = false;
	}

	private Action nextAction(Percept p) {
		int n = Integer.parseInt((String) p.getAttribute(FourInRow.SIZE));
		Point[] ret = pureMinMax(p);
		// System.out.println(ret[0]);
		// System.out.println(ret[1]);
		int idx = 0;
		if (ret[1] != null) {
			idx = 1;
		}
		if (ret[0] == null) {
			return randomAction(p);
		}
		int x = (int) ret[idx].getX();
		int y = (int) ret[idx].getY();
		if (idx == 1) {
			try {
				Thread.sleep((long) (200 * Math.random()));
			} catch (Exception e) {
			}
			System.out.println("Warning at: " + x + ", " + y + "!!!");
		}
		return new Action( x + ":" + y + ":" + color);
		// return randomAction(p);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public Action compute(Percept p) {
		// try{
		if (WARNING) {
			return randomAction(p);
		}
		try {
			Thread.sleep((long) (20 * Math.random()));
		} catch (Exception e) {
		}
		if (p.getAttribute(FourInRow.TURN).equals(color)) {
			return nextAction(p);
		}
		return new Action(FourInRow.PASS);
		/*
		 * }catch(Exception ex){ System.out.println("PANIC"); WARNING = true;
		 * return randomAction(p); }
		 */
	}

	/** Solo si el agente falla se juega aleatorio, es mejor a no jugar nada */
	private Action randomAction(Percept p) {
		int n = Integer.parseInt((String) p.getAttribute(FourInRow.SIZE));
		int i = (int) (n * Math.random());
		int j = (int) (n * Math.random());
		boolean flag = (i == n - 1) || !p.getAttribute((i + 1) + ":" + j).equals((String) FourInRow.SPACE);
		while (!flag) {
			i = (int) (n * Math.random());
			j = (int) (n * Math.random());
			flag = (i == n - 1) || !p.getAttribute((i + 1) + ":" + j).equals((String) FourInRow.SPACE);
		}
		return new Action(i + ":" + j + ":" + color);
	}

	private Point[] pureMinMax(Percept p) {
		// Map<Point,Integer> board = new HashMap<Point,Integer>();
		int n = Integer.parseInt((String) p.getAttribute(FourInRow.SIZE));
		int[][] board = new int[n][n];
		// board construction
		for (int x = 0; x < n; ++x) {
			for (int y = 0; y < n; ++y) {
				int value = 2;
				if (p.getAttribute(x + ":" + y).equals((String) FourInRow.SPACE)) {
					value = 0;
				} else if (p.getAttribute(x + ":" + y).equals((String) FourInRow.WHITE)) {
					value = 1;
				} else if (p.getAttribute(x + ":" + y).equals((String) FourInRow.BLACK)) {
					value = 2;
				}
				board[x][y] = value;
			}
		}

		Point warning = findTrouble(board, !color.equals((String) FourInRow.WHITE));

		int[] tops = new int[n];
		for (int x = 0; x < n; ++x) {
			tops[x] = getTop(board, x);
		}

		Point best = maxSearch(board, 0, color.equals((String) FourInRow.WHITE));

		if (best.getY() < 0 || best.getY() >= n) {
			return new Point[] { null, warning };
		}

		int x = (int) best.getY();
		if (best.getX() == Integer.MAX_VALUE) {
			return new Point[] { new Point(tops[x], x), new Point(tops[x], x) };
		}
		return new Point[] { new Point(tops[x], x), warning };
	}

	private Point maxSearch(int[][] board, int lvl, boolean c) {

		if (fullBoard(board)) {
			return new Point(10, -1);
		}

		int size = board.length;
		Point best = new Point(Integer.MIN_VALUE, -1);

		for (int x = 0; x < size; ++x) {

			int top = getTop(board, x);
			if (top == -1) {
				continue;
			}

			board[top][x] = (c) ? 1 : 2;

			if (gameSolved(board, top, x, c)) {
				board[top][x] = 0;
				return new Point(Integer.MAX_VALUE, x);
			}
			Point ac = null;
			if (lvl + 1 >= SEARCH_DEPTH) {
				ac = new Point(eval(board, c), x);
			} else {
				ac = minSearch(board, lvl + 1, !c);
			}

			board[top][x] = 0;
			if (best.getX() < ac.getX()) {
				best = new Point((int) ac.getX(), x);
			}
			if (best.getX() == ac.getX() && Math.random() > 0.7) {
				best = new Point((int) ac.getX(), x);
			}
		}
		return best;
	}

	private Point minSearch(int[][] board, int lvl, boolean c) {
		if (fullBoard(board)) {
			return new Point(0, -1);
		}

		int size = board.length;
		Point best = new Point(Integer.MAX_VALUE, -1);

		for (int x = 0; x < size; ++x) {
			int top = getTop(board, x);
			if (top == -1) {
				continue;
			}
			board[top][x] = (c) ? 1 : 2;
			if (gameSolved(board, top, x, c)) {
				board[top][x] = 0;
				return new Point(Integer.MIN_VALUE, x);
			}

			Point ac = null;
			if (lvl + 1 >= SEARCH_DEPTH ) {
				ac = new Point(eval(board, c), x);
			} else {
				ac = maxSearch(board, lvl + 1, !c);
			}
			board[top][x] = 0;

			if (best.getX() > ac.getX()) {
				best = new Point((int) ac.getX(), x);
			}
			if (best.getX() == ac.getX() && Math.random() > 0.7) {
				best = new Point((int) ac.getX(), x);
			}
		}
		return best;
	}

	private boolean gameSolved(int[][] board, int i, int j, boolean c) {
		int t = (c) ? 1 : 2;
		int size = board.length;
		boolean izq, drc, abj, dai, dad;
		izq = drc = abj = dai = dad = true;
		for (int x = 0; x < 4; ++x) {
			izq = izq && j - x >= 0 && board[i][j - x] == t;
			drc = drc && j + x < size && board[i][j + x] == t;
			abj = abj && i + x < size && board[i + x][j] == t;
			dai = dai && i + x < size && j - x >= 0 && board[i + x][j - x] == t;
			dad = dad && i + x < size && j + x < size && board[i + x][j + x] == t;
		}
		return izq || drc || abj || dai || dad;
	}

	private boolean fullBoard(int[][] board) {
		boolean ret = true;
		int size = board.length;
		for (int x = 0; x < size; ++x) {
			ret = ret && getTop(board, x) == -1;
		}
		return ret;
	}

	private int getTop(int[][] board, int col) {
		int s = board.length;
		for (int x = s - 1; x >= 0; x--) {
			if (board[x][col] == 0) {
				return x;
			}
		}
		return -1;
	}

	// mejorar esta funcion, da un poco de asco
	private int eval(int[][] board, boolean c) {
		int n = board.length;
		int k = (c) ? 1 : 2;
		int ret = 0;
		for (int x = 0; x < n; ++x) {
			for (int y = 0; y < n; ++y) {
				ret += (board[x][y] == k) ? 1 : 0;
			}
		}
		return ret;
	}

	private Point findTrouble(int[][] board, boolean c) {
		int size = board.length;
		for (int x = 0; x < size; ++x) {
			int top = getTop(board, x);
			if (top == -1) {
				continue;
			}
			board[top][x] = (c) ? 1 : 2;
			if (gameSolved(board, top, x, c)) {
				board[top][x] = 0;
				return new Point(top, x);
			}
			board[top][x] = 0;
		}

		return null;

	}

}
