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

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerColumnRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerColumnRendererTest {
	
	private static final SqlServerColumnRenderer RENDERER = new SqlServerColumnRenderer(SqlDialects.SQL_SERVER);
	
	/**
	 * Builds a standalone integer column of a fresh table with the given table and column name.
	 *
	 * @param tableName The name of the owning table
	 * @param columnName The name of the column
	 * @return The built column
	 */
	private static SqlColumn<Object, Integer> column(String tableName, String columnName) {
		return SqlTable.create(Object.class, tableName).column(columnName, SqlTypes.INTEGER, object -> 0);
	}
	
	@Test
	void renderAlterColumnTypeNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnType(null, SqlTypes.INTEGER));
	}
	
	@Test
	void renderAlterColumnTypeNullNewType() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnType(SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void renderAlterColumnNullabilityNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnNullability(null, true));
	}
	
	@Test
	void renderAlterColumnTypeProducesAlterColumn() throws SqlException {
		String sql = RENDERER.renderAlterColumnType(SqlTestFixtures.integerColumn(), SqlTypes.INTEGER).sql();
		assertTrue(sql.contains("ALTER TABLE"));
		assertTrue(sql.contains("ALTER COLUMN"));
		assertTrue(sql.contains("INTEGER"));
	}
	
	@Test
	void renderAlterColumnNullabilityNullable() throws SqlException {
		String sql = RENDERER.renderAlterColumnNullability(SqlTestFixtures.integerColumn(), true).sql();
		assertTrue(sql.endsWith("NULL"));
		assertFalse(sql.contains("NOT NULL"));
	}
	
	@Test
	void renderAlterColumnNullabilityNotNullable() throws SqlException {
		String sql = RENDERER.renderAlterColumnNullability(SqlTestFixtures.integerColumn(), false).sql();
		assertTrue(sql.endsWith("NOT NULL"));
	}
	
	@Test
	void renderAlterColumnSetDefaultWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnSetDefault(null, "0"));
	}
	
	@Test
	void renderAlterColumnSetDefaultWithNullRenderedDefault() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnSetDefault(SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void renderAlterColumnDropDefaultWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnDropDefault(null));
	}
	
	@Test
	void renderAlterColumnSetDefaultAddsNamedConstraint() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnSetDefault(column("users", "age"), "0");
		assertEquals("ALTER TABLE [users] ADD CONSTRAINT [DF_users_age] DEFAULT 0 FOR [age]", rendered.sql());
		assertFalse(rendered.sql().contains("SET DEFAULT"));
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderAlterColumnDropDefaultLooksUpConstraintName() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnDropDefault(column("users", "age"));
		String sql = rendered.sql();
		assertTrue(sql.startsWith("DECLARE @constraint sysname"), sql);
		assertTrue(sql.contains("sys.default_constraints"), sql);
		assertTrue(sql.contains("OBJECT_ID(N'users')"), sql);
		assertTrue(sql.contains("c.name = N'age'"), sql);
		assertTrue(sql.contains("ALTER TABLE [users] DROP CONSTRAINT"), sql);
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderAlterColumnSetDefaultWithStringDefault() throws SqlException {
		String sql = RENDERER.renderAlterColumnSetDefault(column("users", "kind"), "'unknown'").sql();
		assertTrue(sql.contains("DEFAULT 'unknown' FOR [kind]"), sql);
	}
	
	@Test
	void renderAlterColumnSetDefaultQuotesIdentifiersWithBrackets() throws SqlException {
		String sql = RENDERER.renderAlterColumnSetDefault(column("us]ers", "a]ge"), "0").sql();
		assertTrue(sql.contains("ALTER TABLE [us]]ers]"), sql);
		assertTrue(sql.contains("FOR [a]]ge]"), sql);
		assertTrue(sql.contains("CONSTRAINT [DF_us]]ers_a]]ge]"), sql);
	}
	
	@Test
	void renderAlterColumnDropDefaultEscapesStringLiterals() throws SqlException {
		String sql = RENDERER.renderAlterColumnDropDefault(column("us'ers", "a'ge")).sql();
		assertTrue(sql.contains("OBJECT_ID(N'us''ers')"), sql);
		assertTrue(sql.contains("c.name = N'a''ge'"), sql);
	}
	
	@Test
	void alterColumnSetThenDropDefaultUsesSameConstraintName() throws SqlException {
		SqlColumn<Object, Integer> column = column("users", "age");
		String constraintName = SqlServerDialect.defaultConstraintName(column);
		String setSql = RENDERER.renderAlterColumnSetDefault(column, "0").sql();
		String dropSql = RENDERER.renderAlterColumnDropDefault(column).sql();
		
		assertEquals("DF_users_age", constraintName);
		assertTrue(setSql.contains("CONSTRAINT [" + constraintName + "]"), setSql);
		assertTrue(dropSql.contains("OBJECT_ID(N'users')"), dropSql);
		assertTrue(dropSql.contains("c.name = N'age'"), dropSql);
	}
}
