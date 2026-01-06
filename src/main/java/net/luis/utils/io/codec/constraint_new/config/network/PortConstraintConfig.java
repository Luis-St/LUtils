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

package net.luis.utils.io.codec.constraint_new.config.network;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.PortRange;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for network port constraints.<br>
 * <p>
 *     This record stores the constraint values for port codecs.<br>
 *     It includes base constraints and port-specific constraints such as range and type.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact port value that should be matched
 * @param notEqualTo The port value that should be excluded
 * @param in The set of port values that are allowed
 * @param notIn The set of port values that are not allowed
 * @param inRange The port range constraint as a pair of (min, max) inclusive
 * @param notInRange The excluded port range constraint as a pair of (min, max) inclusive
 * @param type The enum constraint config for port type (system, registered, dynamic)
 * @param custom A custom constraint implementation
 */
public record PortConstraintConfig(
	// BaseConstraint fields
	@NonNull Optional<Integer> equalTo,
	@NonNull Optional<Integer> notEqualTo,
	@NonNull Optional<Set<Integer>> in,
	@NonNull Optional<Set<Integer>> notIn,
	// PortConstraint fields
	@NonNull Optional<Pair<Integer, Integer>> inRange,
	@NonNull Optional<Pair<Integer, Integer>> notInRange,
	@NonNull Optional<EnumConstraintConfig<PortRange>> type,
	// Custom constraint
	@NonNull Optional<Constraint<Integer>> custom
) {

	/**
	 * An unconstrained port configuration with no constraints applied.<br>
	 */
	public static final PortConstraintConfig UNCONSTRAINED = new PortConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact port value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withEqualTo(int value) {
		return new PortConstraintConfig(Optional.of(value), this.notEqualTo, this.in, this.notIn, this.inRange, this.notInRange, this.type, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The port value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotEqualTo(int value) {
		return new PortConstraintConfig(this.equalTo, Optional.of(value), this.in, this.notIn, this.inRange, this.notInRange, this.type, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of port values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withIn(@NonNull Collection<Integer> values) {
		return new PortConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.inRange, this.notInRange, this.type, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of port values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotIn(@NonNull Collection<Integer> values) {
		return new PortConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.inRange, this.notInRange, this.type, this.custom);
	}

	/**
	 * Creates a new config with the specified port range constraint (inclusive).<br>
	 *
	 * @param min The minimum port number (inclusive)
	 * @param max The maximum port number (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withInRange(int min, int max) {
		return new PortConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(min, max)), this.notInRange, this.type, this.custom);
	}

	/**
	 * Creates a new config with the specified excluded port range constraint.<br>
	 *
	 * @param min The minimum port number of the excluded range (inclusive)
	 * @param max The maximum port number of the excluded range (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotInRange(int min, int max) {
		return new PortConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.inRange, Optional.of(Pair.of(min, max)), this.type, this.custom);
	}

	/**
	 * Creates a new config with the specified port type constraint.<br>
	 *
	 * @param config The enum constraint config for port type validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withType(@NonNull EnumConstraintConfig<PortRange> config) {
		return new PortConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.inRange, this.notInRange, Optional.of(Objects.requireNonNull(config)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		return new PortConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.inRange, this.notInRange, this.type, Optional.of(Objects.requireNonNull(constraint)));
	}
}
