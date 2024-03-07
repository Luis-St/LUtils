/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.logging;

import net.luis.utils.exception.InvalidValueException;
import net.luis.utils.resources.ResourceLocation;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Helper class to configure logging from system properties.<br>
 *
 * @author Luis-St
 */
class LoggingHelper {
	
	/**
	 * The pattern to match a property in the format 'key=value'.<br>
	 * <p>
	 *     The key must not contain any whitespace characters.<br>
	 *     The value must not contain any line breaks.<br>
	 * </p>
	 */
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(\\S*)(=)(.*)$");
	/**
	 * The pattern to match a valid path.<br>
	 * The path must be relative or absolute.<br>
	 */
	private static final String PATH_PATTERN = "^([a-zA-Z]:|\\./).*$";
	/**
	 * Constant for the system property 'logging.level.status'.<br>
	 * <p>
	 *     The property is used to set the status level of the internal apache logger.<br>
	 *     The value must be a valid {@link Level} name in lower case.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 'error'}.
	 * </p>
	 */
	private static final String LOGGING_LEVEL_STATUS = "logging.level.status";
	/**
	 * Constant for the system property 'logging.config'.<br>
	 * <p>
	 *     The property is used to set the location of the logging configuration file.<br>
	 *     The value must be a relative or absolute path of a file.<br>
	 *     If specified, the system properties will be loaded from the configuration file.<br>
	 * </p>
	 * <p>
	 *     By default, the property is not set.<br>
	 * </p>
	 */
	private static final String LOGGING_CONFIG = "logging.config";
	/**
	 * Constant for the system property 'logging.config.override'.<br>
	 * <p>
	 *     The property is used to enable/disable the override of system properties by the configuration file.<br>
	 *     The value must be 'true' or 'false'.<br>
	 *     If enabled, the system properties set with {@code -D} flag will be overridden by the configuration file.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 'false'}.<br>
	 * </p>
	 */
	private static final String LOGGING_CONFIG_OVERRIDE = "logging.config.override";
	/**
	 * Constant for the system property 'logging.console'.<br>
	 * <p>
	 *     The property is used to enable/disable console logging.<br>
	 *     The value must be 'true' or 'false'.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 'true'}.<br>
	 * </p>
	 */
	private static final String LOGGING_CONSOLE = "logging.console";
	/**
	 * Constant for the system property 'logging.file'.<br>
	 * <p>
	 *     The property is used to enable/disable file logging.<br>
	 *     The value must be 'true' or 'false'.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 'true'}.<br>
	 * </p>
	 */
	private static final String LOGGING_FILE = "logging.file";
	/**
	 * Constant for the system property 'logging.file.folder.root'.<br>
	 * <p>
	 *     The property is used to set the root folder of the log files.<br>
	 *     The value must be a relative or absolute path of a folder.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code './'}.<br>
	 * </p>
	 */
	private static final String LOGGING_FILE_FOLDER_ROOT = "logging.file.folder.root";
	
