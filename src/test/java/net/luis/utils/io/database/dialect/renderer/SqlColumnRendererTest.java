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

package net.luis.utils.io.database.dialect.renderer;

import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlColumnRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlColumnRendererTest {
	
	private static final SqlColumnRenderer RENDERER = new SqlColumnRenderer(SqlDialects.DEFAULT);
	
	private static SqlColumn<Object, Integer> ageColumn() {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		return table.column("age", SqlTypes.INTEGER, object -> 0);
	}
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlColumnRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlColumnRenderer(null));
	}
	
	@Test
	void renderAlterColumnTypeWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnType(null, SqlTypes.LONG));
	}
	
	@Test
	void renderAlterColumnTypeWithNullType() {
		SqlColumn<Object, Integer> column = ageColumn();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnType(column, null));
	}
	
	@Test
	void renderAlterColumnNullabilityWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnNullability(null, true));
	}
	
	@Test
	void renderAlterColumnSetDefaultWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnSetDefault(null, "0"));
	}
	
	@Test
	void renderAlterColumnSetDefaultWithNullRenderedDefault() {
		SqlColumn<Object, Integer> column = ageColumn();
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnSetDefault(column, null));
	}
	
	@Test
	void renderAlterColumnDropDefaultWithNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAlterColumnDropDefault(null));
	}
	
	@Test
	void renderAlterColumnNullabilityWhenNullable() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnNullability(ageColumn(), true);
		assertTrue(rendered.sql().contains("DROP"));
		assertTrue(rendered.sql().contains("NOT NULL"));
		assertFalse(rendered.sql().contains("SET"));
	}
	
	@Test
	void renderAlterColumnNullabilityWhenNotNullable() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnNullability(ageColumn(), false);
		assertTrue(rendered.sql().contains("SET"));
		assertTrue(rendered.sql().contains("NOT NULL"));
		assertFalse(rendered.sql().contains("DROP"));
	}
	
	@Test
	void renderAlterColumnTypeProducesAlterStatement() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnType(ageColumn(), SqlTypes.LONG);
		assertTrue(rendered.sql().contains("ALTER TABLE"));
		assertTrue(rendered.sql().contains("\"users\""));
		assertTrue(rendered.sql().contains("ALTER COLUMN"));
		assertTrue(rendered.sql().contains("\"age\""));
		assertTrue(rendered.sql().contains("TYPE"));
		assertTrue(rendered.sql().contains(SqlDialects.DEFAULT.getTypeName(SqlTypes.LONG)));
	}
	
	@Test
	void renderAlterColumnSetDefaultProducesSetClause() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnSetDefault(ageColumn(), "0");
		assertTrue(rendered.sql().contains("SET DEFAULT"));
		assertTrue(rendered.sql().endsWith("0"));
	}
	
	@Test
	void renderAlterColumnDropDefaultProducesDropClause() throws SqlException {
		SqlRendered rendered = RENDERER.renderAlterColumnDropDefault(ageColumn());
		assertTrue(rendered.sql().contains("\"users\""));
		assertTrue(rendered.sql().contains("\"age\""));
		assertTrue(rendered.sql().contains("DROP DEFAULT"));
	}
	
	@Test
	void renderAlterColumnTypeQuotesIdentifiersWithSpecialCharacters() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "weird\"name");
		SqlColumn<Object, Integer> column = table.column("co\"l", SqlTypes.INTEGER, object -> 0);
		SqlRendered rendered = RENDERER.renderAlterColumnType(column, SqlTypes.LONG);
		assertTrue(rendered.sql().contains("\"weird\"\"name\""));
		assertTrue(rendered.sql().contains("\"co\"\"l\""));
	}
}
