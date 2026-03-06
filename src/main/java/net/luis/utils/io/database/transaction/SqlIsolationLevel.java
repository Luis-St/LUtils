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
 * Enum representing the SQL transaction isolation levels.<br>
 *
 * @author Luis-St
 */
public enum SqlIsolationLevel {
	
	/**
	 * The highest isolation level, where transactions are completely isolated from each other.<br>
	 * It prevents dirty reads, non-repeatable reads, and phantom reads, but can lead to reduced concurrency and performance.<br>
	 */
	SERIALIZABLE,
	/**
	 * A lower isolation level than SERIALIZABLE, where transactions can read data that has been modified by other transactions but not yet committed.<br>
	 * It prevents dirty reads and non-repeatable reads, but allows phantom reads.<br>
	 */
	REPEATABLE_READ,
	/**
	 * A lower isolation level than REPEATABLE_READ, where transactions can read data that has been modified by other transactions but not yet committed.<br>
	 * It prevents dirty reads but allows non-repeatable reads and phantom reads.<br>
	 */
	READ_COMMITTED,
	/**
	 * The lowest isolation level, where transactions can read data that has been modified by other transactions but not yet committed.<br>
	 * It allows dirty reads, non-repeatable reads, and phantom reads, but can lead to higher concurrency and performance.<br>
	 */
	READ_UNCOMMITTED
}
