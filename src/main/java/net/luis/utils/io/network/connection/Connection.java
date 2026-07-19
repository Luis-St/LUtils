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
import net.luis.utils.io.network.connection.event.ConnectEvent;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents an active network connection between a local and a remote endpoint.<br>
 * This interface is implemented by the protocol-specific connection classes (e.g. {@code TcpConnection}, {@code SslConnection})<br>
 * so that events such as {@link ConnectEvent} can reference the connection that triggered them without depending on a specific protocol.<br>
 *
 * @author Luis-St
 */
public interface Connection extends AutoCloseable {
	
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
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
	@NonNull InputStream getInputStream() throws NetworkConnectionException;
	
	/**
	 * Returns the output stream for advanced writing.<br>
	 *
	 * @return The output stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
	@NonNull OutputStream getOutputStream() throws NetworkConnectionException;
	
	/**
	 * Returns whether this connection is still active.<br>
	 * @return True if the connection is active
	 */
	boolean isActive();
	
	@Override
	void close();
}
