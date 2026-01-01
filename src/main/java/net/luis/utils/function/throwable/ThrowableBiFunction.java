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

package net.luis.utils.function.throwable;

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Functional interface that takes two arguments and returns a value.<br>
 * The {@link FunctionalInterface} method is {@link #apply(Object, Object)}.<br>
 * The class is equivalent to {@link BiFunction}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <R> The return type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, U, R, X extends Throwable> {
	
	/**
	 * Converts a throwable bi-function into a bi-function that throws a runtime exception when an exception is thrown.<br>
	 *
	 * @param function The throwable bi-function
	 * @return A caught bi-function
	 * @param <T> The first argument type
	 * @param <U> The second argument type
	 * @param <R> The return type
	 * @throws NullPointerException If the throwable bi-function is null
	 */
	static <T, U, R> @NonNull BiFunction<T, U, R> caught(@NonNull ThrowableBiFunction<T, U, R, ? extends Throwable> function) {
		Objects.requireNonNull(function, "Throwable function must not be null");
		return (t, u) -> {
			try {
				return function.apply(t, u);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Applies the arguments to the function and returns the result.<br>
	 *
	 * @param t The first argument
	 * @param u The second argument
	 * @return The result
	 * @throws X The exception that can be thrown
	 */
	R apply(T t, U u) throws X;
	
	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result.<br>
	 *
	 * @param after The function to apply after this function is applied
	 * @return The composed throwable function
	 * @param <S> The type of the output of the {@code after} function, and of the composed function
	 * @throws NullPointerException If the after function is null
	 */
	default <S> @NonNull ThrowableBiFunction<T, U, S, X> andThen(@NonNull ThrowableFunction<? super R, ? extends S, X> after) {
		Objects.requireNonNull(after, "After function must not be null");
		return (t, u) -> after.apply(this.apply(t, u));
	}
}
