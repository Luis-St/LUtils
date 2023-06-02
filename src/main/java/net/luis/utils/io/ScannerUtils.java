package net.luis.utils.io;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class ScannerUtils {
	
	public static int nextInt(String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		int value = scanner.nextInt();
		scanner.close();
		return value;
	}
	
	public static long nextLong(String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		long value = scanner.nextLong();
		scanner.close();
		return value;
	}
	
	public static float nextFloat(String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		float value = scanner.nextFloat();
		scanner.close();
		return value;
	}
	
	public static double nextDouble(String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		double value = scanner.nextDouble();
		scanner.close();
		return value;
	}
	
	public static String nextString(String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		String value = scanner.nextLine();
		scanner.close();
		return value;
	}
	
}
