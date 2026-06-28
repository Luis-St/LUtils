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
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqliteTableRenderer}.<br>
 *
 * @author Luis-St
 */
class SqliteTableRendererTest {
	
	private static final SqliteTableRenderer RENDERER = new SqliteTableRenderer(SqlDialects.SQLITE);
	
	private static SqlTable<Object> singlePrimaryKeyTable() {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey().autoIncrement());
		return table;
	}
	
	@Test
	void renderAutoIncrementNullRenderer() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAutoIncrement(null, SqlTestFixtures.integerColumn()));
	}
	
	@Test
	void renderAutoIncrementNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAutoIncrement(SqlRenderer.empty(), null));
	}
	
	@Test
	void renderTruncateTableNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTruncateTable(null));
	}
	
	@Test
	void renderTableRebuildNullTable() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTableRebuild(null, List.of(), List.of()));
	}
	
	@Test
	void renderTableRebuildNullColumns() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTableRebuild(SqlTestFixtures.sampleTable(), null, List.of()));
	}
	
	@Test
	void renderTableRebuildNullExtraConstraints() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderTableRebuild(SqlTestFixtures.sampleTable(), List.of(), null));
	}
	
	@Test
	void renderColumnForTableIntegerPrimaryKeyAutoIncrement() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey().autoIncrement());
		String sql = RENDERER.renderColumnForTable(column, false).sql();
		assertTrue(sql.contains("INTEGER PRIMARY KEY AUTOINCREMENT"));
		assertTrue(sql.contains("NOT NULL"));
	}
	
	@Test
	void renderColumnForTableFallsBackToSuper() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, String> column = table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		String sql = RENDERER.renderColumnForTable(column, false).sql();
		assertFalse(sql.contains("AUTOINCREMENT"));
	}
	
	@Test
	void renderTruncateTableUsesDelete() throws SqlException {
		String sql = RENDERER.renderTruncateTable(SqlTestFixtures.sampleTable()).sql();
		assertTrue(sql.contains("DELETE FROM"));
		assertTrue(sql.contains("\"test_table\""));
	}
	
	@Test
	void renderTableRebuildEmitsExpectedStatementSequence() throws SqlException {
		SqlTable<Object> table = singlePrimaryKeyTable();
		List<SqlRendered> statements = RENDERER.renderTableRebuild(table, table.columns(), List.of());
		assertEquals(7, statements.size());
		assertEquals("PRAGMA legacy_alter_table=ON", statements.get(0).sql());
		assertEquals("PRAGMA defer_foreign_keys=ON", statements.get(1).sql());
		assertTrue(statements.get(2).sql().contains("CREATE"));
		assertTrue(statements.get(2).sql().contains("\"_items_rebuild\""));
		assertTrue(statements.get(3).sql().contains("INSERT"));
		assertTrue(statements.get(3).sql().contains("SELECT"));
		assertTrue(statements.get(4).sql().contains("DROP TABLE"));
		assertTrue(statements.get(5).sql().contains("RENAME"));
		assertEquals("PRAGMA legacy_alter_table=OFF", statements.get(6).sql());
	}
	
	@Test
	void renderTableRebuildWithMultipleColumnsAddsCommas() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey().autoIncrement());
		table.column("name", SqlTestFixtures.STRING_TYPE, object -> "test");
		List<SqlRendered> statements = RENDERER.renderTableRebuild(table, table.columns(), List.of());
		String create = statements.get(2).sql();
		assertTrue(create.contains(","));
		assertTrue(create.contains("\"id\""));
		assertTrue(create.contains("\"name\""));
	}
	
	@Test
	void renderTableRebuildWithAuditColumns() throws SqlException {
		SqlTable<Object> table = SqlTable.audited(Object.class, "audited_items");
		table.column("id", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey().autoIncrement());
		List<SqlRendered> statements = RENDERER.renderTableRebuild(table, table.columns(), List.of());
		String create = statements.get(2).sql();
		String auditColumn = table.auditConfig().orElseThrow().columnNames().getFirst();
		assertTrue(create.contains(auditColumn));
	}
	
	@Test
	void renderTableRebuildWithCompositePrimaryKey() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "items");
		SqlColumn<Object, Integer> first = table.column("a", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, Integer> second = table.column("b", SqlTypes.INTEGER, object -> 0, builder -> builder.primaryKey());
		table.compositePrimaryKey(first, second);
		List<SqlRendered> statements = RENDERER.renderTableRebuild(table, table.columns(), List.of());
		assertTrue(statements.get(2).sql().contains("PRIMARY KEY("));
	}
	
	@Test
	void renderTableRebuildWithExtraInlineConstraints() throws SqlException {
		SqlTable<Object> table = singlePrimaryKeyTable();
		List<SqlRendered> extra = List.of(SqlRendered.of("CHECK (\"id\" > 0)"), SqlRendered.of(""));
		List<SqlRendered> statements = RENDERER.renderTableRebuild(table, table.columns(), extra);
		assertTrue(statements.get(2).sql().contains("CHECK (\"id\" > 0)"));
	}
}
