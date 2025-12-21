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

package net.luis.utils.function;

import net.luis.utils.function.throwable.*;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.*;

/**
 * Utility class for the functional interfaces in the {@link java.util.function} and {@link net.luis.utils.function.throwable} packages.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("DuplicatedCode")
public final class FunctionUtils {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private FunctionUtils() {}
	
	/**
	 * Memorizes the given {@link Supplier} and returns a new {@link Supplier} that caches the result of the first call.<br>
	 *
	 * @param delegate The delegate supplier to memorize
	 * @return A new supplier that caches the result of the first call
	 * @param <T> The type of the result
	 * @throws NullPointerException If the delegate supplier is null
	 */
	public static <T> @NonNull Supplier<T> memorize(@NonNull Supplier<T> delegate) {
		Objects.requireNonNull(delegate, "Delegate supplier must not be null");
		return new Supplier<>() {
			private boolean initialized;
			private T value;
			
			@Override
			public @NonNull T get() {
				if (!this.initialized) {
					this.value = delegate.get();
					this.initialized = true;
				}
				return this.value;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link ThrowableSupplier} and returns a new {@link ThrowableSupplier} that caches the result of the first call.<br>
	 *
	 * @param delegate The delegate supplier to memorize
	 * @param <T> The type of the result
	 * @param <X> The type of the exception
	 * @return A new supplier that caches the result of the first call
	 * @throws NullPointerException If the delegate supplier is null
	 */
	public static <T, X extends Throwable> @NonNull ThrowableSupplier<T, X> memorizeThrowable(@NonNull ThrowableSupplier<T, X> delegate) {
		Objects.requireNonNull(delegate, "Delegate supplier must not be null");
		return new ThrowableSupplier<>() {
			private boolean initialized;
			private T value;
			
			@Override
			public @NonNull T get() throws X {
				if (!this.initialized) {
					this.value = delegate.get();
					this.initialized = true;
				}
				return this.value;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link Function} and returns a new {@link Function} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param delegate The delegate function to memorize
	 * @param <T> The type of the input value
	 * @param <R> The type of the result
	 * @return A new function that caches the result for each input
	 * @throws NullPointerException If the delegate function is null
	 */
	public static <T, R> @NonNull Function<T, R> memorize(@NonNull Function<T, R> delegate) {
		Objects.requireNonNull(delegate, "Delegate function must not be null");
		return new Function<>() {
			private final Map<T, R> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T value) {
				return this.cache.computeIfAbsent(value, delegate);
			}
		};
	}
	
	/**
	 * Memorizes the given {@link ThrowableFunction} and returns a new {@link ThrowableFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param delegate The delegate function to memorize
	 * @param <T> The type of the input value
	 * @param <R> The type of the result
	 * @param <X> The type of the exception
	 * @return A new function that caches the result for each input
	 */
	public static <T, R, X extends Throwable> @NonNull ThrowableFunction<T, R, X> memorizeThrowable(@NonNull ThrowableFunction<T, R, X> delegate) {
		Objects.requireNonNull(delegate, "Delegate function must not be null");
		return new ThrowableFunction<>() {
			private final Map<T, R> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T value) throws X {
				if (this.cache.containsKey(value)) {
					return this.cache.get(value);
				}
				R result = delegate.apply(value);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.put(value, result);
				return result;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link BiFunction} and returns a new {@link BiFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param function The delegate function to memorize
	 * @param <T> The type of the first input value
	 * @param <U> The type of the second input value
	 * @param <R> The type of the result
	 * @return A new function that caches the result for each input
	 */
	public static <T, U, R> @NonNull BiFunction<T, U, R> memorize(@NonNull BiFunction<T, U, R> function) {
		Objects.requireNonNull(function, "Delegate function must not be null");
		return new BiFunction<>() {
			private final Map<T, Map<U, R>> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T first, @NonNull U second) {
				if (this.cache.containsKey(first)) {
					Map<U, R> secondMap = this.cache.get(first);
					if (secondMap.containsKey(second)) {
						return secondMap.get(second);
					}
				} else {
					this.cache.put(first, new WeakHashMap<>());
				}
				R result = function.apply(first, second);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.get(first).put(second, result);
				return result;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link ThrowableBiFunction} and returns a new {@link ThrowableBiFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param function The delegate function to memorize
	 * @param <T> The type of the first input value
	 * @param <U> The type of the second input value
	 * @param <R> The type of the result
	 * @param <X> The type of the exception
	 * @return A new function that caches the result for each input
	 */
	public static <T, U, R, X extends Throwable> @NonNull ThrowableBiFunction<T, U, R, X> memorizeThrowable(@NonNull ThrowableBiFunction<T, U, R, X> function) {
		Objects.requireNonNull(function, "Delegate function must not be null");
		return new ThrowableBiFunction<>() {
			private final Map<T, Map<U, R>> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T first, @NonNull U second) throws X {
				if (this.cache.containsKey(first)) {
					Map<U, R> secondMap = this.cache.get(first);
					if (secondMap.containsKey(second)) {
						return secondMap.get(second);
					}
				} else {
					this.cache.put(first, new WeakHashMap<>());
				}
				R result = function.apply(first, second);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.get(first).put(second, result);
				return result;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link TriFunction} and returns a new {@link TriFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param function The delegate function to memorize
	 * @param <T> The type of the first input value
	 * @param <U> The type of the second input value
	 * @param <V> The type of the third input value
	 * @param <R> The type of the result
	 * @return A new function that caches the result for each input
	 */
	public static <T, U, V, R> @NonNull TriFunction<T, U, V, R> memorize(@NonNull TriFunction<T, U, V, R> function) {
		Objects.requireNonNull(function, "Delegate function must not be null");
		return new TriFunction<>() {
			private final Map<T, Map<U, Map<V, R>>> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T first, @NonNull U second, @NonNull V third) {
				if (this.cache.containsKey(first)) {
					Map<U, Map<V, R>> secondMap = this.cache.get(first);
					if (secondMap.containsKey(second)) {
						Map<V, R> thirdMap = secondMap.get(second);
						if (thirdMap.containsKey(third)) {
							return thirdMap.get(third);
						}
					} else {
						secondMap.put(second, new WeakHashMap<>());
					}
				} else {
					Map<U, Map<V, R>> secondMap = new WeakHashMap<>();
					secondMap.put(second, new WeakHashMap<>());
					this.cache.put(first, secondMap);
				}
				R result = function.apply(first, second, third);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.get(first).get(second).put(third, result);
				return result;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link ThrowableTriFunction} and returns a new {@link ThrowableTriFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param function The delegate function to memorize
	 * @param <T> The type of the first input value
	 * @param <U> The type of the second input value
	 * @param <V> The type of the third input value
	 * @param <R> The type of the result
	 * @param <X> The type of the exception
	 * @return A new function that caches the result for each input
	 */
	public static <T, U, V, R, X extends Throwable> @NonNull ThrowableTriFunction<T, U, V, R, X> memorizeThrowable(@NonNull ThrowableTriFunction<T, U, V, R, X> function) {
		Objects.requireNonNull(function, "Delegate function must not be null");
		return new ThrowableTriFunction<>() {
			private final Map<T, Map<U, Map<V, R>>> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T first, @NonNull U second, @NonNull V third) throws X {
				if (this.cache.containsKey(first)) {
					Map<U, Map<V, R>> secondMap = this.cache.get(first);
					if (secondMap.containsKey(second)) {
						Map<V, R> thirdMap = secondMap.get(second);
						if (thirdMap.containsKey(third)) {
							return thirdMap.get(third);
						}
					} else {
						secondMap.put(second, new WeakHashMap<>());
					}
				} else {
					Map<U, Map<V, R>> secondMap = new WeakHashMap<>();
					secondMap.put(second, new WeakHashMap<>());
					this.cache.put(first, secondMap);
				}
				R result = function.apply(first, second, third);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.get(first).get(second).put(third, result);
				return result;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link QuadFunction} and returns a new {@link QuadFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param function The delegate function to memorize
	 * @param <T> The type of the first input value
	 * @param <U> The type of the second input value
	 * @param <V> The type of the third input value
	 * @param <W> The type of the fourth input value
	 * @param <R> The type of the result
	 * @return A new function that caches the result for each input
	 */
	public static <T, U, V, W, R> @NonNull QuadFunction<T, U, V, W, R> memorize(@NonNull QuadFunction<T, U, V, W, R> function) {
		Objects.requireNonNull(function, "Delegate function must not be null");
		return new QuadFunction<>() {
			private final Map<T, Map<U, Map<V, Map<W, R>>>> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T first, @NonNull U second, @NonNull V third, @NonNull W fourth) {
				if (this.cache.containsKey(first)) {
					Map<U, Map<V, Map<W, R>>> secondMap = this.cache.get(first);
					if (secondMap.containsKey(second)) {
						Map<V, Map<W, R>> thirdMap = secondMap.get(second);
						if (thirdMap.containsKey(third)) {
							Map<W, R> fourthMap = thirdMap.get(third);
							if (fourthMap.containsKey(fourth)) {
								return fourthMap.get(fourth);
							}
						} else {
							thirdMap.put(third, new WeakHashMap<>());
						}
					} else {
						Map<V, Map<W, R>> thirdMap = new WeakHashMap<>();
						thirdMap.put(third, new WeakHashMap<>());
						secondMap.put(second, thirdMap);
					}
				} else {
					Map<U, Map<V, Map<W, R>>> secondMap = new WeakHashMap<>();
					Map<V, Map<W, R>> thirdMap = new WeakHashMap<>();
					thirdMap.put(third, new WeakHashMap<>());
					secondMap.put(second, thirdMap);
					this.cache.put(first, secondMap);
				}
				R result = function.apply(first, second, third, fourth);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.get(first).get(second).get(third).put(fourth, result);
				return result;
			}
		};
	}
	
	/**
	 * Memorizes the given {@link ThrowableQuadFunction} and returns a new {@link ThrowableQuadFunction} that caches the result for each input.<br>
	 * The cache is weakly referenced, so it will be cleared when the input is no longer reachable.<br>
	 * Be aware that this may lead to memory leaks if the input is not properly managed.<br>
	 *
	 * @param function The delegate function to memorize
	 * @param <T> The type of the first input value
	 * @param <U> The type of the second input value
	 * @param <V> The type of the third input value
	 * @param <W> The type of the fourth input value
	 * @param <R> The type of the result
	 * @param <X> The type of the exception
	 * @return A new function that caches the result for each input
	 */
	public static <T, U, V, W, R, X extends Throwable> @NonNull ThrowableQuadFunction<T, U, V, W, R, X> memorizeThrowable(@NonNull ThrowableQuadFunction<T, U, V, W, R, X> function) {
		Objects.requireNonNull(function, "Delegate function must not be null");
		return new ThrowableQuadFunction<>() {
			private final Map<T, Map<U, Map<V, Map<W, R>>>> cache = new WeakHashMap<>();
			
			@Override
			public @NonNull R apply(@NonNull T first, @NonNull U second, @NonNull V third, @NonNull W fourth) throws X {
				if (this.cache.containsKey(first)) {
					Map<U, Map<V, Map<W, R>>> secondMap = this.cache.get(first);
					if (secondMap.containsKey(second)) {
						Map<V, Map<W, R>> thirdMap = secondMap.get(second);
						if (thirdMap.containsKey(third)) {
							Map<W, R> fourthMap = thirdMap.get(third);
							if (fourthMap.containsKey(fourth)) {
								return fourthMap.get(fourth);
							}
						} else {
							thirdMap.put(third, new WeakHashMap<>());
						}
					} else {
						Map<V, Map<W, R>> thirdMap = new WeakHashMap<>();
						thirdMap.put(third, new WeakHashMap<>());
						secondMap.put(second, thirdMap);
					}
				} else {
					Map<U, Map<V, Map<W, R>>> secondMap = new WeakHashMap<>();
					Map<V, Map<W, R>> thirdMap = new WeakHashMap<>();
					thirdMap.put(third, new WeakHashMap<>());
					secondMap.put(second, thirdMap);
					this.cache.put(first, secondMap);
				}
				R result = function.apply(first, second, third, fourth);
				Objects.requireNonNull(result, "Function result must not be null");
				this.cache.get(first).get(second).get(third).put(fourth, result);
				return result;
			}
		};
	}
}
