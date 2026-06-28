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
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.infer.SqlTypeInferrer;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.util.*;

/**
 * Represents a sql data type that maps a java type to its sql representation.<br>
 * A sql type knows the {@link #jdbcType() jdbc type code} and the {@link #javaType() java type} it maps, and is responsible for binding values to prepared statements and reading values back from result sets.<br>
 *
 * @see SqlScalarType
 * @see SqlArrayType
 * @see ParameterizedSqlType
 * @see MappedSqlType
 *
 * @author Luis-St
 *
 * @param <T> The java type this sql type maps
 */
@SuppressWarnings("ClassEscapesDefinedScope")
public sealed interface SqlType<T> permits SqlScalarType, ParameterizedSqlType, SqlArrayType, MappedSqlType {
	
	/**
	 * Infers the sql type for the given value using the {@link SqlTypeInferrer#standard() standard} sql type inferrer.<br>
	 *
	 * @param value The value to infer the sql type for
	 * @return The sql type inferred for the value
	 * @throws NullPointerException If the value is null
	 * @throws SqlTypeNotFoundException If no sql type could be inferred for the value
	 * @param <T> The java type of the value
	 */
	static <T> @NonNull SqlType<T> inferType(@NonNull T value) throws SqlTypeNotFoundException {
		return inferType(value, SqlTypeInferrer.standard());
	}
	
	/**
	 * Infers the sql type for the given value using the given sql type inferrer.<br>
	 *
	 * @param value The value to infer the sql type for
	 * @param inferrer The sql type inferrer used to infer the type
	 * @return The sql type inferred for the value
	 * @throws NullPointerException If the value or inferrer is null
	 * @throws SqlTypeNotFoundException If no sql type could be inferred for the value
	 * @param <T> The java type of the value
	 */
	static <T> @NonNull SqlType<T> inferType(@NonNull T value, @NonNull SqlTypeInferrer inferrer) throws SqlTypeNotFoundException {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(inferrer, "Sql type inferrer must not be null");
		
		return inferrer.inferType(value);
	}
	
	/**
	 * Reads the value of the given sql type from the result set at the specified column index.<br>
	 * If the given dialect provides a {@link SqlDialect#readingOverride(SqlType) reading override} for the type, that override is used to read the raw value which is then cast to the {@link #javaType() java type}, otherwise the value is read using the type itself.<br>
	 *
	 * @param type The sql type of the value to read
	 * @param dialect The sql dialect used to resolve a possible reading override
	 * @param resultSet The result set to read the value from
	 * @param columnIndex The one-based column index to read the value from
	 * @return The read value or {@code null} if the column value is sql null
	 * @throws NullPointerException If the type, dialect or result set is null
	 * @throws SqlException If the value could not be read or is of an incompatible type
	 * @param <T> The java type of the value
	 */
	static <T> @Nullable T getValue(@NonNull SqlType<T> type, @NonNull SqlDialect dialect, @NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(resultSet, "Result set must not be null");
		
		Optional<SqlValueReader> override = dialect.readingOverride(type);
		if (override.isPresent()) {
			Object value;
			try {
				value = override.get().read(resultSet, columnIndex);
			} catch (SQLException e) {
				throw new SqlResultMappingException("Failed to retrieve value from result set at column index " + columnIndex, e, type.javaType(), null);
			}
			try {
				return value == null ? null : type.javaType().cast(value);
			} catch (ClassCastException e) {
				throw new SqlResultMappingException("Reading override for column index " + columnIndex + " returned a value of incompatible type " + value.getClass().getName(), e, type.javaType());
			}
		}
		return type.get(SqlTypeInternalAccess.INSTANCE, resultSet, columnIndex);
	}
	
