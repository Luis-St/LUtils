/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.math;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.*;
import java.util.Objects;
import java.util.Random;
import java.util.stream.*;

/**
 * Utility class for math operations.
 *
 * @author Luis-St
 */
public final class Mth {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Mth() {}
	
	//region Sum
	
	/**
	 * Sums all digits of the given integer.<br>
	 * If the given integer is negative, the absolute value will be used.<br>
	 *
	 * @param value The number to sum.
	 * @return The sum of all digits
	 */
	public static int sum(int value) {
		value = Math.abs(value);
		int sum = 0;
		while (value != 0) {
			sum += value % 10;
			value /= 10;
		}
		return sum;
	}
	
	/**
	 * Sums all digits of the given long.<br>
	 * If the given long is negative, the absolute value will be used.<br>
	 *
	 * @param value The number to sum
	 * @return The sum of all digits
	 */
	public static int sum(long value) {
		value = Math.abs(value);
		int sum = 0;
		while (value != 0) {
			sum += (int) (value % 10);
			value /= 10;
		}
		return sum;
	}
	//endregion
	
	//region Random
	
	/**
	 * Returns a random integer between min (inclusive) and max (exclusive).<br>
	 * The value will be greater than or equal to min and less than max (min &le; value &lt; max).<br>
	 *
	 * @param rng The random number generator
	 * @param min The minimum value
	 * @param max The maximum value
	 * @return A random integer between min (inclusive) and max (exclusive)
	 * @throws NullPointerException If the given random number generator is null
	 * @throws IllegalArgumentException If the given minimum value is greater than or equal to the given maximum value
	 */
	public static int randomInt(@NotNull Random rng, int min, int max) {
		Objects.requireNonNull(rng, "Random must not be null");
		if (min >= max) {
			throw new IllegalArgumentException("The minimum value must be less than the maximum value");
		}
		return min + rng.nextInt(max - min);
	}
	
	/**
	 * Returns a random integer between min (exclusive) and max (exclusive).<br>
	 * The value will be greater than min and less than max (min &lt; value &lt; max).<br>
	 *
	 * @param rng The random number generator
	 * @param min The minimum value
	 * @param max The maximum value
	 * @return A random integer between min (exclusive) and max (exclusive)
	 * @throws NullPointerException If the given random number generator is null
	 * @throws IllegalArgumentException If the given minimum value is greater than or equal to the given maximum value
	 */
	public static int randomExclusiveInt(@NotNull Random rng, int min, int max) {
		Objects.requireNonNull(rng, "Random must not be null");
		if (min >= max) {
			throw new IllegalArgumentException("The minimum value must be less than the maximum value");
		}
		return min + rng.nextInt(max - min - 1) + 1;
	}
	
	/**
	 * Returns a random integer between min (inclusive) and max (inclusive).<br>
	 * The value will be greater than or equal to min and less than or equal to max (min &le; value &le; max).<br>
	 *
	 * @param rng The random number generator
	 * @param min The minimum value
	 * @param max The maximum value
	 * @return A random integer between min (inclusive) and max (inclusive)
	 * @throws NullPointerException If the given random number generator is null
	 * @throws IllegalArgumentException If the given minimum value is greater than or equal to the given maximum value
	 */
	public static int randomInclusiveInt(@NotNull Random rng, int min, int max) {
		Objects.requireNonNull(rng, "Random must not be null");
		if (min >= max) {
			throw new IllegalArgumentException("The minimum value must be less than the maximum value");
		}
		return min + rng.nextInt(max - min + 1);
	}
	//endregion
	
	/**
	 * Rounds the given value to the given number of digits after the decimal point.<br>
	 * Example:<br>
	 * <pre>{@code
	 * roundTo(1234.5678, 3) = 1234.568
	 * roundTo(1234.5678, 2) = 1234.57
	 * roundTo(1234.5678, 1) = 1234.6
	 * roundTo(1234.5678, 0) = 1235.0
	 * roundTo(1234.5678, -1) = 1230.0
	 * roundTo(1234.5678, -2) = 1200.0
	 * roundTo(1234.5678, -3) = 1000.0
	 * }</pre>
	 *
	 * @param value The value to round
	 * @param digits The number of digits after the decimal point
	 * @return The rounded value
	 */
	public static double roundTo(double value, int digits) {
		double factor = Math.pow(10, Math.abs(digits));
		if (0 > digits) {
			return Math.round(value / factor) * factor;
		} else {
			return Math.round(value * factor) / factor;
		}
	}
	
	/**
	 * Checks if the given value is in the given bounds.<br>
	 * The bounds are inclusive (min &le; value &le; max).<br>
	 *
	 * @param value The value to check
	 * @param min The minimum value
	 * @param max The maximum value
	 * @return True if the value is in the given bounds, otherwise false
	 */
	public static boolean isInBounds(double value, double min, double max) {
		return max >= value && value >= min;
	}
	
