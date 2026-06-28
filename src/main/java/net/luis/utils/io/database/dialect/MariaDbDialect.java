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
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.type.SqlTypeRegistry;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * SQL dialect implementation for MariaDB.<br>
 * Provides MariaDB-specific SQL generation by extending {@link MySqlDialect}.<br>
 *
 * @author Luis-St
 */

public class MariaDbDialect extends MySqlDialect {
	
	/**
	 * The set of SQL features supported by this dialect.
	 */
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.RETURNING,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.FOR_SHARE,
		SqlFeature.SKIP_LOCKED,
		SqlFeature.NO_WAIT,
		SqlFeature.UPSERT_SUFFIX,
		SqlFeature.ROW_LOCKING,
		SqlFeature.INSERT_OR_IGNORE,
		SqlFeature.RENAME_INDEX,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.DROP_CONSTRAINT,
		SqlFeature.JOINED_DML
	);
	/**
	 * The registry of MariaDB-specific SQL type mappings.
	 */
	private static final SqlTypeRegistry TYPE_REGISTRY = SqlTypeRegistry.builder()
		.register(SqlTypes.JSON, "JSON")
		.register(SqlTypes.UUID, "UUID")
		.build();
	
	@Override
	public @NonNull String name() {
		return "MariaDB";
	}
	
	@Override
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return TYPE_REGISTRY;
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		return this.renderStandardReturning(columns);
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql lock mode must not be null");
		if (mode != SqlLockMode.FOR_SHARE) {
			return super.renderLockClause(mode, skipLocked, noWait);
		}
		
		if (noWait) {
			throw new SqlDialectFeatureException(SqlFeature.NO_WAIT, this); // MariaDB cannot combine NOWAIT with a shared lock
		}
		return SqlRenderer.empty().literal("LOCK").literal("IN").literal("SHARE").literal("MODE").toSql(); // MariaDB has no FOR SHARE keyword
	}
}
