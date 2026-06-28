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

package net.luis.utils.io.database.type.parameter;

import org.jspecify.annotations.NonNull;

/**
 * Represents a parameter that configures a parameterized sql type.<br>
 * Parameters carry the additional arguments of a parameterized type such as the length of a {@code VARCHAR(n)},
 * the precision and scale of a {@code DECIMAL(p, s)} or the fractional second digits of a {@code TIMESTAMP(n)}.<br>
 *
 * @see SqlLengthParameter
 * @see SqlPrecisionParameter
 * @see SqlFractionalParameter
 *
 * @author Luis-St
 */
public interface SqlParameter {
	
	/**
	 * Creates a length parameter with the given length.<br>
	 *
	 * @param length The length of the type, for example the {@code n} in a {@code VARCHAR(n)}
	 * @return A new length parameter
	 * @throws IllegalArgumentException If the length is not positive
	 */
	static @NonNull SqlLengthParameter length(int length) {
		return new SqlLengthParameter(length);
	}
	
	/**
	 * Creates a precision parameter with the given precision and scale.<br>
	 *
	 * @param precision The total number of significant digits, for example the {@code p} in a {@code DECIMAL(p, s)}
	 * @param scale The number of digits after the decimal point, for example the {@code s} in a {@code DECIMAL(p, s)}
	 * @return A new precision parameter
	 * @throws IllegalArgumentException If the precision is not positive, the scale is negative or the scale exceeds the precision
	 */
	static @NonNull SqlPrecisionParameter precision(int precision, int scale) {
		return new SqlPrecisionParameter(precision, scale);
	}
	
	/**
	 * Creates a fractional parameter with the given number of fractional digits.<br>
	 *
	 * @param digits The number of fractional second digits, for example the {@code n} in a {@code TIMESTAMP(n)}
	 * @return A new fractional parameter
	 * @throws IllegalArgumentException If the digits are not between 0 and 9 (both inclusive)
	 */
	static @NonNull SqlFractionalParameter fractional(int digits) {
		return new SqlFractionalParameter(digits);
	}
}
