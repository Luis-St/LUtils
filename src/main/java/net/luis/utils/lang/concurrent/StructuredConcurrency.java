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

package net.luis.utils.lang.concurrent;

import com.google.common.collect.Lists;
import net.luis.utils.exception.StructuredConcurrencyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copied from GitHub.<br>
 * <br>
 * A structured concurrency implementation that allows forking tasks and joining on their completion.<br>
 * This class provides a way to manage a group of related concurrent tasks with proper lifecycle management.<br>
 *
 * <p>Usage example:
 * <pre>{@code
 * try (StructuredConcurrency scope = new StructuredConcurrency()) {
 *     Future<String> task1 = scope.fork(() -> "Hello");
 *     Future<Integer> task2 = scope.fork(() -> 42);
 *
 *     scope.join(); // Wait for all tasks to complete
 *
 *     String result1 = task1.get();
 *     Integer result2 = task2.get();
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public class StructuredConcurrency implements AutoCloseable {
	
	/**
	 * The logger for this class.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(StructuredConcurrency.class);
	
	/**
	 * The executor service used to run tasks.<br>
	 * This can be a custom executor or the {@link ForkJoinPool#commonPool()}.<br>
	 */
	private final ExecutorService executor;
	/**
	 * Whether this instance owns the executor service.<br>
	 * If true, the executor will be shut down when this instance is closed.<br>
	 */
	private final boolean ownsExecutor;
	/**
	 * A phaser used to synchronize task completion.<br>
	 * This allows waiting for all forked tasks to finish before closing the scope.<br>
	 */
	private final Phaser phaser = new Phaser(1);
	/**
	 * The count of tasks that have failed.<br>
	 */
	private final AtomicInteger failureCount = new AtomicInteger(0);
	/**
	 * A list of exceptions thrown by tasks.<br>
	 */
	private final List<Throwable> suppressedExceptions = Collections.synchronizedList(Lists.newArrayList());
	/**
	 * The current state of this structured concurrency scope.<br>
	 */
	private final AtomicReference<State> state = new AtomicReference<>(State.OPEN);
	
	/**
	 * Creates a new StructuredConcurrency instance using the common ForkJoinPool.
	 */
	public StructuredConcurrency() {
		this(ForkJoinPool.commonPool(), false);
	}
	
	/**
	 * Creates a new StructuredConcurrency instance with the specified executor.
	 * The executor will not be shut down when this instance is closed.
	 *
	 * @param executor The executor service to use for task execution
	 * @throws NullPointerException If executor is null
	 * @throws IllegalStateException If the executor is already shut down
	 */
	public StructuredConcurrency(@NonNull ExecutorService executor) {
		this(executor, false);
	}
	
	/**
	 * Creates a new StructuredConcurrency instance with the specified executor.
	 *
	 * @param executor The executor service to use for task execution
	 * @param ownsExecutor Whether this instance should shut down the executor when closed
	 * @throws NullPointerException If executor is null
	 * @throws IllegalStateException If the executor is already shut down
	 */
	public StructuredConcurrency(@NonNull ExecutorService executor, boolean ownsExecutor) {
		this.executor = Objects.requireNonNull(executor, "ExecutorService must not be null");
		this.ownsExecutor = ownsExecutor;
		
		if (executor.isShutdown() || executor.isTerminated()) {
			throw new IllegalStateException("Executor service is already shutdown");
		}
	}
	
	/**
	 * Checks if the underlying executor is shut down.
	 *
	 * @return True if the executor is shut down or terminated
	 */
	public boolean isShutdown() {
		return this.executor.isShutdown() || this.executor.isTerminated();
	}
	
	/**
	 * Gets the current number of failed tasks.
	 *
	 * @return The number of tasks that have thrown exceptions
	 */
	public int getFailureCount() {
		return this.failureCount.get();
	}
	
	/**
	 * Gets a copy of all suppressed exceptions from failed tasks.
	 *
	 * @return An unmodifiable list of suppressed exceptions
	 */
	public @NonNull @Unmodifiable List<Throwable> getSuppressedExceptions() {
		return List.copyOf(this.suppressedExceptions);
	}
	
	/**
	 * Forks a runnable task for execution.
	 *
	 * @param action The runnable task to execute
	 * @return A Future representing the task
	 * @throws NullPointerException If the action is null
	 * @throws IllegalStateException If this scope is not in an open state
	 */
	public @NonNull Future<?> fork(@NonNull Runnable action) {
		Objects.requireNonNull(action, "Runnable action must not be null");
		this.ensureOpenState();
		
		this.phaser.register();
		
		return this.executor.submit(() -> {
			try {
				action.run();
				return null;
			} catch (Throwable t) {
				this.handleTaskException(t);
				throw t;
			} finally {
				this.phaser.arriveAndDeregister();
			}
		});
	}
	
	/**
	 * Forks a callable task for execution.
	 *
	 * @param action The callable task to execute
	 * @param <T> The return type of the callable
	 * @return A Future representing the task result
	 * @throws NullPointerException If the action is null
	 * @throws IllegalStateException If this scope is not in an open state
	 */
	public <T> @NonNull Future<T> fork(@NonNull Callable<T> action) {
		Objects.requireNonNull(action, "Callable action must not be null");
		this.ensureOpenState();
		
		this.phaser.register();
		
		return this.executor.submit(() -> {
			try {
				return action.call();
			} catch (Throwable t) {
				this.handleTaskException(t);
				throw t;
			} finally {
				this.phaser.arriveAndDeregister();
			}
		});
	}
	
	/**
	 * Waits for all forked tasks to complete.
	 *
	 * @throws StructuredConcurrencyException If interrupted while waiting or if any tasks failed
	 * @throws IllegalStateException If this scope is already closed
	 */
	public void join() throws StructuredConcurrencyException {
		this.join(Duration.ofMillis(Long.MAX_VALUE));
	}
	
	/**
	 * Waits for all forked tasks to complete within the specified timeout.<br>
	 *
	 * @param timeout The maximum time to wait
	 * @throws StructuredConcurrencyException If interrupted, timed out, or if any tasks failed
	 * @throws IllegalStateException If this scope is already closed
	 * @throws NullPointerException If timeout is null
	 */
	public void join(@NonNull Duration timeout) throws StructuredConcurrencyException {
		Objects.requireNonNull(timeout, "Timeout must not be null");
		this.join(timeout.toMillis(), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Waits for all forked tasks to complete within the specified timeout.<br>
	 *
	 * @param timeout The maximum time to wait
	 * @param unit The time unit of the timeout
	 * @throws StructuredConcurrencyException If interrupted, timed out, or if any tasks failed
	 * @throws IllegalStateException If this scope is already closed
	 * @throws NullPointerException If the unit is null
	 */
	public void join(long timeout, @NonNull TimeUnit unit) throws StructuredConcurrencyException {
		Objects.requireNonNull(unit, "TimeUnit must not be null");
		if (!this.state.compareAndSet(State.OPEN, State.JOINING)) {
			throw new IllegalStateException("Cannot join: scope is " + this.state.get().name().toLowerCase());
		}
		
		try {
			this.phaser.awaitAdvanceInterruptibly(this.phaser.arriveAndDeregister(), timeout, unit);
			
			if (this.failureCount.get() > 0) {
				StructuredConcurrencyException exception = new StructuredConcurrencyException("One or more tasks failed (" + this.failureCount.get() + " failures)");
				this.suppressedExceptions.forEach(exception::addSuppressed);
				throw exception;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new StructuredConcurrencyException("Interrupted while waiting for tasks to complete", e);
		} catch (TimeoutException e) {
			throw new StructuredConcurrencyException("Timeout while waiting for tasks to complete", e);
		}
	}
	
	/**
	 * Closes this structured concurrency scope and optionally shuts down the executor.<br>
	 * If tasks are still running, this method will wait for them to complete.<br>
	 * If the scope is already closed, this method does nothing.
	 */
	@Override
	public void close() {
		if (!this.state.compareAndSet(State.OPEN, State.CLOSED) && !this.state.compareAndSet(State.JOINING, State.CLOSED)) {
			return;
		}
		
		if (this.phaser.getRegisteredParties() > 1) {
			try {
				this.phaser.awaitAdvanceInterruptibly(this.phaser.arriveAndDeregister(), 30, TimeUnit.SECONDS);
			} catch (InterruptedException | TimeoutException e) {
				LOGGER.warn("Interrupted or timed out while waiting for tasks to complete", e);
				Thread.currentThread().interrupt(); // Restore interrupt status
			}
		}
		
		if (this.ownsExecutor && !this.executor.isShutdown()) {
			this.executor.shutdown();
			try {
				if (!this.executor.awaitTermination(30, TimeUnit.SECONDS)) {
					this.executor.shutdownNow();
					if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
						LOGGER.warn("Executor did not terminate after shutdownNow");
					}
				}
			} catch (InterruptedException e) {
				this.executor.shutdownNow();
			}
		}
	}
	
	/**
	 * Ensures that the current state of this scope is {@link State#OPEN open}.<br>
	 * This method checks if the scope is in an open state and if the executor is not shut down.
	 *
	 * @throws IllegalStateException If the scope is not {@link State#OPEN open} or if the executor is shut down
	 */
	private void ensureOpenState() {
		State currentState = this.state.get();
		if (currentState != State.OPEN) {
			throw new IllegalStateException("Cannot fork tas: scope is " + currentState.name().toLowerCase());
		}
		
		if (this.isShutdown()) {
			throw new IllegalStateException("Cannot fork task: executor service is shut down");
		}
	}
	
	/**
	 * Handles exceptions thrown by tasks.<br>
	 * This method increments the failure count and adds the exception to the list of suppressed exceptions.<br>
	 * This allows the scope to track failures without immediately terminating the scope.
	 *
	 * @param t The exception thrown by the task
	 */
	private void handleTaskException(@NonNull Throwable t) {
		this.failureCount.incrementAndGet();
		this.suppressedExceptions.add(t);
	}
	
	/**
	 * Represents the state of the structured concurrency scope.
	 */
	private enum State {
		
		/**
		 * The scope is open and ready for tasks to be forked.
		 * This is the initial state of the scope.
		 * Tasks can be forked and joined.
		 */
		OPEN,
		
		/**
		 * The scope is currently waiting for tasks to complete.
		 * This state is entered when {@link #join()} is called.
		 */
		JOINING,
		
		/**
		 * The scope is closed and no further tasks can be forked.
		 * Once in this state, the scope cannot be joined or used to fork new tasks.
		 */
		CLOSED
	}
}
