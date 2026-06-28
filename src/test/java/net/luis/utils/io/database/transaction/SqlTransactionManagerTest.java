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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionPropagationException;
import net.luis.utils.io.database.exception.client.transaction.SqlTransactionStateException;
import net.luis.utils.io.database.exception.database.transaction.SqlTransactionConnectionException;
import net.luis.utils.io.database.exception.database.transaction.SqlTransactionSavepointException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTransactionManager}.<br>
 *
 * @author Luis-St
 */
class SqlTransactionManagerTest {
	
	private static final SqlDialect DIALECT = SqlTestFixtures.DIALECT;
	private static final Duration TIMEOUT = Duration.ofSeconds(5);
	private static final SqlIsolationLevel LEVEL = SqlIsolationLevel.READ_COMMITTED;
	
	private static FakeConnection connectionOf(SqlTransaction transaction) {
		return (FakeConnection) transaction.getConnection();
	}
	
	@AfterEach
	void clearThreadLocal() throws Exception {
		Field field = SqlTransactionManager.class.getDeclaredField("CURRENT_TRANSACTION");
		field.setAccessible(true);
		ThreadLocal<?> threadLocal = (ThreadLocal<?>) field.get(null);
		threadLocal.remove();
	}
	
	private SqlTransactionManager manager(FakeDataSource dataSource) {
		return new SqlTransactionManager(dataSource, DIALECT, TIMEOUT);
	}
	
