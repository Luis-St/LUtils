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
 * @param equalTo The exact host value that should be matched
 * @param notEqualTo The host value that should be excluded
 * @param in The set of host values that are allowed
 * @param notIn The set of host values that are not allowed
 * @param minLength The minimum length constraint as a pair of (value, inclusive)
 * @param maxLength The maximum length constraint as a pair of (value, inclusive)
 * @param startsWith The prefix that hosts must start with
 * @param notStartsWith The prefix that hosts must not start with
 * @param startsWithAny The set of prefixes, one of which hosts must start with
 * @param startsWithNone The set of prefixes that hosts must not start with
 * @param contains The substring that hosts must contain
 * @param notContains The substring that hosts must not contain
 * @param containsAny The set of substrings, one of which hosts must contain
 * @param containsNone The set of substrings that hosts must not contain
 * @param containsAll The set of substrings that hosts must all contain
 * @param containsOnly The set of substrings that hosts must exclusively contain
 * @param endsWith The suffix that hosts must end with
 * @param notEndsWith The suffix that hosts must not end with
 * @param endsWithAny The set of suffixes, one of which hosts must end with
 * @param endsWithNone The set of suffixes that hosts must not end with
 * @param matches The pattern that hosts must match
 * @param notMatches The pattern that hosts must not match
 * @param ipv4 If present, requires hosts to be valid IPv4 addresses
 * @param ipv6 If present, requires hosts to be valid IPv6 addresses
 * @param ip The IP address string constraint config
 * @param ipType The enum constraint config for IP address type
 * @param inAnySubnet The set of CIDR subnets the IP must be in
 * @param notInAnySubnet The set of CIDR subnets the IP must not be in
 * @param domain The domain string constraint config
 * @param rootDomain If present, requires hosts to be root domains
 * @param subDomain If present, requires hosts to be subdomains
 * @param custom A custom constraint implementation
 */
