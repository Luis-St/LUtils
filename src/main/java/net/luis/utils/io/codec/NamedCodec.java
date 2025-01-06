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

/**
 *
 * @author Luis-St
 *
 */

public class NamedCodec<C> implements Codec<C> {
	
	private final Codec<C> codec;
	private final String name;
	
	NamedCodec(@NotNull Codec<C> codec, @NotNull String name) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		this.name = Objects.requireNonNull(name, "Name must not be null");
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R map, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(map, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode named '" + this.name + "' null value using '" + this + "'");
		}
		Result<R> encodedResult = this.codec.encodeStart(provider, provider.empty(), value);
		if (encodedResult.isError()) {
			return Result.error("Unable to encode named '" + this.name + "' '" + value + "' with '" + this + "': " + encodedResult.errorOrThrow());
		}
		R encoded = encodedResult.orThrow();
		if (provider.getEmpty(encoded).isSuccess()) {
			return Result.success(provider.empty());
		}
		Result<R> result = provider.set(map, this.name, encoded);
		if (result.isError()) {
			return Result.error("Unable to encode named '" + this.name + "' '" + value + "' with '" + this + "': " + result.errorOrThrow());
		}
		return Result.success(map);
	}
	
	@Override
	public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R map) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (map == null) {
			return Result.error("Unable to decode named '" + this.name + "' null value using '" + this + "'");
		}
		Result<R> value = provider.get(map, this.name);
		if (value.isError()) {
			return Result.error("Unable to decode named '" + this.name + "' of '" + map + "' with '" + this + "': " + value.errorOrThrow());
		}
		return this.codec.decodeStart(provider, value.orThrow());
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NamedCodec<?> that)) return false;
		
		if (!this.codec.equals(that.codec)) return false;
		return this.name.equals(that.name);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.name);
	}
	
	@Override
	public String toString() {
		return "NamedCodec['" + this.name + "', " + this.codec + "]";
	}
	//endregion
}
