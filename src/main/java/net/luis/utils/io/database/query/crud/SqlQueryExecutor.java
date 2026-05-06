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

package net.luis.utils.io.database.query.crud;

import com.google.common.collect.Lists;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.sql.*;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("SqlSourceToSinkFlow")
final class SqlQueryExecutor {
	
	private SqlQueryExecutor() {}
	
	static @NonNull PreparedStatement prepare(@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered rendered, @NonNull Duration timeout, boolean generateKeys) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		
		try {
			PreparedStatement statement = generateKeys ? connection.prepareStatement(rendered.sql(), Statement.RETURN_GENERATED_KEYS) : connection.prepareStatement(rendered.sql());
			
			List<Pair<SqlType<?>, Object>> parameters = rendered.parameters();
			for (int i = 0; i < parameters.size(); i++) {
				Pair<SqlType<?>, Object> pair = parameters.get(i);
				SqlType.setUnsafe(pair.getFirst(), dialect, statement, i + 1, pair.getSecond());
			}
			
			statement.setQueryTimeout((int) timeout.toSeconds());
			return statement;
		} catch (SQLException e) {
			throw new SqlException("Failed to prepare statement with generated keys: " + rendered.sql(), e);
		}
	}
	
	static @NonNull ResultSet executeQuery(@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered rendered, @NonNull Duration timeout) throws SqlException {
		try {
			return prepare(dialect, connection, rendered, timeout, false).executeQuery();
		} catch (SQLException e) {
			throw new SqlException("Failed to execute query: " + rendered.sql(), e);
		}
	}
	
	static int executeUpdate(@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered rendered, @NonNull Duration timeout) throws SqlException {
		try (PreparedStatement statement = prepare(dialect, connection, rendered, timeout, false)) {
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw new SqlException("Failed to execute update: " + rendered.sql(), e);
		}
	}
	
	static <T> @NonNull List<T> executeQueryAndMap(
		@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered rendered, @NonNull Duration timeout, @NonNull ThrowableFunction<ResultSet, T, SqlException> rowMapper
	) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connection, "Connection must not be null");
		Objects.requireNonNull(rendered, "Sql rendered must not be null");
		Objects.requireNonNull(timeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Sql row mapper must not be null");
		
		List<T> results = Lists.newArrayList();
		try (ResultSet resultSet = executeQuery(dialect, connection, rendered, timeout)) {
			while (resultSet.next()) {
				results.add(rowMapper.apply(resultSet));
			}
			return results;
		} catch (SqlException e) {
			throw e;
		} catch (Exception e) {
			throw new SqlException("Failed to map result set", e);
		}
	}
	
	static <T> @NonNull List<T> executeReturningQuery(
		@NonNull SqlDialect dialect, @NonNull Connection connection, @NonNull SqlRendered query, @NonNull SqlRendered returning, @NonNull Duration timeout, @NonNull ThrowableFunction<ResultSet, T, SqlException> rowMapper
	) throws SqlException {
		if (!dialect.isFeatureSupported(SqlFeature.RETURNING)) {
			throw new SqlDialectFeatureException(SqlFeature.RETURNING, dialect);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(query);
		renderer.rendered(returning);
		return executeQueryAndMap(dialect, connection, renderer.toSql(), timeout, rowMapper);
	}
}
