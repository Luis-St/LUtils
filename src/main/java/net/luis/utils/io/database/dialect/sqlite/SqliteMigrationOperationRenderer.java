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

package net.luis.utils.io.database.dialect.sqlite;

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.base.SqlMigrationOperationRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public class SqliteMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	public SqliteMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAddUniqueConstraint(@NonNull String tableName, @NonNull String constraintName, @NonNull List<String> columnNames) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAddForeignKey(
		@NonNull String tableName, @NonNull String constraintName, @NonNull List<String> columns, @NonNull String referencedTable, @NonNull List<String> referencedColumns, @NonNull SqlReferentialAction onDelete, @NonNull SqlReferentialAction onUpdate
	) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAddCheckConstraint(@NonNull String tableName, @NonNull String constraintName, @NonNull SqlCondition condition) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull String tableName, @NonNull String constraintName, @NonNull List<String> columnNames) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderDropConstraint(@NonNull String tableName, @NonNull String constraintName) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("DROP CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
}
