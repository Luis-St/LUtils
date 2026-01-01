/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.*;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A codec for encoding and decoding arrays of elements.<br>
 * This codec uses another codec to encode and decode the elements of the array.<br>
 * <p>
 *     Due to the nature of types and arrays in Java, primitives can not be used in generic types<br>
 *     therefore the primitive array codecs have be predefined in the {@link Codecs} class.
 * </p>
 * By nesting this class, you can create codecs for multidimensional arrays.<br>
 * <p>
 *     <strong>Note:</strong><br>
 *     The type ({@link ArrayCodec#type}) is required due to the nature of Java's type/array system.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of elements in the array
 */
public class ArrayCodec<C> extends AbstractCodec<C[], Object> {
	
	/**
	 * The type of the elements in the array.<br>
	 * <p>
	 *     If this is a nested array codec, this will be the type of the inner elements.<br>
	 *     For example, if this codec is a {@code Integer[][]}, the type will be {@code Integer[]}.
	 * </p>
	 */
	private final Class<C> type;
	/**
	 * The codec used to encode and decode the elements of the array.<br>
	 */
	private final Codec<C> codec;
	
	/**
	 *  Constructs a new array codec using the given codec for the elements.<br>
	 *
	 * @param type The type of the elements in the array
	 * @param codec The codec for the elements
	 * @throws NullPointerException If the type or codec is null
	 */
	public ArrayCodec(@NonNull Class<C> type, @NonNull Codec<C> codec) {
		this.type = Objects.requireNonNull(type, "Type must not be null");
		this.codec = Objects.requireNonNull(codec, "Codec must not be null");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @NonNull Class<C[]> getType() {
		return (Class<C[]>) Array.newInstance(this.type, 0).getClass();
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, C @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null value as array using '" + this + "'");
		}
		
		List<R> elements = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		for (int i = 0; i < value.length; i++) {
			Result<R> result = this.codec.encodeStart(provider, provider.empty(), value[i]);
			
			if (result.hasValue()) {
				R encodedValue = result.resultOrThrow();
				if (provider.getEmpty(encodedValue).isError()) {
					elements.add(encodedValue);
				}
			}
			if (result.hasError()) {
				errors.add("Index " + i + ": " + result.errorOrThrow());
			}
		}
		
		Result<R> merged = provider.merge(current, provider.createList(elements));
		if (merged.isError()) {
			return Result.error(merged.errorOrThrow());
		}
		if (errors.isEmpty()) {
			return merged;
		}
		return Result.partial(merged.resultOrThrow(), "Encoded " + elements.size() + " of " + value.length + " elements successfully:", errors);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "DuplicatedCode" })
	public <R> @NonNull Result<C[]> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as array using'" + this + "'");
		}
		
		Result<List<R>> decoded = provider.getList(value);
		if (decoded.isError()) {
			return Result.error("Unable to decode array using '" + this + "': " + decoded.errorOrThrow());
		}
		List<Result<C>> results = decoded.resultOrThrow().stream().map(element -> this.codec.decodeStart(provider, value, element)).toList();
		
		List<C> elements = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		for (int i = 0; i < results.size(); i++) {
			Result<C> result = results.get(i);
			if (result.hasValue()) {
				elements.add(result.resultOrThrow());
			}
			if (result.hasError()) {
				errors.add("Index " + i + ": " + result.errorOrThrow());
			}
		}
		
		Object array = Array.newInstance(this.type, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Array.set(array, i, this.type.cast(elements.get(i)));
		}
		
		if (errors.isEmpty()) {
			return Result.success((C[]) array);
		}
		return Result.partial((C[]) array, "Decoded " + elements.size() + " of " + results.size() + " elements successfully:", errors);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ArrayCodec<?> that)) return false;
		
		if (!this.type.equals(that.type)) return false;
		return this.codec.equals(that.codec);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.codec);
	}
	
	@Override
	public String toString() {
		return "ArrayCodec[" + this.codec + "]";
	}
	//endregion
}
