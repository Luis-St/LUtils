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

import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link IcmpPing}.<br>
 *
 * @author Luis-St
 */
class IcmpPingTest {
	
	private static final Ipv4Address UNROUTABLE = Ipv4Address.fromOctets(192, 0, 2, 1);
	private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
	private static final boolean ROOT = "root".equals(System.getProperty("user.name"));
	private static boolean supported;
	private static boolean datagramV4;
	private static boolean datagramV6;
	private static boolean defaultRoute;
	
	@BeforeAll
	static void setUp() {
		supported = (OS_NAME.contains("linux") || OS_NAME.contains("mac")) && System.getProperty("os.arch", "").contains("64");
		datagramV4 = supported && canOpen(IcmpVersion.ICMP_V4, Ipv4Address.LOOPBACK);
		datagramV6 = supported && canOpen(IcmpVersion.ICMP_V6, Ipv6Address.LOOPBACK);
		defaultRoute = hasRouteTo(UNROUTABLE);
	}
	
	private static boolean canOpen(@NonNull IcmpVersion version, @NonNull IpAddress<?> loopback) {
		try (IcmpSocket socket = new IcmpSocket(version)) {
			socket.bind(loopback);
			return true;
		} catch (NetworkConnectionException | UnsupportedOperationException e) {
			return false;
		}
	}
	
	private static boolean hasRouteTo(@NonNull Ipv4Address address) {
		try (DatagramSocket socket = new DatagramSocket()) {
			socket.connect(new InetSocketAddress(InetAddress.getByAddress(address.toBytes()), 9));
			return !socket.getLocalAddress().isAnyLocalAddress();
		} catch (IOException | UncheckedIOException e) {
			return false;
		}
	}
	
	private static boolean await(@NonNull BooleanSupplier condition, @NonNull Duration timeout) throws InterruptedException {
		long deadline = System.nanoTime() + timeout.toNanos();
		while (!condition.getAsBoolean()) {
			if (System.nanoTime() >= deadline) {
				return false;
			}
			Thread.sleep(10);
		}
		return true;
	}
	
