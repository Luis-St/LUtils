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
 * A codec for encoding and decoding number values of a certain range.<br>
 * This codec uses a converter to convert between numbers and the range type.<br>
 * The codec can be configured by the following methods:<br>
 * <ul>
 *     <li>{@link #positive()} to only accept positive numbers</li>
 *     <li>{@link #positiveOrZero()} to accept positive numbers and zero</li>
 *     <li>{@link #negative()} to only accept negative numbers</li>
 *     <li>{@link #negativeOrZero()} to accept negative numbers and zero</li>
 *     <li>{@link #atLeast(Number)} to only accept numbers greater or equal to the given number</li>
 *     <li>{@link #atMost(Number)} to only accept numbers smaller or equal to the given number</li>
 *     <li>{@link #range(Number, Number)} to only accept numbers in the given range (inclusive)</li>
 * </ul>
 * <p>
 *     This codec is keyable, so it can be used as a key in a map codec.<br>
 *     The key is encoded as a string using the given string encoder and decoder.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the range
 */
public abstract class RangeCodec<C extends Number & Comparable<C>> implements KeyableCodec<C> {
	
	/**
	 * The name of the underlying range type.<br>
	 */
	private final String name;
	/**
	 * The minimum value of the range (inclusive).<br>
	 */
	private final C minInclusive;
	/**
	 * The maximum value of the range (inclusive).<br>
	 */
	private final C maxInclusive;
	/**
	 * The converter used to convert between numbers and the range type.<br>
	 * The converter is used to avoid casting numbers to generics.<br>
	 */
	private final Function<Number, C> converter;
	/**
	 * The function used to encode the range type as a string.<br>
	 */
	private final Function<C, String> stringEncoder;
	/**
	 * The function used to decode the range type from a string.<br>
	 */
	private final Function<String, C> stringDecoder;
	
	/**
	 * Constructs a new range codec using the given name, minimum and maximum value, converter and decoder.<br>
	 *
	 * @param name The name of the range type
	 * @param min The minimum value of the range (inclusive)
	 * @param max The maximum value of the range (inclusive)
	 * @param converter The converter used to convert between numbers and the range type
	 * @param decoder The decoder used to decode the range type from a string
	 * @throws NullPointerException If the name, minimum value, maximum value, converter or decoder is null
	 */
	protected RangeCodec(@NotNull String name, @NotNull C min, @NotNull C max, @NotNull Function<Number, C> converter, @NotNull Function<String, C> decoder) {
		this(name, min, max, converter, String::valueOf, decoder);
	}
	
	/**
	 * Constructs a new range codec using the given name, minimum and maximum value, converter, encoder and decoder.<br>
	 *
	 * @param name The name of the range type
	 * @param min The minimum value of the range (inclusive)
	 * @param max The maximum value of the range (inclusive)
	 * @param converter The converter used to convert between numbers and the range type
	 * @param stringEncoder The encoder used to encode the range type as a string
	 * @param stringDecoder The decoder used to decode the range type from a string
	 * @throws NullPointerException If the name, minimum value, maximum value, converter, encoder or decoder is null
	 */
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
	
	/**
	 * Encodes the given number value using the given type provider.<br>
	 * The result contains the encoded value or an error message if the encoding failed.<br>
	 * @param provider The type provider
	 * @param value The number value
	 * @return The result of the encoding process
	 * @param <R> The type of the encoded value
	 * @throws NullPointerException If the type provider or value is null
	 */
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
	
	/**
	 * Decodes the given value using the given type provider.<br>
	 * The result contains the decoded value or an error message if the decoding failed.<br>
	 * @param provider The type provider
	 * @param value The value
	 * @return The result of the decoding process
	 * @param <R> The type of the decoded value
	 * @throws NullPointerException If the type provider or value is null
	 */
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
	
	/**
	 * Returns a new range codec that only accepts positive numbers.<br>
	 * @return The new range codec
	 */
	public @NotNull RangeCodec<C> positive() {
		return this.range(this.converter.apply(1), this.maxInclusive);
	}
	
	/**
	 * Returns a new range codec that accepts positive numbers and zero.<br>
	 * @return The new range codec
	 */
	public @NotNull RangeCodec<C> positiveOrZero() {
		return this.range(this.converter.apply(0), this.maxInclusive);
	}
	
	/**
	 * Returns a new range codec that only accepts negative numbers.<br>
	 * @return The new range codec
	 */
	public @NotNull RangeCodec<C> negative() {
		return this.range(this.minInclusive, this.converter.apply(-1));
	}
	
	/**
	 * Returns a new range codec that accepts negative numbers and zero.<br>
	 * @return The new range codec
	 */
	public @NotNull RangeCodec<C> negativeOrZero() {
		return this.range(this.minInclusive, this.converter.apply(0));
	}
	
	/**
	 * Returns a new range codec that only accepts numbers greater or equal to the given number.<br>
	 *
	 * @param min The minimum value
	 * @return The new range codec
	 * @throws NullPointerException If the minimum value is null
	 */
	public @NotNull RangeCodec<C> atLeast(@NotNull C min) {
		Objects.requireNonNull(min, "Min value must not be null");
		return this.range(min, this.maxInclusive);
	}
	
	/**
	 * Returns a new range codec that only accepts numbers smaller or equal to the given number.<br>
	 *
	 * @param max The maximum value
	 * @return The new range codec
	 * @throws NullPointerException If the maximum value is null
	 */
	public @NotNull RangeCodec<C> atMost(@NotNull C max) {
		Objects.requireNonNull(max, "Max value must not be null");
		return this.range(this.minInclusive, max);
	}
	
	/**
	 * Returns a new range codec that only accepts numbers in the given range (inclusive).<br>
	 *
	 * @param minInclusive The minimum value
	 * @param maxInclusive The maximum value
	 * @return The new range codec
	 * @throws NullPointerException If the minimum or maximum value is null
	 */
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
