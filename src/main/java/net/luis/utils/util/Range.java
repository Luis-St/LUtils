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

package net.luis.utils.util;

import net.luis.utils.math.Mth;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a range of values between a minimum and maximum value (inclusive).<br>
 *
 * @author Luis-St
 */
public class Range {
	
	/**
	 * A constant for an empty range.<br>
	 * The minimum and maximum value are both 0.<br>
	 */
	public static final Range EMPTY = new Range(0, 0);
	
	/**
	 * The minimum value of the range.<br>
	 */
	private final double min;
	/**
	 * The maximum value of the range.<br>
	 */
	private final double max;
	
	/**
	 * Constructs a new range with the specified minimum and maximum value.<br>
	 *
	 * @param min The minimum value of the range
	 * @param max The maximum value of the range
	 * @throws IllegalArgumentException If the maximum value is less than the minimum value
	 */
	protected Range(double min, double max) { // protected to prevent instantiation from outside but allow inheritance
		this.min = min;
		this.max = max;
		if (this.min > this.max) {
			throw new IllegalArgumentException("The maximum value must be greater than the minimum value");
		}
	}
	
	/**
	 * Creates a new range with the specified maximum value.<br>
	 * The minimum value is set to 0.<br>
	 *
	 * @param max The maximum value of the range
	 * @return The created range or an empty range if the maximum value is less than 0
	 * @see #of(double, double)
	 */
	public static @NotNull Range of(double max) {
		return of(0.0, max);
	}
	
	/**
	 * Creates a new range with the specified minimum and maximum value.<br>
	 * The characters are converted to their respective double values.<br>
	 *
	 * @param min The minimum value of the range
	 * @param max The maximum value of the range
	 * @return The created range or an empty range if the maximum value is less than the minimum value
	 * @see #of(double, double)
	 */
	public static @NotNull Range of(char min, char max) {
		return of(min, (double) max);
	}
	
	/**
	 * Creates a new range with the specified minimum and maximum value.<br>
	 *
	 * @param min The minimum value of the range
	 * @param max The maximum value of the range
	 * @return The created range or an empty range if the maximum value is less than the minimum value
	 * @see #EMPTY
	 */
	public static @NotNull Range of(double min, double max) {
		if (min > max) {
			return EMPTY;
		}
		return new Range(min, max);
	}
	
	/**
	 * Parses the specified string to a range.<br>
	 * The string must be in the format of {@code [min..max]}.<br>
	 *
	 * @param str The string to parse
	 * @return The parsed range, or an empty range if the string is invalid
	 */
	public static @NotNull Range parse(@Nullable String str) {
		if (StringUtils.isBlank(str)) {
			return EMPTY;
		}
		if (!str.startsWith("[") || !str.endsWith("]") || !str.contains("..")) {
			return EMPTY;
		}
		String[] parts = str.substring(1, str.length() - 1).split("\\.\\.");
		if (parts.length != 2) {
			return EMPTY;
		}
		return of(Double.parseDouble(parts[0].strip()), Double.parseDouble(parts[1].strip()));
	}
	
	/**
	 * Returns the minimum value of the range.<br>
	 * @return The minimum
	 */
	public double getMin() {
		return this.min;
	}
	
	/**
	 * Returns the maximum value of the range.<br>
	 * @return The maximum
	 */
	public double getMax() {
		return this.max;
	}
	
	/**
	 * Returns the range between the minimum and maximum value.<br>
	 * The range is calculated by subtracting the minimum value from the maximum value.<br>
	 *
	 * @return The range
	 */
	public double getRange() {
		return this.max - this.min;
	}
	
	/**
	 * Moves the range by the specified value.<br>
	 * The minimum and maximum value are both increased by the specified value.<br>
	 *
	 * @param value The value to move the range by
	 * @return The moved range
	 */
	public @NotNull Range move(double value) {
		return of(this.min + value, this.max + value);
	}
	
	/**
	 * Expands the range by the specified value.<br>
	 * <p>
	 *     The minimum value is decreased by the absolute of the specified value<br>
	 *     and the maximum value is increased by the absolute of specified value.
	 * </p>
	 *
	 * @param value The value to expand the range by
	 * @return The expanded range
	 */
	public @NotNull Range expand(double value) {
		return of(this.min - Math.abs(value), this.max + Math.abs(value));
	}
	
	/**
	 * Expands the maximum value of the range by the absolute of specified value.<br>
	 *
	 * @param value The value to expand the maximum value by
	 * @return The expanded range
	 */
	public @NotNull Range expandMax(double value) {
		return of(this.min, this.max + Math.abs(value));
	}
	
	/**
	 * Expands the minimum value of the range by the absolute of specified value.<br>
	 * The resulting range is maybe empty if the given value is negative.<br>
	 *
	 * @param value The value to expand the minimum value by
	 * @return The expanded range
	 */
	public @NotNull Range expandMin(double value) {
		return of(this.min - Math.abs(value), this.max);
	}
	
	/**
	 * Checks if the specified value is within the range of this range.<br>
	 *
	 * @param value The value to check
	 * @return True if the value is within the range, otherwise false
	 * @see Mth#isInBounds(double, double, double)
	 */
	public boolean isInRange(double value) {
		return Mth.isInBounds(value, this.min, this.max);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Range range)) return false;
		
		if (Double.compare(range.min, this.min) != 0) return false;
		return Double.compare(range.max, this.max) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.min, this.max);
	}
	
	@Override
	public String toString() {
		return "[" + this.min + ".." + this.max + "]";
	}
	//endregion
}