	@Test
	void constructWithAddress() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertEquals(Ipv4Address.LOOPBACK, ping.address());
		assertEquals(IcmpPing.DEFAULT_MAX_RESULTS, ping.maxResults());
	}
	
	@Test
	void constructWithAddressConfigAndMaxResults() {
		IcmpPing ping = new IcmpPing(Ipv6Address.LOOPBACK, IcmpSocketConfig.DEFAULT, 5);
		assertEquals(Ipv6Address.LOOPBACK, ping.address());
		assertEquals(5, ping.maxResults());
	}
	
	@Test
	void constructWithHostLiteral() throws Exception {
		assertEquals(Ipv4Address.LOOPBACK, new IcmpPing("127.0.0.1").address());
	}
	
	@Test
	void constructWithHostConfigAndMaxResults() throws Exception {
		IcmpPing ping = new IcmpPing("::1", IcmpSocketConfig.DEFAULT, 10);
		assertEquals(Ipv6Address.LOOPBACK, ping.address());
		assertEquals(10, ping.maxResults());
	}
	
	@Test
	void constructWithNullHost() {
		assertThrows(NullPointerException.class, () -> new IcmpPing((String) null));
	}
	
	@Test
	void constructWithUnknownHost() {
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> new IcmpPing("host.invalid"));
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, e.errorType());
	}
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new IcmpPing((IpAddress<?>) null));
	}
	
	@Test
	void constructWithNullSocketConfig() {
		assertThrows(NullPointerException.class, () -> new IcmpPing(Ipv4Address.LOOPBACK, null, 1));
		assertThrows(NullPointerException.class, () -> new IcmpPing("127.0.0.1", null, 1));
	}
	
	@Test
	void constructWithZeroMaxResults() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpPing(Ipv4Address.LOOPBACK, IcmpSocketConfig.DEFAULT, 0));
	}
	
	@Test
	void pingWithZeroCount() {
		assertThrows(IllegalArgumentException.class, () -> new IcmpPing(Ipv4Address.LOOPBACK).ping(0));
	}
	
	@Test
	void pingWithCountAboveMaxResults() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK, IcmpSocketConfig.DEFAULT, 3);
		assertThrows(IllegalArgumentException.class, () -> ping.ping(4));
	}
	
	@Test
	void pingWithNullInterval() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(NullPointerException.class, () -> ping.ping(1, null));
		assertThrows(NullPointerException.class, () -> ping.ping(null, null));
	}
	
	@Test
	void pingWithNullTimeout() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(NullPointerException.class, () -> ping.ping(1, Duration.ZERO, null));
		assertThrows(NullPointerException.class, () -> ping.ping(Duration.ZERO, null, 0, null));
	}
	
	@Test
	void pingWithNegativeInterval() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(IllegalArgumentException.class, () -> ping.ping(1, Duration.ofMillis(-1)));
		assertThrows(IllegalArgumentException.class, () -> ping.ping(Duration.ofMillis(-1), null));
	}
	
	@Test
	void pingWithZeroTimeout() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(IllegalArgumentException.class, () -> ping.ping(1, Duration.ZERO, Duration.ZERO));
		assertThrows(IllegalArgumentException.class, () -> ping.ping(Duration.ZERO, Duration.ZERO, 0, null));
	}
	
	@Test
	void pingWithNegativeTimeout() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(IllegalArgumentException.class, () -> ping.ping(1, Duration.ZERO, Duration.ofMillis(-1)));
		assertThrows(IllegalArgumentException.class, () -> ping.ping(Duration.ZERO, Duration.ofMillis(-1), 0, null));
	}
	
	@Test
	void pingWithNegativePayloadSize() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(IllegalArgumentException.class, () -> ping.ping(1, Duration.ZERO, Duration.ofSeconds(1), -1));
		assertThrows(IllegalArgumentException.class, () -> ping.ping(Duration.ZERO, Duration.ofSeconds(1), -1, null));
	}
	
	@Test
	void pingWithPayloadSizeAboveMax() {
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		assertThrows(IllegalArgumentException.class, () -> ping.ping(1, Duration.ZERO, Duration.ofSeconds(1), 65508));
		assertThrows(IllegalArgumentException.class, () -> ping.ping(Duration.ZERO, Duration.ofSeconds(1), 65508, null));
	}
	
	@Test
	void pingWithRawSocketWithoutPrivilege() {
		assumeTrue(supported);
		assumeFalse(ROOT);
		IcmpSocketConfig config = IcmpSocketConfig.builder().socketType(IcmpSocketType.RAW).build();
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK, config, IcmpPing.DEFAULT_MAX_RESULTS);
		assertThrows(NetworkConnectionException.class, () -> ping.ping(1));
		assertThrows(NetworkConnectionException.class, () -> ping.ping().close());
	}
	
	@Test
	void constructWithMinMaxResultsBoundary() {
		assertEquals(1, new IcmpPing(Ipv4Address.LOOPBACK, IcmpSocketConfig.DEFAULT, 1).maxResults());
	}
	
	@Test
	void pingWithCountAtMaxResults() throws Exception {
		assumeTrue(datagramV4);
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK, IcmpSocketConfig.DEFAULT, 2);
		assertEquals(2, ping.ping(2, Duration.ZERO, Duration.ofSeconds(1)).size());
	}
	
	@Test
	void pingOnceReplied() throws Exception {
		assumeTrue(datagramV4);
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(1);
		assertEquals(1, results.size());
		IcmpPingResult.Replied replied = assertInstanceOf(IcmpPingResult.Replied.class, results.getFirst());
		assertEquals(1, replied.sequenceNumber());
		assertTrue(replied.roundTripTime().isPresent());
	}
	
	@Test
	void pingMultipleTimesWaitsInterval() throws Exception {
		assumeTrue(datagramV4);
		long start = System.nanoTime();
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(3, Duration.ofMillis(200));
		Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
		assertEquals(List.of(1, 2, 3), results.stream().map(IcmpPingResult::sequenceNumber).toList());
		assertTrue(elapsed.compareTo(Duration.ofMillis(400)) >= 0);
	}
	
	@Test
	void pingWithZeroIntervalAndZeroPayload() throws Exception {
		assumeTrue(datagramV4);
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(3, Duration.ZERO, Duration.ofSeconds(1), 0);
		assertEquals(3, results.size());
		assertTrue(results.stream().allMatch(IcmpPingResult.Replied.class::isInstance));
	}
	
	@Test
	void pingWithMaxPayloadSize() throws Exception {
		assumeTrue(datagramV4);
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(1, Duration.ZERO, Duration.ofSeconds(1), IcmpPing.MAX_PAYLOAD_SIZE);
		assertEquals(1, results.size());
		assertInstanceOf(IcmpPingResult.Replied.class, results.getFirst());
	}
	
	@Test
	void pingWithCallback() throws Exception {
		assumeTrue(datagramV4);
		List<IcmpPingResult> received = new ArrayList<>();
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(3, Duration.ZERO, Duration.ofSeconds(1), 8, received::add);
		assertEquals(3, results.size());
		assertEquals(results, received);
	}
	
	@Test
	void pingWithoutCallback() throws Exception {
		assumeTrue(datagramV4);
		assertEquals(2, new IcmpPing(Ipv4Address.LOOPBACK).ping(2, Duration.ZERO, Duration.ofSeconds(1), 8, null).size());
	}
	
	@Test
	void pingOnInterruptedThreadReturnsEmpty() throws Exception {
		assumeTrue(datagramV4);
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		List<IcmpPingResult> results;
		boolean interrupted;
		Thread.currentThread().interrupt();
		try {
			results = ping.ping(3);
		} finally {
			interrupted = Thread.interrupted();
		}
		assertTrue(results.isEmpty());
		assertTrue(interrupted);
	}
	
	@Test
	void pingInterruptedDuringSleepStopsEarly() throws Exception {
		assumeTrue(datagramV4);
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		AtomicReference<List<IcmpPingResult>> results = new AtomicReference<>();
		Thread thread = Thread.ofPlatform().daemon().start(() -> {
			try {
				results.set(ping.ping(5, Duration.ofSeconds(1), Duration.ofSeconds(1), 8, result -> Thread.currentThread().interrupt()));
			} catch (NetworkConnectionException e) {
				results.set(List.of());
			}
		});
		
		thread.join(2000);
		assertFalse(thread.isAlive());
		assertEquals(1, results.get().size());
	}
	
	@Test
	void pingUnroutableAddressRecordsLostOrFailed() throws Exception {
		assumeTrue(datagramV4 && defaultRoute);
		List<IcmpPingResult> results = new IcmpPing(UNROUTABLE).ping(1, Duration.ZERO, Duration.ofMillis(200));
		assertEquals(1, results.size());
		assertFalse(results.getFirst() instanceof IcmpPingResult.Replied);
		assertTrue(results.getFirst() instanceof IcmpPingResult.Lost || results.getFirst() instanceof IcmpPingResult.Failed);
	}
	
	@Test
	void endlessPingProducesResults() throws Exception {
		assumeTrue(datagramV4);
		AtomicInteger callbacks = new AtomicInteger(0);
		try (IcmpPingSession session = new IcmpPing(Ipv4Address.LOOPBACK).ping(Duration.ofMillis(50), Duration.ofSeconds(1), 8, result -> callbacks.incrementAndGet())) {
			assertTrue(await(() -> session.sent() >= 3, Duration.ofSeconds(2)));
			session.cancel();
			assertEquals(session.sent(), callbacks.get());
		}
	}
	
	@Test
	void endlessPingWithoutCallback() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpPingSession session = new IcmpPing(Ipv4Address.LOOPBACK).ping(Duration.ofMillis(50), Duration.ofSeconds(1), 8, null)) {
			assertTrue(await(() -> !session.results().isEmpty(), Duration.ofSeconds(2)));
			assertTrue(session.isRunning());
		}
	}
	
	@Test
	void endlessPingStopsOnCancel() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpPingSession session = new IcmpPing(Ipv4Address.LOOPBACK).ping(Duration.ofMillis(50), null)) {
			assertTrue(await(() -> session.sent() >= 1, Duration.ofSeconds(2)));
			session.cancel();
			assertFalse(session.isRunning());
			long sent = session.sent();
			Thread.sleep(300);
			assertEquals(sent, session.sent());
		}
	}
	
	@Test
	void defaultConstants() {
		assertEquals(100, IcmpPing.DEFAULT_MAX_RESULTS);
		assertEquals(Duration.ofSeconds(1), IcmpPing.DEFAULT_INTERVAL);
		assertEquals(Duration.ofSeconds(1), IcmpPing.DEFAULT_TIMEOUT);
		assertEquals(56, IcmpPing.DEFAULT_PAYLOAD_SIZE);
		assertEquals(65507, IcmpPing.MAX_PAYLOAD_SIZE);
	}
	
	@Test
	void pingWithDefaultsDelegation() throws Exception {
		assumeTrue(datagramV4);
		assertEquals(1, new IcmpPing(Ipv4Address.LOOPBACK).ping(1).size());
	}
	
	@Test
	void pingWithIntervalDelegation() throws Exception {
		assumeTrue(datagramV4);
		assertEquals(2, new IcmpPing(Ipv4Address.LOOPBACK).ping(2, Duration.ZERO).size());
	}
	
	@Test
	void pingWithIntervalAndTimeoutDelegation() throws Exception {
		assumeTrue(datagramV4);
		assertEquals(2, new IcmpPing(Ipv4Address.LOOPBACK).ping(2, Duration.ZERO, Duration.ofSeconds(1)).size());
	}
	
	@Test
	void pingWithPayloadSizeDelegation() throws Exception {
		assumeTrue(datagramV4);
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(1, Duration.ZERO, Duration.ofSeconds(1), 16);
		IcmpPingResult.Replied replied = assertInstanceOf(IcmpPingResult.Replied.class, results.getFirst());
		assertEquals(16, replied.reply().packet().payload().length);
	}
	
	@Test
	void endlessPingDefaultsDelegation() throws Exception {
		assumeTrue(datagramV4);
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		for (IcmpPingSession session : List.of(ping.ping(), ping.ping(result -> {}), ping.ping(Duration.ofMillis(100), null))) {
			assertTrue(session.isRunning());
			session.close();
			assertFalse(session.isRunning());
		}
	}
	
	@Test
	void pingResultListIsUnmodifiable() throws Exception {
		assumeTrue(datagramV4);
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK).ping(1);
		assertThrows(UnsupportedOperationException.class, () -> results.add(new IcmpPingResult.Lost(2)));
	}
	
	@Test
	void pingIpv6Loopback() throws Exception {
		assumeTrue(datagramV6);
		List<IcmpPingResult> results = new IcmpPing(Ipv6Address.LOOPBACK).ping(2, Duration.ZERO);
		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(IcmpPingResult.Replied.class::isInstance));
	}
	
	@Test
	void pingUsesSocketConfigTimeToLive() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocketConfig config = IcmpSocketConfig.builder().timeToLive(1).receiveTimeout(Duration.ZERO).build();
		List<IcmpPingResult> results = new IcmpPing(Ipv4Address.LOOPBACK, config, 5).ping(1, Duration.ZERO, Duration.ofSeconds(1));
		assertEquals(1, results.size());
		assertInstanceOf(IcmpPingResult.Replied.class, results.getFirst());
	}
	
	@Test
	void endlessPingKeepsNewestResults() throws Exception {
		assumeTrue(datagramV4);
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK, IcmpSocketConfig.DEFAULT, 3);
		try (IcmpPingSession session = ping.ping(Duration.ofMillis(20), null)) {
			assertTrue(await(() -> session.sent() >= 6, Duration.ofSeconds(3)));
			session.cancel();
			List<IcmpPingResult> results = session.results();
			assertEquals(3, results.size());
			assertEquals(results.get(0).sequenceNumber() + 1, results.get(1).sequenceNumber());
			assertEquals(results.get(1).sequenceNumber() + 1, results.get(2).sequenceNumber());
			assertEquals(session.sent(), results.get(2).sequenceNumber());
		}
	}
	
	@Test
	void endlessPingCallbackRunsOnPingThread() throws Exception {
		assumeTrue(datagramV4);
		List<String> threadNames = new CopyOnWriteArrayList<>();
		try (IcmpPingSession session = new IcmpPing(Ipv4Address.LOOPBACK).ping(result -> threadNames.add(Thread.currentThread().getName()))) {
			assertTrue(await(() -> !threadNames.isEmpty(), Duration.ofSeconds(2)));
			assertEquals("icmp-ping-127.0.0.1", threadNames.getFirst());
		}
	}
	
	@Test
	void endlessPingCallbackExceptionEndsSession() throws Exception {
		assumeTrue(datagramV4);
		IllegalStateException exception = new IllegalStateException("callback failed");
		try (IcmpPingSession session = new IcmpPing(Ipv4Address.LOOPBACK).ping(result -> {
			throw exception;
		})) {
			assertTrue(await(() -> !session.isRunning(), Duration.ofSeconds(2)));
			assertTrue(await(() -> !session.socket().isOpen(), Duration.ofSeconds(1)));
			assertSame(exception, session.failure().orElseThrow());
		}
	}
	
	@Test
	void pingReuseSameInstance() throws Exception {
		assumeTrue(datagramV4);
		IcmpPing ping = new IcmpPing(Ipv4Address.LOOPBACK);
		List<IcmpPingResult> first = ping.ping(2, Duration.ZERO);
		List<IcmpPingResult> second = ping.ping(2, Duration.ZERO);
		assertEquals(1, first.getFirst().sequenceNumber());
		assertEquals(1, second.getFirst().sequenceNumber());
	}
}
