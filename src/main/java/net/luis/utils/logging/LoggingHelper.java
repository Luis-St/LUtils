package net.luis.utils.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Helper class to configure the logging from system properties.<br>
 *
 * @author Luis-St
 */

class LoggingHelper {
	
	/*
	 * ToDo:
	 *  - Add default logger
	 *  - Allow to configure with a file (json -> to properties)
	 */
	
	private static final Logger LOGGER = LogManager.getLogger(LoggingHelper.class);
	
	/**
	 * Creates a new logger configuration and modifies it according to the system properties.<br>
	 * System properties:
	 * <ul>
	 *     <li>
	 *         logging.statusLevel<br>
	 *         The status level of the internal apache logger, expect a valid {@link Level} name in lower case.
	 *     </li>
	 *     <li>
	 *         logging.console<br>
	 *         Enables/disables console logging, expect 'true' or 'false'.<br>
	 *         Default: 'true'
	 *     </li>
	 *     <li>
	 *         logging.file<br>
	 *         Enables/disables file logging, expect 'true' or 'false'.<br>
	 *         Default: 'true'
	 *     </li>
	 *     <li>
	 *         logging.file.folder.root<br>
	 *         The root folder to log to, expect a relative or absolute path of a folder.<br>
	 *         Default: './'
	 *     </li>
	 *     <li>
	 *         logging.file.folder.{level}<br>
	 *         The folder to a log file of a specific level, expect a relative path of a folder.<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 *         Default: 'logs/'
	 *     </li>
	 *     <li>
	 *         logging.file.folder.{level}.file<br>
	 *         The file name of the current log file of a specific level, expect a file name.<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 *         Default: '{level}.log'
	 *     </li>
	 *     <li>
	 *         logging.file.folder.{level}.archive<br>
	 *         The file pattern of the archived log files of a specific level, expect a file pattern.<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 *         Default: '{level}-%d{dd-MM-yyyy}-%i.log.gz'
	 *     </li>
	 *     <li>
	 *         logging.file.folder (deprecated)<br>
	 *         The folder to log to, expect a relative path of a folder<br>
	 *         Default: 'logs/{level}'
	 *     </li>
	 * </ul>
	 * @param loggers The names of the loggers which should be configured
	 * @return The logger configuration
	 * @throws NullPointerException If the given list of loggers is null
	 * @throws IllegalArgumentException If the value of a system property is invalid or if the logger configuration is invalid
	 */
	public static @NotNull LoggerConfiguration load(@NotNull List<String> loggers) {
		LoggerConfiguration config = new LoggerConfiguration(loggers);
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
		//region Local record
		record LogFile(Level level, String folder, String file, String archive) {
			
			public static LogFile forLevel(@NotNull Level level) {
				Objects.requireNonNull(level, "Level must not be null");
				String folder = System.getProperty("logging.file.folder." + level.name().toLowerCase(), "");
				String file = System.getProperty("logging.file.folder." + level.name().toLowerCase() + ".file", "");
				String archive = System.getProperty("logging.file.folder." + level.name().toLowerCase() + ".archive", "");
				return new LogFile(level, folder, file, archive);
			}
			
			public String folderOr(String folder) {
				return StringUtils.defaultIfEmpty(this.folder, folder);
			}
			
			public String fileOr(String file) {
				return StringUtils.defaultIfEmpty(this.file, file);
			}
			
			public String archiveOr(String archive) {
				return StringUtils.defaultIfEmpty(this.archive, archive);
			}
			
			public boolean isValid() {
				return !this.folder.isEmpty() || !this.file.isEmpty() || !this.archive.isEmpty();
			}
		}
		//endregion
		config.setRootDirectory(System.getProperty("logging.file.folder.root", "./"));
		Stream.of(LoggingUtils.FILE_LEVELS).map(LogFile::forLevel).filter(LogFile::isValid).forEach(log -> {
			String folder = log.folderOr("logs");
			String file = log.fileOr(log.level().name().toLowerCase() + ".log");
			String archive = log.archiveOr(log.level().name().toLowerCase() + "-%d{dd-MM-yyyy}-%i.log.gz");
			if (folder.endsWith("/") && file.startsWith("/")) {
				folder = folder.substring(0, folder.length() - 1);
			} else if (!folder.endsWith("/") && !file.startsWith("/")) {
				folder += "/";
			}
			config.overrideLog(log.level(), folder + file, folder + archive);
		});
		//region Deprecated -> ToDo: Mark as for removal in next version
		String folderProperty = System.getProperty("logging.file.folder", "");
		if (!folderProperty.isEmpty()) {
			LOGGER.warn("Property 'logging.file.folder' is deprecated, use 'logging.file.folder.root' and 'logging.file.folder.{level}(|.file|.archive)' instead");
		}
		if (StringUtils.contains(folderProperty, ".")) {
			throw new IllegalArgumentException("Invalid value '" + folderProperty + "' for property 'logging.file.folder' (deprecated), must not contain '.'");
		}
		String logFolder = getRelativePath(folderProperty);
		if (!"./".equals(logFolder)) {
			for (Level level : LoggingUtils.FILE_LEVELS) {
				String path = logFolder + "/" + level.name().toLowerCase();
				config.overrideLog(level, path + ".log", path + "-%d{dd-MM-yyyy}-%i.log.gz");
			}
		}
		//endregion
		return config;
	}
	
