package net.luis.utils.logging;

import com.google.common.collect.*;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Runtime logger configuration for Log4j2.<br>
 * The default configuration is:<br>
 * <ul>
 *     <li>Console logging enabled</li>
 *     <li>File logging enabled</li>
 * </ul>
 * A disabled logging type will not be initialized and therefore not be available and can not be enabled later.<br>
 * The default loggers are:<br>
 * <ul>
 *     <li>Console: INFO, WARN, ERROR, FATAL</li>
 *     <li>File: none</li>
 * </ul>
 * Additional loggers can be added directly to the configuration or after initialization using {@link LoggingUtils}.<br>
 *
 * @see LoggingUtils
 * @see LoggingHelper
 *
 * @author Luis-St
 */
public class LoggerConfiguration {
	
	/**
	 * Regex to check if a path is absolute.<br>
	 */
	private static final String DRIVER_REGEX = "^([a-zA-Z]:).*$";
	/**
	 * The names of the log levels used in the default patterns.<br>
	 */
	private static final String NAMES = "TRACE=Trace, DEBUG=Debug, INFO=Info, WARN=Warn, ERROR=Error, FATAL=Fatal";
	/**
	 * The default patterns for the log levels.<br>
	 */
	private static final Map<Level, String> DEFAULT_PATTERNS = Utils.make(Maps.newHashMap(), map -> {
		map.put(Level.TRACE, "[%d{HH:mm:ss}] [%t] [%C:%line/%level{" + NAMES + "}] %msg%n%throwable");
		map.put(Level.DEBUG, "[%d{HH:mm:ss}] [%t] [%C{1}:%line/%level{" + NAMES + "}] %msg%n%throwable");
		map.put(Level.INFO, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + NAMES + "}] %msg%n%throwable");
		map.put(Level.WARN, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + NAMES + "}] %msg%n%throwable");
		map.put(Level.ERROR, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + NAMES + "}] %msg%n%throwable");
		map.put(Level.FATAL, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + NAMES + "}] %msg%n%throwable");
	});
	/**
	 * The default logger configuration.<br>
	 */
	public static final LoggerConfiguration DEFAULT = new LoggerConfiguration("*");
	
	/**
	 * The names of the loggers which should be configured.<br>
	 */
	private final List<String> loggers;
	/**
	 * The allowed logging types.<br>
	 */
	private final Set<LoggingType> allowedTypes = Sets.newHashSet(LoggingType.CONSOLE, LoggingType.FILE);
	/**
	 * The pattern overrides for the logging types and levels.<br>
	 */
	private final Map<LoggingType, Map<Level, String>> patternOverrides = Utils.make(Maps.newHashMap(), map -> {
		map.put(LoggingType.CONSOLE, Maps.newHashMap());
		map.put(LoggingType.FILE, Maps.newHashMap());
	});
	/**
	 * The default loggers for the logging types and levels.<br>
	 */
	private final Map<LoggingType, List<Level>> defaultLoggers = Utils.make(Maps.newHashMap(), map -> {
		map.put(LoggingType.CONSOLE, Lists.newArrayList(Level.INFO, Level.WARN, Level.ERROR, Level.FATAL));
		map.put(LoggingType.FILE, Lists.newArrayList());
	});
	/**
	 * The log file and archive patterns for the log levels.<br>
	 */
	private final Map<Level, Map.Entry<String, String>> logs = Utils.make(Maps.newHashMap(), map -> {
		map.put(Level.DEBUG, Map.entry("logs/debug.log", "logs/debug-%d{dd-MM-yyyy}-%i.log.gz"));
		map.put(Level.INFO, Map.entry("logs/info.log", "logs/info-%d{dd-MM-yyyy}-%i.log.gz"));
		map.put(Level.ERROR, Map.entry("logs/error.log", "logs/error-%d{dd-MM-yyyy}-%i.log.gz"));
	});
	/**
	 * The status level for the internal Log4j2 logger.<br>
	 */
	private Level statusLevel = Level.ERROR;
	/**
	 * The root directory for all log files.<br>
	 */
	private String rootDirectory = "./";
	
