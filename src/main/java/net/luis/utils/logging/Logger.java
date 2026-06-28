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

import net.luis.utils.logging.context.LogContext;
import org.jspecify.annotations.NonNull;

import java.time.Instant;

/**
 *
 * @author Luis-St
 *
 */

public interface Logger {
	
	void log(@NonNull LogLevel level, @NonNull LogMessage message);
	
	void log(@NonNull LogLevel level, @NonNull LogMessage message, @NonNull LogContext context);
	
	void log(@NonNull LogLevel level, @NonNull LogMessage message, @NonNull LogContext context, @NonNull Instant timestamp);
	
	void log(@NonNull LogEvent event);
	
	void trace();
	
	void debug();
	
	void info();
	
	void notice();
	
	void warn();
	
	void error();
	
	void critical();
	
	void fatal();
}
