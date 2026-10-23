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

package net.luis.utils.io.network.icmp;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A handle to an endless ping started by {@link IcmpPing#ping()}, which runs on its own thread until it is canceled.<br>
 * <p>
 *     The session keeps the newest results in a rolling window whose size is the result limit of the {@link IcmpPing}.<br>
 *     Once the window is full, every new result drops the oldest one, so an endless ping never grows its memory.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (IcmpPingSession session = new IcmpPing("example.com").ping(result -> System.out.println(result))) {
 *     Thread.sleep(5000);
 *     System.out.println(session.sent() + " sent, last results: " + session.results());
 * }
 * }</pre>
 *
 * @see IcmpPing
 *
 * @author Luis-St
 */
public final class IcmpPingSession implements AutoCloseable {
	
	/**
	 * The results of the newest echo requests, oldest first.<br>
	 */
	private final Deque<IcmpPingResult> results = new ArrayDeque<>();
	/**
	 * The maximum number of results kept.<br>
	 */
	private final int maxResults;
	/**
	 * The socket the echo requests are sent with.<br>
	 */
	private final IcmpSocket socket;
	/**
	 * The number of echo requests sent so far, including those whose results were dropped from the window.<br>
	 */
	private final AtomicLong sent = new AtomicLong();
	/**
	 * Whether the session was cancelled.<br>
	 */
	private final AtomicBoolean cancelled = new AtomicBoolean();
	/**
	 * The thread running the pings, set once it was started.<br>
	 */
	private volatile @Nullable Thread thread;
	/**
	 * The unexpected exception that ended the session, if any.<br>
	 */
	private volatile @Nullable Throwable failure;
	
	/**
	 * Constructs a new session sending with the given socket.<br>
	 * The session takes ownership of the socket and closes it once it ends.<br>
	 *
	 * @param socket The socket to send with
	 * @param maxResults The maximum number of results kept
	 * @throws NullPointerException If the socket is null
	 * @throws IllegalArgumentException If the maximum number of results is less than 1
	 */
	IcmpPingSession(@NonNull IcmpSocket socket, int maxResults) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		if (maxResults < 1) {
			throw new IllegalArgumentException("Max results must be at least 1: " + maxResults);
		}
		this.maxResults = maxResults;
	}
	
	/**
	 * Starts the thread running the given task.<br>
	 *
	 * @param name The name of the thread
	 * @param task The task sending the pings
	 * @throws NullPointerException If the task is null
	 */
	void start(@NonNull String name, @NonNull Runnable task) {
		Objects.requireNonNull(task, "Task must not be null");
		
		this.thread = Thread.ofPlatform().name(name).daemon().start(() -> {
			try {
				task.run();
			} catch (RuntimeException | Error e) {
				this.failure = e;
			} finally {
				this.socket.close();
			}
		});
	}
	
	/**
	 * Returns the socket the echo requests are sent with.<br>
	 * @return The socket
	 */
	@NonNull IcmpSocket socket() {
		return this.socket;
	}
	
	/**
	 * Adds a result to the rolling window, dropping the oldest result if the window is full.<br>
	 *
	 * @param result The result to add
	 * @throws NullPointerException If the result is null
	 */
	void add(@NonNull IcmpPingResult result) {
		Objects.requireNonNull(result, "Result must not be null");
		
		this.sent.incrementAndGet();
		synchronized (this.results) {
			if (this.results.size() >= this.maxResults) {
				this.results.removeFirst();
			}
			this.results.addLast(result);
		}
	}
	
	/**
	 * Returns a snapshot of the newest results, oldest first.<br>
	 * @return An unmodifiable copy of the rolling window
	 */
	public @NonNull List<IcmpPingResult> results() {
		synchronized (this.results) {
			return List.copyOf(this.results);
		}
	}
	
	/**
	 * Returns the number of echo requests sent so far.<br>
	 * This includes requests whose results were already dropped from the rolling window.<br>
	 *
	 * @return The total number of echo requests
	 */
	public long sent() {
		return this.sent.get();
	}
	
	/**
	 * Checks whether the session is still sending echo requests.<br>
	 * @return True if the session was neither cancelled nor ended by a failure
	 */
	public boolean isRunning() {
		Thread current = this.thread;
		return current != null && current.isAlive() && !this.cancelled.get();
	}
	
	/**
	 * Checks whether the session was cancelled.<br>
	 * @return True if {@link #cancel()} was called
	 */
	public boolean isCancelled() {
		return this.cancelled.get();
	}
	
	/**
	 * Returns the unexpected exception that ended the session, for example one thrown by the result callback.<br>
	 * @return The exception, or an empty optional if the session did not fail
	 */
	public @NonNull Optional<Throwable> failure() {
		return Optional.ofNullable(this.failure);
	}
	
	/**
	 * Stops the session and waits until its thread has finished.<br>
	 * <p>
	 *     A pending wait for a reply is aborted and its request is not recorded.<br>
	 *     If the calling thread is interrupted while waiting, the interrupt is preserved and the session still stops shortly after.
	 * </p>
	 */
	public void cancel() {
		if (!this.cancelled.compareAndSet(false, true)) {
			return;
		}
		
		this.socket.close();
		Thread current = this.thread;
		if (current == null || current == Thread.currentThread()) {
			return;
		}
		
		current.interrupt();
		try {
			current.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	public void close() {
		this.cancel();
	}
}
