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

package net.luis.utils.io.database.expression;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTypedNestedExpression}.<br>
 *
 * @author Luis-St
 */
class SqlTypedNestedExpressionTest {
	
	@Test
	void typeDelegatesToWrappedExpression() {
		SqlExpression<String> wrapped = SqlTestFixtures.stringExpression();
		TestNested<String> nested = new TestNested<>(wrapped);
		assertSame(wrapped.type(), nested.type());
	}
	
	@Test
	void typeReflectsDifferentWrappedType() {
		SqlExpression<Integer> wrapped = SqlTestFixtures.integerExpression();
		TestNested<Integer> nested = new TestNested<>(wrapped);
		assertEquals(wrapped.type(), nested.type());
	}
	
	private record TestNested<T>(SqlExpression<T> expression) implements SqlTypedNestedExpression<T> {
		
		@Override
		public SqlRendered toSql(SqlDialect dialect) {
			return SqlRendered.of("test");
		}
	}
}
