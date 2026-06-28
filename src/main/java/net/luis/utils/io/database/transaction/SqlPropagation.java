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

/**
 * Represents the propagation behavior that determines how a transaction relates to an already active transaction when a new one is requested.<br>
 * The propagation is evaluated by the transaction manager when a transaction is started and decides whether to join, suspend, create or forbid a transaction.<br>
 *
 * @author Luis-St
 */
public enum SqlPropagation {
	
	/**
	 * Joins the current transaction if one is active, otherwise a new transaction is created.<br>
	 */
	REQUIRED,
	/**
	 * Always creates a new independent transaction, suspending the current transaction if one is active.<br>
	 */
	REQUIRES_NEW,
	/**
	 * Executes within a nested transaction backed by a savepoint of the current transaction.<br>
	 * Requires an active transaction to be present.<br>
	 */
	NESTED,
	/**
	 * Joins the current transaction if one is active, otherwise executes non-transactionally.<br>
	 */
	SUPPORTS,
	/**
	 * Always executes non-transactionally, suspending the current transaction if one is active.<br>
	 */
	NOT_SUPPORTED,
	/**
	 * Joins the current transaction and requires an active transaction to be present, otherwise an exception is thrown.<br>
	 */
	MANDATORY,
	/**
	 * Always executes non-transactionally and forbids an active transaction, otherwise an exception is thrown.<br>
	 */
	NEVER
}
