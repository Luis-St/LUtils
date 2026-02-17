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
import net.luis.utils.io.database.audit.SqlAuditContext;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL database connection.<br>
 *
 * @author Luis-St
 */
public interface SqlDatabase {
	
	/**
	 * Gets the SQL dialect associated with this database.<br>
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
	 * Executes the given action within an auto-managed transaction.<br>
	 * The transaction is automatically committed on success or rolled back on failure.<br>
	 *
	 * @param action The action to execute within the transaction
	 * @param <T> The return type of the action
	 * @return The result of the action
	 */
	<T> T inTransaction(@NonNull ThrowableFunction<SqlTransaction, T, Exception> action);
	
	/**
	 * Returns the audit context for this database.<br>
	 * @return The audit context
	 */
	@NonNull SqlAuditContext getAuditContext();
}
