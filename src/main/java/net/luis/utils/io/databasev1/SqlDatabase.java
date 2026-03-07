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

package net.luis.utils.io.databasev1;

import net.luis.utils.io.databasev1.audit.SqlAuditContext;
import net.luis.utils.io.databasev1.dialect.SqlDialect;
import net.luis.utils.io.databasev1.exception.SqlException;
import net.luis.utils.io.databasev1.exception.SqlTransactionException;
import net.luis.utils.io.databasev1.query.SqlQueryProvider;
import net.luis.utils.io.databasev1.table.SqlTable;
import net.luis.utils.io.databasev1.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

/**
 * Interface representing a SQL database connection.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabase extends AutoCloseable {
	
	/**
	 * Returns a builder for configuring and creating a new database instance.<br>
	 *
	 * @return A new database builder
	 */
	static @NonNull SqlDatabaseBuilder builder() {
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
	 * Executes the given action within a new transaction.<br>
	 * The transaction is automatically committed if the action completes successfully, or rolled back if the action throws an exception.<br>
	 *
	 * @param action The action to execute within the transaction
	 * @param <R> The return type of the action
	 * @return The result of the action
	 * @throws SqlException If the action or the transaction fails
	 */
	default <R> R inTransaction(@NonNull Function<SqlTransaction, R> action) throws SqlException {
		try (SqlTransaction tx = this.beginTransaction()) {
			try {
				R result = action.apply(tx);
				tx.commit();
				return result;
			} catch (Exception e) {
				try {
					tx.rollback();
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
	}
	
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
	
	@Override
	void close() throws SqlException;
}
