/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.lang.concurrent;

import net.luis.utils.exception.StructuredConcurrencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StructuredConcurrency}.<br>
 *
 * @author Luis-St
 */
class StructuredConcurrencyTest {
	
	@Test
	void defaultConstructor() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertFalse(scope.isShutdown());
			assertEquals(0, scope.getFailureCount());
			assertTrue(scope.getSuppressedExceptions().isEmpty());
		}
	}
	
	@Test
	void constructorWithExecutor() {
		ExecutorService executor = Executors.newCachedThreadPool();
		try (StructuredConcurrency scope = new StructuredConcurrency(executor)) {
			assertFalse(scope.isShutdown());
			assertEquals(0, scope.getFailureCount());
			assertTrue(scope.getSuppressedExceptions().isEmpty());
		} finally {
			executor.shutdown();
		}
	}
	
	@Test
	void constructorWithExecutorAndOwnership() {
		ExecutorService executor = Executors.newCachedThreadPool();
		try (StructuredConcurrency scope = new StructuredConcurrency(executor, true)) {
			assertFalse(scope.isShutdown());
		}
		assertTrue(executor.isShutdown());
	}
	
	@Test
	void constructorWithNullExecutor() {
		assertThrows(NullPointerException.class, () -> new StructuredConcurrency(null));
		assertThrows(NullPointerException.class, () -> new StructuredConcurrency(null, false));
	}
	
	@Test
	void constructorWithShutdownExecutor() {
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.shutdown();
		
		assertThrows(IllegalStateException.class, () -> new StructuredConcurrency(executor));
		assertThrows(IllegalStateException.class, () -> new StructuredConcurrency(executor, false));
	}
	
	@Test
	@Timeout(5)
	void forkRunnable() {
		AtomicBoolean executed = new AtomicBoolean(false);
		
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			Future<?> future = scope.fork(() -> executed.set(true));
			scope.join();
			
			assertNull(future.get());
			assertTrue(executed.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void forkCallable() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			Future<String> future = scope.fork(() -> "Hello World");
			scope.join();
			
			assertEquals("Hello World", future.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void forkMultipleTasks() {
		AtomicInteger counter = new AtomicInteger(0);
		
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			Future<Integer> task1 = scope.fork(() -> {
				try {Thread.sleep(100);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
				return counter.incrementAndGet();
			});
			
			Future<Integer> task2 = scope.fork(() -> {
				try {Thread.sleep(100);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
				return counter.incrementAndGet();
			});
			
			scope.join();
			
			assertEquals(2, counter.get());
			assertTrue(task1.get() <= 2);
			assertTrue(task2.get() <= 2);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void forkNullRunnable() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertThrows(NullPointerException.class, () -> scope.fork((Runnable) null));
		}
	}
	
	@Test
	void forkNullCallable() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertThrows(NullPointerException.class, () -> scope.fork((Callable<String>) null));
		}
	}
	
	@Test
	void forkAfterClose() {
		StructuredConcurrency scope = new StructuredConcurrency();
		scope.close();
		
		assertThrows(IllegalStateException.class, () -> scope.fork(() -> {}));
		assertThrows(IllegalStateException.class, () -> scope.fork(() -> "test"));
	}
	
	@Test
	@Timeout(5)
	void joinWithoutTasks() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.join();
			assertEquals(0, scope.getFailureCount());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void joinWaitsForTasks() {
		AtomicInteger completedTasks = new AtomicInteger(0);
		
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				try {Thread.sleep(200);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
				completedTasks.incrementAndGet();
			});
			
			scope.fork(() -> {
				try {Thread.sleep(300);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
				completedTasks.incrementAndGet();
			});
			
			assertEquals(0, completedTasks.get());
			scope.join();
			assertEquals(2, completedTasks.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void joinWithDurationTimeout() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				try {Thread.sleep(100);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
			});
			
			scope.join(Duration.ofSeconds(1));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void joinWithTimeoutAndUnit() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				try {Thread.sleep(100);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
			});
			
			scope.join(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void joinTimeout() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				try {Thread.sleep(2000);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
			});
			
			assertThrows(StructuredConcurrencyException.class, () -> scope.join(Duration.ofMillis(100)));
		}
	}
	
	@Test
	void joinNullDuration() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertThrows(NullPointerException.class, () -> scope.join((Duration) null));
		}
	}
	
	@Test
	void joinNullTimeUnit() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertThrows(NullPointerException.class, () -> scope.join(1, null));
		}
	}
	
	@Test
	void joinMultipleCalls() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				try {Thread.sleep(100);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
			});
			
			scope.join();
			
			assertThrows(IllegalStateException.class, scope::join);
		} catch (StructuredConcurrencyException e) {
			fail("Unexpected StructuredConcurrencyException: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void taskExceptionTracking() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				throw new RuntimeException("Task 1 failed");
			});
			
			scope.fork(() -> {
				throw new IllegalStateException("Task 2 failed");
			});
			
			StructuredConcurrencyException exception = assertThrows(StructuredConcurrencyException.class, scope::join);
			
			assertEquals(2, scope.getFailureCount());
			assertEquals(2, scope.getSuppressedExceptions().size());
			assertEquals(2, exception.getSuppressed().length);
			assertTrue(exception.getMessage().contains("2 failures"));
		}
	}
	
	@Test
	@Timeout(5)
	void mixedSuccessAndFailure() {
		AtomicInteger successCount = new AtomicInteger(0);
		
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(successCount::incrementAndGet);
			scope.fork(() -> {
				throw new RuntimeException("Failed task");
			});
			scope.fork(successCount::incrementAndGet);
			
			StructuredConcurrencyException exception = assertThrows(StructuredConcurrencyException.class, scope::join);
			
			assertEquals(2, successCount.get());
			assertEquals(1, scope.getFailureCount());
			assertEquals(1, scope.getSuppressedExceptions().size());
		}
	}
	
	@Test
	void getSuppressedExceptionsUnmodifiable() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				throw new RuntimeException("Test exception");
			});
			
			assertThrows(StructuredConcurrencyException.class, scope::join);
			
			List<Throwable> exceptions = scope.getSuppressedExceptions();
			assertThrows(UnsupportedOperationException.class, () -> exceptions.add(new RuntimeException()));
		}
	}
	
	@Test
	void closeDoesNotShutdownUnownedExecutor() {
		ExecutorService executor = Executors.newCachedThreadPool();
		try {
			StructuredConcurrency scope = new StructuredConcurrency(executor, false);
			scope.close();
			
			assertFalse(executor.isShutdown());
		} finally {
			executor.shutdown();
		}
	}
	
	@Test
	void closeShutdownsOwnedExecutor() {
		ExecutorService executor = Executors.newCachedThreadPool();
		StructuredConcurrency scope = new StructuredConcurrency(executor, true);
		scope.close();
		
		assertTrue(executor.isShutdown());
	}
	
	@Test
	void closeMultipleCalls() {
		ExecutorService executor = Executors.newCachedThreadPool();
		StructuredConcurrency scope = new StructuredConcurrency(executor, true);
		
		scope.close();
		scope.close();
		
		assertTrue(executor.isShutdown());
	}
	
	@Test
	@Timeout(10)
	void closeWaitsForRunningTasks() {
		AtomicBoolean taskCompleted = new AtomicBoolean(false);
		
		StructuredConcurrency scope = new StructuredConcurrency();
		scope.fork(() -> {
			try {
				Thread.sleep(500);
				taskCompleted.set(true);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		
		scope.close();
		assertTrue(taskCompleted.get());
	}
	
	@Test
	void isShutdownReflectsExecutorState() {
		ExecutorService executor = Executors.newCachedThreadPool();
		try (StructuredConcurrency scope = new StructuredConcurrency(executor)) {
			assertFalse(scope.isShutdown());
			
			executor.shutdown();
			assertTrue(scope.isShutdown());
		}
	}
	
	@Test
	void getFailureCountInitialValue() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertEquals(0, scope.getFailureCount());
		}
	}
	
	@Test
	void getSuppressedExceptionsInitialValue() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			assertTrue(scope.getSuppressedExceptions().isEmpty());
		}
	}
	
	@Test
	@Timeout(10)
	void concurrentForking() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			AtomicInteger taskCount = new AtomicInteger(0);
			int numberOfThreads = 10;
			CountDownLatch latch = new CountDownLatch(numberOfThreads);
			
			for (int i = 0; i < numberOfThreads; i++) {
				new Thread(() -> {
					try {
						scope.fork(taskCount::incrementAndGet);
					} finally {
						latch.countDown();
					}
				}).start();
			}
			
			latch.await();
			scope.join();
			
			assertEquals(numberOfThreads, taskCount.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(10)
	void concurrentExceptionTracking() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			int numberOfFailingTasks = 5;
			
			for (int i = 0; i < numberOfFailingTasks; i++) {
				final int taskId = i;
				scope.fork(() -> {
					throw new RuntimeException("Task " + taskId + " failed");
				});
			}
			
			assertThrows(StructuredConcurrencyException.class, scope::join);
			assertEquals(numberOfFailingTasks, scope.getFailureCount());
			assertEquals(numberOfFailingTasks, scope.getSuppressedExceptions().size());
		}
	}
	
	@Test
	void interruptedTask() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			scope.fork(() -> {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Task was interrupted", e);
				}
			});
			
			StructuredConcurrencyException exception = assertThrows(StructuredConcurrencyException.class, () -> scope.join(Duration.ofMillis(100)));
			assertEquals("Timeout while waiting for tasks to complete", exception.getMessage());
			assertInstanceOf(TimeoutException.class, exception.getCause());
			assertEquals(0, scope.getFailureCount());
			
			scope.close();
		}
	}
	
	@Test
	@Timeout(5)
	void veryQuickTasks() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			AtomicInteger counter = new AtomicInteger(0);
			
			for (int i = 0; i < 100; i++) {
				scope.fork(counter::incrementAndGet);
			}
			
			scope.join();
			assertEquals(100, counter.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	@Timeout(5)
	void taskReturningNull() {
		try (StructuredConcurrency scope = new StructuredConcurrency()) {
			Future<String> future = scope.fork(() -> null);
			scope.join();
			
			assertNull(future.get());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
}

