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

package net.luis.utils.io.database.table.ops;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing string-specific SQL operations for column conditions.<br>
 * These operations generate SQL conditions based on string patterns, prefix/suffix matching, substring containment, case-insensitive equality, and string length comparisons.<br>
 *
 * @author Luis-St
 */
public interface SqlStringOps {
	
	/**
	 * Creates a condition that checks if the column starts with the given prefix.<br>
	 * Generates SQL: {@code column LIKE 'prefix%'}.<br>
	 *
	 * @param prefix The prefix to check for
	 * @return The starts-with condition
	 */
	@NonNull SqlCondition startsWith(@NonNull String prefix);
	
	/**
	 * Creates a condition that checks if the column contains the given substring.<br>
	 * Generates SQL: {@code column LIKE '%substring%'}.<br>
	 *
	 * @param substring The substring to check for
	 * @return The contains condition
	 */
	@NonNull SqlCondition contains(@NonNull String substring);
	
	/**
	 * Creates a condition that checks if the column ends with the given suffix.<br>
	 * Generates SQL: {@code column LIKE '%suffix'}.<br>
	 *
	 * @param suffix The suffix to check for
	 * @return The ends-with condition
	 */
	@NonNull SqlCondition endsWith(@NonNull String suffix);
	
	/**
	 * Creates a condition using a SQL {@code LIKE} pattern.<br>
	 * Generates SQL: {@code column LIKE pattern}.<br>
	 *
	 * @param pattern The like pattern
	 * @return The like condition
	 */
	@NonNull SqlCondition like(@NonNull String pattern);
	
	/**
	 * Creates a condition that checks if the column is equal to the given value ignoring case.<br>
	 * Generates SQL: {@code LOWER(column) = LOWER(value)}.<br>
	 *
	 * @param value The value to compare to
	 * @return The case-insensitive equality condition
	 */
	@NonNull SqlCondition equalToIgnoreCase(@NonNull String value);
	
	/**
	 * Creates a condition that checks if the column length is greater than the given value.<br>
	 * Generates SQL: {@code LENGTH(column) > length}.<br>
	 *
	 * @param length The length to compare against
	 * @return The length condition
	 */
	@NonNull SqlCondition lengthGreaterThan(int length);
	
	/**
	 * Creates a condition that checks if the column length is less than the given value.<br>
	 * Generates SQL: {@code LENGTH(column) < length}.<br>
	 *
	 * @param length The length to compare against
	 * @return The length condition
	 */
	@NonNull SqlCondition lengthLessThan(int length);
	
	/**
	 * Creates a condition that checks if the column length is equal to the given value.<br>
	 * Generates SQL: {@code LENGTH(column) = length}.<br>
	 *
	 * @param length The length to compare against
	 * @return The length condition
	 */
	@NonNull SqlCondition lengthEquals(int length);
}
