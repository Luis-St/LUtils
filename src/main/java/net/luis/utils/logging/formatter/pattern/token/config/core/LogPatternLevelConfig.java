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

import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import net.luis.utils.logging.formatter.pattern.token.config.util.Embedded;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternLevelConfig(
	@NonNull OptionalInt length,
	@NonNull Optional<Casing> casing,
	@NonNull @Embedded(namespace = "padding") Optional<LogPatternPaddingConfig> paddingConfig
) implements LogPatternTokenConfig {
	
	public LogPatternLevelConfig {
		Objects.requireNonNull(length, "Length must not be null");
		Objects.requireNonNull(casing, "Casing mus not be null");
		Objects.requireNonNull(paddingConfig, "Padding config must not be null");
		
		if (length.isPresent() && length.getAsInt() < 0) {
			throw new LogPatternException("Length must not be negative");
		}
	}
}
