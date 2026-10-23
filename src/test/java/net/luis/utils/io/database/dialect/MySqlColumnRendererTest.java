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
 * Test class for {@link MySqlColumnRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlColumnRendererTest {
	
	private static final MySqlColumnRenderer RENDERER = new MySqlColumnRenderer(SqlDialects.MYSQL);
	
	/**
	 * Builds a standalone integer column of a fresh table so the nullable and the non-nullable branch can use the same names.
	 *
	 * @param tableName The name of the owning table
	 * @param columnName The name of the column
	 * @param notNull Whether the column is declared {@code NOT NULL}
	 * @return The built column
	 */
	private static SqlColumn<Object, Integer> column(String tableName, String columnName, boolean notNull) {
		SqlTable<Object> table = SqlTable.create(Object.class, tableName);
		return table.column(columnName, SqlTypes.INTEGER, object -> 0, builder -> notNull ? builder.notNull() : builder);
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
	void renderAlterColumnTypeProducesModify() throws SqlException {
		String sql = RENDERER.renderAlterColumnType(SqlTestFixtures.integerColumn(), SqlTypes.INTEGER).sql();
		assertTrue(sql.contains("ALTER TABLE"));
		assertTrue(sql.contains("MODIFY"));
		assertTrue(sql.contains("COLUMN"));
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
	void renderAlterColumnDropDefaultWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnDropDefault(null));
	}
	
	@Test
	void renderAlterColumnDropDefaultOnNotNullColumnDelegatesToSuper() throws SqlException {
		SqlColumn<Object, Integer> column = column("users", "age", true);
		SqlRendered rendered = RENDERER.renderAlterColumnDropDefault(column);
		assertEquals("ALTER TABLE `users` ALTER COLUMN `age` DROP DEFAULT", rendered.sql());
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderAlterColumnDropDefaultOnNullableColumnSetsDefaultNull() throws SqlException {
		SqlColumn<Object, Integer> column = column("users", "age", false);
		SqlRendered rendered = RENDERER.renderAlterColumnDropDefault(column);
		assertEquals("ALTER TABLE `users` ALTER COLUMN `age` SET DEFAULT NULL", rendered.sql());
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void renderAlterColumnDropDefaultQuotesIdentifiers() throws SqlException {
		String notNullSql = RENDERER.renderAlterColumnDropDefault(column("us`ers", "a`ge", true)).sql();
		String nullableSql = RENDERER.renderAlterColumnDropDefault(column("us`ers", "a`ge", false)).sql();
		assertTrue(notNullSql.contains("`us``ers`"), notNullSql);
		assertTrue(notNullSql.contains("`a``ge`"), notNullSql);
		assertTrue(nullableSql.contains("`us``ers`"), nullableSql);
		assertTrue(nullableSql.contains("`a``ge`"), nullableSql);
	}
	
	@Test
	void renderAlterColumnDropDefaultProducesNoParameters() throws SqlException {
		assertTrue(RENDERER.renderAlterColumnDropDefault(column("users", "age", true)).parameters().isEmpty());
		assertTrue(RENDERER.renderAlterColumnDropDefault(column("users", "age", false)).parameters().isEmpty());
	}
}
