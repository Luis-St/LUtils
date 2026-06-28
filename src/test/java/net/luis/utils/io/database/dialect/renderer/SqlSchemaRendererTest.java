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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSchemaRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlSchemaRendererTest {
	
	private static final SqlSchemaRenderer RENDERER = new SqlSchemaRenderer(SqlDialects.DEFAULT);
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlSchemaRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaRenderer(null));
	}
	
	@Test
	void renderCreateSchemaWithNullName() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCreateSchema(null, false));
	}
	
	@Test
	void renderDropSchemaWithNullName() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropSchema(null, false, false));
	}
	
	@Test
	void renderCreateSchemaWithoutIfNotExists() throws SqlException {
		SqlRendered rendered = RENDERER.renderCreateSchema("app", false);
		assertTrue(rendered.sql().contains("CREATE SCHEMA"));
		assertTrue(rendered.sql().contains("\"app\""));
		assertFalse(rendered.sql().contains("IF NOT EXISTS"));
	}
	
	@Test
	void renderCreateSchemaWithIfNotExists() throws SqlException {
		SqlRendered rendered = RENDERER.renderCreateSchema("app", true);
		assertTrue(rendered.sql().contains("IF NOT EXISTS"));
		assertTrue(rendered.sql().contains("\"app\""));
	}
	
	@Test
	void renderDropSchemaWithoutIfExistsWithoutCascade() throws SqlException {
		SqlRendered rendered = RENDERER.renderDropSchema("app", false, false);
		assertTrue(rendered.sql().contains("DROP SCHEMA"));
		assertTrue(rendered.sql().contains("\"app\""));
		assertFalse(rendered.sql().contains("IF EXISTS"));
		assertFalse(rendered.sql().contains("CASCADE"));
	}
	
	@Test
	void renderDropSchemaWithIfExists() throws SqlException {
		SqlRendered rendered = RENDERER.renderDropSchema("app", true, false);
		assertTrue(rendered.sql().contains("IF EXISTS"));
		assertFalse(rendered.sql().contains("CASCADE"));
	}
	
	@Test
	void renderDropSchemaWithCascade() throws SqlException {
		SqlRendered rendered = RENDERER.renderDropSchema("app", false, true);
		assertTrue(rendered.sql().contains("CASCADE"));
		assertFalse(rendered.sql().contains("IF EXISTS"));
		assertTrue(rendered.sql().indexOf("CASCADE") > rendered.sql().indexOf("\"app\""));
	}
	
	@Test
	void renderDropSchemaWithIfExistsAndCascade() throws SqlException {
		SqlRendered rendered = RENDERER.renderDropSchema("app", true, true);
		String sql = rendered.sql();
		assertTrue(sql.contains("DROP SCHEMA"));
		assertTrue(sql.contains("IF EXISTS"));
		assertTrue(sql.contains("\"app\""));
		assertTrue(sql.contains("CASCADE"));
		assertTrue(sql.indexOf("IF EXISTS") < sql.indexOf("\"app\""));
		assertTrue(sql.indexOf("\"app\"") < sql.indexOf("CASCADE"));
	}
	
	@Test
	void renderCreateSchemaQuotesSpecialCharacters() throws SqlException {
		SqlRendered rendered = RENDERER.renderCreateSchema("we\"ird", true);
		assertTrue(rendered.sql().contains("\"we\"\"ird\""));
	}
}
