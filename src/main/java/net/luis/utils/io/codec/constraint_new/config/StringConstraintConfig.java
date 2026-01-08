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

package net.luis.utils.io.codec.constraint_new.config;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Configuration record for string constraints.<br>
 * <p>
 *     This record stores the constraint values for {@link net.luis.utils.io.codec.constraint_new.StringConstraint}.<br>
 *     It includes base constraints, length constraints, char sequence pattern matching, and string-specific validations.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The string equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The string set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param equalToIgnoreCase The case-insensitive equality constraint as a pair of (value, negated)
 * @param inIgnoreCase The case-insensitive set constraint as a pair of (values, negated)
 * @param minLength The minimum length constraint as a pair of (value, inclusive)
 * @param maxLength The maximum length constraint as a pair of (value, inclusive)
 * @param startsWith The prefix constraint as a pair of (prefix, negated) where negated=false means startsWith and negated=true means notStartsWith
 * @param startsWithAny The prefix set constraint as a pair of (prefixes, negated) where negated=false means startsWithAny and negated=true means startsWithNone
 * @param contains The substring constraint as a pair of (substring, negated) where negated=false means contains and negated=true means notContains
 * @param containsAny The substring set constraint as a pair of (substrings, negated) where negated=false means containsAny and negated=true means containsNone
 * @param containsAll The set of substrings that strings must all contain
 * @param containsOnly The set of substrings that strings must exclusively contain
 * @param endsWith The suffix constraint as a pair of (suffix, negated) where negated=false means endsWith and negated=true means notEndsWith
 * @param endsWithAny The suffix set constraint as a pair of (suffixes, negated) where negated=false means endsWithAny and negated=true means endsWithNone
 * @param matches The pattern constraint as a pair of (pattern, negated) where negated=false means matches and negated=true means notMatches
 * @param trimmed If present, requires strings to have no leading/trailing whitespace
 * @param blank If present, requires strings to be blank
 * @param notBlank If present, requires strings to not be blank
 * @param upperCase If present, requires strings to be upper case
 * @param lowerCase If present, requires strings to be lower case
 * @param numeric If present, requires strings to contain only digits
 * @param alphabetic If present, requires strings to contain only letters
 * @param alphanumeric If present, requires strings to contain only letters and digits
 * @param ascii If present, requires strings to contain only ASCII characters
 * @param latin1 If present, requires strings to contain only Latin-1 characters
 * @param custom A custom constraint implementation
 */
public record StringConstraintConfig(
	// BaseConstraint fields (merged)
	@NonNull Optional<Pair<String, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<String>, Boolean>> in,
	// StringConstraint case-insensitive fields (merged)
	@NonNull Optional<Pair<String, Boolean>> equalToIgnoreCase,
	@NonNull Optional<Pair<Set<String>, Boolean>> inIgnoreCase,
	// LengthConstraint fields
	@NonNull Optional<Pair<Integer, Boolean>> minLength,
	@NonNull Optional<Pair<Integer, Boolean>> maxLength,
	// CharSequenceConstraint fields (merged)
	@NonNull Optional<Pair<String, Boolean>> startsWith,
	@NonNull Optional<Pair<Set<String>, Boolean>> startsWithAny,
	@NonNull Optional<Pair<String, Boolean>> contains,
	@NonNull Optional<Pair<Set<String>, Boolean>> containsAny,
	@NonNull Optional<Set<String>> containsAll,
	@NonNull Optional<Set<String>> containsOnly,
	@NonNull Optional<Pair<String, Boolean>> endsWith,
	@NonNull Optional<Pair<Set<String>, Boolean>> endsWithAny,
	@NonNull Optional<Pair<Pattern, Boolean>> matches,
	// StringConstraint boolean flags
	@NonNull Optional<Void> trimmed,
	@NonNull Optional<Void> blank,
	@NonNull Optional<Void> notBlank,
	@NonNull Optional<Void> upperCase,
	@NonNull Optional<Void> lowerCase,
	@NonNull Optional<Void> numeric,
	@NonNull Optional<Void> alphabetic,
	@NonNull Optional<Void> alphanumeric,
	@NonNull Optional<Void> ascii,
	@NonNull Optional<Void> latin1,
	// Custom constraint
	@NonNull Optional<Constraint<String>> custom
) {

	/**
	 * An unconstrained string configuration with no constraints applied.<br>
	 */
	public static final StringConstraintConfig UNCONSTRAINED = new StringConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty()
	);

	// BaseConstraint with methods

	public @NonNull StringConstraintConfig withEqualTo(@NonNull String value) {
		return new StringConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotEqualTo(@NonNull String value) {
		return new StringConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withIn(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotIn(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// Case-insensitive with methods

	public @NonNull StringConstraintConfig withEqualToIgnoreCase(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotEqualToIgnoreCase(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withInIgnoreCase(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, Optional.of(Pair.of(Set.copyOf(values), false)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotInIgnoreCase(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, Optional.of(Pair.of(Set.copyOf(values), true)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// LengthConstraint with methods

	public @NonNull StringConstraintConfig withMinLength(int minLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, Optional.of(Pair.of(minLength, true)), this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withMaxLength(int maxLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, Optional.of(Pair.of(maxLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withExactLength(int exactLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// CharSequenceConstraint with methods

	public @NonNull StringConstraintConfig withStartsWith(@NonNull String prefix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, Optional.of(Pair.of(Objects.requireNonNull(prefix), false)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotStartsWith(@NonNull String prefix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, Optional.of(Pair.of(Objects.requireNonNull(prefix), true)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), false)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), true)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContains(@NonNull String substring) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(Objects.requireNonNull(substring), false)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotContains(@NonNull String substring) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(Objects.requireNonNull(substring), true)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), false)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), true)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withEndsWith(@NonNull String suffix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(Objects.requireNonNull(suffix), false)), this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotEndsWith(@NonNull String suffix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(Objects.requireNonNull(suffix), true)), this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), false)), this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), true)), this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withMatches(@NonNull String regex) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), false)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withMatches(@NonNull Pattern pattern) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Objects.requireNonNull(pattern), false)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotMatches(@NonNull String regex) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), true)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Objects.requireNonNull(pattern), true)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// StringConstraint boolean flag with methods

	public @NonNull StringConstraintConfig withTrimmed() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, Optional.of(null), this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withBlank() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, Optional.of(null), this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotBlank() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, Optional.of(null), this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withUpperCase() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, Optional.of(null), this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withLowerCase() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, Optional.of(null), this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNumeric() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, Optional.of(null), this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withAlphabetic() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, Optional.of(null), this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withAlphanumeric() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, Optional.of(null), this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withAscii() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, Optional.of(null), this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withLatin1() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, Optional.of(null), this.custom);
	}

	public @NonNull StringConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, Optional.of(Objects.requireNonNull(constraint)));
	}
}
