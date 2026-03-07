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

package net.luis.utils.io.databasev1;

import net.luis.utils.io.databasev1.exception.SqlException;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Interface for SQL retry operations.<br>
 *
 * @author Luis-St
 */
public interface SqlRetry {
	
	/**
	 * Creates a new retry instance with exponential backoff.<br>
	 *
	 * @param maxRetries The maximum number of retries
	 * @param initialDelay The initial delay between retries
	 * @return The new retry instance
	 */
	static @NonNull SqlRetry withBackoff(int maxRetries, @NonNull Duration initialDelay) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Configures the exception types to retry on.<br>
	 *
	 * @param exceptions The exception types that should trigger a retry
	 * @return This retry instance
	 */
	@SuppressWarnings("unchecked")
	@NonNull SqlRetry retryOn(Class<? extends Throwable> @NonNull ... exceptions);
	
	/**
	 * Executes the given value-returning action with retry logic.<br>
	 *
	 * @param action The action to execute
	 * @param <T> The return type of the action
	 * @return The result of the action
	 * @throws SqlException If all retries are exhausted
	 */
	<T> T execute(@NonNull Supplier<T> action) throws SqlException;
	
	/**
	 * Executes the given void action with retry logic.<br>
	 *
	 * @param action The action to execute
	 * @throws SqlException If all retries are exhausted
	 */
	void run(@NonNull SqlAction action) throws SqlException;
	
	/**
	 * A void action that may throw a {@link SqlException}.<br>
	 */
	@FunctionalInterface
	interface SqlAction {
		
		void run() throws SqlException;
	}
}
