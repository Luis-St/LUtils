/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.logging.factory;

import net.luis.utils.logging.LoggerConfiguration;
import net.luis.utils.logging.LoggingUtils;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Objects;

/**
 * Spring logging configuration factory.<br>
 * Due to the fact that spring boot replaces the logging configuration with its own,<br>
 * this factory is used to replace the spring boot configuration with the logger configuration used before spring boot.<br>
 * <p>
 *     This factory must be registered via the system property 'log4j.configurationFactory'<br>
 *     with the fully qualified class name as its value.<br>
 *     Alternatively, the factory can be registered by calling {@link LoggingUtils#registerSpringFactory()}.
 * </p>
 */
@Order(50)
@Plugin(name = "SpringFactory", category = ConfigurationFactory.CATEGORY)
public class SpringFactory extends ConfigurationFactory {
	
	/**
	 * Constructs a new spring factory for logging configuration.<br>
	 */
	public SpringFactory() {}
	
	/**
	 * Returns the supported file extensions of the factory.<br>
	 * The factory does not depend on the file extension, so it supports all file extensions.<br>
	 *
	 * @return The supported file extensions
	 */
	@Override
	protected String @NotNull [] getSupportedTypes() {
		return new String[] { "*" };
	}
	
	/**
	 * Creates a new configuration with no additional changes.<br>
	 * @return The default configuration
	 */
	private @NotNull Configuration defaultConfiguration() {
		return new LoggerConfiguration("*").build();
	}
	
	/**
	 * @param context The logger context
	 * @param source The configuration source
	 * @return The configuration used before spring boot or the default configuration if none is cached
	 * @throws NullPointerException If the context or source is null
	 */
	@Override
	public @NotNull Configuration getConfiguration(@NotNull LoggerContext context, @NotNull ConfigurationSource source) {
		Objects.requireNonNull(context, "Logger context must not be null");
		Objects.requireNonNull(source, "Configuration source must not be null");
		
		LoggerConfiguration configuration = LoggingUtils.getConfiguration();
		if (configuration != null) {
			return configuration.build();
		} else {
			return this.defaultConfiguration();
		}
	}
	
	/**
	 * @param context The logger context
	 * @param name The configuration name.
	 * @param location The configuration location.
	 * @return The configuration used before spring boot or the default configuration if none is cached
	 * @throws NullPointerException If the context, name, or location is null
	 */
	@Override
	public @NotNull Configuration getConfiguration(@NotNull LoggerContext context, @NotNull String name, @NotNull URI location) {
		Objects.requireNonNull(context, "Logger context must not be null");
		Objects.requireNonNull(name, "Configuration name must not be null");
		Objects.requireNonNull(location, "Configuration location must not be null");
		
		LoggerConfiguration configuration = LoggingUtils.getConfiguration();
		if (configuration != null) {
			return configuration.build();
		} else {
			return this.defaultConfiguration();
		}
	}
}
