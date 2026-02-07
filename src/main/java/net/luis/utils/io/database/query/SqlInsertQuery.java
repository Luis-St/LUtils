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

import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Interface representing a SQL insert query.<br>
 *
 * @param <T> The type of the entity
 * @author Luis-St
 */
public interface SqlInsertQuery<T> {

	@NonNull SqlInsertQuery<T> insert(@NonNull T entity);

	@SuppressWarnings("unchecked")
	@NonNull SqlInsertQuery<T> insert(T @NonNull ... entities);

	@NonNull SqlInsertQuery<T> upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict);

	@NonNull SqlInsertQuery<T> insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns);

	int execute();

	@NonNull List<T> returning();

	@NonNull List<T> fetchInserted();

	@NonNull CompletableFuture<Integer> executeAsync();
	
	@NonNull CompletableFuture<List<T>> returningAsync();
	
	@NonNull CompletableFuture<List<T>> fetchInsertedAsync();
}
