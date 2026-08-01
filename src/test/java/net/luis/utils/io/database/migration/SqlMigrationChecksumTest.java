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

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlEqualToCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlIsNullCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.migration.operation.*;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationChecksum}.<br>
 * <p>
 *     The class exposes only two static entry points, so every private branch is driven through
 *     {@code compute(...)} by choosing operations rather than by reflection. A checksum is an opaque digest, so
 *     almost every assertion compares two {@code compute} results instead of inspecting the digest text.
 * </p>
 *
 * @author Luis-St
 */
class SqlMigrationChecksumTest {
	
	private static final SqlDialect DIALECT = SqlTestFixtures.DIALECT;
	private static final String PREFIX = "s1:";
	
	private static @NonNull String compute(SqlMigrationOperation @NonNull ... operations) throws SqlException {
		return SqlMigrationChecksum.compute(List.of(operations), DIALECT);
	}
	
	private static @NonNull Fixture fixture() {
		return fixture("users", "id", "name");
	}
	
	private static @NonNull Fixture fixture(@NonNull String table, @NonNull String id, @NonNull String name) {
		SqlTable<Object> owner = SqlTable.create(Object.class, table);
		return new Fixture(owner, owner.column(id, SqlTypes.INTEGER, _ -> 0), owner.column(name, SqlTestFixtures.STRING_TYPE, _ -> ""));
	}
	
	private static @NonNull Fixture retypedFixture() {
		SqlTable<Object> owner = SqlTable.create(Object.class, "users");
		return new Fixture(owner, owner.column("id", SqlTypes.LONG, _ -> 0L), owner.column("name", SqlTypes.TEXT, _ -> ""));
	}
	
	private static @NonNull SqlColumnOptions optionsWithDefault(@NonNull Object value) {
		return new SqlColumnOptions(false, false, false, Optional.of(value), null, null);
	}
	
	private static @NonNull SqlAlterColumnOperation setDefault(@NonNull Fixture fixture, @NonNull Object value) {
		return new SqlAlterColumnOperation(fixture.name(), List.of(new SqlSetDefaultAlteration(value)));
	}
	
	@Test
	void computeWithNullOperations() {
		assertThrows(NullPointerException.class, () -> SqlMigrationChecksum.compute(null, DIALECT));
	}
	
