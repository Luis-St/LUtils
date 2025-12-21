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

import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Functional interface that takes four arguments and returns a value.<br>
 * The {@link FunctionalInterface} method is {@link #apply(Object, Object, Object, Object)}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the first argument
 * @param <U> The type of the second argument
 * @param <V> The type of the third argument
 * @param <W> The type of the fourth argument
 * @param <R> The type of the result
 */
@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {
	
	/**
	 * Applies the arguments to the function and returns a value.<br>
	 *
	 * @param t The first argument
	 * @param u The second argument
	 * @param v The third argument
	 * @param w The fourth argument
	 * @return The result
	 */
	R apply(T t, U u, V v, W w);
	
	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result.<br>
	 *
	 * @param after The function to apply after this function is applied
	 * @return The composed function
	 * @param <S> The type of the output of the {@code after} function, and of the composed function
	 * @throws NullPointerException If the after function is null
	 */
	default <S> @NonNull QuadFunction<T, U, V, W, S> andThen(@NonNull Function<? super R, ? extends S> after) {
		Objects.requireNonNull(after, "After function must not be null");
		return (t, u, v, w) -> after.apply(this.apply(t, u, v, w));
	}
}
