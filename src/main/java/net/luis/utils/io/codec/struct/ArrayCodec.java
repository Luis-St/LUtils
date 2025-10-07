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

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A codec for encoding and decoding arrays of elements.<br>
 * This codec uses another codec to encode and decode the elements of the array.<br>
 * <p>
 *     The array codec can be lengthened to only accept arrays of a certain length range.<br>
 *     The minimum length and maximum length are inclusive.<br>
 *     If the array length is out of range, the codec will return an error during encoding or decoding.
 * </p>
 * <p>
 *     Due to the nature of types and arrays in Java, primitives can not be used in generic types<br>
 *     therefore the primitive array codecs have be predefined in the {@link Codecs} class.
 * </p>
 * By nesting this class, you can create codecs for multidimensional arrays.<br>
 *
 * @apiNote The type ({@link ArrayCodec#type}) is required due to the nature of Java's type/array system.
 *
 * @author Luis-St
 *
 * @param <C> The type of elements in the array
 */
public class ArrayCodec<C> implements Codec<C[]> {
	
	/**
	 * The type of the elements in the array.<br>
	 * @apiNote If this is a nested array codec, this will be the type of the inner elements.<br>
	 * For example, if this codec is a {@code Integer[][]}, the type will be {@code Integer[]}.<br>
	 */
	private final Class<C> type;
	/**
	 * The codec used to encode and decode the elements of the array.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The minimum length of the array (inclusive).<br>
	 */
	private final int minLength;
	/**
	 * The maximum length of the array (inclusive).<br>
	 */
	private final int maxLength;
	
	/**
	 * Constructs a new array codec using the given codec for the elements.<br>
	 * The array codec will accept array of any size.<br>
	 * Do not use this constructor directly, use any of the array factory methods in {@link Codec} instead.<br>
	 *
	 * @param type The type of the elements in the array
	 * @param codec The codec for the elements
	 * @throws NullPointerException If the type or codec is null
	 */
	@ApiStatus.Internal
	public ArrayCodec(@NotNull Class<C> type, @NotNull Codec<C> codec) {
		this(type, codec, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a new array codec using the given codec for the elements and the size range of the array.<br>
	 * Do not use this constructor directly, use any of the array factory methods in {@link Codec} instead.<br>
	 *
	 * @param type The type of the elements in the array
	 * @param codec The codec for the elements
	 * @param minLength The minimum size of the array (inclusive)
	 * @param maxLength The maximum size of the array (inclusive)
	 * @throws NullPointerException If the type or codec is null
	 * @throws IllegalArgumentException If the minimum size is less than zero or greater than the maximum size
	 */
	@ApiStatus.Internal
	public ArrayCodec(@NotNull Class<C> type, @NotNull Codec<C> codec, int minLength, int maxLength) {
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		if (0 > minLength) {
			throw new IllegalArgumentException("Minimum length must be at least 0");
		}
		if (minLength > maxLength) {
			throw new IllegalArgumentException("Minimum length must be less than or equal to maximum length");
		}
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, C @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as array using '" + this + "'");
		}
		if (value.length > this.maxLength || this.minLength > value.length) {
			return Result.error("Array length '" + value.length + "' is out of range: " + this.minLength + ".." + this.maxLength);
		}
		
		List<Result<R>> encoded = Arrays.stream(value).map(v -> this.codec.encodeStart(provider, provider.empty(), v)).toList();
		if (encoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to encode some elements of array '" + Arrays.deepToString(value) + "' using '" + this + "': \n" + encoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		
		List<R> results = encoded.stream().map(Result::resultOrThrow).filter(result -> provider.getEmpty(result).isError()).toList();
		return provider.merge(current, provider.createList(results));
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public @NotNull <R> Result<C[]> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as array using'" + this + "'");
		}
		
		Result<List<R>> decoded = provider.getList(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode array using '" + this + "': " + decoded.errorOrThrow());
		}
		
		List<Result<C>> results = decoded.resultOrThrow().stream().map(v -> this.codec.decodeStart(provider, v)).toList();
		if (results.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to decode some elements of array using '" + this + "': \n" + results.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		if (this.maxLength >= results.size() && results.size() >= this.minLength) {
			Object array = Array.newInstance(this.type, results.size());
			for (int i = 0; i < results.size(); i++) {
				Array.set(array, i, this.type.cast(results.get(i).resultOrThrow()));
			}
			return Result.success((C[]) array);
		}
		return Result.error("Array was decoded successfully but length '" + results.size() + "' is out of range: " + this.minLength + ".." + this.maxLength);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ArrayCodec<?> that)) return false;
		
		if (this.minLength != that.minLength) return false;
		if (this.maxLength != that.maxLength) return false;
		return this.codec.equals(that.codec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.minLength, this.maxLength);
	}
	
	@Override
	public String toString() {
		if (this.minLength == 0 && this.maxLength == Integer.MAX_VALUE) {
			return "ArrayCodec[" + this.codec + "]";
		}
		return "ArrayCodec[" + this.codec + "," + this.minLength + ".." + this.maxLength + "]";
	}
	//endregion
}
