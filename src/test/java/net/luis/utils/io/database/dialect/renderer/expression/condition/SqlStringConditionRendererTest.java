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

import net.luis.utils.io.database.condition.conditions.SqlStringCondition;
import net.luis.utils.io.database.condition.conditions.string.*;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlStringConditionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlStringConditionRendererTest {
	
	private static final SqlStringConditionRenderer RENDERER = new SqlStringConditionRenderer(SqlDialects.DEFAULT);
	
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
	void constructWithDialect() {
		assertNotNull(new SqlStringConditionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlStringConditionRenderer(null));
	}
	
	@Test
	void renderNullCondition() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownStringType() {
		SqlStringCondition unknown = new SqlStringCondition() {};
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderConcatExpressionWithNullRenderer() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcatExpression(null, SqlRendered.of("a"), SqlRendered.of("b")));
	}
	
	@Test
	void renderConcatExpressionWithNullLeft() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcatExpression(SqlRenderer.empty(), null, SqlRendered.of("b")));
	}
	
	@Test
	void renderConcatExpressionWithNullRight() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderConcatExpression(SqlRenderer.empty(), SqlRendered.of("a"), null));
	}
	
	@Test
	void renderContainsNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderContains(null));
	}
	
	@Test
	void renderEndsWithNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderEndsWith(null));
	}
	
	@Test
	void renderEqualsIgnoreCaseNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderEqualsIgnoreCase(null));
	}
	
	@Test
	void renderLikeNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderLike(null));
	}
	
	@Test
	void renderStartsWithNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderStartsWith(null));
	}
	
	@Test
	void renderContainsCondition() throws SqlException {
		SqlContainsCondition condition = new SqlContainsCondition(new SqlValueExpression<>("name"), new SqlValueExpression<>("a"));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("LIKE"));
		assertTrue(rendered.sql().contains("||"));
		assertEquals(2, countOccurrences(rendered.sql(), "'%'"));
	}
	
	@Test
	void renderEndsWithCondition() throws SqlException {
		SqlEndsWithCondition condition = new SqlEndsWithCondition(new SqlValueExpression<>("name"), new SqlValueExpression<>("x"));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("LIKE '%' ||"));
		assertEquals(1, countOccurrences(rendered.sql(), "'%'"));
	}
	
	@Test
	void renderEqualsIgnoreCaseCondition() throws SqlException {
		SqlEqualsIgnoreCaseCondition condition = new SqlEqualsIgnoreCaseCondition(new SqlValueExpression<>("a"), new SqlValueExpression<>("b"));
		SqlRendered rendered = RENDERER.render(condition);
		assertEquals(2, countOccurrences(rendered.sql(), "UPPER("));
		assertTrue(rendered.sql().contains("="));
	}
	
	@Test
	void renderLikeCondition() throws SqlException {
		SqlLikeCondition condition = new SqlLikeCondition(new SqlValueExpression<>("name"), new SqlValueExpression<>("%a%"));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("LIKE"));
		assertFalse(rendered.sql().contains("'%'"));
	}
	
	@Test
	void renderStartsWithCondition() throws SqlException {
		SqlStartsWithCondition condition = new SqlStartsWithCondition(new SqlValueExpression<>("name"), new SqlValueExpression<>("x"));
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("|| '%'"));
		assertEquals(1, countOccurrences(rendered.sql(), "'%'"));
	}
	
	@Test
	void renderConcatExpressionDirectly() throws SqlException {
		SqlRendered rendered = RENDERER.renderConcatExpression(SqlRenderer.empty(), SqlRendered.of("a"), SqlRendered.of("b"));
		assertEquals("a || b", rendered.sql());
	}
	
	@Test
	void renderContainsWithColumnSubstring() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, String> column = table.column("name", SqlTypes.TEXT, object -> "");
		
		SqlContainsCondition condition = new SqlContainsCondition(new SqlValueExpression<>("name"), column);
		SqlRendered rendered = RENDERER.render(condition);
		assertTrue(rendered.sql().contains("LIKE"));
		assertTrue(rendered.sql().contains("\"name\""));
		assertEquals(2, countOccurrences(rendered.sql(), "'%'"));
	}
}
