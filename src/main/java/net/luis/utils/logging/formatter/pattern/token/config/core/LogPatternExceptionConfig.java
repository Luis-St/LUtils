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

import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternExceptionConfig(
	@NonNull OptionalInt depth,
	@NonNull Optional<Boolean> omitCommonFrames,
	@NonNull Optional<Boolean> causes,
	@NonNull OptionalInt maxCauseDepth,
	@NonNull Optional<Boolean> suppressed
) {
	
	public LogPatternExceptionConfig {
		Objects.requireNonNull(depth, "Depth must not be null");
		Objects.requireNonNull(omitCommonFrames, "Omit common frames must not be null");
		Objects.requireNonNull(causes, "Causes must not be null");
		Objects.requireNonNull(maxCauseDepth, "Max cause depth must not be null");
		Objects.requireNonNull(suppressed, "Suppressed must not be null");
		
		if (depth.isPresent() && depth.getAsInt() < 0) {
			throw new IllegalArgumentException("Depth must not be negative");
		}
		if (maxCauseDepth.isPresent() && maxCauseDepth.getAsInt() < 0) {
			throw new IllegalArgumentException("Max cause depth must not be negative");
		}
	}
}
