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

package net.luis.utils.io.reader;

/**
 * A record to define a quote with an opening and a closing character.<br>
 * In contrast to a {@link StringScope}, the opening and closing character of a quote can be the same.<br>
 *
 * @author Luis-St
 *
 * @param open The opening character of the quote
 * @param close The closing character of the quote
 */
public record StringQuote(char open, char close) {
	
	/**
	 * Constant quote for single quotes.<br>
	 */
	public static final StringQuote SINGLE_QUOTE = new StringQuote('\'', '\'');
	/**
	 * Constant quote for double quotes.<br>
	 */
	public static final StringQuote DOUBLE_QUOTE = new StringQuote('"', '"');
	
	/**
	 * Constructs a new quote with the given opening and closing character.<br>
	 *
	 * @param open The opening character
	 * @param close The closing character
	 * @throws IllegalArgumentException If the opening or closing character is the null character
	 */
	public StringQuote {
		if (open == '\0' || close == '\0') {
			throw new IllegalArgumentException("Opening and closing character must not be the null character");
		}
	}
}
