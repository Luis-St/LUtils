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
	@NonNull Optional<Integer> minWidth,
	@NonNull Optional<Integer> maxWidth,
	@NonNull Optional<Align> align,
	@NonNull Optional<Character> padChar,
	@NonNull Optional<TruncateFrom> truncateFrom
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternPaddingConfig> CODEC = CodecBuilder.of(
		Codecs.INTEGER.optional().optionalFieldOf("minWidth", Optional.empty(), LogPatternPaddingConfig::minWidth),
		Codecs.INTEGER.optional().optionalFieldOf("maxWidth", Optional.empty(), LogPatternPaddingConfig::maxWidth),
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
		
		if (minWidth.isPresent() && minWidth.get() < 0) {
			throw new LogPatternException("Min width must be non-negative: " + minWidth.get());
		}
		if (maxWidth.isPresent() && maxWidth.get() <= 0) {
			throw new LogPatternException("Max width must be positive: " + maxWidth.get());
		}
		if (truncateFrom.isPresent() && maxWidth.isEmpty()) {
			throw new LogPatternException("Truncate from can only be used with max width");
		}
		if (minWidth.isEmpty()) {
			if (align.isPresent()) {
				throw new LogPatternException("Align can only be used with min width");
			}
			if (padChar.isPresent()) {
				throw new LogPatternException("Pad char can only be used with min width");
			}
		}
		if (minWidth.isPresent() && maxWidth.isPresent() && minWidth.get() > maxWidth.get()) {
			throw new LogPatternException("Min width must not be greater than max width: " + minWidth.get() + " > " + maxWidth.get());
		}
		if (padChar.isPresent() && ILLEGAL_PAD_CHARS.contains(padChar.get())) {
			throw new LogPatternException("Pad char must not be a newline, carriage return or tab: " + padChar.get());
		}
	}
}
