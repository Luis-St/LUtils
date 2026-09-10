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

import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Embedded;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternTimestampConfig(
	@NonNull Optional<String> format,
	@NonNull Optional<ZoneId> timezone,
	@NonNull Optional<String> prefix,
	@NonNull Optional<String> suffix,
	@NonNull @Embedded(namespace = "padding") Optional<LogPatternPaddingConfig> paddingConfig
) {
	
	private static final Set<String> PRESETS = Set.of(
		"ISO8601", "ISO8601_LOCAL", "UNIX_MILLIS", "UNIX_SECONDS", "RFC_2822", "RFC_1123"
	);
	private static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternTimestampConfig {
		Objects.requireNonNull(format, "Format must not be null");
		Objects.requireNonNull(timezone, "Timezone must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		Objects.requireNonNull(paddingConfig, "Padding config must not be null");
		
		if (prefix.isPresent() && Strings.CI.containsAny(prefix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Prefix must not contain newline, carriage return or tab");
		}
		if (suffix.isPresent() && Strings.CI.containsAny(suffix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Suffix must not contain newline, carriage return or tab");
		}
	}
}
