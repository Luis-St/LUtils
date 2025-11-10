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

package net.luis.utils.io.codec;

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A codec that groups multiple codecs together to encode and decode a single object.<br>
 * This codec uses a factory function to create the final object from the encoded components.<br>
 * It is used to combine multiple codecs into one, allowing for complex objects to be encoded and decoded easily.<br>
 *
 * @see CodecBuilder
 * @see CodecCreator
 *
 * @author Luis-St
 *
 * @param <O> The type of the object that this codec encodes and decodes
 */
public class CodecGroup<O> implements Codec<O> {
	
	/**
	 * The list of codecs that are grouped together in this codec.<br>
	 * Each codec is responsible for encoding and decoding a specific component of the final object.<br>
	 */
	private final List<ConfiguredCodec<?, O>> codecs;
	/**
	 * The factory function that creates the final object from the encoded components.<br>
	 * This function takes a list of decoded components and returns the final object.<br>
	 */
	private final ResultingFunction<List<Object>, O> factory;
	
	/**
	 * Constructs a new codec group using the given list of codecs and factory function.<br>
	 * This constructor is intended for internal use only and should not be used directly.<br>
	 *
	 * @param codecs The list of codecs to group together
	 * @param factory The factory function to create the final object from the encoded components
	 * @throws NullPointerException If the codecs list or factory function is null
	 */
	@ApiStatus.Internal
	public CodecGroup(@NotNull List<ConfiguredCodec<?, O>> codecs, @NotNull ResultingFunction<List<Object>, O> factory) {
		this.codecs = Objects.requireNonNull(codecs, "Codecs list must not be null");
		this.factory = Objects.requireNonNull(factory, "Factory function must not be null");
	}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to encode null value with '" + this);
		}
		
		Result<R> mergedMap = provider.merge(current, provider.createMap());
		if (mergedMap.isError()) {
			return Result.error("Unable to encode '" + value + "' with '" + this + "': " + mergedMap.errorOrThrow());
		}
		R map = mergedMap.resultOrThrow();
		
		for (int i = 0; i < this.codecs.size(); i++) {
			ConfiguredCodec<?, O> codec = this.codecs.get(i);
			Objects.requireNonNull(codec, "Codec of component " + i + " must not be null");
			
			Result<R> encoded = codec.encodeStart(provider, map, value);
			if (encoded.isError()) {
				return Result.error("Unable to encode component of '" + value + "' with '" + codec + "': " + encoded.errorOrThrow());
			}
		}
		
		return Result.success(map);
	}
	
	@Override
	public @NotNull <R> Result<O> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value using '" + this);
		}
		Result<Map<String, R>> decodedMap = provider.getMap(value);
		if (decodedMap.isError()) {
			return Result.error("Unable to decode '" + value + "' using '" + this + "': " + decodedMap.errorOrThrow());
		}
		
		List<Object> components = Lists.newArrayListWithCapacity(this.codecs.size());
		for (int i = 0; i < this.codecs.size(); i++) {
			ConfiguredCodec<?, O> codec = this.codecs.get(i);
			Objects.requireNonNull(codec, "Codec of component " + i + " must not be null");
			
			Result<?> decoded = codec.decodeStart(provider, value);
			if (decoded.isError()) {
				return Result.error("Unable to decode component of '" + value + "' using '" + codec + "': " + decoded.errorOrThrow());
			}
			
			components.add(decoded.resultOrThrow());
		}
		
		return this.factory.apply(components);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CodecGroup<?> that)) return false;
		
		if (!this.codecs.equals(that.codecs)) return false;
		return this.factory.equals(that.factory);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.codecs, this.factory);
	}
	
	@Override
	public String toString() {
		return this.codecs.stream().map(ConfiguredCodec::toString).collect(Collectors.joining(", ", "GroupCodec[", "]"));
	}
	//endregion
}
