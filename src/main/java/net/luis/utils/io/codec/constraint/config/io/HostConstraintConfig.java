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

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.matcher.ConstraintMatchers;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for host type constraints.<br>
 * <p>
 *     This record stores the constraint values for host codecs.<br>
 *     It includes base constraints and either IP address or domain name constraints,
 *     which are mutually exclusive.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param ip The IP address constraint config (mutually exclusive with domain)
 * @param domain The domain name constraint config (mutually exclusive with ip)
 * @param custom A custom constraint implementation
 */
public record HostConstraintConfig(
	@NonNull Optional<Pair<String, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<String>, Boolean>> in,
	@NonNull Optional<IpConstraintConfig> ip,
	@NonNull Optional<DomainConstraintConfig> domain,
	@NonNull Optional<Constraint<String>> custom
) implements ConstraintConfig<String> {
	
	/**
	 * An unconstrained host configuration with no constraints applied.<br>
	 */
	public static final HostConstraintConfig UNCONSTRAINED = new HostConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new host constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param ip The IP address constraint config (mutually exclusive with domain)
	 * @param domain The domain name constraint config (mutually exclusive with ip)
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If both ip and domain constraints are present
	 */
	public HostConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(ip, "Optional for 'ip' constraint must not be null");
		Objects.requireNonNull(domain, "Optional for 'domain' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (ip.isPresent() && domain.isPresent()) {
			throw new IllegalArgumentException("Both ip and domain constraints cannot be present at the same time");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact host value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new HostConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.ip, this.domain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The host value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new HostConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.ip, this.domain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of host values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.ip, this.domain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of host values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.ip, this.domain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP address constraint.<br>
	 * <p>
	 *     This clears any existing domain constraint as they are mutually exclusive.
	 * </p>
	 *
	 * @param config The IP address constraint config
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIp(@NonNull IpConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'ip' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, Optional.of(config), Optional.empty(), this.custom);
	}
	
	/**
	 * Creates a new config with the specified domain name constraint.<br>
	 * <p>
	 *     This clears any existing IP constraint as they are mutually exclusive.
	 * </p>
	 *
	 * @param config The domain name constraint config
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withDomain(@NonNull DomainConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'domain' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, Optional.empty(), Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.ip, this.domain, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchNestedConfig(value, this.ip, "Ip address"),
			() -> ConstraintMatchers.matchNestedConfig(value, this.domain, "Domain"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
