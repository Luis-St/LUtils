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

package net.luis.utils.io.network.tls;

import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * The writing half of a TLS record layer on top of an existing output stream.<br>
 * <p>
 *     The stream starts out unprotected, so records are written as plaintext until a handshake layer installs keys through {@link #changeCipherSpec(TlsRecordCipher)}.<br>
 *     Installing keys also resets the record sequence number.
 * </p>
 * <p>
 *     Every write produces at least one record, and each record is written to the wrapped stream in a single call.<br>
 *     Writes longer than {@link TlsRecord#MAX_PLAINTEXT_LENGTH} bytes are split across several records.<br>
 *     Callers writing many small pieces should put a {@link java.io.BufferedOutputStream} in front of this stream.
 * </p>
 * <p>
 *     Once a fatal alert or a {@code close_notify} has been sent, nothing more can be written.<br>
 *     Closing this stream sends a {@code close_notify} if none was sent yet and closes the wrapped stream.<br>
 *     Every failure, including one of the wrapped stream, is reported as a {@link TlsException}.<br>
 *     Instances are not thread-safe.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * try (TlsOutputStream out = new TlsOutputStream(client.getOutputStream())) {
 *     // The handshake layer sends its messages unprotected and installs the keys afterward
 *     out.writeRecord(TlsContentType.HANDSHAKE, clientHello);
 *     out.flush();
 *     out.changeCipherSpec(suite.createCipher(clientKey, clientIv));
 *
 *     out.write(request);
 *     out.flush();
 * }
 * }</pre>
 *
 * @see TlsInputStream
 * @see TlsCipherSuite
 *
 * @author Luis-St
 */
public class TlsOutputStream extends OutputStream {
	
	/**
	 * The stream the records are written to.<br>
	 */
	private final OutputStream out;
	/**
	 * The cipher that protects the written records.<br>
	 */
	private TlsRecordCipher cipher = TlsRecordCipher.NULL;
	/**
	 * The sequence number of the next written record.<br>
	 */
	private long sequence;
	/**
	 * Whether a fatal alert or a {@code close_notify} has been sent.<br>
	 */
	private boolean outboundClosed;
	/**
	 * Whether this stream has been closed.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a new unprotected tls output stream on top of the given stream.<br>
	 *
	 * @param out The stream to write the records to
	 * @throws NullPointerException If the output stream is null
	 */
	public TlsOutputStream(@NonNull OutputStream out) {
		this.out = Objects.requireNonNull(out, "Output stream must not be null");
	}
	
	/**
	 * Installs the cipher for every record written from now on and resets the record sequence number.<br>
	 *
	 * @param cipher The cipher to install
	 * @throws NullPointerException If the cipher is null
	 */
	public void changeCipherSpec(@NonNull TlsRecordCipher cipher) {
		this.cipher = Objects.requireNonNull(cipher, "Cipher must not be null");
		this.sequence = 0;
	}
	
	@Override
	public void write(int value) throws TlsException {
		this.writeFragments(TlsContentType.APPLICATION_DATA, new byte[] { (byte) value }, 0, 1);
	}
	
	@Override
	public void write(byte @NonNull [] buffer, int offset, int length) throws TlsException {
		Objects.requireNonNull(buffer, "Buffer must not be null");
		Objects.checkFromIndexSize(offset, length, buffer.length);
		
		if (length > 0) {
			this.writeFragments(TlsContentType.APPLICATION_DATA, buffer, offset, length);
		}
	}
	
	/**
	 * Writes data of any content type, for use by a handshake layer.<br>
	 * The data is split across as many records as needed.<br>
	 *
	 * @param type The content type of the data
	 * @param data The data to write
	 * @throws NullPointerException If the type or the data is null
	 * @throws IllegalArgumentException If the data is empty and the type is not application data
	 * @throws TlsException If this stream is closed or writing to the wrapped stream fails
	 */
	public void writeRecord(@NonNull TlsContentType type, byte @NonNull [] data) throws TlsException {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		if (data.length == 0 && type != TlsContentType.APPLICATION_DATA) {
			throw new IllegalArgumentException("A " + type + " record must not be empty");
		}
		
		this.writeFragments(type, data, 0, data.length);
	}
	
	/**
	 * Sends an alert and flushes the wrapped stream.<br>
	 * After a fatal alert or a {@code close_notify} nothing more can be written.<br>
	 *
	 * @param alert The alert to send
	 * @throws NullPointerException If the alert is null
	 * @throws TlsException If this stream is closed or writing to the wrapped stream fails
	 */
	public void writeAlert(@NonNull TlsAlert alert) throws TlsException {
		Objects.requireNonNull(alert, "Alert must not be null");
		
		this.writeRecord(TlsContentType.ALERT, alert.encode());
		this.flush();
		if (alert.isTerminal()) {
			this.outboundClosed = true;
		}
	}
	
	/**
	 * Sends the single byte change cipher spec record and flushes the wrapped stream.<br>
	 * The matching keys still have to be installed through {@link #changeCipherSpec(TlsRecordCipher)}.<br>
	 *
	 * @throws TlsException If this stream is closed or writing to the wrapped stream fails
	 */
	public void writeChangeCipherSpec() throws TlsException {
		this.writeRecord(TlsContentType.CHANGE_CIPHER_SPEC, new byte[] { 1 });
		this.flush();
	}
	
	@Override
	public void flush() throws TlsException {
		try {
			this.out.flush();
		} catch (IOException e) {
			throw new TlsException("Failed to flush the transport", e);
		}
	}
	
	@Override
	public void close() throws TlsException {
		if (this.closed) {
			return;
		}
		
		try (this.out) {
			if (!this.outboundClosed) {
				this.writeAlert(TlsAlert.CLOSE_NOTIFY);
			}
		} catch (TlsException e) {
			throw e;
		} catch (IOException e) {
			throw new TlsException("Failed to close the transport", e);
		} finally {
			this.closed = true;
		}
	}
	
	/**
	 * Splits the given section into fragments and writes one record per fragment.<br>
	 *
	 * @param type The content type of the section
	 * @param data The array holding the section
	 * @param offset The index of the first byte of the section
	 * @param length The length of the section
	 * @throws NullPointerException If the type or the data is null
	 * @throws TlsException If this stream is closed, a terminal alert has been sent or writing to the wrapped stream fails
	 */
	private void writeFragments(@NonNull TlsContentType type, byte @NonNull [] data, int offset, int length) throws TlsException {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(data, "Data must not be null");
		if (this.closed) {
			throw new TlsException("Stream is closed");
		}
		if (this.outboundClosed) {
			throw new TlsException("A terminal alert has already been sent");
		}
		
		int written = 0;
		do {
			int count = Math.min(TlsRecord.MAX_PLAINTEXT_LENGTH, length - written);
			this.emit(type, CryptoBytes.slice(data, offset + written, count));
			written += count;
		} while (written < length);
	}
	
	/**
	 * Protects a single fragment and writes its record.<br>
	 *
	 * @param type The real content type of the fragment
	 * @param fragment The fragment to write
	 * @throws NullPointerException If the type or the fragment is null
	 * @throws TlsException If the sequence number is exhausted or writing to the wrapped stream fails
	 */
	private void emit(@NonNull TlsContentType type, byte @NonNull [] fragment) throws TlsException {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(fragment, "Fragment must not be null");
		if (this.sequence == Long.MAX_VALUE) {
			throw TlsAlertException.local(TlsAlertDescription.INTERNAL_ERROR, "Record sequence number exhausted, the keys must be updated");
		}
		
		byte[] body = this.cipher.encrypt(this.sequence++, type, fragment);
		try {
			this.out.write(CryptoBytes.concat(TlsRecord.header(this.cipher.outerContentType(type), body.length), body));
		} catch (IOException e) {
			throw new TlsException("Failed to write to the transport", e);
		}
	}
}
