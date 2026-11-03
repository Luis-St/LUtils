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

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.*;
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.AnsiColor;
import net.luis.utils.logging.formatter.pattern.token.config.util.AnsiStyle;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternDecorationConfig(
	@NonNull Optional<String> prefix,
	@NonNull Optional<String> suffix,
	@NonNull Optional<AnsiColor> color,
	@NonNull @Unmodifiable List<AnsiStyle> style
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternDecorationConfig> CODEC = CodecBuilder.of(
		Codecs.STRING.optional().optionalFieldOf("prefix", Optional.empty(), LogPatternDecorationConfig::prefix),
		Codecs.STRING.optional().optionalFieldOf("suffix", Optional.empty(), LogPatternDecorationConfig::suffix),
		Codecs.enumLowerName(AnsiColor.class).optional().optionalFieldOf("color", Optional.empty(), LogPatternDecorationConfig::color),
		Codecs.enumLowerName(AnsiStyle.class).list().optionalFieldOf("style", Lists.newArrayList(), LogPatternDecorationConfig::style)
	).create(LogPatternDecorationConfig::new);
	private static final String ANSI_ESCAPE = "\u001B[";
	private static final String ANSI_RESET = ANSI_ESCAPE + "0m";
	
	public LogPatternDecorationConfig {
		Objects.requireNonNull(prefix, "Prefix must not be null");
		Objects.requireNonNull(suffix, "Suffix must not be null");
		Objects.requireNonNull(color, "Color must not be null");
		Objects.requireNonNull(style, "Style must not be null");
		
		if (prefix.isPresent() && prefix.get().isEmpty()) {
			throw new LogPatternException("Prefix must not be empty");
		}
		if (suffix.isPresent() && suffix.get().isEmpty()) {
			throw new LogPatternException("Suffix must not be empty");
		}
		if (style.stream().anyMatch(Objects::isNull)) {
			throw new LogPatternException("Style list must not contain null values");
		}
		
		style = Collections.unmodifiableList(style);
	}
	
	public @NonNull String format(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		String result = value;
		if (this.color.isPresent() || !this.style.isEmpty()) {
			StringJoiner codes = new StringJoiner(";", ANSI_ESCAPE, "m");
			for (AnsiStyle element : this.style) {
				codes.add(String.valueOf(element.getCode()));
			}
			this.color.ifPresent(element -> codes.add(String.valueOf(element.getCode())));
			result = codes + result + ANSI_RESET;
		}
		return this.prefix.orElse("") + result + this.suffix.orElse("");
	}
}
