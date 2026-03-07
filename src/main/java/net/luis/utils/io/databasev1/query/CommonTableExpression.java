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

package net.luis.utils.io.databasev1.query;

import org.jspecify.annotations.NonNull;

/**
 * Represents a Common Table Expression (CTE) for use in SQL queries.<br>
 *
 * @author Luis-St
 */
public class CommonTableExpression {
	
	/**
	 * Creates a new CTE with the given name.<br>
	 * Generates SQL: {@code WITH name AS (...)}.<br>
	 *
	 * @param name The name of the CTE
	 * @return The CTE
	 */
	public static @NonNull CommonTableExpression of(@NonNull String name) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a new CTE with the given name and query.<br>
	 * Generates SQL: {@code WITH name AS (...)}.<br>
	 *
	 * @param name The name of the CTE
	 * @param query The select query for the CTE
	 * @return The CTE
	 */
	public static @NonNull CommonTableExpression of(@NonNull String name, @NonNull SqlSelectQuery<?> query) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a new recursive CTE with the given name.<br>
	 * Generates SQL: {@code WITH RECURSIVE name AS (...)}.<br>
	 *
	 * @param name The name of the CTE
	 * @return The recursive CTE
	 */
	public static @NonNull CommonTableExpression recursive(@NonNull String name) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a new recursive CTE with the given name and query.<br>
	 * Generates SQL: {@code WITH RECURSIVE name AS (...)}.<br>
	 *
	 * @param name The name of the CTE
	 * @param query The select query for the CTE
	 * @return The recursive CTE
	 */
	public static @NonNull CommonTableExpression recursive(@NonNull String name, @NonNull SqlSelectQuery<?> query) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the name of this CTE.<br>
	 * @return The CTE name
	 */
	public @NonNull String name() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns whether this CTE is recursive.<br>
	 * @return Whether the CTE is recursive
	 */
	public boolean isRecursive() {
		throw new UnsupportedOperationException();
	}
}
