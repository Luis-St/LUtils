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

package net.luis.utils.logging.appender;

import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public abstract class AbstractLogAppender implements LogAppender {
	
	private LogAppenderState state = LogAppenderState.ENABLED;
	
	@Override
	public void enable() {
		this.state = LogAppenderState.ENABLED;
	}
	
	@Override
	public void disable() {
		this.state = LogAppenderState.DISABLED;
	}
	
	@Override
	public @NonNull LogAppenderState getState() {
		return this.state;
	}
}
