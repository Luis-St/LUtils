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

package net.luis.utils.io.network.connection.ssl;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.event.ConnectionEvent;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A blocking SSL/TLS server that accepts secure client connections.<br>
 * This class mirrors the plain TCP server but wraps every accepted connection in a TLS session negotiated from the configured {@link SSLContext}.<br>
 * <p>
 *     The TLS handshake for each client is performed on the client's worker thread (not the accept thread),<br>
 *     so a slow or failing handshake does not block other incoming connections.<br>
 *     The {@code onClientConnect} handler is only invoked after a successful handshake.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 8443);
 * SslServerConfig config = SslServerConfig.builder(sslContext)
 *     .executorStrategy(ClientExecutorStrategy.virtualThreads())
 *     .onClientConnect(event -> System.out.println("Client connected: " + event.remoteEndpoint()))
 *     .onMessage((server, connection, data) -> {
 *         connection.send("Echo: ".getBytes());
 *         connection.send(data);
 *     })
 *     .build();
 *
 * try (SslServer server = new SslServer(bindAddress, config)) {
 *     server.start();
 *     // Server runs until stopped or closed
 * }
 * }</pre>
 *
 * @see SslServerConfig
 * @see SslConnection
 *
 * @author Luis-St
 */
public final class SslServer implements NetworkServer {
	
	/**
	 * The endpoint to bind the server to.<br>
	 */
	private final IpEndpoint bindEndpoint;
	/**
	 * The configuration for this server.<br>
	 */
	private final @NonNull SslServerConfig config;
	/**
	 * Whether the server is currently running.<br>
	 */
	private final AtomicBoolean running = new AtomicBoolean(false);
	/**
	 * The set of currently active client connections.<br>
	 */
	private final Set<SslConnection> connections = ConcurrentHashMap.newKeySet();
	/**
	 * The underlying SSL server socket for accepting connections.<br>
	 */
	private volatile SSLServerSocket serverSocket;
	/**
	 * The executor service for handling client connections.<br>
	 */
	private volatile ExecutorService executor;
	/**
	 * The thread that accepts incoming connections.<br>
	 */
	private volatile Thread acceptThread;
	
	/**
	 * Constructs a new SSL server with the specified bind endpoint and configuration.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param config The server configuration
	 * @throws NullPointerException If bind endpoint or config is null
	 */
	public SslServer(@NonNull IpEndpoint bindEndpoint, @NonNull SslServerConfig config) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Creates a new SSL server with the specified configuration and starts it on the specified bind endpoint.<br>
	 * Startup failures are reported to the configured error handler, use {@link #isRunning()} to check whether the server came up.<br>
	 *
	 * @param bindEndpoint The endpoint to bind to
	 * @param config The server configuration
	 * @return The started server
	 * @throws NullPointerException If bind endpoint or config is null
	 */
	public static @NonNull SslServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull SslServerConfig config) {
		Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		Objects.requireNonNull(config, "Config must not be null");
		
		SslServer server = new SslServer(bindEndpoint, config);
		try {
			server.start();
			return server;
		} catch (Throwable e) {
			server.close();
			throw e;
		}
	}
	
