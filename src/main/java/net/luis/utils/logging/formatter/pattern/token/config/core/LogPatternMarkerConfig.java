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
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternMarkerConfig(
	@NonNull Optional<Boolean> path,
	@NonNull Optional<String> separator,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternMarkerConfig> CODEC = CodecBuilder.of(
		Codecs.BOOLEAN.optional().optionalFieldOf("path", Optional.empty(), LogPatternMarkerConfig::path),
		Codecs.STRING.optional().optionalFieldOf("separator", Optional.empty(), LogPatternMarkerConfig::separator),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternMarkerConfig::padding)
	).create(LogPatternMarkerConfig::new);
	static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternMarkerConfig {
		Objects.requireNonNull(path, "Path must not be null");
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		
		if (separator.isPresent()) {
			if (path.isPresent() && !path.get()) {
				throw new LogPatternException("Separator can not be used if path is disabled");
			}
			if (separator.get().isEmpty()) {
				throw new LogPatternException("Separator must not be empty");
			}
			
			if (Strings.CI.containsAny(separator.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Separator must not contain newline, carriage return or tab");
			}
		}
	}
}
