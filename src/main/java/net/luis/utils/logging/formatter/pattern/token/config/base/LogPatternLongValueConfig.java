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
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import net.luis.utils.math.Radix;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternLongValueConfig(
	@NonNull Optional<Radix> radix,
	@NonNull Optional<Casing> casing,
	@NonNull Optional<LogPatternPaddingConfig> padding,
	@NonNull Optional<LogPatternDecorationConfig> decoration
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternLongValueConfig> CODEC = CodecBuilder.of(
		Codecs.enumLowerName(Radix.class).optional().optionalFieldOf("radix", Optional.empty(), LogPatternLongValueConfig::radix),
		Codecs.enumLowerName(Casing.class).optional().optionalFieldOf("casing", Optional.empty(), LogPatternLongValueConfig::casing),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternLongValueConfig::padding),
		LogPatternDecorationConfig.CODEC.optional().optionalFieldOf("decoration", Optional.empty(), LogPatternLongValueConfig::decoration)
	).create(LogPatternLongValueConfig::new);
	
	public LogPatternLongValueConfig {
		Objects.requireNonNull(radix, "Radix must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		Objects.requireNonNull(decoration, "Decoration config must not be null");
	}
	
	public @NonNull String format(long value) {
		String result = switch (this.radix.orElse(Radix.DECIMAL)) {
			case BINARY -> Long.toBinaryString(value);
			case OCTAL -> Long.toOctalString(value);
			case DECIMAL -> Long.toString(value);
			case HEXADECIMAL -> Long.toHexString(value);
		};
		return LogTokenFormatHelper.format(result, this.casing, this.padding, this.decoration);
	}
}
