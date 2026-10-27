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
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record LogPatternExceptionConfig(
	@NonNull Optional<Integer> depth,
	@NonNull Optional<Boolean> omitCommonFrames,
	@NonNull Optional<Boolean> causes,
	@NonNull Optional<Integer> maxCauseDepth,
	@NonNull Optional<Boolean> suppressed
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternExceptionConfig> CODEC = CodecBuilder.of(
		Codecs.INTEGER.optional().optionalFieldOf("depth", Optional.empty(), LogPatternExceptionConfig::depth),
		Codecs.BOOLEAN.optional().optionalFieldOf("omitCommonFrames", Optional.empty(), LogPatternExceptionConfig::omitCommonFrames),
		Codecs.BOOLEAN.optional().optionalFieldOf("causes", Optional.empty(), LogPatternExceptionConfig::causes),
		Codecs.INTEGER.optional().optionalFieldOf("maxCauseDepth", Optional.empty(), LogPatternExceptionConfig::maxCauseDepth),
		Codecs.BOOLEAN.optional().optionalFieldOf("suppressed", Optional.empty(), LogPatternExceptionConfig::suppressed)
	).create(LogPatternExceptionConfig::new);
	
	public LogPatternExceptionConfig {
		Objects.requireNonNull(depth, "Depth must not be null");
		Objects.requireNonNull(omitCommonFrames, "Omit common frames must not be null");
		Objects.requireNonNull(causes, "Causes must not be null");
		Objects.requireNonNull(maxCauseDepth, "Max cause depth must not be null");
		Objects.requireNonNull(suppressed, "Suppressed must not be null");
		
		if (depth.isPresent() && depth.get() < 0) {
			throw new LogPatternException("Depth must not be negative");
		}
		if (maxCauseDepth.isPresent()) {
			if (maxCauseDepth.get() < 0) {
				throw new LogPatternException("Max cause depth must not be negative");
			}
			if (causes.isPresent() && !causes.get()) {
				throw new LogPatternException("Max cause depth can not be used if causes are disabled");
			}
		}
	}
}
