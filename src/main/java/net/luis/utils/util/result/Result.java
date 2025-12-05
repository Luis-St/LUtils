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

package net.luis.utils.util.result;

import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the result of an operation that may succeed, fail, or partially succeed.<br>
 * A Result can be in one of three states:<br>
 * <ul>
 *     <li>Success: The operation completed successfully with a value.</li>
 *     <li>Error: The operation failed with an error message.</li>
 *     <li>Partial: The operation completed with a value but also produced an error message.</li>
 * </ul>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result value
 */
public sealed interface Result<T> permits Success, Error, Partial {
	
	//region Static factory methods
	
	/**
	 * Creates a new successful result with the specified value.<br>
	 *
	 * @param value The value of the result
	 * @return The created result
	 * @param <T> The type of the result
	 */
	static <T> @NotNull Result<T> success(@Nullable T value) {
		return new Success<>(value);
	}
	
	/**
	 * Creates a new failed result with the specified error message.<br>
	 *
	 * @param error The error message of the result
	 * @return The created result
	 * @param <T> The type of the result
	 * @throws NullPointerException If the error message is null
	 */
	static <T> @NotNull Result<T> error(@NotNull String error) {
		return new Error<>(error);
	}
	
	/**
	 * Creates a new partial result with the specified value and error message.<br>
	 * A partial result indicates that the operation produced a value but also encountered an error.<br>
	 *
	 * @param value The partial value of the result
	 * @param error The error message describing what went wrong
	 * @return The created partial result
	 * @param <T> The type of the result
	 * @throws NullPointerException If the error message is null
	 */
	static <T> @NotNull Result<T> partial(@Nullable T value, @NotNull String error) {
		return new Partial<>(value, error, new ArrayList<>());
	}
	
	/**
	 * Creates a new partial result with the specified value, error message, and list of causes.<br>
	 * A partial result indicates that the operation produced a value but also encountered an error.<br>
	 *
	 * @param value The partial value of the result
	 * @param error The error message describing what went wrong
	 * @param causes The list of causes leading to this partial result
	 * @param <T> The type of the result
	 * @return The created partial result
	 * @throws NullPointerException If the error message or causes list is null
	 */
	static <T> @NotNull Result<T> partial(@Nullable T value, @NotNull String error, @NotNull List<String> causes) {
		return new Partial<>(value, error, causes);
	}
	//endregion
	
	/**
	 * Checks if the result is successful (not an error or partial result).<br>
	 * @return True if the result is successful, otherwise false
	 */
	boolean isSuccess();
	
	/**
	 * Checks if the result is an error (no value available).<br>
	 * @return True if the result is an error, otherwise false
	 */
	boolean isError();
	
	/**
	 * Checks if the result is partial (has both a value and an error message).<br>
	 * @return True if the result is partial, otherwise false
	 */
	boolean isPartial();
	
	/**
	 * Checks if the result has a value (success or partial).<br>
	 * @return True if the result has a value, otherwise false
	 */
	boolean hasValue();
	
	/**
	 * Checks if the result has an error message (error or partial).<br>
	 * @return True if the result has an error message, otherwise false
	 */
	boolean hasError();
	
	/**
	 * Returns the result value as an {@link Optional}.<br>
	 * @return The result value, or empty if this is an error result
	 */
	@NotNull Optional<T> result();
	
	/**
	 * Gets the result value or throws an exception.<br>
	 *
	 * @return The result value
	 * @throws IllegalStateException If the result is an error (no value available)
	 */
	@UnknownNullability
	T resultOrThrow();
	
	/**
	 * Gets the result value or throws the specified exception.<br>
	 *
	 * @param exceptionSupplier The supplier providing the exception to be thrown
	 * @return The result value
	 * @param <X> The type of the exception
	 * @throws NullPointerException If the exception supplier is null
	 * @throws X If the result is an error (no value available)
	 */
	<X extends RuntimeException> @UnknownNullability T resultOrThrow(@NotNull Function<String, ? extends X> exceptionSupplier);
	
	/**
	 * Returns the error message as an {@link Optional}.<br>
	 * @return The error message, or empty if this is a success result
	 */
	@NotNull Optional<String> error();
	
	/**
	 * Gets the error message or throws an exception.<br>
	 *
	 * @return The error message
	 * @throws IllegalStateException If the result does not have an error message
	 */
	@NotNull String errorOrThrow();
	
	/**
	 * Maps the result value to another type.<br>
	 * If the result is an error, the mapping is not applied.<br>
	 * For partial results, the mapping is applied to the value while preserving the error message.<br>
	 *
	 * @param mapper The mapper function
	 * @return The mapped result
	 * @param <R> The type of the mapped result
	 * @throws NullPointerException If the mapper is null
	 */
	<R> @NotNull Result<R> map(@NotNull Function<T, R> mapper);
	
	/**
	 * Maps the result value to another result.<br>
	 * This is useful for chaining operations or validating the result.<br>
	 * If the result is an error, the mapping is not applied.<br>
	 * For partial results, the mapping is applied to the value, and errors are combined.<br>
	 *
	 * @param mapper The mapper function
	 * @return The mapped result
	 * @param <R> The type of the mapped result
	 * @throws NullPointerException If the mapper is null
	 */
	<R> @NotNull Result<R> flatMap(@NotNull Function<T, Result<R>> mapper);
	
	/**
	 * Gets the result value or the specified default value.<br>
	 * This is useful for providing a fallback value if the operation fails.<br>
	 *
	 * @param fallback The fallback value
	 * @return The result value or the fallback value
	 * @throws NullPointerException If the fallback value is null
	 */
	@UnknownNullability
	T orElse(@NotNull T fallback);
	
	/**
	 * Gets the result value or the result of the specified supplier.<br>
	 * This is useful for providing a fallback value if the operation fails.<br>
	 *
	 * @param supplier The supplier of the fallback value
	 * @return The result value or the fallback value
	 * @throws NullPointerException If the supplier is null
	 */
	@UnknownNullability
	T orElseGet(@NotNull Supplier<? extends T> supplier);
}
