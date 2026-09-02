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
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.event.ConnectEventHandler;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents an active network connection between a local and a remote endpoint.<br>
 * This interface is implemented by the protocol-specific connection classes (e.g. {@code TcpConnection}, {@code SslConnection})<br>
 * so that events such as {@link ConnectEventHandler} can reference the connection that triggered them without depending on a specific protocol.<br>
 * <p>
 *     Next to the message oriented {@link #send(byte[])} and {@link #receive()}, a connection hands out the streams of the
 *     underlying socket, for protocols that have to drive the conversation themselves.<br>
 *     Which wrapper is put on top of them is left to the caller, a reader, a writer, a data stream, or a parser of its own.
 * </p>
 * <p>
 *     A wrapper has to be created once and then reused, because a buffered wrapper reads ahead and a second one built over
 *     the same connection would wait for data the first one already holds. The streams themselves are stable, so the same
 *     instance is returned on every call, and a wrapper that has to outlive a single call can be kept in {@link #context()}.<br>
 *     Wrappers must not be closed, because that closes the connection, {@link #close()} is used instead.
 * </p>
 *
 * @author Luis-St
 */
public interface Connection extends AutoCloseable {
	
	/**
	 * Returns whether this connection is still active.<br>
	 * @return True if the connection is active
	 */
	boolean isActive();
	
	/**
	 * Returns the context of this connection.<br>
	 * The context stores user data attached to this connection, it is empty until data is stored in it.<br>
	 * <p>
	 *     The returned context is bound to this connection, so state stored in it is visible to all event handlers<br>
	 *     that are invoked for this connection and is discarded together with the connection.
	 * </p>
	 *
	 * @return The context of this connection
	 */
	@NonNull ConnectionContext context();
	
	/**
	 * Returns the remote endpoint of this connection.<br>
	 * @return The remote endpoint
	 */
	@NonNull IpEndpoint remoteEndpoint();
	
	/**
	 * Returns the local endpoint of this connection.<br>
	 * @return The local endpoint
	 */
	@NonNull IpEndpoint localEndpoint();
	
	/**
	 * Sends data over this connection.<br>
	 *
	 * @param data The data to send
	 * @throws NullPointerException If data is null
	 * @throws NetworkConnectionException If sending fails or data exceeds the buffer size
	 */
	void send(byte @NonNull [] data) throws NetworkConnectionException;
	
	/**
	 * Receives data from this connection (blocking).<br>
	 * Uses the configured buffer size.<br>
	 *
	 * @return The received data, or an empty array if the connection was closed
	 * @throws NetworkConnectionException If receiving fails
	 */
	byte @NonNull [] receive() throws NetworkConnectionException;
	
	/**
	 * Receives data with a custom buffer size (blocking).<br>
	 *
	 * @param maxBytes The maximum number of bytes to receive
	 * @return The received data, or an empty array if the connection was closed
	 * @throws IllegalArgumentException If maxBytes is less than 1
	 * @throws NetworkConnectionException If receiving fails
	 */
	byte @NonNull [] receive(int maxBytes) throws NetworkConnectionException;
	
	/**
	 * Returns the input stream for advanced reading.<br>
	 * The stream is buffered and the same instance is returned on every call, so it can be stored and read across calls.<br>
	 * <p>
	 *     {@link #receive()} reads through this same buffer, so the two can be mixed without skipping bytes.
	 * </p>
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
	@NonNull InputStream getInputStream() throws NetworkConnectionException;
	
	/**
	 * Returns the output stream for advanced writing.<br>
	 * The stream is not buffered and the same instance is returned on every call, so everything written to it goes to the peer immediately.<br>
	 *
	 * @return The output stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
	@NonNull OutputStream getOutputStream() throws NetworkConnectionException;
	
	@Override
	void close();
}
