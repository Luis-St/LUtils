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

package net.luis.utils.logging.filter;

/**
 *
 * @author Luis-St
 *
 */

public enum LogFilterResult {
	
	/**
	 * Indicates that the log event should be passed to the appenders.<br>
	 * No further filters will be evaluated.
	 */
	ACCEPT,
	/**
	 * Indicates that the log event should be rejected and not passed to the appenders.<br>
	 * No further filters will be evaluated.
	 */
	REJECT,
	/**
	 * Indicates that the log event should be passed to the next filter in the chain.<br>
	 * If there are no more filters, the log event will be passed to the appenders.<br>
	 * This is the default behavior.
	 */
	NEXT_OR_ACCEPT,
	/**
	 * Indicates that the log event should be passed to the next filter in the chain.<br>
	 * If there are no more filters, the log event will be rejected and not passed to the appenders.
	 */
	NEXT_OR_REJECT
}
