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

package net.luis.utils.io.database.transaction;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionStateException;
import net.luis.utils.io.database.exception.database.transaction.*;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTableProvider;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Savepoint;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTransaction}.<br>
 *
 * @author Luis-St
 */
class SqlTransactionTest {
	
	private static final SqlDialect DIALECT = SqlTestFixtures.DIALECT;
	private static final Duration TIMEOUT = Duration.ofSeconds(5);
	private static final SqlIsolationLevel LEVEL = SqlIsolationLevel.READ_COMMITTED;
	
	private static String savepointName(Savepoint savepoint) {
		try {
			return savepoint.getSavepointName();
		} catch (Exception e) {
			return null;
		}
	}
	
	private SqlTransaction owning(FakeConnection connection) throws SqlException {
		return new SqlTransaction(connection, DIALECT, false, TIMEOUT, LEVEL, true, true, false, null);
	}
	
	private SqlTransaction joining(FakeConnection connection) throws SqlException {
		return new SqlTransaction(connection, DIALECT, false, TIMEOUT, LEVEL, false, false, false, null);
	}
	
	private SqlTransaction nonTransactional(FakeConnection connection) throws SqlException {
		return new SqlTransaction(connection, DIALECT, false, TIMEOUT, LEVEL, true, false, true, null);
	}
	
	private SqlTransaction nested(FakeConnection connection, Savepoint savepoint) throws SqlException {
		SqlTransaction transaction = new SqlTransaction(connection, DIALECT, false, TIMEOUT, LEVEL, false, false, false, null);
		transaction.setNestedSavepoint(savepoint);
		return transaction;
	}
	
