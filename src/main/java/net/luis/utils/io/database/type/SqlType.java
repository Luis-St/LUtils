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
import java.time.*;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("ClassEscapesDefinedScope")
public sealed interface SqlType<T> permits SqlScalarType, ParameterizedSqlType, SqlArrayType, MappedSqlType {
	
	static <T> @NonNull SqlType<T> inferType(@NonNull T value) throws SqlTypeNotFoundException {
		return inferType(value, SqlTypeInferrer.standard());
	}
	
	static <T> @NonNull SqlType<T> inferType(@NonNull T value, @NonNull SqlTypeInferrer inferrer) throws SqlTypeNotFoundException {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(inferrer, "Sql type inferrer must not be null");
		
		return inferrer.inferType(value);
	}
	
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
	
	int jdbcType();
	
	@NonNull Class<T> javaType();
	
	default @NonNull SqlType<?> baseType() {
		if (this instanceof MappedSqlType<?, ?> mapped) {
			return mapped.sourceType().baseType();
		}
		return this;
	}
	
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
			return resultSet.getObject(columnIndex, this.javaType());
		} catch (SQLException e) {
			try {
				return this.javaType().cast(temporalFallback(resultSet, columnIndex, this.javaType()));
			} catch (UnsupportedOperationException ignored) {
				throw new SqlResultMappingException("Failed to retrieve value from result set at column index " + columnIndex, e, this.javaType(), null);
			} catch (SQLException fallback) {
				throw new SqlResultMappingException("Failed to retrieve value from result set at column index " + columnIndex, fallback, this.javaType(), null);
			}
		}
	}
	
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
