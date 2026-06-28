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

import javax.sql.DataSource;
import javax.sql.rowset.*;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Hand-written JDBC fakes (real implementing classes, no mocks) backing the transaction tests.<br>
 * <p>
 *     {@link FakeConnection} records every connection operation the transaction code performs and can
 *     be configured to throw {@link SQLException} on any chosen method, exercising every {@code catch}
 *     branch. {@link FakeDataSource} hands out fakes and can throw, block or fail on a chosen call.
 *     The bulk of the JDBC interface surface is stubbed to throw {@link UnsupportedOperationException}
 *     because the production code never touches it.
 * </p>
 *
 * @author Luis-St
 */
final class TransactionFakes {
	
	private TransactionFakes() {}
	
	/**
	 * Builds a {@link CachedRowSet} exposing a {@code TABLE_SCHEM} column, one row per given name,
	 * positioned before the first row (so {@code while (next())} iterates them).<br>
	 *
	 * @param schemas The schema names to expose (none for an empty result)
	 * @return A populated cached row set positioned before the first row
	 */
	static CachedRowSet schemaRowSet(String... schemas) {
		try {
			RowSetMetaDataImpl metaData = new RowSetMetaDataImpl();
			metaData.setColumnCount(1);
			metaData.setColumnType(1, Types.VARCHAR);
			metaData.setColumnName(1, "TABLE_SCHEM");
			
			CachedRowSet rowSet = RowSetProvider.newFactory().createCachedRowSet();
			rowSet.setMetaData(metaData);
			for (String schema : schemas) {
				rowSet.moveToInsertRow();
				rowSet.updateString(1, schema);
				rowSet.insertRow();
			}
			rowSet.moveToCurrentRow();
			rowSet.beforeFirst();
			return rowSet;
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to build schema row set fixture", e);
		}
	}
}

/**
 * A real {@link Savepoint} carrying an id and optional name.<br>
 *
 * @author Luis-St
 */
final class FakeSavepoint implements Savepoint {
	
	private final int id;
	private final String name;
	
	FakeSavepoint(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public int getSavepointId() {
		return this.id;
	}
	
	@Override
	public String getSavepointName() {
		return this.name;
	}
}

/**
 * A recording {@link Connection} fake configurable to throw on any operation.<br>
 *
 * @author Luis-St
 */
class FakeConnection implements Connection {
	
	final List<String> executedSql = new ArrayList<>();
	final List<Boolean> autoCommitCalls = new ArrayList<>();
	final List<Boolean> readOnlyCalls = new ArrayList<>();
	final List<Integer> isolationCalls = new ArrayList<>();
	final List<Savepoint> releasedSavepoints = new ArrayList<>();
	final List<Savepoint> rolledBackToSavepoints = new ArrayList<>();
	private int savepointCounter;
	int commitCount;
	int rollbackCount;
	int closeCount;
	boolean originalAutoCommit = true;
	boolean originalReadOnly;
	int originalIsolation = Connection.TRANSACTION_READ_COMMITTED;
	boolean failGetAutoCommit;
	boolean failSetAutoCommit;
	boolean failSetReadOnly;
	boolean failSetTransactionIsolation;
	boolean failCommit;
	boolean failRollback;
	boolean failRollbackSavepoint;
	boolean failSetSavepoint;
	boolean failReleaseSavepoint;
	boolean failClose;
	boolean failExecute;
	boolean failGetSchemas;
	CachedRowSet schemas = TransactionFakes.schemaRowSet();
	
	@Override
	public boolean getAutoCommit() throws SQLException {
		if (this.failGetAutoCommit) {
			throw new SQLException("getAutoCommit failed");
		}
		return this.originalAutoCommit;
	}
	
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (this.failSetAutoCommit) {
			throw new SQLException("setAutoCommit failed");
		}
		this.autoCommitCalls.add(autoCommit);
	}
	
