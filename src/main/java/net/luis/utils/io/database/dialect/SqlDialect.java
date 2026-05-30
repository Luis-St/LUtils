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
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.SqlNullOrdering;
import net.luis.utils.io.database.expression.orderable.SqlOrdering;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.window.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.SqlCheckConstraintInfo;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.*;
import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public interface SqlDialect {
	
	@NonNull String name();
	
	@NonNull SqlTableRenderer tableRenderer();
	
	@NonNull SqlIndexRenderer indexRenderer();
	
	@NonNull SqlColumnRenderer columnRenderer();
	
	@NonNull SqlMigrationOperationRenderer migrationRenderer();
	
	@NonNull SqlSchemaRenderer schemaRenderer();
	
	boolean isTypeSupported(@NonNull SqlType<?> type);
	
	@NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException;
	
	default @NonNull Optional<SqlValueBinder> bindingOverride(@NonNull SqlType<?> type) {
		return Optional.empty();
	}
	
	default @NonNull Optional<SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
		return Optional.empty();
	}
	
	@NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) throws SqlException;
	
	@NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException;
	
	@NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) throws SqlException;
	
	@NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) throws SqlException;
	
	@NonNull SqlRendered renderWindowFrame(@NonNull SqlWindowFrame frame) throws SqlException;
	
	@NonNull SqlRendered renderFrameBound(@NonNull SqlFrameBound bound) throws SqlException;
	
	boolean isFeatureSupported(@NonNull SqlFeature feature);
	
	default int maxBindParameters() {
		return 65535;
	}
	
	boolean isIndexMethodSupported(@NonNull SqlIndexMethod method);
	
	@NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlException;
	
	@NonNull String renderValueLiteral(@NonNull Object value) throws SqlException;
	
	@NonNull String quoteIdentifier(@NonNull String identifier);
	
	void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) throws SqlException;
	
	@NonNull SqlRendered renderLimitOffset(long limit, long offset, boolean hasOrdering) throws SqlException;
	
	@NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException;
	
	@NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException;
	
	@NonNull SqlRendered renderSetOperation(@NonNull SqlSetOperation operation) throws SqlException;
	
	@NonNull SqlRendered renderLateralJoin() throws SqlException;
	
	@NonNull SqlRendered renderBooleanLiteral(boolean value) throws SqlException;
	
	@NonNull SqlRendered renderOrdering(@NonNull SqlOrdering ordering, @NonNull SqlNullOrdering nullOrdering) throws SqlException;
	
	@NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException;
	
	@NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull SqlColumn<?, ?> conflictColumn, @NonNull SqlRendered valueTuples) throws SqlException;
	
	@NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException;
	
	@NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException;
	
	@NonNull List<SqlCheckConstraintInfo> getCheckConstraints(@NonNull Connection connection, @NonNull String schema, @NonNull String tableName) throws SqlException;
	
	@NonNull String getCreateSchemaColumnsTableSql() throws SqlException;
	
	@NonNull String getCreateSchemaCheckConstraintsTableSql() throws SqlException;
	
	@NonNull String getInsertSchemaColumnSql();
	
	@NonNull String getInsertSchemaCheckConstraintSql();
	
	@NonNull String getSelectSchemaColumnsSql();
	
	@NonNull String getSelectSchemaCheckConstraintsSql();
	
	@NonNull String getDeleteSchemaColumnsSql();
	
	@NonNull String getDeleteSchemaCheckConstraintsSql();
}
