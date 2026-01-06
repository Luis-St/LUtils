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

import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration record for map constraints on map types.<br>
 * <p>
 *     This record stores the constraint values for {@link net.luis.utils.io.codec.constraint_new.MapConstraint}.<br>
 *     It extends size constraints with map-specific constraints for keys and values.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param <K> The type of the keys in the map
 * @param <V> The type of the values in the map
 * @param min The minimum size constraint as a pair of (value, inclusive)
 * @param max The maximum size constraint as a pair of (value, inclusive)
 * @param requiredKeys The set of keys that must be present in the map
 * @param forbiddenKeys The set of keys that must not be present in the map
 * @param allowedKeys The set of keys that are the only ones allowed in the map
 * @param nonNullKeys If present, requires all keys to be non-null
 * @param uniqueValues If present, requires all values to be unique
 * @param nonNullValues If present, requires all values to be non-null
 */
public record MapConstraintConfig<K, V>(
	@NonNull Optional<Pair<Integer, Boolean>> min,
	@NonNull Optional<Pair<Integer, Boolean>> max,
	@NonNull Optional<Set<K>> requiredKeys,
	@NonNull Optional<Set<K>> forbiddenKeys,
	@NonNull Optional<Set<K>> allowedKeys,
	@NonNull Optional<Void> nonNullKeys,
	@NonNull Optional<Void> uniqueValues,
	@NonNull Optional<Void> nonNullValues
) {

	/**
	 * Creates an unconstrained map configuration with no constraints applied.<br>
	 *
	 * @param <K> The type of the keys
	 * @param <V> The type of the values
	 * @return An unconstrained map constraint config
	 */
	public static <K, V> @NonNull MapConstraintConfig<K, V> unconstrained() {
		return new MapConstraintConfig<>(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
		);
	}

	/**
	 * Creates a new map constraint config with the specified minimum size (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @return A new config with the minimum size constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withMinSize(int minSize) {
		return new MapConstraintConfig<>(Optional.of(Pair.of(minSize, true)), this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified maximum size (inclusive).<br>
	 *
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the maximum size constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withMaxSize(int maxSize) {
		return new MapConstraintConfig<>(this.min, Optional.of(Pair.of(maxSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified exact size.<br>
	 *
	 * @param exactSize The exact size required
	 * @return A new config with the exact size constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withExactSize(int exactSize) {
		return new MapConstraintConfig<>(Optional.of(Pair.of(exactSize, true)), Optional.of(Pair.of(exactSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified size range (inclusive).<br>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @param maxSize The maximum size (inclusive)
	 * @return A new config with the size range constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withSizeBetween(int minSize, int maxSize) {
		return new MapConstraintConfig<>(Optional.of(Pair.of(minSize, true)), Optional.of(Pair.of(maxSize, true)), this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified required key added.<br>
	 *
	 * @param key The key that must be present in the map
	 * @return A new config with the required key constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withRequiredKey(@NonNull K key) {
		Set<K> keys = new HashSet<>(this.requiredKeys.orElse(Set.of()));
		keys.add(Objects.requireNonNull(key, "Key must not be null"));
		return new MapConstraintConfig<>(this.min, this.max, Optional.of(Set.copyOf(keys)), this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified required keys added.<br>
	 *
	 * @param keys The keys that must be present in the map
	 * @return A new config with the required keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withRequiredKeys(@NonNull Collection<K> keys) {
		Set<K> allKeys = new HashSet<>(this.requiredKeys.orElse(Set.of()));
		allKeys.addAll(Objects.requireNonNull(keys, "Keys must not be null"));
		return new MapConstraintConfig<>(this.min, this.max, Optional.of(Set.copyOf(allKeys)), this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified forbidden key added.<br>
	 *
	 * @param key The key that must not be present in the map
	 * @return A new config with the forbidden key constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withForbiddenKey(@NonNull K key) {
		Set<K> keys = new HashSet<>(this.forbiddenKeys.orElse(Set.of()));
		keys.add(Objects.requireNonNull(key, "Key must not be null"));
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, Optional.of(Set.copyOf(keys)), this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified forbidden keys added.<br>
	 *
	 * @param keys The keys that must not be present in the map
	 * @return A new config with the forbidden keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withForbiddenKeys(@NonNull Collection<K> keys) {
		Set<K> allKeys = new HashSet<>(this.forbiddenKeys.orElse(Set.of()));
		allKeys.addAll(Objects.requireNonNull(keys, "Keys must not be null"));
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, Optional.of(Set.copyOf(allKeys)), this.allowedKeys, this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified allowed key added.<br>
	 *
	 * @param key The only key that is allowed in the map
	 * @return A new config with the allowed key constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withAllowedKey(@NonNull K key) {
		Set<K> keys = new HashSet<>(this.allowedKeys.orElse(Set.of()));
		keys.add(Objects.requireNonNull(key, "Key must not be null"));
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, this.forbiddenKeys, Optional.of(Set.copyOf(keys)), this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the specified allowed keys set.<br>
	 *
	 * @param keys The collection of keys that are allowed in the map
	 * @return A new config with the allowed keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withAllowedKeys(@NonNull Collection<K> keys) {
		Set<K> allKeys = new HashSet<>(this.allowedKeys.orElse(Set.of()));
		allKeys.addAll(Objects.requireNonNull(keys, "Keys must not be null"));
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, this.forbiddenKeys, Optional.of(Set.copyOf(allKeys)), this.nonNullKeys, this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the non-null keys constraint enabled.<br>
	 *
	 * @return A new config with the non-null keys constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withNonNullKeys() {
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, Optional.of(null), this.uniqueValues, this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the unique values constraint enabled.<br>
	 *
	 * @return A new config with the unique values constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withUniqueValues() {
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, Optional.of(null), this.nonNullValues);
	}

	/**
	 * Creates a new map constraint config with the non-null values constraint enabled.<br>
	 *
	 * @return A new config with the non-null values constraint applied
	 */
	public @NonNull MapConstraintConfig<K, V> withNonNullValues() {
		return new MapConstraintConfig<>(this.min, this.max, this.requiredKeys, this.forbiddenKeys, this.allowedKeys, this.nonNullKeys, this.uniqueValues, Optional.of(null));
	}
}
