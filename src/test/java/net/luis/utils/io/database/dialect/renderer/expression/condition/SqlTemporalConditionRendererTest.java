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

package net.luis.utils.io.database.dialect.renderer.expression.condition;

import net.luis.utils.io.database.condition.conditions.SqlTemporalCondition;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTemporalConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlTemporalConditionRendererTest {
	
	private static final SqlTemporalConditionRenderer RENDERER = new SqlTemporalConditionRenderer(SqlDialects.DEFAULT, new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT));
	
	@Test
	void constructWithDialectAndTemporalRenderer() {
		assertNotNull(new SqlTemporalConditionRenderer(SqlDialects.DEFAULT, new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT)));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalConditionRenderer(null, new SqlTemporalFunctionRenderer(SqlDialects.DEFAULT)));
	}
	
	@Test
	void constructWithNullTemporalRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlTemporalConditionRenderer(SqlDialects.DEFAULT, null));
	}
	
	@Test
	void renderNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownTemporalType() {
		SqlTemporalCondition unknown = new SqlTemporalCondition() {};
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderAfterNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAfter(null));
	}
	
	@Test
	void renderBeforeNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderBefore(null));
	}
	
	@Test
	void renderWithinLastNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderWithinLast(null));
	}
	
	@Test
	void renderWithinNextNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderWithinNext(null));
	}
	
	@Test
	void renderAfterCondition() throws SqlException {
		SqlAfterCondition condition = new SqlAfterCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(1));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains(">"));
	}
	
	@Test
	void renderBeforeCondition() throws SqlException {
		SqlBeforeCondition condition = new SqlBeforeCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(9));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("<"));
	}
	
	@Test
	void renderWithinLastCondition() throws SqlException {
		SqlWithinLastCondition condition = new SqlWithinLastCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(30));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains(">="));
		assertTrue(rendered.sql().contains("CURRENT_TIMESTAMP"));
		assertTrue(rendered.sql().contains("-"));
		assertTrue(rendered.sql().contains("INTERVAL"));
	}
	
	@Test
	void renderWithinNextCondition() throws SqlException {
		SqlWithinNextCondition condition = new SqlWithinNextCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(30));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("<="));
		assertTrue(rendered.sql().contains("CURRENT_TIMESTAMP"));
		assertTrue(rendered.sql().contains("+"));
		assertTrue(rendered.sql().contains("INTERVAL"));
	}
	
	@Test
	void renderWithinLastUsesSecondPart() throws SqlException {
		SqlWithinLastCondition condition = new SqlWithinLastCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(30));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("SECOND"));
	}
}
