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

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlConnectionException;
import net.luis.utils.io.database.transaction.SqlIsolationLevel;
import net.luis.utils.io.database.transaction.SqlPropagation;
import org.jetbrains.annotations.NotNull;
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
	private String defaultSchema = "public";
	private Duration queryTimeout = Duration.ofSeconds(30);
	private Duration defaultTransactionTimeout = Duration.ofSeconds(60);
	private SqlIsolationLevel defaultTransactionIsolationLevel = SqlIsolationLevel.READ_COMMITTED;
	private SqlPropagation defaultTransactionPropagation = SqlPropagation.REQUIRED;
	private boolean autoCloseDataSource;
	
	public SqlDatabaseBuilder(@NonNull DataSource dataSource, @NotNull SqlDialect dialect) {
		this.dataSource = Objects.requireNonNull(dataSource, "Data source must not be null");
		this.dialect = Objects.requireNonNull(dialect, "SQL dialect must not be null");
	}
	
	public @NonNull SqlDatabaseBuilder defaultSchema(@NotNull String defaultSchema) {
		this.defaultSchema = Objects.requireNonNull(defaultSchema, "Default schema must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder queryTimeout(@NotNull Duration queryTimeout) {
		this.queryTimeout = Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder defaultTransactionTimeout(@NotNull Duration defaultTransactionTimeout) {
		this.defaultTransactionTimeout = Objects.requireNonNull(defaultTransactionTimeout, "Default transaction timeout must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder defaultTransactionIsolationLevel(@NotNull SqlIsolationLevel defaultTransactionIsolationLevel) {
		this.defaultTransactionIsolationLevel = Objects.requireNonNull(defaultTransactionIsolationLevel, "Default transaction isolation level must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder defaultTransactionPropagation(@NotNull SqlPropagation defaultTransactionPropagation) {
		this.defaultTransactionPropagation = Objects.requireNonNull(defaultTransactionPropagation, "Default transaction propagation behavior must not be null");
		return this;
	}
	
	public @NonNull SqlDatabaseBuilder autoCloseDataSource(boolean autoCloseDataSource) {
		this.autoCloseDataSource = autoCloseDataSource;
		return this;
	}
	
	public @NonNull SqlDatabase build() throws SqlConnectionException {
		return new SqlDatabase(this.dataSource, this.dialect, this.defaultSchema, this.queryTimeout, this.defaultTransactionTimeout, this.defaultTransactionIsolationLevel, this.defaultTransactionPropagation, this.autoCloseDataSource);
	}
}
