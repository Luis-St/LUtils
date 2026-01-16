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

package net.luis.utils.io.network.connection.executor;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Strategy for handling concurrent client connections/message processing.<br>
 * This sealed interface allows configuration of how servers handle multiple clients.<br>
 * <p>
 *     Two strategies are provided:
 * </p>
 * <ul>
 *     <li>{@link VirtualThreadStrategy} - One virtual thread per client (recommended for Java 21+)</li>
 *     <li>{@link ThreadPoolStrategy} - Fixed or cached thread pool</li>
 * </ul>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Use virtual threads (recommended)
 * ClientExecutorStrategy strategy = ClientExecutorStrategy.virtualThreads();
 *
 * // Use a fixed thread pool with 10 threads
 * ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(10);
 *
 * // Use a cached thread pool
 * ClientExecutorStrategy strategy = ClientExecutorStrategy.cachedPool();
 *
 * // Use a custom executor (not owned by the server)
 * ClientExecutorStrategy strategy = ClientExecutorStrategy.custom(myExecutor);
 * }</pre>
 *
 * @see VirtualThreadStrategy
 * @see ThreadPoolStrategy
 *
 * @author Luis-St
 */
public sealed interface ClientExecutorStrategy permits VirtualThreadStrategy, ThreadPoolStrategy {
	
	/**
	 * Creates a virtual thread per client strategy.<br>
	 * This is the recommended approach for modern Java applications (Java 21+).<br>
	 *
	 * @return A new virtual thread strategy
	 */
	static @NonNull VirtualThreadStrategy virtualThreads() {
		return new VirtualThreadStrategy();
	}
	
	/**
	 * Creates a fixed thread pool strategy with the specified number of threads.<br>
	 *
	 * @param threads The number of threads in the pool
	 * @return A new fixed thread pool strategy
	 * @throws IllegalArgumentException If threads is less than 1
	 */
	static @NonNull ThreadPoolStrategy fixedPool(int threads) {
		if (threads < 1) {
			throw new IllegalArgumentException("Thread pool size must be at least 1: " + threads);
		}
		return new ThreadPoolStrategy(threads, false, null);
	}
	
	/**
	 * Creates a cached thread pool strategy.<br>
	 * Threads are created as needed and reused when available.<br>
	 *
	 * @return A new cached thread pool strategy
	 */
	static @NonNull ThreadPoolStrategy cachedPool() {
		return new ThreadPoolStrategy(0, true, null);
	}
	
	/**
	 * Uses a custom executor service.<br>
	 * The executor is not owned by this strategy and will not be shut down when the server is closed.<br>
	 *
	 * @param executor The custom executor service to use
	 * @return A new thread pool strategy wrapping the custom executor
	 * @throws NullPointerException If executor is null
	 */
	static @NonNull ThreadPoolStrategy custom(@NonNull ExecutorService executor) {
		Objects.requireNonNull(executor, "Executor must not be null");
		return new ThreadPoolStrategy(0, false, executor);
	}
	
	/**
	 * Creates an executor service based on this strategy.<br>
	 * @return A new executor service
	 */
	@NonNull ExecutorService createExecutor();
	
	/**
	 * Returns whether this strategy owns the executor.<br>
	 * <p>
	 *     If true, the executor should be shut down when the server is closed.<br>
	 * 	   If false, the executor is managed externally and should not be shut down.
	 * </p>
	 *
	 * @return True if the executor is owned by this strategy
	 */
	boolean ownsExecutor();
}
