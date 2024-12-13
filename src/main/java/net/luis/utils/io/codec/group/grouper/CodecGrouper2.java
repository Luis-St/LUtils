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

package net.luis.utils.io.codec.group.grouper;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.ConfigurableCodec;
import net.luis.utils.io.codec.group.function.CodecGroupingFunction2;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record CodecGrouper2<CI1, CI2, O>(
	@NotNull ConfigurableCodec<CI1, O> codec1,
	@NotNull ConfigurableCodec<CI2, O> codec2
) {
	
	@SuppressWarnings({ "DuplicatedCode", "UnqualifiedFieldAccess" })
	public @NotNull Codec<O> create(@NotNull CodecGroupingFunction2<CI1, CI2, O> function) {
		return new Codec<>() {
			
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
				if (value == null) {
					return Result.error("Unable to encode null value using '" + this);
				}
				Result<R> mergedMap = provider.merge(current, provider.createMap());
				if (mergedMap.isError()) {
					return Result.error("Unable to encode '" + value + "' using '" + this + "': " + mergedMap.errorOrThrow());
				}
				R map = mergedMap.orThrow();
				Result<R> encoded1 = codec1.encodeNamedStart(provider, map, value);
				if (encoded1.isError()) {
					return Result.error("Unable to encode component of '" + value + "' using '" + codec1 + "': " + encoded1.errorOrThrow());
				}
				Result<R> encoded2 = codec2.encodeNamedStart(provider, map, value);
				if (encoded2.isError()) {
					return Result.error("Unable to encode component of '" + value + "' using '" + codec2 + "': " + encoded2.errorOrThrow());
				}
				return Result.success(map);
			}
			
			@Override
			public <R> @NotNull Result<O> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				if (value == null) {
					return Result.error("Unable to decode null value using '" + this);
				}
				Result<Map<String, R>> decodedMap = provider.getMap(value);
				if (decodedMap.isError()) {
					return Result.error("Unable to decode '" + value + "' using '" + this + "': " + decodedMap.errorOrThrow());
				}
				Result<CI1> decoded1 = codec1.decodeNamedStart(provider, value);
				if (decoded1.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec1 + "': " + decoded1.errorOrThrow());
				}
				Result<CI2> decoded2 = codec2.decodeNamedStart(provider, value);
				if (decoded2.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec2 + "': " + decoded2.errorOrThrow());
				}
				return Result.success(function.create(decoded1.orThrow(), decoded2.orThrow()));
			}
			
			@Override
			public String toString() {
				return "Codec[" + codec1 + ", " + codec2 + "]";
			}
		};
	}
}
