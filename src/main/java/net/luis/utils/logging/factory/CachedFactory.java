package net.luis.utils.logging.factory;

import net.luis.utils.logging.LoggerConfiguration;
import net.luis.utils.logging.LoggingUtils;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Cached Log4j2 configuration factory.<br>
 * This factory always returns the cached configuration of the {@link LoggingUtils} class<br>
 * or the default configuration if {@link LoggingUtils} has not been initialized.<br>
 * <p>
 *     Can be used with spring boot to auto replace the spring boot configuration with the logger configuration used before spring boot.<br>
 *     The factory is will be automatically registered if the spring boot framework is detected<br>
 *     and the {@link LoggingUtils} class is initialized.<br>
 * </p>
 * <p>
 *     The factory can also be manually registered by setting the system property "log4j.configurationFactory"<br>
 *     to the fully qualified class name of this factory.<br>
 *     This must be done before the log4j2 framework is initialized.<br>
 * </p>
 *
 * @see LoggingUtils
 *
 * @author Luis-St
 */
@Order(50)
@Plugin(name = "CachedFactory", category = ConfigurationFactory.CATEGORY)
public class CachedFactory extends ConfigurationFactory {
	
	/**
	 * @return The supported file extensions of the factory
	 */
	@Override
	protected String @NotNull [] getSupportedTypes() {
		return new String[] {"*"};
	}
	
	/**
	 * @return The default configuration
	 */
	private @NotNull Configuration defaultConfiguration() {
		return new LoggerConfiguration("*").build();
	}
	
	/**
	 * @param context The logger context
	 * @param source The configuration source
	 * @return The configuration currently cached or the default configuration if none is cached.<br>
	 */
	@Override
	public @NotNull Configuration getConfiguration(@NotNull LoggerContext context, @NotNull ConfigurationSource source) {
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
	 * @return The configuration currently cached or the default configuration if none is cached.
	 */
	@Override
	public @NotNull Configuration getConfiguration(@NotNull LoggerContext context, @NotNull String name, @NotNull URI location) {
		LoggerConfiguration configuration = LoggingUtils.getConfiguration();
		if (configuration != null) {
			return configuration.build();
		} else {
			return this.defaultConfiguration();
		}
	}
}
