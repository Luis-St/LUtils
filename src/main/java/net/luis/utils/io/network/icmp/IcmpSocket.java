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
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.foreign.ValueLayout.*;

/**
 * A socket for sending and receiving ICMP messages,<br>
 * implemented on top of the native POSIX socket API through the Foreign Function and Memory API.<br>
 * <p>
 *     The Java standard library has no ICMP socket, only {@link InetAddress#isReachable(int)},<br>
 *     which silently falls back to a TCP connection when ICMP is not available.<br>
 *     This class opens a real ICMP socket instead, either an unprivileged datagram socket or a raw socket as chosen by {@link IcmpSocketConfig#socketType()}.
 * </p>
 * <p>
 *     Only 64-bit Linux and macOS are supported.<br>
 *     Because this class calls native code, the module should be granted native access with {@code --enable-native-access=net.luis.utils},<br>
 *     otherwise the runtime prints a warning on first use.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IcmpSocketConfig config = IcmpSocketConfig.builder()
 *     .receiveTimeout(Duration.ofSeconds(1))
 *     .build();
 *
 * try (IcmpSocket socket = new IcmpSocket(IcmpVersion.ICMP_V4, config)) {
 *     IcmpEchoReply reply = socket.ping(Ipv4Address.LOOPBACK, 1, "ping".getBytes());
 *     System.out.println("Reply from " + reply.packet().address() + " in " + reply.roundTripTime().toMillis() + " ms");
 * }
 * }</pre>
 * <p>
 *     The socket is thread safe.<br>
 *     Closing it from another thread wakes up a blocked receive call within a short time.
 * </p>
 *
 * @see IcmpSocketConfig
 * @see IcmpPacket
 *
 * @author Luis-St
 */
public final class IcmpSocket implements AutoCloseable {
	
	/**
	 * The longest time a receive call waits in the kernel before it checks whether the socket was closed.<br>
	 */
	private static final int POLL_SLICE_MILLIS = 100;
	/**
	 * The largest size of an IPv4 header, which precedes the ICMP message on some sockets.<br>
	 */
	private static final int MAX_IPV4_HEADER_SIZE = 60;
	
	/**
	 * The ICMP version of this socket.<br>
	 */
	private final IcmpVersion version;
	/**
	 * The configuration for this socket.<br>
	 */
	private final IcmpSocketConfig config;
	/**
	 * The platform this socket is running on.<br>
	 */
	private final IcmpNative.Platform platform;
	/**
	 * The file descriptor of the native socket.<br>
	 */
	private final int fd;
	/**
	 * The echo identifier used when the kernel does not choose one.<br>
	 */
	private final int ownIdentifier = ThreadLocalRandom.current().nextInt(0x10000);
	/**
	 * The lock that keeps the file descriptor from being closed while a native call uses it.<br>
	 */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	/**
	 * Whether this socket was closed.<br>
	 */
	private final AtomicBoolean closed = new AtomicBoolean();
	
	/**
	 * Opens a new ICMP socket with default configuration.<br>
	 *
	 * @param version The ICMP version of the socket
	 * @throws NullPointerException If the version is null
	 * @throws UnsupportedOperationException If the platform is not supported
	 * @throws NetworkConnectionException If the socket cannot be opened
	 */
	public IcmpSocket(@NonNull IcmpVersion version) throws NetworkConnectionException {
		this(version, IcmpSocketConfig.DEFAULT);
	}
	
