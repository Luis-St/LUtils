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
import net.luis.utils.io.codec.constraint_new.UUIDConstraint;
import net.luis.utils.io.codec.constraint_new.core.UUIDVariant;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for UUID constraints.<br>
 * <p>
 *     This record stores the constraint values for {@link UUIDConstraint}.<br>
 *     It includes base constraints, version constraints, variant constraints, and special UUID validations.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The UUID equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The UUID set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param version The UUID version number to constrain to
 * @param variant The UUID variant to constrain to
 * @param nil If present, requires the UUID to be the nil UUID
 * @param notNil If present, requires the UUID to not be the nil UUID
 * @param max If present, requires the UUID to be the max UUID
 * @param custom A custom constraint implementation
 */
public record UUIDConstraintConfig(
	@NonNull Optional<Pair<UUID, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<UUID>, Boolean>> in,
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
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact UUID that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withEqualTo(@NonNull UUID value) {
		return new UUIDConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The UUID that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNotEqualTo(@NonNull UUID value) {
		return new UUIDConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of UUIDs that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withIn(@NonNull Collection<UUID> values) {
		return new UUIDConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of UUIDs that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNotIn(@NonNull Collection<UUID> values) {
		return new UUIDConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.version, this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified version constraint.<br>
	 *
	 * @param version The UUID version number to constrain to
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withVersion(int version) {
		return new UUIDConstraintConfig(this.equalTo, this.in, Optional.of(version), this.variant, this.nil, this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the specified variant constraint.<br>
	 *
	 * @param variant The UUID variant to constrain to
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withVariant(@NonNull UUIDVariant variant) {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, Optional.of(Objects.requireNonNull(variant)), this.nil, this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the nil UUID constraint enabled.<br>
	 * <p>
	 *     The nil UUID is a special UUID with all bits set to zero (00000000-0000-0000-0000-000000000000).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNil() {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, Optional.of(null), this.notNil, this.max, this.custom);
	}

	/**
	 * Creates a new config with the not-nil UUID constraint enabled.<br>
	 * <p>
	 *     Requires the UUID to not be the nil UUID (00000000-0000-0000-0000-000000000000).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withNotNil() {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, this.nil, Optional.of(null), this.max, this.custom);
	}

	/**
	 * Creates a new config with the max UUID constraint enabled.<br>
	 * <p>
	 *     The max UUID is a special UUID with all bits set to one (ffffffff-ffff-ffff-ffff-ffffffffffff).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withMax() {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, this.nil, this.notNil, Optional.of(null), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull UUIDConstraintConfig withCustom(@NonNull Constraint<UUID> constraint) {
		return new UUIDConstraintConfig(this.equalTo, this.in, this.version, this.variant, this.nil, this.notNil, this.max, Optional.of(Objects.requireNonNull(constraint)));
	}
}
