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

package net.luis.utils.io.database.exception.entity;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Exception thrown when a SQL entity is stale.<br>
 * <p>
 *     A stale entity indicates that the row was modified or deleted by another
 *     transaction between the time it was read and the time the update or delete
 *     was attempted. This is the primary mechanism for optimistic locking.<br>
 * </p>
 *
 * @author Luis-St
 */
public class SqlStaleEntityException extends SqlEntityException {
	
	/**
	 * Constructs a new SQL stale entity exception with no details.<br>
	 */
	public SqlStaleEntityException() {}
	
	/**
	 * Constructs a new SQL stale entity exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public SqlStaleEntityException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new SQL stale entity exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public SqlStaleEntityException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new SQL stale entity exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public SqlStaleEntityException(@Nullable Throwable cause) {}
	
	/**
	 * Constructs a new SQL stale entity exception with version context information.<br>
	 *
	 * @param entityType The type of the stale entity
	 * @param entityId The identifier of the stale entity
	 * @param expectedVersion The version that was expected but not found
	 */
	public SqlStaleEntityException(@NonNull Class<?> entityType, @NonNull Object entityId, long expectedVersion) {}
	
	/**
	 * Returns the type of the stale entity.<br>
	 * @return The entity type, or {@code null} if not available
	 */
	public @Nullable Class<?> getEntityType() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the identifier of the stale entity.<br>
	 * @return The entity identifier, or {@code null} if not available
	 */
	public @Nullable Object getEntityId() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the version that was expected but not found in the database.<br>
	 * @return The expected version, or {@code -1} if not available
	 */
	public long getExpectedVersion() {
		throw new UnsupportedOperationException();
	}
}
