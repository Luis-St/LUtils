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

import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlLookupTypeInferrer implements SqlTypeInferrer {
	
	private final Map<Class<?>, SqlType<?>> lookup;
	
	SqlLookupTypeInferrer(@NonNull Map<Class<?>, SqlType<?>> lookup) {
		this.lookup = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(lookup, "Sql type lookup map must not be null")));
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
