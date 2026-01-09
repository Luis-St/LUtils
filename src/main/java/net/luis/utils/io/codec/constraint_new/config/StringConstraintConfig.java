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
import net.luis.utils.io.codec.constraint_new.StringConstraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Configuration record for string constraints.<br>
 * <p>
 *     This record stores the constraint values for {@link StringConstraint}.<br>
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
 *
 * @throws NullPointerException If any optional field is null
 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
 * @throws IllegalArgumentException If the 'inIgnoreCase' constraint set is empty when present
 * @throws IllegalArgumentException If the minimum length is negative when present
 * @throws IllegalArgumentException If the maximum length is negative when present
 * @throws IllegalArgumentException If the minimum length is greater than the maximum length when both are present
 * @throws IllegalArgumentException If min and max length are equal but at least one bound is exclusive when both are present
 * @throws IllegalArgumentException If the 'startsWithAny' constraint set is empty when present
 * @throws IllegalArgumentException If the 'containsAny' constraint set is empty when present
 * @throws IllegalArgumentException If the 'containsAll' constraint set is empty when present
 * @throws IllegalArgumentException If the 'containsOnly' constraint set is empty when present
 * @throws IllegalArgumentException If the 'endsWithAny' constraint set is empty when present
 * @throws IllegalArgumentException If both 'blank' and 'notBlank' constraints are present
 * @throws IllegalArgumentException If both 'upperCase' and 'lowerCase' constraints are present
 * @throws IllegalArgumentException If 'blank' constraint conflicts with minimum length greater than zero
 * @throws IllegalArgumentException If 'notBlank' constraint conflicts with maximum length of zero
 */
