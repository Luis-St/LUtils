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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Renderer that turns column alteration operations into dialect-specific SQL.<br>
 * Each method produces a {@link SqlRendered} {@code ALTER TABLE ... ALTER COLUMN} statement for the configured {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlColumnRenderer {
	
	/**
	 * The dialect used to render the column alterations.
	 */
	protected final SqlDialect dialect;
	
	/**
	 * Constructs a new column renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect used to render the column alterations
	 * @throws NullPointerException If the dialect is null
	 */
	public SqlColumnRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Renders an {@code ALTER COLUMN} statement that changes the type of the given column to the given new type.<br>
	 *
	 * @param column The column whose type should be altered
	 * @param newType The new type to set for the column
	 * @return The rendered statement
	 * @throws NullPointerException If the column or the new type is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAlterColumnType(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> newType) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(newType, "New sql type must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).type().literal(this.dialect.getTypeName(newType)).toSql();
	}
	
	/**
	 * Renders an {@code ALTER COLUMN} statement that changes the nullability of the given column.<br>
	 * If {@code nullable} is {@code true} the {@code NOT NULL} constraint is dropped, otherwise it is set.<br>
	 *
	 * @param column The column whose nullability should be altered
	 * @param nullable Whether the column should allow {@code null} values
	 * @return The rendered statement
	 * @throws NullPointerException If the column is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull SqlColumn<?, ?> column, boolean nullable) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName));
		
		if (nullable) {
			return renderer.drop().not().null_().toSql();
		} else {
			return renderer.set().not().null_().toSql();
		}
	}
	
	/**
	 * Renders an {@code ALTER COLUMN} statement that sets the default value of the given column.<br>
	 *
	 * @param column The column whose default value should be set
	 * @param renderedDefault The already rendered default value literal to set
	 * @return The rendered statement
	 * @throws NullPointerException If the column or the rendered default value is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAlterColumnSetDefault(@NonNull SqlColumn<?, ?> column, @NonNull String renderedDefault) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(renderedDefault, "Sql rendered default value must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).set().default_().literal(renderedDefault).toSql();
	}
	
	/**
	 * Renders an {@code ALTER COLUMN} statement that drops the default value of the given column.<br>
	 *
	 * @param column The column whose default value should be dropped
	 * @return The rendered statement
	 * @throws NullPointerException If the column is null
	 * @throws SqlException If rendering fails
	 */
	public @NonNull SqlRendered renderAlterColumnDropDefault(@NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).drop().default_().toSql();
	}
}
