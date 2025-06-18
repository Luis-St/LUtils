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

package net.luis.utils.io.codec.encoder;

import net.luis.utils.io.codec.ResultingFunction;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an encoder for a specific type.<br>
 * The implementation encodes a value of the type specified by the type provider<br>
 * and returns a result containing the encoded value or an error message.<br>
 *
 * @author Luis-St
 *
 * @param <C> The type the encoder is for
 */
@FunctionalInterface
public interface Encoder<C> {
	
	/**
	 * Encodes the value of the specified type and returns the encoded value directly.<br>
	 *
	 * @param provider The type provider
	 * @param value The value to encode
	 * @return The encoded value
	 * @param <R> The type to encode to
	 * @throws NullPointerException If the type provider is null
	 * @throws EncoderException If an error occurs during encoding
	 */
	default <R> @NotNull R encode(@NotNull TypeProvider<R> provider, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.encode(provider, provider.empty(), value);
	}
	
	/**
	 * Encodes the value of the specified type and returns the encoded value directly.<br>
	 * <p>
	 *     The current value is the value that is currently encoded.<br>
	 *     In the most cases this value should be equal to {@link TypeProvider#empty()}.<br>
	 *     In the case of encoding a value that is part of a bigger structure, the current value should be the structure.
	 * </p>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param value The value to encode
	 * @return The encoded value
	 * @param <R> The type to encode to
	 * @throws NullPointerException If the type provider is null
	 * @throws EncoderException If an error occurs during encoding
	 * @see #encodeStart(TypeProvider, Object, Object)
	 */
	default <R> @NotNull R encode(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		return this.encodeStart(provider, current, value).orThrow(EncoderException::new);
	}
	
	/**
	 * Encodes the value of the specified type and returns the result.<br>
	 * The result contains the encoded value or an error message.<br>
	 * <p>
	 *     The current value is the value that is currently encoded.<br>
	 *     In the most cases this value should be equal to {@link TypeProvider#empty()}.<br>
	 *     In the case of encoding a value that is part of a bigger structure, the current value should be the structure.
	 * </p>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param value The value to encode
	 * @return The result
	 * @param <R> The type to encode to
	 * @throws NullPointerException If the type provider is null
	 */
	<R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value);
	
	/**
	 * Maps the type of the encoded value to another type.<br>
	 * The mapping function is applied to the raw input value of {@link #encodeStart(TypeProvider, Object, Object)}.<br>
	 * The mapping function returns a result containing the mapped value or an error message if the mapping process fails.<br>
	 * The mapping function is not allowed to throw any exceptions.<br>
	 *
	 * @param to The mapping function
	 * @return The mapped encoder
	 * @param <O> The type to map to
	 * @throws NullPointerException If the mapping function is null
	 */
	default <O> @NotNull Encoder<O> mapEncoder(@NotNull ResultingFunction<O, C> to) {
		Objects.requireNonNull(to, "Encode mapping function must not be null");
		return new Encoder<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				Result<C> result = to.apply(value);
				if (result.isError()) {
					return Result.error("Failed to map value to encode: " + result.errorOrThrow());
				}
				return Encoder.this.encodeStart(provider, current, result.orThrow());
			}
		};
	}
}
