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

import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Describes a single column as read from the live database schema.<br>
 *
 * @author Luis-St
 *
 * @param tableName The name of the table the column belongs to
 * @param columnName The name of the column
 * @param jdbcType The JDBC type code of the column
 * @param parameter The resolved parameter describing the column type or {@code null} if it could not be resolved
 * @param nullable Whether the column accepts {@code null} values
 * @param autoIncrement Whether the column is auto-incremented
 * @param primaryKey Whether the column is part of the primary key
 * @param unique Whether the column has a unique constraint
 * @param ordinalPosition The ordinal position of the column within the table
 */
public record SqlSchemaColumnInfo(
	@NonNull String tableName,
	@NonNull String columnName,
	int jdbcType,
	@Nullable SqlParameter parameter,
	boolean nullable,
	boolean autoIncrement,
	boolean primaryKey,
	boolean unique,
	int ordinalPosition
) {
	
	/**
	 * Constructs a new schema column info with the given values.<br>
	 * @throws NullPointerException If the table name or column name is null
	 */
	public SqlSchemaColumnInfo {
		Objects.requireNonNull(tableName, "Table name must not be null");
		Objects.requireNonNull(columnName, "Column name must not be null");
	}
}
