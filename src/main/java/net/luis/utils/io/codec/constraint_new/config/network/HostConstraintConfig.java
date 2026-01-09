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
	 * Constructs a new host constraint config with the specified parameters.<br>
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
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If min length is greater than max length when both are present
	 * @throws IllegalArgumentException If min and max length are equal but at least one bound is exclusive when both are present
	 * @throws IllegalArgumentException If the starts with any set is empty when present
	 * @throws IllegalArgumentException If the contains any set is empty when present
	 * @throws IllegalArgumentException If the contains all set is empty when present
	 * @throws IllegalArgumentException If the contains only set is empty when present
	 * @throws IllegalArgumentException If the ends with any set is empty when present
	 * @throws IllegalArgumentException If the in any subnet set is empty when present
	 * @throws IllegalArgumentException If both ipv4 and ipv6 constraints are present
	 * @throws IllegalArgumentException If both root domain and sub domain constraints are present
	 */
	public HostConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(minLength, "Optional for 'min length' constraint must not be null");
		Objects.requireNonNull(maxLength, "Optional for 'max length' constraint must not be null");
		Objects.requireNonNull(startsWith, "Optional for 'starts with' constraint must not be null");
		Objects.requireNonNull(startsWithAny, "Optional for 'starts with any' constraint must not be null");
		Objects.requireNonNull(contains, "Optional for 'contains' constraint must not be null");
		Objects.requireNonNull(containsAny, "Optional for 'contains any' constraint must not be null");
		Objects.requireNonNull(containsAll, "Optional for 'contains all' constraint must not be null");
		Objects.requireNonNull(containsOnly, "Optional for 'contains only' constraint must not be null");
		Objects.requireNonNull(endsWith, "Optional for 'ends with' constraint must not be null");
		Objects.requireNonNull(endsWithAny, "Optional for 'ends with any' constraint must not be null");
		Objects.requireNonNull(matches, "Optional for 'matches' constraint must not be null");
		Objects.requireNonNull(ipv4, "Optional for 'ipv4' constraint must not be null");
		Objects.requireNonNull(ipv6, "Optional for 'ipv6' constraint must not be null");
		Objects.requireNonNull(ip, "Optional for 'ip' constraint must not be null");
		Objects.requireNonNull(ipType, "Optional for 'ip type' constraint must not be null");
		Objects.requireNonNull(inAnySubnet, "Optional for 'in any subnet' constraint must not be null");
		Objects.requireNonNull(domain, "Optional for 'domain' constraint must not be null");
		Objects.requireNonNull(rootDomain, "Optional for 'root domain' constraint must not be null");
		Objects.requireNonNull(subDomain, "Optional for 'sub domain' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (minLength.isPresent() && maxLength.isPresent()) {
			if (minLength.get().getFirst().compareTo(maxLength.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + minLength.get().getFirst() + " > " + maxLength.get().getFirst());
			}
			if (minLength.get().getFirst().compareTo(maxLength.get().getFirst()) == 0 && (!minLength.get().getSecond() || !maxLength.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
		}
		
		if (startsWithAny.isPresent() && startsWithAny.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("Starts with any set must not be empty when present");
		}
		
		if (containsAny.isPresent() && containsAny.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("Contains any set must not be empty when present");
		}
		
		if (containsAll.isPresent() && containsAll.get().isEmpty()) {
			throw new IllegalArgumentException("Contains all set must not be empty when present");
		}
		
		if (containsOnly.isPresent() && containsOnly.get().isEmpty()) {
			throw new IllegalArgumentException("Contains only set must not be empty when present");
		}
		
		if (endsWithAny.isPresent() && endsWithAny.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("Ends with any set must not be empty when present");
		}
		
		if (inAnySubnet.isPresent() && inAnySubnet.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In any subnet set must not be empty when present");
		}
		
		if (ipv4.isPresent() && ipv6.isPresent()) {
			throw new IllegalArgumentException("Both ipv4 and ipv6 constraints cannot be present at the same time");
		}
		
		if (rootDomain.isPresent() && subDomain.isPresent()) {
			throw new IllegalArgumentException("Both root domain and sub domain constraints cannot be present at the same time");
		}
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact host value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new HostConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The host value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new HostConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of host values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of host values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
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
		Objects.requireNonNull(prefix, "Prefix for 'starts with' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(prefix, false)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative prefix constraint.<br>
	 *
	 * @param prefix The prefix that hosts must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotStartsWith(@NonNull String prefix) {
		Objects.requireNonNull(prefix, "Prefix for 'not starts with' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, Optional.of(Pair.of(prefix, true)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes, one of which hosts must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		Objects.requireNonNull(prefixes, "Prefixes for 'starts with any' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), false)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes that hosts must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		Objects.requireNonNull(prefixes, "Prefixes for 'starts with none' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), true)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified containment constraint.<br>
	 *
	 * @param substring The substring that hosts must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContains(@NonNull String substring) {
		Objects.requireNonNull(substring, "Substring for 'contains' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(substring, false)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative containment constraint.<br>
	 *
	 * @param substring The substring that hosts must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotContains(@NonNull String substring) {
		Objects.requireNonNull(substring, "Substring for 'not contains' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(substring, true)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings, one of which hosts must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains any' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), false)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains none' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), true)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified all-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must all contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains all' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified only-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that hosts must exclusively contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains only' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified suffix constraint.<br>
	 *
	 * @param suffix The suffix that hosts must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWith(@NonNull String suffix) {
		Objects.requireNonNull(suffix, "Suffix for 'ends with' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(suffix, false)), this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative suffix constraint.<br>
	 *
	 * @param suffix The suffix that hosts must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotEndsWith(@NonNull String suffix) {
		Objects.requireNonNull(suffix, "Suffix for 'not ends with' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(suffix, true)), this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes, one of which hosts must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		Objects.requireNonNull(suffixes, "Suffixes for 'ends with any' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), false)), this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes that hosts must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		Objects.requireNonNull(suffixes, "Suffixes for 'ends with none' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), true)), this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified regex constraint.<br>
	 *
	 * @param regex The regex pattern that hosts must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMatches(@NonNull String regex) {
		Objects.requireNonNull(regex, "Regex for 'matches' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), false)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that hosts must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withMatches(@NonNull Pattern pattern) {
		Objects.requireNonNull(pattern, "Pattern for 'matches' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(pattern, false)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative regex constraint.<br>
	 *
	 * @param regex The regex pattern that hosts must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotMatches(@NonNull String regex) {
		Objects.requireNonNull(regex, "Regex for 'not matches' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), true)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that hosts must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		Objects.requireNonNull(pattern, "Pattern for 'not matches' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(pattern, true)), this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
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
		Objects.requireNonNull(config, "Config for 'ip' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, Optional.of(config), this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified IP type constraint.<br>
	 *
	 * @param config The enum constraint config for IP address type validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withIpType(@NonNull EnumConstraintConfig<IpAddressType> config) {
		Objects.requireNonNull(config, "Config for 'ip type' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, Optional.of(config), this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs for 'in any subnet' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), false)), this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative subnet membership constraint.<br>
	 *
	 * @param cidrs The collection of CIDR notation subnets to exclude
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withNotInAnySubnet(@NonNull Collection<String> cidrs) {
		Objects.requireNonNull(cidrs, "CIDRs for 'not in any subnet' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, Optional.of(Pair.of(Set.copyOf(cidrs), true)), this.domain, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified domain constraint.<br>
	 *
	 * @param config The string constraint config for domain validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull HostConstraintConfig withDomain(@NonNull StringConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'domain' constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, Optional.of(config), this.rootDomain, this.subDomain, this.custom);
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
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new HostConstraintConfig(this.equalTo, this.in, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.ipv4, this.ipv6, this.ip, this.ipType, this.inAnySubnet, this.domain, this.rootDomain, this.subDomain, Optional.of(constraint));
	}
}