	/**
	 * Opens a new ICMP socket with the specified configuration.<br>
	 *
	 * @param version The ICMP version of the socket
	 * @param config The socket configuration
	 * @throws NullPointerException If the version or config is null
	 * @throws UnsupportedOperationException If the platform is not supported
	 * @throws NetworkConnectionException If the socket cannot be opened, for example because the process lacks the rights for the socket type
	 */
	public IcmpSocket(@NonNull IcmpVersion version, @NonNull IcmpSocketConfig config) throws NetworkConnectionException {
		this.version = Objects.requireNonNull(version, "Version must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
		this.platform = IcmpNative.platform();
		
		boolean ipv4 = version == IcmpVersion.ICMP_V4;
		int domain = ipv4 ? this.platform.afInet() : this.platform.afInet6();
		int type = config.socketType() == IcmpSocketType.RAW ? IcmpNative.Platform.SOCK_RAW : IcmpNative.Platform.SOCK_DGRAM;
		int protocol = ipv4 ? IcmpNative.Platform.IPPROTO_ICMP : IcmpNative.Platform.IPPROTO_ICMPV6;
		
		try (Arena arena = Arena.ofConfined()) {
			IcmpNative.Result opened = IcmpNative.socket(arena, domain, type, protocol);
			if (opened.failed()) {
				throw this.failure(this.openFailureMessage(opened.errno()), opened.errno(), null);
			}
			this.fd = (int) opened.value();
			
			if (config.timeToLive() > 0) {
				IcmpNative.Result option = ipv4 ?
					IcmpNative.setIntOption(arena, this.fd, IcmpNative.Platform.IPPROTO_IP, this.platform.ipTtl(), config.timeToLive()) :
					IcmpNative.setIntOption(arena, this.fd, IcmpNative.Platform.IPPROTO_IPV6, this.platform.ipv6UnicastHops(), config.timeToLive());
				if (option.failed()) {
					IcmpNative.close(this.fd);
					throw this.failure("Failed to set the time to live to " + config.timeToLive(), option.errno(), null);
				}
			}
		}
	}
	
	/**
	 * Computes the internet checksum of RFC 1071 over the given data.<br>
	 *
	 * @param data The data to sum
	 * @return The ones' complement of the ones' complement sum as a 16 bit value
	 * @throws NullPointerException If the data is null
	 */
	private static int checksum(byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		
		long sum = 0;
		for (int i = 0; i + 1 < data.length; i += 2) {
			sum += ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
		}
		if (data.length % 2 != 0) {
			sum += (data[data.length - 1] & 0xFF) << 8;
		}
		while ((sum >>> 16) != 0) {
			sum = (sum & 0xFFFF) + (sum >>> 16);
		}
		return (int) (~sum & 0xFFFF);
	}
	
	/**
	 * Returns the ICMP version of this socket.<br>
	 * @return The ICMP version
	 */
	public @NonNull IcmpVersion version() {
		return this.version;
	}
	
	/**
	 * Checks whether this socket is still open.<br>
	 * @return True if the socket was not closed
	 */
	public boolean isOpen() {
		return !this.closed.get();
	}
	
	/**
	 * Returns the echo identifier this socket uses.<br>
	 * <p>
	 *     For a {@link IcmpSocketType#DATAGRAM datagram} socket on Linux the kernel chooses the identifier when the socket is bound or sends its first packet.<br>
	 *     Until then this method returns 0.<br>
	 *     Otherwise the identifier is chosen randomly when the socket is created.
	 * </p>
	 *
	 * @return The echo identifier between 0 and 65535
	 * @throws NetworkConnectionException If the socket is closed or the identifier cannot be read
	 */
	public int identifier() throws NetworkConnectionException {
		if (!this.kernelChoosesIdentifier()) {
			return this.ownIdentifier;
		}
		
		this.lock.readLock().lock();
		try (Arena arena = Arena.ofConfined()) {
			this.ensureOpen();
			
			MemorySegment address = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
			IcmpNative.Result result = IcmpNative.socketName(arena, this.fd, address);
			if (result.failed()) {
				throw this.failure("Failed to read the local address", result.errno(), null);
			}
			return IcmpNative.decodePort(address);
		} finally {
			this.lock.readLock().unlock();
		}
	}
	
	/**
	 * Binds this socket to a local address, which selects the source address of outgoing packets.<br>
	 * This must be called before the first packet is sent.<br>
	 *
	 * @param localAddress The local address to bind to
	 * @throws NullPointerException If the local address is null
	 * @throws IllegalArgumentException If the address does not belong to the version of this socket
	 * @throws NetworkConnectionException If the socket is closed or binding fails
	 */
	public void bind(@NonNull IpAddress<?> localAddress) throws NetworkConnectionException {
		Objects.requireNonNull(localAddress, "Local address must not be null");
		this.requireSupported(localAddress);
		
		this.lock.readLock().lock();
		try (Arena arena = Arena.ofConfined()) {
			this.ensureOpen();
			
			MemorySegment address = IcmpNative.encodeAddress(arena, localAddress, 0, this.scopeId(localAddress));
			IcmpNative.Result result = IcmpNative.bind(arena, this.fd, address);
			if (result.failed()) {
				throw this.failure("Failed to bind to " + localAddress, result.errno(), localAddress);
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}
	
	/**
	 * Sends an ICMP packet to its address.<br>
	 * <p>
	 *     The IPv4 checksum is computed by this method.<br>
	 *     The ICMPv6 checksum covers a pseudo header with the source address, so the kernel computes it.<br>
	 *     A datagram socket on Linux replaces the identifier of an echo request with {@link #identifier()}.
	 * </p>
	 *
	 * @param packet The packet to send
	 * @throws NullPointerException If the packet is null
	 * @throws IllegalArgumentException If the address of the packet does not belong to the version of this socket
	 * @throws NetworkConnectionException If the socket is closed or sending fails
	 */
	public void send(@NonNull IcmpPacket packet) throws NetworkConnectionException {
		Objects.requireNonNull(packet, "Packet must not be null");
		this.requireSupported(packet.address());
		byte[] data = this.encode(packet);
		
		this.lock.readLock().lock();
		try (Arena arena = Arena.ofConfined()) {
			this.ensureOpen();
			MemorySegment address = IcmpNative.encodeAddress(arena, packet.address(), 0, this.scopeId(packet.address()));
			MemorySegment buffer = arena.allocateFrom(JAVA_BYTE, data);
			
			IcmpNative.Result result = IcmpNative.sendTo(arena, this.fd, buffer, address);
			while (result.failed() && result.errno() == this.platform.eintr()) {
				result = IcmpNative.sendTo(arena, this.fd, buffer, address);
			}
			
			if (result.failed()) {
				throw this.failure("Failed to send ICMP packet to " + packet.address(), result.errno(), packet.address());
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}
	
	/**
	 * Receives the next ICMP packet using the configured buffer size.<br>
	 *
	 * @return The received packet
	 * @throws NetworkTimeoutException If no packet arrives within the configured receive timeout
	 * @throws NetworkConnectionException If the socket is closed or receiving fails
	 */
	public @NonNull IcmpPacket receive() throws NetworkConnectionException {
		return this.receive(this.config.bufferSize());
	}
	
	/**
	 * Receives the next ICMP packet.<br>
	 * A message longer than the given size is truncated.<br>
	 *
	 * @param maxBytes The maximum size of the ICMP message including its header
	 * @return The received packet
	 * @throws IllegalArgumentException If the maximum size is less than the ICMP header size
	 * @throws NetworkTimeoutException If no packet arrives within the configured receive timeout
	 * @throws NetworkConnectionException If the socket is closed or receiving fails
	 */
	public @NonNull IcmpPacket receive(int maxBytes) throws NetworkConnectionException {
		if (maxBytes < IcmpPacket.HEADER_SIZE) {
			throw new IllegalArgumentException("Max bytes must be at least " + IcmpPacket.HEADER_SIZE + ": " + maxBytes);
		}
		
		IcmpPacket packet = this.receiveUntil(maxBytes, this.deadline(System.nanoTime()));
		if (packet == null) {
			throw this.timeout();
		}
		return packet;
	}
	
	/**
	 * Sends an echo request to the destination and waits for the matching echo reply.<br>
	 * <p>
	 *     A reply matches if it carries the identifier of this socket and the given sequence number.<br>
	 *     All other packets received in the meantime are discarded.<br>
	 *     The configured receive timeout limits the whole exchange.
	 * </p>
	 *
	 * @param destination The address to ping
	 * @param sequenceNumber The sequence number of the echo request
	 * @param payload The data the reply is expected to echo
	 * @return The echo reply and the round trip time
	 * @throws NullPointerException If the destination or payload is null
	 * @throws IllegalArgumentException If the destination does not belong to the version of this socket or the sequence number is not between 0 and 65535
	 * @throws NetworkTimeoutException If no matching reply arrives within the configured receive timeout
	 * @throws NetworkConnectionException If the socket is closed, sending fails or receiving fails
	 */
	public @NonNull IcmpEchoReply ping(@NonNull IpAddress<?> destination, int sequenceNumber, byte @NonNull [] payload) throws NetworkConnectionException {
		return this.tryPing(destination, sequenceNumber, payload).orElseThrow(this::timeout);
	}
	
	/**
	 * Sends an echo request to the destination and waits for the matching echo reply.<br>
	 * <p>
	 *     Unlike {@link #ping(IpAddress, int, byte[])} a missing reply is not an error here,<br>
	 *     because a lost echo request is an expected outcome of a ping.<br>
	 *     The configured receive timeout limits the whole exchange.
	 * </p>
	 *
	 * @param destination The address to ping
	 * @param sequenceNumber The sequence number of the echo request
	 * @param payload The data the reply is expected to echo
	 * @return The echo reply and the round trip time, or an empty optional if no matching reply arrived within the configured receive timeout
	 * @throws NullPointerException If the destination or payload is null
	 * @throws IllegalArgumentException If the destination does not belong to the version of this socket or the sequence number is not between 0 and 65535
	 * @throws NetworkConnectionException If the socket is closed, sending fails or receiving fails
	 */
	public @NonNull Optional<IcmpEchoReply> tryPing(@NonNull IpAddress<?> destination, int sequenceNumber, byte @NonNull [] payload) throws NetworkConnectionException {
		IcmpPacket request = IcmpPacket.echoRequest(destination, this.identifier(), sequenceNumber, payload);
		
		long start = System.nanoTime();
		long deadline = this.deadline(start);
		this.send(request);
		int identifier = this.identifier();
		
		while (true) {
			IcmpPacket packet = this.receiveUntil(this.config.bufferSize(), deadline);
			if (packet == null) {
				return Optional.empty();
			}
			
			if (packet.isEchoReply() && packet.identifier() == identifier && packet.sequenceNumber() == sequenceNumber) {
				return Optional.of(new IcmpEchoReply(packet, Duration.ofNanos(System.nanoTime() - start)));
			}
		}
	}
	
	@Override
	public void close() {
		if (!this.closed.compareAndSet(false, true)) {
			return;
		}
		
		this.lock.writeLock().lock();
		try {
			IcmpNative.close(this.fd);
		} finally {
			this.lock.writeLock().unlock();
		}
	}
	
	/**
	 * Receives the next packet, waiting at most until the given deadline.<br>
	 * The wait is split into short slices, so a concurrent {@link #close()} is noticed quickly.<br>
	 *
	 * @param maxBytes The maximum size of the ICMP message including its header
	 * @param deadline The {@link System#nanoTime()} at which to give up, or 0 to wait forever
	 * @return The received packet, or null if the deadline passed
	 * @throws IllegalArgumentException If the maximum size is less than the ICMP header size
	 * @throws NetworkConnectionException If the socket is closed, receiving fails or the packet is malformed
	 */
	private @Nullable IcmpPacket receiveUntil(int maxBytes, long deadline) throws NetworkConnectionException {
		if (maxBytes < IcmpPacket.HEADER_SIZE) {
			throw new IllegalArgumentException("Max bytes must be at least " + IcmpPacket.HEADER_SIZE + ": " + maxBytes);
		}
		
		int headerSpace = this.receivesIpv4Header() ? MAX_IPV4_HEADER_SIZE : 0;
		
		this.lock.readLock().lock();
		try (Arena arena = Arena.ofConfined()) {
			MemorySegment buffer = arena.allocate(maxBytes + headerSpace);
			MemorySegment address = arena.allocate(IcmpNative.SOCKADDR_STORAGE_SIZE, 8);
			
			while (true) {
				this.ensureOpen();
				int wait = POLL_SLICE_MILLIS;
				if (deadline != 0) {
					long remaining = deadline - System.nanoTime();
					if (remaining <= 0) {
						return null;
					}
					
					wait = (int) Math.clamp(Duration.ofNanos(remaining).toMillis(), 1, POLL_SLICE_MILLIS);
				}
				
				try (Arena call = Arena.ofConfined()) {
					IcmpNative.Result polled = IcmpNative.poll(call, this.fd, IcmpNative.POLLIN, wait);
					if (polled.failed() && polled.errno() != this.platform.eintr()) {
						throw this.failure("Failed to wait for ICMP packets", polled.errno(), null);
					}
					if (polled.value() <= 0) {
						continue;
					}
					
					IcmpNative.Result received = IcmpNative.receiveFrom(call, this.fd, buffer, address);
					if (received.failed()) {
						if (received.errno() == this.platform.eagain() || received.errno() == this.platform.eintr()) {
							continue;
						}
						throw this.failure("Failed to receive ICMP packet", received.errno(), null);
					}
					return this.decode(buffer.asSlice(0, received.value()).toArray(JAVA_BYTE), address, maxBytes);
				}
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}
	
	/**
	 * Encodes a packet into its wire format.<br>
	 * The checksum is only filled in for ICMP over IPv4.<br>
	 *
	 * @param packet The packet to encode
	 * @return The encoded message
	 * @throws NullPointerException If the packet is null
	 */
	private byte @NonNull [] encode(@NonNull IcmpPacket packet) {
		Objects.requireNonNull(packet, "Packet must not be null");
		
		byte[] data = new byte[packet.length()];
		data[0] = (byte) packet.type();
		data[1] = (byte) packet.code();
		data[4] = (byte) (packet.restOfHeader() >>> 24);
		data[5] = (byte) (packet.restOfHeader() >>> 16);
		data[6] = (byte) (packet.restOfHeader() >>> 8);
		data[7] = (byte) packet.restOfHeader();
		System.arraycopy(packet.payload(), 0, data, IcmpPacket.HEADER_SIZE, packet.payload().length);
		
		if (this.version == IcmpVersion.ICMP_V4) {
			int checksum = checksum(data);
			data[2] = (byte) (checksum >>> 8);
			data[3] = (byte) checksum;
		}
		return data;
	}
	
	/**
	 * Decodes a received datagram into a packet.<br>
	 *
	 * @param data The received bytes, including the IPv4 header if the socket delivers it
	 * @param address The source socket address
	 * @param maxBytes The maximum size of the ICMP message including its header
	 * @return The decoded packet
	 * @throws NullPointerException If the data or address is null
	 * @throws IllegalArgumentException If the maximum size is less than the ICMP header size
	 * @throws NetworkConnectionException If the datagram is too short or its source address is unknown
	 */
	private @NonNull IcmpPacket decode(byte @NonNull [] data, @NonNull MemorySegment address, int maxBytes) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		if (maxBytes < IcmpPacket.HEADER_SIZE) {
			throw new IllegalArgumentException("Max bytes must be at least " + IcmpPacket.HEADER_SIZE + ": " + maxBytes);
		}
		
		int offset = this.receivesIpv4Header() && data.length > 0 ? (data[0] & 0x0F) * 4 : 0;
		if (data.length < offset + IcmpPacket.HEADER_SIZE) {
			throw new NetworkConnectionException("Received ICMP packet is too short: " + data.length + " bytes", NetworkErrorType.PROTOCOL_ERROR);
		}
		
		IpAddress<?> source = IcmpNative.decodeAddress(address);
		if (source == null) {
			throw new NetworkConnectionException("Received ICMP packet from an unknown address family", NetworkErrorType.PROTOCOL_ERROR);
		}
		
		int restOfHeader = ((data[offset + 4] & 0xFF) << 24) | ((data[offset + 5] & 0xFF) << 16) | ((data[offset + 6] & 0xFF) << 8) | (data[offset + 7] & 0xFF);
		int end = Math.min(data.length, offset + maxBytes);
		byte[] payload = Arrays.copyOfRange(data, offset + IcmpPacket.HEADER_SIZE, end);
		return new IcmpPacket(source, data[offset] & 0xFF, data[offset + 1] & 0xFF, restOfHeader, payload);
	}
	
	/**
	 * Returns the IPv6 scope id of an address from its zone id.<br>
	 * A numeric zone id is used as is, a named one is looked up as a network interface.<br>
	 *
	 * @param address The address
	 * @return The scope id, or 0 if the address has no zone id
	 * @throws NullPointerException If the address is null
	 * @throws NetworkConnectionException If the named network interface does not exist
	 */
	private int scopeId(@NonNull IpAddress<?> address) throws NetworkConnectionException {
		Objects.requireNonNull(address, "Address must not be null");
		if (!(address instanceof Ipv6Address ipv6) || ipv6.zoneId() == null) {
			return 0;
		}
		
		String zoneId = ipv6.zoneId();
		if (!zoneId.isEmpty() && zoneId.chars().allMatch(Character::isDigit)) {
			return Integer.parseUnsignedInt(zoneId);
		}
		
		try {
			NetworkInterface networkInterface = NetworkInterface.getByName(zoneId);
			if (networkInterface == null) {
				throw new NetworkConnectionException("Unknown network interface of zone id: " + zoneId, NetworkErrorType.HOST_UNREACHABLE, new IpEndpoint(address, 0));
			}
			return networkInterface.getIndex();
		} catch (SocketException e) {
			throw new NetworkConnectionException("Failed to look up the network interface of zone id: " + zoneId, e, NetworkErrorType.IO_ERROR, new IpEndpoint(address, 0));
		}
	}
	
	/**
	 * Computes the deadline of a receive operation from the configured receive timeout.<br>
	 *
	 * @param start The {@link System#nanoTime()} at which the operation started
	 * @return The deadline, or 0 if the timeout is infinite
	 */
	private long deadline(long start) {
		if (this.config.receiveTimeout().isZero()) {
			return 0;
		}
		long deadline = start + this.config.receiveTimeout().toNanos();
		return deadline == 0 ? 1 : deadline;
	}
	
	/**
	 * Checks whether the kernel chooses the echo identifier of this socket.<br>
	 * @return True for datagram sockets on Linux
	 */
	private boolean kernelChoosesIdentifier() {
		return this.config.socketType() == IcmpSocketType.DATAGRAM && this.platform.kernelIdentifier();
	}
	
	/**
	 * Checks whether received datagrams start with an IPv4 header.<br>
	 * @return True for IPv4 raw sockets and for IPv4 datagram sockets on macOS
	 */
	private boolean receivesIpv4Header() {
		return this.version == IcmpVersion.ICMP_V4 && (this.config.socketType() == IcmpSocketType.RAW || this.platform.ipv4HeaderOnDatagram());
	}
	
	/**
	 * Ensures that the address belongs to the version of this socket.<br>
	 *
	 * @param address The address to check
	 * @throws IllegalArgumentException If the address has a different IP version
	 */
	private void requireSupported(@NonNull IpAddress<?> address) {
		if (!this.version.supports(address)) {
			throw new IllegalArgumentException("Address " + address + " does not belong to " + this.version);
		}
	}
	
	/**
	 * Ensures that this socket is still open.<br>
	 * @throws NetworkConnectionException If the socket was closed
	 */
	private void ensureOpen() throws NetworkConnectionException {
		if (this.closed.get()) {
			throw new NetworkConnectionException("Socket is closed", NetworkErrorType.SOCKET_CLOSED);
		}
	}
	
	/**
	 * Creates the exception for a receive operation that ran past the configured receive timeout.<br>
	 * @return The exception to throw
	 */
	private @NonNull NetworkTimeoutException timeout() {
		return new NetworkTimeoutException("Receive timed out", NetworkErrorType.READ_TIMEOUT, this.config.receiveTimeout());
	}
	
	/**
	 * Builds the message of a failure to open the socket.<br>
	 * A missing permission gets a hint about the rights the configured socket type needs.<br>
	 *
	 * @param errno The error number
	 * @return The message
	 */
	private @NonNull String openFailureMessage(int errno) {
		String message = "Failed to open " + this.config.socketType() + " " + this.version + " socket";
		if (errno != this.platform.eacces() && errno != this.platform.eperm()) {
			return message;
		}
		if (this.config.socketType() == IcmpSocketType.RAW) {
			return message + ", raw sockets need root rights or the CAP_NET_RAW capability";
		}
		return message + ", on Linux the group of this process must be inside net.ipv4.ping_group_range";
	}
	
	/**
	 * Creates the exception for a failed native call and reports it to the error handler.<br>
	 *
	 * @param message The message describing the failed operation
	 * @param errno The error number of the call
	 * @param address The remote or local address involved, or null if none
	 * @return The exception to throw
	 * @throws NullPointerException If the message is null
	 */
	private @NonNull NetworkConnectionException failure(@NonNull String message, int errno, @Nullable IpAddress<?> address) {
		Objects.requireNonNull(message, "Message must not be null");
		
		NetworkErrorType errorType = this.platform.errorType(errno);
		IOException cause = new IOException(IcmpNative.describe(errno));
		NetworkUtils.handleError(this.config.onError(), errorType, message, cause);
		return new NetworkConnectionException(message + ": " + cause.getMessage(), cause, errorType, address == null ? null : new IpEndpoint(address, 0));
	}
}
