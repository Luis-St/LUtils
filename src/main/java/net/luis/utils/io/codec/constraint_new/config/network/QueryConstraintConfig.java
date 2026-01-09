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
import net.luis.utils.io.codec.constraint_new.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import net.luis.utils.util.Pair;
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
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Set<String>> requiredKeys,
	@NonNull Optional<Set<String>> forbiddenKeys,
	@NonNull Optional<Set<String>> allowedKeys,
	@NonNull Optional<Void> nonNullKeys,
	@NonNull Optional<Void> uniqueValues,
	@NonNull Optional<Void> nonNullValues,
	@NonNull Optional<Map<String, StringConstraintConfig>> valueConstraints,
	@NonNull Optional<Map<Pattern, StringConstraintConfig>> patternValueConstraints,
	@NonNull Optional<Void> singleValued,
	@NonNull Optional<Map<String, SizeConstraintConfig>> multiValuedConstraints,
	@NonNull Optional<Constraint<Map<String, List<String>>>> custom
) {
	
	/**
	 * An unconstrained query configuration with no constraints applied.<br>
	 */
	public static final QueryConstraintConfig UNCONSTRAINED = new QueryConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified minimum size constraint (inclusive).<br>
	 *
	 * @param minSize The minimum number of query parameters (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withMinSize(int minSize) {
		return new QueryConstraintConfig(Optional.of(Pair.of(minSize, true)), this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum size constraint (inclusive).<br>
	 *
	 * @param maxSize The maximum number of query parameters (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withMaxSize(int maxSize) {
		return new QueryConstraintConfig(this.min, Optional.of(Pair.of(maxSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact size constraint.<br>
	 *
	 * @param exactSize The exact number of query parameters required
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withExactSize(int exactSize) {
		return new QueryConstraintConfig(Optional.of(Pair.of(exactSize, true)), Optional.of(Pair.of(exactSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified size range constraint (inclusive).<br>
	 *
	 * @param minSize The minimum number of query parameters (inclusive)
	 * @param maxSize The maximum number of query parameters (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withSizeBetween(int minSize, int maxSize) {
		return new QueryConstraintConfig(Optional.of(Pair.of(minSize, true)), Optional.of(Pair.of(maxSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified required keys constraint.<br>
	 *
	 * @param keys The collection of keys that must be present
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withRequiredKeys(@NonNull Collection<String> keys) {
		return new QueryConstraintConfig(this.min, this.max, Optional.of(Set.copyOf(keys)), this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified forbidden keys constraint.<br>
	 *
	 * @param keys The collection of keys that must not be present
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withForbiddenKeys(@NonNull Collection<String> keys) {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, Optional.of(Set.copyOf(keys)), this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified allowed keys constraint.<br>
	 *
	 * @param keys The collection of keys that are allowed (others forbidden)
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withAllowedKeys(@NonNull Collection<String> keys) {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, Optional.of(Set.copyOf(keys)), this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the non-null keys constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withNonNullKeys() {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, Optional.of(null), this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the unique values constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withUniqueValues() {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, Optional.of(null), this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the non-null values constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withNonNullValues() {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, Optional.of(null), this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified value constraint for a key.<br>
	 *
	 * @param key The query parameter key to constrain
	 * @param config The string constraint config for value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withValue(@NonNull String key, @NonNull StringConstraintConfig config) {
		Map<String, StringConstraintConfig> newConstraints = new HashMap<>(this.valueConstraints.orElse(Map.of()));
		newConstraints.put(Objects.requireNonNull(key), Objects.requireNonNull(config));
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, Optional.of(Map.copyOf(newConstraints)), this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified value constraint for keys matching a regex.<br>
	 *
	 * @param regex The regex pattern to match query parameter keys
	 * @param config The string constraint config for value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withValues(@NonNull String regex, @NonNull StringConstraintConfig config) {
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
		Map<Pattern, StringConstraintConfig> newConstraints = new HashMap<>(this.patternValueConstraints.orElse(Map.of()));
		newConstraints.put(Objects.requireNonNull(pattern), Objects.requireNonNull(config));
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, Optional.of(Map.copyOf(newConstraints)), this.singleValued, this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the single-valued constraint.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withSingleValued() {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, Optional.of(null), this.multiValuedConstraints, this.custom);
	}
	
	/**
	 * Creates a new config with the specified multi-valued constraint for a key.<br>
	 *
	 * @param key The query parameter key to constrain
	 * @param config The size constraint config for multi-value validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withMultiValued(@NonNull String key, @NonNull SizeConstraintConfig config) {
		Map<String, SizeConstraintConfig> newConstraints = new HashMap<>(this.multiValuedConstraints.orElse(Map.of()));
		newConstraints.put(Objects.requireNonNull(key), Objects.requireNonNull(config));
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, Optional.of(Map.copyOf(newConstraints)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull QueryConstraintConfig withCustom(@NonNull Constraint<Map<String, List<String>>> constraint) {
		return new QueryConstraintConfig(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.valueConstraints, this.patternValueConstraints, this.singleValued, this.multiValuedConstraints, Optional.of(Objects.requireNonNull(constraint)));
	}
}