	//region Tier 1 - Constructors
	@Test
	void constructWithThreeArgsUsesDefaultAcquisitionTimeout() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW);
		assertNotNull(inner);
		inner.close();
		outer.close();
	}
	
	@Test
	void constructWithFourArgs() {
		assertNotNull(new SqlTransactionManager(new FakeDataSource(), DIALECT, TIMEOUT, Duration.ofMillis(50)));
	}
	//endregion
	
	//region Tier 2 - Exceptions
	@Test
	void constructThreeArgsWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(null, DIALECT, TIMEOUT));
	}
	
	@Test
	void constructThreeArgsWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(new FakeDataSource(), null, TIMEOUT));
	}
	
	@Test
	void constructThreeArgsWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(new FakeDataSource(), DIALECT, null));
	}
	
	@Test
	void constructFourArgsWithNullDataSource() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(null, DIALECT, TIMEOUT, TIMEOUT));
	}
	
	@Test
	void constructFourArgsWithNullDialect() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(new FakeDataSource(), null, TIMEOUT, TIMEOUT));
	}
	
	@Test
	void constructFourArgsWithNullQueryTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(new FakeDataSource(), DIALECT, null, TIMEOUT));
	}
	
	@Test
	void constructFourArgsWithNullAcquisitionTimeout() {
		assertThrows(NullPointerException.class, () -> new SqlTransactionManager(new FakeDataSource(), DIALECT, TIMEOUT, null));
	}
	
	@Test
	void beginWithNullPropagationThrows() {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		assertThrows(NullPointerException.class, () -> manager.begin(false, LEVEL, null));
	}
	
	@Test
	void beginWithNullIsolationLevelThrows() {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		assertThrows(NullPointerException.class, () -> manager.begin(false, null, SqlPropagation.REQUIRED));
	}
	
	@Test
	void beginNestedWithoutActiveTransactionThrows() {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		assertThrows(SqlTransactionPropagationException.class, () -> manager.begin(false, LEVEL, SqlPropagation.NESTED));
	}
	
	@Test
	void beginMandatoryWithoutActiveTransactionThrows() {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		assertThrows(SqlTransactionPropagationException.class, () -> manager.begin(false, LEVEL, SqlPropagation.MANDATORY));
	}
	
	@Test
	void beginNeverWithActiveTransactionThrows() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		assertThrows(SqlTransactionPropagationException.class, () -> manager.begin(false, LEVEL, SqlPropagation.NEVER));
		outer.close();
	}
	
	@Test
	void beginDisableAutoCommitFailureWrapped() {
		FakeConnection connection = new FakeConnection();
		connection.failSetAutoCommit = true;
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.factory = () -> connection;
		SqlTransactionManager manager = this.manager(dataSource);
		assertThrows(SqlTransactionConnectionException.class, () -> manager.begin(false, LEVEL, SqlPropagation.REQUIRED));
		assertEquals(1, connection.closeCount);
	}
	
	@Test
	void beginEnableAutoCommitFailureWrapped() {
		FakeConnection connection = new FakeConnection();
		connection.failSetAutoCommit = true;
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.factory = () -> connection;
		SqlTransactionManager manager = this.manager(dataSource);
		assertThrows(SqlTransactionConnectionException.class, () -> manager.begin(false, LEVEL, SqlPropagation.SUPPORTS));
		assertEquals(1, connection.closeCount);
	}
	
	@Test
	void beginConfigureConnectionFailureWrapped() {
		FakeConnection connection = new FakeConnection();
		connection.failSetTransactionIsolation = true;
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.factory = () -> connection;
		SqlTransactionManager manager = this.manager(dataSource);
		assertThrows(SqlTransactionConnectionException.class, () -> manager.begin(false, LEVEL, SqlPropagation.REQUIRED));
		assertEquals(1, connection.closeCount);
	}
	
	@Test
	void beginAcquireDirectFailureWrapped() {
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.throwSqlOnCall = 1;
		SqlTransactionManager manager = this.manager(dataSource);
		assertThrows(SqlTransactionConnectionException.class, () -> manager.begin(false, LEVEL, SqlPropagation.REQUIRED));
	}
	
	@Test
	void beginNestedSavepointFailureWrapped() throws SqlException {
		FakeConnection connection = new FakeConnection();
		connection.failSetSavepoint = true;
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.factory = () -> connection;
		SqlTransactionManager manager = this.manager(dataSource);
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		assertThrows(SqlTransactionSavepointException.class, () -> manager.begin(false, LEVEL, SqlPropagation.NESTED));
		outer.close();
	}
	//endregion
	
	//region Tier 3 - Branch coverage
	@Test
	void beginRequiredWithoutCurrentCreatesNewTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		assertTrue(transaction.isActive());
		FakeConnection connection = connectionOf(transaction);
		assertTrue(connection.autoCommitCalls.contains(false));
		assertTrue(connection.readOnlyCalls.contains(false));
		assertTrue(connection.isolationCalls.contains(LEVEL.jdbcLevel()));
		transaction.close();
	}
	
	@Test
	void beginRequiredJoinsActiveTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		assertSame(outer.getConnection(), inner.getConnection());
		inner.commit();
		assertEquals(0, connectionOf(outer).commitCount);
		inner.close();
		outer.close();
	}
	
	@Test
	void beginRequiredJoinWithDifferentIsolationLevel() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, SqlIsolationLevel.READ_COMMITTED, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, SqlIsolationLevel.SERIALIZABLE, SqlPropagation.REQUIRED);
		assertSame(outer.getConnection(), inner.getConnection());
		assertEquals(SqlIsolationLevel.READ_COMMITTED, inner.getIsolationLevel());
		inner.close();
		outer.close();
	}
	
	@Test
	void beginRequiresNewWithoutCurrentCreatesNewTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW);
		assertTrue(transaction.isActive());
		assertNull(transaction.getSuspended());
		transaction.close();
	}
	
	@Test
	void beginRequiresNewSuspendsCurrentTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW);
		assertSame(outer, inner.getSuspended());
		assertNotSame(outer.getConnection(), inner.getConnection());
		inner.close();
		outer.close();
	}
	
	@Test
	void beginNestedWithActiveTransactionCreatesSavepoint() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.NESTED);
		assertSame(outer.getConnection(), inner.getConnection());
		inner.commit();
		assertFalse(connectionOf(outer).releasedSavepoints.isEmpty());
		inner.close();
		outer.close();
	}
	
	@Test
	void beginSupportsJoinsActiveTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.SUPPORTS);
		assertSame(outer.getConnection(), inner.getConnection());
		inner.close();
		outer.close();
	}
	
	@Test
	void beginSupportsWithoutCurrentCreatesNonTransactional() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, LEVEL, SqlPropagation.SUPPORTS);
		assertThrows(SqlTransactionStateException.class, () -> transaction.savepoint("s"));
		transaction.close();
	}
	
	@Test
	void beginNotSupportedWithoutCurrentCreatesNonTransactional() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, LEVEL, SqlPropagation.NOT_SUPPORTED);
		assertNull(transaction.getSuspended());
		assertThrows(SqlTransactionStateException.class, () -> transaction.savepoint("s"));
		transaction.close();
	}
	
	@Test
	void beginNotSupportedSuspendsCurrentTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.NOT_SUPPORTED);
		assertSame(outer, inner.getSuspended());
		inner.close();
		outer.close();
	}
	
	@Test
	void beginMandatoryJoinsActiveTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.MANDATORY);
		assertSame(outer.getConnection(), inner.getConnection());
		inner.close();
		outer.close();
	}
	
	@Test
	void beginNeverWithoutCurrentCreatesNonTransactional() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, LEVEL, SqlPropagation.NEVER);
		assertThrows(SqlTransactionStateException.class, () -> transaction.savepoint("s"));
		transaction.close();
	}
	
	@Test
	void restoreRemovesThreadLocalWhenNoSuspended() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		transaction.close();
		assertThrows(SqlTransactionPropagationException.class, () -> manager.begin(false, LEVEL, SqlPropagation.MANDATORY));
	}
	
	@Test
	void restoreReinstatesSuspendedTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW);
		inner.close();
		SqlTransaction mandatory = assertDoesNotThrow(() -> manager.begin(false, LEVEL, SqlPropagation.MANDATORY));
		mandatory.close();
		outer.close();
	}
	
	@Test
	void beginRegistersRestoreListener() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction mandatory = assertDoesNotThrow(() -> manager.begin(false, LEVEL, SqlPropagation.MANDATORY));
		mandatory.close();
		outer.close();
	}
	//endregion
	
	//region Tier 4 - Simple inputs
	@Test
	void defaultAcquisitionTimeoutConstantValue() {
		assertEquals(Duration.ofSeconds(10), SqlTransactionManager.DEFAULT_CONNECTION_ACQUISITION_TIMEOUT);
	}
	
	@Test
	void beginReadOnlyFlagPropagatedToTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction readOnly = manager.begin(true, LEVEL, SqlPropagation.REQUIRES_NEW);
		assertTrue(readOnly.isReadOnly());
		readOnly.close();
		SqlTransaction writable = manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW);
		assertFalse(writable.isReadOnly());
		writable.close();
	}
	
	@Test
	void beginIsolationLevelPropagatedToTransaction() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction transaction = manager.begin(false, SqlIsolationLevel.SERIALIZABLE, SqlPropagation.REQUIRES_NEW);
		assertEquals(SqlIsolationLevel.SERIALIZABLE, transaction.getIsolationLevel());
		transaction.close();
	}
	//endregion
	
	//region Tier 5 - Complex inputs
	@Test
	void nestedRequiresNewRestoresAcrossTwoLevels() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW);
		inner.close();
		SqlTransaction joined = assertDoesNotThrow(() -> manager.begin(false, LEVEL, SqlPropagation.MANDATORY));
		assertSame(outer.getConnection(), joined.getConnection());
		joined.close();
		outer.close();
	}
	
	@Test
	void joiningTransactionSharesOuterConnection() throws SqlException {
		SqlTransactionManager manager = this.manager(new FakeDataSource());
		SqlTransaction outer = manager.begin(true, SqlIsolationLevel.SERIALIZABLE, SqlPropagation.REQUIRED);
		SqlTransaction inner = manager.begin(false, LEVEL, SqlPropagation.SUPPORTS);
		assertSame(outer.getConnection(), inner.getConnection());
		assertTrue(inner.isReadOnly());
		assertEquals(SqlIsolationLevel.SERIALIZABLE, inner.getIsolationLevel());
		inner.close();
		outer.close();
	}
	
	@Test
	void boundedAcquireWrapsExecutionSqlException() throws SqlException {
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.throwSqlOnCall = 2;
		SqlTransactionManager manager = this.manager(dataSource);
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		assertThrows(SqlTransactionConnectionException.class, () -> manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW));
		outer.close();
	}
	
	@Test
	void boundedAcquireWrapsExecutionNonSqlException() throws SqlException {
		FakeDataSource dataSource = new FakeDataSource();
		dataSource.throwRuntimeOnCall = 2;
		SqlTransactionManager manager = this.manager(dataSource);
		SqlTransaction outer = manager.begin(false, LEVEL, SqlPropagation.REQUIRED);
		SqlTransactionConnectionException exception = assertThrows(SqlTransactionConnectionException.class, () -> manager.begin(false, LEVEL, SqlPropagation.REQUIRES_NEW));
		assertInstanceOf(SQLException.class, exception.getCause());
		outer.close();
	}
	//endregion
}
