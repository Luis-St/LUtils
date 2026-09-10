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

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternContextItemConfig(
	@NonNull String key,
	@NonNull Optional<String> defaultValue,
	@NonNull Optional<String> prefix,
	@NonNull Optional<String> suffix,
	@NonNull @Embedded(namespace = "padding") Optional<LogPatternPaddingConfig> paddingConfig
) {
	
	static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternContextItemConfig {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		
		if (key.isBlank()) {
			throw new LogPatternException("Key must not be empty or blank");
		}
		if (prefix.isPresent() && Strings.CI.containsAny(prefix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Prefix must not contain newline, carriage return or tab");
		}
		if (suffix.isPresent() && Strings.CI.containsAny(suffix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Suffix must not contain newline, carriage return or tab");
		}
	}
}