	//region Tier 1 - Constructors
	@Test
	void constructNonOwningTransaction() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.joining(connection);
		assertTrue(transaction.isActive());
		assertSame(connection, transaction.getConnection());
		assertSame(DIALECT, transaction.getDialect());
	}
	//endregion
	
	@Test
	void constructOwningReadsOriginalConnectionState() {
		FakeConnection connection = new FakeConnection();
		connection.originalAutoCommit = true;
		connection.originalReadOnly = false;
		connection.originalIsolation = Connection.TRANSACTION_READ_COMMITTED;
		assertDoesNotThrow(() -> this.owning(connection));
	}
	
	//region Tier 2 - Exceptions
	@Test
	void constructWithNullConnection() {
		assertThrows(NullPointerException.class, () -> new SqlTransaction(null, DIALECT, false, TIMEOUT, LEVEL, false, false, false, null));
	}
	
	@Test
	void constructWithNullDialect() {
		FakeConnection connection = new FakeConnection();
		assertThrows(NullPointerException.class, () -> new SqlTransaction(connection, null, false, TIMEOUT, LEVEL, false, false, false, null));
	}
	
	@Test
	void constructWithNullQueryTimeout() {
		FakeConnection connection = new FakeConnection();
		assertThrows(NullPointerException.class, () -> new SqlTransaction(connection, DIALECT, false, null, LEVEL, false, false, false, null));
	}
	
	@Test
	void constructWithNullIsolationLevel() {
		FakeConnection connection = new FakeConnection();
		assertThrows(NullPointerException.class, () -> new SqlTransaction(connection, DIALECT, false, TIMEOUT, null, false, false, false, null));
	}
	
	@Test
	void constructOwningWithFailingStateReadThrows() {
		FakeConnection connection = new FakeConnection();
		connection.failGetAutoCommit = true;
		assertThrows(SqlTransactionConnectionException.class, () -> this.owning(connection));
	}
	
	@Test
	void setNestedSavepointWithNull() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.setNestedSavepoint(null));
	}
	
	@Test
	void addListenerWithNull() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.addListener(null));
	}
	
	@Test
	void commitWhenNotActiveThrows() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		transaction.commit();
		assertThrows(SqlTransactionStateException.class, transaction::commit);
	}
	
	@Test
	void rollbackWhenNotActiveThrows() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		transaction.commit();
		assertThrows(SqlTransactionStateException.class, transaction::rollback);
	}
	
	@Test
	void rollbackJoiningTransactionThrows() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		assertThrows(SqlTransactionStateException.class, transaction::rollback);
	}
	
	@Test
	void commitConnectionFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failCommit = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlTransactionCommitException.class, transaction::commit);
	}
	
	@Test
	void rollbackConnectionFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failRollback = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlTransactionRollbackException.class, transaction::rollback);
	}
	
	@Test
	void commitNestedSavepointReleaseFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failReleaseSavepoint = true;
		SqlTransaction transaction = this.nested(connection, new FakeSavepoint(1, "n"));
		assertThrows(SqlTransactionSavepointException.class, transaction::commit);
	}
	
	@Test
	void rollbackNestedSavepointFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failRollbackSavepoint = true;
		SqlTransaction transaction = this.nested(connection, new FakeSavepoint(1, "n"));
		assertThrows(SqlTransactionRollbackException.class, transaction::rollback);
	}
	
	@Test
	void rollbackToWithNullSavepoint() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.rollbackTo(null));
	}
	
	@Test
	void rollbackToWhenNotActiveThrows() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		transaction.commit();
		assertThrows(SqlTransactionStateException.class, () -> transaction.rollbackTo(new SqlSavepoint("x")));
	}
	
	@Test
	void rollbackToUnknownSavepointThrows() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(SqlTransactionStateException.class, () -> transaction.rollbackTo(new SqlSavepoint("x")));
	}
	
	@Test
	void rollbackToConnectionFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.savepoint("s");
		connection.failRollbackSavepoint = true;
		assertThrows(SqlTransactionRollbackException.class, () -> transaction.rollbackTo(new SqlSavepoint("s")));
	}
	
	@Test
	void savepointWithNullName() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.savepoint(null));
	}
	
	@Test
	void savepointWhenNotActiveThrows() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		transaction.commit();
		assertThrows(SqlTransactionStateException.class, () -> transaction.savepoint("s"));
	}
	
	@Test
	void savepointDuplicateNameThrows() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		transaction.savepoint("s");
		assertThrows(SqlTransactionStateException.class, () -> transaction.savepoint("s"));
	}
	
	@Test
	void savepointInNonTransactionalThrows() throws SqlException {
		SqlTransaction transaction = this.nonTransactional(new FakeConnection());
		assertThrows(SqlTransactionStateException.class, () -> transaction.savepoint("s"));
	}
	
	@Test
	void savepointConnectionFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failSetSavepoint = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlTransactionSavepointException.class, () -> transaction.savepoint("s"));
	}
	
	@Test
	void createSchemaWithNullName() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.createSchema(null));
	}
	
	@Test
	void createSchemaIfNotExistsWithNullName() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.createSchemaIfNotExists(null));
	}
	
	@Test
	void existsSchemaWithNullName() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.existsSchema(null));
	}
	
	@Test
	void dropSchemaWithNullName() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.dropSchema(null, false));
	}
	
	@Test
	void tableWithNullTable() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.table(null));
	}
	
	@Test
	void fromWithNullTable() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		assertThrows(NullPointerException.class, () -> transaction.from(null));
	}
	
	@Test
	void createSchemaStatementFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failExecute = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlException.class, () -> transaction.createSchema("s"));
	}
	
	@Test
	void createSchemaIfNotExistsStatementFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failExecute = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlException.class, () -> transaction.createSchemaIfNotExists("s"));
	}
	
	@Test
	void existsSchemaQueryFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failGetSchemas = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlException.class, () -> transaction.existsSchema("s"));
	}
	
	@Test
	void dropSchemaStatementFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failExecute = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlException.class, () -> transaction.dropSchema("s", false));
	}
	
	@Test
	void closeConnectionFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failClose = true;
		SqlTransaction transaction = this.owning(connection);
		assertThrows(SqlTransactionConnectionException.class, transaction::close);
	}
	//endregion
	
	@Test
	void closeListenerFailureWrapped() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterClose() {
				throw new IllegalStateException("boom");
			}
		});
		assertThrows(SqlClientException.class, transaction::close);
	}
	
	//region Tier 3 - Branch coverage
	@Test
	void initialStateIsActive() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		assertTrue(transaction.isActive());
		assertFalse(transaction.isCommitted());
		assertFalse(transaction.isRolledBack());
	}
	
	@Test
	void commitNonTransactionalKeepsActiveAndFires() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.nonTransactional(connection);
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				fired.set(true);
			}
		});
		transaction.commit();
		assertTrue(transaction.isActive());
		assertTrue(fired.get());
		assertEquals(0, connection.commitCount);
	}
	
	@Test
	void commitJoiningMarksCommittedWithoutConnectionCommit() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.joining(connection);
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				fired.set(true);
			}
		});
		transaction.commit();
		assertTrue(transaction.isCommitted());
		assertTrue(fired.get());
		assertEquals(0, connection.commitCount);
	}
	
	@Test
	void commitOwningCommitsConnection() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				fired.set(true);
			}
		});
		transaction.commit();
		assertEquals(1, connection.commitCount);
		assertTrue(transaction.isCommitted());
		assertTrue(fired.get());
	}
	
	@Test
	void commitNestedReleasesSavepoint() throws SqlException {
		FakeConnection connection = new FakeConnection();
		FakeSavepoint savepoint = new FakeSavepoint(1, "n");
		SqlTransaction transaction = this.nested(connection, savepoint);
		transaction.commit();
		assertTrue(connection.releasedSavepoints.contains(savepoint));
		assertTrue(transaction.isCommitted());
	}
	
	@Test
	void rollbackNonTransactionalKeepsActiveAndFires() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.nonTransactional(connection);
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterRollback() {
				fired.set(true);
			}
		});
		transaction.rollback();
		assertTrue(transaction.isActive());
		assertTrue(fired.get());
	}
	
	@Test
	void rollbackOwningRollsBackConnection() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.rollback();
		assertEquals(1, connection.rollbackCount);
		assertTrue(transaction.isRolledBack());
	}
	
	@Test
	void rollbackNestedRollsBackToSavepoint() throws SqlException {
		FakeConnection connection = new FakeConnection();
		FakeSavepoint savepoint = new FakeSavepoint(1, "n");
		SqlTransaction transaction = this.nested(connection, savepoint);
		transaction.rollback();
		assertTrue(connection.rolledBackToSavepoints.contains(savepoint));
		assertTrue(transaction.isRolledBack());
	}
	
	@Test
	void savepointCreatesAndRegistersName() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		SqlSavepoint savepoint = transaction.savepoint("s");
		assertEquals("s", savepoint.name());
		assertDoesNotThrow(() -> transaction.rollbackTo(savepoint));
	}
	
	@Test
	void rollbackToExistingSavepoint() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		SqlSavepoint savepoint = transaction.savepoint("s");
		transaction.rollbackTo(savepoint);
		assertEquals(1, connection.rolledBackToSavepoints.size());
	}
	
	@Test
	void existsSchemaReturnsTrueOnMatch() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.schemas = TransactionFakes.schemaRowSet("app");
		SqlTransaction transaction = this.owning(connection);
		assertTrue(transaction.existsSchema("app"));
	}
	
	@Test
	void existsSchemaReturnsFalseWhenNoMatch() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.schemas = TransactionFakes.schemaRowSet("other");
		SqlTransaction transaction = this.owning(connection);
		assertFalse(transaction.existsSchema("app"));
	}
	
	@Test
	void existsSchemaReturnsFalseWhenEmpty() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.schemas = TransactionFakes.schemaRowSet();
		SqlTransaction transaction = this.owning(connection);
		assertFalse(transaction.existsSchema("app"));
	}
	
	@Test
	void closeNonOwningOnlyFiresAfterClose() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.joining(connection);
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterClose() {
				fired.set(true);
			}
		});
		transaction.close();
		assertTrue(fired.get());
		assertEquals(0, connection.closeCount);
		assertEquals(0, connection.rollbackCount);
	}
	
	@Test
	void closeOwningRestoresStateAndCloses() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.originalAutoCommit = true;
		connection.originalReadOnly = false;
		connection.originalIsolation = Connection.TRANSACTION_READ_COMMITTED;
		SqlTransaction transaction = this.owning(connection);
		transaction.commit();
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterClose() {
				fired.set(true);
			}
		});
		transaction.close();
		assertEquals(Boolean.TRUE, connection.autoCommitCalls.getLast());
		assertEquals(Boolean.FALSE, connection.readOnlyCalls.getLast());
		assertEquals(Connection.TRANSACTION_READ_COMMITTED, connection.isolationCalls.getLast());
		assertEquals(1, connection.closeCount);
		assertTrue(fired.get());
	}
	
	@Test
	void closeActiveOwningRollsBackFirst() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.close();
		assertEquals(1, connection.rollbackCount);
		assertEquals(1, connection.closeCount);
	}
	
	@Test
	void closeNonTransactionalSkipsRollback() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.nonTransactional(connection);
		transaction.close();
		assertEquals(0, connection.rollbackCount);
		assertEquals(1, connection.closeCount);
	}
	
	@Test
	void closeReleasesNestedSavepoint() throws SqlException {
		FakeConnection connection = new FakeConnection();
		FakeSavepoint savepoint = new FakeSavepoint(1, "n");
		SqlTransaction transaction = this.nested(connection, savepoint);
		transaction.close();
		assertTrue(connection.releasedSavepoints.contains(savepoint));
	}
	
	@Test
	void closeSwallowsNestedSavepointReleaseError() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failReleaseSavepoint = true;
		SqlTransaction transaction = this.nested(connection, new FakeSavepoint(1, "n"));
		assertDoesNotThrow(transaction::close);
	}
	
	@Test
	void createSchemaRendersWithoutIfNotExists() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.createSchema("app");
		assertFalse(connection.executedSql.getFirst().toUpperCase(Locale.ROOT).contains("IF NOT EXISTS"));
	}
	
	@Test
	void createSchemaIfNotExistsRendersIfNotExists() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.createSchemaIfNotExists("app");
		assertTrue(connection.executedSql.getFirst().toUpperCase(Locale.ROOT).contains("IF NOT EXISTS"));
	}
	//endregion
	
	@Test
	void dropSchemaCascadeFlagPropagated() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.dropSchema("app", false);
		transaction.dropSchema("app", true);
		assertFalse(connection.executedSql.get(0).toUpperCase(Locale.ROOT).contains("CASCADE"));
		assertTrue(connection.executedSql.get(1).toUpperCase(Locale.ROOT).contains("CASCADE"));
	}
	
	//region Tier 4 - Simple inputs
	@Test
	void gettersReflectConstructorArgs() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = new SqlTransaction(connection, DIALECT, true, TIMEOUT, SqlIsolationLevel.SERIALIZABLE, false, false, false, null);
		assertSame(connection, transaction.getConnection());
		assertSame(DIALECT, transaction.getDialect());
		assertTrue(transaction.isReadOnly());
		assertEquals(TIMEOUT, transaction.getQueryTimeout());
		assertEquals(SqlIsolationLevel.SERIALIZABLE, transaction.getIsolationLevel());
	}
	
	@Test
	void getSuspendedReturnsNullWhenNone() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		assertNull(transaction.getSuspended());
	}
	
	@Test
	void getSuspendedReturnsProvidedTransaction() throws SqlException {
		SqlTransaction suspended = this.joining(new FakeConnection());
		SqlTransaction transaction = new SqlTransaction(new FakeConnection(), DIALECT, false, TIMEOUT, LEVEL, true, true, false, suspended);
		assertSame(suspended, transaction.getSuspended());
	}
	
	@Test
	void tableReturnsProvider() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		SqlTableProvider<Object> provider = transaction.table(SqlTestFixtures.sampleTable());
		assertNotNull(provider);
	}
	
	@Test
	void fromReturnsProvider() throws SqlException {
		SqlTransaction transaction = this.owning(new FakeConnection());
		SqlQueryProvider<Object> provider = transaction.from(SqlTestFixtures.sampleTable());
		assertNotNull(provider);
	}
	//endregion
	
	@Test
	void addListenerFiresOnCommit() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		AtomicBoolean fired = new AtomicBoolean(false);
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				fired.set(true);
			}
		});
		transaction.commit();
		assertTrue(fired.get());
	}
	
	//region Tier 5 - Complex inputs
	@Test
	void commitThenCloseFiresCommitAndCloseListeners() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.joining(connection);
		List<String> order = new ArrayList<>();
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				order.add("commit");
			}
			
			@Override
			public void afterClose() {
				order.add("close");
			}
		});
		transaction.commit();
		transaction.close();
		assertEquals(List.of("commit", "close"), order);
		assertEquals(0, connection.rollbackCount);
	}
	
	@Test
	void multipleListenersFireInRegistrationOrder() throws SqlException {
		SqlTransaction transaction = this.joining(new FakeConnection());
		List<Integer> order = new ArrayList<>();
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				order.add(1);
			}
		});
		transaction.addListener(new SqlTransactionListener() {
			
			@Override
			public void afterCommit() {
				order.add(2);
			}
		});
		transaction.commit();
		assertEquals(List.of(1, 2), order);
	}
	
	@Test
	void savepointRollbackToRoundTrip() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.savepoint("a");
		transaction.savepoint("b");
		transaction.rollbackTo(new SqlSavepoint("a"));
		assertEquals(1, connection.rolledBackToSavepoints.size());
		Savepoint used = connection.rolledBackToSavepoints.getFirst();
		assertEquals("a", savepointName(used));
	}
	
	@Test
	void rollbackToAfterRollbackToStillUsesRegisteredSavepoint() throws SqlException {
		FakeConnection connection = new FakeConnection();
		SqlTransaction transaction = this.owning(connection);
		transaction.savepoint("a");
		transaction.rollbackTo(new SqlSavepoint("a"));
		transaction.rollbackTo(new SqlSavepoint("a"));
		assertEquals(2, connection.rolledBackToSavepoints.size());
		AtomicInteger matching = new AtomicInteger();
		connection.rolledBackToSavepoints.forEach(savepoint -> {
			if ("a".equals(savepointName(savepoint))) {
				matching.incrementAndGet();
			}
		});
		assertEquals(2, matching.get());
	}
	//endregion
}
