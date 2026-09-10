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
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import net.luis.utils.logging.formatter.pattern.token.config.util.Embedded;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternMessageConfig(
	@NonNull OptionalInt maxLength,
	@NonNull Optional<String> ellipsis,
	@NonNull List<Character> escapeCharacters,
	@NonNull Optional<String> escapeSequence,
	@NonNull Optional<Casing> casing,
	@NonNull Optional<String> prefix,
	@NonNull Optional<String> suffix,
	@NonNull @Embedded(namespace = "padding") Optional<LogPatternPaddingConfig> paddingConfig
) {
	
	private static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternMessageConfig {
		Objects.requireNonNull(maxLength, "Max length must not be null");
		Objects.requireNonNull(ellipsis, "Ellipsis must not be null");
		Objects.requireNonNull(escapeCharacters, "Escape characters must not be null");
		Objects.requireNonNull(escapeSequence, "Escape sequence must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		Objects.requireNonNull(paddingConfig, "Padding config must not be null");
		
		if (maxLength.isPresent() && maxLength.getAsInt() < 0) {
			throw new LogPatternException("Max length must not be negative");
		}
		if (ellipsis.isPresent()) {
			if (ellipsis.get().isEmpty()) {
				throw new LogPatternException("Ellipsis must not be empty");
			}
			if (maxLength.isEmpty()) {
				throw new LogPatternException("Ellipsis can only be used with max length");
			}
			if (ellipsis.get().length() > maxLength.getAsInt()) {
				throw new LogPatternException("Ellipsis length must not be greater than max length");
			}
		}
		if (escapeSequence.isPresent() && Strings.CI.containsAny(escapeSequence.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Escape sequence must not contain newline, carriage return or tab");
		}
		if (prefix.isPresent() && Strings.CI.containsAny(prefix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Prefix must not contain newline, carriage return or tab");
		}
		if (suffix.isPresent() && Strings.CI.containsAny(suffix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Suffix must not contain newline, carriage return or tab");
		}
	}
}
