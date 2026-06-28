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

package net.luis.utils.io.database.condition.conditions.comparison;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlInListCondition}.<br>
 *
 * @author Luis-St
 */
class SqlInListConditionTest {
	
	private static List<SqlExpression<?>> options(int count) throws SqlException {
		List<SqlExpression<?>> options = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			options.add(new SqlValueExpression<>(i));
		}
		return options;
	}
	
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
	void constructWithOptions() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlInListCondition condition = new SqlInListCondition(value, List.of(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression()));
		assertSame(value, condition.value());
		assertEquals(2, condition.options().size());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlInListCondition(null, List.of(SqlTestFixtures.integerExpression())));
	}
	
	@Test
	void constructWithNullOptions() {
		assertThrows(NullPointerException.class, () -> new SqlInListCondition(SqlTestFixtures.integerExpression(), null));
	}
	
	@Test
	void constructWithEmptyOptions() {
		assertThrows(IllegalArgumentException.class, () -> new SqlInListCondition(SqlTestFixtures.integerExpression(), List.of()));
	}
	
	@Test
	void constructWithNullOptionElement() {
		List<SqlExpression<?>> options = Arrays.asList(SqlTestFixtures.integerExpression(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlInListCondition(SqlTestFixtures.integerExpression(), options));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), List.of(SqlTestFixtures.integerExpression()));
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void optionsListIsUnmodifiable() {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), List.of(SqlTestFixtures.integerExpression()));
		assertThrows(UnsupportedOperationException.class, () -> condition.options().add(SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void constructDefensivelyCopiesOptions() {
		List<SqlExpression<?>> source = new ArrayList<>(List.of(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression()));
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), source);
		source.add(SqlTestFixtures.integerExpression());
		assertEquals(2, condition.options().size());
	}
	
	@Test
	void toSqlSmallListRendersSingleIn() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), options(3));
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("IN("));
		assertFalse(sql.contains(" OR "));
	}
	
	@Test
	void toSqlListAtBatchSizeRendersSingleIn() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), options(1000));
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("IN("));
		assertFalse(sql.contains(" OR "));
	}
	
	@Test
	void toSqlListJustOverBatchSizeRendersTwoGroups() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), options(1001));
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.startsWith("("));
		assertEquals(1, countOccurrences(sql, " OR "));
	}
	
	@Test
	void toSqlSingleOptionRendersIn() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), options(1));
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("IN("));
		assertFalse(sql.contains(" OR "));
	}
	
	@Test
	void toSqlListSpanningThreeGroups() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), options(2500));
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.startsWith("("));
		assertEquals(2, countOccurrences(sql, " OR "));
	}
	
	@Test
	void toSqlListSpanningManyGroups() throws SqlException {
		SqlInListCondition condition = new SqlInListCondition(SqlTestFixtures.integerExpression(), options(5000));
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertEquals(4, countOccurrences(sql, " OR "));
	}
	
	@Test
	void equalsAndHashCode() {
		List<SqlExpression<?>> options = List.of(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression());
		SqlInListCondition first = new SqlInListCondition(SqlTestFixtures.integerExpression(), options);
		SqlInListCondition second = new SqlInListCondition(SqlTestFixtures.integerExpression(), options);
		SqlInListCondition different = new SqlInListCondition(SqlTestFixtures.integerExpression(), List.of(SqlTestFixtures.stringExpression(), SqlTestFixtures.stringExpression()));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
