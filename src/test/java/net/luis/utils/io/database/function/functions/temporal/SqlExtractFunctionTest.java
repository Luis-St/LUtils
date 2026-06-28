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

package net.luis.utils.io.database.function.functions.temporal;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlExtractFunction}.<br>
 *
 * @author Luis-St
 */
class SqlExtractFunctionTest {
	
	@Test
	void constructWithExpressionPartAndType() {
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlExtractFunction<Integer> function = new SqlExtractFunction<>(expression, SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE);
		assertSame(expression, function.expression());
		assertEquals(SqlTemporalPart.YEAR, function.part());
		assertEquals(SqlTestFixtures.INTEGER_TYPE, function.type());
	}
	
	@Test
	void constructWithNullExpression() {
		assertThrows(NullPointerException.class, () -> new SqlExtractFunction<>(null, SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullPart() {
		assertThrows(NullPointerException.class, () -> new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.YEAR, null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlExtractFunction<Integer> function = new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void requiresCastReturnsTrue() {
		SqlExtractFunction<Integer> function = new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE);
		assertTrue(function.requiresCast());
	}
	
	@Test
	void recordEqualityByComponents() {
		SqlExtractFunction<Integer> first = new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE);
		SqlExtractFunction<Integer> second = new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.YEAR, SqlTestFixtures.INTEGER_TYPE);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlExtractFunction<Integer> function = new SqlExtractFunction<>(SqlTestFixtures.integerExpression(), SqlTemporalPart.HOUR, SqlTestFixtures.INTEGER_TYPE);
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("EXTRACT("));
		assertTrue(sql.contains("FROM"));
	}
}