	/**
	 * Creates a new logger configuration for the given loggers.<br>
	 * The configuration is created with the default values and modifies it according to the system properties.<br>
	 * General system properties:<br>
	 * <ul>
	 *     <li>
	 *         logging.level.status<br>
	 *         The status level of the internal apache logger, expect a valid {@link Level} name in lower case.<br>
	 *         Default: 'error'<br>
	 *     </li>
	 *     <li>
	 *         logging.config<br>
	 *         The location of the logging configuration file, expect a relative or absolute path of a file.<br>
	 *         If specified, the system properties will be loaded from the configuration file.<br>
	 *         Default: None<br>
	 *     </li>
	 *     <li>
	 *         logging.config.override<br>
	 *         Enables/disables the override of system properties by the configuration file, expect 'true' or 'false'.<br>
	 *         Default: 'false'<br>
	 *     </li>
	 *     <li>
	 *         logging.console<br>
	 *         Enables/disables console logging, expect 'true' or 'false'.<br>
	 *         Default: 'true'<br>
	 *     </li>
	 *     <li>
	 *         logging.file<br>
	 *         Enables/disables file logging, expect 'true' or 'false'.<br>
	 *         Default: 'true'<br>
	 *     </li>
	 * </ul>
	 *
	 * File logging system properties:<br>
	 * <ul>
	 *     <li>
	 * 	       logging.file.{level}<br>
	 * 	       Enables/disables file logging for the given level, expect 'true' or 'false'.<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 * 	       <br>
	 * 	       Default:
	 * 	       <ul>
	 * 	           <li>Enabled levels: None</li>
	 * 	           <li>Disabled levels: Trace, Debug, Info, Warn, Error, Fatal</li>
	 * 	       </ul>
	 * 	       Examples:
	 * 	       <ul>
	 * 	           <li>
	 * 	               logging.file.debug=true<br>
	 * 	               Enables file logging for the debug level<br>
	 * 	           </li>
	 * 	           <li>
	 * 	               logging.file.info=false<br>
	 * 	               Disables file logging for the info level<br>
	 * 	           </li>
	 * 	       </ul>
	 * 	  </li>
	 *    <li>
	 *        logging.file.folder.root<br>
	 *        The root folder to log to, expect a relative or absolute path of a folder.<br>
	 *        Default: './'<br>
	 *    </li>
	 *    <li>
	 *        logging.file.folder.{level}<br>
	 *        The folder to a log file of a specific level, expect a relative path of a folder.<br>
	 *        {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 *        Default: 'logs/'<br>
	 *    </li>
	 *    <li>
	 *        logging.file.folder.{level}.file<br>
	 *        The file name of the current log file of a specific level, expect a file name.<br>
	 *        {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 *        Default: '{level}.log'<br>
	 *    </li>
	 *    <li>
	 *        logging.file.folder.{level}.archive<br>
	 *        The file pattern of the archived log files of a specific level, expect a file pattern.<br>
	 *        {level} must be replaced with a valid {@link Level} name in lower case.<br>
	 *        Default: '{level}-%d{dd-MM-yyyy}-%i.log.gz'<br>
	 *    </li>
	 * </ul>
	 *
	 * @param loggers The names of the loggers which should be configured
	 * @return The logger configuration
	 * @throws NullPointerException If the given list of loggers is null
	 * @throws InvalidValueException If the value of a system property is invalid
	 * @throws IllegalStateException If a system property is already set and the configuration tries to override it
	 * @see #loadInternal(List)
	 * @see #loadProperties(ResourceLocation)
	 */
	public static @NotNull LoggerConfiguration load(@NotNull List<String> loggers) {
		String config = System.getProperty("logging.config");
		if (StringUtils.isNotBlank(config)) {
			String file = config.replace("\\", "/");
			if (!file.matches(PATH_PATTERN)) {
				throw new InvalidValueException("Invalid value '" + file + "' for property 'logging.config', must be a relative or absolute path");
			}
			loadProperties(ResourceLocation.external(file));
		}
		return loadInternal(loggers);
	}
	
