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
 * Represents a successful result with a value.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result value
 * @param value The successful result value (may be null)
 */
record Success<T>(@Nullable T value) implements Result<T> {
	
	@Override
	public boolean isSuccess() {
		return true;
	}
	
	@Override
	public boolean isError() {
		return false;
	}
	
	@Override
	public boolean isPartial() {
		return false;
	}
	
	@Override
	public boolean hasValue() {
		return true;
	}
	
	@Override
	public boolean hasError() {
		return false;
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
		return Optional.empty();
	}
	
	@Override
	public @NotNull String errorOrThrow() {
		throw new IllegalStateException("Result is not an error");
	}
	
	@Override
	public <R> @NotNull Result<R> map(@NotNull Function<T, R> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return new Success<>(mapper.apply(this.value));
	}
	
	@Override
	public <R> @NotNull Result<R> flatMap(@NotNull Function<T, Result<R>> mapper) {
		Objects.requireNonNull(mapper, "Mapper must not be null");
		return mapper.apply(this.value);
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
		return "Success[" + this.value + "]";
	}
}
