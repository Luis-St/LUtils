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

package net.luis.utils.io.codec.constraint_new.config;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.SizeConstraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Configuration record for size constraints on collection types.<br>
 * <p>
 *     This record stores the constraint values for {@link SizeConstraint}.<br>
 *     Each field is optional and represents a specific size constraint.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param min The minimum size constraint as a pair of (value, inclusive)
 * @param max The maximum size constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record SizeConstraintConfig(
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Constraint<Integer>> custom
) {
	
	/**
	 * An unconstrained size configuration with no constraints applied.<br>
	 */
	public static final SizeConstraintConfig UNCONSTRAINED = new SizeConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new size constraint config with the specified minimum size (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @return A new config with the minimum size constraint applied
	 */
	public @NonNull SizeConstraintConfig withMinSize(int minSize) {
		return new SizeConstraintConfig(Optional.of(Pair.of(minSize, true)), this.max, this.custom);
	}
	
	/**
	 * Creates a new size constraint config with the specified maximum size (inclusive).<br>
	 *
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the maximum size constraint applied
	 */
	public @NonNull SizeConstraintConfig withMaxSize(int maxSize) {
		return new SizeConstraintConfig(this.min, Optional.of(Pair.of(maxSize, true)), this.custom);
	}
	
	/**
	 * Creates a new size constraint config with the specified exact size.<br>
	 * <p>
	 *     This sets both min and max to the same value with inclusive bounds.
	 * </p>
	 *
	 * @param exactSize The exact size required
	 * @return A new config with the exact size constraint applied
	 */
	public @NonNull SizeConstraintConfig withExactSize(int exactSize) {
		return new SizeConstraintConfig(Optional.of(Pair.of(exactSize, true)), Optional.of(Pair.of(exactSize, true)), this.custom);
	}
	
	/**
	 * Creates a new size constraint config with the specified size range (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the size range constraint applied
	 */
	public @NonNull SizeConstraintConfig withSizeBetween(int minSize, int maxSize) {
		return new SizeConstraintConfig(Optional.of(Pair.of(minSize, true)), Optional.of(Pair.of(maxSize, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull SizeConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		return new SizeConstraintConfig(this.min, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
