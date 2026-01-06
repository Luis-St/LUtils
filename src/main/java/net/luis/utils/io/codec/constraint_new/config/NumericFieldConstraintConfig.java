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
 * @param equalTo The exact value that should be matched
 * @param notEqualTo The value that should be excluded
 * @param in The set of values that are allowed
 * @param notIn The set of values that are not allowed
 * @param min The minimum value constraint as a pair of (value, inclusive)
 * @param max The maximum value constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record NumericFieldConstraintConfig(
	@NonNull Optional<Integer> equalTo,
	@NonNull Optional<Integer> notEqualTo,
	@NonNull Optional<Set<Integer>> in,
	@NonNull Optional<Set<Integer>> notIn,
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Constraint<Integer>> custom
) {

	/**
	 * An unconstrained numeric field configuration with no constraints applied.<br>
	 */
	public static final NumericFieldConstraintConfig UNCONSTRAINED = new NumericFieldConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withEqualTo(int value) {
		return new NumericFieldConstraintConfig(Optional.of(value), this.notEqualTo, this.in, this.notIn, this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withNotEqualTo(int value) {
		return new NumericFieldConstraintConfig(this.equalTo, Optional.of(value), this.in, this.notIn, this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withIn(@NonNull Collection<Integer> values) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withNotIn(@NonNull Collection<Integer> values) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withGreaterThan(int value) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(value, false)), this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withGreaterThanOrEqual(int value) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(value, true)), this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withLessThan(int value) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, Optional.of(Pair.of(value, false)), this.custom);
	}

	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withLessThanOrEqual(int value) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, Optional.of(Pair.of(value, true)), this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withBetween(int min, int max) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withBetweenOrEqual(int min, int max) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull NumericFieldConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		return new NumericFieldConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
