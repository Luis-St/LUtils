/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.util;

import com.google.common.collect.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Utility class for various helper methods.<br>
 *
 * @author Luis-St
 */
public final class Utils {
	
	/**
	 * Empty UUID constant.<br>
	 */
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private Utils() {}
	
	/**
	 * Checks if the given UUID is empty.<br>
	 * An empty UUID is a UUID with all zeros.<br>
	 * @param uuid The UUID to check
	 * @return True, if the UUID is empty, otherwise false
	 */
	public static boolean isEmpty(@Nullable UUID uuid) {
		return uuid == EMPTY_UUID || EMPTY_UUID.equals(uuid);
	}
	
	/**
	 * Applies the given action to the given object and returns the object.<br>
	 * This is useful for creating objects and applying actions to them in one line.<br>
	 * @param object The object to apply the action to
	 * @param action The action to apply to the object
	 * @return The object after the action has been applied
	 * @throws NullPointerException If the object or action is null
	 * @param <T> The type of the object
	 */
	public static <T> @NotNull T make(@NotNull T object, @NotNull Consumer<T> action) {
		Objects.requireNonNull(object, "Object must not be null");
		Objects.requireNonNull(action, "Action must not be null").accept(object);
		return object;
	}
	
	/**
	 * Maps the given map to a list using the given mapper function.<br>
	 * The mapper takes the key and value of the map and returns the resulting list value.<br>
	 * <p>
	 *     If the map or the function is null, an empty list is returned.<br>
	 *     In the case the map is empty, the resulting list is also empty.<br>
	 * </p>
	 * @param map The map to convert to a list
	 * @param function The function to use for mapping
	 * @return The list containing the mapped entries
	 * @param <K> The type of the map key
	 * @param <V> The type of the map value
	 * @param <T> The type of the list
	 */
	public static <K, V, T> @NotNull List<T> mapToList(@Nullable Map<K, V> map, @Nullable BiFunction<K, V, T> function) {
		List<T> list = Lists.newArrayList();
		if (map == null || map.isEmpty() || function == null) {
			return list;
		}
		for (Map.Entry<K, V> entry : map.entrySet()) {
			list.add(function.apply(entry.getKey(), entry.getValue()));
		}
		return list;
	}
	