public record HostConstraintConfig(
	// BaseConstraint fields
	@NonNull Optional<String> equalTo,
	@NonNull Optional<String> notEqualTo,
	@NonNull Optional<Set<String>> in,
	@NonNull Optional<Set<String>> notIn,
	// LengthConstraint fields
	@NonNull Optional<Pair<Integer, Boolean>> minLength,
	@NonNull Optional<Pair<Integer, Boolean>> maxLength,
	// CharSequenceConstraint fields
	@NonNull Optional<String> startsWith,
	@NonNull Optional<String> notStartsWith,
	@NonNull Optional<Set<String>> startsWithAny,
	@NonNull Optional<Set<String>> startsWithNone,
	@NonNull Optional<String> contains,
	@NonNull Optional<String> notContains,
	@NonNull Optional<Set<String>> containsAny,
	@NonNull Optional<Set<String>> containsNone,
	@NonNull Optional<Set<String>> containsAll,
	@NonNull Optional<Set<String>> containsOnly,
	@NonNull Optional<String> endsWith,
	@NonNull Optional<String> notEndsWith,
	@NonNull Optional<Set<String>> endsWithAny,
	@NonNull Optional<Set<String>> endsWithNone,
	@NonNull Optional<Pattern> matches,
	@NonNull Optional<Pattern> notMatches,
	// HostConstraint fields
	@NonNull Optional<Void> ipv4,
	@NonNull Optional<Void> ipv6,
	@NonNull Optional<StringConstraintConfig> ip,
	@NonNull Optional<EnumConstraintConfig<IpAddressType>> ipType,
	@NonNull Optional<Set<String>> inAnySubnet,
	@NonNull Optional<Set<String>> notInAnySubnet,
	@NonNull Optional<StringConstraintConfig> domain,
	@NonNull Optional<Void> rootDomain,
	@NonNull Optional<Void> subDomain,
	// Custom constraint
	@NonNull Optional<Constraint<String>> custom
) {

	/**
	 * An unconstrained host configuration with no constraints applied.<br>
	 */
	public static final HostConstraintConfig UNCONSTRAINED = new HostConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty()
	);

	// BaseConstraint with methods

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact host value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEqualTo(@NonNull String value) {
		return new HostConstraintConfig(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The host value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEqualTo(@NonNull String value) {
		return new HostConstraintConfig(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of host values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIn(@NonNull Collection<String> values) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of host values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotIn(@NonNull Collection<String> values) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	// LengthConstraint with methods

	/**
	 * Creates a new config with the specified minimum length constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMinLength(int minLength) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(minLength, true)), this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified maximum length constraint (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMaxLength(int maxLength) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, Optional.of(Pair.of(maxLength, true)), this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified exact length constraint.<br>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withExactLength(int exactLength) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified length range constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	// CharSequenceConstraint with methods

	/**
	 * Creates a new config with the specified prefix constraint.<br>
	 *
	 * @param prefix The prefix that hosts must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWith(@NonNull String prefix) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, Optional.of(Objects.requireNonNull(prefix)), this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative prefix constraint.<br>
	 *
	 * @param prefix The prefix that hosts must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotStartsWith(@NonNull String prefix) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, Optional.of(Objects.requireNonNull(prefix)), this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes, one of which hosts must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, Optional.of(Set.copyOf(prefixes)), this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes that hosts must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, Optional.of(Set.copyOf(prefixes)), this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified containment constraint.<br>
	 *
	 * @param substring The substring that hosts must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContains(@NonNull String substring) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, Optional.of(Objects.requireNonNull(substring)), this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative containment constraint.<br>
	 *
	 * @param substring The substring that hosts must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotContains(@NonNull String substring) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, Optional.of(Objects.requireNonNull(substring)), this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings, one of which hosts must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, Optional.of(Set.copyOf(substrings)), this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified all-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must all contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified only-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must exclusively contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified suffix constraint.<br>
	 *
	 * @param suffix The suffix that hosts must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWith(@NonNull String suffix) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, Optional.of(Objects.requireNonNull(suffix)), this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative suffix constraint.<br>
	 *
	 * @param suffix The suffix that hosts must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEndsWith(@NonNull String suffix) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Objects.requireNonNull(suffix)), this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes, one of which hosts must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, Optional.of(Set.copyOf(suffixes)), this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes that hosts must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, Optional.of(Set.copyOf(suffixes)), this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified regex constraint.<br>
	 *
	 * @param regex The regex pattern that hosts must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMatches(@NonNull String regex) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, Optional.of(Pattern.compile(regex)), this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that hosts must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMatches(@NonNull Pattern pattern) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, Optional.of(Objects.requireNonNull(pattern)), this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative regex constraint.<br>
	 *
	 * @param regex The regex pattern that hosts must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotMatches(@NonNull String regex) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, Optional.of(Pattern.compile(regex)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that hosts must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, Optional.of(Objects.requireNonNull(pattern)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	// HostConstraint with methods

	/**
	 * Creates a new config with the IPv4 constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpv4() {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, Optional.of(null), this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the IPv6 constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpv6() {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, Optional.of(null), this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified IP address constraint.<br>
	 *
	 * @param config The string constraint config for IP address validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIp(@NonNull StringConstraintConfig config) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, Optional.of(Objects.requireNonNull(config)), this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified IP type constraint.<br>
	 *
	 * @param config The enum constraint config for IP address type validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpType(@NonNull EnumConstraintConfig<IpAddressType> config) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, Optional.of(Objects.requireNonNull(config)), this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withInAnySubnet(@NonNull Collection<String> cidrs) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, Optional.of(Set.copyOf(cidrs)), this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified negative subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets to exclude
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotInAnySubnet(@NonNull Collection<String> cidrs) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, Optional.of(Set.copyOf(cidrs)), this.domain, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified domain constraint.<br>
	 *
	 * @param config The string constraint config for domain validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withDomain(@NonNull StringConstraintConfig config) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, Optional.of(Objects.requireNonNull(config)), this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the root domain constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withRootDomain() {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, Optional.of(null), this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the subdomain constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withSubDomain() {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, Optional.of(null), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		return new HostConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.notInAnySubnet, this.domain, this.rootDomain, this.subDomain, Optional.of(Objects.requireNonNull(constraint)));
	}
}
