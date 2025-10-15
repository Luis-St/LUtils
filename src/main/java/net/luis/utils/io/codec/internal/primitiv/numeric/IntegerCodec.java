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

package net.luis.utils.io.codec.internal.primitiv.numeric;

import net.luis.utils.io.codec.KeyableCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for integers.<br>
 *
 * @author Luis-St
 */
public class IntegerCodec implements KeyableCodec<Integer> {
	
	/**
	 * Constructs a new integer codec.<br>
	 */
	public IntegerCodec() {}
	
	@Override
	public @NotNull <R> Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Integer value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as integer using '" + this + "'");
		}
		return provider.createInteger(value);
	}
	
	@Override
	public @NotNull <R> Result<String> encodeKey(@NotNull TypeProvider<R> provider, @NotNull Integer key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		return Result.success(Integer.toString(key));
	}
	
	@Override
	public @NotNull <R> Result<Integer> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as integer using '" + this + "'");
		}
		
		return provider.getInteger(value);
	}
	
	@Override
	public @NotNull <R> Result<Integer> decodeKey(@NotNull TypeProvider<R> provider, @NotNull String key) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(Integer.parseInt(key));
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as integer using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "IntegerCodec";
	}
}
