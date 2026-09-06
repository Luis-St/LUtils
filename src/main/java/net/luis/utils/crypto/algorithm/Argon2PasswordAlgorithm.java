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
 * Argon2id, parameterised by memory, passes and parallelism.<br>
 * <p>
 *     Argon2id is the winner of the password hashing competition and the recommended default.<br>
 *     It is memory hard, so an attacker with custom hardware has to pay for memory rather than only for arithmetic,<br>
 *     which is what PBKDF2 fails to do.<br>
 *     The id variant is used because it resists both side channel and time memory tradeoff attacks.
 * </p>
 * <p>
 *     Memory is measured in kibibytes, and one derivation allocates that much for as long as it runs.<br>
 *     The bounds below exist because these numbers are read back from stored records,<br>
 *     where an unbounded value would let a record decide how much memory and time a later verification spends.
 * </p>
 *
 * @see PasswordAlgorithm
 *
 * @author Luis-St
 *
 * @param memory The memory cost in kibibytes
 * @param iterations The number of passes over the memory
 * @param parallelism The number of lanes to derive with
 */
public record Argon2PasswordAlgorithm(
	int memory,
	int iterations,
	int parallelism
) implements PasswordAlgorithm {
	
	/**
	 * The Argon2 version this library writes, which is 1.3 as a PHC version number.<br>
	 */
	public static final int VERSION = 19;
	/**
	 * The smallest memory cost that is accepted, in kibibytes.<br>
	 */
	public static final int MIN_MEMORY = 8;
	/**
	 * The largest memory cost that is accepted, in kibibytes, which is one gibibyte.<br>
	 */
	public static final int MAX_MEMORY = 1_048_576;
	/**
	 * The smallest number of passes that is accepted.<br>
	 */
	public static final int MIN_ITERATIONS = 1;
	/**
	 * The largest number of passes that is accepted.<br>
	 */
	public static final int MAX_ITERATIONS = 100;
	/**
	 * The smallest number of lanes that is accepted.<br>
	 */
	public static final int MIN_PARALLELISM = 1;
	/**
	 * The largest number of lanes that is accepted.<br>
	 */
	public static final int MAX_PARALLELISM = 16;
	
	/**
	 * Constructs a new Argon2id algorithm with the given cost parameters.<br>
	 * <p>
	 *     The memory cost is additionally required to be at least eight times the parallelism,<br>
	 *     which Argon2 itself requires because every lane needs four blocks in each of its two slices.
	 * </p>
	 *
	 * @throws IllegalArgumentException If a cost parameter is outside the accepted range, or the memory is too small for the parallelism
	 */
	public Argon2PasswordAlgorithm {
		if (memory < MIN_MEMORY || memory > MAX_MEMORY) {
			throw new IllegalArgumentException("Memory must be in [" + MIN_MEMORY + ", " + MAX_MEMORY + "] KiB, was " + memory);
		}
		if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) {
			throw new IllegalArgumentException("Iterations must be in [" + MIN_ITERATIONS + ", " + MAX_ITERATIONS + "], was " + iterations);
		}
		if (parallelism < MIN_PARALLELISM || parallelism > MAX_PARALLELISM) {
			throw new IllegalArgumentException("Parallelism must be in [" + MIN_PARALLELISM + ", " + MAX_PARALLELISM + "], was " + parallelism);
		}
		if (memory < 8 * parallelism) {
			throw new IllegalArgumentException("Memory must be at least 8 times the parallelism, was " + memory + " KiB for " + parallelism + " lanes");
		}
	}
	
	@Override
	public @NonNull String identifier() {
		return "argon2id";
	}
	
	@Override
	public @NonNull String jcaName() {
		return "ARGON2";
	}
	
	@Override
	public boolean requiresBouncyCastle() {
		return true;
	}
	
	@Override
	public @NonNull String encodeParameters() {
		return "v=" + VERSION + "$m=" + this.memory + ",t=" + this.iterations + ",p=" + this.parallelism;
	}
	
	@Override
	public boolean isWeakerThan(@NonNull PasswordAlgorithm current) {
		Objects.requireNonNull(current, "Current algorithm must not be null");
		
		if (!(current instanceof Argon2PasswordAlgorithm argon2)) {
			return true;
		}
		return this.memory < argon2.memory() || this.iterations < argon2.iterations() || this.parallelism < argon2.parallelism();
	}
	
	@Override
	public @NonNull PasswordAlgorithm parseParameters(@NonNull String @NonNull [] parameters) {
		Objects.requireNonNull(parameters, "Parameters must not be null");
		if (parameters.length != 2) {
			throw new IllegalArgumentException("Argon2id takes exactly two parameter sections, got " + parameters.length);
		}
		
		int version = PasswordAlgorithm.readInt(parameters[0], "v");
		if (version != VERSION) {
			throw new IllegalArgumentException("Unsupported Argon2 version " + version + ", only " + VERSION + " is written and read");
		}
		
		String[] costs = parameters[1].split(",", -1);
		if (costs.length != 3) {
			throw new IllegalArgumentException("Argon2id takes exactly three cost parameters, got " + costs.length);
		}
		
		return new Argon2PasswordAlgorithm(
			PasswordAlgorithm.readInt(costs[0], "m"),
			PasswordAlgorithm.readInt(costs[1], "t"),
			PasswordAlgorithm.readInt(costs[2], "p")
		);
	}
}
