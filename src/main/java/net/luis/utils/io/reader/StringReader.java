/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A utility class to read strings.<br>
 *
 * @author Luis-St
 */
public class StringReader {
	
	/**
	 * The string to read from.<br>
	 */
	private final String string;
	/**
	 * The current index of the reader.<br>
	 */
	private int index;
	/**
	 * The marked index of the reader.<br>
	 */
	private int markedIndex = -1;
	
	/**
	 * Constructs a new string reader with the given string.<br>
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public StringReader(@NotNull String string) {
		this.string = Objects.requireNonNull(string, "String must not be null");
	}
	
	/**
	 * @return The string to read from
	 */
	public @NotNull String getString() {
		return this.string;
	}
	
	/**
	 * @return The current index of the reader
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Reads the next character from the string.<br>
	 * Increments the index by one.<br>
	 * @return The next character
	 * @throws IndexOutOfBoundsException If there are no more characters to read ({@link #canRead()} returns false)
	 */
	public char read() {
		return this.string.charAt(this.index++);
	}
	
	/**
	 * Reads the given number of characters from the string.<br>
	 * @param amount The number of characters to read
	 * @return The read characters as a string
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 * @throws IndexOutOfBoundsException If there are not enough characters to read ({@link #canRead(int)} returns false)
	 */
	public @NotNull String read(int amount) {
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			builder.append(this.read());
		}
		return builder.toString();
	}
	
	/**
	 * Checks whether there is at least the given number of characters to read.<br>
	 * @param amount The number of characters to read
	 * @return True if there is at least a given number of characters to read, otherwise false
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 */
	public boolean canRead(int amount) {
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		return this.index + amount <= this.string.length();
	}
	
	/**
	 * Checks whether there are more characters to read.<br>
	 * @return True if there are more characters to read, otherwise false
	 */
	public boolean canRead() {
		return this.canRead(1);
	}
	
	/**
	 * Checks whether the number of characters to read matches the given predicate.<br>
	 * @param amount The number of characters to read
	 * @param predicate The predicate to match the characters
	 * @return True if the number of characters to read matches the predicate, otherwise false
	 */
	public boolean canRead(int amount, @NotNull Predicate<Character> predicate) {
		Objects.requireNonNull(predicate, "Predicate must not be null");
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		for (int i = 0; i < amount; i++) {
			if (!this.canRead() || !predicate.test(this.string.charAt(this.index + i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Peeks the current character without incrementing the index.<br>
	 * @return The current character
	 */
	public char peek() {
		return this.string.charAt(this.index);
	}
	
	/**
	 * Skips the next character.<br>
	 * Increments the index by one.<br>
	 */
	public void skip() {
		this.index++;
	}
	
	/**
	 * Skips the given number of characters.<br>
	 * @param amount The number of characters to skip
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 */
	public void skip(int amount) {
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		this.index += amount;
	}
	
	/**
	 * Skips all characters that are equal to the given character.<br>
	 * <p>
	 *     If the next character is not equal to the given character, nothing happens.<br>
	 *     If there are no more characters to read, nothing happens.<br>
	 * </p>
	 * @param c The character to skip
	 */
	public void skip(char c) {
		while (this.canRead() && this.peek() == c) {
			this.skip();
		}
	}
	
	/**
	 * Skips all characters that match the given predicate.<br>
	 * <p>
	 *     If the next character does not match the predicate, nothing happens.<br>
	 *     If there are no more characters to read, nothing happens.<br>
	 * </p>
	 * @param predicate The predicate to match
	 * @throws NullPointerException If the predicate is null
	 */
	public void skip(@NotNull Predicate<Character> predicate) {
		Objects.requireNonNull(predicate, "Predicate must not be null");
		while (this.canRead() && predicate.test(this.peek())) {
			this.skip();
		}
	}
	
	/**
	 * Skips all whitespaces.<br>
	 * A whitespace is a character that returns true when calling {@link Character#isWhitespace(char)}.<br>
	 */
	public void skipWhitespaces() {
		this.skip(Character::isWhitespace);
	}
	
	/**
	 * Marks the current index of the reader.<br>
	 * The marked index can be reset to by calling {@link #reset()}.<br>
	 * After calling {@link #reset()}, the marked index will be reset.<br>
	 */
	public void mark() {
		this.markedIndex = this.index;
	}
	
	/**
	 * Resets the index of the reader to zero or if set to the marked index.<br>
	 */
	public void reset() {
		if (this.markedIndex == -1) {
			this.index = 0;
		} else {
			this.index = this.markedIndex;
			this.markedIndex = -1;
		}
	}
	
	/**
	 * Reads the remaining string.<br>
	 * If there are no more characters to read, an empty string is returned.<br>
	 * @return The remaining string
	 */
	public @NotNull String readRemaining() {
		if (!this.canRead()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		while (this.canRead()) {
			builder.append(this.read());
		}
		return builder.toString();
	}
	
	/**
	 * Reads the string until the next whitespace (' ') is found.<br>
	 * The whitespace is read but not included in the result.<br>
	 * If there are no more characters to read, an empty string is returned.<br>
	 * @return The string read until the next whitespace
	 */
	public @NotNull String readUnquotedString() {
		return this.readUnquotedString(' ');
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * <p>
	 *     The string will be read in 'raw' mode, meaning that no escaping is done.<br>
	 *     The terminator is read but not included in the result.<br>
	 * </p>
	 * <p>
	 *     If there are no more characters to read, an empty string is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator
	 */
	public @NotNull String readUnquotedString(char terminator) {
		if (!this.canRead()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		while (this.canRead()) {
			char c = this.read();
			if (c == terminator) {
				break;
			}
			builder.append(c);
		}
		return builder.toString();
	}
	
	/**
	 * Reads a quoted string.<br>
	 * <p>
	 *     A quoted string is a string enclosed in single or double quotes.<br>
	 *     The quotes are read but not included in the result.<br>
	 * </p>
	 * <p>
	 *     If there are no more characters to read, an empty string is returned.
	 * </p>
	 * @return The quoted string which was read
	 * @throws IllegalArgumentException If the next read character is not a single or double quote
	 */
	public @NotNull String readQuotedString() {
		if (!this.canRead()) {
			return "";
		}
		char next = this.peek();
		if (next != '"' && next != '\'') {
			throw new IllegalArgumentException("Expected a single or double quote as next character, but found: '" + next + "'");
		}
		this.skip();
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		while (this.canRead()) {
			char c = this.read();
			if (escaped) {
				builder.append(c);
				escaped = false;
			} else if (c == '\\') {
				builder.append(c);
				escaped = true;
			} else if (c == next) {
				break;
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 *     The terminator and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part of the string, the terminator is ignored.<br>
	 *     If the terminator is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator
	 * @throws IllegalArgumentException If the terminator is a backslash
	 */
	public @NotNull String readUntil(char terminator) {
		if (terminator == '\\') {
			throw new IllegalArgumentException("Terminator cannot be a backslash");
		}
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		while (this.canRead()) {
			char c = this.read();
			if (escaped) {
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
				continue;
			} else if (!inSingleQuotes && !inDoubleQuotes && c == terminator) {
				break;
			} else if (c == '\'') {
				inSingleQuotes = !inSingleQuotes;
			} else if (c == '\"') {
				inDoubleQuotes = !inDoubleQuotes;
			}
			builder.append(c);
		}
		return builder.toString();
	}
	
	/**
	 * Reads a string.<br>
	 * <p>
	 *     If the next character is a single or double quote, {@link #readQuotedString()} is called otherwise {@link #readUnquotedString()} is called.<br>
	 *     If there are no more characters to read, an empty string is returned.<br>
	 * </p>
	 * @return The string which was read
	 */
	public @NotNull String readString() {
		if (!this.canRead()) {
			return "";
		}
		char next = this.peek();
		if (next == '"' || next == '\'') {
			this.skip();
			return this.readUntil(next);
		}
		return this.readUnquotedString();
	}
	
	/**
	 * Reads a boolean.<br>
	 * <p>
	 *     The boolean is read as an unquoted string and then parsed.<br>
	 *     The value is parsed case-insensitive.<br>
	 * </p>
	 * @return The boolean value which was read
	 * @throws IllegalArgumentException If the read value is not a boolean
	 */
	public boolean readBoolean() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a boolean value but found nothing");
		}
		int start = this.index;
		char c = Character.toLowerCase(this.peek());
		String value = null;
		if (c == 't') {
			value = this.read(4);
		} else if (c == 'f') {
			value = this.read(5);
		} else {
			throw new IllegalArgumentException("Expected a start character of 'true' or 'false' but found: '" + c + "'");
		}
		if ("true".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value)) {
			return false;
		}
		this.index = start;
		throw new IllegalArgumentException("Expected a boolean value but found: " + value);
	}
	
	/**
	 * Reads a number as a string.<br>
	 * <p>
	 *     The number can be a byte, short, integer, long, float or double.<br>
	 *     The first character can be a minus or plus sign.<br>
	 *     The number can contain a dot for float or double values.<br>
	 *     The number can contain a type suffix for byte, short, integer, long, float or double values.<br>
	 * </p>
	 * @return The number as a string which was read
	 * @throws IllegalArgumentException If the read value is not a number
	 */
	private @NotNull String readNumber() {
		StringBuilder builder = new StringBuilder();
		if (this.peek() == '-' || this.peek() == '+') {
			builder.append(this.read());
		}
		boolean hasDot = false;
		while (this.canRead()) {
			char c = this.peek();
			if (!Character.isDigit(c) && c != '.') {
				break;
			}
			if (c == '.') {
				if (hasDot) {
					throw new IllegalArgumentException("Expected a number but found: '" + builder.toString() + c + "'");
				}
				hasDot = true;
			}
			builder.append(this.read());
		}
		if (this.canRead()) {
			char type = Character.toLowerCase(this.peek());
			if (type == 'b' || type == 's' || type == 'i' || type == 'l' || type == 'f' || type == 'd') {
				builder.append(Character.toLowerCase(this.read()));
			}
		}
		return builder.toString();
	}
	
	/**
	 * Reads a byte.<br>
	 * <p>
	 *     The byte is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid byte, the byte value is returned.<br>
	 * </p>
	 * @return The byte value which was read
	 * @throws IllegalArgumentException If the read value is not a byte
	 */
	public byte readByte() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a byte value but found nothing");
		}
		String value = this.readNumber();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Expected a byte value but found nothing");
		}
		char last = Character.toLowerCase(value.charAt(value.length() - 1));
		if (!Character.isDigit(last) && last != 'b') {
			throw new IllegalArgumentException("Expected a byte value but found: '" + value + "'");
		}
		try {
			return Byte.parseByte(Character.isDigit(last) ? value : value.substring(0, value.length() - 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a byte value but found: '" + value + "'", e);
		}
	}
	
	/**
	 * Reads a short.<br>
	 * <p>
	 *     The short is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid short, the short value is returned.<br>
	 * </p>
	 * @return The short value which was read
	 * @throws IllegalArgumentException If the read value is not a short
	 */
	public short readShort() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a short value but found nothing");
		}
		String value = this.readNumber();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Expected a short value but found nothing");
		}
		char last = Character.toLowerCase(value.charAt(value.length() - 1));
		if (!Character.isDigit(last) && last != 's') {
			throw new IllegalArgumentException("Expected a short value but found: '" + value + "'");
		}
		try {
			return Short.parseShort(Character.isDigit(last) ? value : value.substring(0, value.length() - 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a short value but found: '" + value + "'", e);
		}
	}
	
	/**
	 * Reads an integer.<br>
	 * <p>
	 *     The integer is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid integer, the integer value is returned.<br>
	 * </p>
	 * @return The integer value which was read
	 * @throws IllegalArgumentException If the read value is not an integer
	 */
	public int readInt() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected an integer value but found nothing");
		}
		String value = this.readNumber();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Expected a integer value but found nothing");
		}
		char last = Character.toLowerCase(value.charAt(value.length() - 1));
		if (!Character.isDigit(last) && last != 'i') {
			throw new IllegalArgumentException("Expected a integer value but found: '" + value + "'");
		}
		try {
			return Integer.parseInt(Character.isDigit(last) ? value : value.substring(0, value.length() - 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected an integer value but found: '" + value + "'", e);
		}
	}
	
	/**
	 * Reads a long.<br>
	 * <p>
	 *     The long is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid long, the long value is returned.<br>
	 * </p>
	 * @return The long value which was read
	 * @throws IllegalArgumentException If the read value is not a long
	 */
	public long readLong() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a long value but found nothing");
		}
		String value = this.readNumber();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Expected a long value but found nothing");
		}
		char last = Character.toLowerCase(value.charAt(value.length() - 1));
		if (!Character.isDigit(last) && last != 'l') {
			throw new IllegalArgumentException("Expected a long value but found: '" + value + "'");
		}
		try {
			return Long.parseLong(Character.isDigit(last) ? value : value.substring(0, value.length() - 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a long value but found: '" + value + "'", e);
		}
	}
	
	/**
	 * Reads a float.<br>
	 * <p>
	 *     The float is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid float, the float value is returned.<br>
	 * </p>
	 * @return The float value which was read
	 * @throws IllegalArgumentException If the read value is not a float
	 */
	public float readFloat() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a float value but found nothing");
		}
		String value = this.readNumber();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Expected a float value but found nothing");
		}
		char last = Character.toLowerCase(value.charAt(value.length() - 1));
		if (!Character.isDigit(last) && last != 'f') {
			throw new IllegalArgumentException("Expected a float value but found: '" + value + "'");
		}
		try {
			return Float.parseFloat(Character.isDigit(last) ? value : value.substring(0, value.length() - 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a float value but found: '" + value + "'", e);
		}
	}
	
	/**
	 * Reads a double.<br>
	 * <p>
	 *     The double is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid double, the double value is returned.<br>
	 * </p>
	 * @return The double value which was read
	 * @throws IllegalArgumentException If the read value is not a double
	 */
	public double readDouble() {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a double value but found nothing");
		}
		String value = this.readNumber();
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Expected a double value but found nothing");
		}
		char last = Character.toLowerCase(value.charAt(value.length() - 1));
		if (!Character.isDigit(last) && last != 'd') {
			throw new IllegalArgumentException("Expected a double value but found: '" + value + "'");
		}
		try {
			return Double.parseDouble(Character.isDigit(last) ? value : value.substring(0, value.length() - 1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a double value but found: '" + value + "'", e);
		}
	}
}
