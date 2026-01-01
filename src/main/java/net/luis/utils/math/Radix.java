/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a radix for number conversion.<br>
 * The radix is the base of a number system.<br>
 * This class represents the most common radices used in computer science:<br>
 * <ul>
 *     <li>{@link #BINARY Binary} (base 2)</li>
 *     <li>{@link #OCTAL Octal} (base 8)</li>
 *     <li>{@link #DECIMAL Decimal} (base 10)</li>
 *     <li>{@link #HEXADECIMAL Hexadecimal} (base 16)</li>
 * </ul>
 *
 * @author Luis-St
 */
public enum Radix {
	
	/**
	 * Represents the binary radix (base 2) with the prefix {@code 0b}.<br>
	 */
	BINARY(2, "0b"),
	/**
	 * Represents the octal radix (base 8) with the prefix {@code 0}.<br>
	 */
	OCTAL(8, "0"),
	/**
	 * Represents the decimal radix (base 10) with no prefix.<br>
	 */
	DECIMAL(10, ""),
	/**
	 * Represents the hexadecimal radix (base 16) with the prefix {@code 0x}.<br>
	 */
	HEXADECIMAL(16, "0x");
	
	/**
	 * The radix value.<br>
	 */
	private final int radix;
	/**
	 * The prefix for the radix used in string conversion.<br>
	 */
	private final String prefix;
	
	/**
	 * Constructs a new radix with the given radix value and prefix.<br>
	 *
	 * @param radix The radix value
	 * @param prefix The prefix for the radix
	 * @throws IllegalArgumentException If the radix is less than or equal to 0
	 * @throws NullPointerException If the prefix is null
	 */
	Radix(int radix, @NonNull String prefix) {
		if (0 >= radix) {
			throw new IllegalArgumentException("Radix must be greater than 0 but found: " + radix);
		}
		this.radix = radix;
		this.prefix = Objects.requireNonNull(prefix, "Prefix must not be null");
	}
	
	/**
	 * Returns the radix value.<br>
	 * @return The radix value
	 */
	public int getRadix() {
		return this.radix;
	}
	
	/**
	 * Returns the prefix for the radix used in string conversion.<br>
	 * @return The prefix for the radix
	 */
	public @NonNull String getPrefix() {
		return this.prefix;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	//endregion
}
