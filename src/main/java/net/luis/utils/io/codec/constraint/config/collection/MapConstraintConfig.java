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

package net.luis.utils.io.codec.constraint.config.collection;

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.merged.collection.MapConstraint;
import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for map constraints on map types.<br>
 * <p>
 *     This record stores the constraint values for {@link MapConstraint}.<br>
 *     It extends size constraints with map-specific constraints for keys and values.
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the Map and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of Maps and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 * <p>
 *     The size field stores size constraints using {@link SizeConstraintConfig}.
 * </p>
 *
 * @author Luis-St
 *
 * @param <K> The type of the keys in the map
 * @param <V> The type of the values in the map
 * @param equalTo The map equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The map set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param size The size constraint configuration
 * @param requiredKeys The set of keys that must be present in the map
 * @param forbiddenKeys The set of keys that must not be present in the map
 * @param allowedKeys The set of keys that are the only ones allowed in the map
 * @param nonNullKeys If present, requires all keys to be non-null
 * @param uniqueValues If present, requires all values to be unique
 * @param nonNullValues If present, requires all values to be non-null
 * @param custom A custom constraint implementation
 */
@SuppressWarnings("OptionalContainsCollection")
public record MapConstraintConfig<K, V>(
	@NonNull Optional<Pair<Map<K, V>, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Map<K, V>>, Boolean>> in,
	@NonNull Optional<SizeConstraintConfig> size,
	@NonNull Optional<Set<K>> requiredKeys,
	@NonNull Optional<Set<K>> forbiddenKeys,
	@NonNull Optional<Set<K>> allowedKeys,
	@NonNull Optional<Unit> nonNullKeys,
	@NonNull Optional<Unit> uniqueValues,
	@NonNull Optional<Unit> nonNullValues,
	@NonNull Optional<Constraint<Map<K, V>>> custom
) implements ConstraintConfig<Map<K, V>> {
	
	/**
	 * Constructs a new map constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The map equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The map set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param size The size constraint configuration
	 * @param requiredKeys The set of keys that must be present in the map
	 * @param forbiddenKeys The set of keys that must not be present in the map
	 * @param allowedKeys The set of keys that are the only ones allowed in the map
	 * @param nonNullKeys If present, requires all keys to be non-null
	 * @param uniqueValues If present, requires all values to be unique
	 * @param nonNullValues If present, requires all values to be non-null
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'requiredKeys' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'forbiddenKeys' constraint set is empty when present
	 * @throws IllegalArgumentException If the 'allowedKeys' constraint set is empty when present
	 * @throws IllegalArgumentException If required keys and forbidden keys overlap
	 * @throws IllegalArgumentException If required keys are not a subset of allowed keys when both are present
	 * @throws IllegalArgumentException If forbidden keys and allowed keys overlap
	 */
	public MapConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(size, "Optional for 'size' constraint must not be null");
		Objects.requireNonNull(requiredKeys, "Optional for 'required keys' constraint must not be null");
		Objects.requireNonNull(forbiddenKeys, "Optional for 'forbidden keys' constraint must not be null");
		Objects.requireNonNull(allowedKeys, "Optional for 'allowed keys' constraint must not be null");
		Objects.requireNonNull(nonNullKeys, "Optional for 'non null keys' constraint must not be null");
		Objects.requireNonNull(uniqueValues, "Optional for 'unique values' constraint must not be null");
		Objects.requireNonNull(nonNullValues, "Optional for 'non null values' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in' constraint set must not be empty when present");
		}
		
		if (requiredKeys.isPresent() && requiredKeys.get().isEmpty()) {
			throw new IllegalArgumentException("The 'required keys' constraint set must not be empty when present");
		}
		
		if (forbiddenKeys.isPresent() && forbiddenKeys.get().isEmpty()) {
			throw new IllegalArgumentException("The 'forbidden keys' constraint set must not be empty when present");
		}
		
		if (allowedKeys.isPresent() && allowedKeys.get().isEmpty()) {
			throw new IllegalArgumentException("The 'allowed keys' constraint set must not be empty when present");
		}
		
		if (requiredKeys.isPresent() && forbiddenKeys.isPresent()) {
			Set<K> overlap = new HashSet<>(requiredKeys.get());
			overlap.retainAll(forbiddenKeys.get());
			
			if (!overlap.isEmpty()) {
				throw new IllegalArgumentException("Required keys and forbidden keys must not overlap when both are present, but got overlap: " + overlap);
			}
		}
		
		if (requiredKeys.isPresent() && allowedKeys.isPresent() && !allowedKeys.get().containsAll(requiredKeys.get())) {
			throw new IllegalArgumentException("Required keys must be a subset of allowed keys when both are present");
		}
		
		if (forbiddenKeys.isPresent() && allowedKeys.isPresent()) {
			Set<K> overlap = new HashSet<>(forbiddenKeys.get());
			overlap.retainAll(allowedKeys.get());
			
			if (!overlap.isEmpty()) {
				throw new IllegalArgumentException("Forbidden keys and allowed keys must not overlap when both are present, but got overlap: " + overlap);
			}
		}
	}
	
	/**
	 * Creates an unconstrained map configuration with no constraints applied.<br>
	 *
	 * @param <K> The type of the keys
	 * @param <V> The type of the values
	 * @return An unconstrained map constraint config
	 */
	public static <K, V> @NonNull MapConstraintConfig<K, V> unconstrained() {
		return new MapConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact map that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withEqualTo(@NonNull Map<K, V> value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new MapConstraintConfig<>(Optional.of(Pair.of(Map.copyOf(value), false)), this.in, this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The map that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withNotEqualTo(@NonNull Map<K, V> value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new MapConstraintConfig<>(Optional.of(Pair.of(Map.copyOf(value), true)), this.in, this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of maps that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withIn(@NonNull Collection<Map<K, V>> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		
		Set<Map<K, V>> copies = new HashSet<>();
		for (Map<K, V> value : values) {
			copies.add(Map.copyOf(value));
		}
		
		return new MapConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(copies), false)), this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of maps that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withNotIn(@NonNull Collection<Map<K, V>> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		
		Set<Map<K, V>> copies = new HashSet<>();
		for (Map<K, V> value : values) {
			copies.add(Map.copyOf(value));
		}
		
		return new MapConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(copies), true)), this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified minimum size (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @return A new config with the minimum size constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withMinSize(int minSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withMinSize(minSize);
		return new MapConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified maximum size (inclusive).<br>
	 *
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the maximum size constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withMaxSize(int maxSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withMaxSize(maxSize);
		return new MapConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified exact size.<br>
	 *
	 * @param exactSize The exact size required
	 * @return A new config with the exact size constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withExactSize(int exactSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withExactSize(exactSize);
		return new MapConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified size range (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the size range constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withSizeBetween(int minSize, int maxSize) {
		SizeConstraintConfig newSize = this.size.orElse(SizeConstraintConfig.UNCONSTRAINED).withSizeBetween(minSize, maxSize);
		return new MapConstraintConfig<>(this.equalTo, this.in, Optional.of(newSize), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified size constraints.<br>
	 * <p>
	 *     This method applies the given {@link SizeConstraintConfig} to this config.
	 * </p>
	 *
	 * @param sizeConfig The size constraint configuration to apply
	 * @return A new config with the size constraints applied
	 * @throws NullPointerException If the size config is null
	 */
	public @NonNull MapConstraintConfig<K, V> withSize(@NonNull SizeConstraintConfig sizeConfig) {
		Objects.requireNonNull(sizeConfig, "Size config must not be null");
		return new MapConstraintConfig<>(this.equalTo, this.in, Optional.of(sizeConfig), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified required key added.<br>
	 *
	 * @param key The key that must be present in the map
	 * @return A new config with the required key constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withRequiredKey(@NonNull K key) {
		Objects.requireNonNull(key, "Key for 'required key' constraint must not be null");
		
		Set<K> keys = new HashSet<>(this.requiredKeys.orElse(Set.of()));
		keys.add(key);
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, Optional.of(Set.copyOf(keys)), this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified required keys added.<br>
	 *
	 * @param keys The keys that must be present in the map
	 * @return A new config with the required keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withRequiredKeys(@NonNull Collection<K> keys) {
		Objects.requireNonNull(keys, "Keys for 'required keys' constraint must not be null");
		
		Set<K> allKeys = new HashSet<>(this.requiredKeys.orElse(Set.of()));
		allKeys.addAll(keys);
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, Optional.of(Set.copyOf(allKeys)), this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified forbidden key added.<br>
	 *
	 * @param key The key that must not be present in the map
	 * @return A new config with the forbidden key constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withForbiddenKey(@NonNull K key) {
		Objects.requireNonNull(key, "Key for 'forbidden key' constraint must not be null");
		
		Set<K> keys = new HashSet<>(this.forbiddenKeys.orElse(Set.of()));
		keys.add(key);
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, Optional.of(Set.copyOf(keys)), this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified forbidden keys added.<br>
	 *
	 * @param keys The keys that must not be present in the map
	 * @return A new config with the forbidden keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withForbiddenKeys(@NonNull Collection<K> keys) {
		Objects.requireNonNull(keys, "Keys for 'forbidden keys' constraint must not be null");
		
		Set<K> allKeys = new HashSet<>(this.forbiddenKeys.orElse(Set.of()));
		allKeys.addAll(keys);
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, Optional.of(Set.copyOf(allKeys)), this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified allowed key added.<br>
	 *
	 * @param key The only key that is allowed in the map
	 * @return A new config with the allowed key constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withAllowedKey(@NonNull K key) {
		Objects.requireNonNull(key, "Key for 'allowed key' constraint must not be null");
		
		Set<K> keys = new HashSet<>(this.allowedKeys.orElse(Set.of()));
		keys.add(key);
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, this.forbiddenKeys, Optional.of(Set.copyOf(keys)), this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the specified allowed keys set.<br>
	 *
	 * @param keys The collection of keys that are allowed in the map
	 * @return A new config with the allowed keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withAllowedKeys(@NonNull Collection<K> keys) {
		Objects.requireNonNull(keys, "Keys for 'allowed keys' constraint must not be null");
		
		Set<K> allKeys = new HashSet<>(this.allowedKeys.orElse(Set.of()));
		allKeys.addAll(keys);
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, this.forbiddenKeys, Optional.of(Set.copyOf(allKeys)), this.nonNullKeys, this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the non-null keys constraint enabled.<br>
	 *
	 * @return A new config with the non-null keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withNonNullKeys() {
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, Optional.of(Unit.INSTANCE), this.uniqueValues, this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the unique values constraint enabled.<br>
	 *
	 * @return A new config with the unique values constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withUniqueValues() {
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, Optional.of(Unit.INSTANCE), this.nonNullValues, this.custom);
	}
	
	/**
	 * Creates a new map constraint config with the non-null values constraint enabled.<br>
	 *
	 * @return A new config with the non-null values constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withNonNullValues() {
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, Optional.of(Unit.INSTANCE), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withCustom(@NonNull Constraint<Map<K, V>> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new MapConstraintConfig<>(this.equalTo, this.in, this.size, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull Map<K, V> value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchExtractedValue(value, this.size, Map::size, "Size"),
			() -> ConstraintMatchers.matchRequiredKeys(value.keySet(), this.requiredKeys, "Map"),
			() -> ConstraintMatchers.matchForbiddenKeys(value.keySet(), this.forbiddenKeys, "Map"),
			() -> ConstraintMatchers.matchAllowedKeys(value.keySet(), this.allowedKeys, "Map"),
			() -> ConstraintMatchers.matchFlag(value, this.nonNullKeys, m -> m.keySet().stream().noneMatch(Objects::isNull), "Map keys must not contain null"),
			() -> ConstraintMatchers.matchFlag(value, this.uniqueValues, m -> m.values().stream().distinct().count() == m.size(), "Map values must be unique"),
			() -> ConstraintMatchers.matchFlag(value, this.nonNullValues, m -> m.values().stream().noneMatch(Objects::isNull), "Map values must not contain null"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
