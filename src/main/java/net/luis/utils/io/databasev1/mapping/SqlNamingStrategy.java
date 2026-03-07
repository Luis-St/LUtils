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
 * Interface for pluggable naming strategies that convert between Java record component names and SQL column names.<br>
 *
 * @author Luis-St
 */
public interface SqlNamingStrategy {
	
	/**
	 * Converts a SQL column name to a Java record component name.<br>
	 *
	 * @param columnName The SQL column name
	 * @return The Java record component name
	 */
	@NonNull String toComponentName(@NonNull String columnName);
	
	/**
	 * Converts a Java record component name to a SQL column name.<br>
	 *
	 * @param componentName The Java record component name
	 * @return The SQL column name
	 */
	@NonNull String toColumnName(@NonNull String componentName);
}
