/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.codec.types.primitiv.numeric;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for shorts.<br>
 *
 * @author Luis-St
 */
public class ShortCodec extends AbstractCodec<Short, Object> {
	
	/**
	 * Constructs a new short codec.<br>
	 */
	public ShortCodec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Short value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as short using '" + this + "'");
		}
		return provider.createShort(value);
	}
	
	@Override
	public @NonNull Result<String> encodeKey(@NonNull Short key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(Short.toString(key));
	}
	
	@Override
	public <R> @NonNull Result<Short> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as short using '" + this + "'");
		}
		
		return provider.getShort(value);
	}
	
	@Override
	public @NonNull Result<Short> decodeKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(Short.parseShort(key));
		} catch (Exception e) {
			return Result.error("Unable to decode key '" + key + "' as short using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "ShortCodec";
	}
}
