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
import net.luis.utils.io.codec.constraint_new.DepthConstraint;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for depth constraints on recursive structures.<br>
 * <p>
 *     This record stores the constraint values for {@link DepthConstraint}.<br>
 *     Each field is optional and represents a specific depth constraint.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum depth constraint as a pair of (value, inclusive)
 * @param max The maximum depth constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record DepthConstraintConfig(
	@NonNull Optional<Pair<Integer, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Integer>, Boolean>> in,
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Constraint<Integer>> custom
) implements ConstraintConfig<Integer> {
	
	/**
	 * An unconstrained depth configuration with no constraints applied.<br>
	 */
	public static final DepthConstraintConfig UNCONSTRAINED = new DepthConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new depth constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum depth constraint as a pair of (value, inclusive)
	 * @param max The maximum depth constraint as a pair of (value, inclusive)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If the minimum depth is negative when present
	 * @throws IllegalArgumentException If the maximum depth is negative when present
	 * @throws IllegalArgumentException If the maximum depth is less than the minimum depth when both are present
	 * @throws IllegalArgumentException If min and max depth are equal but at least one bound is exclusive when both are present
	 */
	public DepthConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (min.isPresent() && min.get().getFirst() < 0) {
			throw new IllegalArgumentException("Minimum depth must not be negative when present, but got " + min.get().getFirst());
		}
		
		if (max.isPresent() && max.get().getFirst() < 0) {
			throw new IllegalArgumentException("Maximum depth must not be negative when present, but got " + max.get().getFirst());
		}
		
		if (min.isPresent() && max.isPresent()) {
			if (min.get().getFirst() > max.get().getFirst()) {
				throw new IllegalArgumentException("Maximum depth must not be less than minimum depth when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
			}
			if (min.get().getFirst().equals(max.get().getFirst()) && (!min.get().getSecond() || !max.get().getSecond())) {
				throw new IllegalArgumentException("Min and max depth are equal but at least one bound is exclusive when both are present");
			}
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull DepthConstraintConfig withEqualTo(int value) {
		return new DepthConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull DepthConstraintConfig withNotEqualTo(int value) {
		return new DepthConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DepthConstraintConfig withIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new DepthConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DepthConstraintConfig withNotIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new DepthConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new depth constraint config with the specified minimum depth (inclusive).<br>
	 *
	 * @param minDepth The minimum depth (inclusive)
	 * @return A new config with the minimum depth constraint applied
	 */
	public @NonNull DepthConstraintConfig withMinDepth(int minDepth) {
		return new DepthConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minDepth, true)), this.max, this.custom);
	}
	
	/**
	 * Creates a new depth constraint config with the specified maximum depth (inclusive).<br>
	 *
	 * @param maxDepth The maximum depth (inclusive)
	 * @return A new config with the maximum depth constraint applied
	 */
	public @NonNull DepthConstraintConfig withMaxDepth(int maxDepth) {
		return new DepthConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(maxDepth, true)), this.custom);
	}
	
	/**
	 * Creates a new depth constraint config with the specified exact depth.<br>
	 * <p>
	 *     This sets both min and max to the same value with inclusive bounds.
	 * </p>
	 *
	 * @param exactDepth The exact depth required
	 * @return A new config with the exact depth constraint applied
	 */
	public @NonNull DepthConstraintConfig withExactDepth(int exactDepth) {
		return new DepthConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(exactDepth, true)), Optional.of(Pair.of(exactDepth, true)), this.custom);
	}
	
	/**
	 * Creates a new depth constraint config with the specified depth range (inclusive).<br>
	 *
	 * @param minDepth The minimum depth (inclusive)
	 * @param maxDepth The maximum depth (inclusive)
	 * @return A new config with the depth range constraint applied
	 */
	public @NonNull DepthConstraintConfig withDepthBetween(int minDepth, int maxDepth) {
		return new DepthConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minDepth, true)), Optional.of(Pair.of(maxDepth, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DepthConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new DepthConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.min, this.max),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
