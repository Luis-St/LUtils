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
 * Scrypt, parameterised by a cost, a block size and a parallelism.<br>
 * <p>
 *     Scrypt is memory hard and predates Argon2id, which is the reason it is here.<br>
 *     Choose it when records have to stay readable by something that already speaks scrypt,<br>
 *     and choose {@link Argon2PasswordAlgorithm} otherwise.
 * </p>
 * <p>
 *     The memory a derivation allocates is 128 times the cost times the block size, in bytes,<br>
 *     so the two parameters have to be bounded together rather than one at a time.<br>
 *     A record carrying a large cost and a large block size would otherwise decide how much memory a later verification spends.
 * </p>
 *
 * @see PasswordAlgorithm
 *
 * @author Luis-St
 *
 * @param cost The cost parameter, which has to be a power of two
 * @param blockSize The block size parameter
 * @param parallelism The parallelism parameter
 */
public record ScryptPasswordAlgorithm(
	int cost,
	int blockSize,
	int parallelism
) implements PasswordAlgorithm {
	
	/**
	 * The smallest cost that is accepted.<br>
	 */
	public static final int MIN_COST = 1_024;
	/**
	 * The largest cost that is accepted.<br>
	 */
	public static final int MAX_COST = 1_048_576;
	/**
	 * The smallest block size that is accepted.<br>
	 */
	public static final int MIN_BLOCK_SIZE = 1;
	/**
	 * The largest block size that is accepted.<br>
	 */
	public static final int MAX_BLOCK_SIZE = 64;
	/**
	 * The smallest parallelism that is accepted.<br>
	 */
	public static final int MIN_PARALLELISM = 1;
	/**
	 * The largest parallelism that is accepted.<br>
	 */
	public static final int MAX_PARALLELISM = 16;
	/**
	 * The largest amount of memory a single derivation may demand, in bytes, which is one gibibyte.<br>
	 */
	public static final long MAX_MEMORY = 1_073_741_824L;
	
	/**
	 * Constructs a new scrypt algorithm with the given cost parameters.<br>
	 * @throws IllegalArgumentException If a cost parameter is outside the accepted range, the cost is not a power of two, or the combination would allocate more than the memory bound
	 */
	public ScryptPasswordAlgorithm {
		if (cost < MIN_COST || cost > MAX_COST) {
			throw new IllegalArgumentException("Cost must be in [" + MIN_COST + ", " + MAX_COST + "], was " + cost);
		}
		if (Integer.bitCount(cost) != 1) {
			throw new IllegalArgumentException("Cost must be a power of two, was " + cost);
		}
		if (blockSize < MIN_BLOCK_SIZE || blockSize > MAX_BLOCK_SIZE) {
			throw new IllegalArgumentException("Block size must be in [" + MIN_BLOCK_SIZE + ", " + MAX_BLOCK_SIZE + "], was " + blockSize);
		}
		if (parallelism < MIN_PARALLELISM || parallelism > MAX_PARALLELISM) {
			throw new IllegalArgumentException("Parallelism must be in [" + MIN_PARALLELISM + ", " + MAX_PARALLELISM + "], was " + parallelism);
		}
		
		long memory = 128L * cost * blockSize;
		if (memory > MAX_MEMORY) {
			throw new IllegalArgumentException("Cost " + cost + " with block size " + blockSize + " would allocate " + memory + " bytes, more than the bound of " + MAX_MEMORY);
		}
	}
	
	/**
	 * Returns the memory a single derivation with these parameters allocates, in bytes.<br>
	 * @return The memory in bytes
	 */
	public long memory() {
		return 128L * this.cost * this.blockSize;
	}
	
	@Override
	public @NonNull String identifier() {
		return "scrypt";
	}
	
	@Override
	public @NonNull String jcaName() {
		return "SCRYPT";
	}
	
	@Override
	public boolean requiresBouncyCastle() {
		return true;
	}
	
	@Override
	public @NonNull String encodeParameters() {
		return "ln=" + Integer.numberOfTrailingZeros(this.cost) + ",r=" + this.blockSize + ",p=" + this.parallelism;
	}
	
	@Override
	public boolean isWeakerThan(@NonNull PasswordAlgorithm current) {
		Objects.requireNonNull(current, "Current algorithm must not be null");
		
		if (!(current instanceof ScryptPasswordAlgorithm scrypt)) {
			return !(current instanceof Pbkdf2PasswordAlgorithm);
		}
		return this.cost < scrypt.cost() || this.blockSize < scrypt.blockSize() || this.parallelism < scrypt.parallelism();
	}
	
	@Override
	public @NonNull PasswordAlgorithm parseParameters(@NonNull String @NonNull [] parameters) {
		Objects.requireNonNull(parameters, "Parameters must not be null");
		if (parameters.length != 1) {
			throw new IllegalArgumentException("Scrypt takes exactly one parameter section, got " + parameters.length);
		}
		
		String[] costs = parameters[0].split(",", -1);
		if (costs.length != 3) {
			throw new IllegalArgumentException("Scrypt takes exactly three cost parameters, got " + costs.length);
		}
		
		int logCost = PasswordAlgorithm.readInt(costs[0], "ln");
		if (logCost < 1 || logCost > Integer.SIZE - 2) {
			throw new IllegalArgumentException("Cost exponent must be in [1, " + (Integer.SIZE - 2) + "], was " + logCost);
		}
		return new ScryptPasswordAlgorithm(1 << logCost, PasswordAlgorithm.readInt(costs[1], "r"), PasswordAlgorithm.readInt(costs[2], "p"));
	}
}