	/**
	 * Configures the current logging configuration according to the system properties.<br>
	 * System properties:
	 * <ul>
	 *     <li>
	 *         logging.console.{level}<br>
	 *         Enables/disables console logging for the given level, expect 'true' or 'false'<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case<br>
	 *         <br>
	 *         Default:
	 *         <ul>
	 *             <li>Enabled levels: Info, Warn, Error, Fatal</li>
	 *             <li>Disabled levels: Trace, Debug</li>
	 *         </ul>
	 *         Examples:
	 *         <ul>
	 *             <li>
	 *                 logging.console.debug=true<br>
	 *                 Enables console logging for the debug level
	 *             </li>
	 *             <li>
	 *                 logging.console.info=false<br>
	 *                 Disables console logging for the info level
	 *             </li>
	 *         </ul>
	 *     </li>
	 *     <li>
	 *         logging.file.{level}<br>
	 *         Enables/disables file logging for the given level, expect 'true' or 'false'<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case<br>
	 *         <br>
	 *         Default:
	 *         <ul>
	 *             <li>Enabled levels: None</li>
	 *             <li>Disabled levels: Trace, Debug, Info, Warn, Error, Fatal</li>
	 *         </ul>
	 *         Examples:
	 *         <ul>
	 *             <li>
	 *                 logging.file.debug=true<br>
	 *                 Enables file logging for the debug level
	 *             </li>
	 *             <li>
	 *                 logging.file.info=false<br>
	 *                 Disables file logging for the info level
	 *             </li>
	 *         </ul>
	 *    </li>
	 * </ul>
	 * @throws IllegalArgumentException If the value of a system property is invalid
	 */
	public static void configure() {
		configureType(LoggingType.FILE, LoggingUtils.FILE_LEVELS);
		configureType(LoggingType.CONSOLE, LoggingUtils.CONSOLE_LEVELS);
	}
	
	/**
	 * Configures the current logging configuration according to the system properties for the given type.<br>
	 * @param type The type to configure
	 * @param levels The allowed levels for the given type
	 * @see #configure()
	 */
	private static void configureType(@NotNull LoggingType type, Level @NotNull [] levels) {
		Stream.of(levels).map(level -> {
			return Map.entry(level, System.getProperty("logging." + type + "." + level.name().toLowerCase(), ""));
		}).filter(entry -> !entry.getValue().isEmpty()).forEach(entry -> {
			Level level = entry.getKey();
			String value = entry.getValue();
			if (isEnabled(value)) {
				LoggingUtils.enable(type, level);
			} else if (isDisabled(value)) {
				LoggingUtils.disable(type, level);
			} else {
				throw new IllegalArgumentException("Invalid value '" + value + "' for property 'logging." + type + "." + level.name().toLowerCase() + "'");
			}
		});
	}
	
	//region Helper methods
	
	/**
	 * Checks if the given property is enabled.<br>
	 * A property is enabled if it is equal to 'true', 'enable' or 'enabled', '1', 'on' or 'yes' (case-insensitive).
	 * @param property The property to check.
	 * @return True if the property is enabled, false otherwise.
	 */
	private static boolean isEnabled(@Nullable String property) {
		return StringUtils.equalsAnyIgnoreCase(property, "true", "enable", "enabled", "1", "on", "yes");
	}
	
	/**
	 * Checks if the given property is disabled.<br>
	 * A property is disabled if it is equal to 'false', 'disable' or 'disabled', '0', 'off' or 'no' (case-insensitive).
	 * @param property The property to check.
	 * @return True if the property is disabled, false otherwise.
	 */
	private static boolean isDisabled(@Nullable String property) {
		return StringUtils.equalsAnyIgnoreCase(property, "false", "disable", "disabled", "0", "off", "no");
	}
	
	/**
	 * Returns the relative path of the given file.<br>
	 * If the file is null, empty or equal to '/' the current directory ('./') is returned.<br>
	 * If the file starts with './' and ends with '/' it is returned as is.<br>
	 * All other files are returned as: {@code './' + file + '/'}<br>
	 * Examples:
	 * <pre>
	 *     getRelativePath(null) = "./"
	 *     getRelativePath("") = "./"
	 *     getRelativePath("/") = "./"
	 *     getRelativePath("./") = "./"
	 *     getRelativePath("test") = "./test/"
	 *     getRelativePath("/test") = "./test/"
	 *     getRelativePath("test/") = "./test/"
	 *     getRelativePath("/test/") = "./test/"
	 *     getRelativePath("./test") = "./test/"
	 *     getRelativePath("test/test") = "./test/test/"
	 * </pre>
	 * @param file The file to get the relative path of.
	 * @return The relative path of the given file.
	 */
	private static @NotNull String getRelativePath(@Nullable String file) {
		String str = StringUtils.stripToEmpty(file).replace("\\", "/");
		if (str.isEmpty() || "/".equals(str) || "./".equals(str)) {
			return "./";
		}
		if (!str.endsWith("/")) {
			str += "/";
		}
		if (str.startsWith("./") && str.endsWith("/")) {
			return str;
		} else if (!str.startsWith("./") && !str.startsWith("/")) {
			return "./" + str;
		} else if (str.startsWith(".")) {
			return "./" + str.substring(1);
		} else if (str.startsWith("/")) {
			return "." + str;
		}
		return str;
	}
	//endregion
}
