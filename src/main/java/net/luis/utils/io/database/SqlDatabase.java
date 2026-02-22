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

import net.luis.utils.io.database.audit.SqlAuditContext;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlConnectionException;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.SqlTransactionException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL database connection.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabase extends AutoCloseable {

	/**
	 * Creates a new database instance from the given configuration.<br>
	 *
	 * @param config The database configuration
	 * @return The new database instance
	 * @throws SqlConnectionException If the connection cannot be established
	 */
	static @NonNull SqlDatabase of(@NonNull SqlDatabaseConfig config) throws SqlConnectionException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the SQL dialect associated with this database.<br>
	 * @return The SQL dialect
	 */
	@NonNull SqlDialect getDialect();

	/**
	 * Checks the health of the database connection.<br>
	 * @return Whether the database connection is healthy
	 */
	boolean health();

	/**
	 * Pings the database to check if it is reachable.<br>
	 * @return Whether the database responded to the ping
	 */
	boolean ping();

	/**
	 * Returns a query provider bound to the specified table.<br>
	 *
	 * @param table The table to query
	 * @param <T> The entity type
	 * @return A query provider for the given table
	 */
	<T> @NonNull SqlQueryProvider<T> from(@NonNull SqlTable<T> table);

	/**
	 * Begins a new transaction.<br>
	 * Executes the SQL statement {@code START TRANSACTION}.<br>
	 *
	 * @return The new transaction
	 * @throws SqlTransactionException If the transaction cannot be started
	 */
	@NonNull SqlTransaction beginTransaction() throws SqlTransactionException;

	/**
	 * Begins a new transaction with the specified audit context.<br>
	 *
	 * @param context The audit context for the transaction
	 * @return The new transaction
	 * @throws SqlTransactionException If the transaction cannot be started
	 */
	@NonNull SqlTransaction beginTransaction(@NonNull SqlAuditContext context) throws SqlTransactionException;

	/**
	 * Returns the audit context for this database.<br>
	 * @return The audit context
	 */
	@NonNull SqlAuditContext getAuditContext();

	/**
	 * Closes this database connection and releases all resources.<br>
	 *
	 * @throws SqlException If an error occurs while closing the connection
	 */
	@Override
	void close() throws SqlException;
}
