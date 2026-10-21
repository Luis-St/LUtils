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

import net.luis.utils.io.network.connection.exception.FrameTooLargeException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Describes how messages are delimited on the byte stream of a {@link Connection}.<br>
 * A stream connection has no message boundaries of its own, so the framing decides which bytes one send writes and one receive returns.<br>
 * <p>
 *     The available framings are:
 * </p>
 * <ul>
 *     <li>{@link None} writes the raw bytes and returns whatever is currently available, so no message boundaries are preserved</li>
 *     <li>{@link LengthPrefixed} writes a 4-byte big-endian length header in front of every message, so each receive returns exactly one send</li>
 *     <li>{@link Delimited} terminates every message with a fixed byte sequence that must not occur inside a message</li>
 *     <li>{@link Lines} terminates every message with {@code CRLF} and accepts both {@code CRLF} and a bare {@code LF} when reading</li>
 * </ul>
 * <p>
 *     Both peers have to use the same framing.<br>
 *     {@link None} and the text based framings interoperate with peers that are not built on this library, {@link LengthPrefixed} does not.
 * </p>
 *
 * @see Connection
 *
 * @author Luis-St
 */
public sealed interface Framing {
	
	/**
	 * Returns the framing that writes and reads raw bytes without message boundaries.<br>
	 * @return The raw framing
	 */
	static @NonNull None none() {
		return new None();
	}
	
	/**
	 * Returns the framing that prefixes every message with its 4-byte big-endian length.<br>
	 * @return The length-prefixed framing
	 */
	static @NonNull LengthPrefixed lengthPrefixed() {
		return new LengthPrefixed();
	}
	
	/**
	 * Returns the framing that terminates every message with the given byte sequence.<br>
	 *
	 * @param delimiter The byte sequence that terminates a message
	 * @return The delimited framing
	 * @throws NullPointerException If the delimiter is null
	 * @throws IllegalArgumentException If the delimiter is empty
	 */
	static @NonNull Delimited delimited(byte @NonNull ... delimiter) {
		return new Delimited(delimiter);
	}
	
	/**
	 * Returns the framing that terminates every message with a line break.<br>
	 * @return The line framing
	 */
	static @NonNull Lines lines() {
		return new Lines();
	}
	
	/**
	 * Validates the arguments of a read operation.<br>
	 *
	 * @param in The input stream to read from
	 * @param maxBytes The maximum length of a message in bytes
	 * @throws NullPointerException If the input stream is null
	 * @throws IllegalArgumentException If max bytes is less than 1
	 */
	private static void validateRead(@NonNull InputStream in, int maxBytes) {
		Objects.requireNonNull(in, "Input stream must not be null");
		if (maxBytes < 1) {
			throw new IllegalArgumentException("Max bytes must be at least 1: " + maxBytes);
		}
	}
	
	/**
	 * Reads bytes from the given input stream until the given delimiter has been read.<br>
	 * This is the shared read path of the delimiter based framings.<br>
	 * <p>
	 *     The given slack is the number of bytes the message may exceed max bytes by, because the caller strips them afterward.<br>
	 *     The caller is responsible for rejecting a message that is still longer than max bytes after stripping.
	 * </p>
	 *
	 * @param in The input stream to read from
	 * @param delimiter The byte sequence that terminates a message
	 * @param maxBytes The maximum length of a message in bytes, excluding the delimiter
	 * @param slack The number of bytes the message may exceed max bytes by
	 * @return The message without the delimiter, or null if the stream ended between messages
	 * @throws NullPointerException If the input stream or the delimiter is null
	 * @throws IllegalArgumentException If the delimiter is empty, max bytes is less than 1 or the slack is negative
	 * @throws FrameTooLargeException If the message is longer than max bytes plus the slack
	 * @throws EOFException If the stream ends in the middle of a message
	 * @throws IOException If an I/O error occurs while reading
	 */
	private static byte @Nullable [] readUntil(@NonNull InputStream in, byte @NonNull [] delimiter, int maxBytes, int slack) throws IOException {
		validateRead(in, maxBytes);
		Objects.requireNonNull(delimiter, "Delimiter must not be null");
		if (delimiter.length == 0) {
			throw new IllegalArgumentException("Delimiter must not be empty");
		}
		if (slack < 0) {
			throw new IllegalArgumentException("Slack must not be negative: " + slack);
		}
		
		int limit = limitOf(maxBytes, (long) delimiter.length + slack);
		byte[] buffer = new byte[Math.min(limit, 256)];
		int size = 0;
		while (true) {
			int next = in.read();
			if (next == -1) {
				if (size == 0) {
					return null;
				}
				throw new EOFException("Connection closed in the middle of a message");
			}
			
			if (size == buffer.length) {
				buffer = Arrays.copyOf(buffer, (int) Math.min((long) buffer.length * 2, limit));
			}
			buffer[size++] = (byte) next;
			if (size >= delimiter.length && Arrays.equals(buffer, size - delimiter.length, size, delimiter, 0, delimiter.length)) {
				return Arrays.copyOf(buffer, size - delimiter.length);
			}
			if (size >= limit) {
				throw new FrameTooLargeException(size, maxBytes);
			}
		}
	}
	
