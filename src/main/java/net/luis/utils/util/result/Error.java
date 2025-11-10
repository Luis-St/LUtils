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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a failed result with an error message.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result value
 * @param errorMessage The error message (must not be null)
 */
record Error<T>(@NotNull String errorMessage) implements Result<T> {
	
	/**
	 * Constructs a new error result with the specified error message.<br>
	 *
	 * @param errorMessage The error message
	 * @throws NullPointerException If the error message is null
	 */
	Error {
		Objects.requireNonNull(errorMessage, "Error must not be null");
	}
	
	@Override
	public boolean isSuccess() {
		return false;
	}
	
	@Override
	public boolean isError() {
		return true;
	}
	
	@Override
	public boolean isPartial() {
		return false;
	}
	
	@Override
	public boolean hasValue() {
		return false;
	}
	
	@Override
	public boolean hasError() {
		return true;
	}
	
	@Override
	public @NotNull Optional<T> result() {
		return Optional.empty();
	}
	
	@Override
	public @UnknownNullability T resultOrThrow() {
		throw new IllegalStateException("Result failed, no value present: " + this.errorMessage);
	}
	
	@Override
	public <X extends RuntimeException> @UnknownNullability T resultOrThrow(@NotNull Function<String, ? extends X> exceptionSupplier) {
		Objects.requireNonNull(exceptionSupplier, "Exception supplier must not be null");
		throw exceptionSupplier.apply(this.errorMessage);
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
	@SuppressWarnings("unchecked")
	public <R> @NotNull Result<R> map(@NotNull Function<T, R> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return (Result<R>) this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <R> @NotNull Result<R> flatMap(@NotNull Function<T, Result<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return (Result<R>) this;
	}
	
	@Override
	public @UnknownNullability T orElse(@NotNull T fallback) {
		Objects.requireNonNull(fallback, "Fallback must not be null");
		return fallback;
	}
	
	@Override
	public @UnknownNullability T orElseGet(@NotNull Supplier<? extends T> supplier) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		return supplier.get();
	}
	
	@Override
	public @NotNull String toString() {
		return "Error[" + this.errorMessage + "]";
	}
}
