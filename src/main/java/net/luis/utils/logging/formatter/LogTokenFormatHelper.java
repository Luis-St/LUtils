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

package net.luis.utils.logging.formatter;

import net.luis.utils.logging.event.LogEvent;
import net.luis.utils.logging.formatter.pattern.token.config.LogPatternTokenConfig;
import net.luis.utils.logging.formatter.pattern.token.config.base.*;
import net.luis.utils.logging.formatter.pattern.token.config.util.Casing;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class LogTokenFormatHelper {
	
	public static @NonNull String format(
		@NonNull String value, @NonNull Optional<Casing> casing, @NonNull Optional<LogPatternPaddingConfig> padding, @NonNull Optional<LogPatternDecorationConfig> decoration
	) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(decoration, "Decoration config must not be null");
		
		String result = format(value, casing, padding);
		if (value.isEmpty() || decoration.isEmpty()) {
			return result;
		}
		return decoration.get().format(result);
	}
	
	public static @NonNull String format(@NonNull String value, @NonNull Optional<Casing> casing, @NonNull Optional<LogPatternPaddingConfig> padding) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(casing, "Casing must not be null");
		Objects.requireNonNull(padding, "Padding config must not be null");
		
		String result = value;
		if (casing.isPresent()) {
			result = switch (casing.get()) {
				case LOWER -> result.toLowerCase();
				case UPPER -> result.toUpperCase();
				case NONE -> result;
			};
		}
		
		if (padding.isPresent()) {
			return padding.get().format(result);
		}
		return result;
	}
	
	public static <T extends LogPatternTokenConfig> @NonNull LogTokenFormatter<T> configBased() {
		return new LogTokenFormatter<>() {
			@Override
			public @NonNull String format(@NonNull LogEvent event, @Nullable LogPatternTokenConfig config, @NonNull String plainTokenContent) {
				if (config == null) {
					throw new IllegalArgumentException("Unable to format log event based on the configuration, no configuration was provided"); // ToDo: better exception
				}
				return config.format(event, plainTokenContent);
			}
		};
	}
	
	public static @NonNull LogTokenFormatter<LogPatternStringValueConfig> stringValue(@NonNull Function<LogEvent, @Nullable String> provider, @NonNull String defaultValue) {
		Objects.requireNonNull(provider, "Value provider must not be null");
		Objects.requireNonNull(defaultValue, "Default value must not be null");
		
		return new LogTokenFormatter<>() {
			@Override
			public @NonNull String format(@NonNull LogEvent event, @Nullable LogPatternStringValueConfig config, @NonNull String plainTokenContent) {
				Objects.requireNonNull(event, "Log event must not be null");
				Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
				
				String value = provider.apply(event);
				if (value == null && config == null) {
					return defaultValue;
				} else if (value == null) {
					return LogTokenFormatHelper.format(defaultValue, config.casing(), config.padding(), config.decoration());
				} else if (config == null) {
					return value;
				}
				return LogTokenFormatHelper.format(value, config.casing(), config.padding(), config.decoration());
			}
		};
	}
	
	public static @NonNull LogTokenFormatter<LogPatternBooleanValueConfig> booleanValue(@NonNull Function<LogEvent, Boolean> provider) {
		Objects.requireNonNull(provider, "Value provider must not be null");
		
		return new LogTokenFormatter<>() {
			@Override
			public @NonNull String format(@NonNull LogEvent event, @Nullable LogPatternBooleanValueConfig config, @NonNull String plainTokenContent) {
				Objects.requireNonNull(event, "Log event must not be null");
				Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
				
				boolean value = provider.apply(event);
				if (config == null) {
					return Boolean.toString(value);
				}
				return config.format(value);
			}
		};
	}
	
	public static @NonNull LogTokenFormatter<LogPatternLongValueConfig> longValue(@NonNull Function<LogEvent, Long> provider) {
		Objects.requireNonNull(provider, "Value provider must not be null");
		
		return new LogTokenFormatter<>() {
			@Override
			public @NonNull String format(@NonNull LogEvent event, @Nullable LogPatternLongValueConfig config, @NonNull String plainTokenContent) {
				Objects.requireNonNull(event, "Log event must not be null");
				Objects.requireNonNull(plainTokenContent, "Plain token content must not be null");
				
				long value = provider.apply(event);
				if (config == null) {
					return Long.toString(value);
				}
				return config.format(value);
			}
		};
	}
}
