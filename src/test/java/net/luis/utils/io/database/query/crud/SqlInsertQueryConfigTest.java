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
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null);
		assertEquals(ENTITIES, config.entities());
		assertFalse(config.isUpsert());
		assertFalse(config.isInsertOrIgnore());
		assertFalse(config.isInsertFromSelect());
	}
	
	@Test
	void createPlainInsert() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null);
		assertFalse(config.isUpsert());
		assertFalse(config.isInsertOrIgnore());
		assertFalse(config.isInsertFromSelect());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(null, DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), null, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null));
	}
	
	@Test
	void constructWithNullConnectionSource() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, null, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, null, resultSet -> null, ENTITIES, null, null, null, false, false, false, null));
	}
	
	@Test
	void constructWithNullRowMapper() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, null, ENTITIES, null, null, null, false, false, false, null));
	}
	
	@Test
	void constructWithNullEntities() {
		assertThrows(NullPointerException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, null, null, null, null, false, false, false, null));
	}
	
	@Test
	void constructWithEmptyEntitiesNotFromSelect() {
		assertThrows(IllegalArgumentException.class, () -> new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, null, null, false, false, false, null));
	}
	
	@Test
	void createUpsertAndInsertOrIgnoreMutuallyExclusive() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, integerColumn(), List.of(integerColumn()), true, true, false, null));
	}
	
	@Test
	void createUpsertWithoutConflictColumn() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, true, false, false, null));
	}
	
	@Test
	void createInsertOrIgnoreWithNullConflictColumns() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, true, false, null));
	}
	
	@Test
	void createInsertOrIgnoreWithEmptyConflictColumns() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, List.of(), false, true, false, null));
	}
	
	@Test
	void createInsertFromSelectWithoutSelect() {
		assertThrows(SqlStatementBuilderException.class, () -> SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, List.of(), null, null, null, false, false, true, null));
	}
	
	@Test
	void createValidUpsert() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		SqlColumn<Object, Integer> conflict = integerColumn();
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, conflict, null, true, false, false, null);
		assertTrue(config.isUpsert());
		assertSame(conflict, config.conflictColumn());
	}
	
	@Test
	void createValidInsertOrIgnore() throws net.luis.utils.io.database.exception.client.SqlStatementBuilderException {
		List<SqlColumn<Object, ?>> conflicts = List.of(integerColumn());
		SqlInsertQueryConfig<Object> config = SqlInsertQueryConfig.create(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, conflicts, false, true, false, null);
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
	void auditUserProviderStoredVerbatim() {
		SqlAuditUserProvider provider = () -> Optional.of("tester");
		SqlInsertQueryConfig<Object> config = new SqlInsertQueryConfig<>(sampleTable(), DIALECT, SOURCE, TIMEOUT, resultSet -> null, ENTITIES, null, null, null, false, false, false, provider);
		assertSame(provider, config.auditUserProvider());
	}
}
