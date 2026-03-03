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

package net.luis.utils.io.database.function.scalar;

import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL sequence functions.<br>
 *
 * @author Luis-St
 */
public class SqlSequence {
	
	/**
	 * Advances the sequence and returns the new value.<br>
	 * Generates SQL: {@code NEXTVAL('sequenceName')}.<br>
	 *
	 * @param sequenceName The name of the sequence
	 * @return The next value expression
	 */
	public static @NonNull SqlExpression<Long> nextValue(@NonNull String sequenceName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the most recently obtained value from the sequence in the current session.<br>
	 * Generates SQL: {@code CURRVAL('sequenceName')}.<br>
	 *
	 * @param sequenceName The name of the sequence
	 * @return The current value expression
	 */
	public static @NonNull SqlExpression<Long> currentValue(@NonNull String sequenceName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the sequence's current value and returns that value.<br>
	 * Generates SQL: {@code SETVAL('sequenceName', value)}.<br>
	 *
	 * @param sequenceName The name of the sequence
	 * @param value The value to set
	 * @return The set value expression
	 */
	public static @NonNull SqlExpression<Long> setValue(@NonNull String sequenceName, long value) {
		throw new UnsupportedOperationException();
	}
}
