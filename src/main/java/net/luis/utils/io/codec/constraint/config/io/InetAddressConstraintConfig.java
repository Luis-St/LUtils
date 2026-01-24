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
import net.luis.utils.io.codec.constraint.util.IpAddressType;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.net.InetAddress;
import java.util.*;

/**
 * Configuration record for InetAddress constraints.<br>
 * <p>
 *     This record stores the constraint values for InetAddress codecs.<br>
 *     It includes base constraints and InetAddress-specific constraints for IP version,
 *     IP address type, and subnet membership.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param ipVersion The enum constraint config for IP version (IPv4/IPv6)
 * @param ipType The enum constraint config for IP address type (PUBLIC, PRIVATE, LOOPBACK, etc.)
 * @param inAnySubnet The subnet membership constraint as a pair of (CIDRs, negated) where negated=false means inAnySubnet and negated=true means notInAnySubnet
 * @param custom A custom constraint implementation
 */
public record InetAddressConstraintConfig(
	@NonNull Optional<Pair<InetAddress, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<InetAddress>, Boolean>> in,
	@NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion,
	@NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType,
	@NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet,
	@NonNull Optional<Constraint<InetAddress>> custom
) implements ConstraintConfig<InetAddress> {
	
	/**
	 * An unconstrained InetAddress configuration with no constraints applied.<br>
	 */
	public static final InetAddressConstraintConfig UNCONSTRAINED = new InetAddressConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new InetAddress constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param ipVersion The enum constraint config for IP version (IPv4/IPv6)
	 * @param ipType The enum constraint config for IP address type (PUBLIC, PRIVATE, LOOPBACK, etc.)
	 * @param inAnySubnet The subnet membership constraint as a pair of (CIDRs, negated) where negated=false means inAnySubnet and negated=true means notInAnySubnet
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If the inAnySubnet constraint set is empty when present
	 */
	public InetAddressConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(ipVersion, "Optional for 'ip version' constraint must not be null");
		Objects.requireNonNull(ipType, "Optional for 'ip type' constraint must not be null");
		Objects.requireNonNull(inAnySubnet, "Optional for 'in any subnet' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (inAnySubnet.isPresent() && inAnySubnet.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In any subnet constraint set must not be empty when present");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact InetAddress that should be matched
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull InetAddressConstraintConfig withEqualTo(@NonNull InetAddress value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new InetAddressConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.ipVersion, this.ipType, this.inAnySubnet, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The InetAddress that should be excluded
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull InetAddressConstraintConfig withNotEqualTo(@NonNull InetAddress value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new InetAddressConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.ipVersion, this.ipType, this.inAnySubnet, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of InetAddresses that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull InetAddressConstraintConfig withIn(@NonNull Collection<InetAddress> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.ipVersion, this.ipType, this.inAnySubnet, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of InetAddresses that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull InetAddressConstraintConfig withNotIn(@NonNull Collection<InetAddress> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.ipVersion, this.ipType, this.inAnySubnet, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP version constraint.<br>
	 *
	 * @param config The enum constraint config for IP version validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull InetAddressConstraintConfig withIpVersion(@NonNull EnumConstraintConfig<IpVersion> config) {
		Objects.requireNonNull(config, "Config for 'ip version' constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, this.in, Optional.of(config), this.ipType, this.inAnySubnet, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP address type constraint.<br>
	 *
	 * @param config The enum constraint config for IP address type validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull InetAddressConstraintConfig withIpType(@NonNull EnumConstraintConfig<IpAddressType> config) {
		Objects.requireNonNull(config, "Config for 'ip type' constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, Optional.of(config), this.inAnySubnet, this.custom);
	}
	
	/**
	 * Creates a new config with the specified subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation strings for allowed subnets
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the cidrs collection is null
	 */
	public @NonNull InetAddressConstraintConfig withInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs for 'in any subnet' constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), false)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified excluded subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation strings for excluded subnets
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the cidrs collection is null
	 */
	public @NonNull InetAddressConstraintConfig withNotInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs for 'not in any subnet' constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull InetAddressConstraintConfig withCustom(@NonNull Constraint<InetAddress> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new InetAddressConstraintConfig(this.equalTo, this.in, this.ipVersion, this.ipType, this.inAnySubnet, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull InetAddress value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> IOMatchers.matchInetAddressIpVersion(value, this.ipVersion),
			() -> IOMatchers.matchInetAddressIpType(value, this.ipType),
			() -> IOMatchers.matchInetAddressInAnySubnet(value, this.inAnySubnet),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
