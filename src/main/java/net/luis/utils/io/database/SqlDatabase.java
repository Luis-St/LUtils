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
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Interface representing a SQL database connection.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabase extends AutoCloseable {
	
	/**
	 * Creates a new database connection from the given configuration.<br>
	 *
	 * @param config The database configuration
	 * @return The created database connection
	 */
	static @NonNull SqlDatabase create(@NonNull SqlDatabaseConfig config) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a new database connection from the given configuration and sets it as the default.<br>
	 *
	 * @param config The database configuration
	 * @return The created database connection
	 */
	static @NonNull SqlDatabase createAndSetDefault(@NonNull SqlDatabaseConfig config) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the default database connection.<br>
	 * @return The default database connection
	 */
	static @NonNull SqlDatabase getDefault() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Sets the default database connection.<br>
	 * @param database The database connection to set as default
	 */
	static void setDefault(@NonNull SqlDatabase database) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	void close();
	
	/**
	 * Returns the SQL dialect used by this database.<br>
	 * @return The SQL dialect
	 */
	@NonNull SqlDialect<?, ?> getDialect();
	
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
	 * Begins a new transaction.<br>
	 * Executes the SQL statement {@code START TRANSACTION}.<br>
	 *
	 * @return The new transaction
	 */
	@NonNull SqlTransaction beginTransaction();

	/**
	 * Begins a new transaction with the given isolation level.<br>
	 * Executes the SQL statements {@code SET TRANSACTION ISOLATION LEVEL ...} and {@code START TRANSACTION}.<br>
	 *
	 * @param isolationLevel The isolation level for the transaction
	 * @return The new transaction
	 */
	@NonNull SqlTransaction beginTransaction(@NonNull SqlIsolationLevel isolationLevel);
	
	/**
	 * Executes the given action within an auto-managed transaction.<br>
	 * The transaction is automatically committed on success or rolled back on failure.<br>
	 *
	 * @param action The action to execute within the transaction
	 * @param <T> The return type of the action
	 * @return The result of the action
	 */
	<T> T inTransaction(@NonNull Function<SqlTransaction, T> action);

	/**
	 * Asynchronously executes the given action within an auto-managed transaction.<br>
	 * The transaction is automatically committed on success or rolled back on failure.<br>
	 *
	 * @param action The action to execute within the transaction
	 * @param <T> The return type of the action
	 * @return A future containing the result of the action
	 */
	<T> @NonNull CompletableFuture<T> inTransactionAsync(@NonNull Function<SqlTransaction, T> action);
}
