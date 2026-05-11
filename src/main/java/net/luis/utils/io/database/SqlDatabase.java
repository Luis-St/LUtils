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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlConnectionException;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.transaction.SqlTransactionException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.table.SqlTableProvider;
import net.luis.utils.io.database.transaction.*;
import org.jetbrains.annotations.NotNull;
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
	private final Duration defaultTransactionTimeout;
	private final SqlIsolationLevel defaultTransactionIsolationLevel;
	private final SqlPropagation defaultTransactionPropagation;
	private final boolean autoCloseDataSource;
	private final SqlTransactionManager transactionManager;
	
	SqlDatabase(
		@NonNull DataSource dataSource,
		@NotNull SqlDialect dialect,
		@NonNull Duration queryTimeout,
		@NonNull Duration defaultTransactionTimeout,
		@NonNull SqlIsolationLevel defaultTransactionIsolationLevel,
		@NonNull SqlPropagation defaultTransactionPropagation,
		boolean autoCloseDataSource
	) throws SqlConnectionException {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		this.defaultTransactionTimeout = Objects.requireNonNull(defaultTransactionTimeout, "Default transaction timeout must not be null");
		this.defaultTransactionIsolationLevel = Objects.requireNonNull(defaultTransactionIsolationLevel, "Default transaction isolation level must not be null");
		this.defaultTransactionPropagation = Objects.requireNonNull(defaultTransactionPropagation, "Default transaction propagation behavior must not be null");
		this.autoCloseDataSource = autoCloseDataSource;
		this.transactionManager = new SqlTransactionManager(dataSource, dialect, queryTimeout);
	}
	
	public static @NonNull SqlDatabaseBuilder builder(@NonNull DataSource dataSource, @NotNull SqlDialect dialect) {
		return new SqlDatabaseBuilder(dataSource, dialect);
	}
	
	public @NonNull DataSource getDataSource() {
		return this.dataSource;
	}
	
	public @NotNull SqlDialect getDialect() {
		return this.dialect;
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
	public void createSchema(@NotNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute("CREATE SCHEMA " + this.dialect.quoteIdentifier(name));
		} catch (SQLException e) {
			throw new SqlException("Failed to create schema " + name, e);
		}
	}
	
	@Override
	public void createSchemaIfNotExists(@NotNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			statement.execute("CREATE SCHEMA IF NOT EXISTS " + this.dialect.quoteIdentifier(name));
		} catch (SQLException e) {
			throw new SqlException("Failed to create schema " + name, e);
		}
	}
	
	@Override
	public boolean existsSchema(@NotNull String name) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); ResultSet resultSet = connection.getMetaData().getSchemas()) {
			while (resultSet.next()) {
				if (name.equals(resultSet.getString("TABLE_SCHEM"))) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			throw new SqlException("Failed to check if schema " + name + " exists", e);
		}
	}
	
	@Override
	public void dropSchema(@NotNull String name, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		try (Connection connection = this.dataSource.getConnection(); Statement statement = connection.createStatement()) {
			String sql = "DROP SCHEMA " + this.dialect.quoteIdentifier(name);
			if (cascade) {
				sql += " CASCADE";
			}
			statement.execute(sql);
		} catch (SQLException e) {
			throw new SqlException("Failed to drop schema " + name, e);
		}
	}
	
	@Override
	public @NonNull <T> SqlTableProvider<T> table(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		try {
			return new SqlTableProvider<>(table, this.dialect, this.dataSource.getConnection(), this.queryTimeout);
		} catch (SQLException e) {
			throw new SqlConnectionException("Failed to obtain connection for table " + table.getName(), e);
		}
	}
	
	@Override
	public @NonNull <T> SqlQueryProvider<T> from(@NonNull SqlTable<T> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		try {
			return new SqlQueryProvider<>(table, this.dialect, this.dataSource.getConnection(), this.queryTimeout);
		} catch (SQLException e) {
			throw new SqlConnectionException("Failed to obtain connection for table " + table.getName(), e);
		}
	}
	
	public @NonNull SqlTransaction beginTransaction() throws SqlTransactionException {
		return this.transactionManager.begin(false, this.defaultTransactionTimeout, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation);
	}
	
	public @NonNull SqlTransaction beginTransaction(@NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlTransactionException {
		return this.transactionManager.begin(false, timeout, isolationLevel, propagation);
	}
	
	public @NonNull SqlTransaction beginReadOnlyTransaction() throws SqlTransactionException {
		return this.transactionManager.begin(true, this.defaultTransactionTimeout, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation);
	}
	
	public @NonNull SqlTransaction beginReadOnlyTransaction(@NonNull Duration timeout, @NonNull SqlIsolationLevel isolationLevel, @NonNull SqlPropagation propagation) throws SqlTransactionException {
		return this.transactionManager.begin(true, timeout, isolationLevel, propagation);
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull ThrowableFunction<SqlTransaction, T, SqlException> action) throws SqlException {
		try (SqlTransaction tx = this.beginTransaction()) {
			return this.inTransaction(tx, action);
		}
	}
	
	public <T> @UnknownNullability T inTransaction(@NonNull SqlTransaction transaction, @NonNull ThrowableSupplier<T, SqlException> action) throws SqlException {
		Objects.requireNonNull(action, "Transaction action must not be null");
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
			} catch (SqlTransactionException rollbackEx) {
				e.addSuppressed(rollbackEx);
			}
			
			if (e instanceof SqlException sqlEx) {
				throw sqlEx;
			}
			
			if (e instanceof RuntimeException rte) {
				throw rte;
			}
			throw new SqlException("Transaction failed", e);
		}
	}
	
	@Override
	public void close() throws SqlConnectionException {
		if (this.autoCloseDataSource) {
			if (this.dataSource instanceof Closeable closeable) {
				try {
					closeable.close();
				} catch (Exception e) {
					throw new SqlConnectionException("Failed to close data source", e);
				}
			} else if (this.dataSource instanceof AutoCloseable autoCloseable) {
				try {
					autoCloseable.close();
				} catch (Exception e) {
					throw new SqlConnectionException("Failed to close data source", e);
				}
			}
		}
	}
}
