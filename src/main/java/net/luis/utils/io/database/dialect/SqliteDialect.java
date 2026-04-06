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

import com.google.common.collect.Lists;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedFeatureException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SimpleSqlRendered;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

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
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.CLOB, Types.NCLOB -> "TEXT";
			case Types.LONGVARBINARY, Types.BLOB -> "BLOB";
			case Types.DATE -> "TEXT";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported scalar JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR -> "TEXT";
			case Types.BINARY, Types.VARBINARY -> "BLOB";
			case Types.NUMERIC, Types.DECIMAL -> "REAL";
			case Types.TIME, Types.TIMESTAMP, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP_WITH_TIMEZONE -> "TEXT";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	protected @NonNull String renderAutoIncrement(@NonNull SqlColumn<?, ?> column) {
		return " AUTOINCREMENT";
	}
	
	@Override
	protected @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<?, ?> column, boolean skipPrimaryKey) {
		if (column.isPrimaryKey() && column.isAutoIncrement() && column.getType().jdbcType() == Types.INTEGER) {
			StringBuilder sql = new StringBuilder();
			sql.append(this.quoteIdentifier(column.getName()));
			sql.append(" INTEGER PRIMARY KEY AUTOINCREMENT");
			
			if (!column.isNullable()) {
				sql.append(" NOT NULL");
			}
			return SimpleSqlRendered.of(sql.toString(), Lists.newArrayList());
		}
		return super.renderColumnForTable(column, skipPrimaryKey);
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) {
		Objects.requireNonNull(table, "Table must not be null");
		return SimpleSqlRendered.of("DELETE FROM " + this.quoteIdentifier(table.getName()));
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlDialectUnsupportedFeatureException {
		Objects.requireNonNull(columns, "Columns must not be null");
		if (columns.isEmpty()) {
			return SimpleSqlRendered.of("RETURNING *");
		}
		
		String cols = columns.stream().map(c -> this.quoteIdentifier(c.getName())).collect(Collectors.joining(", "));
		return SimpleSqlRendered.of("RETURNING " + cols);
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlDialectUnsupportedFeatureException {
		throw new SqlDialectUnsupportedFeatureException("Row-level locking is not supported by dialect " + this.name());
	}
}
