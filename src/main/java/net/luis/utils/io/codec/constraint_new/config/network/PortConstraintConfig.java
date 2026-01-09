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
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param inRange The port range constraint as a pair of ((min, max), negated) where negated=false means inRange and negated=true means notInRange
 * @param type The enum constraint config for port type (system, registered, dynamic)
 * @param custom A custom constraint implementation
 */
public record PortConstraintConfig(
	@NonNull Optional<Pair<Integer, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Integer>, Boolean>> in,
	@NonNull Optional<Pair<Pair<Integer, Integer>, Boolean>> inRange,
	@NonNull Optional<EnumConstraintConfig<PortRange>> type,
	@NonNull Optional<Constraint<Integer>> custom
) {
	
	/**
	 * An unconstrained port configuration with no constraints applied.<br>
	 */
	public static final PortConstraintConfig UNCONSTRAINED = new PortConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact port value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withEqualTo(int value) {
		return new PortConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.inRange, this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The port value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotEqualTo(int value) {
		return new PortConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.inRange, this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of port values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withIn(@NonNull Collection<Integer> values) {
		return new PortConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.inRange, this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of port values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotIn(@NonNull Collection<Integer> values) {
		return new PortConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.inRange, this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified port range constraint (inclusive).<br>
	 *
	 * @param min The minimum port number (inclusive)
	 * @param max The maximum port number (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withInRange(int min, int max) {
		return new PortConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Pair.of(min, max), false)), this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified excluded port range constraint.<br>
	 *
	 * @param min The minimum port number of the excluded range (inclusive)
	 * @param max The maximum port number of the excluded range (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotInRange(int min, int max) {
		return new PortConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Pair.of(min, max), true)), this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified port type constraint.<br>
	 *
	 * @param config The enum constraint config for port type validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withType(@NonNull EnumConstraintConfig<PortRange> config) {
		return new PortConstraintConfig(this.equalTo, this.in, this.inRange, Optional.of(Objects.requireNonNull(config)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		return new PortConstraintConfig(this.equalTo, this.in, this.inRange, this.type, Optional.of(Objects.requireNonNull(constraint)));
	}
}
