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

package net.luis.utils.logging.formatter.pattern.config;

import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.util.Embedded;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternMarkerConfig(
	@NotNull Optional<Boolean> path,
	@NotNull Optional<String> separator,
	@NotNull Optional<String> prefix,
	@NotNull Optional<String> suffix,
	@NotNull @Embedded(namespace = "padding") Optional<LogPatternPaddingConfig> paddingConfig
) {
	
	static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternMarkerConfig {
		Objects.requireNonNull(path, "Path must not be null");
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		Objects.requireNonNull(paddingConfig, "Padding config must not be null");
		
		if (separator.isPresent()) {
			if (separator.get().isBlank()) {
				throw new LogPatternException("Separator must not be blank");
			}
			
			if (Strings.CI.containsAny(separator.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Separator must not contain newline, carriage return or tab");
			}
		}
		if (prefix.isPresent() && Strings.CI.containsAny(prefix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Prefix must not contain newline, carriage return or tab");
		}
		if (suffix.isPresent() && Strings.CI.containsAny(suffix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Suffix must not contain newline, carriage return or tab");
		}
	}
}
