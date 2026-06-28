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

import java.util.Map;

/**
 * Resolves the {@link SqlType} that should be used to store a given java value.<br>
 * Implementations map the runtime class of a value to a matching sql type, for example to bind a value to a statement or to create a column definition.<br>
 *
 * @see SqlStandardTypeInferrer
 * @see SqlLookupTypeInferrer
 *
 * @author Luis-St
 */
public interface SqlTypeInferrer {
	
	/**
	 * Returns the standard type inferrer that recognizes the built-in java and time types supported by the library.<br>
	 * @return The standard sql type inferrer
	 */
	static @NonNull SqlTypeInferrer standard() {
		return SqlStandardTypeInferrer.INSTANCE;
	}
	
	/**
	 * Creates a type inferrer that resolves sql types from the given lookup map.<br>
	 * The map is queried first by exact class and then by assignability.<br>
	 *
	 * @param lookup The map from java classes to their corresponding sql types
	 * @return A lookup-based sql type inferrer backed by the given map
	 * @throws NullPointerException If the lookup map is null
	 */
	static @NonNull SqlTypeInferrer of(@NonNull Map<Class<?>, SqlType<?>> lookup) {
		return new SqlLookupTypeInferrer(lookup);
	}
	
	/**
	 * Infers the sql type that matches the given java value based on its runtime class.<br>
	 *
	 * @param value The value to infer the sql type for
	 * @return The sql type matching the value
	 * @throws NullPointerException If the value is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the value
	 */
	<T> @NonNull SqlType<T> inferType(@NonNull T value) throws SqlTypeNotFoundException;
}
