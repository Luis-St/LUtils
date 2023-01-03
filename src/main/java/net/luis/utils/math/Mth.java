package net.luis.utils.math;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 *
 * @author Luis-st
 *
 */

public class Mth {
	
	private static final Logger LOGGER = LogManager.getLogger(Mth.class);
	
	public static byte sum(byte b) {
		String s = String.valueOf(b);
		byte sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Byte.parseByte("" + s.charAt(j));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get byte for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
	public static int sum(int i) {
		String s = String.valueOf(i);
		int sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Integer.parseInt("" + s.charAt(j));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get int for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
	public static long sum(long l) {
		String s = String.valueOf(l);
		long sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Long.parseLong("" + s.charAt(j));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get long for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
	public static int randomInt(Random rng, int min, int max) {
		return min + rng.nextInt(max - min);
	}
	
	public static int randomExclusiveInt(Random rng, int min, int max) {
		return min + rng.nextInt(max - min - 1) + 1;
	}
	
	public static int randomInclusiveInt(Random rng, int min, int max) {
		return min + rng.nextInt(max - min + 1);
	}
	
	public static double roundTo(double value, double roundValue) {
		double d = Math.round(value * roundValue);
		return d / roundValue;
	}
	
	public static boolean isInBounds(int value, int min, int max) {
		return max >= value && value >= min;
	}
	
	public static boolean sameValues(Number... numbers) {
		if (numbers.length == 0) {
			return false;
		} else if (numbers.length == 1) {
			return true;
		}
		Number number = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (!number.equals(numbers[i])) {
				return false;
			}
		}
		return true;
	}
	
	public static float frac(float f) {
		int i = (int) f;
		return f - i;
	}
	
	public static double frac(double f) {
		int i = (int) f;
		return f - i;
	}
	
	public static byte clamp(byte value, byte min, byte max) {
		if (min > value) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}
	
	public static short clamp(short value, short min, short max) {
		if (min > value) {
			return min;
		} else {
			return value > max ? max : value;
		}
	}
	
	public static int clamp(int value, int min, int max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	
	public static long clamp(long value, long min, long max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	
	public static float clamp(float value, float min, float max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	
	public static double clamp(double value, double min, double max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	
	public static double average(byte... values) {
		long sum = 0;
		for (byte value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(short... values) {
		long sum = 0;
		for (short value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(int... values) {
		long sum = 0;
		for (int value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(long... values) {
		long sum = 0;
		for (long value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(float... values) {
		long sum = 0;
		for (float value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(double... values) {
		long sum = 0;
		for (double value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}
	
}
