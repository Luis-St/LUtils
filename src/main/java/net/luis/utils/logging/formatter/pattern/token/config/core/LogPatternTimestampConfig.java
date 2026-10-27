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
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

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
	private static final Set<String> PRESETS = Set.of(
		"ISO8601", "ISO8601_LOCAL", "UNIX_MILLIS", "UNIX_SECONDS", "RFC_2822", "RFC_1123"
	);
	
	private static @NonNull Result<String> validateFormat(@NonNull String format) {
		if (PRESETS.contains(format.toUpperCase())) {
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
		}
	}
}
