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
 * Configuration record for zone offset type constraints.<br>
 * <p>
 *     This record stores the constraint values for zone offset codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, and hour-based constraints.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the zone offset and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of ZoneOffsets and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The zone offset equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The zone offset set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum zone offset constraint as a pair of (value, inclusive)
 * @param max The maximum zone offset constraint as a pair of (value, inclusive)
 * @param positive The positive constraint as a Boolean where false means positive and true means nonPositive
 * @param negative The negative constraint as a Boolean where false means negative and true means nonNegative
 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
 * @param hours A nested config for hour component constraints
 * @param custom A custom constraint implementation
 */
public record ZoneOffsetConstraintConfig(
	@NonNull Optional<Pair<ZoneOffset, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<ZoneOffset>, Boolean>> in,
	@NonNull Optional<Pair<ZoneOffset, Boolean>> min,
	@NonNull Optional<Pair<ZoneOffset, Boolean>> max,
	@NonNull Optional<Boolean> positive,
	@NonNull Optional<Boolean> negative,
	@NonNull Optional<Boolean> zero,
	@NonNull Optional<NumericFieldConstraintConfig> hours,
	@NonNull Optional<Constraint<ZoneOffset>> custom
) {
	
	/**
	 * An unconstrained zone offset configuration with no constraints applied.<br>
	 */
	public static final ZoneOffsetConstraintConfig UNCONSTRAINED = new ZoneOffsetConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new zone offset constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The zone offset equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The zone offset set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum zone offset constraint as a pair of (value, inclusive)
	 * @param max The maximum zone offset constraint as a pair of (value, inclusive)
	 * @param positive The positive constraint as a Boolean where false means positive and true means nonPositive
	 * @param negative The negative constraint as a Boolean where false means negative and true means nonNegative
	 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
	 * @param hours A nested config for hour component constraints
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If positive and negative constraints are both present
	 */
	public ZoneOffsetConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(positive, "Optional for 'positive' constraint must not be null");
		Objects.requireNonNull(negative, "Optional for 'negative' constraint must not be null");
		Objects.requireNonNull(zero, "Optional for 'zero' constraint must not be null");
		Objects.requireNonNull(hours, "Optional for 'hours' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
		
		if (positive.isPresent() && negative.isPresent()) {
			throw new IllegalArgumentException("Positive and negative constraints are mutually exclusive");
		}
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact zone offset that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withEqualTo(@NonNull ZoneOffset value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new ZoneOffsetConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The zone offset that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNotEqualTo(@NonNull ZoneOffset value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new ZoneOffsetConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of ZoneOffsets that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withIn(@NonNull Collection<ZoneOffset> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of ZoneOffsets that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNotIn(@NonNull Collection<ZoneOffset> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold zone offset (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withGreaterThan(@NonNull ZoneOffset value) {
		Objects.requireNonNull(value, "Value for 'greater than' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold zone offset (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withGreaterThanOrEqual(@NonNull ZoneOffset value) {
		Objects.requireNonNull(value, "Value for 'greater than or equal' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold zone offset (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withLessThan(@NonNull ZoneOffset value) {
		Objects.requireNonNull(value, "Value for 'less than' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold zone offset (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withLessThanOrEqual(@NonNull ZoneOffset value) {
		Objects.requireNonNull(value, "Value for 'less than or equal' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum zone offset (exclusive)
	 * @param max The maximum zone offset (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withBetween(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		Objects.requireNonNull(min, "Min value for 'between' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum zone offset (inclusive)
	 * @param max The maximum zone offset (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withBetweenOrEqual(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		Objects.requireNonNull(min, "Min value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between or equal' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.positive, this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withPositive() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(false), this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNonPositive() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(true), this.negative, this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNegative() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(false), this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withNonNegative() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(true), this.zero, this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the zero (UTC) constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withZero() {
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(false), this.hours, this.custom);
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
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(true), this.hours, this.custom);
	}
	
	/**
	 * Creates a new config with the specified hours constraint.<br>
	 *
	 * @param hoursConfig The numeric field constraint config for hours validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withHours(@NonNull NumericFieldConstraintConfig hoursConfig) {
		Objects.requireNonNull(hoursConfig, "Config for 'hours' constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(hoursConfig), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull ZoneOffsetConstraintConfig withCustom(@NonNull Constraint<ZoneOffset> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new ZoneOffsetConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.hours, Optional.of(constraint));
	}
}
