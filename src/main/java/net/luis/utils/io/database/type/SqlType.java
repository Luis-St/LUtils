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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.type.SqlResultRetrievalException;
import net.luis.utils.io.database.exception.type.SqlStatementBindException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public sealed interface SqlType<T> permits SqlScalarType, ParameterizedSqlType, SqlArrayType, MappedSqlType {
	
	int jdbcType();
	
	@NonNull Class<T> javaType();
	
	default @Nullable T get(@NonNull ResultSet resultSet, int columnIndex) throws SqlException {
		Objects.requireNonNull(resultSet, "Result set must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Column index must be greater than or equal to 1");
		}
		
		try {
			return resultSet.getObject(columnIndex, this.javaType());
		} catch (SQLException e) {
			throw new SqlResultRetrievalException("Failed to retrieve value from result set at column index " + columnIndex, e);
		}
	}
	
	default void set(@NonNull SqlDialect dialect, @NonNull PreparedStatement preparedStatement, int columnIndex, @Nullable T value) throws SqlException {
		Objects.requireNonNull(dialect, "Dialect must not be null");
		Objects.requireNonNull(preparedStatement, "Prepared statement must not be null");
		if (columnIndex < 1) {
			throw new IllegalArgumentException("Column index must be greater than or equal to 1");
		}
		
		try {
			preparedStatement.setObject(columnIndex, value, this.jdbcType());
		} catch (SQLException e) {
			throw new SqlStatementBindException("Failed to bind value to prepared statement at column index " + columnIndex, e);
		}
	}
	
	default <O> @NonNull SqlType<O> map(
		@NonNull Class<O> targetType,
		@NonNull ThrowableFunction<@Nullable O, @Nullable T, SqlStatementBindException> fromTargetToSource,
		@NonNull ThrowableFunction<@NonNull T, @Nullable O, SqlResultRetrievalException> fromSourceToTarget
	) {
		Objects.requireNonNull(targetType, "Target type must not be null");
		Objects.requireNonNull(fromTargetToSource, "Getter function must not be null");
		Objects.requireNonNull(fromSourceToTarget, "Setter function must not be null");
		
		return new MappedSqlType<>(this, targetType, fromTargetToSource, fromSourceToTarget);
	}
}
