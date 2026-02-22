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

import net.luis.utils.io.database.SqlIsolationLevel;
import net.luis.utils.io.database.exception.SqlTransactionException;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Interface representing a SQL transaction.<br>
 *
 * @author Luis-St
 */
public interface SqlTransaction {

	/**
	 * Returns a query provider bound to the specified table within this transaction.<br>
	 *
	 * @param table The table to query
	 * @param <T> The entity type
	 * @return A query provider for the given table within this transaction
	 */
	<T> @NonNull SqlQueryProvider<T> from(@NonNull SqlTable<T> table);

	/**
	 * Commits the current transaction.<br>
	 * Executes SQL: {@code COMMIT}.<br>
	 *
	 * @throws SqlTransactionException If the commit fails
	 */
	void commit() throws SqlTransactionException;

	/**
	 * Rolls back the current transaction.<br>
	 * Executes SQL: {@code ROLLBACK}.<br>
	 *
	 * @throws SqlTransactionException If the rollback fails
	 */
	void rollback() throws SqlTransactionException;

	/**
	 * Rolls back the current transaction to the given savepoint.<br>
	 * Executes SQL: {@code ROLLBACK TO SAVEPOINT name}.<br>
	 *
	 * @param savepoint The savepoint to roll back to
	 * @throws SqlTransactionException If the rollback fails
	 */
	void rollbackTo(@NonNull SqlSavepoint savepoint) throws SqlTransactionException;

	/**
	 * Creates a savepoint with the given name.<br>
	 * Executes SQL: {@code SAVEPOINT name}.<br>
	 *
	 * @param name The name of the savepoint
	 * @return The created savepoint
	 * @throws SqlTransactionException If the savepoint cannot be created
	 */
	@NonNull SqlSavepoint savepoint(@NonNull String name) throws SqlTransactionException;

	/**
	 * Sets the transaction to read-only mode.<br>
	 * Executes SQL: {@code SET TRANSACTION READ ONLY}.<br>
	 *
	 * @return This transaction
	 */
	@NonNull SqlTransaction readOnly();

	/**
	 * Sets the timeout for the transaction.<br>
	 *
	 * @param timeout The maximum duration for the transaction
	 * @return This transaction
	 */
	@NonNull SqlTransaction timeout(@NonNull Duration timeout);

	/**
	 * Sets the isolation level for the transaction.<br>
	 * Executes SQL: {@code SET TRANSACTION ISOLATION LEVEL ...}.<br>
	 *
	 * @param level The isolation level
	 * @return This transaction
	 */
	@NonNull SqlTransaction isolation(@NonNull SqlIsolationLevel level);
}