	@Override
	public boolean isReadOnly() {
		return this.originalReadOnly;
	}
	
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if (this.failSetReadOnly) {
			throw new SQLException("setReadOnly failed");
		}
		this.readOnlyCalls.add(readOnly);
	}
	
	@Override
	public int getTransactionIsolation() {
		return this.originalIsolation;
	}
	
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if (this.failSetTransactionIsolation) {
			throw new SQLException("setTransactionIsolation failed");
		}
		this.isolationCalls.add(level);
	}
	
	@Override
	public void commit() throws SQLException {
		if (this.failCommit) {
			throw new SQLException("commit failed");
		}
		this.commitCount++;
	}
	
	@Override
	public void rollback() throws SQLException {
		if (this.failRollback) {
			throw new SQLException("rollback failed");
		}
		this.rollbackCount++;
	}
	
	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		if (this.failRollbackSavepoint) {
			throw new SQLException("rollback(savepoint) failed");
		}
		this.rolledBackToSavepoints.add(savepoint);
	}
	
	@Override
	public Savepoint setSavepoint() throws SQLException {
		if (this.failSetSavepoint) {
			throw new SQLException("setSavepoint failed");
		}
		return new FakeSavepoint(++this.savepointCounter, null);
	}
	
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		if (this.failSetSavepoint) {
			throw new SQLException("setSavepoint(name) failed");
		}
		return new FakeSavepoint(++this.savepointCounter, name);
	}
	
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (this.failReleaseSavepoint) {
			throw new SQLException("releaseSavepoint failed");
		}
		this.releasedSavepoints.add(savepoint);
	}
	
	@Override
	public void close() throws SQLException {
		this.closeCount++;
		if (this.failClose) {
			throw new SQLException("close failed");
		}
	}
	
	@Override
	public boolean isClosed() {
		return this.closeCount > 0;
	}
	
	@Override
	public Statement createStatement() {
		return new FakeStatement(this);
	}
	
	@Override
	public DatabaseMetaData getMetaData() {
		return new FakeDatabaseMetaData(this);
	}
	
	@Override
	public boolean isValid(int a0) {
		throw new UnsupportedOperationException("isValid");
	}
	
	@Override
	public void abort(Executor a0) {
		throw new UnsupportedOperationException("abort");
	}
	
	@Override
	public Statement createStatement(int a0, int a1, int a2) {
		throw new UnsupportedOperationException("createStatement");
	}
	
	@Override
	public Statement createStatement(int a0, int a1) {
		throw new UnsupportedOperationException("createStatement");
	}
	
	@Override
	public PreparedStatement prepareStatement(String a0, String[] a1) {
		throw new UnsupportedOperationException("prepareStatement");
	}
	
	@Override
	public PreparedStatement prepareStatement(String a0, int a1) {
		throw new UnsupportedOperationException("prepareStatement");
	}
	
	@Override
	public PreparedStatement prepareStatement(String a0, int a1, int a2) {
		throw new UnsupportedOperationException("prepareStatement");
	}
	
	@Override
	public PreparedStatement prepareStatement(String a0, int[] a1) {
		throw new UnsupportedOperationException("prepareStatement");
	}
	
	@Override
	public PreparedStatement prepareStatement(String a0) {
		throw new UnsupportedOperationException("prepareStatement");
	}
	
	@Override
	public PreparedStatement prepareStatement(String a0, int a1, int a2, int a3) {
		throw new UnsupportedOperationException("prepareStatement");
	}
	
	@Override
	public CallableStatement prepareCall(String a0, int a1, int a2) {
		throw new UnsupportedOperationException("prepareCall");
	}
	
	@Override
	public CallableStatement prepareCall(String a0) {
		throw new UnsupportedOperationException("prepareCall");
	}
	
	@Override
	public CallableStatement prepareCall(String a0, int a1, int a2, int a3) {
		throw new UnsupportedOperationException("prepareCall");
	}
	
	@Override
	public String nativeSQL(String a0) {
		throw new UnsupportedOperationException("nativeSQL");
	}
	
	@Override
	public String getCatalog() {
		throw new UnsupportedOperationException("getCatalog");
	}
	
	@Override
	public void setCatalog(String a0) {
		throw new UnsupportedOperationException("setCatalog");
	}
	
	@Override
	public SQLWarning getWarnings() {
		throw new UnsupportedOperationException("getWarnings");
	}
	
	@Override
	public void clearWarnings() {
		throw new UnsupportedOperationException("clearWarnings");
	}
	
	@Override
	public Map<String, Class<?>> getTypeMap() {
		throw new UnsupportedOperationException("getTypeMap");
	}
	
	@Override
	public void setTypeMap(Map<String, Class<?>> a0) {
		throw new UnsupportedOperationException("setTypeMap");
	}
	
	@Override
	public int getHoldability() {
		throw new UnsupportedOperationException("getHoldability");
	}
	
	@Override
	public void setHoldability(int a0) {
		throw new UnsupportedOperationException("setHoldability");
	}
	
	@Override
	public Clob createClob() {
		throw new UnsupportedOperationException("createClob");
	}
	
	@Override
	public Blob createBlob() {
		throw new UnsupportedOperationException("createBlob");
	}
	
	@Override
	public NClob createNClob() {
		throw new UnsupportedOperationException("createNClob");
	}
	
	@Override
	public SQLXML createSQLXML() {
		throw new UnsupportedOperationException("createSQLXML");
	}
	
	@Override
	public void setClientInfo(String a0, String a1) {
		throw new UnsupportedOperationException("setClientInfo");
	}
	
	@Override
	public Properties getClientInfo() {
		throw new UnsupportedOperationException("getClientInfo");
	}
	
	@Override
	public void setClientInfo(Properties a0) {
		throw new UnsupportedOperationException("setClientInfo");
	}
	
	@Override
	public String getClientInfo(String a0) {
		throw new UnsupportedOperationException("getClientInfo");
	}
	
	@Override
	public Array createArrayOf(String a0, Object[] a1) {
		throw new UnsupportedOperationException("createArrayOf");
	}
	
	@Override
	public Struct createStruct(String a0, Object[] a1) {
		throw new UnsupportedOperationException("createStruct");
	}
	
	@Override
	public String getSchema() {
		throw new UnsupportedOperationException("getSchema");
	}
	
	@Override
	public void setSchema(String a0) {
		throw new UnsupportedOperationException("setSchema");
	}
	
	@Override
	public void setNetworkTimeout(Executor a0, int a1) {
		throw new UnsupportedOperationException("setNetworkTimeout");
	}
	
	@Override
	public int getNetworkTimeout() {
		throw new UnsupportedOperationException("getNetworkTimeout");
	}
	
	@Override
	public <T> T unwrap(Class<T> a0) {
		throw new UnsupportedOperationException("unwrap");
	}
	
	@Override
	public boolean isWrapperFor(Class<?> a0) {
		throw new UnsupportedOperationException("isWrapperFor");
	}
}

/**
 * A {@link Statement} fake recording the SQL passed to {@link #execute(String)}.<br>
 *
 * @author Luis-St
 */
class FakeStatement implements Statement {
	
