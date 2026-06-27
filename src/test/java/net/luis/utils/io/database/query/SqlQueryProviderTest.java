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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.SqlSession;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.query.crud.SqlInsertQuery;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.query.row.SqlRow4;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlQueryProvider}.<br>
 *
 * @author Luis-St
 */
class SqlQueryProviderTest {
	
	private static SqlTable<Person> personTable() {
		SqlTable<Person> table = SqlTable.create(Person.class, "person");
		table.column("id", INTEGER_TYPE, Person::id);
		table.column("name", STRING_TYPE, Person::name);
		return table;
	}
	
	private static SqlQueryProvider<Person> personProvider() {
		return new SqlQueryProvider<>(personTable(), DIALECT, SOURCE, TIMEOUT);
	}
	
	private static long markerCount(String sql) {
		return sql.chars().filter(character -> character == '?').count();
	}
	
	@Test
	void constructWithFourArgs() throws SqlException {
		SqlQueryProvider<Person> provider = personProvider();
		assertNotNull(provider);
		assertTrue(provider.select().toSql(DIALECT).sql().contains("SELECT"));
	}
	
	@Test
	void constructWithSixArgsAndProviders() {
		SqlAuditUserProvider userProvider = () -> java.util.Optional.of("tester");
		assertNotNull(new SqlQueryProvider<>(personTable(), DIALECT, SOURCE, TIMEOUT, userProvider, null));
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlQueryProvider<>(null, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlQueryProvider<>(personTable(), null, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlQueryProvider<>(personTable(), DIALECT, null, TIMEOUT));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlQueryProvider<>(personTable(), DIALECT, SOURCE, null));
	}
	
	@Test
	void constructWithTypeWithoutMatchingConstructor() {
		SqlTable<Person> table = SqlTable.create(Person.class, "person");
		table.column("id", INTEGER_TYPE, Person::id);
		assertThrows(IllegalStateException.class, () -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithConstructorArityMatchButTypeMismatch() {
		SqlTable<Mismatch> table = SqlTable.create(Mismatch.class, "mismatch");
		table.column("value", INTEGER_TYPE, mismatch -> 0);
		assertThrows(IllegalStateException.class, () -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void selectVarargsWithNullArray() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.select((SqlExpression<?>[]) null));
	}
	
	@Test
	void selectVarargsWithNullElement() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.select(integerExpression(), null));
	}
	
