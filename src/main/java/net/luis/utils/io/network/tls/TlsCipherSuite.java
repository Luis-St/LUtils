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

import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * The cipher suites the record layer of this package can protect records with.<br>
 * Each suite also names the key material a handshake layer has to derive for it.<br>
 * <p>
 *     Only AEAD suites are modeled.<br>
 *     The block cipher suites authenticate before they encrypt, which is structurally open to padding oracle attacks.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TlsCipherSuite suite = TlsCipherSuite.byId(serverHello.cipherSuite()).orElseThrow();
 *
 * byte[] key = deriveKey(suite.hash(), suite.keyLength());
 * byte[] iv = deriveIv(suite.hash(), suite.ivLength());
 * in.changeCipherSpec(suite.createCipher(key, iv));
 * }</pre>
 *
 * @see TlsAeadRecordCipher
 *
 * @author Luis-St
 */
public enum TlsCipherSuite {
	
	/**
	 * The TLS 1.3 suite with AES-128-GCM, which every TLS 1.3 peer must implement.<br>
	 */
	TLS_AES_128_GCM_SHA256(0x1301, TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, HashAlgorithm.SHA_256),
	/**
	 * The TLS 1.3 suite with AES-256-GCM.<br>
	 */
	TLS_AES_256_GCM_SHA384(0x1302, TlsVersion.TLS_1_3, AeadAlgorithm.AES_256_GCM, HashAlgorithm.SHA_384),
	/**
	 * The TLS 1.3 suite with ChaCha20-Poly1305.<br>
	 */
	TLS_CHACHA20_POLY1305_SHA256(0x1303, TlsVersion.TLS_1_3, AeadAlgorithm.CHACHA20_POLY1305, HashAlgorithm.SHA_256),
	/**
	 * The TLS 1.2 suite with ECDHE, ECDSA and AES-128-GCM.<br>
	 */
	TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256(0xC02B, TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM, HashAlgorithm.SHA_256),
	/**
	 * The TLS 1.2 suite with ECDHE, ECDSA and AES-256-GCM.<br>
	 */
	TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384(0xC02C, TlsVersion.TLS_1_2, AeadAlgorithm.AES_256_GCM, HashAlgorithm.SHA_384),
	/**
	 * The TLS 1.2 suite with ECDHE, RSA and AES-128-GCM.<br>
	 */
	TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256(0xC02F, TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM, HashAlgorithm.SHA_256),
	/**
	 * The TLS 1.2 suite with ECDHE, RSA and AES-256-GCM.<br>
	 */
	TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384(0xC030, TlsVersion.TLS_1_2, AeadAlgorithm.AES_256_GCM, HashAlgorithm.SHA_384),
	/**
	 * The TLS 1.2 suite with ECDHE, RSA and ChaCha20-Poly1305.<br>
	 */
	TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256(0xCCA8, TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305, HashAlgorithm.SHA_256),
	/**
	 * The TLS 1.2 suite with ECDHE, ECDSA and ChaCha20-Poly1305.<br>
	 */
	TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256(0xCCA9, TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305, HashAlgorithm.SHA_256);
	
	/**
	 * The two byte suite id used in the handshake messages.<br>
	 */
	private final int id;
	/**
	 * The only version this suite can be negotiated for.<br>
	 */
	private final TlsVersion version;
	/**
	 * The algorithm this suite protects records with.<br>
	 */
	private final AeadAlgorithm aead;
	/**
	 * The hash the key schedule or the PRF of this suite is built on.<br>
	 */
	private final HashAlgorithm hash;
	
	/**
	 * Constructs a new cipher suite constant.<br>
	 *
	 * @param id The two byte suite id
	 * @param version The only version this suite can be negotiated for
	 * @param aead The algorithm this suite protects records with
	 * @param hash The hash the key schedule or the PRF is built on
	 */
	TlsCipherSuite(int id, @NonNull TlsVersion version, @NonNull AeadAlgorithm aead, @NonNull HashAlgorithm hash) {
		this.id = id;
		this.version = Objects.requireNonNull(version, "Version must not be null");
		this.aead = Objects.requireNonNull(aead, "Aead must not be null");
		this.hash = Objects.requireNonNull(hash, "Hash must not be null");
	}
	
	/**
	 * Returns the cipher suite with the given two byte id.<br>
	 *
	 * @param id The two byte suite id
	 * @return An optional containing the matching suite, or an empty optional if the suite is not supported
	 */
	public static @NonNull Optional<TlsCipherSuite> byId(int id) {
		for (TlsCipherSuite suite : values()) {
			if (suite.id == id) {
				return Optional.of(suite);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Returns the two byte suite id used in the handshake messages.<br>
	 * @return The suite id
	 */
	public int id() {
		return this.id;
	}
	
	/**
	 * Returns the only version this suite can be negotiated for.<br>
	 * @return The version
	 */
	public @NonNull TlsVersion version() {
		return this.version;
	}
	
	/**
	 * Returns the algorithm this suite protects records with.<br>
	 * @return The AEAD algorithm
	 */
	public @NonNull AeadAlgorithm aead() {
		return this.aead;
	}
	
	/**
	 * Returns the hash the key schedule of TLS 1.3 or the PRF of TLS 1.2 is built on for this suite.<br>
	 * @return The hash algorithm
	 */
	public @NonNull HashAlgorithm hash() {
		return this.hash;
	}
	
	/**
	 * Returns the number of key bytes a handshake layer has to derive per direction.<br>
	 * @return The key length in bytes
	 */
	public int keyLength() {
		return this.aead.keyLength();
	}
	
	/**
	 * Returns the number of fixed IV bytes a handshake layer has to derive per direction.<br>
	 * This is four for the AES-GCM suites of TLS 1.2 and twelve for every other suite.<br>
	 *
	 * @return The IV length in bytes
	 */
	public int ivLength() {
		return TlsAeadRecordCipher.ivLength(this.version, this.aead);
	}
	
	/**
	 * Creates the record cipher for one direction of a connection.<br>
	 *
	 * @param key The traffic key of this direction
	 * @param iv The fixed IV of this direction
	 * @return The created cipher
	 * @throws NullPointerException If the key or the IV is null
	 * @throws IllegalArgumentException If the key or the IV has the wrong length
	 */
	public @NonNull TlsRecordCipher createCipher(byte @NonNull [] key, byte @NonNull [] iv) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(iv, "IV must not be null");
		
		return new TlsAeadRecordCipher(this.version, this.aead, key, iv);
	}
}
