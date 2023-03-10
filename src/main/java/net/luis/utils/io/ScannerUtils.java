package net.luis.utils.io;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class ScannerUtils {
	
	public static int nextInt(@NotNull String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		int value = scanner.nextInt();
		scanner.close();
		return value;
	}
	
	public static long nextLong(@NotNull String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		long value = scanner.nextLong();
		scanner.close();
		return value;
	}
	
	public static float nextFloat(@NotNull String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		float value = scanner.nextFloat();
		scanner.close();
		return value;
	}
	
	public static double nextDouble(@NotNull String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		double value = scanner.nextDouble();
		scanner.close();
		return value;
	}
	
	public static @NotNull String nextString(@NotNull String output) {
		Scanner scanner = new Scanner(System.in);
		if (!StringUtils.isEmpty(output)) {
			System.out.println(output);
		}
		String value = scanner.nextLine();
		scanner.close();
		return value;
	}
	
}