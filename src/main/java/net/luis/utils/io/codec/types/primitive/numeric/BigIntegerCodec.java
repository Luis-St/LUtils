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

import java.math.BigInteger;
import java.util.Objects;

/**
 * Internal codec implementation for big integers.<br>
 * Uses string representation for encoding and decoding.<br>
 *
 * @author Luis-St
 */
public class BigIntegerCodec
	extends AbstractConstrainableCodec<BigInteger, IntegerConstraintConfig<BigInteger>, BigIntegerCodec>
	implements IntegerConstraint<BigInteger, BigIntegerCodec> {
	
	/**
	 * Constructs a new big integer codec.<br>
	 */
	public BigIntegerCodec() {
		super(BigIntegerCodec::new, IntegerConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new big integer codec with the specified integer constraint configuration.<br>
	 *
	 * @param config The integer constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private BigIntegerCodec(@NonNull IntegerConstraintConfig<BigInteger> config) {
		super(BigIntegerCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable BigInteger value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as big integer", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).toString());
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull BigInteger key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).toString();
	}
	
	@Override
	public <R> @NonNull BigInteger decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as big integer", this);
		}
		
		String string = provider.getString(value);
		try {
			BigInteger bigInteger = new BigInteger(string);
			return this.validateDecodeConstraints(bigInteger);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode big integer '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull BigInteger decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			BigInteger bigInteger = new BigInteger(key);
			return this.validateDecodeConstraints(bigInteger);
		} catch (NumberFormatException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as big integer: " + e.getMessage(), this, e);
		}
	}
}
