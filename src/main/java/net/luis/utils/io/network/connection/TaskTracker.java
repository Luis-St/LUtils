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

import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Counts the handlers a server is currently running, so that a graceful stop can wait for them.<br>
 * <p>
 *     A server calls {@link #started()} before it submits a handler and {@link #finished()} once the handler returned.<br>
 *     {@link #awaitIdle(Duration)} blocks until no handler is running anymore or the given time has elapsed.
 * </p>
 *
 * @see NetworkServer#stop(Duration)
 *
 * @author Luis-St
 */
public final class TaskTracker {
	
	/**
	 * The monitor used to wait for and signal changes of the running handler count.<br>
	 */
	private final Object monitor = new Object();
	/**
	 * The number of running handlers, guarded by the monitor.<br>
	 */
	private int running;
	
	/**
	 * Constructs a new task tracker without running handlers.<br>
	 */
	public TaskTracker() {}
	
	/**
	 * Records that a handler was started.<br>
	 */
	public void started() {
		synchronized (this.monitor) {
			this.running++;
		}
	}
	
	/**
	 * Records that a handler has finished and wakes up waiting threads.<br>
	 */
	public void finished() {
		synchronized (this.monitor) {
			this.running = Math.max(0, this.running - 1);
			this.monitor.notifyAll();
		}
	}
	
	/**
	 * Returns the number of currently running handlers.<br>
	 * @return The running handler count
	 */
	public int running() {
		synchronized (this.monitor) {
			return this.running;
		}
	}
	
	/**
	 * Waits until no handler is running anymore or the given time has elapsed.<br>
	 * If the waiting thread is interrupted, the method returns immediately and the interrupt flag is restored.<br>
	 *
	 * @param timeout The maximum time to wait
	 * @return True if no handler is running anymore, false if the time has elapsed first
	 * @throws NullPointerException If the timeout is null
	 */
	public boolean awaitIdle(@NonNull Duration timeout) {
		Objects.requireNonNull(timeout, "Timeout must not be null");
		
		long deadline = System.nanoTime() + timeout.toNanos();
		synchronized (this.monitor) {
			while (this.running > 0) {
				long remaining = deadline - System.nanoTime();
				if (remaining <= 0) {
					return false;
				}
				
				try {
					TimeUnit.NANOSECONDS.timedWait(this.monitor, remaining);
				} catch (InterruptedException _) {
					Thread.currentThread().interrupt();
					return false;
				}
			}
			return true;
		}
	}
}
