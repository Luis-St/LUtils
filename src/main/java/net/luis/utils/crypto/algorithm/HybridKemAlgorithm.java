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
 * A hybrid of a classical and a post-quantum key encapsulation mechanism.<br>
 * <p>
 *     Both components are run and their secrets combined, so the result stays secure as long as either component holds.<br>
 *     This is the shape to deploy during the migration: it survives a break of the elliptic curve and equally a break of the lattice scheme.
 * </p>
 *
 * @author Luis-St
 *
 * @param classical The classical component
 * @param postQuantum The post-quantum component
 */
public record HybridKemAlgorithm(
	@NonNull DhKemAlgorithm classical,
	@NonNull NativeKemAlgorithm postQuantum
) implements KemAlgorithm {
	
	/**
	 * Constructs a new hybrid kem algorithm.<br>
	 * @throws NullPointerException If the classical or the post-quantum component is null
	 */
	public HybridKemAlgorithm {
		Objects.requireNonNull(classical, "Classical component must not be null");
		Objects.requireNonNull(postQuantum, "Post-quantum component must not be null");
	}
	
	@Override
	public @NonNull String name() {
		return this.classical.name() + "+" + this.postQuantum.name();
	}
	
	@Override
	public int encapsulationLength() {
		return this.classical.encapsulationLength() + this.postQuantum.encapsulationLength();
	}
	
	@Override
	public int publicKeyLength() {
		return this.classical.publicKeyLength() + this.postQuantum.publicKeyLength();
	}
	
	@Override
	public int sharedSecretLength() {
		return 32;
	}
	
	@Override
	public boolean isPostQuantum() {
		return true;
	}
}
