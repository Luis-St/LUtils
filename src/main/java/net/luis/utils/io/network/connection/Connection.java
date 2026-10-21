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

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.SslConnection;
import net.luis.utils.io.network.connection.tcp.TcpConnection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A stream based connection between two endpoints.<br>
 * <p>
 *     A connection is the same type on both sides of a communication.<br>
 *     A client opens it with {@link TcpConnection#connectTo} or {@link SslConnection#connectTo},
 *     a stream server accepts it and hands it to its {@link ConnectionHandler}.
 * </p>
 * <p>
 *     Messages are delimited on the stream by the {@link Framing} of the connection.<br>
 *     Sends and receives are thread safe, concurrent sends never interleave and concurrent receives never split a message.<br>
 *     The raw streams returned by {@link #getInputStream()} and {@link #getOutputStream()} bypass the framing and this locking,
 *     so they are meant for protocols that delimit messages themselves and must not be mixed with concurrent sends.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (TcpConnection connection = TcpConnection.connectTo(endpoint)) {
 *     connection.send("Hello".getBytes());
 *     connection.receive().ifPresent(response -> System.out.println(new String(response)));
 * }
 * }</pre>
 *
 * @see TcpConnection
 * @see SslConnection
 * @see Framing
 *
 * @author Luis-St
 */
public abstract sealed class Connection implements NetworkClient<byte[]> permits TcpConnection, SslConnection {
	
	/**
	 * The underlying socket of this connection.<br>
	 */
	private final Socket socket;
	/**
	 * The streams of the socket, so that every caller gets the same instance.<br>
	 */
	private final SocketStreams streams;
	/**
	 * The framing that delimits messages on the stream.<br>
	 */
	private final Framing framing;
	/**
	 * The maximum length of a message in bytes.<br>
	 */
	private final int maxMessageSize;
	/**
	 * The read timeout of the connection, or null if a receive waits forever.<br>
	 */
	private final @Nullable Duration readTimeout;
	/**
	 * The local endpoint of the connection, captured when it was created.<br>
	 */
	private final IpEndpoint localEndpoint;
	/**
	 * The remote endpoint of the connection, captured when it was created.<br>
	 */
	private final IpEndpoint remoteEndpoint;
	/**
	 * The lock that serializes sends, so that concurrent messages never interleave on the stream.<br>
	 */
	private final ReentrantLock writeLock = new ReentrantLock();
	/**
	 * The lock that serializes receives, so that concurrent receives never split a message.<br>
	 */
	private final ReentrantLock readLock = new ReentrantLock();
	/**
	 * Whether the socket was handed over to another connection, for example by a TLS upgrade.<br>
	 */
	private volatile boolean released;
	
	/**
	 * Constructs a new connection over the given connected socket.<br>
	 * The read timeout is applied to the socket.<br>
	 *
	 * @param socket The connected socket
	 * @param framing The framing that delimits messages on the stream
	 * @param maxMessageSize The maximum length of a message in bytes
	 * @param readTimeout The read timeout, or null if a receive waits forever
	 * @throws NullPointerException If the socket or the framing is null
	 * @throws IllegalArgumentException If the socket is not connected, the max message size is less than 1 or the read timeout is not positive
	 * @throws NetworkConnectionException If the read timeout cannot be applied to the socket
	 */
	protected Connection(@NonNull Socket socket, @NonNull Framing framing, int maxMessageSize, @Nullable Duration readTimeout) throws NetworkConnectionException {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
		this.framing = Objects.requireNonNull(framing, "Framing must not be null");
		if (socket.isClosed() || !socket.isConnected()) {
			throw new IllegalArgumentException("Socket must be connected");
		}
		if (maxMessageSize < 1) {
			throw new IllegalArgumentException("Max message size must be at least 1: " + maxMessageSize);
		}
		NetworkUtils.validateTimeout(readTimeout, "Read timeout");
		
		this.maxMessageSize = maxMessageSize;
		this.readTimeout = readTimeout;
		this.streams = new SocketStreams(socket);
		this.localEndpoint = IpEndpoint.from((InetSocketAddress) socket.getLocalSocketAddress());
		this.remoteEndpoint = IpEndpoint.from((InetSocketAddress) socket.getRemoteSocketAddress());
		
		try {
			socket.setSoTimeout(NetworkUtils.toTimeoutMillis(readTimeout));
		} catch (SocketException e) {
			throw new NetworkConnectionException("Failed to apply read timeout", e, NetworkErrorType.IO_ERROR, this.remoteEndpoint);
		}
	}
	
	/**
	 * Returns the framing that delimits messages on the stream of this connection.<br>
	 * @return The framing
	 */
	public @NonNull Framing framing() {
		return this.framing;
	}
	
	/**
	 * Returns the maximum length of a message in bytes.<br>
	 * Longer messages are rejected by both {@link #send(byte[])} and {@link #receive()}.<br>
	 *
	 * @return The maximum message size
	 */
	public int maxMessageSize() {
		return this.maxMessageSize;
	}
	
	/**
	 * Returns the read timeout of this connection.<br>
	 * @return The read timeout, or an empty optional if a receive waits forever
	 */
	public @NonNull Optional<Duration> readTimeout() {
		return Optional.ofNullable(this.readTimeout);
	}
	
	@Override
	public boolean isActive() {
		return !this.released && !this.socket.isClosed() && this.socket.isConnected();
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> localEndpoint() {
		return Optional.of(this.localEndpoint);
	}
	
	@Override
	public @NonNull Optional<IpEndpoint> remoteEndpoint() {
		return Optional.of(this.remoteEndpoint);
	}
	
	@Override
	public void send(byte @NonNull [] data) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		NetworkUtils.validateMessageSize(data, this.maxMessageSize, this.remoteEndpoint);
		
		this.writeLock.lock();
		try {
			this.ensureActive();
			this.framing.write(this.streams.output(), data);
		} catch (SocketException e) {
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, this.remoteEndpoint);
		} catch (IOException e) {
			throw NetworkUtils.mapIoFailure(e, "Failed to send data", this.remoteEndpoint);
		} finally {
			this.writeLock.unlock();
		}
	}
	
	@Override
	public @NonNull Optional<byte[]> receive() throws NetworkConnectionException {
		this.readLock.lock();
		try {
			if (!this.released && this.socket.isClosed()) {
				return Optional.empty();
			}
			this.ensureActive();
			return Optional.ofNullable(this.framing.read(this.streams.input(), this.maxMessageSize));
		} catch (FrameTooLargeException e) {
			throw new NetworkConnectionException(e.getMessage(), e, NetworkErrorType.MESSAGE_TOO_LARGE, this.remoteEndpoint);
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Read timed out", e, NetworkErrorType.READ_TIMEOUT, Objects.requireNonNullElse(this.readTimeout, Duration.ZERO), this.remoteEndpoint);
		} catch (EOFException e) {
			throw new NetworkConnectionException("Connection closed in the middle of a message", e, NetworkErrorType.CONNECTION_RESET, this.remoteEndpoint);
		} catch (SocketException e) {
			if (this.socket.isClosed()) {
				return Optional.empty();
			}
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, this.remoteEndpoint);
		} catch (IOException e) {
			throw NetworkUtils.mapIoFailure(e, "Failed to receive data", this.remoteEndpoint);
		} finally {
			this.readLock.unlock();
		}
	}
	
	/**
	 * Returns the buffered input stream of this connection.<br>
	 * <p>
	 *     The same stream is returned on every call and {@link #receive()} reads through the same buffer,
	 *     so raw reads and receives can be mixed without losing bytes.<br>
	 *     Reading from the stream bypasses the framing.<br>
	 *     The stream must not be closed, the connection is closed instead.
	 * </p>
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the connection is not active or the stream cannot be obtained
	 */
	public @NonNull InputStream getInputStream() throws NetworkConnectionException {
		this.ensureActive();
		return this.streams.input();
	}
	
	/**
	 * Returns the unbuffered output stream of this connection.<br>
	 * <p>
	 *     Everything written to the stream reaches the peer immediately and bypasses the framing.<br>
	 *     Writes to the stream are not synchronized with {@link #send(byte[])}.<br>
	 *     The stream must not be closed, the connection is closed instead.
	 * </p>
	 *
	 * @return The output stream
	 * @throws NetworkConnectionException If the connection is not active or the stream cannot be obtained
	 */
	public @NonNull OutputStream getOutputStream() throws NetworkConnectionException {
		this.ensureActive();
		return this.streams.output();
	}
	
	/**
	 * Creates a client that sends and receives values of the given codec over this connection.<br>
	 * <p>
	 *     Every value is encoded into a single binary message, which is delimited by the framing of this connection.<br>
	 *     The framing therefore has to delimit arbitrary binary data, so it must be {@link Framing.LengthPrefixed}.<br>
	 *     The delimiter based framings split a value whose encoding contains the delimiter, and {@link Framing.None} does not preserve message boundaries at all.<br>
	 *     The returned client shares this connection, closing it closes this connection.
	 * </p>
	 *
	 * @param codec The codec used to encode and decode the values
	 * @return The client for values of the codec
	 * @param <T> The type of the values
	 * @throws NullPointerException If the codec is null
	 * @throws IllegalStateException If the framing of this connection is not length prefixed
	 */
	public <T> @NonNull CodecClient<T> map(@NonNull Codec<T> codec) {
		Objects.requireNonNull(codec, "Codec must not be null");
		if (!(this.framing instanceof Framing.LengthPrefixed)) {
			throw new IllegalStateException("A codec client requires a length prefixed framing, but the framing is " + this.framing);
		}
		return new CodecClient<>(this, codec);
	}
	
	/**
	 * Closes this connection and its underlying socket.<br>
	 * A connection whose socket was handed over to an upgraded connection does not close the socket, the upgraded connection owns it.<br>
	 */
	@Override
	public void close() {
		if (!this.released) {
			NetworkUtils.closeQuietly(this.socket);
		}
	}
	
	/**
	 * Returns the underlying socket of this connection.<br>
	 * @return The socket
	 */
	protected final @NonNull Socket socket() {
		return this.socket;
	}
	
	/**
	 * Returns the streams of the underlying socket.<br>
	 * @return The streams
	 */
	protected final @NonNull SocketStreams streams() {
		return this.streams;
	}
	
	/**
	 * Returns whether the underlying socket was handed over to another connection.<br>
	 * @return True if this connection was released by {@link #handOver(HandOver)}
	 */
	protected final boolean isReleased() {
		return this.released;
	}
	
	/**
	 * Hands the underlying socket over to the connection created by the given action, for example a TLS upgrade.<br>
	 * <p>
	 *     The action runs while both the send and the receive lock are held, so no message is in flight during the hand over.<br>
	 *     If the action succeeds, this connection is released.<br>
	 *     It is no longer active afterward and closing it leaves the socket open.<br>
	 *     If the action fails, this connection is not released and the caller decides whether to close it.<br>
	 *     The action itself may already have closed the socket, for example a TLS upgrade whose handshake failed.
	 * </p>
	 *
	 * @param action The action that creates the new owner of the socket
	 * @return The result of the action
	 * @param <R> The type of the new owner
	 * @throws NullPointerException If the action is null
	 * @throws NetworkConnectionException If this connection is not active or the action fails
	 */
	protected final <R> @NonNull R handOver(@NonNull HandOver<R> action) throws NetworkConnectionException {
		Objects.requireNonNull(action, "Action must not be null");
		
		this.writeLock.lock();
		this.readLock.lock();
		try {
			this.ensureActive();
			R result = Objects.requireNonNull(action.run(), "Hand over result must not be null");
			this.released = true;
			return result;
		} finally {
			this.readLock.unlock();
			this.writeLock.unlock();
		}
	}
	
	/**
	 * Ensures that this connection is active.<br>
	 * @throws NetworkConnectionException If the connection is closed or was handed over
	 */
	protected final void ensureActive() throws NetworkConnectionException {
		if (this.released) {
			throw new NetworkConnectionException("Connection was handed over to an upgraded connection", NetworkErrorType.NOT_CONNECTED, this.remoteEndpoint);
		}
		if (!this.isActive()) {
			throw new NetworkConnectionException("Connection is closed", NetworkErrorType.SOCKET_CLOSED, this.remoteEndpoint);
		}
	}
	
	/**
	 * An action that creates the new owner of the socket of a connection.<br>
	 *
	 * @see Connection#handOver(HandOver)
	 *
	 * @author Luis-St
	 *
	 * @param <R> The type of the new owner
	 */
	@FunctionalInterface
	protected interface HandOver<R> {
		
		/**
		 * Creates the new owner of the socket.<br>
		 *
		 * @return The new owner
		 * @throws NetworkConnectionException If the new owner cannot be created
		 */
		@NonNull R run() throws NetworkConnectionException;
	}
}
