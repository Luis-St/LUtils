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

import javax.security.auth.DestroyFailedException;
import java.security.PrivateKey;
import java.util.*;

/**
 * The private half of a hybrid key pair, holding one classical and one post-quantum component.<br>
 * <p>
 *     The encoded form matches {@link HybridPublicKey}: {@code length || classical || length || post-quantum}, with each length a four byte big-endian integer.
 * </p>
 * <p>
 *     Destroying this key destroys both components and reports which ones refused, rather than quietly doing nothing and leaving {@link #isDestroyed()} answering false forever.
 * </p>
 *
 * @see HybridPublicKey
 *
 * @author Luis-St
 *
 * @param classical The classical component
 * @param postQuantum The post-quantum component
 */
public record HybridPrivateKey(
	@NonNull PrivateKey classical,
	@NonNull PrivateKey postQuantum
) implements PrivateKey {
	
	/**
	 * Constructs a new hybrid private key.<br>
	 * @throws NullPointerException If the classical or the post-quantum component is null
	 */
	public HybridPrivateKey {
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
	
	@Override
	public void destroy() throws DestroyFailedException {
		List<String> failures = new ArrayList<>();
		for (PrivateKey component : List.of(this.classical, this.postQuantum)) {
			try {
				component.destroy();
			} catch (DestroyFailedException e) {
				failures.add(component.getAlgorithm() + ": " + e.getMessage());
			}
		}
		
		if (!failures.isEmpty()) {
			throw new DestroyFailedException("Could not destroy every component: " + String.join(", ", failures));
		}
	}
	
	@Override
	public boolean isDestroyed() {
		return this.classical.isDestroyed() && this.postQuantum.isDestroyed();
	}
	
	/**
	 * Returns a description of this key naming both component algorithms.<br>
	 * <p>
	 *     The generated record string is deliberately not used here.<br>
	 *     It would print both components, and a private key implementation is free to render its material in its own string form.
	 * </p>
	 *
	 * @return The description of this key
	 */
	@Override
	public @NonNull String toString() {
		return "HybridPrivateKey[" + this.classical.getAlgorithm() + "+" + this.postQuantum.getAlgorithm() + "]";
	}
}
