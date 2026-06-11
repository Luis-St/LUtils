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

package net.luis.utils.io.database.query.row;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.expression.SqlAliasedExpression;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.table.SqlAliasedColumn;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRowMapper}.<br>
 *
 * @author Luis-St
 */
class SqlRowMapperTest {
	
	@Test
	void forExpressionsWithNullRowType() {
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forExpressions(null, List.of(integerExpression()), DIALECT));
	}
	
	@Test
	void forExpressionsWithNullExpressions() {
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forExpressions(SqlRow2.class, null, DIALECT));
	}
	
	@Test
	void forExpressionsWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forExpressions(SqlRow2.class, List.of(integerExpression()), null));
	}
	
	@Test
	void forProjectionWithNullTargetType() {
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forProjection(null, List.of(stringExpression()), DIALECT));
	}
	
	@Test
	void forProjectionWithNullExpressions() {
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forProjection(Person.class, null, DIALECT));
	}
	
	@Test
	void forProjectionWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forProjection(Person.class, List.of(stringExpression()), null));
	}
	
	@Test
	void forExpressionsWithNullExpressionElement() {
		List<SqlExpression<?>> expressions = Arrays.asList(integerExpression(), null);
		assertThrows(NullPointerException.class, () -> SqlRowMapper.forExpressions(SqlRow2.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsOrdinalAccessorOutOfRange() {
		List<SqlExpression<?>> expressions = List.of(integerExpression(), stringExpression());
		assertThrows(IllegalArgumentException.class, () -> SqlRowMapper.forExpressions(SqlRow3.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsNonOrdinalAccessorWithoutMatch() {
		List<SqlExpression<?>> expressions = List.of(integerExpression());
		assertThrows(IllegalArgumentException.class, () -> SqlRowMapper.forExpressions(NameView.class, expressions, DIALECT));
	}
	
	@Test
	void forProjectionWithNonRecordNonInterface() {
		List<SqlExpression<?>> expressions = List.of(stringExpression());
		assertThrows(IllegalArgumentException.class, () -> SqlRowMapper.forProjection(String.class, expressions, DIALECT));
	}
	
	@Test
	void forRecordProjectionPositionalFallbackOutOfRange() {
		List<SqlExpression<?>> expressions = List.of(integerExpression(), stringExpression());
		assertThrows(IllegalArgumentException.class, () -> SqlRowMapper.forProjection(Trio.class, expressions, DIALECT));
	}
	
	@Test
	void forInterfaceProjectionMethodWithoutMatch() {
		List<SqlExpression<?>> expressions = List.of(stringColumn());
		assertThrows(IllegalArgumentException.class, () -> SqlRowMapper.forProjection(BadView.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsBuildsOrdinalMapper() {
		List<SqlExpression<?>> expressions = List.of(integerExpression(), stringExpression());
		assertNotNull(SqlRowMapper.forExpressions(SqlRow2.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsResolvesColumnName() {
		List<SqlExpression<?>> expressions = List.of(integerColumn(), stringColumn());
		assertNotNull(SqlRowMapper.forExpressions(SqlRow2.class, expressions, DIALECT));
	}
	
	@Test
	void forProjectionRecordByName() {
		List<SqlExpression<?>> expressions = List.of(integerColumn(), stringColumn());
		assertNotNull(SqlRowMapper.forProjection(Person.class, expressions, DIALECT));
	}
	
	@Test
	void forProjectionRecordPositionalFallback() {
		List<SqlExpression<?>> expressions = List.of(integerExpression(), stringExpression());
		assertNotNull(SqlRowMapper.forProjection(Person.class, expressions, DIALECT));
	}
	
	@Test
	void forProjectionInterfaceByName() {
		List<SqlExpression<?>> expressions = List.of(stringColumn());
		assertNotNull(SqlRowMapper.forProjection(NameView.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsNonOrdinalAccessorWithMatch() {
		List<SqlExpression<?>> expressions = List.of(stringColumn());
		assertNotNull(SqlRowMapper.forExpressions(NameView.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsResolvesAliasedExpressionName() {
		SqlAliasedExpression<String> aliased = new SqlAliasedExpression<>(stringExpression(), SqlAlias.of("name"));
		List<SqlExpression<?>> expressions = List.of(aliased);
		assertNotNull(SqlRowMapper.forProjection(NameView.class, expressions, DIALECT));
	}
	
	@Test
	void forExpressionsResolvesAliasedColumnName() {
		SqlAliasedColumn<Object, String> aliased = new SqlAliasedColumn<>(stringColumn(), SqlAlias.of("alias"));
		List<SqlExpression<?>> expressions = List.of(aliased);
		assertNotNull(SqlRowMapper.forProjection(NameView.class, expressions, DIALECT));
	}
	
	@Test
	void forProjectionRecordMixedNameAndPositional() {
		List<SqlExpression<?>> expressions = List.of(stringColumn(), integerExpression());
		assertNotNull(SqlRowMapper.forProjection(Person.class, expressions, DIALECT));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void forExpressionsOrdinalMapperReadsRow() throws SqlException {
		List<SqlExpression<?>> expressions = List.of(integerExpression(), stringExpression());
		ThrowableFunction<ResultSet, SqlRow2<Integer, String>, SqlException> mapper =
			SqlRowMapper.forExpressions((Class<SqlRow2<Integer, String>>) (Class<?>) SqlRow2.class, expressions, DIALECT);
		
		SqlRow2<Integer, String> row = mapper.apply(resultRow(1, "a"));
		assertEquals(1, row.first());
		assertEquals("a", row.second());
	}
	
	@Test
	void forRecordProjectionConstructsRecord() throws SqlException {
		List<SqlExpression<?>> expressions = List.of(integerColumn(), stringColumn());
		ThrowableFunction<ResultSet, Person, SqlException> mapper = SqlRowMapper.forProjection(Person.class, expressions, DIALECT);
		
		Person person = mapper.apply(resultRow(7, "luis"));
		assertEquals(7, person.id());
		assertEquals("luis", person.name());
	}
	
	@Test
	void forRecordProjectionWrapsConstructorFailure() {
		List<SqlExpression<?>> expressions = List.of(integerExpression());
		ThrowableFunction<ResultSet, Validated, SqlException> mapper = SqlRowMapper.forProjection(Validated.class, expressions, DIALECT);
		
		assertThrows(SqlResultMappingException.class, () -> mapper.apply(resultRow(-1)));
	}
	
	@Test
	void forInterfaceProjectionConstructsProxy() throws SqlException {
		List<SqlExpression<?>> expressions = List.of(stringColumn());
		ThrowableFunction<ResultSet, NameView, SqlException> mapper = SqlRowMapper.forProjection(NameView.class, expressions, DIALECT);
		
		NameView view = mapper.apply(resultRow("luis"));
		assertEquals("luis", view.name());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void proxyObjectMethodsAndAccessors() throws SqlException {
		List<SqlExpression<?>> expressions = List.of(integerExpression(), stringExpression());
		ThrowableFunction<ResultSet, SqlRow2<Integer, String>, SqlException> mapper =
			SqlRowMapper.forExpressions((Class<SqlRow2<Integer, String>>) (Class<?>) SqlRow2.class, expressions, DIALECT);
		
		SqlRow2<Integer, String> row = mapper.apply(resultRow(1, "x"));
		SqlRow2<Integer, String> sameValues = mapper.apply(resultRow(1, "x"));
		Object same = row;
		
		assertEquals(row, same);
		assertNotEquals(row, sameValues);
		assertEquals(row.hashCode(), sameValues.hashCode());
		assertEquals("SqlRow[1, x]", row.toString());
		assertEquals(1, row.first());
		assertEquals("x", row.second());
	}
	
	@Test
	void proxyUnknownMethodThrows() throws SqlException {
		List<SqlExpression<?>> expressions = List.of(stringColumn());
		ThrowableFunction<ResultSet, Defaulted, SqlException> mapper = SqlRowMapper.forProjection(Defaulted.class, expressions, DIALECT);
		
		Defaulted view = mapper.apply(resultRow("a"));
		assertEquals("a", view.name());
		assertThrows(UnsupportedOperationException.class, view::greeting);
		assertThrows(UnsupportedOperationException.class, () -> view.prefixed("p"));
	}
	
	private interface NameView {
		
		String name();
	}
	
	private interface BadView {
		
		String missing();
	}
	
	private interface Defaulted {
		
		String name();
		
		default String greeting() {
			return "hi " + this.name();
		}
		
		default String prefixed(String prefix) {
			return prefix + this.name();
		}
	}
	
	private record Person(int id, String name) {}
	
	private record Trio(int a, int b, int c) {}
	
	private record Validated(int id) {
		
		private Validated {
			if (id < 0) {
				throw new IllegalArgumentException("id must not be negative");
			}
		}
	}
}
