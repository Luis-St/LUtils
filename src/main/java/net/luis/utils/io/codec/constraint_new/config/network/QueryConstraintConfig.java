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
import net.luis.utils.io.codec.constraint_new.config.*;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Configuration record for URI query parameter constraints.<br>
 * <p>
 *     This record stores the constraint values for query parameter codecs.<br>
 *     It includes map constraints (size, keys) and query-specific constraints
 *     for value validation and single/multi-value requirements.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated)
 * @param in The set membership constraint as a pair of (values, negated)
 * @param min The minimum size constraint as a pair of (value, inclusive)
 * @param max The maximum size constraint as a pair of (value, inclusive)
 * @param requiredKeys The set of keys that must be present
 * @param forbiddenKeys The set of keys that must not be present
 * @param allowedKeys The set of keys that are allowed (others forbidden)
 * @param nonNullKeys If present, requires all keys to be non-null
 * @param uniqueValues If present, requires all values to be unique
 * @param nonNullValues If present, requires all values to be non-null
 * @param valueConstraints A map of key to string constraint config for value validation
 * @param patternValueConstraints A map of pattern to string constraint config for value validation
 * @param singleValued If present, requires all parameters to have exactly one value
 * @param multiValuedConstraints A map of key to size constraint config for multi-value validation
 * @param custom A custom constraint implementation
 */
