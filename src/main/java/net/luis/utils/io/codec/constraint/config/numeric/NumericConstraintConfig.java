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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintValidators;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for numeric field constraints (used in temporal field constraints).<br>
 * <p>
 *     This record stores the constraint values for numeric fields like day of month, year, hour, etc.<br>
 *     It includes base constraints and comparable constraints for integer values.
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
 * @param min The minimum value constraint as a pair of (value, inclusive)
 * @param max The maximum value constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record NumericConstraintConfig(
	@NonNull Optional<Pair<Integer, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Integer>, Boolean>> in,
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Constraint<Integer>> custom
) implements ConstraintConfig<Integer> {
	
	/**
	 * An unconstrained numeric field configuration with no constraints applied.<br>
	 */
	public static final NumericConstraintConfig UNCONSTRAINED = new NumericConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new numeric field constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum value constraint as a pair of (value, inclusive)
	 * @param max The maximum value constraint as a pair of (value, inclusive)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If min is greater than max when both are present
	 * @throws IllegalArgumentException If min equals max with at least one exclusive bound when both are present
	 */
	public NumericConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (min.isPresent() && max.isPresent()) {
			if (min.get().getFirst().compareTo(max.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
			}
			if (min.get().getFirst().compareTo(max.get().getFirst()) == 0 && (!min.get().getSecond() || !max.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
		}
	}
	
	@Override
	public boolean isUnconstrained() {
		return this.equals(UNCONSTRAINED);
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withEqualTo(int value) {
		return new NumericConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withNotEqualTo(int value) {
		return new NumericConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull NumericConstraintConfig withIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new NumericConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull NumericConstraintConfig withNotIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new NumericConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withGreaterThan(int value) {
		return new NumericConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withGreaterThanOrEqual(int value) {
		return new NumericConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withLessThan(int value) {
		return new NumericConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withLessThanOrEqual(int value) {
		return new NumericConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withBetween(int min, int max) {
		return new NumericConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericConstraintConfig withBetweenOrEqual(int min, int max) {
		return new NumericConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull NumericConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new NumericConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public void validate(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo),
			() -> ConstraintValidators.validateIn(value, this.in),
			() -> ConstraintValidators.validateRange(value, this.min, this.max),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
