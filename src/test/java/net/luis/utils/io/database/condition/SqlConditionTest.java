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
import net.luis.utils.io.database.exception.SqlException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCondition}.<br>
 *
 * @author Luis-St
 */
class SqlConditionTest {
	
	@Test
	void allOfVarargsWithNullFirst() {
		assertThrows(NullPointerException.class, () -> SqlCondition.allOf(null, SqlCondition.never()));
	}
	
	@Test
	void allOfVarargsWithNullSecond() {
		assertThrows(NullPointerException.class, () -> SqlCondition.allOf(SqlCondition.always(), null));
	}
	
	@Test
	void allOfVarargsWithNullOthersArray() {
		assertThrows(NullPointerException.class, () -> SqlCondition.allOf(SqlCondition.always(), SqlCondition.never(), (SqlCondition[]) null));
	}
	
	@Test
	void allOfVarargsWithNullOthersElement() {
		assertThrows(NullPointerException.class, () -> SqlCondition.allOf(SqlCondition.always(), SqlCondition.never(), (SqlCondition) null));
	}
	
	@Test
	void anyOfVarargsWithNullFirst() {
		assertThrows(NullPointerException.class, () -> SqlCondition.anyOf(null, SqlCondition.never()));
	}
	
	@Test
	void anyOfVarargsWithNullSecond() {
		assertThrows(NullPointerException.class, () -> SqlCondition.anyOf(SqlCondition.always(), null));
	}
	
	@Test
	void anyOfVarargsWithNullOthersArray() {
		assertThrows(NullPointerException.class, () -> SqlCondition.anyOf(SqlCondition.always(), SqlCondition.never(), (SqlCondition[]) null));
	}
	
	@Test
	void anyOfVarargsWithNullOthersElement() {
		assertThrows(NullPointerException.class, () -> SqlCondition.anyOf(SqlCondition.always(), SqlCondition.never(), (SqlCondition) null));
	}
	
	@Test
	void allOfListWithNull() {
		assertThrows(NullPointerException.class, () -> SqlCondition.allOf(null));
	}
	
	@Test
	void anyOfListWithNull() {
		assertThrows(NullPointerException.class, () -> SqlCondition.anyOf(null));
	}
	
	@Test
	void andWithNullOther() {
		assertThrows(NullPointerException.class, () -> SqlCondition.always().and(null));
	}
	
