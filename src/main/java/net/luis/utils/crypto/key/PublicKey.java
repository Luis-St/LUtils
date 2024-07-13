/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Implementation of a cryptographic public key.<br>
 * The key is a {@link BigInteger} value.<br>
 *
 * @author Luis-St
 */
public class PublicKey implements Key {
	
	/**
	 * The public key.<br>
	 */
	private final BigInteger key;
	/**
	 * The modulus of the public key.<br>
	 */
	private final BigInteger modulus;
	
	/**
	 * Constructs a new public key with the given key and modulus.<br>
	 * @param key The public key
	 * @param modulus The modulus of the public key
	 * @throws NullPointerException If the key or the modulus is null
	 */
	public PublicKey(@NotNull BigInteger key, @NotNull BigInteger modulus) {
		this.key = Objects.requireNonNull(key, "The key must not be null");
		this.modulus = Objects.requireNonNull(modulus, "The modulus must not be null");
	}
	
	/**
	 * @return The public key
	 */
	@Override
	public @NotNull BigInteger getKey() {
		return this.key;
	}
	
	/**
	 * @return The modulus of the public key
	 */
	@Override
	public @NotNull BigInteger getModulus() {
		return this.modulus;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof PublicKey publicKey)) return false;
		
		if (!this.key.equals(publicKey.key)) return false;
		return this.modulus.equals(publicKey.modulus);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.key, this.modulus);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.key);
	}
	//endregion
}
