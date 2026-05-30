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

import net.luis.utils.io.database.dialect.renderer.SqlDialectRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlTypeRegistry;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Luis-St
 *
 */

public class H2Dialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.NULLS_ORDERING,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.IS_DISTINCT_FROM,
		SqlFeature.UPSERT_SUFFIX,
		SqlFeature.TRANSACTIONAL_DDL,
		SqlFeature.ROW_LOCKING,
		SqlFeature.INSERT_OR_IGNORE,
		SqlFeature.RENAME_INDEX,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.DROP_CONSTRAINT
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH
	);
	private static final SqlTypeRegistry TYPE_REGISTRY = SqlTypeRegistry.builder()
		.register(SqlTypes.UUID, "UUID")
		.register(SqlTypes.JSON, "JSON")
		.build();
	
	@Override
	public @NonNull String name() {
		return "H2";
	}
	
	@Override
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return TYPE_REGISTRY;
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.temporalFunctionRenderer(new H2TemporalFunctionRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.CLOB -> "CHARACTER LARGE OBJECT";
			case Types.BLOB, Types.LONGVARBINARY -> "BINARY LARGE OBJECT";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		if (parameter instanceof SqlLengthParameter length) {
			return switch (jdbcType) {
				case Types.VARCHAR -> "CHARACTER VARYING(" + length.length() + ")";
				case Types.VARBINARY -> "BINARY VARYING(" + length.length() + ")";
				default -> super.getParameterizedTypeName(jdbcType, parameter);
			};
		}
		return super.getParameterizedTypeName(jdbcType, parameter);
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
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql Lock mode must not be null");
		
		if (mode == SqlLockMode.FOR_SHARE) {
			throw new SqlDialectFeatureException(SqlFeature.FOR_SHARE, this);
		}
		if (skipLocked) {
			throw new SqlDialectFeatureException(SqlFeature.SKIP_LOCKED, this);
		}
		if (noWait) {
			throw new SqlDialectFeatureException(SqlFeature.NO_WAIT, this);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.for_().update();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull String getCheckConstraintsQueryString() {
		return "SELECT CONSTRAINT_NAME, CHECK_EXPRESSION FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
	}
}

class H2TemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	H2TemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("'SECOND'").comma().rendered(function.expression().toSql(this.dialect));
		renderer.comma().literal("TIMESTAMP '1970-01-01 00:00:00'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("'" + function.part().name() + "'").comma();
		renderer.rendered(function.secondSummand().toSql(this.dialect)).comma();
		renderer.rendered(function.firstSummand().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("'" + function.part().name() + "'").comma();
		renderer.literal("-").openingBracket().rendered(function.subtrahend().toSql(this.dialect)).closingBracket().comma();
		renderer.rendered(function.minuend().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		Objects.requireNonNull(duration, "Sql duration expression must not be null");
		Objects.requireNonNull(part, "Temporal part must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect));
		return renderer.toSql();
	}
}
