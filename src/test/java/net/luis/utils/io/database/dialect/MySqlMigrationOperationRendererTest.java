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
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlMigrationOperationRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlMigrationOperationRendererTest {
	
	private static final MySqlMigrationOperationRenderer RENDERER = new MySqlMigrationOperationRenderer(SqlDialects.MYSQL);
	
	private static SqlTable<Object> table(String name) {
		return SqlTable.create(Object.class, name);
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
	void renderRenameTableProducesRenameTable() throws SqlException {
		String sql = RENDERER.renderRenameTable(table("old"), table("new")).sql();
		assertTrue(sql.contains("RENAME TABLE"));
		assertTrue(sql.contains("`old`"));
		assertTrue(sql.contains("`new`"));
	}
}
