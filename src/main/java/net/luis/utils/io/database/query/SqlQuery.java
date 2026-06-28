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

package net.luis.utils.io.database.query;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.rendering.SqlRenderable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Represents a sql query that can be rendered into a sql statement.<br>
 * It is the base abstraction for all query types and extends {@link SqlRenderable}.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the elements returned by the query
 */
@FunctionalInterface
public interface SqlQuery<E> extends SqlRenderable {
	
	/**
	 * Creates an unmodifiable copy of the given list with the given element appended.<br>
	 *
	 * @param list The list to copy
	 * @param element The element to append
	 * @param <T> The type of the list elements
	 * @return An unmodifiable list containing all elements of the given list followed by the given element
	 * @throws NullPointerException If the list or element is null
	 */
	static <T> @NonNull List<T> copyAndAdd(@NonNull List<T> list, @NonNull T element) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(element, "Element must not be null");
		
		List<T> copy = Lists.newArrayListWithCapacity(list.size() + 1);
		copy.addAll(list);
		copy.add(element);
		return Collections.unmodifiableList(copy);
	}
	
	/**
	 * Creates an unmodifiable copy of the given list with all the given elements appended.<br>
	 *
	 * @param list The list to copy
	 * @param elements The elements to append
	 * @param <T> The type of the list elements
	 * @return An unmodifiable list containing all elements of the given list followed by the given elements
	 * @throws NullPointerException If the list or elements is null
	 */
	static <T> @NonNull List<T> copyAndAddAll(@NonNull List<T> list, @NonNull Collection<? extends T> elements) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(elements, "Elements must not be null");
		
		List<T> copy = Lists.newArrayListWithCapacity(list.size() + elements.size());
		copy.addAll(list);
		copy.addAll(elements);
		return Collections.unmodifiableList(copy);
	}
}
