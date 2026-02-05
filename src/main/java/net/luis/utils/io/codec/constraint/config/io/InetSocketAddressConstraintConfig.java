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
import net.luis.utils.io.codec.constraint.config.validator.ConstraintValidators;
import net.luis.utils.io.codec.constraint.config.validator.IOValidators;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Configuration record for InetSocketAddress constraints.<br>
 * <p>
 *     This record stores the constraint values for InetSocketAddress codecs.<br>
 *     It includes base constraints and InetSocketAddress-specific constraints for
 *     address validation, port validation, and resolution state.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param address The nested InetAddressConstraintConfig for address validation
 * @param port The PortConstraintConfig for port validation
 * @param resolved If present, requires the socket address to be resolved
 * @param unresolved If present, requires the socket address to be unresolved
 * @param custom A custom constraint implementation
 */
public record InetSocketAddressConstraintConfig(
	@NonNull Optional<Pair<InetSocketAddress, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<InetSocketAddress>, Boolean>> in,
	@NonNull Optional<InetAddressConstraintConfig> address,
	@NonNull Optional<PortConstraintConfig> port,
	@NonNull Optional<Unit> resolved,
	@NonNull Optional<Unit> unresolved,
	@NonNull Optional<Constraint<InetSocketAddress>> custom
) implements ConstraintConfig<InetSocketAddress> {
	
	/**
	 * An unconstrained InetSocketAddress configuration with no constraints applied.<br>
	 */
	public static final InetSocketAddressConstraintConfig UNCONSTRAINED = new InetSocketAddressConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new InetSocketAddress constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param address The nested InetAddressConstraintConfig for address validation
	 * @param port The PortConstraintConfig for port validation
	 * @param resolved If present, requires the socket address to be resolved
	 * @param unresolved If present, requires the socket address to be unresolved
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If both resolved and unresolved constraints are present
	 */
	public InetSocketAddressConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(address, "Optional for 'address' constraint must not be null");
		Objects.requireNonNull(port, "Optional for 'port' constraint must not be null");
		Objects.requireNonNull(resolved, "Optional for 'resolved' constraint must not be null");
		Objects.requireNonNull(unresolved, "Optional for 'unresolved' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (resolved.isPresent() && unresolved.isPresent()) {
			throw new IllegalArgumentException("Both resolved and unresolved constraints cannot be present at the same time");
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
	 * @param value The exact InetSocketAddress that should be matched
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withEqualTo(@NonNull InetSocketAddress value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new InetSocketAddressConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.address, this.port, this.resolved, this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The InetSocketAddress that should be excluded
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withNotEqualTo(@NonNull InetSocketAddress value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new InetSocketAddressConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.address, this.port, this.resolved, this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of InetSocketAddresses that are allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withIn(@NonNull Collection<InetSocketAddress> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new InetSocketAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.address, this.port, this.resolved, this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of InetSocketAddresses that are not allowed
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withNotIn(@NonNull Collection<InetSocketAddress> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new InetSocketAddressConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.address, this.port, this.resolved, this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the specified address constraint.<br>
	 *
	 * @param config The InetAddressConstraintConfig for address validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withAddress(@NonNull InetAddressConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'address' constraint must not be null");
		return new InetSocketAddressConstraintConfig(this.equalTo, this.in, Optional.of(config), this.port, this.resolved, this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the specified port constraint.<br>
	 *
	 * @param config The PortConstraintConfig for port validation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the config is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withPort(@NonNull PortConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'port' constraint must not be null");
		return new InetSocketAddressConstraintConfig(this.equalTo, this.in, this.address, Optional.of(config), this.resolved, this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the resolved constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull InetSocketAddressConstraintConfig withResolved() {
		return new InetSocketAddressConstraintConfig(this.equalTo, this.in, this.address, this.port, Optional.of(Unit.INSTANCE), this.unresolved, this.custom);
	}
	
	/**
	 * Creates a new config with the unresolved constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull InetSocketAddressConstraintConfig withUnresolved() {
		return new InetSocketAddressConstraintConfig(this.equalTo, this.in, this.address, this.port, this.resolved, Optional.of(Unit.INSTANCE), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the constraint is null
	 */
	public @NonNull InetSocketAddressConstraintConfig withCustom(@NonNull Constraint<InetSocketAddress> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new InetSocketAddressConstraintConfig(this.equalTo, this.in, this.address, this.port, this.resolved, this.unresolved, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public void validate(@NonNull InetSocketAddress value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo),
			() -> ConstraintValidators.validateIn(value, this.in),
			() -> IOValidators.validateInetSocketAddressAddress(value, this.address),
			() -> IOValidators.validateInetSocketAddressPort(value, this.port),
			() -> ConstraintValidators.validateFlag(value, this.resolved, v -> !v.isUnresolved(), "InetSocketAddress '" + value + "' must be resolved"),
			() -> ConstraintValidators.validateFlag(value, this.unresolved, InetSocketAddress::isUnresolved, "InetSocketAddress '" + value + "' must be unresolved"),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
