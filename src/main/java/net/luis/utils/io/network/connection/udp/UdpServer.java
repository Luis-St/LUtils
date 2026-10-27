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
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkTimeoutException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * A server that receives datagrams on a {@link UdpPeer} and hands each one to a {@link DatagramHandler}.<br>
 * <p>
 *     The server owns its peer, which is bound when the server starts and closed when it stops.<br>
 *     The receive loop runs on a virtual thread, every datagram is handled on a thread of the configured executor.<br>
 *     Replies and other datagrams are sent through {@link #peer()}.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * IpEndpoint bindAddress = new IpEndpoint(Ipv4Address.ANY, 9999);
 * try (UdpServer server = UdpServer.startOn(bindAddress, (srv, datagram) -> srv.peer().send(datagram))) {
 *     // Server runs until it is stopped or closed
 * }
 * }</pre>
 *
 * @see UdpServerConfig
 * @see UdpPeer
 * @see DatagramHandler
 *
 * @author Luis-St
 */
public final class UdpServer implements NetworkServer {
	
	/**
	 * The endpoint the server binds to.<br>
	 */
	private final IpEndpoint bindEndpoint;
	/**
	 * The configuration of this server.<br>
	 */
	private final UdpServerConfig config;
	/**
	 * The handler every received datagram is handed to.<br>
	 */
	private final DatagramHandler handler;
	/**
	 * The tracker of the running handlers, used by a graceful stop.<br>
	 */
	private final TaskTracker tracker = new TaskTracker();
	/**
	 * Whether the server is running.<br>
	 */
	private volatile boolean running;
	/**
	 * The peer the server receives on, or null if the server was never started.<br>
	 */
	private volatile @Nullable UdpPeer peer;
	/**
	 * The executor the datagrams are handled on.<br>
	 */
	private volatile @Nullable ExecutorService executor;
	
	/**
	 * Constructs a new UDP server with the default configuration.<br>
	 * The server is not started.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param handler The handler every received datagram is handed to
	 * @throws NullPointerException If the bind endpoint or the handler is null
	 */
	public UdpServer(@NonNull IpEndpoint bindEndpoint, @NonNull DatagramHandler handler) {
		this(bindEndpoint, UdpServerConfig.DEFAULT, handler);
	}
	
	/**
	 * Constructs a new UDP server with the given configuration.<br>
	 * The server is not started.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param config The configuration of the server
	 * @param handler The handler every received datagram is handed to
	 * @throws NullPointerException If the bind endpoint, the configuration or the handler is null
	 */
	public UdpServer(@NonNull IpEndpoint bindEndpoint, @NonNull UdpServerConfig config, @NonNull DatagramHandler handler) {
		this.bindEndpoint = Objects.requireNonNull(bindEndpoint, "Bind endpoint must not be null");
		this.config = Objects.requireNonNull(config, "Config must not be null");
		this.handler = Objects.requireNonNull(handler, "Handler must not be null");
	}
	