	@Test
	void computeWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlMigrationChecksum.compute(List.of(), null));
	}
	
	@Test
	void computeWithNullOperationInList() {
		List<SqlMigrationOperation> operations = Collections.singletonList(null);
		assertThrows(NullPointerException.class, () -> SqlMigrationChecksum.compute(operations, DIALECT));
	}
	
	@Test
	void isComparableWithNullChecksum() {
		assertThrows(NullPointerException.class, () -> SqlMigrationChecksum.isComparable(null));
	}
	
	@Test
	void isComparableWithPrefixedChecksum() throws SqlException {
		assertTrue(SqlMigrationChecksum.isComparable(compute(fixture().createTable())));
	}
	
	@Test
	void isComparableWithLegacyChecksum() {
		assertFalse(SqlMigrationChecksum.isComparable("0".repeat(64)));
	}
	
	@Test
	void isComparableWithEmptyChecksum() {
		assertFalse(SqlMigrationChecksum.isComparable(""));
	}
	
	@Test
	void computeReturnsPrefixedChecksum() throws SqlException {
		String checksum = compute(fixture().createTable());
		
		assertTrue(checksum.startsWith(PREFIX));
		assertEquals(64, checksum.length());
		assertTrue(checksum.substring(PREFIX.length()).matches("[0-9a-f]+"));
	}
	
	@Test
	void computeWithEmptyOperations() throws SqlException {
		String checksum = assertDoesNotThrow(() -> compute());
		
		assertTrue(checksum.startsWith(PREFIX));
		assertEquals(checksum, compute());
	}
	
	@Test
	void computeWithCreateTableOperation() throws SqlException {
		String checksum = compute(fixture().createTable());
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropTableOperation(fixture().table())), checksum);
	}
	
	@Test
	void computeWithCreateTableWithoutColumns() throws SqlException {
		Fixture fixture = fixture();
		SqlCreateTableOperation operation = new SqlCreateTableOperation(fixture.table(), List.of(), List.of());
		
		assertNotEquals(compute(), compute(operation));
		assertNotEquals(compute(fixture().createTable()), compute(operation));
	}
	
	@Test
	void computeWithCreateTableWithoutPrimaryKey() throws SqlException {
		Fixture fixture = fixture();
		SqlCreateTableOperation operation = new SqlCreateTableOperation(fixture.table(), fixture.definitions(), List.of());
		
		assertNotEquals(compute(), compute(operation));
		assertNotEquals(compute(fixture().createTable()), compute(operation));
	}
	
	@Test
	void computeWithDropTableOperation() throws SqlException {
		String checksum = compute(new SqlDropTableOperation(fixture().table()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropColumnOperation(fixture().id())), checksum);
	}
	
	@Test
	void computeWithRenameTableOperation() throws SqlException {
		String checksum = compute(new SqlRenameTableOperation(fixture().table(), fixture("accounts", "id", "name").table()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropTableOperation(fixture().table())), checksum);
	}
	
	@Test
	void computeWithAddColumnOperation() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlAddColumnOperation(fixture.name(), fixture.name().type(), SqlColumnOptions.EMPTY));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropColumnOperation(fixture().name())), checksum);
	}
	
	@Test
	void computeWithDropColumnOperation() throws SqlException {
		String checksum = compute(new SqlDropColumnOperation(fixture().name()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropTableOperation(fixture().table())), checksum);
		assertEquals(compute(new SqlDropColumnOperation(fixture("accounts", "key", "label").name())), checksum);
	}
	
	@Test
	void computeWithRenameColumnOperation() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlRenameColumnOperation(fixture.id(), fixture.name()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropColumnOperation(fixture().id())), checksum);
		assertEquals(compute(new SqlRenameColumnOperation(fixture().name(), fixture().id())), checksum);
	}
	
	@Test
	void computeWithAlterColumnSetType() throws SqlException {
		Fixture fixture = fixture();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(fixture.name(), List.of(new SqlSetTypeAlteration(SqlTypes.TEXT)));
		
		assertNotEquals(compute(), compute(operation));
		assertNotEquals(compute(new SqlAlterColumnOperation(fixture().name(), List.of())), compute(operation));
	}
	
	@Test
	void computeWithAlterColumnSetNullable() throws SqlException {
		Fixture fixture = fixture();
		SqlAlterColumnOperation nullable = new SqlAlterColumnOperation(fixture.name(), List.of(new SqlSetNullableAlteration(true)));
		SqlAlterColumnOperation notNullable = new SqlAlterColumnOperation(fixture().name(), List.of(new SqlSetNullableAlteration(false)));
		
		assertNotEquals(compute(), compute(nullable));
		assertNotEquals(compute(notNullable), compute(nullable));
	}
	
	@Test
	void computeWithAlterColumnSetDefault() throws SqlException {
		String checksum = compute(setDefault(fixture(), "value"));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(setDefault(fixture(), "other")), checksum);
	}
	
	@Test
	void computeWithAlterColumnDropDefault() throws SqlException {
		Fixture fixture = fixture();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(fixture.name(), List.of(new SqlDropDefaultAlteration()));
		
		assertNotEquals(compute(), compute(operation));
		assertNotEquals(compute(setDefault(fixture(), "value")), compute(operation));
	}
	
	@Test
	void computeWithAlterColumnWithoutAlterations() throws SqlException {
		Fixture fixture = fixture();
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(fixture.name(), List.of());
		
		assertNotEquals(compute(), compute(operation));
		assertNotEquals(compute(setDefault(fixture(), "value")), compute(operation));
	}
	
	@Test
	void computeWithCreateIndexOperation() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlCreateIndexOperation(fixture.index("users_idx", false), fixture.table()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlCreateIndexOperation(fixture().index("users_idx", true), fixture().table())), checksum);
	}
	
	@Test
	void computeWithCreateIndexWithWhereCondition() throws SqlException {
		Fixture fixture = fixture();
		SqlIndex index = new SqlIndex("users_idx", fixture.indexColumns(), false, new SqlIsNullCondition(fixture.name()), SqlIndexMethod.BTREE);
		String checksum = compute(fixture.createTable(), new SqlCreateIndexOperation(index, fixture.table()));
		
		assertNotEquals(compute(fixture().createTable(), new SqlCreateIndexOperation(fixture().index("users_idx", false), fixture().table())), checksum);
	}
	
	@Test
	void computeWithDropIndexOperation() throws SqlException {
		String checksum = compute(new SqlDropIndexOperation(fixture().table(), "users_idx"));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropIndexOperation(fixture().table(), "other_idx")), checksum);
	}
	
	@Test
	void computeWithDropIndexWithoutTable() throws SqlException {
		String checksum = compute(new SqlDropIndexOperation(null, "users_idx"));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropIndexOperation(fixture().table(), "users_idx")), checksum);
	}
	
	@Test
	void computeWithRenameIndexOperation() throws SqlException {
		String checksum = compute(new SqlRenameIndexOperation(fixture().table(), "old_idx", "new_idx"));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlRenameIndexOperation(fixture().table(), "new_idx", "old_idx")), checksum);
	}
	
	@Test
	void computeWithRenameIndexWithoutTable() throws SqlException {
		String checksum = compute(new SqlRenameIndexOperation(null, "old_idx", "new_idx"));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlRenameIndexOperation(fixture().table(), "old_idx", "new_idx")), checksum);
	}
	
	@Test
	void computeWithAddUniqueConstraintOperation() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlAddUniqueConstraintOperation(fixture.table(), "users_unique", fixture.indexColumns()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlAddCompositePrimaryKeyOperation(fixture().table(), "users_unique", fixture().indexColumns())), checksum);
	}
	
	@Test
	void computeWithAddForeignKeyOperation() throws SqlException {
		Fixture fixture = fixture();
		Fixture referenced = fixture("accounts", "id", "name");
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(
			fixture.table(), "users_fk", fixture.indexColumns(), referenced.table(), referenced.indexColumns(), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION
		);
		
		assertNotEquals(compute(), compute(operation));
		assertNotEquals(compute(new SqlAddForeignKeyOperation(
			fixture().table(), "users_fk", fixture().indexColumns(), fixture("accounts", "id", "name").table(), fixture("accounts", "id", "name").indexColumns(),
			SqlReferentialAction.RESTRICT, SqlReferentialAction.NO_ACTION
		)), compute(operation));
	}
	
	@Test
	void computeWithAddCheckConstraintOperation() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlAddCheckConstraintOperation(fixture.table(), "users_check", new SqlIsNullCondition(fixture.name())));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlAddCheckConstraintOperation(fixture().table(), "users_check", SqlCondition.always())), checksum);
	}
	
	@Test
	void computeWithAddCompositePrimaryKeyOperation() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlAddCompositePrimaryKeyOperation(fixture.table(), "users_pk", fixture.indexColumns()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlAddCompositePrimaryKeyOperation(fixture().table(), "other_pk", fixture().indexColumns())), checksum);
	}
	
	@Test
	void computeWithDropConstraintOperation() throws SqlException {
		String checksum = compute(new SqlDropConstraintOperation(fixture().table(), "users_check"));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropConstraintOperation(fixture().table(), "other_check")), checksum);
	}
	
	@Test
	void computeWithEnableAuditingOperation() throws SqlException {
		String checksum = compute(new SqlEnableAuditingOperation(fixture().table(), SqlAuditConfig.DEFAULT));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDisableAuditingOperation(fixture().table(), SqlAuditConfig.DEFAULT)), checksum);
	}
	
	@Test
	void computeWithDisableAuditingOperation() throws SqlException {
		String checksum = compute(new SqlDisableAuditingOperation(fixture().table(), SqlAuditConfig.DEFAULT));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlExecuteDataOperation(fixture().table())), checksum);
	}
	
	@Test
	void computeWithExecuteDataOperation() throws SqlException {
		String checksum = compute(new SqlExecuteDataOperation(fixture().table()));
		
		assertNotEquals(compute(), checksum);
		assertNotEquals(compute(new SqlDropTableOperation(fixture().table())), checksum);
	}
	
	@Test
	void computeWithColumnOptionsDefaultValue() throws SqlException {
		Fixture fixture = fixture();
		SqlAddColumnOperation operation = new SqlAddColumnOperation(fixture.name(), fixture.name().type(), optionsWithDefault("value"));
		
		assertNotEquals(compute(new SqlAddColumnOperation(fixture().name(), fixture().name().type(), SqlColumnOptions.EMPTY)), compute(operation));
	}
	
	@Test
	void computeWithColumnOptionsReferencesTable() throws SqlException {
		Fixture fixture = fixture();
		SqlColumnOptions options = new SqlColumnOptions(false, false, false, Optional.empty(), fixture("accounts", "id", "name").table(), null);
		SqlAddColumnOperation operation = new SqlAddColumnOperation(fixture.name(), fixture.name().type(), options);
		
		assertNotEquals(compute(new SqlAddColumnOperation(fixture().name(), fixture().name().type(), SqlColumnOptions.EMPTY)), compute(operation));
	}
	
	@Test
	void computeWithColumnOptionsCheckCondition() throws SqlException {
		Fixture fixture = fixture();
		SqlColumnOptions options = new SqlColumnOptions(false, false, false, Optional.empty(), null, new SqlIsNullCondition(fixture.name()));
		SqlAddColumnOperation operation = new SqlAddColumnOperation(fixture.name(), fixture.name().type(), options);
		
		assertNotEquals(compute(new SqlAddColumnOperation(fixture().name(), fixture().name().type(), SqlColumnOptions.EMPTY)), compute(operation));
	}
	
	@Test
	void computeWithColumnOptionsFlags() throws SqlException {
		Fixture fixture = fixture();
		SqlColumnOptions set = new SqlColumnOptions(true, true, true, Optional.empty(), null, null);
		SqlAddColumnOperation operation = new SqlAddColumnOperation(fixture.name(), fixture.name().type(), set);
		
		assertNotEquals(compute(new SqlAddColumnOperation(fixture().name(), fixture().name().type(), SqlColumnOptions.EMPTY)), compute(operation));
	}
	
	@Test
	void computeReusesTableToken() throws SqlException {
		Fixture fixture = fixture();
		String shared = compute(new SqlDropColumnOperation(fixture.name()), new SqlDropColumnOperation(fixture.id()));
		
		Fixture first = fixture();
		Fixture second = fixture("accounts", "id", "name");
		assertNotEquals(compute(new SqlDropColumnOperation(first.name()), new SqlDropColumnOperation(second.id())), shared);
	}
	
	@Test
	void computeReusesColumnToken() throws SqlException {
		Fixture fixture = fixture();
		String shared = compute(new SqlDropColumnOperation(fixture.name()), new SqlDropColumnOperation(fixture.name()));
		
		Fixture other = fixture();
		assertNotEquals(compute(new SqlDropColumnOperation(other.name()), new SqlDropColumnOperation(other.id())), shared);
	}
	
	@Test
	void computeReusesTypeToken() throws SqlException {
		Fixture fixture = fixture();
		SqlType<?> type = fixture.name().type();
		String shared = compute(
			new SqlAddColumnOperation(fixture.id(), type, SqlColumnOptions.EMPTY),
			new SqlAddColumnOperation(fixture.name(), type, SqlColumnOptions.EMPTY)
		);
		
		Fixture other = fixture();
		assertNotEquals(compute(
			new SqlAddColumnOperation(other.id(), SqlTypes.INTEGER, SqlColumnOptions.EMPTY),
			new SqlAddColumnOperation(other.name(), SqlTypes.TEXT, SqlColumnOptions.EMPTY)
		), shared);
	}
	
	@Test
	void computeDistinguishesDistinctTables() throws SqlException {
		String distinct = compute(new SqlDropTableOperation(fixture().table()), new SqlDropTableOperation(fixture("accounts", "id", "name").table()));
		
		Fixture repeated = fixture();
		assertNotEquals(compute(new SqlDropTableOperation(repeated.table()), new SqlDropTableOperation(repeated.table())), distinct);
	}
	
	@Test
	void computeWithByteArrayDefault() throws SqlException {
		String checksum = compute(setDefault(fixture(), new byte[] { 1, 2 }));
		
		assertNotEquals(compute(setDefault(fixture(), new Integer[] { 1, 2 })), checksum);
		assertNotEquals(compute(setDefault(fixture(), new byte[] { 2, 1 })), checksum);
	}
	
	@Test
	void computeWithObjectArrayDefault() throws SqlException {
		String checksum = compute(setDefault(fixture(), new Object[] { 1, 2 }));
		
		assertNotEquals(compute(setDefault(fixture(), new Object[] { 1 })), checksum);
		assertNotEquals(compute(setDefault(fixture(), new Object[] { 2, 1 })), checksum);
	}
	
	@Test
	void computeWithSingleElementArrayDefault() throws SqlException {
		String checksum = compute(setDefault(fixture(), new Object[] { 1 }));
		
		assertNotEquals(compute(setDefault(fixture(), new Object[] { 1, 2 })), checksum);
		assertNotEquals(compute(setDefault(fixture(), new Object[] {})), checksum);
	}
	
	@Test
	void computeWithEmptyArrayDefault() throws SqlException {
		String checksum = compute(setDefault(fixture(), new Object[] {}));
		
		assertNotEquals(compute(setDefault(fixture(), new Object[] { 1 })), checksum);
		assertEquals(compute(setDefault(fixture(), new Object[] {})), checksum);
	}
	
	@Test
	void computeWithNestedArrayDefault() throws SqlException {
		String checksum = compute(setDefault(fixture(), new Object[] { new int[] { 1, 2 } }));
		
		assertNotEquals(compute(setDefault(fixture(), new Object[] { 1, 2 })), checksum);
		assertEquals(compute(setDefault(fixture(), new Object[] { new int[] { 1, 2 } })), checksum);
	}
	
	@Test
	void computeWithArrayContainingNullDefault() throws SqlException {
		String checksum = assertDoesNotThrow(() -> compute(setDefault(fixture(), new Object[] { null, 1 })));
		
		assertNotEquals(compute(setDefault(fixture(), new Object[] { 1 })), checksum);
		assertEquals(compute(setDefault(fixture(), new Object[] { null, 1 })), checksum);
	}
	
	@Test
	void computeWithMultipleConditions() throws SqlException {
		Fixture fixture = fixture();
		SqlCondition first = new SqlIsNullCondition(fixture.name());
		SqlCondition second = new SqlEqualToCondition(fixture.name(), SqlTestFixtures.stringExpression());
		String checksum = compute(
			new SqlAddCheckConstraintOperation(fixture.table(), "first", first),
			new SqlAddCheckConstraintOperation(fixture.table(), "second", second)
		);
		
		Fixture other = fixture();
		assertNotEquals(compute(
			new SqlAddCheckConstraintOperation(other.table(), "first", new SqlEqualToCondition(other.name(), SqlTestFixtures.stringExpression())),
			new SqlAddCheckConstraintOperation(other.table(), "second", new SqlIsNullCondition(other.name()))
		), checksum);
	}
	
	@Test
	void computeWithConditionValueLiteral() throws SqlException {
		Fixture fixture = fixture();
		SqlCondition condition = new SqlEqualToCondition(fixture.name(), SqlTestFixtures.stringExpression());
		String checksum = compute(new SqlAddCheckConstraintOperation(fixture.table(), "users_check", condition));
		
		Fixture other = fixture();
		assertNotEquals(compute(new SqlAddCheckConstraintOperation(other.table(), "users_check", new SqlIsNullCondition(other.name()))), checksum);
	}
	
	@Test
	void computeReplacesIdentifiersInCondition() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(fixture.createTable(), new SqlAddCheckConstraintOperation(fixture.table(), "users_check", new SqlIsNullCondition(fixture.name())));
		
		Fixture renamed = fixture("accounts", "identifier", "display_name");
		assertEquals(compute(renamed.createTable(), new SqlAddCheckConstraintOperation(renamed.table(), "users_check", new SqlIsNullCondition(renamed.name()))), checksum);
	}
	
	@Test
	void computeReplacesPrefixIdentifiersLongestFirst() throws SqlException {
		Fixture fixture = fixture("users", "id", "id_ref");
		String checksum = compute(fixture.createTable(), new SqlAddCheckConstraintOperation(fixture.table(), "users_check", new SqlIsNullCondition(fixture.name())));
		
		Fixture renamed = fixture("users", "key", "key_ref");
		assertEquals(compute(renamed.createTable(), new SqlAddCheckConstraintOperation(renamed.table(), "users_check", new SqlIsNullCondition(renamed.name()))), checksum);
	}
	
	@Test
	void computeIsDeterministic() throws SqlException {
		assertEquals(compute(fixture().createTable()), compute(fixture().createTable()));
	}
	
	@Test
	void computeStableAcrossColumnRename() throws SqlException {
		assertEquals(compute(fixture("users", "id", "display_name").createTable()), compute(fixture().createTable()));
	}
	
	@Test
	void computeStableAcrossTableRename() throws SqlException {
		assertEquals(compute(fixture("accounts", "id", "name").createTable()), compute(fixture().createTable()));
	}
	
	@Test
	void computeStableAcrossColumnRetype() throws SqlException {
		assertEquals(compute(retypedFixture().createTable()), compute(fixture().createTable()));
	}
	
	@Test
	void computeStableAcrossRenameOfEveryObject() throws SqlException {
		assertEquals(compute(fixture("accounts", "identifier", "display_name").createTable()), compute(fixture().createTable()));
	}
	
	@Test
	void computeDetectsAddedColumn() throws SqlException {
		Fixture fixture = fixture();
		SqlColumn<Object, ?> added = fixture.table().column("age", SqlTypes.INTEGER, _ -> 0);
		List<SqlColumnDefinition> definitions = new ArrayList<>(fixture.definitions());
		definitions.add(new SqlColumnDefinition(added, added.type(), SqlColumnOptions.EMPTY));
		
		SqlCreateTableOperation operation = new SqlCreateTableOperation(fixture.table(), definitions, List.<SqlColumn<?, ?>>of(fixture.id()));
		assertNotEquals(compute(fixture().createTable()), compute(operation));
	}
	
	@Test
	void computeDetectsRemovedColumn() throws SqlException {
		Fixture fixture = fixture();
		SqlCreateTableOperation operation = new SqlCreateTableOperation(
			fixture.table(), List.of(fixture.definitions().getFirst()), List.<SqlColumn<?, ?>>of(fixture.id())
		);
		
		assertNotEquals(compute(fixture().createTable()), compute(operation));
	}
	
	@Test
	void computeDetectsReorderedOperations() throws SqlException {
		Fixture fixture = fixture();
		String ordered = compute(fixture.createTable(), new SqlCreateIndexOperation(fixture.index("users_idx", false), fixture.table()));
		
		Fixture other = fixture();
		assertNotEquals(compute(new SqlCreateIndexOperation(other.index("users_idx", false), other.table()), other.createTable()), ordered);
	}
	
	@Test
	void computeDetectsReplacedBody() throws SqlException {
		assertNotEquals(compute(new SqlDropTableOperation(fixture().table())), compute(fixture().createTable()));
	}
	
	@Test
	void computeDetectsChangedConstraintName() throws SqlException {
		Fixture fixture = fixture();
		String checksum = compute(new SqlAddUniqueConstraintOperation(fixture.table(), "users_unique", fixture.indexColumns()));
		
		Fixture other = fixture();
		assertNotEquals(compute(new SqlAddUniqueConstraintOperation(other.table(), "users_unique_v2", other.indexColumns())), checksum);
	}
	
	@Test
	void computeDetectsChangedTypeSharingPattern() throws SqlException {
		Fixture fixture = fixture();
		String shared = compute(
			new SqlAddColumnOperation(fixture.id(), SqlTypes.INTEGER, SqlColumnOptions.EMPTY),
			new SqlAddColumnOperation(fixture.name(), SqlTypes.INTEGER, SqlColumnOptions.EMPTY)
		);
		
		Fixture other = fixture();
		assertNotEquals(compute(
			new SqlAddColumnOperation(other.id(), SqlTypes.INTEGER, SqlColumnOptions.EMPTY),
			new SqlAddColumnOperation(other.name(), SqlTypes.TEXT, SqlColumnOptions.EMPTY)
		), shared);
	}
	
	@Test
	void computeDetectsChangedPrimaryKeyArity() throws SqlException {
		Fixture fixture = fixture();
		SqlCreateTableOperation operation = new SqlCreateTableOperation(fixture.table(), fixture.definitions(), fixture.indexColumns());
		
		assertNotEquals(compute(fixture().createTable()), compute(operation));
	}
	
	@Test
	void computeMultiOperationMigrationConsistency() throws SqlException {
		String checksum = compute(operations(fixture()));
		
		assertEquals(compute(operations(fixture("accounts", "identifier", "display_name"))), checksum);
		assertNotEquals(compute(Arrays.copyOf(operations(fixture()), 3)), checksum);
	}
	
	private static SqlMigrationOperation @NonNull [] operations(@NonNull Fixture fixture) {
		return new SqlMigrationOperation[] {
			fixture.createTable(),
			new SqlAddColumnOperation(fixture.name(), fixture.name().type(), SqlColumnOptions.EMPTY),
			new SqlCreateIndexOperation(fixture.index("users_idx", false), fixture.table()),
			new SqlAddUniqueConstraintOperation(fixture.table(), "users_unique", fixture.indexColumns()),
			new SqlAddCheckConstraintOperation(fixture.table(), "users_check", new SqlIsNullCondition(fixture.name()))
		};
	}
	
	private record Fixture(@NonNull SqlTable<Object> table, @NonNull SqlColumn<Object, ?> id, @NonNull SqlColumn<Object, ?> name) {
		
		private @NonNull List<SqlColumnDefinition> definitions() {
			return List.of(
				new SqlColumnDefinition(this.id, this.id.type(), SqlColumnOptions.EMPTY),
				new SqlColumnDefinition(this.name, this.name.type(), SqlColumnOptions.EMPTY)
			);
		}
		
		private @NonNull List<SqlColumn<?, ?>> indexColumns() {
			return List.of(this.id, this.name);
		}
		
		private @NonNull SqlCreateTableOperation createTable() {
			return new SqlCreateTableOperation(this.table, this.definitions(), List.<SqlColumn<?, ?>>of(this.id));
		}
		
		private @NonNull SqlIndex index(@NonNull String name, boolean unique) {
			return new SqlIndex(name, List.<SqlColumn<?, ?>>of(this.id), unique, SqlIndexMethod.BTREE);
		}
	}
}
