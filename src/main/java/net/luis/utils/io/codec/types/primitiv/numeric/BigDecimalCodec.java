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

package net.luis.utils.io.codec.types.primitiv.numeric;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Internal codec implementation for big decimals.<br>
 * Uses string representation for encoding and decoding.<br>
 *
 * @author Luis-St
 */
public class BigDecimalCodec extends AbstractCodec<BigDecimal, Object> {
	
	/**
	 * Constructs a new big decimal codec.<br>
	 */
	public BigDecimalCodec() {}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable BigDecimal value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as big decimal using '" + this + "'");
		}
		return provider.createString(value.toPlainString());
	}
	
	@Override
	public @NotNull Result<String> encodeKey(@NotNull BigDecimal key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.toPlainString());
	}
	
	@Override
	public <R> @NotNull Result<BigDecimal> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as big decimal using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(new BigDecimal(string));
		} catch (NumberFormatException e) {
			return Result.error("Unable to decode big decimal '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<BigDecimal> decodeKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(new BigDecimal(key));
		} catch (NumberFormatException e) {
			return Result.error("Unable to decode key '" + key + "' as big decimal using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "BigDecimalCodec";
	}
}
