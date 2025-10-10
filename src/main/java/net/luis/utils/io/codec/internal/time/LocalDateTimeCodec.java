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

package net.luis.utils.io.codec.internal.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Internal codec implementation for local date times.<br>
 * Uses the ISO-8601 string format as an internal representation.<br>
 *
 * @author Luis-St
 */
@ApiStatus.Internal
public class LocalDateTimeCodec implements Codec<LocalDateTime> {
	
	private final Codec<String> internalCodec = Codecs.STRING;
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable LocalDateTime value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as local date time using '" + this + "'");
		}
		return provider.createString(value.toString());
	}
	
	@Override
	public @NotNull <R> Result<LocalDateTime> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as local date time using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(LocalDateTime.parse(string));
		} catch (DateTimeParseException e) {
			return Result.error("Unable to decode local date time '" + string + "' using '" + this + "': Unable to parse local date time: " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "LocalDateTimeCodec";
	}
}
