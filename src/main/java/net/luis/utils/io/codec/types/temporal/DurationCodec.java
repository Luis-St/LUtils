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
import net.luis.utils.io.codec.constraint.config.temporal.DurationConstraintConfig;
import net.luis.utils.io.codec.constraint.merged.temporal.DurationConstraint;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal codec implementation for durations.<br>
 * Uses a human-readable string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class DurationCodec
	extends AbstractConstrainableCodec<Duration, DurationConstraintConfig, DurationCodec>
	implements DurationConstraint<DurationCodec> {
	
	/**
	 * Pattern to match duration parts (e.g., "1h", "30m", "45s", "500ms", "200ns").<br>
	 * Supports optional sign and is case-insensitive.<br>
	 */
	private static final Pattern DURATION_PATTERN = Pattern.compile("([+-]?\\d+)([a-z]{1,2})", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Constructs a new duration codec.<br>
	 */
	public DurationCodec() {
		super(DurationCodec::new, DurationConstraintConfig.UNCONSTRAINED);
	}
	
	/**
	 * Constructs a new duration codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private DurationCodec(@NonNull DurationConstraintConfig config) {
		super(DurationCodec::new, config);
	}
	
	@Override
	public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Duration value) throws EncoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			throw new EncoderException("Unable to encode null as duration", this);
		}
		
		Duration validated = this.validateEncodeConstraints(value);
		
		long totalSeconds = validated.getSeconds();
		long days = totalSeconds / 86400;
		long hours = (totalSeconds % 86400) / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		long milliseconds = validated.toMillis() % 1000;
		long nanos = validated.toNanos() % 1_000_000;
		
		StringBuilder builder = new StringBuilder();
		if (days > 0) {
			builder.append(days).append("d ");
		}
		if (hours > 0) {
			builder.append(hours).append("h ");
		}
		if (minutes > 0) {
			builder.append(minutes).append("m ");
		}
		if (seconds > 0) {
			builder.append(seconds).append("s ");
		}
		if (milliseconds > 0) {
			builder.append(milliseconds).append("ms ");
		}
		if (nanos > 0) {
			builder.append(nanos).append("ns");
		}
		
		String encoded = builder.toString().trim();
		if (encoded.isEmpty()) {
			encoded = "0s";
		}
		return provider.createString(encoded);
	}
	
	@Override
	public <R> @NonNull Duration decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			throw new DecoderException("Unable to decode null value as duration", this);
		}
		
		String string = provider.getString(value);
		try {
			String[] parts = string.toLowerCase().split("\\s+");
			long totalSeconds = 0;
			long nanos = 0;
			
			for (String part : parts) {
				if (part.isEmpty()) {
					continue;
				}
				
				Matcher matcher = DURATION_PATTERN.matcher(part);
				if (!matcher.matches()) {
					throw new DecoderException("Unable to decode duration '" + string + "': Invalid duration format, expected format like '1y 2mo 3w 4d 5h 6m 7s 800ms 900ns' but got '" + part + "'", this);
				}
				
				long partValue = Long.parseLong(matcher.group(1));
				String unit = matcher.group(2).toLowerCase();
				switch (unit) {
					case "y" -> totalSeconds += partValue * 86400 * 365;
					case "mo" -> totalSeconds += partValue * 86400 * 30;
					case "w" -> totalSeconds += partValue * 86400 * 7;
					case "d" -> totalSeconds += partValue * 86400;
					case "h" -> totalSeconds += partValue * 3600;
					case "m" -> totalSeconds += partValue * 60;
					case "s" -> totalSeconds += partValue;
					case "ms" -> nanos += partValue * 1_000_000;
					case "ns" -> nanos += partValue;
					default -> throw new DecoderException("Unable to decode duration '" + string + "': Unknown time unit, expected one of 'y', 'mo', 'w', 'd', 'h', 'm', 's', 'ms', or 'ns' but got '" + unit + "'", this);
				}
			}
			
			Duration duration = Duration.ofSeconds(totalSeconds, nanos);
			return this.validateDecodeConstraints(duration);
		} catch (DecoderException e) {
			throw e;
		} catch (Exception e) {
			throw new DecoderException("Unable to decode duration '" + string + "': " + e.getMessage(), this, e);
		}
	}
}
