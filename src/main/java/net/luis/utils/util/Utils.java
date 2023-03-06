package net.luis.utils.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-st
 *
 */

public class Utils {
	
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static boolean isEmpty(@NotNull UUID uuid) {
	    return uuid == EMPTY_UUID || uuid.equals(EMPTY_UUID);
	}
	
	public static <T> @NotNull T make(T object, Consumer<T> consumer) {
		consumer.accept(object);
		return Objects.requireNonNull(object);
	}
	
	public static <K, V, T> @NotNull List<T> mapToList(Map<K, V> map, BiFunction<K, V, T> function) {
		List<T> list = Lists.newArrayList();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			list.add(function.apply(entry.getKey(), entry.getValue()));
		}
		return list;
	}
	
	public static <T, K, V> @NotNull Map<K, V> listToMap(List<T> list, Function<T, Entry<K, V>> function) {
		Map<K, V> map = Maps.newHashMap();
		for (T t : list) {
			Entry<K, V> entry = function.apply(t);
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public static <T, U> @NotNull List<U> mapList(List<T> list, Function<T, U> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	public static <T, U, V> @NotNull List<V> mapList(List<T> list, Function<T, U> firstFunction, Function<U, V> secondFunction) {
		return list.stream().map(firstFunction).map(secondFunction).collect(Collectors.toList());
	}
	
	public static <K, T, V> @NotNull Map<T, V> mapKey(Map<K, V> map, Function<K, T> function) {
		Map<T, V> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(function.apply(entry.getKey()), entry.getValue());
		}
		return mapped;
	}
	
	public static <K, V, T> @NotNull Map<K, T> mapValue(Map<K, V> map, Function<V, T> function) {
		Map<K, T> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(entry.getKey(), function.apply(entry.getValue()));
		}
		return mapped;
	}
	
	public static <T> @NotNull List<T> reverseList(List<T> list) {
		List<T> reversedList = Lists.newArrayList();
		for (int i = list.size(); i-- > 0; ) {
			reversedList.add(list.get(i));
		}
		return reversedList;
	}
	
	public static <T, R> @Nullable R runIf(T value, Predicate<T> predicate, Function<T, R> function) {
		if (value != null && predicate.test(value)) {
			return function.apply(value);
		}
		return null;
	}
	
	public static <T, R> @Nullable R runIfNot(T value, Predicate<T> predicate, Function<T, R> function) {
		if (value != null && !predicate.test(value)) {
			return function.apply(value);
		}
		return null;
	}
	
	public static <T, R> @Nullable R runIfNotNull(T value, Function<T, R> function) {
		if (value != null) {
			return function.apply(value);
		}
		return null;
	}
	
	public static <T> @NotNull T warpNullTo(T value, T nullFallback) {
		if (value == null) {
			return Objects.requireNonNull(nullFallback, "The fallback value must not be null");
		}
		return value;
	}
	
	public static @NotNull Random systemRandom() {
		return new Random(System.currentTimeMillis());
	}
	
	@SafeVarargs
	public static <T> @NotNull List<T> concatLists(List<T>... lists) {
		List<T> list = Lists.newArrayList();
		for (List<T> t : lists) {
			list.addAll(t);
		}
		return list;
	}
	
	public static <T> @NotNull T getRandom(T[] values, Random rng) {
		return values[rng.nextInt(values.length)];
	}
	
	public static <T> @NotNull Optional<T> getRandomSafe(T[] values, Random rng) {
		return values.length == 0 ? Optional.empty() : Optional.of(getRandom(values, rng));
	}
	
	public static <T> @NotNull T getRandom(List<T> values, Random rng) {
		return values.get(rng.nextInt(values.size()));
	}
	
	public static <T> @NotNull Optional<T> getRandomSafe(List<T> values, Random rng) {
		return values.isEmpty() ? Optional.empty() : Optional.of(getRandom(values, rng));
	}
	
}
