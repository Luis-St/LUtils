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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.zoned.ZoneOffsetConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Internal codec implementation for zone offsets.<br>
 * Uses the string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class ZoneOffsetCodec
	extends AbstractConstrainableCodec<ZoneOffset, ZoneOffsetConstraintConfig, ZoneOffsetCodec>
	implements ZoneOffsetConstraint<ZoneOffsetCodec> {
	
	/**
	 * Constructs a new zone offset codec.<br>
	 */
	public ZoneOffsetCodec() {
		super(ZoneOffsetCodec::new, ZoneOffsetConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new zone offset codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private ZoneOffsetCodec(@NonNull ZoneOffsetConstraintConfig config) {
		super(ZoneOffsetCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable ZoneOffset value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as zone offset", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).getId(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull ZoneOffset key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).getId();
	}
	
	@Override
	public <R> @NonNull ZoneOffset decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as zone offset", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			ZoneOffset zoneOffset = ZoneOffset.of(string);
			return this.validateDecodeConstraints(zoneOffset);
		} catch (DateTimeException e) {
			throw new DecoderException("Unable to decode zone offset '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull ZoneOffset decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			ZoneOffset zoneOffset = ZoneOffset.of(key);
			return this.validateDecodeConstraints(zoneOffset);
		} catch (DateTimeException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as zone offset: " + e.getMessage(), this, e);
		}
	}
}
