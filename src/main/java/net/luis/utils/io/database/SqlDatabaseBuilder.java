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

import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.database.SqlConnectionException;
import net.luis.utils.io.database.transaction.*;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.Objects;

/**
 * A fluent builder for {@link SqlDatabase} instances.<br>
 * The data source and dialect are mandatory and provided on construction, while all other settings have
 * sensible defaults and can be overridden through the fluent setter methods.<br>
 * The configured database is created by calling {@link #build()}.<br>
 *
 * @see SqlDatabase
 *
 * @author Luis-St
 */
public class SqlDatabaseBuilder {
	
	/**
	 * The data source used to obtain connections to the underlying database.<br>
	 */
	private final DataSource dataSource;
	/**
	 * The sql dialect used to render statements for the underlying database.<br>
	 */
	private final SqlDialect dialect;
	/**
	 * The timeout applied to individual queries, defaulting to thirty seconds.<br>
	 */
	private Duration queryTimeout = Duration.ofSeconds(30);
	/**
	 * The timeout applied when acquiring a connection from the data source, defaulting to the transaction manager default.<br>
	 */
	private Duration connectionAcquisitionTimeout = SqlTransactionManager.DEFAULT_CONNECTION_ACQUISITION_TIMEOUT;
	/**
	 * The default isolation level for transactions, defaulting to {@link SqlIsolationLevel#READ_COMMITTED}.<br>
	 */
	private SqlIsolationLevel defaultTransactionIsolationLevel = SqlIsolationLevel.READ_COMMITTED;
	/**
	 * The default propagation behavior for transactions, defaulting to {@link SqlPropagation#REQUIRED}.<br>
	 */
	private SqlPropagation defaultTransactionPropagation = SqlPropagation.REQUIRED;
	/**
	 * Whether the data source should be closed when the database is closed, defaulting to {@code false}.<br>
	 */
	private boolean autoCloseDataSource;
	/**
	 * The audit user provider used for audited operations, defaulting to an empty provider.<br>
	 */
	private SqlAuditUserProvider auditUserProvider = SqlAuditUserProvider.empty();
	
	/**
	 * Constructs a new database builder using the given data source and dialect.<br>
	 *
	 * @param dataSource The data source used to obtain connections to the underlying database
	 * @param dialect The sql dialect used to render statements
	 * @throws NullPointerException If the data source or dialect is null
	 */
	public SqlDatabaseBuilder(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	/**
	 * Sets the timeout applied to individual queries executed against the database.<br>
	 *
	 * @param queryTimeout The query timeout to use
	 * @return This builder for chaining
	 * @throws NullPointerException If the query timeout is null
	 */
	public @NonNull SqlDatabaseBuilder queryTimeout(@NonNull Duration queryTimeout) {
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the timeout applied when acquiring a connection from the data source.<br>
	 *
	 * @param connectionAcquisitionTimeout The connection acquisition timeout to use
	 * @return This builder for chaining
	 * @throws NullPointerException If the connection acquisition timeout is null
	 */
	public @NonNull SqlDatabaseBuilder connectionAcquisitionTimeout(@NonNull Duration connectionAcquisitionTimeout) {
		this.connectionAcquisitionTimeout = Objects.requireNonNull(connectionAcquisitionTimeout, "Connection acquisition timeout must not be null");
		return this;
	}
	
	/**
	 * Sets the default isolation level used for transactions started without an explicit isolation level.<br>
	 *
	 * @param defaultTransactionIsolationLevel The default isolation level to use
	 * @return This builder for chaining
	 * @throws NullPointerException If the default isolation level is null
	 */
	public @NonNull SqlDatabaseBuilder defaultTransactionIsolationLevel(@NonNull SqlIsolationLevel defaultTransactionIsolationLevel) {
		this.defaultTransactionIsolationLevel = Objects.requireNonNull(defaultTransactionIsolationLevel, "Default transaction isolation level must not be null");
		return this;
	}
	
	/**
	 * Sets the default propagation behavior used for transactions started without an explicit propagation.<br>
	 *
	 * @param defaultTransactionPropagation The default propagation behavior to use
	 * @return This builder for chaining
	 * @throws NullPointerException If the default propagation behavior is null
	 */
	public @NonNull SqlDatabaseBuilder defaultTransactionPropagation(@NonNull SqlPropagation defaultTransactionPropagation) {
		this.defaultTransactionPropagation = Objects.requireNonNull(defaultTransactionPropagation, "Default transaction propagation behavior must not be null");
		return this;
	}
	
	/**
	 * Sets whether the data source should be closed when the built database is closed.<br>
	 *
	 * @param autoCloseDataSource Whether the data source should be closed with the database
	 * @return This builder for chaining
	 */
	public @NonNull SqlDatabaseBuilder autoCloseDataSource(boolean autoCloseDataSource) {
		this.autoCloseDataSource = autoCloseDataSource;
		return this;
	}
	
	/**
	 * Sets the audit user provider used by the built database for audited operations.<br>
	 *
	 * @param auditUserProvider The audit user provider to use
	 * @return This builder for chaining
	 * @throws NullPointerException If the audit user provider is null
	 */
	public @NonNull SqlDatabaseBuilder auditUserProvider(@NonNull SqlAuditUserProvider auditUserProvider) {
		this.auditUserProvider = Objects.requireNonNull(auditUserProvider, "Audit user provider must not be null");
		return this;
	}
	
	/**
	 * Builds a new database from the current configuration of this builder.<br>
	 *
	 * @return The configured database
	 * @throws SqlConnectionException If the connection to the underlying database can not be established
	 */
	public @NonNull SqlDatabase build() throws SqlConnectionException {
		return new SqlDatabase(
			this.dataSource,
			this.dialect,
			this.queryTimeout,
			this.connectionAcquisitionTimeout,
			this.defaultTransactionIsolationLevel,
			this.defaultTransactionPropagation,
			this.autoCloseDataSource,
			this.auditUserProvider
		);
	}
}
