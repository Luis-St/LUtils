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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NetworkUtils}.<br>
 *
 * @author Luis-St
 */
class NetworkUtilsTest {
	
	@Test
	void handleErrorWithNullErrorType() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, null, "message", new RuntimeException()));
	}
	
	@Test
	void handleErrorWithNullMessage() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, NetworkErrorType.IO_ERROR, null, new RuntimeException()));
	}
	
	@Test
	void handleErrorWithNullCause() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, NetworkErrorType.IO_ERROR, "message", null));
	}
	
	@Test
	void handleErrorWithNullHandler() {
		assertDoesNotThrow(() -> NetworkUtils.handleError(null, NetworkErrorType.IO_ERROR, "message", new RuntimeException()));
	}
	
	@Test
	void handleErrorInvokesHandler() {
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<String> capturedMessage = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		
		RuntimeException cause = new RuntimeException("test");
		NetworkUtils.handleError((type, msg, c) -> {
			capturedType.set(type);
			capturedMessage.set(msg);
			capturedCause.set(c);
		}, NetworkErrorType.CONNECTION_REFUSED, "Connection failed", cause);
		
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, capturedType.get());
		assertEquals("Connection failed", capturedMessage.get());
		assertSame(cause, capturedCause.get());
	}
	
	@Test
	void shutdownExecutorWithNullExecutor() {
		assertDoesNotThrow(() -> NetworkUtils.shutdownExecutor(null, true));
	}
	
	@Test
	void shutdownExecutorWhenNotOwned() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		NetworkUtils.shutdownExecutor(executor, false);
		assertFalse(executor.isShutdown());
		executor.shutdown();
	}
	
	@Test
	void shutdownExecutorWhenOwned() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		NetworkUtils.shutdownExecutor(executor, true);
		assertTrue(executor.isShutdown());
	}
	
	@Test
	void shutdownExecutorWaitsForCompletion() throws InterruptedException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		AtomicBoolean taskCompleted = new AtomicBoolean(false);
		
		executor.submit(() -> {
			try {
				Thread.sleep(100);
				taskCompleted.set(true);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		
		NetworkUtils.shutdownExecutor(executor, true);
		assertTrue(executor.isShutdown());
		assertTrue(taskCompleted.get());
	}
}
