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
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MySqlStringConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class MySqlStringConditionRendererTest {
	
	private static final MySqlStringConditionRenderer RENDERER = new MySqlStringConditionRenderer(SqlDialects.MYSQL);
	
	@Test
	void renderConcatExpressionNullRenderer() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcatExpression(null, SqlRendered.of("a"), SqlRendered.of("b")));
	}
	
	@Test
	void renderConcatExpressionNullLeft() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcatExpression(SqlRenderer.empty(), null, SqlRendered.of("b")));
	}
	
	@Test
	void renderConcatExpressionNullRight() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcatExpression(SqlRenderer.empty(), SqlRendered.of("a"), null));
	}
	
	@Test
	void renderConcatExpressionProducesConcat() throws SqlException {
		String sql = RENDERER.renderConcatExpression(SqlRenderer.empty(), SqlRendered.of("a"), SqlRendered.of("b")).sql();
		assertTrue(sql.contains("CONCAT("));
	}
}