@SuppressWarnings("OptionalContainsCollection")
public record QueryConstraintConfig(
	@NonNull Optional<Pair<Map<String, List<String>>, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Map<String, List<String>>>, Boolean>> in,
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Set<String>> requiredKeys,
	@NonNull Optional<Set<String>> forbiddenKeys,
	@NonNull Optional<Set<String>> allowedKeys,
	@NonNull Optional<Unit> nonNullKeys,
	@NonNull Optional<Unit> uniqueValues,
	@NonNull Optional<Unit> nonNullValues,
	@NonNull Optional<Map<String, StringConstraintConfig>> valueConstraints,
	@NonNull Optional<Map<Pattern, StringConstraintConfig>> patternValueConstraints,
	@NonNull Optional<Unit> singleValued,
	@NonNull Optional<Map<String, SizeConstraintConfig>> multiValuedConstraints,
	@NonNull Optional<Constraint<Map<String, List<String>>>> custom
) implements ConstraintConfig<Map<String, List<String>>> {
	
	/**
	 * An unconstrained query configuration with no constraints applied.<br>
	 */
	public static final QueryConstraintConfig UNCONSTRAINED = new QueryConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new query constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated)
	 * @param in The set membership constraint as a pair of (values, negated)
	 * @param min The minimum size constraint as a pair of (value, inclusive)
	 * @param max The maximum size constraint as a pair of (value, inclusive)
	 * @param requiredKeys The set of keys that must be present
	 * @param forbiddenKeys The set of keys that must not be present
	 * @param allowedKeys The set of keys that are allowed (others forbidden)
	 * @param nonNullKeys If present, requires all keys to be non-null
	 * @param uniqueValues If present, requires all values to be unique
	 * @param nonNullValues If present, requires all values to be non-null
	 * @param valueConstraints A map of key to string constraint config for value validation
	 * @param patternValueConstraints A map of pattern to string constraint config for value validation
	 * @param singleValued If present, requires all parameters to have exactly one value
	 * @param multiValuedConstraints A map of key to size constraint config for multi-value validation
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the in set is empty when present
	 * @throws IllegalArgumentException If min is greater than max when both are present
	 * @throws IllegalArgumentException If min and max are equal but at least one bound is exclusive when both are present
	 * @throws IllegalArgumentException If the required keys set is empty when present
	 * @throws IllegalArgumentException If the forbidden keys set is empty when present
	 * @throws IllegalArgumentException If the allowed keys set is empty when present
	 */
	public QueryConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(requiredKeys, "Optional for 'required keys' constraint must not be null");
		Objects.requireNonNull(forbiddenKeys, "Optional for 'forbidden keys' constraint must not be null");
		Objects.requireNonNull(allowedKeys, "Optional for 'allowed keys' constraint must not be null");
		Objects.requireNonNull(nonNullKeys, "Optional for 'non null keys' constraint must not be null");
		Objects.requireNonNull(uniqueValues, "Optional for 'unique values' constraint must not be null");
		Objects.requireNonNull(nonNullValues, "Optional for 'non null values' constraint must not be null");
		Objects.requireNonNull(valueConstraints, "Optional for 'value constraints' constraint must not be null");
		Objects.requireNonNull(patternValueConstraints, "Optional for 'pattern value constraints' constraint must not be null");
		Objects.requireNonNull(singleValued, "Optional for 'single valued' constraint must not be null");
		Objects.requireNonNull(multiValuedConstraints, "Optional for 'multi valued constraints' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
		
		if (min.isPresent() && max.isPresent()) {
			if (min.get().getFirst().compareTo(max.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
			}
			if (min.get().getFirst().compareTo(max.get().getFirst()) == 0 && (!min.get().getSecond() || !max.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
		}
		
		if (requiredKeys.isPresent() && requiredKeys.get().isEmpty()) {
			throw new IllegalArgumentException("Required keys set must not be empty when present");
		}
		
		if (forbiddenKeys.isPresent() && forbiddenKeys.get().isEmpty()) {
			throw new IllegalArgumentException("Forbidden keys set must not be empty when present");
		}
		
		if (allowedKeys.isPresent() && allowedKeys.get().isEmpty()) {
			throw new IllegalArgumentException("Allowed keys set must not be empty when present");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equality constraint.<br>
	 *
	 * @param value The value that the query parameters must equal
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull QueryConstraintConfig withEqualTo(@NonNull Map<String, List<String>> value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new QueryConstraintConfig(Optional.of(Pair.of(Map.copyOf(value), false)), this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that the query parameters must not equal
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull QueryConstraintConfig withNotEqualTo(@NonNull Map<String, List<String>> value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new QueryConstraintConfig(Optional.of(Pair.of(Map.copyOf(value), true)), this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified set membership constraint.<br>
	 *
	 * @param values The values that the query parameters must be in
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull QueryConstraintConfig withIn(@NonNull Collection<Map<String, List<String>>> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new QueryConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-in constraint.<br>
	 *
	 * @param values The values that the query parameters must not be in
	 * @return A new config with the constraint applied
	 * @throws NullPointerException If the values collection is null
	 */
	public @NonNull QueryConstraintConfig withNotIn(@NonNull Collection<Map<String, List<String>>> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new QueryConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum size constraint (inclusive).<br>
	 *
	 * @param minSize The minimum number of query parameters (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withMinSize(int minSize) {
		return new QueryConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minSize, true)), this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum size constraint (inclusive).<br>
	 *
	 * @param maxSize The maximum number of query parameters (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withMaxSize(int maxSize) {
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(maxSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact size constraint.<br>
	 *
	 * @param exactSize The exact number of query parameters required
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withExactSize(int exactSize) {
		return new QueryConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(exactSize, true)), Optional.of(Pair.of(exactSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified size range constraint (inclusive).<br>
	 *
	 * @param minSize The minimum number of query parameters (inclusive)
	 * @param maxSize The maximum number of query parameters (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withSizeBetween(int minSize, int maxSize) {
		return new QueryConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(minSize, true)), Optional.of(Pair.of(maxSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified required keys constraint.<br>
	 *
	 * @param keys The collection of keys that must be present
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withRequiredKeys(@NonNull Collection<String> keys) {
		Objects.requireNonNull(keys, "Keys for 'required keys' constraint must not be null");
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(Set.copyOf(keys)), this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified forbidden keys constraint.<br>
	 *
	 * @param keys The collection of keys that must not be present
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withForbiddenKeys(@NonNull Collection<String> keys) {
		Objects.requireNonNull(keys, "Keys for 'forbidden keys' constraint must not be null");
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, Optional.of(Set.copyOf(keys)), this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified allowed keys constraint.<br>
	 *
	 * @param keys The collection of keys that are allowed (others forbidden)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withAllowedKeys(@NonNull Collection<String> keys) {
		Objects.requireNonNull(keys, "Keys for 'allowed keys' constraint must not be null");
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, Optional.of(Set.copyOf(keys)), this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the non-null keys constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withNonNullKeys() {
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, Optional.of(Unit.INSTANCE), this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the unique values constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withUniqueValues() {
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, Optional.of(Unit.INSTANCE), this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the non-null values constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withNonNullValues() {
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, Optional.of(Unit.INSTANCE), this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified value constraint for a key.<br>
	 *
	 * @param key The query parameter key to constrain
	 * @param config The string constraint config for value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withValue(@NonNull String key, @NonNull StringConstraintConfig config) {
		Objects.requireNonNull(key, "Key for 'value' constraint must not be null");
		Objects.requireNonNull(config, "Config for 'value' constraint must not be null");
		
		Map<String, StringConstraintConfig> newConstraints = new HashMap<>(this.valueConstraints.orElse(Map.of()));
		newConstraints.put(key, config);
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, Optional.of(Map.copyOf(newConstraints)), this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified value constraint for keys matching a regex.<br>
	 *
	 * @param regex The regex pattern to match query parameter keys
	 * @param config The string constraint config for value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withValues(@NonNull String regex, @NonNull StringConstraintConfig config) {
		Objects.requireNonNull(regex, "Regex for 'values' constraint must not be null");
		return this.withValues(Pattern.compile(regex), config);
	}
	
	/**
	 * Creates a new config with the specified value constraint for keys matching a pattern.<br>
	 *
	 * @param pattern The compiled pattern to match query parameter keys
	 * @param config The string constraint config for value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withValues(@NonNull Pattern pattern, @NonNull StringConstraintConfig config) {
		Objects.requireNonNull(pattern, "Pattern for 'values' constraint must not be null");
		Objects.requireNonNull(config, "Config for 'values' constraint must not be null");
		
		Map<Pattern, StringConstraintConfig> newConstraints = new HashMap<>(this.patternValueConstraints.orElse(Map.of()));
		newConstraints.put(pattern, config);
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, Optional.of(Map.copyOf(newConstraints)), this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the single-valued constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withSingleValued() {
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, Optional.of(Unit.INSTANCE), this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-valued constraint for a key.<br>
	 *
	 * @param key The query parameter key to constrain
	 * @param config The size constraint config for multi-value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withMultiValued(@NonNull String key, @NonNull SizeConstraintConfig config) {
		Objects.requireNonNull(key, "Key for 'multi valued' constraint must not be null");
		Objects.requireNonNull(config, "Config for 'multi valued' constraint must not be null");
		
		Map<String, SizeConstraintConfig> newConstraints = new HashMap<>(this.multiValuedConstraints.orElse(Map.of()));
		newConstraints.put(key, config);
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, Optional.of(Map.copyOf(newConstraints)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withCustom(@NonNull Constraint<Map<String, List<String>>> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new QueryConstraintConfig(this.equalTo, this.in, this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull Map<String, List<String>> value) {
		Objects.requireNonNull(value, "Value must not be null");

		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value.size(), this.min, this.max),
			() -> ConstraintMatchers.matchRequiredKeys(value.keySet(), this.requiredKeys, "Query"),
			() -> ConstraintMatchers.matchForbiddenKeys(value.keySet(), this.forbiddenKeys, "Query"),
			() -> ConstraintMatchers.matchAllowedKeys(value.keySet(), this.allowedKeys, "Query"),
			() -> ConstraintMatchers.matchFlag(value, this.nonNullKeys, v -> v.keySet().stream().noneMatch(Objects::isNull), "Query keys must not be null"),
			() -> NetworkMatchers.matchUniqueValues(value, this.uniqueValues),
			() -> ConstraintMatchers.matchFlag(value, this.nonNullValues, v -> v.values().stream().flatMap(List::stream).noneMatch(Objects::isNull), "Query values must not be null"),
			() -> NetworkMatchers.matchValueConstraints(value, this.valueConstraints),
			() -> NetworkMatchers.matchPatternValueConstraints(value, this.patternValueConstraints),
			() -> NetworkMatchers.matchSingleValued(value, this.singleValued),
			() -> NetworkMatchers.matchMultiValuedConstraints(value, this.multiValuedConstraints),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
