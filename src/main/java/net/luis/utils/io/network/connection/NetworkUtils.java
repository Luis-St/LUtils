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

import net.luis.utils.io.network.Endpoint;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for network client and server implementations.<br>
 * This class provides common helper methods used across TCP and UDP implementations.<br>
 *
 * @author Luis-St
 */
public final class NetworkUtils {
	
	/**
	 * The default timeout in seconds for executor shutdown.<br>
	 */
	private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
	/**
	 * The size in bytes of the length-prefix header used to frame messages on the wire.<br>
	 */
	private static final int FRAME_HEADER_SIZE = Integer.BYTES;
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private NetworkUtils() {}
	
	/**
	 * Returns a scratch buffer of at least the given size for an unframed read operation.<br>
	 * The given buffer is reused when it is large enough, otherwise a larger one is allocated.<br>
	 * <p>
	 *     The returned buffer may be larger than requested, so reads must be bounded to the requested length.<br>
	 *     Callers are expected to store the returned buffer and pass it back on the next call.
	 * </p>
	 * <p>
	 *     Framed reads allocate an exact sized payload per message and do not need a scratch buffer,
	 *     so this is only used when framing is disabled.
	 * </p>
	 *
	 * @param current The buffer from the previous call, or null if none was allocated yet
	 * @param maxBytes The minimum required buffer size
	 * @return The buffer to read into, which is the given buffer if it was large enough
	 */
	public static byte @NonNull [] resizeBuffer(byte @Nullable [] current, int maxBytes) {
		if (current == null || current.length < maxBytes) {
			return new byte[maxBytes];
		}
		return current;
	}

	/**
	 * Validates that the given data does not exceed the configured buffer size.<br>
	 *
	 * @param data The data to validate
	 * @param bufferSize The maximum allowed size in bytes
	 * @param endpoint The endpoint to attach to the thrown exception, or null if not available
	 * @throws NullPointerException If data is null
	 * @throws NetworkConnectionException If the data exceeds the buffer size
	 */
	public static void validateMessageSize(byte @NonNull [] data, int bufferSize, @Nullable Endpoint endpoint) throws NetworkConnectionException {
		Objects.requireNonNull(data, "Data must not be null");
		if (data.length > bufferSize) {
			throw new NetworkConnectionException("Message size " + data.length + " exceeds buffer size " + bufferSize, NetworkErrorType.MESSAGE_TOO_LARGE, endpoint);
		}
	}
	
