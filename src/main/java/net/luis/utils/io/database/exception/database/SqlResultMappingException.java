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

package net.luis.utils.io.database.exception.database;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * Thrown when a value cannot be retrieved from a result set or mapped to its target java type.<br>
 *
 * @author Luis-St
 */
public class SqlResultMappingException extends SqlDatabaseException {
	
	/**
	 * The java type the value was being mapped to, or null if unknown.<br>
	 */
	private final @Nullable Class<?> targetType;
	/**
	 * The name or label of the result set column being mapped, or null if unknown.<br>
	 */
	private final @Nullable String column;
	
	/**
	 * Constructs a new result mapping exception from the given message and cause.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @throws NullPointerException If the cause is null
	 */
	public SqlResultMappingException(@Nullable String message, @NonNull SQLException cause) {
		this(message, cause, null, null);
	}
	
	/**
	 * Constructs a new result mapping exception from the given message, cause, target type and column.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The sql exception cause
	 * @param targetType The java type the value was being mapped to, may be null
	 * @param column The name or label of the result set column, may be null
	 * @throws NullPointerException If the cause is null
	 */
	public SqlResultMappingException(@Nullable String message, @NonNull SQLException cause, @Nullable Class<?> targetType, @Nullable String column) {
		super(message, cause);
		this.targetType = targetType;
		this.column = column;
	}
	
	/**
	 * Constructs a new result mapping exception from the given message, a non-jdbc cause and target type.<br>
	 * This is used when mapping a retrieved value to its target java type fails for a reason other than a
	 * {@link SQLException} (for example a reflective construction failure); no jdbc diagnostics are carried.<br>
	 *
	 * @param message The detail message, may be null
	 * @param cause The cause of the mapping failure
	 * @param targetType The java type the value was being mapped to, may be null
	 * @throws NullPointerException If the cause is null
	 */
	public SqlResultMappingException(@Nullable String message, @NonNull Throwable cause, @Nullable Class<?> targetType) {
		super(message, null, -1);
		this.initCause(Objects.requireNonNull(cause, "Cause must not be null"));
		this.targetType = targetType;
		this.column = null;
	}
	
	/**
	 * Returns the java type the value was being mapped to.<br>
	 * @return An optional holding the target type, or an empty optional if unknown
	 */
	public @NonNull Optional<Class<?>> targetType() {
		return Optional.ofNullable(this.targetType);
	}
	
	/**
	 * Returns the name or label of the result set column being mapped.<br>
	 * @return An optional holding the column, or an empty optional if unknown
	 */
	public @NonNull Optional<String> column() {
		return Optional.ofNullable(this.column);
	}
}
