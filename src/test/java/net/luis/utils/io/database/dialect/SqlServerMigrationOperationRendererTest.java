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
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlServerMigrationOperationRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlServerMigrationOperationRendererTest {
	
	private static final SqlServerMigrationOperationRenderer RENDERER = new SqlServerMigrationOperationRenderer(SqlDialects.SQL_SERVER);
	
	private static SqlTable<Object> table(String name) {
		return SqlTable.create(Object.class, name);
	}
	
	private static SqlColumn<Object, Integer> column(SqlTable<Object> table, String name) {
		return table.column(name, SqlTypes.INTEGER, object -> 0);
	}
	
	@Test
	void renderRenameTableNullFromTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameTable(null, table("new")));
	}
	
	@Test
	void renderRenameTableNullToTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameTable(table("old"), null));
	}
	
	@Test
	void renderRenameColumnNullTable() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(null, column(table, "old"), column(table, "new")));
	}
	
	@Test
	void renderRenameColumnNullFromColumn() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(table, null, column(table, "new")));
	}
	
	@Test
	void renderRenameColumnNullToColumn() {
		SqlTable<Object> table = table("users");
		assertThrows(NullPointerException.class, () -> RENDERER.renderRenameColumn(table, column(table, "old"), null));
	}
	
	@Test
	void renderRenameTableUsesSpRename() throws SqlException {
		String sql = RENDERER.renderRenameTable(table("old"), table("new")).sql();
		assertTrue(sql.contains("EXEC"));
		assertTrue(sql.contains("sp_rename"));
		assertTrue(sql.contains("'old'"));
		assertTrue(sql.contains("'new'"));
	}
	
	@Test
	void renderRenameColumnUsesSpRenameWithColumn() throws SqlException {
		SqlTable<Object> table = table("users");
		String sql = RENDERER.renderRenameColumn(table, column(table, "old"), column(table, "new")).sql();
		assertTrue(sql.contains("sp_rename"));
		assertTrue(sql.contains("'COLUMN'"));
		assertTrue(sql.contains("'users.old'"));
	}
}
