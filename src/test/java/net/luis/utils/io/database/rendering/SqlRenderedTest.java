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

package net.luis.utils.io.database.rendering;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRendered}.<br>
 *
 * @author Luis-St
 */
class SqlRenderedTest {
	
	@Test
	void constructWithStatementsAndParameters() {
		Pair<SqlType<?>, Object> pair = Pair.of(SqlTestFixtures.INTEGER_TYPE, 1);
		SqlRendered rendered = new SqlRendered(List.of("SELECT", "?"), List.of(pair));
		assertEquals(List.of("SELECT", "?"), rendered.statements());
		assertEquals(List.of(pair), rendered.parameters());
	}
	
	@Test
	void constructWithNullStatements() {
		assertThrows(NullPointerException.class, () -> new SqlRendered(null, List.of()));
	}
	
	@Test
	void constructWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new SqlRendered(List.of("X"), null));
	}
	
	@Test
	void ofWithNullSql() {
		assertThrows(NullPointerException.class, () -> SqlRendered.of(null));
	}
	
	@Test
	void statementsListIsUnmodifiable() {
		SqlRendered rendered = SqlRendered.of("SELECT 1");
		assertThrows(UnsupportedOperationException.class, () -> rendered.statements().add("X"));
	}
	
	@Test
	void parametersListIsUnmodifiable() {
		Pair<SqlType<?>, Object> pair = Pair.of(SqlTestFixtures.INTEGER_TYPE, 1);
		SqlRendered rendered = new SqlRendered(List.of("?"), List.of(pair));
		assertThrows(UnsupportedOperationException.class, () -> rendered.parameters().add(pair));
	}
	
	@Test
	void ofProducesSingleStatementNoParameters() {
		SqlRendered rendered = SqlRendered.of("SELECT 1");
		assertEquals(List.of("SELECT 1"), rendered.statements());
		assertTrue(rendered.parameters().isEmpty());
		assertEquals("SELECT 1", rendered.sql());
	}
	
	@Test
	void sqlOfEmptyStatementsIsEmptyString() {
		SqlRendered rendered = new SqlRendered(List.of(), List.of());
		assertEquals("", rendered.sql());
	}
	
	@Test
	void sqlJoinsTokensWithSpaces() {
		SqlRendered rendered = new SqlRendered(List.of("SELECT", "*", "FROM", "users"), List.of());
		assertEquals("SELECT * FROM users", rendered.sql());
	}
	
	@Test
	void sqlNoSpaceBeforePunctuationTokens() {
		SqlRendered rendered = new SqlRendered(List.of("func", "(", "a", ",", "b", ")"), List.of());
		assertEquals("func(a, b)", rendered.sql());
	}
	
	@Test
	void sqlNoSpaceAfterOpeningBracket() {
		SqlRendered rendered = new SqlRendered(List.of("(", "x", ")"), List.of());
		assertEquals("(x)", rendered.sql());
	}
	
	@Test
	void sqlCachesResultAcrossCalls() {
		SqlRendered rendered = SqlRendered.of("SELECT 1");
		assertSame(rendered.sql(), rendered.sql());
	}
	
	@Test
	void equalsWithNonRenderedObject() {
		SqlRendered rendered = SqlRendered.of("SELECT 1");
		assertNotEquals(rendered, "SELECT 1");
		assertNotEquals(null, rendered);
	}
	
	@Test
	void equalsWithDifferentStatements() {
		SqlRendered a = new SqlRendered(List.of("A"), List.of());
		SqlRendered b = new SqlRendered(List.of("B"), List.of());
		assertNotEquals(a, b);
	}
	
	@Test
	void equalsWithDifferentParameters() {
		Pair<SqlType<?>, Object> first = Pair.of(SqlTestFixtures.INTEGER_TYPE, 1);
		Pair<SqlType<?>, Object> second = Pair.of(SqlTestFixtures.INTEGER_TYPE, 2);
		SqlRendered a = new SqlRendered(List.of("?"), List.of(first));
		SqlRendered b = new SqlRendered(List.of("?"), List.of(second));
		assertNotEquals(a, b);
	}
	
	@Test
	void equalsWithIdenticalContent() {
		Pair<SqlType<?>, Object> pair = Pair.of(SqlTestFixtures.INTEGER_TYPE, 1);
		SqlRendered a = new SqlRendered(List.of("?"), List.of(pair));
		SqlRendered b = new SqlRendered(List.of("?"), List.of(pair));
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void sqlSingleTokenHasNoSeparator() {
		SqlRendered rendered = new SqlRendered(List.of("SELECT"), List.of());
		assertEquals("SELECT", rendered.sql());
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		SqlRendered a = new SqlRendered(List.of("SELECT", "1"), List.of());
		SqlRendered b = new SqlRendered(List.of("SELECT", "1"), List.of());
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void toStringContainsStatementsAndParameters() {
		SqlRendered rendered = SqlRendered.of("SELECT 1");
		String string = rendered.toString();
		assertTrue(string.contains("statements="));
		assertTrue(string.contains("parameters="));
	}
	
	@Test
	void sqlWithNestedBracketsAndCommas() {
		SqlRendered rendered = new SqlRendered(List.of("SELECT", "COALESCE", "(", "a", ",", "b", ")", "FROM", "t"), List.of());
		assertEquals("SELECT COALESCE(a, b) FROM t", rendered.sql());
	}
	
	@Test
	void constructorCopiesDefendAgainstSourceMutation() {
		List<String> source = new ArrayList<>(List.of("SELECT", "X"));
		SqlRendered rendered = new SqlRendered(source, List.of());
		source.add("MUTATED");
		assertEquals(List.of("SELECT", "X"), rendered.statements());
	}
}