	private final FakeConnection connection;
	
	FakeStatement(FakeConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public boolean execute(String sql) throws SQLException {
		this.connection.executedSql.add(sql);
		if (this.connection.failExecute) {
			throw new SQLException("execute failed");
		}
		return false;
	}
	
	@Override
	public void close() {}
	
	@Override
	public boolean execute(String a0, String[] a1) {
		throw new UnsupportedOperationException("execute");
	}
	
	@Override
	public boolean execute(String a0, int[] a1) {
		throw new UnsupportedOperationException("execute");
	}
	
	@Override
	public boolean execute(String a0, int a1) {
		throw new UnsupportedOperationException("execute");
	}
	
	@Override
	public void cancel() {
		throw new UnsupportedOperationException("cancel");
	}
	
	@Override
	public ResultSet executeQuery(String a0) {
		throw new UnsupportedOperationException("executeQuery");
	}
	
	@Override
	public int executeUpdate(String a0, String[] a1) {
		throw new UnsupportedOperationException("executeUpdate");
	}
	
	@Override
	public int executeUpdate(String a0, int a1) {
		throw new UnsupportedOperationException("executeUpdate");
	}
	
	@Override
	public int executeUpdate(String a0) {
		throw new UnsupportedOperationException("executeUpdate");
	}
	
	@Override
	public int executeUpdate(String a0, int[] a1) {
		throw new UnsupportedOperationException("executeUpdate");
	}
	
	@Override
	public int getMaxFieldSize() {
		throw new UnsupportedOperationException("getMaxFieldSize");
	}
	
	@Override
	public void setMaxFieldSize(int a0) {
		throw new UnsupportedOperationException("setMaxFieldSize");
	}
	
	@Override
	public int getMaxRows() {
		throw new UnsupportedOperationException("getMaxRows");
	}
	
	@Override
	public void setMaxRows(int a0) {
		throw new UnsupportedOperationException("setMaxRows");
	}
	
	@Override
	public void setEscapeProcessing(boolean a0) {
		throw new UnsupportedOperationException("setEscapeProcessing");
	}
	
	@Override
	public int getQueryTimeout() {
		throw new UnsupportedOperationException("getQueryTimeout");
	}
	
	@Override
	public void setQueryTimeout(int a0) {
		throw new UnsupportedOperationException("setQueryTimeout");
	}
	
	@Override
	public SQLWarning getWarnings() {
		throw new UnsupportedOperationException("getWarnings");
	}
	
	@Override
	public void clearWarnings() {
		throw new UnsupportedOperationException("clearWarnings");
	}
	
	@Override
	public void setCursorName(String a0) {
		throw new UnsupportedOperationException("setCursorName");
	}
	
	@Override
	public ResultSet getResultSet() {
		throw new UnsupportedOperationException("getResultSet");
	}
	
	@Override
	public int getUpdateCount() {
		throw new UnsupportedOperationException("getUpdateCount");
	}
	
	@Override
	public boolean getMoreResults(int a0) {
		throw new UnsupportedOperationException("getMoreResults");
	}
	
	@Override
	public boolean getMoreResults() {
		throw new UnsupportedOperationException("getMoreResults");
	}
	
	@Override
	public int getFetchDirection() {
		throw new UnsupportedOperationException("getFetchDirection");
	}
	
	@Override
	public void setFetchDirection(int a0) {
		throw new UnsupportedOperationException("setFetchDirection");
	}
	
	@Override
	public int getFetchSize() {
		throw new UnsupportedOperationException("getFetchSize");
	}
	
	@Override
	public void setFetchSize(int a0) {
		throw new UnsupportedOperationException("setFetchSize");
	}
	
	@Override
	public int getResultSetConcurrency() {
		throw new UnsupportedOperationException("getResultSetConcurrency");
	}
	
	@Override
	public int getResultSetType() {
		throw new UnsupportedOperationException("getResultSetType");
	}
	
	@Override
	public void addBatch(String a0) {
		throw new UnsupportedOperationException("addBatch");
	}
	
	@Override
	public void clearBatch() {
		throw new UnsupportedOperationException("clearBatch");
	}
	
	@Override
	public int[] executeBatch() {
		throw new UnsupportedOperationException("executeBatch");
	}
	
	@Override
	public Connection getConnection() {
		throw new UnsupportedOperationException("getConnection");
	}
	
	@Override
	public ResultSet getGeneratedKeys() {
		throw new UnsupportedOperationException("getGeneratedKeys");
	}
	
	@Override
	public int getResultSetHoldability() {
		throw new UnsupportedOperationException("getResultSetHoldability");
	}
	
	@Override
	public boolean isClosed() {
		throw new UnsupportedOperationException("isClosed");
	}
	
	@Override
	public boolean isPoolable() {
		throw new UnsupportedOperationException("isPoolable");
	}
	
	@Override
	public void setPoolable(boolean a0) {
		throw new UnsupportedOperationException("setPoolable");
	}
	
	@Override
	public void closeOnCompletion() {
		throw new UnsupportedOperationException("closeOnCompletion");
	}
	
	@Override
	public boolean isCloseOnCompletion() {
		throw new UnsupportedOperationException("isCloseOnCompletion");
	}
	
	@Override
	public <T> T unwrap(Class<T> a0) {
		throw new UnsupportedOperationException("unwrap");
	}
	
	@Override
	public boolean isWrapperFor(Class<?> a0) {
		throw new UnsupportedOperationException("isWrapperFor");
	}
}

/**
 * A {@link DatabaseMetaData} fake exposing the connection's schema row set.<br>
 *
 * @author Luis-St
 */
class FakeDatabaseMetaData implements DatabaseMetaData {
	