@SuppressWarnings("OptionalContainsCollection")
public record StringConstraintConfig(
	@NonNull Optional<Pair<String, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<String>, Boolean>> in,
	@NonNull Optional<Pair<String, Boolean>> equalToIgnoreCase,
	@NonNull Optional<Pair<Set<String>, Boolean>> inIgnoreCase,
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
	@NonNull Optional<Constraint<String>> custom
) {
	
	/**
	 * An unconstrained string configuration with no constraints applied.<br>
	 */
	public static final StringConstraintConfig UNCONSTRAINED = new StringConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Canonical constructor for string constraint configuration.<br>
	 *
	 * @param equalTo The string equality constraint
	 * @param in The string set constraint
	 * @param equalToIgnoreCase The case-insensitive equality constraint
	 * @param inIgnoreCase The case-insensitive set constraint
	 * @param minLength The minimum length constraint
	 * @param maxLength The maximum length constraint
	 * @param startsWith The prefix constraint
	 * @param startsWithAny The prefix set constraint
	 * @param contains The substring constraint
	 * @param containsAny The substring set constraint
	 * @param containsAll The set of substrings that strings must all contain
	 * @param containsOnly The set of substrings that strings must exclusively contain
	 * @param endsWith The suffix constraint
	 * @param endsWithAny The suffix set constraint
	 * @param matches The pattern constraint
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
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'inIgnoreCase' constraint set is empty when present
	 * @throws IllegalArgumentException If the minimum length is negative when present
	 * @throws IllegalArgumentException If the maximum length is negative when present
	 * @throws IllegalArgumentException If the minimum length is greater than the maximum length when both are present
	 * @throws IllegalArgumentException If min and max length are equal but at least one bound is exclusive when both are present
	 * @throws IllegalArgumentException If the 'startsWithAny' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'containsAny' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'containsAll' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'containsOnly' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'endsWithAny' constraint set is empty when present
	 * @throws IllegalArgumentException If both 'blank' and 'notBlank' constraints are present
	 * @throws IllegalArgumentException If both 'upperCase' and 'lowerCase' constraints are present
	 * @throws IllegalArgumentException If 'blank' constraint conflicts with minimum length greater than zero
	 * @throws IllegalArgumentException If 'notBlank' constraint conflicts with maximum length of zero
	 */
	public StringConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(equalToIgnoreCase, "Optional for 'equal to ignore case' constraint must not be null");
		Objects.requireNonNull(inIgnoreCase, "Optional for 'in ignore case' constraint must not be null");
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
		Objects.requireNonNull(trimmed, "Optional for 'trimmed' constraint must not be null");
		Objects.requireNonNull(blank, "Optional for 'blank' constraint must not be null");
		Objects.requireNonNull(notBlank, "Optional for 'not blank' constraint must not be null");
		Objects.requireNonNull(upperCase, "Optional for 'upper case' constraint must not be null");
		Objects.requireNonNull(lowerCase, "Optional for 'lower case' constraint must not be null");
		Objects.requireNonNull(numeric, "Optional for 'numeric' constraint must not be null");
		Objects.requireNonNull(alphabetic, "Optional for 'alphabetic' constraint must not be null");
		Objects.requireNonNull(alphanumeric, "Optional for 'alphanumeric' constraint must not be null");
		Objects.requireNonNull(ascii, "Optional for 'ascii' constraint must not be null");
		Objects.requireNonNull(latin1, "Optional for 'latin1' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in' constraint set must not be empty when present");
		}
		
		if (inIgnoreCase.isPresent() && inIgnoreCase.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in ignore case' constraint set must not be empty when present");
		}
		
		if (minLength.isPresent() && minLength.get().getFirst() < 0) {
			throw new IllegalArgumentException("Min length must be non-negative when present, but got " + minLength.get().getFirst());
		}
		
		if (maxLength.isPresent() && maxLength.get().getFirst() < 0) {
			throw new IllegalArgumentException("Max length must be non-negative when present, but got " + maxLength.get().getFirst());
		}
		
		if (minLength.isPresent() && maxLength.isPresent() && minLength.get().getFirst() > maxLength.get().getFirst()) {
			throw new IllegalArgumentException("Min length must be less than or equal to max length when both are present, but got " + minLength.get().getFirst() + " > " + maxLength.get().getFirst());
		}
		
		if (minLength.isPresent() && maxLength.isPresent() && minLength.get().getFirst().equals(maxLength.get().getFirst()) && (!minLength.get().getSecond() || !maxLength.get().getSecond())) {
			throw new IllegalArgumentException("Min and max length are equal but at least one bound is exclusive when both are present");
		}
		
		if (startsWithAny.isPresent() && startsWithAny.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'starts with any' constraint set must not be empty when present");
		}
		
		if (containsAny.isPresent() && containsAny.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'contains any' constraint set must not be empty when present");
		}
		
		if (containsAll.isPresent() && containsAll.get().isEmpty()) {
			throw new IllegalArgumentException("The 'contains all' constraint set must not be empty when present");
		}
		if (containsOnly.isPresent() && containsOnly.get().isEmpty()) {
			throw new IllegalArgumentException("The 'contains only' constraint set must not be empty when present");
		}
		
		if (endsWithAny.isPresent() && endsWithAny.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'ends with any' constraint set must not be empty when present");
		}
		
		if (blank.isPresent() && notBlank.isPresent()) {
			throw new IllegalArgumentException("Both 'blank' and 'not blank' constraints cannot be present at the same time");
		}
		
		if (upperCase.isPresent() && lowerCase.isPresent()) {
			throw new IllegalArgumentException("Both 'upper case' and 'lower case' constraints cannot be present at the same time");
		}
		
		if (blank.isPresent() && minLength.isPresent() && minLength.get().getFirst() > 0) {
			throw new IllegalArgumentException("Blank constraint conflicts with minimum length greater than zero");
		}
		
		if (notBlank.isPresent() && maxLength.isPresent() && maxLength.get().getFirst() == 0) {
			throw new IllegalArgumentException("Not blank constraint conflicts with maximum length of zero");
		}
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact string value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withEqualTo(@NonNull String value) {
		return new StringConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The string value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotEqualTo(@NonNull String value) {
		return new StringConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of string values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withIn(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of string values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotIn(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified case-insensitive equal-to constraint.<br>
	 *
	 * @param value The string value to compare against (case-insensitive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withEqualToIgnoreCase(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified case-insensitive not-equal-to constraint.<br>
	 *
	 * @param value The string value that should be excluded (case-insensitive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotEqualToIgnoreCase(@NonNull String value) {
		return new StringConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified case-insensitive inclusion constraint.<br>
	 *
	 * @param values The collection of string values that are allowed (case-insensitive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withInIgnoreCase(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, Optional.of(Pair.of(Set.copyOf(values), false)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified case-insensitive exclusion constraint.<br>
	 *
	 * @param values The collection of string values that are not allowed (case-insensitive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotInIgnoreCase(@NonNull Collection<String> values) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, Optional.of(Pair.of(Set.copyOf(values), true)), this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified minimum length constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withMinLength(int minLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, Optional.of(Pair.of(minLength, true)), this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified maximum length constraint (inclusive).<br>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withMaxLength(int maxLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, Optional.of(Pair.of(maxLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified exact length constraint.<br>
	 * <p>
	 *     This sets both min and max length to the same value with inclusive bounds.
	 * </p>
	 *
	 * @param exactLength The exact length required
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withExactLength(int exactLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, Optional.of(Pair.of(exactLength, true)), Optional.of(Pair.of(exactLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified length range constraint (inclusive).<br>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withLengthBetween(int minLength, int maxLength) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, Optional.of(Pair.of(minLength, true)), Optional.of(Pair.of(maxLength, true)), this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified prefix constraint.<br>
	 *
	 * @param prefix The prefix that strings must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withStartsWith(@NonNull String prefix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, Optional.of(Pair.of(Objects.requireNonNull(prefix), false)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative prefix constraint.<br>
	 *
	 * @param prefix The prefix that strings must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotStartsWith(@NonNull String prefix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, Optional.of(Pair.of(Objects.requireNonNull(prefix), true)), this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes, one of which strings must start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withStartsWithAny(@NonNull Collection<String> prefixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), false)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative multi-prefix constraint.<br>
	 *
	 * @param prefixes The collection of prefixes that strings must not start with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withStartsWithNone(@NonNull Collection<String> prefixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, Optional.of(Pair.of(Set.copyOf(prefixes), true)), this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified substring containment constraint.<br>
	 *
	 * @param substring The substring that strings must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withContains(@NonNull String substring) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(Objects.requireNonNull(substring), false)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative substring containment constraint.<br>
	 *
	 * @param substring The substring that strings must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotContains(@NonNull String substring) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, Optional.of(Pair.of(Objects.requireNonNull(substring), true)), this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified multi-substring containment constraint.<br>
	 *
	 * @param substrings The collection of substrings, one of which strings must contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withContainsAny(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), false)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative multi-substring containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that strings must not contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withContainsNone(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, Optional.of(Pair.of(Set.copyOf(substrings), true)), this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified all-substring containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that strings must all contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withContainsAll(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, Optional.of(Set.copyOf(substrings)), this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified only-substring containment constraint.<br>
	 *
	 * @param substrings The collection of substrings that strings must exclusively contain
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withContainsOnly(@NonNull Collection<String> substrings) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, Optional.of(Set.copyOf(substrings)), this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified suffix constraint.<br>
	 *
	 * @param suffix The suffix that strings must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withEndsWith(@NonNull String suffix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(Objects.requireNonNull(suffix), false)), this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative suffix constraint.<br>
	 *
	 * @param suffix The suffix that strings must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotEndsWith(@NonNull String suffix) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, Optional.of(Pair.of(Objects.requireNonNull(suffix), true)), this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes, one of which strings must end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withEndsWithAny(@NonNull Collection<String> suffixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), false)), this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative multi-suffix constraint.<br>
	 *
	 * @param suffixes The collection of suffixes that strings must not end with
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withEndsWithNone(@NonNull Collection<String> suffixes) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, Optional.of(Pair.of(Set.copyOf(suffixes), true)), this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified regex pattern constraint.<br>
	 *
	 * @param regex The regular expression that strings must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withMatches(@NonNull String regex) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), false)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified compiled pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that strings must match
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withMatches(@NonNull Pattern pattern) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Objects.requireNonNull(pattern), false)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative regex pattern constraint.<br>
	 *
	 * @param regex The regular expression that strings must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotMatches(@NonNull String regex) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Pattern.compile(regex), true)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the specified negative compiled pattern constraint.<br>
	 *
	 * @param pattern The compiled pattern that strings must not match
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotMatches(@NonNull Pattern pattern) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, Optional.of(Pair.of(Objects.requireNonNull(pattern), true)), this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the trimmed constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withTrimmed() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, Optional.of(null), this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the blank constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withBlank() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, Optional.of(null), this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the not-blank constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNotBlank() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, Optional.of(null), this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the upper case constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withUpperCase() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, Optional.of(null), this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the lower case constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withLowerCase() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, Optional.of(null), this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the numeric constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withNumeric() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, Optional.of(null), this.alphabetic, this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the alphabetic constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withAlphabetic() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, Optional.of(null), this.alphanumeric, this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the alphanumeric constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withAlphanumeric() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, Optional.of(null), this.ascii, this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the ASCII constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withAscii() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, Optional.of(null), this.latin1, this.custom);
	}

	/**
	 * Creates a new config with the Latin-1 constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withLatin1() {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, Optional.of(null), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull StringConstraintConfig withCustom(@NonNull Constraint<String> constraint) {
		return new StringConstraintConfig(this.equalTo, this.in, this.equalToIgnoreCase, this.inIgnoreCase, this.minLength, this.maxLength, this.startsWith, this.startsWithAny, this.contains, this.containsAny, this.containsAll, this.containsOnly, this.endsWith, this.endsWithAny, this.matches, this.trimmed, this.blank, this.notBlank, this.upperCase, this.lowerCase, this.numeric, this.alphabetic, this.alphanumeric, this.ascii, this.latin1, Optional.of(Objects.requireNonNull(constraint)));
	}
}
