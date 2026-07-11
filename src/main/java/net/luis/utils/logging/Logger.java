/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

import net.luis.utils.logging.marker.LogMarker;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 *
 * @author Luis-St
 *
 */

public interface Logger {
	
	// --- child logger ---
	
	@NonNull Logger childLogger(@NonNull String name);
	
	@NonNull Logger childLogger(@NonNull String name, @NonNull LogMarker marker);
	
	@NonNull Logger childLogger(@NonNull String name, @NonNull LogContext context);
	
	@NonNull Logger childLogger(@NonNull String name, @NonNull LogMarker marker, @NonNull LogContext context);
	
	// --- buffered ---
	
	@NonNull Logger buffered(@NonNull LogScope scope);
	
	void flushBuffer();
	
	void clearBuffer();
	
	// --- enabled ---
	
	boolean isTraceEnabled();
	
	boolean isTraceEnabled(@NonNull LogMarker marker);
	
	boolean isDebugEnabled();
	
	boolean isDebugEnabled(@NonNull LogMarker marker);
	
	boolean isInfoEnabled();
	
	boolean isInfoEnabled(@NonNull LogMarker marker);
	
	boolean isNoticeEnabled();
	
	boolean isNoticeEnabled(@NonNull LogMarker marker);
	
	boolean isWarnEnabled();
	
	boolean isWarnEnabled(@NonNull LogMarker marker);
	
	boolean isErrorEnabled();
	
	boolean isErrorEnabled(@NonNull LogMarker marker);
	
	boolean isCriticalEnabled();
	
	boolean isCriticalEnabled(@NonNull LogMarker marker);
	
	boolean isFatalEnabled();
	
	boolean isFatalEnabled(@NonNull LogMarker marker);
	
	boolean isEnabled(@NonNull LogLevel level);
	
	boolean isEnabled(@NonNull LogLevel level, @NonNull LogMarker marker);
	
	// --- trace ---
	
	void trace(@NonNull String message, @Nullable Object p1);
	
	void trace(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void trace(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void trace(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void trace(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void trace(@NonNull String message, Object @NonNull ... parameters);
	
	void trace(@NonNull LogMessage message);
	
	void trace(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void trace(@NonNull LogMessage message, @NonNull LogContext context);
	
	void trace(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void trace(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void trace(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void trace(@NonNull LogEvent event);
	
	// -- debug ---
	
	void debug(@NonNull String message, @Nullable Object p1);
	
	void debug(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void debug(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void debug(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void debug(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void debug(@NonNull String message, Object @NonNull ... parameters);
	
	void debug(@NonNull LogMessage message);
	
	void debug(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void debug(@NonNull LogMessage message, @NonNull LogContext context);
	
	void debug(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void debug(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void debug(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void debug(@NonNull LogEvent event);
	
	// --- info ---
	
	void info(@NonNull String message, @Nullable Object p1);
	
	void info(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void info(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void info(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void info(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void info(@NonNull String message, Object @NonNull ... parameters);
	
	void info(@NonNull LogMessage message);
	
	void info(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void info(@NonNull LogMessage message, @NonNull LogContext context);
	
	void info(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void info(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void info(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void info(@NonNull LogEvent event);
	
	// --- notice ---
	
	void notice(@NonNull String message, @Nullable Object p1);
	
	void notice(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void notice(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void notice(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void notice(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void notice(@NonNull String message, Object @NonNull ... parameters);
	
	void notice(@NonNull LogMessage message);
	
	void notice(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void notice(@NonNull LogMessage message, @NonNull LogContext context);
	
	void notice(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void notice(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void notice(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void notice(@NonNull LogEvent event);
	
	// --- warn ---
	
	void warn(@NonNull String message, @Nullable Object p1);
	
	void warn(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void warn(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void warn(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void warn(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void warn(@NonNull String message, Object @NonNull ... parameters);
	
	void warn(@NonNull LogMessage message);
	
	void warn(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void warn(@NonNull LogMessage message, @NonNull LogContext context);
	
	void warn(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void warn(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void warn(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void warn(@NonNull LogEvent event);
	
	// --- error ---
	
	void error(@NonNull String message, @Nullable Object p1);
	
	void error(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void error(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void error(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void error(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void error(@NonNull String message, Object @NonNull ... parameters);
	
	void error(@NonNull LogMessage message);
	
	void error(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void error(@NonNull LogMessage message, @NonNull LogContext context);
	
	void error(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void error(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void error(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void error(@NonNull LogEvent event);
	
	// --- critical ---
	
	void critical(@NonNull String message, @Nullable Object p1);
	
	void critical(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void critical(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void critical(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void critical(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void critical(@NonNull String message, Object @NonNull ... parameters);
	
	void critical(@NonNull LogMessage message);
	
	void critical(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void critical(@NonNull LogMessage message, @NonNull LogContext context);
	
	void critical(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void critical(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void critical(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void critical(@NonNull LogEvent event);
	
	// --- fatal ---
	
	void fatal(@NonNull String message, @Nullable Object p1);
	
	void fatal(@NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void fatal(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void fatal(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void fatal(@NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void fatal(@NonNull String message, Object @NonNull ... parameters);
	
	void fatal(@NonNull LogMessage message);
	
	void fatal(@NonNull LogMarker marker, @NonNull LogMessage message);
	
	void fatal(@NonNull LogMessage message, @NonNull LogContext context);
	
	void fatal(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void fatal(@NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void fatal(@NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void fatal(@NonNull LogEvent event);
	
	// --- log ---
	
	void log(@NonNull LogLevel level, @NonNull String message, @Nullable Object p1);
	
	void log(@NonNull LogLevel level, @NonNull String message, @Nullable Object p1, @Nullable Object p2);
	
	void log(@NonNull LogLevel level, @NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3);
	
	void log(@NonNull LogLevel level, @NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4);
	
	void log(@NonNull LogLevel level, @NonNull String message, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4, @Nullable Object p5);
	
	void log(@NonNull LogLevel level, @NonNull String message, Object @NonNull ... parameters);
	
	void log(@NonNull LogLevel level, @NonNull LogMessage message);
	
	void log(@NonNull LogLevel level, @NonNull LogMarker marker, @NonNull LogMessage message);
	
	void log(@NonNull LogLevel level, @NonNull LogMessage message, @NonNull LogContext context);
	
	void log(@NonNull LogLevel level, @NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context);
	
	void log(@NonNull LogLevel level, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void log(@NonNull LogLevel level, @NonNull LogMarker marker, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void log(@NonNull LogEvent event);
}
