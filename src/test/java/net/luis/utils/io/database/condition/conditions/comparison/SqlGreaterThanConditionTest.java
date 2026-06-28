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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlGreaterThanCondition}.<br>
 *
 * @author Luis-St
 */
class SqlGreaterThanConditionTest {
	
	@Test
	void constructStrict() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlExpression<?> threshold = SqlTestFixtures.integerExpression();
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(value, threshold, false);
		assertSame(value, condition.value());
		assertSame(threshold, condition.threshold());
		assertFalse(condition.equalTo());
	}
	
	@Test
	void constructOrEqual() {
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), true);
		assertTrue(condition.equalTo());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new SqlGreaterThanCondition(null, SqlTestFixtures.integerExpression(), false));
	}
	
	@Test
	void constructWithNullThreshold() {
		assertThrows(NullPointerException.class, () -> new SqlGreaterThanCondition(SqlTestFixtures.integerExpression(), null, false));
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), false);
		assertThrows(NullPointerException.class, () -> condition.toSql(null));
	}
	
	@Test
	void toSqlStrictRendersGreaterThan() throws SqlException {
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), false);
		String sql = condition.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains(">"));
		assertFalse(sql.contains(">="));
	}
	
	@Test
	void toSqlOrEqualRendersGreaterThanOrEqual() throws SqlException {
		SqlGreaterThanCondition condition = new SqlGreaterThanCondition(SqlTestFixtures.integerExpression(), SqlTestFixtures.integerExpression(), true);
		assertTrue(condition.toSql(SqlTestFixtures.DIALECT).sql().contains(">="));
	}
	
	@Test
	void equalsAndHashCode() {
		SqlExpression<?> value = SqlTestFixtures.integerExpression();
		SqlExpression<?> threshold = SqlTestFixtures.integerExpression();
		SqlGreaterThanCondition strict = new SqlGreaterThanCondition(value, threshold, false);
		SqlGreaterThanCondition sameStrict = new SqlGreaterThanCondition(value, threshold, false);
		SqlGreaterThanCondition orEqual = new SqlGreaterThanCondition(value, threshold, true);
		assertEquals(strict, sameStrict);
		assertEquals(strict.hashCode(), sameStrict.hashCode());
		assertNotEquals(strict, orEqual);
	}
}
