package unalcol.agents.examples.games.fourinrow.ISI2017.CEM;

public class Util {
	private static boolean gameSolvedGeneral(int[][] board, int i, int j, boolean c) {
		int[] di = {-1,-1,-1,0,0,0,1,1,1};
		int[] dj = {-1,0,1,-1,0,1,-1,0,1};
		boolean ret = false;
		for (int x = 0; x < di.length && !ret; ++x) {
			int ii = i + di[x];
			int jj = j + dj[x];
			boolean c1 = ii >= 0 && ii < board.length;
			boolean c2 = jj >= 0 && jj < board.length;
			if (c1 && c2) {
				ret = ret || gameSolved(board, ii, jj, c);
			}
		}
		return ret;
	}

	private static boolean gameSolved(int[][] board, int i, int j, boolean c) {
		int t = (c) ? 1 : 2;
		int size = board.length;
		boolean izq, drc, abj, dai, dad, dui, dud;
		izq = drc = abj = dai = dad = dui = dud = true;
		for (int x = 0; x < 4; ++x) {
			izq = izq && j - x >= 0 && board[i][j - x] == t;
			drc = drc && j + x < size && board[i][j + x] == t;
			abj = abj && i + x < size && board[i + x][j] == t;

			dai = dai && i + x < size && j - x >= 0 && board[i + x][j - x] == t;
			dad = dad && i + x < size && j + x < size && board[i + x][j + x] == t;

			dui = dui && i - x >= 0 && j - x >= 0 && board[i - x][j - x] == t;
			dud = dud && i - x < size && j + x < size && board[i - x][j + x] == t;
		}
		return izq || drc || abj || dai || dad || dui || dud;
	}

	public static void main(String[] args){
		int[][] mat = {{0,0,0,0,0,0,0,0},
					   {0,0,0,0,0,0,0,0},
					   {0,0,0,0,0,0,0,0},
					   {0,0,0,0,0,0,0,0},
					   {1,1,1,1,0,2,2,0},
					   {0,0,1,0,0,0,0,0},
					   {0,1,0,1,0,1,0,0},
					   {1,0,0,1,0,0,1,0}};
		System.out.println(Util.gameSolvedGeneral(mat, 6, 1, true));
		
	}

}
