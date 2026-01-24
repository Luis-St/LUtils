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
import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.config.matcher.IOMatchers;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.io.network.address.mac.MacAddress;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for MacAddress constraints.<br>
 * <p>
 *     This record stores the constraint values for MAC address validation.<br>
 *     It includes base constraints, address type constraints (unicast/multicast),
 *     administration constraints (universal/local), broadcast constraints, and string representation constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param unicast Constraint to require unicast addresses (I/G bit = 0)
 * @param multicast Constraint to require multicast addresses (I/G bit = 1)
 * @param universal Constraint to require universally administered addresses (U/L bit = 0)
 * @param local Constraint to require locally administered addresses (U/L bit = 1)
 * @param broadcast Constraint as a pair of (Unit, negated) where negated=false means must be broadcast and negated=true means must not be broadcast
 * @param stringConstraint String constraint config for validating the colon-separated string representation
 * @param custom A custom constraint implementation
 */
public record MacAddressConstraintConfig(
	@NonNull Optional<Pair<MacAddress, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<MacAddress>, Boolean>> in,
	@NonNull Optional<Unit> unicast,
	@NonNull Optional<Unit> multicast,
	@NonNull Optional<Unit> universal,
	@NonNull Optional<Unit> local,
	@NonNull Optional<Pair<Unit, Boolean>> broadcast,
	@NonNull Optional<StringConstraintConfig> stringConstraint,
	@NonNull Optional<Constraint<MacAddress>> custom
) implements ConstraintConfig<MacAddress> {
	
	/**
	 * An unconstrained configuration with no constraints applied.<br>
	 */
	public static final MacAddressConstraintConfig UNCONSTRAINED = new MacAddressConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new MAC address constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param unicast Constraint to require unicast addresses
	 * @param multicast Constraint to require multicast addresses
	 * @param universal Constraint to require universally administered addresses
	 * @param local Constraint to require locally administered addresses
	 * @param broadcast Constraint as a pair of (Unit, negated) where negated=false means must be broadcast and negated=true means must not be broadcast
	 * @param stringConstraint String constraint config for validating the colon-separated string representation
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If both unicast and multicast are present (mutually exclusive)
	 * @throws IllegalArgumentException If both universal and local are present (mutually exclusive)
	 */
	public MacAddressConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(unicast, "Optional for 'unicast' constraint must not be null");
		Objects.requireNonNull(multicast, "Optional for 'multicast' constraint must not be null");
		Objects.requireNonNull(universal, "Optional for 'universal' constraint must not be null");
		Objects.requireNonNull(local, "Optional for 'local' constraint must not be null");
		Objects.requireNonNull(broadcast, "Optional for 'broadcast' constraint must not be null");
		Objects.requireNonNull(stringConstraint, "Optional for 'string constraint' must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (unicast.isPresent() && multicast.isPresent()) {
			throw new IllegalArgumentException("Unicast and multicast constraints are mutually exclusive");
		}
		
		if (universal.isPresent() && local.isPresent()) {
			throw new IllegalArgumentException("Universal and local constraints are mutually exclusive");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull MacAddressConstraintConfig withEqualTo(@NonNull MacAddress value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new MacAddressConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.unicast, this.multicast, this.universal, this.local, this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull MacAddressConstraintConfig withNotEqualTo(@NonNull MacAddress value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new MacAddressConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.unicast, this.multicast, this.universal, this.local, this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull MacAddressConstraintConfig withIn(@NonNull Collection<MacAddress> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new MacAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.unicast, this.multicast, this.universal, this.local, this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull MacAddressConstraintConfig withNotIn(@NonNull Collection<MacAddress> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new MacAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.unicast, this.multicast, this.universal, this.local, this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the unicast constraint.<br>
	 * <p>
	 *     Addresses must be unicast (I/G bit = 0).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull MacAddressConstraintConfig withUnicast() {
		return new MacAddressConstraintConfig(this.equalTo, this.in, Optional.of(Unit.INSTANCE), Optional.empty(), this.universal, this.local, this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the multicast constraint.<br>
	 * <p>
	 *     Addresses must be multicast (I/G bit = 1).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull MacAddressConstraintConfig withMulticast() {
		return new MacAddressConstraintConfig(this.equalTo, this.in, Optional.empty(), Optional.of(Unit.INSTANCE), this.universal, this.local, this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the universal constraint.<br>
	 * <p>
	 *     Addresses must be universally administered (U/L bit = 0).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull MacAddressConstraintConfig withUniversal() {
		return new MacAddressConstraintConfig(this.equalTo, this.in, this.unicast, this.multicast, Optional.of(Unit.INSTANCE), Optional.empty(), this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the local constraint.<br>
	 * <p>
	 *     Addresses must be locally administered (U/L bit = 1).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull MacAddressConstraintConfig withLocal() {
		return new MacAddressConstraintConfig(this.equalTo, this.in, this.unicast, this.multicast, Optional.empty(), Optional.of(Unit.INSTANCE), this.broadcast, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the broadcast constraint.<br>
	 * <p>
	 *     Addresses must be the broadcast address (FF:FF:FF:FF:FF:FF).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull MacAddressConstraintConfig withBroadcast() {
		return new MacAddressConstraintConfig(this.equalTo, this.in, this.unicast, this.multicast, this.universal, this.local, Optional.of(Pair.of(Unit.INSTANCE, false)), this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the not broadcast constraint.<br>
	 * <p>
	 *     Addresses must not be the broadcast address.
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull MacAddressConstraintConfig withNotBroadcast() {
		return new MacAddressConstraintConfig(this.equalTo, this.in, this.unicast, this.multicast, this.universal, this.local, Optional.of(Pair.of(Unit.INSTANCE, true)), this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified string constraint config.<br>
	 * <p>
	 *     The string constraint is applied to the colon-separated string representation of the MAC address.
	 * </p>
	 *
	 * @param config The string constraint config for string representation validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull MacAddressConstraintConfig withStringConstraint(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "String constraint config must not be null");
		return new MacAddressConstraintConfig(this.equalTo, this.in, this.unicast, this.multicast, this.universal, this.local, this.broadcast, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull MacAddressConstraintConfig withCustom(@NonNull Constraint<MacAddress> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new MacAddressConstraintConfig(this.equalTo, this.in, this.unicast, this.multicast, this.universal, this.local, this.broadcast, this.stringConstraint, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull MacAddress value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchFlag(value, this.unicast, MacAddress::isUnicast, "MAC address must be unicast, but was multicast: " + value),
			() -> ConstraintMatchers.matchFlag(value, this.multicast, MacAddress::isMulticast, "MAC address must be multicast, but was unicast: " + value),
			() -> ConstraintMatchers.matchFlag(value, this.universal, MacAddress::isUniversal, "MAC address must be universally administered, but was locally administered: " + value),
			() -> ConstraintMatchers.matchFlag(value, this.local, MacAddress::isLocal, "MAC address must be locally administered, but was universally administered: " + value),
			() -> IOMatchers.matchMacAddressBroadcast(value, this.broadcast),
			() -> ConstraintMatchers.matchNestedConfig(value.toString(), this.stringConstraint, "String representation"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