	/**
	 * Binds the given value to the prepared statement at the specified index using the given sql type.<br>
	 * If the given dialect provides a {@link SqlDialect#bindingOverride(SqlType) binding override} for the type, that override is used to bind the value, otherwise the value is bound using the type itself.<br>
	 *
	 * @param type The sql type of the value to bind
	 * @param dialect The sql dialect used to resolve a possible binding override
	 * @param statement The prepared statement to bind the value to
	 * @param index The one-based index to bind the value to
	 * @param value The value to bind
	 * @throws NullPointerException If the type or dialect is null
	 * @throws SqlException If the value could not be bound to the prepared statement
	 * @param <T> The java type of the value
	 */
	@SuppressWarnings("unchecked")
	static <T> void setValue(@NonNull SqlType<T> type, @NonNull SqlDialect dialect, @NonNull PreparedStatement statement, int index, Object value) throws SqlException {
		Objects.requireNonNull(type, "Sql type must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		
		Optional<SqlValueBinder> override = dialect.bindingOverride(type);
		if (override.isPresent()) {
			try {
				override.get().bind(statement, index, value);
			} catch (SQLException e) {
				throw new SqlStatementBindException("Failed to bind value to prepared statement at column index " + index, e, index);
			}
		} else {
			type.set(SqlTypeInternalAccess.INSTANCE, dialect, statement, index, (T) value);
		}
	}
	
	/**
	 * Reads the value at the given column index from the result set and converts it to the given numeric java type.<br>
	 * This is used as a fallback when the jdbc driver does not support directly reading the requested numeric type.<br>
	 *
	 * @param resultSet The result set to read the value from
	 * @param columnIndex The one-based column index to read the value from
	 * @param javaType The numeric java type to convert the value to
	 * @return The converted numeric value or {@code null} if the column value is sql null
	 * @throws SQLException If the value could not be read from the result set
	 * @throws UnsupportedOperationException If the java type is not a supported numeric type or the value is not numeric
	 */
	private static @Nullable Object numericFallback(@NonNull ResultSet resultSet, int columnIndex, @NonNull Class<?> javaType) throws SQLException {
		if (!Number.class.isAssignableFrom(javaType)) {
			throw new UnsupportedOperationException("No numeric fallback for type " + javaType.getName());
		}
		
		Object raw = resultSet.getObject(columnIndex);
		if (raw == null || resultSet.wasNull()) {
			return null;
		}
		if (!(raw instanceof Number number)) {
			throw new UnsupportedOperationException("Result value at column index " + columnIndex + " is not numeric: " + raw.getClass().getName());
		}
		
		if (javaType == Integer.class) {
			return number.intValue();
		} else if (javaType == Long.class) {
			return number.longValue();
		} else if (javaType == Double.class) {
			return number.doubleValue();
		} else if (javaType == Float.class) {
			return number.floatValue();
		} else if (javaType == Short.class) {
			return number.shortValue();
		} else if (javaType == Byte.class) {
			return number.byteValue();
		}
		throw new UnsupportedOperationException("No numeric fallback for type " + javaType.getName());
	}
	
	/**
	 * Reads the value at the given column index from the result set and converts it to the given temporal java type.<br>
	 * This is used as a fallback when the jdbc driver does not support directly reading the requested temporal type.<br>
	 *
	 * @param resultSet The result set to read the value from
	 * @param columnIndex The one-based column index to read the value from
	 * @param javaType The temporal java type to convert the value to
	 * @return The converted temporal value or {@code null} if the column value is sql null
	 * @throws SQLException If the value could not be read from the result set
	 * @throws UnsupportedOperationException If the java type is not a supported temporal type
	 */
	private static @Nullable Object temporalFallback(@NonNull ResultSet resultSet, int columnIndex, @NonNull Class<?> javaType) throws SQLException {
		if (javaType == LocalDate.class) {
			Date value = resultSet.getDate(columnIndex);
			return value == null ? null : value.toLocalDate();
		} else if (javaType == LocalTime.class) {
			Time value = resultSet.getTime(columnIndex);
			return value == null ? null : value.toLocalTime();
		} else if (javaType == LocalDateTime.class) {
			Timestamp value = resultSet.getTimestamp(columnIndex);
			return value == null ? null : value.toLocalDateTime();
		} else if (javaType == OffsetDateTime.class) {
			Timestamp value = resultSet.getTimestamp(columnIndex);
			return value == null ? null : OffsetDateTime.ofInstant(value.toInstant(), ZoneOffset.UTC);
		} else if (javaType == OffsetTime.class) {
			Time value = resultSet.getTime(columnIndex);
			return value == null ? null : value.toLocalTime().atOffset(ZoneOffset.UTC);
		}
		throw new UnsupportedOperationException("No temporal fallback for type " + javaType.getName());
	}
	
	/**
	 * Returns the jdbc type code of this sql type as defined in {@link Types}.<br>
	 * @return The jdbc type code
	 */
	int jdbcType();
	
	/**
	 * Returns the java type this sql type maps.<br>
	 * @return The java type
	 */
	@NonNull Class<T> javaType();
	
	/**
	 * Returns the underlying base type of this sql type.<br>
	 * For a {@link MappedSqlType mapped} type this resolves to the base type of its source type, otherwise this type itself is returned.<br>
	 * @return The base sql type
	 */
	default @NonNull SqlType<?> baseType() {
		if (this instanceof MappedSqlType<?, ?> mapped) {
			return mapped.sourceType().baseType();
		}
		return this;
	}
	
	/**
	 * Returns an array type with this type as its element type.<br>
	 * The default implementation throws an exception, types that support being used as array elements override this method.<br>
	 * @return An array type wrapping this type as element type
	 * @throws UnsupportedOperationException If this type cannot be used as an array element type
	 */
	default @NonNull SqlArrayType<T> array() {
		throw new UnsupportedOperationException("Array types are not supported for " + this);
	}
	
	/**
	 * Reads the value of this sql type from the result set at the specified column index.<br>
	 * This method is intended for internal use only and must be called with the package-private access token, external callers should use {@link #getValue(SqlType, SqlDialect, ResultSet, int)} instead.<br>
	 *
	 * @param access The internal access token used to restrict the caller to this package
	 * @param resultSet The result set to read the value from
	 * @param columnIndex The one-based column index to read the value from
	 * @return The read value or {@code null} if the column value is sql null
	 * @throws IllegalCallerException If the access token is null
	 * @throws NullPointerException If the result set is null
	 * @throws IllegalArgumentException If the column index is less than 1
	 * @throws SqlException If the value could not be read from the result set
	 */
	@ApiStatus.Internal
	default @Nullable T get(@NonNull SqlTypeInternalAccess access, @NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		if (access == null) {
			throw new IllegalCallerException("SqlType#get should only be called from inside the net.luis.utils.io.database.type package, external callers should use SqlType#getValue");
		}
		
		Objects.requireNonNull(resultSet, "Result set must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Sql column index must be greater than or equal to 1");
		}
		
		try {
			if (this.javaType() == OffsetDateTime.class && this.jdbcType() == Types.TIMESTAMP_WITH_TIMEZONE) {
				Timestamp value = resultSet.getTimestamp(columnIndex, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				return value == null ? null : this.javaType().cast(OffsetDateTime.ofInstant(value.toInstant(), ZoneOffset.UTC));
			}
			
			if (this.javaType() == OffsetTime.class && this.jdbcType() == Types.TIME_WITH_TIMEZONE) {
				Time value = resultSet.getTime(columnIndex, Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				return value == null ? null : this.javaType().cast(value.toLocalTime().atOffset(ZoneOffset.UTC));
			}
			return resultSet.getObject(columnIndex, this.javaType());
		} catch (SQLException e) {
			try {
				return this.javaType().cast(numericFallback(resultSet, columnIndex, this.javaType()));
			} catch (UnsupportedOperationException _) {} catch (SQLException fallback) {
				throw new SqlResultMappingException("Failed to retrieve value from result set at column index " + columnIndex, fallback, this.javaType(), null);
			}
			
			try {
				return this.javaType().cast(temporalFallback(resultSet, columnIndex, this.javaType()));
			} catch (UnsupportedOperationException ignored) {
				throw new SqlResultMappingException("Failed to retrieve value from result set at column index " + columnIndex, e, this.javaType(), null);
			} catch (SQLException fallback) {
				throw new SqlResultMappingException("Failed to retrieve value from result set at column index " + columnIndex, fallback, this.javaType(), null);
			}
		}
	}
	
	/**
	 * Binds the given value of this sql type to the prepared statement at the specified column index.<br>
	 * This method is intended for internal use only and must be called with the package-private access token, external callers should use {@link #setValue(SqlType, SqlDialect, PreparedStatement, int, Object)} instead.<br>
	 *
	 * @param access The internal access token used to restrict the caller to this package
	 * @param dialect The sql dialect used to bind the value
	 * @param preparedStatement The prepared statement to bind the value to
	 * @param columnIndex The one-based column index to bind the value to
	 * @param value The value to bind or {@code null} to bind a sql null
	 * @throws IllegalCallerException If the access token is null
	 * @throws NullPointerException If the dialect or prepared statement is null
	 * @throws IllegalArgumentException If the column index is less than 1
	 * @throws SqlException If the value could not be bound to the prepared statement
	 */
	@ApiStatus.Internal
	default void set(@NonNull SqlTypeInternalAccess access, @NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, @Nullable T value) throws SqlException {
		if (access == null) {
			throw new IllegalCallerException("SqlType#set should only be called from inside the net.luis.utils.io.database.type package, external callers should use SqlType#setValue");
		}
		
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(preparedStatement, "Prepared statement must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Sql column index must be greater than or equal to 1");
		}
		
		try {
			preparedStatement.setObject(columnIndex, value, this.jdbcType());
		} catch (SQLException e) {
			throw new SqlStatementBindException("Failed to bind value to prepared statement at column index " + columnIndex, e, columnIndex);
		}
	}
	
	/**
	 * Creates a new sql type that maps this type to a different target java type.<br>
	 * The given functions are used to convert values between the target type and this type when binding and reading values.<br>
	 *
	 * @param targetType The target java type to map to
	 * @param fromTargetToSource The function converting a target value into a value of this type when binding
	 * @param fromSourceToTarget The function converting a value of this type into a target value when reading
	 * @return The mapped sql type
	 * @throws NullPointerException If the target type or either function is null
	 * @param <O> The target java type to map to
	 */
	default <O> @NonNull SqlType<O> map(
		@NonNull Class<O> targetType,
		@NonNull ThrowableFunction<@Nullable O, @Nullable T, SqlStatementBindException> fromTargetToSource,
		@NonNull ThrowableFunction<@NonNull T, @Nullable O, SqlClientException> fromSourceToTarget
	) {
		Objects.requireNonNull(targetType, "Target type must not be null");
		Objects.requireNonNull(fromTargetToSource, "Getter function must not be null");
		Objects.requireNonNull(fromSourceToTarget, "Setter function must not be null");
		
		return new MappedSqlType<>(this, targetType, fromTargetToSource, fromSourceToTarget);
	}
}
