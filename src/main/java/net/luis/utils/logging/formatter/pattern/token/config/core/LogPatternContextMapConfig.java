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
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternContextMapConfig(
	@NonNull List<Pair</*Key*/ String, /*Default value*/ String>> include,
	@NonNull List<String> exclude,
	@NonNull Optional<String> kvSeparator,
	@NonNull Optional<String> entrySeparator,
	@NonNull Optional<String> prefix,
	@NonNull Optional<String> suffix,
	@NonNull @Embedded(namespace = "padding") Optional<LogPatternPaddingConfig> paddingConfig
) {
	
	static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternContextMapConfig {
		Objects.requireNonNull(include, "Include list must not be null");
		Objects.requireNonNull(exclude, "Exclude list must not be null");
		Objects.requireNonNull(kvSeparator, "Key-value separator must not be null");
		Objects.requireNonNull(entrySeparator, "Entry separator must not be null");
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		Objects.requireNonNull(paddingConfig, "Padding config must not be null");
		
		if (include.stream().anyMatch(pair -> pair.getFirst() == null || pair.getFirst().isBlank())) {
			throw new LogPatternException("Include list must not contain null or blank keys");
		}
		if (exclude.stream().anyMatch(Objects::isNull)) {
			throw new LogPatternException("Exclude list must not contain null values");
		}
		if (kvSeparator.isPresent()) {
			if (kvSeparator.get().isBlank()) {
				throw new LogPatternException("Key-value separator must not be blank");
			}
			
			if (Strings.CI.containsAny(kvSeparator.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Key-value separator must not be a newline, carriage return or tab: " + kvSeparator.get());
			}
		}
		if (entrySeparator.isPresent()) {
			if (entrySeparator.get().isBlank()) {
				throw new LogPatternException("Entry separator must not be blank");
			}
			
			if (Strings.CI.containsAny(entrySeparator.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Entry separator must not be a newline, carriage return or tab: " + entrySeparator.get());
			}
		}
		if (prefix.isPresent() && Strings.CI.containsAny(prefix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Prefix must not contain newline, carriage return or tab");
		}
		if (suffix.isPresent() && Strings.CI.containsAny(suffix.get(), ILLEGAL_CHARS)) {
			throw new LogPatternException("Suffix must not contain newline, carriage return or tab");
		}
		
		include = List.copyOf(include);
		exclude = List.copyOf(exclude);
	}
}
