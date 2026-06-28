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
 * Represents a database-specific sql dialect.<br>
 * A dialect provides the rendering logic, type mappings, feature support and metadata queries required to
 * generate sql for a specific database system.<br>
 *
 * @author Luis-St
 */
public interface SqlDialect {
	
	/**
	 * Returns the name of this dialect.<br>
	 * @return The dialect name
	 */
	@NonNull String name();
	
	/**
	 * Returns the renderer used to render table statements for this dialect.<br>
	 * @return The table renderer
	 */
	@NonNull SqlTableRenderer tableRenderer();
	
	/**
	 * Returns the renderer used to render index statements for this dialect.<br>
	 * @return The index renderer
	 */
	@NonNull SqlIndexRenderer indexRenderer();
	
	/**
	 * Returns the renderer used to render column definitions for this dialect.<br>
	 * @return The column renderer
	 */
	@NonNull SqlColumnRenderer columnRenderer();
	
	/**
	 * Returns the renderer used to render migration operations for this dialect.<br>
	 * @return The migration operation renderer
	 */
	@NonNull SqlMigrationOperationRenderer migrationRenderer();
	
	/**
	 * Returns the renderer used to render schema statements for this dialect.<br>
	 * @return The schema renderer
	 */
	@NonNull SqlSchemaRenderer schemaRenderer();
	
	/**
	 * Checks whether the given sql type is supported by this dialect.<br>
	 *
	 * @param type The sql type to check
	 * @return {@code true} if the type is supported, {@code false} otherwise
	 * @throws NullPointerException If the type is null
	 */
	boolean isTypeSupported(@NonNull SqlType<?> type);
	
	/**
	 * Returns the dialect-specific name of the given sql type.<br>
	 *
	 * @param type The sql type to get the name for
	 * @return The dialect-specific type name
	 * @throws NullPointerException If the type is null
	 * @throws SqlException If the type is not supported by this dialect
	 */
	@NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlException;
	
	/**
	 * Returns the dialect-specific type name used when casting a value to the given type.<br>
	 * By default this delegates to {@link #getTypeName(SqlType)}.<br>
	 *
	 * @param type The sql type to get the cast name for
	 * @return The dialect-specific cast type name
	 * @throws NullPointerException If the type is null
	 * @throws SqlException If the type is not supported by this dialect
	 */
	default @NonNull String getCastTypeName(@NonNull SqlType<?> type) throws SqlException {
		return this.getTypeName(type);
	}
	
	/**
	 * Returns an optional value binder that overrides the default binding for the given type.<br>
	 * By default an empty optional is returned, meaning the default binding is used.<br>
	 *
	 * @param type The sql type to get the binding override for
	 * @return An optional value binder or an empty optional if no override is defined
	 * @throws NullPointerException If the type is null
	 */
	default @NonNull Optional<SqlValueBinder> bindingOverride(@NonNull SqlType<?> type) {
		return Optional.empty();
	}
	
