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
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Align;
import net.luis.utils.logging.formatter.pattern.token.config.util.TruncateFrom;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternPaddingConfig(
	@NonNull OptionalInt minWidth,
	@NonNull OptionalInt maxWidth,
	@NonNull Optional<Align> align,
	@NonNull Optional<Character> padChar,
	@NonNull Optional<TruncateFrom> truncateFrom
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternPaddingConfig> CODEC = CodecBuilder.of(
		Codecs.OPTIONAL_INT.optionalFieldOf("minWidth", OptionalInt.empty(), LogPatternPaddingConfig::minWidth),
		Codecs.OPTIONAL_INT.optionalFieldOf("maxWidth", OptionalInt.empty(), LogPatternPaddingConfig::maxWidth),
		Codecs.enumLowerName(Align.class).optional().optionalFieldOf("align", Optional.empty(), LogPatternPaddingConfig::align),
		Codecs.CHARACTER.optional().optionalFieldOf("padChar", Optional.empty(), LogPatternPaddingConfig::padChar),
		Codecs.enumLowerName(TruncateFrom.class).optional().optionalFieldOf("truncateFrom", Optional.empty(), LogPatternPaddingConfig::truncateFrom)
	).create(LogPatternPaddingConfig::new);
	static final Set<Character> ILLEGAL_PAD_CHARS = Set.of('\n', '\r', '\t');
	
	public LogPatternPaddingConfig {
		Objects.requireNonNull(minWidth, "Min width must not be null");
		Objects.requireNonNull(maxWidth, "Max width must not be null");
		Objects.requireNonNull(align, "Align must not be null");
		Objects.requireNonNull(padChar, "Pad char must not be null");
		Objects.requireNonNull(truncateFrom, "Truncate from must not be null");
		
		if (minWidth.isPresent() && minWidth.getAsInt() < 0) {
			throw new LogPatternException("Min width must be non-negative: " + minWidth.getAsInt());
		}
		if (maxWidth.isPresent() && maxWidth.getAsInt() < 0) {
			throw new LogPatternException("Max width must be non-negative: " + maxWidth.getAsInt());
		}
		if (minWidth.isPresent() && maxWidth.isPresent() && minWidth.getAsInt() > maxWidth.getAsInt()) {
			throw new LogPatternException("Min width must not be greater than max width: " + minWidth.getAsInt() + " > " + maxWidth.getAsInt());
		}
		if (padChar.isPresent() && ILLEGAL_PAD_CHARS.contains(padChar.get())) {
			throw new LogPatternException("Pad char must not be a newline, carriage return or tab: " + padChar.get());
		}
	}
}