	/**
	 * Constructs a new {@link LoggerConfiguration} with the specified logger name.<br>
	 * The logger name is used to identify the logger in the configuration.<br>
	 * The logger name must be the package name, the full class name or a '*' to include all loggers.<br>
	 * If the list contains a '*', all other logger names will be ignored.<br>
	 * @param loggers The names of the logger which should be configured
	 * @throws NullPointerException If the given array is null
	 * @throws IllegalArgumentException If the given list is empty or does not contain any valid logger name
	 * @see #LoggerConfiguration(List)
	 */
	public LoggerConfiguration(String @NotNull ... loggers) {
		this(Lists.newArrayList(Objects.requireNonNull(loggers, "Loggers must not be null")));
	}
	
	/**
	 * Constructs a new {@link LoggerConfiguration} with the specified logger name.<br>
	 * The logger name is used to identify the logger in the configuration.<br>
	 * The logger name must be the package name, the full class name or a '*' to include all loggers.<br>
	 * If the list contains a '*', all other logger names will be ignored.<br>
	 * @param loggers The names of the logger which should be configured
	 * @throws NullPointerException If the given list is null
	 * @throws IllegalArgumentException If the given list is empty or does not contain any valid logger name
	 */
	public LoggerConfiguration(@NotNull List<String> loggers) {
		Objects.requireNonNull(loggers, "Loggers must not be null");
		if (loggers.isEmpty()) {
			throw new IllegalArgumentException("Loggers must not be empty");
		}
		loggers.removeIf(StringUtils::isBlank);
		if (loggers.isEmpty()) {
			throw new IllegalArgumentException("Loggers does not contain any valid logger name");
		}
		this.loggers = loggers.contains("*") ? Lists.newArrayList("*") : loggers;
	}
	
	//region Status level
	
	/**
	 * Sets the status level for the internal Log4j2 logger.<br>
	 * @param level The level to set
	 * @return The current configuration builder
	 * @throws NullPointerException If the given level is null
	 */
	public @NotNull LoggerConfiguration setStatusLevel(@NotNull Level level) {
		this.statusLevel = Objects.requireNonNull(level, "Level must not be null");
		return this;
	}
	//endregion
	
	//region Enable/Disable logging type
	
	/**
	 * Enables the given logging type.<br>
	 * @param type The type to enable
	 * @return The current configuration builder
	 * @throws NullPointerException If the given type is null
	 */
	public @NotNull LoggerConfiguration enableLogging(@NotNull LoggingType type) {
		this.allowedTypes.add(Objects.requireNonNull(type, "Logging type must not be null"));
		return this;
	}
	
	/**
	 * Disables the given logging type.<br>
	 * @param type The type to disable
	 * @return The current configuration builder
	 */
	public @NotNull LoggerConfiguration disableLogging(@Nullable LoggingType type) {
		this.allowedTypes.remove(type);
		return this;
	}
	//endregion
	
	//region Pattern override
	
	/**
	 * Overrides the pattern for the given logging type and level.<br>
	 * Default patterns can be found in {@link LoggerConfiguration#DEFAULT_PATTERNS}.<br>
	 * @param type The logging type to override the pattern for ({@link LoggingType#CONSOLE} or {@link LoggingType#FILE})
	 * @param level The level to override the pattern for ({@link Level#ALL} overrides all levels, {@link Level#OFF} clears all overrides)
	 * @param pattern The pattern to use instead of the default one
	 * @return The current configuration builder
	 * @throws NullPointerException If the given type, level or pattern is null
	 * @throws IllegalArgumentException If the given pattern is empty or does not contain any of the following placeholders: %m, %msg, %message, %throwable, %ex, %exception
	 */
	public @NotNull LoggerConfiguration overridePattern(@NotNull LoggingType type, @NotNull Level level, @NotNull String pattern) {
		Objects.requireNonNull(type, "Logging type must not be null");
		Objects.requireNonNull(level, "Level must not be null");
		Objects.requireNonNull(pattern, "Pattern must not be null");
		if (StringUtils.isBlank(pattern)) {
			throw new IllegalArgumentException("Pattern must not be empty");
		}
		if (!StringUtils.containsAny(pattern, "%m", "%msg", "%message", "%throwable", "%ex", "%exception")) {
			throw new IllegalArgumentException("Pattern must contain at least one of the following placeholders: %m, %msg, %message, %throwable, %ex, %exception");
		}
		if (level == Level.ALL) {
			for (Level temp : type.getAllowedLevels()) {
				this.patternOverrides.computeIfAbsent(type, k -> Maps.newHashMap()).put(temp, pattern);
			}
		} else if (level == Level.OFF) {
			this.patternOverrides.computeIfAbsent(type, k -> Maps.newHashMap()).clear();
		} else {
			this.patternOverrides.computeIfAbsent(type, k -> Maps.newHashMap()).put(level, pattern);
		}
		return this;
	}
	
