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
import net.luis.utils.crypto.util.CryptoRandom;
import org.jspecify.annotations.NonNull;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.foreign.Arena;
import java.util.Objects;

/**
 * A byte array holding secret material that is wiped when the secret is closed.<br>
 * <p>
 *     Every internal derivation in this package produces a secret and closes it in a try-with-resources,<br>
 *     which makes the wipe structural instead of something a finally block has to remember.
 * </p>
 * <p>
 *     Wiping heap memory is the best effort: the garbage collector may already have copied the array elsewhere.<br>
 *     It closes the window, it does not eliminate it.<br>
 *     For material that must never appear in a heap dump, use {@link Arena#ofConfined()} instead.
 * </p>
 * <p>
 *     Instances are not thread-safe and must not be used after {@link #close()}.<br>
 *     Equality is deliberately not implemented: comparing secrets by value belongs in {@link CryptoBytes#equalsConstantTime(byte[], byte[])}, which does it without leaking timing.
 * </p>
 *
 * @author Luis-St
 */
public final class Secret implements AutoCloseable {
	
	/**
	 * The secret material held by this secret.<br>
	 */
	private final byte[] material;
	/**
	 * Whether this secret has been closed and its material wiped.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a new secret over the given array without copying it.<br>
	 *
	 * @param material The material to hold
	 * @throws NullPointerException If the material is null
	 */
	private Secret(byte @NonNull [] material) {
		this.material = Objects.requireNonNull(material, "Material must not be null");
	}
	
	/**
	 * Creates a secret which takes ownership of the given array.<br>
	 * The caller must neither retain nor mutate the array afterward, since it is wiped on close.<br>
	 *
	 * @param material The material to adopt
	 * @return The created secret
	 * @throws NullPointerException If the material is null
	 * @see #copyOf(byte[])
	 */
	public static @NonNull Secret adopt(byte @NonNull [] material) {
		return new Secret(material);
	}
	
	/**
	 * Creates a secret over a copy of the given array.<br>
	 * The caller keeps ownership of the original and remains responsible for wiping it.<br>
	 *
	 * @param material The material to copy
	 * @return The created secret
	 * @throws NullPointerException If the material is null
	 * @see #adopt(byte[])
	 */
	public static @NonNull Secret copyOf(byte @NonNull [] material) {
		Objects.requireNonNull(material, "Material must not be null");
		return new Secret(material.clone());
	}
	
	/**
	 * Creates a secret holding the given number of random bytes.<br>
	 *
	 * @param length The number of bytes to generate
	 * @return The created secret
	 * @throws IllegalArgumentException If the length is negative
	 */
	public static @NonNull Secret random(int length) {
		return new Secret(CryptoRandom.bytes(length));
	}
	
	/**
	 * Returns the secret material held by this secret.<br>
	 * The returned array is the live array, not a copy, and must not be retained past the close.<br>
	 *
	 * @return The secret material
	 * @throws IllegalStateException If this secret has already been closed
	 */
	public byte @NonNull [] material() {
		if (this.closed) {
			throw new IllegalStateException("Secret has already been closed");
		}
		return this.material;
	}
	
	/**
	 * Returns the length of the secret material in bytes.<br>
	 * <p>
	 *     Unlike {@link #material()} this stays readable after the close, deliberately: the length is not secret, and error messages built after a try-with-resources block still need it.
	 * </p>
	 *
	 * @return The length of the material
	 */
	public int length() {
		return this.material.length;
	}
	
	/**
	 * Wraps this secret's material in a JCA secret key for the given algorithm.<br>
	 * The key shares the array with this secret, so it stops being usable once this secret is closed.<br>
	 *
	 * @param algorithm The JCA algorithm name the key is for
	 * @return The wrapped key
	 * @throws NullPointerException If the algorithm is null
	 * @throws IllegalStateException If this secret has already been closed
	 */
	public @NonNull SecretKey toKey(@NonNull String algorithm) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return new SecretKeySpec(this.material(), algorithm);
	}
	
	/**
	 * Wipes the secret material.<br>
	 * Closing an already closed secret does nothing.<br>
	 */
	@Override
	public void close() {
		CryptoBytes.wipe(this.material);
		this.closed = true;
	}
	
	@Override
	public String toString() {
		return "Secret[" + this.material.length + " bytes]";
	}
}
