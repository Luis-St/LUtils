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

package net.luis.utils.io.database.exception.database.statement;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.OptionalInt;

/**
 * Thrown when binding a value to a prepared statement fails at the driver level.<br>
 *
 * @author Luis-St
 */
public class SqlStatementBindException extends SqlDatabaseException {
	
	/**
	 * Sentinel value used for {@link #columnIndex} when the offending column index is unknown.<br>
	 */
	private static final int UNKNOWN_COLUMN_INDEX = -1;
	
	/**
	 * The one based index of the parameter that failed to bind, or {@code -1} if unknown.<br>
	 */
	private final int columnIndex;
	
	/**
	 * Constructs a new statement bind exception from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlStatementBindException(@Nullable String message, @NonNull SQLException cause) {
		this(message, cause, UNKNOWN_COLUMN_INDEX);
	}
	
	/**
	 * Constructs a new statement bind exception from the given message, cause and column index.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param columnIndex The one based index of the parameter that failed to bind, or {@code -1} if unknown
	 * @throws NullPointerException If the cause is null
	 */
	public SqlStatementBindException(@Nullable String message, @NonNull SQLException cause, int columnIndex) {
		super(message, cause);
		this.columnIndex = columnIndex;
	}
	
	/**
	 * Returns the one based index of the parameter that failed to bind.<br>
	 * @return An optional int holding the column index, or an empty optional int if unknown
	 */
	public @NonNull OptionalInt columnIndex() {
		return this.columnIndex == UNKNOWN_COLUMN_INDEX ? OptionalInt.empty() : OptionalInt.of(this.columnIndex);
	}
}
