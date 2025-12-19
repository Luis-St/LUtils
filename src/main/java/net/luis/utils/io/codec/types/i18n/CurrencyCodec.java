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

package net.luis.utils.io.codec.types.i18n;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Currency;
import java.util.Objects;

/**
 * Internal codec implementation for currencies.<br>
 * Uses the ISO 4217 currency code as an internal representation.<br>
 *
 * @author Luis-St
 */
public class CurrencyCodec extends AbstractCodec<Currency, Object> {
	
	/**
	 * Constructs a new currency codec.<br>
	 */
	public CurrencyCodec() {}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Currency value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			return Result.error("Unable to encode null as currency using '" + this + "'");
		}
		return provider.createString(value.getCurrencyCode());
	}
	
	@Override
	public @NotNull Result<String> encodeKey(@NotNull Currency key) {
		Objects.requireNonNull(key, "Key must not be null");
		return Result.success(key.getCurrencyCode());
	}
	
	@Override
	public <R> @NotNull Result<Currency> decodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as currency using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error(result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			return Result.success(Currency.getInstance(string));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode currency '" + string + "' using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public @NotNull Result<Currency> decodeKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Result.success(Currency.getInstance(key));
		} catch (IllegalArgumentException e) {
			return Result.error("Unable to decode key '" + key + "' as currency using '" + this + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "CurrencyCodec";
	}
}
