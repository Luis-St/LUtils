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
import net.luis.utils.io.codec.constraint_new.LengthConstraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Configuration record for length constraints on types with measurable length (strings, arrays).<br>
 * <p>
 *     This record stores the constraint values for {@link LengthConstraint}.<br>
 *     Each field is optional and represents a specific length constraint.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param min The minimum length constraint as a pair of (value, inclusive)
 * @param max The maximum length constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record LengthConstraintConfig(
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Constraint<Integer>> custom
) {
	
	/**
	 * An unconstrained length configuration with no constraints applied.<br>
	 */
	public static final LengthConstraintConfig UNCONSTRAINED = new LengthConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new length constraint config with the specified minimum length (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the minimum length constraint applied
	 */
	public @NonNull LengthConstraintConfig withMinLength(int minLength) {
		return new LengthConstraintConfig(Optional.of(Pair.of(minLength, true)), this.max, this.custom);
	}
	
	/**
	 * Creates a new length constraint config with the specified maximum length (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the maximum length constraint applied
	 */
	public @NonNull LengthConstraintConfig withMaxLength(int maxLength) {
		return new LengthConstraintConfig(this.min, Optional.of(Pair.of(maxLength, true)), this.custom);
	}
	
	/**
	 * Creates a new length constraint config with the specified exact length.<br>
	 * <p>
	 *     This sets both min and max to the same value with inclusive bounds.
	 * </p>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the exact length constraint applied
	 */
	public @NonNull LengthConstraintConfig withExactLength(int exactLength) {
		return new LengthConstraintConfig(Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.custom);
	}
	
	/**
	 * Creates a new length constraint config with the specified length range (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the length range constraint applied
	 */
	public @NonNull LengthConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new LengthConstraintConfig(Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LengthConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		return new LengthConstraintConfig(this.min, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
