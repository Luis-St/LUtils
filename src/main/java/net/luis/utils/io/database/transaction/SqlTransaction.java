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
import net.luis.utils.io.database.audit.SqlAuditContext;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Optional;

/**
 * Interface representing a SQL transaction.<br>
 *
 * @author Luis-St
 */
public interface SqlTransaction {
	
	/**
	 * Returns whether the current thread is in a transaction.<br>
	 * @return Whether a transaction is active
	 */
	static boolean isInTransaction() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the current transaction, if any.<br>
	 * @return An optional containing the current transaction
	 */
	static @NonNull Optional<SqlTransaction> current() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an exception if no transaction is currently active.<br>
	 */
	static void requireActive() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Commits the current transaction.<br>
	 * Executes SQL: {@code COMMIT}.<br>
	 */
	void commit();
	
	/**
	 * Rolls back the current transaction.<br>
	 * Executes SQL: {@code ROLLBACK}.<br>
	 */
	void rollback();
	
	/**
	 * Rolls back the current transaction to the given savepoint.<br>
	 * Executes SQL: {@code ROLLBACK TO SAVEPOINT name}.<br>
	 *
	 * @param savepoint The savepoint to roll back to
	 */
	void rollbackTo(@NonNull SqlSavepoint savepoint);
	
	/**
	 * Creates a savepoint with the given name.<br>
	 * Executes SQL: {@code SAVEPOINT name}.<br>
	 *
	 * @param name The name of the savepoint
	 * @return The created savepoint
	 */
	@NonNull SqlSavepoint savepoint(@NonNull String name);
	
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
	
	/**
	 * Sets the audit context for this transaction.<br>
	 * The audit context can be used to track metadata about the transaction for auditing purposes, such as the user performing the transaction, the reason for the transaction, or any relevant tags.<br>
	 *
	 * @param context The audit context to set for this transaction
	 * @return This transaction
	 */
	@NonNull SqlTransaction auditContext(@NonNull SqlAuditContext context);
}