	/**
	 * Overrides the console pattern for the given level.<br>
	 * @param level The level to override the pattern for ({@link Level#ALL} overrides all levels, {@link Level#OFF} clears all overrides)
	 * @param pattern The pattern to use instead of the default one
	 * @return The current configuration builder
	 * @throws NullPointerException If the given level or pattern is null
	 * @throws IllegalArgumentException If the given pattern is empty or does not contain any of the following placeholders: %m, %msg, %message, %throwable, %ex, %exception
	 * @see LoggerConfiguration#overridePattern(LoggingType, Level, String)
	 */
	public @NotNull LoggerConfiguration overrideConsolePattern(@NotNull Level level, @NotNull String pattern) {
		return this.overridePattern(LoggingType.CONSOLE, level, pattern);
	}
	
	/**
	 * Overrides the file pattern for the given level.<br>
	 * @param level The level to override the pattern for ({@link Level#ALL} overrides all levels, {@link Level#OFF} clears all overrides)
	 * @param pattern The pattern to use instead of the default one
	 * @return The current configuration builder
	 * @throws NullPointerException If the given level or pattern is null
	 * @throws IllegalArgumentException If the given pattern is empty or does not contain any of the following placeholders: %m, %msg, %message, %throwable, %ex, %exception
	 * @see LoggerConfiguration#overridePattern(LoggingType, Level, String)
	 */
	public @NotNull LoggerConfiguration overrideFilePattern(@NotNull Level level, @NotNull String pattern) {
		return this.overridePattern(LoggingType.FILE, level, pattern);
	}
	//endregion
	
	//region Log override
	
