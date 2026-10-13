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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.query.util.SqlColumnValue;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlInsertQueryConfig}.<br>
 *
 * @author Luis-St
 */
class SqlInsertQueryConfigTest {
	
	private static final List<Object> ENTITIES = List.of(new Object());
	
	@Test
	void constructWithValidEntities() {
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		assertEquals(ENTITIES, config.entities());
		assertFalse(config.isUpsert());
		assertFalse(config.isInsertOrIgnore());
		assertFalse(config.isInsertFromSelect());
	}
	
	@Test
	void createPlainInsert() throws SqlStatementBuilderException {
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null);
		assertFalse(config.isUpsert());
		assertFalse(config.isInsertOrIgnore());
		assertFalse(config.isInsertFromSelect());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullEntities() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithEmptyEntitiesNotFromSelect() {
		assertThrows(IllegalArgumentException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, null, null, List.of(), List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullOmittedColumns() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, null, List.of(), false, false, false, null));
	}
	
	@Test
	void constructWithNullOverrides() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), null, false, false, false, null));
	}
	
	@Test
	void withOmittedColumnsReturnsCopyWithNewColumns() {
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		List<SqlColumn<Object, ?>> omitted = List.of(integerColumn());
		SqlInsertQueryConfig<Object> updated = config.withOmittedColumns(omitted);
		assertEquals(omitted, updated.omittedColumns());
		assertEquals(config.entities(), updated.entities());
		assertTrue(config.omittedColumns().isEmpty());
	}
	
	@Test
	void withOmittedColumnsWithNullThrows() {
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		assertThrows(NullPointerException.class, () -> config.withOmittedColumns(null));
	}
	
	@Test
	void withOverridesReturnsCopyWithNewOverrides() {
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		SqlColumn<Object, Integer> column = integerColumn();
		List<SqlColumnValue<Object, ?>> overrides = List.of(SqlColumnValue.of(column, 1));
		SqlInsertQueryConfig<Object> updated = config.withOverrides(overrides);
		assertEquals(overrides, updated.overrides());
		assertTrue(config.overrides().isEmpty());
	}
	
	@Test
	void withOverridesWithNullThrows() {
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		assertThrows(NullPointerException.class, () -> config.withOverrides(null));
	}
	
	@Test
	void withAuditUserProviderWithNullDoesNotThrow() {
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		SqlInsertQueryConfig<Object> updated = assertDoesNotThrow(() -> config.withAuditUserProvider(null));
		assertNotNull(updated);
		assertNull(updated.auditUserProvider());
	}
	
	@Test
	void withAuditUserProviderReturnsCopyWithNewProvider() {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		SqlInsertQueryConfig<Object> updated = config.withAuditUserProvider(provider);
		assertNotSame(config, updated);
		assertSame(provider, updated.auditUserProvider());
		assertNull(config.auditUserProvider());
	}
	
	@Test
	void withAuditUserProviderClearsExistingProvider() {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, provider);
		SqlInsertQueryConfig<Object> updated = config.withAuditUserProvider(null);
		assertNull(updated.auditUserProvider());
		assertSame(provider, config.auditUserProvider());
	}
	
	@Test
	void withAuditUserProviderPreservesOtherComponents() {
		SqlColumn<Object, Integer> column = integerColumn();
		List<SqlColumn<Object, ?>> conflicts = List.of(column);
		List<SqlColumn<Object, ?>> omitted = List.of(column);
		List<SqlColumnValue<Object, ?>> overrides = List.of(SqlColumnValue.of(column, 1));
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, conflicts, null, omitted, overrides, true, false, false, null);
		SqlInsertQueryConfig<Object> updated = config.withAuditUserProvider(() -> Optional.of("tester"));
		assertSame(config.table(), updated.table());
		assertSame(config.dialect(), updated.dialect());
		assertEquals(config.entities(), updated.entities());
		assertEquals(omitted, updated.omittedColumns());
		assertEquals(overrides, updated.overrides());
		assertEquals(conflicts, updated.conflictColumns());
		assertTrue(updated.isUpsert());
		assertFalse(updated.isInsertOrIgnore());
		assertFalse(updated.isInsertFromSelect());
	}
	
	@Test
	void withAuditUserProviderReplacesProvider() {
		SqlAuditUserProvider first = () -> Optional.of("first");
		SqlAuditUserProvider second = () -> Optional.of("second");
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, first);
		assertSame(second, config.withAuditUserProvider(second).auditUserProvider());
	}
	
	@Test
	void withAuditUserProviderChainedWithOtherCopyMethods() {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlColumn<Object, Integer> column = integerColumn();
		List<SqlColumn<Object, ?>> omitted = List.of(column);
		List<SqlColumnValue<Object, ?>> overrides = List.of(SqlColumnValue.of(column, 1));
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, null);
		SqlInsertQueryConfig<Object> updated = config.withOmittedColumns(omitted).withOverrides(overrides).withAuditUserProvider(provider);
		assertEquals(omitted, updated.omittedColumns());
		assertEquals(overrides, updated.overrides());
		assertSame(provider, updated.auditUserProvider());
		assertTrue(config.omittedColumns().isEmpty());
		assertTrue(config.overrides().isEmpty());
	}
	
	@Test
	void withAuditUserProviderOnUpsertConfigPreservesConflictColumns() throws SqlStatementBuilderException {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		List<SqlColumn<Object, ?>> conflicts = List.of(integerColumn());
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, conflicts, null, true, false, false, null);
		SqlInsertQueryConfig<Object> updated = config.withAuditUserProvider(provider);
		assertTrue(updated.isUpsert());
		assertEquals(conflicts, updated.conflictColumns());
		assertSame(provider, updated.auditUserProvider());
	}
	
	@Test
	void createUpsertAndInsertOrIgnoreMutuallyExclusive() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, List.of(integerColumn()), null, true, true, false, null));
	}
	
	@Test
	void createUpsertWithoutConflictColumn() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, true, false, false, null));
	}
	
	@Test
	void createUpsertWithEmptyConflictColumnsThrows() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, List.of(), null, true, false, false, null));
	}
	
	@Test
	void createInsertOrIgnoreWithNullConflictColumns() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, true, false, null));
	}
	
	@Test
	void createInsertOrIgnoreWithEmptyConflictColumns() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, List.of(), null, false, true, false, null));
	}
	
	@Test
	void createInsertFromSelectWithoutSelect() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, null, null, false, false, true, null));
	}
	
	@Test
	void createValidUpsert() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		SqlColumn<Object, Integer> conflict = integerColumn();
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, List.of(conflict), null, true, false, false, null);
		assertTrue(config.isUpsert());
		assertEquals(List.of(conflict), config.conflictColumns());
	}
	
	@Test
	void createValidInsertOrIgnore() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		List<SqlColumn<Object, ?>> conflicts = List.of(integerColumn());
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, conflicts, null, false, true, false, null);
		assertTrue(config.isInsertOrIgnore());
		assertEquals(conflicts, config.conflictColumns());
	}
	
	@Test
	void createValidInsertFromSelect() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), sampleSelect(), null, null, false, false, true, null);
		assertTrue(config.isInsertFromSelect());
		assertTrue(config.entities().isEmpty());
	}
	
	@Test
	void createDefaultsToEmptyOmittedColumnsAndOverrides() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null);
		assertTrue(config.omittedColumns().isEmpty());
		assertTrue(config.overrides().isEmpty());
	}
	
	@Test
	void auditUserProviderStoredVerbatim() {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, List.of(), List.of(), false, false, false, provider);
		assertSame(provider, config.auditUserProvider());
	}
}
