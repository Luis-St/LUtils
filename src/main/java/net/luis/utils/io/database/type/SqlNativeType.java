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

import java.sql.Types;
import java.util.Locale;
import java.util.Objects;

/**
 * Describes a column type as reported by a jdbc driver.<br>
 * It carries the full metadata required to resolve the matching {@link SqlType}, the jdbc type code alone is not sufficient because drivers report several distinct native types under the same code,
 * most notably {@link Types#OTHER} which is used by PostgreSQL for {@code uuid}, {@code jsonb}, {@code inet} and {@code cidr} alike.<br>
 *
 * @see SqlTypeRegistry
 * @see SqlNativeTypeMapper
 *
 * @author Luis-St
 *
 * @param jdbcType The jdbc type code as defined in {@link Types}
 * @param typeName The native type name as reported by the driver
 * @param columnSize The column size as reported by the driver
 * @param decimalDigits The number of decimal digits as reported by the driver
 */
public record SqlNativeType(
	int jdbcType,
	@NonNull String typeName,
	int columnSize,
	int decimalDigits
) {
	
	/**
	 * Constructs a new native type with the given values.<br>
	 * @throws NullPointerException If the type name is null
	 */
	public SqlNativeType {
		Objects.requireNonNull(typeName, "Sql native type name must not be null");
	}
	
	/**
	 * Constructs a new native type with the given jdbc type code and type name without a column size or decimal digits.<br>
	 *
	 * @param jdbcType The jdbc type code as defined in {@link Types}
	 * @param typeName The native type name as reported by the driver
	 * @throws NullPointerException If the type name is null
	 */
	public SqlNativeType(int jdbcType, @NonNull String typeName) {
		this(jdbcType, typeName, 0, 0);
	}
	
	/**
	 * Normalizes the given native type name into the form used to look up types in a {@link SqlTypeRegistry}.<br>
	 * The name is lowercased, trimmed and stripped of any type arguments and modifiers, so that {@code VARBINARY(64)}, {@code varbinary (64)} and {@code VARBINARY} all normalize to {@code varbinary}.<br>
	 *
	 * @param typeName The native type name to normalize
	 * @return The normalized native type name
	 * @throws NullPointerException If the type name is null
	 */
	public static @NonNull String normalize(@NonNull String typeName) {
		Objects.requireNonNull(typeName, "Sql native type name must not be null");
		
		String normalized = typeName.trim().toLowerCase(Locale.ROOT);
		int argumentIndex = normalized.indexOf('(');
		if (argumentIndex >= 0) {
			normalized = normalized.substring(0, argumentIndex);
		}
		return normalized.trim();
	}
	
	/**
	 * Returns the {@link #typeName() type name} of this native type in its {@link #normalize(String) normalized} form.<br>
	 * @return The normalized native type name
	 */
	public @NonNull String normalizedTypeName() {
		return normalize(this.typeName);
	}
}
