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
import net.luis.utils.io.database.audit.SqlAuditConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTable}.<br>
 *
 * @author Luis-St
 */
class SqlTableTest {
	
	//region Constructors / factories
	@Test
	void createWithDefaultSchema() {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		assertEquals("public", table.schema());
		assertEquals("t", table.name());
		assertFalse(table.isAudited());
	}
	
	@Test
	void createWithExplicitSchema() {
		assertEquals("custom", SqlTable.create(Object.class, "t", "custom").schema());
	}
	
	@Test
	void auditedWithDefaults() {
		SqlTable<Object> table = SqlTable.audited(Object.class, "t");
		assertTrue(table.isAudited());
		assertEquals("public", table.schema());
		assertEquals(SqlAuditConfig.DEFAULT, table.auditConfig().orElseThrow());
	}
	
	@Test
	void auditedWithSchema() {
		SqlTable<Object> table = SqlTable.audited(Object.class, "t", "custom");
		assertEquals("custom", table.schema());
		assertTrue(table.isAudited());
	}
	
	@Test
	void auditedWithConfig() {
		SqlTable<Object> table = SqlTable.audited(Object.class, "t", SqlAuditConfig.DEFAULT);
		assertEquals("public", table.schema());
		assertTrue(table.isAudited());
	}
	
	@Test
	void auditedWithSchemaAndConfig() {
		SqlTable<Object> table = SqlTable.audited(Object.class, "t", "custom", SqlAuditConfig.DEFAULT);
		assertEquals("custom", table.schema());
		assertTrue(table.isAudited());
	}
	//endregion
	
	//region Exceptions
	@Test
	void createWithNullType() {
		assertThrows(NullPointerException.class, () -> SqlTable.create(null, "t"));
	}
	
	@Test
	void createWithNullName() {
		assertThrows(NullPointerException.class, () -> SqlTable.create(Object.class, null));
	}
	
	@Test
	void createWithNullSchema() {
		assertThrows(NullPointerException.class, () -> SqlTable.create(Object.class, "t", null));
	}
	
