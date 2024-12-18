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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class ConfigurableCodec<C, O> implements Codec<C> {
	
	private final Codec<C> codec;
	private @Nullable String name;
	private @Nullable Function<O, C> getter;
	
	ConfigurableCodec(@NotNull CodecBuilder<O> ignored, @NotNull Codec<C> codec) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		return this.codec.encodeStart(provider, current, value);
	}
	
	public <R> @NotNull Result<R> encodeNamedStart(@NotNull TypeProvider<R> provider, @NotNull R map, @NotNull O object) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Map must not be null");
		if (this.name == null) {
			return Result.error("Unable to encode field of '" + object + "' with '" + this + "': Name is null");
		}
		if (this.getter == null) {
			return Result.error("Unable to encode field '" + this.name + "' of '" + object + "' with '" + this + "': Getter is null");
		}
		Result<R> result = this.encodeStart(provider, provider.empty(), this.getter.apply(object));
		if (result.isError()) {
			return Result.error("Unable to encode '" + object + "' with '" + this + "': " + result.errorOrThrow());
		}
		R encoded = result.orThrow();
		if (provider.getEmpty(encoded).isSuccess()) {
			return Result.success(map);
		}
		return provider.set(map, this.name, encoded);
	}
	
	@Override
	public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.codec.decodeStart(provider, value);
	}
	
	public <R> @NotNull Result<C> decodeNamedStart(@NotNull TypeProvider<R> provider, @NotNull R map) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Map must not be null");
		if (this.name == null) {
			return Result.error("Unable to decode field of '" + map + "' with '" + this + "': Name is null");
		}
		Result<R> value = provider.get(map, this.name);
		if (value.isError()) {
			return Result.error("Unable to decode field '" + this.name + "' of '" + map + "' with '" + this + "': " + value.errorOrThrow());
		}
		return this.decodeStart(provider, value.orThrow());
	}
	
	public @NotNull ConfigurableCodec<C, O> named(@NotNull String name) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		return this;
	}
	
	public @NotNull ConfigurableCodec<C, O> getter(@NotNull Function<O, C> getter) {
		this.getter = Objects.requireNonNull(getter, "Getter must not be null");
		return this;
	}
}
