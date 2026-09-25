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
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;
import java.util.Locale;

import static java.lang.foreign.ValueLayout.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link IcmpNative}.<br>
 *
 * @author Luis-St
 */
@Isolated("Tests reuse the number of a descriptor they just closed, which a concurrently running test could reopen")
class IcmpNativeTest {
	
	private static final IcmpNative.Platform LINUX = new IcmpNative.Platform(false, true, false, true, 2, 10, 2, 16, 0x40, 4, 11, 1, 13, 98, 90, 101, 113);
	private static final IcmpNative.Platform MAC = new IcmpNative.Platform(true, false, true, false, 2, 30, 4, 4, 0x80, 4, 35, 1, 13, 48, 40, 51, 65);
	private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
	private static boolean supported;
	
	@BeforeAll
	static void setUp() {
		boolean knownOs = OS_NAME.contains("linux") || OS_NAME.contains("mac");
		supported = knownOs && System.getProperty("os.arch", "").contains("64");
	}
	
	private static int openUdp(@NonNull Arena arena, int family) {
		IcmpNative.Result result = IcmpNative.socket(arena, family, IcmpNative.Platform.SOCK_DGRAM, 0);
		assertFalse(result.failed(), "Failed to open a UDP socket");
		return (int) result.value();
	}
	
	private static int bindLoopback(@NonNull Arena arena, int fd, @NonNull IpAddress<?> loopback) {
		assertFalse(IcmpNative.bind(arena, fd, IcmpNative.encodeAddress(arena, loopback, 0, 0)).failed(), "Failed to bind to " + loopback);
		MemorySegment local = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
		assertFalse(IcmpNative.socketName(arena, fd, local).failed(), "Failed to read the local address");
		return IcmpNative.decodePort(local);
	}
	
	private static int closedDescriptor(@NonNull Arena arena) {
		int fd = openUdp(arena, IcmpNative.platform().afInet());
		IcmpNative.close(fd);
		return fd;
	}
	
	@Test
	void constructResult() {
		IcmpNative.Result result = new IcmpNative.Result(5, 0);
		assertEquals(5, result.value());
		assertEquals(0, result.errno());
	}
	
	@Test
	void constructPlatform() {
		assertFalse(LINUX.bsdSockaddr());
		assertTrue(LINUX.longNfds());
		assertFalse(LINUX.ipv4HeaderOnDatagram());
		assertTrue(LINUX.kernelIdentifier());
		assertEquals(2, LINUX.afInet());
		assertEquals(10, LINUX.afInet6());
		assertEquals(2, LINUX.ipTtl());
		assertEquals(16, LINUX.ipv6UnicastHops());
		assertEquals(0x40, LINUX.msgDontWait());
		assertEquals(4, LINUX.eintr());
		assertEquals(11, LINUX.eagain());
		assertEquals(1, LINUX.eperm());
		assertEquals(13, LINUX.eacces());
		assertEquals(98, LINUX.eaddrinuse());
		assertEquals(90, LINUX.emsgsize());
		assertEquals(101, LINUX.enetunreach());
		assertEquals(113, LINUX.ehostunreach());
	}
	