	@Test
	void createWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> SqlTable.create(Object.class, " "));
	}
	
	@Test
	void createWithBlankSchema() {
		assertThrows(IllegalArgumentException.class, () -> SqlTable.create(Object.class, "t", " "));
	}
	
	@Test
	void auditedWithNullConfig() {
		assertThrows(NullPointerException.class, () -> SqlTable.audited(Object.class, "t", "public", null));
	}
	
	@Test
	void columnWithNullAction() {
		SqlTable<Object> table = sampleTable();
		assertThrows(NullPointerException.class, () -> table.column("id", INTEGER_TYPE, object -> 0, null));
	}
	
	@Test
	void columnWithDuplicateName() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(IllegalStateException.class, () -> table.column("id", INTEGER_TYPE, object -> 0));
	}
	
	@Test
	void columnReservedAuditNameCollision() {
		SqlTable<Object> table = SqlTable.audited(Object.class, "t");
		assertThrows(IllegalStateException.class, () -> table.column("version", STRING_TYPE, object -> "x"));
	}
	
	@Test
	void columnMultiplePrimaryKeysAllowedForComposite() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> first = table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, ?> second = table.column("id2", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		assertEquals(List.of(first, second), table.primaryKeyColumns());
		assertDoesNotThrow(() -> table.compositePrimaryKey(first, second));
		assertTrue(table.compositePrimaryKey().isPresent());
	}
	
	@Test
	void compositePrimaryKeyVarargsWithNullFirst() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> second = integerColumn();
		assertThrows(NullPointerException.class, () -> table.compositePrimaryKey(null, second));
	}
	
	@Test
	void compositePrimaryKeyVarargsWithNullSecond() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> first = integerColumn();
		assertThrows(NullPointerException.class, () -> table.compositePrimaryKey(first, null));
	}
	
	@Test
	void compositePrimaryKeyVarargsWithNullOthersArray() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> first = integerColumn();
		SqlColumn<Object, ?> second = stringColumn();
		assertThrows(NullPointerException.class, () -> table.compositePrimaryKey(first, second, (SqlColumn<Object, ?>[]) null));
	}
	
	@Test
	void compositePrimaryKeyVarargsWithNullOtherElement() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> first = integerColumn();
		SqlColumn<Object, ?> second = stringColumn();
		SqlColumn<Object, ?> third = integerColumn();
		assertThrows(NullPointerException.class, () -> table.compositePrimaryKey(first, second, third, null));
	}
	
	@Test
	void generateCompositePrimaryKeyWithoutPrimaryKeys() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(IllegalStateException.class, table::generateCompositePrimaryKey);
	}
	
	@Test
	void foreignKeyWithNullReferencedColumn() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> ref = SqlTable.create(Object.class, "ref");
		assertThrows(NullPointerException.class, () -> table.foreignKey(List.of(column), ref, (SqlColumn<Object, ?>) null));
	}
	
	@Test
	void foreignKeyWithReferencingColumnNotInTable() {
		SqlTable<Object> table = sampleTable();
		SqlTable<Object> ref = SqlTable.create(Object.class, "ref");
		SqlColumn<Object, ?> refColumn = ref.column("rid", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, ?> referencing = integerColumn();
		assertThrows(IllegalArgumentException.class, () -> table.foreignKey(List.of(referencing), ref, refColumn));
	}
	
	@Test
	void uniqueConstraintVarargsWithNull() {
		SqlTable<Object> table = sampleTable();
		assertThrows(NullPointerException.class, () -> table.uniqueConstraint((SqlColumn<Object, ?>[]) null));
	}
	
	@Test
	void uniqueConstraintListWithNull() {
		SqlTable<Object> table = sampleTable();
		assertThrows(NullPointerException.class, () -> table.uniqueConstraint((List<SqlColumn<Object, ?>>) null));
	}
	
	@Test
	void uniqueConstraintEmpty() {
		SqlTable<Object> table = sampleTable();
		assertThrows(IllegalArgumentException.class, () -> table.uniqueConstraint(List.of()));
	}
	
	@Test
	void uniqueConstraintWithNullElement() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		List<SqlColumn<Object, ?>> columns = Arrays.asList(column, null);
		assertThrows(NullPointerException.class, () -> table.uniqueConstraint(columns));
	}
	
	@Test
	void uniqueConstraintColumnNotInTable() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> foreign = integerColumn();
		assertThrows(IllegalArgumentException.class, () -> table.uniqueConstraint(List.of(foreign)));
	}
	
	@Test
	void checkConstraintWithNull() {
		SqlTable<Object> table = sampleTable();
		assertThrows(NullPointerException.class, () -> table.checkConstraint(null));
	}
	
	@Test
	void columnForIndexWithZero() {
		assertThrows(IllegalArgumentException.class, () -> sampleTable().columnForIndex(0));
	}
	
	@Test
	void columnForIndexWithNegative() {
		assertThrows(IllegalArgumentException.class, () -> sampleTable().columnForIndex(-1));
	}
	//endregion
	
	//region Branch coverage
	@Test
	void columnAssignsIncrementingIndexes() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> first = table.column("a", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, ?> second = table.column("b", INTEGER_TYPE, object -> 0);
		SqlColumn<Object, ?> third = table.column("c", INTEGER_TYPE, object -> 0);
		assertEquals(1, first.index());
		assertEquals(2, second.index());
		assertEquals(3, third.index());
		assertEquals(3, table.columns().size());
	}
	
	@Test
	void columnWithActionAppliesBuilder() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.notNull().unique());
		assertFalse(column.nullable());
		assertTrue(column.unique());
	}
	
	@Test
	void columnOnAuditedTableWithNonReservedName() {
		SqlTable<Object> table = SqlTable.audited(Object.class, "t");
		table.column("id", INTEGER_TYPE, object -> 0);
		assertEquals(1, table.columns().size());
	}
	
	@Test
	void columnSinglePrimaryKeyAllowed() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		assertEquals(1, table.primaryKeyColumns().size());
	}
	
	@Test
	void generateCompositePrimaryKeyWithSinglePrimaryKeyThrows() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		assertThrows(IllegalArgumentException.class, table::generateCompositePrimaryKey);
	}
	
	@Test
	void primaryKeyColumnsCollectedWhenNoComposite() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		assertEquals(List.of(column), table.primaryKeyColumns());
	}
	
	@Test
	void primaryKeyColumnsEmptyWhenNonePresent() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0);
		assertTrue(table.primaryKeyColumns().isEmpty());
	}
	
	@Test
	void columnForNameFound() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertSame(column, table.columnForName("id"));
	}
	
	@Test
	void columnForNameMissingReturnsNull() {
		assertNull(sampleTable().columnForName("missing"));
	}
	
	@Test
	void columnForNameWithNull() {
		assertThrows(NullPointerException.class, () -> sampleTable().columnForName(null));
	}
	
	@Test
	void columnForIndexFound() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		assertSame(column, table.columnForIndex(1));
	}
	
	@Test
	void columnForIndexMissingReturnsNull() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0);
		assertNull(table.columnForIndex(99));
	}
	
	@Test
	void isAuditedTrueForAuditedTable() {
		assertTrue(SqlTable.audited(Object.class, "t").isAudited());
	}
	
	@Test
	void isAuditedFalseForPlainTable() {
		assertFalse(SqlTable.create(Object.class, "t").isAudited());
	}
	
	@Test
	void auditConfigPresentForAudited() {
		assertTrue(SqlTable.audited(Object.class, "t").auditConfig().isPresent());
	}
	
	@Test
	void auditConfigEmptyForPlain() {
		assertTrue(SqlTable.create(Object.class, "t").auditConfig().isEmpty());
	}
	
	@Test
	void toStringPublicSchemaIsNameOnly() {
		assertEquals("t", SqlTable.create(Object.class, "t").toString());
	}
	
	@Test
	void toStringCustomSchemaIsQualified() {
		assertEquals("custom.t", SqlTable.create(Object.class, "t", "custom").toString());
	}
	
	@Test
	void uniqueConstraintAddedForValidColumns() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		table.uniqueConstraint(List.of(column));
		assertEquals(1, table.uniqueConstraints().size());
	}
	
	@Test
	void checkConstraintAdded() {
		SqlTable<Object> table = sampleTable();
		table.checkConstraint(alwaysCondition());
		assertEquals(1, table.checkConstraints().size());
	}
	
	@Test
	void foreignKeyAddedForValidColumns() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> ref = SqlTable.create(Object.class, "ref");
		SqlColumn<Object, ?> refColumn = ref.column("rid", INTEGER_TYPE, object -> 0);
		table.foreignKey(List.of(column), ref, refColumn);
		assertEquals(1, table.foreignKeys().size());
	}
	//endregion
	
	//region Simple inputs
	@Test
	void accessorsReflectConstruction() {
		SqlTable<Object> table = SqlTable.create(Object.class, "t", "custom");
		assertEquals(Object.class, table.type());
		assertEquals("t", table.name());
		assertEquals("custom", table.schema());
	}
	
	@Test
	void columnsReturnsUnmodifiableCopy() {
		SqlTable<Object> table = sampleTable();
		table.column("id", INTEGER_TYPE, object -> 0);
		assertThrows(UnsupportedOperationException.class, () -> table.columns().add(integerColumn()));
	}
	
	@Test
	void foreignKeysUniqueChecksReturnUnmodifiable() {
		SqlTable<Object> table = sampleTable();
		assertThrows(UnsupportedOperationException.class, () -> table.foreignKeys().add(null));
		assertThrows(UnsupportedOperationException.class, () -> table.uniqueConstraints().add(null));
		assertThrows(UnsupportedOperationException.class, () -> table.checkConstraints().add(alwaysCondition()));
	}
	
	@Test
	void uniqueConstraintVarargsDelegates() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		table.uniqueConstraint(column);
		assertEquals(1, table.uniqueConstraints().size());
	}
	//endregion
	
	//region Complex inputs
	@Test
	void buildTableWithColumnsConstraintsAndForeignKey() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> id = table.column("id", INTEGER_TYPE, object -> 0, builder -> builder.primaryKey());
		SqlColumn<Object, ?> name = table.column("name", STRING_TYPE, object -> "x");
		table.uniqueConstraint(List.of(name));
		table.checkConstraint(alwaysCondition());
		SqlTable<Object> ref = SqlTable.create(Object.class, "ref");
		SqlColumn<Object, ?> refColumn = ref.column("rid", INTEGER_TYPE, object -> 0);
		table.foreignKey(List.of(id), ref, refColumn);
		
		assertEquals(2, table.columns().size());
		assertEquals(List.of(id, name), table.columns());
		assertEquals(1, table.uniqueConstraints().size());
		assertEquals(1, table.checkConstraints().size());
		assertEquals(1, table.foreignKeys().size());
		assertEquals(List.of(id), table.primaryKeyColumns());
	}
	
	@Test
	void foreignKeyWithReferencedColumnsList() {
		SqlTable<Object> table = sampleTable();
		SqlColumn<Object, ?> column = table.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> ref = SqlTable.create(Object.class, "ref");
		SqlColumn<Object, ?> refColumn = ref.column("rid", INTEGER_TYPE, object -> 0);
		table.foreignKey(List.of(column), ref, List.of(refColumn));
		table.foreignKey(List.of(column), ref, List.of(refColumn), SqlReferentialAction.CASCADE, SqlReferentialAction.SET_NULL);
		
		assertEquals(2, table.foreignKeys().size());
		assertEquals(SqlReferentialAction.CASCADE, table.foreignKeys().get(1).getForeignKey().onUpdate());
		assertEquals(SqlReferentialAction.SET_NULL, table.foreignKeys().get(1).getForeignKey().onDelete());
	}
	
	@Test
	void equalsForIdenticallyBuiltTables() {
		SqlTable<Object> first = SqlTable.create(Object.class, "t");
		first.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> second = SqlTable.create(Object.class, "t");
		second.column("id", INTEGER_TYPE, object -> 0);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsWithNonTableObject() {
		SqlTable<Object> table = SqlTable.create(Object.class, "t");
		assertNotEquals("t", table);
		assertNotEquals(table, new Object());
	}
	
	@Test
	void notEqualsWhenNameDiffers() {
		assertNotEquals(SqlTable.create(Object.class, "t"), SqlTable.create(Object.class, "u"));
	}
	
	@Test
	void notEqualsWhenColumnsDiffer() {
		SqlTable<Object> first = SqlTable.create(Object.class, "t");
		first.column("id", INTEGER_TYPE, object -> 0);
		first.column("name", STRING_TYPE, object -> "x");
		SqlTable<Object> second = SqlTable.create(Object.class, "t");
		second.column("id", INTEGER_TYPE, object -> 0);
		assertNotEquals(first, second);
	}
	
	@Test
	void notEqualsWhenAuditConfigDiffers() {
		assertNotEquals(SqlTable.audited(Object.class, "t"), SqlTable.create(Object.class, "t"));
	}
	
	@Test
	void notEqualsWhenTypeDiffers() {
		assertNotEquals(SqlTable.create(Object.class, "t"), SqlTable.create(String.class, "t"));
	}
	
	@Test
	void notEqualsWhenSchemaDiffers() {
		assertNotEquals(SqlTable.create(Object.class, "t", "a"), SqlTable.create(Object.class, "t", "b"));
	}
	
	@Test
	void notEqualsWhenForeignKeysDiffer() {
		SqlTable<Object> first = SqlTable.create(Object.class, "t");
		SqlColumn<Object, ?> column = first.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> second = SqlTable.create(Object.class, "t");
		second.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> ref = SqlTable.create(Object.class, "ref");
		SqlColumn<Object, ?> refColumn = ref.column("rid", INTEGER_TYPE, object -> 0);
		first.foreignKey(List.of(column), ref, refColumn);
		assertNotEquals(first, second);
	}
	
	@Test
	void notEqualsWhenUniqueConstraintsDiffer() {
		SqlTable<Object> first = SqlTable.create(Object.class, "t");
		SqlColumn<Object, ?> column = first.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> second = SqlTable.create(Object.class, "t");
		second.column("id", INTEGER_TYPE, object -> 0);
		first.uniqueConstraint(List.of(column));
		assertNotEquals(first, second);
	}
	
	@Test
	void notEqualsWhenCheckConstraintsDiffer() {
		SqlTable<Object> first = SqlTable.create(Object.class, "t");
		first.column("id", INTEGER_TYPE, object -> 0);
		SqlTable<Object> second = SqlTable.create(Object.class, "t");
		second.column("id", INTEGER_TYPE, object -> 0);
		first.checkConstraint(alwaysCondition());
		assertNotEquals(first, second);
	}
	//endregion
}
