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
 * A concrete sql type that has been parameterized with a fixed {@link SqlParameter}.<br>
 * Instances of this class are created by configuring a {@link ParameterizableSqlType} with a parameter, for example to obtain a {@code VARCHAR(n)} or {@code DECIMAL(p, s)} type.<br>
 *
 * @see SqlType
 * @see ParameterizableSqlType
 * @see SqlParameter
 *
 * @author Luis-St
 *
 * @param <T> The java type this sql type maps to
 * @param <P> The type of parameter this type has been configured with
 */
public final class ParameterizedSqlType<T, P extends SqlParameter> implements SqlType<T> {
	
	/**
	 * The jdbc type code of this sql type.
	 */
	private final int jdbcType;
	/**
	 * The java type this sql type maps to.
	 */
	private final Class<T> javaType;
	/**
	 * The parameter this type has been configured with.
	 */
	private final P parameter;
	
	/**
	 * Constructs a new parameterized sql type with the given jdbc type, java type and parameter.<br>
	 *
	 * @param jdbcType The jdbc type code of this sql type
	 * @param javaType The java type this sql type maps to
	 * @param parameter The parameter this type is configured with
	 * @throws NullPointerException If the java type or parameter is null
	 */
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
	
	/**
	 * Returns the parameter this type has been configured with.<br>
	 * @return The parameter
	 */
	public @NonNull P parameter() {
		return this.parameter;
	}
	
	@Override
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
