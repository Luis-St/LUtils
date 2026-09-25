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
import org.jspecify.annotations.Nullable;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.*;

/**
 * Bindings to the POSIX socket functions of the C library through the Foreign Function and Memory API.<br>
 * <p>
 *     Only 64-bit Linux and macOS are supported.<br>
 *     The constants and the socket address layouts differ between the two, so they are described by a {@link Platform}.<br>
 *     Every call captures {@code errno} directly after the native function returns,<br>
 *     because the Java runtime may overwrite it before a later call could read it.
 * </p>
 *
 * @author Luis-St
 */
final class IcmpNative {
	
	/**
	 * The size of an IPv4 socket address in bytes.<br>
	 */
	private static final long SOCKADDR_IN_SIZE = 16;
	/**
	 * The size of an IPv6 socket address in bytes.<br>
	 */
	private static final long SOCKADDR_IN6_SIZE = 28;
	/**
	 * The layout of a single poll descriptor, which is the same on every supported platform.<br>
	 */
	private static final StructLayout POLLFD = MemoryLayout.structLayout(JAVA_INT.withName("fd"), JAVA_SHORT.withName("events"), JAVA_SHORT.withName("revents"));
	/**
	 * The layout of the state captured after each native call.<br>
	 */
	private static final StructLayout CAPTURE_LAYOUT = Linker.Option.captureStateLayout();
	/**
	 * The handle reading {@code errno} from the captured state.<br>
	 */
	private static final VarHandle ERRNO = CAPTURE_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("errno"));
	/**
	 * The platform this runtime is running on, or null if it is not supported.<br>
	 */
	private static final @Nullable Platform PLATFORM = Platform.detect();
	/**
	 * The poll event signalling that data is available to read.<br>
	 */
	static final short POLLIN = 0x0001;
	/**
	 * The size of the generic socket address storage in bytes.<br>
	 */
	static final long SOCKADDR_STORAGE_SIZE = 128;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private IcmpNative() {}
	
	/**
	 * Returns the platform this runtime is running on.<br>
	 *
	 * @return The platform
	 * @throws UnsupportedOperationException If the operating system or architecture is not supported
	 */
	static @NonNull Platform platform() {
		if (PLATFORM == null) {
			throw new UnsupportedOperationException("ICMP sockets are only supported on 64 bit Linux and macOS, not on " + System.getProperty("os.name"));
		}
		return PLATFORM;
	}
	
	/**
	 * Creates a new socket.<br>
	 *
	 * @param arena The arena used for the captured state
	 * @param domain The address family
	 * @param type The socket type
	 * @param protocol The protocol number
	 * @return The file descriptor or a negative value on failure
	 * @throws NullPointerException If the arena is null
	 */
	static @NonNull Result socket(@NonNull Arena arena, int domain, int type, int protocol) {
		Objects.requireNonNull(arena, "Arena must not be null");
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		int result = (int) invoke(Functions.SOCKET, state, domain, type, protocol);
		return Result.of(result, state);
	}
	
	/**
	 * Sets an integer socket option.<br>
	 *
	 * @param arena The arena used for the option value and the captured state
	 * @param fd The file descriptor of the socket
	 * @param level The protocol level of the option
	 * @param name The option name
	 * @param value The option value
	 * @return Zero or a negative value on failure
	 * @throws NullPointerException If the arena is null
	 * @throws IllegalArgumentException If the file descriptor is negative
	 */
	static @NonNull Result setIntOption(@NonNull Arena arena, int fd, int level, int name, int value) {
		Objects.requireNonNull(arena, "Arena must not be null");
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		MemorySegment option = arena.allocateFrom(JAVA_INT, value);
		int result = (int) invoke(Functions.SETSOCKOPT, state, fd, level, name, option, (int) JAVA_INT.byteSize());
		return Result.of(result, state);
	}
	
	/**
	 * Binds a socket to a local address.<br>
	 *
	 * @param arena The arena used for the captured state
	 * @param fd The file descriptor of the socket
	 * @param address The encoded socket address
	 * @return Zero or a negative value on failure
	 * @throws NullPointerException If the arena or address is null
	 * @throws IllegalArgumentException If the file descriptor is negative
	 */
	static @NonNull Result bind(@NonNull Arena arena, int fd, @NonNull MemorySegment address) {
		Objects.requireNonNull(arena, "Arena must not be null");
		Objects.requireNonNull(address, "Address must not be null");
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		int result = (int) invoke(Functions.BIND, state, fd, address, (int) address.byteSize());
		return Result.of(result, state);
	}
	
	/**
	 * Reads the local address a socket is bound to.<br>
	 *
	 * @param arena The arena used for the length and the captured state
	 * @param fd The file descriptor of the socket
	 * @param address The segment receiving the socket address
	 * @return Zero or a negative value on failure
	 * @throws NullPointerException If the arena or address is null
	 * @throws IllegalArgumentException If the file descriptor is negative
	 */
	static @NonNull Result socketName(@NonNull Arena arena, int fd, @NonNull MemorySegment address) {
		Objects.requireNonNull(arena, "Arena must not be null");
		Objects.requireNonNull(address, "Address must not be null");
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		MemorySegment length = arena.allocateFrom(JAVA_INT, (int) address.byteSize());
		int result = (int) invoke(Functions.GETSOCKNAME, state, fd, address, length);
		return Result.of(result, state);
	}
	
	/**
	 * Sends a datagram to the given address.<br>
	 *
	 * @param arena The arena used for the captured state
	 * @param fd The file descriptor of the socket
	 * @param data The data to send
	 * @param address The encoded destination address
	 * @return The number of bytes sent or a negative value on failure
	 * @throws NullPointerException If the arena, data or address is null
	 * @throws IllegalArgumentException If the file descriptor is negative
	 */
	static @NonNull Result sendTo(@NonNull Arena arena, int fd, @NonNull MemorySegment data, @NonNull MemorySegment address) {
		Objects.requireNonNull(arena, "Arena must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(address, "Address must not be null");
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		long result = (long) invoke(Functions.SENDTO, state, fd, data, data.byteSize(), 0, address, (int) address.byteSize());
		return Result.of(result, state);
	}
	
	/**
	 * Receives a datagram without blocking.<br>
	 *
	 * @param arena The arena used for the length and the captured state
	 * @param fd The file descriptor of the socket
	 * @param buffer The buffer receiving the data
	 * @param address The segment receiving the source address
	 * @return The number of bytes received or a negative value on failure
	 * @throws NullPointerException If the arena, buffer or address is null
	 * @throws IllegalArgumentException If the file descriptor is negative
	 */
	static @NonNull Result receiveFrom(@NonNull Arena arena, int fd, @NonNull MemorySegment buffer, @NonNull MemorySegment address) {
		Objects.requireNonNull(arena, "Arena must not be null");
		Objects.requireNonNull(buffer, "Buffer must not be null");
		Objects.requireNonNull(address, "Address must not be null");
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		MemorySegment length = arena.allocateFrom(JAVA_INT, (int) address.byteSize());
		long result = (long) invoke(Functions.RECVFROM, state, fd, buffer, buffer.byteSize(), platform().msgDontWait(), address, length);
		return Result.of(result, state);
	}
	
	/**
	 * Waits until a socket is ready for the given events.<br>
	 *
	 * @param arena The arena used for the poll descriptor and the captured state
	 * @param fd The file descriptor of the socket
	 * @param events The events to wait for
	 * @param timeoutMillis The maximum time to wait in milliseconds, or -1 to wait forever
	 * @return The number of ready descriptors, zero on timeout or a negative value on failure
	 * @throws NullPointerException If the arena is null
	 * @throws IllegalArgumentException If the file descriptor is negative or the timeout is less than -1
	 */
	static @NonNull Result poll(@NonNull Arena arena, int fd, short events, int timeoutMillis) {
		Objects.requireNonNull(arena, "Arena must not be null");
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		if (timeoutMillis < -1) {
			throw new IllegalArgumentException("Timeout must be at least -1: " + timeoutMillis);
		}
		
		MemorySegment state = arena.allocate(CAPTURE_LAYOUT);
		MemorySegment descriptor = arena.allocate(POLLFD);
		descriptor.set(JAVA_INT, 0, fd);
		descriptor.set(JAVA_SHORT, 4, events);
		
		int result = (int) invoke(Functions.POLL, state, descriptor, platform().longNfds() ? 1L : 1, timeoutMillis);
		return Result.of(result, state);
	}
	
	/**
	 * Closes a file descriptor.<br>
	 *
	 * @param fd The file descriptor to close
	 * @throws IllegalArgumentException If the file descriptor is negative
	 */
	static void close(int fd) {
		if (fd < 0) {
			throw new IllegalArgumentException("File descriptor must not be negative: " + fd);
		}
		
		try (Arena arena = Arena.ofConfined()) {
			invoke(Functions.CLOSE, arena.allocate(CAPTURE_LAYOUT), fd);
		}
	}
	
	/**
	 * Returns the system message describing an error number.<br>
	 *
	 * @param errno The error number
	 * @return The error message including the error number
	 */
	static @NonNull String describe(int errno) {
		MemorySegment message = (MemorySegment) invoke(Functions.STRERROR, errno);
		if (message.equals(MemorySegment.NULL)) {
			return "Unknown error (errno " + errno + ")";
		}
		return message.reinterpret(Long.MAX_VALUE).getString(0) + " (errno " + errno + ")";
	}
	
	/**
	 * Encodes an address into a native socket address.<br>
	 *
	 * @param arena The arena to allocate the socket address in
	 * @param address The address to encode
	 * @param port The port, which ICMP datagram sockets on Linux use as the echo identifier
	 * @param scopeId The IPv6 scope id, ignored for IPv4 addresses
	 * @return The encoded socket address
	 * @throws NullPointerException If the arena or address is null
	 * @throws IllegalArgumentException If the port is not between 0 and 65535
	 */
	static @NonNull MemorySegment encodeAddress(@NonNull Arena arena, @NonNull IpAddress<?> address, int port, int scopeId) {
		Objects.requireNonNull(arena, "Arena must not be null");
		Objects.requireNonNull(address, "Address must not be null");
		if (port < 0 || port > 0xFFFF) {
			throw new IllegalArgumentException("Port must be between 0 and 65535: " + port);
		}
		Platform platform = platform();
		
		boolean ipv4 = address instanceof Ipv4Address;
		long size = ipv4 ? SOCKADDR_IN_SIZE : SOCKADDR_IN6_SIZE;
		MemorySegment segment = arena.allocate(size, 4);
		platform.writeFamily(segment, ipv4 ? platform.afInet() : platform.afInet6(), size);
		segment.set(JAVA_SHORT_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN), 2, (short) port);
		
		byte[] bytes = address.toBytes();
		if (ipv4) {
			MemorySegment.copy(bytes, 0, segment, JAVA_BYTE, 4, bytes.length);
		} else {
			MemorySegment.copy(bytes, 0, segment, JAVA_BYTE, 8, bytes.length);
			segment.set(JAVA_INT, 24, scopeId);
		}
		return segment;
	}
	
	/**
	 * Decodes the address of a native socket address.<br>
	 * A non-zero IPv6 scope id becomes the numeric zone id of the address.<br>
	 *
	 * @param segment The socket address
	 * @return The decoded address, or null if the address family is neither IPv4 nor IPv6
	 * @throws NullPointerException If the segment is null
	 */
	static @Nullable IpAddress<?> decodeAddress(@NonNull MemorySegment segment) {
		Objects.requireNonNull(segment, "Segment must not be null");
		Platform platform = platform();
		
		int family = platform.readFamily(segment);
		if (family == platform.afInet()) {
			return Ipv4Address.fromBytes(segment.asSlice(4, 4).toArray(JAVA_BYTE));
		} else if (family == platform.afInet6()) {
			Ipv6Address address = Ipv6Address.fromBytes(segment.asSlice(8, 16).toArray(JAVA_BYTE));
			int scopeId = segment.get(JAVA_INT, 24);
			return scopeId == 0 ? address : address.withZoneId(Integer.toUnsignedString(scopeId));
		}
		return null;
	}
	
	/**
	 * Decodes the port of a native socket address.<br>
	 *
	 * @param segment The socket address
	 * @return The port in host byte order
	 * @throws NullPointerException If the segment is null
	 */
	static int decodePort(@NonNull MemorySegment segment) {
		Objects.requireNonNull(segment, "Segment must not be null");
		return Short.toUnsignedInt(segment.get(JAVA_SHORT_UNALIGNED.withOrder(ByteOrder.BIG_ENDIAN), 2));
	}
	
	/**
	 * Invokes a native function and rethrows anything it throws unchecked.<br>
	 *
	 * @param handle The method handle of the function
	 * @param arguments The arguments of the call
	 * @return The result of the call
	 * @throws NullPointerException If the handle or arguments are null
	 */
	private static Object invoke(@NonNull MethodHandle handle, Object @NonNull ... arguments) {
		Objects.requireNonNull(handle, "Handle must not be null");
		Objects.requireNonNull(arguments, "Arguments must not be null");
		
		try {
			return handle.invokeWithArguments(arguments);
		} catch (RuntimeException | Error e) {
			throw e;
		} catch (Throwable t) {
			throw new IllegalStateException("Native call failed", t);
		}
	}
	
	/**
	 * The result of a native call together with the {@code errno} captured directly after it.<br>
	 *
	 * @author Luis-St
	 *
	 * @param value The return value of the call
	 * @param errno The error number, only meaningful if the call failed
	 */
	record Result(long value, int errno) {
		
		/**
		 * Creates a result from a return value and the captured state.<br>
		 *
		 * @param value The return value
		 * @param state The captured state
		 * @return The result
		 * @throws NullPointerException If the state is null
		 */
		private static @NonNull Result of(long value, @NonNull MemorySegment state) {
			Objects.requireNonNull(state, "State must not be null");
			return new Result(value, value < 0 ? (int) ERRNO.get(state, 0L) : 0);
		}
		
		/**
		 * Checks whether the native call failed.<br>
		 * @return True if the call returned a negative value
		 */
		boolean failed() {
			return this.value < 0;
		}
	}
	
	/**
	 * The constants and layout differences of a supported operating system.<br>
	 *
	 * @author Luis-St
	 *
	 * @param bsdSockaddr Whether socket addresses start with a length byte followed by a one byte family
	 * @param longNfds Whether the descriptor count of poll is a 64 bit value
	 * @param ipv4HeaderOnDatagram Whether ICMP datagram sockets deliver IPv4 packets including the IP header
	 * @param kernelIdentifier Whether ICMP datagram sockets replace the echo identifier with their own
	 * @param afInet The IPv4 address family
	 * @param afInet6 The IPv6 address family
	 * @param ipTtl The IPv4 time to live option
	 * @param ipv6UnicastHops The IPv6 unicast hop limit option
	 * @param msgDontWait The flag making a receive call non-blocking
	 * @param eintr The error number of an interrupted call
	 * @param eagain The error number of a call that would block
	 * @param eperm The error number of an operation that is not permitted
	 * @param eacces The error number of a denied permission
	 * @param eaddrinuse The error number of an address already in use
	 * @param emsgsize The error number of a message that is too long
	 * @param enetunreach The error number of an unreachable network
	 * @param ehostunreach The error number of an unreachable host
	 */
	record Platform(
		boolean bsdSockaddr,
		boolean longNfds,
		boolean ipv4HeaderOnDatagram,
		boolean kernelIdentifier,
		int afInet,
		int afInet6,
		int ipTtl,
		int ipv6UnicastHops,
		int msgDontWait,
		int eintr,
		int eagain,
		int eperm,
		int eacces,
		int eaddrinuse,
		int emsgsize,
		int enetunreach,
		int ehostunreach
	) {
		
		/**
		 * The datagram socket type.<br>
		 */
		static final int SOCK_DGRAM = 2;
		/**
		 * The raw socket type.<br>
		 */
		static final int SOCK_RAW = 3;
		/**
		 * The IPv4 protocol level.<br>
		 */
		static final int IPPROTO_IP = 0;
		/**
		 * The ICMP protocol number.<br>
		 */
		static final int IPPROTO_ICMP = 1;
		/**
		 * The IPv6 protocol level.<br>
		 */
		static final int IPPROTO_IPV6 = 41;
		/**
		 * The ICMPv6 protocol number.<br>
		 */
		static final int IPPROTO_ICMPV6 = 58;
		
		/**
		 * Detects the platform of this runtime.<br>
		 * @return The platform, or null if it is not supported
		 */
		private static @Nullable Platform detect() {
			if (ADDRESS.byteSize() != 8) {
				return null;
			}
			
			String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
			if (os.contains("linux")) {
				return new Platform(false, true, false, true, 2, 10, 2, 16, 0x40, 4, 11, 1, 13, 98, 90, 101, 113);
			} else if (os.contains("mac") || os.contains("darwin")) {
				return new Platform(true, false, true, false, 2, 30, 4, 4, 0x80, 4, 35, 1, 13, 48, 40, 51, 65);
			}
			return null;
		}
		
		/**
		 * Maps an error number to the matching network error type.<br>
		 *
		 * @param errno The error number
		 * @return The error type
		 */
		@NonNull NetworkErrorType errorType(int errno) {
			if (errno == this.enetunreach) {
				return NetworkErrorType.NETWORK_UNREACHABLE;
			} else if (errno == this.ehostunreach) {
				return NetworkErrorType.HOST_UNREACHABLE;
			} else if (errno == this.eaddrinuse) {
				return NetworkErrorType.ADDRESS_IN_USE;
			} else if (errno == this.emsgsize) {
				return NetworkErrorType.MESSAGE_TOO_LARGE;
			}
			return NetworkErrorType.IO_ERROR;
		}
		
		/**
		 * Writes the address family into a socket address.<br>
		 *
		 * @param segment The socket address
		 * @param family The address family
		 * @param size The size of the socket address
		 * @throws NullPointerException If the segment is null
		 */
		private void writeFamily(@NonNull MemorySegment segment, int family, long size) {
			Objects.requireNonNull(segment, "Segment must not be null");
			
			if (this.bsdSockaddr) {
				segment.set(JAVA_BYTE, 0, (byte) size);
				segment.set(JAVA_BYTE, 1, (byte) family);
			} else {
				segment.set(JAVA_SHORT, 0, (short) family);
			}
		}
		
		/**
		 * Reads the address family of a socket address.<br>
		 *
		 * @param segment The socket address
		 * @return The address family
		 * @throws NullPointerException If the segment is null
		 */
		private int readFamily(@NonNull MemorySegment segment) {
			Objects.requireNonNull(segment, "Segment must not be null");
			
			if (this.bsdSockaddr) {
				return Byte.toUnsignedInt(segment.get(JAVA_BYTE, 1));
			}
			return Short.toUnsignedInt(segment.get(JAVA_SHORT, 0));
		}
	}
	
	/**
	 * Lazily linked handles of the native functions.<br>
	 * The functions are only looked up once a socket is created on a supported platform.<br>
	 *
	 * @author Luis-St
	 */
	private static final class Functions {
		
		/**
		 * The linker of the native platform.<br>
		 */
		private static final Linker LINKER = Linker.nativeLinker();
		/**
		 * The option capturing {@code errno} after a call.<br>
		 */
		private static final Linker.Option ERRNO_OPTION = Linker.Option.captureCallState("errno");
		/**
		 * The handle of {@code int socket(int domain, int type, int protocol)}.<br>
		 */
		private static final MethodHandle SOCKET = link("socket", FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT, JAVA_INT));
		/**
		 * The handle of {@code int setsockopt(int fd, int level, int name, const void* value, socklen_t length)}.<br>
		 */
		private static final MethodHandle SETSOCKOPT = link("setsockopt", FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT, JAVA_INT, ADDRESS, JAVA_INT));
		/**
		 * The handle of {@code int bind(int fd, const struct sockaddr* address, socklen_t length)}.<br>
		 */
		private static final MethodHandle BIND = link("bind", FunctionDescriptor.of(JAVA_INT, JAVA_INT, ADDRESS, JAVA_INT));
		/**
		 * The handle of {@code int getsockname(int fd, struct sockaddr* address, socklen_t* length)}.<br>
		 */
		private static final MethodHandle GETSOCKNAME = link("getsockname", FunctionDescriptor.of(JAVA_INT, JAVA_INT, ADDRESS, ADDRESS));
		/**
		 * The handle of {@code ssize_t sendto(int fd, const void* buffer, size_t length, int flags, const struct sockaddr* address, socklen_t addressLength)}.<br>
		 */
		private static final MethodHandle SENDTO = link("sendto", FunctionDescriptor.of(JAVA_LONG, JAVA_INT, ADDRESS, JAVA_LONG, JAVA_INT, ADDRESS, JAVA_INT));
		/**
		 * The handle of {@code ssize_t recvfrom(int fd, void* buffer, size_t length, int flags, struct sockaddr* address, socklen_t* addressLength)}.<br>
		 */
		private static final MethodHandle RECVFROM = link("recvfrom", FunctionDescriptor.of(JAVA_LONG, JAVA_INT, ADDRESS, JAVA_LONG, JAVA_INT, ADDRESS, ADDRESS));
		/**
		 * The handle of {@code int poll(struct pollfd* fds, nfds_t count, int timeout)}.<br>
		 * The type of {@code nfds_t} is 64 bit on Linux and 32 bit on macOS.<br>
		 */
		private static final MethodHandle POLL = link("poll", FunctionDescriptor.of(JAVA_INT, ADDRESS, platform().longNfds() ? JAVA_LONG : JAVA_INT, JAVA_INT));
		/**
		 * The handle of {@code int close(int fd)}.<br>
		 */
		private static final MethodHandle CLOSE = link("close", FunctionDescriptor.of(JAVA_INT, JAVA_INT));
		/**
		 * The handle of {@code char* strerror(int errno)}, linked without capturing the call state.<br>
		 */
		private static final MethodHandle STRERROR = LINKER.downcallHandle(find("strerror"), FunctionDescriptor.of(ADDRESS, JAVA_INT));
		
		/**
		 * Private constructor to prevent instantiation.<br>
		 * This is a static holder class.<br>
		 */
		private Functions() {}
		
		/**
		 * Links a function of the C library that captures {@code errno}.<br>
		 *
		 * @param name The name of the function
		 * @param descriptor The descriptor of the function
		 * @return The method handle, which takes the capture segment as its first argument
		 */
		private static @NonNull MethodHandle link(@NonNull String name, @NonNull FunctionDescriptor descriptor) {
			return LINKER.downcallHandle(find(name), descriptor, ERRNO_OPTION);
		}
		
		/**
		 * Looks up a function of the C library.<br>
		 *
		 * @param name The name of the function
		 * @return The address of the function
		 * @throws NullPointerException If the name is null
		 * @throws UnsupportedOperationException If the function does not exist
		 */
		private static @NonNull MemorySegment find(@NonNull String name) {
			Objects.requireNonNull(name, "Name must not be null");
			return LINKER.defaultLookup().find(name).orElseThrow(() -> new UnsupportedOperationException("Native function " + name + " is not available"));
		}
	}
}
