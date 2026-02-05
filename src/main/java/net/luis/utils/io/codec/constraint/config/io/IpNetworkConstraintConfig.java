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
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.io.network.address.IpNetwork;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for IpNetwork constraints.<br>
 * <p>
 *     This record stores the constraint values for IP network validation.<br>
 *     It includes base constraints, IP version constraints, prefix length constraints,
 *     canonical form constraints, and string representation constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param ipVersion The enum constraint config for IP version (IPv4/IPv6)
 * @param prefixLength The length constraint config for prefix length validation
 * @param canonical Constraint to require canonical form (no host bits set)
 * @param stringConstraint String constraint config for validating the CIDR notation string representation
 * @param custom A custom constraint implementation
 */
public record IpNetworkConstraintConfig(
	@NonNull Optional<Pair<IpNetwork<?, ?>, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<IpNetwork<?, ?>>, Boolean>> in,
	@NonNull Optional<EnumConstraintConfig<IpVersion>> ipVersion,
	@NonNull Optional<LengthConstraintConfig> prefixLength,
	@NonNull Optional<Unit> canonical,
	@NonNull Optional<StringConstraintConfig> stringConstraint,
	@NonNull Optional<Constraint<IpNetwork<?, ?>>> custom
) implements ConstraintConfig<IpNetwork<?, ?>> {
	
	/**
	 * An unconstrained configuration with no constraints applied.<br>
	 */
	public static final IpNetworkConstraintConfig UNCONSTRAINED = new IpNetworkConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new IP network constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param ipVersion The enum constraint config for IP version
	 * @param prefixLength The length constraint config for prefix length validation
	 * @param canonical Constraint to require canonical form
	 * @param stringConstraint String constraint config for validating the CIDR notation string representation
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 */
	public IpNetworkConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(ipVersion, "Optional for 'ip version' constraint must not be null");
		Objects.requireNonNull(prefixLength, "Optional for 'prefix length' constraint must not be null");
		Objects.requireNonNull(canonical, "Optional for 'canonical' constraint must not be null");
		Objects.requireNonNull(stringConstraint, "Optional for 'string constraint' must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
	}
	
	@Override
	public boolean isUnconstrained() {
		return this.equals(UNCONSTRAINED);
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull IpNetworkConstraintConfig withEqualTo(@NonNull IpNetwork<?, ?> value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new IpNetworkConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.ipVersion, this.prefixLength, this.canonical, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull IpNetworkConstraintConfig withNotEqualTo(@NonNull IpNetwork<?, ?> value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new IpNetworkConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.ipVersion, this.prefixLength, this.canonical, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull IpNetworkConstraintConfig withIn(@NonNull Collection<? extends IpNetwork<?, ?>> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new IpNetworkConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.ipVersion, this.prefixLength, this.canonical, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull IpNetworkConstraintConfig withNotIn(@NonNull Collection<? extends IpNetwork<?, ?>> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new IpNetworkConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.ipVersion, this.prefixLength, this.canonical, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP version constraint.<br>
	 *
	 * @param config The enum constraint config for IP version validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull IpNetworkConstraintConfig withIpVersion(@NonNull EnumConstraintConfig<IpVersion> config) {
		Objects.requireNonNull(config, "Config for 'ip version' constraint must not be null");
		return new IpNetworkConstraintConfig(this.equalTo, this.in, Optional.of(config), this.prefixLength, this.canonical, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified prefix length constraints.<br>
	 *
	 * @param config The length constraint config for prefix length validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull IpNetworkConstraintConfig withPrefixLength(@NonNull LengthConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'prefix length' constraint must not be null");
		return new IpNetworkConstraintConfig(this.equalTo, this.in, this.ipVersion, Optional.of(config), this.canonical, this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the canonical form constraint.<br>
	 * <p>
	 *     Networks must be in canonical form (no host bits set).
	 * </p>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IpNetworkConstraintConfig withCanonical() {
		return new IpNetworkConstraintConfig(this.equalTo, this.in, this.ipVersion, this.prefixLength, Optional.of(Unit.INSTANCE), this.stringConstraint, this.custom);
	}
	
	/**
	 * Creates a new config with the specified string constraint config.<br>
	 * <p>
	 *     The string constraint is applied to the CIDR notation string representation of the IP network.
	 * </p>
	 *
	 * @param config The string constraint config for string representation validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull IpNetworkConstraintConfig withStringConstraint(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "String constraint config must not be null");
		return new IpNetworkConstraintConfig(this.equalTo, this.in, this.ipVersion, this.prefixLength, this.canonical, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull IpNetworkConstraintConfig withCustom(@NonNull Constraint<IpNetwork<?, ?>> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new IpNetworkConstraintConfig(this.equalTo, this.in, this.ipVersion, this.prefixLength, this.canonical, this.stringConstraint, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public void validate(@NonNull IpNetwork<?, ?> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo),
			() -> ConstraintValidators.validateIn(value, this.in),
			() -> IOValidators.validateIpNetworkIpVersion(value, this.ipVersion),
			() -> ConstraintValidators.validateExtractedValue(value, this.prefixLength, IpNetwork::prefixLength, "Prefix length"),
			() -> ConstraintValidators.validateFlag(value, this.canonical, IpNetwork::isCanonical, "Network must be in canonical form, but was: " + value.toCidrNotation()),
			() -> ConstraintValidators.validateNestedConfig(value.toString(), this.stringConstraint, "String representation"),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
