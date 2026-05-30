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
 *
 * @author Luis-St
 *
 */

public class SqlDatabaseBuilder {
	
	private final DataSource dataSource;
	private final SqlDialect dialect;
	private Duration queryTimeout = Duration.ofSeconds(30);
	private Duration connectionAcquisitionTimeout = SqlTransactionManager.DEFAULT_CONNECTION_ACQUISITION_TIMEOUT;
	private SqlIsolationLevel defaultTransactionIsolationLevel = SqlIsolationLevel.READ_COMMITTED;
	private SqlPropagation defaultTransactionPropagation = SqlPropagation.REQUIRED;
	private boolean autoCloseDataSource;
	private SqlAuditUserProvider auditUserProvider = SqlAuditUserProvider.empty();
	
	public SqlDatabaseBuilder(@NonNull DataSource dataSource, @NonNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "Sql dialect must not be null");
	}
	
	public @NonNull SqlDatabaseBuilder queryTimeout(@NonNull Duration queryTimeout) {
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder connectionAcquisitionTimeout(@NonNull Duration connectionAcquisitionTimeout) {
		this.connectionAcquisitionTimeout = Objects.requireNonNull(connectionAcquisitionTimeout, "Connection acquisition timeout must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder defaultTransactionIsolationLevel(@NonNull SqlIsolationLevel defaultTransactionIsolationLevel) {
		this.defaultTransactionIsolationLevel = Objects.requireNonNull(defaultTransactionIsolationLevel, "Default transaction isolation level must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder defaultTransactionPropagation(@NonNull SqlPropagation defaultTransactionPropagation) {
		this.defaultTransactionPropagation = Objects.requireNonNull(defaultTransactionPropagation, "Default transaction propagation behavior must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder autoCloseDataSource(boolean autoCloseDataSource) {
		this.autoCloseDataSource = autoCloseDataSource;
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder auditUserProvider(@NonNull SqlAuditUserProvider auditUserProvider) {
		this.auditUserProvider = Objects.requireNonNull(auditUserProvider, "Audit user provider must not be null");
		return this;
	}
	
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
