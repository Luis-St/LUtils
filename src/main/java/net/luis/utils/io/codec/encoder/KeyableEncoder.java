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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.codec.ResultingFunction;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Extension of {@link Encoder} that allows encoding keys.<br>
 * A key is a string used to identify a value in a data structure.<br>
 *
 * @see Encoder
 *
 * @author Luis-St
 *
 * @param <C> The type of the value to encode
 */
public interface KeyableEncoder<C> extends Encoder<C> {
	
	/**
	 * Creates a new keyable encoder from the specified encoder and key encoder.<br>
	 * The key encoder function converts a key to a string.<br>
	 *
	 * @param encoder The encoder
	 * @param keyEncoder The key encoder function
	 * @return The keyable encoder
	 * @param <C> The type of the value to encode
	 * @throws NullPointerException If the encoder or the key encoder function is null
	 */
	static <C> @NotNull KeyableEncoder<C> of(@NotNull Encoder<C> encoder, @NotNull ResultingFunction<C, String> keyEncoder) {
		Objects.requireNonNull(encoder, "Encoder must not be null");
		Objects.requireNonNull(keyEncoder, "Key encoder must not be null");
		return new KeyableEncoder<>() {
			
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				return encoder.encodeStart(provider, current, value);
			}
			
			@Override
			public <R> @NotNull Result<String> encodeKey(@Nullable TypeProvider<R> provider, @NotNull C key) {
				Objects.requireNonNull(key, "Key must not be null");
				return keyEncoder.apply(key);
			}
		};
	}
	
	/**
	 * Encodes the key of the specified type and returns the encoded key as a result.<br>
	 * The result contains the encoded key or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param key The key to encode
	 * @return The result
	 * @param <R> The type to encode to
	 * @throws NullPointerException If the type provider or the key is null
	 */
	<R> @NotNull Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C key);
}
