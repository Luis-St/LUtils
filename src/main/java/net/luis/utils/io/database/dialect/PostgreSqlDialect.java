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

import net.luis.utils.io.database.dialect.rendering.base.SqlFunctionRenderer;
import net.luis.utils.io.database.dialect.rendering.base.SqlRenderingHelper;
import net.luis.utils.io.database.dialect.rendering.postgres.PostgreSqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlArrayType;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class PostgreSqlDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.RETURNING,
		SqlFeature.LATERAL_JOIN,
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.NULLS_ORDERING,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.FOR_SHARE,
		SqlFeature.SKIP_LOCKED,
		SqlFeature.NO_WAIT,
		SqlFeature.TRUNCATE_CASCADE
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH,
		SqlIndexMethod.GIN,
		SqlIndexMethod.GIST,
		SqlIndexMethod.BRIN,
		SqlIndexMethod.SPGIST
	);
	
	@Override
	public @NonNull String name() {
		return "PostgreSQL";
	}
	
	@Override
	protected @NonNull SqlFunctionRenderer createFunctionRenderer() {
		return SqlFunctionRenderer.builder(this)
			.temporal(new PostgreSqlTemporalFunctionRenderer(this))
			.build();
	}
	
	@Override
	public boolean isTypeSupported(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return true;
	}
	
	@Override
	public @NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		
		if (type.baseType() instanceof SqlArrayType<?> arrayType) {
			return this.getTypeName(arrayType.elementType()) + "[]";
		}
		return super.getTypeName(type);
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.LONGVARBINARY, Types.BLOB -> "BYTEA";
			case Types.LONGNVARCHAR, Types.NCLOB, Types.CLOB -> "TEXT";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getLengthParameterizedTypeName(int jdbcType, @NonNull SqlLengthParameter length) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.NCHAR -> "CHAR(" + length.length() + ")";
			case Types.NVARCHAR -> "VARCHAR(" + length.length() + ")";
			default -> super.getLengthParameterizedTypeName(jdbcType, length);
		};
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("BY").default_().as().keyword("IDENTITY");
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Sql index method must not be null");
		return SUPPORTED_INDEX_METHODS.contains(method);
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.quoteIdentifier(table.getName())).cascade();
		return renderer.toSql();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(columns, "Sql columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.returning();
		if (columns.isEmpty()) {
			renderer.literal("*");
		} else {
			SqlRenderingHelper.renderList(renderer, columns, (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderLateralJoin() {
		return SqlRendered.of("LATERAL");
	}
}
