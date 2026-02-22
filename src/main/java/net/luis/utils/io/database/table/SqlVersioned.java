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

/**
 * Interface for entities that support optimistic locking via a version column.<br>
 * <p>
 *     Entities implementing this interface participate in automatic version checks during updates and deletes.<br>
 *     The framework adds {@code WHERE version = ?} and {@code SET version = version + 1} clauses automatically.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The self-referential entity type
 */
public interface SqlVersioned<T extends SqlVersioned<T>> {
	
	/**
	 * Returns the current version of this entity.<br>
	 * @return The version number, starting at {@code 0} for new entities
	 */
	long version();
	
	/**
	 * Returns an immutable copy of this entity with the specified version.<br>
	 *
	 * @param version The new version number
	 * @return A new entity instance with the updated version
	 */
	@NonNull T withVersion(long version);
}
