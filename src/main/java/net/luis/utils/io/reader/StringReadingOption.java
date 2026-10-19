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
 * Options to modify how a {@link StringReader} interprets special characters while reading.<br>
 * <p>
 *     By default, a string reader treats the backslash ('\\') as escape character<br>
 *     and single (') and double (") quotes as delimiters of quoted parts.<br>
 *     Each option disables one of these behaviors, all other behaviors are kept.
 * </p>
 * <p>
 *     The options are passed to the reader on construction:
 * </p>
 * <pre>{@code
 * StringReader reader = new StringReader("it's a \"test\"", StringReadingOption.IGNORE_SINGLE_QUOTES);
 * }</pre>
 *
 * @author Luis-St
 *
 * @see StringReader
 * @see ScopedStringReader
 */
public enum StringReadingOption {
	
	/**
	 * Disables the handling of escape characters.<br>
	 * <p>
	 *     The backslash ('\\') is treated as a regular character,<br>
	 *     it is included in the result and does not escape the following character.<br>
	 *     In addition, the backslash is allowed as terminator.
	 * </p>
	 */
	IGNORE_ESCAPES,
	/**
	 * Disables the handling of single quotes (').<br>
	 * <p>
	 *     Single quotes are treated as regular characters,<br>
	 *     they do not start or end a quoted part and are not accepted as quote of a quoted string.
	 * </p>
	 */
	IGNORE_SINGLE_QUOTES,
	/**
	 * Disables the handling of double quotes (").<br>
	 * <p>
	 *     Double quotes are treated as regular characters,<br>
	 *     they do not start or end a quoted part and are not accepted as quote of a quoted string.
	 * </p>
	 */
	IGNORE_DOUBLE_QUOTES
}