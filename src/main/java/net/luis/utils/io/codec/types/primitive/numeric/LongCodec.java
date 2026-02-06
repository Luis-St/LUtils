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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.AbstractConstrainableCodec;
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.numeric.IntegerConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for longs.<br>
 *
 * @author Luis-St
 */
public class LongCodec
	extends AbstractConstrainableCodec<Long, IntegerConstraintConfig<Long>, LongCodec>
	implements IntegerConstraint<Long, LongCodec> {
	
	/**
	 * Constructs a new long codec.<br>
	 */
	public LongCodec() {
		super(LongCodec::new, IntegerConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new long codec with the specified integer constraint configuration.<br>
	 *
	 * @param config The integer constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private LongCodec(@NonNull IntegerConstraintConfig<Long> config) {
		super(LongCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Long value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as long", this);
		}
		
		return provider.createLong(this.validateEncodeConstraints(value));
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Long key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return Long.toString(this.validateEncodeConstraints(key));
	}
	
	@Override
	public <R> @NonNull Long decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as long", this);
		}
		
		return this.validateDecodeConstraints(provider.getLong(value));
	}
	
	@Override
	public @NonNull Long decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Long longValue = Long.parseLong(key);
			return this.validateDecodeConstraints(longValue);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as long: " + e.getMessage(), this, e);
		}
	}
}
