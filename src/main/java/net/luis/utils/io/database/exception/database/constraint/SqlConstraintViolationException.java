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

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Thrown when an integrity constraint is violated ({@code SQLState} class {@code 23}).<br>
 * This is the base type for the more specific unique, foreign key, not-null and check constraint failures and
 * is used directly when the violated constraint kind cannot be determined.<br>
 *
 * @author Luis-St
 */
public class SqlConstraintViolationException extends SqlDatabaseException {
	
	/**
	 * The name of the violated constraint, or null if unknown.<br>
	 */
	private final @Nullable String constraintName;
	
	/**
	 * Constructs a new constraint violation exception from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlConstraintViolationException(@Nullable String message, @NonNull SQLException cause) {
		this(message, cause, null);
	}
	
	/**
	 * Constructs a new constraint violation exception from the given message, cause and constraint name.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param constraintName The name of the violated constraint, may be null
	 * @throws NullPointerException If the cause is null
	 */
	public SqlConstraintViolationException(@Nullable String message, @NonNull SQLException cause, @Nullable String constraintName) {
		super(message, cause);
		this.constraintName = constraintName;
	}
	
	/**
	 * Returns the name of the violated constraint.<br>
	 * @return An optional holding the constraint name, or an empty optional if unknown
	 */
	public @NonNull Optional<String> constraintName() {
		return Optional.ofNullable(this.constraintName);
	}
}
