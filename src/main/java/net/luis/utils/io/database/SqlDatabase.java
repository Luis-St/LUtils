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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.function.throwable.ThrowableSupplier;
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.SqlConnectionException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import net.luis.utils.io.database.transaction.*;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.*;
import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("SqlSourceToSinkFlow")
public class SqlDatabase implements SqlProvider, AutoCloseable {
	
	private final DataSource dataSource;
	private final SqlDialect dialect;
	private final Duration queryTimeout;
	private final SqlIsolationLevel defaultTransactionIsolationLevel;
	private final SqlPropagation defaultTransactionPropagation;
	private final boolean autoCloseDataSource;
	private final SqlAuditUserProvider auditUserProvider;
	private final SqlTransactionManager transactionManager;
	
	SqlDatabase(
		@NonNull DataSource dataSource,
		@NonNull SqlDialect dialect,
		@NonNull Duration queryTimeout,
		@NonNull SqlIsolationLevel defaultTransactionIsolationLevel,
		@NonNull SqlPropagation defaultTransactionPropagation,
		boolean autoCloseDataSource,
		@NonNull SqlAuditUserProvider auditUserProvider
	) throws SqlConnectionException {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.defaultTransactionIsolationLevel = Objects.requireNonNull(defaultTransactionIsolationLevel, "Default transaction isolation level must not be null");
		this.defaultTransactionPropagation = Objects.requireNonNull(defaultTransactionPropagation, "Default transaction propagation behavior must not be null");
		this.autoCloseDataSource = autoCloseDataSource;
		this.auditUserProvider = Objects.requireNonNull(auditUserProvider, "Audit user provider must not be null");
		this.transactionManager = new SqlTransactionManager(dataSource, dialect, queryTimeout);
	}
	
	public static @NonNull SqlDatabaseBuilder builder(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		return new SqlDatabaseBuilder(dataSource, dialect);
	}
	
	public @NonNull DataSource getDataSource() {
		return this.dataSource;
	}
	
	public @NonNull SqlDialect getDialect() {
		return this.dialect;
	}
	
	public @NonNull SqlAuditUserProvider getAuditUserProvider() {
		return this.auditUserProvider;
	}
	
	public boolean health() {
		try (Connection connection = this.dataSource.getConnection()) {
			return connection.isValid(5);
		} catch (SQLException e) {
			return false;
		}
	}
	
	public boolean ping() {
		try (Connection connection = this.dataSource.getConnection()) {
			return connection.isValid(1);
		} catch (SQLException e) {
			return false;
		}
	}
	
	@Override
	public void createSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, false).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public void createSchemaIfNotExists(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderCreateSchema(name, true).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to create sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public boolean existsSchema(@NonNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); ResultSet resultSet = connection.getMetaData().getSchemas()) {
			while (resultSet.next()) {
				if (name.equals(resultSet.getString("TABLE_SCHEM"))) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new SqlException("Failed to check if sql schema '" + name + "' exists", e);
		}
	}
	
	@Override
	public void dropSchema(@NonNull String name, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute(this.dialect.schemaRenderer().renderDropSchema(name, false, cascade).sql());
		} catch (SQLException e) {
			throw new SqlException("Failed to drop sql schema '" + name + "'", e);
		}
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		return new SqlTableProvider<>(table, this.dialect, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout);
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		return new SqlQueryProvider<>(table, this.dialect, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout, this.auditUserProvider, null);
	}
	
	public @NonNull SqlTransaction beginTransaction() throws SqlException {
		return this.transactionManager.begin(false, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation);
	}
	
	public @NonNull SqlTransaction beginTransaction(@NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlException {
		return this.transactionManager.begin(false, isolationLevel, propagation);
	}
	
	public @NonNull SqlTransaction beginReadOnlyTransaction() throws SqlException {
		return this.transactionManager.begin(true, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation);
	}
	
	public @NonNull SqlTransaction beginReadOnlyTransaction(@NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlException {
		return this.transactionManager.begin(true, isolationLevel, propagation);
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		try (SqlTransaction tx = this.beginTransaction()) {
			return this.inTransaction(tx, action);
		}
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableSupplier<T, SqlException> action) throws SqlException {
		Objects.requireNonNull(action, "Sql transaction action must not be null");
		return this.inTransaction(transaction, _ -> action.get());
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		try {
			T result = action.apply(transaction);
			transaction.commit();
			return result;
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (SqlException rollbackEx) {
				e.addSuppressed(rollbackEx);
			}
			
			if (e instanceof SqlException sqlEx) {
				throw sqlEx;
			}
			
			if (e instanceof RuntimeException rte) {
				throw rte;
			}
			throw new SqlException("Sql transaction failed", e);
		}
	}
	
	public @NonNull SqlSession openSession() {
		return new SqlSession(this, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout, null);
	}
	
	public @NonNull SqlSession openSession(@NonNull SqlAuditUserProvider auditUserProvider) {
		Objects.requireNonNull(auditUserProvider, "Sql audit user provider must not be null");
		return new SqlSession(this, SqlConnectionSource.pooled(this.dataSource), this.queryTimeout, auditUserProvider);
	}
	
	public @NonNull SqlSession openSession(@NonNull SqlTransaction transaction) {
		Objects.requireNonNull(transaction, "Sql transaction must not be null");
		return new SqlSession(this, SqlConnectionSource.fixed(transaction.getConnection()), this.queryTimeout, null, transaction);
	}
	
	public @NonNull SqlSession openSession(@NonNull SqlTransaction transaction, @NonNull SqlAuditUserProvider auditUserProvider) {
		Objects.requireNonNull(transaction, "Sql transaction must not be null");
		Objects.requireNonNull(auditUserProvider, "Audit user provider must not be null");
		
		return new SqlSession(this, SqlConnectionSource.fixed(transaction.getConnection()), this.queryTimeout, auditUserProvider, transaction);
	}
	
	@Override
	public void close() throws SqlException {
		if (this.autoCloseDataSource) {
			if (this.dataSource instanceof Closeable closeable) {
				try {
					closeable.close();
				} catch (Exception e) {
					throw new SqlException("Failed to close data source", e);
				}
			} else if (this.dataSource instanceof AutoCloseable autoCloseable) {
				try {
					autoCloseable.close();
				} catch (Exception e) {
					throw new SqlException("Failed to close data source", e);
				}
			}
		}
	}
}
