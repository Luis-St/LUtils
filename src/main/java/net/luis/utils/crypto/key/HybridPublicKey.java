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

package net.luis.utils.crypto.key;

import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import java.security.PublicKey;
import java.util.Objects;

/**
 * The public half of a hybrid key pair, holding one classical and one post-quantum component.<br>
 * <p>
 *     The encoded form is {@code length || classical || length || post-quantum}, with each length a four byte big-endian integer.<br>
 *     No JCA key factory knows the composite algorithm name, so the encoding is decoded against the algorithm it belongs to rather than by name.
 * </p>
 * <p>
 *     Because the encoded form covers both components, a {@link KeyId} computed from a hybrid key identifies the pair as a whole.<br>
 *     Swapping either half produces a different id, which is intended.
 * </p>
 *
 * @see HybridPrivateKey
 *
 * @author Luis-St
 *
 * @param classical The classical component
 * @param postQuantum The post-quantum component
 */
public record HybridPublicKey(
	@NonNull PublicKey classical,
	@NonNull PublicKey postQuantum
) implements PublicKey {
	
	/**
	 * Constructs a new hybrid public key.<br>
	 * @throws NullPointerException If the classical or the post-quantum component is null
	 */
	public HybridPublicKey {
		Objects.requireNonNull(classical, "Classical component must not be null");
		Objects.requireNonNull(postQuantum, "Post-quantum component must not be null");
	}
	
	@Override
	public @NonNull String getAlgorithm() {
		return "Hybrid(" + this.classical.getAlgorithm() + "+" + this.postQuantum.getAlgorithm() + ")";
	}
	
	@Override
	public @NonNull String getFormat() {
		return "RAW-HYBRID";
	}
	
	@Override
	public byte @NonNull [] getEncoded() {
		byte[] first = this.classical.getEncoded();
		byte[] second = this.postQuantum.getEncoded();
		return CryptoBytes.concat(CryptoBytes.of(first.length), first, CryptoBytes.of(second.length), second);
	}
}
