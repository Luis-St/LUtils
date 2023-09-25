package net.luis.utils.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class LoggingUtils {
	
	static final Level[] CONSOLE_LEVELS = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL};
	static final Level[] FILE_LEVELS = new Level[] {Level.DEBUG, Level.INFO, Level.ERROR};
	private static boolean initialized = false;
	
	//region Initialization
	public static void load() {
		LoggerConfiguration config = LoggingHelper.load();
		initialize(config);
		LoggingHelper.configure();
	}
	
	public static void initialize() {
		initialize(LoggerConfiguration.DEFAULT);
	}
	
	public static void initialize(@NotNull LoggerConfiguration configuration) {
		initialize(configuration, false);
	}
	
	public static void initialize(@NotNull LoggerConfiguration configuration, boolean override) {
		if (!initialized) {
			Configurator.initialize(Objects.requireNonNull(configuration, "Configuration must not be null").build());
			initialized = true;
		} else if (override) {
			Configurator.initialize(Objects.requireNonNull(configuration, "Configuration must not be null").build());
		} else {
			throw new IllegalStateException("Logging has already been initialized");
		}
	}
	
	public static boolean initializeSafe(@NotNull LoggerConfiguration configuration) {
		if (!isInitialized()) {
			initialize(configuration);
			return true;
		}
		return false;
	}
	
	public static boolean isInitialized() {
		return initialized;
	}
	//endregion
	
	//region Helper methods
	private static void enableAppender(@NotNull String name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		config.getRootLogger().addAppender(Objects.requireNonNull(config.getAppender(name), "Appender " + name + " not found, appender may not be registered"), null, null);
		context.updateLoggers(config);
	}
	
	private static void disableAppender(@NotNull String name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		config.getRootLogger().removeAppender(name);
		context.updateLoggers(config);
	}
	
	static void checkLevel(@Nullable LoggingType type, @Nullable Level level) {
		if (level == Level.ALL || level == Level.OFF) {
			throw new IllegalArgumentException("Level must not be 'all' or 'off'");
		}
		if (type == LoggingType.FILE && level != Level.DEBUG && level != Level.INFO && level != Level.ERROR) {
			throw new IllegalArgumentException("Logging type 'file' does not support level '" + level.name().toLowerCase() + "'");
		}
	}
	
	static @NotNull String getLogger(@NotNull LoggingType type, @NotNull Level level) {
		checkLevel(type, level);
		String levelName = Objects.requireNonNull(level, "Level must not be null").name().toLowerCase();
		String typeName = Objects.requireNonNull(type, "Logging type must not be null").name().toLowerCase();
		return StringUtils.capitalize(typeName) + StringUtils.capitalize(levelName);
	}
	//endregion
	
	//region Enable logging
	public static void enable(@NotNull Level level, @NotNull LoggingType type) {
		enableAppender(getLogger(type, level));
	}
	
	public static void enableConsole(@NotNull Level level) {
		enable(level, LoggingType.CONSOLE);
	}
	
	public static void enableConsole() {
		Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::enableConsole);
	}
	
	public static void enableFile(@NotNull Level level) {
		enable(level, LoggingType.FILE);
	}
	
	public static void enableFile() {
		Arrays.stream(FILE_LEVELS).forEach(LoggingUtils::enableFile);
	}
	//endregion
	
	//region Disable logging
	public static void disable(Level level, LoggingType type) {
		disableAppender(getLogger(type, level));
	}
	
	public static void disableConsole(Level level) {
		disable(level, LoggingType.CONSOLE);
	}
	
	public static void disableConsole() {
		Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::disableConsole);
	}
	
	public static void disableFile(@NotNull Level level) {
		disable(level, LoggingType.FILE);
	}
	
	public static void disableFile() {
		Arrays.stream(FILE_LEVELS).forEach(LoggingUtils::disableFile);
	}
	//endregion
}
