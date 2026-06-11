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
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDialect}.<br>
 *
 * @author Luis-St
 */
class SqlDialectTest {
	
	@Test
	void bindingOverrideDefaultReturnsEmpty() {
		assertTrue(new StubDialect().bindingOverride(SqlTypes.INTEGER).isEmpty());
	}
	
	@Test
	void readingOverrideDefaultReturnsEmpty() {
		assertTrue(new StubDialect().readingOverride(SqlTypes.INTEGER).isEmpty());
	}
	
	@Test
	void maxBindParametersDefaultValue() {
		assertEquals(65535, new StubDialect().maxBindParameters());
	}
	
	/**
	 * Minimal hand-written {@link SqlDialect} that leaves the three interface default methods intact
	 * so they can be exercised in isolation. All abstract members are stubbed and must not be invoked.
	 */
	private static final class StubDialect implements SqlDialect {
		
		@Override
		public @NonNull String name() {
			return "Stub";
		}
		
		@Override
		public @NonNull SqlTableRenderer tableRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlIndexRenderer indexRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlColumnRenderer columnRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlMigrationOperationRenderer migrationRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlSchemaRenderer schemaRenderer() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isTypeSupported(@NonNull SqlType<?> type) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getTypeName(@NonNull SqlType<?> type) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderWindowClause(@NonNull SqlWindowClause clause) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderWindowFrame(@NonNull SqlWindowFrame frame) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderFrameBound(@NonNull SqlFrameBound bound) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isFeatureSupported(@NonNull SqlFeature feature) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String renderValueLiteral(@NonNull Object value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String quoteIdentifier(@NonNull String identifier) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderLimitOffset(long limit, long offset, boolean hasOrdering) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderSetOperation(@NonNull SqlSetOperation operation) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderLateralJoin() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderBooleanLiteral(boolean value) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderOrdering(@NonNull SqlOrdering ordering, @NonNull SqlNullOrdering nullOrdering) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull SqlColumn<?, ?> conflictColumn, @NonNull SqlRendered valueTuples) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderInsertOrIgnoreModifier() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull List<SqlCheckConstraintInfo> getCheckConstraints(@NonNull Connection connection, @NonNull String schema, @NonNull String tableName) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getCreateSchemaColumnsTableSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getCreateSchemaCheckConstraintsTableSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getInsertSchemaColumnSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getInsertSchemaCheckConstraintSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getSelectSchemaColumnsSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getSelectSchemaCheckConstraintsSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getDeleteSchemaColumnsSql() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull String getDeleteSchemaCheckConstraintsSql() {
			throw new UnsupportedOperationException();
		}
	}
}
