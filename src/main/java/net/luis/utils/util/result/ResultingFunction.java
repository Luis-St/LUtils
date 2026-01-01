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

package net.luis.utils.util.result;

import net.luis.utils.function.throwable.ThrowableFunction;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Functional interface for mapping a value to a result.<br>
 * This function is a more friendly variant of {@code Function<T, Result<R>>}.<br>
 * The main use case is to map a value to a result that may contain an error.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the input value
 * @param <R> The type of the output result
 */
@FunctionalInterface
public interface ResultingFunction<T, R> extends Function<T, Result<R>> {
	
	/**
	 * Creates a new resulting function that returns the input value as output result.<br>
	 * The output result is a success result with the input value as value.<br>
	 *
	 * @param <T> The type of the input and output value
	 * @return The resulting function
	 */
	static <T> @NonNull ResultingFunction<T, T> identity() {
		return direct(Function.identity());
	}
	
	/**
	 * Creates a new resulting function that applies the given function to the value.<br>
	 * The output result is a success result with the output of the function as value.<br>
	 *
	 * @param function The function to apply to the value
	 * @param <T> The type of the input value
	 * @param <R> The type of the output result
	 * @return The resulting function
	 * @throws NullPointerException If the function is null
	 */
	static <T, R> @NonNull ResultingFunction<T, R> direct(@NonNull Function<T, R> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return value -> Result.success(function.apply(value));
	}
	
	/**
	 * Creates a new resulting function that applies the given throwable function to the value.<br>
	 * If the function throws an exception, the output result is an error with the exception message.<br>
	 * In all other cases, the output result is a success result with the output of the function as value.<br>
	 *
	 * @param function The throwable function to apply to the value
	 * @param <T> The type of the input value
	 * @param <R> The type of the output result
	 * @return The resulting function
	 * @throws NullPointerException If the throwable function is null
	 */
	static <T, R> @NonNull ResultingFunction<T, R> throwable(@NonNull ThrowableFunction<T, R, ? extends Throwable> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return value -> {
			try {
				return Result.success(function.apply(value));
			} catch (Throwable throwable) {
				String message = throwable.getMessage();
				return Result.error(message == null ? "Unknown error, no message provided" : message);
			}
		};
	}
	
	@Override
	@NonNull Result<R> apply(@UnknownNullability T value);
}