	/**
	 * Computes the maximum number of bytes a delimited message may occupy on the stream, including its delimiter.<br>
	 * The result is capped at {@link Integer#MAX_VALUE}, so a large limit does not overflow.<br>
	 *
	 * @param maxBytes The maximum length of a message in bytes, excluding the delimiter
	 * @param delimiterLength The length of the delimiter in bytes, including any slack the caller strips afterward
	 * @return The maximum number of bytes including the delimiter
	 * @throws IllegalArgumentException If max bytes or the delimiter length is less than 1
	 */
	private static int limitOf(int maxBytes, long delimiterLength) {
		if (maxBytes < 1) {
			throw new IllegalArgumentException("Max bytes must be at least 1: " + maxBytes);
		}
		if (delimiterLength < 1) {
			throw new IllegalArgumentException("Delimiter length must be at least 1: " + delimiterLength);
		}
		return (int) Math.min(maxBytes + delimiterLength, Integer.MAX_VALUE);
	}
	
	/**
	 * Writes the given message followed by the given suffix in a single write call and flushes the stream.<br>
	 *
	 * @param out The output stream to write to
	 * @param data The message to write
	 * @param suffix The bytes written after the message
	 * @throws NullPointerException If the output stream, the data or the suffix is null
	 * @throws IOException If an I/O error occurs while writing
	 */
	private static void writeWithSuffix(@NonNull OutputStream out, byte @NonNull [] data, byte @NonNull [] suffix) throws IOException {
		Objects.requireNonNull(out, "Output stream must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		
		byte[] frame = Arrays.copyOf(data, data.length + suffix.length);
		System.arraycopy(suffix, 0, frame, data.length, suffix.length);
		out.write(frame);
		out.flush();
	}
	
	/**
	 * Writes the given message to the given output stream and flushes it.<br>
	 * The message and its framing bytes are written in a single write call.<br>
	 *
	 * @param out The output stream to write to
	 * @param data The message to write
	 * @throws NullPointerException If the output stream or the data is null
	 * @throws IOException If an I/O error occurs while writing
	 */
	void write(@NonNull OutputStream out, byte @NonNull [] data) throws IOException;
	
	/**
	 * Reads a single message from the given input stream (blocking).<br>
	 * <p>
	 *     A message that is longer than the given limit is rejected instead of being read into memory.<br>
	 *     If the stream ends cleanly before the first byte of a message, null is returned.<br>
	 *     If it ends in the middle of a message, an {@link EOFException} is thrown.
	 * </p>
	 *
	 * @param in The input stream to read from
	 * @param maxBytes The maximum length of a message in bytes
	 * @return The message without its framing bytes, or null if the stream ended between messages
	 * @throws NullPointerException If the input stream is null
	 * @throws IllegalArgumentException If max bytes is less than 1
	 * @throws FrameTooLargeException If the message is longer than max bytes
	 * @throws EOFException If the stream ends in the middle of a message
	 * @throws IOException If an I/O error occurs while reading
	 */
	byte @Nullable [] read(@NonNull InputStream in, int maxBytes) throws IOException;
	
	/**
	 * The framing that writes and reads raw bytes without message boundaries.<br>
	 * <p>
	 *     A receive returns whatever is currently available, up to the configured limit.<br>
	 *     A single receive may therefore hold several messages or only a part of one.<br>
	 *     Use the streams of the connection for protocols that delimit messages themselves.
	 * </p>
	 *
	 * @author Luis-St
	 */
	record None() implements Framing {
		
		@Override
		public void write(@NonNull OutputStream out, byte @NonNull [] data) throws IOException {
			Objects.requireNonNull(out, "Output stream must not be null");
			Objects.requireNonNull(data, "Data must not be null");
			
			out.write(data);
			out.flush();
		}
		
		@Override
		public byte @Nullable [] read(@NonNull InputStream in, int maxBytes) throws IOException {
			validateRead(in, maxBytes);
			
			int first = in.read();
			if (first == -1) {
				return null;
			}
			
			int available = Math.min(Math.max(in.available(), 0), maxBytes - 1);
			byte[] data = new byte[1 + available];
			data[0] = (byte) first;
			int read = in.readNBytes(data, 1, available);
			return read == available ? data : Arrays.copyOf(data, 1 + read);
		}
	}
	
	/**
	 * The framing that prefixes every message with its length as a 4-byte big-endian integer.<br>
	 * Each receive returns exactly the bytes of one send, regardless of how the stream fragments or coalesces them.<br>
	 *
	 * @author Luis-St
	 */
	record LengthPrefixed() implements Framing {
		
		/**
		 * The size of the length header in bytes.<br>
		 */
		private static final int HEADER_SIZE = Integer.BYTES;
		
		@Override
		public void write(@NonNull OutputStream out, byte @NonNull [] data) throws IOException {
			Objects.requireNonNull(out, "Output stream must not be null");
			Objects.requireNonNull(data, "Data must not be null");
			
			int length = data.length;
			byte[] frame = new byte[HEADER_SIZE + length];
			frame[0] = (byte) (length >>> 24);
			frame[1] = (byte) (length >>> 16);
			frame[2] = (byte) (length >>> 8);
			frame[3] = (byte) length;
			System.arraycopy(data, 0, frame, HEADER_SIZE, length);
			
			out.write(frame);
			out.flush();
		}
		
		@Override
		public byte @Nullable [] read(@NonNull InputStream in, int maxBytes) throws IOException {
			validateRead(in, maxBytes);
			
			byte[] header = new byte[HEADER_SIZE];
			int headerRead = in.readNBytes(header, 0, HEADER_SIZE);
			if (headerRead == 0) {
				return null;
			}
			if (headerRead < HEADER_SIZE) {
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
	}
	
	/**
	 * The framing that terminates every message with a fixed byte sequence.<br>
	 * <p>
	 *     The delimiter is appended on write and stripped on read.<br>
	 *     It must not occur inside a message, because the message would be split at that position.
	 * </p>
	 *
	 * @author Luis-St
	 *
	 * @param delimiter The byte sequence that terminates a message
	 */
	record Delimited(byte @NonNull [] delimiter) implements Framing {
		
		/**
		 * Constructs a new delimited framing.<br>
		 * The delimiter is copied, so later changes to the given array do not affect the framing.<br>
		 *
		 * @param delimiter The byte sequence that terminates a message
		 * @throws NullPointerException If the delimiter is null
		 * @throws IllegalArgumentException If the delimiter is empty
		 */
		public Delimited {
			Objects.requireNonNull(delimiter, "Delimiter must not be null");
			if (delimiter.length == 0) {
				throw new IllegalArgumentException("Delimiter must not be empty");
			}
			delimiter = delimiter.clone();
		}
		
		/**
		 * Returns a copy of the byte sequence that terminates a message.<br>
		 * @return The delimiter
		 */
		@Override
		public byte @NonNull [] delimiter() {
			return this.delimiter.clone();
		}
		
		@Override
		public void write(@NonNull OutputStream out, byte @NonNull [] data) throws IOException {
			writeWithSuffix(out, data, this.delimiter);
		}
		
		@Override
		public byte @Nullable [] read(@NonNull InputStream in, int maxBytes) throws IOException {
			return readUntil(in, this.delimiter, maxBytes, 0);
		}
		
		//region Object overrides
		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (!(object instanceof Delimited that)) return false;
			
			return Arrays.equals(this.delimiter, that.delimiter);
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(this.delimiter);
		}
		
		@Override
		public String toString() {
			return "Delimited[delimiter=" + Arrays.toString(this.delimiter) + "]";
		}
		//endregion
	}
	
	/**
	 * The framing that terminates every message with a line break, as used by text protocols such as SMTP.<br>
	 * <p>
	 *     A message is written followed by {@code CRLF}.<br>
	 *     A read ends at the next {@code LF} and strips a {@code CR} in front of it, so peers that terminate lines with a bare {@code LF} are understood as well.<br>
	 *     The stripped {@code CR} does not count toward the maximum message length.
	 * </p>
	 *
	 * @author Luis-St
	 */
	record Lines() implements Framing {
		
		/**
		 * The line break that is written after every message.<br>
		 */
		private static final byte[] CRLF = { '\r', '\n' };
		/**
		 * The byte that ends a line when reading.<br>
		 */
		private static final byte[] LF = { '\n' };
		
		@Override
		public void write(@NonNull OutputStream out, byte @NonNull [] data) throws IOException {
			writeWithSuffix(out, data, CRLF);
		}
		
		@Override
		public byte @Nullable [] read(@NonNull InputStream in, int maxBytes) throws IOException {
			byte[] line = readUntil(in, LF, maxBytes, 1);
			if (line == null) {
				return null;
			}
			if (line.length > 0 && line[line.length - 1] == '\r') {
				line = Arrays.copyOf(line, line.length - 1);
			}
			if (line.length > maxBytes) {
				throw new FrameTooLargeException(line.length, maxBytes);
			}
			return line;
		}
	}
}
