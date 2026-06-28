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

import java.sql.Connection;

/**
 * Represents the transaction isolation levels that control how concurrent transactions interact with each other.<br>
 * Each level maps to a corresponding {@link Connection} isolation constant that is applied to the underlying jdbc connection.<br>
 * Higher levels provide stronger consistency guarantees at the cost of reduced concurrency.<br>
 *
 * @see Connection
 *
 * @author Luis-St
 */
public enum SqlIsolationLevel {
	
	/**
	 * The strongest isolation level.<br>
	 * Transactions are fully isolated from one another as if they were executed serially, preventing dirty reads, non-repeatable reads and phantom reads.<br>
	 */
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),
	/**
	 * Guarantees that rows read once during a transaction yield the same values when read again, preventing dirty reads and non-repeatable reads.<br>
	 * Phantom reads may still occur.<br>
	 */
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
	/**
	 * Ensures that only committed data is read, preventing dirty reads.<br>
	 * Non-repeatable reads and phantom reads may still occur.<br>
	 */
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
	/**
	 * The weakest isolation level.<br>
	 * Allows a transaction to read uncommitted changes made by other transactions, permitting dirty reads, non-repeatable reads and phantom reads.<br>
	 */
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED);
	
	/**
	 * The jdbc isolation constant from {@link Connection} that corresponds to this isolation level.
	 */
	private final int jdbcLevel;
	
	/**
	 * Constructs a new sql isolation level with the given jdbc isolation constant.<br>
	 * @param jdbcLevel The jdbc isolation constant from {@link Connection} that corresponds to this isolation level
	 */
	SqlIsolationLevel(int jdbcLevel) {
		this.jdbcLevel = jdbcLevel;
	}
	
	/**
	 * Returns the jdbc isolation constant that corresponds to this isolation level.<br>
	 * The returned value can be passed to {@link Connection#setTransactionIsolation(int)}.<br>
	 * @return The jdbc isolation constant from {@link Connection}
	 */
	public int jdbcLevel() {
		return this.jdbcLevel;
	}
}
