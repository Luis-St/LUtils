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

package net.luis.utils.io.database.util;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.expression.SqlExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCaseWhenBranch}.<br>
 *
 * @author Luis-St
 */
class SqlCaseWhenBranchTest {
	
	@Test
	void constructWithConditionAndExpression() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlCaseWhenBranch<String> branch = new SqlCaseWhenBranch<>(condition, expression);
		assertSame(condition, branch.condition());
		assertSame(expression, branch.expression());
	}
	
	@Test
	void constructWithNullCondition() {
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		assertThrows(NullPointerException.class, () -> new SqlCaseWhenBranch<>(null, expression));
	}
	
	@Test
	void constructWithNullExpression() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		assertThrows(NullPointerException.class, () -> new SqlCaseWhenBranch<>(condition, null));
	}
	
	@Test
	void accessorsReturnComponents() {
		SqlCondition condition = SqlTestFixtures.neverCondition();
		SqlExpression<Integer> expression = SqlTestFixtures.integerExpression();
		SqlCaseWhenBranch<Integer> branch = new SqlCaseWhenBranch<>(condition, expression);
		assertSame(condition, branch.condition());
		assertSame(expression, branch.expression());
	}
	
	@Test
	void equalsAndHashCodeForEqualBranches() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlCaseWhenBranch<String> a = new SqlCaseWhenBranch<>(condition, expression);
		SqlCaseWhenBranch<String> b = new SqlCaseWhenBranch<>(condition, expression);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void notEqualWhenConditionOrExpressionDiffers() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlCaseWhenBranch<String> base = new SqlCaseWhenBranch<>(condition, expression);
		assertNotEquals(base, new SqlCaseWhenBranch<>(SqlTestFixtures.neverCondition(), expression));
		assertNotEquals(base, new SqlCaseWhenBranch<>(condition, SqlTestFixtures.integerExpression()));
	}
	
	@Test
	void toStringContainsComponents() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlExpression<String> expression = SqlTestFixtures.stringExpression();
		SqlCaseWhenBranch<String> branch = new SqlCaseWhenBranch<>(condition, expression);
		String string = branch.toString();
		assertTrue(string.contains(condition.toString()));
		assertTrue(string.contains(expression.toString()));
	}
}
