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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ThreadPoolStrategy}.<br>
 *
 * @author Luis-St
 */
class ThreadPoolStrategyTest {
	
	@Test
	void fixedPoolSize() {
		ThreadPoolStrategy strategy = ClientExecutorStrategy.fixedPool(8);
		
		assertEquals(8, strategy.poolSize());
		assertFalse(strategy.isCached());
		assertFalse(strategy.isCustom());
		assertTrue(strategy.ownsExecutor());
		
		ExecutorService executor = strategy.createExecutor();
		assertNotNull(executor);
		executor.shutdownNow();
	}
	
	@Test
	void cachedPool() {
		ThreadPoolStrategy strategy = ClientExecutorStrategy.cachedPool();
		
		assertEquals(0, strategy.poolSize());
		assertTrue(strategy.isCached());
		assertFalse(strategy.isCustom());
		assertTrue(strategy.ownsExecutor());
		
		ExecutorService executor = strategy.createExecutor();
		assertNotNull(executor);
		executor.shutdownNow();
	}
	
	@Test
	void customExecutor() {
		ExecutorService customExecutor = Executors.newSingleThreadExecutor();
		try {
			ThreadPoolStrategy strategy = ClientExecutorStrategy.custom(customExecutor);
			
			assertEquals(0, strategy.poolSize());
			assertFalse(strategy.isCached());
			assertTrue(strategy.isCustom());
			assertFalse(strategy.ownsExecutor());
			
			ExecutorService returned = strategy.createExecutor();
			assertSame(customExecutor, returned);
		} finally {
			customExecutor.shutdownNow();
		}
	}
	
	@Test
	void createExecutorReturnsNewInstancesForOwned() {
		ThreadPoolStrategy strategy = ClientExecutorStrategy.fixedPool(4);
		
		ExecutorService executor1 = strategy.createExecutor();
		ExecutorService executor2 = strategy.createExecutor();
		
		assertNotSame(executor1, executor2);
		
		executor1.shutdownNow();
		executor2.shutdownNow();
	}
	
	@Test
	void createExecutorReturnsSameInstanceForCustom() {
		ExecutorService customExecutor = Executors.newFixedThreadPool(2);
		try {
			ThreadPoolStrategy strategy = ClientExecutorStrategy.custom(customExecutor);
			
			ExecutorService executor1 = strategy.createExecutor();
			ExecutorService executor2 = strategy.createExecutor();
			
			assertSame(executor1, executor2);
			assertSame(customExecutor, executor1);
		} finally {
			customExecutor.shutdownNow();
		}
	}
	
	@Test
	void implementsClientExecutorStrategy() {
		ThreadPoolStrategy strategy = ClientExecutorStrategy.fixedPool(1);
		assertInstanceOf(ClientExecutorStrategy.class, strategy);
		strategy.createExecutor().shutdownNow();
	}
}
