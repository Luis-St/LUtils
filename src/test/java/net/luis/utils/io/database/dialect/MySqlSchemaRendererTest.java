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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlSchemaRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlSchemaRendererTest {
	
	private static final MySqlSchemaRenderer RENDERER = new MySqlSchemaRenderer(SqlDialects.MYSQL);
	
	@Test
	void renderCreateSchemaNullName() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderCreateSchema(null, false));
	}
	
	@Test
	void renderDropSchemaNullName() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderDropSchema(null, false, false));
	}
	
	@Test
	void renderCreateSchemaWithIfNotExists() throws SqlException {
		String sql = RENDERER.renderCreateSchema("app", true).sql();
		assertTrue(sql.contains("CREATE DATABASE"));
		assertTrue(sql.contains("IF NOT EXISTS"));
		assertTrue(sql.contains("`app`"));
	}
	
	@Test
	void renderCreateSchemaWithoutIfNotExists() throws SqlException {
		String sql = RENDERER.renderCreateSchema("app", false).sql();
		assertTrue(sql.contains("CREATE DATABASE"));
		assertTrue(sql.contains("`app`"));
		assertFalse(sql.contains("IF NOT EXISTS"));
	}
	
	@Test
	void renderDropSchemaWithIfExists() throws SqlException {
		String sql = RENDERER.renderDropSchema("app", true, false).sql();
		assertTrue(sql.contains("DROP DATABASE"));
		assertTrue(sql.contains("IF EXISTS"));
	}
	
	@Test
	void renderDropSchemaWithoutIfExists() throws SqlException {
		String sql = RENDERER.renderDropSchema("app", false, false).sql();
		assertTrue(sql.contains("DROP DATABASE"));
		assertTrue(sql.contains("`app`"));
		assertFalse(sql.contains("IF EXISTS"));
	}
	
	@Test
	void renderDropSchemaIgnoresCascade() throws SqlException {
		assertEquals(RENDERER.renderDropSchema("app", true, false).sql(), RENDERER.renderDropSchema("app", true, true).sql());
	}
}
