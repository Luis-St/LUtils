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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlNegatedCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlIsNullCondition;
import net.luis.utils.io.database.condition.conditions.numeric.SqlIsZeroCondition;
import net.luis.utils.io.database.condition.conditions.string.SqlLikeCondition;
import net.luis.utils.io.database.condition.conditions.temporal.SqlAfterCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlConditionRendererTest {
	
	private static final SqlDialect DIALECT = SqlDialects.DEFAULT;
	private static final SqlConditionRenderer RENDERER = new SqlConditionRenderer(
		DIALECT,
		new SqlComparisonConditionRenderer(DIALECT),
		new SqlNumericConditionRenderer(DIALECT),
		new SqlStringConditionRenderer(DIALECT),
		new SqlTemporalConditionRenderer(DIALECT, new SqlTemporalFunctionRenderer(DIALECT))
	);
	
	private static int countOccurrences(String text, String needle) {
		int count = 0;
		int index = 0;
		while ((index = text.indexOf(needle, index)) != -1) {
			count++;
			index += needle.length();
		}
		return count;
	}
	
	@Test
	void constructWithRenderers() {
		assertNotNull(RENDERER);
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlConditionRenderer(
			null,
			new SqlComparisonConditionRenderer(DIALECT),
			new SqlNumericConditionRenderer(DIALECT),
			new SqlStringConditionRenderer(DIALECT),
			new SqlTemporalConditionRenderer(DIALECT, new SqlTemporalFunctionRenderer(DIALECT))
		));
	}
	
	@Test
	void constructWithNullComparisonRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlConditionRenderer(
			DIALECT,
			null,
			new SqlNumericConditionRenderer(DIALECT),
			new SqlStringConditionRenderer(DIALECT),
			new SqlTemporalConditionRenderer(DIALECT, new SqlTemporalFunctionRenderer(DIALECT))
		));
	}
	
	@Test
	void constructWithNullNumericRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlConditionRenderer(
			DIALECT,
			new SqlComparisonConditionRenderer(DIALECT),
			null,
			new SqlStringConditionRenderer(DIALECT),
			new SqlTemporalConditionRenderer(DIALECT, new SqlTemporalFunctionRenderer(DIALECT))
		));
	}
	
	@Test
	void constructWithNullStringRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlConditionRenderer(
			DIALECT,
			new SqlComparisonConditionRenderer(DIALECT),
			new SqlNumericConditionRenderer(DIALECT),
			null,
			new SqlTemporalConditionRenderer(DIALECT, new SqlTemporalFunctionRenderer(DIALECT))
		));
	}
	
	@Test
	void constructWithNullTemporalRenderer() {
		assertThrows(NullPointerException.class, () -> new SqlConditionRenderer(
			DIALECT,
			new SqlComparisonConditionRenderer(DIALECT),
			new SqlNumericConditionRenderer(DIALECT),
			new SqlStringConditionRenderer(DIALECT),
			null
		));
	}
	
	@Test
	void renderNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownConditionType() {
		SqlCondition unknown = new SqlCondition() {};
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderAlwaysCondition() throws SqlException {
		SqlRendered rendered = RENDERER.render(SqlCondition.always());
		assertEquals("TRUE", rendered.sql());
	}
	
	@Test
	void renderNeverCondition() throws SqlException {
		SqlRendered rendered = RENDERER.render(SqlCondition.never());
		assertEquals("FALSE", rendered.sql());
	}
	
	@Test
	void renderNegatedCondition() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlNegatedCondition(SqlCondition.always()));
		assertTrue(rendered.sql().contains("NOT"));
		assertTrue(rendered.sql().contains("(TRUE)"));
	}
	
	@Test
	void renderAllOfMultiple() throws SqlException {
		SqlCondition condition = SqlCondition.allOf(SqlCondition.always(), SqlCondition.never(), SqlCondition.always());
		SqlRendered rendered = RENDERER.render(condition);
		assertEquals(2, countOccurrences(rendered.sql(), "AND"));
		assertEquals(3, countOccurrences(rendered.sql(), "("));
	}
	
	@Test
	void renderAnyOfMultiple() throws SqlException {
		SqlCondition condition = SqlCondition.anyOf(SqlCondition.always(), SqlCondition.never(), SqlCondition.always());
		SqlRendered rendered = RENDERER.render(condition);
		assertEquals(2, countOccurrences(rendered.sql(), "OR"));
		assertEquals(3, countOccurrences(rendered.sql(), "("));
	}
	
	@Test
	void renderComparisonConditionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlIsNullCondition(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("IS NULL"));
	}
	
	@Test
	void renderNumericConditionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlIsZeroCondition(new SqlValueExpression<>(5)));
		assertTrue(rendered.sql().contains("= 0"));
	}
	
	@Test
	void renderStringConditionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlLikeCondition(new SqlValueExpression<>("name"), new SqlValueExpression<>("%a%")));
		assertTrue(rendered.sql().contains("LIKE"));
	}
	
	@Test
	void renderTemporalConditionDelegates() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlAfterCondition(new SqlValueExpression<>(5), new SqlValueExpression<>(1)));
		assertTrue(rendered.sql().contains(">"));
	}
	
	@Test
	void renderNestedAllOfAnyOf() throws SqlException {
		SqlCondition anyOf = SqlCondition.anyOf(SqlCondition.always(), SqlCondition.never());
		SqlCondition negated = new SqlNegatedCondition(anyOf);
		SqlCondition allOf = SqlCondition.allOf(SqlCondition.always(), negated);
		
		SqlRendered rendered = RENDERER.render(allOf);
		assertTrue(rendered.sql().contains("AND"));
		assertTrue(rendered.sql().contains("OR"));
		assertTrue(rendered.sql().contains("NOT"));
		assertEquals(countOccurrences(rendered.sql(), "("), countOccurrences(rendered.sql(), ")"));
	}
}
