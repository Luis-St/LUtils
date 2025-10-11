/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.internal.time;

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal codec implementation for periods.<br>
 * Uses a human-readable string format as an internal representation.<br>
 *
 * @author Luis-St
 */
public class PeriodCodec implements Codec<Period> {
	
	/**
	 * Pattern to match period parts (e.g., "1y", "2mo", "3d").<br>
	 * Supports optional sign and is case-insensitive.<br>
	 */
	private static final Pattern PERIOD_PATTERN = Pattern.compile("([+-]?\\d+)([a-z]{1,2})", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Constructs a new period codec.<br>
	 */
	public PeriodCodec() {}
	
	@Override
	public <R> @NotNull Result<R> encodeStart(@NotNull TypeProvider<R> provider, @NotNull R current, @Nullable Period value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to encode null as period using '" + this + "'");
		}
		
		if (value.isZero()) {
			return provider.createString("0d");
		}
		
		List<String> parts = Lists.newArrayList();
		if (value.getYears() != 0) {
			parts.add(value.getYears() + "y");
		}
		if (value.getMonths() != 0) {
			parts.add(value.getMonths() + "m");
		}
		if (value.getDays() != 0) {
			parts.add(value.getDays() + "d");
		}
		
		return provider.createString(String.join(" ", parts));
	}
	
	@Override
	public <R> @NotNull Result<Period> decodeStart(@NotNull TypeProvider<R> provider, @Nullable R value) {
		Objects.requireNonNull(provider, "Type provider must not be null");
		if (value == null) {
			return Result.error("Unable to decode null value as period using '" + this + "'");
		}
		
		Result<String> result = provider.getString(value);
		if (result.isError()) {
			return Result.error("Unable to decode period from non-string value using '" + this + "': " + result.errorOrThrow());
		}
		
		String string = result.resultOrThrow();
		try {
			if ("0d".equalsIgnoreCase(string)) {
				return Result.success(Period.ZERO);
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
					return Result.error("Unable to decode period '" + string + "' using '" + this + "': Invalid period format, expected format like '1y 2mo 3d' but got '" + part + "'");
				}
				
				int partValue = Integer.parseInt(matcher.group(1));
				String unit = matcher.group(2).toLowerCase();
				
				switch (unit) {
					case "y" -> years += partValue;
					case "mo" -> months += partValue;
					case "d" -> days += partValue;
					default -> {
						return Result.error("Unable to decode period '" + string + "' using '" + this + "': Unknown time unit, expected one of 'y', 'mo', or 'd' but got '" + unit + "'");
					}
				}
			}
			return Result.success(Period.of(years, months, days));
		} catch (Exception e) {
			return Result.error("Unable to decode period '" + string + "' using '" + this + "': Failed to parse period '" + string + "': " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "PeriodCodec";
	}
}
