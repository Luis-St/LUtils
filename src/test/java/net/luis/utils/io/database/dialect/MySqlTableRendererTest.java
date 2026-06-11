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
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlTableRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlTableRendererTest {
	
	private static final MySqlTableRenderer RENDERER = new MySqlTableRenderer(SqlDialects.MYSQL);
	
	@Test
	void renderAutoIncrementNullRenderer() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAutoIncrement(null, SqlTestFixtures.integerColumn()));
	}
	
	@Test
	void renderAutoIncrementNullColumn() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAutoIncrement(SqlRenderer.empty(), null));
	}
	
	@Test
	void renderAutoIncrementAppendsKeyword() throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		RENDERER.renderAutoIncrement(renderer, SqlTestFixtures.integerColumn());
		assertTrue(renderer.toSql().sql().contains("AUTO_INCREMENT"));
	}
	
	@Test
	void renderAutoIncrementKeywordValue() throws SqlException {
		assertEquals("AUTO_INCREMENT", RENDERER.renderAutoIncrementKeyword().sql());
	}
}
