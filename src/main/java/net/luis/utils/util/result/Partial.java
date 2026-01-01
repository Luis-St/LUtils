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

import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a partial result with both a value and an error message.<br>
 * This indicates that the operation produced a result but also encountered an issue.<br>
 *
 * @param value The partial result value
 * @param errorMessage The error message describing what went wrong
 * @param causes The list of causes leading to this partial result
 * @param <T> The type of the result value
 */
record Partial<T>(@Nullable T value, @NonNull String errorMessage, @NonNull List<String> causes) implements Result<T> {
	
	/**
	 * Constructs a new partial result with the specified value, error message, and list of causes.<br>
	 *
	 * @param value The partial result value
	 * @param errorMessage The error message describing what went wrong
	 * @param causes The list of causes leading to this partial result
	 * @throws NullPointerException If the error message is null
	 */
	Partial {
		Objects.requireNonNull(errorMessage, "Error must not be null");
		Objects.requireNonNull(causes, "List of causes must not be null");
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
	public @NonNull Optional<T> result() {
		return Optional.ofNullable(this.value);
	}
	
	@Override
	public @UnknownNullability T resultOrThrow() {
		return this.value;
	}
	
	@Override
	public <X extends RuntimeException> @UnknownNullability T resultOrThrow(@NonNull Function<String, ? extends X> exceptionSupplier) {
		Objects.requireNonNull(exceptionSupplier, "Exception supplier must not be null");
		return this.value;
	}
	
	private @NonNull String buildErrorMessage() {
		StringBuilder sb = new StringBuilder(this.errorMessage);
		if (this.causes.isEmpty()) {
			return sb.toString();
		}
		
		sb.append("\n");
		for (String cause : this.causes) {
			sb.append(" - ").append(cause).append("\n");
		}
		return sb.toString().stripTrailing();
	}
	
	@Override
	public @NonNull Optional<String> error() {
		return Optional.of(this.buildErrorMessage());
	}
	
	@Override
	public @NonNull String errorOrThrow() {
		return this.buildErrorMessage();
	}
	
	@Override
	public <R> @NonNull Result<R> map(@NonNull Function<T, R> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return new Partial<>(mapper.apply(this.value), this.errorMessage, this.causes);
	}
	
	@Override
	public <R> @NonNull Result<R> flatMap(@NonNull Function<T, Result<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		Result<R> mapped = mapper.apply(this.value);
		
		if (mapped.hasError()) {
			if (mapped.hasValue()) {
				return Result.partial(mapped.resultOrThrow(), "Mapping of partial result failed", List.of(this.errorMessage, mapped.errorOrThrow()));
			}
			return Result.error(this.errorMessage + "; " + mapped.errorOrThrow());
		}
		
		return Result.partial(mapped.resultOrThrow(), this.errorMessage);
	}
	
	@Override
	public @UnknownNullability T orElse(@NonNull T fallback) {
		Objects.requireNonNull(fallback, "Fallback must not be null");
		return this.value;
	}
	
	@Override
	public @UnknownNullability T orElseGet(@NonNull Supplier<? extends T> supplier) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		return this.value;
	}
	
	@Override
	public @NonNull String toString() {
		return "Partial[value=" + this.value + ", error=" + this.errorMessage + "]";
	}
}
