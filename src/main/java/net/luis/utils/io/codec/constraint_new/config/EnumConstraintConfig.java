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
 * @param equalTo The exact enum value that should be matched
 * @param notEqualTo The enum value that should be excluded
 * @param in The set of enum values that are allowed
 * @param notIn The set of enum values that are not allowed
 * @param custom A custom constraint implementation
 */
public record EnumConstraintConfig<T extends Enum<T>>(
	@NonNull Optional<T> equalTo,
	@NonNull Optional<T> notEqualTo,
	@NonNull Optional<Set<T>> in,
	@NonNull Optional<Set<T>> notIn,
	@NonNull Optional<Constraint<T>> custom
) {

	/**
	 * Creates an unconstrained enum configuration with no constraints applied.<br>
	 *
	 * @param <T> The enum type
	 * @return An unconstrained enum constraint config
	 */
	public static <T extends Enum<T>> @NonNull EnumConstraintConfig<T> unconstrained() {
		return new EnumConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		);
	}

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact enum value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withEqualTo(@NonNull T value) {
		return new EnumConstraintConfig<>(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The enum value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withNotEqualTo(@NonNull T value) {
		return new EnumConstraintConfig<>(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of enum values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withIn(@NonNull Collection<T> values) {
		return new EnumConstraintConfig<>(this.equalTo, this.notEqualTo, Optional.of(EnumSet.copyOf(values)), this.notIn, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of enum values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withNotIn(@NonNull Collection<T> values) {
		return new EnumConstraintConfig<>(this.equalTo, this.notEqualTo, this.in, Optional.of(EnumSet.copyOf(values)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull EnumConstraintConfig<T> withCustom(@NonNull Constraint<T> constraint) {
		return new EnumConstraintConfig<>(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Objects.requireNonNull(constraint)));
	}
}
