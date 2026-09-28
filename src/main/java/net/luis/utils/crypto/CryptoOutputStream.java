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

import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * An output stream that seals everything written to it to a public key.<br>
 * <p>
 *     This is the bounded-memory counterpart of {@link CryptoMessages}.<br>
 *     Nothing larger than one chunk is ever held, so a payload that does not fit in memory can still be encrypted.
 * </p>
 * <p>
 *     The payload is framed into chunks with an individually authenticated tag per chunk.<br>
 *     Each frame carries an explicit kind byte which is bound into the associated data, so a truncated stream is detected rather than mistaken for a short one.<br>
 *     The nonce of a chunk is {@code noncePrefix || counter}, where the prefix takes whatever the suite's cipher does not need for the counter.
 * </p>
 * <p>
 *     The header is the one {@link CryptoMessages} writes, under its own magic "LUCS", followed by the nonce prefix and the chunks.<br>
 *     A stream is therefore never mistaken for a single-recipient message, which was possible while both layouts shared a magic.
 * </p>
 * <p>
 *     The header is written by the constructor, so constructing the stream already encapsulates to the recipient and touches the target.<br>
 *     The final chunk is written by {@link #finish()} or {@link #close()}, and a stream that is never finished produces a truncated artifact that will not open.<br>
 *     Use try-with-resources.
 * </p>
 * <p>
 *     Closing this stream closes the target, matching {@link GZIPOutputStream}.<br>
 *     Call {@link #finish()} instead when the sealed payload is one section of a longer file or connection that has to stay open.
 * </p>
 * <p>
 *     Instances are not thread-safe.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * // Records written one at a time, so the serialised log never exists in memory
 * try (CryptoOutputStream sealed = new CryptoOutputStream(recipient.getPublic(), Files.newOutputStream(target)); DataOutputStream out = new DataOutputStream(new BufferedOutputStream(sealed))) {
 *     out.writeInt(entries.size());
 *     for (AuditEntry entry : entries) {
 *         out.writeLong(entry.timestamp());
 *         out.writeUTF(entry.actor());
 *     }
 * }
 *
 * // Compressed, then sealed, so the cipher never sees the redundancy
 * try (OutputStream out = new GZIPOutputStream(new CryptoOutputStream(recipient.getPublic(), target))) {
 *     out.write(report);
 * }
 *
 * // One section of a longer connection, which stays open for whatever follows
 * CryptoOutputStream sealed = new CryptoOutputStream(recipient.getPublic(), connection);
 * sealed.write(payload);
 * sealed.finish();
 * connection.write(theNextSection);
 * }</pre>
 * <p>
 *     Compressing before sealing shrinks the ciphertext, and it also leaks: the length of the result depends on how compressible the plaintext was.<br>
 *     Where an attacker can influence part of the payload and observe the size, that is enough to recover the rest, so compress only what is uniformly under your control.
 * </p>
 *
 * @see CryptoInputStream
 * @see CryptoMessages
 *
 * @author Luis-St
 */
public class CryptoOutputStream extends OutputStream {
	
	/**
	 * The stream the sealed payload is written to.<br>
	 */
	private final OutputStream out;
	/**
	 * The suite this stream is sealed with.<br>
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
	 * The plaintext of the chunk currently being filled.<br>
	 */
	private final byte[] buffer = new byte[CryptoMessages.CHUNK_SIZE];
	/**
	 * How many bytes of the buffer are filled.<br>
	 */
	private int buffered;
	/**
	 * The counter of the next chunk, which is the varying part of its nonce.<br>
	 */
	private int counter;
	/**
	 * Whether the final chunk has been written.<br>
	 */
	private boolean finished;
	/**
	 * Whether this stream has been closed.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a stream sealing to the given recipient with the current suite.<br>
	 *
	 * @param recipient The public key to encrypt to
	 * @param out The stream to write the sealed payload to
	 * @throws NullPointerException If the recipient or the output stream is null
	 * @throws IOException If writing the header fails
	 */
	public CryptoOutputStream(@NonNull PublicKey recipient, @NonNull OutputStream out) throws IOException {
		this(CryptoSuite.current(), recipient, out);
	}
	
	/**
	 * Constructs a stream sealing to the given recipient with the given suite.<br>
	 *
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param out The stream to write the sealed payload to
	 * @throws NullPointerException If the suite, the recipient or the output stream is null
	 * @throws IOException If writing the header fails
	 */
	public CryptoOutputStream(@NonNull CryptoSuite suite, @NonNull PublicKey recipient, @NonNull OutputStream out) throws IOException {
		this(CryptoRandom.instance(), suite, recipient, out);
	}
	
	/**
	 * Constructs a stream sealing to the given recipient, drawing the nonce prefix from the given source.<br>
	 * This constructor exists so the wire format can be tested against known answers with a fixed source.<br>
	 *
	 * @param random The source to draw the nonce prefix from
	 * @param suite The suite to encrypt with
	 * @param recipient The public key to encrypt to
	 * @param out The stream to write the sealed payload to
	 * @throws NullPointerException If the random source, the suite, the recipient or the output stream is null
	 * @throws IOException If writing the header fails
	 */
	public CryptoOutputStream(@NonNull SecureRandom random, @NonNull CryptoSuite suite, @NonNull PublicKey recipient, @NonNull OutputStream out) throws IOException {
		Objects.requireNonNull(random, "Random must not be null");
		Objects.requireNonNull(suite, "Suite must not be null");
		Objects.requireNonNull(recipient, "Recipient must not be null");
		Objects.requireNonNull(out, "Output stream must not be null");
		
		this.out = out;
		this.suite = suite;
		
		try (Kems.Encapsulation encapsulated = Kems.encapsulate(suite.kem(), recipient)) {
			UUID messageId = UUIDs.v7();
			
			try (Secret material = CryptoMessages.deriveMaterial(suite, encapsulated.sharedSecret(), messageId, encapsulated.encapsulation())) {
				int keyLength = suite.aead().keyLength();
				this.key = Secret.adopt(CryptoBytes.slice(material.material(), 0, keyLength));
				byte[] commitment = CryptoBytes.slice(material.material(), keyLength, CryptoMessages.COMMITMENT_LENGTH);
				this.prologue = CryptoBytes.concat(CryptoMessages.header(CryptoMessages.STREAM_MAGIC, suite, messageId, commitment), encapsulated.encapsulation());
			}
		}
		this.noncePrefix = CryptoRandom.bytes(random, noncePrefixLength(suite));
		
		this.out.write(this.prologue);
		this.out.write(this.noncePrefix);
	}
	
	/**
	 * Returns the length of the fixed part of a chunk nonce for the given suite.<br>
	 * <p>
	 *     The counter takes the trailing four bytes and the prefix takes the rest, so the nonce is always exactly as long as the suite's cipher requires.<br>
	 *     Deriving it rather than fixing it is what lets the wider nonce of XChaCha20-Poly1305 work here at all.
	 * </p>
	 *
	 * @param suite The suite the stream is sealed with
	 * @return The nonce prefix length in bytes
	 * @throws NullPointerException If the suite is null
	 */
	private static int noncePrefixLength(@NonNull CryptoSuite suite) {
		Objects.requireNonNull(suite, "Suite must not be null");
		return suite.aead().nonceLength() - Integer.BYTES;
	}
	
	/**
	 * Returns the suite this stream is sealed with.<br>
	 * @return The suite
	 */
	public @NonNull CryptoSuite suite() {
		return this.suite;
	}
	
	@Override
	public void write(int b) throws IOException {
		this.write(new byte[] { (byte) b }, 0, 1);
	}
	
	@Override
	public void write(byte @NonNull [] data, int offset, int length) throws IOException {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.checkFromIndexSize(offset, length, data.length);
		this.requireWritable();
		
		int written = 0;
		while (written < length) {
			int take = Math.min(length - written, this.buffer.length - this.buffered);
			System.arraycopy(data, offset + written, this.buffer, this.buffered, take);
			this.buffered += take;
			written += take;
			
			if (this.buffered == this.buffer.length) {
				this.writeChunk(CryptoMessages.CHUNK_MORE);
			}
		}
	}
	
	/**
	 * Seals whatever is buffered as a non-final chunk and flushes the target.<br>
	 * <p>
	 *     A chunk may be shorter than the chunk size, so flushing mid-payload is honoured rather than deferred.<br>
	 *     An empty buffer writes no chunk, so flushing repeatedly costs nothing.
	 * </p>
	 *
	 * @throws IOException If writing fails
	 */
	@Override
	public void flush() throws IOException {
		if (!this.finished) {
			if (this.buffered > 0) {
				this.writeChunk(CryptoMessages.CHUNK_MORE);
			}
			this.out.flush();
		}
	}
	
	/**
	 * Seals whatever is buffered as the final chunk and flushes the target, without closing it.<br>
	 * <p>
	 *     This is what makes a sealed payload embeddable in a longer file or connection.<br>
	 *     Calling it again does nothing, and {@link #close()} calls it for you.
	 * </p>
	 * <p>
	 *     A stream that is never finished produces a truncated artifact, which {@link CryptoInputStream} refuses to open.
	 * </p>
	 *
	 * @throws IOException If writing fails
	 */
	public void finish() throws IOException {
		if (!this.finished) {
			this.writeChunk(CryptoMessages.CHUNK_FINAL);
			this.finished = true;
			this.out.flush();
		}
	}
	
	/**
	 * Finishes this stream and closes the target.<br>
	 * The cipher key is wiped, and closing an already closed stream does nothing.<br>
	 *
	 * @throws IOException If writing or closing fails
	 */
	@Override
	public void close() throws IOException {
		if (this.closed) {
			return;
		}
		
		try {
			this.finish();
		} finally {
			this.closed = true;
			this.key.close();
			CryptoBytes.wipe(this.buffer);
			this.out.close();
		}
	}
	
	/**
	 * Seals the buffered plaintext as one chunk and writes its frame.<br>
	 *
	 * @param kind The kind byte of the chunk
	 * @throws IOException If writing fails
	 * @throws MalformedDataException If the chunk counter would wrap
	 */
	private void writeChunk(byte kind) throws IOException {
		byte[] plaintext = CryptoBytes.slice(this.buffer, 0, this.buffered);
		byte[] ciphertext;
		try {
			ciphertext = Aeads.encrypt(this.suite.aead(), this.key.toKey(this.suite.aead()), this.nonce(), plaintext, this.aad(kind));
		} finally {
			CryptoBytes.wipe(plaintext);
		}
		
		this.out.write(kind);
		this.out.write(CryptoBytes.of(ciphertext.length));
		this.out.write(ciphertext);
		this.buffered = 0;
		if (kind != CryptoMessages.CHUNK_FINAL) {
			this.advance();
		}
	}
	
	/**
	 * Builds the nonce of the chunk currently being written.<br>
	 * @return The built nonce
	 */
	private byte @NonNull [] nonce() {
		return CryptoBytes.concat(this.noncePrefix, CryptoBytes.of(this.counter));
	}
	
	/**
	 * Builds the associated data of the chunk currently being written.<br>
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
	 * <p>
	 *     The counter is the varying part of the nonce.<br>
	 *     Wrapping it would repeat a nonce under the same key, which for the modes that are not misuse resistant loses the authentication key outright,<br>
	 *     so the stream is rejected instead.
	 * </p>
	 *
	 * @throws MalformedDataException If the counter would wrap
	 */
	private void advance() {
		if (this.counter == Integer.MAX_VALUE) {
			throw new MalformedDataException("Sealed stream exceeds the maximum number of chunks");
		}
		this.counter++;
	}
	
	/**
	 * Requires that this stream still accepts writes.<br>
	 * @throws IOException If this stream has been finished or closed
	 */
	private void requireWritable() throws IOException {
		if (this.closed) {
			throw new IOException("Stream is closed");
		}
		if (this.finished) {
			throw new IOException("Stream is finished, no further payload can be sealed into it");
		}
	}
}
