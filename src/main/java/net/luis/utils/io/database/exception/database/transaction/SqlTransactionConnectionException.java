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

package net.luis.utils.io.database.exception.database.transaction;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;

/**
 * Thrown when acquiring a connection or toggling its auto-commit state for a transaction fails at the driver level.<br>
 *
 * @author Luis-St
 */
public class SqlTransactionConnectionException extends SqlDatabaseException {
	
	/**
	 * Constructs a new {@code SqlTransactionConnectionException} from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlTransactionConnectionException(@Nullable String message, @NonNull SQLException cause) {
		super(message, cause);
	}
}
