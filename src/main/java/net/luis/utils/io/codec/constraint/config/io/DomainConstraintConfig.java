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

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint_new.config.matcher.NetworkMatchers;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Configuration record for domain name constraints.<br>
 * <p>
 *     This record stores the constraint values for domain name validation.<br>
 *     It includes base constraints, length constraints, char sequence pattern matching,
 *     and domain-specific constraints for structure (root domain vs subdomain).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param length The length constraint configuration
 * @param startsWith The prefix constraint as a pair of (prefix, negated) where negated=false means startsWith and negated=true means notStartsWith
 * @param startsWithAny The multi-prefix constraint as a pair of (prefixes, negated) where negated=false means startsWithAny and negated=true means startsWithNone
 * @param contains The containment constraint as a pair of (substring, negated) where negated=false means contains and negated=true means notContains
 * @param containsAny The multi-containment constraint as a pair of (substrings, negated) where negated=false means containsAny and negated=true means containsNone
 * @param containsAll The set of substrings that values must all contain
 * @param containsOnly The set of substrings that values must exclusively contain
 * @param endsWith The suffix constraint as a pair of (suffix, negated) where negated=false means endsWith and negated=true means notEndsWith
 * @param endsWithAny The multi-suffix constraint as a pair of (suffixes, negated) where negated=false means endsWithAny and negated=true means endsWithNone
 * @param matches The pattern constraint as a pair of (pattern, negated) where negated=false means matches and negated=true means notMatches
 * @param rootDomain If present, requires values to be root domains
 * @param subDomain If present, requires values to be subdomains
 * @param custom A custom constraint implementation
 */
