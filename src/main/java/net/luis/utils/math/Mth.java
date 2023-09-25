package net.luis.utils.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.stream.*;

/**
 *
 * @author Luis-St
 *
 */

public class Mth {
	
	//region Sum
	public static int sum(int value) {
		int sum = 0;
		while (value != 0) {
			sum += value % 10;
			value /= 10;
		}
		return sum;
	}
	
	public static int sum(long value) {
		int sum = 0;
		while (value != 0) {
			sum += (int) (value % 10);
			value /= 10;
		}
		return sum;
	}
	//endregion
	
	//region Random
	public static int randomInt(@NotNull Random rng, int min, int max) {
		return min + Objects.requireNonNull(rng, "Random must not be null").nextInt(max - min);
	}
	
	public static int randomExclusiveInt(@NotNull Random rng, int min, int max) {
		return min + Objects.requireNonNull(rng, "Random must not be null").nextInt(max - min - 1) + 1;
	}
	
	public static int randomInclusiveInt(@NotNull Random rng, int min, int max) {
		return min + Objects.requireNonNull(rng, "Random must not be null").nextInt(max - min + 1);
	}
	//endregion
	
	public static double roundTo(double value, int roundValue) {
		double i = 1;
		for (int j = 0; j < roundValue; j++) {
			i *= 10;
		}
		return Math.round(value * i) / i;
	}
	
	public static boolean isInBounds(int value, int min, int max) {
		return max >= value && value >= min;
	}
	
	public static boolean sameValues(Number @Nullable ... numbers) {
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
		return f - ((int) f);
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
	public static double average(int @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.NaN;
		}
		return IntStream.of(values).average().orElse(0);
	}
	
	public static double average(long @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.NaN;
		}
		return LongStream.of(values).average().orElse(0);
	}
	
	public static double average(double @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.NaN;
		}
		return DoubleStream.of(values).average().orElse(0);
	}
	//endregion
	
	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}
}
