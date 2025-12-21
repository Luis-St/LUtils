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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a chance of something happening.<br>
 * The chance is a double between 0.0 and 1.0, where 0.0 is 0% and 1.0 is 100%.<br>
 *
 * @author Luis-St
 */
public class Chance {
	
	/**
	 * The pattern used to parse a chance.<br>
	 * <p>
	 *     The chance can be either 1.0, or 0. followed by at least one digit.<br>
	 *     The chance must be followed by a percent sign.<br>
	 *     A dot or a comma can be used as a decimal separator.
	 * </p>
	 */
	private static final Pattern CHANCE_PATTERN = Pattern.compile("^(1[.,]0|0[.,](\\d+))%$");
	/**
	 * The random number generator used to generate the chance.<br>
	 */
	private static final Random RNG = new Random();
	/**
	 * A constant value for a chance of 0.0.<br>
	 * This is the default chance.<br>
	 */
	public static final Chance ZERO = new Chance(0.0);
	/**
	 * A constant value for a chance of 1.0.<br>
	 */
	public static final Chance ONE = new Chance(1.0);
	/**
	 * The chance value of something happens.<br>
	 */
	private final double chance;
	
	/**
	 * Constructs a new chance with the given chance.<br>
	 * The chance will be clamped between 0.0 and 1.0.<br>
	 *
	 * @param chance The chance value as a double
	 */
	protected Chance(double chance) { // protected to prevent instantiation from outside but allow inheritance
		this.chance = Mth.clamp(chance, 0.0, 1.0);
	}
	
	/**
	 * Creates a new chance with the given chance.<br>
	 * <p>
	 *     If the chance is 0.0 or below, {@link #ZERO} will be returned.<br>
	 *     If the chance is 1.0 or above, {@link #ONE} will be returned.
	 * </p>
	 *
	 * @param chance The chance as a double
	 * @return The created chance
	 */
	public static @NonNull Chance of(double chance) {
		if (0.0 >= chance) {
			return ZERO;
		} else if (chance >= 1.0) {
			return ONE;
		}
		return new Chance(chance);
	}
	
	/**
	 * Parses a chance from a string.<br>
	 * <p>
	 *     The chance can be either 1.0, or 0. followed by at least one digit.<br>
	 *     The chance must be followed by a percent sign.<br>
	 *     A dot or a comma can be used as a decimal separator.
	 * </p>
	 * <p>
	 *     If the given string to parse is null or empty, {@link #ZERO} will be returned.<br>
	 *     If the given string does not match the pattern ({@link #CHANCE_PATTERN}), {@link #ZERO} will be returned.
	 * </p>
	 *
	 * @param chance The chance string to parse
	 * @return The parsed chance or {@link #ZERO}
	 */
	public static @NonNull Chance parse(@Nullable String chance) {
		if (StringUtils.isBlank(chance)) {
			return ZERO;
		}
		Matcher matcher = CHANCE_PATTERN.matcher(chance);
		if (!matcher.matches()) {
			return ZERO;
		}
		if (matcher.group(1) != null && matcher.group(2) != null) {
			return of(Double.parseDouble("0." + matcher.group(2)));
		}
		if (matcher.group(1) != null) {
			return ONE;
		}
		return ZERO;
	}
	
	/**
	 * Sets the seed of the random number generator.<br>
	 * @param seed The seed
	 */
	public static void setSeed(long seed) {
		RNG.setSeed(seed);
	}
	
	/**
	 * Checks if the chance is always true.<br>
	 * @return True if the chance is 1.0 or above, otherwise false
	 */
	public boolean isTrue() {
		return this.chance >= 1.0;
	}
	
	/**
	 * Checks if the chance is always false.<br>
	 * @return True if the chance is 0.0 or below, otherwise false
	 */
	public boolean isFalse() {
		return 0.0 >= this.chance;
	}
	
	/**
	 * Gets the result of the chance.<br>
	 * <p>
	 *     If {@link #isTrue()} this will always return true.<br>
	 *     If {@link #isFalse()} this will always return false.<br>
	 *     Otherwise, true if the random number generator returns a number below the chance.
	 * </p>
	 *
	 * @return The result of the chance
	 */
	public boolean result() {
		if (this.isTrue()) {
			return true;
		} else if (this.isFalse()) {
			return false;
		} else {
			return this.chance > RNG.nextDouble();
		}
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Chance that)) return false;
		
		return Double.compare(that.chance, this.chance) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.chance);
	}
	
	@Override
	public String toString() {
		return this.chance + "%";
	}
	//endregion
}
