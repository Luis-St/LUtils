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

import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public final class DirectParameterizableSqlType<T, P extends SqlParameter> implements ParameterizableSqlType<T, P> {
	
	private final int jdbcType;
	private final Class<T> javaType;
	private final Class<P> parameterType;
	
	DirectParameterizableSqlType(int jdbcType, @NonNull Class<T> javaType, @NonNull Class<P> parameterType) {
		this.jdbcType = jdbcType;
		this.javaType = Objects.requireNonNull(javaType, "Java type must not be null");
		this.parameterType = Objects.requireNonNull(parameterType, "Parameter type must not be null");
	}
	
	public int jdbcType() {
		return this.jdbcType;
	}
	
	public @NonNull Class<T> javaType() {
		return this.javaType;
	}
	
	@Override
	public @NonNull Class<P> parameterType() {
		return this.parameterType;
	}
	
	@Override
	public @NonNull ParameterizedSqlType<T, P> configure(@NonNull P parameter) {
		return new ParameterizedSqlType<>(this.jdbcType, this.javaType, parameter);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DirectParameterizableSqlType<?, ?> that)) return false;
		
		if (this.jdbcType != that.jdbcType) return false;
		if (!this.javaType.equals(that.javaType)) return false;
		return this.parameterType.equals(that.parameterType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.jdbcType, this.javaType, this.parameterType);
	}
	
	@Override
	public @NonNull String toString() {
		return "DirectParameterizableSqlType[jdbcType=" + this.jdbcType + ", javaType=" + this.javaType + ", parameterType=" + this.parameterType + "]";
	}
	//endregion
}
