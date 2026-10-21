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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.SslServer;
import net.luis.utils.io.network.connection.tcp.TcpServer;
import net.luis.utils.io.network.connection.tcp.TcpServerConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Base class of the servers that accept stream connections.<br>
 * <p>
 *     The server accepts connections on a virtual thread and serves every connection on a thread of the configured executor.<br>
 *     For each accepted socket, the subclass opens the connection, which may include a TLS handshake,
 *     and the connection is handed to the {@link ConnectionHandler} of the server.<br>
 *     Once the handler returns, the server closes the connection.
 * </p>
 * <p>
 *     Failures that occur on the threads of the server are reported to the error handler of the configuration.<br>
 *     Failures while the server is stopping are not reported, because they are caused by the stop itself.
 * </p>
 *
 * @see TcpServer
 * @see SslServer
 * @see ConnectionHandler
 *
 * @author Luis-St
 *
 * @param <S> The type of the server itself
 * @param <C> The type of the connections the server accepts
 */
public abstract sealed class StreamServer<S extends StreamServer<S, C>, C extends Connection> implements NetworkServer permits TcpServer, SslServer {
	
	/**
	 * The maximum time a stop waits for the accept thread to finish.<br>
	 */
	private static final Duration ACCEPT_THREAD_JOIN_TIMEOUT = Duration.ofSeconds(5);
	
	/**
	 * The endpoint the server binds to.<br>
	 */
	private final IpEndpoint bindEndpoint;
	/**
	 * The transport configuration of the server.<br>
	 */
	private final TcpServerConfig transport;
	/**
	 * The handler every accepted connection is handed to.<br>
	 */
	private final ConnectionHandler<S, C> handler;
	/**
	 * The connections that are currently handled.<br>
	 */
	private final Set<C> connections = ConcurrentHashMap.newKeySet();
	/**
	 * The accepted sockets that are currently served, including those whose connection is still being opened.<br>
	 */
	private final Set<Socket> sockets = ConcurrentHashMap.newKeySet();
	/**
	 * The tracker of the running handlers, used by a graceful stop.<br>
	 */
	private final TaskTracker tracker = new TaskTracker();
	/**
	 * Whether the server is running.<br>
	 */
	private volatile boolean running;
	/**
	 * The server socket that accepts connections.<br>
	 */
	private volatile @Nullable ServerSocket serverSocket;
	/**
	 * The executor the connections are served on.<br>
	 */
	private volatile @Nullable ExecutorService executor;
	/**
	 * The thread that accepts connections, or null if the server is not running.<br>
	 */
	private volatile @Nullable Thread acceptThread;
	
