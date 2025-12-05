/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.constraint;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * A constraint interface for size-based validation of collections and maps.<br>
 * Provides methods to validate the number of elements in a collection or entries in a map.<br>
 *
 * @author Luis-St
 *
 * @param <T> The collection or map type being constrained
 */
public interface SizeConstraint<T> {
	
	/**
	 * Gets the size (number of elements) of the given value.<br>
	 * Implementations should handle various types appropriately.<br>
	 *
	 * @param value The value to get the size of
	 * @return The size of the value, or -1 if the value is null or unsupported
	 */
	int getSize(@Nullable T value);
	
	/**
	 * Checks if the value has at least the specified minimum size.<br>
	 *
	 * @param value The value to check
	 * @param minSize The minimum allowed size (inclusive)
	 * @return {@code true} if the value has at least the minimum size, {@code false} otherwise
	 */
	default boolean hasMinSize(@Nullable T value, int minSize) {
		int size = getSize(value);
		return size >= 0 && size >= minSize;
	}
	
	/**
	 * Checks if the value has at most the specified maximum size.<br>
	 *
	 * @param value The value to check
	 * @param maxSize The maximum allowed size (inclusive)
	 * @return {@code true} if the value has at most the maximum size, {@code false} otherwise
	 */
	default boolean hasMaxSize(@Nullable T value, int maxSize) {
		int size = getSize(value);
		return size >= 0 && size <= maxSize;
	}
	
	/**
	 * Checks if the value has exactly the specified size.<br>
	 *
	 * @param value The value to check
	 * @param exactSize The exact required size
	 * @return {@code true} if the value has exactly the specified size, {@code false} otherwise
	 */
	default boolean hasExactSize(@Nullable T value, int exactSize) {
		int size = getSize(value);
		return size == exactSize;
	}
	
	/**
	 * Checks if the value's size is within the specified range (inclusive).<br>
	 *
	 * @param value The value to check
	 * @param minSize The minimum allowed size (inclusive)
	 * @param maxSize The maximum allowed size (inclusive)
	 * @return {@code true} if the value's size is within the range, {@code false} otherwise
	 */
	default boolean hasSizeInRange(@Nullable T value, int minSize, int maxSize) {
		int size = getSize(value);
		return size >= 0 && size >= minSize && size <= maxSize;
	}
	
	/**
	 * Checks if the value is empty (has zero elements).<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value is empty, {@code false} otherwise
	 */
	default boolean isEmpty(@Nullable T value) {
		return getSize(value) == 0;
	}
	
	/**
	 * Checks if the value is not empty (has at least one element).<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value is not empty, {@code false} otherwise
	 */
	default boolean isNotEmpty(@Nullable T value) {
		int size = getSize(value);
		return size > 0;
	}
	
	/**
	 * Default implementation for collections.<br>
	 *
	 * @param <C> The element type of the collection
	 */
	interface ForCollection<C> extends SizeConstraint<Collection<C>> {
		
		@Override
		default int getSize(@Nullable Collection<C> value) {
			return value == null ? -1 : value.size();
		}
	}
	
	/**
	 * Default implementation for maps.<br>
	 *
	 * @param <K> The key type of the map
	 * @param <V> The value type of the map
	 */
	interface ForMap<K, V> extends SizeConstraint<Map<K, V>> {
		
		@Override
		default int getSize(@Nullable Map<K, V> value) {
			return value == null ? -1 : value.size();
		}
	}
	
}
