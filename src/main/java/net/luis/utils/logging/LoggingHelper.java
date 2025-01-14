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

package net.luis.utils.logging;

import net.luis.utils.exception.InvalidValueException;
import net.luis.utils.resources.ResourceLocation;
import net.luis.utils.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.luis.utils.logging.LoggerConfiguration.*;

/**
 * Helper class to configure logging from system properties.<br>
 *
 * @author Luis-St
 */
final class LoggingHelper {
	
	/**
	 * The pattern to match a property in the format 'key=value'.<br>
	 * <p>
	 *     The key must not contain any whitespace characters.<br>
	 *     The value must not contain any line breaks.<br>
	 * </p>
	 */
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("^(\\S*)(\\s*=\\s*)(.*)$");
	/**
	 * Constant for the system property 'logging.status_level'.<br>
	 * <p>
	 *     The property is used to set the status level of the internal apache logger.<br>
	 *     The value must be a valid {@link Level} name in lower case.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 'error'}.
	 * </p>
	 */
	private static final String LOGGING_LEVEL_STATUS = "logging.status_level";
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
	 * Constant for the system property 'logging.file.size'.<br>
	 * <p>
	 *     The property is used to set the maximum size of a log file.<br>
	 *     The value must be a number followed by a unit, the unit must be one of the following:<br>
	 * </p>
	 * <ul>
	 *     <li>{@code 'B'}: bytes</li>
	 *     <li>{@code 'KB'}: kilobytes</li>
	 *     <li>{@code 'MB'}: megabytes</li>
	 *     <li>{@code 'GB'}: gigabytes</li>
	 *     <li>{@code 'TB'}: terabytes</li>
	 * </ul>
	 * <p>
	 *     The default value is {@code '20MB'}.<br>
	 * </p>
	 */
	private static final String LOGGING_FILE_SIZE = "logging.file.size";
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
	 * Constant for the system property 'logging.archive.type'.<br>
	 * <p>
	 *     The property is used to set the type of the log archives.<br>
	 *     The value must be one of the following:<br>
	 * </p>
	 * <ul>
	 *     <li>{@code '.zip'}: compressed with the zip algorithm</li>
	 *     <li>{@code '.gz'}: compressed with the gzip algorithm</li>
	 *     <li>{@code '.bz2'}: compressed with the bzip2 algorithm</li>
	 *     <li>{@code '.xz'}: compressed with the xz algorithm</li>
	 * </ul>
	 * <p>
	 *     The default value is {@code '.gz'}.<br>
	 * </p>
	 */
	private static final String LOGGING_ARCHIVE_TYPE = "logging.archive.type";
	/**
	 * Constant for the system property 'logging.archive.compression_level'.<br>
	 * <p>
	 *     The property is used to set the compression level of the log archives.<br>
	 *     The value must be a number between 0 and 9, a higher value means a higher compression level.<br>
	 *     If the value is out of range, the value will be clamped to the nearest valid value.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 4}.<br>
	 * </p>
	 */
	private static final String LOGGING_ARCHIVE_COMPRESSION_LEVEL = "logging.archive.compression_Level";
	/**
	 * Constant for the system property 'logging.archive.max_files'.<br>
	 * <p>
	 *     The property is used to set the maximum number of log archives which should be kept.<br>
	 *     The value must be a number greater than 0.<br>
	 *     If the value is less than 1, the value will be clamped to 1.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 10}.<br>
	 * </p>
	 */
	private static final String LOGGING_ARCHIVE_MAX_FILES = "logging.archive.max_files";
	/**
	 * Constant for the system property 'logging.archive.auto_deletion.depth'.<br>
	 * <p>
	 *     The property is used to set the maximum depth of the log archive auto deletion.<br>
	 *     The value must be a number greater than 0.<br>
	 *     If the value is less than 1, the value will be clamped to 1.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 1}.<br>
	 * </p>
	 */
	private static final String LOGGING_ARCHIVE_AUTO_DELETION_DEPTH = "logging.archive.auto_deletion.depth";
	/**
	 * Constant for the system property 'logging.archive.auto_deletion.age'.<br>
	 * <p>
	 *     The property is used to set the maximum age of the log archive auto deletion.<br>
	 *     The value must be a number greater than 0.<br>
	 *     If the value is less than 1, the value will be clamped to 1.<br>
	 * </p>
	 * <p>
	 *     The default value is {@code 30}.<br>
	 * </p>
	 */
	private static final String LOGGING_ARCHIVE_AUTO_DELETION_AGE = "logging.archive.auto_deletion.age";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private LoggingHelper() {}
	
