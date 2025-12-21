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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.DayOfWeek;
import java.util.Objects;

/**
 * Internal codec implementation for day of week.<br>
 * Uses the string name as an internal representation.<br>
 *
 * @author Luis-St
 */
public class DayOfWeekCodec extends AbstractCodec<DayOfWeek, Object> {
	
	/**
	 * Constructs a new day of week codec.<br>
	 */
	public DayOfWeekCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable DayOfWeek value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as day of week using '" + this + "'");
		}
		return provider.createString(value.name());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull DayOfWeek key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.name());
	}
	
	@Override
	public <R> @NonNull Result<DayOfWeek> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as day of week using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(DayOfWeek.valueOf(string));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode day of week '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<DayOfWeek> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(DayOfWeek.valueOf(key));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode key '" + key + "' as day of week using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "DayOfWeekCodec";
	}
}