	/**
	 * Constructs a new stream server.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param transport The transport configuration of the server
	 * @param handler The handler every accepted connection is handed to
	 * @throws NullPointerException If the bind endpoint, the transport configuration or the handler is null
	 */
	protected StreamServer(@NonNull IpEndpoint bindEndpoint, @NonNull TcpServerConfig transport, @NonNull ConnectionHandler<S, C> handler) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.transport = Objects.requireNonNull(transport, "Transport config must not be null");
		this.handler = Objects.requireNonNull(handler, "Handler must not be null");
	}
	
	@Override
	public boolean isRunning() {
		ServerSocket serverSocket = this.serverSocket;
		return this.running && serverSocket != null && !serverSocket.isClosed();
	}
	
	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		ServerSocket serverSocket = this.serverSocket;
		if (serverSocket != null && serverSocket.isBound() && !serverSocket.isClosed()) {
			return IpEndpoint.from((InetSocketAddress) serverSocket.getLocalSocketAddress());
		}
		return this.bindEndpoint;
	}
	
	@Override
	public synchronized void start() throws NetworkConnectionException {
		if (this.running) {
			return;
		}
		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(this.bindEndpoint.toInetSocketAddress(), this.transport.backlog());
		} catch (BindException e) {
			NetworkUtils.closeQuietly(serverSocket);
			throw NetworkUtils.mapBindFailure(e, "Failed to bind to " + this.bindEndpoint, this.bindEndpoint);
		} catch (IOException e) {
			NetworkUtils.closeQuietly(serverSocket);
			throw new NetworkConnectionException("Failed to start server on " + this.bindEndpoint, e, NetworkErrorType.IO_ERROR, this.bindEndpoint);
		}
		
		ServerSocket acceptSocket = serverSocket;
		this.serverSocket = acceptSocket;
		this.executor = this.transport.executorStrategy().createExecutor();
		this.running = true;
		this.acceptThread = Thread.ofVirtual().name(this.getClass().getSimpleName() + "-Accept").start(() -> this.acceptLoop(acceptSocket));
	}
	
	@Override
	public void stop() {
		this.stop(Duration.ZERO);
	}
	
	@Override
	public synchronized void stop(@NonNull Duration gracePeriod) {
		NetworkUtils.validateGracePeriod(gracePeriod);
		if (!this.running) {
			return;
		}
		
		this.running = false;
		NetworkUtils.closeQuietly(this.serverSocket);
		this.awaitAcceptThread();
		this.tracker.awaitIdle(gracePeriod);
		
		for (Socket socket : this.sockets) {
			NetworkUtils.closeQuietly(socket);
		}
		NetworkUtils.shutdownExecutor(this.executor, this.transport.executorStrategy().ownsExecutor());
		this.executor = null;
	}
	
	/**
	 * Returns a snapshot of the connections that are currently handled.<br>
	 * @return An unmodifiable set of the current connections
	 */
	public @NonNull Set<C> connections() {
		return Set.copyOf(this.connections);
	}
	
	/**
	 * Sends the given message to every active connection of this server.<br>
	 * A failed send does not stop the broadcast, it is reported to the error handler of the configuration.<br>
	 *
	 * @param data The message to send
	 * @throws NullPointerException If the data is null
	 */
	public void broadcast(byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		
		for (C connection : this.connections) {
			if (connection.isActive()) {
				try {
					connection.send(data);
				} catch (NetworkConnectionException e) {
					NetworkUtils.reportError(this.transport.onError(), connection, e);
				}
			}
		}
	}
	
	@Override
	public void close() {
		this.stop();
	}
	
	/**
	 * Returns this server as its own type.<br>
	 * @return This server
	 */
	protected abstract @NonNull S self();
	
	/**
	 * Opens the connection for an accepted socket.<br>
	 * This method runs on the thread that serves the connection, so a slow handshake does not block other connections.<br>
	 *
	 * @param socket The accepted socket, with the socket options of the configuration already applied
	 * @return The opened connection
	 * @throws NullPointerException If the socket is null
	 * @throws NetworkConnectionException If the connection cannot be opened
	 */
	protected abstract @NonNull C open(@NonNull Socket socket) throws NetworkConnectionException;
	
	/**
	 * Waits until the accept thread has finished, at most for {@link #ACCEPT_THREAD_JOIN_TIMEOUT}.<br>
	 * <p>
	 *     Closing the server socket only makes the accept thread leave its loop.<br>
	 *     The listening socket is only released once the accept thread has left its blocking accept call.<br>
	 *     Waiting for it ensures that no connection is accepted after the stop returns.<br>
	 *     If the current thread is interrupted while waiting, the wait ends early and the interrupt flag is restored.
	 * </p>
	 */
	private void awaitAcceptThread() {
		Thread thread = this.acceptThread;
		this.acceptThread = null;
		if (thread == null || thread == Thread.currentThread()) {
			return;
		}
		
		try {
			thread.join(ACCEPT_THREAD_JOIN_TIMEOUT);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Accepts connections on the given server socket until the server is stopped.<br>
	 *
	 * @param serverSocket The server socket to accept connections on
	 * @throws NullPointerException If the server socket is null
	 */
	private void acceptLoop(@NonNull ServerSocket serverSocket) {
		Objects.requireNonNull(serverSocket, "Server socket must not be null");
		
		while (this.running && !serverSocket.isClosed()) {
			Socket socket;
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				if (this.running && !serverSocket.isClosed()) {
					NetworkUtils.reportError(this.transport.onError(), null, new NetworkConnectionException("Failed to accept connection", e, NetworkErrorType.IO_ERROR, this.bindEndpoint));
				}
				continue;
			}
			
			ExecutorService executor = this.executor;
			if (!this.running || executor == null) {
				NetworkUtils.closeQuietly(socket);
				break;
			}
			
			this.sockets.add(socket);
			this.tracker.started();
			try {
				executor.execute(() -> this.serve(socket));
			} catch (RejectedExecutionException e) {
				this.sockets.remove(socket);
				NetworkUtils.closeQuietly(socket);
				this.tracker.finished();
			}
		}
	}
	
	/**
	 * Serves an accepted socket until the handler returns.<br>
	 * The connection is opened, registered and handed to the handler, afterward it is unregistered and closed.<br>
	 *
	 * @param socket The accepted socket
	 * @throws NullPointerException If the socket is null
	 */
	private void serve(@NonNull Socket socket) {
		Objects.requireNonNull(socket, "Socket must not be null");
		
		C connection = null;
		try {
			socket.setTcpNoDelay(this.transport.tcpNoDelay());
			socket.setKeepAlive(this.transport.keepAlive());
			connection = this.open(socket);
			this.connections.add(connection);
			this.handler.handle(this.self(), connection);
		} catch (Exception e) {
			if (this.running) {
				NetworkUtils.reportError(this.transport.onError(), connection, e);
			}
		} finally {
			if (connection != null) {
				this.connections.remove(connection);
				connection.close();
			}
			this.sockets.remove(socket);
			NetworkUtils.closeQuietly(socket);
			this.tracker.finished();
		}
	}
}
