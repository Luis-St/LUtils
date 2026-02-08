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

package net.luis.utils.io.codec.encoder;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an encoder for a specific type.<br>
 * <p>
 *     The implementation encodes a value of the type specified by the type provider and returns the encoded value or throws an error.
 * </p>
 * <p>
 *     The encoder also has the functionality to encode keys (string values).<br>
 *     This is useful when encoding data structures like maps or dictionaries.<br>
 *     This is not supported by default and needs to be implemented separately if required.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The type of the value to encode
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
	default <R> @UnknownNullability R encode(@NonNull TypeProvider<R> provider, @Nullable C value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.encode(provider, provider.empty(), value);
	}
	
	/**
	 * Encodes the value of the specified type and returns the encoded value.<br>
	 * <p>
	 *     The current value is the value that is currently encoded.<br>
	 *     In the most cases this value should be equal to {@link TypeProvider#empty()}.<br>
	 *     In the case of encoding a component that is part of a bigger structure, the current value should be the structure.
	 * </p>
	 *
	 * @param provider The type provider
	 * @param current The current value
	 * @param value The value to encode
	 * @return The encoded value
	 * @param <R> The type to encode to
	 * @throws NullPointerException If the type provider is null
	 * @throws EncoderException If an error occurs during encoding
	 */
	<R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable C value) throws EncoderException;
	
	/**
	 * Encodes the key of the specified type and returns the encoded key.<br>
	 *
	 * @param key The key to encode
	 * @return The encoded key
	 * @throws NullPointerException If the key is null
	 * @throws EncoderException If an error occurs during encoding
	 */
	default @NonNull String encodeKey(@NonNull C key) throws EncoderException {
		Objects.requireNonNull(key, "Key to encode must not be null");
		throw new EncoderException("Encoding keys is not supported by this encoder", this);
	}
	
	/**
	 * Maps the type of the encoded value to another type.<br>
	 * The mapping function is applied to the input value of {@link #encode(TypeProvider, Object, Object)}.<br>
	 * The mapping function returns the mapped value or throws an {@link EncoderException} if the mapping fails.<br>
	 *
	 * @param function The mapping function
	 * @return The mapped encoder
	 * @param <O> The type to map to
	 * @throws NullPointerException If the mapping function is null
	 */
	default <O> @NonNull Encoder<O> mapEncoder(@NonNull ThrowableFunction<O, C, EncoderException> function) {
		Objects.requireNonNull(function, "Encode mapping function must not be null");
		
		return new Encoder<>() {
			@Override
			public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable O value) throws EncoderException {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				
				return Encoder.this.encode(provider, current, function.apply(value));
			}
			
			@Override
			public @NonNull String encodeKey(@NonNull O key) throws EncoderException {
				Objects.requireNonNull(key, "Key to encode must not be null");
				return Encoder.this.encodeKey(function.apply(key));
			}
		};
	}
}
