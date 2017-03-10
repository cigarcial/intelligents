package test;

import java.util.*;

public class Test {

	
	public static void main(String[] args){
		long init = System.currentTimeMillis();
		for(int x=0;x<100000;++x){
			System.out.println(( x*new Random().nextDouble() )/100);
		}
		System.out.println(1<<0);
		System.out.println( ((System.currentTimeMillis()-init)/1000.0) );
	}
}