	private final FakeConnection connection;
	
	FakeDatabaseMetaData(FakeConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public ResultSet getSchemas() throws SQLException {
		if (this.connection.failGetSchemas) {
			throw new SQLException("getSchemas failed");
		}
		return this.connection.schemas;
	}
	
	@Override
	public Connection getConnection() {
		return this.connection;
	}
	
	@Override
	public ResultSet getAttributes(String a0, String a1, String a2, String a3) {
		throw new UnsupportedOperationException("getAttributes");
	}
	
	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException("isReadOnly");
	}
	
	@Override
	public String getURL() {
		throw new UnsupportedOperationException("getURL");
	}
	
	@Override
	public boolean supportsMixedCaseIdentifiers() {
		throw new UnsupportedOperationException("supportsMixedCaseIdentifiers");
	}
	
	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() {
		throw new UnsupportedOperationException("supportsMixedCaseQuotedIdentifiers");
	}
	
	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() {
		throw new UnsupportedOperationException("supportsDataDefinitionAndDataManipulationTransactions");
	}
	
	@Override
	public boolean allProceduresAreCallable() {
		throw new UnsupportedOperationException("allProceduresAreCallable");
	}
	
	@Override
	public boolean allTablesAreSelectable() {
		throw new UnsupportedOperationException("allTablesAreSelectable");
	}
	
	@Override
	public String getUserName() {
		throw new UnsupportedOperationException("getUserName");
	}
	
	@Override
	public boolean nullsAreSortedHigh() {
		throw new UnsupportedOperationException("nullsAreSortedHigh");
	}
	
	@Override
	public boolean nullsAreSortedLow() {
		throw new UnsupportedOperationException("nullsAreSortedLow");
	}
	
	@Override
	public boolean nullsAreSortedAtStart() {
		throw new UnsupportedOperationException("nullsAreSortedAtStart");
	}
	
	@Override
	public boolean nullsAreSortedAtEnd() {
		throw new UnsupportedOperationException("nullsAreSortedAtEnd");
	}
	
	@Override
	public String getDatabaseProductName() {
		throw new UnsupportedOperationException("getDatabaseProductName");
	}
	
	@Override
	public String getDatabaseProductVersion() {
		throw new UnsupportedOperationException("getDatabaseProductVersion");
	}
	
	@Override
	public String getDriverName() {
		throw new UnsupportedOperationException("getDriverName");
	}
	
	@Override
	public String getDriverVersion() {
		throw new UnsupportedOperationException("getDriverVersion");
	}
	
	@Override
	public int getDriverMajorVersion() {
		throw new UnsupportedOperationException("getDriverMajorVersion");
	}
	
	@Override
	public int getDriverMinorVersion() {
		throw new UnsupportedOperationException("getDriverMinorVersion");
	}
	
	@Override
	public boolean usesLocalFiles() {
		throw new UnsupportedOperationException("usesLocalFiles");
	}
	
	@Override
	public boolean usesLocalFilePerTable() {
		throw new UnsupportedOperationException("usesLocalFilePerTable");
	}
	
	@Override
	public boolean storesUpperCaseIdentifiers() {
		throw new UnsupportedOperationException("storesUpperCaseIdentifiers");
	}
	
	@Override
	public boolean storesLowerCaseIdentifiers() {
		throw new UnsupportedOperationException("storesLowerCaseIdentifiers");
	}
	
	@Override
	public boolean storesMixedCaseIdentifiers() {
		throw new UnsupportedOperationException("storesMixedCaseIdentifiers");
	}
	
	@Override
	public boolean storesUpperCaseQuotedIdentifiers() {
		throw new UnsupportedOperationException("storesUpperCaseQuotedIdentifiers");
	}
	
	@Override
	public boolean storesLowerCaseQuotedIdentifiers() {
		throw new UnsupportedOperationException("storesLowerCaseQuotedIdentifiers");
	}
	
	@Override
	public boolean storesMixedCaseQuotedIdentifiers() {
		throw new UnsupportedOperationException("storesMixedCaseQuotedIdentifiers");
	}
	
	@Override
	public String getIdentifierQuoteString() {
		throw new UnsupportedOperationException("getIdentifierQuoteString");
	}
	
	@Override
	public String getSQLKeywords() {
		throw new UnsupportedOperationException("getSQLKeywords");
	}
	
	@Override
	public String getNumericFunctions() {
		throw new UnsupportedOperationException("getNumericFunctions");
	}
	
	@Override
	public String getStringFunctions() {
		throw new UnsupportedOperationException("getStringFunctions");
	}
	
	@Override
	public String getSystemFunctions() {
		throw new UnsupportedOperationException("getSystemFunctions");
	}
	
	@Override
	public String getTimeDateFunctions() {
		throw new UnsupportedOperationException("getTimeDateFunctions");
	}
	
	@Override
	public String getSearchStringEscape() {
		throw new UnsupportedOperationException("getSearchStringEscape");
	}
	
	@Override
	public String getExtraNameCharacters() {
		throw new UnsupportedOperationException("getExtraNameCharacters");
	}
	
	@Override
	public boolean supportsAlterTableWithAddColumn() {
		throw new UnsupportedOperationException("supportsAlterTableWithAddColumn");
	}
	