	/**
	 * Returns an optional value reader that overrides the default reading for the given type.<br>
	 * By default an empty optional is returned, meaning the default reading is used.<br>
	 *
	 * @param type The sql type to get the reading override for
	 * @return An optional value reader or an empty optional if no override is defined
	 * @throws NullPointerException If the type is null
	 */
	default @NonNull Optional<SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
		return Optional.empty();
	}
	
	/**
	 * Renders the given sql expression into a rendered sql fragment.<br>
	 *
	 * @param expression The expression to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the expression is null
	 * @throws SqlException If the expression cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) throws SqlException;
	
	/**
	 * Renders the given sql function into a rendered sql fragment.<br>
	 *
	 * @param function The function to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the function is null
	 * @throws SqlException If the function cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException;
	
	/**
	 * Renders the given sql condition into a rendered sql fragment.<br>
	 *
	 * @param condition The condition to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the condition is null
	 * @throws SqlException If the condition cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) throws SqlException;
	
	/**
	 * Renders the given window clause into a rendered sql fragment.<br>
	 *
	 * @param clause The window clause to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the clause is null
	 * @throws SqlException If the clause cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) throws SqlException;
	
	/**
	 * Renders the given window frame into a rendered sql fragment.<br>
	 *
	 * @param frame The window frame to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the frame is null
	 * @throws SqlException If the frame cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderWindowFrame(@NonNull SqlWindowFrame frame) throws SqlException;
	
	/**
	 * Renders the given frame bound into a rendered sql fragment.<br>
	 *
	 * @param bound The frame bound to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the bound is null
	 * @throws SqlException If the bound cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderFrameBound(@NonNull SqlFrameBound bound) throws SqlException;
	
	/**
	 * Checks whether the given optional sql feature is supported by this dialect.<br>
	 *
	 * @param feature The feature to check
	 * @return {@code true} if the feature is supported, {@code false} otherwise
	 * @throws NullPointerException If the feature is null
	 */
	boolean isFeatureSupported(@NonNull SqlFeature feature);
	
	/**
	 * Returns the maximum number of bind parameters allowed in a single statement.<br>
	 * By default this is {@code 65535}.<br>
	 *
	 * @return The maximum number of bind parameters
	 */
	default int maxBindParameters() {
		return 65535;
	}
	
	/**
	 * Checks whether the given index method is supported by this dialect.<br>
	 *
	 * @param method The index method to check
	 * @return {@code true} if the index method is supported, {@code false} otherwise
	 * @throws NullPointerException If the method is null
	 */
	boolean isIndexMethodSupported(@NonNull SqlIndexMethod method);
	
	/**
	 * Returns the dialect-specific name of the given index method.<br>
	 *
	 * @param method The index method to get the name for
	 * @return The dialect-specific index method name
	 * @throws NullPointerException If the method is null
	 * @throws SqlException If the index method is not supported by this dialect
	 */
	@NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlException;
	
	/**
	 * Renders the given value as a dialect-specific sql literal.<br>
	 *
	 * @param value The value to render
	 * @return The rendered sql literal
	 * @throws NullPointerException If the value is null
	 * @throws SqlException If the value cannot be rendered as a literal by this dialect
	 */
	@NonNull String renderValueLiteral(@NonNull Object value) throws SqlException;
	
	/**
	 * Quotes the given identifier using the dialect-specific quoting rules.<br>
	 *
	 * @param identifier The identifier to quote
	 * @return The quoted identifier
	 * @throws NullPointerException If the identifier is null
	 */
	@NonNull String quoteIdentifier(@NonNull String identifier);
	
	/**
	 * Renders the given referential action into the given renderer.<br>
	 *
	 * @param renderer The renderer to render into
	 * @param action The referential action to render
	 * @throws NullPointerException If the renderer or action is null
	 * @throws SqlException If the referential action cannot be rendered by this dialect
	 */
	void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) throws SqlException;
	
	/**
	 * Renders the limit and offset clause for the given values.<br>
	 *
	 * @param limit The maximum number of rows to return
	 * @param offset The number of rows to skip
	 * @param hasOrdering Whether the query has an ordering clause
	 * @return The rendered limit and offset clause
	 * @throws SqlException If the limit and offset clause cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderLimitOffset(long limit, long offset, boolean hasOrdering) throws SqlException;
	
	/**
	 * Renders the returning clause for the given columns.<br>
	 *
	 * @param columns The columns to return
	 * @return The rendered returning clause
	 * @throws NullPointerException If the columns list is null
	 * @throws SqlException If the returning clause cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException;
	
	/**
	 * Renders the row locking clause for the given lock mode.<br>
	 *
	 * @param mode The lock mode to render
	 * @param skipLocked Whether locked rows should be skipped
	 * @param noWait Whether the statement should fail immediately if a lock cannot be acquired
	 * @return The rendered lock clause
	 * @throws NullPointerException If the mode is null
	 * @throws SqlException If the lock clause cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException;
	
	/**
	 * Renders the dialect-specific lock hint for the given lock mode.<br>
	 * By default an empty fragment is returned.<br>
	 *
	 * @param mode The lock mode to render
	 * @param skipLocked Whether locked rows should be skipped
	 * @param noWait Whether the statement should fail immediately if a lock cannot be acquired
	 * @return The rendered lock hint or an empty fragment if not supported
	 * @throws NullPointerException If the mode is null
	 * @throws SqlException If the lock hint cannot be rendered by this dialect
	 */
	default @NonNull SqlRendered renderLockHint(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		return SqlRendered.of("");
	}
	
	/**
	 * Renders the given set operation into a rendered sql fragment.<br>
	 *
	 * @param operation The set operation to render
	 * @return The rendered sql fragment
	 * @throws NullPointerException If the operation is null
	 * @throws SqlException If the set operation cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderSetOperation(@NonNull SqlSetOperation operation) throws SqlException;
	
	/**
	 * Renders the lateral join keyword for this dialect.<br>
	 *
	 * @return The rendered lateral join keyword
	 * @throws SqlException If the lateral join cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderLateralJoin() throws SqlException;
	
	/**
	 * Renders the given boolean value as a dialect-specific literal.<br>
	 *
	 * @param value The boolean value to render
	 * @return The rendered boolean literal
	 * @throws SqlException If the boolean literal cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderBooleanLiteral(boolean value) throws SqlException;
	
	/**
	 * Renders the ordering direction together with the given null ordering.<br>
	 *
	 * @param ordering The ordering direction to render
	 * @param nullOrdering The null ordering to render
	 * @return The rendered ordering clause
	 * @throws NullPointerException If the ordering or null ordering is null
	 * @throws SqlException If the ordering cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderOrdering(@NonNull SqlOrdering ordering, @NonNull SqlNullOrdering nullOrdering) throws SqlException;
	
	/**
	 * Renders the upsert clause for the given conflict and update columns.<br>
	 *
	 * @param conflictColumn The column that triggers the conflict
	 * @param updateColumns The columns to update on conflict
	 * @return The rendered upsert clause
	 * @throws NullPointerException If the conflict column or update columns is null
	 * @throws SqlException If the upsert clause cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException;
	
	/**
	 * Renders a complete upsert statement for the given table, columns and values.<br>
	 *
	 * @param table The table to upsert into
	 * @param columns The columns to insert values for
	 * @param conflictColumn The column that triggers the conflict
	 * @param valueTuples The rendered value tuples to insert
	 * @return The rendered upsert statement
	 * @throws NullPointerException If the table, columns, conflict column or value tuples is null
	 * @throws SqlException If the upsert statement cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull SqlColumn<?, ?> conflictColumn, @NonNull SqlRendered valueTuples) throws SqlException;
	
	/**
	 * Renders the insert-or-ignore modifier for this dialect.<br>
	 *
	 * @return The rendered insert-or-ignore modifier
	 * @throws SqlException If the insert-or-ignore modifier cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException;
	
	/**
	 * Checks whether this dialect uses an insert-or-ignore modifier rather than a suffix.<br>
	 * By default {@code false} is returned.<br>
	 *
	 * @return {@code true} if an insert-or-ignore modifier is used, {@code false} otherwise
	 */
	default boolean usesInsertOrIgnoreModifier() {
		return false;
	}
	
	/**
	 * Renders the insert-or-ignore suffix for the given conflict columns.<br>
	 *
	 * @param conflictColumns The columns that trigger the conflict
	 * @return The rendered insert-or-ignore suffix
	 * @throws NullPointerException If the conflict columns is null
	 * @throws SqlException If the insert-or-ignore suffix cannot be rendered by this dialect
	 */
	@NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException;
	
	/**
	 * Retrieves the check constraints for the given table from the database.<br>
	 *
	 * @param connection The connection used to query the database
	 * @param schema The schema the table belongs to
	 * @param tableName The name of the table to query
	 * @return The list of check constraints for the table
	 * @throws NullPointerException If the connection, schema or table name is null
	 * @throws SqlException If the check constraints cannot be retrieved
	 */
	@NonNull List<SqlCheckConstraintInfo> getCheckConstraints(@NonNull Connection connection, @NonNull String schema, @NonNull String tableName) throws SqlException;
	
	/**
	 * Returns the sql used to create the schema columns metadata table.<br>
	 *
	 * @return The create statement for the schema columns metadata table
	 * @throws SqlException If the statement cannot be provided by this dialect
	 */
	@NonNull String getCreateSchemaColumnsTableSql() throws SqlException;
	
	/**
	 * Returns the sql used to create the schema check constraints metadata table.<br>
	 *
	 * @return The create statement for the schema check constraints metadata table
	 * @throws SqlException If the statement cannot be provided by this dialect
	 */
	@NonNull String getCreateSchemaCheckConstraintsTableSql() throws SqlException;
	
	/**
	 * Returns the sql used to insert a row into the schema columns metadata table.<br>
	 * @return The insert statement for the schema columns metadata table
	 */
	@NonNull String getInsertSchemaColumnSql();
	
	/**
	 * Returns the sql used to insert a row into the schema check constraints metadata table.<br>
	 * @return The insert statement for the schema check constraints metadata table
	 */
	@NonNull String getInsertSchemaCheckConstraintSql();
	
	/**
	 * Returns the sql used to select rows from the schema columns metadata table.<br>
	 * @return The select statement for the schema columns metadata table
	 */
	@NonNull String getSelectSchemaColumnsSql();
	
	/**
	 * Returns the sql used to select rows from the schema check constraints metadata table.<br>
	 * @return The select statement for the schema check constraints metadata table
	 */
	@NonNull String getSelectSchemaCheckConstraintsSql();
	
	/**
	 * Returns the sql used to delete rows from the schema columns metadata table.<br>
	 * @return The delete statement for the schema columns metadata table
	 */
	@NonNull String getDeleteSchemaColumnsSql();
	
	/**
	 * Returns the sql used to delete rows from the schema check constraints metadata table.<br>
	 * @return The delete statement for the schema check constraints metadata table
	 */
	@NonNull String getDeleteSchemaCheckConstraintsSql();
}
