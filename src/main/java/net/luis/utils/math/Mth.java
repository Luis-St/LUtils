package net.luis.utils.math;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Random;

/**
 *
 * @author Luis-St
 *
 */

public class Mth {
	
	private static final Logger LOGGER = LogManager.getLogger(Mth.class);
	
	//region Sum
	public static long sum(int value) {
		String s = String.valueOf(value);
		long sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Long.parseLong(String.valueOf(s.charAt(j)));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get int for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	
	public static long sum(long value) {
		String s = String.valueOf(value);
		long sum = 0;
		for (int j = 0; j < s.length(); j++) {
			try {
				sum += Long.parseLong(String.valueOf(s.charAt(j)));
			} catch (NumberFormatException e) {
				LOGGER.warn("Fail to get long for char {}", s.charAt(j));
			}
		}
		return sum;
	}
	//endregion
	
	//region Random
	public static int randomInt(Random rng, int min, int max) {
		return min + Objects.requireNonNull(rng, "Random must not be null").nextInt(max - min);
	}
	
	public static int randomExclusiveInt(Random rng, int min, int max) {
		return min + Objects.requireNonNull(rng, "Random must not be null").nextInt(max - min - 1) + 1;
	}
	
	public static int randomInclusiveInt(Random rng, int min, int max) {
		return min + Objects.requireNonNull(rng, "Random must not be null").nextInt(max - min + 1);
	}
	//endregion
	
	public static double roundTo(double value, double roundValue) {
		double d = Math.round(value * roundValue);
		return d / roundValue;
	}
	
	public static boolean isInBounds(int value, int min, int max) {
		return max >= value && value >= min;
	}
	
	public static boolean sameValues(Number... numbers) {
		if (numbers == null || numbers.length == 0) {
			return false;
		} else if (numbers.length == 1) {
			return true;
		}
		Number number = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (number.intValue() != numbers[i].intValue()) {
				return false;
			}
		}
		return true;
	}
	
	public static double frac(double f) {
		int i = (int) f;
		return f - i;
	}
	
	//region Clamp
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
	
	public static double clamp(double value, double min, double max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	//endregion
	
	//region Average
	public static double average(int... values) {
		values = ArrayUtils.nullToEmpty(values);
		long sum = 0;
		for (int value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(long... values) {
		values = ArrayUtils.nullToEmpty(values);
		long sum = 0;
		for (long value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	
	public static double average(double... values) {
		values = ArrayUtils.nullToEmpty(values);
		long sum = 0;
		for (double value : values) {
			sum += value;
		}
		return (double) sum / (double) values.length;
	}
	//endregion
	
	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}
}
