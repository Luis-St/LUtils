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
import net.luis.utils.function.throwable.ThrowableConsumer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * The reading half of a TLS record layer on top of an existing input stream.<br>
 * <p>
 *     The stream starts out unprotected, like every TLS connection does, so records are read as plaintext<br>
 *     until a handshake layer installs keys through {@link #changeCipherSpec(TlsRecordCipher)}.<br>
 *     Installing keys also resets the record sequence number, exactly as the specification requires.
 * </p>
 * <p>
 *     The read methods only ever return application data.<br>
 *     Handshake and change cipher spec records that arrive in between are passed to the record handler before the next record is read,<br>
 *     so a handshake layer can install new keys in time for a TLS 1.3 key update.<br>
 *     Without a handler such a record fails the read with an {@code unexpected_message} alert.<br>
 *     During the handshake itself the handshake layer reads records of any type through {@link #readRecord()}.
 * </p>
 * <p>
 *     Received alerts are acted on.<br>
 *     A {@code close_notify} ends the stream, and a fatal alert fails the read with a {@link TlsAlertException}.<br>
 *     Once a read has failed, every further read fails as well, because the position in the record stream is lost.<br>
 *     Every failure, including one of the wrapped stream, is reported as a {@link TlsException}.
 * </p>
 * <p>
 *     This class implements the record layer only.<br>
 *     Negotiating keys needs both directions of the connection and therefore belongs in a handshake layer,<br>
 *     which drives this stream together with a {@link TlsOutputStream}.<br>
 *     Closing this stream closes the wrapped stream.<br>
 *     Instances are not thread-safe.
 * </p>
 * <p>
 *     The key and the IV of a direction are derived from its traffic secret.<br>
 *     TLS 1.3 uses {@code HKDF-Expand-Label} of RFC 8446 over the hash of the cipher suite, which the example below builds with {@link net.luis.utils.crypto.Kdfs}.<br>
 *     The traffic secrets themselves come out of the key schedule of the handshake layer.<br>
 *     TLS 1.2 derives its key block with its own PRF instead, which this library does not provide.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TcpClient client = TcpClient.connectTo(endpoint);
 * TlsInputStream in = new TlsInputStream(client.getInputStream());
 * TlsOutputStream out = new TlsOutputStream(client.getOutputStream());
 *
 * TlsCipherSuite suite = TlsCipherSuite.TLS_AES_128_GCM_SHA256;
 * KdfAlgorithm kdf = KdfAlgorithm.HKDF_SHA_256; // HKDF over suite.hash()
 * in.changeCipherSpec(createCipher(suite, kdf, serverTrafficSecret));
 * out.changeCipherSpec(createCipher(suite, kdf, clientTrafficSecret));
 * in.setRecordHandler(record -> handshake.handlePostHandshake(record, in, out));
 *
 * out.write("ping".getBytes(StandardCharsets.UTF_8));
 * out.flush();
 * byte[] reply = in.readNBytes(4);
 *
 * static TlsRecordCipher createCipher(TlsCipherSuite suite, KdfAlgorithm kdf, Secret trafficSecret) {
 *     try (Secret key = Kdfs.expand(kdf, trafficSecret, expandLabel("key", suite.keyLength()), suite.keyLength()); Secret iv = Kdfs.expand(kdf, trafficSecret, expandLabel("iv", suite.ivLength()), suite.ivLength())) {
 *         return suite.createCipher(key.material(), iv.material());
 *     }
 * }
 *
 * static byte[] expandLabel(String label, int length) {
 *     byte[] name = ("tls13 " + label).getBytes(StandardCharsets.US_ASCII);
 *     return CryptoBytes.concat(CryptoBytes.of((short) length), new byte[] { (byte) name.length }, name, new byte[] { 0 });
 * }
 * }</pre>
 *
 * @see TlsOutputStream
 * @see TlsCipherSuite
 *
 * @author Luis-St
 */
public class TlsInputStream extends InputStream {
	
	/**
	 * The stream the records are read from.<br>
	 */
	private final InputStream in;
	/**
	 * The cipher that removes the protection of the received records.<br>
	 */
	private TlsRecordCipher cipher = TlsRecordCipher.NULL;
	/**
	 * The sequence number of the next received record.<br>
	 */
	private long sequence;
	/**
	 * The handler receiving the records that are not application data while application data is read.<br>
	 */
	private ThrowableConsumer<TlsRecord, TlsException> recordHandler = record -> {
		throw TlsAlertException.local(TlsAlertDescription.UNEXPECTED_MESSAGE, "Received a " + record.type() + " record while reading application data");
	};
	/**
	 * Whether transport that ends without a {@code close_notify} alert is treated as a truncation attack.<br>
	 */
	private boolean requireCloseNotify = true;
	/**
	 * The application data of the record currently being served.<br>
	 */
	private byte[] buffer = CryptoBytes.EMPTY;
	/**
	 * How much of the buffer has already been served.<br>
	 */
	private int position;
	/**
	 * Whether the peer has sent a {@code close_notify} alert.<br>
	 */
	private boolean closeNotifyReceived;
	/**
	 * Whether no further records will be read, because the peer ended the connection.<br>
	 */
	private boolean inboundClosed;
	/**
	 * The failure of an earlier read, after which the stream is unusable.<br>
	 */
	private @Nullable TlsException failure;
	/**
	 * Whether this stream has been closed.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a new unprotected tls input stream on top of the given stream.<br>
	 *
	 * @param in The stream to read the records from
	 * @throws NullPointerException If the input stream is null
	 */
	public TlsInputStream(@NonNull InputStream in) {
		this.in = Objects.requireNonNull(in, "Input stream must not be null");
	}
	
	/**
	 * Installs the cipher for the records the peer sends from now on and resets the record sequence number.<br>
	 *
	 * @param cipher The cipher to install
	 * @throws NullPointerException If the cipher is null
	 */
	public void changeCipherSpec(@NonNull TlsRecordCipher cipher) {
		this.cipher = Objects.requireNonNull(cipher, "Cipher must not be null");
		this.sequence = 0;
	}
	
	/**
	 * Sets the handler for the records that are not application data and arrive while application data is read.<br>
	 * The handler is called before the next record is read, so it may install a new cipher.<br>
	 *
	 * @param recordHandler The handler to set
	 * @throws NullPointerException If the record handler is null
	 */
	public void setRecordHandler(@NonNull ThrowableConsumer<TlsRecord, TlsException> recordHandler) {
		this.recordHandler = Objects.requireNonNull(recordHandler, "Record handler must not be null");
	}
	
	/**
	 * Sets whether transport that ends without a {@code close_notify} alert is treated as a truncation attack.<br>
	 * This is enabled by default, and should only be disabled for protocols that detect truncation on their own.<br>
	 *
	 * @param requireCloseNotify True to fail a read on a missing {@code close_notify}, false to report a clean end of stream
	 */
	public void setRequireCloseNotify(boolean requireCloseNotify) {
		this.requireCloseNotify = requireCloseNotify;
	}
	
	/**
	 * Checks whether the peer has announced the end of its data with a {@code close_notify} alert.<br>
	 * @return True if a {@code close_notify} alert was received
	 */
	public boolean isCloseNotifyReceived() {
		return this.closeNotifyReceived;
	}
	
	/**
	 * Reads the next record of any content type, for use by a handshake layer.<br>
	 * Alerts are acted on instead of being returned.<br>
	 *
	 * @return An optional containing the next record, or an empty optional if the peer ended the connection
	 * @throws TlsAlertException If a record is malformed, fails to authenticate or is a fatal alert
	 * @throws TlsException If this stream is closed, an earlier read failed, the transport ends too early or reading from the transport fails
	 */
	public @NonNull Optional<TlsRecord> readRecord() throws TlsException {
		this.ensureOpen();
		if (this.failure != null) {
			throw new TlsException("An earlier read failed, the stream is unusable", this.failure);
		}
		
		try {
			return this.nextRecord();
		} catch (TlsException e) {
			this.failure = e;
			throw e;
		}
	}
	
	@Override
	public int read() throws TlsException {
		if (!this.fillBuffer()) {
			return -1;
		}
		return this.buffer[this.position++] & 0xFF;
	}
	
	@Override
	public int read(byte @NonNull [] target, int offset, int length) throws TlsException {
		Objects.requireNonNull(target, "Target must not be null");
		Objects.checkFromIndexSize(offset, length, target.length);
		if (length == 0) {
			return 0;
		}
		if (!this.fillBuffer()) {
			return -1;
		}
		
		int count = Math.min(length, this.buffer.length - this.position);
		System.arraycopy(this.buffer, this.position, target, offset, count);
		this.position += count;
		return count;
	}
	
	@Override
	public int available() throws TlsException {
		this.ensureOpen();
		return this.buffer.length - this.position;
	}
	
	@Override
	public void close() throws TlsException {
		if (this.closed) {
			return;
		}
		this.closed = true;
		this.buffer = CryptoBytes.EMPTY;
		this.position = 0;
		
		try {
			this.in.close();
		} catch (IOException e) {
			throw new TlsException("Failed to close the transport", e);
		}
	}
	
	/**
	 * Ensures that this stream has not been closed.<br>
	 * @throws TlsException If this stream is closed
	 */
	private void ensureOpen() throws TlsException {
		if (this.closed) {
			throw new TlsException("Stream is closed");
		}
	}
	
	/**
	 * Reads records until the buffer holds unread application data.<br>
	 * Records of other types are passed to the record handler.<br>
	 *
	 * @return True if application data is available, false if the peer ended the connection
	 * @throws TlsException If reading a record or handling one fails
	 */
	private boolean fillBuffer() throws TlsException {
		this.ensureOpen();
		while (this.position == this.buffer.length) {
			Optional<TlsRecord> next = this.readRecord();
			if (next.isEmpty()) {
				return false;
			}
			
			TlsRecord record = next.get();
			if (record.type() == TlsContentType.APPLICATION_DATA) {
				this.buffer = record.data();
				this.position = 0;
			} else {
				this.recordHandler.accept(record);
			}
		}
		return true;
	}
	
	/**
	 * Reads, unprotects and validates the next record that is not an alert.<br>
	 *
	 * @return An optional containing the next record, or an empty optional if the peer ended the connection
	 * @throws TlsException If the record is malformed, fails to authenticate, is a fatal alert or cannot be read
	 */
	private @NonNull Optional<TlsRecord> nextRecord() throws TlsException {
		while (!this.inboundClosed) {
			byte[] header = this.readTransport(TlsRecord.HEADER_LENGTH);
			if (header.length == 0) {
				this.inboundClosed = true;
				if (this.requireCloseNotify) {
					throw new TlsException("Transport ended without a close_notify alert");
				}
				return Optional.empty();
			}
			if (header.length < TlsRecord.HEADER_LENGTH) {
				throw new TlsException("Transport ended inside a record header");
			}
			
			int code = header[0] & 0xFF;
			TlsContentType type = TlsContentType.byCode(code).orElseThrow(
				() -> TlsAlertException.local(TlsAlertDescription.UNEXPECTED_MESSAGE, "Unknown record content type " + code)
			);
			int length = ((header[3] & 0xFF) << 8) | (header[4] & 0xFF);
			if (length > this.cipher.maxBodyLength()) {
				throw TlsAlertException.local(TlsAlertDescription.RECORD_OVERFLOW, "Record of " + length + " bytes exceeds the limit of " + this.cipher.maxBodyLength() + " bytes");
			}
			
			byte[] body = this.readTransport(length);
			if (body.length < length) {
				throw new TlsException("Transport ended inside a record body");
			}
			
			if (type == TlsContentType.CHANGE_CIPHER_SPEC && this.isTls13()) {
				if (length != 1 || body[0] != 1) {
					throw TlsAlertException.local(TlsAlertDescription.UNEXPECTED_MESSAGE, "Malformed change_cipher_spec record");
				}
				continue;
			}
			if (this.sequence == Long.MAX_VALUE) {
				throw TlsAlertException.local(TlsAlertDescription.INTERNAL_ERROR, "Record sequence number exhausted, the keys must be updated");
			}
			
			TlsRecord record = this.cipher.decrypt(this.sequence++, type, body);
			if (record.type() == TlsContentType.ALERT) {
				this.handleAlert(record);
			} else if (record.length() == 0 && record.type() != TlsContentType.APPLICATION_DATA) {
				throw TlsAlertException.local(TlsAlertDescription.UNEXPECTED_MESSAGE, "Received an empty " + record.type() + " record");
			} else {
				return Optional.of(record);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Reads up to the given number of bytes from the transport, blocking until they are read or the transport ends.<br>
	 *
	 * @param length The number of bytes to read
	 * @return The bytes read, which are fewer than requested only if the transport ended
	 * @throws TlsException If reading from the transport fails
	 */
	private byte @NonNull [] readTransport(int length) throws TlsException {
		try {
			return this.in.readNBytes(length);
		} catch (IOException e) {
			throw new TlsException("Failed to read from the transport", e);
		}
	}
	
	/**
	 * Acts on a received alert.<br>
	 * A {@code close_notify} ends the stream, a fatal alert fails the read and a warning is ignored.<br>
	 *
	 * @param record The alert record
	 * @throws NullPointerException If the record is null
	 * @throws TlsAlertException If the alert is malformed or fatal
	 */
	private void handleAlert(@NonNull TlsRecord record) throws TlsAlertException {
		Objects.requireNonNull(record, "Record must not be null");
		
		TlsAlert alert = TlsAlert.decode(record.data()).orElseThrow(
			() -> TlsAlertException.local(TlsAlertDescription.DECODE_ERROR, "Malformed alert record")
		);
		if (alert.description() == TlsAlertDescription.CLOSE_NOTIFY) {
			this.closeNotifyReceived = true;
			this.inboundClosed = true;
			return;
		}
		
		boolean fatal = alert.level() == TlsAlertLevel.FATAL || (this.isTls13() && alert.description() != TlsAlertDescription.USER_CANCELED);
		if (fatal) {
			this.inboundClosed = true;
			throw TlsAlertException.remote(alert);
		}
	}
	
	/**
	 * Checks whether the installed cipher protects records for TLS 1.3.<br>
	 * @return True if TLS 1.3 is in use
	 */
	private boolean isTls13() {
		return this.cipher.version().filter(TlsVersion.TLS_1_3::equals).isPresent();
	}
}
