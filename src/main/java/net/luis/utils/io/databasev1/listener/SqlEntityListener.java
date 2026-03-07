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

package net.luis.utils.io.databasev1.listener;

import org.jspecify.annotations.NonNull;

/**
 * Interface for entity lifecycle hooks.<br>
 * All methods have default empty implementations, so listeners can override only the events they care about.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlEntityListener<T> {
	
	/**
	 * Called before an entity is inserted.<br>
	 * @param entity The entity about to be inserted
	 */
	default void beforeInsert(@NonNull T entity) {}
	
	/**
	 * Called after an entity is inserted.<br>
	 * @param entity The entity that was inserted
	 */
	default void afterInsert(@NonNull T entity) {}
	
	/**
	 * Called before an entity is updated.<br>
	 * @param entity The entity about to be updated
	 */
	default void beforeUpdate(@NonNull T entity) {}
	
	/**
	 * Called after an entity is updated.<br>
	 * @param entity The entity that was updated
	 */
	default void afterUpdate(@NonNull T entity) {}
	
	/**
	 * Called before an entity is deleted.<br>
	 * @param entity The entity about to be deleted
	 */
	default void beforeDelete(@NonNull T entity) {}
	
	/**
	 * Called after an entity is deleted.<br>
	 * @param entity The entity that was deleted
	 */
	default void afterDelete(@NonNull T entity) {}
}
