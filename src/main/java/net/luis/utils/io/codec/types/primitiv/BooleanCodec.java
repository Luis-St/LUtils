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

package net.luis.utils.io.codec.types.primitiv;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for booleans.<br>
 *
 * @author Luis-St
 */
public class BooleanCodec extends AbstractCodec<Boolean, Object> {
	
	/**
	 * Constructs a new boolean codec.<br>
	 */
	public BooleanCodec() {}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Boolean value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as boolean using '" + this + "'");
		}
		return provider.createBoolean(value);
	}
	
	@Override
	public @NotNull Result<String> encodeKey(@NotNull Boolean key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.toString());
	}
	
	@Override
	public <R> @NotNull Result<Boolean> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as boolean using '" + this + "'");
		}
		
		return provider.getBoolean(value);
	}
	
	@Override
	public @NotNull Result<Boolean> decodeKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		if ("true".equals(key)) {
			return Result.success(true);
		} else if ("false".equals(key)) {
			return Result.success(false);
		}
		return Result.error("Unable to decode key '" + key + "' as boolean using '" + this + "'");
	}
	
	@Override
	public String toString() {
		return "BooleanCodec";
	}
}
