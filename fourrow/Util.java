package unalcol.agents.examples.games.fourinrow.ISI2017.CEM;

public class Util {
	private static boolean gameSolved(int[][] board,int i,int j,boolean c){
		int t = (c)? 1 : 2;
		int size = board.length;
		boolean izq,drc,abj,dai,dad;
		izq = drc = abj = dai = dad = true;
		for(int x=0;x<4;++x){
			izq = izq && i-x >= 0 && board[i-x][j] == t ;
			drc = drc && i+x < size && board[i+x][j] == t;
			abj = abj && j+x < size && board[i][j+x] == t;
			dai = dai && i-x >= 0 && j+x < size && board[i-x][j+x] == t;
			dad = dad && i+x < size && j+x < size && board[i+x][j+x] == t;
		}
		return izq || drc || abj || dai || dad;
	}

	public static void main(String[] args){
		int[][] mat = {{0,0,0,0,0,0,0,0},
					   {0,0,0,0,0,0,0,0},
					   {0,0,0,0,0,0,0,0},
					   {0,0,0,0,0,0,0,0},
					   {1,1,1,1,0,2,2,0},
					   {0,0,0,0,0,0,0,0},
					   {0,1,0,1,0,1,0,0},
					   {1,0,0,1,0,0,1,0}};
		System.out.println(Util.gameSolved(mat, 4, 3, !true));
		
	}

}
