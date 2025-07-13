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

package net.luis.utils.io.codec.group.grouper;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.group.function.CodecGroupingFunction1;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.*;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a group of one codec.<br>
 * The grouper is used for creating a new single codec that encodes and decodes a value of a specific type.<br>
 * Each codec is responsible for encoding and decoding a specific component (field) of the value.<br>
 * The grouped codec is created by providing a function that creates the value from the decoded components.<br>
 *
 * @author Luis-St
 *
 * @param codec1 The first codec
 * @param <CI1> The type of the first component
 * @param <O> The type of the resulting object this grouper is for
 */
public record CodecGrouper1<CI1, O>(
	@NotNull ConfiguredCodec<CI1, O> codec1
) {
	
	/**
	 * Constructs a new codec grouper with the provided codec.<br>
	 * Do not use this constructor directly, use the builder method in {@link CodecBuilder} instead.<br>
	 *
	 * @param codec1 The codec
	 * @throws NullPointerException If any of the provided codecs is null
	 */
	@ApiStatus.Internal
	public CodecGrouper1 {
		Objects.requireNonNull(codec1, "Configured codec #1 must not be null");
	}
	
	/**
	 * Creates a new codec using the codecs of this grouper and the provided grouping function.<br>
	 * The grouping function is used to create the resulting object from the decoded components.<br>
	 *
	 * @param function The grouping function
	 * @return The created codec
	 * @throws NullPointerException If the provided grouping function is null
	 */
	@SuppressWarnings({ "DuplicatedCode", "UnqualifiedFieldAccess" })
	public @NotNull Codec<O> create(@NotNull CodecGroupingFunction1<CI1, O> function) {
		Objects.requireNonNull(function, "Codec grouping function must not be null");
		return new Codec<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				if (value == null) {
					return Result.error("Unable to encode null value using '" + this);
				}
				Result<R> mergedMap = provider.merge(current, provider.createMap());
				if (mergedMap.isError()) {
					return Result.error("Unable to encode '" + value + "' using '" + this + "': " + mergedMap.errorOrThrow());
				}
				R map = mergedMap.orThrow();
				Result<R> encoded1 = codec1.encodeStart(provider, map, value);
				if (encoded1.isError()) {
					return Result.error("Unable to encode component of '" + value + "' using '" + codec1 + "': " + encoded1.errorOrThrow());
				}
				return Result.success(map);
			}
			
			@Override
			public <R> @NotNull Result<O> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				if (value == null) {
					return Result.error("Unable to decode null value using '" + this);
				}
				Result<Map<String, R>> decodedMap = provider.getMap(value);
				if (decodedMap.isError()) {
					return Result.error("Unable to decode '" + value + "' using '" + this + "': " + decodedMap.errorOrThrow());
				}
				Result<CI1> decoded1 = codec1.decodeStart(provider, value);
				if (decoded1.isError()) {
					return Result.error("Unable to decode component of '" + value + "' using '" + codec1 + "': " + decoded1.errorOrThrow());
				}
				return Result.success(function.create(decoded1.orThrow()));
			}
			
			@Override
			public String toString() {
				return "Codec[" + codec1 + "]";
			}
		};
	}
}