	/**
	 * Checks if the given values have the same value.<br>
	 * <p>
	 *     If the given array is null or empty, false will be returned.<br>
	 *     If the given array has only one element, true will be returned.<br>
	 *     If there are more than one element, the integer value of the first element will be compared<br>
	 *     to the integer value of the other elements.
	 * </p>
	 *
	 * @param numbers The numbers to compare
	 * @return True if all numbers have the same value, otherwise false
	 */
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
	
	/**
	 * Returns the fractional (decimal) part of the given value.<br>
	 *
	 * @param f The value
	 * @return The fractional part of the given value
	 */
	public static double frac(double f) {
		return f - ((int) f);
	}
	
	//region Clamp
	
	/**
	 * Clamps the given integer value between the given min and max value.<br>
	 * <p>
	 *     If the given value is less than the given min value, the min value will be returned.<br>
	 *     If the given value is greater than the given max value, the max value will be returned.<br>
	 *     (min &le; value &le; max)
	 * </p>
	 *
	 * @param value The value to clamp
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return The clamped value
	 */
	public static int clamp(int value, int min, int max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	
	/**
	 * Clamps the given long value between the given min and max value.<br>
	 * <p>
	 *     If the given value is less than the given min value, the min value will be returned.<br>
	 *     If the given value is greater than the given max value, the max value will be returned.<br>
	 *     (min &le; value &le; max)
	 * </p>
	 *
	 * @param value The value to clamp
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return The clamped value
	 */
	public static long clamp(long value, long min, long max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	
	/**
	 * Clamps the given double value between the given min and max value.<br>
	 * <p>
	 *     If the given value is less than the given min value, the min value will be returned.<br>
	 *     If the given value is greater than the given max value, the max value will be returned.<br>
	 *     (min &le; value &le; max)
	 * </p>
	 *
	 * @param value The value to clamp
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return The clamped value
	 */
	public static double clamp(double value, double min, double max) {
		if (min > value) {
			return min;
		} else {
			return Math.min(value, max);
		}
	}
	//endregion
	
	//region Min
	
	/**
	 * Returns the minimum of the given integer values.<br>
	 * If the given array is null or empty, {@link Integer#MAX_VALUE} will be returned.<br>
	 *
	 * @param values The values
	 * @return The minimum of the given values
	 * @throws IllegalStateException If the minimum value cannot be determined (should never happen)
	 */
	public static int min(int @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Integer.MAX_VALUE;
		}
		return IntStream.of(values).min().orElseThrow(() -> new IllegalStateException("Unable to determine minimum value"));
	}
	
	/**
	 * Returns the minimum of the given long values.<br>
	 * If the given array is null or empty, {@link Long#MAX_VALUE} will be returned.<br>
	 *
	 * @param values The values
	 * @return The minimum of the given values
	 * @throws IllegalStateException If the minimum value cannot be determined (should never happen)
	 */
	public static long min(long @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Long.MAX_VALUE;
		}
		return LongStream.of(values).min().orElseThrow(() -> new IllegalStateException("Unable to determine minimum value"));
	}
	
