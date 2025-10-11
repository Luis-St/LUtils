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

package net.luis.utils.io.codec.internal;

import net.luis.utils.io.codec.KeyableCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Internal codec implementation for UUIDs.<br>
 * Uses the standard string representation as an internal representation.<br>
 *
 * @author Luis-St
 */
@ApiStatus.Internal
public class UUIDCodec implements KeyableCodec<UUID> {
	
	/**
	 * Constructs a new UUID codec.<br>
	 */
	public UUIDCodec() {}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable UUID value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as UUID using '" + this + "'");
		}
		return provider.createString(value.toString());
	}
	
	@Override
	public @NotNull <R> Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull UUID key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		return Result.success(key.toString());
	}
	
	@Override
	public @NotNull <R> Result<UUID> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as UUID using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(UUID.fromString(string));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode UUID '" + string + "' using '" + this + "': Unable to parse UUID: " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull <R> Result<UUID> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(UUID.fromString(key));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode UUID from key '" + key + "' using'" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "UUIDCodec";
	}
}
