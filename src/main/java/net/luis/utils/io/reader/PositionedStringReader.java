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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Arrays;

/**
 * A string reader which knows the line and column of its current index.<br>
 * <p>
 *     The position is computed on demand from a table of line starts,<br>
 *     which is built once when a position is requested for the first time.<br>
 *     Therefore, tracking the position does not slow down reading.
 * </p>
 * <p>
 *     Supported line breaks are: LF ('\n'), CR ('\r'), CRLF ('\r\n').<br>
 *     Exceptions thrown while reading contain the line and column instead of the index.
 * </p>
 *
 * @author Luis-St
 */
public class PositionedStringReader extends StringReader {
	
	/**
	 * The indices at which the lines of the string start, or null if not computed yet.<br>
	 */
	private int @Nullable [] lineStarts;
	
	/**
	 * Constructs a new positioned string reader with the given string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character}.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public PositionedStringReader(@NonNull String string) {
		super(string);
	}
	
	/**
	 * Constructs a new positioned string reader with the given string and escape character.<br>
	 *
	 * @param string The string to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @throws NullPointerException If the string is null
	 */
	public PositionedStringReader(@NonNull String string, @Nullable Character escapeCharacter) {
		super(string, escapeCharacter);
	}
	
	/**
	 * Constructs a new positioned string reader from the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character}.<br>
	 *
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public PositionedStringReader(@NonNull Reader reader) {
		super(reader);
	}
	
	/**
	 * Constructs a new positioned string reader from the given reader with the given escape character.<br>
	 * The reader is closed after reading the string.<br>
	 *
	 * @param reader The reader to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public PositionedStringReader(@NonNull Reader reader, @Nullable Character escapeCharacter) {
		super(reader, escapeCharacter);
	}
	
	/**
	 * Computes the indices at which the lines of the given string start.<br>
	 *
	 * @param string The string
	 * @return The start indices of the lines
	 */
	private static int @NonNull [] computeLineStarts(@NonNull String string) {
		int[] starts = new int[16];
		int count = 1;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\r' && i + 1 < string.length() && string.charAt(i + 1) == '\n') {
				continue;
			}
			
			if (c == '\n' || c == '\r') {
				if (count == starts.length) {
					starts = Arrays.copyOf(starts, count * 2);
				}
				starts[count++] = i + 1;
			}
		}
		return Arrays.copyOf(starts, count);
	}
	
	/**
	 * Returns the position of the current index of the reader.<br>
	 *
	 * @return The current position
	 * @see #getPosition(int)
	 */
	public @NonNull StringPosition getPosition() {
		return this.getPosition(Math.min(this.getIndex(), this.getString().length()));
	}
	
	/**
	 * Returns the position of the given index in the string of this reader.<br>
	 * An index which points to a line break belongs to the line which is ended by the line break.<br>
	 *
	 * @param index The index
	 * @return The position of the index
	 * @throws IndexOutOfBoundsException If the index is negative or greater than the length of the string
	 */
	public @NonNull StringPosition getPosition(int index) {
		if (0 > index || index > this.getString().length()) {
			throw new IndexOutOfBoundsException("Index " + index + " is outside the string of length " + this.getString().length());
		}
		if (this.lineStarts == null) {
			this.lineStarts = computeLineStarts(this.getString());
		}
		
		int line = Arrays.binarySearch(this.lineStarts, index);
		if (0 > line) {
			line = -line - 2;
		}
		return new StringPosition(index, line + 1, index - this.lineStarts[line] + 1);
	}
	
	@Override
	protected @NonNull String describeLocation() {
		return this.getPosition().toString();
	}
}
