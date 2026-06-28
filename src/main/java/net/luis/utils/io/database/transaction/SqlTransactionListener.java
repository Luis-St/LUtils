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
 * A callback interface that is notified about lifecycle events of a transaction.<br>
 * Listeners can be registered on a transaction to react after it has been committed, rolled back or closed.<br>
 * All methods are optional and default to doing nothing, so implementations only need to override the events they are interested in.<br>
 *
 * @author Luis-St
 */
public interface SqlTransactionListener {
	
	/**
	 * Invoked after the transaction has been successfully committed.<br>
	 */
	default void afterCommit() {}
	
	/**
	 * Invoked after the transaction has been rolled back.<br>
	 */
	default void afterRollback() {}
	
	/**
	 * Invoked after the transaction has been closed, regardless of whether it was committed or rolled back.<br>
	 */
	default void afterClose() {}
}
