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

package net.luis.utils.io.network.connection.udp;

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A UDP socket that sends and receives datagrams.<br>
 * <p>
 *     UDP has no distinction between client and server, so a peer is used on both sides of a communication.<br>
 *     A peer is bound to a local endpoint with {@link #bindTo(Endpoint)} or to an ephemeral port with {@link #ephemeral()}.<br>
 *     It sends to any destination and receives from any source, every datagram carries its own endpoint.
 * </p>
 * <p>
 *     A peer can be connected to a single remote endpoint with {@link #connect(Endpoint)}.<br>
 *     A connected peer only receives datagrams from that endpoint and can send raw bytes to it with {@link #send(byte[])}.
 * </p>
 * <p>
 *     A {@link UdpServer} runs a receive loop over a peer and hands every datagram to its handler,
 *     replies are sent through {@link UdpServer#peer()}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (UdpPeer peer = UdpPeer.ephemeral()) {
 *     peer.connect(new IpEndpoint(Ipv4Address.LOOPBACK, 9999));
 *     peer.send("Hello".getBytes());
 *     peer.receive().ifPresent(datagram -> System.out.println(new String(datagram.data())));
 * }
 * }</pre>
 *
 * @see UdpPeerConfig
 * @see UdpServer
 *
 * @author Luis-St
 */
public final class UdpPeer implements NetworkClient<UdpDatagram> {
	
	/**
	 * The configuration of this peer.<br>
	 */
	private final UdpPeerConfig config;
	/**
	 * The underlying datagram socket.<br>
	 */
	private final DatagramSocket socket;
	/**
	 * The lock that serializes receives, so that the receive buffer is used by one receive at a time.<br>
	 */
	private final ReentrantLock receiveLock = new ReentrantLock();
	/**
	 * The reusable buffer for received datagrams, guarded by the receive lock.<br>
	 */
	private final byte[] receiveBuffer;
	/**
	 * The remote endpoint this peer is connected to, or null if it is not connected.<br>
	 */
	private volatile @Nullable IpEndpoint target;
	
	/**
	 * Constructs a new peer over the given bound socket.<br>
	 *
	 * @param socket The bound datagram socket
	 * @param config The configuration of the peer
	 * @throws NullPointerException If the socket or the configuration is null
	 */
	private UdpPeer(@NonNull DatagramSocket socket, @NonNull UdpPeerConfig config) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
		this.receiveBuffer = new byte[config.maxDatagramSize()];
	}
	
	/**
	 * Creates a peer with the default configuration that is bound to the given local endpoint.<br>
	 *
	 * @param localEndpoint The local endpoint to bind to
	 * @return The bound peer
	 * @throws NullPointerException If the local endpoint is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public static @NonNull UdpPeer bindTo(@NonNull Endpoint localEndpoint) throws NetworkConnectionException {
		return bindTo(localEndpoint, UdpPeerConfig.DEFAULT);
	}
	
	/**
	 * Creates a peer with the given configuration that is bound to the given local endpoint.<br>
	 *
	 * @param localEndpoint The local endpoint to bind to
	 * @param config The configuration of the peer
	 * @return The bound peer
	 * @throws NullPointerException If the local endpoint or the configuration is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public static @NonNull UdpPeer bindTo(@NonNull Endpoint localEndpoint, @NonNull UdpPeerConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(localEndpoint, "Local endpoint must not be null");
		return bind(localEndpoint, localEndpoint.toInetSocketAddress(), config);
	}
	
	/**
	 * Creates a peer with the default configuration that is bound to an ephemeral port on the wildcard address.<br>
	 *
	 * @return The bound peer
	 * @throws NetworkConnectionException If binding fails
	 */
	public static @NonNull UdpPeer ephemeral() throws NetworkConnectionException {
		return ephemeral(UdpPeerConfig.DEFAULT);
	}
	
	/**
	 * Creates a peer with the given configuration that is bound to an ephemeral port on the wildcard address.<br>
	 *
	 * @param config The configuration of the peer
	 * @return The bound peer
	 * @throws NullPointerException If the configuration is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public static @NonNull UdpPeer ephemeral(@NonNull UdpPeerConfig config) throws NetworkConnectionException {
		return bind(null, new InetSocketAddress(0), config);
	}
	
	/**
	 * Creates a datagram socket with the given configuration and binds it to the given address.<br>
	 *
	 * @param endpoint The endpoint reported in exceptions, or null if the peer is bound to an ephemeral port
	 * @param address The address to bind to
	 * @param config The configuration of the peer
	 * @return The bound peer
	 * @throws NullPointerException If the address or the configuration is null
	 * @throws NetworkConnectionException If binding fails
	 */
	private static @NonNull UdpPeer bind(@Nullable Endpoint endpoint, @NonNull InetSocketAddress address, @NonNull UdpPeerConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(address, "Address must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		if (address.isUnresolved()) {
			throw new NetworkConnectionException("Failed to resolve " + endpoint, NetworkErrorType.HOST_UNREACHABLE, endpoint);
		}
		
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(null);
			socket.setReuseAddress(config.reuseAddress());
			socket.setBroadcast(config.broadcast());
			socket.setSoTimeout(NetworkUtils.toTimeoutMillis(config.receiveTimeout()));
			socket.bind(address);
			return new UdpPeer(socket, config);
		} catch (BindException e) {
			NetworkUtils.closeQuietly(socket);
			throw NetworkUtils.mapBindFailure(e, "Failed to bind to " + address, endpoint);
		} catch (IOException e) {
			NetworkUtils.closeQuietly(socket);
			throw new NetworkConnectionException("Failed to bind to " + address, e, NetworkErrorType.IO_ERROR, endpoint);
		}
	}
	
	/**
	 * Returns the configuration of this peer.<br>
	 * @return The configuration
	 */
	public @NonNull UdpPeerConfig config() {
		return this.config;
	}
	
	/**
	 * Connects this peer to the given remote endpoint.<br>
	 * <p>
	 *     A connected peer only receives datagrams from the remote endpoint
	 *     and can send raw bytes to it with {@link #send(byte[])}.<br>
	 *     Sending to another destination is rejected while the peer is connected.<br>
	 *     No packet is sent, the connection only exists locally.
	 * </p>
	 *
	 * @param remoteEndpoint The remote endpoint to connect to
	 * @throws NullPointerException If the remote endpoint is null
	 * @throws NetworkConnectionException If the peer is closed, the endpoint cannot be resolved or connecting fails
	 */
	public void connect(@NonNull Endpoint remoteEndpoint) throws NetworkConnectionException {
		Objects.requireNonNull(remoteEndpoint, "Remote endpoint must not be null");
		this.ensureActive();
		
		InetSocketAddress address = this.resolve(remoteEndpoint);
		try {
			this.socket.connect(address);
			this.target = IpEndpoint.from(address);
		} catch (SocketException e) {
			throw new NetworkConnectionException("Failed to connect to " + remoteEndpoint, e, NetworkErrorType.CONNECTION_FAILED, remoteEndpoint);
		}
	}
	
	/**
	 * Disconnects this peer from its remote endpoint.<br>
	 * Afterward the peer receives from and sends to any endpoint again.<br>
	 * Calling this method on a peer that is not connected has no effect.<br>
	 */
	public void disconnect() {
		this.socket.disconnect();
		this.target = null;
	}
	
	/**
	 * Returns whether this peer is connected to a single remote endpoint.<br>
	 * @return True if the peer is connected
	 */
	public boolean isConnected() {
		return this.target != null;
	}
	
	@Override
	public boolean isActive() {
		return !this.socket.isClosed();
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> localEndpoint() {
		if (this.socket.isClosed() || !this.socket.isBound()) {
			return Optional.empty();
		}
		return Optional.of(IpEndpoint.from((InetSocketAddress) this.socket.getLocalSocketAddress()));
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> remoteEndpoint() {
		return Optional.ofNullable(this.target);
	}
	
	/**
	 * Sends the data of the given datagram to its endpoint.<br>
	 *
	 * @param datagram The datagram to send
	 * @throws NullPointerException If the datagram is null
	 * @throws NetworkConnectionException If the peer is closed, the datagram is too large or sending fails
	 */
	@Override
	public void send(@NonNull UdpDatagram datagram) throws NetworkConnectionException {
		Objects.requireNonNull(datagram, "Datagram must not be null");
		this.send(datagram.endpoint(), datagram.data());
	}
	
	/**
	 * Sends the given data as a single datagram to the remote endpoint this peer is connected to.<br>
	 *
	 * @param data The data to send
	 * @throws NullPointerException If the data is null
	 * @throws NetworkConnectionException If the peer is not connected, the data is too large or sending fails
	 */
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		IpEndpoint target = this.target;
		if (target == null) {
			throw new NetworkConnectionException("Peer is not connected to a remote endpoint", NetworkErrorType.NOT_CONNECTED);
		}
		this.send(target, data);
	}
	
	/**
	 * Sends the given data as a single datagram to the given destination.<br>
	 * A connected peer only sends to the remote endpoint it is connected to.<br>
	 *
	 * @param destination The endpoint to send to
	 * @param data The data to send
	 * @throws NullPointerException If the destination or the data is null
	 * @throws NetworkConnectionException If the data is too large, the peer is closed, the destination cannot be resolved or is not the connected endpoint, or sending fails
	 */
	public void send(@NonNull Endpoint destination, byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(destination, "Destination must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		NetworkUtils.validateMessageSize(data, this.config.maxDatagramSize(), destination);
		this.ensureActive();
		
		InetSocketAddress address = this.resolve(destination);
		IpEndpoint target = this.target;
		if (target != null && !target.equals(IpEndpoint.from(address))) {
			throw new NetworkConnectionException("Peer is connected to " + target + " and cannot send to " + destination, NetworkErrorType.ALREADY_CONNECTED, destination);
		}
		
		try {
			this.socket.send(new DatagramPacket(data, data.length, address));
		} catch (IOException e) {
			throw NetworkUtils.mapIoFailure(e, "Failed to send datagram to " + destination, destination);
		}
	}
	
	/**
	 * Receives the next datagram (blocking).<br>
	 * <p>
	 *     A datagram that is larger than the maximum datagram size of the configuration is truncated.<br>
	 *     An empty optional is returned once the peer was closed, also when it was closed while waiting.
	 * </p>
	 *
	 * @return The received datagram, or an empty optional if the peer was closed
	 * @throws NetworkTimeoutException If no datagram arrived within the receive timeout
	 * @throws NetworkConnectionException If receiving fails
	 */
	@Override
	public @NonNull Optional<UdpDatagram> receive() throws NetworkConnectionException {
		this.receiveLock.lock();
		try {
			if (this.socket.isClosed()) {
				return Optional.empty();
			}
			
			DatagramPacket packet = new DatagramPacket(this.receiveBuffer, this.receiveBuffer.length);
			this.socket.receive(packet);
			
			byte[] data = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
			return Optional.of(new UdpDatagram(IpEndpoint.from((InetSocketAddress) packet.getSocketAddress()), data));
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Receive timed out", e, NetworkErrorType.READ_TIMEOUT, Objects.requireNonNullElse(this.config.receiveTimeout(), Duration.ZERO), null);
		} catch (IOException e) {
			if (this.socket.isClosed()) {
				return Optional.empty();
			}
			throw NetworkUtils.mapIoFailure(e, "Failed to receive datagram", null);
		} finally {
			this.receiveLock.unlock();
		}
	}
	
	@Override
	public void close() {
		this.socket.close();
	}
	
	/**
	 * Ensures that this peer is not closed.<br>
	 * @throws NetworkConnectionException If the peer is closed
	 */
	private void ensureActive() throws NetworkConnectionException {
		if (this.socket.isClosed()) {
			throw new NetworkConnectionException("Peer is closed", NetworkErrorType.SOCKET_CLOSED);
		}
	}
	
	/**
	 * Resolves the given endpoint into a socket address.<br>
	 *
	 * @param endpoint The endpoint to resolve
	 * @return The resolved socket address
	 * @throws NullPointerException If the endpoint is null
	 * @throws NetworkConnectionException If the endpoint cannot be resolved
	 */
	private @NonNull InetSocketAddress resolve(@NonNull Endpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		
		InetSocketAddress address = endpoint.toInetSocketAddress();
		if (address.isUnresolved()) {
			throw new NetworkConnectionException("Failed to resolve " + endpoint, new UnknownHostException(address.getHostString()), NetworkErrorType.HOST_UNREACHABLE, endpoint);
		}
		return address;
	}
}