	@Override
	public boolean supportsAlterTableWithDropColumn() {
		throw new UnsupportedOperationException("supportsAlterTableWithDropColumn");
	}
	
	@Override
	public boolean supportsColumnAliasing() {
		throw new UnsupportedOperationException("supportsColumnAliasing");
	}
	
	@Override
	public boolean nullPlusNonNullIsNull() {
		throw new UnsupportedOperationException("nullPlusNonNullIsNull");
	}
	
	@Override
	public boolean supportsConvert(int a0, int a1) {
		throw new UnsupportedOperationException("supportsConvert");
	}
	
	@Override
	public boolean supportsConvert() {
		throw new UnsupportedOperationException("supportsConvert");
	}
	
	@Override
	public boolean supportsTableCorrelationNames() {
		throw new UnsupportedOperationException("supportsTableCorrelationNames");
	}
	
	@Override
	public boolean supportsDifferentTableCorrelationNames() {
		throw new UnsupportedOperationException("supportsDifferentTableCorrelationNames");
	}
	
	@Override
	public boolean supportsExpressionsInOrderBy() {
		throw new UnsupportedOperationException("supportsExpressionsInOrderBy");
	}
	
	@Override
	public boolean supportsOrderByUnrelated() {
		throw new UnsupportedOperationException("supportsOrderByUnrelated");
	}
	
	@Override
	public boolean supportsGroupBy() {
		throw new UnsupportedOperationException("supportsGroupBy");
	}
	
	@Override
	public boolean supportsGroupByUnrelated() {
		throw new UnsupportedOperationException("supportsGroupByUnrelated");
	}
	
	@Override
	public boolean supportsGroupByBeyondSelect() {
		throw new UnsupportedOperationException("supportsGroupByBeyondSelect");
	}
	
	@Override
	public boolean supportsLikeEscapeClause() {
		throw new UnsupportedOperationException("supportsLikeEscapeClause");
	}
	
	@Override
	public boolean supportsMultipleResultSets() {
		throw new UnsupportedOperationException("supportsMultipleResultSets");
	}
	
	@Override
	public boolean supportsMultipleTransactions() {
		throw new UnsupportedOperationException("supportsMultipleTransactions");
	}
	
	@Override
	public boolean supportsNonNullableColumns() {
		throw new UnsupportedOperationException("supportsNonNullableColumns");
	}
	
	@Override
	public boolean supportsMinimumSQLGrammar() {
		throw new UnsupportedOperationException("supportsMinimumSQLGrammar");
	}
	
	@Override
	public boolean supportsCoreSQLGrammar() {
		throw new UnsupportedOperationException("supportsCoreSQLGrammar");
	}
	
	@Override
	public boolean supportsExtendedSQLGrammar() {
		throw new UnsupportedOperationException("supportsExtendedSQLGrammar");
	}
	
	@Override
	public boolean supportsANSI92EntryLevelSQL() {
		throw new UnsupportedOperationException("supportsANSI92EntryLevelSQL");
	}
	
	@Override
	public boolean supportsANSI92IntermediateSQL() {
		throw new UnsupportedOperationException("supportsANSI92IntermediateSQL");
	}
	
	@Override
	public boolean supportsANSI92FullSQL() {
		throw new UnsupportedOperationException("supportsANSI92FullSQL");
	}
	
	@Override
	public boolean supportsIntegrityEnhancementFacility() {
		throw new UnsupportedOperationException("supportsIntegrityEnhancementFacility");
	}
	
	@Override
	public boolean supportsOuterJoins() {
		throw new UnsupportedOperationException("supportsOuterJoins");
	}
	
	@Override
	public boolean supportsFullOuterJoins() {
		throw new UnsupportedOperationException("supportsFullOuterJoins");
	}
	
	@Override
	public boolean supportsLimitedOuterJoins() {
		throw new UnsupportedOperationException("supportsLimitedOuterJoins");
	}
	
	@Override
	public String getSchemaTerm() {
		throw new UnsupportedOperationException("getSchemaTerm");
	}
	
	@Override
	public String getProcedureTerm() {
		throw new UnsupportedOperationException("getProcedureTerm");
	}
	
	@Override
	public String getCatalogTerm() {
		throw new UnsupportedOperationException("getCatalogTerm");
	}
	
	@Override
	public boolean isCatalogAtStart() {
		throw new UnsupportedOperationException("isCatalogAtStart");
	}
	
	@Override
	public String getCatalogSeparator() {
		throw new UnsupportedOperationException("getCatalogSeparator");
	}
	
	@Override
	public boolean supportsSchemasInDataManipulation() {
		throw new UnsupportedOperationException("supportsSchemasInDataManipulation");
	}
	
	@Override
	public boolean supportsSchemasInProcedureCalls() {
		throw new UnsupportedOperationException("supportsSchemasInProcedureCalls");
	}
	
	@Override
	public boolean supportsSchemasInTableDefinitions() {
		throw new UnsupportedOperationException("supportsSchemasInTableDefinitions");
	}
	
	@Override
	public boolean supportsSchemasInIndexDefinitions() {
		throw new UnsupportedOperationException("supportsSchemasInIndexDefinitions");
	}
	
	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() {
		throw new UnsupportedOperationException("supportsSchemasInPrivilegeDefinitions");
	}
	
	@Override
	public boolean supportsCatalogsInDataManipulation() {
		throw new UnsupportedOperationException("supportsCatalogsInDataManipulation");
	}
	
	@Override
	public boolean supportsCatalogsInProcedureCalls() {
		throw new UnsupportedOperationException("supportsCatalogsInProcedureCalls");
	}
	
