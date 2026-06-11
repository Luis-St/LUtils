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

package net.luis.utils.io.database.dialect.renderer.expression;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnknownConstructException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlAbsFunction;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlExpressionRenderer}.<br>
 *
 * @author Luis-St
 */
class SqlExpressionRendererTest {
	
	private static final SqlExpressionRenderer RENDERER = new SqlExpressionRenderer(SqlDialects.DEFAULT);
	
	@Test
	void constructWithDialect() {
		assertNotNull(new SqlExpressionRenderer(SqlDialects.DEFAULT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlExpressionRenderer(null));
	}
	
	@Test
	void renderNullExpression() {
		assertThrows(NullPointerException.class, () -> RENDERER.render(null));
	}
	
	@Test
	void renderUnknownExpressionType() {
		SqlExpression<Object> unknown = new UnknownExpression();
		assertThrows(SqlDialectUnknownConstructException.class, () -> RENDERER.render(unknown));
	}
	
	@Test
	void renderOrderedNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderOrdered(null));
	}
	
	@Test
	void renderAliasedNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderAliased(null));
	}
	
	@Test
	void renderValueNullDirect() {
		assertThrows(NullPointerException.class, () -> RENDERER.renderValue(null));
	}
	
	@Test
	void renderValueExpression() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlValueExpression<>(5));
		assertNotNull(rendered);
		assertEquals("?", rendered.sql());
		assertEquals(1, rendered.parameters().size());
	}
	
	@Test
	void renderColumnExpression() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "users");
		SqlColumn<Object, Integer> column = table.column("id", SqlTypes.INTEGER, object -> 0);
		
		SqlRendered rendered = RENDERER.render(column);
		assertNotNull(rendered);
		assertTrue(rendered.sql().contains("\"id\""));
	}
	
	@Test
	void renderFunctionExpression() throws SqlException {
		SqlAbsFunction<Integer> function = new SqlAbsFunction<>(new SqlValueExpression<>(5));
		
		SqlRendered rendered = RENDERER.render(function);
		assertNotNull(rendered);
		assertTrue(rendered.sql().contains("ABS"));
	}
	
	@Test
	void renderOrderedExpression() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlValueExpression<>(5).ascending());
		assertNotNull(rendered);
		assertTrue(rendered.sql().contains("ASC"));
		assertTrue(rendered.sql().contains("?"));
	}
	
	@Test
	void renderAliasedExpression() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlValueExpression<>(5).as(SqlAlias.of("x")));
		assertNotNull(rendered);
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains("\"x\""));
	}
	
	@Test
	void renderValueWithExplicitType() throws SqlException {
		SqlRendered rendered = RENDERER.render(new SqlValueExpression<>(5, SqlTypes.INTEGER));
		assertNotNull(rendered);
		assertEquals("?", rendered.sql());
	}
	
	@Test
	void renderOrderedAliasedNested() throws SqlException {
		SqlExpression<Integer> aliased = new SqlValueExpression<>(5).as(SqlAlias.of("x"));
		SqlRendered rendered = RENDERER.render(aliased.ascending());
		assertNotNull(rendered);
		assertTrue(rendered.sql().contains("?"));
		assertTrue(rendered.sql().contains("AS"));
		assertTrue(rendered.sql().contains("\"x\""));
		assertTrue(rendered.sql().contains("ASC"));
	}
	
	private static final class UnknownExpression implements SqlExpression<Object> {
		
		@Override
		public @NonNull SqlType<Object> type() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) {
			throw new UnsupportedOperationException();
		}
	}
}
