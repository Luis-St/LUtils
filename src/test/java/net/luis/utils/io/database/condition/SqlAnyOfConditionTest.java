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

package net.luis.utils.io.database.condition;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.conditions.comparison.SqlIsNullCondition;
import net.luis.utils.io.database.condition.conditions.numeric.SqlIsZeroCondition;
import net.luis.utils.io.database.exception.SqlException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAnyOfCondition}.<br>
 *
 * @author Luis-St
 */
class SqlAnyOfConditionTest {
	
	@Test
	void constructWithTwoConditions() {
		SqlAnyOfCondition condition = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never()));
		assertEquals(2, condition.conditions().size());
		assertInstanceOf(SqlAlwaysCondition.class, condition.conditions().get(0));
		assertInstanceOf(SqlNeverCondition.class, condition.conditions().get(1));
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new SqlAnyOfCondition(null));
	}
	
	@Test
	void constructWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAnyOfCondition(List.of()));
	}
	
	@Test
	void constructWithSingleCondition() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAnyOfCondition(List.of(SqlCondition.always())));
	}
	
	@Test
	void constructWithNullConditionElement() {
		List<SqlCondition> conditions = Arrays.asList(SqlCondition.always(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlAnyOfCondition(conditions));
	}
	
	@Test
	void conditionsListIsUnmodifiable() {
		SqlAnyOfCondition condition = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never()));
		assertThrows(UnsupportedOperationException.class, () -> condition.conditions().add(SqlCondition.always()));
	}
	
	@Test
	void constructDefensivelyCopiesInput() {
		List<SqlCondition> source = new ArrayList<>(List.of(SqlCondition.always(), SqlCondition.never()));
		SqlAnyOfCondition condition = new SqlAnyOfCondition(source);
		source.add(SqlCondition.always());
		assertEquals(2, condition.conditions().size());
	}
	
	@Test
	void notAppliesDeMorgan() {
		SqlCondition isNull = new SqlIsNullCondition(SqlTestFixtures.stringExpression());
		SqlCondition isZero = new SqlIsZeroCondition(SqlTestFixtures.integerExpression());
		SqlCondition negated = new SqlAnyOfCondition(List.of(isNull, isZero)).not();
		assertInstanceOf(SqlAllOfCondition.class, negated);
		List<SqlCondition> children = ((SqlAllOfCondition) negated).conditions();
		assertInstanceOf(SqlNegatedCondition.class, children.get(0));
		assertInstanceOf(SqlNegatedCondition.class, children.get(1));
	}
	
	@Test
	void toSqlRendersOrGroup() throws SqlException {
		String sql = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never())).toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("OR"));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never())).toSql(null));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlAnyOfCondition first = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never()));
		SqlAnyOfCondition second = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never()));
		SqlAnyOfCondition different = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never(), SqlCondition.always()));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
	
	@Test
	void constructWithThreeConditions() throws SqlException {
		SqlAnyOfCondition condition = new SqlAnyOfCondition(List.of(SqlCondition.always(), SqlCondition.never(), SqlCondition.always()));
		assertEquals(3, condition.conditions().size());
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains("OR"));
	}
}
