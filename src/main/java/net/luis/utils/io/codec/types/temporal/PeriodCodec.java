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
import net.luis.utils.io.codec.constraint.config.temporal.PeriodConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.PeriodConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal codec implementation for periods.<br>
 * Uses a human-readable string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class PeriodCodec
	extends AbstractConstrainableCodec<Period, PeriodConstraintConfig, PeriodCodec>
	implements PeriodConstraint<PeriodCodec> {
	
	/**
	 * Pattern to match period parts (e.g., "1y", "2mo", "3d").<br>
	 * Supports optional sign and is case-insensitive.<br>
	 */
	private static final Pattern PERIOD_PATTERN = Pattern.compile("([+-]?\\d+)([a-z]{1,2})", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Constructs a new period codec.<br>
	 */
	public PeriodCodec() {
		super(PeriodCodec::new, PeriodConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new period codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private PeriodCodec(@NonNull PeriodConstraintConfig config) {
		super(PeriodCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Period value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as period", this);
		}
		
		Period validated = this.validateEncodeConstraints(value);
		
		if (validated.isZero()) {
			return provider.createString("0d", EncoderException::new);
		}
		
		List<String> parts = new ArrayList<>();
		if (validated.getYears() != 0) {
			parts.add(validated.getYears() + "y");
		}
		if (validated.getMonths() != 0) {
			parts.add(validated.getMonths() + "m");
		}
		if (validated.getDays() != 0) {
			parts.add(validated.getDays() + "d");
		}
		return provider.createString(String.join(" ", parts), EncoderException::new);
	}
	
	@Override
	public <R> @NonNull Period decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as period", this);
		}
		
		String string = provider.getString(value, DecoderException::new);
		try {
			if ("0d".equalsIgnoreCase(string)) {
				return Period.ZERO;
			}
			
			String[] parts = string.split("\\s+");
			int years = 0;
			int months = 0;
			int days = 0;
			
			for (String part : parts) {
				if (part.isEmpty()) {
					continue;
				}
				
				Matcher matcher = PERIOD_PATTERN.matcher(part);
				if (!matcher.matches()) {
					throw new DecoderException("Unable to decode period '" + string + "': Invalid period format, expected format like '1y 2m 3d' but got '" + part + "'", this);
				}
				
				int partValue = Integer.parseInt(matcher.group(1));
				String unit = matcher.group(2).toLowerCase();
				
				switch (unit) {
					case "y" -> years += partValue;
					case "m" -> months += partValue;
					case "d" -> days += partValue;
					default -> throw new DecoderException("Unable to decode period '" + string + "': Unknown time unit, expected one of 'y', 'm', or 'd' but got '" + unit + "'", this);
				}
			}
			
			Period period = Period.of(years, months, days);
			return this.validateDecodeConstraints(period);
		} catch (DecoderException e) {
			throw e;
		} catch (Exception e) {
			throw new DecoderException("Unable to decode period '" + string + "': " + e.getMessage(), this, e);
		}
	}
}
