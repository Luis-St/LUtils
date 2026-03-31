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
 * Internal codec implementation for doubles.<br>
 *
 * @author Luis-St
 */
public class DoubleCodec
	extends AbstractConstrainableCodec<Double, DecimalConstraintConfig<Double>, DoubleCodec>
	implements DecimalConstraint<Double, DoubleCodec> {
	
	/**
	 * Creates a new double codec.<br>
	 */
	public DoubleCodec() {
		super(DoubleCodec::new, DecimalConstraintConfig.unconstrained());
	}
	
	/**
	 * Creates a new double codec with the specified decimal constraint configuration.<br>
	 *
	 * @param config The decimal constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private DoubleCodec(@NonNull DecimalConstraintConfig<Double> config) {
		super(DoubleCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Double value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as double", this);
		}
		
		return provider.createDouble(this.validateEncodeConstraints(value), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Double key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return Double.toString(this.validateEncodeConstraints(key));
	}
	
	@Override
	public <R> @NonNull Double decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null as double", this);
		}
		
		return this.validateDecodeConstraints(provider.getDouble(value, DecoderException::new));
	}
	
	@Override
	public @NonNull Double decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Double doubleValue = Double.parseDouble(key);
			return this.validateDecodeConstraints(doubleValue);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as double: " + e.getMessage(), this);
		}
	}
}
