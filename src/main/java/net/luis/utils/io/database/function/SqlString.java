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

package net.luis.utils.io.database.function;

import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL string functions.<br>
 *
 * @author Luis-St
 */
public class SqlString {
	
	/**
	 * Converts a string column to lowercase.<br>
	 * Generates SQL: {@code LOWER(column)}.<br>
	 *
	 * @param column The column to convert
	 * @return The lowercase expression
	 */
	public static @NonNull SqlExpression<String> lower(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a string column to uppercase.<br>
	 * Generates SQL: {@code UPPER(column)}.<br>
	 *
	 * @param column The column to convert
	 * @return The uppercase expression
	 */
	public static @NonNull SqlExpression<String> upper(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Removes leading and trailing whitespace from a string column.<br>
	 * Generates SQL: {@code TRIM(column)}.<br>
	 *
	 * @param column The column to trim
	 * @return The trimmed expression
	 */
	public static @NonNull SqlExpression<String> trim(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the length of a string column.<br>
	 * Generates SQL: {@code LENGTH(column)}.<br>
	 *
	 * @param column The column to measure
	 * @return The length expression
	 */
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlColumn<String> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts a substring from a string column.<br>
	 * Generates SQL: {@code SUBSTRING(column FROM start FOR length)}.<br>
	 *
	 * @param column The column to extract from
	 * @param start The start position
	 * @param length The number of characters to extract
	 * @return The substring expression
	 */
	public static @NonNull SqlExpression<String> substring(@NonNull SqlColumn<String> column, int start, int length) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Concatenates the given expressions into a single string.<br>
	 * Generates SQL: {@code CONCAT(val1, val2, ...)}.<br>
	 *
	 * @param values The expressions to concatenate
	 * @return The concatenated expression
	 */
	public static @NonNull SqlExpression<String> concat(SqlExpression<?> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Replaces occurrences of a search string with a replacement string in a column.<br>
	 * Generates SQL: {@code REPLACE(column, search, replacement)}.<br>
	 *
	 * @param column The column to search in
	 * @param search The string to search for
	 * @param replacement The replacement string
	 * @return The replace expression
	 */
	public static @NonNull SqlExpression<String> replace(@NonNull SqlColumn<String> column, @NonNull String search, @NonNull String replacement) {
		throw new UnsupportedOperationException();
	}
}
