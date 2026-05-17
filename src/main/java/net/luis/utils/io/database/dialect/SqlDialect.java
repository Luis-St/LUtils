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

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.rendering.base.SqlMigrationOperationRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.SqlNullOrdering;
import net.luis.utils.io.database.expression.orderable.SqlOrdering;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.window.*;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlDialect {
	
	@NonNull String name();
	
	@NonNull SqlMigrationOperationRenderer migrationRenderer();
	
	boolean isTypeSupported(@NonNull SqlType<?> type);
	
	@NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException;
	
	@NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) throws SqlException;
	
	@NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException;
	
	@NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) throws SqlException;
	
	@NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) throws SqlException;
	
	@NonNull SqlRendered renderWindowFrame(@NonNull SqlWindowFrame frame) throws SqlException;
	
	@NonNull SqlRendered renderFrameBound(@NonNull SqlFrameBound bound) throws SqlException;
	
	boolean isFeatureSupported(@NonNull SqlFeature feature);
	
	boolean isIndexMethodSupported(@NonNull SqlIndexMethod method);
	
	@NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlException;
	
	@NonNull String renderValueLiteral(@NonNull Object value) throws SqlException;
	
	@NonNull String quoteIdentifier(@NonNull String identifier);
	
	@NonNull SqlRendered renderQualifiedName(@NonNull String schema, @NonNull String name) throws SqlException;
	
	@NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists) throws SqlException;
	
	@NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists) throws SqlException;
	
	@NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException;
	
	@NonNull SqlRendered renderColumnDefinition(@NonNull SqlColumn<?, ?> column) throws SqlException;
	
	void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) throws SqlException;
	
	@NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException;
	
	@NonNull SqlRendered renderDropIndex(@NonNull SqlTable<?> owningTable, @NonNull String indexName) throws SqlException;
	
	@NonNull SqlRendered renderLimitOffset(long limit, long offset) throws SqlException;
	
	@NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException;
	
	@NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException;
	
	@NonNull SqlRendered renderSetOperation(@NonNull SqlSetOperation operation) throws SqlException;
	
	@NonNull SqlRendered renderLateralJoin() throws SqlException;
	
	@NonNull SqlRendered renderBooleanLiteral(boolean value) throws SqlException;
	
	@NonNull SqlRendered renderOrdering(@NonNull SqlOrdering ordering, @NonNull SqlNullOrdering nullOrdering) throws SqlException;
	
	@NonNull SqlRendered renderCreateSchema(@NonNull String name, boolean ifNotExists) throws SqlException;
	
	@NonNull SqlRendered renderDropSchema(@NonNull String name, boolean ifExists, boolean cascade) throws SqlException;
	
	@NonNull SqlRendered renderAutoIncrementKeyword() throws SqlException;
	
	@NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException;
	
	@NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException;
	
	@NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException;
	
	@NonNull SqlRendered renderAlterColumnType(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> newType) throws SqlException;
	
	@NonNull SqlRendered renderAlterColumnNullability(@NonNull String tableName, @NonNull String columnName, @NonNull SqlType<?> columnType, boolean nullable) throws SqlException;
	
	@NonNull SqlRendered renderAlterColumnSetDefault(@NonNull String tableName, @NonNull String columnName, @NonNull String renderedDefault) throws SqlException;
	
	@NonNull SqlRendered renderAlterColumnDropDefault(@NonNull String tableName, @NonNull String columnName) throws SqlException;
	
	@NonNull SqlRendered renderDropIndex(@NonNull String tableName, @NonNull String indexName) throws SqlException;
	
	@NonNull SqlRendered renderRenameIndex(@Nullable String tableName, @NonNull String from, @NonNull String to) throws SqlException;
}
