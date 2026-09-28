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
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A UDP server that receives datagrams on a bound port.<br>
 * This class provides a simple blocking server that dispatches incoming datagrams to a message handler.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 9999);
 * UdpServerConfig config = UdpServerConfig.builder()
 *     .onMessage((server, datagram, data) -> {
 *         System.out.println("From " + datagram.endpoint() + ": " + new String(data));
 *         server.send(datagram.endpoint(), "Pong".getBytes());
 *     })
 *     .build();
 *
 * try (UdpServer server = new UdpServer(bindAddress, config)) {
 *     server.start();
 *     Thread.sleep(60000); // Run for 1 minute
 *     server.stop();
 * }
 * }</pre>
 *
 * @see UdpServerConfig
 * @see UdpDatagram
 *
 * @author Luis-St
 */
public final class UdpServer implements NetworkServer {
	
	/**
	 * The endpoint to bind the server to.<br>
	 */
	private final IpEndpoint bindEndpoint;
	/**
	 * The configuration for this server.<br>
	 */
	private final UdpServerConfig config;
	/**
	 * Whether the server is currently running.<br>
	 */
	private final AtomicBoolean running = new AtomicBoolean(false);
	/**
	 * The underlying datagram socket for receiving packets.<br>
	 */
	private volatile DatagramSocket socket;
	/**
	 * The executor service for handling incoming datagrams.<br>
	 */
	private volatile ExecutorService executor;
	/**
	 * The thread that receives incoming datagrams.<br>
	 */
	private volatile Thread acceptThread;
	
	/**
	 * Constructs a new UDP server with the specified bind endpoint and default configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @throws NullPointerException If bind endpoint is null
	 */
	public UdpServer(@NonNull IpEndpoint bindEndpoint) {
		this(bindEndpoint, UdpServerConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new UDP server with the specified bind endpoint and configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param config The server configuration
	 * @throws NullPointerException If bind endpoint or config is null
	 */
	public UdpServer(@NonNull IpEndpoint bindEndpoint, @NonNull UdpServerConfig config) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Creates a new UDP server with default configuration and starts it on the specified bind endpoint.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @return The started server
	 * @throws NullPointerException If bind endpoint is null
	 */
	public static @NonNull UdpServer startOn(@NonNull IpEndpoint bindEndpoint) {
		return startOn(bindEndpoint, UdpServerConfig.DEFAULT);
	}
	
	/**
	 * Creates a new UDP server with the specified configuration and starts it on the specified bind endpoint.<br>
	 * Startup failures are reported to the configured error handler, use {@link #isRunning()} to check whether the server came up.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param config The server configuration
	 * @return The started server
	 * @throws NullPointerException If bind endpoint or config is null
	 */
	public static @NonNull UdpServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull UdpServerConfig config) {
		Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		UdpServer server = new UdpServer(bindEndpoint, config);
		try {
			server.start();
			return server;
		} catch (Throwable e) {
			server.close();
			throw e;
		}
	}
	
	@Override
	public boolean isRunning() {
		return this.running.get() && this.socket != null && !this.socket.isClosed();
	}
	
	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		if (this.socket != null && this.socket.isBound()) {
			InetSocketAddress address = (InetSocketAddress) this.socket.getLocalSocketAddress();
			return IpEndpoint.from(address);
		}
		return this.bindEndpoint;
	}
	
	@Override
	public void start() {
		if (this.running.getAndSet(true)) {
			return;
		}
		
		try {
			this.socket = new DatagramSocket(null);
			this.socket.setReuseAddress(this.config.reuseAddress());
			this.socket.setBroadcast(this.config.broadcast());
			this.socket.bind(this.bindEndpoint.toInetSocketAddress());
			
			this.executor = this.config.executorStrategy().createExecutor();
			
			this.acceptThread = new Thread(this::acceptLoop, "UdpServer-Accept");
			this.acceptThread.setDaemon(true);
			this.acceptThread.start();
		} catch (BindException e) {
			this.running.set(false);
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.ADDRESS_IN_USE, "Address already in use: " + this.bindEndpoint, e);
		} catch (IOException e) {
			this.running.set(false);
			NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to start server on " + this.bindEndpoint, e);
		}
	}
	
	@Override
	public void stop() {
		if (!this.running.getAndSet(false)) {
			return;
		}
		
		if (this.socket != null && !this.socket.isClosed()) {
			this.socket.close();
		}
		
		if (this.acceptThread != null) {
			this.acceptThread.interrupt();
		}
		
		NetworkUtils.shutdownExecutor(this.executor, this.config.executorStrategy().ownsExecutor());
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
	 * @throws NetworkConnectionException If the destination cannot be resolved, if the server is not running, if sending fails, or if data exceeds buffer size
	 */
	public void send(@NonNull Endpoint destination, byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(destination, "Destination must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		NetworkUtils.validateMessageSize(data, this.config.bufferSize(), destination);
		
		if (!this.isRunning()) {
			throw new NetworkConnectionException("Server is not running", NetworkErrorType.SOCKET_CLOSED);
		}
		
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
	
	@Override
	public void close() {
		this.stop();
	}
	
	//region Helper methods
	
	/**
	 * The main loop that receives incoming datagrams.<br>
	 * This method runs on a dedicated thread and dispatches datagrams to handlers.<br>
	 */
	private void acceptLoop() {
		byte[] buffer = new byte[this.config.bufferSize()];
		
		while (this.running.get() && !Thread.currentThread().isInterrupted()) {
			try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				this.socket.receive(packet);
				
				InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();
				IpEndpoint sourceEndpoint = IpEndpoint.from(address);
				
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
				
				UdpDatagram datagram = new UdpDatagram(sourceEndpoint, data);
				
				if (this.config.onMessage() != null) {
					if (!this.isRunning()) {
						break;
					}
					
					this.executor.submit(() -> {
						try {
							this.config.onMessage().handle(this, datagram, data);
						} catch (Exception e) {
							NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Error in message handler", e);
						}
					});
				}
			} catch (SocketException e) {
				if (this.running.get()) {
					NetworkUtils.handleError(this.config.onError(), NetworkErrorType.SOCKET_CLOSED, "Socket closed unexpectedly", e);
				}
				break;
			} catch (IOException e) {
				if (this.running.get()) {
					NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Error receiving datagram", e);
				}
			}
		}
	}
	//endregion
}