	@Test
	void socketWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.socket(null, 2, 2, 0));
	}
	
	@Test
	void setIntOptionWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.setIntOption(null, 0, 0, 2, 64));
	}
	
	@Test
	void setIntOptionWithNegativeFd() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.setIntOption(arena, -1, 0, 2, 64));
		}
	}
	
	@Test
	void bindWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.bind(null, 0, MemorySegment.NULL));
	}
	
	@Test
	void bindWithNullAddress() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.bind(arena, 0, null));
		}
	}
	
	@Test
	void bindWithNegativeFd() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.bind(arena, -1, MemorySegment.NULL));
		}
	}
	
	@Test
	void socketNameWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.socketName(null, 0, MemorySegment.NULL));
	}
	
	@Test
	void socketNameWithNullAddress() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.socketName(arena, 0, null));
		}
	}
	
	@Test
	void socketNameWithNegativeFd() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.socketName(arena, -1, MemorySegment.NULL));
		}
	}
	
	@Test
	void sendToWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.sendTo(null, 0, MemorySegment.NULL, MemorySegment.NULL));
	}
	
	@Test
	void sendToWithNullData() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.sendTo(arena, 0, null, MemorySegment.NULL));
		}
	}
	
	@Test
	void sendToWithNullAddress() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.sendTo(arena, 0, MemorySegment.NULL, null));
		}
	}
	
	@Test
	void sendToWithNegativeFd() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.sendTo(arena, -1, MemorySegment.NULL, MemorySegment.NULL));
		}
	}
	
	@Test
	void receiveFromWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.receiveFrom(null, 0, MemorySegment.NULL, MemorySegment.NULL));
	}
	
	@Test
	void receiveFromWithNullBuffer() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.receiveFrom(arena, 0, null, MemorySegment.NULL));
		}
	}
	
	@Test
	void receiveFromWithNullAddress() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.receiveFrom(arena, 0, MemorySegment.NULL, null));
		}
	}
	
	@Test
	void receiveFromWithNegativeFd() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.receiveFrom(arena, -1, MemorySegment.NULL, MemorySegment.NULL));
		}
	}
	
	@Test
	void pollWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.poll(null, 0, IcmpNative.POLLIN, 0));
	}
	
	@Test
	void pollWithNegativeFd() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.poll(arena, -1, IcmpNative.POLLIN, 0));
		}
	}
	
	@Test
	void pollWithTimeoutBelowMinusOne() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.poll(arena, 0, IcmpNative.POLLIN, -2));
		}
	}
	
	@Test
	void closeWithNegativeFd() {
		assertThrows(IllegalArgumentException.class, () -> IcmpNative.close(-1));
	}
	
	@Test
	void encodeAddressWithNullArena() {
		assertThrows(NullPointerException.class, () -> IcmpNative.encodeAddress(null, Ipv4Address.LOOPBACK, 0, 0));
	}
	
	@Test
	void encodeAddressWithNullAddress() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(NullPointerException.class, () -> IcmpNative.encodeAddress(arena, null, 0, 0));
		}
	}
	
	@Test
	void encodeAddressWithNegativePort() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, -1, 0));
		}
	}
	
	@Test
	void encodeAddressWithPortAboveMax() {
		try (Arena arena = Arena.ofConfined()) {
			assertThrows(IllegalArgumentException.class, () -> IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 65536, 0));
		}
	}
	
	@Test
	void decodeAddressWithNullSegment() {
		assertThrows(NullPointerException.class, () -> IcmpNative.decodeAddress(null));
	}
	
	@Test
	void decodePortWithNullSegment() {
		assertThrows(NullPointerException.class, () -> IcmpNative.decodePort(null));
	}
	
	@Test
	void platformOnSupportedSystem() {
		assumeTrue(supported);
		IcmpNative.Platform platform = IcmpNative.platform();
		assertNotNull(platform);
		assertSame(platform, IcmpNative.platform());
	}
	
	@Test
	void platformOnLinux() {
		assumeTrue(supported && OS_NAME.contains("linux"));
		IcmpNative.Platform platform = IcmpNative.platform();
		assertFalse(platform.bsdSockaddr());
		assertTrue(platform.longNfds());
		assertTrue(platform.kernelIdentifier());
		assertEquals(2, platform.afInet());
		assertEquals(10, platform.afInet6());
		assertEquals(11, platform.eagain());
	}
	
	@Test
	void platformOnMac() {
		assumeTrue(supported && OS_NAME.contains("mac"));
		IcmpNative.Platform platform = IcmpNative.platform();
		assertTrue(platform.bsdSockaddr());
		assertFalse(platform.longNfds());
		assertTrue(platform.ipv4HeaderOnDatagram());
		assertEquals(30, platform.afInet6());
	}
	
	@Test
	void socketWithInvalidDomainFails() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			IcmpNative.Result result = IcmpNative.socket(arena, -1, IcmpNative.Platform.SOCK_DGRAM, 0);
			assertTrue(result.failed());
			assertNotEquals(0, result.errno());
		}
	}
	
	@Test
	void socketWithUdpSucceeds() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			IcmpNative.Result result = IcmpNative.socket(arena, IcmpNative.platform().afInet(), IcmpNative.Platform.SOCK_DGRAM, 0);
			try {
				assertFalse(result.failed());
				assertTrue(result.value() >= 0);
				assertEquals(0, result.errno());
			} finally {
				if (!result.failed()) {
					IcmpNative.close((int) result.value());
				}
			}
		}
	}
	
	@Test
	void setIntOptionSucceeds() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				IcmpNative.Result result = IcmpNative.setIntOption(arena, fd, IcmpNative.Platform.IPPROTO_IP, IcmpNative.platform().ipTtl(), 64);
				assertEquals(0, result.value());
				assertFalse(result.failed());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void setIntOptionWithUnknownOptionFails() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				IcmpNative.Result result = IcmpNative.setIntOption(arena, fd, IcmpNative.Platform.IPPROTO_IP, 9999, 1);
				assertTrue(result.failed());
				assertNotEquals(0, result.errno());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void bindToLoopbackSucceeds() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				assertFalse(IcmpNative.bind(arena, fd, IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 0, 0)).failed());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void bindToForeignAddressFails() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				IcmpNative.Result result = IcmpNative.bind(arena, fd, IcmpNative.encodeAddress(arena, Ipv4Address.fromOctets(192, 0, 2, 1), 0, 0));
				assertTrue(result.failed());
				assertNotEquals(0, result.errno());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void socketNameOfBoundSocket() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				assertFalse(IcmpNative.bind(arena, fd, IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 0, 0)).failed());
				MemorySegment local = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
				assertFalse(IcmpNative.socketName(arena, fd, local).failed());
				assertEquals(Ipv4Address.LOOPBACK, IcmpNative.decodeAddress(local));
				assertTrue(IcmpNative.decodePort(local) > 0);
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void socketNameWithClosedFdFails() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = closedDescriptor(arena);
			MemorySegment local = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
			assertTrue(IcmpNative.socketName(arena, fd, local).failed());
		}
	}
	
	@Test
	void sendToLoopbackSucceeds() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				int port = bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				MemorySegment data = arena.allocateFrom(JAVA_BYTE, new byte[] { 1, 2, 3, 4 });
				IcmpNative.Result result = IcmpNative.sendTo(arena, fd, data, IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, port, 0));
				assertEquals(4, result.value());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void sendToWithClosedFdFails() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = closedDescriptor(arena);
			MemorySegment data = arena.allocateFrom(JAVA_BYTE, new byte[] { 1, 2, 3, 4 });
			assertTrue(IcmpNative.sendTo(arena, fd, data, IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 9, 0)).failed());
		}
	}
	
	@Test
	void receiveFromWithoutDataFails() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				MemorySegment source = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
				IcmpNative.Result result = IcmpNative.receiveFrom(arena, fd, arena.allocate(16), source);
				assertTrue(result.failed());
				assertEquals(IcmpNative.platform().eagain(), result.errno());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void receiveFromAfterSendToReturnsData() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				int port = bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				byte[] sent = { 1, 2, 3, 4 };
				IcmpNative.sendTo(arena, fd, arena.allocateFrom(JAVA_BYTE, sent), IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, port, 0));
				
				MemorySegment buffer = arena.allocate(16);
				MemorySegment source = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
				IcmpNative.Result result = IcmpNative.receiveFrom(arena, fd, buffer, source);
				assertEquals(4, result.value());
				assertArrayEquals(sent, buffer.asSlice(0, 4).toArray(JAVA_BYTE));
				assertEquals(Ipv4Address.LOOPBACK, IcmpNative.decodeAddress(source));
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void pollWithoutDataTimesOut() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				IcmpNative.Result result = IcmpNative.poll(arena, fd, IcmpNative.POLLIN, 0);
				assertEquals(0, result.value());
				assertFalse(result.failed());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void pollWithDataIsReady() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				int port = bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				IcmpNative.sendTo(arena, fd, arena.allocateFrom(JAVA_BYTE, new byte[] { 1 }), IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, port, 0));
				assertEquals(1, IcmpNative.poll(arena, fd, IcmpNative.POLLIN, 1000).value());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void pollWithInfiniteTimeoutAndPendingData() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				int port = bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				IcmpNative.sendTo(arena, fd, arena.allocateFrom(JAVA_BYTE, new byte[] { 1 }), IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, port, 0));
				assertEquals(1, IcmpNative.poll(arena, fd, IcmpNative.POLLIN, -1).value());
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void closeCreatedSocket() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			assertDoesNotThrow(() -> IcmpNative.close(fd));
			assertTrue(IcmpNative.socketName(arena, fd, arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8)).failed());
		}
	}
	
	@Test
	void describeKnownErrno() {
		assumeTrue(supported);
		String message = IcmpNative.describe(IcmpNative.platform().eperm());
		assertTrue(message.endsWith("(errno 1)"));
		assertFalse(message.substring(0, message.length() - "(errno 1)".length()).isBlank());
	}
	
	@Test
	void describeUnknownErrno() {
		assumeTrue(supported);
		assertTrue(IcmpNative.describe(99999).endsWith("(errno 99999)"));
	}
	
	@Test
	void encodeIpv4Address() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = IcmpNative.encodeAddress(arena, Ipv4Address.fromOctets(10, 1, 2, 3), 0x1234, 7);
			assertEquals(16, segment.byteSize());
			assertEquals((byte) 0x12, segment.get(JAVA_BYTE, 2));
			assertEquals((byte) 0x34, segment.get(JAVA_BYTE, 3));
			assertArrayEquals(new byte[] { 10, 1, 2, 3 }, segment.asSlice(4, 4).toArray(JAVA_BYTE));
			assertArrayEquals(new byte[8], segment.asSlice(8, 8).toArray(JAVA_BYTE));
		}
	}
	
	@Test
	void encodeIpv6Address() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment segment = IcmpNative.encodeAddress(arena, Ipv6Address.LOOPBACK, 0, 3);
			assertEquals(28, segment.byteSize());
			assertArrayEquals(Ipv6Address.LOOPBACK.toBytes(), segment.asSlice(8, 16).toArray(JAVA_BYTE));
			assertEquals(3, segment.get(JAVA_INT, 24));
		}
	}
	
	@Test
	void encodePortBoundaries() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			assertEquals(0, IcmpNative.decodePort(IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 0, 0)));
			assertEquals(65535, IcmpNative.decodePort(IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 65535, 0)));
		}
	}
	
	@Test
	void decodeIpv4Address() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 20);
			assertEquals(address, IcmpNative.decodeAddress(IcmpNative.encodeAddress(arena, address, 0, 0)));
		}
	}
	
	@Test
	void decodeIpv6AddressWithoutScopeId() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			Ipv6Address address = Ipv6Address.fromBits(0x20010DB800000000L, 1L);
			IpAddress<?> decoded = IcmpNative.decodeAddress(IcmpNative.encodeAddress(arena, address, 0, 0));
			assertEquals(address, decoded);
			assertNull(assertInstanceOf(Ipv6Address.class, decoded).zoneId());
		}
	}
	
	@Test
	void decodeIpv6AddressWithScopeId() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			IpAddress<?> decoded = IcmpNative.decodeAddress(IcmpNative.encodeAddress(arena, Ipv6Address.LOOPBACK, 0, 5));
			assertEquals("5", assertInstanceOf(Ipv6Address.class, decoded).zoneId());
		}
	}
	
	@Test
	void decodeUnknownFamilyReturnsNull() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			assertNull(IcmpNative.decodeAddress(arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8)));
		}
	}
	
	@Test
	void resultFailedWithNegativeValue() {
		assertTrue(new IcmpNative.Result(-1, 1).failed());
	}
	
	@Test
	void resultNotFailedWithZeroOrPositive() {
		assertFalse(new IcmpNative.Result(0, 0).failed());
		assertFalse(new IcmpNative.Result(3, 0).failed());
	}
	
	@Test
	void errorTypeNetworkUnreachable() {
		assumeTrue(supported);
		IcmpNative.Platform platform = IcmpNative.platform();
		assertEquals(NetworkErrorType.NETWORK_UNREACHABLE, platform.errorType(platform.enetunreach()));
	}
	
	@Test
	void errorTypeHostUnreachable() {
		assumeTrue(supported);
		IcmpNative.Platform platform = IcmpNative.platform();
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, platform.errorType(platform.ehostunreach()));
	}
	
	@Test
	void errorTypeAddressInUse() {
		assumeTrue(supported);
		IcmpNative.Platform platform = IcmpNative.platform();
		assertEquals(NetworkErrorType.ADDRESS_IN_USE, platform.errorType(platform.eaddrinuse()));
	}
	
	@Test
	void errorTypeMessageTooLarge() {
		assumeTrue(supported);
		IcmpNative.Platform platform = IcmpNative.platform();
		assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, platform.errorType(platform.emsgsize()));
	}
	
	@Test
	void errorTypeFallsBackToIoError() {
		assumeTrue(supported);
		IcmpNative.Platform platform = IcmpNative.platform();
		assertEquals(NetworkErrorType.IO_ERROR, platform.errorType(platform.eperm()));
		assertEquals(NetworkErrorType.IO_ERROR, platform.errorType(0));
		assertEquals(NetworkErrorType.IO_ERROR, platform.errorType(-1));
	}
	
	@Test
	void platformConstants() {
		assertEquals(2, IcmpNative.Platform.SOCK_DGRAM);
		assertEquals(3, IcmpNative.Platform.SOCK_RAW);
		assertEquals(0, IcmpNative.Platform.IPPROTO_IP);
		assertEquals(1, IcmpNative.Platform.IPPROTO_ICMP);
		assertEquals(41, IcmpNative.Platform.IPPROTO_IPV6);
		assertEquals(58, IcmpNative.Platform.IPPROTO_ICMPV6);
	}
	
	@Test
	void publicConstants() {
		assertEquals(1, IcmpNative.POLLIN);
		assertEquals(128, IcmpNative.SOCKADDR_STORAGE_SIZE);
	}
	
	@Test
	void encodeWritesPlatformFamily() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment ipv4 = IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, 0, 0);
			MemorySegment ipv6 = IcmpNative.encodeAddress(arena, Ipv6Address.LOOPBACK, 0, 0);
			assertEquals(Ipv4Address.LOOPBACK, IcmpNative.decodeAddress(ipv4));
			assertEquals(Ipv6Address.LOOPBACK, IcmpNative.decodeAddress(ipv6));
			if (OS_NAME.contains("linux")) {
				assertEquals(2, ipv4.get(JAVA_SHORT, 0));
				assertEquals(10, ipv6.get(JAVA_SHORT, 0));
			}
		}
	}
	
	@Test
	void errorTypeWithMacPlatform() {
		assertEquals(NetworkErrorType.NETWORK_UNREACHABLE, MAC.errorType(51));
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, MAC.errorType(65));
		assertEquals(NetworkErrorType.ADDRESS_IN_USE, MAC.errorType(48));
		assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, MAC.errorType(40));
	}
	
	@Test
	void udpLoopbackExchange() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				int port = bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				byte[] sent = { 9, 8, 7, 6, 5 };
				IcmpNative.Result written = IcmpNative.sendTo(arena, fd, arena.allocateFrom(JAVA_BYTE, sent), IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, port, 0));
				assertEquals(1, IcmpNative.poll(arena, fd, IcmpNative.POLLIN, 1000).value());
				
				MemorySegment buffer = arena.allocate(64);
				MemorySegment source = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
				IcmpNative.Result read = IcmpNative.receiveFrom(arena, fd, buffer, source);
				assertEquals(written.value(), read.value());
				assertArrayEquals(sent, buffer.asSlice(0, read.value()).toArray(JAVA_BYTE));
				assertEquals(port, IcmpNative.decodePort(source));
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void ipv6UdpLoopbackExchange() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			IcmpNative.Result opened = IcmpNative.socket(arena, IcmpNative.platform().afInet6(), IcmpNative.Platform.SOCK_DGRAM, 0);
			assumeFalse(opened.failed(), "IPv6 is not available");
			int fd = (int) opened.value();
			try {
				assumeFalse(IcmpNative.bind(arena, fd, IcmpNative.encodeAddress(arena, Ipv6Address.LOOPBACK, 0, 0)).failed(), "::1 is not available");
				MemorySegment local = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
				IcmpNative.socketName(arena, fd, local);
				byte[] sent = { 1, 2, 3 };
				IcmpNative.sendTo(arena, fd, arena.allocateFrom(JAVA_BYTE, sent), IcmpNative.encodeAddress(arena, Ipv6Address.LOOPBACK, IcmpNative.decodePort(local), 0));
				
				MemorySegment buffer = arena.allocate(16);
				MemorySegment source = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
				assertEquals(1, IcmpNative.poll(arena, fd, IcmpNative.POLLIN, 1000).value());
				IcmpNative.Result read = IcmpNative.receiveFrom(arena, fd, buffer, source);
				assertArrayEquals(sent, buffer.asSlice(0, read.value()).toArray(JAVA_BYTE));
				assertEquals(Ipv6Address.LOOPBACK, IcmpNative.decodeAddress(source));
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
	
	@Test
	void encodeDecodeRoundTripMultipleAddresses() {
		assumeTrue(supported);
		List<IpAddress<?>> addresses = List.of(Ipv4Address.UNSPECIFIED, Ipv4Address.BROADCAST, Ipv6Address.UNSPECIFIED, Ipv6Address.MAX);
		try (Arena arena = Arena.ofConfined()) {
			for (IpAddress<?> address : addresses) {
				for (int port : new int[] { 0, 1, 65535 }) {
					MemorySegment segment = IcmpNative.encodeAddress(arena, address, port, 0);
					assertEquals(address, IcmpNative.decodeAddress(segment));
					assertEquals(port, IcmpNative.decodePort(segment));
				}
			}
		}
	}
	
	@Test
	void receiveFromTruncatesToBufferSize() {
		assumeTrue(supported);
		try (Arena arena = Arena.ofConfined()) {
			int fd = openUdp(arena, IcmpNative.platform().afInet());
			try {
				int port = bindLoopback(arena, fd, Ipv4Address.LOOPBACK);
				byte[] sent = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
				IcmpNative.sendTo(arena, fd, arena.allocateFrom(JAVA_BYTE, sent), IcmpNative.encodeAddress(arena, Ipv4Address.LOOPBACK, port, 0));
				
				MemorySegment buffer = arena.allocate(4);
				IcmpNative.Result read = IcmpNative.receiveFrom(arena, fd, buffer, arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8));
				assertEquals(4, read.value());
				assertArrayEquals(new byte[] { 1, 2, 3, 4 }, buffer.toArray(JAVA_BYTE));
			} finally {
				IcmpNative.close(fd);
			}
		}
	}
}
