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

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link VirtualThreadStrategy}.<br>
 *
 * @author Luis-St
 */
class VirtualThreadStrategyTest {
	
	@Test
	void createExecutor() {
		VirtualThreadStrategy strategy = new VirtualThreadStrategy();
		
		ExecutorService executor = strategy.createExecutor();
		assertNotNull(executor);
		executor.shutdownNow();
	}
	
	@Test
	void ownsExecutor() {
		VirtualThreadStrategy strategy = new VirtualThreadStrategy();
		
		assertTrue(strategy.ownsExecutor());
	}
	
	@Test
	void executorRunsTasksOnVirtualThreads() throws InterruptedException {
		VirtualThreadStrategy strategy = new VirtualThreadStrategy();
		ExecutorService executor = strategy.createExecutor();
		
		try {
			CountDownLatch latch = new CountDownLatch(1);
			AtomicBoolean isVirtual = new AtomicBoolean(false);
			
			executor.submit(() -> {
				isVirtual.set(Thread.currentThread().isVirtual());
				latch.countDown();
			});
			
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertTrue(isVirtual.get());
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void implementsClientExecutorStrategy() {
		VirtualThreadStrategy strategy = new VirtualThreadStrategy();
		assertInstanceOf(ClientExecutorStrategy.class, strategy);
	}
}
