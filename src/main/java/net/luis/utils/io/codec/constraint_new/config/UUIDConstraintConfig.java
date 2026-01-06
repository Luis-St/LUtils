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
import net.luis.utils.io.codec.constraint_new.core.UUIDVariant;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for UUID constraints.<br>
 * <p>
 *     This record stores the constraint values for {@link net.luis.utils.io.codec.constraint_new.UUIDConstraint}.<br>
 *     It includes base constraints, version constraints, variant constraints, and special UUID validations.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact UUID value that should be matched
 * @param notEqualTo The UUID value that should be excluded
 * @param in The set of UUIDs that are allowed
 * @param notIn The set of UUIDs that are not allowed
 * @param version The UUID version number to constrain to
 * @param variant The UUID variant to constrain to
 * @param nil If present, requires the UUID to be the nil UUID
 * @param notNil If present, requires the UUID to not be the nil UUID
 * @param max If present, requires the UUID to be the max UUID
 * @param custom A custom constraint implementation
 */
public record UUIDConstraintConfig(
	@NonNull Optional<UUID> equalTo,
	@NonNull Optional<UUID> notEqualTo,
	@NonNull Optional<Set<UUID>> in,
	@NonNull Optional<Set<UUID>> notIn,
	@NonNull Optional<Integer> version,
	@NonNull Optional<UUIDVariant> variant,
	@NonNull Optional<Void> nil,
	@NonNull Optional<Void> notNil,
	@NonNull Optional<Void> max,
	@NonNull Optional<Constraint<UUID>> custom
) {

	/**
	 * An unconstrained UUID configuration with no constraints applied.<br>
	 */
	public static final UUIDConstraintConfig UNCONSTRAINED = new UUIDConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);

	public @NonNull UUIDConstraintConfig withEqualTo(@NonNull UUID value) {
		return new UUIDConstraintConfig(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withNotEqualTo(@NonNull UUID value) {
		return new UUIDConstraintConfig(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withIn(@NonNull Collection<UUID> values) {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withNotIn(@NonNull Collection<UUID> values) {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withVersion(int version) {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(version), this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withVariant(@NonNull UUIDVariant variant) {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.version, Optional.of(Objects.requireNonNull(variant)), this.nil, this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withNil() {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.version, this.variant, Optional.of(null), this.notNil, this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withNotNil() {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.version, this.variant, this.nil, Optional.of(null), this.max, this.custom);
	}

	public @NonNull UUIDConstraintConfig withMax() {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.version, this.variant, this.nil, this.notNil, Optional.of(null), this.custom);
	}

	public @NonNull UUIDConstraintConfig withCustom(@NonNull Constraint<UUID> constraint) {
		return new UUIDConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.version, this.variant, this.nil, this.notNil, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
