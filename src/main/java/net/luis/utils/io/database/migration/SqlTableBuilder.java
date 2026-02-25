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

import java.util.function.Consumer;

/**
 * Builder for table definitions in migrations.<br>
 * Provides a fluent API for defining columns and primary keys when creating a new table.<br>
 *
 * @author Luis-St
 */
public interface SqlTableBuilder {
	
	/**
	 * Adds a column with the specified name and type.<br>
	 *
	 * @param name The column name
	 * @param type The column type
	 * @return This builder for chaining
	 */
	@NonNull SqlTableBuilder column(@NonNull String name, @NonNull SqlColumnType type);
	
	/**
	 * Adds a column with the specified name, type, and additional options.<br>
	 *
	 * @param name The column name
	 * @param type The column type
	 * @param options A consumer to configure column options
	 * @return This builder for chaining
	 */
	@NonNull SqlTableBuilder column(@NonNull String name, @NonNull SqlColumnType type, @NonNull Consumer<SqlColumnBuilder> options);
	
	/**
	 * Sets the primary key for the table.<br>
	 *
	 * @param columns The column names forming the primary key
	 * @return This builder for chaining
	 */
	@NonNull SqlTableBuilder primaryKey(String @NonNull ... columns);
}
