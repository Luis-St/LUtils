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
import net.luis.utils.io.codec.constraint.config.numeric.BigDecimalConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.numeric.BigDecimalConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Internal codec implementation for big decimals.<br>
 * Uses string representation for encoding and decoding.<br>
 *
 * @author Luis-St
 */
public class BigDecimalCodec
	extends AbstractConstrainableCodec<BigDecimal, BigDecimalConstraintConfig, BigDecimalCodec>
	implements BigDecimalConstraint<BigDecimalCodec> {
	
	/**
	 * Constructs a new big decimal codec.<br>
	 */
	public BigDecimalCodec() {
		super(BigDecimalCodec::new, BigDecimalConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new big decimal codec with the specified big decimal constraint configuration.<br>
	 *
	 * @param config The big decimal constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private BigDecimalCodec(@NonNull BigDecimalConstraintConfig config) {
		super(BigDecimalCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable BigDecimal value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as big decimal", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toPlainString());
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull BigDecimal key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toPlainString();
	}
	
	@Override
	public <R> @NonNull BigDecimal decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as big decimal", this);
		}
		
		String string = provider.getString(value);
		try {
			BigDecimal bigDecimal = new BigDecimal(string);
			return this.validateDecodeConstraints(bigDecimal);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode big decimal '" + string + "': " + e.getMessage(), this);
		}
	}
	
	@Override
	public @NonNull BigDecimal decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			BigDecimal bigDecimal = new BigDecimal(key);
			return this.validateDecodeConstraints(bigDecimal);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as big decimal: " + e.getMessage(), this);
		}
	}
}
