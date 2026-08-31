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

package net.luis.utils.crypto.algorithm;

import org.jspecify.annotations.NonNull;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * The authenticated encryption modes available in this library.<br>
 * <p>
 *     AES-128-GCM is deliberately absent.<br>
 *     A library that drops RSA on a 2030 horizon has no consistent reason to keep a 128-bit symmetric key.<br>
 *     If AES-128 is needed for external interop, that is a deliberate addition with a written justification, not a default.
 * </p>
 * <p>
 *     Every mode here takes a 96-bit nonce except XChaCha20-Poly1305, which takes 192 bits.<br>
 *     A nonce may never repeat under one key, so a 96-bit nonce is either counted or budgeted against {@link #randomNonceMessageLimit()}.<br>
 *     The wider nonce removes that bookkeeping, and GCM-SIV survives a repeat instead of preventing one.
 * </p>
 * <p>
 *     None of these modes are key-committing on their own.<br>
 *     A ciphertext can be constructed that authenticates under two different keys,<br>
 *     which matters wherever a reader tries more than one key against the same ciphertext.<br>
 *     Any such construction has to bind an explicit key commitment.
 * </p>
 *
 * @author Luis-St
 */
public enum AeadAlgorithm {
	
	/**
	 * AES-256 in Galois/Counter mode, served by the JDK.<br>
	 */
	AES_256_GCM("AES/GCM/NoPadding", "AES", 32, 12, 16, false),
	/**
	 * AES-256 in the nonce-misuse-resistant GCM-SIV mode, served by BouncyCastle.<br>
	 */
	AES_256_GCM_SIV("AES/GCM-SIV/NoPadding", "AES", 32, 12, 16, true),
	/**
	 * ChaCha20 with Poly1305, served by the JDK and faster than AES on hardware without AES-NI.<br>
	 */
	CHACHA20_POLY1305("ChaCha20-Poly1305", "ChaCha20", 32, 12, 16, false),
	/**
	 * ChaCha20 with Poly1305 over a 192-bit nonce, served by BouncyCastle.<br>
	 * <p>
	 *     The nonce is wide enough that generating it at random needs no counter and no message budget,<br>
	 *     which is the one thing none of the 96-bit-nonce modes can offer.
	 * </p>
	 */
	XCHACHA20_POLY1305("XChaCha20-Poly1305", "ChaCha20", 32, 24, 16, true);
	
	/**
	 * The JCA transformation name of this algorithm.<br>
	 */
	private final String jcaName;
	/**
	 * The JCA name of the key type of this algorithm.<br>
	 */
	private final String keyJcaName;
	/**
	 * The required key length of this algorithm in bytes.<br>
	 */
	private final int keyLength;
	/**
	 * The required nonce length of this algorithm in bytes.<br>
	 */
	private final int nonceLength;
	/**
	 * The authentication tag length of this algorithm in bytes.<br>
	 */
	private final int tagLength;
	/**
	 * Whether this algorithm needs BouncyCastle to be installed.<br>
	 */
	private final boolean requiresBouncyCastle;
	
	/**
	 * Constructs a new aead algorithm constant.<br>
	 *
	 * @param jcaName The JCA transformation name
	 * @param keyJcaName The JCA name of the key type
	 * @param keyLength The required key length in bytes
	 * @param nonceLength The required nonce length in bytes
	 * @param tagLength The authentication tag length in bytes
	 * @param requiresBouncyCastle Whether the algorithm needs BouncyCastle
	 */
	AeadAlgorithm(@NonNull String jcaName, @NonNull String keyJcaName, int keyLength, int nonceLength, int tagLength, boolean requiresBouncyCastle) {
		this.jcaName = jcaName;
		this.keyJcaName = keyJcaName;
		this.keyLength = keyLength;
		this.nonceLength = nonceLength;
		this.tagLength = tagLength;
		this.requiresBouncyCastle = requiresBouncyCastle;
	}
	
	/**
	 * Returns the JCA transformation name of this algorithm.<br>
	 * @return The JCA transformation name
	 */
	public @NonNull String jcaName() {
		return this.jcaName;
	}
	
	/**
	 * Returns the JCA name of the key type of this algorithm.<br>
	 * @return The JCA key name
	 */
	public @NonNull String keyJcaName() {
		return this.keyJcaName;
	}
	
	/**
	 * Returns the required key length of this algorithm in bytes.<br>
	 * @return The key length
	 */
	public int keyLength() {
		return this.keyLength;
	}
	
	/**
	 * Returns the required nonce length of this algorithm in bytes.<br>
	 * @return The nonce length
	 */
	public int nonceLength() {
		return this.nonceLength;
	}
	
	/**
	 * Returns the authentication tag length of this algorithm in bytes.<br>
	 * @return The tag length
	 */
	public int tagLength() {
		return this.tagLength;
	}
	
	/**
	 * Returns whether this algorithm needs BouncyCastle to be installed.<br>
	 * @return True if BouncyCastle is required
	 */
	public boolean requiresBouncyCastle() {
		return this.requiresBouncyCastle;
	}
	
	/**
	 * Returns the number of messages that may be encrypted under one key with random nonces.<br>
	 * <p>
	 *     For the 96-bit-nonce modes this is the birthday bound at a 2^-32 collision probability.<br>
	 *     GCM-SIV tolerates a repeated nonce, degrading only to leaking equality of plaintexts, and therefore carries no such limit.<br>
	 *     XChaCha20-Poly1305 carries none either, for the different reason that its birthday bound sits at 2^80 and does not fit in a long.
	 * </p>
	 *
	 * @return The message limit under one key
	 */
	public long randomNonceMessageLimit() {
		return switch (this) {
			case AES_256_GCM_SIV, XCHACHA20_POLY1305 -> Long.MAX_VALUE;
			case AES_256_GCM, CHACHA20_POLY1305 -> 1L << 32;
		};
	}
	
	/**
	 * Builds the parameter spec carrying the given nonce for this algorithm.<br>
	 * <p>
	 *     The GCM modes take the tag length alongside the nonce, the ChaCha20 modes take the nonce alone.<br>
	 *     BouncyCastle's GCM-SIV accepts a gcm parameter spec, which was verified against the provider rather than assumed.<br>
	 *     Its XChaCha20-Poly1305 accepts either spec, and is given the iv one that matches what the cipher actually is.
	 * </p>
	 *
	 * @param nonce The nonce to carry
	 * @return The built parameter spec
	 * @throws NullPointerException If the nonce is null
	 */
	public @NonNull AlgorithmParameterSpec parameterSpec(byte @NonNull [] nonce) {
		Objects.requireNonNull(nonce, "Nonce must not be null");
		
		return switch (this) {
			case CHACHA20_POLY1305, XCHACHA20_POLY1305 -> new IvParameterSpec(nonce);
			case AES_256_GCM, AES_256_GCM_SIV -> new GCMParameterSpec(this.tagLength * Byte.SIZE, nonce);
		};
	}
}
