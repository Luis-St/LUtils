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

import net.luis.utils.io.database.rendering.SqlRenderable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface SqlQuery<E> extends SqlRenderable {
	
	static <T> @NonNull List<T> copyAndAdd(@NonNull List<T> list, @NonNull T element) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(element, "Element must not be null");
		
		List<T> copy = new ArrayList<>(list.size() + 1);
		copy.addAll(list);
		copy.add(element);
		return Collections.unmodifiableList(copy);
	}
	
	static <T> @NonNull List<T> copyAndAddAll(@NonNull List<T> list, @NonNull Collection<? extends T> elements) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(elements, "Elements must not be null");
		
		List<T> copy = new ArrayList<>(list.size() + elements.size());
		copy.addAll(list);
		copy.addAll(elements);
		return Collections.unmodifiableList(copy);
	}
}
