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

package net.luis.utils.logging.formatter.pattern.token.config.core;

import net.luis.utils.io.codec.*;
import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternTimestampConfig(
	@NonNull Optional<String> format,
	@NonNull Optional<ZoneId> timezone,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternTimestampConfig> CODEC = CodecBuilder.of(
		Codecs.STRING.validate(LogPatternTimestampConfig::validateFormat).optional().optionalFieldOf("format", Optional.empty(), LogPatternTimestampConfig::format),
		Codecs.ZONE_ID.optional().optionalFieldOf("timezone", Optional.empty(), LogPatternTimestampConfig::timezone),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternTimestampConfig::padding)
	).create(LogPatternTimestampConfig::new);
	
	private static @NonNull Result<String> validateFormat(@NonNull String format) {
		if (DateTimePresent.byName(format.toUpperCase()) != null) {
			return Result.success(format);
		}
		
		try {
			DateTimeFormatter.ofPattern(format);
			return Result.success(format);
		} catch (IllegalArgumentException e) {
			return Result.error("Invalid timestamp format '" + format + "': " + e.getMessage());
		}
	}
	
	public LogPatternTimestampConfig {
		Objects.requireNonNull(format, "Format must not be null");
		Objects.requireNonNull(timezone, "Timezone must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		
		if (format.isPresent()) {
			Result<String> result = validateFormat(format.get());
			
			if (result.isError()) {
				throw new LogPatternException(result.errorOrThrow());
			}
			format = format.map(String::toUpperCase);
		}
	}
	
	@Override
	public @NonNull String format(@NonNull LogEvent event, @NonNull String plainTokenContent) {
		Objects.requireNonNull(event, "Log event must not be null");
		Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
		
		String result = "";
		if (this.format.isPresent()) {
			DateTimePresent present = DateTimePresent.byName(this.format.get());
			
			ZoneId timeZone = this.timezone.orElse(ZoneId.systemDefault());
			if (present != null) {
				result = present.format(event.timestamp(), timeZone);
			} else {
				try {
					result = DateTimeFormatter.ofPattern(this.format.get()).format(event.timestamp().atZone(timeZone));
				} catch (Exception e) {
					result = event.timestamp().toString();
				}
			}
		} else {
			result = event.timestamp().toString();
		}
		return LogTokenFormatHelper.format(result, Optional.empty(), this.padding);
	}
	
	enum DateTimePresent {
		
		ISO8601("ISO8601", DateTimeFormatter.ISO_DATE_TIME),
		ISO8601_LOCAL("ISO8601_LOCAL", DateTimeFormatter.ISO_LOCAL_DATE_TIME),
		UNIX_MILLIS("UNIX_MILLIS") {
			@Override
			public @NonNull String format(@NonNull Instant timestamp, @NonNull ZoneId timeZone) {
				Objects.requireNonNull(timestamp, "Timestamp must not be null");
				Objects.requireNonNull(timeZone, "Time zone must not be null");
				
				return String.valueOf(timestamp.toEpochMilli());
			}
		},
		UNIX_SECONDS("UNIX_SECONDS") {
			@Override
			public @NonNull String format(@NonNull Instant timestamp, @NonNull ZoneId timeZone) {
				Objects.requireNonNull(timestamp, "Timestamp must not be null");
				Objects.requireNonNull(timeZone, "Time zone must not be null");
				
				return String.valueOf(timestamp.getEpochSecond());
			}
		},
		RFC_2822("RFC_2822", DateTimeFormatter.ofPattern("EEE, dd MMM uuuu HH:mm:ss Z", Locale.ENGLISH)),
		RFC_1123("RFC_1123", DateTimeFormatter.RFC_1123_DATE_TIME);
		
		private final String name;
		private final Optional<DateTimeFormatter> formatter;
		
		private DateTimePresent(@NonNull String name) {
			this.name = Objects.requireNonNull(name, "Name must not be null");
			this.formatter = Optional.empty();
		}
		
		public static @Nullable DateTimePresent byName(@NonNull String name) {
			Objects.requireNonNull(name, "Name must not be null");
			return Arrays.stream(values()).filter(present -> present.name.equals(name)).findFirst().orElse(null);
		}
		
		private DateTimePresent(@NonNull String name, @NonNull DateTimeFormatter formatter) {
			this.name = Objects.requireNonNull(name, "Name must not be null");
			this.formatter = Optional.of(Objects.requireNonNull(formatter, "Date time formatter must not be null"));
		}
		
		public @NonNull String format(@NonNull Instant timestamp, @NonNull ZoneId timeZone) {
			Objects.requireNonNull(timestamp, "Timestamp must not be null");
			Objects.requireNonNull(timeZone, "Time zone must not be null");
			if (this.formatter.isEmpty()) {
				throw new UnsupportedOperationException("Date time present does not suport formatting");
			}
			
			return this.formatter.get().format(timestamp.atZone(timeZone));
		}
	}
}
