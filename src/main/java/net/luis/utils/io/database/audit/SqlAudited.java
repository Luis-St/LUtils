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

package net.luis.utils.io.database.audit;

import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Wraps an entity together with its {@link SqlAuditMetadata audit metadata}.<br>
 * Provides convenience accessors that delegate to the wrapped audit metadata.<br>
 *
 * @author Luis-St
 *
 * @param entity The audited entity
 * @param audit The audit metadata of the entity
 * @param <T> The type of the audited entity
 */
public record SqlAudited<T>(
	@NonNull T entity,
	@NonNull SqlAuditMetadata audit
) {
	
	/**
	 * Constructs a new audited entity with the given entity and audit metadata.<br>
	 * @throws NullPointerException If the entity or audit metadata is null
	 */
	public SqlAudited {
		Objects.requireNonNull(entity, "Audited entity must not be null");
		Objects.requireNonNull(audit, "Sql audit metadata must not be null");
	}
	
	/**
	 * Creates a new audited entity with the given entity and audit metadata.<br>
	 *
	 * @param entity The audited entity
	 * @param audit The audit metadata of the entity
	 * @param <T> The type of the audited entity
	 * @return The newly created audited entity
	 * @throws NullPointerException If the entity or audit metadata is null
	 */
	public static <T> @NonNull SqlAudited<T> of(@NonNull T entity, @NonNull SqlAuditMetadata audit) {
		return new SqlAudited<>(entity, audit);
	}
	
	/**
	 * Returns the version of the audited entity.<br>
	 * @return The version or an empty optional if no version is present
	 */
	public @NonNull OptionalLong version() {
		return this.audit.version();
	}
	
	/**
	 * Returns the timestamp at which the entity was created.<br>
	 * @return The created-at timestamp or an empty optional if not present
	 */
	public @NonNull Optional<LocalDateTime> createdAt() {
		return this.audit.createdAt();
	}
	
	/**
	 * Returns the user that created the entity.<br>
	 * @return The created-by user or an empty optional if not present
	 */
	public @NonNull Optional<String> createdBy() {
		return this.audit.createdBy();
	}
	
	/**
	 * Returns the timestamp at which the entity was last updated.<br>
	 * @return The updated-at timestamp or an empty optional if not present
	 */
	public @NonNull Optional<LocalDateTime> updatedAt() {
		return this.audit.updatedAt();
	}
	
	/**
	 * Returns the user that last updated the entity.<br>
	 * @return The updated-by user or an empty optional if not present
	 */
	public @NonNull Optional<String> updatedBy() {
		return this.audit.updatedBy();
	}
}
