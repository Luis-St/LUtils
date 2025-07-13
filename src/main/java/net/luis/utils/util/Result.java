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

package net.luis.utils.util;

import org.jetbrains.annotations.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the result of an operation that may fail.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result
 */
public class Result<T> implements Supplier<Either<T, String>> {
	
	/**
	 * The internal result of this instance.<br>
	 * This is either the result or the error message.<br>
	 */
	private final Either<T, String> result;
	
	/**
	 * Constructs a new result with the specified result.<br>
	 *
	 * @param result The result of the operation
	 * @throws NullPointerException If the result is null
	 */
	protected Result(@NotNull Either<T, String> result) { // protected to prevent instantiation from outside but allow inheritance
		this.result = Objects.requireNonNull(result, "Result must not be null");
	}
	
	//region Static factory methods
	
	/**
	 * Creates a new successful result with the specified value.<br>
	 *
	 * @param value The value of the result
	 * @return The created result
	 * @param <T> The type of the result
	 */
	public static <T> @NotNull Result<T> success(@Nullable T value) {
		return new Result<>(Either.left(value));
	}
	
	/**
	 * Creates a new failed result with the specified error message.<br>
	 *
	 * @param error The error message of the result
	 * @return The created result
	 * @param <T> The type of the result
	 * @throws NullPointerException If the error message is null
	 */
	public static <T> @NotNull Result<T> error(@NotNull String error) {
		return new Result<>(Either.right(Objects.requireNonNull(error, "Error must not be null")));
	}
	//endregion
	
	/**
	 * @return The raw {@link Either} of this result
	 */
	@Override
	public @NotNull Either<T, String> get() {
		return this.result;
	}
	
	/**
	 * Checks if the result is successful.<br>
	 * @return True if the result is successful, otherwise false
	 */
	public boolean isSuccess() {
		return this.result.isLeft();
	}
	
	/**
	 * Returns the result of the operation as an {@link Optional}.<br>
	 * @return The result of the operation
	 */
	public @NotNull Optional<T> result() {
		return this.result.left();
	}
	
	/**
	 * Gets the result of the operation or throws an exception.<br>
	 *
	 * @return The result of the operation
	 * @throws IllegalStateException If the result is an error
	 */
	public @UnknownNullability T orThrow() {
		return this.orThrow(error -> new IllegalStateException("Result failed, no value present: " + error));
	}
	
	/**
	 * Gets the result of the operation or throws the specified exception.<br>
	 *
	 * @param exceptionSupplier The supplier holding the exception to be thrown
	 * @return The result of the operation
	 * @param <X> The type of the exception
	 * @throws NullPointerException If the exception supplier is null
	 * @throws X If the result is an error
	 */
	public <X extends RuntimeException> @UnknownNullability T orThrow(@NotNull Function<String, ? extends X> exceptionSupplier) {
		if (this.result.isLeft()) {
			return this.result.leftOrThrow();
		}
		throw Objects.requireNonNull(exceptionSupplier, "Supplied exception must not be null").apply(this.result.rightOrThrow());
	}
	
	/**
	 * Checks if the result is an error.<br>
	 * @return True if the result is an error, otherwise false
	 */
	public boolean isError() {
		return this.result.isRight();
	}
	
	/**
	 * Returns the error message of the result as an {@link Optional}.<br>
	 * @return The error message
	 */
	public @NotNull Optional<String> error() {
		return this.result.right();
	}
	
	/**
	 * Gets the error message of the operation or throws an exception.<br>
	 *
	 * @return The error message
	 * @throws IllegalStateException If the result is not an error
	 */
	public @NotNull String errorOrThrow() {
		return this.error().orElseThrow(() -> new IllegalStateException("Result is not an error"));
	}
	
	/**
	 * Maps the result of the operation to another type.<br>
	 * If the result is an error, the mapping is not applied.<br>
	 *
	 * @param mapper The mapper function
	 * @return The mapped result
	 * @param <R> The type of the mapped result
	 * @throws NullPointerException If the mapper is null
	 */
	public <R> @NotNull Result<R> map(@NotNull Function<T, R> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return new Result<>(this.result.mapLeft(mapper));
	}
	
	/**
	 * Maps the result of the operation to another result of the same type.<br>
	 * This is useful for chaining operations or validating the result.<br>
	 * If the result is an error, the mapping is not applied.<br>
	 *
	 * @param mapper The mapper function
	 * @return The mapped result
	 * @param <R> The type of the mapped result
	 * @throws NullPointerException If the mapper is null
	 */
	public <R> @NotNull Result<R> flatMap(@NotNull Function<T, Result<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		if (this.isSuccess()) {
			return mapper.apply(this.orThrow());
		}
		return new Result<>(Either.right(this.errorOrThrow()));
	}
	
	/**
	 * Gets the result of the operation or the specified default value.<br>
	 * This is useful for providing a fallback value if the operation fails.<br>
	 * This method is equivalent to {@link #orElseGet(Supplier)} with a constant supplier.<br>
	 *
	 * @param fallback The fallback value
	 * @return The result of the operation or the fallback value
	 * @throws NullPointerException If the fallback value is null
	 */
	public @UnknownNullability T orElse(@NotNull T fallback) {
		Objects.requireNonNull(fallback, "Fallback must not be null");
		return this.orElseGet(() -> fallback);
	}
	
	/**
	 * Gets the result of the operation or the result of the specified supplier.<br>
	 * This is useful for providing a fallback value if the operation fails.<br>
	 *
	 * @param supplier The supplier of the fallback value
	 * @return The result of the operation or the fallback value
	 * @throws NullPointerException If the supplier is null
	 */
	public @UnknownNullability T orElseGet(@NotNull Supplier<? extends T> supplier) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		if (this.result.isLeft()) {
			return this.result.leftOrThrow();
		}
		return supplier.get();
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Result<?> result)) return false;
		
		return this.result.equals(result.result);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.result);
	}
	
	@Override
	public String toString() {
		if (this.isSuccess()) {
			return String.valueOf(this.orThrow());
		}
		return this.error().orElseThrow();
	}
	//endregion
}
