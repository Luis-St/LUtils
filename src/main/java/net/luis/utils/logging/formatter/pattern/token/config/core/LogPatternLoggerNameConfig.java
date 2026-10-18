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
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternLoggerNameConfig(
	@NonNull OptionalInt segments,
	@NonNull Optional<Casing> casing,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternLoggerNameConfig> CODEC = CodecBuilder.of(
		Codecs.OPTIONAL_INT.optionalFieldOf("segments", OptionalInt.empty(), LogPatternLoggerNameConfig::segments),
		Codecs.enumLowerName(Casing.class).optional().optionalFieldOf("casing", Optional.empty(), LogPatternLoggerNameConfig::casing),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternLoggerNameConfig::padding)
	).create(LogPatternLoggerNameConfig::new);
	
	public LogPatternLoggerNameConfig {
		Objects.requireNonNull(segments, "Segments must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		
		if (segments.isPresent() && segments.getAsInt() < 0) {
			throw new IllegalArgumentException("Segments must not be negative");
		}
	}
}