	@Override
	public boolean supportsCatalogsInTableDefinitions() {
		throw new UnsupportedOperationException("supportsCatalogsInTableDefinitions");
	}
	
	@Override
	public boolean supportsCatalogsInIndexDefinitions() {
		throw new UnsupportedOperationException("supportsCatalogsInIndexDefinitions");
	}
	
	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() {
		throw new UnsupportedOperationException("supportsCatalogsInPrivilegeDefinitions");
	}
	
	@Override
	public boolean supportsPositionedDelete() {
		throw new UnsupportedOperationException("supportsPositionedDelete");
	}
	
	@Override
	public boolean supportsPositionedUpdate() {
		throw new UnsupportedOperationException("supportsPositionedUpdate");
	}
	
	@Override
	public boolean supportsSelectForUpdate() {
		throw new UnsupportedOperationException("supportsSelectForUpdate");
	}
	
	@Override
	public boolean supportsStoredProcedures() {
		throw new UnsupportedOperationException("supportsStoredProcedures");
	}
	
	@Override
	public boolean supportsSubqueriesInComparisons() {
		throw new UnsupportedOperationException("supportsSubqueriesInComparisons");
	}
	
	@Override
	public boolean supportsSubqueriesInExists() {
		throw new UnsupportedOperationException("supportsSubqueriesInExists");
	}
	
	@Override
	public boolean supportsSubqueriesInIns() {
		throw new UnsupportedOperationException("supportsSubqueriesInIns");
	}
	
	@Override
	public boolean supportsSubqueriesInQuantifieds() {
		throw new UnsupportedOperationException("supportsSubqueriesInQuantifieds");
	}
	
	@Override
	public boolean supportsCorrelatedSubqueries() {
		throw new UnsupportedOperationException("supportsCorrelatedSubqueries");
	}
	
	@Override
	public boolean supportsUnion() {
		throw new UnsupportedOperationException("supportsUnion");
	}
	
	@Override
	public boolean supportsUnionAll() {
		throw new UnsupportedOperationException("supportsUnionAll");
	}
	
	@Override
	public boolean supportsOpenCursorsAcrossCommit() {
		throw new UnsupportedOperationException("supportsOpenCursorsAcrossCommit");
	}
	
	@Override
	public boolean supportsOpenCursorsAcrossRollback() {
		throw new UnsupportedOperationException("supportsOpenCursorsAcrossRollback");
	}
	
	@Override
	public boolean supportsOpenStatementsAcrossCommit() {
		throw new UnsupportedOperationException("supportsOpenStatementsAcrossCommit");
	}
	
	@Override
	public boolean supportsOpenStatementsAcrossRollback() {
		throw new UnsupportedOperationException("supportsOpenStatementsAcrossRollback");
	}
	
	@Override
	public int getMaxBinaryLiteralLength() {
		throw new UnsupportedOperationException("getMaxBinaryLiteralLength");
	}
	
	@Override
	public int getMaxCharLiteralLength() {
		throw new UnsupportedOperationException("getMaxCharLiteralLength");
	}
	
	@Override
	public int getMaxColumnNameLength() {
		throw new UnsupportedOperationException("getMaxColumnNameLength");
	}
	
	@Override
	public int getMaxColumnsInGroupBy() {
		throw new UnsupportedOperationException("getMaxColumnsInGroupBy");
	}
	
	@Override
	public int getMaxColumnsInIndex() {
		throw new UnsupportedOperationException("getMaxColumnsInIndex");
	}
	
	@Override
	public int getMaxColumnsInOrderBy() {
		throw new UnsupportedOperationException("getMaxColumnsInOrderBy");
	}
	
	@Override
	public int getMaxColumnsInSelect() {
		throw new UnsupportedOperationException("getMaxColumnsInSelect");
	}
	
	@Override
	public int getMaxColumnsInTable() {
		throw new UnsupportedOperationException("getMaxColumnsInTable");
	}
	
	@Override
	public int getMaxConnections() {
		throw new UnsupportedOperationException("getMaxConnections");
	}
	
	@Override
	public int getMaxCursorNameLength() {
		throw new UnsupportedOperationException("getMaxCursorNameLength");
	}
	
	@Override
	public int getMaxIndexLength() {
		throw new UnsupportedOperationException("getMaxIndexLength");
	}
	
	@Override
	public int getMaxSchemaNameLength() {
		throw new UnsupportedOperationException("getMaxSchemaNameLength");
	}
	
	@Override
	public int getMaxProcedureNameLength() {
		throw new UnsupportedOperationException("getMaxProcedureNameLength");
	}
	
	@Override
	public int getMaxCatalogNameLength() {
		throw new UnsupportedOperationException("getMaxCatalogNameLength");
	}
	
	@Override
	public int getMaxRowSize() {
		throw new UnsupportedOperationException("getMaxRowSize");
	}
	
	@Override
	public boolean doesMaxRowSizeIncludeBlobs() {
		throw new UnsupportedOperationException("doesMaxRowSizeIncludeBlobs");
	}
	
	@Override
	public int getMaxStatementLength() {
		throw new UnsupportedOperationException("getMaxStatementLength");
	}
	
	@Override
	public int getMaxStatements() {
		throw new UnsupportedOperationException("getMaxStatements");
	}
	
	@Override
	public int getMaxTableNameLength() {
		throw new UnsupportedOperationException("getMaxTableNameLength");
	}
	
