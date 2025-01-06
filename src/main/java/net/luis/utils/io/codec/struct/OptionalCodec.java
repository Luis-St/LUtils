/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 * @author Luis-St
 *
 */

public class OptionalCodec<C> implements Codec<Optional<C>> {
	
	private final Codec<C> codec;
	private @Nullable Supplier<C> defaultProvider;
	
	public OptionalCodec(@NotNull Codec<C> codec) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	private @NotNull Optional<C> getDefault() {
		if (this.defaultProvider == null) {
			return Optional.empty();
		}
		return Optional.of(this.defaultProvider.get());
	}
	
	@Override
	@SuppressWarnings("OptionalAssignedToNull")
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Optional<C> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.success(current);
		}
		return value.map(c -> this.codec.encodeStart(provider, current, c)).orElseGet(() -> Result.success(current));
	}
	
	@Override
	public <R> @NotNull Result<Optional<C>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.success(this.getDefault());
		}
		Result<R> decoded = provider.getEmpty(value);
		if (decoded.isSuccess()) {
			return Result.success(this.getDefault());
		}
		Result<C> result = this.codec.decodeStart(provider, value);
		if (result.isError()) {
			return Result.success(this.getDefault());
		}
		return result.map(Optional::of);
	}
	
	public @NotNull Codec<C> orElseFlat(@Nullable C defaultValue) {
		return this.orElseGetFlat(() -> defaultValue);
	}
	
	public @NotNull Codec<C> orElseGetFlat(@NotNull Supplier<C> supplier) {
		Objects.requireNonNull(supplier, "Supplier must not be null");
		return this.xmap(Optional::ofNullable, optional -> optional.orElseGet(supplier));
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OptionalCodec<?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return Objects.equals(this.defaultProvider, that.defaultProvider);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec);
	}
	
	@Override
	public String toString() {
		return "OptionalCodec[" + this.codec + "]";
	}
	//endregion
}
