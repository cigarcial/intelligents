package test;

import java.util.*;

public class Test {

	
	public static void main(String[] args){
		for(int x=0;x<1000;++x){
			System.out.println(( x*new Random().nextDouble() )/100);
		}
		System.out.println(1<<0);
	}
}
