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

import net.luis.utils.io.database.SqlRenderable;
import net.luis.utils.io.database.dialect.SqlDialect;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface representing a SQL insert query.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlInsertQuery<T> extends SqlRenderable {
	
	/**
	 * Executes the insert query.<br>
	 * @return The number of rows inserted
	 */
	int execute();
	
	/**
	 * Executes the insert query and returns the inserted entities.<br>
	 * Generates SQL: {@code INSERT INTO ... RETURNING *}.<br>
	 *
	 * @return The list of inserted entities
	 */
	@NonNull List<T> returning();
	
	/**
	 * Executes the insert query and fetches the inserted entities.<br>
	 * @return The list of inserted entities
	 */
	@NonNull List<T> fetchInserted();
	
	/**
	 * Asynchronously executes the insert query.<br>
	 * @return A future containing the number of rows inserted
	 */
	@NonNull CompletableFuture<Integer> executeAsync();
	
	/**
	 * Asynchronously executes the insert query and returns the inserted entities.<br>
	 * Generates SQL: {@code INSERT INTO ... RETURNING *}.<br>
	 *
	 * @return A future containing the list of inserted entities
	 */
	@NonNull CompletableFuture<List<T>> returningAsync();
	
	/**
	 * Asynchronously executes the insert query and fetches the inserted entities.<br>
	 * @return A future containing the list of inserted entities
	 */
	@NonNull CompletableFuture<List<T>> fetchInsertedAsync();
	
	@Override
	@NonNull String toSql(@NonNull SqlDialect<?, ?> dialect);
}
