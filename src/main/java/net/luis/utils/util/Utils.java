package net.luis.utils.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 *
 * @author Luis-st
 *
 */

public class Utils {
	
	public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	public static <T> T make(T object, @NotNull Consumer<T> consumer) {
		consumer.accept(object);
		return object;
	}
	
	@NotNull
	public static <K, V, T> List<T> mapToList(Map<K, V> map, BiFunction<K, V, T> function) {
		List<T> list = Lists.newArrayList();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			list.add(function.apply(entry.getKey(), entry.getValue()));
		}
		return list;
	}
	
	@NotNull
	public static <T, K, V> Map<K, V> listToMap(List<T> list, Function<T, Entry<K, V>> function) {
		Map<K, V> map = Maps.newHashMap();
		for (T t : list) {
			Entry<K, V> entry = function.apply(t);
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	@NotNull
	public static <T, U> List<U> mapList(List<T> list, Function<T, U> function) {
		return list.stream().map(function).collect(Collectors.toList());
	}
	
	@NotNull
	public static <T, U, V> List<V> mapList(List<T> list, Function<T, U> firstFunction, Function<U, V> secondFunction) {
		return list.stream().map(firstFunction).map(secondFunction).collect(Collectors.toList());
	}
	
	@NotNull
	public static <K, T, V> Map<T, V> mapKey(Map<K, V> map, Function<K, T> function) {
		Map<T, V> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(function.apply(entry.getKey()), entry.getValue());
		}
		return mapped;
	}
	
	@NotNull
	public static <K, V, T> Map<K, T> mapValue(Map<K, V> map, Function<V, T> function) {
		Map<K, T> mapped = Maps.newHashMap();
		for (Entry<K, V> entry : map.entrySet()) {
			mapped.put(entry.getKey(), function.apply(entry.getValue()));
		}
		return mapped;
	}
	
	@NotNull
	public static <T> List<T> reverseList(List<T> list) {
		List<T> reversedList = Lists.newArrayList();
		for (int i = list.size(); i-- > 0;) {
			reversedList.add(list.get(i));
		}
		return reversedList;
	}
	
	@Nullable
	public static <T, R> R runIfNotNull(T value, Function<T, R> function) {
		if (value != null) {
			return function.apply(value);
		}
		return null;
	}
	
	@NotNull
	public static <T> T warpNullTo(T value, T nullFallback) {
		if (value == null) {
			return Objects.requireNonNull(nullFallback, "The fallback value must not be null");
		}
		return value;
	}
	
	public static Random systemRandom() {
		return new Random(System.currentTimeMillis());
	}
	
	@SafeVarargs
	public static <T> List<T> concatLists(List<T>... lists) {
		List<T> list = Lists.newArrayList();
		for (List<T> t : lists) {
			list.addAll(t);
		}
		return list;
	}
	
	@Nullable
	public static <T> T getRandom(T[] values, Random rng) {
		return values[rng.nextInt(values.length)];
	}
	
	@Nullable
	public static <T> Optional<T> getRandomSafe(T[] values, Random rng) {
		return values.length == 0 ? Optional.empty() : Optional.of(getRandom(values, rng));
	}
	
	@Nullable
	public static <T> T getRandom(List<T> values, Random rng) {
		return values.get(rng.nextInt(values.size()));
	}
	
	@NotNull
	public static <T> Optional<T> getRandomSafe(List<T> values, Random rng) {
		return values.isEmpty() ? Optional.empty() : Optional.of(getRandom(values, rng));
	}
	
}
