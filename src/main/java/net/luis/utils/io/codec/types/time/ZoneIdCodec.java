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

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;

/**
 * Internal codec implementation for zone ids.<br>
 * Uses the string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class ZoneIdCodec extends AbstractCodec<ZoneId, Object> {
	
	/**
	 * Constructs a new zone id codec.<br>
	 */
	public ZoneIdCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable ZoneId value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as zone id using '" + this + "'");
		}
		return provider.createString(value.getId());
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull ZoneId key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.getId());
	}
	
	@Override
	public <R> @NonNull Result<ZoneId> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as zone id using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(ZoneId.of(string));
		} catch (ZoneRulesException e) {
			return Result.error("Unable to decode zone id '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NonNull Result<ZoneId> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(ZoneId.of(key));
		} catch (ZoneRulesException e) {
			return Result.error("Unable to decode key '" + key + "' as zone id using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "ZoneIdCodec";
	}
}
