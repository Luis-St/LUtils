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

public final class ParameterizedSqlType<T, P extends SqlParameter> implements SqlType<T> {
	
	private final int jdbcType;
	private final Class<T> javaType;
	private final P parameter;
	
	ParameterizedSqlType(int jdbcType, @NonNull Class<T> javaType, @NonNull P parameter) {
		this.jdbcType = jdbcType;
		this.javaType = Objects.requireNonNull(javaType, "Java type must not be null");
		this.parameter = Objects.requireNonNull(parameter, "Parameter must not be null");
	}
	
	@Override
	public int jdbcType() {
		return this.jdbcType;
	}
	
	@Override
	public @NonNull Class<T> javaType() {
		return this.javaType;
	}
	
	public @NonNull P parameter() {
		return this.parameter;
	}
	
	public @NonNull SqlArrayType<T> array() {
		return new SqlArrayType<>(this);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ParameterizedSqlType<?, ?> that)) return false;
		
		if (this.jdbcType != that.jdbcType) return false;
		if (!this.javaType.equals(that.javaType)) return false;
		return this.parameter.equals(that.parameter);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.jdbcType, this.javaType, this.parameter);
	}
	
	@Override
	public @NonNull String toString() {
		return "ParameterizedSqlType[jdbcType=" + this.jdbcType + ", javaType=" + this.javaType + ", parameter=" + this.parameter + "]";
	}
	//endregion
}
