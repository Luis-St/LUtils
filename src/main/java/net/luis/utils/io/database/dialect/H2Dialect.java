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

import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedFeatureException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedTypeException;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SimpleSqlRendered;
import net.luis.utils.io.database.rendering.SqlRendered;
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
		SqlFeature.FOR_UPDATE
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH
	);
	
	@Override
	public @NonNull String name() {
		return "H2";
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.CLOB -> "CHARACTER LARGE OBJECT";
			case Types.BLOB -> "BINARY LARGE OBJECT";
			case Types.LONGVARBINARY -> "BINARY LARGE OBJECT";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
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
		Objects.requireNonNull(feature, "Feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Index method must not be null");
		return SUPPORTED_INDEX_METHODS.contains(method);
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlDialectUnsupportedFeatureException {
		Objects.requireNonNull(mode, "Lock mode must not be null");
		if (mode == SqlLockMode.FOR_SHARE) {
			throw new SqlDialectUnsupportedFeatureException("FOR SHARE is not supported by dialect " + this.name());
		}
		if (skipLocked) {
			throw new SqlDialectUnsupportedFeatureException("SKIP LOCKED is not supported by dialect " + this.name());
		}
		if (noWait) {
			throw new SqlDialectUnsupportedFeatureException("NOWAIT is not supported by dialect " + this.name());
		}
		return SimpleSqlRendered.of("FOR UPDATE");
	}
}
