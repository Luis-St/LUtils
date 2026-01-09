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
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.time.Year;
import java.util.*;

/**
 * Configuration record for year type constraints.<br>
 * <p>
 *     This record stores the constraint values for year codecs.<br>
 *     It includes base constraints and comparable constraints for year values.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the year and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of Years and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The year equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The year set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum year constraint as a pair of (value, inclusive)
 * @param max The maximum year constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record YearConstraintConfig(
	@NonNull Optional<Pair<Year, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Year>, Boolean>> in,
	@NonNull Optional<Pair<Year, Boolean>> min,
	@NonNull Optional<Pair<Year, Boolean>> max,
	@NonNull Optional<Constraint<Year>> custom
) {
	
	/**
	 * An unconstrained year configuration with no constraints applied.<br>
	 */
	public static final YearConstraintConfig UNCONSTRAINED = new YearConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new year constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The year equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The year set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum year constraint as a pair of (value, inclusive)
	 * @param max The maximum year constraint as a pair of (value, inclusive)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 */
	public YearConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact year that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withEqualTo(@NonNull Year value) {
		return new YearConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The year that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withNotEqualTo(@NonNull Year value) {
		return new YearConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Years that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withIn(@NonNull Collection<Year> values) {
		return new YearConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Years that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withNotIn(@NonNull Collection<Year> values) {
		return new YearConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold year (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withAfter(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold year (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withAfterOrEqual(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold year (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBefore(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold year (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBeforeOrEqual(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum year (exclusive)
	 * @param max The maximum year (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBetween(@NonNull Year min, @NonNull Year max) {
		return new YearConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum year (inclusive)
	 * @param max The maximum year (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBetweenOrEqual(@NonNull Year min, @NonNull Year max) {
		return new YearConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withCustom(@NonNull Constraint<Year> constraint) {
		return new YearConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
