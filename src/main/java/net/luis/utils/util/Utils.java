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
 *
 * @author Luis-St
 *
 */

public class Utils {
	
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static boolean isEmpty(@Nullable UUID uuid) {
		return uuid == EMPTY_UUID || EMPTY_UUID.equals(uuid);
	}
	
	public static <T> @NotNull T make(@NotNull T object, @NotNull Consumer<T> action) {
		Objects.requireNonNull(object, "Object must not be null");
		Objects.requireNonNull(action, "Action must not be null").accept(object);
		return object;
	}
	
	public static <K, V, T> @NotNull List<T> mapToList(@NotNull Map<K, V> map, @NotNull BiFunction<K, V, T> mapper) {
		List<T> list = Lists.newArrayList();
		for (Map.Entry<K, V> entry : Objects.requireNonNull(map, "Map must not be null").entrySet()) {
			list.add(Objects.requireNonNull(mapper, "Mapper must not be null").apply(entry.getKey(), entry.getValue()));
		}
		return list;
	}
	
	public static <T, K, V> @NotNull Map<K, V> listToMap(@NotNull List<T> list, @NotNull Function<T, Entry<K, V>> mapper) {
		Map<K, V> map = Maps.newHashMap();
		for (T t : Objects.requireNonNull(list, "List must not be null")) {
			Entry<K, V> entry = Objects.requireNonNull(mapper, "Mapper must not be null").apply(t);
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	//region List util methods
	public static <T, U> @NotNull List<U> mapList(@NotNull List<T> list, @NotNull Function<T, U> mapper) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return list.stream().map(mapper).collect(Collectors.toList());
	}
	
	public static <T, U, V> @NotNull List<V> mapList(@NotNull List<T> list, @NotNull Function<T, U> firstMapper, @NotNull Function<U, V> secondMapper) {
		Objects.requireNonNull(list, "List must not be null");
		Objects.requireNonNull(firstMapper, "First mapper must not be null");
		Objects.requireNonNull(secondMapper, "Second mapper must not be null");
		return list.stream().map(firstMapper).map(secondMapper).collect(Collectors.toList());
	}
	//endregion
	
	//region Map util methods
	public static <K, T, V> @NotNull Map<T, V> mapKey(@NotNull Map<K, V> map, @NotNull Function<K, T> mapper) {
		Map<T, V> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : Objects.requireNonNull(map, "Map must not be null").entrySet()) {
			mapped.put(Objects.requireNonNull(mapper, "Mapper must not be null").apply(entry.getKey()), entry.getValue());
		}
		return mapped;
	}
	
	public static <K, V, T> @NotNull Map<K, T> mapValue(@NotNull Map<K, V> map, @NotNull Function<V, T> mapper) {
		Map<K, T> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : Objects.requireNonNull(map, "Map must not be null").entrySet()) {
			mapped.put(entry.getKey(), Objects.requireNonNull(mapper, "Mapper must not be null").apply(entry.getValue()));
		}
		return mapped;
	}
	//endregion
	
	//region Mapper methods
	public static <T, R> R mapIf(@Nullable T value, @NotNull Predicate<T> condition, @NotNull Function<T, R> mapper) {
		Objects.requireNonNull(condition, "Condition must not be null");
		if (value != null && condition.test(value)) {
			return Objects.requireNonNull(mapper, "Mapper must not be null").apply(value);
		}
		throw new NullPointerException("Value must not be null");
	}
	
	public static <T, R> R mapIfNot(@Nullable T value, @NotNull Predicate<T> condition, @NotNull Function<T, R> mapper) {
		Objects.requireNonNull(condition, "Condition must not be null");
		if (value != null && !condition.test(value)) {
			return Objects.requireNonNull(mapper, "Mapper must not be null").apply(value);
		}
		throw new NullPointerException("Value must not be null");
	}
	
	@Deprecated // Use Optional#map instead
	public static <T, R> R mapIfNotNull(@Nullable T value, @NotNull Function<T, R> mapper) {
		if (value != null) {
			return Objects.requireNonNull(mapper, "Mapper must not be null").apply(value);
		}
		return null;
	}
	//endregion
	
	//region Duplicate checks
	public static boolean hasDuplicates(@NotNull Object[] values) {
		Objects.requireNonNull(values, "Values must not be null");
		return values.length != Arrays.stream(values).distinct().count();
	}
	
	public static boolean hasDuplicates(@NotNull List<?> values) {
		Objects.requireNonNull(values, "Values must not be null");
		return values.size() != values.stream().distinct().count();
	}
	
	public static boolean hasDuplicates(@NotNull Object object, @NotNull Object[] values) {
		Objects.requireNonNull(object, "Object must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		if (!ArrayUtils.contains(values, object)) {
			return false;
		}
		return values.length != Sets.newHashSet(values).size();
	}
	
	public static boolean hasDuplicates(@NotNull Object object, @NotNull List<?> values) {
		Objects.requireNonNull(object, "Object must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		if (!values.contains(object)) {
			return false;
		}
		return values.size() != Sets.newHashSet(values).size();
	}
	//endregion
	
	//region Null helper methods
	public static <T> @NotNull T warpNullTo(@Nullable T value, @NotNull T nullFallback) {
		if (value == null) {
			return Objects.requireNonNull(nullFallback, "Fallback value must not be null");
		}
		return value;
	}
	
	public static <T> @NotNull T warpNullTo(@Nullable T value, @NotNull Supplier<T> nullFallback) {
		if (value == null) {
			return Objects.requireNonNull(nullFallback, "Fallback supplier must not be null").get();
		}
		return value;
	}
	
	public static <T> void executeIfNotNull(@Nullable T value, @NotNull Consumer<T> action) {
		if (value != null) {
			Objects.requireNonNull(action, "Action must not be null").accept(value);
		}
	}
	//endregion
	
	//region Random util methods
	public static @NotNull Random systemRandom() {
		return new Random(System.currentTimeMillis());
	}
	
	public static <T> @NotNull T getRandom(T @NotNull [] values, @NotNull Random rng) {
		Objects.requireNonNull(values, "Values must not be null");
		Objects.requireNonNull(rng, "Random must not be null");
		return values[rng.nextInt(values.length)];
	}
	
	public static <T> @NotNull Optional<T> getRandomSafe(T @NotNull [] values, @NotNull Random rng) {
		Objects.requireNonNull(values, "Values must not be null");
		Objects.requireNonNull(rng, "Random must not be null");
		return values.length == 0 ? Optional.empty() : Optional.of(getRandom(values, rng));
	}
	
	public static <T> @NotNull T getRandom(@NotNull List<T> values, @NotNull Random rng) {
		Objects.requireNonNull(values, "Values must not be null");
		Objects.requireNonNull(rng, "Random must not be null");
		return values.get(rng.nextInt(values.size()));
	}
	
	public static <T> @NotNull Optional<T> getRandomSafe(@NotNull List<T> values, @NotNull Random rng) {
		Objects.requireNonNull(values, "Values must not be null");
		Objects.requireNonNull(rng, "Random must not be null");
		return values.isEmpty() ? Optional.empty() : Optional.of(getRandom(values, rng));
	}
	//endregion
}
