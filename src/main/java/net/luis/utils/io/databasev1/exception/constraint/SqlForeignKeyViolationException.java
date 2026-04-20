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

/*package net.luis.utils.io.databasev1.exception.constraint;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

*//**
 * Exception thrown when a SQL foreign key constraint is violated.<br>
 *
 * @author Luis-St
 *//*
public class SqlForeignKeyViolationException extends SqlConstraintViolationException {
	
	*//**
	 * Constructs a new SQL foreign key violation exception with no details.<br>
	 *//*
	public SqlForeignKeyViolationException() {}
	
	*//**
	 * Constructs a new SQL foreign key violation exception with the specified message.<br>
	 * @param message The message of the exception
	 *//*
	public SqlForeignKeyViolationException(@Nullable String message) {
		super(message);
	}
	
	*//**
	 * Constructs a new SQL foreign key violation exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 *//*
	public SqlForeignKeyViolationException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	*//**
	 * Constructs a new SQL foreign key violation exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 *//*
	public SqlForeignKeyViolationException(@Nullable Throwable cause) {
		super(cause);
	}
	
	*//**
	 * Returns the name of the violated foreign key constraint.<br>
	 * @return The constraint name
	 *//*
	public @NonNull String getConstraintName() {
		throw new UnsupportedOperationException();
	}
	
	*//**
	 * Returns the name of the referenced table.<br>
	 * @return The referenced table name
	 *//*
	public @NonNull String getReferencedTableName() {
		throw new UnsupportedOperationException();
	}
	
	*//**
	 * Returns the missing key value that caused the violation.<br>
	 * @return The missing key value or {@code null} if not available
	 *//*
	public @Nullable Object getMissingKeyValue() {
		throw new UnsupportedOperationException();
	}
}*/
