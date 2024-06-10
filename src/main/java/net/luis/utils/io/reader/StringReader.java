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
	 * Resets the index of the reader to zero.<br>
	 */
	public void reset() {
		this.index = 0;
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
	 *     If the value is 'true' (case-insensitive), true is returned.<br>
	 *     If the value is 'false' (case-insensitive), false is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The boolean value which was read
	 * @throws IllegalArgumentException If the read value is not a boolean
	 */
	public boolean readBoolean(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a boolean value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		if ("true".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value)) {
			return false;
		}
		throw new IllegalArgumentException("Expected a boolean value but found: " + value);
	}
	
	/**
	 * Reads a byte.<br>
	 * <p>
	 *     The byte is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid byte, the byte value is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The byte value which was read
	 * @throws IllegalArgumentException If the read value is not a byte
	 */
	public byte readByte(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a byte value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		try {
			return Byte.parseByte(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a byte value but found: " + value, e);
		}
	}
	
	/**
	 * Reads a short.<br>
	 * <p>
	 *     The short is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid short, the short value is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The short value which was read
	 * @throws IllegalArgumentException If the read value is not a short
	 */
	public short readShort(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a short value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		try {
			return Short.parseShort(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a short value but found: " + value, e);
		}
	}
	
	/**
	 * Reads an integer.<br>
	 * <p>
	 *     The integer is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid integer, the integer value is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The integer value which was read
	 * @throws IllegalArgumentException If the read value is not an integer
	 */
	public int readInt(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected an integer value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected an integer value but found: " + value, e);
		}
	}
	
	/**
	 * Reads a long.<br>
	 * <p>
	 *     The long is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid long, the long value is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The long value which was read
	 * @throws IllegalArgumentException If the read value is not a long
	 */
	public long readLong(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a long value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a long value but found: " + value, e);
		}
	}
	
	/**
	 * Reads a float.<br>
	 * <p>
	 *     The float is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid float, the float value is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The float value which was read
	 * @throws IllegalArgumentException If the read value is not a float
	 */
	public float readFloat(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a float value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a float value but found: " + value, e);
		}
	}
	
	/**
	 * Reads a double.<br>
	 * <p>
	 *     The double is read as an unquoted string and then parsed.<br>
	 *     If the value is a valid double, the double value is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The double value which was read
	 */
	public double readDouble(char terminator) {
		if (!this.canRead()) {
			throw new IllegalArgumentException("Expected a double value but found nothing");
		}
		String value = this.readUnquotedString(terminator);
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Expected a double value but found: " + value, e);
		}
	}
}