	@Override
	public int getMaxTablesInSelect() {
		throw new UnsupportedOperationException("getMaxTablesInSelect");
	}
	
	@Override
	public int getMaxUserNameLength() {
		throw new UnsupportedOperationException("getMaxUserNameLength");
	}
	
	@Override
	public int getDefaultTransactionIsolation() {
		throw new UnsupportedOperationException("getDefaultTransactionIsolation");
	}
	
	@Override
	public boolean supportsTransactions() {
		throw new UnsupportedOperationException("supportsTransactions");
	}
	
	@Override
	public boolean supportsTransactionIsolationLevel(int a0) {
		throw new UnsupportedOperationException("supportsTransactionIsolationLevel");
	}
	
	@Override
	public boolean supportsDataManipulationTransactionsOnly() {
		throw new UnsupportedOperationException("supportsDataManipulationTransactionsOnly");
	}
	
	@Override
	public boolean dataDefinitionCausesTransactionCommit() {
		throw new UnsupportedOperationException("dataDefinitionCausesTransactionCommit");
	}
	
	@Override
	public boolean dataDefinitionIgnoredInTransactions() {
		throw new UnsupportedOperationException("dataDefinitionIgnoredInTransactions");
	}
	
	@Override
	public ResultSet getProcedures(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getProcedures");
	}
	
	@Override
	public ResultSet getProcedureColumns(String a0, String a1, String a2, String a3) {
		throw new UnsupportedOperationException("getProcedureColumns");
	}
	
	@Override
	public ResultSet getTables(String a0, String a1, String a2, String[] a3) {
		throw new UnsupportedOperationException("getTables");
	}
	
	@Override
	public ResultSet getSchemas(String a0, String a1) {
		throw new UnsupportedOperationException("getSchemas");
	}
	
	@Override
	public ResultSet getCatalogs() {
		throw new UnsupportedOperationException("getCatalogs");
	}
	
	@Override
	public ResultSet getTableTypes() {
		throw new UnsupportedOperationException("getTableTypes");
	}
	
	@Override
	public ResultSet getColumns(String a0, String a1, String a2, String a3) {
		throw new UnsupportedOperationException("getColumns");
	}
	
	@Override
	public ResultSet getColumnPrivileges(String a0, String a1, String a2, String a3) {
		throw new UnsupportedOperationException("getColumnPrivileges");
	}
	
	@Override
	public ResultSet getTablePrivileges(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getTablePrivileges");
	}
	
	@Override
	public ResultSet getBestRowIdentifier(String a0, String a1, String a2, int a3, boolean a4) {
		throw new UnsupportedOperationException("getBestRowIdentifier");
	}
	
	@Override
	public ResultSet getVersionColumns(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getVersionColumns");
	}
	
	@Override
	public ResultSet getPrimaryKeys(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getPrimaryKeys");
	}
	
	@Override
	public ResultSet getImportedKeys(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getImportedKeys");
	}
	
	@Override
	public ResultSet getExportedKeys(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getExportedKeys");
	}
	
	@Override
	public ResultSet getCrossReference(String a0, String a1, String a2, String a3, String a4, String a5) {
		throw new UnsupportedOperationException("getCrossReference");
	}
	
	@Override
	public ResultSet getTypeInfo() {
		throw new UnsupportedOperationException("getTypeInfo");
	}
	
	@Override
	public ResultSet getIndexInfo(String a0, String a1, String a2, boolean a3, boolean a4) {
		throw new UnsupportedOperationException("getIndexInfo");
	}
	
	@Override
	public boolean supportsResultSetType(int a0) {
		throw new UnsupportedOperationException("supportsResultSetType");
	}
	
	@Override
	public boolean supportsResultSetConcurrency(int a0, int a1) {
		throw new UnsupportedOperationException("supportsResultSetConcurrency");
	}
	
	@Override
	public boolean ownUpdatesAreVisible(int a0) {
		throw new UnsupportedOperationException("ownUpdatesAreVisible");
	}
	
	@Override
	public boolean ownDeletesAreVisible(int a0) {
		throw new UnsupportedOperationException("ownDeletesAreVisible");
	}
	
	@Override
	public boolean ownInsertsAreVisible(int a0) {
		throw new UnsupportedOperationException("ownInsertsAreVisible");
	}
	
	@Override
	public boolean othersUpdatesAreVisible(int a0) {
		throw new UnsupportedOperationException("othersUpdatesAreVisible");
	}
	
	@Override
	public boolean othersDeletesAreVisible(int a0) {
		throw new UnsupportedOperationException("othersDeletesAreVisible");
	}
	
	@Override
	public boolean othersInsertsAreVisible(int a0) {
		throw new UnsupportedOperationException("othersInsertsAreVisible");
	}
	
	@Override
	public boolean updatesAreDetected(int a0) {
		throw new UnsupportedOperationException("updatesAreDetected");
	}
	
	@Override
	public boolean deletesAreDetected(int a0) {
		throw new UnsupportedOperationException("deletesAreDetected");
	}
	
	@Override
	public boolean insertsAreDetected(int a0) {
		throw new UnsupportedOperationException("insertsAreDetected");
	}
	
	@Override
	public boolean supportsBatchUpdates() {
		throw new UnsupportedOperationException("supportsBatchUpdates");
	}
	
	@Override
	public ResultSet getUDTs(String a0, String a1, String a2, int[] a3) {
		throw new UnsupportedOperationException("getUDTs");
	}
	
	@Override
	public boolean supportsSavepoints() {
		throw new UnsupportedOperationException("supportsSavepoints");
	}
	
