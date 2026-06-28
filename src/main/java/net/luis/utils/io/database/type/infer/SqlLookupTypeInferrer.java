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

package net.luis.utils.io.database.type.infer;

import com.google.common.collect.Maps;
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * A {@link SqlTypeInferrer} that resolves sql types from a fixed lookup map.<br>
 * The lookup is first queried by the exact runtime class of the value and, if no match is found, by iterating the entries in insertion order and selecting the first key that is assignable from the value's class.<br>
 *
 * @see SqlTypeInferrer
 *
 * @author Luis-St
 */
public class SqlLookupTypeInferrer implements SqlTypeInferrer {
	
	/**
	 * The unmodifiable map from java classes to their corresponding sql types.<br>
	 */
	private final Map<Class<?>, SqlType<?>> lookup;
	
	/**
	 * Constructs a new lookup type inferrer with the given lookup map.<br>
	 * The map is copied into an unmodifiable {@link LinkedHashMap} to preserve its iteration order.<br>
	 *
	 * @param lookup The map from java classes to their corresponding sql types
	 * @throws NullPointerException If the lookup map is null
	 */
	SqlLookupTypeInferrer(@NonNull Map<Class<?>, SqlType<?>> lookup) {
		this.lookup = Collections.unmodifiableMap(Maps.newLinkedHashMap(Objects.requireNonNull(lookup, "Sql type lookup map must not be null")));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull <T> SqlType<T> inferType(@NonNull T value) throws SqlTypeNotFoundException {
		Objects.requireNonNull(value, "Value must not be null");
		
		SqlType<?> type = this.lookup.get(value.getClass());
		if (type == null) {
			for (Map.Entry<Class<?>, SqlType<?>> entry : this.lookup.entrySet()) {
				if (entry.getKey().isAssignableFrom(value.getClass())) {
					return (SqlType<T>) entry.getValue();
				}
			}
			throw new SqlTypeNotFoundException("No sql type found for java type " + value.getClass().getName() + " in lookup");
		}
		return (SqlType<T>) type;
	}
}
