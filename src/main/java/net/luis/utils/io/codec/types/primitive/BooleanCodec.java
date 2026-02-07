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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for booleans.<br>
 *
 * @author Luis-St
 */
public class BooleanCodec extends AbstractCodec<Boolean> {
	
	/**
	 * Constructs a new boolean codec.<br>
	 */
	public BooleanCodec() {}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Boolean value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as boolean", this);
		}
		
		return provider.createBoolean(value, EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Boolean key) {
		Objects.requireNonNull(key, "Key must not be null");
		return key.toString();
	}
	
	@Override
	public <R> @NonNull Boolean decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null as boolean", this);
		}
		
		return provider.getBoolean(value, DecoderException::new);
	}
	
	@Override
	public @NonNull Boolean decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		if ("true".equals(key)) {
			return true;
		} else if ("false".equals(key)) {
			return false;
		}
		throw new DecoderException("Unable to decode key '" + key + "' as boolean", this);
	}
	
	@Override
	public String toString() {
		return "BooleanCodec";
	}
}
