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

import java.util.Objects;

/**
 * A simple scalar sql type that maps a single java type to a fixed jdbc type code.<br>
 *
 * @see SqlType
 *
 * @author Luis-St
 *
 * @param <T> The java type this scalar type maps
 */
public final class SqlScalarType<T> implements SqlType<T> {
	
	/**
	 * The jdbc type code of this scalar type.
	 */
	private final int jdbcType;
	/**
	 * The java type this scalar type maps.
	 */
	private final Class<T> javaType;
	
	/**
	 * Constructs a new scalar sql type with the given jdbc type code and java type.<br>
	 *
	 * @param jdbcType The jdbc type code of the scalar type
	 * @param javaType The java type the scalar type maps
	 * @throws NullPointerException If the java type is null
	 */
	SqlScalarType(int jdbcType, @NonNull Class<T> javaType) {
		this.jdbcType = jdbcType;
		this.javaType = Objects.requireNonNull(javaType, "Java type must not be null");
	}
	
	@Override
	public int jdbcType() {
		return this.jdbcType;
	}
	
	@Override
	public @NonNull Class<T> javaType() {
		return this.javaType;
	}
	
	@Override
	public @NonNull SqlArrayType<T> array() {
		return new SqlArrayType<>(this);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlScalarType<?> that)) return false;
		
		if (this.jdbcType != that.jdbcType) return false;
		return this.javaType.equals(that.javaType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.jdbcType, this.javaType);
	}
	
	@Override
	public @NonNull String toString() {
		return "SqlScalarType[jdbcType=" + this.jdbcType + ", javaType=" + this.javaType + "]";
	}
	//endregion
}
