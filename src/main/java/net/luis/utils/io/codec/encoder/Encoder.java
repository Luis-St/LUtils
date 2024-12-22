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

import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public interface Encoder<C> {
	
	default <R> @NotNull R encode(@NotNull TypeProvider<R> provider, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		return this.encode(provider, provider.empty(), value);
	}
	
	default <R> @NotNull R encode(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		return this.encodeStart(provider, current, value).orThrow();
	}
	
	<R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable C value);
	
	default <O> @NotNull Encoder<O> mapEncoder(@NotNull Function<O, C> to) {
		Objects.requireNonNull(to, "Encode mapping function must not be null");
		return new Encoder<>() {
			@Override
			public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable O value) {
				Objects.requireNonNull(provider, "Type provider must not be null");
				Objects.requireNonNull(current, "Current value must not be null");
				return Encoder.this.encodeStart(provider, current, to.apply(value));
			}
		};
	}
}