	@Test
	void insertEntityWithNull() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.insert((Person) null));
	}
	
	@Test
	void insertArrayWithNull() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.insert((Person[]) null));
	}
	
	@Test
	void insertCollectionWithNull() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.insert((List<Person>) null));
	}
	
	@Test
	void insertWithNullConflictColumns() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.insert(new Person(1, "a"), (SqlColumn<Person, ?>[]) null));
	}
	
	@Test
	void insertFromSelectWithNullQuery() {
		SqlQueryProvider<Person> provider = personProvider();
		assertThrows(NullPointerException.class, () -> provider.insert((SqlSelectQuery<?>) null));
	}
	
	@Test
	void selectReturnsFullEntityQuery() throws SqlException {
		SqlSelectQuery<Person> query = personProvider().select();
		String sql = query.toSql(DIALECT).sql();
		assertTrue(sql.contains(DIALECT.quoteIdentifier("id")));
		assertTrue(sql.contains(DIALECT.quoteIdentifier("name")));
	}
	
	@Test
	void selectSingleExpressionReturnsScalarQuery() throws SqlException {
		SqlSelectQuery<Integer> query = personProvider().select(integerExpression());
		assertEquals(1, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectTwoExpressionsReturnsRow2Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(integerExpression(), stringExpression());
		assertEquals(2, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectSixteenExpressionsReturnsRow16Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(16, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectVarargsReturnsObjectArrayQuery() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(new SqlExpression<?>[] { integerExpression(), stringExpression() });
		assertEquals(2, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void subqueryDelegatesToSelect() throws SqlException {
		SqlQueryProvider<Person> provider = personProvider();
		SqlSelectQuery<?> subquery = provider.subquery(integerExpression(), stringExpression());
		SqlSelectQuery<?> select = provider.select(new SqlExpression<?>[] { integerExpression(), stringExpression() });
		assertEquals(select.toSql(DIALECT).sql(), subquery.toSql(DIALECT).sql());
	}
	
	@Test
	void insertEntityReturnsInsertQuery() throws SqlException {
		SqlInsertQuery<Person> query = personProvider().insert(new Person(1, "a"));
		assertTrue(query.toSql(DIALECT).sql().contains("INSERT INTO"));
	}
	
	@Test
	void insertVarargsReturnsInsertQuery() throws SqlException {
		SqlInsertQuery<Person> query = personProvider().insert(new Person(1, "a"), new Person(2, "b"));
		assertEquals(4, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void insertCollectionReturnsInsertQuery() throws SqlException {
		SqlInsertQuery<Person> query = personProvider().insert(List.of(new Person(1, "a")));
		assertTrue(query.toSql(DIALECT).sql().contains("INSERT INTO"));
	}
	
	@Test
	void insertWithConflictColumnsReturnsInsertOrIgnore() throws SqlException {
		SqlTable<Person> table = SqlTable.create(Person.class, "person");
		SqlColumn<Person, Integer> id = table.column("id", INTEGER_TYPE, Person::id);
		table.column("name", STRING_TYPE, Person::name);
		SqlQueryProvider<Person> provider = new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT);
		assertNotNull(provider.insert(new Person(1, "a"), id));
	}
	
	@Test
	void insertFromSelectReturnsInsertQuery() throws SqlException {
		SqlInsertQuery<Person> query = personProvider().insert(sampleSelect());
		assertTrue(query.toSql(DIALECT).sql().contains("INSERT INTO"));
	}
	
	@Test
	void updateReturnsUpdateQuery() {
		assertNotNull(personProvider().update());
	}
	
	@Test
	void deleteReturnsDeleteQuery() {
		assertNotNull(personProvider().delete());
	}
	
	@Test
	void selectArityOverloadsRenderAllExpressions() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(integerExpression(), integerExpression(), integerExpression());
		assertEquals(3, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void constructWithPrimitiveConstructorParams() throws SqlException {
		SqlTable<Counter> table = SqlTable.create(Counter.class, "counter");
		table.column("value", INTEGER_TYPE, Counter::value);
		SqlQueryProvider<Counter> provider = assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
		assertTrue(provider.select().toSql(DIALECT).sql().contains("SELECT"));
	}
	
	@Test
	void constructWithBoxedConstructorParams() {
		SqlTable<Boxed> table = SqlTable.create(Boxed.class, "boxed");
		table.column("value", INTEGER_TYPE, Boxed::value);
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void insertCollectionPreservesEntities() throws SqlException {
		SqlInsertQuery<Person> query = personProvider().insert(List.of(new Person(1, "a"), new Person(2, "b")));
		assertEquals(4, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void constructWithPrimitiveBooleanParam() {
		SqlTable<Flag> table = SqlTable.create(Flag.class, "flag");
		table.column("value", SqlTypes.BOOLEAN, Flag::value);
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithPrimitiveByteParam() {
		SqlTable<Tiny> table = SqlTable.create(Tiny.class, "tiny");
		table.column("value", SqlTypes.BYTE, Tiny::value);
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithPrimitiveShortParam() {
		SqlTable<Small> table = SqlTable.create(Small.class, "small");
		table.column("value", SqlTypes.SHORT, Small::value);
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithPrimitiveLongParam() {
		SqlTable<Big> table = SqlTable.create(Big.class, "big");
		table.column("value", SqlTypes.LONG, Big::value);
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithPrimitiveDoubleParam() {
		SqlTable<Decimal> table = SqlTable.create(Decimal.class, "decimal");
		table.column("value", SqlTypes.DOUBLE, Decimal::value);
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT));
	}
	
	@Test
	void constructWithSessionAndAuditedTableUsesAuditMapper() throws SqlException {
		SqlTable<Person> table = SqlTable.audited(Person.class, "person");
		table.column("id", INTEGER_TYPE, Person::id);
		table.column("name", STRING_TYPE, Person::name);
		SqlDatabase database = SqlDatabase.builder(failingDataSource(), DIALECT).build();
		SqlSession session = database.openSession();
		assertDoesNotThrow(() -> new SqlQueryProvider<>(table, DIALECT, SOURCE, TIMEOUT, null, session));
	}
	
	@Test
	void constructWithSessionButNonAuditedTableUsesEntityMapper() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(failingDataSource(), DIALECT).build();
		SqlSession session = database.openSession();
		assertDoesNotThrow(() -> new SqlQueryProvider<>(personTable(), DIALECT, SOURCE, TIMEOUT, null, session));
	}
	
	@Test
	void selectSingleExpressionWithNull() {
		assertThrows(NullPointerException.class, () -> personProvider().select((SqlExpression<Integer>) null));
	}
	
	@Test
	void selectHighArityWithNullElement() {
		assertThrows(NullPointerException.class, () -> personProvider().select(integerExpression(), integerExpression(), integerExpression(), null, integerExpression()));
	}
	
	@Test
	void selectFourExpressionsReturnsRow4Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(integerExpression(), integerExpression(), integerExpression(), integerExpression());
		assertEquals(4, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectFiveExpressionsReturnsRow5Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(5, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectSixExpressionsReturnsRow6Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression()
		);
		assertEquals(6, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectSevenExpressionsReturnsRow7Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression()
		);
		assertEquals(7, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectEightExpressionsReturnsRow8Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(8, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectNineExpressionsReturnsRow9Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(9, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectTenExpressionsReturnsRow10Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(10, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectElevenExpressionsReturnsRow11Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression()
		);
		assertEquals(11, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectTwelveExpressionsReturnsRow12Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression()
		);
		assertEquals(12, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectThirteenExpressionsReturnsRow13Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(13, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectFourteenExpressionsReturnsRow14Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(14, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectFifteenExpressionsReturnsRow15Query() throws SqlException {
		SqlSelectQuery<?> query = personProvider().select(
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression(),
			integerExpression(), integerExpression(), integerExpression(), integerExpression(), integerExpression()
		);
		assertEquals(15, markerCount(query.toSql(DIALECT).sql()));
	}
	
	@Test
	void selectArityOverloadReturnTypeIsSqlRow() throws SqlException {
		SqlSelectQuery<SqlRow4<Integer, String, Integer, String>> query = personProvider().select(integerExpression(), stringExpression(), integerExpression(), stringExpression());
		assertNotNull(query);
		assertEquals(4, markerCount(query.toSql(DIALECT).sql()));
	}
	
	private record Person(int id, String name) {}
	
	private record Counter(int value) {}
	
	private record Boxed(Integer value) {}
	
	private record Mismatch(String value) {}
	
	private record Flag(boolean value) {}
	
	private record Tiny(byte value) {}
	
	private record Small(short value) {}
	
	private record Big(long value) {}
	
	private record Decimal(double value) {}
}
