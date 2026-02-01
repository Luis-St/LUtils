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

import net.luis.utils.io.codec.constraint.config.*;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintValidators;
import net.luis.utils.io.codec.constraint.config.validator.IOValidators;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.network.address.IpAddress;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for IpAddress constraints.<br>
 * <p>
 *     This record stores the constraint values for IP address validation.<br>
 *     It includes base constraints, IP version constraints, IP type constraints,
 *     subnet membership constraints, and string representation constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param ipVersion The enum constraint config for IP version (IPv4/IPv6)
 * @param ipType The enum constraint config for IP address type (PUBLIC, PRIVATE, LOOPBACK, etc.)
 * @param inAnySubnet The subnet constraint as a pair of (cidrs, negated) where negated=false means inAnySubnet and negated=true means notInAnySubnet
 * @param stringConstraint String constraint config for validating the string representation of the IP address
 * @param custom A custom constraint implementation
 */
public record IpAddressConstraintConfig(
	@NonNull Optional<Pair<IpAddress<?>, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<IpAddress<?>>, Boolean>> in,
	@NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion,
	@NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType,
	@NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet,
	@NonNull Optional<StringConstraintConfig> stringConstraint,
	@NonNull Optional<Constraint<IpAddress<?>>> custom
) implements ConstraintConfig<IpAddress<?>> {
	
	/**
	 * An unconstrained configuration with no constraints applied.<br>
	 */
	public static final IpAddressConstraintConfig UNCONSTRAINED = new IpAddressConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new IP address constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param ipVersion The enum constraint config for IP version
	 * @param ipType The enum constraint config for IP address type
	 * @param inAnySubnet The subnet constraint as a pair of (cidrs, negated) where negated=false means inAnySubnet and negated=true means notInAnySubnet
	 * @param stringConstraint String constraint config for validating the string representation
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If the in any subnet set is empty when present
	 */
	public IpAddressConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(ipVersion, "Optional for 'ip version' constraint must not be null");
		Objects.requireNonNull(ipType, "Optional for 'ip type' constraint must not be null");
		Objects.requireNonNull(inAnySubnet, "Optional for 'in any subnet' constraint must not be null");
		Objects.requireNonNull(stringConstraint, "Optional for 'string constraint' must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (inAnySubnet.isPresent() && inAnySubnet.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In any subnet set must not be empty when present");
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
	public @NonNull IpAddressConstraintConfig withEqualTo(@NonNull IpAddress<?> value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new IpAddressConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.ipVersion, this.ipType, this.inAnySubnet, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull IpAddressConstraintConfig withNotEqualTo(@NonNull IpAddress<?> value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new IpAddressConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.ipVersion, this.ipType, this.inAnySubnet, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull IpAddressConstraintConfig withIn(@NonNull Collection<? extends IpAddress<?>> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.ipVersion, this.ipType, this.inAnySubnet, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull IpAddressConstraintConfig withNotIn(@NonNull Collection<? extends IpAddress<?>> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.ipVersion, this.ipType, this.inAnySubnet, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP version constraint.<br>
	 *
	 * @param config The enum constraint config for IP version validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull IpAddressConstraintConfig withIpVersion(@NonNull EnumConstraintConfig<IpVersion> config) {
		Objects.requireNonNull(config, "Config for 'ip version' constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, this.in, Optional.of(config), this.ipType, this.inAnySubnet, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP type constraint.<br>
	 *
	 * @param config The enum constraint config for IP address type validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull IpAddressConstraintConfig withIpType(@NonNull EnumConstraintConfig<IpAddressType> config) {
		Objects.requireNonNull(config, "Config for 'ip type' constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, Optional.of(config), this.inAnySubnet, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the cidrs collection is null
	 */
	public @NonNull IpAddressConstraintConfig withInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs for 'in any subnet' constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), false)), this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets to exclude
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the cidrs collection is null
	 */
	public @NonNull IpAddressConstraintConfig withNotInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs for 'not in any subnet' constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), true)), this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified string constraint config.<br>
	 * <p>
	 *     The string constraint is applied to the string representation of the IP address.
	 * </p>
	 *
	 * @param config The string constraint config for string representation validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull IpAddressConstraintConfig withStringConstraint(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "String constraint config must not be null");
		return new IpAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, this.inAnySubnet, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull IpAddressConstraintConfig withCustom(@NonNull Constraint<IpAddress<?>> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new IpAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, this.inAnySubnet, this.stringConstraint, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull IpAddress<?> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo),
			() -> ConstraintValidators.validateIn(value, this.in),
			() -> IOValidators.validateIpVersion(value.toString(), this.ipVersion),
			() -> IOValidators.validateIpType(value.toString(), this.ipType),
			() -> IOValidators.validateInAnySubnet(value.toString(), this.inAnySubnet),
			() -> ConstraintValidators.validateNestedConfig(value.toString(), this.stringConstraint, "String representation"),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
