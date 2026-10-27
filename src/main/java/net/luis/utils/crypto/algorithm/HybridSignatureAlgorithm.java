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

import java.util.Objects;

/**
 * A hybrid of a classical and a post-quantum signature scheme.<br>
 * <p>
 *     Both components sign the same message and both signatures have to verify, so a forgery requires breaking both schemes.<br>
 *     The two signatures travel together in one length-prefixed composite.
 * </p>
 *
 * @author Luis-St
 *
 * @param classical The classical component
 * @param postQuantum The post-quantum component
 */
public record HybridSignatureAlgorithm(
	@NonNull NativeSignatureAlgorithm classical,
	@NonNull NativeSignatureAlgorithm postQuantum
) implements SignatureAlgorithm {
	
	/**
	 * Constructs a new hybrid signature algorithm.<br>
	 * @throws NullPointerException If the classical or the post-quantum component is null
	 */
	public HybridSignatureAlgorithm {
		Objects.requireNonNull(classical, "Classical component must not be null");
		Objects.requireNonNull(postQuantum, "Post-quantum component must not be null");
	}
	
	@Override
	public @NonNull String name() {
		return this.classical.name() + "+" + this.postQuantum.name();
	}
	
	@Override
	public int publicKeyLength() {
		return this.classical.publicKeyLength() + this.postQuantum.publicKeyLength();
	}
	
	@Override
	public boolean isPostQuantum() {
		return true;
	}
	
	@Override
	public boolean requiresBouncyCastle() {
		return this.classical.requiresBouncyCastle() || this.postQuantum.requiresBouncyCastle();
	}
}
