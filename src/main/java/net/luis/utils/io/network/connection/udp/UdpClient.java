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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.*;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.Optional;

/**
 * A connectionless UDP client for sending and receiving datagrams.<br>
 * This class provides a simple blocking API for UDP communication.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * UdpClientConfig config = UdpClientConfig.builder()
 *     .receiveTimeout(Duration.ofSeconds(5))
 *     .build();
 *
 * try (UdpClient client = new UdpClient(config)) {
 *     client.bind(new IpEndpoint(Ipv4Address.ANY, 0)); // Bind to any available port
 *
 *     IpEndpoint server = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
 *     client.send(server, "Hello".getBytes());
 *
 *     UdpDatagram response = client.receive();
 *     System.out.println("From: " + response.endpoint());
 * }
 * }</pre>
 *
 * @see UdpClientConfig
 * @see UdpDatagram
 *
 * @author Luis-St
 */
public final class UdpClient implements NetworkClient<UdpDatagram> {
	
	/**
	 * The configuration for this client.<br>
	 */
	private final UdpClientConfig config;
	/**
	 * The underlying datagram socket for communication.<br>
	 */
	private volatile DatagramSocket socket;
	
	/**
	 * Constructs a new UDP client with default configuration.<br>
	 */
	public UdpClient() {
		this(UdpClientConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new UDP client with the specified configuration.<br>
	 *
	 * @param config The client configuration
	 * @throws NullPointerException If config is null
	 */
	public UdpClient(@NonNull UdpClientConfig config) {
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Creates a new UDP client with default configuration and binds it to the specified local endpoint.<br>
	 *
	 * @param localEndpoint The local endpoint to bind to
	 * @return The bound client
	 * @throws NullPointerException If local endpoint is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public static @NonNull UdpClient bindTo(@NonNull Endpoint localEndpoint) throws NetworkConnectionException {
		return bindTo(localEndpoint, UdpClientConfig.DEFAULT);
	}
	
	/**
	 * Creates a new UDP client with the specified configuration and binds it to the specified local endpoint.<br>
	 * If the binding fails, the client is closed before the exception is propagated.<br>
	 *
	 * @param localEndpoint The local endpoint to bind to
	 * @param config The client configuration
	 * @return The bound client
	 * @throws NullPointerException If local endpoint or config is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public static @NonNull UdpClient bindTo(@NonNull Endpoint localEndpoint, @NonNull UdpClientConfig config) throws NetworkConnectionException {
		Objects.requireNonNull(localEndpoint, "Local endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		UdpClient client = new UdpClient(config);
		try {
			client.bind(localEndpoint);
			return client;
		} catch (NetworkConnectionException e) {
			client.close();
			throw e;
		}
	}
	
	/**
	 * Binds the client to a local endpoint.<br>
	 * This must be called before receiving datagrams.<br>
	 *
	 * @param localEndpoint The local endpoint to bind to
	 * @throws NullPointerException If local endpoint is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public void bind(@NonNull Endpoint localEndpoint) throws NetworkConnectionException {
		Objects.requireNonNull(localEndpoint, "Local endpoint must not be null");
		if (this.socket != null && !this.socket.isClosed()) {
			throw new NetworkConnectionException("Client is already bound", NetworkErrorType.ALREADY_CONNECTED, localEndpoint);
		}
		
		try {
			this.socket = new DatagramSocket(null);
			this.socket.setReuseAddress(this.config.reuseAddress());
			this.socket.setBroadcast(this.config.broadcast());
			
			if (!this.config.receiveTimeout().isZero()) {
				this.socket.setSoTimeout((int) this.config.receiveTimeout().toMillis());
			}
			
			this.socket.bind(localEndpoint.toInetSocketAddress());
		} catch (BindException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.ADDRESS_IN_USE, "Address already in use: " + localEndpoint, e);
			throw new NetworkConnectionException("Address already in use: " + localEndpoint, e, NetworkErrorType.ADDRESS_IN_USE, localEndpoint);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to bind to " + localEndpoint, e);
			throw new NetworkConnectionException("Failed to bind to " + localEndpoint, e, NetworkErrorType.IO_ERROR, localEndpoint);
		}
	}
	
	@Override
	public boolean isActive() {
		return this.socket != null && !this.socket.isClosed();
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> localEndpoint() {
		if (this.socket == null || this.socket.isClosed() || !this.socket.isBound()) {
			return Optional.empty();
		}
		
		InetSocketAddress address = (InetSocketAddress) this.socket.getLocalSocketAddress();
		return Optional.of(IpEndpoint.from(address));
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> remoteEndpoint() {
		return Optional.empty();
	}
	
	/**
	 * Sends a datagram to the specified endpoint.<br>
	 * <p>
	 *     A {@link HostEndpoint} destination is resolved before the datagram is sent,<br>
	 *     because a datagram carries a literal address rather than a name.<br>
	 *     Unlike TLS, UDP has no use for the hostname itself, so nothing is lost by resolving it here.
	 * </p>
	 *
	 * @param destination The destination endpoint
	 * @param data The data to send
	 * @throws NullPointerException If destination or data is null
	 * @throws NetworkConnectionException If the destination cannot be resolved, if sending fails, or if data exceeds buffer size
	 */
	public void send(@NonNull Endpoint destination, byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(destination, "Destination must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		NetworkUtils.validateMessageSize(data, this.config.bufferSize(), destination);
		
		this.ensureSocketCreated();
		
		InetSocketAddress address = destination.toInetSocketAddress();
		if (address.isUnresolved()) {
			UnknownHostException cause = new UnknownHostException(address.getHostString());
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.HOST_UNREACHABLE, "Failed to resolve " + destination, cause);
			throw new NetworkConnectionException("Failed to resolve " + destination, cause, NetworkErrorType.HOST_UNREACHABLE, destination);
		}
		
		try {
			DatagramPacket packet = new DatagramPacket(data, data.length, address);
			this.socket.send(packet);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to send datagram to " + destination, e);
			throw new NetworkConnectionException("Failed to send datagram to " + destination, e, NetworkErrorType.IO_ERROR, destination);
		}
	}
	
	@Override
	public void send(@NonNull UdpDatagram datagram) throws NetworkConnectionException {
		Objects.requireNonNull(datagram, "Datagram must not be null");
		this.send(datagram.endpoint(), datagram.data());
	}
	
	@Override
	public @NonNull UdpDatagram receive() throws NetworkConnectionException {
		return this.receive(this.config.bufferSize());
	}
	
	@Override
	public @NonNull UdpDatagram receive(int maxBytes) throws NetworkConnectionException {
		if (maxBytes < 1) {
			throw new IllegalArgumentException("Max bytes must be at least 1: " + maxBytes);
		}
		
		this.ensureSocketBound();
		
		byte[] buffer = new byte[maxBytes];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try {
			this.socket.receive(packet);
			
			InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();
			IpEndpoint sourceEndpoint = IpEndpoint.from(address);
			
			byte[] data = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
			
			return new UdpDatagram(sourceEndpoint, data);
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Receive timed out", NetworkErrorType.READ_TIMEOUT, this.config.receiveTimeout());
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to receive datagram", e);
			throw new NetworkConnectionException("Failed to receive datagram", e, NetworkErrorType.IO_ERROR);
		}
	}
	
	@Override
	public void close() {
		if (this.socket != null && !this.socket.isClosed()) {
			this.socket.close();
		}
	}
	
	//region Helper methods
	
	/**
	 * Ensures that the socket is created before performing send operations.<br>
	 * @throws NetworkConnectionException If the socket cannot be created
	 */
	private void ensureSocketCreated() throws NetworkConnectionException {
		if (this.socket == null || this.socket.isClosed()) {
			try {
				this.socket = new DatagramSocket(null);
				this.socket.setReuseAddress(this.config.reuseAddress());
				this.socket.setBroadcast(this.config.broadcast());
			} catch (SocketException e) {
				NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to create socket", e);
				throw new NetworkConnectionException("Failed to create socket", e, NetworkErrorType.IO_ERROR);
			}
		}
	}
	
	/**
	 * Ensures that the socket is bound before performing receive operations.<br>
	 * @throws NetworkConnectionException If the socket is not bound
	 */
	private void ensureSocketBound() throws NetworkConnectionException {
		if (this.socket == null || this.socket.isClosed()) {
			throw new NetworkConnectionException("Client is not bound", NetworkErrorType.NOT_CONNECTED);
		}
		
		if (!this.socket.isBound()) {
			throw new NetworkConnectionException("Client is not bound", NetworkErrorType.NOT_CONNECTED);
		}
	}
	//endregion
}
