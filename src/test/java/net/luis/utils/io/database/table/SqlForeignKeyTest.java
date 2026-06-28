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

import net.luis.utils.io.database.SqlReferentialAction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlForeignKey}.<br>
 *
 * @author Luis-St
 */
class SqlForeignKeyTest {
	
	private static SqlTable<Object> referencedTable() {
		return SqlTable.create(Object.class, "ref_table");
	}
	
	private static SqlColumn<Object, ?> primaryKeyColumn(SqlTable<Object> table) {
		return table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
	}
	
	@Test
	void constructWithValidArguments() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = new SqlForeignKey<>(ref, List.of(column), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		assertSame(ref, foreignKey.referencedTable());
		assertEquals(1, foreignKey.referencedColumns().size());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onUpdate());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onDelete());
	}
	
	@Test
	void constructWithNullReferencedTable() {
		SqlColumn<Object, ?> column = primaryKeyColumn(referencedTable());
		assertThrows(NullPointerException.class, () -> new SqlForeignKey<>(null, List.of(column), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullReferencedColumns() {
		SqlTable<Object> ref = referencedTable();
		assertThrows(NullPointerException.class, () -> new SqlForeignKey<>(ref, null, SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullOnUpdate() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		assertThrows(NullPointerException.class, () -> new SqlForeignKey<>(ref, List.of(column), null, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullOnDelete() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		assertThrows(NullPointerException.class, () -> new SqlForeignKey<>(ref, List.of(column), SqlReferentialAction.NO_ACTION, null));
	}
	
	@Test
	void constructWithEmptyColumns() {
		SqlTable<Object> ref = referencedTable();
		assertThrows(IllegalArgumentException.class, () -> new SqlForeignKey<>(ref, List.of(), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithColumnFromDifferentTable() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> foreign = integerColumn();
		assertThrows(IllegalArgumentException.class, () -> new SqlForeignKey<>(ref, List.of(foreign), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void ofTableWithNullTable() {
		assertThrows(NullPointerException.class, () -> SqlForeignKey.of(null));
	}
	
	@Test
	void ofTableAndColumnWithNullColumn() {
		SqlTable<Object> ref = referencedTable();
		assertThrows(NullPointerException.class, () -> SqlForeignKey.of(ref, null));
	}
	
	@Test
	void ofTableWithoutPrimaryKey() {
		SqlTable<Object> ref = referencedTable();
		ref.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(IllegalArgumentException.class, () -> SqlForeignKey.of(ref));
	}
	
	@Test
	void constructWithColumnFromReferencedTable() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = new SqlForeignKey<>(ref, List.of(column), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		assertTrue(foreignKey.referencedColumns().contains(column));
	}
	
	@Test
	void ofTableUsesPrimaryKeyColumns() {
		SqlTable<Object> ref = referencedTable();
		primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = SqlForeignKey.of(ref);
		assertEquals(ref.primaryKeyColumns(), foreignKey.referencedColumns());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onUpdate());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onDelete());
	}
	
	@Test
	void ofTableAndColumnUsesSingleColumn() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = SqlForeignKey.of(ref, column);
		assertEquals(List.of(column), foreignKey.referencedColumns());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onUpdate());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onDelete());
	}
	
	@Test
	void ofFullArgumentsUsesProvidedActions() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = SqlForeignKey.of(ref, List.of(column), SqlReferentialAction.CASCADE, SqlReferentialAction.SET_NULL);
		assertEquals(SqlReferentialAction.CASCADE, foreignKey.onUpdate());
		assertEquals(SqlReferentialAction.SET_NULL, foreignKey.onDelete());
		assertEquals(List.of(column), foreignKey.referencedColumns());
	}
	
	@Test
	void accessorsReturnComponents() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = new SqlForeignKey<>(ref, List.of(column), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION);
		assertSame(ref, foreignKey.referencedTable());
		assertEquals(List.of(column), foreignKey.referencedColumns());
		assertEquals(SqlReferentialAction.CASCADE, foreignKey.onUpdate());
		assertEquals(SqlReferentialAction.NO_ACTION, foreignKey.onDelete());
	}
	
	@Test
	void referencedColumnsAreUnmodifiable() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> foreignKey = new SqlForeignKey<>(ref, List.of(column), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		assertThrows(UnsupportedOperationException.class, () -> foreignKey.referencedColumns().add(column));
	}
	
	@Test
	void multiColumnForeignKey() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> first = ref.column("id", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, ?> second = ref.column("name", STRING_TYPE, object -> "x");
		SqlForeignKey<Object> foreignKey = new SqlForeignKey<>(ref, List.of(first, second), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION);
		assertEquals(2, foreignKey.referencedColumns().size());
		assertSame(first, foreignKey.referencedColumns().get(0));
		assertSame(second, foreignKey.referencedColumns().get(1));
	}
	
	@Test
	void multiColumnMixedOwnershipThrows() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> owned = ref.column("id", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, ?> foreign = integerColumn();
		assertThrows(IllegalArgumentException.class, () -> new SqlForeignKey<>(ref, List.of(owned, foreign), SqlReferentialAction.NO_ACTION, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void equalsAndHashCodeForEqualValues() {
		SqlTable<Object> ref = referencedTable();
		SqlColumn<Object, ?> column = primaryKeyColumn(ref);
		SqlForeignKey<Object> first = SqlForeignKey.of(ref, column);
		SqlForeignKey<Object> second = SqlForeignKey.of(ref, column);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, SqlForeignKey.of(ref, List.of(column), SqlReferentialAction.NO_ACTION, SqlReferentialAction.CASCADE));
	}
}
