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

package net.luis.utils.io.network.connection.tcp;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.event.ConnectionEvent;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A blocking TCP server that accepts client connections.<br>
 * This class provides a simple server that dispatches incoming connections to a message handler.
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 8080);
 * TcpServerConfig config = TcpServerConfig.builder()
 *     .executorStrategy(ClientExecutorStrategy.virtualThreads())
 *     .onClientConnect(event -> System.out.println("Client connected: " + event.remoteEndpoint()))
 *     .onMessage((server, connection, data) -> {
 *         System.out.println("Received: " + new String(data));
 *         connection.send("Echo: ".getBytes());
 *         connection.send(data);
 *     })
 *     .build();
 *
 * try (TcpServer server = new TcpServer(bindAddress, config)) {
 *     server.start();
 *     // Server runs until stopped or closed
 * }
 * }</pre>
 *
 * @see TcpServerConfig
 * @see TcpConnection
 *
 * @author Luis-St
 */
public final class TcpServer implements NetworkServer {

	private final @NonNull IpEndpoint bindEndpoint;
	private final @NonNull TcpServerConfig config;
	private volatile ServerSocket serverSocket;
	private volatile ExecutorService executor;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private volatile Thread acceptThread;
	private final Set<TcpConnection> connections = ConcurrentHashMap.newKeySet();

	/**
	 * Constructs a new TCP server with the specified bind endpoint and default configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @throws NullPointerException If bindEndpoint is null
	 */
	public TcpServer(@NonNull IpEndpoint bindEndpoint) {
		this(bindEndpoint, TcpServerConfig.DEFAULT);
	}

	/**
	 * Constructs a new TCP server with the specified bind endpoint and configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param config The server configuration
	 * @throws NullPointerException If bindEndpoint or config is null
	 */
	public TcpServer(@NonNull IpEndpoint bindEndpoint, @NonNull TcpServerConfig config) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}

	@Override
	public void start() {
		if (this.running.getAndSet(true)) {
			return; // Already running
		}

		try {
			this.serverSocket = new ServerSocket();
			this.serverSocket.setReuseAddress(true);
			this.serverSocket.bind(this.bindEndpoint.toInetSocketAddress(), this.config.backlog());

			this.executor = this.config.executorStrategy().createExecutor();

			this.acceptThread = new Thread(this::acceptLoop, "TcpServer-Accept");
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
			return; // Not running
		}

		// Close all client connections
		for (TcpConnection connection : this.connections) {
			connection.close();
		}
		this.connections.clear();

		if (this.serverSocket != null && !this.serverSocket.isClosed()) {
			try {
				this.serverSocket.close();
			} catch (IOException ignored) {
				// Ignore close errors
			}
		}

		if (this.acceptThread != null) {
			this.acceptThread.interrupt();
		}

		this.shutdownExecutor();
	}

	@Override
	public boolean isRunning() {
		return this.running.get() && this.serverSocket != null && !this.serverSocket.isClosed();
	}

	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		if (this.serverSocket != null && this.serverSocket.isBound()) {
			InetSocketAddress address = (InetSocketAddress) this.serverSocket.getLocalSocketAddress();
			return this.createEndpoint(address);
		}
		return this.bindEndpoint;
	}

	/**
	 * Returns the number of currently connected clients.<br>
	 *
	 * @return The number of active connections
	 */
	public int getClientCount() {
		return this.connections.size();
	}

	/**
	 * Broadcasts data to all connected clients.<br>
	 *
	 * @param data The data to broadcast
	 * @throws NullPointerException If data is null
	 */
	public void broadcast(byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");

		for (TcpConnection connection : this.connections) {
			if (connection.isActive()) {
				try {
					connection.send(data);
				} catch (NetworkConnectionException e) {
					this.handleError(NetworkErrorType.IO_ERROR, "Failed to broadcast to " + connection.remoteEndpoint(), e);
				}
			}
		}
	}

	@Override
	public void close() {
		this.stop();
	}

	//region Helper methods
	private void acceptLoop() {
		while (this.running.get() && !Thread.currentThread().isInterrupted()) {
			try {
				Socket clientSocket = this.serverSocket.accept();

				// Configure client socket
				clientSocket.setTcpNoDelay(this.config.tcpNoDelay());
				clientSocket.setKeepAlive(this.config.keepAlive());
				if (!this.config.clientReadTimeout().isZero()) {
					clientSocket.setSoTimeout((int) this.config.clientReadTimeout().toMillis());
				}

				TcpConnection connection = new TcpConnection(clientSocket, this.config.clientBufferSize(), this.config.clientReadTimeout());
				this.connections.add(connection);

				// Fire connect event
				if (this.config.onClientConnect() != null) {
					ConnectionEvent event = ConnectionEvent.now(connection.localEndpoint(), connection.remoteEndpoint());
					this.config.onClientConnect().handle(event);
				}

				// Handle client in separate thread
				this.executor.submit(() -> this.handleClient(connection));
			} catch (SocketException e) {
				if (this.running.get()) {
					this.handleError(NetworkErrorType.SOCKET_CLOSED, "Server socket closed unexpectedly", e);
				}
				break;
			} catch (IOException e) {
				if (this.running.get()) {
					this.handleError(NetworkErrorType.IO_ERROR, "Error accepting client", e);
				}
			}
		}
	}

	private void handleClient(@NonNull TcpConnection connection) {
		try {
			while (this.running.get() && connection.isActive()) {
				byte[] data = connection.receive();

				if (data.length == 0) {
					break; // Connection closed
				}

				if (this.config.onMessage() != null) {
					try {
						this.config.onMessage().handle(this, connection, data);
					} catch (Exception e) {
						this.handleError(NetworkErrorType.IO_ERROR, "Error in message handler", e);
					}
				}
			}
		} catch (NetworkConnectionException e) {
			if (e.errorType() != NetworkErrorType.READ_TIMEOUT) {
				// Only log non-timeout errors
				this.handleError(e.errorType(), "Client error: " + e.getMessage(), e);
			}
		} finally {
			// Fire disconnect event
			if (this.config.onClientDisconnect() != null && connection.isActive()) {
				try {
					ConnectionEvent event = ConnectionEvent.now(connection.localEndpoint(), connection.remoteEndpoint());
					this.config.onClientDisconnect().handle(event);
				} catch (Exception ignored) {
					// Ignore errors in disconnect handler
				}
			}

			this.connections.remove(connection);
			connection.close();
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
