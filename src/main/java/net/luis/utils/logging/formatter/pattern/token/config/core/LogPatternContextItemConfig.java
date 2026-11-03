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
import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternDecorationConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternContextItemConfig(
	@NonNull Optional<String> defaultValue,
	@NonNull Optional<Casing> casing,
	@NonNull Optional<LogPatternPaddingConfig> padding,
	@NonNull Optional<LogPatternDecorationConfig> decoration
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternContextItemConfig> CODEC = CodecBuilder.of(
		Codecs.STRING.optional().optionalFieldOf("defaultValue", Optional.empty(), LogPatternContextItemConfig::defaultValue),
		Codecs.enumLowerName(Casing.class).optional().optionalFieldOf("casing", Optional.empty(), LogPatternContextItemConfig::casing),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternContextItemConfig::padding),
		LogPatternDecorationConfig.CODEC.optional().optionalFieldOf("decoration", Optional.empty(), LogPatternContextItemConfig::decoration)
	).create(LogPatternContextItemConfig::new);
	
	public LogPatternContextItemConfig {
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		Objects.requireNonNull(decoration, "Decoration config must not be null");
	}
	
	private static @NonNull String extractKey(@NonNull String plainTokenContent) {
		Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
		
		String identifier = plainTokenContent;
		int configStart = identifier.indexOf('{');
		if (configStart >= 0) {
			identifier = identifier.substring(0, configStart);
		}
		return identifier.substring(identifier.indexOf(':') + 1);
	}
	
	@Override
	public @NonNull String format(@NonNull LogEvent event, @NonNull String plainTokenContent) {
		Objects.requireNonNull(event, "Log event must not be null");
		Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
		
		String result = event.context().get(extractKey(plainTokenContent)).map(String::valueOf).orElseGet(() -> this.defaultValue.orElse(""));
		return LogTokenFormatHelper.format(result, this.casing, this.padding, this.decoration);
	}
}
