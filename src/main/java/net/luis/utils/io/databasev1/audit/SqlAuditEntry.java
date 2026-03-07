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

package net.luis.utils.io.databasev1.audit;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Interface representing a SQL audit entry.<br>
 * Audit entries track the entity, operation type, timestamp, and user associated with a database operation.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the audited entity
 */
public interface SqlAuditEntry<T> {
	
	/**
	 * Creates an audit entry for an {@code INSERT} operation.<br>
	 *
	 * @param entity The entity being inserted
	 * @param <T> The type of the audited entity
	 * @return The audit entry
	 */
	static <T> @NonNull SqlAuditEntry<T> forCreate(@NonNull T entity) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates an audit entry for an {@code UPDATE} operation.<br>
	 *
	 * @param entity The entity being updated
	 * @param <T> The type of the audited entity
	 * @return The audit entry
	 */
	static <T> @NonNull SqlAuditEntry<T> forUpdate(@NonNull T entity) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates an audit entry for a {@code DELETE} operation.<br>
	 *
	 * @param entity The entity being deleted
	 * @param <T> The type of the audited entity
	 * @return The audit entry
	 */
	static <T> @NonNull SqlAuditEntry<T> forDelete(@NonNull T entity) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the entity associated with this audit entry.<br>
	 * @return The audited entity
	 */
	@NonNull T getEntity();
	
	/**
	 * Returns the type of operation that was performed.<br>
	 * @return The audit operation type
	 */
	@NonNull SqlAuditOperation getOperation();
	
	/**
	 * Returns the timestamp when the operation was performed.<br>
	 * @return The operation timestamp
	 */
	@NonNull Instant getTimestamp();
	
	/**
	 * Returns the user who performed the operation.<br>
	 * @return The user, or {@code null} if no audit context was set
	 */
	@Nullable String getUser();
}