	/**
	 * Loads the system properties from the given location.<br>
	 * The location must be a valid file, and the properties must be in the format 'key=value'.<br>
	 * If a property is already set, the override is allowed if the system property 'logging.config.override' is set to 'true'.<br>
	 * @param location The location of the properties file
	 * @throws NullPointerException If the given location is null
	 * @throws IllegalStateException If a system property is already set and the configuration tries to override it
	 * @throws RuntimeException If the properties file could not be loaded
	 * @see #PROPERTY_PATTERN
	 */
	private static void loadProperties(@NotNull ResourceLocation location) {
		Objects.requireNonNull(location, "Location must not be null");
		boolean noOverrides = !Boolean.parseBoolean(System.getProperty("logging.config.override", "false"));
		try {
			for (String line : location.getLines().toList()) {
				if (StringUtils.isBlank(line) || StringUtils.startsWithAny(line, "#", "!")) {
					continue;
				}
				Matcher matcher = PROPERTY_PATTERN.matcher(line);
				if (matcher.matches()) {
					String key = matcher.group(1);
					if (System.getProperty(key) != null && noOverrides) {
						throw new IllegalStateException("System property '" + key + "' is already set and override is not allowed");
					}
					System.setProperty(key, matcher.group(3));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to load logging config file '" + location + "'", e);
		}
	}
	
	/**
	 * Creates a new logger configuration for the given loggers.<br>
	 * The configuration is created with the default values and modifies it according to the system properties.<br>
	 * The system properties are specified in the documentation of the method {@link #load(List)}.<br>
	 * @param loggers The names of the loggers which should be configured
	 * @return The logger configuration
	 * @throws NullPointerException If the given list of loggers is null
	 * @throws InvalidValueException If the value of a system property is invalid
	 * @see #load(List)
	 */
	private static @NotNull LoggerConfiguration loadInternal(@NotNull List<String> loggers) {
		LoggerConfiguration config = new LoggerConfiguration(loggers);
		String statusLevel = System.getProperty(LOGGING_LEVEL_STATUS);
		if (StringUtils.isNotBlank(statusLevel)) {
			Level level = Level.getLevel(statusLevel);
			if (level == null) {
				throw new InvalidValueException("Invalid value '" + statusLevel + "' for property '" + LOGGING_LEVEL_STATUS + "'");
			} else {
				config.setStatusLevel(level);
			}
		}
		String consoleLogging = System.getProperty(LOGGING_CONSOLE);
		if (StringUtils.isNotBlank(consoleLogging)) {
			if (isEnabled(consoleLogging)) {
				config.enableLogging(LoggingType.CONSOLE);
			} else if (isDisabled(consoleLogging)) {
				config.disableLogging(LoggingType.CONSOLE);
			} else {
				throw new InvalidValueException("Invalid value '" + consoleLogging + "' for property '" + LOGGING_CONSOLE + "'");
			}
		}
		String fileLogging = System.getProperty(LOGGING_FILE);
		if (StringUtils.isNotBlank(fileLogging)) {
			if (isEnabled(fileLogging)) {
				config.enableLogging(LoggingType.FILE);
			} else if (isDisabled(fileLogging)) {
				config.disableLogging(LoggingType.FILE);
			} else {
				throw new InvalidValueException("Invalid value '" + fileLogging + "' for property '" + LOGGING_FILE + "'");
			}
		}
		String root = System.getProperty(LOGGING_FILE_FOLDER_ROOT, "./").replace("\\", "/");
		if (!root.matches(PATH_PATTERN)) {
			throw new InvalidValueException("Invalid value '" + root + "' for property '" + LOGGING_FILE_FOLDER_ROOT + "', must be a relative or absolute path");
		}
		config.setRootDirectory(StringUtils.strip(root, "/ ") + "/"); // Root must be relative or absolute and end with '/'
		//region Local record
		record LogFile(Level level, String folder, String file, String archive) {
			
			public static @NotNull LogFile forLevel(@NotNull Level level) {
				Objects.requireNonNull(level, "Level must not be null");
				String folder = System.getProperty("logging.file.folder." + level.name().toLowerCase(), null);
				String file = System.getProperty("logging.file.folder." + level.name().toLowerCase() + ".file", null);
				String archive = System.getProperty("logging.file.folder." + level.name().toLowerCase() + ".archive", null);
				return new LogFile(level, folder, file, archive);
			}
			
			public @NotNull String folderOr(@NotNull String defaultFolder) {
				String folder = this.folder != null ? this.folder : Objects.requireNonNull(defaultFolder, "Default folder must not be null");
				return StringUtils.strip(folder.replace("\\", "/"), "/ ");
			}
			
			public @NotNull String fileOr(@NotNull String defaultFile) {
				String file = this.file != null ? this.file : Objects.requireNonNull(defaultFile, "Default file must not be null");
				return StringUtils.strip(file.replace("\\", "/"), "/ ");
			}
			
			public @NotNull String archiveOr(@NotNull String defaultArchive) {
				String archive = this.archive != null ? this.archive : Objects.requireNonNull(defaultArchive, "Default archive must not be null");
				return StringUtils.strip(archive.replace("\\", "/"), "/ ");
			}
			
			public boolean isValid() {
				return this.folder != null || this.file != null || this.archive != null;
			}
		}
		//endregion
		Stream.of(LoggingType.FILE.getAllowedLevels()).map(LogFile::forLevel).filter(LogFile::isValid).forEach(log -> {
			String folder = log.folderOr("logs"); // Folder must not be null and starts and ends with '/'
			String file = "/" + log.fileOr(log.level().name().toLowerCase() + ".log"); // File must not be null and starts with '/'
			String archive = "/" + log.archiveOr(log.level().name().toLowerCase() + "-%d{dd-MM-yyyy}-%i.log.gz"); // Archive must not be null and starts with '/'
			config.overrideLog(log.level(), folder + file, folder + archive);
		});
		for (Level level : LoggingType.FILE) {
			String value = System.getProperty("logging." + LoggingType.FILE + "." + level.name().toLowerCase(), "");
			if (isEnabled(value)) {
				config.addDefaultLogger(LoggingType.FILE, level);
			} else if (isDisabled(value)) {
				config.removeDefaultLogger(LoggingType.FILE, level);
			} else {
				throw new InvalidValueException("Invalid value '" + value + "' for property 'logging." + LoggingType.FILE + "." + level.name().toLowerCase() + "'");
			}
		}
		return config;
	}
	
	/**
	 * Configures the current logging configuration according to the system properties.<br>
	 * Only console logging is configured, due file logging must be configured at startup.<br>
	 * System properties:
	 * <ul>
	 *     <li>
	 *         logging.console.{level}<br>
	 *         Enables/disables console logging for the given level, expect 'true' or 'false'<br>
	 *         {level} must be replaced with a valid {@link Level} name in lower case<br>
	 *         <br>
	 *         Default:
	 *         <ul>
	 *             <li>Enabled levels: info, warn, error, fatal</li>
	 *             <li>Disabled levels: trace, debug</li>
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
	 * </ul>
	 * @throws IllegalArgumentException If the value of a system property is invalid
	 */
	public static void configure() {
		Stream.of(LoggingType.CONSOLE.getAllowedLevels()).map(level -> {
			return Pair.of(level, System.getProperty("logging." + LoggingType.CONSOLE + "." + level.name().toLowerCase(), ""));
		}).filter(entry -> !entry.getSecond().isEmpty()).forEach(entry -> {
			Level level = entry.getFirst();
			String value = entry.getSecond();
			if (isEnabled(value)) {
				LoggingUtils.enableConsole(level);
			} else if (isDisabled(value)) {
				LoggingUtils.disableConsole(level);
			} else {
				throw new IllegalArgumentException("Invalid value '" + value + "' for property 'logging." + LoggingType.CONSOLE + "." + level.name().toLowerCase() + "'");
			}
		});
	}
	
	//region Helper methods
	
	/**
	 * Checks if the given property is enabled.<br>
	 * A property is enabled if it is equal to 'true', 'enable' or 'enabled', '1', 'on' or 'yes' (case-insensitive).
	 * @param property The property to check
	 * @return True if the property is enabled, false otherwise
	 */
	private static boolean isEnabled(@Nullable String property) {
		return StringUtils.equalsAnyIgnoreCase(property, "true", "enable", "enabled", "1", "on", "yes");
	}
	
	/**
	 * Checks if the given property is disabled.<br>
	 * A property is disabled if it is equal to 'false', 'disable' or 'disabled', '0', 'off' or 'no' (case-insensitive).
	 * @param property The property to check
	 * @return True if the property is disabled, false otherwise
	 */
	private static boolean isDisabled(@Nullable String property) {
		return StringUtils.equalsAnyIgnoreCase(property, "false", "disable", "disabled", "0", "off", "no");
	}
	//endregion
}
