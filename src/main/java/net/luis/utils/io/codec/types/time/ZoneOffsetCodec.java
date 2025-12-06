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

package net.luis.utils.io.codec.types.time;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Internal codec implementation for zone offsets.<br>
 * Uses the string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class ZoneOffsetCodec extends AbstractCodec<ZoneOffset, Object> {

	/**
	 * Constructs a new zone offset codec.<br>
	 */
	public ZoneOffsetCodec() {}

	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable ZoneOffset value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");

		if (value == null) {
			return Result.error("Unable to encode null as zone offset using '" + this + "'");
		}
		return provider.createString(value.getId());
	}

	@Override
	public @NotNull Result<String> encodeKey(@NotNull ZoneOffset key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.getId());
	}

	@Override
	public <R> @NotNull Result<ZoneOffset> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as zone offset using '" + this + "'");
		}

		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}

		String string = result.resultOrThrow();
		try {
			return Result.success(ZoneOffset.of(string));
		} catch (DateTimeException e) {
			return Result.error("Unable to decode zone offset '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public @NotNull Result<ZoneOffset> decodeKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");

		try {
			return Result.success(ZoneOffset.of(key));
		} catch (DateTimeException e) {
			return Result.error("Unable to decode key '" + key + "' as zone offset using '" + this + "': " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "ZoneOffsetCodec";
	}
}
