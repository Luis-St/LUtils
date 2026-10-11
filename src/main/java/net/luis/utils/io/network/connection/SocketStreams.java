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

import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

/**
 * Holds the streams of a single socket.<br>
 * This class exists so that a connection or client hands out the same stream on every call instead of creating a new one.<br>
 * <p>
 *     A stable stream is what makes a user built wrapper safe.<br>
 *     A buffered wrapper reads ahead, so if a peer sends two lines in one packet,<br>
 *     the wrapper pulls both into its buffer and returns the first line.<br>
 *     A second wrapper built afterward starts with an empty buffer and waits for data that has already arrived,<br>
 *     while the second line sits in the buffer of the first wrapper and is never read.<br>
 *     Building the wrapper once over a stream that never changes avoids that,<br>
 *     and where the wrapper has to survive across calls it can be kept in the connection context.
 * </p>
 * <p>
 *     The input side is buffered exactly once here, and the reception methods of the owner read through that same buffer,<br>
 *     so a raw stream read and a receive can be mixed without skipping bytes.<br>
 *     The output side is not buffered, so everything written to it reaches the peer immediately.
 * </p>
 * <p>
 *     The streams must never be closed, because closing either of them closes the underlying socket and discards whatever is still buffered.<br>
 *     The owning connection or client is closed instead.
 * </p>
 *
 * @see Connection
 *
 * @author Luis-St
 */
public final class SocketStreams {
	
	/**
	 * The size in bytes of the buffer that is placed in front of the socket input stream.<br>
	 */
	private static final int BUFFER_SIZE = 8192;
	
	/**
	 * The socket the streams belong to.<br>
	 */
	private final Socket socket;
	/**
	 * The buffered input stream of the socket, created on the first access.<br>
	 */
	private @Nullable BufferedInputStream input;
	/**
	 * The output stream of the socket, created on the first access.<br>
	 */
	private @Nullable OutputStream output;
	
	/**
	 * Constructs a new stream holder for the given socket.<br>
	 *
	 * @param socket The socket to wrap
	 * @throws NullPointerException If socket is null
	 */
	public SocketStreams(@NonNull Socket socket) {
		this.socket = Objects.requireNonNull(socket, "Socket must not be null");
	}
	
	/**
	 * Returns the buffered input stream of the socket.<br>
	 * The same stream is returned on every call, so it can be stored and used across calls without losing buffered data.<br>
	 * <p>
	 *     The socket is asked for its stream on every call even though the buffer is created only once, so that a socket
	 *     that died since the last call is reported instead of the buffer returning an endless end of stream.
	 * </p>
	 *
	 * @return The input stream
	 * @throws NetworkConnectionException If the socket was reset or the stream cannot be obtained
	 */
	public synchronized @NonNull InputStream input() throws NetworkConnectionException {
		InputStream raw;
		try {
			raw = this.socket.getInputStream();
		} catch (SocketException e) {
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET);
		} catch (IOException e) {
			throw new NetworkConnectionException("Failed to get input stream", e, NetworkErrorType.IO_ERROR);
		}
		
		if (this.input == null) {
			this.input = new BufferedInputStream(raw, BUFFER_SIZE);
		}
		return this.input;
	}
	
	/**
	 * Returns the output stream of the socket.<br>
	 * The stream is not buffered, so everything written to it goes to the peer immediately.<br>
	 *
	 * @return The output stream
	 * @throws NetworkConnectionException If the stream cannot be obtained
	 */
	public synchronized @NonNull OutputStream output() throws NetworkConnectionException {
		if (this.output == null) {
			try {
				this.output = this.socket.getOutputStream();
			} catch (IOException e) {
				throw new NetworkConnectionException("Failed to get output stream", e, NetworkErrorType.IO_ERROR);
			}
		}
		return this.output;
	}
	
	/**
	 * Checks whether unread input is pending, either in the buffer of the input stream or in the socket itself.<br>
	 * This is used before the connection is handed to another owner, such as a TLS upgrade, because the new owner
	 * reads from the raw socket and would neither see the buffered bytes nor expect the pending ones.<br>
	 *
	 * @return True if input is pending
	 */
	public synchronized boolean hasPendingInput() {
		try {
			return this.input != null && this.input.available() > 0;
		} catch (IOException _) {
			return false;
		}
	}
}
