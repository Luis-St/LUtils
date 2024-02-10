/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a range of values between a minimum and maximum value (inclusive).<br>
 *
 * @author Luis-St
 */
public class Range {
	
	/**
	 * The minimum value of the range.<br>
	 */
	private final double min;
	/**
	 * The maximum value of the range.<br>
	 */
	private final double max;
	
	/**
	 * Constructs a new {@link Range} with the specified minimum and maximum value.<br>
	 * @param min The minimum value of the range
	 * @param max The maximum value of the range
	 * @throws IllegalArgumentException If the maximum value is less than the minimum value
	 */
	private Range(double min, double max) {
		this.min = min;
		this.max = max;
		if (this.min > this.max) {
			throw new IllegalArgumentException("The maximum value must be greater than the minimum value");
		}
	}
	
	/**
	 * Creates a new {@link Range} with the specified maximum value.<br>
	 * The minimum value is set to 0.<br>
	 * @param max The maximum value of the range
	 * @return The created range
	 * @throws IllegalArgumentException If the maximum value is less than 0
	 */
	public static @NotNull Range of(double max) {
		return of(0, max);
	}
	
	/**
	 * Creates a new {@link Range} with the specified minimum and maximum value.<br>
	 * The characters are converted to their respective doubleeger values.<br>
	 * @param min The minimum value of the range
	 * @param max The maximum value of the range
	 * @return The created range
	 * @throws IllegalArgumentException If the maximum value is less than the minimum value
	 */
	public static @NotNull Range of(char min, char max) {
		return of(min, (double) max);
	}
	
	/**
	 * Creates a new {@link Range} with the specified minimum and maximum value.<br>
	 * @param min The minimum value of the range
	 * @param max The maximum value of the range
	 * @return The created range
	 * @throws IllegalArgumentException If the maximum value is less than the minimum value
	 */
	public static @NotNull Range of(double min, double max) {
		return new Range(min, max);
	}
	
	/**
	 * @return The minimum value of the range
	 */
	public Number getMin() {
		return this.min;
	}
	
	/**
	 * @return The maximum value of the range
	 */
	public double getMax() {
		return this.max;
	}
	
	/**
	 * @return The range between the minimum and maximum value
	 */
	public double getRange() {
		return this.max - this.min;
	}
	
	/**
	 * Checks if the specified value is within the range of this range.<br>
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
		return "[" + this.min + "; " + this.max + "]";
	}
	//endregion
}