	/**
	 * Creates a new UDP server with the default configuration and starts it.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param handler The handler every received datagram is handed to
	 * @return The started server
	 * @throws NullPointerException If the bind endpoint or the handler is null
	 * @throws NetworkConnectionException If the server cannot be bound or started
	 */
	public static @NonNull UdpServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull DatagramHandler handler) throws NetworkConnectionException {
		return startOn(bindEndpoint, UdpServerConfig.DEFAULT, handler);
	}
	
	/**
	 * Creates a new UDP server with the given configuration and starts it.<br>
	 *
	 * @param bindEndpoint The endpoint the server binds to
	 * @param config The configuration of the server
	 * @param handler The handler every received datagram is handed to
	 * @return The started server
	 * @throws NullPointerException If the bind endpoint, the configuration or the handler is null
	 * @throws NetworkConnectionException If the server cannot be bound or started
	 */
	public static @NonNull UdpServer startOn(@NonNull IpEndpoint bindEndpoint, @NonNull UdpServerConfig config, @NonNull DatagramHandler handler) throws NetworkConnectionException {
		UdpServer server = new UdpServer(bindEndpoint, config, handler);
		server.start();
		return server;
	}
	
	/**
	 * Returns the configuration of this server.<br>
	 * @return The configuration
	 */
	public @NonNull UdpServerConfig config() {
		return this.config;
	}
	
	/**
	 * Returns the peer this server receives on, which is also used to send replies.<br>
	 *
	 * @return The peer of the server
	 * @throws IllegalStateException If the server was never started
	 */
	public @NonNull UdpPeer peer() {
		UdpPeer peer = this.peer;
		if (peer == null) {
			throw new IllegalStateException("Server was never started");
		}
		return peer;
	}
	
	@Override
	public boolean isRunning() {
		UdpPeer peer = this.peer;
		return this.running && peer != null && peer.isActive();
	}
	
	@Override
	public @NonNull IpEndpoint boundEndpoint() {
		UdpPeer peer = this.peer;
		if (peer != null) {
			return peer.localEndpoint().orElse(this.bindEndpoint);
		}
		return this.bindEndpoint;
	}
	
	@Override
	public synchronized void start() throws NetworkConnectionException {
		if (this.running) {
			return;
		}
		
		UdpPeer peer = UdpPeer.bindTo(this.bindEndpoint, this.config.peer());
		this.peer = peer;
		this.executor = this.config.executorStrategy().createExecutor();
		this.running = true;
		Thread.ofVirtual().name("UdpServer-Receive").start(() -> this.receiveLoop(peer));
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
		this.tracker.awaitIdle(gracePeriod);
		
		UdpPeer peer = this.peer;
		if (peer != null) {
			peer.close();
		}
		NetworkUtils.shutdownExecutor(this.executor, this.config.executorStrategy().ownsExecutor());
		this.executor = null;
	}
	
	@Override
	public void close() {
		this.stop();
	}
	
	/**
	 * Receives datagrams on the given peer until the server is stopped and hands them to the handler.<br>
	 * <p>
	 *     A graceful stop only clears the running flag, so the loop may still be blocked in a receive.<br>
	 *     Datagrams that arrive after the stop was requested are dropped.
	 * </p>
	 *
	 * @param peer The peer to receive on
	 * @throws NullPointerException If the peer is null
	 */
	private void receiveLoop(@NonNull UdpPeer peer) {
		Objects.requireNonNull(peer, "Peer must not be null");
		
		while (this.running && peer.isActive()) {
			Optional<UdpDatagram> datagram;
			try {
				datagram = peer.receive();
			} catch (NetworkTimeoutException e) {
				continue;
			} catch (NetworkConnectionException e) {
				if (this.running) {
					NetworkUtils.reportError(this.config.onError(), peer, e);
				}
				continue;
			}
			
			ExecutorService executor = this.executor;
			if (datagram.isEmpty() || !this.running || executor == null) {
				break;
			}
			this.dispatch(executor, peer, datagram.get());
		}
	}
	
	/**
	 * Hands the given datagram to the handler on a thread of the given executor.<br>
	 *
	 * @param executor The executor to run the handler on
	 * @param peer The peer the datagram was received on
	 * @param datagram The received datagram
	 * @throws NullPointerException If the executor, the peer or the datagram is null
	 */
	private void dispatch(@NonNull ExecutorService executor, @NonNull UdpPeer peer, @NonNull UdpDatagram datagram) {
		Objects.requireNonNull(executor, "Executor must not be null");
		Objects.requireNonNull(peer, "Peer must not be null");
		Objects.requireNonNull(datagram, "Datagram must not be null");
		
		this.tracker.started();
		try {
			executor.execute(() -> {
				try {
					this.handler.handle(this, datagram);
				} catch (Exception e) {
					NetworkUtils.reportError(this.config.onError(), peer, e);
				} finally {
					this.tracker.finished();
				}
			});
		} catch (RejectedExecutionException e) {
			this.tracker.finished();
		}
	}
}
