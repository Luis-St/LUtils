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

import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.FrameTooLargeException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
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