@SuppressWarnings("OptionalContainsCollection")
public record DomainConstraintConfig(
	@NonNull Optional<Pair<String, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<String>, Boolean>> in,
	@NonNull Optional<LengthConstraintConfig> length,
	@NonNull Optional<Pair<String, Boolean>> startsWith,
	@NonNull Optional<Pair<Set<String>, Boolean>> startsWithAny,
	@NonNull Optional<Pair<String, Boolean>> contains,
	@NonNull Optional<Pair<Set<String>, Boolean>> containsAny,
	@NonNull Optional<Set<String>> containsAll,
	@NonNull Optional<Set<String>> containsOnly,
	@NonNull Optional<Pair<String, Boolean>> endsWith,
	@NonNull Optional<Pair<Set<String>, Boolean>> endsWithAny,
	@NonNull Optional<Pair<Pattern, Boolean>> matches,
	@NonNull Optional<Unit> rootDomain,
	@NonNull Optional<Unit> subDomain,
	@NonNull Optional<Constraint<String>> custom
) implements ConstraintConfig<String> {
	
	/**
	 * An unconstrained domain configuration with no constraints applied.<br>
	 */
	public static final DomainConstraintConfig UNCONSTRAINED = new DomainConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new domain constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The inclusion constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param length The length constraint configuration
	 * @param startsWith The prefix constraint as a pair of (prefix, negated) where negated=false means startsWith and negated=true means notStartsWith
	 * @param startsWithAny The multi-prefix constraint as a pair of (prefixes, negated) where negated=false means startsWithAny and negated=true means startsWithNone
	 * @param contains The containment constraint as a pair of (substring, negated) where negated=false means contains and negated=true means notContains
	 * @param containsAny The multi-containment constraint as a pair of (substrings, negated) where negated=false means containsAny and negated=true means containsNone
	 * @param containsAll The set of substrings that values must all contain
	 * @param containsOnly The set of substrings that values must exclusively contain
	 * @param endsWith The suffix constraint as a pair of (suffix, negated) where negated=false means endsWith and negated=true means notEndsWith
	 * @param endsWithAny The multi-suffix constraint as a pair of (suffixes, negated) where negated=false means endsWithAny and negated=true means endsWithNone
	 * @param matches The pattern constraint as a pair of (pattern, negated) where negated=false means matches and negated=true means notMatches
	 * @param rootDomain If present, requires values to be root domains
	 * @param subDomain If present, requires values to be subdomains
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in constraint set is empty when present
	 * @throws IllegalArgumentException If the starts with any set is empty when present
	 * @throws IllegalArgumentException If the contains any set is empty when present
	 * @throws IllegalArgumentException If the contains all set is empty when present
	 * @throws IllegalArgumentException If the contains only set is empty when present
	 * @throws IllegalArgumentException If the ends with any set is empty when present
	 * @throws IllegalArgumentException If both root domain and sub domain constraints are present
	 */
	public DomainConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(length, "Optional for 'length' constraint must not be null");
		Objects.requireNonNull(startsWith, "Optional for 'starts with' constraint must not be null");
		Objects.requireNonNull(startsWithAny, "Optional for 'starts with any' constraint must not be null");
		Objects.requireNonNull(contains, "Optional for 'contains' constraint must not be null");
		Objects.requireNonNull(containsAny, "Optional for 'contains any' constraint must not be null");
		Objects.requireNonNull(containsAll, "Optional for 'contains all' constraint must not be null");
		Objects.requireNonNull(containsOnly, "Optional for 'contains only' constraint must not be null");
		Objects.requireNonNull(endsWith, "Optional for 'ends with' constraint must not be null");
		Objects.requireNonNull(endsWithAny, "Optional for 'ends with any' constraint must not be null");
		Objects.requireNonNull(matches, "Optional for 'matches' constraint must not be null");
		Objects.requireNonNull(rootDomain, "Optional for 'root domain' constraint must not be null");
		Objects.requireNonNull(subDomain, "Optional for 'sub domain' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");

		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
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

		if (rootDomain.isPresent() && subDomain.isPresent()) {
			throw new IllegalArgumentException("Both root domain and sub domain constraints cannot be present at the same time");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new DomainConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotEqualTo(@NonNull String value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new DomainConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotIn(@NonNull Collection<String> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum length constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withMinLength(int minLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withMinLength(minLength);
		return new DomainConstraintConfig(this.equalTo, this.in, Optional.of(newLength), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified maximum length constraint (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withMaxLength(int maxLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withMaxLength(maxLength);
		return new DomainConstraintConfig(this.equalTo, this.in, Optional.of(newLength), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified exact length constraint.<br>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withExactLength(int exactLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withExactLength(exactLength);
		return new DomainConstraintConfig(this.equalTo, this.in, Optional.of(newLength), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified length range constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withLengthBetween(int minLength, int maxLength) {
		LengthConstraintConfig newLength = this.length.orElse(LengthConstraintConfig.UNCONSTRAINED).withLengthBetween(minLength, maxLength);
		return new DomainConstraintConfig(this.equalTo, this.in, Optional.of(newLength), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}

	/**
	 * Creates a new config with the specified length constraints.<br>
	 * <p>
	 *     This method applies the given {@link LengthConstraintConfig} to this config.
	 * </p>
	 *
	 * @param lengthConfig The length constraint configuration to apply
	 * @return A new config with the length constraints applied
	 * @throws NullPointerException If the length config is null
	 */
	public @NonNull DomainConstraintConfig withLength(@NonNull LengthConstraintConfig lengthConfig) {
		Objects.requireNonNull(lengthConfig, "Length config must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, Optional.of(lengthConfig), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified prefix constraint.<br>
	 *
	 * @param prefix The prefix that values must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withStartsWith(@NonNull String prefix) {
		Objects.requireNonNull(prefix, "Prefix for 'starts with' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, Optional.of(Pair.of(prefix, false)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative prefix constraint.<br>
	 *
	 * @param prefix The prefix that values must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotStartsWith(@NonNull String prefix) {
		Objects.requireNonNull(prefix, "Prefix for 'not starts with' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, Optional.of(Pair.of(prefix, true)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes, one of which values must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		Objects.requireNonNull(prefixes, "Prefixes for 'starts with any' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), false)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes that values must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		Objects.requireNonNull(prefixes, "Prefixes for 'starts with none' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), true)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified containment constraint.<br>
	 *
	 * @param substring The substring that values must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withContains(@NonNull String substring) {
		Objects.requireNonNull(substring, "Substring for 'contains' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, Optional.of(Pair.of(substring, false)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative containment constraint.<br>
	 *
	 * @param substring The substring that values must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotContains(@NonNull String substring) {
		Objects.requireNonNull(substring, "Substring for 'not contains' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, Optional.of(Pair.of(substring, true)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings, one of which values must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains any' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), false)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that values must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains none' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), true)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified all-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that values must all contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains all' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified only-containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that values must exclusively contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		Objects.requireNonNull(substrings, "Substrings for 'contains only' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified suffix constraint.<br>
	 *
	 * @param suffix The suffix that values must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withEndsWith(@NonNull String suffix) {
		Objects.requireNonNull(suffix, "Suffix for 'ends with' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(suffix, false)), this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative suffix constraint.<br>
	 *
	 * @param suffix The suffix that values must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotEndsWith(@NonNull String suffix) {
		Objects.requireNonNull(suffix, "Suffix for 'not ends with' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(suffix, true)), this.endsWithAny, this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes, one of which values must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		Objects.requireNonNull(suffixes, "Suffixes for 'ends with any' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), false)), this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes that values must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		Objects.requireNonNull(suffixes, "Suffixes for 'ends with none' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), true)), this.matches, this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified regex constraint.<br>
	 *
	 * @param regex The regex pattern that values must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withMatches(@NonNull String regex) {
		Objects.requireNonNull(regex, "Regex for 'matches' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), false)), this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that values must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withMatches(@NonNull Pattern pattern) {
		Objects.requireNonNull(pattern, "Pattern for 'matches' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(pattern, false)), this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative regex constraint.<br>
	 *
	 * @param regex The regex pattern that values must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotMatches(@NonNull String regex) {
		Objects.requireNonNull(regex, "Regex for 'not matches' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), true)), this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the specified negative pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that values must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		Objects.requireNonNull(pattern, "Pattern for 'not matches' constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(pattern, true)), this.rootDomain, this.subDomain, this.custom);
	}
	
	/**
	 * Creates a new config with the root domain constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withRootDomain() {
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, Optional.of(Unit.INSTANCE), Optional.empty(), this.custom);
	}
	
	/**
	 * Creates a new config with the subdomain constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withSubDomain() {
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, Optional.empty(), Optional.of(Unit.INSTANCE), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DomainConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new DomainConstraintConfig(this.equalTo, this.in, this.length, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.rootDomain, this.subDomain, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchExtractedValue(value, this.length, String::length, "Length"),
			() -> ConstraintMatchers.matchStartsWith(value, this.startsWith),
			() -> ConstraintMatchers.matchStartsWithAny(value, this.startsWithAny),
			() -> ConstraintMatchers.matchContains(value, this.contains),
			() -> ConstraintMatchers.matchContainsAny(value, this.containsAny),
			() -> ConstraintMatchers.matchContainsAll(value, this.containsAll),
			() -> ConstraintMatchers.matchContainsOnly(value, this.containsOnly),
			() -> ConstraintMatchers.matchEndsWith(value, this.endsWith),
			() -> ConstraintMatchers.matchEndsWithAny(value, this.endsWithAny),
			() -> ConstraintMatchers.matchPattern(value, this.matches),
			() -> NetworkMatchers.matchRootDomain(value, this.rootDomain),
			() -> NetworkMatchers.matchSubDomain(value, this.subDomain),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
