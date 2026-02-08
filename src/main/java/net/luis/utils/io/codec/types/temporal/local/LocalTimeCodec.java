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

package net.luis.utils.io.codec.types.temporal.local;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.temporal.local.LocalTimeConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.local.LocalTimeConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Internal codec implementation for local times.<br>
 * <p>
 *     Uses the ISO-8601 string format as an internal representation.<br>
 *     Supports temporal constraints including comparison, span, and time field constraints.
 * </p>
 *
 * @author Luis-St
 */
public class LocalTimeCodec
	extends AbstractConstrainableCodec<LocalTime, LocalTimeConstraintConfig, LocalTimeCodec>
	implements LocalTimeConstraint<LocalTimeCodec> {
	
	/**
	 * Constructs a new local time codec.<br>
	 */
	public LocalTimeCodec() {
		super(LocalTimeCodec::new, LocalTimeConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new local time codec with the specified constraint configuration.<br>
	 *
	 * @param constraintConfig The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private LocalTimeCodec(@NonNull LocalTimeConstraintConfig constraintConfig) {
		super(LocalTimeCodec::new, constraintConfig);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable LocalTime value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as local time", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull LocalTime key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toString();
	}
	
	@Override
	public <R> @NonNull LocalTime decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as local time", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			LocalTime time = LocalTime.parse(string);
			return this.validateDecodeConstraints(time);
		} catch (DateTimeParseException e) {
			throw new DecoderException("Unable to decode local time '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull LocalTime decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			LocalTime time = LocalTime.parse(key);
			return this.validateDecodeConstraints(time);
		} catch (DateTimeParseException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as local time: " + e.getMessage(), this, e);
		}
	}
}
