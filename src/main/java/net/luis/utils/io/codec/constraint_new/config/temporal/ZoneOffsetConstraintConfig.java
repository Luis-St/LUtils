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

package net.luis.utils.io.codec.constraint_new.config.temporal;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.time.ZoneOffset;
import java.util.*;

/**
 * Configuration record for ZoneOffset type constraints.<br>
 * <p>
 *     This record stores the constraint values for ZoneOffset codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, and hour-based constraints.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact ZoneOffset that should be matched
 * @param notEqualTo The ZoneOffset that should be excluded
 * @param in The set of ZoneOffsets that are allowed
 * @param notIn The set of ZoneOffsets that are not allowed
 * @param min The minimum ZoneOffset constraint as a pair of (value, inclusive)
 * @param max The maximum ZoneOffset constraint as a pair of (value, inclusive)
 * @param positive If present, requires the offset to be positive (east of UTC)
 * @param negative If present, requires the offset to be negative (west of UTC)
 * @param nonNegative If present, requires the offset to be non-negative
 * @param nonPositive If present, requires the offset to be non-positive
 * @param zero If present, requires the offset to be zero (UTC)
 * @param nonZero If present, requires the offset to be non-zero
 * @param hours A nested config for hour component constraints
 * @param custom A custom constraint implementation
 */
public record ZoneOffsetConstraintConfig(
	@NonNull Optional<ZoneOffset> equalTo,
	@NonNull Optional<ZoneOffset> notEqualTo,
	@NonNull Optional<Set<ZoneOffset>> in,
	@NonNull Optional<Set<ZoneOffset>> notIn,
	@NonNull Optional<Pair<ZoneOffset, Boolean>> min,
	@NonNull Optional<Pair<ZoneOffset, Boolean>> max,
	@NonNull Optional<Void> positive,
	@NonNull Optional<Void> negative,
	@NonNull Optional<Void> nonNegative,
	@NonNull Optional<Void> nonPositive,
	@NonNull Optional<Void> zero,
	@NonNull Optional<Void> nonZero,
	@NonNull Optional<NumericFieldConstraintConfig> hours,
	@NonNull Optional<Constraint<ZoneOffset>> custom
) {

	/**
	 * An unconstrained ZoneOffset configuration with no constraints applied.<br>
	 */
	public static final ZoneOffsetConstraintConfig UNCONSTRAINED = new ZoneOffsetConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact ZoneOffset that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withEqualTo(@NonNull ZoneOffset value) {
		return new ZoneOffsetConstraintConfig(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The ZoneOffset that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNotEqualTo(@NonNull ZoneOffset value) {
		return new ZoneOffsetConstraintConfig(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of ZoneOffsets that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withIn(@NonNull Collection<ZoneOffset> values) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of ZoneOffsets that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNotIn(@NonNull Collection<ZoneOffset> values) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold ZoneOffset (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withGreaterThan(@NonNull ZoneOffset value) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold ZoneOffset (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withGreaterThanOrEqual(@NonNull ZoneOffset value) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold ZoneOffset (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withLessThan(@NonNull ZoneOffset value) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold ZoneOffset (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withLessThanOrEqual(@NonNull ZoneOffset value) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum ZoneOffset (exclusive)
	 * @param max The maximum ZoneOffset (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withBetween(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum ZoneOffset (inclusive)
	 * @param max The maximum ZoneOffset (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withBetweenOrEqual(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withPositive() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, Optional.of(null), this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNegative() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, Optional.of(null), this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNonNegative() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, Optional.of(null), this.nonPositive, this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNonPositive() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, Optional.of(null), this.zero, this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the zero (UTC) constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withZero() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, Optional.of(null), this.nonZero, this.hours, this.custom);
	}

	/**
	 * Creates a new config with the UTC constraint enabled.<br>
	 * <p>
	 *     This is an alias for {@link #withZero()}.
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withUtc() {
		return this.withZero();
	}

	/**
	 * Creates a new config with the non-zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNonZero() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, Optional.of(null), this.hours, this.custom);
	}

	/**
	 * Creates a new config with the specified hours constraint.<br>
	 *
	 * @param hoursConfig The numeric field constraint config for hours validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withHours(@NonNull NumericFieldConstraintConfig hoursConfig) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, Optional.of(Objects.requireNonNull(hoursConfig)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withCustom(@NonNull Constraint<ZoneOffset> constraint) {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, this.positive, this.negative, this.nonNegative, this.nonPositive, this.zero, this.nonZero, this.hours, Optional.of(Objects.requireNonNull(constraint)));
	}
}
