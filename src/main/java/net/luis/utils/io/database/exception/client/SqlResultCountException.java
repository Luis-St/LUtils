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

package net.luis.utils.io.database.exception.client;

import net.luis.utils.io.database.exception.SqlClientException;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when a query returns a different number of rows than the caller required,<br>
 * for example when a single result was requested but several rows matched.<br>
 *
 * @author Luis-St
 */
public class SqlResultCountException extends SqlClientException {
	
	/**
	 * The number of rows the caller expected.<br>
	 */
	private final long expected;
	/**
	 * The number of rows that were actually returned.<br>
	 */
	private final long actual;
	
	/**
	 * Constructs a new result count exception with the given message, expected and actual counts.<br>
	 *
	 * @param message The detail message, may be null
	 * @param expected The number of rows the caller expected
	 * @param actual The number of rows that were actually returned
	 */
	public SqlResultCountException(@Nullable String message, long expected, long actual) {
		super(message);
		this.expected = expected;
		this.actual = actual;
	}
	
	/**
	 * Returns the number of rows the caller expected.<br>
	 * @return The expected row count
	 */
	public long expected() {
		return this.expected;
	}
	
	/**
	 * Returns the number of rows that were actually returned.<br>
	 * @return The actual row count
	 */
	public long actual() {
		return this.actual;
	}
}
