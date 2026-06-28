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

package net.luis.utils.io.database.exception.database.constraint;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;

/**
 * Thrown when a not-null constraint is violated ({@code SQLState} {@code 23502}).<br>
 *
 * @author Luis-St
 */
public class SqlNotNullConstraintException extends SqlConstraintViolationException {
	
	/**
	 * Constructs a new {@code SqlNotNullConstraintException} from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlNotNullConstraintException(@Nullable String message, @NonNull SQLException cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new {@code SqlNotNullConstraintException} from the given message, cause and constraint name.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param constraintName The name of the violated constraint, may be null
	 * @throws NullPointerException If the cause is null
	 */
	public SqlNotNullConstraintException(@Nullable String message, @NonNull SQLException cause, @Nullable String constraintName) {
		super(message, cause, constraintName);
	}
}
