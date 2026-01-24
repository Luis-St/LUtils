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

package net.luis.utils.io.codec.constraint.config.io;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.config.matcher.IOMatchers;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.PortRange;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
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
) implements ConstraintConfig<Integer> {
	
	/**
	 * An unconstrained port configuration with no constraints applied.<br>
	 */
	public static final PortConstraintConfig UNCONSTRAINED = new PortConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new port constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param inRange The port range constraint as a pair of ((min, max), negated) where negated=false means inRange and negated=true means notInRange
	 * @param type The enum constraint config for port type (system, registered, dynamic)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If the equalTo port value is not between 0 and 65535 when present
	 * @throws IllegalArgumentException If any port value in the in constraint set is not between 0 and 65535 when present
	 * @throws IllegalArgumentException If the inRange min or max port value is not between 0 and 65535 when present
	 * @throws IllegalArgumentException If the inRange min is greater than max when both are present
	 */
	public PortConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(inRange, "Optional for 'in range' constraint must not be null");
		Objects.requireNonNull(type, "Optional for 'type' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (equalTo.isPresent() && (equalTo.get().getFirst() < 0 || equalTo.get().getFirst() > 65535)) {
			throw new IllegalArgumentException("Port must be between 0 and 65535 when present, but got " + equalTo.get().getFirst());
		}
		
		if (in.isPresent()) {
			for (Integer port : in.get().getFirst()) {
				if (port < 0 || port > 65535) {
					throw new IllegalArgumentException("Port must be between 0 and 65535 when present, but got " + port);
				}
			}
		}
		
		if (inRange.isPresent()) {
			if (inRange.get().getFirst().getFirst() < 0 || inRange.get().getFirst().getFirst() > 65535) {
				throw new IllegalArgumentException("Port must be between 0 and 65535 when present, but got " + inRange.get().getFirst().getFirst());
			}
			if (inRange.get().getFirst().getSecond() < 0 || inRange.get().getFirst().getSecond() > 65535) {
				throw new IllegalArgumentException("Port must be between 0 and 65535 when present, but got " + inRange.get().getFirst().getSecond());
			}
			if (inRange.get().getFirst().getFirst() > inRange.get().getFirst().getSecond()) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + inRange.get().getFirst().getFirst() + " > " + inRange.get().getFirst().getSecond());
			}
		}
	}
	
	//region With methods
	
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
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new PortConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.inRange, this.type, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of port values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withNotIn(@NonNull Collection<Integer> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
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
		Objects.requireNonNull(config, "Config for 'type' constraint must not be null");
		return new PortConstraintConfig(this.equalTo, this.in, this.inRange, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PortConstraintConfig withCustom(@NonNull Constraint<Integer> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new PortConstraintConfig(this.equalTo, this.in, this.inRange, this.type, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull Integer value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> IOMatchers.matchPortRange(value, this.inRange),
			() -> IOMatchers.matchPortType(value, this.type),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
