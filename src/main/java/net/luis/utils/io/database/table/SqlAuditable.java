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

package net.luis.utils.io.database.table;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Interface for entities that support audit tracking.<br>
 * <p>
 *     Entities implementing this interface can track creation and modification timestamps and user identifiers.<br>
 *     All fields are optional.<br>
 *     Getters return {@code null} for unconfigured fields, and {@code with*} methods throw {@link UnsupportedOperationException} for unconfigured fields.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The self-referential entity type
 */
public interface SqlAuditable<T extends SqlAuditable<T>> {

	/**
	 * Returns the timestamp when this entity was created.<br>
	 * @return The creation timestamp, or {@code null} if not configured
	 */
	default @Nullable Instant createdAt() {
		return null;
	}

	/**
	 * Returns the timestamp when this entity was last updated.<br>
	 * @return The last update timestamp, or {@code null} if not configured
	 */
	default @Nullable Instant updatedAt() {
		return null;
	}

	/**
	 * Returns the identifier of the user who created this entity.<br>
	 * @return The creator identifier, or {@code null} if not configured
	 */
	default @Nullable String createdBy() {
		return null;
	}

	/**
	 * Returns the identifier of the user who last updated this entity.<br>
	 * @return The last updater identifier, or {@code null} if not configured
	 */
	default @Nullable String updatedBy() {
		return null;
	}

	/**
	 * Returns an immutable copy of this entity with the specified creation timestamp.<br>
	 *
	 * @param createdAt The creation timestamp
	 * @return A new entity instance with the updated creation timestamp
	 * @throws UnsupportedOperationException If the creation timestamp is not configured
	 */
	default @NonNull T withCreatedAt(@NonNull Instant createdAt) {
		throw new UnsupportedOperationException("createdAt not configured");
	}

	/**
	 * Returns an immutable copy of this entity with the specified update timestamp.<br>
	 *
	 * @param updatedAt The update timestamp
	 * @return A new entity instance with the updated update timestamp
	 * @throws UnsupportedOperationException If the update timestamp is not configured
	 */
	default @NonNull T withUpdatedAt(@NonNull Instant updatedAt) {
		throw new UnsupportedOperationException("updatedAt not configured");
	}

	/**
	 * Returns an immutable copy of this entity with the specified creator identifier.<br>
	 *
	 * @param createdBy The creator identifier
	 * @return A new entity instance with the updated creator identifier
	 * @throws UnsupportedOperationException If the creator identifier is not configured
	 */
	default @NonNull T withCreatedBy(@NonNull String createdBy) {
		throw new UnsupportedOperationException("createdBy not configured");
	}

	/**
	 * Returns an immutable copy of this entity with the specified updater identifier.<br>
	 *
	 * @param updatedBy The updater identifier
	 * @return A new entity instance with the updated updater identifier
	 * @throws UnsupportedOperationException If the updater identifier is not configured
	 */
	default @NonNull T withUpdatedBy(@NonNull String updatedBy) {
		throw new UnsupportedOperationException("updatedBy not configured");
	}
}
