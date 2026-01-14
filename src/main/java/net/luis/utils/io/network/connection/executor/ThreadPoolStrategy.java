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
import org.jspecify.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Strategy that uses a traditional thread pool for client handling.<br>
 * This can be either a fixed-size pool, a cached pool, or a custom executor.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Fixed pool with 10 threads
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .executorStrategy(ClientExecutorStrategy.fixedPool(10))
 *     .build();
 *
 * // Cached pool
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .executorStrategy(ClientExecutorStrategy.cachedPool())
 *     .build();
 *
 * // Custom executor (not owned, not shut down by server)
 * ExecutorService myExecutor = Executors.newFixedThreadPool(20);
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .executorStrategy(ClientExecutorStrategy.custom(myExecutor))
 *     .build();
 * }</pre>
 *
 * @see ClientExecutorStrategy
 *
 * @author Luis-St
 */
public final class ThreadPoolStrategy implements ClientExecutorStrategy {
	
	private final int poolSize;
	private final boolean cached;
	private final @Nullable ExecutorService customExecutor;
	
	/**
	 * Constructs a new thread pool strategy.<br>
	 *
	 * @param poolSize The number of threads (ignored if cached or custom)
	 * @param cached Whether to use a cached thread pool
	 * @param customExecutor A custom executor, or null to create one
	 */
	ThreadPoolStrategy(int poolSize, boolean cached, @Nullable ExecutorService customExecutor) {
		this.poolSize = poolSize;
		this.cached = cached;
		this.customExecutor = customExecutor;
	}
	
	@Override
	public @NonNull ExecutorService createExecutor() {
		if (this.customExecutor != null) {
			return this.customExecutor;
		}
		if (this.cached) {
			return Executors.newCachedThreadPool();
		}
		return Executors.newFixedThreadPool(this.poolSize);
	}
	
	@Override
	public boolean ownsExecutor() {
		return this.customExecutor == null;
	}
	
	/**
	 * Returns the pool size for fixed thread pools.<br>
	 * @return The pool size, or 0 for cached/custom pools
	 */
	public int poolSize() {
		return this.poolSize;
	}
	
	/**
	 * Returns whether this is a cached thread pool strategy.<br>
	 * @return True if cached
	 */
	public boolean isCached() {
		return this.cached;
	}
	
	/**
	 * Returns whether this strategy uses a custom executor.<br>
	 * @return True if using a custom executor
	 */
	public boolean isCustom() {
		return this.customExecutor != null;
	}
}
