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

package net.luis.utils.logging.event;

import net.luis.utils.logging.*;
import net.luis.utils.logging.context.LogContext;
import net.luis.utils.logging.marker.LogMarker;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record LogEventRecord(
	@NonNull LogLevel level,
	@NotNull LogMarker marker,
	@NonNull LogMessage message,
	@NonNull LogContext context,
	@NonNull Instant timestamp,
	@NonNull StackTraceElement source,
	@NonNull ThreadInfo threadInfo
) implements LogEvent {
	
	public LogEventRecord {
		Objects.requireNonNull(level, "Level must not be null");
		Objects.requireNonNull(marker, "Marker must not be null");
		Objects.requireNonNull(message, "Message must not be null");
		Objects.requireNonNull(context, "Context must not be null");
		Objects.requireNonNull(timestamp, "Timestamp must not be null");
		Objects.requireNonNull(source, "Source must not be null");
		Objects.requireNonNull(threadInfo, "Thread info must not be null");
	}
}
