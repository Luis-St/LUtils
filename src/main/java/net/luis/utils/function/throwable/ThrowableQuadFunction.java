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

package net.luis.utils.function.throwable;

import net.luis.utils.function.QuadFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Functional interface that takes four arguments and returns a value.<br>
 * The {@link FunctionalInterface} method is {@link #apply(Object, Object, Object, Object)}.<br>
 * The class is equivalent to {@link QuadFunction}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <V> The type of the third argument
 * @param <W> The type of the fourth argument
 * @param <R> The return type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableQuadFunction<T, U, V, W, R, X extends Throwable> {
	
	/**
	 * Converts a throwable quad-function into a quad-function that throws a runtime exception when an exception is thrown.<br>
	 * @param function The throwable quad-function
	 * @return A caught quad-function
	 * @param <T> The first argument type
	 * @param <U> The second argument type
	 * @param <R> The return type
	 * @throws NullPointerException If the throwable quad-function is null
	 */
	static <T, U, V, W, R> @NotNull QuadFunction<T, U, V, W, R> caught(@NotNull ThrowableQuadFunction<T, U, V, W, R, ? extends Throwable> function) {
		Objects.requireNonNull(function, "Throwable function must not be null");
		return (t, u, v, w) -> {
			try {
				return function.apply(t, u, v, w);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Applies the arguments to the function and returns the result.<br>
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 * @param w The fourth argument
	 * @return The result
	 * @throws X The exception that can be thrown
	 */
	R apply(T t, U u, V v, W w) throws X;
	
	/**
	 * Returns a composed function that first applies this function to its input,<br>
	 * and then applies the {@code after} function to the result.<br>
	 * @param after The function to apply after this function is applied
	 * @return The composed throwable function
	 * @param <S> The type of the output of the {@code after} function, and of the composed function
	 * @throws NullPointerException If the after function is null
	 */
	default <S> @NotNull ThrowableQuadFunction<T, U, V, W, S, X> andThen(@NotNull ThrowableFunction<? super R, ? extends S, X> after) {
		Objects.requireNonNull(after, "After function must not be null");
		return (t, u, v, w) -> after.apply(this.apply(t, u, v, w));
	}
}
