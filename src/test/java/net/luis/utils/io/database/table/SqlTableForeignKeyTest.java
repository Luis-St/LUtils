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

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTableForeignKey}.<br>
 *
 * @author Luis-St
 */
class SqlTableForeignKeyTest {
	
	private static SqlForeignKey<Object> sampleForeignKey() {
		SqlTable<Object> referencedTable = SqlTable.create(Object.class, "ref_table");
		SqlColumn<Object, Integer> referencedColumn = referencedTable.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		return SqlForeignKey.of(referencedTable, referencedColumn);
	}
	
	@Test
	void constructWithValidArguments() {
		SqlForeignKey<Object> foreignKey = sampleForeignKey();
		SqlColumn<Object, Integer> referencing = integerColumn();
		SqlTableForeignKey<Object, Object> tableForeignKey = new SqlTableForeignKey<>(List.of(referencing), foreignKey);
		assertEquals(1, tableForeignKey.getReferencingColumns().size());
		assertSame(referencing, tableForeignKey.getReferencingColumns().get(0));
		assertSame(foreignKey, tableForeignKey.getForeignKey());
	}
	
	@Test
	void constructWithNullReferencingColumns() {
		SqlForeignKey<Object> foreignKey = sampleForeignKey();
		assertThrows(NullPointerException.class, () -> new SqlTableForeignKey<>(null, foreignKey));
	}
	
	@Test
	void constructWithNullForeignKey() {
		assertThrows(NullPointerException.class, () -> new SqlTableForeignKey<>(List.of(integerColumn()), null));
	}
	
	@Test
	void constructWithEmptyReferencingColumns() {
		SqlForeignKey<Object> foreignKey = sampleForeignKey();
		assertThrows(IllegalArgumentException.class, () -> new SqlTableForeignKey<>(List.of(), foreignKey));
	}
	
	@Test
	void constructWithNonEmptyReferencingColumns() {
		SqlTableForeignKey<Object, Object> tableForeignKey = new SqlTableForeignKey<>(List.of(integerColumn()), sampleForeignKey());
		assertEquals(1, tableForeignKey.getReferencingColumns().size());
	}
	
	@Test
	void accessorsReturnComponents() {
		SqlForeignKey<Object> foreignKey = sampleForeignKey();
		SqlColumn<Object, Integer> referencing = integerColumn();
		SqlTableForeignKey<Object, Object> tableForeignKey = new SqlTableForeignKey<>(List.of(referencing), foreignKey);
		assertSame(referencing, tableForeignKey.getReferencingColumns().get(0));
		assertSame(foreignKey, tableForeignKey.getForeignKey());
	}
	
	@Test
	void referencingColumnsAreUnmodifiable() {
		SqlTableForeignKey<Object, Object> tableForeignKey = new SqlTableForeignKey<>(List.of(integerColumn()), sampleForeignKey());
		assertThrows(UnsupportedOperationException.class, () -> tableForeignKey.getReferencingColumns().add(stringColumn()));
	}
	
	@Test
	void multiColumnReferencingKey() {
		SqlColumn<Object, Integer> first = integerColumn();
		SqlColumn<Object, String> second = stringColumn();
		SqlTableForeignKey<Object, Object> tableForeignKey = new SqlTableForeignKey<>(List.of(first, second), sampleForeignKey());
		assertEquals(2, tableForeignKey.getReferencingColumns().size());
		assertSame(first, tableForeignKey.getReferencingColumns().get(0));
		assertSame(second, tableForeignKey.getReferencingColumns().get(1));
	}
	
	@Test
	void equalsAndHashCodeForEqualValues() {
		SqlForeignKey<Object> foreignKey = sampleForeignKey();
		SqlColumn<Object, Integer> referencing = integerColumn();
		SqlTableForeignKey<Object, Object> first = new SqlTableForeignKey<>(List.of(referencing), foreignKey);
		SqlTableForeignKey<Object, Object> second = new SqlTableForeignKey<>(List.of(referencing), foreignKey);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SqlTableForeignKey<>(List.of(stringColumn()), foreignKey));
	}
}
