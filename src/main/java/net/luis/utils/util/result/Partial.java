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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a partial result with both a value and an error message.<br>
 * This indicates that the operation produced a result but also encountered an issue.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result value
 * @param value The partial result value (may be null)
 * @param errorMessage The error message describing what went wrong (must not be null)
 */
record Partial<T>(@Nullable T value, @NotNull String errorMessage) implements Result<T> {

	/**
	 * Constructs a new partial result with the specified value and error message.<br>
	 *
	 * @param value The partial result value
	 * @param errorMessage The error message
	 * @throws NullPointerException If the error message is null
	 */
	Partial {
		Objects.requireNonNull(errorMessage, "Error must not be null");
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public boolean isError() {
		return false;
	}

	@Override
	public boolean isPartial() {
		return true;
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public boolean hasError() {
		return true;
	}

	@Override
	public @NotNull Optional<T> result() {
		return Optional.ofNullable(this.value);
	}

	@Override
	public @UnknownNullability T resultOrThrow() {
		return this.value;
	}

	@Override
	public <X extends RuntimeException> @UnknownNullability T resultOrThrow(@NotNull Function<String, ? extends X> exceptionSupplier) {
		Objects.requireNonNull(exceptionSupplier, "Exception supplier must not be null");
		return this.value;
	}

	@Override
	public @NotNull Optional<String> error() {
		return Optional.of(this.errorMessage);
	}

	@Override
	public @NotNull String errorOrThrow() {
		return this.errorMessage;
	}

	@Override
	public <R> @NotNull Result<R> map(@NotNull Function<T, R> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return new Partial<>(mapper.apply(this.value), this.errorMessage);
	}

	@Override
	public <R> @NotNull Result<R> flatMap(@NotNull Function<T, Result<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		Result<R> mapped = mapper.apply(this.value);

		// If the mapped result has an error, combine the error messages
		if (mapped.hasError()) {
			String combinedError = this.errorMessage + "; " + mapped.errorOrThrow();
			// If the mapped result has a value, return a partial result
			if (mapped.hasValue()) {
				return Result.partial(mapped.resultOrThrow(), combinedError);
			}
			// Otherwise return an error with the combined message
			return Result.error(combinedError);
		}

		// If the mapped result is successful, return a partial result with the original error
		return Result.partial(mapped.resultOrThrow(), this.errorMessage);
	}

	@Override
	public @UnknownNullability T orElse(@NotNull T fallback) {
		Objects.requireNonNull(fallback, "Fallback must not be null");
		return this.value;
	}

	@Override
	public @UnknownNullability T orElseGet(@NotNull Supplier<? extends T> supplier) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		return this.value;
	}
	
	@Override
	public @NotNull String toString() {
		return "Partial[value=" + this.value + ", error=" + this.errorMessage + "]";
	}
}