	@Test
	void orWithNullOther() {
		assertThrows(NullPointerException.class, () -> SqlCondition.always().or(null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlCondition.always().toSql(null));
	}
	
	@Test
	void alwaysReturnsAlwaysCondition() {
		assertInstanceOf(SqlAlwaysCondition.class, SqlCondition.always());
	}
	
	@Test
	void neverReturnsNeverCondition() {
		assertInstanceOf(SqlNeverCondition.class, SqlCondition.never());
	}
	
	@Test
	void allOfVarargsBuildsAllOfCondition() {
		SqlCondition condition = SqlCondition.allOf(SqlCondition.always(), SqlCondition.never());
		assertInstanceOf(SqlAllOfCondition.class, condition);
		assertEquals(2, ((SqlAllOfCondition) condition).conditions().size());
	}
	
	@Test
	void allOfVarargsIncludesOthers() {
		SqlCondition condition = SqlCondition.allOf(SqlCondition.always(), SqlCondition.never(), SqlCondition.always());
		assertInstanceOf(SqlAllOfCondition.class, condition);
		assertEquals(3, ((SqlAllOfCondition) condition).conditions().size());
	}
	
	@Test
	void allOfListWithSingleConditionReturnsElement() {
		SqlCondition condition = SqlCondition.allOf(List.of(SqlCondition.always()));
		assertInstanceOf(SqlAlwaysCondition.class, condition);
	}
	
	@Test
	void allOfListWithMultipleBuildsAllOfCondition() {
		SqlCondition condition = SqlCondition.allOf(List.of(SqlCondition.always(), SqlCondition.never()));
		assertInstanceOf(SqlAllOfCondition.class, condition);
		assertEquals(2, ((SqlAllOfCondition) condition).conditions().size());
	}
	
	@Test
	void anyOfVarargsBuildsAnyOfCondition() {
		SqlCondition condition = SqlCondition.anyOf(SqlCondition.always(), SqlCondition.never());
		assertInstanceOf(SqlAnyOfCondition.class, condition);
		assertEquals(2, ((SqlAnyOfCondition) condition).conditions().size());
	}
	
	@Test
	void anyOfVarargsIncludesOthers() {
		SqlCondition condition = SqlCondition.anyOf(SqlCondition.always(), SqlCondition.never(), SqlCondition.always());
		assertInstanceOf(SqlAnyOfCondition.class, condition);
		assertEquals(3, ((SqlAnyOfCondition) condition).conditions().size());
	}
	
	@Test
	void anyOfListWithSingleConditionReturnsElement() {
		SqlCondition condition = SqlCondition.anyOf(List.of(SqlCondition.always()));
		assertInstanceOf(SqlAlwaysCondition.class, condition);
	}
	
	@Test
	void anyOfListWithMultipleBuildsAnyOfCondition() {
		SqlCondition condition = SqlCondition.anyOf(List.of(SqlCondition.always(), SqlCondition.never()));
		assertInstanceOf(SqlAnyOfCondition.class, condition);
		assertEquals(2, ((SqlAnyOfCondition) condition).conditions().size());
	}
	
	@Test
	void andDelegatesToAllOf() {
		SqlCondition condition = SqlCondition.always().and(SqlCondition.never());
		assertInstanceOf(SqlAllOfCondition.class, condition);
		List<SqlCondition> conditions = ((SqlAllOfCondition) condition).conditions();
		assertInstanceOf(SqlAlwaysCondition.class, conditions.get(0));
		assertInstanceOf(SqlNeverCondition.class, conditions.get(1));
	}
	
	@Test
	void orDelegatesToAnyOf() {
		SqlCondition condition = SqlCondition.always().or(SqlCondition.never());
		assertInstanceOf(SqlAnyOfCondition.class, condition);
		List<SqlCondition> conditions = ((SqlAnyOfCondition) condition).conditions();
		assertInstanceOf(SqlAlwaysCondition.class, conditions.get(0));
		assertInstanceOf(SqlNeverCondition.class, conditions.get(1));
	}
	
	@Test
	void notWrapsInNegatedCondition() {
		SqlIsNullCondition receiver = new SqlIsNullCondition(SqlTestFixtures.stringExpression());
		SqlCondition negated = receiver.not();
		assertInstanceOf(SqlNegatedCondition.class, negated);
		assertSame(receiver, ((SqlNegatedCondition) negated).condition());
	}
	
	@Test
	void toSqlAlwaysRendersTrue() throws SqlException {
		assertEquals("TRUE", SqlCondition.always().toSql(SqlTestFixtures.DIALECT).sql());
	}
	
	@Test
	void toSqlNeverRendersFalse() throws SqlException {
		assertEquals("FALSE", SqlCondition.never().toSql(SqlTestFixtures.DIALECT).sql());
	}
	
	@Test
	void allOfVarargsWithManyOthers() {
		SqlCondition condition = SqlCondition.allOf(SqlCondition.always(), SqlCondition.never(), SqlCondition.always(), SqlCondition.never(), SqlCondition.always());
		assertInstanceOf(SqlAllOfCondition.class, condition);
		List<SqlCondition> conditions = ((SqlAllOfCondition) condition).conditions();
		assertEquals(5, conditions.size());
		assertInstanceOf(SqlAlwaysCondition.class, conditions.get(0));
		assertInstanceOf(SqlNeverCondition.class, conditions.get(1));
		assertInstanceOf(SqlAlwaysCondition.class, conditions.get(4));
	}
}
