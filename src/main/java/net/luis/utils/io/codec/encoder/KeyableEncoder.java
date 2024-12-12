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

package net.luis.utils.io.codec.encoder;

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

public interface KeyableEncoder<C> extends Encoder<C> {
	
	static <C> @NotNull KeyableEncoder<C> of(@NotNull Encoder<C> encoder, @NotNull Function<C, String> toKey) {
		return new KeyableEncoder<>() {
			
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
				return encoder.encodeStart(provider, current, value);
			}
			
			@Override
			public <R> @NotNull Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C value) {
				return Optional.ofNullable(toKey.apply(value)).map(Result::success).orElseGet(() -> {
					return Result.error("Unable to encode key with codec '" + this + "': Value '" + value + "' could not be converted to a string");
				});
			}
		};
	}
	
	<R> @NotNull Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull C value);
}
