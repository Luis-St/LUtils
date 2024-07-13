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

import net.luis.utils.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Implementation of a cryptographic key pair.<br>
 * The key pair consists of a public key and a private key.<br>
 *
 * @author Luis-St
 */
public class KeyPair extends Pair<PublicKey, PrivateKey> {
	
	/**
	 * Constructs a new key pair with the given public and private key.<br>
	 * @param publicKey The public key
	 * @param privateKey The private key
	 * @throws NullPointerException If the public key or the private key is null
	 * @throws IllegalArgumentException If the modulus of the public key and the private key is not equal
	 */
	public KeyPair(@NotNull PublicKey publicKey, @NotNull PrivateKey privateKey) {
		super(Objects.requireNonNull(publicKey, "The public key must not be null"), Objects.requireNonNull(privateKey, "The private key must not be null"));
		if (!publicKey.getModulus().equals(privateKey.getModulus())) {
			throw new IllegalArgumentException("The modulus of the public key and the private key must be equal");
		}
	}
	
	/**
	 * @return The public key of the key pair
	 */
	public @NotNull PublicKey getPublicKey() {
		return this.getFirst();
	}
	
	/**
	 * @return The private key of the key pair
	 */
	public @NotNull PrivateKey getPrivateKey() {
		return this.getSecond();
	}
}
