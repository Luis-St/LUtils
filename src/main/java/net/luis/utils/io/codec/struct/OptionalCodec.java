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
		this.codec = codec;
	}
	
	private @NotNull Optional<C> getDefault() {
		if (this.defaultProvider == null) {
			return Optional.empty();
		}
		return Optional.of(this.defaultProvider.get());
	}
	
	@Override
	@SuppressWarnings("OptionalAssignedToNull")
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Optional<C> value) {
		if (value == null) {
			return Result.success(current);
		}
		return value.map(c -> this.codec.encodeStart(provider, current, c)).orElseGet(() -> Result.success(current));
	}
	
	@Override
	public @NotNull <R> Result<Optional<C>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		if (value == null) {
			return Result.success(this.getDefault());
		}
		Result<R> decoded = provider.getEmpty(value);
		if (decoded.isSuccess()) {
			return Result.success(this.getDefault());
		}
		return this.codec.decodeStart(provider, value).map(Optional::of);
	}
	
	public @NotNull Codec<Optional<C>> orElse(@NotNull C defaultValue) {
		return this.orElseGet(() -> defaultValue);
	}
	
	public @NotNull Codec<Optional<C>> orElseGet(@NotNull Supplier<C> defaultProvider) {
		this.defaultProvider = defaultProvider;
		return this;
	}
}