	/**
	 * Maps the given list to a map using the given mapper function.<br>
	 * The mapper takes the list value and returns the resulting map entry.<br>
	 * <p>
	 *     If the list or the function is null, an empty map is returned.<br>
	 *     In the case the list is empty, the resulting map is also empty.<br>
	 * </p>
	 * @param list The list to convert
	 * @param function The function to use for mapping
	 * @return The map containing the mapped elements
	 * @param <T> The type of the list
	 * @param <K> The type of the map key
	 * @param <V> The type of the map value
	 */
	public static <T, K, V> @NotNull Map<K, V> listToMap(@Nullable List<T> list, @Nullable Function<T, Entry<K, V>> function) {
		Map<K, V> map = Maps.newHashMap();
		if (list == null || list.isEmpty() || function == null) {
			return map;
		}
		for (T t : list) {
			Entry<K, V> entry = function.apply(t);
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	//region List util methods
	
	/**
	 * Maps the given list to a new list using the given mapper function.<br>
	 * <p>
	 *     If the list or the function is null, an empty list is returned.<br>
	 *     In the case the list is empty, the resulting list is also empty.<br>
	 * </p>
	 * <p>
	 *     Shorthand for:
	 * </p>
	 * <pre>{@code
	 *  list.stream().map(function).collect(Collectors.toList());
	 * }</pre>
	 * @param list The list to map
	 * @param function The function to use for mapping
	 * @return The list after mapping or an empty list
	 * @param <T> The type of the list
	 * @param <U> The type of the resulting list
	 */
	public static <T, U> @NotNull List<U> mapList(@Nullable List<T> list, @Nullable Function<T, U> function) {
		if (list == null || list.isEmpty()) {
			return Lists.newArrayList();
		}
		if (function == null) {
			return Lists.newArrayList();
		}
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	/**
	 * Maps the given list to a new list using the given two mapper functions.<br>
	 * <p>
	 *     If the list or the functions are null, an empty list is returned.<br>
	 *     In the case the list is empty, the resulting list is also empty.<br>
	 * </p>
	 * <p>
	 *     Shorthand for:
	 * </p>
	 * <pre>{@code
	 * list.stream().map(first).map(second).collect(Collectors.toList());
	 * }</pre>
	 * @param list The list to map
	 * @param first The first function to use for mapping
	 * @param second The second function to use for mapping
	 * @return The list after mapping or an empty list
	 * @param <T> The type of the list
	 * @param <U> The type of the list after the first mapper
	 * @param <V> The type of the resulting list
	 */
	public static <T, U, V> @NotNull List<V> mapList(@Nullable List<T> list, @Nullable Function<T, U> first, @Nullable Function<U, V> second) {
		if (list == null || list.isEmpty()) {
			return Lists.newArrayList();
		}
		if (first == null || second == null) {
			return Lists.newArrayList();
		}
		return list.stream().map(first).map(second).collect(Collectors.toList());
	}
	//endregion
	
	//region Map util methods
	
	/**
	 * Maps the keys of the given map using the given mapper function.<br>
	 * <p>
	 *     If the map or the function is null, an empty map is returned.<br>
	 *     In the case the map is empty, the resulting map is also empty.<br>
	 * </p>
	 * <p>
	 *     If the function returns null for a key, the key is not added to the resulting map.<br>
	 * </p>
	 * @param map The map to map the keys
	 * @param function The function to use for mapping
	 * @return A new map with the mapped keys
	 * @param <K> The type of the map key
	 * @param <T> The type of the key in the resulting map
	 * @param <V> The type of the map value
	 */
	public static <K, T, V> @NotNull Map<T, V> mapKey(@Nullable Map<K, V> map, @Nullable Function<K, T> function) {
		Map<T, V> mapped = Maps.newHashMap();
		if (map == null || map.isEmpty() || function == null) {
			return mapped;
		}
		for (Entry<K, V> entry : map.entrySet()) {
			T key = function.apply(entry.getKey());
			if (key != null) {
				mapped.put(key, entry.getValue());
			}
		}
		return mapped;
	}
	
	/**
	 * Maps the values of the given map using the given mapper function.<br>
	 * <p>
	 *     If the map or the function is null, an empty map is returned.<br>
	 *     In the case the map is empty, the resulting map is also empty.<br>
	 * </p>
	 * <p>
	 *     The function must be able to handle null values.<br>
	 * </p>
	 * @param map The map to map the values
	 * @param function The function to use for mapping
	 * @return A new map with the mapped values
	 * @param <K> The type of the map key
	 * @param <V> The type of the map value
	 * @param <T> The type of the value in the resulting map
	 */
	public static <K, V, T> @NotNull Map<K, T> mapValue(@Nullable Map<K, V> map, @Nullable Function<V, T> function) {
		Map<K, T> mapped = Maps.newHashMap();
		if (map == null || map.isEmpty() || function == null) {
			return mapped;
		}
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(entry.getKey(), function.apply(entry.getValue()));
		}
		return mapped;
	}
	//endregion
	
	//region Duplicate checks
	
	/**
	 * Checks if the given array contains duplicate values.<br>
	 * If the array is null or empty, false is returned.<br>
	 * @param array The array to check
	 * @return True, if the array contains duplicate values, otherwise false
	 */
	public static boolean hasDuplicates(Object @Nullable [] array) {
		if (array == null || array.length == 0) {
			return false;
		}
		return array.length != Arrays.stream(array).distinct().count();
	}
	
	/**
	 * Checks if the given list contains duplicate values.<br>
	 * If the list is null or empty, false is returned.<br>
	 * @param list The list to check
	 * @return True, if the list contains duplicate values, otherwise false
	 */
	public static boolean hasDuplicates(@Nullable List<?> list) {
		if (list == null || list.isEmpty()) {
			return false;
		}
		return list.size() != list.stream().distinct().count();
	}
	
	/**
	 * Checks if the given object is contained multiple times in the given array.<br>
	 * @param object The object to check
	 * @param array The array to check in
	 * @return True, if the object is contained multiple times in the array, otherwise false
	 */
	public static boolean hasDuplicates(@Nullable Object object, Object @Nullable [] array) {
		if (array == null || !ArrayUtils.contains(array, object)) {
			return false;
		}
		return array.length != Sets.newHashSet(array).size();
	}
	
	/**
	 * Checks if the given object is contained multiple times in the given list.<br>
	 * @param object The object to check
	 * @param list The list to check in
	 * @return True, if the object is contained multiple times in the list, otherwise false
	 */
	public static boolean hasDuplicates(@Nullable Object object, @Nullable List<?> list) {
		if (list == null || !list.contains(object)) {
			return false;
		}
		return list.size() != Sets.newHashSet(list).size();
	}
	//endregion
	
	//region Null helper methods
	
	/**
	 * Wraps the given value to the given fallback value if the value is null.<br>
	 * If the value is not null, the value is returned.<br>
	 * @param value The value to wrap
	 * @param nullFallback The fallback value to use
	 * @return The value or the fallback value if the value is null
	 * @throws NullPointerException If the fallback value is null
	 * @param <T> The type of the value
	 */
	public static <T> @NotNull T warpNullTo(@Nullable T value, @NotNull T nullFallback) {
		Objects.requireNonNull(nullFallback, "Fallback value must not be null");
		if (value == null) {
			return nullFallback;
		}
		return value;
	}
	
	/**
	 * Wraps the given value to the value returned by the given fallback supplier if the value is null.<br>
	 * If the value is not null, the value is returned.<br>
	 * @param value The value to wrap
	 * @param nullFallback The fallback supplier to use
	 * @return The value or the value returned by the fallback supplier if the value is null
	 * @throws NullPointerException If the fallback supplier is null
	 * @param <T> The type of the value
	 */
	public static <T> @NotNull T warpNullTo(@Nullable T value, @NotNull Supplier<T> nullFallback) {
		Objects.requireNonNull(nullFallback, "Fallback supplier must not be null");
		if (value == null) {
			return nullFallback.get();
		}
		return value;
	}
	
	/**
	 * Executes the given action if the given value is not null.<br>
	 * @param value The value to check
	 * @param action The action to execute
	 * @throws NullPointerException If the action is null
	 * @param <T> The type of the value
	 */
	public static <T> void executeIfNotNull(@Nullable T value, @NotNull Consumer<T> action) {
		Objects.requireNonNull(action, "Action must not be null");
		if (value != null) {
			action.accept(value);
		}
	}
	//endregion
	
	//region Random util methods
	
	/**
	 * Returns a new {@link Random} instance using the current system time as seed.<br>
	 * @return A new random number generator
	 */
	public static @NotNull Random systemRandom() {
		return new Random(System.currentTimeMillis());
	}
	
	/**
	 * Returns a random value from the given array using the given random number generator.<br>
	 * @param rng The random number generator to use
	 * @param values The array to get the random value from
	 * @return A random value from the array
	 * @throws NullPointerException If the random number generator or array is null
	 * @throws IllegalArgumentException If the array is empty
	 * @param <T> The type of the array
	 */
	@SafeVarargs
	public static <T> @NotNull T getRandom(@NotNull Random rng, T @NotNull ... values) {
		Objects.requireNonNull(rng, "Random must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		return values[rng.nextInt(values.length)];
	}
	
	/**
	 * Returns a random value from the given array using the given random number generator.<br>
	 * If the array is empty or null, an empty optional is returned.<br>
	 * @param rng The random number generator to use
	 * @param values The array to get the random value from
	 * @return A random value from the array
	 * @throws NullPointerException If the random number generator is null
	 * @see #getRandom(Random, Object[])
	 * @param <T> The type of the array
	 */
	@SafeVarargs
	public static <T> @NotNull Optional<T> getRandomSafe(@NotNull Random rng, T @Nullable ... values) {
		Objects.requireNonNull(rng, "Random must not be null");
		if (values == null || values.length == 0) {
			return Optional.empty();
		}
		return Optional.of(getRandom(rng, values));
	}
	
	/**
	 * Returns a random value from the given list using the given random number generator.<br>
	 * @param rng The random number generator to use
	 * @param values The list to get the random value from
	 * @return A random value from the list
	 * @throws NullPointerException If the random number generator or list is null
	 * @param <T> The type of the list
	 */
	public static <T> @NotNull T getRandom(@NotNull Random rng, @NotNull List<T> values) {
		Objects.requireNonNull(rng, "Random must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		return values.get(rng.nextInt(values.size()));
	}
	
	/**
	 * Returns a random value from the given list using the given random number generator.<br>
	 * If the list is empty or null, an empty optional is returned.<br>
	 * @param rng The random number generator to use
	 * @param values The list to get the random value from
	 * @return A random value from the list
	 * @throws NullPointerException If the random number generator is null
	 * @see #getRandom(Random, List)
	 * @param <T> The type of the list
	 */
	public static <T> @NotNull Optional<T> getRandomSafe(@NotNull Random rng, @Nullable List<T> values) {
		Objects.requireNonNull(rng, "Random must not be null");
		if (values == null || values.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(getRandom(rng, values));
	}
	//endregion
	
	/**
	 * Throws the given exception as a sneaky exception.<br>
	 * @param e The exception to throw
	 * @param <E> The type of the exception
	 * @throws E The given exception as a sneaky exception
	 * @throws NullPointerException If the exception is null
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Throwable> void throwSneaky(@NotNull Throwable e) throws E {
		Objects.requireNonNull(e, "Exception must not be null");
		throw (E) e;
	}
}
