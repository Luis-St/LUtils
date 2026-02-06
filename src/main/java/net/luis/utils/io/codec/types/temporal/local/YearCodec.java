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
import net.luis.utils.io.codec.constraint.config.temporal.local.YearConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.local.YearConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.DateTimeException;
import java.time.Year;
import java.util.Objects;

/**
 * Internal codec implementation for years.<br>
 * <p>
 *     Uses the ISO-8601 integer format as an internal representation.<br>
 *     Supports temporal comparison constraints.
 * </p>
 *
 * @author Luis-St
 */
public class YearCodec
	extends AbstractConstrainableCodec<Year, YearConstraintConfig, YearCodec>
	implements YearConstraint<YearCodec> {
	
	/**
	 * Constructs a new year codec.<br>
	 */
	public YearCodec() {
		super(YearCodec::new, YearConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new year codec with the specified constraint configuration.<br>
	 *
	 * @param constraintConfig The constraint configuration
	 * @throws NullPointerException If the constraint config is null
	 */
	private YearCodec(@NonNull YearConstraintConfig constraintConfig) {
		super(YearCodec::new, constraintConfig);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Year value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as year", this);
		}
		
		return provider.createInteger(this.validateEncodeConstraints(value).getValue());
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull Year key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return String.valueOf(this.validateEncodeConstraints(key).getValue());
	}
	
	@Override
	public <R> @NonNull Year decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as year", this);
		}
		
		int yearValue = provider.getInteger(value);
		try {
			Year year = Year.of(yearValue);
			return this.validateDecodeConstraints(year);
		} catch (DateTimeException e) {
			throw new DecoderException("Unable to decode year '" + yearValue + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull Year decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			Year year = Year.of(Integer.parseInt(key));
			return this.validateDecodeConstraints(year);
		} catch (NumberFormatException | DateTimeException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as year: " + e.getMessage(), this, e);
		}
	}
}
