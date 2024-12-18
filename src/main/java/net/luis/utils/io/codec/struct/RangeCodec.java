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
import net.luis.utils.io.codec.KeyableCodec;
import net.luis.utils.io.codec.decoder.KeyableDecoder;
import net.luis.utils.io.codec.encoder.KeyableEncoder;
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

public abstract class RangeCodec<C extends Number & Comparable<C>> implements KeyableCodec<C> {
	
	private final C min;
	private final C max;
	private final Function<Number, C> converter;
	private final KeyableEncoder<C> encoder;
	private final KeyableDecoder<C> decoder;
	
	protected RangeCodec(@NotNull C min, @NotNull C max, @NotNull Function<Number, C> converter, @NotNull Function<String, C> fromKey) {
		this(min, max, converter, String::valueOf, fromKey);
	}
	
	protected RangeCodec(@NotNull C min, @NotNull C max, @NotNull Function<Number, C> converter, @NotNull Function<C, String> toKey, @NotNull Function<String, C> fromKey) {
		this.min = Objects.requireNonNull(min, "Min value must not be null");
		this.max = Objects.requireNonNull(max, "Max value must not be null");
		this.converter = Objects.requireNonNull(converter, "Converter must not be null");
		this.encoder = KeyableEncoder.of(this, Objects.requireNonNull(toKey, "String encoder must not be null"));
		this.decoder = KeyableDecoder.of(this, Objects.requireNonNull(fromKey, "String decoder must not be null"));
	}
	
	private static <C extends Number & Comparable<C>> @NotNull Codec<C> ranged(@NotNull Codec<C> codec, @NotNull C minInclusive, @NotNull C maxInclusive) {
		Objects.requireNonNull(codec, "Codec must not be null");
		Objects.requireNonNull(minInclusive, "Min value must not be null");
		Objects.requireNonNull(maxInclusive, "Max value must not be null");
		if (minInclusive.compareTo(maxInclusive) > 0) {
			throw new IllegalArgumentException("Min value '" + minInclusive + "' is greater than max value '" + maxInclusive + "'");
		}
		return new Codec<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				if (value == null) {
					return Result.error("Unable to decode null value as number with codec '" + codec + "'");
				}
				return codec.encodeStart(provider, current, value);
			}

			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Result<C> result = codec.decodeStart(provider, value);
				if (result.isError()) {
					return result;
				}
				C decoded = result.orThrow();
				if (decoded.compareTo(minInclusive) >= 0 && 0 >= decoded.compareTo(maxInclusive)) {
					return result;
				}
				return Result.error("Number '" + decoded + "' was decoded successfully, but is out of range: " + minInclusive + ".." + minInclusive);
			}
			
			@Override
			public String toString() {
				return "RangeCodec " + codec + "[" + minInclusive + ".." + minInclusive + "]";
			}
		};
	}
	
	@Override
	public @NotNull <R> Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Value must not be null");
		return this.encoder.encodeKey(provider, key);
	}
	
	@Override
	public @NotNull <R> Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Value must not be null");
		return this.decoder.decodeKey(provider, key);
	}
	
	public @NotNull KeyableCodec<C> positive() {
		return this.makeKeyable(ranged(this, this.converter.apply(1), this.max));
	}
	
	public @NotNull KeyableCodec<C> positiveOrZero() {
		return this.makeKeyable(ranged(this, this.converter.apply(0), this.max));
	}
	
	public @NotNull KeyableCodec<C> negative() {
		return this.makeKeyable(ranged(this, this.min, this.converter.apply(-1)));
	}
	
	public @NotNull KeyableCodec<C> negativeOrZero() {
		return this.makeKeyable(ranged(this, this.min, this.converter.apply(0)));
	}
	
	public @NotNull KeyableCodec<C> atLeast(@NotNull C min) {
		return this.makeKeyable(ranged(this, min, this.max));
	}
	
	public @NotNull KeyableCodec<C> atMost(@NotNull C max) {
		return this.makeKeyable(ranged(this, this.min, max));
	}
	
	public @NotNull KeyableCodec<C> between(@NotNull C minInclusive, @NotNull C maxInclusive) {
		return this.makeKeyable(ranged(this, minInclusive, maxInclusive));
	}
	
	private @NotNull KeyableCodec<C> makeKeyable(@NotNull Codec<C> codec) {
		Objects.requireNonNull(codec, "Codec must not be null");
		return Codec.keyable(codec, this.encoder, this.decoder);
	}
}
