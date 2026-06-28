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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationColumnBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationColumnBuilderTest {
	
	@Test
	void buildWithDefaults() {
		SqlColumnOptions options = new SqlMigrationColumnBuilder<>().build();
		assertFalse(options.notNull());
		assertFalse(options.unique());
		assertFalse(options.autoIncrement());
		assertTrue(options.defaultValue().isEmpty());
		assertNull(options.referencesTable());
		assertNull(options.check());
	}
	
	@Test
	void defaultValueWithNull() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationColumnBuilder<>().defaultValue(null));
	}
	
	@Test
	void referencesWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationColumnBuilder<>().references(null));
	}
	
	@Test
	void checkWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationColumnBuilder<>().check(null));
	}
	
	@Test
	void notNullSetsFlag() {
		assertTrue(new SqlMigrationColumnBuilder<>().notNull().build().notNull());
	}
	
	@Test
	void uniqueSetsFlag() {
		assertTrue(new SqlMigrationColumnBuilder<>().unique().build().unique());
	}
	
	@Test
	void autoIncrementSetsFlag() {
		assertTrue(new SqlMigrationColumnBuilder<>().autoIncrement().build().autoIncrement());
	}
	
	@Test
	void defaultValueSetsPresentOptional() {
		SqlColumnOptions options = new SqlMigrationColumnBuilder<String>().defaultValue("d").build();
		assertTrue(options.defaultValue().isPresent());
		assertEquals("d", options.defaultValue().orElseThrow());
	}
	
	@Test
	void referencesSetsTable() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		assertSame(table, new SqlMigrationColumnBuilder<>().references(table).build().referencesTable());
	}
	
	@Test
	void checkSetsCondition() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		assertSame(condition, new SqlMigrationColumnBuilder<>().check(condition).build().check());
	}
	
	@Test
	void settersReturnSameInstance() {
		SqlMigrationColumnBuilder<String> builder = new SqlMigrationColumnBuilder<>();
		assertSame(builder, builder.notNull());
		assertSame(builder, builder.unique());
		assertSame(builder, builder.autoIncrement());
		assertSame(builder, builder.defaultValue("d"));
		assertSame(builder, builder.references(SqlTestFixtures.sampleTable()));
		assertSame(builder, builder.check(SqlTestFixtures.alwaysCondition()));
	}
	
	@Test
	void fullyConfiguredBuild() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlColumnOptions options = new SqlMigrationColumnBuilder<String>().notNull().unique().autoIncrement().defaultValue("d").references(table).check(condition).build();
		assertTrue(options.notNull());
		assertTrue(options.unique());
		assertTrue(options.autoIncrement());
		assertEquals("d", options.defaultValue().orElseThrow());
		assertSame(table, options.referencesTable());
		assertSame(condition, options.check());
	}
}
