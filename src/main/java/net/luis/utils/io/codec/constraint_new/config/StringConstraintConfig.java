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
 * @param equalTo The exact string value that should be matched
 * @param notEqualTo The string value that should be excluded
 * @param in The set of strings that are allowed
 * @param notIn The set of strings that are not allowed
 * @param equalToIgnoreCase The string value that should be matched (case-insensitive)
 * @param notEqualToIgnoreCase The string value that should be excluded (case-insensitive)
 * @param inIgnoreCase The set of strings that are allowed (case-insensitive)
 * @param notInIgnoreCase The set of strings that are not allowed (case-insensitive)
 * @param minLength The minimum length constraint as a pair of (value, inclusive)
 * @param maxLength The maximum length constraint as a pair of (value, inclusive)
 * @param startsWith The prefix that strings must start with
 * @param notStartsWith The prefix that strings must not start with
 * @param startsWithAny The set of prefixes, one of which strings must start with
 * @param startsWithNone The set of prefixes that strings must not start with
 * @param contains The substring that strings must contain
 * @param notContains The substring that strings must not contain
 * @param containsAny The set of substrings, one of which strings must contain
 * @param containsNone The set of substrings that strings must not contain
 * @param containsAll The set of substrings that strings must all contain
 * @param containsOnly The set of substrings that strings must exclusively contain
 * @param endsWith The suffix that strings must end with
 * @param notEndsWith The suffix that strings must not end with
 * @param endsWithAny The set of suffixes, one of which strings must end with
 * @param endsWithNone The set of suffixes that strings must not end with
 * @param matches The pattern that strings must match
 * @param notMatches The pattern that strings must not match
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
	// BaseConstraint fields
	@NonNull Optional<String> equalTo,
	@NonNull Optional<String> notEqualTo,
	@NonNull Optional<Set<String>> in,
	@NonNull Optional<Set<String>> notIn,
	// StringConstraint case-insensitive fields
	@NonNull Optional<String> equalToIgnoreCase,
	@NonNull Optional<String> notEqualToIgnoreCase,
	@NonNull Optional<Set<String>> inIgnoreCase,
	@NonNull Optional<Set<String>> notInIgnoreCase,
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
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty()
	);

	// BaseConstraint with methods

	public @NonNull StringConstraintConfig withEqualTo(@NonNull String value) {
		return new StringConstraintConfig(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotEqualTo(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withIn(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotIn(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// Case-insensitive with methods

	public @NonNull StringConstraintConfig withEqualToIgnoreCase(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Objects.requireNonNull(value)), this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotEqualToIgnoreCase(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, Optional.of(Objects.requireNonNull(value)), this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withInIgnoreCase(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, Optional.of(Set.copyOf(values)), this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotInIgnoreCase(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, Optional.of(Set.copyOf(values)), this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// LengthConstraint with methods

	public @NonNull StringConstraintConfig withMinLength(int minLength) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, Optional.of(Pair.of(minLength, true)), this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withMaxLength(int maxLength) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, Optional.of(Pair.of(maxLength, true)), this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withExactLength(int exactLength) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// CharSequenceConstraint with methods

	public @NonNull StringConstraintConfig withStartsWith(@NonNull String prefix) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, Optional.of(Objects.requireNonNull(prefix)), this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotStartsWith(@NonNull String prefix) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, Optional.of(Objects.requireNonNull(prefix)), this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, Optional.of(Set.copyOf(prefixes)), this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, Optional.of(Set.copyOf(prefixes)), this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContains(@NonNull String substring) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, Optional.of(Objects.requireNonNull(substring)), this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotContains(@NonNull String substring) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, Optional.of(Objects.requireNonNull(substring)), this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, Optional.of(Set.copyOf(substrings)), this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withEndsWith(@NonNull String suffix) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, Optional.of(Objects.requireNonNull(suffix)), this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotEndsWith(@NonNull String suffix) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Objects.requireNonNull(suffix)), this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, Optional.of(Set.copyOf(suffixes)), this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, Optional.of(Set.copyOf(suffixes)), this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withMatches(@NonNull String regex) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, Optional.of(Pattern.compile(regex)), this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withMatches(@NonNull Pattern pattern) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, Optional.of(Objects.requireNonNull(pattern)), this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotMatches(@NonNull String regex) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, Optional.of(Pattern.compile(regex)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, Optional.of(Objects.requireNonNull(pattern)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	// StringConstraint boolean flag with methods

	public @NonNull StringConstraintConfig withTrimmed() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, Optional.of(null), this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withBlank() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, Optional.of(null), this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNotBlank() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, Optional.of(null), this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withUpperCase() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, Optional.of(null), this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withLowerCase() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, Optional.of(null), this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withNumeric() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, Optional.of(null), this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withAlphabetic() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, Optional.of(null), this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withAlphanumeric() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, Optional.of(null), this.ascii, this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withAscii() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, Optional.of(null), this.latin1, this.custom);
	}

	public @NonNull StringConstraintConfig withLatin1() {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, Optional.of(null), this.custom);
	}

	public @NonNull StringConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		return new StringConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.equalToIgnoreCase, this.notEqualToIgnoreCase, this.inIgnoreCase, this.notInIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.notStartsWith, this.startsWithAny, this.startsWithNone, this.contains, this.notContains, this.containsAny, this.containsNone, this.containsAll, this.containsOnly, this.endsWith, this.notEndsWith, this.endsWithAny, this.endsWithNone, this.matches, this.notMatches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, Optional.of(Objects.requireNonNull(constraint)));
	}
}
