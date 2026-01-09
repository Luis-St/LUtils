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
import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.IpAddressType;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Configuration record for host type constraints.<br>
 * <p>
 *     This record stores the constraint values for host codecs.<br>
 *     It includes base constraints, length constraints, char sequence pattern matching,
 *     and host-specific constraints for IP addresses and domain names.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param minLength The minimum length constraint as a pair of (value, inclusive)
 * @param maxLength The maximum length constraint as a pair of (value, inclusive)
 * @param startsWith The prefix constraint as a pair of (prefix, negated) where negated=false means startsWith and negated=true means notStartsWith
 * @param startsWithAny The multi-prefix constraint as a pair of (prefixes, negated) where negated=false means startsWithAny and negated=true means startsWithNone
 * @param contains The containment constraint as a pair of (substring, negated) where negated=false means contains and negated=true means notContains
 * @param containsAny The multi-containment constraint as a pair of (substrings, negated) where negated=false means containsAny and negated=true means containsNone
 * @param containsAll The set of substrings that hosts must all contain
 * @param containsOnly The set of substrings that hosts must exclusively contain
 * @param endsWith The suffix constraint as a pair of (suffix, negated) where negated=false means endsWith and negated=true means notEndsWith
 * @param endsWithAny The multi-suffix constraint as a pair of (suffixes, negated) where negated=false means endsWithAny and negated=true means endsWithNone
 * @param matches The pattern constraint as a pair of (pattern, negated) where negated=false means matches and negated=true means notMatches
 * @param ipv4 If present, requires hosts to be valid IPv4 addresses
 * @param ipv6 If present, requires hosts to be valid IPv6 addresses
 * @param ip The IP address string constraint config
 * @param ipType The enum constraint config for IP address type
 * @param inAnySubnet The subnet constraint as a pair of (cidrs, negated) where negated=false means inAnySubnet and negated=true means notInAnySubnet
 * @param domain The domain string constraint config
 * @param rootDomain If present, requires hosts to be root domains
 * @param subDomain If present, requires hosts to be subdomains
 * @param custom A custom constraint implementation
 */
@SuppressWarnings("OptionalContainsCollection")
public record HostConstraintConfig(
	@NonNull Optional<Pair<String, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<String>, Boolean>> in,
	@NonNull Optional<Pair<Integer, Boolean>> minLength,
	@NonNull Optional<Pair<Integer, Boolean>> maxLength,
	@NonNull Optional<Pair<String, Boolean>> startsWith,
	@NonNull Optional<Pair<Set<String>, Boolean>> startsWithAny,
	@NonNull Optional<Pair<String, Boolean>> contains,
	@NonNull Optional<Pair<Set<String>, Boolean>> containsAny,
	@NonNull Optional<Set<String>> containsAll,
	@NonNull Optional<Set<String>> containsOnly,
	@NonNull Optional<Pair<String, Boolean>> endsWith,
	@NonNull Optional<Pair<Set<String>, Boolean>> endsWithAny,
	@NonNull Optional<Pair<Pattern, Boolean>> matches,
	@NonNull Optional<Void> ipv4,
	@NonNull Optional<Void> ipv6,
	@NonNull Optional<StringConstraintConfig> ip,
	@NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType,
	@NonNull Optional<Pair<Set<String>, Boolean>> inAnySubnet,
	@NonNull Optional<StringConstraintConfig> domain,
	@NonNull Optional<Void> rootDomain,
	@NonNull Optional<Void> subDomain,
	@NonNull Optional<Constraint<String>> custom
) {
	
	/**
	 * An unconstrained host configuration with no constraints applied.<br>
	 */
	public static final HostConstraintConfig UNCONSTRAINED = new HostConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact host value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEqualTo(@NonNull String value) {
		return new HostConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The host value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEqualTo(@NonNull String value) {
		return new HostConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of host values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIn(@NonNull Collection<String> values) {
		return new HostConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of host values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotIn(@NonNull Collection<String> values) {
		return new HostConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum length constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMinLength(int minLength) {
		return new HostConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minLength, true)), this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum length constraint (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMaxLength(int maxLength) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, Optional.of(Pair.of(maxLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact length constraint.<br>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withExactLength(int exactLength) {
		return new HostConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified length range constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new HostConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified prefix constraint.<br>
	 *
	 * @param prefix The prefix that hosts must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWith(@NonNull String prefix) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(Objects.requireNonNull(prefix), false)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative prefix constraint.<br>
	 *
	 * @param prefix The prefix that hosts must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotStartsWith(@NonNull String prefix) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(Objects.requireNonNull(prefix), true)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes, one of which hosts must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), false)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes that hosts must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), true)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified containment constraint.<br>
	 *
	 * @param substring The substring that hosts must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContains(@NonNull String substring) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(Objects.requireNonNull(substring), false)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative containment constraint.<br>
	 *
	 * @param substring The substring that hosts must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotContains(@NonNull String substring) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(Objects.requireNonNull(substring), true)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings, one of which hosts must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), false)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), true)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified all-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must all contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified only-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must exclusively contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified suffix constraint.<br>
	 *
	 * @param suffix The suffix that hosts must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWith(@NonNull String suffix) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(Objects.requireNonNull(suffix), false)), this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative suffix constraint.<br>
	 *
	 * @param suffix The suffix that hosts must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEndsWith(@NonNull String suffix) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(Objects.requireNonNull(suffix), true)), this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes, one of which hosts must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), false)), this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes that hosts must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), true)), this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified regex constraint.<br>
	 *
	 * @param regex The regex pattern that hosts must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMatches(@NonNull String regex) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), false)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that hosts must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMatches(@NonNull Pattern pattern) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Objects.requireNonNull(pattern), false)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative regex constraint.<br>
	 *
	 * @param regex The regex pattern that hosts must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotMatches(@NonNull String regex) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), true)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that hosts must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Objects.requireNonNull(pattern), true)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the IPv4 constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpv4() {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, Optional.of(null), this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the IPv6 constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpv6() {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, Optional.of(null), this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP address constraint.<br>
	 *
	 * @param config The string constraint config for IP address validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIp(@NonNull StringConstraintConfig config) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, Optional.of(Objects.requireNonNull(config)), this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP type constraint.<br>
	 *
	 * @param config The enum constraint config for IP address type validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpType(@NonNull EnumConstraintConfig<IpAddressType> config) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, Optional.of(Objects.requireNonNull(config)), this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withInAnySubnet(@NonNull Collection<String> cidrs) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), false)), this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets to exclude
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotInAnySubnet(@NonNull Collection<String> cidrs) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), true)), this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified domain constraint.<br>
	 *
	 * @param config The string constraint config for domain validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withDomain(@NonNull StringConstraintConfig config) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, Optional.of(Objects.requireNonNull(config)), this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the root domain constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withRootDomain() {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, Optional.of(null), this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the subdomain constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withSubDomain() {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, Optional.of(null), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, Optional.of(Objects.requireNonNull(constraint)));
	}
}
