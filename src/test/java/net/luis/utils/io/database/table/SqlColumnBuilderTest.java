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

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlColumnBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlColumnBuilderTest {
	
	private static SqlColumnBuilder<Object, Integer> builder() {
		return new SqlColumnBuilder<>(sampleTable(), "id", 1, INTEGER_TYPE, object -> 0);
	}
	
	@Test
	void constructWithValidArguments() {
		SqlColumn<Object, Integer> column = builder().build();
		assertNotNull(column);
		assertTrue(column.nullable());
		assertEquals(Optional.empty(), column.defaultValue());
		assertFalse(column.autoIncrement());
		assertFalse(column.unique());
		assertFalse(column.primaryKey());
		assertEquals(Optional.empty(), column.foreignKey());
		assertTrue(column.checks().isEmpty());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlColumnBuilder<>(null, "id", 1, INTEGER_TYPE, object -> 0));
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlColumnBuilder<>(sampleTable(), null, 1, INTEGER_TYPE, object -> 0));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlColumnBuilder<>(sampleTable(), "id", 1, null, object -> 0));
	}
	
	@Test
	void constructWithNullGetter() {
		assertThrows(NullPointerException.class, () -> new SqlColumnBuilder<>(sampleTable(), "id", 1, INTEGER_TYPE, null));
	}
	
	@Test
	void defaultValueWithNull() {
		assertThrows(NullPointerException.class, () -> builder().defaultValue(null));
	}
	
	@Test
	void addConstraintWithNull() {
		assertThrows(NullPointerException.class, () -> builder().addConstraint(null));
	}
	
	@Test
	void foreignKeyWithNullColumn() {
		SqlTable<Object> otherTable = SqlTable.create(Object.class, "other");
		assertThrows(NullPointerException.class, () -> builder().foreignKey(otherTable, null));
	}
	
	@Test
	void buildWithBlankNameThrows() {
		SqlColumnBuilder<Object, Integer> builder = new SqlColumnBuilder<>(sampleTable(), " ", 1, INTEGER_TYPE, object -> 0);
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildWithIndexZeroThrows() {
		SqlColumnBuilder<Object, Integer> builder = new SqlColumnBuilder<>(sampleTable(), "id", 0, INTEGER_TYPE, object -> 0);
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void buildAutoIncrementNonNumericThrows() {
		SqlColumnBuilder<Object, String> builder = new SqlColumnBuilder<>(sampleTable(), "name", 1, STRING_TYPE, object -> "x").autoIncrement();
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void notNullSetsColumnNotNullable() {
		assertFalse(builder().notNull().build().nullable());
	}
	
	@Test
	void defaultValueSetsPresentOptional() {
		assertEquals(Optional.of(5), builder().defaultValue(5).build().defaultValue());
	}
	
	@Test
	void autoIncrementSetsFlagWithNumericType() {
		assertTrue(builder().autoIncrement().build().autoIncrement());
	}
	
	@Test
	void uniqueSetsFlag() {
		assertTrue(builder().unique().build().unique());
	}
	
	@Test
	void primaryKeySetsFlagAndNotNullable() {
		SqlColumn<Object, Integer> column = builder().primaryKey().build();
		assertTrue(column.primaryKey());
		assertFalse(column.nullable());
	}
	
	@Test
	void addConstraintAppendsCheck() {
		SqlColumn<Object, Integer> column = builder().addConstraint(alwaysCondition()).build();
		assertEquals(1, column.checks().size());
		assertEquals(alwaysCondition(), column.checks().get(0));
	}
	
	@Test
	void foreignKeySetsForeignKeyPresent() {
		SqlTable<Object> refTable = SqlTable.create(Object.class, "ref_table");
		SqlColumn<Object, ?> refColumn = refTable.column("ref", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, Integer> column = builder().foreignKey(refTable, refColumn).build();
		assertTrue(column.foreignKey().isPresent());
	}
	
	@Test
	void defaultColumnHasEmptyForeignKey() {
		assertTrue(builder().build().foreignKey().isEmpty());
	}
	
	@Test
	void foreignKeySingleArgSetsForeignKeyFromPrimaryKey() {
		SqlTable<Object> refTable = SqlTable.create(Object.class, "ref_table");
		refTable.column("id", INTEGER_TYPE, object -> 0, configuration -> configuration.primaryKey());
		SqlColumn<Object, Integer> column = builder().foreignKey(refTable).build();
		assertTrue(column.foreignKey().isPresent());
		assertEquals(refTable.primaryKeyColumns(), column.foreignKey().get().referencedColumns());
	}
	
	@Test
	void eachSetterReturnsSameBuilder() {
		SqlColumnBuilder<Object, Integer> builder = builder();
		assertSame(builder, builder.notNull());
		assertSame(builder, builder.defaultValue(1));
		assertSame(builder, builder.autoIncrement());
		assertSame(builder, builder.unique());
		assertSame(builder, builder.primaryKey());
		assertSame(builder, builder.addConstraint(alwaysCondition()));
	}
	
	@Test
	void buildFullyConfiguredColumn() {
		SqlColumn<Object, Integer> column = builder()
			.notNull()
			.defaultValue(7)
			.unique()
			.autoIncrement()
			.addConstraint(alwaysCondition())
			.build();
		assertFalse(column.nullable());
		assertTrue(column.unique());
		assertTrue(column.autoIncrement());
		assertEquals(Optional.of(7), column.defaultValue());
		assertEquals(1, column.checks().size());
	}
	
	@Test
	void addMultipleConstraintsAccumulate() {
		SqlColumn<Object, Integer> column = builder()
			.addConstraint(alwaysCondition())
			.addConstraint(neverCondition())
			.build();
		assertEquals(2, column.checks().size());
		assertEquals(alwaysCondition(), column.checks().get(0));
		assertEquals(neverCondition(), column.checks().get(1));
	}
}
