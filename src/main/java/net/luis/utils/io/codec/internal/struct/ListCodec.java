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

package net.luis.utils.io.codec.internal.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A codec for encoding and decoding lists of elements.<br>
 * This codec uses another codec to encode and decode the elements of the list.<br>
 * <p>
 *     The list codec can be sized to only accept lists of a certain size range.<br>
 *     The minimum size and maximum size are inclusive.<br>
 *     If the list size is out of range, the codec will return an error during encoding or decoding.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of elements in the list
 */
@ApiStatus.Internal
public class ListCodec<C> implements Codec<List<C>> {
	
	/**
	 * The codec used to encode and decode the elements of the list.<br>
	 */
	private final Codec<C> codec;
	/**
	 * The minimum size of the list (inclusive).<br>
	 */
	private final int minSize;
	/**
	 * The maximum size of the list (inclusive).<br>
	 */
	private final int maxSize;
	
	/**
	 * Constructs a new list codec using the given codec for the elements.<br>
	 * The list codec will accept lists of any size.<br>
	 *
	 * @param codec The codec for the elements
	 * @throws NullPointerException If the codec is null
	 */
	public ListCodec(@NotNull Codec<C> codec) {
		this(codec, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Constructs a new list codec using the given codec for the elements and the size range of the list.<br>
	 *
	 * @param codec The codec for the elements
	 * @param minSize The minimum size of the list (inclusive)
	 * @param maxSize The maximum size of the list (inclusive)
	 * @throws NullPointerException If the codec is null
	 * @throws IllegalArgumentException If the minimum size is less than zero or greater than the maximum size
	 */
	public ListCodec(@NotNull Codec<C> codec, int minSize, int maxSize) {
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
		
		if (0 > minSize) {
			throw new IllegalArgumentException("Minimum size must be at least 0");
		}
		if (minSize > maxSize) {
			throw new IllegalArgumentException("Minimum size must be less than or equal to maximum size");
		}
		
		this.minSize = minSize;
		this.maxSize = maxSize;
	}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable List<C> value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as list using '" + this + "'");
		}
		if (value.size() > this.maxSize || this.minSize > value.size()) {
			return Result.error("List size '" + value.size() + "' is out of range: " + this.minSize + ".." + this.maxSize);
		}
		
		List<Result<R>> encoded = value.stream().map(v -> this.codec.encodeStart(provider, provider.empty(), v)).toList();
		if (encoded.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to encode some elements of list '" + value + "' using '" + this + "': \n" + encoded.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		
		List<R> results = encoded.stream().map(Result::resultOrThrow).filter(result -> provider.getEmpty(result).isError()).toList();
		return provider.merge(current, provider.createList(results));
	}
	
	@Override
	public <R> @NotNull Result<List<C>> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as list using'" + this + "'");
		}
		
		Result<List<R>> decoded = provider.getList(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode list using '" + this + "': " + decoded.errorOrThrow());
		}
		
		List<Result<C>> results = decoded.resultOrThrow().stream().map(v -> this.codec.decodeStart(provider, v)).toList();
		if (results.stream().anyMatch(Result::isError)) {
			return Result.error("Unable to decode some elements of list using '" + this + "': \n" + results.stream().filter(Result::isError).map(Result::errorOrThrow).collect(Collectors.joining("\n")));
		}
		
		if (this.maxSize >= results.size() && results.size() >= this.minSize) {
			return Result.success(results.stream().map(Result::resultOrThrow).collect(Collectors.toList()));
		}
		return Result.error("List was decoded successfully but size '" + results.size() + "' is out of range: " + this.minSize + ".." + this.maxSize);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ListCodec<?> listCodec)) return false;
		
		if (this.minSize != listCodec.minSize) return false;
		if (this.maxSize != listCodec.maxSize) return false;
		return this.codec.equals(listCodec.codec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codec, this.minSize, this.maxSize);
	}
	
	@Override
	public String toString() {
		if (this.minSize == 0 && this.maxSize == Integer.MAX_VALUE) {
			return "ListCodec[" + this.codec + "]";
		}
		return "ListCodec[" + this.codec + "," + this.minSize + ".." + this.maxSize + "]";
	}
	//endregion
}
