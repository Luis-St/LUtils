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
 *
 * @author Luis-St
 *
 */

public class SqlTypeRegistryBuilder {
	
	private final Map<SqlType<?>, SqlTypeMapping> mappings = Maps.newHashMap();
	
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull String nativeTypeName) {
		return this.register(type, new SqlTypeMapping(nativeTypeName));
	}
	
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull String nativeTypeName, @NonNull SqlValueBinder binder) {
		Objects.requireNonNull(binder, "Sql value binder must not be null");
		return this.register(type, new SqlTypeMapping(nativeTypeName, binder, null));
	}
	
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull String nativeTypeName, @NonNull SqlValueBinder binder, @NonNull SqlValueReader reader) {
		Objects.requireNonNull(binder, "Sql value binder must not be null");
		Objects.requireNonNull(reader, "Sql value reader must not be null");
		return this.register(type, new SqlTypeMapping(nativeTypeName, binder, reader));
	}
	
	public @NonNull SqlTypeRegistryBuilder register(@NonNull SqlType<?> type, @NonNull SqlTypeMapping mapping) {
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(mapping, "Sql type mapping must not be null");
		
		this.mappings.put(type, mapping);
		return this;
	}
	
	public @NonNull SqlTypeRegistry build() {
		return new SqlTypeRegistry(this.mappings);
	}
}
