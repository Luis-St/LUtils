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

package net.luis.utils.io.codec.types.temporal;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.temporal.InstantConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.InstantConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Internal codec implementation for instants.<br>
 * <p>
 *     Uses the ISO-8601 string format as an internal representation.<br>
 *     Supports temporal comparison constraints.
 * </p>
 *
 * @author Luis-St
 */
public class InstantCodec
	extends AbstractConstrainableCodec<Instant, InstantConstraintConfig, InstantCodec>
	implements InstantConstraint<InstantCodec> {
	
	/**
	 * Constructs a new instant codec.<br>
	 */
	public InstantCodec() {
		super(InstantCodec::new, InstantConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new instant codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private InstantCodec(@NonNull InstantConstraintConfig config) {
		super(InstantCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Instant value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as instant", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString());
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Instant key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toString();
	}
	
	@Override
	public <R> @NonNull Instant decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as instant", this);
		}
		
		String string = provider.getString(value);
		try {
			Instant instant = Instant.parse(string);
			return this.validateDecodeConstraints(instant);
		} catch (DateTimeParseException e) {
			throw new DecoderException("Unable to decode instant '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull Instant decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Instant instant = Instant.parse(key);
			return this.validateDecodeConstraints(instant);
		} catch (DateTimeParseException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as instant: " + e.getMessage(), this, e);
		}
	}
}
