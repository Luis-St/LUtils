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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Extension of {@link Decoder} that allows decoding keys.<br>
 * A key is a string used to identify a value in a data structure.<br>
 *
 * @see Decoder
 *
 * @author Luis-St
 *
 * @param <C> The type of the value to decode
 */
public interface KeyableDecoder<C> extends Decoder<C> {
	
	/**
	 * Creates a new keyable decoder from the specified decoder and key decoder.<br>
	 * The key decoder function converts a key to a value.<br>
	 * If the key decoder is not able to handle a key, it can simply return null.<br>
	 *
	 * @param decoder The decoder
	 * @param keyDecoder The key decoder function
	 * @return The keyable decoder
	 * @param <C> The type of the value to decode
	 * @throws NullPointerException If the decoder or the key decoder function is null
	 */
	static <C> @NotNull KeyableDecoder<C> of(@NotNull Decoder<C> decoder, @NotNull ThrowableFunction<String, @Nullable C, ? extends Exception> keyDecoder) {
		Objects.requireNonNull(decoder, "Decoder must not be null");
		Objects.requireNonNull(keyDecoder, "Key decoder must not be null");
		return new KeyableDecoder<>() {
			
			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				return decoder.decodeStart(provider, value);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeKey(@Nullable TypeProvider<R> provider, @NotNull String key) {
				Objects.requireNonNull(key, "Key must not be null");
				
				try {
					C decodedKey = keyDecoder.apply(key);
					return Optional.ofNullable(decodedKey).map(Result::success).orElseGet(() -> {
						return Result.error("Unable to decode key with codec '" + this + "': Key '" + key + "' could not be converted back to a value");
					});
				} catch (Exception e) {
					return Result.error("Unable to decode key with codec '" + this + "': Key '" + key + "' could not be converted back to a value due to an exception: " + e.getMessage());
				}
			}
		};
	}
	
	/**
	 * Decodes a key to a value of the specified type and returns the result.<br>
	 * The result contains the decoded key or an error message.<br>
	 *
	 * @param provider The type provider
	 * @param key The key to decode
	 * @return The result
	 * @param <R> The type to decode from
	 * @throws NullPointerException If the type provider or the key is null
	 */
	<R> @NotNull Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key);
}
