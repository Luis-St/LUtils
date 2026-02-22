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

import net.luis.utils.io.database.audit.SqlTimestampSource;
import net.luis.utils.io.database.exception.SqlConnectionException;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;
import java.time.Clock;
import java.util.concurrent.Executor;

/**
 * Interface representing a SQL database configuration builder.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabaseConfig {

	/**
	 * Creates a new database configuration builder.<br>
	 * @return The new configuration builder
	 */
	static @NonNull SqlDatabaseConfig builder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the data source for the database connection.<br>
	 *
	 * @param dataSource The data source to use
	 * @return This configuration builder
	 */
	@NonNull SqlDatabaseConfig dataSource(@NonNull DataSource dataSource);

	/**
	 * Sets the executor for asynchronous operations.<br>
	 *
	 * @param executor The executor to use for async operations
	 * @return This configuration builder
	 */
	@NonNull SqlDatabaseConfig asyncExecutor(@NonNull Executor executor);

	/**
	 * Sets the connection pool size for asynchronous operations.<br>
	 *
	 * @param size The pool size
	 * @return This configuration builder
	 */
	@NonNull SqlDatabaseConfig asyncConnectionPoolSize(int size);

	/**
	 * Sets the timestamp source for audit operations.<br>
	 *
	 * @param source The audit timestamp source
	 * @return This configuration builder
	 */
	@NonNull SqlDatabaseConfig auditTimestampSource(@NonNull SqlTimestampSource source);

	/**
	 * Sets the clock used for audit timestamps.<br>
	 *
	 * @param clock The clock to use
	 * @return This configuration builder
	 */
	@NonNull SqlDatabaseConfig auditClock(@NonNull Clock clock);

	/**
	 * Builds the configuration and creates a database instance.<br>
	 *
	 * @return The configured database instance
	 * @throws SqlConnectionException If the connection cannot be established
	 */
	@NonNull SqlDatabase build() throws SqlConnectionException;
}
