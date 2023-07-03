package net.luis.utils.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class LoggingUtils {
	
	static Level[] CONSOLE_LEVELS = new Level[] {Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL};
	static Level[] FILE_LEVELS = new Level[] {Level.DEBUG, Level.INFO, Level.ERROR};
	
	public static void initialize(LoggerConfiguration configuration) {
		Configurator.initialize(Objects.requireNonNull(configuration, "Configuration must not be null").build());
	}
	
	//region Helper methods
	private static void enableAppender(String name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		config.getRootLogger().addAppender(Objects.requireNonNull(config.getAppender(name), "Appender " + name + " not found"), null, null);
		context.updateLoggers(config);
	}
	
	private static void disableAppender(String name) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		Configuration config = context.getConfiguration();
		config.getRootLogger().removeAppender(name);
		context.updateLoggers(config);
	}
	
	static void checkLevel(LoggingType type, Level level) {
		if (level == Level.ALL || level == Level.OFF) {
			throw new IllegalArgumentException("Level must not be 'all' or 'off'");
		}
		if (type == LoggingType.FILE && level != Level.DEBUG && level != Level.INFO && level != Level.ERROR) {
			throw new IllegalArgumentException("Logging type 'file' does not support level '" + level.name().toLowerCase() + "'");
		}
	}
	
	static @NotNull String getLogger(LoggingType type, Level level) {
		checkLevel(type, level);
		String levelName = Objects.requireNonNull(level, "Level must not be null").name().toLowerCase();
		String typeName = Objects.requireNonNull(type, "Logging type must not be null").name().toLowerCase();
		return StringUtils.capitalize(typeName) + StringUtils.capitalize(levelName);
	}
	//endregion
	
	//region Enable logging
	public static void enable(Level level, LoggingType type) {
		enableAppender(getLogger(type, level));
	}
	
	public static void enableConsole(Level level) {
		enable(level, LoggingType.CONSOLE);
	}
	
	public static void enableConsole() {
		Arrays.stream(CONSOLE_LEVELS).forEach(LoggingUtils::enableConsole);
	}
	
	public static void enableFile(Level level) {
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
	
	public static void disableFile(Level level) {
		disable(level, LoggingType.FILE);
	}
	
	public static void disableFile() {
		Arrays.stream(FILE_LEVELS).forEach(LoggingUtils::disableFile);
	}
	//endregion
}