	/**
	 * Sets the root directory for all log files.<br>
	 * The root directory is the directory where the log files will be stored.<br>
	 * The default root directory is './'.<br>
	 * @param rootDirectory The root directory to set, the path must be relative or absolute
	 * @return The current configuration builder
	 * @throws NullPointerException If the given root folder is null
	 * @throws IllegalArgumentException If the given root folder is empty or not relative or absolute
	 */
	public @NotNull LoggerConfiguration setRootDirectory(@NotNull String rootDirectory) {
		Objects.requireNonNull(rootDirectory, "Root directory must not be null");
		if (StringUtils.isBlank(rootDirectory)) {
			throw new IllegalArgumentException("Root folder must not be empty");
		}
		rootDirectory = rootDirectory.replace("\\", "/");
		if (!rootDirectory.startsWith("./") && !rootDirectory.matches(DRIVER_REGEX)) { // Must start with './' or a driver letter
			throw new IllegalArgumentException("Root folder must be relative or absolute");
		}
		this.rootDirectory = StringUtils.strip(rootDirectory, "/") + "/"; // Must end with '/'
		return this;
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for the given log level.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute or relative.<br>
	 * The log file will be built as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param level The level to override the log file for
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given level, file name or file pattern is null
	 * @throws IllegalArgumentException If the given level is not valid, or if the given files are empty, absolute or relative
	 */
	public @NotNull LoggerConfiguration overrideLog(@NotNull Level level, @NotNull String file, @NotNull String archive) {
		this.validateLevel(level);
		Objects.requireNonNull(file, "File must not be null");
		Objects.requireNonNull(archive, "Archive must not be null");
		if (StringUtils.isBlank(file)) {
			throw new IllegalArgumentException("File name must not be empty");
		}
		if (StringUtils.isBlank(archive)) {
			throw new IllegalArgumentException("Archive must not be empty");
		}
		file = file.replace("\\", "/");
		if (file.matches("^([a-zA-Z]:|\\./).*$")) {
			throw new IllegalArgumentException("File name must not be absolute or relative");
		}
		archive = archive.replace("\\", "/");
		if (archive.matches("^([a-zA-Z]:|\\./).*$")) {
			throw new IllegalArgumentException("Archive must not be absolute or relative");
		}
		this.logs.put(level, Map.entry(StringUtils.strip(file, "/"), StringUtils.strip(archive, "/"))); // Must not start with '/'
		return this;
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for log level {@link Level#DEBUG}.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute or relative.<br>
	 * The log file will be built as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given file name or file pattern is null
	 * @throws IllegalArgumentException If the given level is not valid, or if the given files are empty, absolute or relative
	 * @see LoggerConfiguration#overrideLog(Level, String, String)
	 */
	public @NotNull LoggerConfiguration overrideDebugLog(@NotNull String file, @NotNull String archive) {
		return this.overrideLog(Level.DEBUG, file, archive);
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for log level {@link Level#INFO}.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute or relative.<br>
	 * The log file will be built as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given file name or file pattern is null
	 * @throws IllegalArgumentException If the given level is not valid, or if the given files are empty or absolute
	 * @see LoggerConfiguration#overrideLog(Level, String, String)
	 */
	public @NotNull LoggerConfiguration overrideInfoLog(@NotNull String file, @NotNull String archive) {
		return this.overrideLog(Level.INFO, file, archive);
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for log level {@link Level#ERROR}.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute or relative.<br>
	 * The log file will be built as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given file name or file pattern is null
	 * @throws IllegalArgumentException If the given level is not valid, or if the given files are empty, absolute or relative
	 * @see LoggerConfiguration#overrideLog(Level, String, String)
	 */
	public @NotNull LoggerConfiguration overrideErrorLog(@NotNull String file, @NotNull String archive) {
		return this.overrideLog(Level.ERROR, file, archive);
	}
	
	/**
	 * Validates the given level.<br>
	 * @param level The level to validate
	 * @throws NullPointerException If the given level is null
	 * @throws IllegalArgumentException If the given level is not {@link Level#DEBUG}, {@link Level#INFO} or {@link Level#ERROR}
	 */
	private void validateLevel(@NotNull Level level) {
		Objects.requireNonNull(level, "Level must not be null");
		if (level == Level.ALL || level == Level.OFF) {
			throw new IllegalArgumentException("Level must not be 'all' or 'off'");
		}
		if (level != Level.DEBUG && level != Level.INFO && level != Level.ERROR) {
			throw new IllegalArgumentException("Logging type 'file' does not support level '" + level.name().toLowerCase() + "'");
		}
	}
	//endregion
	
	//region Default loggers
	
	/**
	 * Adds the default logger for the given type and level.<br>
	 * All default loggers will be automatically enabled at startup.<br>
	 * <p>
	 *     Console loggers which are not configured at startup can be configured later using<br>
	 *     the enable and disable methods in {@link LoggingUtils}.<br>
	 * </p>
	 * <p>
	 *     File loggers which are not configured at startup are not available and can not be configured later.<br>
	 *     If a file logger is  configured at startup, it can be configured later using<br>
	 *     the enable and disable methods in {@link LoggingUtils}.<br>
	 * </p>
	 * @param type The type to add the default logger for
	 * @param level The level to add the default logger for
	 * @return The current configuration builder
	 * @throws NullPointerException If the given type or level is null
	 * @throws IllegalArgumentException If the given type is not allowed
	 */
	public @NotNull LoggerConfiguration addDefaultLogger(@NotNull LoggingType type, @NotNull Level level) {
		LoggingUtils.checkLevel(type, level);
		if (!this.allowedTypes.contains(type)) {
			throw new IllegalArgumentException("Logging type '" + type.name().toLowerCase() + "' is not allowed");
		}
		this.defaultLoggers.computeIfAbsent(type, k -> Lists.newArrayList()).add(level);
		return this;
	}
	
	/**
	 * Removes the default logger for the given type and level.<br>
	 * Removed default loggers will not be automatically enabled at startup.<br>
	 * <p>
	 *     Console loggers which are not configured at startup can be configured later using<br>
	 *     the enable and disable methods in {@link LoggingUtils}.<br>
	 * </p>
	 * <p>
	 *     File loggers which are not configured at startup are not available and can not be configured later.<br>
	 *     If a file logger is  configured at startup, it can be configured later using<br>
	 *     the enable and disable methods in {@link LoggingUtils}.<br>
	 * </p>
	 * @param type The type to remove the default logger for
	 * @param level The level to remove the default logger for
	 * @return The current configuration builder
	 */
	public @NotNull LoggerConfiguration removeDefaultLogger(@Nullable LoggingType type, @Nullable Level level) {
		this.defaultLoggers.getOrDefault(type, Lists.newArrayList()).remove(level);
		return this;
	}
	//endregion
	
	/**
	 * Builds the configuration.<br>
	 * @return The built configuration
	 */
	public @NotNull Configuration build() {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setConfigurationName("RuntimeConfiguration");
		builder.setStatusLevel(this.statusLevel);
		List<String> appenders = Lists.newArrayList();
		if (this.allowedTypes.contains(LoggingType.CONSOLE)) {
			for (Level level : LoggingType.CONSOLE) {
				String name = LoggingUtils.getLogger(LoggingType.CONSOLE, level);
				builder.add(
					builder.newAppender(name, "Console")
						.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
						.addComponent(builder.newLayout("PatternLayout")
							.addAttribute("pattern", this.getPattern(LoggingType.CONSOLE, level)))
						.addComponent(builder.newFilter("LevelMatchFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
							.addAttribute("level", level)));
				if (this.defaultLoggers.getOrDefault(LoggingType.CONSOLE, Lists.newArrayList()).contains(level)) {
					appenders.add(name);
				}
			}
		}
		if (this.allowedTypes.contains(LoggingType.FILE)) {
			for (Level level : LoggingType.FILE) {
				// File loggers must be configured at startup otherwise they are not available
				// Reason: Avoid creating a new file for each logger
				if (this.defaultLoggers.getOrDefault(LoggingType.FILE, Lists.newArrayList()).contains(level)) {
					String name = LoggingUtils.getLogger(LoggingType.FILE, level);
					builder.add(
						builder.newAppender(name, "RollingRandomAccessFile")
							.addAttribute("fileName", this.rootDirectory + this.logs.get(level).getKey())
							.addAttribute("filePattern", this.rootDirectory + this.logs.get(level).getValue())
							.addComponent(builder.newLayout("PatternLayout")
								.addAttribute("pattern", this.getPattern(LoggingType.FILE, level)))
							.addComponent(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
								.addAttribute("level", level))
							.addComponent(builder.newComponent("Policies")
								.addComponent(builder.newComponent("SizeBasedTriggeringPolicy"))
								.addComponent(builder.newComponent("OnStartupTriggeringPolicy"))
								.addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
									.addAttribute("interval", 1)
									.addAttribute("modulate", true))));
					appenders.add(name);
				}
			}
		}
		if (this.loggers.stream().anyMatch(logger -> logger.contains("*"))) {
			RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ALL);
			for (String appender : appenders) {
				rootLogger.add(builder.newAppenderRef(appender));
			}
			builder.add(rootLogger);
		} else {
			builder.add(builder.newRootLogger(Level.OFF));
			for (String logger : this.loggers) {
				LoggerComponentBuilder loggerBuilder = builder.newLogger(logger, Level.ALL);
				loggerBuilder.addAttribute("additivity", false);
				for (String appender : appenders) {
					loggerBuilder.add(builder.newAppenderRef(appender));
				}
				builder.add(loggerBuilder);
			}
		}
		return builder.build();
	}
	
	//region Helper methods
	
	/**
	 * Gets the pattern for the given type and level.<br>
	 * @param type The type to get the pattern for
	 * @param level The level to get the pattern for
	 * @return The pattern for the given type and level, or the default pattern if there is no override
	 */
	private @NotNull String getPattern(@NotNull LoggingType type, @NotNull Level level) {
		return this.patternOverrides.getOrDefault(type, Maps.newHashMap()).getOrDefault(level, DEFAULT_PATTERNS.get(level));
	}
	//endregion
}
