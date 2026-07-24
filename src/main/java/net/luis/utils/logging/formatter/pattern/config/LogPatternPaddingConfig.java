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

package net.luis.utils.logging.formatter.pattern.config;

import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@Embeddable
public record LogPatternPaddingConfig(
	@NotNull OptionalInt minWidth,
	@NotNull OptionalInt maxWidth,
	@NotNull Optional<Align> align,
	@NotNull Optional<Character> padChar,
	@NotNull Optional<TruncateFrom> truncateFrom
) {
	
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