	/**
	 * Writes the given data to the socket and flushes it.<br>
	 * This method contains the shared write path of the connection oriented clients and connections.<br>
	 * <p>
	 *     When framing is enabled, the data is written as a single length-prefixed frame by {@link #writeFrame(OutputStream, byte[])},
	 *     so that the peer receives exactly these bytes as one message. When it is disabled, the bytes are written directly to the
	 *     stream and message boundaries are not preserved, which requires the peer to delimit messages itself.
	 * </p>
	 *
	 * @param socket The socket to write to
	 * @param data The data to write
	 * @param framing Whether to write the data as a length-prefixed frame
	 * @param onError The handler to notify on an I/O error, or null if none is configured
	 * @param endpoint The endpoint to attach to thrown exceptions, or null if not available
	 * @param onDisconnect The action to run when the connection was reset, or null if the caller tracks no connection state
	 * @throws NullPointerException If socket or data is null
	 * @throws NetworkConnectionException If writing fails
	 */
	public static void writeAll(@NonNull Socket socket, byte @NonNull [] data, boolean framing, @Nullable ErrorEventHandler onError, @Nullable Endpoint endpoint, @Nullable Runnable onDisconnect) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		
		try {
			OutputStream out = socket.getOutputStream();
			if (framing) {
				writeFrame(out, data);
			} else {
				out.write(data);
				out.flush();
			}
		} catch (SocketException e) {
			if (onDisconnect != null) {
				onDisconnect.run();
			}
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, endpoint);
		} catch (IOException e) {
			handleError(onError, NetworkErrorType.IO_ERROR, "Failed to send data", e);
			throw new NetworkConnectionException("Failed to send data", e, NetworkErrorType.IO_ERROR, endpoint);
		}
	}
	
	/**
	 * Reads a single message from the socket (blocking).<br>
	 * This method contains the shared read path of the connection oriented clients and connections.<br>
	 * <p>
	 *     When framing is enabled, one complete length-prefixed frame is read by {@link #readFrame(InputStream, int)}, so that exactly
	 *     the bytes passed to the peers {@link #writeAll} call are returned, regardless of how the stream fragments or coalesces them.
	 * </p>
	 * <p>
	 *     When it is disabled, whatever is currently available is returned instead, up to the given limit. A single read may then hold
	 *     several messages or only part of one, so the caller has to delimit messages itself.
	 * </p>
	 *
	 * @param socket The socket to read from
	 * @param buffer The scratch buffer used for unframed reads, which must be at least maxBytes long, or null when framing is enabled
	 * @param maxBytes The maximum payload length that is accepted
	 * @param framing Whether to read a single length-prefixed frame
	 * @param readTimeout The read timeout to report on a timeout
	 * @param onError The handler to notify on an I/O error, or null if none is configured
	 * @param endpoint The endpoint to attach to thrown exceptions, or null if not available
	 * @param onDisconnect The action to run when the connection was closed or reset, or null if the caller tracks no connection state
	 * @return The received message, or an empty array if the connection was closed cleanly between messages
	 * @throws NullPointerException If socket or read timeout is null, or if framing is disabled and buffer is null
	 * @throws NetworkConnectionException If receiving fails, or the declared frame length exceeds maxBytes
	 * @throws NetworkTimeoutException If the read times out
	 * @see #readMessage(InputStream, byte[], int, boolean, Duration, ErrorEventHandler, Endpoint, Runnable)
	 */
	public static byte @NonNull [] readAvailable(
		@NonNull Socket socket,
		byte @Nullable [] buffer,
		int maxBytes,
		boolean framing,
		@NonNull Duration readTimeout,
		@Nullable ErrorEventHandler onError,
		@Nullable Endpoint endpoint,
		@Nullable Runnable onDisconnect
	) throws NetworkConnectionException {
		Objects.requireNonNull(socket, "Socket must not be null");
		
		InputStream in;
		try {
			in = socket.getInputStream();
		} catch (SocketException e) {
			if (onDisconnect != null) {
				onDisconnect.run();
			}
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, endpoint);
		} catch (IOException e) {
			handleError(onError, NetworkErrorType.IO_ERROR, "Failed to receive data", e);
			throw new NetworkConnectionException("Failed to receive data", e, NetworkErrorType.IO_ERROR, endpoint);
		}
		
		return readMessage(in, buffer, maxBytes, framing, readTimeout, onError, endpoint, onDisconnect);
	}
	
	/**
	 * Reads a single message from the given input stream (blocking).<br>
	 * This is the stream based variant of {@link #readAvailable(Socket, byte[], int, boolean, Duration, ErrorEventHandler, Endpoint, Runnable)}.<br>
	 * It lets the caller pass the buffered stream it also hands to the user, so that both read through the same buffer and no buffered bytes are skipped.<br>
	 * <p>
	 *     When framing is enabled, one complete length-prefixed frame is read by {@link #readFrame(InputStream, int)}, so that exactly
	 *     the bytes passed to the peers {@link #writeAll} call are returned, regardless of how the stream fragments or coalesces them.
	 * </p>
	 * <p>
	 *     When it is disabled, whatever is currently available is returned instead, up to the given limit. A single read may then hold
	 *     several messages or only part of one, so the caller has to delimit messages itself.
	 * </p>
	 *
	 * @param in The input stream to read from
	 * @param buffer The scratch buffer used for unframed reads, which must be at least maxBytes long, or null when framing is enabled
	 * @param maxBytes The maximum payload length that is accepted
	 * @param framing Whether to read a single length-prefixed frame
	 * @param readTimeout The read timeout to report on a timeout
	 * @param onError The handler to notify on an I/O error, or null if none is configured
	 * @param endpoint The endpoint to attach to thrown exceptions, or null if not available
	 * @param onDisconnect The action to run when the connection was closed or reset, or null if the caller tracks no connection state
	 * @return The received message, or an empty array if the connection was closed cleanly between messages
	 * @throws NullPointerException If the input stream or read timeout is null, or if framing is disabled and buffer is null
	 * @throws NetworkConnectionException If receiving fails, or the declared frame length exceeds maxBytes
	 * @throws NetworkTimeoutException If the read times out
	 */
	public static byte @NonNull [] readMessage(
		@NonNull InputStream in,
		byte @Nullable [] buffer,
		int maxBytes,
		boolean framing,
		@NonNull Duration readTimeout,
		@Nullable ErrorEventHandler onError,
		@Nullable Endpoint endpoint,
		@Nullable Runnable onDisconnect
	) throws NetworkConnectionException {
		Objects.requireNonNull(in, "Input stream must not be null");
		Objects.requireNonNull(readTimeout, "Read timeout must not be null");
		if (!framing) {
			Objects.requireNonNull(buffer, "Buffer must not be null when framing is disabled");
		}
		
		try {
			if (!framing) {
				int bytesRead = in.read(buffer, 0, maxBytes);

				if (bytesRead == -1) {
					if (onDisconnect != null) {
						onDisconnect.run();
					}
					return ArrayUtils.EMPTY_BYTE_ARRAY;
				}

				return Arrays.copyOf(buffer, bytesRead);
			}

			byte[] data = readFrame(in, maxBytes);

			if (data == null) {
				if (onDisconnect != null) {
					onDisconnect.run();
				}
				return ArrayUtils.EMPTY_BYTE_ARRAY;
			}

			return data;
		} catch (FrameTooLargeException e) {
			throw new NetworkConnectionException(e.getMessage(), e, NetworkErrorType.MESSAGE_TOO_LARGE, endpoint);
		} catch (SocketTimeoutException e) {
			throw new NetworkTimeoutException("Read timed out", NetworkErrorType.READ_TIMEOUT, readTimeout, endpoint);
		} catch (EOFException e) {
			if (onDisconnect != null) {
				onDisconnect.run();
			}
			throw new NetworkConnectionException("Connection reset while receiving data", e, NetworkErrorType.CONNECTION_RESET, endpoint);
		} catch (SocketException e) {
			if (onDisconnect != null) {
				onDisconnect.run();
			}
			throw new NetworkConnectionException("Connection reset", e, NetworkErrorType.CONNECTION_RESET, endpoint);
		} catch (IOException e) {
			handleError(onError, NetworkErrorType.IO_ERROR, "Failed to receive data", e);
			throw new NetworkConnectionException("Failed to receive data", e, NetworkErrorType.IO_ERROR, endpoint);
		}
	}
	
	/**
	 * Maps a failure raised while establishing a connection to the matching network exception.<br>
	 * The failure is reported to the given error handler before the mapped exception is returned.<br>
	 * <p>
	 *     The returned exception is meant to be thrown by the caller, so that the compiler still sees
	 *     the connect method as terminating.
	 * </p>
	 *
	 * @param cause The failure that was caught
	 * @param endpoint The endpoint that was being connected to
	 * @param connectTimeout The configured connect timeout, reported on a timeout
	 * @param onError The handler to notify, or null if none is configured
	 * @return The mapped exception to throw
	 * @throws NullPointerException If cause, endpoint, or connect timeout is null
	 */
	public static @NonNull NetworkConnectionException mapConnectFailure(@NonNull IOException cause, @NonNull Endpoint endpoint, @NonNull Duration connectTimeout, @Nullable ErrorEventHandler onError) {
		Objects.requireNonNull(cause, "Cause must not be null");
		Objects.requireNonNull(endpoint, "Endpoint must not be null");
		Objects.requireNonNull(connectTimeout, "Connect timeout must not be null");
		
		return switch (cause) {
			case SocketTimeoutException e -> {
				handleError(onError, NetworkErrorType.CONNECTION_TIMEOUT, "Connection timed out to " + endpoint, e);
				yield new NetworkTimeoutException("Connection timed out to " + endpoint, NetworkErrorType.CONNECTION_TIMEOUT, connectTimeout, endpoint);
			}
			case SSLHandshakeException e -> {
				handleError(onError, NetworkErrorType.HANDSHAKE_FAILED, "SSL handshake failed with " + endpoint, e);
				yield new NetworkConnectionException("SSL handshake failed with " + endpoint, e, NetworkErrorType.HANDSHAKE_FAILED, endpoint);
			}
			case ConnectException e -> {
				handleError(onError, NetworkErrorType.CONNECTION_REFUSED, "Connection refused by " + endpoint, e);
				yield new NetworkConnectionException("Connection refused by " + endpoint, e, NetworkErrorType.CONNECTION_REFUSED, endpoint);
			}
			case NoRouteToHostException e -> {
				handleError(onError, NetworkErrorType.HOST_UNREACHABLE, "Host unreachable: " + endpoint, e);
				yield new NetworkConnectionException("Host unreachable: " + endpoint, e, NetworkErrorType.HOST_UNREACHABLE, endpoint);
			}
			default -> {
				handleError(onError, NetworkErrorType.CONNECTION_FAILED, "Failed to connect to " + endpoint, cause);
				yield new NetworkConnectionException("Failed to connect to " + endpoint, cause, NetworkErrorType.CONNECTION_FAILED, endpoint);
			}
		};
	}
	
	/**
	 * Writes a single length-prefixed frame to the given output stream and flushes it.<br>
	 * The frame consists of a 4-byte big-endian length header followed by the payload bytes,<br>
	 * so that the receiving side can reassemble exactly the bytes passed to this method regardless of how the stream fragments them.<br>
	 *
	 * @param out The output stream to write to
	 * @param data The payload to send
	 * @throws NullPointerException If the output stream or data is null
	 * @throws IOException If an I/O error occurs while writing
	 */
	public static void writeFrame(@NonNull OutputStream out, byte @NonNull [] data) throws IOException {
		Objects.requireNonNull(out, "Output stream must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		
		int length = data.length;
		byte[] frame = new byte[FRAME_HEADER_SIZE + length];
		frame[0] = (byte) (length >>> 24);
		frame[1] = (byte) (length >>> 16);
		frame[2] = (byte) (length >>> 8);
		frame[3] = (byte) length;
		System.arraycopy(data, 0, frame, FRAME_HEADER_SIZE, length);
		
		out.write(frame);
		out.flush();
	}
	
	/**
	 * Reads a single length-prefixed frame from the given input stream (blocking).<br>
	 * Reassembles the frame regardless of how many raw reads it takes to arrive, looping until the full header and payload have been read.<br>
	 *
	 * @param in The input stream to read from
	 * @param maxBytes The maximum payload length that is accepted
	 * @return The payload bytes, or null if the stream ended cleanly before any frame data was read
	 * @throws NullPointerException If the input stream is null
	 * @throws EOFException If the stream ends in the middle of a frame
	 * @throws FrameTooLargeException If the declared frame length exceeds {@code maxBytes}
	 * @throws IOException If the declared frame length is invalid, or an I/O error occurs while reading
	 */
	public static byte @Nullable [] readFrame(@NonNull InputStream in, int maxBytes) throws IOException {
		Objects.requireNonNull(in, "Input stream must not be null");
		
		byte[] header = new byte[FRAME_HEADER_SIZE];
		int headerRead = in.readNBytes(header, 0, FRAME_HEADER_SIZE);
		if (headerRead == 0) {
			return null;
		}
		if (headerRead < FRAME_HEADER_SIZE) {
			throw new EOFException("Connection closed while reading frame header");
		}
		
		int length = ((header[0] & 0xFF) << 24) | ((header[1] & 0xFF) << 16) | ((header[2] & 0xFF) << 8) | (header[3] & 0xFF);
		if (length < 0) {
			throw new IOException("Invalid frame length: " + length);
		}
		if (length > maxBytes) {
			throw new FrameTooLargeException(length, maxBytes);
		}
		
		byte[] payload = new byte[length];
		int payloadRead = in.readNBytes(payload, 0, length);
		if (payloadRead < length) {
			throw new EOFException("Connection closed while reading frame payload");
		}
		return payload;
	}
	
	/**
	 * Handles an error by notifying the configured error handler if present.<br>
	 * This method safely invokes the error handler without throwing exceptions.<br>
	 *
	 * @param handler The error handler to notify, or null if no handler is configured
	 * @param errorType The type of error that occurred
	 * @param message A human-readable error message
	 * @param cause The underlying exception
	 * @throws NullPointerException If the error type, message, or cause is null
	 */
	public static void handleError(@Nullable ErrorEventHandler handler, @NonNull NetworkErrorType errorType, @NonNull String message, @NonNull Throwable cause) {
		handleError(handler, null, errorType, message, cause);
	}
	
	/**
	 * Handles an error that occurred within the scope of a specific connection by notifying the configured error handler if present.<br>
	 * This method safely invokes the error handler without throwing exceptions.<br>
	 *
	 * @param handler The error handler to notify, or null if no handler is configured
	 * @param connection The connection the error occurred on, or null if not available
	 * @param errorType The type of error that occurred
	 * @param message A human-readable error message
	 * @param cause The underlying exception
	 * @throws NullPointerException If the error type, message, or cause is null
	 */
	public static void handleError(@Nullable ErrorEventHandler handler, @Nullable Connection connection, @NonNull NetworkErrorType errorType, @NonNull String message, @NonNull Throwable cause) {
		Objects.requireNonNull(errorType, "Error type must not be null");
		Objects.requireNonNull(message, "Message must not be null");
		Objects.requireNonNull(cause, "Cause must not be null");
		
		if (handler != null) {
			handler.handle(connection, errorType, message, cause);
		}
	}
	
	/**
	 * Shuts down an executor service gracefully.<br>
	 * This method attempts a graceful shutdown, waiting up to 5 seconds for tasks to complete.<br>
	 * If tasks don't complete in time, it forces an immediate shutdown.<br>
	 * <p>
	 *     This method only shuts down the executor if {@code ownsExecutor} is {@code true},<br>
	 *     indicating that the caller is responsible for managing the executor's lifecycle.
	 * </p>
	 *
	 * @param executor The executor service to shut down, or null if no executor is configured
	 * @param ownsExecutor Whether the caller owns the executor and should shut it down
	 */
	public static void shutdownExecutor(@Nullable ExecutorService executor, boolean ownsExecutor) {
		if (executor != null && ownsExecutor) {
			executor.shutdown();
			
			try {
				if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
					executor.shutdownNow();
				}
			} catch (InterruptedException e) {
				executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
}
