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

package net.luis.utils.crypto;

import net.luis.utils.crypto.exception.AuthenticationException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.Objects;

/**
 * An input stream that unseals what {@link CryptoOutputStream} wrote.<br>
 * <p>
 *     Nothing larger than one chunk is ever held, and no byte is served before the chunk carrying it has been authenticated.<br>
 *     A chunk that does not authenticate fails the read rather than producing plaintext.
 * </p>
 * <p>
 *     Truncation is detected.<br>
 *     Every chunk carries a kind byte bound into its associated data, and this stream reports end of input only after the chunk marked final.<br>
 *     A source that simply stops is a {@link MalformedDataException}, never a clean end of stream.
 * </p>
 * <p>
 *     The header is read by the constructor, so constructing the stream already decapsulates and checks the key commitment.<br>
 *     A wrong key therefore fails immediately instead of on the first read.
 * </p>
 * <p>
 *     Closing this stream closes the source, matching every filter stream in the JDK.<br>
 *     Instances are not thread-safe.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // The records written by the CryptoOutputStream example, read back one at a time
 * try (CryptoInputStream sealed = new CryptoInputStream(recipient.getPrivate(), Files.newInputStream(source)); DataInputStream in = new DataInputStream(new BufferedInputStream(sealed))) {
 *     int count = in.readInt();
 *     for (int i = 0; i < count; i++) {
 *         accept(new AuditEntry(in.readLong(), in.readUTF()));
 *     }
 * }
 *
 * // Line by line, without the file ever being held whole
 * try (BufferedReader reader = new BufferedReader(new InputStreamReader(new CryptoInputStream(recipient.getPrivate(), Files.newInputStream(source)), StandardCharsets.UTF_8))) {
 *     reader.lines().filter(line -> line.startsWith("ERROR")).forEach(this::report);
 * }
 * }</pre>
 * <p>
 *     A line handed to that loop has already been authenticated, because the chunk carrying it was verified before any of its bytes were served.<br>
 *     What streaming cannot promise is that the rest of the payload will authenticate, so a consumer that must not act on a partial payload has to read to the end before acting on any of it.
 * </p>
 *
 * @see CryptoOutputStream
 * @see CryptoMessages
 *
 * @author Luis-St
 */
public class CryptoInputStream extends InputStream {
	
	/**
	 * The stream the sealed payload is read from.<br>
	 */
	private final InputStream in;
	/**
	 * The suite this stream was sealed with.<br>
	 */
	private final CryptoSuite suite;
	/**
	 * The cipher key of this stream, wiped when this stream is closed.<br>
	 */
	private final Secret key;
	/**
	 * The header and encapsulation, bound into the associated data of every chunk.<br>
	 */
	private final byte[] prologue;
	/**
	 * The fixed part of every chunk nonce.<br>
	 */
	private final byte[] noncePrefix;
	/**
	 * The authenticated plaintext of the chunk currently being served.<br>
	 */
	private byte[] buffer = CryptoBytes.EMPTY;
	/**
	 * How much of the buffer has already been served.<br>
	 */
	private int position;
	/**
	 * The counter of the next chunk, which is the varying part of its nonce.<br>
	 */
	private int counter;
	/**
	 * Whether the chunk marked final has been read.<br>
	 */
	private boolean finished;
	/**
	 * Whether this stream has been closed.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a stream unsealing the given source.<br>
	 * <p>
	 *     The header is read and the key commitment checked here, so a key that does not belong to this stream fails now rather than on the first read.
	 * </p>
	 *
	 * @param recipient The private key to decrypt with
	 * @param in The stream to read the sealed payload from
	 * @throws NullPointerException If the recipient or the input stream is null
	 * @throws MalformedDataException If the header is not readable
	 * @throws AuthenticationException If the header does not belong to this key
	 * @throws IOException If reading the header fails
	 */
	public CryptoInputStream(@NonNull PrivateKey recipient, @NonNull InputStream in) throws IOException {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(in, "Input stream must not be null");
		
		this.in = in;
		byte[] head = in.readNBytes(CryptoMessages.HEADER_LENGTH);
		CryptoMessages.Head parsed = CryptoMessages.Head.parse(head, CryptoMessages.STREAM_MAGIC);
		this.suite = parsed.suite();
		
		int prefixLength = this.suite.aead().nonceLength() - Integer.BYTES;
		byte[] encapsulation = in.readNBytes(this.suite.kem().encapsulationLength());
		this.noncePrefix = in.readNBytes(prefixLength);
		if (encapsulation.length != this.suite.kem().encapsulationLength() || this.noncePrefix.length != prefixLength) {
			throw new MalformedDataException("Truncated sealed stream header");
		}
		this.prologue = CryptoBytes.concat(head, encapsulation);
		
		try (Secret shared = Kems.decapsulate(this.suite.kem(), recipient, encapsulation); Secret material = CryptoMessages.deriveMaterial(this.suite, shared, parsed.messageId(), encapsulation)) {
			int keyLength = this.suite.aead().keyLength();
			byte[] commitment = CryptoBytes.slice(material.material(), keyLength, CryptoMessages.COMMITMENT_LENGTH);
			if (!CryptoBytes.equalsConstantTime(commitment, parsed.commitment())) {
				throw new AuthenticationException("Key commitment mismatch - the header does not belong to this key");
			}
			this.key = Secret.adopt(CryptoBytes.slice(material.material(), 0, keyLength));
		}
	}
	
