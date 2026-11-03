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

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.*;
import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.LogTokenFormatHelper;
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternDecorationConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternMessageConfig(
	@NonNull Optional<Integer> maxLength,
	@NonNull Optional<String> ellipsis,
	@NonNull @Unmodifiable List<Character> escapedCharacters,
	@NonNull Optional<String> escapeSequence,
	@NonNull Optional<Casing> casing,
	@NonNull Optional<LogPatternPaddingConfig> padding,
	@NonNull Optional<LogPatternDecorationConfig> decoration
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternMessageConfig> CODEC = CodecBuilder.of(
		Codecs.INTEGER.optional().optionalFieldOf("maxLength", Optional.empty(), LogPatternMessageConfig::maxLength),
		Codecs.STRING.optional().optionalFieldOf("ellipsis", Optional.empty(), LogPatternMessageConfig::ellipsis),
		Codecs.CHARACTER.list().optionalFieldOf("escapedCharacters", Lists.newArrayList(), LogPatternMessageConfig::escapedCharacters),
		Codecs.STRING.optional().optionalFieldOf("escapeSequence", Optional.empty(), LogPatternMessageConfig::escapeSequence),
		Codecs.enumLowerName(Casing.class).optional().optionalFieldOf("casing", Optional.empty(), LogPatternMessageConfig::casing),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternMessageConfig::padding),
		LogPatternDecorationConfig.CODEC.optional().optionalFieldOf("decoration", Optional.empty(), LogPatternMessageConfig::decoration)
	).create(LogPatternMessageConfig::new);
	static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternMessageConfig {
		Objects.requireNonNull(maxLength, "Max length must not be null");
		Objects.requireNonNull(ellipsis, "Ellipsis must not be null");
		Objects.requireNonNull(escapedCharacters, "Escaped characters must not be null");
		Objects.requireNonNull(escapeSequence, "Escape sequence must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		Objects.requireNonNull(decoration, "Decoration config must not be null");
		
		if (maxLength.isPresent() && maxLength.get() < 0) {
			throw new LogPatternException("Max length must not be negative");
		}
		if (ellipsis.isPresent()) {
			if (ellipsis.get().isEmpty()) {
				throw new LogPatternException("Ellipsis must not be empty");
			}
			if (maxLength.isEmpty()) {
				throw new LogPatternException("Ellipsis can only be used with max length");
			}
			if (ellipsis.get().length() > maxLength.get()) {
				throw new LogPatternException("Ellipsis length must not be greater than max length");
			}
		}
		if (!escapedCharacters.isEmpty() && escapeSequence.isEmpty()) {
			throw new LogPatternException("Escaped characters can only be used with an escape sequence");
		}
		if (escapeSequence.isPresent()) {
			if (escapedCharacters.isEmpty()) {
				throw new LogPatternException("Escape sequence can only be used with escaped characters");
			}
			if (Strings.CI.containsAny(escapeSequence.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Escape sequence must not contain newline, carriage return or tab");
			}
		}
	}
	
	@Override
	public @NonNull String format(@NonNull LogEvent event, @NonNull String plainTokenContent) {
		Objects.requireNonNull(event, "Log event must not be null");
		Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
		
		String message = event.message().message();
		String ellipsis = "";
		if (this.maxLength.isPresent() && message.length() > this.maxLength.get()) {
			ellipsis = this.ellipsis.orElse("");
			message = message.substring(0, this.maxLength.get() - ellipsis.length());
		}
		
		if (this.escapeSequence.isPresent()) {
			String escapeSequence = this.escapeSequence.get();
			StringBuilder escaped = new StringBuilder(message.length());
			for (char c : message.toCharArray()) {
				if (this.escapedCharacters.contains(c)) {
					escaped.append(escapeSequence);
				}
				escaped.append(c);
			}
			message = escaped.toString();
		}
		return LogTokenFormatHelper.format(message + ellipsis, this.casing, this.padding, this.decoration);
	}
}
