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

import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL string functions.<br>
 *
 * @author Luis-St
 */
public class SqlString {
	
	/**
	 * Converts a string expression to lowercase.<br>
	 * Generates SQL: {@code LOWER(expression)}.<br>
	 *
	 * @param expr The expression to convert
	 * @return The lowercase expression
	 */
	public static @NonNull SqlExpression<String> lower(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a string expression to uppercase.<br>
	 * Generates SQL: {@code UPPER(expression)}.<br>
	 *
	 * @param expr The expression to convert
	 * @return The uppercase expression
	 */
	public static @NonNull SqlExpression<String> upper(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Removes leading and trailing whitespace from a string expression.<br>
	 * Generates SQL: {@code TRIM(expression)}.<br>
	 *
	 * @param expr The expression to trim
	 * @return The trimmed expression
	 */
	public static @NonNull SqlExpression<String> trim(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the length of a string expression.<br>
	 * Generates SQL: {@code LENGTH(expression)}.<br>
	 *
	 * @param expr The expression to measure
	 * @return The length expression
	 */
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts a substring from a string expression.<br>
	 * Generates SQL: {@code SUBSTRING(expression FROM start FOR length)}.<br>
	 *
	 * @param expr The expression to extract from
	 * @param start The start position
	 * @param length The number of characters to extract
	 * @return The substring expression
	 */
	public static @NonNull SqlExpression<String> substring(@NonNull SqlExpression<String> expr, int start, int length) {
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
	 * Replaces occurrences of a search string with a replacement string in an expression.<br>
	 * Generates SQL: {@code REPLACE(expression, search, replacement)}.<br>
	 *
	 * @param expr The expression to search in
	 * @param search The string to search for
	 * @param replacement The replacement string
	 * @return The replace expression
	 */
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<String> expr, @NonNull String search, @NonNull String replacement) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Removes leading whitespace from a string expression.<br>
	 * Generates SQL: {@code LTRIM(expression)}.<br>
	 *
	 * @param expr The expression to trim
	 * @return The left-trimmed expression
	 */
	public static @NonNull SqlExpression<String> leftTrim(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Removes trailing whitespace from a string expression.<br>
	 * Generates SQL: {@code RTRIM(expression)}.<br>
	 *
	 * @param expr The expression to trim
	 * @return The right-trimmed expression
	 */
	public static @NonNull SqlExpression<String> rightTrim(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Removes the specified characters from both sides of a string expression.<br>
	 * Generates SQL: {@code TRIM(BOTH characters FROM expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to trim
	 * @param characters The characters to remove
	 * @return The trimmed expression
	 */
	public static @NonNull SqlExpression<String> trimChars(@NonNull SqlExpression<String> expr, @NonNull String characters) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the character length of a string expression.<br>
	 * Generates SQL: {@code CHAR_LENGTH(expression)}.<br>
	 *
	 * @param expr The expression to measure
	 * @return The character length expression
	 */
	public static @NonNull SqlExpression<Integer> charLength(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the position of a substring within a string expression.<br>
	 * Generates SQL: {@code POSITION(substring IN expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to search in
	 * @param substring The substring to find
	 * @return The position expression (1-based)
	 */
	public static @NonNull SqlExpression<Integer> position(@NonNull SqlExpression<String> expr, @NonNull String substring) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the leftmost n characters of a string expression.<br>
	 * Generates SQL: {@code LEFT(expression, n)}.<br>
	 *
	 * @param expr The expression to extract from
	 * @param n The number of characters to extract
	 * @return The left substring expression
	 */
	public static @NonNull SqlExpression<String> left(@NonNull SqlExpression<String> expr, int n) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the rightmost n characters of a string expression.<br>
	 * Generates SQL: {@code RIGHT(expression, n)}.<br>
	 *
	 * @param expr The expression to extract from
	 * @param n The number of characters to extract
	 * @return The right substring expression
	 */
	public static @NonNull SqlExpression<String> right(@NonNull SqlExpression<String> expr, int n) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Left-pads a string expression to the specified length with the given fill string.<br>
	 * Generates SQL: {@code LPAD(expression, length, fill)}.<br>
	 *
	 * @param expr The expression to pad
	 * @param length The target length
	 * @param fill The fill string
	 * @return The left-padded expression
	 */
	public static @NonNull SqlExpression<String> leftPad(@NonNull SqlExpression<String> expr, int length, @NonNull String fill) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Right-pads a string expression to the specified length with the given fill string.<br>
	 * Generates SQL: {@code RPAD(expression, length, fill)}.<br>
	 *
	 * @param expr The expression to pad
	 * @param length The target length
	 * @param fill The fill string
	 * @return The right-padded expression
	 */
	public static @NonNull SqlExpression<String> rightPad(@NonNull SqlExpression<String> expr, int length, @NonNull String fill) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the ASCII code of the first character of a string expression.<br>
	 * Generates SQL: {@code ASCII(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The ASCII code expression
	 */
	public static @NonNull SqlExpression<Integer> ascii(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the character for the given code point.<br>
	 * Generates SQL: {@code CHR(codePoint)} or {@code CHAR(codePoint)} depending on the dialect.<br>
	 *
	 * @param codePoint The code point to convert
	 * @return The character expression
	 */
	public static @NonNull SqlExpression<String> chr(int codePoint) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the concatenated characters for the given code points.<br>
	 * Generates SQL: {@code CHAR(cp1, cp2, ...)} depending on the dialect.<br>
	 *
	 * @param codePoints The code points to convert
	 * @return The character expression
	 */
	public static @NonNull SqlExpression<String> charFunc(int @NonNull ... codePoints) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a string expression to its hexadecimal representation.<br>
	 * Generates SQL: {@code HEX(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to convert
	 * @return The hexadecimal expression
	 */
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a hexadecimal string expression back to a regular string.<br>
	 * Generates SQL: {@code UNHEX(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The hexadecimal expression to convert
	 * @return The decoded string expression
	 */
	public static @NonNull SqlExpression<String> unhex(@NonNull SqlExpression<String> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Concatenates values from a group into a single string separated by the given separator.<br>
	 * Generates SQL: {@code GROUP_CONCAT(expression SEPARATOR separator)} or {@code STRING_AGG(expression, separator)} depending on the dialect.<br>
	 *
	 * @param expr The expression to aggregate
	 * @param separator The separator between values
	 * @return The aggregated string expression
	 */
	public static @NonNull SqlExpression<String> groupConcat(@NonNull SqlExpression<String> expr, @NonNull String separator) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Concatenates only distinct values from grouped rows into a single string with the specified separator.<br>
	 * Generates SQL: {@code STRING_AGG(DISTINCT expression, 'separator')} or {@code GROUP_CONCAT(DISTINCT expression SEPARATOR 'separator')} depending on the dialect.<br>
	 *
	 * @param expr The expression to aggregate
	 * @param separator The separator between values
	 * @return The aggregated string expression
	 */
	public static @NonNull SqlExpression<String> groupConcatDistinct(@NonNull SqlExpression<String> expr, @NonNull String separator) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Concatenates values from grouped rows with explicit ordering into a single string with the specified separator.<br>
	 * Generates SQL: {@code STRING_AGG(expression, 'separator' ORDER BY orderBy)} or {@code GROUP_CONCAT(expression ORDER BY orderBy SEPARATOR 'separator')} depending on the dialect.<br>
	 *
	 * @param expr The expression to aggregate
	 * @param separator The separator between values
	 * @param orderBy The ordering to apply to the aggregated elements
	 * @return The aggregated string expression
	 */
	public static @NonNull SqlExpression<String> groupConcatOrdered(@NonNull SqlExpression<String> expr, @NonNull String separator, @NonNull SqlOrderable orderBy) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Concatenates expressions with a separator, skipping null values.<br>
	 * Generates SQL: {@code CONCAT_WS(separator, val1, val2, ...)}.<br>
	 *
	 * @param separator The separator between values
	 * @param values The expressions to concatenate
	 * @return The concatenated expression
	 */
	public static @NonNull SqlExpression<String> concatWs(@NonNull String separator, SqlExpression<?> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
}
