/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
import net.luis.utils.util.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public interface KeyableDecoder<C> extends Decoder<C> {
	
	static <C> @NotNull KeyableDecoder<C> of(@NotNull Decoder<C> decoder, @NotNull Function<String, @Nullable C> fromKey) {
		return new KeyableDecoder<>() {
			
			@Override
			public <R> @NotNull Result<C> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
				return decoder.decodeStart(provider, value);
			}
			
			@Override
			public <R> @NotNull Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String value) {
				return Optional.ofNullable(fromKey.apply(value)).map(Result::success).orElseGet(() -> {
					return Result.error("Unable to decode key with codec '" + this + "': Key '" + value + "' could not be converted back to a value");
				});
			}
		};
	}
	
	<R> @NotNull Result<C> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String value);
}
