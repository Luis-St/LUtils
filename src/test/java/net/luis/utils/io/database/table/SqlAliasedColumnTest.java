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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlAlias;
import net.luis.utils.io.database.rendering.SqlRendered;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAliasedColumn}.<br>
 *
 * @author Luis-St
 */
class SqlAliasedColumnTest {
	
	@Test
	void constructWithValidArguments() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlAlias alias = SqlAlias.of("a");
		SqlAliasedColumn<Object, Integer> aliased = new SqlAliasedColumn<>(column, alias);
		assertNotNull(aliased);
		assertSame(column, aliased.column());
		assertSame(alias, aliased.alias());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAliasedColumn<>(null, SqlAlias.of("a")));
	}
	
	@Test
	void constructWithNullAlias() {
		assertThrows(NullPointerException.class, () -> new SqlAliasedColumn<>(integerColumn(), null));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlAliasedColumn<>(integerColumn(), SqlAlias.of("a")).toSql(null));
	}
	
	@Test
	void typeReturnsColumnType() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlAliasedColumn<Object, Integer> aliased = new SqlAliasedColumn<>(column, SqlAlias.of("a"));
		assertSame(column.type(), aliased.type());
		assertEquals(INTEGER_TYPE, aliased.type());
	}
	
	@Test
	void toSqlRendersAliasAndColumnName() throws SqlException {
		SqlAliasedColumn<Object, Integer> aliased = new SqlAliasedColumn<>(integerColumn(), SqlAlias.of("a"));
		SqlRendered rendered = aliased.toSql(DIALECT);
		assertEquals("\"a\".\"id\"", rendered.sql());
		assertTrue(rendered.parameters().isEmpty());
	}
	
	@Test
	void accessorsReturnComponents() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlAliasedColumn<Object, Integer> aliased = new SqlAliasedColumn<>(column, SqlAlias.of("a"));
		assertSame(column, aliased.column());
		assertEquals("a", aliased.alias().get());
	}
	
	@Test
	void equalsAndHashCodeForEqualValues() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlAliasedColumn<Object, Integer> first = new SqlAliasedColumn<>(column, SqlAlias.of("a"));
		SqlAliasedColumn<Object, Integer> second = new SqlAliasedColumn<>(column, SqlAlias.of("a"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SqlAliasedColumn<>(column, SqlAlias.of("b")));
	}
	
	@Test
	void toSqlWithStringColumn() throws SqlException {
		SqlAliasedColumn<Object, String> aliased = new SqlAliasedColumn<>(stringColumn(), SqlAlias.of("a"));
		SqlRendered rendered = aliased.toSql(DIALECT);
		assertEquals("\"a\".\"name\"", rendered.sql());
	}
}
