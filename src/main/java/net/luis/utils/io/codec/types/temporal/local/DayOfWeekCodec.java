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
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.core.EnumConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.DayOfWeek;
import java.util.Objects;

/**
 * Internal codec implementation for day of week.<br>
 * Uses the string name as an internal representation.<br>
 *
 * @author Luis-St
 */
public class DayOfWeekCodec
	extends AbstractConstrainableCodec<DayOfWeek, EnumConstraintConfig<DayOfWeek>, DayOfWeekCodec>
	implements EnumConstraint<DayOfWeek, DayOfWeekCodec> {
	
	/**
	 * Constructs a new day of week codec.<br>
	 */
	public DayOfWeekCodec() {
		super(DayOfWeekCodec::new, EnumConstraintConfig.unconstrained());
	}
	
	/**
	 * Constructs a new day of week codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private DayOfWeekCodec(@NonNull EnumConstraintConfig<DayOfWeek> config) {
		super(DayOfWeekCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable DayOfWeek value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as day of week", this);
		}
		
		return provider.createString(this.validateEncodeConstraints(value).name(), EncoderException::new);
	}
	
	@Override
	public @NonNull String encodeKey(@NonNull DayOfWeek key) throws EncoderException {
		Objects.requireNonNull(key, "Key must not be null");
		return this.validateEncodeConstraints(key).name();
	}
	
	@Override
	public <R> @NonNull DayOfWeek decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as day of week", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			DayOfWeek dayOfWeek = DayOfWeek.valueOf(string);
			return this.validateDecodeConstraints(dayOfWeek);
		} catch (IllegalArgumentException e) {
			throw new DecoderException("Unable to decode day of week '" + string + "': " + e.getMessage(), this, e);
		}
	}
	
	@Override
	public @NonNull DayOfWeek decodeKey(@NonNull String key) throws DecoderException {
		Objects.requireNonNull(key, "Key must not be null");
		
		try {
			DayOfWeek dayOfWeek = DayOfWeek.valueOf(key);
			return this.validateDecodeConstraints(dayOfWeek);
		} catch (IllegalArgumentException e) {
			throw new DecoderException("Unable to decode key '" + key + "' as day of week: " + e.getMessage(), this, e);
		}
	}
}
