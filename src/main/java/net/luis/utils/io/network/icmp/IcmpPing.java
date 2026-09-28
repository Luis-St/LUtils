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

import net.luis.utils.io.network.address.HostnameResolver;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Pings a single host like the {@code ping} tool, either a fixed number of times or endlessly until cancelled.<br>
 * <p>
 *     {@link #ping(int)} sends the given number of echo requests on the calling thread and returns one result per request.<br>
 *     {@link #ping()} pings endlessly on its own thread and returns an {@link IcmpPingSession} to read the results and cancel it.<br>
 *     The interval between requests, the timeout per reply and the payload size can be passed to the overloads,<br>
 *     everything else comes from the {@link IcmpSocketConfig} of this instance.
 * </p>
 * <p>
 *     The number of results is limited by {@link #maxResults()}.<br>
 *     A fixed ping may not send more requests than that, and an endless ping keeps only that many of the newest results.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IcmpPing ping = new IcmpPing("example.com");
 *
 * List<IcmpPingResult> results = ping.ping(3);
 * results.forEach(result -> System.out.println(result.sequenceNumber() + ": " + result.roundTripTime()));
 *
 * IcmpPingSession session = ping.ping(Duration.ofMillis(500), result -> System.out.println(result));
 * Thread.sleep(5000);
 * session.cancel();
 * }</pre>
 *
 * @see IcmpPingResult
 * @see IcmpPingSession
 *
 * @author Luis-St
 */
public final class IcmpPing {
	
	/**
	 * The default maximum number of results.<br>
	 */
	public static final int DEFAULT_MAX_RESULTS = 100;
	/**
	 * The default time between two echo requests.<br>
	 */
	public static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(1);
	/**
	 * The default time to wait for each reply.<br>
	 */
	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);
	/**
	 * The default payload size in bytes, which makes an ICMP message of 64 bytes like the ping tool sends.<br>
	 */
	public static final int DEFAULT_PAYLOAD_SIZE = 56;
	/**
	 * The largest payload an echo request over IPv4 can carry, which is the maximum IP packet size minus the IP and ICMP headers.<br>
	 */
	public static final int MAX_PAYLOAD_SIZE = 65507;
	
	/**
	 * The address this instance pings.<br>
	 */
	private final IpAddress<?> address;
	/**
	 * The configuration of the sockets opened for each ping, whose receive timeout is replaced by the timeout of the ping.<br>
	 */
	private final IcmpSocketConfig socketConfig;
	/**
	 * The maximum number of results a ping returns or keeps.<br>
	 */
	private final int maxResults;
	
	/**
	 * Constructs a new ping for the given host with the default socket configuration and result limit.<br>
	 *
	 * @param host The hostname or IP address literal to ping
	 * @throws NullPointerException If the host is null
	 * @throws NetworkConnectionException If the host cannot be resolved
	 */
	public IcmpPing(@NonNull String host) throws NetworkConnectionException {
		this(host, IcmpSocketConfig.DEFAULT, DEFAULT_MAX_RESULTS);
	}
	
	/**
	 * Constructs a new ping for the given host.<br>
	 *
	 * @param host The hostname or IP address literal to ping
	 * @param socketConfig The configuration of the sockets opened for each ping
	 * @param maxResults The maximum number of results a ping returns or keeps
	 * @throws NullPointerException If the host or socket configuration is null
	 * @throws IllegalArgumentException If the maximum number of results is less than 1
	 * @throws NetworkConnectionException If the host cannot be resolved
	 */
	public IcmpPing(@NonNull String host, @NonNull IcmpSocketConfig socketConfig, int maxResults) throws NetworkConnectionException {
		this(resolve(host), socketConfig, maxResults);
	}
	
	/**
	 * Constructs a new ping for the given address with the default socket configuration and result limit.<br>
	 *
	 * @param address The address to ping
	 * @throws NullPointerException If the address is null
	 */
	public IcmpPing(@NonNull IpAddress<?> address) {
		this(address, IcmpSocketConfig.DEFAULT, DEFAULT_MAX_RESULTS);
	}
	
	/**
	 * Constructs a new ping for the given address.<br>
	 *
	 * @param address The address to ping
	 * @param socketConfig The configuration of the sockets opened for each ping
	 * @param maxResults The maximum number of results a ping returns or keeps
	 * @throws NullPointerException If the address or socket configuration is null
	 * @throws IllegalArgumentException If the maximum number of results is less than 1
	 */
	public IcmpPing(@NonNull IpAddress<?> address, @NonNull IcmpSocketConfig socketConfig, int maxResults) {
		this.address = Objects.requireNonNull(address, "Address must not be null");
		this.socketConfig = Objects.requireNonNull(socketConfig, "Socket config must not be null");
		if (maxResults < 1) {
			throw new IllegalArgumentException("Max results must be at least 1: " + maxResults);
		}
		this.maxResults = maxResults;
	}
	
	/**
	 * Resolves a host to its first address.<br>
	 *
	 * @param host The hostname or IP address literal
	 * @return The resolved address
	 * @throws NullPointerException If the host is null
	 * @throws NetworkConnectionException If the host cannot be resolved
	 */
	private static @NonNull IpAddress<?> resolve(@NonNull String host) throws NetworkConnectionException {
		Objects.requireNonNull(host, "Host must not be null");
		
		Optional<IpAddress<?>> address = HostnameResolver.resolve(host);
		if (address.isEmpty()) {
			throw new NetworkConnectionException("Unknown host: " + host, NetworkErrorType.HOST_UNREACHABLE);
		}
		return address.get();
	}
	
	/**
	 * Validates the parameters shared by all pings.<br>
	 *
	 * @param interval The time between the start of two echo requests
	 * @param timeout The time to wait for each reply
	 * @param payloadSize The payload size of each echo request in bytes
	 * @throws NullPointerException If the interval or timeout is null
	 * @throws IllegalArgumentException If the interval is negative, the timeout is not positive or the payload size is not between 0 and 65507
	 */
	private static void validate(@NonNull Duration interval, @NonNull Duration timeout, int payloadSize) {
		Objects.requireNonNull(interval, "Interval must not be null");
		Objects.requireNonNull(timeout, "Timeout must not be null");
		
		if (interval.isNegative()) {
			throw new IllegalArgumentException("Interval must not be negative: " + interval);
		}
		if (timeout.isNegative() || timeout.isZero()) {
			throw new IllegalArgumentException("Timeout must be positive: " + timeout);
		}
		if (payloadSize < 0 || payloadSize > MAX_PAYLOAD_SIZE) {
			throw new IllegalArgumentException("Payload size must be between 0 and " + MAX_PAYLOAD_SIZE + ": " + payloadSize);
		}
	}
	
	/**
	 * Sleeps until the given {@link System#nanoTime()}.<br>
	 * An interrupt ends the sleep early and is preserved on the thread.<br>
	 *
	 * @param deadline The time to wake up at
	 * @return True if the deadline was reached, false if the thread was interrupted
	 */
	private static boolean sleepUntil(long deadline) {
		long remaining = deadline - System.nanoTime();
		if (remaining <= 0) {
			return !Thread.currentThread().isInterrupted();
		}
		
		try {
			Thread.sleep(Duration.ofNanos(remaining));
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}
	
	/**
	 * Returns the address this instance pings.<br>
	 * @return The address
	 */
	public @NonNull IpAddress<?> address() {
		return this.address;
	}
	
	/**
	 * Returns the maximum number of results a ping returns or keeps.<br>
	 * @return The result limit
	 */
	public int maxResults() {
		return this.maxResults;
	}
	
	/**
	 * Sends the given number of echo requests with the default interval, timeout and payload size.<br>
	 *
	 * @param count The number of echo requests
	 * @return One result per echo request in the order they were sent
	 * @throws IllegalArgumentException If the count is less than 1 or greater than the result limit
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(int, Duration, Duration, int, Consumer)
	 */
	public @NonNull List<IcmpPingResult> ping(int count) throws NetworkConnectionException {
		return this.ping(count, DEFAULT_INTERVAL, DEFAULT_TIMEOUT, DEFAULT_PAYLOAD_SIZE, null);
	}
	
	/**
	 * Sends the given number of echo requests with the default timeout and payload size.<br>
	 *
	 * @param count The number of echo requests
	 * @param interval The time between the start of two echo requests
	 * @return One result per echo request in the order they were sent
	 * @throws NullPointerException If the interval is null
	 * @throws IllegalArgumentException If the count is less than 1 or greater than the result limit or the interval is negative
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(int, Duration, Duration, int, Consumer)
	 */
	public @NonNull List<IcmpPingResult> ping(int count, @NonNull Duration interval) throws NetworkConnectionException {
		return this.ping(count, interval, DEFAULT_TIMEOUT, DEFAULT_PAYLOAD_SIZE, null);
	}
	
	/**
	 * Sends the given number of echo requests with the default payload size.<br>
	 *
	 * @param count The number of echo requests
	 * @param interval The time between the start of two echo requests
	 * @param timeout The time to wait for each reply
	 * @return One result per echo request in the order they were sent
	 * @throws NullPointerException If the interval or timeout is null
	 * @throws IllegalArgumentException If the count is less than 1 or greater than the result limit, the interval is negative or the timeout is not positive
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(int, Duration, Duration, int, Consumer)
	 */
	public @NonNull List<IcmpPingResult> ping(int count, @NonNull Duration interval, @NonNull Duration timeout) throws NetworkConnectionException {
		return this.ping(count, interval, timeout, DEFAULT_PAYLOAD_SIZE, null);
	}
	
	/**
	 * Sends the given number of echo requests.<br>
	 *
	 * @param count The number of echo requests
	 * @param interval The time between the start of two echo requests
	 * @param timeout The time to wait for each reply
	 * @param payloadSize The payload size of each echo request in bytes
	 * @return One result per echo request in the order they were sent
	 * @throws NullPointerException If the interval or timeout is null
	 * @throws IllegalArgumentException If the count is less than 1 or greater than the result limit, the interval is negative, the timeout is not positive or the payload size is not between 0 and 65507
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(int, Duration, Duration, int, Consumer)
	 */
	public @NonNull List<IcmpPingResult> ping(int count, @NonNull Duration interval, @NonNull Duration timeout, int payloadSize) throws NetworkConnectionException {
		return this.ping(count, interval, timeout, payloadSize, null);
	}
	
	/**
	 * Sends the given number of echo requests on the calling thread and waits for all replies.<br>
	 * <p>
	 *     A request that is not answered within the timeout or cannot be sent is recorded as a result instead of aborting the ping.<br>
	 *     Interrupting the calling thread stops the ping early and returns the results collected so far.
	 * </p>
	 *
	 * @param count The number of echo requests
	 * @param interval The time between the start of two echo requests
	 * @param timeout The time to wait for each reply
	 * @param payloadSize The payload size of each echo request in bytes
	 * @param onResult The callback receiving each result as soon as it is known, or null
	 * @return One result per echo request in the order they were sent
	 * @throws NullPointerException If the interval or timeout is null
	 * @throws IllegalArgumentException If the count is less than 1 or greater than the result limit, the interval is negative, the timeout is not positive or the payload size is not between 0 and 65507
	 * @throws NetworkConnectionException If the socket cannot be opened
	 */
	public @NonNull List<IcmpPingResult> ping(int count, @NonNull Duration interval, @NonNull Duration timeout, int payloadSize, @Nullable Consumer<IcmpPingResult> onResult) throws NetworkConnectionException {
		if (count < 1 || count > this.maxResults) {
			throw new IllegalArgumentException("Count must be between 1 and " + this.maxResults + ": " + count);
		}
		validate(interval, timeout, payloadSize);
		
		List<IcmpPingResult> results = new ArrayList<>(count);
		try (IcmpSocket socket = this.open(timeout)) {
			this.run(socket, count, interval, payloadSize, () -> Thread.currentThread().isInterrupted(), result -> {
				results.add(result);
				if (onResult != null) {
					onResult.accept(result);
				}
			});
		}
		return List.copyOf(results);
	}
	
	/**
	 * Starts an endless ping with the default interval, timeout and payload size.<br>
	 *
	 * @return The session to read the results from and to cancel the ping with
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(Duration, Duration, int, Consumer)
	 */
	public @NonNull IcmpPingSession ping() throws NetworkConnectionException {
		return this.ping(DEFAULT_INTERVAL, DEFAULT_TIMEOUT, DEFAULT_PAYLOAD_SIZE, null);
	}
	
	/**
	 * Starts an endless ping with the default interval, timeout and payload size.<br>
	 *
	 * @param onResult The callback receiving each result as soon as it is known, or null
	 * @return The session to read the results from and to cancel the ping with
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(Duration, Duration, int, Consumer)
	 */
	public @NonNull IcmpPingSession ping(@Nullable Consumer<IcmpPingResult> onResult) throws NetworkConnectionException {
		return this.ping(DEFAULT_INTERVAL, DEFAULT_TIMEOUT, DEFAULT_PAYLOAD_SIZE, onResult);
	}
	
	/**
	 * Starts an endless ping with the default timeout and payload size.<br>
	 *
	 * @param interval The time between the start of two echo requests
	 * @param onResult The callback receiving each result as soon as it is known, or null
	 * @return The session to read the results from and to cancel the ping with
	 * @throws NullPointerException If the interval is null
	 * @throws IllegalArgumentException If the interval is negative
	 * @throws NetworkConnectionException If the socket cannot be opened
	 * @see #ping(Duration, Duration, int, Consumer)
	 */
	public @NonNull IcmpPingSession ping(@NonNull Duration interval, @Nullable Consumer<IcmpPingResult> onResult) throws NetworkConnectionException {
		return this.ping(interval, DEFAULT_TIMEOUT, DEFAULT_PAYLOAD_SIZE, onResult);
	}
	
	/**
	 * Starts an endless ping on its own thread.<br>
	 * <p>
	 *     The socket is opened before this method returns, so a missing permission is reported here and not on the ping thread.<br>
	 *     The session keeps the newest {@link #maxResults()} results and drops older ones.<br>
	 *     The callback is called on the ping thread, and an exception thrown by it ends the session.
	 * </p>
	 *
	 * @param interval The time between the start of two echo requests
	 * @param timeout The time to wait for each reply
	 * @param payloadSize The payload size of each echo request in bytes
	 * @param onResult The callback receiving each result as soon as it is known, or null
	 * @return The session to read the results from and to cancel the ping with
	 * @throws NullPointerException If the interval or timeout is null
	 * @throws IllegalArgumentException If the interval is negative, the timeout is not positive or the payload size is not between 0 and 65507
	 * @throws NetworkConnectionException If the socket cannot be opened
	 */
	public @NonNull IcmpPingSession ping(@NonNull Duration interval, @NonNull Duration timeout, int payloadSize, @Nullable Consumer<IcmpPingResult> onResult) throws NetworkConnectionException {
		validate(interval, timeout, payloadSize);
		
		IcmpPingSession session = new IcmpPingSession(this.open(timeout), this.maxResults);
		session.start("icmp-ping-" + this.address, () -> this.run(session.socket(), -1, interval, payloadSize, session::isCancelled, result -> {
			session.add(result);
			if (onResult != null) {
				onResult.accept(result);
			}
		}));
		return session;
	}
	
	/**
	 * Sends echo requests until the count is reached, the ping is cancelled or the socket is closed.<br>
	 *
	 * @param socket The socket to send with
	 * @param count The number of echo requests, or a negative value to ping endlessly
	 * @param interval The time between the start of two echo requests
	 * @param payloadSize The payload size of each echo request in bytes
	 * @param cancelled The check whether the ping was cancelled
	 * @param sink The consumer receiving each result
	 * @throws NullPointerException If the socket, interval, cancellation check or sink is null
	 * @throws IllegalArgumentException If the interval is negative or the payload size is not between 0 and 65507
	 */
	private void run(@NonNull IcmpSocket socket, long count, @NonNull Duration interval, int payloadSize, @NonNull BooleanSupplier cancelled, @NonNull Consumer<IcmpPingResult> sink) {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(interval, "Interval must not be null");
		Objects.requireNonNull(cancelled, "Cancelled must not be null");
		Objects.requireNonNull(sink, "Sink must not be null");
		if (interval.isNegative()) {
			throw new IllegalArgumentException("Interval must not be negative: " + interval);
		}
		if (payloadSize < 0 || payloadSize > MAX_PAYLOAD_SIZE) {
			throw new IllegalArgumentException("Payload size must be between 0 and " + MAX_PAYLOAD_SIZE + ": " + payloadSize);
		}
		
		byte[] payload = new byte[payloadSize];
		for (int i = 0; i < payload.length; i++) {
			payload[i] = (byte) i;
		}
		
		long next = System.nanoTime();
		for (long i = 0; count < 0 || i < count; i++) {
			if (i > 0 && !sleepUntil(next)) {
				return;
			}
			if (cancelled.getAsBoolean()) {
				return;
			}
			next += interval.toNanos();
			
			int sequenceNumber = (int) ((i + 1) & 0xFFFF);
			IcmpPingResult result;
			try {
				Optional<IcmpEchoReply> reply = socket.tryPing(this.address, sequenceNumber, payload);
				result = reply.<IcmpPingResult>map(echo -> new IcmpPingResult.Replied(sequenceNumber, echo)).orElseGet(() -> new IcmpPingResult.Lost(sequenceNumber));
			} catch (NetworkConnectionException e) {
				if (!socket.isOpen()) {
					return;
				}
				result = new IcmpPingResult.Failed(sequenceNumber, Objects.requireNonNullElse(e.getMessage(), e.errorType().name()));
			}
			sink.accept(result);
		}
	}
	
	/**
	 * Opens a socket for the address of this instance whose receive timeout is the given timeout.<br>
	 *
	 * @param timeout The time to wait for each reply
	 * @return The opened socket
	 * @throws NetworkConnectionException If the socket cannot be opened
	 */
	private @NonNull IcmpSocket open(@NonNull Duration timeout) throws NetworkConnectionException {
		IcmpSocketConfig config = new IcmpSocketConfig(this.socketConfig.socketType(), timeout, this.socketConfig.bufferSize(), this.socketConfig.timeToLive(), this.socketConfig.onError());
		return new IcmpSocket(IcmpVersion.of(this.address), config);
	}
}
