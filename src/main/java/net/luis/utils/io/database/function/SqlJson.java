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

import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL JSON functions.<br>
 *
 * @author Luis-St
 */
public class SqlJson {
	
	/**
	 * Sets the value at the specified JSON path.<br>
	 * Generates SQL: {@code JSON_SET(expression, path, value)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON expression to modify
	 * @param path The JSON path
	 * @param value The value to set
	 * @return The modified JSON expression
	 */
	public static @NonNull SqlExpression<String> set(@NonNull SqlExpression<?> expr, @NonNull String path, @NonNull Object value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Inserts a value at the specified JSON path if it does not already exist.<br>
	 * Generates SQL: {@code JSON_INSERT(expression, path, value)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON expression to modify
	 * @param path The JSON path
	 * @param value The value to insert
	 * @return The modified JSON expression
	 */
	public static @NonNull SqlExpression<String> insert(@NonNull SqlExpression<?> expr, @NonNull String path, @NonNull Object value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Replaces the value at the specified JSON path if it already exists.<br>
	 * Generates SQL: {@code JSON_REPLACE(expression, path, value)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON expression to modify
	 * @param path The JSON path
	 * @param value The replacement value
	 * @return The modified JSON expression
	 */
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<?> expr, @NonNull String path, @NonNull Object value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Removes the value at the specified JSON path.<br>
	 * Generates SQL: {@code JSON_REMOVE(expression, path)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON expression to modify
	 * @param path The JSON path to remove
	 * @return The modified JSON expression
	 */
	public static @NonNull SqlExpression<String> remove(@NonNull SqlExpression<?> expr, @NonNull String path) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Merges the given JSON string into the expression using RFC 7396 merge patch semantics.<br>
	 * Generates SQL: {@code JSON_MERGE_PATCH(expression, json)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON expression to merge into
	 * @param json The JSON string to merge
	 * @return The merged JSON expression
	 */
	public static @NonNull SqlExpression<String> mergePatch(@NonNull SqlExpression<?> expr, @NonNull String json) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the JSON type of the value at the top level or specified path.<br>
	 * Generates SQL: {@code JSON_TYPE(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON expression to inspect
	 * @return The JSON type expression
	 */
	public static @NonNull SqlExpression<String> typeOf(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the length of a JSON array expression.<br>
	 * Generates SQL: {@code JSON_ARRAY_LENGTH(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON array expression
	 * @return The array length expression
	 */
	public static @NonNull SqlExpression<Integer> arrayLength(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the keys of a JSON object expression.<br>
	 * Generates SQL: {@code JSON_OBJECT_KEYS(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The JSON object expression
	 * @return The keys expression
	 */
	public static @NonNull SqlExpression<String> keys(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
}
