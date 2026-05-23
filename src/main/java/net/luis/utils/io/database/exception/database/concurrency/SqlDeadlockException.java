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

package net.luis.utils.io.database.exception.database.concurrency;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;

/**
 * Thrown when a transaction is rolled back due to a deadlock or serialization failure ({@code SQLState} {@code 40001}/{@code 40P01}); the failure is transient.<br>
 *
 * @author Luis-St
 */
public class SqlDeadlockException extends SqlDatabaseException {
	
	/**
	 * Constructs a new {@code SqlDeadlockException} from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlDeadlockException(@Nullable String message, @NonNull SQLException cause) {
		super(message, cause, true);
	}
}
