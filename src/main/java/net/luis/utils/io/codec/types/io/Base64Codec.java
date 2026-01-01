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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Base64;
import java.util.Objects;

/**
 * Internal codec implementation for Base64-encoded byte arrays.<br>
 * Uses Base64 encoding to represent byte arrays as strings.<br>
 *
 * @author Luis-St
 */
public class Base64Codec extends AbstractCodec<byte[], Object> {
	
	/**
	 * Constructs a new Base64 codec.<br>
	 */
	public Base64Codec() {}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, byte @Nullable [] value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as base 64 using '" + this + "'");
		}
		return provider.createString(Base64.getEncoder().encodeToString(value));
	}
	
	@Override
	public <R> @NonNull Result<byte[]> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as base 64 using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error("Unable to decode base 64 from a non-string value using '" + this + "': " + result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(Base64.getDecoder().decode(string));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode base 64 string '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "Base64Codec";
	}
}
