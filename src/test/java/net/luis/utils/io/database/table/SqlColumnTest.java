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

import java.util.List;
import java.util.Optional;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlColumn}.<br>
 *
 * @author Luis-St
 */
class SqlColumnTest {
	
	private static SqlColumnBuilder<Object, Integer> intBuilder(SqlTable<Object> table, String name, int index) {
		return new SqlColumnBuilder<>(table, name, index, INTEGER_TYPE, object -> 0);
	}
	
	private static SqlColumn<Object, Integer> intColumn(SqlTable<Object> table, String name, int index) {
		return intBuilder(table, name, index).build();
	}
	
	@Test
	void constructWithValidArguments() {
		SqlColumn<Object, Integer> column = new SqlColumn<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of(alwaysCondition()));
		assertEquals("id", column.name());
		assertEquals(1, column.index());
		assertEquals(INTEGER_TYPE, column.type());
		assertEquals(1, column.checks().size());
	}
	
	@Test
	void constructWithNullOwningTable() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(null, "id", 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(sampleTable(), null, 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(sampleTable(), "id", 1, null, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithNullGetter() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(sampleTable(), "id", 1, INTEGER_TYPE, null, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithNullDefaultValue() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0, true, null, false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithNullForeignKey() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, null, List.of()));
	}
	
	@Test
	void constructWithNullChecks() {
		assertThrows(NullPointerException.class, () -> new SqlColumn<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), null));
	}
	
	@Test
	void constructWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlColumn<>(sampleTable(), " ", 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithIndexZero() {
		assertThrows(IllegalArgumentException.class, () -> new SqlColumn<>(sampleTable(), "id", 0, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructWithNegativeIndex() {
		assertThrows(IllegalArgumentException.class, () -> new SqlColumn<>(sampleTable(), "id", -1, INTEGER_TYPE, object -> 0, true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void constructAutoIncrementNonNumericType() {
		assertThrows(IllegalArgumentException.class, () -> new SqlColumn<>(sampleTable(), "name", 1, STRING_TYPE, object -> "x", true, Optional.empty(), true, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void toSqlWithNullDialect() {
		assertThrows(NullPointerException.class, () -> intColumn(sampleTable(), "id", 1).toSql(null));
	}
	
	@Test
	void constructWithIndexOne() {
		assertDoesNotThrow(() -> intColumn(sampleTable(), "id", 1));
	}
	
	@Test
	void constructAutoIncrementNumericType() {
		SqlColumn<Object, Integer> column = new SqlColumn<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0, true, Optional.empty(), true, false, false, Optional.empty(), List.of());
		assertTrue(column.autoIncrement());
	}
	
	@Test
	void constructAutoIncrementFalseNonNumeric() {
		assertDoesNotThrow(() -> new SqlColumn<>(sampleTable(), "name", 1, STRING_TYPE, object -> "x", true, Optional.empty(), false, false, false, Optional.empty(), List.of()));
	}
	
	@Test
	void toSqlPublicSchemaOmitsPrefix() throws SqlException {
		SqlRendered rendered = intColumn(sampleTable(), "id", 1).toSql(DIALECT);
		assertEquals("\"test_table\".\"id\"", rendered.sql());
	}
	
	@Test
	void toSqlCustomSchemaAddsPrefix() throws SqlException {
		SqlTable<Object> table = SqlTable.create(Object.class, "t", "custom");
		SqlRendered rendered = intColumn(table, "id", 1).toSql(DIALECT);
		assertEquals("\"custom\".\"t\".\"id\"", rendered.sql());
	}
	
	@Test
	void equalsWithNonColumnObject() {
		assertNotEquals("string", intColumn(sampleTable(), "id", 1));
		assertNotEquals(intColumn(sampleTable(), "id", 1), new Object());
	}
	
	@Test
	void equalsWithEqualColumns() {
		SqlColumn<Object, Integer> first = intColumn(sampleTable(), "id", 1);
		SqlColumn<Object, Integer> second = intColumn(sampleTable(), "id", 1);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsDifferentIndex() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intColumn(sampleTable(), "id", 2));
	}
	
	@Test
	void equalsDifferentUnique() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intBuilder(sampleTable(), "id", 1).unique().build());
	}
	
	@Test
	void equalsDifferentNullable() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intBuilder(sampleTable(), "id", 1).notNull().build());
	}
	
	@Test
	void equalsDifferentPrimaryKey() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intBuilder(sampleTable(), "id", 1).primaryKey().build());
	}
	
	@Test
	void equalsDifferentAutoIncrement() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intBuilder(sampleTable(), "id", 1).autoIncrement().build());
	}
	
	@Test
	void equalsDifferentName() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intColumn(sampleTable(), "other", 1));
	}
	
	@Test
	void equalsDifferentType() {
		SqlColumn<Object, String> stringColumn = new SqlColumnBuilder<>(sampleTable(), "id", 1, STRING_TYPE, object -> "x").build();
		assertNotEquals(intColumn(sampleTable(), "id", 1), stringColumn);
	}
	
	@Test
	void equalsDifferentSchema() {
		SqlColumn<Object, Integer> custom = intColumn(SqlTable.create(Object.class, "test_table", "custom"), "id", 1);
		assertNotEquals(intColumn(sampleTable(), "id", 1), custom);
	}
	
	@Test
	void equalsDifferentTableName() {
		SqlColumn<Object, Integer> other = intColumn(SqlTable.create(Object.class, "other"), "id", 1);
		assertNotEquals(intColumn(sampleTable(), "id", 1), other);
	}
	
	@Test
	void equalsDifferentDefaultValue() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intBuilder(sampleTable(), "id", 1).defaultValue(1).build());
	}
	
	@Test
	void equalsDifferentChecks() {
		assertNotEquals(intColumn(sampleTable(), "id", 1), intBuilder(sampleTable(), "id", 1).addConstraint(alwaysCondition()).build());
	}
	
	@Test
	void equalsDifferentForeignKey() {
		SqlTable<Object> refTable = SqlTable.create(Object.class, "ref_table");
		SqlColumn<Object, ?> refColumn = refTable.column("ref", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> withForeignKey = intBuilder(sampleTable(), "id", 1).foreignKey(refTable, refColumn).build();
		assertNotEquals(intColumn(sampleTable(), "id", 1), withForeignKey);
	}
	
	@Test
	void typeReturnsType() {
		assertEquals(INTEGER_TYPE, intColumn(sampleTable(), "id", 1).type());
	}
	
	@Test
	void ofCreatesAliasedColumn() {
		SqlColumn<Object, Integer> column = intColumn(sampleTable(), "id", 1);
		SqlAliasedColumn<?, ?> aliased = assertInstanceOf(SqlAliasedColumn.class, column.of(SqlAlias.of("a")));
		assertSame(column, aliased.column());
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		SqlColumn<Object, Integer> first = intColumn(sampleTable(), "id", 1);
		SqlColumn<Object, Integer> second = intColumn(sampleTable(), "id", 1);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first.hashCode(), intColumn(sampleTable(), "other", 1).hashCode());
	}
	
	@Test
	void checksAreUnmodifiable() {
		SqlColumn<Object, Integer> column = intBuilder(sampleTable(), "id", 1).addConstraint(alwaysCondition()).build();
		assertThrows(UnsupportedOperationException.class, () -> column.checks().add(neverCondition()));
	}
	
	@Test
	void fullyConfiguredColumnAccessors() {
		SqlTable<Object> refTable = SqlTable.create(Object.class, "ref_table");
		SqlColumn<Object, ?> refColumn = refTable.column("ref", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> column = intBuilder(sampleTable(), "id", 1)
			.defaultValue(1)
			.unique()
			.primaryKey()
			.foreignKey(refTable, refColumn)
			.addConstraint(alwaysCondition())
			.addConstraint(neverCondition())
			.build();
		assertEquals(Optional.of(1), column.defaultValue());
		assertTrue(column.unique());
		assertTrue(column.primaryKey());
		assertTrue(column.foreignKey().isPresent());
		assertEquals(alwaysCondition(), column.checks().get(0));
		assertEquals(neverCondition(), column.checks().get(1));
	}
	
	@Test
	void equalsIgnoresGetterIdentity() {
		SqlColumn<Object, Integer> first = new SqlColumnBuilder<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0).build();
		SqlColumn<Object, Integer> second = new SqlColumnBuilder<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 42).build();
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
