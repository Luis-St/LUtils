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

import com.google.common.collect.Maps;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Objects;

/**
 * A fluent builder used to construct a {@link SqlTypeRegistry}.<br>
 * It collects the mappings between {@link SqlType}s and their {@link SqlTypeMapping} before building an immutable registry.<br>
 *
 * @see SqlTypeRegistry
 * @see SqlTypeMapping
 *
 * @author Luis-St
 */
public class SqlTypeRegistryBuilder {
	
	/**
	 * The collected mappings from sql type to its type mapping.
	 */
	private final Map<SqlType<?>, SqlTypeMapping> mappings = Maps.newHashMap();
	
	/**
	 * Registers the given native type name for the given sql type using the default value handling.<br>
	 *
	 * @param type The sql type to register the mapping for
	 * @param nativeTypeName The native database type name to map the type to
	 * @return This builder for chaining
	 * @throws NullPointerException If the type or native type name is null
	 * @throws IllegalArgumentException If the native type name is blank
	 */
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull String nativeTypeName) {
		return this.register(type, new SqlTypeMapping(nativeTypeName));
	}
	
	/**
	 * Registers the given native type name and binder for the given sql type.<br>
	 *
	 * @param type The sql type to register the mapping for
	 * @param nativeTypeName The native database type name to map the type to
	 * @param binder The binder used to write values of the type to a statement
	 * @return This builder for chaining
	 * @throws NullPointerException If the type, native type name or binder is null
	 * @throws IllegalArgumentException If the native type name is blank
	 */
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull String nativeTypeName, @NonNull SqlValueBinder binder) {
		Objects.requireNonNull(binder, "Sql value binder must not be null");
		return this.register(type, new SqlTypeMapping(nativeTypeName, binder, null));
	}
	
	/**
	 * Registers the given native type name, binder and reader for the given sql type.<br>
	 *
	 * @param type The sql type to register the mapping for
	 * @param nativeTypeName The native database type name to map the type to
	 * @param binder The binder used to write values of the type to a statement
	 * @param reader The reader used to read values of the type from a result set
	 * @return This builder for chaining
	 * @throws NullPointerException If the type, native type name, binder or reader is null
	 * @throws IllegalArgumentException If the native type name is blank
	 */
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull String nativeTypeName, @NonNull SqlValueBinder binder, @NonNull SqlValueReader reader) {
		Objects.requireNonNull(binder, "Sql value binder must not be null");
		Objects.requireNonNull(reader, "Sql value reader must not be null");
		return this.register(type, new SqlTypeMapping(nativeTypeName, binder, reader));
	}
	
	/**
	 * Registers the given type mapping for the given sql type.<br>
	 * An existing mapping for the same type is replaced.<br>
	 *
	 * @param type The sql type to register the mapping for
	 * @param mapping The type mapping to register
	 * @return This builder for chaining
	 * @throws NullPointerException If the type or mapping is null
	 */
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull SqlTypeMapping mapping) {
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(mapping, "Sql type mapping must not be null");
		
		this.mappings.put(type, mapping);
		return this;
	}
	
	/**
	 * Builds an immutable sql type registry from the collected mappings.<br>
	 * @return The built registry
	 */
	public @NonNull SqlTypeRegistry build() {
		return new SqlTypeRegistry(this.mappings);
	}
}
