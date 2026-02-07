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
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneIdConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.zoned.ZoneIdConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;

/**
 * Internal codec implementation for zone ids.<br>
 * Uses the string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class ZoneIdCodec
	extends AbstractConstrainableCodec<ZoneId, ZoneIdConstraintConfig, ZoneIdCodec>
	implements ZoneIdConstraint<ZoneIdCodec> {
	
	/**
	 * Constructs a new zone id codec.<br>
	 */
	public ZoneIdCodec() {
		super(ZoneIdCodec::new, ZoneIdConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new zone id codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private ZoneIdCodec(@NonNull ZoneIdConstraintConfig config) {
		super(ZoneIdCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable ZoneId value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as zone id", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).getId(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull ZoneId key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).getId();
	}
	
	@Override
	public <R> @NonNull ZoneId decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as zone id", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			ZoneId zoneId = ZoneId.of(string);
			return this.validateDecodeConstraints(zoneId);
		} catch (ZoneRulesException e) {
			throw new DecoderException("Unable to decode zone id '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull ZoneId decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			ZoneId zoneId = ZoneId.of(key);
			return this.validateDecodeConstraints(zoneId);
		} catch (ZoneRulesException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as zone id: " + e.getMessage(), this, e);
		}
	}
}
