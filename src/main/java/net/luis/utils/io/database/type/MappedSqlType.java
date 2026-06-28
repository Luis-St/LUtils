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
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * A sql type that maps the java value of an underlying source type to a different java type.<br>
 * Values are bound by converting the target value to the source value and read by converting the source value back to the target value, using the given conversion functions.<br>
 *
 * @see SqlType
 * @see MappedParameterizableSqlType
 *
 * @author Luis-St
 *
 * @param <S> The java type of the underlying source type
 * @param <T> The java type this type maps the source value to
 */
@SuppressWarnings("ClassEscapesDefinedScope")
public final class MappedSqlType<S, T> implements SqlType<T> {
	
	/**
	 * The underlying source type that performs the actual binding and reading.
	 */
	private final SqlType<S> sourceType;
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
	 * Constructs a new mapped sql type wrapping the given source type.<br>
	 *
	 * @param sourceType The underlying source type that performs the actual binding and reading
	 * @param javaType The java type this type maps the source value to
	 * @param fromTargetToSource The function converting a target value into a source value when binding a value
	 * @param fromSourceToTarget The function converting a source value into a target value when reading a value
	 * @throws NullPointerException If the source type, java type or any of the functions is null
	 */
	MappedSqlType(
		@NonNull SqlType<S> sourceType,
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
	 * Returns the underlying source type that performs the actual binding and reading.<br>
	 * @return The source type
	 */
	public @NonNull SqlType<S> sourceType() {
		return this.sourceType;
	}
	
	@Override
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
	public @NonNull SqlArrayType<T> array() {
		return new SqlArrayType<>(this);
	}
	
	@Override
	public int jdbcType() {
		return this.sourceType.jdbcType();
	}
	
	@Override
	@ApiStatus.Internal
	public @Nullable T get(@NonNull SqlTypeInternalAccess access, @NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		S source = this.sourceType.get(access, resultSet, columnIndex);
		if (source == null) {
			return null;
		}
		return this.fromSourceToTarget.apply(source);
	}
	
	@Override
	@ApiStatus.Internal
	public void set(@NonNull SqlTypeInternalAccess access, @NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, @Nullable T value) throws SqlException {
		this.sourceType.set(access, dialect, preparedStatement, columnIndex, this.fromTargetToSource.apply(value));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MappedSqlType<?, ?> that)) return false;
		return this.sourceType.equals(that.sourceType) && this.javaType.equals(that.javaType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.sourceType, this.javaType);
	}
	
	@Override
	public @NonNull String toString() {
		return "MappedSqlType[sourceType=" + this.sourceType + ", javaType=" + this.javaType + "]";
	}
	//endregion
}
