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

package net.luis.utils.io.database.type;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Maps a {@link SqlType} to its native database type name and the optional binder and reader used for value conversion.<br>
 * The binder and reader override the default value handling of the type when they are present.<br>
 *
 * @see SqlTypeRegistry
 * @see SqlValueBinder
 * @see SqlValueReader
 *
 * @author Luis-St
 *
 * @param nativeTypeName The native database type name this mapping maps to
 * @param binder The binder used to write values to a statement or {@code null} to use the default binding
 * @param reader The reader used to read values from a result set or {@code null} to use the default reading
 */
public record SqlTypeMapping(
	@NonNull String nativeTypeName,
	@Nullable SqlValueBinder binder,
	@Nullable SqlValueReader reader
) {
	
	/**
	 * Constructs a new sql type mapping with the given native type name, binder and reader.<br>
	 *
	 * @param nativeTypeName The native database type name this mapping maps to
	 * @param binder The binder used to write values to a statement or {@code null} to use the default binding
	 * @param reader The reader used to read values from a result set or {@code null} to use the default reading
	 * @throws NullPointerException If the native type name is null
	 * @throws IllegalArgumentException If the native type name is blank
	 */
	public SqlTypeMapping {
		Objects.requireNonNull(nativeTypeName, "Sql native type name must not be null");
		
		if (nativeTypeName.isBlank()) {
			throw new IllegalArgumentException("Sql native type name must not be blank");
		}
	}
	
	/**
	 * Constructs a new sql type mapping with the given native type name and no binder or reader.<br>
	 *
	 * @param nativeTypeName The native database type name this mapping maps to
	 * @throws NullPointerException If the native type name is null
	 * @throws IllegalArgumentException If the native type name is blank
	 */
	public SqlTypeMapping(@NonNull String nativeTypeName) {
		this(nativeTypeName, null, null);
	}
}
