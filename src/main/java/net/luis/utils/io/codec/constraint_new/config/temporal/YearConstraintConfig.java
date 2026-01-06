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
 * Configuration record for Year type constraints.<br>
 * <p>
 *     This record stores the constraint values for Year codecs.<br>
 *     It includes base constraints and comparable constraints for year values.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact Year that should be matched
 * @param notEqualTo The Year that should be excluded
 * @param in The set of Years that are allowed
 * @param notIn The set of Years that are not allowed
 * @param min The minimum Year constraint as a pair of (value, inclusive)
 * @param max The maximum Year constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record YearConstraintConfig(
	@NonNull Optional<Year> equalTo,
	@NonNull Optional<Year> notEqualTo,
	@NonNull Optional<Set<Year>> in,
	@NonNull Optional<Set<Year>> notIn,
	@NonNull Optional<Pair<Year, Boolean>> min,
	@NonNull Optional<Pair<Year, Boolean>> max,
	@NonNull Optional<Constraint<Year>> custom
) {

	/**
	 * An unconstrained Year configuration with no constraints applied.<br>
	 */
	public static final YearConstraintConfig UNCONSTRAINED = new YearConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact Year that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withEqualTo(@NonNull Year value) {
		return new YearConstraintConfig(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The Year that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withNotEqualTo(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Years that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withIn(@NonNull Collection<Year> values) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Years that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withNotIn(@NonNull Collection<Year> values) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.min, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold Year (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withAfter(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Year (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withAfterOrEqual(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold Year (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBefore(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.custom);
	}

	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Year (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBeforeOrEqual(@NonNull Year value) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum Year (exclusive)
	 * @param max The maximum Year (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBetween(@NonNull Year min, @NonNull Year max) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum Year (inclusive)
	 * @param max The maximum Year (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withBetweenOrEqual(@NonNull Year min, @NonNull Year max) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull YearConstraintConfig withCustom(@NonNull Constraint<Year> constraint) {
		return new YearConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.min, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
