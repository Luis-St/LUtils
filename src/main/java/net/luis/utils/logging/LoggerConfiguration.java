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
	
	private static final String DRIVER_REGEX = "^([a-zA-Z]:).*$";
	private static final String NAMES = "TRACE=Trace, DEBUG=Debug, INFO=Info, WARN=Warn, ERROR=Error, FATAL=Fatal";
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
	
	private final List<String> loggers;
	private final Set<LoggingType> allowedTypes = Sets.newHashSet(LoggingType.CONSOLE, LoggingType.FILE);
	private final Map<LoggingType, Map<Level, String>> patternOverrides = Utils.make(Maps.newHashMap(), map -> {
		map.put(LoggingType.CONSOLE, Maps.newHashMap());
		map.put(LoggingType.FILE, Maps.newHashMap());
	});
	private final Map<LoggingType, List<Level>> defaultLoggers = Utils.make(Maps.newHashMap(), map -> {
		map.put(LoggingType.CONSOLE, Lists.newArrayList(Level.INFO, Level.WARN, Level.ERROR, Level.FATAL));
		map.put(LoggingType.FILE, Lists.newArrayList());
	});
	private final Map<Level, Map.Entry<String, String>> logs = Utils.make(Maps.newHashMap(), map -> {
		map.put(Level.DEBUG, Map.entry("logs/debug.log", "logs/debug-%d{dd-MM-yyyy}-%i.log.gz"));
		map.put(Level.INFO, Map.entry("logs/info.log", "logs/info-%d{dd-MM-yyyy}-%i.log.gz"));
		map.put(Level.ERROR, Map.entry("logs/error.log", "logs/error-%d{dd-MM-yyyy}-%i.log.gz"));
	});
	private Level statusLevel = Level.ERROR;
	private String rootDirectory = "./";
	
	/**
	 * Constructs a new LoggerConfiguration with the specified logger name.<br>
	 * The logger name is used to identify the logger in the configuration.<br>
	 * The logger name must be the package name, the full class name or a '*' to include all loggers.<br>
	 * If the list contains a '*', all other logger names will be ignored.<br>
	 * @param loggers The names of the logger which should be configured
	 * @throws NullPointerException If the given array is null
	 * @throws IllegalArgumentException If the given list is empty or does not contain any valid logger name
	 * @see #LoggerConfiguration(List)
	 */
	public LoggerConfiguration(String @NotNull ... loggers) {
		this(Arrays.asList(loggers));
	}
	
	/**
	 * Constructs a new LoggerConfiguration with the specified logger name.<br>
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
	 */
	public @NotNull LoggerConfiguration overridePattern(@NotNull LoggingType type, @NotNull Level level, @NotNull String pattern) {
		Objects.requireNonNull(type, "Logging type must not be null");
		Objects.requireNonNull(pattern, "Pattern must not be null");
		if (level == Level.ALL) {
			for (Level temp : LoggingUtils.CONSOLE_LEVELS) {
				this.patternOverrides.getOrDefault(type, Maps.newHashMap()).put(temp, pattern);
			}
		} else if (level == Level.OFF) {
			this.patternOverrides.getOrDefault(type, Maps.newHashMap()).clear();
		} else {
			this.patternOverrides.getOrDefault(type, Maps.newHashMap()).put(Objects.requireNonNull(level, "Level must not be null"), pattern);
		}
		return this;
	}
	
	/**
	 * Overrides the console pattern for the given level.<br>
	 * @param level The level to override the pattern for ({@link Level#ALL} overrides all levels, {@link Level#OFF} clears all overrides)
	 * @param pattern The pattern to use instead of the default one
	 * @return The current configuration builder
	 * @throws NullPointerException If the given level or pattern is null
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
	 */
	public @NotNull LoggerConfiguration setRootDirectory(@NotNull String rootDirectory) {
		Objects.requireNonNull(rootDirectory, "Root directory must not be null");
		if (!rootDirectory.startsWith("./") && !rootDirectory.matches(DRIVER_REGEX)) {
			throw new IllegalArgumentException("Root folder must be relative or absolute");
		}
		this.rootDirectory = rootDirectory;
		return this;
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for the given log level.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute.<br>
	 * The log file will be build as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param level The level to override the log file for
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given level, file name or file pattern is null
	 * @throws IllegalArgumentException If the given level is not valid or if the given file name or file pattern is absolute
	 */
	public @NotNull LoggerConfiguration overrideLog(@NotNull Level level, @NotNull String file, @NotNull String archive) {
		this.validateLevel(level);
		Objects.requireNonNull(file, "File must not be null");
		Objects.requireNonNull(archive, "Archive must not be null");
		if (file.matches(DRIVER_REGEX)) {
			throw new IllegalArgumentException("File name must not be absolute");
		}
		if (archive.matches(DRIVER_REGEX)) {
			throw new IllegalArgumentException("File pattern must not be absolute");
		}
		if (file.startsWith("/") || file.startsWith("\\")) {
			file = file.substring(1);
		}
		if (archive.startsWith("/") || archive.startsWith("\\")) {
			archive = archive.substring(1);
		}
		this.logs.put(level, Map.entry(file, archive));
		return this;
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for log level {@link Level#DEBUG}.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute.<br>
	 * The log file will be build as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given file name or file pattern is null
	 * @see LoggerConfiguration#overrideLog(Level, String, String)
	 */
	public @NotNull LoggerConfiguration overrideDebugLog(@NotNull String file, @NotNull String archive) {
		return this.overrideLog(Level.DEBUG, file, archive);
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for log level {@link Level#INFO}.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute.<br>
	 * The log file will be build as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given file name or file pattern is null
	 * @see LoggerConfiguration#overrideLog(Level, String, String)
	 */
	public @NotNull LoggerConfiguration overrideInfoLog(@NotNull String file, @NotNull String archive) {
		return this.overrideLog(Level.INFO, file, archive);
	}
	
	/**
	 * Overrides the pattern for the log file and the archived log file for log level {@link Level#ERROR}.<br>
	 * If the root directory is not set, the default root directory ('./') is used.<br>
	 * If the root directory has been set, the given file and archive will be appended to the root directory.<br>
	 * The file and archive must not be absolute.<br>
	 * The log file will be build as follows: {@code rootDirectory + (file|archive)}<br>
	 * Default patterns can be found in {@link LoggerConfiguration#logs}.<br>
	 * @param file The pattern for the current log file
	 * @param archive The pattern for all archived log files
	 * @return The current configuration builder
	 * @throws NullPointerException If the given file name or file pattern is null
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
	 * Adds a default logger for the given type and level.<br>
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
		this.defaultLoggers.getOrDefault(type, Lists.newArrayList()).add(level);
		return this;
	}
	
	/**
	 * Removes a default logger for the given type and level.<br>
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
		List<AppenderRefComponentBuilder> appenders = Lists.newArrayList();
		if (this.allowedTypes.contains(LoggingType.CONSOLE)) {
			for (Level level : LoggingUtils.CONSOLE_LEVELS) {
				String name = LoggingUtils.getLogger(LoggingType.CONSOLE, level);
				builder.add(
					builder.newAppender(name, "Console")
						.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
						.addComponent(builder.newLayout("PatternLayout")
							.addAttribute("pattern", this.getPattern(LoggingType.CONSOLE, level)))
						.addComponent(builder.newFilter("LevelMatchFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
							.addAttribute("level", level)));
				if (this.defaultLoggers.getOrDefault(LoggingType.CONSOLE, Lists.newArrayList()).contains(level)) {
					appenders.add(builder.newAppenderRef(name));
				}
			}
		}
		if (this.allowedTypes.contains(LoggingType.FILE)) { // Add way to modify Policies
			String root = this.rootDirectory.endsWith("/") || this.rootDirectory.endsWith("\\") ? this.rootDirectory : this.rootDirectory + "/";
			for (Level level : LoggingUtils.FILE_LEVELS) {
				String name = LoggingUtils.getLogger(LoggingType.FILE, level);
				builder.add(
					builder.newAppender(name, "RollingRandomAccessFile")
						.addAttribute("fileName", root + this.logs.get(level).getKey())
						.addAttribute("filePattern", root + this.logs.get(level).getValue())
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
				if (this.defaultLoggers.getOrDefault(LoggingType.FILE, Lists.newArrayList()).contains(level)) {
					appenders.add(builder.newAppenderRef(name));
				}
			}
		}
		if (this.loggers.stream().anyMatch(logger -> logger.contains("*"))) {
			RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ALL);
			for (AppenderRefComponentBuilder appender : appenders) {
				rootLogger.add(appender);
			}
			builder.add(rootLogger);
		} else {
			builder.add(builder.newRootLogger(Level.OFF));
			for (String logger : this.loggers) {
				LoggerComponentBuilder loggerBuilder = builder.newLogger(logger, Level.ALL);
				loggerBuilder.addAttribute("additivity", false);
				for (AppenderRefComponentBuilder appender : appenders) {
					loggerBuilder.add(appender);
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
