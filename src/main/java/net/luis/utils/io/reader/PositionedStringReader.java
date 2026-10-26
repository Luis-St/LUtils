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
import java.util.function.Function;

/**
 * A string reader which knows the line and column of its current index.<br>
 * <p>
 *     The position is computed on the fly from the last computed position.<br>
 *     Only the characters between the last computed position and the requested index are scanned,<br>
 *     therefore each character is processed at most once while the reader moves forward.
 * </p>
 * <p>
 *     Sub readers which are positioned string readers continue from the position of this reader,<br>
 *     so the characters before the sub reader are not scanned again.
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
	 * The index of the last computed position.<br>
	 */
	private int trackedIndex;
	/**
	 * The one-based line of the last computed position.<br>
	 */
	private int trackedLine = 1;
	/**
	 * The index at which the line of the last computed position starts.<br>
	 */
	private int trackedLineStart;
	
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
	 * Checks whether the character at the given index of the given string ends a line.<br>
	 * A carriage return followed by a line feed does not end a line, the line feed does.<br>
	 *
	 * @param string The string
	 * @param index The index of the character
	 * @return True if the character ends a line, otherwise false
	 */
	private static boolean isLineBreak(@NonNull String string, int index) {
		char c = string.charAt(index);
		if (c == '\n') {
			return true;
		}
		return c == '\r' && (index + 1 >= string.length() || string.charAt(index + 1) != '\n');
	}
	
	@Override
	public <R extends StringReader> @NonNull R createSubReader(int length, @NonNull Function<String, R> creator) {
		R reader = super.createSubReader(length, creator);
		if (reader instanceof PositionedStringReader positioned) {
			positioned.trackedIndex = this.trackedIndex;
			positioned.trackedLine = this.trackedLine;
			positioned.trackedLineStart = this.trackedLineStart;
		}
		return reader;
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
	 * <p>
	 *     The position is computed from the last computed position,<br>
	 *     only the characters between the last computed position and the given index are scanned.
	 * </p>
	 *
	 * @param index The index
	 * @return The position of the index
	 * @throws IndexOutOfBoundsException If the index is negative or greater than the length of the string
	 */
	public @NonNull StringPosition getPosition(int index) {
		String string = this.getString();
		if (0 > index || index > string.length()) {
			throw new IndexOutOfBoundsException("Index " + index + " is outside the string of length " + string.length());
		}
		
		if (index >= this.trackedIndex) {
			for (int i = this.trackedIndex; i < index; i++) {
				if (isLineBreak(string, i)) {
					this.trackedLine++;
					this.trackedLineStart = i + 1;
				}
			}
		} else if (index < this.trackedLineStart) {
			for (int i = this.trackedLineStart - 1; i >= index; i--) {
				if (isLineBreak(string, i)) {
					this.trackedLine--;
				}
			}
			
			int lineStart = index;
			while (lineStart > 0 && !isLineBreak(string, lineStart - 1)) {
				lineStart--;
			}
			this.trackedLineStart = lineStart;
		}
		this.trackedIndex = index;
		return new StringPosition(index, this.trackedLine, index - this.trackedLineStart + 1);
	}
	
	@Override
	protected @NonNull String describeLocation() {
		return this.getPosition().toString();
	}
}
