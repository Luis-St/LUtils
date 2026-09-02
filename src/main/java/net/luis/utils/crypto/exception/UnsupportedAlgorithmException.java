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

package net.luis.utils.crypto.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Thrown when an algorithm is not available from any registered provider.<br>
 * <p>
 *     This is almost always a deployment problem rather than a programming error: the wrong JDK version, or a missing BouncyCastle installation for the algorithms that require it.
 * </p>
 * <p>
 *     Use {@link #forAlgorithm(String, Throwable)} to report a named algorithm.<br>
 *     The constructors take a message like every other exception, because a constructor that silently rewrote its first argument into a sentence would read as if it took a message anyway.
 * </p>
 *
 * @author Luis-St
 */
public class UnsupportedAlgorithmException extends CryptoException {
	
	/**
	 * Constructs a new unsupported algorithm exception with no details.<br>
	 */
	public UnsupportedAlgorithmException() {}
	
	/**
	 * Constructs a new unsupported algorithm exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public UnsupportedAlgorithmException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new unsupported algorithm exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public UnsupportedAlgorithmException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new unsupported algorithm exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public UnsupportedAlgorithmException(@Nullable Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new unsupported algorithm exception naming the algorithm which is not available.<br>
	 *
	 * @param algorithm The name of the algorithm which is not available
	 * @param cause The cause of the exception
	 * @return The created exception
	 * @throws NullPointerException If the algorithm is null
	 */
	public static @NonNull UnsupportedAlgorithmException forAlgorithm(@NonNull String algorithm, @Nullable Throwable cause) {
		Objects.requireNonNull(algorithm, "Algorithm must not be null");
		return new UnsupportedAlgorithmException("Algorithm '" + algorithm + "' is not available from any registered provider", cause);
	}
}
