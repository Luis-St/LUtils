package net.luis.utils.logging;

import com.google.common.collect.*;
import net.luis.utils.util.Pair;
import net.luis.utils.util.Utils;
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
 *
 * @author Luis-St
 *
 */

public class LoggerConfiguration {
	
	private static final Map<Level, String> DEFAULT_PATTERNS = Utils.make(Maps.newHashMap(), patterns -> {
		String names = "TRACE=Trace, DEBUG=Debug, INFO=Info, WARN=Warn, ERROR=Error, FATAL=Fatal";
		patterns.put(Level.TRACE, "[%d{HH:mm:ss}] [%t] [%C:%line/%level{" + names + "}] %msg%n%throwable");
		patterns.put(Level.DEBUG, "[%d{HH:mm:ss}] [%t] [%C{1}:%line/%level{" + names + "}] %msg%n%throwable");
		patterns.put(Level.INFO, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + names + "}] %msg%n%throwable");
		patterns.put(Level.WARN, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + names + "}] %msg%n%throwable");
		patterns.put(Level.ERROR, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + names + "}] %msg%n%throwable");
		patterns.put(Level.FATAL, "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + names + "}] %msg%n%throwable");
	});
	public static final LoggerConfiguration DEFAULT = new LoggerConfiguration();
	
	private final Set<LoggingType> allowedTypes = Sets.newHashSet(LoggingType.CONSOLE, LoggingType.FILE);
	private final Map<LoggingType, Map<Level, String>> patternOverrides = Utils.make(Maps.newEnumMap(LoggingType.class), type -> {
		type.put(LoggingType.CONSOLE, Maps.newHashMap());
		type.put(LoggingType.FILE, Maps.newHashMap());
	});
	private final Map<LoggingType, List<Level>> defaultLoggers = Utils.make(Maps.newEnumMap(LoggingType.class), loggers -> {
		loggers.put(LoggingType.CONSOLE, Lists.newArrayList(Level.INFO, Level.WARN, Level.ERROR, Level.FATAL));
		loggers.put(LoggingType.FILE, Lists.newArrayList());
	});
	private final Map<Level, Pair<String, String>> logs = Utils.make(Maps.newHashMap(), logs -> {
		logs.put(Level.DEBUG, Pair.of("./logs/debug.log", "./logs/debug-%d{dd-MM-yyyy}-%i.log.gz"));
		logs.put(Level.INFO, Pair.of("./logs/info.log", "./logs/info-%d{dd-MM-yyyy}-%i.log.gz"));
		logs.put(Level.ERROR, Pair.of("./logs/error.log", "./logs/error-%d{dd-MM-yyyy}-%i.log.gz"));
	});
	private Level statusLevel = Level.ERROR;
	
	//region Status level
	public @NotNull LoggerConfiguration setStatusLevel(@NotNull Level level) {
		this.statusLevel = Objects.requireNonNull(level, "Level must not be null");
		return this;
	}
	//endregion
	
	//region Enable/Disable logging type
	public @NotNull LoggerConfiguration enableLogging(@NotNull LoggingType type) {
		this.allowedTypes.add(Objects.requireNonNull(type, "Logging type must not be null"));
		return this;
	}
	
	public @NotNull LoggerConfiguration disableLogging(@Nullable LoggingType type) {
		this.allowedTypes.remove(type);
		return this;
	}
	//endregion
	
	//region Pattern override
	public @NotNull LoggerConfiguration overridePattern(@NotNull LoggingType type, @NotNull Level level, @NotNull String pattern) {
		Objects.requireNonNull(type, "Logging type must not be null");
		Objects.requireNonNull(level, "Level must not be null");
		Objects.requireNonNull(pattern, "Pattern must not be null");
		if (level == Level.ALL) {
			for (Level temp : LoggingUtils.CONSOLE_LEVELS) {
				this.patternOverrides.get(type).put(temp, pattern);
			}
		} else if (level == Level.OFF) {
			this.patternOverrides.get(type).clear();
		} else {
			this.patternOverrides.get(type).put(level, pattern);
		}
		return this;
	}
	
	public @NotNull LoggerConfiguration overrideConsolePattern(@NotNull Level level, @NotNull String pattern) {
		return this.overridePattern(LoggingType.CONSOLE, level, pattern);
	}
	
	public @NotNull LoggerConfiguration overrideFilePattern(@NotNull Level level, @NotNull String pattern) {
		return this.overridePattern(LoggingType.FILE, level, pattern);
	}
	//endregion
	
	//region Log override
	public @NotNull LoggerConfiguration overrideLog(@NotNull Level level, @NotNull String fileName, @NotNull String filePattern) {
		this.validateLevel(level);
		this.logs.put(level, Pair.of(Objects.requireNonNull(fileName, "File name must not be null"), Objects.requireNonNull(filePattern, "File pattern must not be null")));
		return this;
	}
	
	public @NotNull LoggerConfiguration overrideDebugLog(@NotNull String fileName, @NotNull String filePattern) {
		return this.overrideLog(Level.DEBUG, fileName, filePattern);
	}
	
	public @NotNull LoggerConfiguration overrideInfoLog(@NotNull String fileName, @NotNull String filePattern) {
		return this.overrideLog(Level.INFO, fileName, filePattern);
	}
	
	public @NotNull LoggerConfiguration overrideErrorLog(@NotNull String fileName, @NotNull String filePattern) {
		return this.overrideLog(Level.ERROR, fileName, filePattern);
	}
	
	private void validateLevel(@NotNull Level level) {
		Objects.requireNonNull(level, "Level must not be null");
		if (level == Level.ALL || level == Level.OFF) {
			throw new IllegalArgumentException("Level must not be ALL or OFF");
		}
		if (level != Level.DEBUG && level != Level.INFO && level != Level.ERROR) {
			throw new IllegalArgumentException("Logging type 'file' does not support level '" + level.name().toLowerCase() + "'");
		}
	}
	//endregion
	
	//region Default loggers
	public @NotNull LoggerConfiguration addDefaultLogger(@NotNull LoggingType type, @NotNull Level level) {
		LoggingUtils.checkLevel(type, Objects.requireNonNull(level, "Level must not be null"));
		this.defaultLoggers.get(type).add(level);
		return this;
	}
	
	public @NotNull LoggerConfiguration removeDefaultLogger(@NotNull LoggingType type, @Nullable Level level) {
		this.defaultLoggers.get(type).remove(level);
		return this;
	}
	//endregion
	
	public @NotNull Configuration build() {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setConfigurationName("RuntimeConfiguration");
		builder.setStatusLevel(this.statusLevel);
		RootLoggerComponentBuilder rootBuilder = builder.newRootLogger(Level.ALL);
		if (this.allowedTypes.contains(LoggingType.CONSOLE)) {
			for (Level level : LoggingUtils.CONSOLE_LEVELS) {
				String name = LoggingUtils.getLogger(LoggingType.CONSOLE, level);
				builder.add(builder.newAppender(name, "Console")
					.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
					.add(this.pattern(builder, this.getPattern(LoggingType.CONSOLE, level)))
					.add(this.filter(builder, "LevelMatchFilter", level)));
				if (this.defaultLoggers.getOrDefault(LoggingType.CONSOLE, Lists.newArrayList()).contains(level)) {
					rootBuilder.add(builder.newAppenderRef(name));
				}
			}
		}
		if (this.allowedTypes.contains(LoggingType.FILE)) {
			for (Level level : LoggingUtils.FILE_LEVELS) {
				String name = LoggingUtils.getLogger(LoggingType.FILE, level);
				builder.add(builder.newAppender(name, "RollingRandomAccessFile")
					.addAttribute("fileName", this.logs.get(level).getFirst())
					.addAttribute("filePattern", this.logs.get(level).getSecond())
					.add(this.pattern(builder, this.getPattern(LoggingType.FILE, level)))
					.add(this.filter(builder, "ThresholdFilter", level))
					.addComponent(this.policies(builder)));
				if (this.defaultLoggers.getOrDefault(LoggingType.FILE, Lists.newArrayList()).contains(level)) {
					rootBuilder.add(builder.newAppenderRef(name));
				}
			}
		}
		return builder.add(rootBuilder).build();
	}
	
	//region Helper methods
	private String getPattern(@Nullable LoggingType type, @NotNull Level level) {
		return this.patternOverrides.getOrDefault(type, Maps.newHashMap()).getOrDefault(level, DEFAULT_PATTERNS.get(level));
	}
	
	private @NotNull LayoutComponentBuilder pattern(@NotNull ConfigurationBuilder<BuiltConfiguration> builder, @NotNull String pattern) {
		Objects.requireNonNull(builder, "Builder must not be null");
		Objects.requireNonNull(pattern, "Pattern must not be null");
		return builder.newLayout("PatternLayout").addAttribute("pattern", pattern);
	}
	
	private @NotNull FilterComponentBuilder filter(@NotNull ConfigurationBuilder<BuiltConfiguration> builder, @NotNull String filter, @NotNull Level level) {
		Objects.requireNonNull(builder, "Builder must not be null");
		Objects.requireNonNull(filter, "Filter must not be null");
		Objects.requireNonNull(level, "Level must not be null");
		return builder.newFilter(filter, Filter.Result.ACCEPT, Filter.Result.DENY).addAttribute("level", level);
	}
	
	private @NotNull ComponentBuilder<?> policies(@NotNull ConfigurationBuilder<BuiltConfiguration> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		return builder.newComponent("Policies").addComponent(builder.newComponent("SizeBasedTriggeringPolicy")).addComponent(builder.newComponent("OnStartupTriggeringPolicy"));
	}
	//endregion
}
