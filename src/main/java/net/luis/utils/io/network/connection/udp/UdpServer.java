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
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
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
	
	private final @NonNull IpEndpoint bindEndpoint;
	private final @NonNull UdpServerConfig config;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private volatile DatagramSocket socket;
	private volatile ExecutorService executor;
	private volatile Thread acceptThread;
	
	/**
	 * Constructs a new UDP server with the specified bind endpoint and default configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @throws NullPointerException If bindEndpoint is null
	 */
	public UdpServer(@NonNull IpEndpoint bindEndpoint) {
		this(bindEndpoint, UdpServerConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new UDP server with the specified bind endpoint and configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param config The server configuration
	 * @throws NullPointerException If bindEndpoint or config is null
	 */
	public UdpServer(@NonNull IpEndpoint bindEndpoint, @NonNull UdpServerConfig config) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
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
			this.handleError(NetworkErrorType.ADDRESS_IN_USE, "Address already in use: " + this.bindEndpoint, e);
		} catch (IOException e) {
			this.running.set(false);
			this.handleError(NetworkErrorType.IO_ERROR, "Failed to start server on " + this.bindEndpoint, e);
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
		
		this.shutdownExecutor();
	}
	
	@Override
	public boolean isRunning() {
		return this.running.get() && this.socket != null && !this.socket.isClosed();
	}
	
	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		if (this.socket != null && this.socket.isBound()) {
			InetSocketAddress address = (InetSocketAddress) this.socket.getLocalSocketAddress();
			return this.createEndpoint(address);
		}
		return this.bindEndpoint;
	}
	
	/**
	 * Sends a datagram to the specified endpoint.<br>
	 *
	 * @param destination The destination endpoint
	 * @param data The data to send
	 * @throws NullPointerException If destination or data is null
	 * @throws NetworkConnectionException If sending fails
	 */
	public void send(@NonNull IpEndpoint destination, byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(destination, "Destination must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		
		if (!this.isRunning()) {
			throw new NetworkConnectionException("Server is not running", NetworkErrorType.SOCKET_CLOSED);
		}
		
		try {
			DatagramPacket packet = new DatagramPacket(data, data.length, destination.toInetSocketAddress());
			this.socket.send(packet);
		} catch (IOException e) {
			this.handleError(NetworkErrorType.IO_ERROR, "Failed to send datagram to " + destination, e);
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
	private void acceptLoop() {
		byte[] buffer = new byte[this.config.bufferSize()];
		
		while (this.running.get() && !Thread.currentThread().isInterrupted()) {
			try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				this.socket.receive(packet);
				
				InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();
				IpEndpoint sourceEndpoint = this.createEndpoint(address);
				
				byte[] data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
				
				UdpDatagram datagram = new UdpDatagram(sourceEndpoint, data);
				
				if (this.config.onMessage() != null) {
					this.executor.submit(() -> {
						try {
							this.config.onMessage().handle(this, datagram, data);
						} catch (Exception e) {
							this.handleError(NetworkErrorType.IO_ERROR, "Error in message handler", e);
						}
					});
				}
			} catch (SocketException e) {
				if (this.running.get()) {
					this.handleError(NetworkErrorType.SOCKET_CLOSED, "Socket closed unexpectedly", e);
				}
				break;
			} catch (IOException e) {
				if (this.running.get()) {
					this.handleError(NetworkErrorType.IO_ERROR, "Error receiving datagram", e);
				}
			}
		}
	}
	
	private @NonNull IpEndpoint createEndpoint(@NonNull InetSocketAddress address) {
		IpAddress<?> ipAddress;
		if (address.getAddress() instanceof Inet4Address inet4) {
			ipAddress = Ipv4Address.from(inet4);
		} else {
			ipAddress = Ipv6Address.from((Inet6Address) address.getAddress());
		}
		return new IpEndpoint(ipAddress, address.getPort());
	}
	
	private void handleError(@NonNull NetworkErrorType errorType, @NonNull String message, @NonNull Throwable cause) {
		if (this.config.onError() != null) {
			this.config.onError().handle(errorType, message, cause);
		}
	}
	
	private void shutdownExecutor() {
		if (this.executor != null && this.config.executorStrategy().ownsExecutor()) {
			this.executor.shutdown();
			
			try {
				if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
					this.executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				this.executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
	//endregion
}
