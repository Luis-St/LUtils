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

import net.luis.utils.crypto.Aeads;
import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * Record protection for the AEAD cipher suites of TLS 1.2 and TLS 1.3.<br>
 * <p>
 *     Three nonce constructions exist and they are not interchangeable:
 * </p>
 * <ul>
 *     <li>The AES-GCM suites of TLS 1.2 (RFC 5288) concatenate a four byte salt with an eight byte explicit nonce, which is sent in front of the ciphertext</li>
 *     <li>The ChaCha20-Poly1305 suites of TLS 1.2 (RFC 7905) combine a twelve byte IV with the sequence number, and nothing is sent</li>
 *     <li>TLS 1.3 (RFC 8446) uses the same implicit nonce, authenticates the record header and appends the real content type to the plaintext</li>
 * </ul>
 * <p>
 *     The explicit nonce of TLS 1.2 is the sequence number, so no nonce ever repeats under one key.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TlsRecordCipher cipher = new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_256_GCM, key, iv);
 * out.changeCipherSpec(cipher);
 * }</pre>
 *
 * @see TlsCipherSuite#createCipher(byte[], byte[])
 * @see Aeads
 *
 * @author Luis-St
 */
public final class TlsAeadRecordCipher implements TlsRecordCipher {
	
	/**
	 * The length of the fixed IV of the TLS 1.2 AES-GCM suites in bytes.<br>
	 */
	private static final int SALT_LENGTH = 4;
	
	/**
	 * The version the records are protected for.<br>
	 */
	private final TlsVersion version;
	/**
	 * The algorithm the records are protected with.<br>
	 */
	private final AeadAlgorithm algorithm;
	/**
	 * The traffic key of this direction.<br>
	 */
	private final SecretKey key;
	/**
	 * The fixed IV of this direction.<br>
	 */
	private final byte[] iv;
	
