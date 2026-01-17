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

package net.luis.utils.io.codec.constraint_new;

import net.luis.utils.io.codec.constraint_new.config.MapConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for map types that provides key-based validation operations.<br>
 * <p>
 *     This interface extends {@link SizeConstraint} with methods for constraining maps based on
 *     required, forbidden, and allowed keys.<br>
 *     It allows validation of map structure by specifying which keys must be present,
 *     which keys must not be present, and which keys are the only ones allowed.
 * </p>
 *
 * @author Luis-St
 *
 * @param <K> The type of the keys in the map
 * @param <V> The type of the values in the map
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface MapConstraint<K, V, C> extends ApplicableConstraint<MapConstraintConfig<K, V>, C>, SizeConstraint<Map<K, V>, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<MapConstraintConfig<K, V>> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Map<K, V> value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Map<K, V> value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Map<K, V>> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Map<K, V>> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Map<K, V>> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C minSize(int minSize) {
		return this.apply(config -> config.withMinSize(minSize));
	}
	
	@Override
	default @NonNull C maxSize(int maxSize) {
		return this.apply(config -> config.withMaxSize(maxSize));
	}
	
	@Override
	default @NonNull C exactSize(int exactSize) {
		return this.apply(config -> config.withExactSize(exactSize));
	}
	
	@Override
	default @NonNull C sizeBetween(int minSize, int maxSize) {
		return this.apply(config -> config.withSizeBetween(minSize, maxSize));
	}
	
	/**
	 * Applies a required key constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that maps contain the specified key.
	 * </p>
	 *
	 * @param key The key that must be present in the map
	 * @return A new type with the applied required key constraint
	 * @throws NullPointerException If the key is null
	 * @see #requiredKeys(Collection)
	 * @see #forbiddenKey(Object)
	 */
	default @NonNull C requiredKey(@NonNull K key) {
		return this.apply(config -> config.withRequiredKey(key));
	}
	
	/**
	 * Applies a multiple required keys constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that maps contain all of the specified keys.
	 * </p>
	 *
	 * @param keys The collection of keys that must all be present in the map
	 * @return A new type with the applied required keys constraint
	 * @throws NullPointerException If the collection is null
	 * @see #requiredKey(Object)
	 * @see #forbiddenKeys(Collection)
	 */
	default @NonNull C requiredKeys(@NonNull Collection<K> keys) {
		return this.apply(config -> config.withRequiredKeys(keys));
	}
	
	/**
	 * Applies a forbidden key constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that maps do not contain the specified key.
	 * </p>
	 *
	 * @param key The key that must not be present in the map
	 * @return A new type with the applied forbidden key constraint
	 * @throws NullPointerException If the key is null
	 * @see #forbiddenKeys(Collection)
	 * @see #requiredKey(Object)
	 */
	default @NonNull C forbiddenKey(@NonNull K key) {
		return this.apply(config -> config.withForbiddenKey(key));
	}
	
	/**
	 * Applies a multiple forbidden keys constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that maps do not contain any of the specified keys.
	 * </p>
	 *
	 * @param keys The collection of keys that must not be present in the map
	 * @return A new type with the applied forbidden keys constraint
	 * @throws NullPointerException If the collection is null
	 * @see #forbiddenKey(Object)
	 * @see #requiredKeys(Collection)
	 */
	default @NonNull C forbiddenKeys(@NonNull Collection<K> keys) {
		return this.apply(config -> config.withForbiddenKeys(keys));
	}
	
	/**
	 * Applies an allowed key constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that maps only contain the specified key (and no other keys).
	 * </p>
	 *
	 * @param key The only key that is allowed in the map
	 * @return A new type with the applied allowed key constraint
	 * @throws NullPointerException If the key is null
	 * @see #allowedKeys(Collection)
	 */
	default @NonNull C allowedKey(@NonNull K key) {
		return this.apply(config -> config.withAllowedKey(key));
	}
	
	/**
	 * Applies a multiple allowed keys constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that maps only contain keys from the specified collection.<br>
	 *     Any key not in the collection will cause validation to fail.
	 * </p>
	 *
	 * @param keys The collection of keys that are allowed in the map
	 * @return A new type with the applied allowed keys constraint
	 * @throws NullPointerException If the collection is null
	 * @see #allowedKey(Object)
	 */
	default @NonNull C allowedKeys(@NonNull Collection<K> keys) {
		return this.apply(config -> config.withAllowedKeys(keys));
	}
	
	/**
	 * Applies a non-null keys constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that all keys in the map are non-null.
	 * </p>
	 *
	 * @return A new type with the applied non-null keys constraint
	 * @see #uniqueValues()
	 * @see #nonNullValues()
	 */
	default @NonNull C nonNullKeys() {
		return this.apply(MapConstraintConfig::withNonNullKeys);
	}
	
	/**
	 * Applies a unique values constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that all values in the map are unique (no duplicates).
	 * </p>
	 *
	 * @return A new type with the applied unique values constraint
	 * @see #nonNullKeys()
	 * @see #nonNullValues()
	 */
	default @NonNull C uniqueValues() {
		return this.apply(MapConstraintConfig::withUniqueValues);
	}
	
	/**
	 * Applies a non-null values constraint to the map.<br>
	 * <p>
	 *     The returned type will validate that all values in the map are non-null.
	 * </p>
	 *
	 * @return A new type with the applied non-null values constraint
	 * @see #nonNullKeys()
	 * @see #uniqueValues()
	 */
	default @NonNull C nonNullValues() {
		return this.apply(MapConstraintConfig::withNonNullValues);
	}
}
