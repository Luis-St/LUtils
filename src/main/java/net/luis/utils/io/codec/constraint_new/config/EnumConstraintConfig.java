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
import net.luis.utils.io.codec.constraint_new.config.matcher.ConstraintMatchers;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for enum-based constraints.<br>
 * <p>
 *     This record stores the constraint values for enum types.<br>
 *     It includes base constraints for equality and membership validation.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The enum type this config is for
 * @param equalTo The enum equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The enum set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param custom A custom constraint implementation
 */
public record EnumConstraintConfig<T extends Enum<T>>(
	@NonNull Optional<Pair<T, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<T>, Boolean>> in,
	@NonNull Optional<Constraint<T>> custom
) implements ConstraintConfig<T> {
	
	/**
	 * Constructs a new enum constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The enum equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The enum set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 */
	public EnumConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
	}
	
	/**
	 * Creates an unconstrained enum configuration with no constraints applied.<br>
	 *
	 * @param <T> The enum type
	 * @return An unconstrained enum constraint config
	 */
	public static <T extends Enum<T>> @NonNull EnumConstraintConfig<T> unconstrained() {
		return new EnumConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact enum value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withEqualTo(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new EnumConstraintConfig<>(Optional.of(Pair.of(value, false)), this.in, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The enum value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withNotEqualTo(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new EnumConstraintConfig<>(Optional.of(Pair.of(value, true)), this.in, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of enum values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withIn(@NonNull Collection<T> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new EnumConstraintConfig<>(this.equalTo, Optional.of(Pair.of(EnumSet.copyOf(values), false)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of enum values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withNotIn(@NonNull Collection<T> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new EnumConstraintConfig<>(this.equalTo, Optional.of(Pair.of(EnumSet.copyOf(values), true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withCustom(@NonNull Constraint<T> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new EnumConstraintConfig<>(this.equalTo, this.in, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull T value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
