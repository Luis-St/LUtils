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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.KeyableCodec;
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
	
	private final String name;
	private final C minInclusive;
	private final C maxInclusive;
	private final Function<Number, C> converter;
	private final Function<C, String> stringEncoder;
	private final Function<String, C> stringDecoder;
	
	protected RangeCodec(@NotNull String name, @NotNull C min, @NotNull C max, @NotNull Function<Number, C> converter, @NotNull Function<String, C> decoder) {
		this(name, min, max, converter, String::valueOf, decoder);
	}
	
	protected RangeCodec(@NotNull String name, @NotNull C min, @NotNull C max, @NotNull Function<Number, C> converter, @NotNull Function<C, String> stringEncoder, @NotNull Function<String, C> stringDecoder) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.minInclusive = Objects.requireNonNull(min, "Min value must not be null");
		this.maxInclusive = Objects.requireNonNull(max, "Max value must not be null");
		this.converter = Objects.requireNonNull(converter, "Converter must not be null");
		this.stringEncoder = Objects.requireNonNull(stringEncoder, "String encoder must not be null");
		this.stringDecoder = Objects.requireNonNull(stringDecoder, "String decoder must not be null");
	}
	
	//region Encode
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as number using '" + this + "'");
		}
		return this.encodeNumber(provider, value);
	}
	
	protected abstract @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull C value);
	
	@Override
	public @NotNull <R> Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Value must not be null");
		String encoded;
		try {
			encoded = this.stringEncoder.apply(key);
		} catch (Exception e) {
			return Result.error("Unable to encode key '" + key + "' with codec '" + this + "': " + e.getMessage());
		}
		if (encoded == null) {
			return Result.error("Unable to encode key '" + key + "' with codec '" + this + "'");
		}
		return Result.success(encoded);
	}
	//endregion
	
	//region Decode
	@Override
	public @NotNull <R> Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as number using '" + this + "'");
		}
		Result<C> result = this.decodeNumber(provider, value);
		if (result.isError()) {
			return result;
		}
		C decoded = result.orThrow();
		if (decoded.compareTo(this.minInclusive) >= 0 && 0 >= decoded.compareTo(this.maxInclusive)) {
			return result;
		}
		return Result.error("Number '" + decoded + "' was decoded successfully, but is out of range: " + this.minInclusive + ".." + this.minInclusive);
	}
	
	protected abstract @NotNull <R> Result<C> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value);
	
	@Override
	public @NotNull <R> Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Value must not be null");
		C decoded;
		try {
			decoded = this.stringDecoder.apply(key);
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' with codec '" + this + "': " + e.getMessage());
		}
		if (decoded == null) {
			return Result.error("Unable to decode key '" + key + "' with codec '" + this + "'");
		}
		return Result.success(decoded);
	}
	//endregion
	
	public @NotNull RangeCodec<C> positive() {
		return this.range(this.converter.apply(1), this.maxInclusive);
	}
	
	public @NotNull RangeCodec<C> positiveOrZero() {
		return this.range(this.converter.apply(0), this.maxInclusive);
	}
	
	public @NotNull RangeCodec<C> negative() {
		return this.range(this.minInclusive, this.converter.apply(-1));
	}
	
	public @NotNull RangeCodec<C> negativeOrZero() {
		return this.range(this.minInclusive, this.converter.apply(0));
	}
	
	public @NotNull RangeCodec<C> atLeast(@NotNull C min) {
		Objects.requireNonNull(min, "Min value must not be null");
		return this.range(min, this.maxInclusive);
	}
	
	public @NotNull RangeCodec<C> atMost(@NotNull C max) {
		Objects.requireNonNull(max, "Max value must not be null");
		return this.range(this.minInclusive, max);
	}
	
	public @NotNull RangeCodec<C> range(@NotNull C minInclusive, @NotNull C maxInclusive) {
		Objects.requireNonNull(minInclusive, "Min value must not be null");
		Objects.requireNonNull(maxInclusive, "Max value must not be null");
		return new RangeCodec<>(this.name, minInclusive, maxInclusive, this.converter, this.stringEncoder, this.stringDecoder) {
			@Override
			protected @NotNull <R> Result<R> encodeNumber(@NotNull TypeProvider<R> provider, @NotNull C value) {
				return RangeCodec.this.encodeNumber(provider, value);
			}
			
			@Override
			protected @NotNull <R> Result<C> decodeNumber(@NotNull TypeProvider<R> provider, @NotNull R value) {
				return RangeCodec.this.decodeNumber(provider, value);
			}
		};
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RangeCodec<?> that)) return false;
		
		if (!this.name.equals(that.name)) return false;
		if (!this.converter.equals(that.converter)) return false;
		if (!this.stringEncoder.equals(that.stringEncoder)) return false;
		if (!this.stringDecoder.equals(that.stringDecoder)) return false;
		if (!this.minInclusive.equals(that.minInclusive)) return false;
		return this.maxInclusive.equals(that.maxInclusive);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.converter, this.stringEncoder, this.stringDecoder);
	}
	
	@Override
	public String toString() {
		return "Range" + this.name + "Codec[" + this.minInclusive + ".." + this.maxInclusive + "]";
	}
	//endregion
}
