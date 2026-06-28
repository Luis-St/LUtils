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

package net.luis.utils.io.database.query;

/**
 * Represents the row locking mode that can be applied to a sql query.<br>
 * It controls how the rows selected by a query are locked for concurrent access.<br>
 *
 * @author Luis-St
 */
public enum SqlLockMode {
	
	/**
	 * Acquires an exclusive lock on the selected rows, preventing other transactions from reading or writing them.<br>
	 */
	FOR_UPDATE,
	/**
	 * Acquires a shared lock on the selected rows, allowing other transactions to read but not to write them.<br>
	 */
	FOR_SHARE
}
