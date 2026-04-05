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

import net.luis.utils.io.database.exception.dialect.*;
import net.luis.utils.io.database.function.SqlFunctionType;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlDialect {
	
	@NonNull String name();
	
	boolean isTypeSupported(@NonNull SqlType<?> type);
	
	@NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlDialectUnsupportedTypeException;
	
	boolean isFunctionSupported(@NonNull SqlFunctionType type);
	
	@NonNull SqlRendered renderFunction(@NonNull SqlFunctionType type, @NonNull List<SqlRendered> arguments) throws SqlDialectUnsupportedFunctionException;
	
	boolean isFeatureSupported(@NonNull SqlFeature feature);
	
	boolean isIndexMethodSupported(@NonNull SqlIndexMethod method);
	
	@NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlDialectUnsupportedIndexMethodException;
	
	@NonNull String quoteIdentifier(@NonNull String identifier);
	
	@NonNull String renderQualifiedName(@NonNull String schema, @NonNull String name);
	
	@NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists);
	
	@NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists);
	
	@NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table);
	
	@NonNull SqlRendered renderColumnDefinition(@NonNull SqlColumn<?, ?> column);
	
	@NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index);
	
	@NonNull SqlRendered renderDropIndex(@NonNull SqlIndex index);
	
	@NonNull SqlRendered renderLimitOffset(long limit, long offset);
	
	@NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlDialectUnsupportedFeatureException;
	
	@NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlDialectUnsupportedFeatureException;
	
	@NonNull String renderSetOperation(@NonNull SqlSetOperation operation);
	
	@NonNull String renderLateralJoin() throws SqlDialectUnsupportedFeatureException;
	
	@NonNull String renderBooleanLiteral(boolean value);
}
