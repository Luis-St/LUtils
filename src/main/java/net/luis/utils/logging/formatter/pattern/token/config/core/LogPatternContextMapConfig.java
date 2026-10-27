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

import com.google.common.collect.*;
import net.luis.utils.io.codec.*;
import net.luis.utils.logging.formatter.pattern.LogPatternException;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.LogPatternPaddingConfig;
import org.apache.commons.lang3.Strings;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("RedundantUnmodifiable")
public record LogPatternContextMapConfig(
	@NonNull @Unmodifiable Map</*Key*/ String, /*Default value*/ String> include,
	@NonNull @Unmodifiable List<String> exclude,
	@NonNull Optional<String> kvSeparator,
	@NonNull Optional<String> entrySeparator,
	@NonNull Optional<LogPatternPaddingConfig> padding
) implements LogPatternTokenConfig {
	
	public static final Codec<LogPatternContextMapConfig> CODEC = CodecBuilder.of(
		Codecs.map(Codecs.STRING).optionalFieldOf("include", Maps.newLinkedHashMap(), LogPatternContextMapConfig::include),
		Codecs.STRING.list().optionalFieldOf("exclude", Lists.newArrayList(), LogPatternContextMapConfig::exclude),
		Codecs.STRING.optional().optionalFieldOf("kvSeparator", Optional.empty(), LogPatternContextMapConfig::kvSeparator),
		Codecs.STRING.optional().optionalFieldOf("entrySeparator", Optional.empty(), LogPatternContextMapConfig::entrySeparator),
		LogPatternPaddingConfig.CODEC.optional().optionalFieldOf("padding", Optional.empty(), LogPatternContextMapConfig::padding)
	).create(LogPatternContextMapConfig::new);
	static final String[] ILLEGAL_CHARS = { "\n", "\r", "\t" };
	
	public LogPatternContextMapConfig {
		Objects.requireNonNull(include, "Include list must not be null");
		Objects.requireNonNull(exclude, "Exclude list must not be null");
		Objects.requireNonNull(kvSeparator, "Key-value separator must not be null");
		Objects.requireNonNull(entrySeparator, "Entry separator must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		
		if (include.entrySet().stream().anyMatch(pair -> pair.getValue() == null || pair.getValue().isBlank())) {
			throw new LogPatternException("Include list must not contain null or blank values");
		}
		if (exclude.stream().anyMatch(Objects::isNull)) {
			throw new LogPatternException("Exclude list must not contain null values");
		}
		Set<String> intersection = Sets.intersection(include.keySet(), Sets.newHashSet(exclude));
		if (!intersection.isEmpty()) {
			throw new LogPatternException("The following context keys are declared in the include and exclude list: " + String.join(",", intersection));
		}
		if (kvSeparator.isPresent()) {
			if (kvSeparator.get().isEmpty()) {
				throw new LogPatternException("Key-value separator must not be empty");
			}
			
			if (Strings.CI.containsAny(kvSeparator.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Key-value separator must not be a newline, carriage return or tab: " + kvSeparator.get());
			}
		}
		if (entrySeparator.isPresent()) {
			if (entrySeparator.get().isEmpty()) {
				throw new LogPatternException("Entry separator must not be empty");
			}
			
			if (Strings.CI.containsAny(entrySeparator.get(), ILLEGAL_CHARS)) {
				throw new LogPatternException("Entry separator must not be a newline, carriage return or tab: " + entrySeparator.get());
			}
		}
		
		include = Collections.unmodifiableMap(include);
		exclude = Collections.unmodifiableList(exclude);
	}
}
