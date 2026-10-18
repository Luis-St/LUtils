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
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternBooleanValueConfig(
	@NonNull Optional<String> trueValue,
	@NonNull Optional<String> falseValue,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternBooleanValueConfig> CODEC = CodecBuilder.of(
		Codecs.STRING.optional().optionalFieldOf("trueValue", Optional.empty(), LogPatternBooleanValueConfig::trueValue),
		Codecs.STRING.optional().optionalFieldOf("falseValue", Optional.empty(), LogPatternBooleanValueConfig::falseValue),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternBooleanValueConfig::padding)
	).create(LogPatternBooleanValueConfig::new);
	
	public LogPatternBooleanValueConfig {
		Objects.requireNonNull(trueValue, "True value must not be null");
		Objects.requireNonNull(falseValue, "False value must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
	}
}
