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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedFeatureException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqliteDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.RETURNING,
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.NULLS_ORDERING,
		SqlFeature.FOR_UPDATE
	);
	
	@Override
	public @NonNull String name() {
		return "SQLite";
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.BOOLEAN, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT -> "INTEGER";
			case Types.REAL, Types.FLOAT, Types.DOUBLE -> "REAL";
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.CLOB, Types.NCLOB, Types.DATE -> "TEXT";
			case Types.LONGVARBINARY, Types.BLOB -> "BLOB";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported scalar JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR, Types.TIME, Types.TIMESTAMP, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP_WITH_TIMEZONE -> "TEXT";
			case Types.BINARY, Types.VARBINARY -> "BLOB";
			case Types.NUMERIC, Types.DECIMAL -> "REAL";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("AUTOINCREMENT");
	}
	
	@Override
	protected @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<?, ?> column, boolean skipPrimaryKey) throws SqlException {
		if (column.isPrimaryKey() && column.isAutoIncrement() && column.getType().jdbcType() == Types.INTEGER) {
			
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.literal(this.quoteIdentifier(column.getName()));
			renderer.literal("INTEGER").primary().key().keyword("AUTOINCREMENT");
			if (!column.isNullable()) {
				renderer.not().null_();
			}
			return renderer.toSql();
		}
		return super.renderColumnForTable(column, skipPrimaryKey);
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.delete().from().literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(columns, "Columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.returning();
		if (columns.isEmpty()) {
			renderer.literal("*");
		} else {
			this.renderList(renderer, columns, (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		throw new SqlDialectUnsupportedFeatureException("Row-level locking is not supported by dialect " + this.name());
	}
}
