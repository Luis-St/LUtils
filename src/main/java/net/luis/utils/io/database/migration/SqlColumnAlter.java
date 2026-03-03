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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.dialect.SqlColumnType;
import org.jspecify.annotations.NonNull;

/**
 * Interface for altering column properties in migrations.<br>
 * Provides a fluent API for changing the type, nullability, and default value of an existing column.<br>
 *
 * @author Luis-St
 *
 * @param <V> The type of the column value
 */
public interface SqlColumnAlter<V> {
	
	/**
	 * Changes the column type.<br>
	 *
	 * @param type The new column type
	 * @return This alter instance for chaining
	 */
	@NonNull SqlColumnAlter<V> setType(@NonNull SqlColumnType type);
	
	/**
	 * Changes the nullability of the column.<br>
	 *
	 * @param nullable Whether the column should allow null values
	 * @return This alter instance for chaining
	 */
	@NonNull SqlColumnAlter<V> setNullable(boolean nullable);
	
	/**
	 * Sets a new default value for the column.<br>
	 *
	 * @param value The new default value
	 * @return This alter instance for chaining
	 */
	@NonNull SqlColumnAlter<V> setDefault(@NonNull V value);
	
	/**
	 * Drops the default value from the column.<br>
	 * @return This alter instance for chaining
	 */
	@NonNull SqlColumnAlter<V> dropDefault();
}
