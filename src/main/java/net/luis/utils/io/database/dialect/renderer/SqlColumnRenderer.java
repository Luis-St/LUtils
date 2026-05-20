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
 *
 * @author Luis-St
 *
 */

public class SqlColumnRenderer {
	
	protected final SqlDialect dialect;
	
	public SqlColumnRenderer(@NonNull SqlDialect dialect) {
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlRendered renderAlterColumnType(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> newType) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(newType, "New sql type must not be null");
		
		String tableName = column.getOwningTable().getName();
		String columnName = column.getName();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).type().literal(this.dialect.getTypeName(newType)).toSql();
	}
	
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull SqlColumn<?, ?> column, boolean nullable) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableName = column.getOwningTable().getName();
		String columnName = column.getName();
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName));
		
		if (nullable) {
			return renderer.drop().not().null_().toSql();
		} else {
			return renderer.set().not().null_().toSql();
		}
	}
	
	public @NonNull SqlRendered renderAlterColumnSetDefault(@NonNull SqlColumn<?, ?> column, @NonNull String renderedDefault) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(renderedDefault, "Sql rendered default value must not be null");
		
		String tableName = column.getOwningTable().getName();
		String columnName = column.getName();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).set().default_().literal(renderedDefault).toSql();
	}
	
	public @NonNull SqlRendered renderAlterColumnDropDefault(@NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableName = column.getOwningTable().getName();
		String columnName = column.getName();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).drop().default_().toSql();
	}
}
