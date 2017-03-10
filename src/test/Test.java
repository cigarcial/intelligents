package test;

import java.util.*;

public class Test {

	public static void main(String[] args) {
		long init = System.currentTimeMillis();
		for (int x = 0; x < 100000; ++x) {
			System.out.println((x * new Random().nextDouble()) / 100);
		}
		System.out.println(1 << 0);
		System.out.println(((System.currentTimeMillis() - init) / 1000.0));
		for (int x = 0; x < 4; ++x) {
			System.out.println("dir: " + x);
			for (int y = 0; y < 4; ++y) {
				System.out.println((y + 4 - x) % 4);
			}
		}
		int t = 0;
		for (int x = 0; x < 4; ++x) {
			t = ((t >> 2) + 1) << 2;
			System.out.println(t>>2);
		}
	}
}
