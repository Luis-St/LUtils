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
	
	/**
	 * The logger of this class.<br>
	 */
	private static final Logger LOGGER = LogManager.getLogger(LoggingHelper.class);
	
	/**
	 * Creates a new logger configuration and modifies it according to the system properties.<br>
	 * General system properties:<br>
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
	 * </ul>
	 *
	 * File logging system properties:<br>
	 * <ul>
	 *     <li>
	 * 	       logging.file.{level}<br>
	 * 	       Enables/disables file logging for the given level, expect 'true' or 'false'<br>
	 * 	       {level} must be replaced with a valid {@link Level} name in lower case<br>
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
	 * 	               Enables file logging for the debug level
	 * 	           </li>
	 * 	           <li>
	 * 	               logging.file.info=false<br>
	 * 	               Disables file logging for the info level
	 * 	           </li>
	 * 	       </ul>
	 * 	 </li
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
	 *
	 * </ul>
	 *
	 * For removal system properties:<br>
	 * <ul>
	 *     <li>
	 * 	       logging.file.folder (deprecated)<br>
	 * 	       The folder to log to, expect a relative path of a folder<br>
	 * 	       Default: 'logs/{level}'
	 * 	   </li>
	 * </ul>
	 *
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
		String root = System.getProperty("logging.file.folder.root", "./").replace("\\", "/");
		if (!root.matches("^([a-zA-Z]:|\\./).*$")) {
			throw new IllegalArgumentException("Invalid value '" + root + "' for property 'logging.file.folder.root', must be a relative or absolute path");
		}
		config.setRootDirectory(StringUtils.strip(root, "/ ") + "/"); // Root must be relative or absolute and end with '/'
		//region Local record
		record LogFile(Level level, String folder, String file, String archive) {
			
			public static LogFile forLevel(@NotNull Level level) {
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
		// Configure default file loggers before initializing the loggers
		for (Level level : LoggingType.FILE) {
			String value = System.getProperty("logging." + LoggingType.FILE + "." + level.name().toLowerCase(), "");
			if (isEnabled(value)) {
				config.addDefaultLogger(LoggingType.FILE, level);
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
			return Map.entry(level, System.getProperty("logging." + LoggingType.CONSOLE + "." + level.name().toLowerCase(), ""));
		}).filter(entry -> !entry.getValue().isEmpty()).forEach(entry -> {
			Level level = entry.getKey();
			String value = entry.getValue();
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
	 * @param file The file to get the relative path of
	 * @return The relative path of the given file
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