	@Override
	public boolean supportsNamedParameters() {
		throw new UnsupportedOperationException("supportsNamedParameters");
	}
	
	@Override
	public boolean supportsMultipleOpenResults() {
		throw new UnsupportedOperationException("supportsMultipleOpenResults");
	}
	
	@Override
	public boolean supportsGetGeneratedKeys() {
		throw new UnsupportedOperationException("supportsGetGeneratedKeys");
	}
	
	@Override
	public ResultSet getSuperTypes(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getSuperTypes");
	}
	
	@Override
	public ResultSet getSuperTables(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getSuperTables");
	}
	
	@Override
	public boolean supportsResultSetHoldability(int a0) {
		throw new UnsupportedOperationException("supportsResultSetHoldability");
	}
	
	@Override
	public int getResultSetHoldability() {
		throw new UnsupportedOperationException("getResultSetHoldability");
	}
	
	@Override
	public int getDatabaseMajorVersion() {
		throw new UnsupportedOperationException("getDatabaseMajorVersion");
	}
	
	@Override
	public int getDatabaseMinorVersion() {
		throw new UnsupportedOperationException("getDatabaseMinorVersion");
	}
	
	@Override
	public int getJDBCMajorVersion() {
		throw new UnsupportedOperationException("getJDBCMajorVersion");
	}
	
	@Override
	public int getJDBCMinorVersion() {
		throw new UnsupportedOperationException("getJDBCMinorVersion");
	}
	
	@Override
	public int getSQLStateType() {
		throw new UnsupportedOperationException("getSQLStateType");
	}
	
	@Override
	public boolean locatorsUpdateCopy() {
		throw new UnsupportedOperationException("locatorsUpdateCopy");
	}
	
	@Override
	public boolean supportsStatementPooling() {
		throw new UnsupportedOperationException("supportsStatementPooling");
	}
	
	@Override
	public RowIdLifetime getRowIdLifetime() {
		throw new UnsupportedOperationException("getRowIdLifetime");
	}
	
	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() {
		throw new UnsupportedOperationException("supportsStoredFunctionsUsingCallSyntax");
	}
	
	@Override
	public boolean autoCommitFailureClosesAllResultSets() {
		throw new UnsupportedOperationException("autoCommitFailureClosesAllResultSets");
	}
	
	@Override
	public ResultSet getClientInfoProperties() {
		throw new UnsupportedOperationException("getClientInfoProperties");
	}
	
	@Override
	public ResultSet getFunctions(String a0, String a1, String a2) {
		throw new UnsupportedOperationException("getFunctions");
	}
	
	@Override
	public ResultSet getFunctionColumns(String a0, String a1, String a2, String a3) {
		throw new UnsupportedOperationException("getFunctionColumns");
	}
	
	@Override
	public ResultSet getPseudoColumns(String a0, String a1, String a2, String a3) {
		throw new UnsupportedOperationException("getPseudoColumns");
	}
	
	@Override
	public boolean generatedKeyAlwaysReturned() {
		throw new UnsupportedOperationException("generatedKeyAlwaysReturned");
	}
	
	@Override
	public <T> T unwrap(Class<T> a0) {
		throw new UnsupportedOperationException("unwrap");
	}
	
	@Override
	public boolean isWrapperFor(Class<?> a0) {
		throw new UnsupportedOperationException("isWrapperFor");
	}
}

/**
 * A {@link DataSource} fake handing out {@link FakeConnection}s, configurable to throw, block, or
 * fail on a chosen (1-based) {@code getConnection} call.<br>
 *
 * @author Luis-St
 */
class FakeDataSource implements DataSource {
	
	final List<FakeConnection> handedOut = new ArrayList<>();
	Supplier<FakeConnection> factory = FakeConnection::new;
	int getConnectionCalls;
	int throwSqlOnCall;
	int throwRuntimeOnCall;
	CountDownLatch blockOn;
	
	@Override
	public Connection getConnection() throws SQLException {
		this.getConnectionCalls++;
		if (this.getConnectionCalls == this.throwSqlOnCall) {
			throw new SQLException("getConnection failed");
		}
		if (this.getConnectionCalls == this.throwRuntimeOnCall) {
			throw new IllegalStateException("getConnection failed (runtime)");
		}
		if (this.blockOn != null) {
			try {
				this.blockOn.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new SQLException("interrupted", e);
			}
		}
		FakeConnection connection = this.factory.get();
		this.handedOut.add(connection);
		return connection;
	}
	
	@Override
	public Connection getConnection(String a0, String a1) {
		throw new UnsupportedOperationException("getConnection");
	}
	
	@Override
	public PrintWriter getLogWriter() {
		throw new UnsupportedOperationException("getLogWriter");
	}
	
	@Override
	public void setLogWriter(PrintWriter a0) {
		throw new UnsupportedOperationException("setLogWriter");
	}
	
	@Override
	public int getLoginTimeout() {
		throw new UnsupportedOperationException("getLoginTimeout");
	}
	
	@Override
	public void setLoginTimeout(int a0) {
		throw new UnsupportedOperationException("setLoginTimeout");
	}
	
	@Override
	public Logger getParentLogger() {
		throw new UnsupportedOperationException("getParentLogger");
	}
	
	@Override
	public <T> T unwrap(Class<T> a0) {
		throw new UnsupportedOperationException("unwrap");
	}
	
	@Override
	public boolean isWrapperFor(Class<?> a0) {
		throw new UnsupportedOperationException("isWrapperFor");
	}
}