	@Override
	public void start() {
		if (this.running.getAndSet(true)) {
			return;
		}
		
		try {
			SSLServerSocket sslServerSocket = (SSLServerSocket) this.config.sslContext().getServerSocketFactory().createServerSocket();
			this.serverSocket = sslServerSocket;
			sslServerSocket.setReuseAddress(true);
			
			if (!this.config.enabledProtocols().isEmpty()) {
				sslServerSocket.setEnabledProtocols(this.config.enabledProtocols().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			}
			if (!this.config.enabledCipherSuites().isEmpty()) {
				sslServerSocket.setEnabledCipherSuites(this.config.enabledCipherSuites().toArray(ArrayUtils.EMPTY_STRING_ARRAY));
			}
			switch (this.config.clientAuth()) {
				case NONE -> {}
				case REQUESTED -> sslServerSocket.setWantClientAuth(true);
				case REQUIRED -> sslServerSocket.setNeedClientAuth(true);
			}
			
			sslServerSocket.bind(this.bindEndpoint.toInetSocketAddress(), this.config.backlog());
			
			this.executor = this.config.executorStrategy().createExecutor();
			
			this.acceptThread = new Thread(this::acceptLoop, "SSLServer-Accept");
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
		
		for (SslConnection connection : this.connections) {
			connection.close();
		}
		this.connections.clear();
		
		if (this.serverSocket != null && !this.serverSocket.isClosed()) {
			try {
				this.serverSocket.close();
			} catch (IOException _) {}
		}
		
		if (this.acceptThread != null) {
			this.acceptThread.interrupt();
		}
		
		NetworkUtils.shutdownExecutor(this.executor, this.config.executorStrategy().ownsExecutor());
	}
	
	@Override
	public boolean isRunning() {
		return this.running.get() && this.serverSocket != null && !this.serverSocket.isClosed();
	}
	
	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		if (this.serverSocket != null && this.serverSocket.isBound()) {
			InetSocketAddress address = (InetSocketAddress) this.serverSocket.getLocalSocketAddress();
			return IpEndpoint.from(address);
		}
		return this.bindEndpoint;
	}
	
	/**
	 * Returns the number of currently connected clients.<br>
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
		
		for (SslConnection connection : this.connections) {
			if (connection.isActive()) {
				try {
					connection.send(data);
				} catch (NetworkConnectionException e) {
					NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Failed to broadcast to " + connection.remoteEndpoint(), e);
				}
			}
		}
	}
	
	@Override
	public void close() {
		this.stop();
	}
	
	//region Helper methods
	
	/**
	 * The main loop that accepts incoming client connections.<br>
	 * This method runs on a dedicated thread and handles new connections.<br>
	 */
	private void acceptLoop() {
		while (this.running.get() && !Thread.currentThread().isInterrupted()) {
			try {
				SSLSocket clientSocket = (SSLSocket) this.serverSocket.accept();
				
				clientSocket.setTcpNoDelay(this.config.tcpNoDelay());
				clientSocket.setKeepAlive(this.config.keepAlive());
				if (!this.config.clientReadTimeout().isZero()) {
					clientSocket.setSoTimeout((int) this.config.clientReadTimeout().toMillis());
				}
				
				SslConnection connection = new SslConnection(clientSocket, this.config.clientBufferSize(), this.config.clientReadTimeout());
				this.connections.add(connection);
				
				if (this.isRunning()) {
					this.executor.submit(() -> this.handleClient(connection));
				} else {
					connection.close();
					this.connections.remove(connection);
					break;
				}
			} catch (SocketException e) {
				if (this.running.get()) {
					NetworkUtils.handleError(this.config.onError(), NetworkErrorType.SOCKET_CLOSED, "Server socket closed unexpectedly", e);
				}
				break;
			} catch (IOException e) {
				if (this.running.get()) {
					NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Error accepting client", e);
				}
			}
		}
	}
	
	/**
	 * Handles communication with a connected client.<br>
	 * This method runs on the executor, performs the TLS handshake, and processes incoming messages.<br>
	 *
	 * @param connection The client connection to handle
	 * @throws NullPointerException If connection is null
	 */
	private void handleClient(@NonNull SslConnection connection) {
		Objects.requireNonNull(connection, "Connection must not be null");
		
		boolean connected = false;
		try {
			connection.startHandshake();
			connected = true;
			
			if (this.config.onClientConnect() != null) {
				ConnectionEvent event = ConnectionEvent.now(connection.localEndpoint(), connection.remoteEndpoint());
				this.config.onClientConnect().handle(event);
			}
			
			while (this.running.get() && connection.isActive()) {
				byte[] data = connection.receive();
				
				if (data.length == 0) {
					break;
				}
				
				if (this.config.onMessage() != null) {
					try {
						this.config.onMessage().handle(this, connection, data);
					} catch (Exception e) {
						NetworkUtils.handleError(this.config.onError(), NetworkErrorType.IO_ERROR, "Error in message handler", e);
					}
				}
			}
		} catch (NetworkConnectionException e) {
			if (e.errorType() != NetworkErrorType.READ_TIMEOUT) {
				NetworkUtils.handleError(this.config.onError(), e.errorType(), "Client error: " + e.getMessage(), e);
			}
		} finally {
			if (connected && this.config.onClientDisconnect() != null && connection.isActive()) {
				try {
					ConnectionEvent event = ConnectionEvent.now(connection.localEndpoint(), connection.remoteEndpoint());
					this.config.onClientDisconnect().handle(event);
				} catch (Exception _) {}
			}
			
			this.connections.remove(connection);
			connection.close();
		}
	}
	//endregion
}
