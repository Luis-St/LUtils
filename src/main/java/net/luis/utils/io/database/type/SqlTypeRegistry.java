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
 *
 * @author Luis-St
 *
 */

public final class SqlTypeRegistry {
	
	private static final SqlTypeRegistry EMPTY = new SqlTypeRegistry(Map.of());
	
	private final Map<SqlType<?>, SqlTypeMapping> mappings;
	
	SqlTypeRegistry(@NonNull Map<SqlType<?>, SqlTypeMapping> mappings) {
		this.mappings = Map.copyOf(Objects.requireNonNull(mappings, "Sql type mappings must not be null"));
	}
	
	public static @NonNull SqlTypeRegistry empty() {
		return EMPTY;
	}
	
	public static @NonNull SqlTypeRegistryBuilder builder() {
		return new SqlTypeRegistryBuilder();
	}
	
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
