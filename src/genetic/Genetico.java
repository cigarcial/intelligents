package genetic;

import java.util.Random;

public class Genetico {
	
	int[] dfa(int[][] T, int[][] out, int[] in){
		int[] o = new int[in.length];
		int state = 0;
		for (int i = 0; i < in.length; i++) {
			o[i] = out[state][in[i]];
			state = T[state][in[i]];
		}
		return o;
	}
	
	boolean[] codifica(int[][] t, int[][] out){
		int qn = t.length;
		boolean[] code = new boolean[Math.ceil((Math.log(qn,2)))*2*qn];
		int k = 0;
		for (int i = 0; i < qn; i++) {
			for (int j = 0; j < 2; j++) {
				code[k++] = t[i][j]/2==1;
				code[k++] = t[i][j]%2==1;
				code[k++] = out[i][j]==1;
			}
		}
		return code;
	}
	
	int[][][] decodifica ( boolean[] code){
		int[][] t = new int [4][2];
		int[][] out = new int [4][2];
		int k = 0;
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t[i].length; j++) {
				t[i][j] = (code[k]?2:0) + (code[k+1]?1:0);
				out[i][j] = (code[k]?1:0);
				k+=3;
			}
		}
		return new int[][][]{t,out};
	}
	
	double f(int[][] t,int[][] out, int[] in ){
		int[] o = dfa(t,out,in);
		int s =0;
		for (int i = 0; i < in.length; i++) {
			s += (in[i]==o[i-1]?1:0);
		}
		return s;
	}
	
	boolean[][] cruce(boolean[] p, boolean[] m){
		boolean[] h1 = p.clone();
		boolean[] h2 = m.clone();
		int pc = (int) (Math.random()*h1.length-1)+1;
		for (int i = 0; i < h1.length; i++) {
			boolean tmp = h1[i];
			h1[i] = h2[i];
			h2[i] = tmp;
		}
		return new boolean[][]{h1,h2};
	}
	
	
	int ruleta(double[] x){
		double s=0;
		for (int i = 0; i < x.length; i++) {
			s+=x[i];
		}
		double u = Math.random();
		System.out.println(u);
		double[] p = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			p[i] =x[i]/s;
			System.out.println(p[i]);
		}
		int k =0;
		while(k<p.length && p[k]<u){
			u -= p[k];
			k++;
		}
		return k;
	}
	
	int torneo (int[] ind, double[] fit){
		while(ind.length>1){
			int[] nind = new int[ind.length/2];
			double[] nfit = new double[fit.length/2];
			for (int i = 0; i < ind.length; i+=2) {
				int r = ruleta(new double[]{fit[i], fit[i+1]});
				nind[i/2]= ind[i+r];
				nfit[i/2]= fit[i+r];
			}
			fit =nfit;
			ind=nind;
		}
		return ind[0];
	}
	
	int ruleta_muerte(int[] x){
		int fm = x[0];
		int fM = x[0];
		for (int i = 0; i < x.length; i++) {
			if(x[i]>fM) fM=x[i];
			else if (x[i]<fm)fm = x[i];
		}
		double[] xr = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			xr[i] = fM + fm - x[i];
		}
		return ruleta(xr);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*Random rand = new Random();
		int n=5;
		int cont=0;
		int[] ruleta = new int[n];
		
		for (int i = 0; i < ruleta.length; i++) {
			ruleta[i] = rand.nextInt(15);
			cont+=ruleta[i];
		}*/
		
		double[] k = {8,5,2,2};
		Genetico g = new Genetico();
		System.out.println(g.ruleta(k));
		

	}

}
