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

package net.luis.utils.io.database;

import net.luis.utils.function.throwable.ThrowableSupplier;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.transaction.*;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDatabase}.<br>
 *
 * @author Luis-St
 */
class SqlDatabaseTest {
	
	private static final SqlIsolationLevel LEVEL = SqlIsolationLevel.READ_COMMITTED;
	private static final SqlPropagation PROPAGATION = SqlPropagation.REQUIRED;
	
	private static SqlDatabase database(DataSource dataSource) throws SqlException {
		return SqlDatabase.builder(dataSource, DIALECT).build();
	}
	
	private static String createSchemaSql(String name, boolean ifNotExists) throws SqlException {
		return DIALECT.schemaRenderer().renderCreateSchema(name, ifNotExists).sql();
	}
	
	private static String dropSchemaSql(String name, boolean cascade) throws SqlException {
		return DIALECT.schemaRenderer().renderDropSchema(name, false, cascade).sql();
	}
	
	@SuppressWarnings("unchecked")
	private static <X extends Throwable> void sneakyThrow(Throwable throwable) throws X {
		throw (X) throwable;
	}
	
	@Test
	void constructWithBuilder() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlAuditUserProvider provider = SqlAuditUserProvider.of("admin");
		SqlDatabase database = SqlDatabase.builder(source, DIALECT).auditUserProvider(provider).build();
		assertNotNull(database);
		assertSame(source, database.getDataSource());
		assertSame(DIALECT, database.getDialect());
		assertSame(provider, database.getAuditUserProvider());
	}
	
	@Test
	void constructViaConstructorWithAllArguments() {
		RecordingDataSource source = recordingDataSource();
		SqlAuditUserProvider provider = SqlAuditUserProvider.empty();
		SqlDatabase database = assertDoesNotThrow(() -> new SqlDatabase(source, DIALECT, TIMEOUT, TIMEOUT, LEVEL, PROPAGATION, false, provider));
		assertNotNull(database);
		assertSame(source, database.getDataSource());
		assertSame(DIALECT, database.getDialect());
		assertSame(provider, database.getAuditUserProvider());
	}
	
	@Test
	void constructWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(null, DIALECT, TIMEOUT, TIMEOUT, LEVEL, PROPAGATION, false, SqlAuditUserProvider.empty()));
	}
	
	@Test
	void constructWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(failingDataSource(), null, TIMEOUT, TIMEOUT, LEVEL, PROPAGATION, false, SqlAuditUserProvider.empty()));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(failingDataSource(), DIALECT, null, TIMEOUT, LEVEL, PROPAGATION, false, SqlAuditUserProvider.empty()));
	}
	
	@Test
	void constructWithNullConnectionAcquisitionTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(failingDataSource(), DIALECT, TIMEOUT, null, LEVEL, PROPAGATION, false, SqlAuditUserProvider.empty()));
	}
	
	@Test
	void constructWithNullIsolationLevel() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(failingDataSource(), DIALECT, TIMEOUT, TIMEOUT, null, PROPAGATION, false, SqlAuditUserProvider.empty()));
	}
	
	@Test
	void constructWithNullPropagation() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(failingDataSource(), DIALECT, TIMEOUT, TIMEOUT, LEVEL, null, false, SqlAuditUserProvider.empty()));
	}
	
	@Test
	void constructWithNullAuditUserProvider() {
		assertThrows(NullPointerException.class, () -> new SqlDatabase(failingDataSource(), DIALECT, TIMEOUT, TIMEOUT, LEVEL, PROPAGATION, false, null));
	}
	
	@Test
	void builderWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> SqlDatabase.builder(null, DIALECT));
	}
	
	@Test
	void builderWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlDatabase.builder(failingDataSource(), null));
	}
	
	@Test
	void createSchemaWithNullName() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(NullPointerException.class, () -> database.createSchema(null));
	}
	
	@Test
	void createSchemaIfNotExistsWithNullName() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(NullPointerException.class, () -> database.createSchemaIfNotExists(null));
	}
	
	@Test
	void existsSchemaWithNullName() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(NullPointerException.class, () -> database.existsSchema(null));
	}
	
	@Test
	void dropSchemaWithNullName() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(NullPointerException.class, () -> database.dropSchema(null, false));
	}
	
	@Test
	void tableWithNullTable() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(NullPointerException.class, () -> database.table(null));
	}
	
	@Test
	void fromWithNullTable() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(NullPointerException.class, () -> database.from(null));
	}
	
	@Test
	void createSchemaWrapsSqlException() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(SqlException.class, () -> database.createSchema("schema"));
	}
	
	@Test
	void createSchemaIfNotExistsWrapsSqlException() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(SqlException.class, () -> database.createSchemaIfNotExists("schema"));
	}
	
	@Test
	void existsSchemaWrapsSqlException() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(SqlException.class, () -> database.existsSchema("schema"));
	}
	
	@Test
	void dropSchemaWrapsSqlException() throws SqlException {
		SqlDatabase database = database(failingDataSource());
		assertThrows(SqlException.class, () -> database.dropSchema("schema", true));
	}
	
	@Test
	void createSchemaWrapsStatementSqlException() throws SqlException {
		SqlDatabase database = database(throwingConnectionDataSource());
		assertThrows(SqlException.class, () -> database.createSchema("schema"));
	}
	
	@Test
	void beginTransactionWithNullIsolationLevel() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.beginTransaction(null, PROPAGATION));
	}
	
	@Test
	void beginTransactionWithNullPropagation() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.beginTransaction(LEVEL, null));
	}
	
	@Test
	void beginReadOnlyTransactionWithNullIsolationLevel() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.beginReadOnlyTransaction(null, PROPAGATION));
	}
	
	@Test
	void beginReadOnlyTransactionWithNullPropagation() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.beginReadOnlyTransaction(LEVEL, null));
	}
	
	@Test
	void inTransactionWithNullAction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.inTransaction(null));
	}
	
	@Test
	void inTransactionWithTransactionAndNullSupplier() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertThrows(NullPointerException.class, () -> database.inTransaction(transaction, (ThrowableSupplier<Object, SqlException>) null));
		}
	}
	
	@Test
	void inTransactionWithFunctionAndNullTransaction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.inTransaction(null, transaction -> "x"));
	}
	
	@Test
	void inTransactionWithSupplierAndNullTransaction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.inTransaction(null, () -> "x"));
	}
	
	@Test
	void openSessionWithNullAuditUserProvider() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.openSession((SqlAuditUserProvider) null));
	}
	
	@Test
	void openSessionWithNullTransaction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.openSession((SqlTransaction) null));
	}
	
	@Test
	void openSessionWithTransactionAndNullAuditUserProvider() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertThrows(NullPointerException.class, () -> database.openSession(transaction, null));
		}
	}
	
	@Test
	void openSessionWithNullTransactionAndProvider() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertThrows(NullPointerException.class, () -> database.openSession(null, SqlAuditUserProvider.of("admin")));
	}
	
	@Test
	void getDataSourceReturnsSource() throws SqlException {
		DataSource source = recordingDataSource();
		assertSame(source, database(source).getDataSource());
	}
	
	@Test
	void getDialectReturnsDialect() throws SqlException {
		assertSame(DIALECT, database(recordingDataSource()).getDialect());
	}
	
	@Test
	void getAuditUserProviderReturnsProvider() throws SqlException {
		SqlAuditUserProvider provider = SqlAuditUserProvider.of("admin");
		SqlDatabase database = SqlDatabase.builder(recordingDataSource(), DIALECT).auditUserProvider(provider).build();
		assertSame(provider, database.getAuditUserProvider());
	}
	
	@Test
	void healthReturnsTrueWhenConnectionValid() throws SqlException {
		assertTrue(database(recordingDataSource()).health());
	}
	
	@Test
	void healthReturnsFalseWhenConnectionInvalid() throws SqlException {
		assertFalse(database(throwingConnectionDataSource()).health());
	}
	
	@Test
	void healthReturnsFalseOnSqlException() throws SqlException {
		assertFalse(database(failingDataSource()).health());
	}
	
	@Test
	void pingReturnsTrueWhenConnectionValid() throws SqlException {
		assertTrue(database(recordingDataSource()).ping());
	}
	
	@Test
	void pingReturnsFalseWhenConnectionInvalid() throws SqlException {
		assertFalse(database(throwingConnectionDataSource()).ping());
	}
	
	@Test
	void pingReturnsFalseOnSqlException() throws SqlException {
		assertFalse(database(failingDataSource()).ping());
	}
	
	@Test
	void createSchemaExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).createSchema("my_schema");
		assertTrue(source.executedSql().contains(createSchemaSql("my_schema", false)));
	}
	
	@Test
	void createSchemaIfNotExistsExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).createSchemaIfNotExists("my_schema");
		assertTrue(source.executedSql().contains(createSchemaSql("my_schema", true)));
	}
	
	@Test
	void existsSchemaReturnsFalseForEmptyResult() throws SqlException {
		assertFalse(database(recordingDataSource()).existsSchema("x"));
	}
	
	@Test
	void existsSchemaReturnsTrueWhenSchemaPresent() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of("TABLE_SCHEM", "x"))));
		assertTrue(database(source).existsSchema("x"));
	}
	
	@Test
	void existsSchemaReturnsFalseWhenSchemaAbsent() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of("TABLE_SCHEM", "other"))));
		assertFalse(database(source).existsSchema("x"));
	}
	
	@Test
	void dropSchemaWithoutCascadeExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).dropSchema("my_schema", false);
		assertTrue(source.executedSql().contains(dropSchemaSql("my_schema", false)));
	}
	
	@Test
	void dropSchemaWithCascadeExecutesRenderedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		database(source).dropSchema("my_schema", true);
		assertTrue(source.executedSql().contains(dropSchemaSql("my_schema", true)));
	}
	
	@Test
	void tableReturnsProvider() throws SqlException {
		assertNotNull(database(recordingDataSource()).table(sampleTable()));
	}
	
	@Test
	void fromReturnsQueryProvider() throws SqlException {
		assertNotNull(database(recordingDataSource()).from(sampleTable()));
	}
	
	@Test
	void beginTransactionReturnsActiveWritableTransaction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertNotNull(transaction);
			assertTrue(transaction.isActive());
			assertFalse(transaction.isReadOnly());
		}
	}
	
	@Test
	void beginTransactionWithIsolationAndPropagation() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction(SqlIsolationLevel.SERIALIZABLE, PROPAGATION)) {
			assertTrue(transaction.isActive());
			assertEquals(SqlIsolationLevel.SERIALIZABLE, transaction.getIsolationLevel());
		}
	}
	
	@Test
	void beginReadOnlyTransactionReturnsReadOnlyTransaction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginReadOnlyTransaction()) {
			assertTrue(transaction.isActive());
			assertTrue(transaction.isReadOnly());
		}
	}
	
	@Test
	void beginReadOnlyTransactionWithIsolationAndPropagation() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginReadOnlyTransaction(LEVEL, PROPAGATION)) {
			assertTrue(transaction.isActive());
			assertTrue(transaction.isReadOnly());
		}
	}
	
	@Test
	void inTransactionCommitsAndReturnsResult() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		String result = database(source).inTransaction(transaction -> "ok");
		assertEquals("ok", result);
		assertEquals(1, source.commitCount());
		assertEquals(0, source.rollbackCount());
	}
	
	@Test
	void inTransactionRollsBackOnException() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlDatabase database = database(source);
		assertThrows(IllegalStateException.class, () -> database.inTransaction(transaction -> {
			throw new IllegalStateException("boom");
		}));
		assertEquals(1, source.rollbackCount());
		assertEquals(0, source.commitCount());
	}
	
	@Test
	void inTransactionRethrowsSqlExceptionDirectly() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlDatabase database = database(source);
		SqlException thrown = new SqlException("boom");
		assertSame(thrown, assertThrows(SqlException.class, () -> database.inTransaction(transaction -> {
			throw thrown;
		})));
		assertEquals(1, source.rollbackCount());
	}
	
	@Test
	void inTransactionWrapsCheckedException() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlDatabase database = database(source);
		SqlException thrown = assertThrows(SqlException.class, () -> database.inTransaction(transaction -> {
			SqlDatabaseTest.sneakyThrow(new IOException("checked failure"));
			return null;
		}));
		assertInstanceOf(IOException.class, thrown.getCause());
		assertEquals(1, source.rollbackCount());
	}
	
	@Test
	void inTransactionWithSupplierCommits() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlDatabase database = database(source);
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertEquals(42, database.inTransaction(transaction, () -> 42));
		}
		assertEquals(1, source.commitCount());
	}
	
	@Test
	void openSessionReturnsOpenSession() throws SqlException {
		SqlSession session = database(recordingDataSource()).openSession();
		assertNotNull(session);
		assertEquals(0, session.trackedCount());
	}
	
	@Test
	void openSessionWithAuditUserProvider() throws SqlException {
		assertNotNull(database(recordingDataSource()).openSession(SqlAuditUserProvider.of("admin")));
	}
	
	@Test
	void openSessionWithTransaction() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertNotNull(database.openSession(transaction));
		}
	}
	
	@Test
	void openSessionWithTransactionAndAuditUserProvider() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		try (SqlTransaction transaction = database.beginTransaction()) {
			assertNotNull(database.openSession(transaction, SqlAuditUserProvider.of("admin")));
		}
	}
	
	@Test
	void closeWithAutoCloseDisabledDoesNothing() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(recordingDataSource(), DIALECT).autoCloseDataSource(false).build();
		assertDoesNotThrow(database::close);
	}
	
	@Test
	void closeClosesCloseableDataSource() throws SqlException {
		CloseableDataSource source = closeableDataSource(false);
		SqlDatabase database = SqlDatabase.builder(source, DIALECT).autoCloseDataSource(true).build();
		database.close();
		assertTrue(source.closed());
	}
	
	@Test
	void closeClosesAutoCloseableDataSource() throws SqlException {
		AutoCloseableDataSource source = autoCloseableDataSource(false);
		SqlDatabase database = SqlDatabase.builder(source, DIALECT).autoCloseDataSource(true).build();
		database.close();
		assertTrue(source.closed());
	}
	
	@Test
	void closeWrapsCloseableException() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(closeableDataSource(true), DIALECT).autoCloseDataSource(true).build();
		assertThrows(SqlException.class, database::close);
	}
	
	@Test
	void closeWrapsAutoCloseableException() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(autoCloseableDataSource(true), DIALECT).autoCloseDataSource(true).build();
		assertThrows(SqlException.class, database::close);
	}
	
	@Test
	void closeWithAutoCloseEnabledButPlainDataSource() throws SqlException {
		SqlDatabase database = SqlDatabase.builder(recordingDataSource(), DIALECT).autoCloseDataSource(true).build();
		assertDoesNotThrow(database::close);
	}
	
	@Test
	void closeWithAutoCloseDisabledDoesNotCloseCloseableSource() throws SqlException {
		CloseableDataSource source = closeableDataSource(false);
		SqlDatabase database = SqlDatabase.builder(source, DIALECT).autoCloseDataSource(false).build();
		database.close();
		assertFalse(source.closed());
	}
	
	@Test
	void createSchemaOpensAndReleasesConnection() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlDatabase database = database(source);
		database.createSchema("first");
		database.createSchema("second");
		assertEquals(2, source.executedSql().size());
	}
	
	@Test
	void existsSchemaScansMultipleRowsUntilMatch() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		source.enqueueResultSet(labeledResultSet(List.of(Map.of("TABLE_SCHEM", "a"), Map.of("TABLE_SCHEM", "b"), Map.of("TABLE_SCHEM", "x"))));
		assertTrue(database(source).existsSchema("x"));
	}
	
	@Test
	void inTransactionWrapsRollbackFailureAsSuppressed() throws SqlException {
		RecordingDataSource source = rollbackFailingDataSource();
		SqlDatabase database = database(source);
		SqlException thrown = assertThrows(SqlException.class, () -> database.inTransaction(transaction -> {
			SqlDatabaseTest.sneakyThrow(new IOException("checked failure"));
			return null;
		}));
		assertTrue(thrown.getSuppressed().length >= 1 || thrown.getCause().getSuppressed().length >= 1);
	}
	
	@Test
	void multipleSchemaOperationsAccumulateExecutedSql() throws SqlException {
		RecordingDataSource source = recordingDataSource();
		SqlDatabase database = database(source);
		database.createSchema("a");
		database.createSchemaIfNotExists("b");
		database.dropSchema("c", true);
		assertEquals(List.of(createSchemaSql("a", false), createSchemaSql("b", true), dropSchemaSql("c", true)), source.executedSql());
	}
	
	@Test
	void inTransactionNestedDifferentResultTypes() throws SqlException {
		SqlDatabase database = database(recordingDataSource());
		assertNull(database.inTransaction(transaction -> null));
		assertEquals("value", database.inTransaction(transaction -> "value"));
		assertEquals(42, database.<Integer>inTransaction(transaction -> 42));
	}
	
	@Test
	void inTransactionRollsBackWhenCommitFails() throws SqlException {
		RecordingDataSource source = commitFailingDataSource();
		SqlDatabase database = database(source);
		assertThrows(SqlException.class, () -> database.inTransaction(transaction -> "ok"));
		assertEquals(1, source.rollbackCount());
		assertEquals(0, source.commitCount());
	}
}
