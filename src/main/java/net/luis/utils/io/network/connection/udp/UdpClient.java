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

import net.luis.utils.io.network.IpEndpoint;
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
public final class UdpClient implements NetworkClient {
	
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
	 * Binds the client to a local endpoint.<br>
	 * This must be called before receiving datagrams.<br>
	 *
	 * @param localEndpoint The local endpoint to bind to
	 * @throws NullPointerException If local endpoint is null
	 * @throws NetworkConnectionException If binding fails
	 */
	public void bind(@NonNull IpEndpoint localEndpoint) throws NetworkConnectionException {
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
	
	/**
	 * Sends a datagram to the specified endpoint.<br>
	 *
	 * @param destination The destination endpoint
	 * @param data The data to send
	 * @throws NullPointerException If destination or data is null
	 * @throws NetworkConnectionException If sending fails or data exceeds buffer size
	 */
	public void send(@NonNull IpEndpoint destination, byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(destination, "Destination must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		this.validateMessageSize(data, destination);
		
		this.ensureSocketCreated();
		
		try {
			DatagramPacket packet = new DatagramPacket(data, data.length, destination.toInetSocketAddress());
			this.socket.send(packet);
		} catch (IOException e) {
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to send datagram to " + destination, e);
			throw new NetworkConnectionException("Failed to send datagram to " + destination, e, NetworkErrorType.IO_ERROR, destination);
		}
	}
	
	/**
	 * Sends a datagram.<br>
	 *
	 * @param datagram The datagram to send
	 * @throws NullPointerException If datagram is null
	 * @throws NetworkConnectionException If sending fails
	 */
	public void send(@NonNull UdpDatagram datagram) throws NetworkConnectionException {
		Objects.requireNonNull(datagram, "Datagram must not be null");
		this.send(datagram.endpoint(), datagram.data());
	}
	
	/**
	 * Receives a datagram (blocking).<br>
	 * Uses the buffer size from the configuration.<br>
	 *
	 * @return The received datagram
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
	public @NonNull UdpDatagram receive() throws NetworkConnectionException {
		return this.receive(this.config.bufferSize());
	}
	
	/**
	 * Receives a datagram with a custom buffer size (blocking).<br>
	 *
	 * @param maxBytes The maximum number of bytes to receive
	 * @return The received datagram
	 * @throws IllegalArgumentException If maxBytes is less than 1
	 * @throws NetworkConnectionException If receiving fails
	 * @throws NetworkTimeoutException If the receive times out
	 */
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
	public void close() {
		if (this.socket != null && !this.socket.isClosed()) {
			this.socket.close();
		}
	}
	
	//region Helper methods
	
	/**
	 * Validates that the message size does not exceed the configured buffer size.<br>
	 *
	 * @param data The data to validate
	 * @param destination The destination endpoint for error reporting
	 * @throws NetworkConnectionException If the data exceeds the buffer size
	 */
	private void validateMessageSize(byte @NonNull [] data, @NonNull IpEndpoint destination) throws NetworkConnectionException {
		if (data.length > this.config.bufferSize()) {
			throw new NetworkConnectionException("Message size " + data.length + " exceeds buffer size " + this.config.bufferSize(), NetworkErrorType.MESSAGE_TOO_LARGE, destination);
		}
	}
	
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
