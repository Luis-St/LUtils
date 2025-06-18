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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.ResultMappingFunction;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a decoder for a specific type.<br>
 * The implementation decodes a value of the type specified by the type provider<br>
 * and returns a result containing the decoded value or an error message.<br>
 *
 * @author Luis-St
 *
 * @param <C> The type the decoder is for
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
	 * @see #decodeStart(TypeProvider, Object)
	 */
	default <R> @NotNull C decode(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.decodeStart(provider, value).orThrow(DecoderException::new);
	}
	
	/**
	 * Decodes the value of the specified type and returns the result.<br>
	 * The result contains the decoded value or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param value The value to decode
	 * @return The result
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the type provider is null
	 */
	<R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value);
	
	/**
	 * Maps the type of the decoded value to another type.<br>
	 * The mapping function is applied to the decoded result of the decoding process.<br>
	 * The mapping function returns a result containing the mapped value or an error message if the mapping process fails.<br>
	 *
	 * @param from The mapping function
	 * @return The mapped decoder
	 * @param <O> The type to map to
	 * @throws NullPointerException If the mapping function is null
	 */
	default <O> @NotNull Decoder<O> mapDecoder(@NotNull ResultMappingFunction<C, O> from) {
		Objects.requireNonNull(from, "Decode mapping function must not be null");
		return new Decoder<>() {
			@Override
			public <R> @NotNull Result<O> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				return from.apply(Decoder.this.decodeStart(provider, value));
			}
		};
	}
}
