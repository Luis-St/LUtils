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
 * A parameterizable sql type whose java value is bound and read directly, without any conversion.<br>
 * Configuring this type with a parameter produces a plain {@link ParameterizedSqlType} that binds and reads the java type as-is.<br>
 *
 * @see ParameterizableSqlType
 * @see ParameterizedSqlType
 * @see MappedParameterizableSqlType
 *
 * @author Luis-St
 *
 * @param <T> The java type the configured sql type maps to
 * @param <P> The type of parameter required to configure this type
 */
public final class DirectParameterizableSqlType<T, P extends SqlParameter> implements ParameterizableSqlType<T, P> {
	
	/**
	 * The jdbc type code of the configured sql type.
	 */
	private final int jdbcType;
	/**
	 * The java type the configured sql type maps to.
	 */
	private final Class<T> javaType;
	/**
	 * The class of the parameter required to configure this type.
	 */
	private final Class<P> parameterType;
	
	/**
	 * Constructs a new direct parameterizable sql type with the given jdbc type, java type and parameter type.<br>
	 *
	 * @param jdbcType The jdbc type code of the configured sql type
	 * @param javaType The java type the configured sql type maps to
	 * @param parameterType The class of the parameter required to configure this type
	 * @throws NullPointerException If the java type or parameter type is null
	 */
	DirectParameterizableSqlType(int jdbcType, @NonNull Class<T> javaType, @NonNull Class<P> parameterType) {
		this.jdbcType = jdbcType;
		this.javaType = Objects.requireNonNull(javaType, "Java type must not be null");
		this.parameterType = Objects.requireNonNull(parameterType, "Parameter type must not be null");
	}
	
	/**
	 * Returns the jdbc type code of the configured sql type.<br>
	 * @return The jdbc type code
	 */
	public int jdbcType() {
		return this.jdbcType;
	}
	
	/**
	 * Returns the java type the configured sql type maps to.<br>
	 * @return The java type class
	 */
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
