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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test class for {@link ClientExecutorStrategy}.<br>
 *
 * @author Luis-St
 */
class ClientExecutorStrategyTest {

	@Test
	void virtualThreads() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.virtualThreads();

		assertNotNull(strategy);
		assertInstanceOf(VirtualThreadStrategy.class, strategy);
		assertTrue(strategy.ownsExecutor());

		ExecutorService executor = strategy.createExecutor();
		assertNotNull(executor);
		executor.shutdownNow();
	}

	@Test
	void fixedPool() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.fixedPool(10);

		assertNotNull(strategy);
		assertInstanceOf(ThreadPoolStrategy.class, strategy);
		assertTrue(strategy.ownsExecutor());

		ThreadPoolStrategy threadPool = (ThreadPoolStrategy) strategy;
		assertEquals(10, threadPool.poolSize());
		assertFalse(threadPool.isCached());
		assertFalse(threadPool.isCustom());

		ExecutorService executor = strategy.createExecutor();
		assertNotNull(executor);
		executor.shutdownNow();
	}

	@Test
	void fixedPoolWithInvalidSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> ClientExecutorStrategy.fixedPool(0));
		assertThrows(IllegalArgumentException.class, () -> ClientExecutorStrategy.fixedPool(-1));
		assertThrows(IllegalArgumentException.class, () -> ClientExecutorStrategy.fixedPool(-10));
	}

	@Test
	void cachedPool() {
		ClientExecutorStrategy strategy = ClientExecutorStrategy.cachedPool();

		assertNotNull(strategy);
		assertInstanceOf(ThreadPoolStrategy.class, strategy);
		assertTrue(strategy.ownsExecutor());

		ThreadPoolStrategy threadPool = (ThreadPoolStrategy) strategy;
		assertEquals(0, threadPool.poolSize());
		assertTrue(threadPool.isCached());
		assertFalse(threadPool.isCustom());

		ExecutorService executor = strategy.createExecutor();
		assertNotNull(executor);
		executor.shutdownNow();
	}

	@Test
	void customExecutor() {
		ExecutorService customExecutor = Executors.newFixedThreadPool(5);
		try {
			ClientExecutorStrategy strategy = ClientExecutorStrategy.custom(customExecutor);

			assertNotNull(strategy);
			assertInstanceOf(ThreadPoolStrategy.class, strategy);
			assertFalse(strategy.ownsExecutor());

			ThreadPoolStrategy threadPool = (ThreadPoolStrategy) strategy;
			assertTrue(threadPool.isCustom());

			ExecutorService returned = strategy.createExecutor();
			assertSame(customExecutor, returned);
		} finally {
			customExecutor.shutdownNow();
		}
	}

	@Test
	void customExecutorWithNullThrows() {
		assertThrows(NullPointerException.class, () -> ClientExecutorStrategy.custom(null));
	}
}
