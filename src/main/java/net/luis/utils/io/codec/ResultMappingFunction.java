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

package net.luis.utils.io.codec;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * Functional interface for mapping the type of a {@link Result}.<br>
 * This function is a more friendly variant of {@code Function<Result<T>, Result<R>>}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the input result
 * @param <R> The type of the output result
 */
@FunctionalInterface
public interface ResultMappingFunction<T, R> extends Function<Result<T>, Result<R>> {
	
	/**
	 * Creates a new result mapping function that applies the given function to the result.<br>
	 * If the result is an error, the error is propagated to the output result and the function is not applied.<br>
	 *
	 * @param function The function to apply to the result
	 * @param <T> The type of the input result
	 * @param <R> The type of the output result
	 * @return The result mapping function
	 * @throws NullPointerException If the function is null
	 */
	static <T, R> @NotNull ResultMappingFunction<T, R> direct(@NotNull Function<T, R> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return result -> result.map(function);
	}
	
	/**
	 * Creates a new result mapping function that applies the given throwable function to the result.<br>
	 * If the result is an error, the error is propagated to the output result and the function is not applied.<br>
	 * If the function throws an exception, the exception is caught and the output result is an error with the exception message.<br>
	 * In all other cases, the function is applied to the result.<br>
	 *
	 * @param function The throwable function to apply to the result
	 * @param <T> The type of the input result
	 * @param <R> The type of the output result
	 * @return The result mapping function
	 * @throws NullPointerException If the throwable function is null
	 */
	static <T, R> @NotNull ResultMappingFunction<T, R> throwable(@NotNull ThrowableFunction<T, R, ? extends Throwable> function) {
		Objects.requireNonNull(function, "Function must not be null");
		return result -> {
			if (result.isError()) {
				return Result.error(result.errorOrThrow());
			}
			try {
				return Result.success(function.apply(result.orThrow()));
			} catch (Throwable throwable) {
				return Result.error(throwable.getMessage());
			}
		};
	}
	
	@Override
	@NotNull Result<R> apply(@NotNull Result<T> input);
}
