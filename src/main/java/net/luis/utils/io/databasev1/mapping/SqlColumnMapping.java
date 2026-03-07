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

package net.luis.utils.io.databasev1.mapping;

import org.jspecify.annotations.NonNull;

/**
 * Static utility for resolving column names to record component names and vice versa.<br>
 * Provides conversion between SQL snake_case column names and Java camelCase record component names.<br>
 *
 * @author Luis-St
 */
public final class SqlColumnMapping {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private SqlColumnMapping() {}
	
	/**
	 * Converts a SQL snake_case column name to a Java camelCase component name.<br>
	 *
	 * @param columnName The snake_case column name
	 * @return The camelCase component name
	 */
	public static @NonNull String toComponentName(@NonNull String columnName) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a Java camelCase component name to a SQL snake_case column name.<br>
	 *
	 * @param componentName The camelCase component name
	 * @return The snake_case column name
	 */
	public static @NonNull String toColumnName(@NonNull String componentName) {
		throw new UnsupportedOperationException();
	}
}
