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

import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class ConfigurableCodec<C, O> implements Codec<C> {
	
	private final CodecBuilder<O> builder;
	private final Codec<C> codec;
	private @Nullable String name;
	private @Nullable Function<O, C> getter;
	
	ConfigurableCodec(@NotNull CodecBuilder<O> builder, @NotNull Codec<C> codec) {
		this.builder = builder;
		this.codec = codec;
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		return this.codec.encodeStart(provider, current, value);
	}
	
	public @NotNull <R> Result<R> encodeNamedStart(@NotNull TypeProvider<R> provider, @NotNull R map, @NotNull O object) {
		if (this.name == null) {
			return Result.error("Unable to encode field of '" + object + "' with '" + this + "': Name is null");
		}
		if (this.getter == null) {
			return Result.error("Unable to encode field '" + this.name + "' of '" + object + "' with '" + this + "': Getter is null");
		}
		C value = this.getter.apply(object);
		Result<R> encodedValue = this.encodeStart(provider, provider.empty(), value);
		if (encodedValue.isError()) {
			return Result.error("Unable to encode '" + object + "' with '" + this + "': " + encodedValue.errorOrThrow());
		}
		return provider.set(map, this.name, encodedValue.orThrow());
	}
	
	@Override
	public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		return this.codec.decodeStart(provider, value);
	}
	
	public @NotNull <R> Result<C> decodeNamedStart(@NotNull TypeProvider<R> provider, @NotNull R map) {
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
		this.name = name;
		return this;
	}
	
	public @NotNull ConfigurableCodec<C, O> getter(@NotNull Function<O, C> getter) {
		this.getter = getter;
		return this;
	}
}
