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

package net.luis.utils.logging.formatter.pattern.token.config.base;

import net.luis.utils.io.codec.*;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternStringValueConfig(
	@NonNull Optional<Casing> casing,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternStringValueConfig> CODEC = CodecBuilder.of(
		Codecs.enumLowerName(Casing.class).optional().optionalFieldOf("casing", Optional.empty(), LogPatternStringValueConfig::casing),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternStringValueConfig::padding)
	).create(LogPatternStringValueConfig::new);
	
	public LogPatternStringValueConfig {
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
	}
}