	/**
	 * Constructs a new AEAD record cipher for one direction of a connection.<br>
	 *
	 * @param version The negotiated version
	 * @param algorithm The algorithm of the negotiated cipher suite
	 * @param key The traffic key of this direction
	 * @param iv The fixed IV of this direction
	 * @throws NullPointerException If the version, the algorithm, the key or the IV is null
	 * @throws IllegalArgumentException If the algorithm is not used by TLS, or the key or the IV has the wrong length
	 */
	public TlsAeadRecordCipher(@NonNull TlsVersion version, @NonNull AeadAlgorithm algorithm, byte @NonNull [] key, byte @NonNull [] iv) {
		this.version = Objects.requireNonNull(version, "Version must not be null");
		this.algorithm = Objects.requireNonNull(algorithm, "Algorithm must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(iv, "IV must not be null");
		
		int ivLength = ivLength(version, algorithm);
		if (iv.length != ivLength) {
			throw new IllegalArgumentException(algorithm + " requires an IV of " + ivLength + " bytes for " + version + ", got " + iv.length);
		}
		this.key = Aeads.key(algorithm, key.clone());
		this.iv = iv.clone();
	}
	
	/**
	 * Returns the length of the fixed IV the given version and algorithm require.<br>
	 *
	 * @param version The negotiated version
	 * @param algorithm The algorithm of the negotiated cipher suite
	 * @return The IV length in bytes
	 * @throws NullPointerException If the version or the algorithm is null
	 * @throws IllegalArgumentException If the algorithm is not used by TLS
	 */
	static int ivLength(@NonNull TlsVersion version, @NonNull AeadAlgorithm algorithm) {
		Objects.requireNonNull(version, "Version must not be null");
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		
		return switch (algorithm) {
			case AES_128_GCM, AES_256_GCM -> version == TlsVersion.TLS_1_2 ? SALT_LENGTH : algorithm.nonceLength();
			case CHACHA20_POLY1305 -> algorithm.nonceLength();
			default -> throw new IllegalArgumentException(algorithm + " is not used by any TLS cipher suite");
		};
	}
	
	/**
	 * Builds the additional data of a TLS 1.2 record, which is {@code seq_num || type || version || length}.<br>
	 *
	 * @param sequence The record sequence number
	 * @param type The content type of the record
	 * @param length The length of the plaintext fragment
	 * @return The additional data
	 * @throws NullPointerException If the type is null
	 */
	private static byte @NonNull [] additionalData(long sequence, @NonNull TlsContentType type, int length) {
		Objects.requireNonNull(type, "Type must not be null");
		return CryptoBytes.concat(CryptoBytes.of(sequence), new byte[] { (byte) type.code() }, CryptoBytes.of((short) TlsVersion.TLS_1_2.code()), CryptoBytes.of((short) length));
	}
	
	@Override
	public byte @NonNull [] encrypt(long sequence, @NonNull TlsContentType type, byte @NonNull [] fragment) {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(fragment, "Fragment must not be null");
		
		if (this.version == TlsVersion.TLS_1_3) {
			byte[] inner = CryptoBytes.concat(fragment, new byte[] { (byte) type.code() });
			byte[] header = TlsRecord.header(TlsContentType.APPLICATION_DATA, inner.length + this.algorithm.tagLength());
			return Aeads.encrypt(this.algorithm, this.key, this.implicitNonce(sequence), inner, header);
		}
		
		byte[] additionalData = additionalData(sequence, type, fragment.length);
		if (this.hasExplicitNonce()) {
			byte[] explicitNonce = CryptoBytes.of(sequence);
			return CryptoBytes.concat(explicitNonce, Aeads.encrypt(this.algorithm, this.key, CryptoBytes.concat(this.iv, explicitNonce), fragment, additionalData));
		}
		return Aeads.encrypt(this.algorithm, this.key, this.implicitNonce(sequence), fragment, additionalData);
	}
	
	@Override
	public @NonNull TlsRecord decrypt(long sequence, @NonNull TlsContentType type, byte @NonNull [] body) throws TlsAlertException {
		Objects.requireNonNull(type, "Type must not be null");
		Objects.requireNonNull(body, "Body must not be null");
		
		int prefixLength = this.hasExplicitNonce() ? Long.BYTES : 0;
		int plaintextLength = body.length - prefixLength - this.algorithm.tagLength();
		if (plaintextLength < 0) {
			throw TlsAlertException.local(TlsAlertDescription.BAD_RECORD_MAC, "Record " + sequence + " is too short to hold an authentication tag");
		}
		
		int maxPlaintextLength = this.version == TlsVersion.TLS_1_3 ? TlsRecord.MAX_PLAINTEXT_LENGTH + 1 : TlsRecord.MAX_PLAINTEXT_LENGTH;
		if (plaintextLength > maxPlaintextLength) {
			throw TlsAlertException.local(TlsAlertDescription.RECORD_OVERFLOW, "Record " + sequence + " has a plaintext of " + plaintextLength + " bytes");
		}
		
		if (this.version == TlsVersion.TLS_1_3) {
			return this.unwrapInnerPlaintext(sequence, this.open(sequence, this.implicitNonce(sequence), body, TlsRecord.header(type, body.length)));
		}
		
		byte[] nonce = this.hasExplicitNonce() ? CryptoBytes.concat(this.iv, CryptoBytes.slice(body, 0, Long.BYTES)) : this.implicitNonce(sequence);
		byte[] ciphertext = CryptoBytes.slice(body, prefixLength, body.length - prefixLength);
		return new TlsRecord(type, this.open(sequence, nonce, ciphertext, additionalData(sequence, type, plaintextLength)));
	}
	
	@Override
	public @NonNull Optional<TlsVersion> version() {
		return Optional.of(this.version);
	}
	
	@Override
	public @NonNull TlsContentType outerContentType(@NonNull TlsContentType type) {
		Objects.requireNonNull(type, "Type must not be null");
		return this.version == TlsVersion.TLS_1_3 ? TlsContentType.APPLICATION_DATA : type;
	}
	
	@Override
	public int maxBodyLength() {
		return this.version.maxCiphertextLength();
	}
	
	/**
	 * Checks whether the nonce of each record is sent in front of its ciphertext.<br>
	 * This is only the case for the AES-GCM suites of TLS 1.2.<br>
	 *
	 * @return True if the nonce is explicit
	 */
	private boolean hasExplicitNonce() {
		return this.iv.length == SALT_LENGTH;
	}
	
	/**
	 * Builds the implicit nonce of a record, which is the fixed IV combined with the sequence number.<br>
	 *
	 * @param sequence The record sequence number
	 * @return The nonce
	 */
	private byte @NonNull [] implicitNonce(long sequence) {
		return CryptoBytes.xor(this.iv, this.algorithm.sequenceNonce(sequence));
	}
	
	/**
	 * Decrypts and authenticates a ciphertext.<br>
	 *
	 * @param sequence The record sequence number, used for the error message
	 * @param nonce The nonce of the record
	 * @param ciphertext The ciphertext and its tag
	 * @param additionalData The additional data of the record
	 * @return The plaintext
	 * @throws NullPointerException If the nonce, the ciphertext or the additional data is null
	 * @throws TlsAlertException If the ciphertext does not authenticate
	 */
	private byte @NonNull [] open(long sequence, byte @NonNull [] nonce, byte @NonNull [] ciphertext, byte @NonNull [] additionalData) throws TlsAlertException {
		Objects.requireNonNull(nonce, "Nonce must not be null");
		Objects.requireNonNull(ciphertext, "Ciphertext must not be null");
		Objects.requireNonNull(additionalData, "Additional data must not be null");
		
		return Aeads.tryDecrypt(this.algorithm, this.key, nonce, ciphertext, additionalData).orElseThrow(
			() -> TlsAlertException.local(TlsAlertDescription.BAD_RECORD_MAC, "Record " + sequence + " failed to authenticate")
		);
	}
	
	/**
	 * Removes the padding and the content type from the inner plaintext of a TLS 1.3 record.<br>
	 *
	 * @param sequence The record sequence number, used for the error message
	 * @param inner The inner plaintext, which is the fragment followed by its content type and zero padding
	 * @return The record with its real content type
	 * @throws NullPointerException If the inner plaintext is null
	 * @throws TlsAlertException If the inner plaintext holds no content type or an unknown one
	 */
	private @NonNull TlsRecord unwrapInnerPlaintext(long sequence, byte @NonNull [] inner) throws TlsAlertException {
		Objects.requireNonNull(inner, "Inner plaintext must not be null");
		
		int end = inner.length - 1;
		while (end >= 0 && inner[end] == 0) {
			end--;
		}
		if (end < 0) {
			throw TlsAlertException.local(TlsAlertDescription.UNEXPECTED_MESSAGE, "Record " + sequence + " carries no inner content type");
		}
		
		int code = inner[end] & 0xFF;
		TlsContentType type = TlsContentType.byCode(code).orElseThrow(
			() -> TlsAlertException.local(TlsAlertDescription.UNEXPECTED_MESSAGE, "Record " + sequence + " has the unknown inner content type " + code)
		);
		return new TlsRecord(type, Arrays.copyOf(inner, end));
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.algorithm + "/" + this.version;
	}
	//endregion
}
