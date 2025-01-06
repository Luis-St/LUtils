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

package net.luis.utils.function.throwable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Functional interface that takes an argument and returns a value.<br>
 * The {@link FunctionalInterface} method is {@link #apply(Object)}.<br>
 * The class is equivalent to {@link Function}, but the functional method can throw a checked exception.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the argument
 * @param <R> The return type
 * @param <X> The exception that can be thrown
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, X extends Throwable> {
	
	/**
	 * Converts a throwable function into a function that throws a runtime exception when an exception is thrown.<br>
	 * @param function The throwable function
	 * @return A caught function
	 * @param <T> The argument type
	 * @param <R> The return type
	 * @throws NullPointerException If the throwable function is null
	 */
	static <T, R> @NotNull Function<T, R> caught(@NotNull ThrowableFunction<T, R, ? extends Throwable> function) {
		Objects.requireNonNull(function, "Throwable function must not be null");
		return (t) -> {
			try {
				return function.apply(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		};
	}
	
	/**
	 * Applies the argument to the function and returns the result.<br>
	 * @param t The argument
	 * @return The result
	 * @throws X The exception that can be thrown
	 */
	R apply(T t) throws X;
	
	/**
	 * Returns a composed function that first applies this function to its input,<br>
	 * and then applies the {@code after} function to the result.<br>
	 * @param after The function to apply after this function is applied
	 * @return The composed throwable function
	 * @param <S> The type of the output of the {@code after} function, and of the composed function
	 * @throws NullPointerException If the after function is null
	 */
	default <S> @NotNull ThrowableFunction<T, S, X> andThen(@NotNull ThrowableFunction<? super R, ? extends S, X> after) {
		Objects.requireNonNull(after, "After function must not be null");
		return (t) -> after.apply(this.apply(t));
	}
}
