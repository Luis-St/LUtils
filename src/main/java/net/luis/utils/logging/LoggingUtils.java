package net.luis.utils.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class LoggingUtils {
	
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
	//endregion
	
	//region Console enable methods
	public static void enableConsoleTrace() {
		enableAppender("ConsoleTrace");
	}
	
	public static void enableConsoleDebug() {
		enableAppender("ConsoleDebug");
	}
	
	public static void enableConsoleInfo() {
		enableAppender("ConsoleInfo");
	}
	
	public static void enableConsoleWarn() {
		enableAppender("ConsoleWarn");
	}
	
	public static void enableConsoleError() {
		enableAppender("ConsoleError");
	}
	
	public static void enableConsoleFatal() {
		enableAppender("ConsoleFatal");
	}
	
	public static void enableConsole(@NotNull Level level) {
		switch (level.intLevel()) {
			case 0 -> disableConsoleAll();
			case 600 -> enableConsoleTrace();
			case 500 -> enableConsoleDebug();
			case 400 -> enableConsoleInfo();
			case 300 -> enableConsoleWarn();
			case 200 -> enableConsoleError();
			case 100 -> enableConsoleFatal();
			case Integer.MAX_VALUE -> enableConsoleAll();
			default -> {
			}
		}
	}
	
	public static void enableConsoleAll() {
		enableConsoleTrace();
		enableConsoleDebug();
		enableConsoleInfo();
		enableConsoleWarn();
		enableConsoleError();
		enableConsoleFatal();
	}
	
	public static void enableConsoleAll(Level... levels) {
		Arrays.asList(levels).forEach(LoggingUtils::enableConsole);
	}
	//endregion
	
	//region Console disable methods
	public static void disableConsoleTrace() {
		disableAppender("ConsoleTrace");
	}
	
	public static void disableConsoleDebug() {
		disableAppender("ConsoleDebug");
	}
	
	public static void disableConsoleInfo() {
		disableAppender("ConsoleInfo");
	}
	
	public static void disableConsoleWarn() {
		disableAppender("ConsoleWarn");
	}
	
	public static void disableConsoleError() {
		disableAppender("ConsoleError");
	}
	
	public static void disableConsoleFatal() {
		disableAppender("ConsoleFatal");
	}
	
	public static void disableConsole(@NotNull Level level) {
		switch (level.intLevel()) {
			case 0 -> enableConsoleAll();
			case 600 -> disableConsoleTrace();
			case 500 -> disableConsoleDebug();
			case 400 -> disableConsoleInfo();
			case 300 -> disableConsoleWarn();
			case 200 -> disableConsoleError();
			case 100 -> disableConsoleFatal();
			case Integer.MAX_VALUE -> disableConsoleAll();
			default -> {
			}
		}
	}
	
	public static void disableConsoleAll() {
		disableConsoleTrace();
		disableConsoleDebug();
		disableConsoleInfo();
		disableConsoleWarn();
		disableConsoleError();
		disableConsoleFatal();
	}
	
	public static void disableConsoleAll(Level... levels) {
		Arrays.asList(levels).forEach(LoggingUtils::disableConsole);
	}
	//endregion
	
	//region File enable methods
	public static void enableDebugFile() {
		enableAppender("DebugLogFile");
	}
	
	public static void enableInfoFile() {
		enableAppender("InfoLogFile");
	}
	
	public static void enableFiles() {
		enableDebugFile();
		enableInfoFile();
	}
	//endregion
	
	//region File disable methods
	private static void disableDebugFile() {
		disableAppender("DebugLogFile");
	}
	
	private static void disableInfoFile() {
		disableAppender("InfoLogFile");
	}
	
	public static void disableFiles() {
		disableDebugFile();
		disableInfoFile();
	}
	//endregion
}
