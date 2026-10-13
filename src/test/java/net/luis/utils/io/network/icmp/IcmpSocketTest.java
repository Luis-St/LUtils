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

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.exception.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link IcmpSocket}.<br>
 *
 * @author Luis-St
 */
class IcmpSocketTest {
	
	private static final Ipv4Address UNROUTABLE = Ipv4Address.fromOctets(192, 0, 2, 1);
	private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
	private static final boolean LINUX = OS_NAME.contains("linux");
	private static final boolean MAC = OS_NAME.contains("mac");
	private static final boolean ROOT = "root".equals(System.getProperty("user.name"));
	private static boolean supported;
	private static boolean datagramV4;
	private static boolean datagramV6;
	private static boolean defaultRoute;
	
	@BeforeAll
	static void setUp() {
		supported = (LINUX || MAC) && System.getProperty("os.arch", "").contains("64");
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
	
	private static @NonNull IcmpSocket open(@NonNull IcmpVersion version, @NonNull Duration timeout) throws NetworkConnectionException {
		return new IcmpSocket(version, IcmpSocketConfig.builder().receiveTimeout(timeout).build());
	}
	
	private static @Nullable NetworkInterface loopbackInterface() throws SocketException {
		NetworkInterface lo = NetworkInterface.getByName("lo");
		return lo != null ? lo : NetworkInterface.getByName("lo0");
	}
	
	@Test
	void constructWithVersion() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertTrue(socket.isOpen());
			assertEquals(IcmpVersion.ICMP_V4, socket.version());
		}
	}
	
	@Test
	void constructWithVersionAndConfig() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			assertTrue(socket.isOpen());
		}
	}
	
	@Test
	void constructIpv6Socket() throws Exception {
		assumeTrue(datagramV6);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6)) {
			assertEquals(IcmpVersion.ICMP_V6, socket.version());
			assertTrue(socket.isOpen());
		}
	}
	
	@Test
	void constructWithNullVersion() {
		assertThrows(NullPointerException.class, () -> new IcmpSocket(null));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new IcmpSocket(IcmpVersion.ICMP_V4, null));
	}
	
	@Test
	void constructRawSocketWithoutPrivilege() {
		assumeTrue(supported);
		assumeFalse(ROOT);
		AtomicInteger calls = new AtomicInteger(0);
		AtomicReference<Throwable> cause = new AtomicReference<>();
		IcmpSocketConfig config = IcmpSocketConfig.builder().socketType(IcmpSocketType.RAW).onError((connection, type, message, error) -> {
			calls.incrementAndGet();
			cause.set(error);
		}).build();
		
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> new IcmpSocket(IcmpVersion.ICMP_V4, config));
		assertTrue(e.getMessage().contains("CAP_NET_RAW"));
		assertEquals(1, calls.get());
		assertNotNull(cause.get());
	}
	
	@Test
	void bindWithNullAddress() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(NullPointerException.class, () -> socket.bind(null));
		}
	}
	
	@Test
	void bindWithWrongVersion() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(IllegalArgumentException.class, () -> socket.bind(Ipv6Address.LOOPBACK));
		}
	}
	
	@Test
	void bindAfterClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		socket.close();
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> socket.bind(Ipv4Address.LOOPBACK));
		assertEquals(NetworkErrorType.SOCKET_CLOSED, e.errorType());
	}
	
	@Test
	void bindToForeignAddress() throws Exception {
		assumeTrue(datagramV4);
		AtomicInteger calls = new AtomicInteger(0);
		IcmpSocketConfig config = IcmpSocketConfig.builder().onError((connection, type, message, cause) -> calls.incrementAndGet()).build();
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4, config)) {
			NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> socket.bind(UNROUTABLE));
			assertTrue(e.getMessage().startsWith("Failed to bind to 192.0.2.1"));
			assertEquals(new IpEndpoint(UNROUTABLE, 0), e.endpoint());
			assertEquals(NetworkErrorType.IO_ERROR, e.errorType());
			assertEquals(1, calls.get());
		}
	}
	
	@Test
	void sendWithNullPacket() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(NullPointerException.class, () -> socket.send(null));
		}
	}
	
	@Test
	void sendWithWrongVersion() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK, 1, 1, new byte[0]);
			assertThrows(IllegalArgumentException.class, () -> socket.send(packet));
		}
	}
	
	@Test
	void sendAfterClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		socket.close();
		IcmpPacket packet = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, 1, 1, new byte[0]);
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> socket.send(packet));
		assertEquals(NetworkErrorType.SOCKET_CLOSED, e.errorType());
	}
	
	@Test
	void receiveWithMaxBytesBelowHeaderSize() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(IllegalArgumentException.class, () -> socket.receive(7));
		}
	}
	
	@Test
	void receiveAfterClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		socket.close();
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, socket::receive);
		assertEquals(NetworkErrorType.SOCKET_CLOSED, e.errorType());
	}
	
	@Test
	void receiveTimesOut() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofMillis(200))) {
			NetworkTimeoutException e = assertThrows(NetworkTimeoutException.class, socket::receive);
			assertEquals(Duration.ofMillis(200), e.timeout());
			assertEquals(NetworkErrorType.READ_TIMEOUT, e.errorType());
		}
	}
	
	@Test
	void identifierAfterClose() throws Exception {
		assumeTrue(datagramV4 && LINUX);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		socket.close();
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, socket::identifier);
		assertEquals(NetworkErrorType.SOCKET_CLOSED, e.errorType());
	}
	
	@Test
	void pingWithNullDestination() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(NullPointerException.class, () -> socket.ping(null, 1, new byte[0]));
		}
	}
	
	@Test
	void pingWithNullPayload() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(NullPointerException.class, () -> socket.ping(Ipv4Address.LOOPBACK, 1, null));
		}
	}
	
	@Test
	void pingWithSequenceNumberOutOfRange() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(IllegalArgumentException.class, () -> socket.ping(Ipv4Address.LOOPBACK, -1, new byte[0]));
			assertThrows(IllegalArgumentException.class, () -> socket.ping(Ipv4Address.LOOPBACK, 65536, new byte[0]));
		}
	}
	
	@Test
	void pingWithWrongVersion() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertThrows(IllegalArgumentException.class, () -> socket.ping(Ipv6Address.LOOPBACK, 1, new byte[0]));
		}
	}
	
	@Test
	void pingAfterClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		socket.close();
		NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> socket.ping(Ipv4Address.LOOPBACK, 1, new byte[0]));
		assertEquals(NetworkErrorType.SOCKET_CLOSED, e.errorType());
	}
	
	@Test
	void sendWithUnknownZoneId() throws Exception {
		assumeTrue(datagramV6);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6)) {
			IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK.withZoneId("nosuchif0"), 1, 1, new byte[0]);
			NetworkConnectionException e = assertThrows(NetworkConnectionException.class, () -> socket.send(packet));
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, e.errorType());
			assertTrue(e.getMessage().contains("nosuchif0"));
		}
	}
	
	@Test
	void constructWithTimeToLive() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocketConfig config = IcmpSocketConfig.builder().receiveTimeout(Duration.ofSeconds(1)).timeToLive(1).build();
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4, config)) {
			assertTrue(socket.isOpen());
			assertTrue(socket.ping(Ipv4Address.LOOPBACK, 1, new byte[0]).packet().isEchoReply());
		}
	}
	
	@Test
	void constructIpv6WithHopLimit() throws Exception {
		assumeTrue(datagramV6);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6, IcmpSocketConfig.builder().timeToLive(64).build())) {
			assertTrue(socket.isOpen());
		}
	}
	
	@Test
	void isOpenAfterClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		assertTrue(socket.isOpen());
		socket.close();
		assertFalse(socket.isOpen());
	}
	
	@Test
	void closeTwice() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		socket.close();
		assertDoesNotThrow(socket::close);
		assertFalse(socket.isOpen());
	}
	
	@Test
	void identifierOfUnboundDatagramSocket() throws Exception {
		assumeTrue(datagramV4 && LINUX);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertEquals(0, socket.identifier());
		}
	}
	
	@Test
	void identifierAfterBind() throws Exception {
		assumeTrue(datagramV4 && LINUX);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			socket.bind(Ipv4Address.LOOPBACK);
			int identifier = socket.identifier();
			assertTrue(identifier >= 0 && identifier <= 65535);
			assertEquals(identifier, socket.identifier());
		}
	}
	
	@Test
	void identifierOnMac() throws Exception {
		assumeTrue(datagramV4 && MAC);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		int identifier = socket.identifier();
		assertTrue(identifier >= 0 && identifier <= 65535);
		assertEquals(identifier, socket.identifier());
		socket.close();
		assertEquals(identifier, assertDoesNotThrow(socket::identifier));
	}
	
	@Test
	void bindToLoopback() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertDoesNotThrow(() -> socket.bind(Ipv4Address.LOOPBACK));
		}
	}
	
	@Test
	void sendEchoRequestToLoopback() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			IcmpPacket request = IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, socket.identifier(), 1, new byte[] { 1, 2, 3, 4 });
			assertDoesNotThrow(() -> socket.send(request));
			IcmpPacket reply = socket.receive();
			assertTrue(reply.isEchoReply());
			assertEquals(Ipv4Address.LOOPBACK, reply.address());
		}
	}
	
	@Test
	void sendIpv6EchoRequestToLoopback() throws Exception {
		assumeTrue(datagramV6);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V6, Duration.ofSeconds(1))) {
			socket.send(IcmpPacket.echoRequest(Ipv6Address.LOOPBACK, socket.identifier(), 1, new byte[] { 1, 2, 3, 4 }));
			IcmpPacket reply = socket.receive();
			assertTrue(reply.isEchoReply());
			assertEquals(Ipv6Address.LOOPBACK, reply.address());
		}
	}
	
	@Test
	void sendWithNumericZoneId() throws Exception {
		assumeTrue(datagramV6);
		NetworkInterface lo = loopbackInterface();
		assumeTrue(lo != null);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6)) {
			IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK.withZoneId(String.valueOf(lo.getIndex())), 1, 1, new byte[0]);
			assertDoesNotThrow(() -> socket.send(packet));
		}
	}
	
	@Test
	void sendWithNamedZoneId() throws Exception {
		assumeTrue(datagramV6);
		NetworkInterface lo = loopbackInterface();
		assumeTrue(lo != null);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6)) {
			IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK.withZoneId(lo.getName()), 1, 1, new byte[0]);
			assertDoesNotThrow(() -> socket.send(packet));
		}
	}
	
	@Test
	void sendWithoutZoneId() throws Exception {
		assumeTrue(datagramV6);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6)) {
			IcmpPacket packet = IcmpPacket.echoRequest(Ipv6Address.LOOPBACK, 1, 1, new byte[0]);
			assertDoesNotThrow(() -> socket.send(packet));
		}
	}
	
	@Test
	void receiveWithInfiniteTimeoutWokenByClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4);
		AtomicReference<Throwable> error = new AtomicReference<>();
		Thread thread = Thread.ofPlatform().daemon().start(() -> {
			try {
				socket.receive();
			} catch (Throwable t) {
				error.set(t);
			}
		});
		
		Thread.sleep(200);
		socket.close();
		thread.join(1000);
		assertFalse(thread.isAlive());
		assertEquals(NetworkErrorType.SOCKET_CLOSED, assertInstanceOf(NetworkConnectionException.class, error.get()).errorType());
	}
	
	@Test
	void receiveWithTimeoutReturnsPendingPacket() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			socket.send(IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, socket.identifier(), 3, new byte[0]));
			IcmpPacket reply = assertDoesNotThrow(() -> socket.receive());
			assertTrue(reply.isEchoReply());
			assertEquals(3, reply.sequenceNumber());
		}
	}
	
	@Test
	void receiveTruncatesToMaxBytes() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			socket.send(IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, socket.identifier(), 1, new byte[32]));
			IcmpPacket reply = socket.receive(IcmpPacket.HEADER_SIZE);
			assertTrue(reply.isEchoReply());
			assertEquals(0, reply.payload().length);
		}
	}
	
	@Test
	void pingLoopback() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			IcmpEchoReply reply = socket.ping(Ipv4Address.LOOPBACK, 1, "ping".getBytes());
			assertTrue(reply.packet().isEchoReply());
			assertEquals(1, reply.packet().sequenceNumber());
			assertEquals(socket.identifier(), reply.packet().identifier());
			assertArrayEquals("ping".getBytes(), reply.packet().payload());
			assertFalse(reply.roundTripTime().isNegative());
		}
	}
	
	@Test
	void tryPingDiscardsNonMatchingReplies() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			byte[] payload = { 1, 2, 3, 4 };
			socket.send(IcmpPacket.echoRequest(Ipv4Address.LOOPBACK, socket.identifier(), 1, payload));
			Optional<IcmpEchoReply> reply = socket.tryPing(Ipv4Address.LOOPBACK, 2, payload);
			assertTrue(reply.isPresent());
			assertEquals(2, reply.get().packet().sequenceNumber());
		}
	}
	
	@Test
	void tryPingReturnsEmptyOnTimeout() throws Exception {
		assumeTrue(datagramV4 && defaultRoute);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofMillis(200))) {
			assertEquals(Optional.empty(), socket.tryPing(UNROUTABLE, 1, new byte[0]));
		}
	}
	
	@Test
	void pingThrowsOnTimeout() throws Exception {
		assumeTrue(datagramV4 && defaultRoute);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofMillis(200))) {
			assertThrows(NetworkTimeoutException.class, () -> socket.ping(UNROUTABLE, 1, new byte[0]));
		}
	}
	
	@Test
	void versionReturnsConstructedVersion() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4)) {
			assertEquals(IcmpVersion.ICMP_V4, socket.version());
		}
		assumeTrue(datagramV6);
		try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V6)) {
			assertEquals(IcmpVersion.ICMP_V6, socket.version());
		}
	}
	
	@Test
	void pingWithEmptyPayload() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			assertEquals(0, socket.ping(Ipv4Address.LOOPBACK, 1, new byte[0]).packet().payload().length);
		}
	}
	
	@Test
	void pingWithOddLengthPayload() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			assertArrayEquals(new byte[] { 1, 2, 3 }, socket.ping(Ipv4Address.LOOPBACK, 1, new byte[] { 1, 2, 3 }).packet().payload());
		}
	}
	
	@Test
	void pingWithMaxSequenceNumber() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			assertEquals(65535, socket.ping(Ipv4Address.LOOPBACK, 65535, new byte[0]).packet().sequenceNumber());
		}
	}
	
	@Test
	void pingMultipleTimesOnOneSocket() throws Exception {
		assumeTrue(datagramV4);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			Set<Integer> identifiers = new HashSet<>();
			for (int sequenceNumber = 1; sequenceNumber <= 5; sequenceNumber++) {
				IcmpEchoReply reply = socket.ping(Ipv4Address.LOOPBACK, sequenceNumber, new byte[] { (byte) sequenceNumber });
				assertEquals(sequenceNumber, reply.packet().sequenceNumber());
				identifiers.add(reply.packet().identifier());
			}
			assertEquals(Set.of(socket.identifier()), identifiers);
		}
	}
	
	@Test
	void pingWithLargePayload() throws Exception {
		assumeTrue(datagramV4);
		byte[] payload = new byte[1400];
		Arrays.fill(payload, (byte) 0xFF);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1))) {
			assertArrayEquals(payload, socket.ping(Ipv4Address.LOOPBACK, 1, payload).packet().payload());
		}
	}
	
	@Test
	void pingIpv6Loopback() throws Exception {
		assumeTrue(datagramV6);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V6, Duration.ofSeconds(1))) {
			IcmpEchoReply reply = socket.ping(Ipv6Address.LOOPBACK, 1, new byte[] { 1, 2 });
			assertEquals(129, reply.packet().type());
			assertEquals(Ipv6Address.LOOPBACK, reply.packet().address());
		}
	}
	
	@Test
	void concurrentPingAndClose() throws Exception {
		assumeTrue(datagramV4);
		IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofSeconds(1));
		AtomicReference<Throwable> error = new AtomicReference<>();
		Thread thread = Thread.ofPlatform().daemon().start(() -> {
			try {
				for (int i = 0; ; i = (i + 1) & 0xFFFF) {
					socket.ping(Ipv4Address.LOOPBACK, i, new byte[8]);
				}
			} catch (Throwable t) {
				error.set(t);
			}
		});
		
		Thread.sleep(100);
		socket.close();
		thread.join(2000);
		assertFalse(thread.isAlive());
		assertEquals(NetworkErrorType.SOCKET_CLOSED, assertInstanceOf(NetworkConnectionException.class, error.get()).errorType());
		assertFalse(socket.isOpen());
	}
	
	@Test
	void receiveTimeoutLimitsWholeExchange() throws Exception {
		assumeTrue(datagramV4 && defaultRoute);
		try (IcmpSocket socket = open(IcmpVersion.ICMP_V4, Duration.ofMillis(300))) {
			long start = System.nanoTime();
			socket.tryPing(UNROUTABLE, 1, new byte[0]);
			Duration elapsed = Duration.ofNanos(System.nanoTime() - start);
			assertTrue(elapsed.compareTo(Duration.ofMillis(300)) >= 0);
			assertTrue(elapsed.compareTo(Duration.ofSeconds(1)) < 0);
		}
	}
}
