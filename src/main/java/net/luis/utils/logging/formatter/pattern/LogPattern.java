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

package net.luis.utils.logging.formatter.pattern;

import net.luis.utils.logging.formatter.pattern.token.*;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPattern(@NonNull String rawPattern, @NonNull List<LogPatternToken> tokens) {
	
	private static final LogPatternParser DEFAULT_PARSER = new LogPatternParser(Set.of(StandardLogPatternTokenType.values()));
	
	public LogPattern {
		Objects.requireNonNull(rawPattern, "Raw pattern must not be null");
		tokens = List.copyOf(Objects.requireNonNull(tokens, "Tokens must not be null"));
	}
	
	public static @NonNull LogPattern parse(@NonNull String rawPattern) {
		return DEFAULT_PARSER.parse(rawPattern);
	}
	
	public static @NonNull LogPattern parse(@NonNull Set<LogPatternTokenType> tokenTypes, @NonNull String rawPattern) {
		return new LogPatternParser(Objects.requireNonNull(tokenTypes, "Token types must not be null")).parse(rawPattern);
	}
}
