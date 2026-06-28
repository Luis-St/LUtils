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

package net.luis.utils.io.database.function.functions.generic;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCaseWhenFunction}.<br>
 *
 * @author Luis-St
 */
class SqlCaseWhenFunctionTest {
	
	private static SqlCaseWhenBranch<String> stringBranch() {
		return new SqlCaseWhenBranch<>(SqlTestFixtures.alwaysCondition(), SqlTestFixtures.stringExpression());
	}
	
	@Test
	void constructWithSingleBranchAndElse() {
		SqlCaseWhenBranch<String> branch = stringBranch();
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(branch), SqlTestFixtures.stringExpression());
		assertEquals(1, function.branches().size());
		assertNotNull(function.elseValue());
		assertEquals(branch.expression().type(), function.type());
	}
	
	@Test
	void constructWithNullElseValue() {
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(stringBranch()), null);
		assertNull(function.elseValue());
	}
	
	@Test
	void constructWithNullBranches() {
		assertThrows(NullPointerException.class, () -> new SqlCaseWhenFunction<String>(null, null));
	}
	
	@Test
	void constructWithEmptyBranches() {
		assertThrows(IllegalArgumentException.class, () -> new SqlCaseWhenFunction<String>(List.of(), null));
	}
	
	@Test
	void constructWithNullBranchInList() {
		List<SqlCaseWhenBranch<String>> branches = Arrays.asList(stringBranch(), null);
		assertThrows(IllegalArgumentException.class, () -> new SqlCaseWhenFunction<>(branches, null));
	}
	
	@Test
	void constructWithMismatchedBranchTypes() {
		@SuppressWarnings("unchecked")
		SqlCaseWhenBranch<String> integerBranch = (SqlCaseWhenBranch<String>) (SqlCaseWhenBranch<?>) new SqlCaseWhenBranch<>(SqlTestFixtures.alwaysCondition(), SqlTestFixtures.integerExpression());
		List<SqlCaseWhenBranch<String>> branches = List.of(stringBranch(), integerBranch);
		assertThrows(IllegalArgumentException.class, () -> new SqlCaseWhenFunction<>(branches, null));
	}
	
	@Test
	void constructWithMismatchedElseType() {
		@SuppressWarnings("unchecked")
		SqlExpression<String> integerElse = (SqlExpression<String>) (SqlExpression<?>) SqlTestFixtures.integerExpression();
		assertThrows(IllegalArgumentException.class, () -> new SqlCaseWhenFunction<>(List.of(stringBranch()), integerElse));
	}
	
	@Test
	void constructWithMatchingMultipleBranches() {
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(stringBranch(), stringBranch(), stringBranch()), null);
		assertEquals(3, function.branches().size());
	}
	
	@Test
	void elseValueTypeCheckSkippedWhenNull() {
		assertDoesNotThrow(() -> new SqlCaseWhenFunction<>(List.of(stringBranch()), null));
	}
	
	@Test
	void typeReturnsFirstBranchType() {
		SqlCaseWhenBranch<String> branch = stringBranch();
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(branch), null);
		assertEquals(branch.expression().type(), function.type());
	}
	
	@Test
	void branchesListIsUnmodifiable() {
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(stringBranch()), null);
		assertThrows(UnsupportedOperationException.class, () -> function.branches().add(stringBranch()));
	}
	
	@Test
	void requiresCastDefaultsFalse() {
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(stringBranch()), null);
		assertFalse(function.requiresCast());
	}
	
	@Test
	void branchesCopyIsolatedFromSource() {
		List<SqlCaseWhenBranch<String>> source = new ArrayList<>(List.of(stringBranch()));
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(source, null);
		source.add(stringBranch());
		assertEquals(1, function.branches().size());
	}
	
	@Test
	void toSqlWithNullDialect() {
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(stringBranch()), null);
		assertThrows(NullPointerException.class, () -> function.toSql(null));
	}
	
	@Test
	void toSqlRendersWithDefaultDialect() throws SqlException {
		SqlCaseWhenFunction<String> function = new SqlCaseWhenFunction<>(List.of(stringBranch()), SqlTestFixtures.stringExpression());
		String sql = function.toSql(SqlTestFixtures.DIALECT).sql();
		assertTrue(sql.contains("CASE"));
		assertTrue(sql.contains("WHEN"));
		assertTrue(sql.contains("THEN"));
		assertTrue(sql.contains("END"));
	}
}
