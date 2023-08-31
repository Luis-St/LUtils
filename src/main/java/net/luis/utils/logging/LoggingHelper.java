package net.luis.utils.logging;

import net.luis.utils.io.FileUtils;
import net.luis.utils.util.Pair;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 *
 * @author Luis-St
 *
 */

class LoggingHelper {
	
	public static @NotNull LoggerConfiguration load() {
		LoggerConfiguration config = new LoggerConfiguration();
		String statusLevel = System.getProperty("logging.statusLevel");
		if (statusLevel != null) {
			Level level = Level.getLevel(statusLevel);
			if (level == null) {
				throw new IllegalArgumentException("Invalid value '" + statusLevel + "' for property 'logging.statusLevel'");
			} else {
				config.setStatusLevel(level);
			}
		}
		String consoleLogging = System.getProperty("logging.console");
		if (consoleLogging != null) {
			if (isEnabled(consoleLogging)) {
				config.enableLogging(LoggingType.CONSOLE);
			} else if (isDisabled(consoleLogging)) {
				config.disableLogging(LoggingType.CONSOLE);
			} else {
				throw new IllegalArgumentException("Invalid value '" + consoleLogging + "' for property 'logging.console'");
			}
		}
		String fileLogging = System.getProperty("logging.file");
		if (fileLogging != null) {
			if (isEnabled(fileLogging)) {
				config.enableLogging(LoggingType.FILE);
			} else if (isDisabled(fileLogging)) {
				config.disableLogging(LoggingType.FILE);
			} else {
				throw new IllegalArgumentException("Invalid value '" + fileLogging + "' for property 'logging.file'");
			}
		}
		String logFolder = FileUtils.getRelativePath(System.getProperty("logging.file.folder"));
		if (!"./".equals(logFolder)) {
			for (Level level : LoggingUtils.FILE_LEVELS) {
				String path = logFolder + "/" + level.name().toLowerCase();
				config.overrideLog(level, path + ".log", path + "-%d{dd-MM-yyyy}-%i.log.gz");
			}
		}
		return config;
	}
	
	public static void configure() {
		Stream.of(LoggingUtils.FILE_LEVELS).map(level -> {
			return Pair.of(level, System.getProperty("logging.file." + level.name().toLowerCase()));
		}).filter(pair -> pair.getSecond() != null).forEach(pair -> {
			String property = pair.getSecond();
			if (isEnabled(property)) {
				LoggingUtils.enableFile(pair.getFirst());
			} else if (isDisabled(property)) {
				LoggingUtils.disableFile(pair.getFirst());
			} else {
				throw new IllegalArgumentException("Invalid value '" + property + "' for property 'logging.file." + pair.getFirst().name().toLowerCase() + "'");
			}
		});
		Stream.of(LoggingUtils.CONSOLE_LEVELS).map(level -> {
			return Pair.of(level, System.getProperty("logging.console." + level.name().toLowerCase()));
		}).filter(pair -> pair.getSecond() != null).forEach(pair -> {
			String property = pair.getSecond();
			if (isEnabled(property)) {
				LoggingUtils.enableConsole(pair.getFirst());
			} else if (isDisabled(property)) {
				LoggingUtils.disableConsole(pair.getFirst());
			} else {
				throw new IllegalArgumentException("Invalid value '" + property + "' for property 'logging.console." + pair.getFirst().name().toLowerCase() + "'");
			}
		});
	}
	
	//region Helper methods
	private static boolean isEnabled(String property) {
		return "true".equalsIgnoreCase(property) || "enable".equalsIgnoreCase(property) || "enabled".equalsIgnoreCase(property);
	}
	
	private static boolean isDisabled(String property) {
		return "false".equalsIgnoreCase(property) || "disable".equalsIgnoreCase(property) || "disabled".equalsIgnoreCase(property);
	}
	//endregion
}
