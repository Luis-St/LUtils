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
import net.luis.utils.logging.marker.LogMarker;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

// try (LogScope scope = logger.createScope*) {} // Binds the buffered logger to this scope
public interface LogScope extends AutoCloseable {
	
	int capacity();
	
	int size();
	
	@NonNull LogMarker marker();
	
	@NonNull LogContext context();
	
	void flush();
	
	void clear();
	
	@Override
	void close();
}
