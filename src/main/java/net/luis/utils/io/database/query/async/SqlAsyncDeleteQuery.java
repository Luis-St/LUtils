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

package net.luis.utils.io.database.query.async;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Interface representing an asynchronous SQL delete query.<br>
 * <p>
 *     All terminal operations return {@link CompletableFuture} for non-blocking execution.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlAsyncDeleteQuery<T> {

	/**
	 * Sets the condition for the delete query.<br>
	 * Generates SQL: {@code WHERE condition}.<br>
	 *
	 * @param condition The condition for the delete
	 * @return This delete query for method chaining
	 */
	@NonNull SqlAsyncDeleteQuery<T> where(@NonNull SqlCondition condition);

	/**
	 * Asynchronously executes the delete query.<br>
	 * Generates SQL: {@code DELETE FROM table WHERE ...}.<br>
	 *
	 * @return A future that completes with the number of rows deleted
	 */
	@NonNull CompletableFuture<Integer> execute();
}
