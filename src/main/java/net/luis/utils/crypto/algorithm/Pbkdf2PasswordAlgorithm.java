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
 * PBKDF2 over HMAC-SHA-512, parameterized by an iteration count.<br>
 * <p>
 *     This is the only password algorithm the JDK serves on its own, which is why it stays available.<br>
 *     It has no memory hardness, so an attacker with custom hardware gains far more against it than against the other two.<br>
 *     Prefer {@link Argon2PasswordAlgorithm} for anything written from now on.
 * </p>
 *
 * @see PasswordAlgorithm
 *
 * @author Luis-St
 *
 * @param iterations The number of iterations to derive with
 */
public record Pbkdf2PasswordAlgorithm(int iterations) implements PasswordAlgorithm {
	
	/**
	 * The smallest iteration count that is accepted.<br>
	 */
	public static final int MIN_ITERATIONS = 1_000;
	/**
	 * The largest iteration count that is accepted, which bounds the work a stored record can demand.<br>
	 */
	public static final int MAX_ITERATIONS = 10_000_000;
	
	/**
	 * Constructs a new PBKDF2 algorithm with the given iteration count.<br>
	 * @throws IllegalArgumentException If the iteration count is outside the accepted range
	 */
	public Pbkdf2PasswordAlgorithm {
		if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) {
			throw new IllegalArgumentException("Iteration count must be in [" + MIN_ITERATIONS + ", " + MAX_ITERATIONS + "], was " + iterations);
		}
	}
	
	@Override
	public @NonNull String identifier() {
		return "pbkdf2-sha512";
	}
	
	@Override
	public @NonNull String jcaName() {
		return "PBKDF2WithHmacSHA512";
	}
	
	@Override
	public boolean requiresBouncyCastle() {
		return false;
	}
	
	@Override
	public @NonNull String encodeParameters() {
		return "i=" + this.iterations;
	}
	
	@Override
	public boolean isWeakerThan(@NonNull PasswordAlgorithm current) {
		Objects.requireNonNull(current, "Current algorithm must not be null");
		return !(current instanceof Pbkdf2PasswordAlgorithm pbkdf2) || this.iterations < pbkdf2.iterations();
	}
	
	@Override
	public @NonNull PasswordAlgorithm parseParameters(@NonNull String @NonNull [] parameters) {
		Objects.requireNonNull(parameters, "Parameters must not be null");
		
		if (parameters.length != 1) {
			throw new IllegalArgumentException("PBKDF2 takes exactly one parameter section, got " + parameters.length);
		}
		return new Pbkdf2PasswordAlgorithm(PasswordAlgorithm.readInt(parameters[0], "i"));
	}
}
