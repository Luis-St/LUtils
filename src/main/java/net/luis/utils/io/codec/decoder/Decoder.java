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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import net.luis.utils.util.result.ResultMappingFunction;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a decoder for a specific type.<br>
 * <p>
 *     The implementation decodes a value of the type specified by the type provider<br>
 *     and returns a result containing the decoded value or an error message.
 * </p>
 * <p>
 *     The decoder also has the functionality to decode keys (string values).<br>
 *     This is useful when decoding data structures like maps or dictionaries.<br>
 *     This is not supported by default and needs to be implemented separately if required.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the value to decode
 */
@FunctionalInterface
public interface Decoder<C> {
	
	/**
	 * Decodes the value of the specified type and returns the decoded value directly.<br>
	 *
	 * @param provider The type provider
	 * @param value The value to decode
	 * @return The decoded value
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the type provider is null
	 * @throws DecoderException If an error occurs during decoding
	 */
	default <R> @UnknownNullability C decode(@NonNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.decode(provider, provider.empty(), value);
	}
	
	/**
	 * Decodes the value of the specified type and returns the decoded value directly.<br>
	 * <p>
	 *     The current value is the full value that is currently decoded.<br>
	 *     In the most cases this value should be equal to {@link TypeProvider#empty()}.<br>
	 *     In the case of decoding a component that is part of a bigger structure, the current value should be the structure.
	 * </p>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param value The value to decode
	 * @param <R> The type to decode from
	 * @return The decoded value
	 * @throws NullPointerException If the type provider or the current value is null
	 * @throws DecoderException If an error occurs during decoding
	 */
	default <R> @UnknownNullability C decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		return this.decodeStart(provider, current, value).resultOrThrow(DecoderException::new);
	}
	
	/**
	 * Decodes the value of the specified type and returns the result.<br>
	 * The result contains the decoded value or an error message.<br>
	 * <p>
	 *     The current value is the full value that is currently decoded.<br>
	 *     In the most cases this value should be equal to {@link TypeProvider#empty()}.<br>
	 *     In the case of decoding a component that is part of a bigger structure, the current value should be the structure:
	 * </p>
	 * <p>
	 *     For example:
	 * </p>
	 * <ul>
	 *     <li>If decoding a single integer value, the current value should be {@link TypeProvider#empty()}.</li>
	 *     <li>If decoding an element of an array, the current value should be the full array.</li>
	 *     <li>If decoding a value of a map, the current value should be the full map.</li>
	 * </ul>
	 *
	 * @param provider The type provider
	 * @param value The value to decode
	 * @return The result of the decoding
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the type provider or the current value is null
	 */
	<R> @NonNull Result<C> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value);
	
	/**
	 * Decodes a key to a value of the specified type and returns the result.<br>
	 * The result contains the decoded key or an error message.<br>
	 *
	 * @param key The key to decode
	 * @return The result
	 * @throws NullPointerException If the key is null
	 */
	default @NonNull Result<C> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key to decode must not be null");
		return Result.error("Decoding keys is not supported by this decoder");
	}
	
	/**
	 * Maps the type of the decoded value to another type.<br>
	 * The mapping function is applied to the decoded result of {@link #decodeStart(TypeProvider, Object, Object)}.<br>
	 * The mapping function returns a result containing the mapped value or an error message if the mapping process fails.<br>
	 *
	 * @param function The mapping function
	 * @return The mapped decoder
	 * @param <O> The type to map to
	 * @throws NullPointerException If the mapping function is null
	 */
	default <O> @NonNull Decoder<O> mapDecoder(@NonNull ResultMappingFunction<C, O> function) {
		Objects.requireNonNull(function, "Decode mapping function must not be null");
		return new Decoder<>() {
			@Override
			public <R> @NonNull Result<O> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				
				return function.apply(Decoder.this.decodeStart(provider, current, value));
			}
			
			@Override
			public @NonNull Result<O> decodeKey(@NonNull String key) {
				Objects.requireNonNull(key, "Key to decode must not be null");
				return function.apply(Decoder.this.decodeKey(key));
			}
		};
	}
}
