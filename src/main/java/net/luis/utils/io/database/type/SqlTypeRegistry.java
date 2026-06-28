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

import java.util.*;

/**
 * An immutable registry that maps {@link SqlType}s to their corresponding {@link SqlTypeMapping}.<br>
 * It is used by a dialect to resolve the native type name, binder and reader for a given type.<br>
 *
 * @see SqlTypeMapping
 * @see SqlTypeRegistryBuilder
 *
 * @author Luis-St
 */
public final class SqlTypeRegistry {
	
	/**
	 * A shared empty registry that contains no mappings.
	 */
	private static final SqlTypeRegistry EMPTY = new SqlTypeRegistry(Map.of());
	
	/**
	 * The immutable mappings from sql type to its type mapping.
	 */
	private final Map<SqlType<?>, SqlTypeMapping> mappings;
	
	/**
	 * Constructs a new sql type registry with the given mappings.<br>
	 * The mappings are copied into an immutable map.<br>
	 *
	 * @param mappings The mappings from sql type to its type mapping
	 * @throws NullPointerException If the mappings are null
	 */
	SqlTypeRegistry(@NonNull Map<SqlType<?>, SqlTypeMapping> mappings) {
		this.mappings = Map.copyOf(Objects.requireNonNull(mappings, "Sql type mappings must not be null"));
	}
	
	/**
	 * Returns a shared empty registry that contains no mappings.<br>
	 * @return The empty registry
	 */
	public static @NonNull SqlTypeRegistry empty() {
		return EMPTY;
	}
	
	/**
	 * Creates a new builder used to construct a sql type registry.<br>
	 * @return A new registry builder
	 */
	public static @NonNull SqlTypeRegistryBuilder builder() {
		return new SqlTypeRegistryBuilder();
	}
	
	/**
	 * Resolves the type mapping registered for the given sql type.<br>
	 *
	 * @param type The sql type to resolve the mapping for
	 * @return An optional containing the mapping or an empty optional if no mapping is registered
	 * @throws NullPointerException If the type is null
	 */
	public @NonNull Optional<SqlTypeMapping> resolve(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return Optional.ofNullable(this.mappings.get(type));
	}
	
	//region Object overrides
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlTypeRegistry that)) return false;
		
		return this.mappings.equals(that.mappings);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.mappings);
	}
	
	@Override
	public @NonNull String toString() {
		return "SqlTypeRegistry" + this.mappings;
	}
	//endregion
}
