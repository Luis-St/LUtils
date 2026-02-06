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
import net.luis.utils.io.codec.constraint.config.numeric.DecimalConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.numeric.DecimalConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Internal codec implementation for floats.<br>
 *
 * @author Luis-St
 */
public class FloatCodec
	extends AbstractConstrainableCodec<Float, DecimalConstraintConfig<Float>, FloatCodec>
	implements DecimalConstraint<Float, FloatCodec> {
	
	/**
	 * Constructs a new float codec.<br>
	 */
	public FloatCodec() {
		super(FloatCodec::new, DecimalConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new float codec with the specified decimal constraint configuration.<br>
	 *
	 * @param config The decimal constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private FloatCodec(@NonNull DecimalConstraintConfig<Float> config) {
		super(FloatCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Float value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as float", this);
		}
		
		return provider.createFloat(this.validateEncodeConstraints(value));
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Float key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return Float.toString(this.validateEncodeConstraints(key));
	}
	
	@Override
	public <R> @NonNull Float decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as float", this);
		}
		
		return this.validateDecodeConstraints(provider.getFloat(value));
	}
	
	@Override
	public @NonNull Float decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Float floatValue = Float.parseFloat(key);
			return this.validateDecodeConstraints(floatValue);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as float: " + e.getMessage(), this);
		}
	}
}
