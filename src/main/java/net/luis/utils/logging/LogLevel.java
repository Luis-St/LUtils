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

import net.luis.utils.util.Priority;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class LogLevel {
	
	private static final String TRACE_NAME = "TRACE";
	private static final String DEBUG_NAME = "DEBUG";
	private static final String INFO_NAME = "INFO";
	private static final String NOTICE_NAME = "NOTICE";
	private static final String WARN_NAME = "WARN";
	private static final String ERROR_NAME = "ERROR";
	private static final String CRITICAL_NAME = "CRITICAL";
	private static final String FATAL_NAME = "FATAL";
	
	public static final LogLevel TRACE = new LogLevel(TRACE_NAME, new Priority(TRACE_NAME, Long.MIN_VALUE));
	public static final LogLevel DEBUG = new LogLevel(DEBUG_NAME, new Priority(DEBUG_NAME, Integer.MIN_VALUE));
	public static final LogLevel INFO = new LogLevel(INFO_NAME, new Priority(INFO_NAME, Integer.MIN_VALUE / 2));
	public static final LogLevel NOTICE = new LogLevel(NOTICE_NAME, new Priority(NOTICE_NAME, 0));
	public static final LogLevel WARN = new LogLevel(WARN_NAME, new Priority(WARN_NAME, Integer.MAX_VALUE / 2));
	public static final LogLevel ERROR = new LogLevel(ERROR_NAME, new Priority(ERROR_NAME, Integer.MAX_VALUE));
	public static final LogLevel CRITICAL = new LogLevel(CRITICAL_NAME, new Priority(CRITICAL_NAME, Long.MAX_VALUE / 2));
	public static final LogLevel FATAL = new LogLevel(FATAL_NAME, new Priority(FATAL_NAME, Long.MAX_VALUE));
	
	private final String name;
	private final Priority priority;
	
	public LogLevel(@NonNull String name, @NonNull Priority priority) {
		this.name = Objects.requireNonNull(name, "Log level name must not be null");
		this.priority = Objects.requireNonNull(priority, "Log level priority must not be null");
	}
	
	public @NonNull String getName() {
		return this.name;
	}
	
	public @NonNull Priority getPriority() {
		return this.priority;
	}
}
