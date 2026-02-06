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

package net.luis.utils.io.codec.types.i18n;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Currency;
import java.util.Objects;

/**
 * Internal codec implementation for currencies.<br>
 * Uses the ISO 4217 currency code as an internal representation.<br>
 *
 * @author Luis-St
 */
public class CurrencyCodec extends AbstractCodec<Currency> {
	
	/**
	 * Constructs a new currency codec.<br>
	 */
	public CurrencyCodec() {}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Currency value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		
		if (value == null) {
			throw new EncoderException("Unable to encode null as currency", this);
		}
		return provider.createString(value.getCurrencyCode());
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Currency key) {
		Objects.requireNonNull(key, "Key must not be null");
		return key.getCurrencyCode();
	}
	
	@Override
	public <R> @NonNull Currency decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as currency", this);
		}
		
		String string = provider.getString(value);
		try {
			return Currency.getInstance(string);
		} catch (IllegalArgumentException e) {
			throw new DecoderException("Unable to decode currency '" + string + ": " + e.getMessage(), this);
		}
	}
	
	@Override
	public @NonNull Currency decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			return Currency.getInstance(key);
		} catch (IllegalArgumentException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as currency: " + e.getMessage(), this);
		}
	}
	
	@Override
	public String toString() {
		return "CurrencyCodec";
	}
}
