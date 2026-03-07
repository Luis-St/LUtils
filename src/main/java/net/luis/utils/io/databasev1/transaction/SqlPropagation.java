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

package net.luis.utils.io.databasev1.transaction;

/**
 * Enum representing the transaction propagation behaviors.<br>
 * Defines how transactions relate to each other when nested or called from existing transactional contexts.<br>
 *
 * @author Luis-St
 */
public enum SqlPropagation {
	
	/**
	 * Joins the current transaction if one exists, otherwise creates a new one.<br>
	 */
	REQUIRED,
	
	/**
	 * Always creates a new transaction, suspending the current one if it exists.<br>
	 */
	REQUIRES_NEW,
	
	/**
	 * Creates a nested transaction using a savepoint within the current transaction.<br>
	 * Requires an active transaction to exist.<br>
	 */
	NESTED,
	
	/**
	 * Joins the current transaction if one exists, otherwise executes non-transactionally.<br>
	 */
	SUPPORTS,
	
	/**
	 * Executes non-transactionally, suspending the current transaction if one exists.<br>
	 */
	NOT_SUPPORTED,
	
	/**
	 * Requires an active transaction to exist; throws an exception if none is present.<br>
	 */
	MANDATORY,
	
	/**
	 * Requires that no active transaction exists; throws an exception if one is present.<br>
	 */
	NEVER
}
