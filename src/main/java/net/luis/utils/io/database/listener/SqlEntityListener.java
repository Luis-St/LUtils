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

package net.luis.utils.io.database.listener;

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
	
	default void beforeInsert(@NonNull T entity) {}
	
	default void afterInsert(@NonNull T entity) {}
	
	default void beforeUpdate(@NonNull T entity) {}
	
	default void afterUpdate(@NonNull T entity) {}
	
	default void beforeDelete(@NonNull T entity) {}
	
	default void afterDelete(@NonNull T entity) {}
}
