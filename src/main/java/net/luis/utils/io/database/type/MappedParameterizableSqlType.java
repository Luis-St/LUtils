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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A parameterizable sql type that maps the java value of an underlying source type to a different java type.<br>
 * Configuring this type with a parameter configures the wrapped source type and applies the conversion functions to the resulting {@link SqlType}.<br>
 *
 * @see ParameterizableSqlType
 * @see DirectParameterizableSqlType
 * @see MappedSqlType
 *
 * @author Luis-St
 *
 * @param <S> The java type of the underlying source type
 * @param <T> The java type this type maps the source value to
 * @param <P> The type of parameter required to configure this type
 */
public final class MappedParameterizableSqlType<S, T, P extends SqlParameter> implements ParameterizableSqlType<T, P> {
	
	/**
	 * The underlying source type that is configured and then mapped.
	 */
	private final ParameterizableSqlType<S, P> sourceType;
	/**
	 * The java type this type maps the source value to.
	 */
	private final Class<T> javaType;
	/**
	 * The function converting a target value into a source value when binding a value.
	 */
	private final ThrowableFunction<@Nullable T, @Nullable S, SqlStatementBindException> fromTargetToSource;
	/**
	 * The function converting a source value into a target value when reading a value.
	 */
	private final ThrowableFunction<@NonNull S, @Nullable T, SqlClientException> fromSourceToTarget;
	
	/**
	 * Constructs a new mapped parameterizable sql type wrapping the given source type.<br>
	 *
	 * @param sourceType The underlying source type to configure and map
	 * @param javaType The java type this type maps the source value to
	 * @param fromTargetToSource The function converting a target value into a source value when binding a value
	 * @param fromSourceToTarget The function converting a source value into a target value when reading a value
	 * @throws NullPointerException If the source type, java type or any of the functions is null
	 */
	MappedParameterizableSqlType(
		@NonNull ParameterizableSqlType<S, P> sourceType,
		@NonNull Class<T> javaType,
		@NonNull ThrowableFunction<@Nullable T, @Nullable S, SqlStatementBindException> fromTargetToSource,
		@NonNull ThrowableFunction<@NonNull S, @Nullable T, SqlClientException> fromSourceToTarget
	) {
		this.sourceType = Objects.requireNonNull(sourceType, "Source type must not be null");
		this.javaType = Objects.requireNonNull(javaType, "Java type must not be null");
		this.fromTargetToSource = Objects.requireNonNull(fromTargetToSource, "Getter function must not be null");
		this.fromSourceToTarget = Objects.requireNonNull(fromSourceToTarget, "Setter function must not be null");
	}
	
	/**
	 * Returns the underlying source type that is configured and then mapped.<br>
	 * @return The source type
	 */
	public @NonNull ParameterizableSqlType<S, P> sourceType() {
		return this.sourceType;
	}
	
	/**
	 * Returns the java type this type maps the source value to.<br>
	 * @return The java type class
	 */
	public @NonNull Class<T> javaType() {
		return this.javaType;
	}
	
	/**
	 * Returns the function converting a target value into a source value when binding a value.<br>
	 * @return The target-to-source conversion function
	 */
	public @NonNull ThrowableFunction<@Nullable T, @Nullable S, SqlStatementBindException> fromTargetToSource() {
		return this.fromTargetToSource;
	}
	
	/**
	 * Returns the function converting a source value into a target value when reading a value.<br>
	 * @return The source-to-target conversion function
	 */
	public @NonNull ThrowableFunction<@NonNull S, @Nullable T, SqlClientException> fromSourceToTarget() {
		return this.fromSourceToTarget;
	}
	
	@Override
	public @NonNull Class<P> parameterType() {
		return this.sourceType.parameterType();
	}
	
	@Override
	public @NonNull SqlType<T> configure(@NonNull P parameter) {
		return this.sourceType.configure(parameter).map(this.javaType, this.fromTargetToSource, this.fromSourceToTarget);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MappedParameterizableSqlType<?, ?, ?> that)) return false;
		
		if (!this.sourceType.equals(that.sourceType)) return false;
		if (!this.javaType.equals(that.javaType)) return false;
		if (!this.fromTargetToSource.equals(that.fromTargetToSource)) return false;
		return this.fromSourceToTarget.equals(that.fromSourceToTarget);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.sourceType, this.javaType, this.fromTargetToSource, this.fromSourceToTarget);
	}
	
	@Override
	public @NonNull String toString() {
		return "MappedParameterizableSqlType[sourceType=" + this.sourceType + ", javaType=" + this.javaType + "]";
	}
	//endregion
}