	/**
	 * Returns the minimum of the given double values.<br>
	 * If the given array is null or empty, {@link Double#MAX_VALUE} will be returned.<br>
	 *
	 * @param values The values
	 * @return The minimum of the given values
	 * @throws IllegalStateException If the minimum value cannot be determined (should never happen)
	 */
	public static double min(double @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.MAX_VALUE;
		}
		return DoubleStream.of(values).min().orElseThrow(() -> new IllegalStateException("Unable to determine minimum value"));
	}
	//endregion
	
	//region Max
	
	/**
	 * Returns the maximum of the given integer values.<br>
	 * If the given array is null or empty, {@link Integer#MIN_VALUE} will be returned.<br>
	 *
	 * @param values The values
	 * @return The maximum of the given values
	 * @throws IllegalStateException If the maximum value cannot be determined (should never happen)
	 */
	public static int max(int @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Integer.MIN_VALUE;
		}
		return IntStream.of(values).max().orElseThrow(() -> new IllegalStateException("Unable to determine maximum value"));
	}
	
	/**
	 * Returns the maximum of the given long values.<br>
	 * If the given array is null or empty, {@link Long#MIN_VALUE} will be returned.<br>
	 *
	 * @param values The values
	 * @return The maximum of the given values
	 * @throws IllegalStateException If the maximum value cannot be determined (should never happen)
	 */
	public static long max(long @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Long.MIN_VALUE;
		}
		return LongStream.of(values).max().orElseThrow(() -> new IllegalStateException("Unable to determine maximum value"));
	}
	
	/**
	 * Returns the maximum of the given double values.<br>
	 * If the given array is null or empty, {@link Double#MIN_VALUE} will be returned.<br>
	 *
	 * @param values The values
	 * @return The maximum of the given values
	 * @throws IllegalStateException If the maximum value cannot be determined (should never happen)
	 */
	public static double max(double @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.MIN_VALUE;
		}
		return DoubleStream.of(values).max().orElseThrow(() -> new IllegalStateException("Unable to determine maximum value"));
	}
	
	//endregion
	
	//region Average
	
	/**
	 * Returns the average of the given integer values.<br>
	 * If the given array is null or empty, {@link Double#NaN} will be returned.<br>
	 *
	 * @param values The values
	 * @return The average of the given values
	 * @throws IllegalStateException If the average value cannot be determined (should never happen)
	 */
	public static double average(int @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.NaN;
		}
		return IntStream.of(values).average().orElseThrow(() -> new IllegalStateException("Unable to determine average value"));
	}
	
	/**
	 * Returns the average of the given long values.<br>
	 * If the given array is null or empty, {@link Double#NaN} will be returned.<br>
	 *
	 * @param values The values
	 * @return The average of the given values
	 * @throws IllegalStateException If the average value cannot be determined (should never happen)
	 */
	public static double average(long @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.NaN;
		}
		return LongStream.of(values).average().orElseThrow(() -> new IllegalStateException("Unable to determine average value"));
	}
	
	/**
	 * Returns the average of the given double values.<br>
	 * If the given array is null or empty, {@link Double#NaN} will be returned.<br>
	 *
	 * @param values The values
	 * @return The average of the given values
	 * @throws IllegalStateException If the average value cannot be determined (should never happen)
	 */
	public static double average(double @Nullable ... values) {
		if (values == null || values.length == 0) {
			return Double.NaN;
		}
		return DoubleStream.of(values).average().orElseThrow(() -> new IllegalStateException("Unable to determine average value"));
	}
	//endregion
	
	/**
	 * Checks if the given value is a power of two.<br>
	 *
	 * @param value The value to check
	 * @return True if the given value is a power of two, otherwise false
	 */
	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}
	
	/**
	 * Parses the given hexadecimal string to a big decimal.<br>
	 *
	 * @param hex The hexadecimal string
	 * @return The parsed big decimal
	 * @throws NullPointerException If the given hexadecimal string is null
	 * @throws NumberFormatException If the given hexadecimal string is not a valid hexadecimal floating point constant
	 */
	public static @NotNull BigDecimal parseHexToBigDecimal(@NotNull String hex) {
		Objects.requireNonNull(hex, "Hexadecimal string must not be null");
		BigDecimal sign = BigDecimal.ONE;
		if (hex.startsWith("-")) {
			hex = hex.substring(1);
			sign = BigDecimal.valueOf(-1);
		} else if (hex.startsWith("+")) {
			hex = hex.substring(1);
		}
		if (!StringUtils.startsWithIgnoreCase(hex, "0x")) {
			throw new NumberFormatException("Not a hexadecimal floating point constant: " + hex);
		}
		hex = hex.substring(2);
		int p = hex.toLowerCase().indexOf("p");
		if (0 > p) {
			throw new NumberFormatException("Not a hexadecimal floating point constant: " + hex);
		}
		return getBigDecimal(hex, p).multiply(sign);
	}
	
	/**
	 * Parses the actual hexadecimal string to a big decimal.<br>
	 *
	 * @param hex The hexadecimal string
	 * @param p The index of the exponent
	 * @return The parsed big decimal
	 * @throws NullPointerException If the given hexadecimal string is null
	 */
	private static @NotNull BigDecimal getBigDecimal(@NotNull String hex, int p) {
		Objects.requireNonNull(hex, "Hexadecimal string must not be null");
		String mantissa = hex.substring(0, p);
		String exponent = hex.substring(p + 1);
		
		int hexadecimalPlaces = 0;
		if (mantissa.contains(".")) {
			int dot = mantissa.indexOf(".");
			hexadecimalPlaces = mantissa.length() - 1 - dot;
			mantissa = mantissa.substring(0, dot) + mantissa.substring(dot + 1);
		}
		
		int binaryExponent = Integer.parseInt(exponent) - (hexadecimalPlaces * 4);
		boolean positive = binaryExponent >= 0;
		if (0 > binaryExponent) {
			binaryExponent = -binaryExponent;
		}
		
		BigDecimal base = new BigDecimal(new BigInteger(mantissa, 16));
		BigDecimal factor = new BigDecimal(BigInteger.TWO.pow(binaryExponent));
		return positive ? base.multiply(factor) : base.divide(factor, RoundingMode.HALF_DOWN);
	}
}
