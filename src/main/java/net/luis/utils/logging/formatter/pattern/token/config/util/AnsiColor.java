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

package net.luis.utils.logging.formatter.pattern.token.config.util;

/**
 *
 * @author Luis-St
 *
 */

public enum AnsiColor {
	BLACK(30),
	RED(31),
	GREEN(32),
	YELLOW(33),
	BLUE(34),
	MAGENTA(35),
	CYAN(36),
	WHITE(37),
	BRIGHT_BLACK(90),
	BRIGHT_RED(91),
	BRIGHT_GREEN(92),
	BRIGHT_YELLOW(93),
	BRIGHT_BLUE(94),
	BRIGHT_MAGENTA(95),
	BRIGHT_CYAN(96),
	BRIGHT_WHITE(97);
	
	private final int code;
	
	private AnsiColor(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
}
