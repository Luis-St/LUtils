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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Strategy that creates a virtual thread per client connection.<br>
 * This is the recommended approach for modern Java applications (Java 21+).<br>
 * <p>
 *     Virtual threads are lightweight and can scale to thousands of concurrent
 *     connections without the overhead of traditional platform threads.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .executorStrategy(ClientExecutorStrategy.virtualThreads())
 *     .build();
 * }</pre>
 *
 * @see ClientExecutorStrategy
 *
 * @author Luis-St
 */
public record VirtualThreadStrategy() implements ClientExecutorStrategy {

	@Override
	public @NonNull ExecutorService createExecutor() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}

	@Override
	public boolean ownsExecutor() {
		return true;
	}
}
