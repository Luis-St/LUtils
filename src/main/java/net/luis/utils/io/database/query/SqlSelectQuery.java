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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.SqlPage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Interface representing a SQL select query.<br>
 *
 * @param <T> The type of the result entity
 * @author Luis-St
 */
public interface SqlSelectQuery<T> {

	@NonNull SqlSelectQuery<T> where(@NonNull SqlCondition condition);

	@NonNull SqlSelectQuery<T> whereExists(@NonNull SqlSelectQuery<?> subquery);

	@NonNull SqlSelectQuery<T> whereNotExists(@NonNull SqlSelectQuery<?> subquery);

	@NonNull SqlSelectQuery<T> groupBy(SqlColumn<?> @NonNull ... columns);

	@NonNull SqlSelectQuery<T> having(@NonNull SqlCondition condition);

	@NonNull SqlSelectQuery<T> orderBy(SqlOrderable @NonNull ... orderables);

	@NonNull SqlSelectQuery<T> limit(int limit);

	@NonNull SqlSelectQuery<T> offset(long offset);

	@NonNull SqlSelectQuery<T> distinct();

	@NonNull List<T> fetch();

	@NonNull Optional<T> fetchFirst();

	@NonNull T fetchOne();

	@Nullable T fetchOneOrNull();

	long count();

	boolean exists();

	@NonNull Stream<T> stream();

	@NonNull SqlPage<T> fetchPage(int page, int pageSize);

	@NonNull SqlSelectQuery<T> forUpdate();

	@NonNull SqlSelectQuery<T> skipLocked();

	@NonNull SqlSelectQuery<T> noWait();

	@NonNull CompletableFuture<List<T>> fetchAsync();

	@NonNull CompletableFuture<Optional<T>> fetchFirstAsync();

	@NonNull CompletableFuture<T> fetchOneAsync();

	@NonNull CompletableFuture<Long> countAsync();

	@NonNull String toSql();

	@NonNull List<Object> getParameters();

	@NonNull String explain();
}
