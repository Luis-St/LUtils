package net.luis.utils.logging;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.luis.utils.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class LoggerConfiguration {
	
	private static final String LEVEL_NAMES = "TRACE=Trace, DEBUG=Debug, INFO=Info, WARN=Warn, ERROR=Error, FATAL=Fatal";
	private static final String TRACE_PATTERN = "[%d{HH:mm:ss}] [%t] [%C:%line/%level{" + LEVEL_NAMES + "}] %msg%n%throwable";
	private static final String DEBUG_PATTERN = "[%d{HH:mm:ss}] [%t] [%C{1}:%line/%level{" + LEVEL_NAMES + "}] %msg%n%throwable";
	private static final String INFO_PATTERN = "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + LEVEL_NAMES + "}] %msg%n%throwable";
	private static final String WARN_PATTERN = "[%d{HH:mm:ss}] [%t] [%C{1}/%level{{" + LEVEL_NAMES + "}] %msg%n%throwable";
	private static final String ERROR_PATTERN = "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + LEVEL_NAMES + "}] %msg%n%throwable";
	private static final String FATAL_PATTERN = "[%d{HH:mm:ss}] [%t] [%C{1}/%level{" + LEVEL_NAMES + "}] %msg%n%throwable";
	public static final LoggerConfiguration DEFAULT = new LoggerConfiguration();
	private final Set<LoggingType> allowedTypes = Sets.newHashSet(LoggingType.CONSOLE, LoggingType.FILE);
	private final Map<Level, String> consolePattern = Maps.newHashMap();
	private final Map<Level, String> filePattern = Maps.newHashMap();
	private final Set<String> defaultLoggers = Sets.newHashSet("ConsoleInfo", "ConsoleWarn", "ConsoleError", "ConsoleFatal");
	private Level statusLevel = Level.ERROR;
	private Pair<String, String> debugLog = Pair.of("./logs/debug.log", "./logs/debug-%d{dd-MM-yyyy}-%i.log.gz");
	private Pair<String, String> infoLog = Pair.of("./logs/info.log", "./logs/info-%d{dd-MM-yyyy}-%i.log.gz");
	
	public LoggerConfiguration setStatusLevel(Level level) {
		this.statusLevel = Objects.requireNonNull(level, "Level must not be null");
		return this;
	}
	
	public LoggerConfiguration enableLogging(LoggingType type) {
		this.allowedTypes.add(Objects.requireNonNull(type, "Type must not be null"));
		return this;
	}
	
	public LoggerConfiguration disableLogging(LoggingType type) {
		this.allowedTypes.remove(type);
		return this;
	}
	
	public LoggerConfiguration overrideConsolePattern(Level level, String pattern) {
		if (level == Level.ALL) {
			for (Level temp : LoggingUtils.CONSOLE_LEVELS) {
				this.consolePattern.put(temp, pattern);
			}
		} else if (level == Level.OFF) {
			this.consolePattern.clear();
		} else {
			this.consolePattern.put(Objects.requireNonNull(level, "Level must not be null"), Objects.requireNonNull(pattern, "Pattern must not be null"));
		}
		return this;
	}
	
	public LoggerConfiguration overrideFilePattern(Level level, String pattern) {
		if (level == Level.ALL) {
			for (Level temp : LoggingUtils.FILE_LEVELS) {
				this.filePattern.put(temp, pattern);
			}
		} else if (level == Level.OFF) {
			this.filePattern.clear();
		} else {
			this.filePattern.put(Objects.requireNonNull(level, "Level must not be null"), Objects.requireNonNull(pattern, "Pattern must not be null"));
		}
		return this;
	}
	
	public LoggerConfiguration overrideDebugLog(String fileName, String filePattern) {
		this.debugLog = Pair.of(Objects.requireNonNull(fileName, "File name must not be null"), Objects.requireNonNull(filePattern, "File pattern must not be null"));
		return this;
	}
	
	public LoggerConfiguration overrideInfoLog(String fileName, String filePattern) {
		this.infoLog = Pair.of(Objects.requireNonNull(fileName, "File name must not be null"), Objects.requireNonNull(filePattern, "File pattern must not be null"));
		return this;
	}
	
	public LoggerConfiguration addDefaultLogger(Level level, LoggingType type) {
		this.defaultLoggers.add(LoggingUtils.getLogger(level, type));
		return this;
	}
	
	public Configuration build() {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setConfigurationName("RuntimeConfiguration");
		builder.setStatusLevel(this.statusLevel);
		if (this.allowedTypes.contains(LoggingType.CONSOLE)) {
			builder.add(builder.newAppender("ConsoleTrace", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(this.pattern(builder, this.consolePattern.getOrDefault(Level.TRACE, TRACE_PATTERN)))
				.add(this.filter(builder, "LevelMatchFilter", Level.TRACE)));
			
			builder.add(builder.newAppender("ConsoleDebug", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(this.pattern(builder, this.consolePattern.getOrDefault(Level.DEBUG, DEBUG_PATTERN)))
				.add(this.filter(builder, "LevelMatchFilter", Level.DEBUG)));
			
			builder.add(builder.newAppender("ConsoleInfo", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(this.pattern(builder, this.consolePattern.getOrDefault(Level.INFO, INFO_PATTERN)))
				.add(this.filter(builder, "LevelMatchFilter", Level.INFO)));
			
			builder.add(builder.newAppender("ConsoleWarn", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(this.pattern(builder, this.consolePattern.getOrDefault(Level.WARN, WARN_PATTERN)))
				.add(this.filter(builder, "LevelMatchFilter", Level.WARN)));
			
			builder.add(builder.newAppender("ConsoleError", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(this.pattern(builder, this.consolePattern.getOrDefault(Level.ERROR, ERROR_PATTERN)))
				.add(this.filter(builder, "LevelMatchFilter", Level.ERROR)));
			
			builder.add(builder.newAppender("ConsoleFatal", "Console")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(this.pattern(builder, this.consolePattern.getOrDefault(Level.FATAL, FATAL_PATTERN)))
				.add(this.filter(builder, "LevelMatchFilter", Level.FATAL)));
		}
		if (this.allowedTypes.contains(LoggingType.FILE)) {
			builder.add(builder.newAppender("DebugLogFile", "RollingRandomAccessFile")
				.addAttribute("fileName", this.debugLog.getFirst())
				.addAttribute("filePattern", this.debugLog.getSecond())
				.add(this.pattern(builder, this.filePattern.getOrDefault(Level.DEBUG, DEBUG_PATTERN)))
				.add(this.filter(builder, "ThresholdFilter", Level.DEBUG))
				.addComponent(this.policies(builder)));
			
			builder.add(builder.newAppender("InfoLogFile", "RollingRandomAccessFile")
				.addAttribute("fileName", this.infoLog.getFirst())
				.addAttribute("filePattern", this.infoLog.getSecond())
				.add(this.pattern(builder, this.filePattern.getOrDefault(Level.INFO, INFO_PATTERN)))
				.add(this.filter(builder, "ThresholdFilter", Level.INFO))
				.addComponent(this.policies(builder)));
		}
		RootLoggerComponentBuilder rootBuilder = builder.newRootLogger(Level.ALL);
		for (String logger : this.defaultLoggers) {
			if (logger.contains("Console") && !this.allowedTypes.contains(LoggingType.CONSOLE)) {
				continue;
			}
			if (logger.contains("LogFile") && !this.allowedTypes.contains(LoggingType.FILE)) {
				continue;
			}
			rootBuilder.add(builder.newAppenderRef(logger));
		}
		return builder.add(rootBuilder).build();
	}
	
	//region Helper methods
	private LayoutComponentBuilder pattern(ConfigurationBuilder<BuiltConfiguration> builder, String pattern) {
		return builder.newLayout("PatternLayout").addAttribute("pattern", pattern);
	}
	
	private FilterComponentBuilder filter(ConfigurationBuilder<BuiltConfiguration> builder, String filter, Level level) {
		return builder.newFilter(filter, Filter.Result.ACCEPT, Filter.Result.DENY).addAttribute("level", level);
	}
	
	private ComponentBuilder<?> policies(ConfigurationBuilder<BuiltConfiguration> builder) {
		return builder.newComponent("Policies").addComponent(builder.newComponent("SizeBasedTriggeringPolicy")).addComponent(builder.newComponent("OnStartupTriggeringPolicy"));
	}
	//endregion
}
