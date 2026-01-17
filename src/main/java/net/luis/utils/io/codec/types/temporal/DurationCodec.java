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

import net.luis.utils.io.codec.AbstractCodec;
import net.luis.utils.io.codec.constraint_new.config.temporal.DurationConstraintConfig;
import net.luis.utils.io.codec.constraint_new.temporal.DurationConstraint;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal codec implementation for durations.<br>
 * Uses a human-readable string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class DurationCodec extends AbstractCodec<Duration, DurationConstraintConfig> implements DurationConstraint<DurationCodec> {
	
	/**
	 * Pattern to match duration parts (e.g., "1h", "30m", "45s", "500ms", "200ns").<br>
	 * Supports optional sign and is case-insensitive.<br>
	 */
	private static final Pattern DURATION_PATTERN = Pattern.compile("([+-]?\\d+)([a-z]{1,2})", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Constructs a new duration codec.<br>
	 */
	public DurationCodec() {}
	
	/**
	 * Constructs a new duration codec with the given configuration.<br>
	 *
	 * @param config The constraint configuration
	 * @throws NullPointerException If the config is null
	 */
	private DurationCodec(@NonNull DurationConstraintConfig config) {
		super(config);
	}
	
	@Override
	public @NonNull DurationCodec apply(@NonNull UnaryOperator<DurationConstraintConfig> configModifier) {
		Objects.requireNonNull(configModifier, "Config modifier must not be null");
		
		return new DurationCodec(
			configModifier.apply(this.getConstraintConfig().orElse(DurationConstraintConfig.UNCONSTRAINED))
		);
	}
	
	@Override
	public <R> @NonNull Result<R> encodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable Duration value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as duration using '" + this + "'");
		}
		
		Result<Void> constraintResult = this.checkConstraints(value);
		if (constraintResult.isError()) {
			return Result.error(constraintResult.errorOrThrow());
		}
		
		long totalSeconds = value.getSeconds();
		long days = totalSeconds / 86400;
		long hours = (totalSeconds % 86400) / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		long milliseconds = value.toMillis() % 1000;
		long nanos = value.toNanos() % 1_000_000;
		
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
	public <R> @NonNull Result<Duration> decodeStart(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		Objects.requireNonNull(current, "Current value must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as duration using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error("Unable to decode duration from non-string value using '" + this + "': " + result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
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
					return Result.error("Unable to decode duration '" + string + "' using '" + this + "': Invalid duration format, expected format like '1y 2mo 3w 4d 5h 6m 7s 800ms 900ns' but got '" + part + "'");
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
					default -> {
						return Result.error("Unable to decode duration '" + string + "' using '" + this + "': Unknown time unit, expected one of 'y', 'mo', 'w', 'd', 'h', 'm', 's', 'ms', or 'ns' but got '" + unit + "'");
					}
				}
			}
			
			Duration duration = Duration.ofSeconds(totalSeconds, nanos);
			Result<Void> constraint = this.checkConstraints(duration);
			if (constraint.isError()) {
				return Result.error(constraint.errorOrThrow());
			}
			return Result.success(duration);
		} catch (Exception e) {
			return Result.error("Unable to decode duration '" + string + "' using '" + this + "': Failed to parse duration '" + string + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return this.getConstraintConfig().map(config -> {
			return "ConstrainedDurationCodec[constraints=" + config + "]";
		}).orElse("DurationCodec");
	}
}
