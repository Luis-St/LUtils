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

package net.luis.utils.io.database.operation;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface providing array-specific column operations.<br>
 *
 * @author Luis-St
 *
 * @param <T> The element type of the array
 */
public interface SqlArrayOps<T> {
	
	/**
	 * Creates a condition that checks if the array contains the given element.<br>
	 * Generates SQL: {@code element = ANY(column)}.<br>
	 *
	 * @param element The element to check for
	 * @return The contains condition
	 */
	@NonNull SqlCondition contains(@NonNull T element);

	/**
	 * Creates a condition that checks if the array overlaps with the given elements.<br>
	 * Generates SQL: {@code column && ARRAY[...]}.<br>
	 *
	 * @param elements The elements to check overlap with
	 * @return The overlaps condition
	 */
	@NonNull SqlCondition overlaps(@NonNull List<T> elements);

	/**
	 * Returns the length of the array column.<br>
	 * Generates SQL: {@code ARRAY_LENGTH(column)}.<br>
	 *
	 * @return The array length expression
	 */
	@NonNull SqlExpression<Integer> length();
}
