package net.luis.utils.logging;

import com.google.common.collect.Lists;
import net.luis.utils.logging.factory.CachedFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Logging utility class.<br>
 * <br>
 * This class is used to initialize or load a logging configuration.<br>
 * It also provides methods to enable/disable logging.<br>
 *
 * @author Luis-St
 */
public class LoggingUtils {
	
	private static final List<String> CONFIGURED_LOGGERS = Lists.newArrayList();
	/**
	 * Supported console logging levels.<br>
	 */
	static final Level[] CONSOLE_LEVELS = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL};
	/**
	 * Supported file logging levels.<br>
	 */
	static final Level[] FILE_LEVELS = new Level[] {Level.DEBUG, Level.INFO, Level.ERROR};
	private static LoggerConfiguration configuration = null;
	private static boolean registeredFactory = false;
	
	//region Configuration
	/**
	 * Loads the logging configuration from the system properties and initializes the logging system.<br>
	 * @deprecated Use the load method which takes a logger name as parameter instead
	 * @throws IllegalStateException If the logging system has already been initialized
	 */
	@Deprecated(forRemoval = true) // ToDo: Remove in the next versions
	public static void load() {
		initialize(LoggingHelper.load(Lists.newArrayList("*")));
		LoggingHelper.configure();
	}
	
	/**
	 * Loads the logging configuration for the given loggers from the system properties and initializes the logging system.<br>
	 * Loading the logger should be done as early as possible in the application lifecycle.<br>
	 * Ideally, in the static-initializer of the main class as first call.<br>
	 * @param loggers The loggers to load the configuration for
	 * @throws NullPointerException If the logger list is null
	 * @throws IllegalArgumentException If the logger list is empty or contains no valid logger
	 * @throws IllegalStateException If the logging system has already been initialized
	 * @see #load(List)
	 */
	public static void load(String @NotNull ... loggers) {
		load(Arrays.asList(loggers));
	}
	
	/**
	 * Loads the logging configuration for the given loggers from the system properties and initializes the logging system.<br>
	 * Loading the logger should be done as early as possible in the application lifecycle.<br>
	 * Ideally, in the static-initializer of the main class as first call.<br>
	 * @param loggers The loggers to load the configuration for
	 * @throws NullPointerException If the logger list is null
	 * @throws IllegalArgumentException If the logger list is empty or contains no valid logger
	 * @throws IllegalStateException If the logging system has already been initialized
	 * @see #initialize(LoggerConfiguration, boolean)
	 */
	public static void load(@NotNull List<String> loggers) {
		initialize(LoggingHelper.load(loggers));
		LoggingHelper.configure();
	}
	
	/**
	 * Initializes the logging system with the default configuration.<br>
	 * The logger should be initialized as early as possible in the application lifecycle.<br>
	 * Ideally, in the static-initializer of the main class as first call.<br>
	 * @deprecated The default configuration is not recommended anymore because it uses wildcard loggers.
	 * @throws IllegalStateException If the logging system has already been initialized
	 * @see LoggerConfiguration#DEFAULT
	 * @see #initialize(LoggerConfiguration)
	 * @see #initialize(LoggerConfiguration, boolean)
	 */
	@Deprecated(forRemoval = true) // ToDo: Remove in the next versions
	public static void initialize() {
		initialize(LoggerConfiguration.DEFAULT);
	}
	
	/**
	 * Initializes the logging system with the given configuration.<br>
	 * The logger should be initialized as early as possible in the application lifecycle.<br>
	 * Ideally, in the static-initializer of the main class as first call.<br>
	 * Example:<br>
	 * <pre>{@code
	 * public class Main {
	 *
	 * 	private static final Logger LOGGER;
	 *
	 * 	public static void main(String[] args) {
	 * 		// ...
	 *    }
	 *
	 * 	static {
	 * 		// Initialize the logging system her
	 * 		LOGGER = LogManager.getLogger(Main.class);
	 *    }
	 * }
	 * }</pre>
	 * @param configuration The configuration to use
	 * @throws NullPointerException If the configuration is null
	 * @throws IllegalStateException If the logging system has already been initialized
	 * @see #initialize(LoggerConfiguration, boolean)
	 */
	public static void initialize(@NotNull LoggerConfiguration configuration) {
		initialize(configuration, false);
	}
	
	/**
	 * Initializes the logging system with the given configuration.<br>
	 * The logger should be initialized as early as possible in the application lifecycle.<br>
	 * Ideally, in the static-initializer of the main class as first call.<br>
	 * Example:<br>
	 * <pre>{@code
	 * public class Main {
	 *
	 * 	private static final Logger LOGGER;
	 *
	 * 	public static void main(String[] args) {
	 * 		// ...
	 *    }
	 *
	 * 	static {
	 * 		// Initialize the logging system her
	 * 		LOGGER = LogManager.getLogger(Main.class);
	 *    }
	 * }
	 * }</pre>
	 * @param configuration The configuration to use
	 * @param override Whether to override the current configuration if the logging system has already been initialized
	 * @throws NullPointerException If the configuration is null
	 * @throws IllegalStateException If the logging system has already been initialized and override is false
	 */
	public static void initialize(@NotNull LoggerConfiguration configuration, boolean override) {
		if (!isInitialized()) {
			initializeInternal(Objects.requireNonNull(configuration, "Logger configuration must not be null"));
		} else if (override) {
			initializeInternal(Objects.requireNonNull(configuration, "Logger configuration must not be null"));
		} else {
			throw new IllegalStateException("Logging has already been initialized");
		}
	}
	
	/**
	 * Initializes the logging system with the given configuration if it has not already been initialized.<br>
	 * @param configuration The configuration to use
	 * @return Whether the logging system has been initialized
	 * @throws NullPointerException If the configuration is null
	 * @see #initialize(LoggerConfiguration)
	 * @see #initialize(LoggerConfiguration, boolean)
	 */
	public static boolean initializeSafe(@NotNull LoggerConfiguration configuration) {
		if (!isInitialized()) {
			initialize(Objects.requireNonNull(configuration, "Logger configuration must not be null"));
			return true;
		}
		return false;
	}
	
	/**
	 * Initializes the log4j2 configurator with the given configuration.<br>
	 * @param configuration The configuration to use
	 * @throws NullPointerException If the configuration is null
	 */
	private static void initializeInternal(@NotNull LoggerConfiguration configuration) {
		Objects.requireNonNull(configuration, "Logger configuration must not be null");
		Configuration config = configuration.build();
		Configurator.initialize(config);
		LoggingUtils.configuration = configuration;
		CONFIGURED_LOGGERS.clear();
		List<String> loggers = Lists.newArrayList(config.getLoggers().keySet());
		loggers.removeIf(String::isEmpty);
		CONFIGURED_LOGGERS.addAll(loggers);
		tryRegisterFactory();
	}
	
	/**
	 * Reconfigures the logging system with the given configuration.<br>
	 * Allows to change the logging configuration after it was initialized.<br>
	 * @param configuration The configuration to use
	 * @throws NullPointerException If the configuration is null
	 */
	public static void reconfigure(@NotNull LoggerConfiguration configuration) {
		Objects.requireNonNull(configuration, "Logger configuration must not be null");
		Configuration config = configuration.build();
		Configurator.reconfigure(config);
		LoggingUtils.configuration = configuration;
		CONFIGURED_LOGGERS.clear();
		List<String> loggers = Lists.newArrayList(config.getLoggers().keySet());
		loggers.removeIf(String::isEmpty);
		CONFIGURED_LOGGERS.addAll(loggers);
	}
	
	/**
	 * Tries to initialize the logging system with the given configuration,<br>
	 * if the logging system has already been initialized, the configuration will be reconfigured.<br>
	 * @param configuration The configuration to use
	 * @throws NullPointerException If the configuration is null
	 */
	public static void initializeOrReconfigure(@NotNull LoggerConfiguration configuration) {
		if (!initializeSafe(configuration)) {
			reconfigure(configuration);
		}
	}
	
	/**
	 * Checks whether the logging system has been initialized.<br>
	 * @return True if the logging system has been initialized, false otherwise
	 */
	public static boolean isInitialized() {
		return configuration != null;
	}
	
	/**
	 * Checks whether the cached factory has been automatically registered.<br>
	 * @return True if the cached factory has been registered, false otherwise
	 */
	public static boolean hasFactoryBeenRegistered() {
		return registeredFactory;
	}
	
	/**
	 * @return The current configuration or null if the logging system has not been initialized
	 */
	public static @Nullable LoggerConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * @return The names of all the configured loggers as an unmodifiable list
	 */
	public static @NotNull @Unmodifiable List<String> getConfiguredLoggers() {
		return List.copyOf(CONFIGURED_LOGGERS);
	}
	
	/**
	 * Checks whether the root logger has been configured.<br>
	 * If this returns true, other loggers were ignored or not configured.<br>
	 * @return True if the root logger has been configured, false otherwise
	 */
	public static boolean isRootLoggerConfigured() {
		return CONFIGURED_LOGGERS.contains("*");
	}
	//endregion
	
	//region Enable logging
	
	/**
	 * Enables logging for the given logging type and level.<br>
	 * @param type  The logging type to enable logging for
	 * @param level The level to enable logging for
	 * @throws NullPointerException If the level or logging type is null
	 * @throws IllegalArgumentException If the level is 'off' or the combination of logging type and level is invalid
	 * @throws IllegalStateException If the appender for the given logging type and level was not found
	 */
	public static void enable(@NotNull LoggingType type, @NotNull Level level) {
		if (level == Level.OFF) {
			throw new IllegalArgumentException("Unable to enable logging of type '" + type + "' for level 'off'");
		} else if (level == Level.ALL) {
			Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::enableConsole);
		} else {
			enableAppender(getLogger(type, level));
		}
	}
	
	/**
	 * Enables console logging for the given level.<br>
	 * @param level The level to enable console logging for
	 * @throws NullPointerException If the level is null
	 * @throws IllegalArgumentException If the level is not supported by console logging
	 * @throws IllegalStateException If the appender for the given console logging level was not found
	 * @see #enable(LoggingType, Level)
	 */
	public static void enableConsole(@NotNull Level level) {
		enable(LoggingType.CONSOLE, level);
	}
	
	/**
	 * Enables console logging for all levels.<br>
	 * @deprecated Use {@link #enableConsole(Level)} with {@link Level#ALL} instead
	 * @throws IllegalArgumentException If a level is not supported by console logging
	 * @throws IllegalStateException If a console logging appender was not found
	 * @see #enable(LoggingType, Level)
	 */
	@Deprecated // ToDo: Mark as for removal in the next versions
	public static void enableConsole() {
		Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::enableConsole);
	}
	
	/**
	 * Enables file logging for the given level.<br>
	 * @param level The level to enable file logging for
	 * @throws NullPointerException If the level is null
	 * @throws IllegalArgumentException If the level is not supported by file logging
	 * @throws IllegalStateException If the appender for the given file logging level was not found
	 * @see #enable(LoggingType, Level)
	 */
	public static void enableFile(@NotNull Level level) {
		enable(LoggingType.FILE, level);
	}
	
	/**
	 * Enables file logging for all levels.<br>
	 * @deprecated Use {@link #enableFile(Level)} with {@link Level#ALL} instead
	 * @throws IllegalArgumentException If a level is not supported by file logging
	 * @throws IllegalStateException If a file logging appender was not found
	 * @see #enable(LoggingType, Level)
	 */
	@Deprecated // ToDo: Mark as for removal in the next versions
	public static void enableFile() {
		Arrays.stream(FILE_LEVELS).forEach(LoggingUtils::enableFile);
	}
	//endregion
	
	//region Disable logging
	
	/**
	 * Disables logging for the given logging type and level.<br>
	 * @param type  The logging type to disable logging for
	 * @param level The level to disable logging for
	 * @throws NullPointerException If the level or logging type is null
	 * @throws IllegalArgumentException If the level is 'off' or the combination of logging type and level is invalid
	 */
	public static void disable(@NotNull LoggingType type, @NotNull Level level) {
		if (level == Level.OFF) {
			throw new IllegalArgumentException("Unable to disable logging of type '" + type + "' for level 'off'");
		} else if (level == Level.ALL) {
			Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::disableConsole);
		} else {
			disableAppender(getLogger(type, level));
		}
	}
	
	/**
	 * Disables console logging for the given level.<br>
	 * @param level The level to disable console logging for
	 * @throws NullPointerException If the level is null
	 * @throws IllegalArgumentException If the level is not supported by console logging
	 * @see #disable(LoggingType, Level)
	 */
	public static void disableConsole(@NotNull Level level) {
		disable(LoggingType.CONSOLE, level);
	}
	
	/**
	 * Disables console logging for all levels.<br>
	 * @deprecated Use {@link #disableConsole(Level)} with {@link Level#ALL} instead
	 * @throws IllegalArgumentException If a level is not supported by console logging
	 * @throws IllegalStateException If a console logging appender was not found
	 * @see #disable(LoggingType, Level)
	 */
	@Deprecated // ToDo: Mark as for removal in the next versions
	public static void disableConsole() {
		Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::disableConsole);
	}
	
	/**
	 * Disables file logging for the given level.<br>
	 * @param level The level to disable file logging for
	 * @throws NullPointerException If the level is null
	 * @throws IllegalArgumentException If the level is not supported by file logging
	 * @see #disable(LoggingType, Level)
	 */
	public static void disableFile(@NotNull Level level) {
		disable(LoggingType.FILE, level);
	}
	
	/**
	 * Disables file logging for all levels.<br>
	 * @deprecated Use {@link #disableFile(Level)} with {@link Level#ALL} instead
	 * @throws IllegalArgumentException If a level is not supported by file logging
	 * @throws IllegalStateException If a file logging appender was not found
	 * @see #disable(LoggingType, Level)
	 */
	@Deprecated // ToDo: Mark as for removal in the next versions
	public static void disableFile() {
		Arrays.stream(FILE_LEVELS).forEach(LoggingUtils::disableFile);
	}
	//endregion
	
	//region Helper methods
	
	/**
	 * Tries to register the cached configuration factory.<br>
	 * If the spring boot framework is found, the factory will be registered,<br>
	 * otherwise the factory will not be registered.<br>
	 */
	private static void tryRegisterFactory() {
		Class<?> spring = null;
		try {
			spring = Class.forName("org.springframework.boot.SpringApplication");
		} catch (Throwable ignored) {}
		if (spring != null) {
			System.setProperty("log4j.configurationFactory", CachedFactory.class.getName());
			registeredFactory = true;
		}
	}
	
	/**
	 * Gets the appender for the given name and enables it.<br>
	 * @param name The name of the appender to enable
	 * @throws NullPointerException If the name of the appender is null
	 * @throws IllegalStateException If the appender was not found
	 */
	private static void enableAppender(@NotNull String name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		Appender appender = config.getAppender(Objects.requireNonNull(name, "Appender name must not be null"));
		if (appender == null) {
			throw new IllegalStateException("Appender " + name + " not found, appender may not be registered");
		}
		if (isRootLoggerConfigured()) {
			config.getRootLogger().addAppender(appender, null, null);
		} else {
			for (String logger : CONFIGURED_LOGGERS) {
				LoggerConfig loggerConfig = config.getLoggerConfig(logger);
				if (loggerConfig == null) {
					throw new IllegalStateException("Logger '" + logger + "' not found, logger may not be registered");
				}
				loggerConfig.addAppender(appender, null, null);
			}
		}
		context.updateLoggers(config);
	}
	
	/**
	 * Gets the appender for the given name and disables it.<br>
	 * @param name The name of the appender to disable
	 */
	private static void disableAppender(@NotNull String name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		if (isRootLoggerConfigured()) {
			config.getRootLogger().removeAppender(name);
		} else {
			for (String logger : CONFIGURED_LOGGERS) {
				LoggerConfig loggerConfig = config.getLoggerConfig(logger);
				if (loggerConfig == null) {
					throw new IllegalStateException("Logger '" + logger + "' not found, logger may not be registered");
				}
				loggerConfig.removeAppender(name);
			}
		}
		context.updateLoggers(config);
	}
	
	/**
	 * Gets the name of the logger for the given logging type and level.<br>
	 * @param type The logging type
	 * @param level The level
	 * @return The name of the logger in upper camel case
	 */
	static @NotNull String getLogger(@NotNull LoggingType type, @NotNull Level level) {
		checkLevel(type, level);
		String levelName = level.name().toLowerCase();
		String typeName = type.name().toLowerCase();
		return StringUtils.capitalize(typeName) + StringUtils.capitalize(levelName);
	}
	
	/**
	 * Checks whether the given level is valid for the given logging type.<br>
	 * Invalid combinations are:<br>
	 * <ul>
	 *     <li>The levels 'all' or 'off' with any logging type</li>
	 *     <li>Logging type 'file' and level 'trace', 'warn' or 'fatal'</li>
	 * </ul>
	 * @param type The logging type to check
	 * @param level The level to check
	 * @throws IllegalArgumentException If the combination of logging type and level is invalid
	 */
	static void checkLevel(@NotNull LoggingType type, @NotNull Level level) {
		Objects.requireNonNull(type, "Logging type must not be null");
		Objects.requireNonNull(level, "Level must not be null");
		if (level == Level.ALL || level == Level.OFF) {
			throw new IllegalArgumentException("Level must not be 'all' or 'off'");
		}
		if (type == LoggingType.FILE && level != Level.DEBUG && level != Level.INFO && level != Level.ERROR) {
			throw new IllegalArgumentException("Logging type 'file' does not support level '" + level.name().toLowerCase() + "'");
		}
	}
	//endregion
}