	/**
	 * Creates a new logger configuration for the given loggers.<br>
	 * The configuration is created with the default values and modifies it according to the system properties.<br>
	 * General system properties:<br>
	 * <ul>
	 *     <li>
	 *         logging.status_level<br>
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
	 * <br>
	 * Log and archive system properties:<br>
	 * <ul>
	 *     <li>
	 *         logging.file.size<br>
	 *         The maximum size of a log file, expect a number followed by a unit.<br>
	 *         Default: '20MB'<br>
	 *     </li>
	 *     <li>
	 *         logging.archive.type<br>
	 *         The type of the log archives, expect a file extension ('.zip', '.gz', '.bz2', '.xz').<br>
	 *         Default: '.gz'<br>
	 *     </li>
	 *     <li>
	 *         logging.archive.compression_level<br>
	 *         The compression level of the log archives, expect a number between 0 and 9.<br>
	 *         Default: '4'<br>
	 *     </li>
	 *     <li>
	 *         logging.archive.max_files<br>
	 *         The maximum number of log archives which should be kept, expect a number greater than 0.<br>
	 *         Default: '10'<br>
	 *     </li>
	 *     <li>
	 *         logging.archive.auto_deletion.depth<br>
	 *         The maximum depth of the log archive auto deletion, expect a number greater than 0.<br>
	 *         Default: '1'<br>
	 *     </li>
	 *     <li>
	 *         logging.archive.auto_deletion.age<br>
	 *         The maximum age of the log archive auto deletion, expect a number greater than 0.<br>
	 *         Default: '30'<br>
	 *     </li>
	 * </ul>
	 * <br>
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
	 *        Default: '{level}-%d{dd-MM-yyyy}-%i.log'<br>
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
		String config = System.getProperty(LOGGING_CONFIG);
		if (StringUtils.isNotBlank(config)) {
			String file = config.replace("\\", "/");
			if (!file.matches(PATH_PATTERN)) {
				throw new InvalidValueException("Invalid value '" + file + "' for property '" + LOGGING_CONFIG + "', must be a relative or absolute path");
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
		boolean noOverrides = !Boolean.parseBoolean(System.getProperty(LOGGING_CONFIG_OVERRIDE, "false"));
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
		String fileSize = System.getProperty(LOGGING_FILE_SIZE, "20MB");
		if (!Pattern.compile(FILE_SIZE, Pattern.CASE_INSENSITIVE).matcher(fileSize).matches()) {
			throw new IllegalArgumentException("Invalid value '" + fileSize + "' for property '" + LOGGING_FILE_SIZE + "'");
		}
		config.setFileSize(fileSize);
		String archiveType = System.getProperty(LOGGING_ARCHIVE_TYPE, "gz");
		if (!Pattern.compile(ARCHIVE_TYPE, Pattern.CASE_INSENSITIVE).matcher(archiveType).matches()) {
			throw new IllegalArgumentException("Invalid value '" + archiveType + "' for property '" + LOGGING_ARCHIVE_TYPE + "'");
		}
		config.setArchiveType(archiveType);
		String compressionLevel = System.getProperty(LOGGING_ARCHIVE_COMPRESSION_LEVEL, "4");
		try {
			config.setCompressionLevel(Integer.parseInt(compressionLevel));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value '" + compressionLevel + "' for property '" + LOGGING_ARCHIVE_COMPRESSION_LEVEL + "', expected a number between 0 and 9");
		}
		String maxArchiveFiles = System.getProperty(LOGGING_ARCHIVE_MAX_FILES, "10");
		try {
			config.setMaxArchiveFiles(NumberUtils.toInt(maxArchiveFiles, 10));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value '" + maxArchiveFiles + "' for property '" + LOGGING_ARCHIVE_MAX_FILES + "', expected a number greater than 0");
		}
		String autoDeletionDepth = System.getProperty(LOGGING_ARCHIVE_AUTO_DELETION_DEPTH, "1");
		try {
			config.setArchiveAutoDeletionDepth(NumberUtils.toInt(autoDeletionDepth, 1));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value '" + autoDeletionDepth + "' for property '" + LOGGING_ARCHIVE_AUTO_DELETION_DEPTH + "', expected a number greater than 0");
		}
		String autoDeletionAge = System.getProperty(LOGGING_ARCHIVE_AUTO_DELETION_AGE, "30");
		try {
			config.setArchiveAutoDeletionAge(NumberUtils.toInt(autoDeletionAge, 30));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid value '" + autoDeletionAge + "' for property '" + LOGGING_ARCHIVE_AUTO_DELETION_AGE + "', expected a number greater than 0");
		}
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