	/**
	 * Returns the suite this stream was sealed with.<br>
	 * @return The suite
	 */
	public @NonNull CryptoSuite suite() {
		return this.suite;
	}
	
	@Override
	public int read() throws IOException {
		byte[] single = new byte[1];
		return this.read(single, 0, 1) == -1 ? -1 : single[0] & 0xFF;
	}
	
	@Override
	public int read(byte @NonNull [] target, int offset, int length) throws IOException {
		Objects.requireNonNull(target, "Target must not be null");
		Objects.checkFromIndexSize(offset, length, target.length);
		if (this.closed) {
			throw new IOException("Stream is closed");
		}
		
		if (length == 0) {
			return 0;
		}
		if (!this.fill()) {
			return -1;
		}
		
		int take = Math.min(length, this.buffer.length - this.position);
		System.arraycopy(this.buffer, this.position, target, offset, take);
		this.position += take;
		return take;
	}
	
	/**
	 * Returns how much authenticated plaintext can be read without touching the source.<br>
	 * <p>
	 *     This counts only what is left of the chunk currently buffered, so it reaches zero at every chunk boundary while the payload continues.<br>
	 *     It is not an end-of-stream test.<br>
	 *     Read until a read returns -1, or frame the payload so the reader knows how much to expect.
	 * </p>
	 *
	 * @return The number of buffered bytes
	 */
	@Override
	public int available() {
		return this.buffer.length - this.position;
	}
	
	/**
	 * Closes this stream and the source.<br>
	 * The cipher key and any buffered plaintext are wiped, and closing an already closed stream does nothing.<br>
	 *
	 * @throws IOException If closing the source fails
	 */
	@Override
	public void close() throws IOException {
		if (this.closed) {
			return;
		}
		
		this.closed = true;
		this.key.close();
		CryptoBytes.wipe(this.buffer);
		this.in.close();
	}
	
	/**
	 * Makes sure the buffer holds unserved plaintext, reading and authenticating the next chunk if it does not.<br>
	 *
	 * @return True if plaintext is available, false at the end of the payload
	 * @throws IOException If reading fails
	 */
	private boolean fill() throws IOException {
		while (this.position == this.buffer.length) {
			if (this.finished) {
				return false;
			}
			this.readChunk();
		}
		return true;
	}
	
	/**
	 * Reads, authenticates and buffers the next chunk.<br>
	 *
	 * @throws MalformedDataException If the stream is truncated or framed wrongly
	 * @throws AuthenticationException If the chunk does not authenticate under this key
	 * @throws IOException If reading fails
	 */
	private void readChunk() throws IOException {
		byte[] frame = this.in.readNBytes(1 + Integer.BYTES);
		if (frame.length == 0) {
			throw new MalformedDataException("Sealed stream ended without a final chunk (truncated)");
		}
		if (frame.length < 1 + Integer.BYTES) {
			throw new MalformedDataException("Truncated chunk header");
		}
		
		byte kind = frame[0];
		if (kind != CryptoMessages.CHUNK_MORE && kind != CryptoMessages.CHUNK_FINAL) {
			throw new MalformedDataException("Unknown chunk kind 0x" + String.format("%02X", kind));
		}
		int length = ByteBuffer.wrap(frame, 1, Integer.BYTES).getInt();
		if (length < this.suite.aead().tagLength() || length > CryptoMessages.CHUNK_SIZE + this.suite.aead().tagLength()) {
			throw new MalformedDataException("Implausible chunk length " + length);
		}
		
		byte[] ciphertext = this.in.readNBytes(length);
		if (ciphertext.length != length) {
			throw new MalformedDataException("Truncated chunk body");
		}
		
		byte[] plaintext = Aeads.decrypt(this.suite.aead(), this.key.toKey(this.suite.aead()), this.nonce(), ciphertext, this.aad(kind));
		CryptoBytes.wipe(this.buffer);
		this.buffer = plaintext;
		this.position = 0;
		if (kind == CryptoMessages.CHUNK_FINAL) {
			this.finished = true;
		} else {
			this.advance();
		}
	}
	
	/**
	 * Builds the nonce of the chunk currently being read.<br>
	 * @return The built nonce
	 */
	private byte @NonNull [] nonce() {
		return CryptoBytes.concat(this.noncePrefix, CryptoBytes.of(this.counter));
	}
	
	/**
	 * Builds the associated data of the chunk currently being read.<br>
	 * Binding the counter and the kind is what makes reordering, truncation and a flipped kind byte detectable.<br>
	 *
	 * @param kind The kind byte of the chunk
	 * @return The built associated data
	 */
	private byte @NonNull [] aad(byte kind) {
		return CryptoBytes.concat(this.prologue, CryptoBytes.of(this.counter), new byte[] { kind });
	}
	
	/**
	 * Advances the chunk counter, refusing to wrap.<br>
	 * A stream claiming more chunks than the counter can carry would repeat a nonce, so it is rejected instead.<br>
	 *
	 * @throws MalformedDataException If the counter would wrap
	 */
	private void advance() {
		if (this.counter == Integer.MAX_VALUE) {
			throw new MalformedDataException("Sealed stream exceeds the maximum number of chunks");
		}
		this.counter++;
	}
}
