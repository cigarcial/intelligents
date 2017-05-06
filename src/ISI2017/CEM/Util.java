package unalcol.agents.examples.labyrinth.multeseo.eater.ISI2017.CEM;

public class Util {
	
	public static void main(String[] args){
		int k =  (1<<9) | (1<<15) | (1<<18);
		for(int x=0;x<4;++x){
			if(Math.random() > 0.5){
				k = k | (1<<(10+x));
				System.out.print("1");
			}else{
				System.out.print("0");
			}
		}
		System.out.println();
		System.out.println(k);
		int w = (k>>10) & 15;
		System.out.println(1<<0);
		
	}

}
