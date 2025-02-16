/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.io.FileUtils;
import net.luis.utils.math.NumberType;
import net.luis.utils.math.Radix;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a reader to read from a string.<br>
 * The reader can read specific types of values from the string such as:<br>
 * <ul>
 *     <li>Quoted and unquoted strings</li>
 *     <li>Booleans</li>
 *     <li>Numbers (byte, short, integer, long, float, double)</li>
 *     <li>Big numbers (BigInteger, BigDecimal)</li>
 * </ul>
 * <p>
 *     This reader implementation is not performance optimized,<br>
 *     it reads the string character by character in {@code O(n)}.<br>
 * </p>
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
	 * Constructs a new string reader from the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public StringReader(@NotNull Reader reader) {
		Objects.requireNonNull(reader, "Reader must not be null");
		try {
			this.string = FileUtils.readString(reader);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read string from reader", e);
		}
	}
	
	/**
	 * Returns the string to read from.<br>
	 * @return The string
	 */
	public @NotNull String getString() {
		return this.string;
	}
	
	/**
	 * Returns the current character index of the reader.<br>
	 * @return The current index
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Reads the next character from the string.<br>
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
		if (!this.canRead(amount)) {
			throw new IndexOutOfBoundsException("Expected " + amount + " characters but found only " + (this.string.length() - this.index) + " remaining characters");
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
	 * If the next character is not equal to the given character<br>
	 * or there are no more characters to read, nothing happens.<br>
	 * @param c The character to skip
	 */
	public void skip(char c) {
		while (this.canRead() && this.peek() == c) {
			this.skip();
		}
	}
	
	/**
	 * Skips all characters that match the given predicate.<br>
	 * If the next character does not match the predicate<br>
	 * or there are no more characters to read, nothing happens.<br>
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
	 * If the marked index is set, it will be reset after calling this method.<br>
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
	 * Reads all characters until the next line break is found.<br>
	 * Supported line breaks are: LF ('\n'), CR ('\r'), CRLF ('\r\n').<br>
	 * @param includeLineBreak Whether the line break should be included in the result or not
	 * @return The line which was read
	 */
	public @NotNull String readLine(boolean includeLineBreak) {
		if (!this.canRead()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		while (this.canRead()) {
			char c = this.read();
			if (c != '\n' && c != '\r') {
				builder.append(c);
			} else {
				// Types of line breaks:
				//  - LF: Line Feed, '\n' (Unix, Linux, macOS)
				//  - CR: Carriage Return, '\r' (Old Mac OS)
				//  - CRLF: CR followed by LF, '\r\n' (Windows)
				if (c == '\n') {
					if (includeLineBreak) {
						builder.append(c);
					}
					break;
				} else {
					if (includeLineBreak) {
						builder.append(c);
					}
					if (this.canRead() && this.peek() == '\n') {
						if (includeLineBreak) {
							builder.append(this.read());
						} else {
							this.skip();
						}
					}
					break;
				}
			}
		}
		return builder.toString();
	}
	
	/**
	 * Reads the remaining string.<br>
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
	
	//region Read string
	
	/**
	 * Reads the string until the next whitespace (' ') is found.<br>
	 * The whitespace is read but not included in the result.<br>
	 * @return The string read until the default terminator
	 * @see #readUnquotedString(char)
	 */
	public @NotNull String readUnquotedString() {
		return this.readUnquotedString(' ');
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The string will be read in 'raw' mode, meaning that no escaping is done.<br>
	 * The terminator is read but not included in the result.<br>
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 */
	public @NotNull String readUnquotedString(char terminator) {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected an unquoted string but found nothing");
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
	 * A quoted string is a string enclosed in single or double quotes.<br>
	 * The quotes are read but not included in the result.<br>
	 * @return The quoted string which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the next read character is not a single or double quote
	 */
	public @NotNull String readQuotedString() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a quoted string but found nothing");
		}
		char next = this.peek();
		if (next != '"' && next != '\'') {
			throw new InvalidStringException("Expected a single or double quote as next character, but found: '" + next + "'");
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
	 * Reads a string.<br>
	 * <p>
	 *     If the next character is a single or double quote,<br>
	 *     {@link #readQuotedString()} is called otherwise {@link #readUnquotedString()} is called.<br>
	 *     If there are no more characters to read, an empty string is returned.<br>
	 * </p>
	 * @return The string which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @see #readQuotedString()
	 * @see #readUnquotedString()
	 */
	public @NotNull String readString() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a string value but found nothing");
		}
		char next = this.peek();
		if (next == '"' || next == '\'') {
			this.skip();
			return this.readUntil(next);
		}
		return this.readUnquotedString();
	}
	//endregion
	
	//region Read until
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The terminator and escape character ('\\') are read but not included in the result.<br>
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
			throw new IllegalArgumentException("Terminator must not be a backslash");
		}
		return this.readUntil(c -> c == terminator, false);
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part of the string, the terminator is ignored.<br>
	 *     If the terminator is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator
	 * @throws IllegalArgumentException If the terminator is a backslash
	 */
	public @NotNull String readUntilInclusive(char terminator) {
		if (terminator == '\\') {
			throw new IllegalArgumentException("Terminator must not be a backslash");
		}
		return this.readUntil(c -> c == terminator, true);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The terminators and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part of the string, the terminator is ignored.<br>
	 *     If none terminator is found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 */
	public @NotNull String readUntil(char @NotNull ... terminators) {
		Set<Character> uniqueTerminators = Sets.newHashSet(ArrayUtils.toObject(terminators));
		if (uniqueTerminators.isEmpty()) {
			throw new IllegalArgumentException("Terminators must not be empty");
		}
		if (uniqueTerminators.contains('\\')) {
			throw new IllegalArgumentException("Terminators must not contain a backslash");
		}
		return this.readUntil(uniqueTerminators::contains, false);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part of the string, the terminator is ignored.<br>
	 *     If none terminator is found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 */
	public @NotNull String readUntilInclusive(char... terminators) {
		Set<Character> uniqueTerminators = Sets.newHashSet(ArrayUtils.toObject(terminators));
		if (uniqueTerminators.isEmpty()) {
			throw new IllegalArgumentException("Terminators must not be empty");
		}
		if (uniqueTerminators.contains('\\')) {
			throw new IllegalArgumentException("Terminators must not contain a backslash");
		}
		return this.readUntil(uniqueTerminators::contains, true);
	}
	
	/**
	 * Internal method to read the string until the given predicate is true.<br>
	 * @param predicate The predicate to match the characters
	 * @param inclusive Whether the character which matches the predicate should be included in the result or not
	 * @return The string which was read until the predicate is true
	 * @see #readUntil(char)
	 * @see #readUntilInclusive(char)
	 * @see #readUntil(char...)
	 * @see #readUntilInclusive(char...)
	 */
	@ApiStatus.Internal
	protected @NotNull String readUntil(@NotNull Predicate<Character> predicate, boolean inclusive) {
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
			} else if (!inSingleQuotes && !inDoubleQuotes && predicate.test(c)) {
				if (inclusive) {
					builder.append(c);
				}
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
	 * Reads the string until the given terminator string is found.<br>
	 * The terminator string and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part of the string, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 */
	public @NotNull String readUntil(@NotNull String terminator, boolean caseSensitive) {
		Objects.requireNonNull(terminator, "Terminator string must not be null");
		if (terminator.isEmpty()) {
			throw new IllegalArgumentException("Terminator string must not be empty");
		}
		if (terminator.length() == 1) {
			return this.readUntil(terminator.charAt(0));
		}
		if (terminator.contains("\\")) {
			throw new IllegalArgumentException("Terminator string must not contain a backslash");
		}
		return this.readUntil(terminator, caseSensitive, false);
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part of the string, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 */
	public @NotNull String readUntilInclusive(@NotNull String terminator, boolean caseSensitive) {
		Objects.requireNonNull(terminator, "Terminator string must not be null");
		if (terminator.isEmpty()) {
			throw new IllegalArgumentException("Terminators string must not be empty");
		}
		if (terminator.length() == 1) {
			return this.readUntilInclusive(terminator.charAt(0));
		}
		if (terminator.contains("\\")) {
			throw new IllegalArgumentException("Terminator string must not contain a backslash");
		}
		return this.readUntil(terminator, caseSensitive, true);
	}
	
	/**
	 * Internal method to read the string until the terminating string is found.<br>
	 * @param terminator The terminator string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @param inclusive Whether the terminator string should be included in the result or not
	 * @return The string which was read until the equals predicate is true
	 * @throws NullPointerException If the terminator string is null
	 * @see #readUntil(String, boolean)
	 * @see #readUntilInclusive(String, boolean)
	 */
	@ApiStatus.Internal
	@SuppressWarnings("DuplicatedCode")
	protected @NotNull String readUntil(@NotNull String terminator, boolean caseSensitive, boolean inclusive) {
		Objects.requireNonNull(terminator, "Terminator string must not be null");
		Predicate<String> matcher = s -> caseSensitive ? terminator.startsWith(s) : StringUtils.startsWithIgnoreCase(s, terminator);
		Predicate<String> breaker = s -> caseSensitive ? terminator.equals(s) : s.equalsIgnoreCase(terminator);
		StringBuilder builder = new StringBuilder();
		StringBuilder terminatorBuilder = new StringBuilder();
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
			} else if (c == '\'') {
				inSingleQuotes = !inSingleQuotes;
			} else if (c == '\"') {
				inDoubleQuotes = !inDoubleQuotes;
			}
			if (!inSingleQuotes && !inDoubleQuotes) {
				if (terminatorBuilder.isEmpty()) {
					if (matcher.test(String.valueOf(c))) {
						terminatorBuilder.append(c);
						continue;
					}
				} else {
					terminatorBuilder.append(c);
					if (breaker.test(terminatorBuilder.toString())) {
						if (inclusive) {
							builder.append(terminatorBuilder);
						}
						break;
					}
					if (!matcher.test(terminatorBuilder.toString())) {
						builder.append(terminatorBuilder);
						terminatorBuilder.setLength(0);
					}
					continue;
				}
			}
			builder.append(c);
		}
		return builder.toString();
	}
	//endregion
	
	//region Read expected
	
	/**
	 * Reads the given expected string from the reader.<br>
	 * If any read character does not match the expected character, an exception is thrown.<br>
	 * <p>
	 *     If the expected string could not be read, an exception is thrown.<br>
	 *     The reader index is set to the last matching character of the expected string.<br>
	 * </p>
	 * @param expected The expected string
	 * @param caseSensitive Whether the expected string should be case-sensitive or not
	 * @return The expected string which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws IllegalArgumentException If the expected string is empty
	 * @throws InvalidStringException If the string read does not match the expected string
	 */
	public @NotNull String readExpected(@NotNull String expected, boolean caseSensitive) {
		Objects.requireNonNull(expected, "Expected string must not be null");
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected '" + expected + "' but found nothing");
		}
		if (expected.isEmpty()) {
			throw new IllegalArgumentException("Expected string must not be empty");
		}
		int length = expected.length();
		if (!this.canRead(length)) {
			throw new InvalidStringException("Expected '" + expected + "' requires " + length + " characters but found " + (this.string.length() - this.index) + " remaining characters");
		}
		int markedIndex = this.markedIndex;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			this.mark();
			char c = this.read();
			if (caseSensitive) {
				if (c != expected.charAt(i)) {
					this.reset();
					throw new InvalidStringException("Expected '" + expected + "' but found: '" + builder + c + "'");
				}
			} else {
				if (Character.toLowerCase(c) != Character.toLowerCase(expected.charAt(i))) {
					this.reset();
					throw new InvalidStringException("Expected '" + expected + "' but found: '" + builder + c + "'");
				}
			}
			builder.append(c);
		}
		this.markedIndex = markedIndex;
		return builder.toString();
	}
	
	/**
	 * Reads the first matching expected string from the reader.<br>
	 * <p>
	 *     If any read character does not match the expected character of a given string,<br>
	 *     the string is removed from the list.<br>
	 *     If no expected string matches the read string, an exception is thrown.<br>
	 * </p>
	 * <p>
	 *     If none of the expected strings could be read, an exception is thrown.<br>
	 *     The reader index is set to the last matching character of the expected strings.<br>
	 * </p>
	 * @param expected The list of expected strings
	 * @param caseSensitive Whether the expected strings should be case-sensitive or not
	 * @return The expected string which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws IllegalArgumentException If the list of expected strings is empty
	 * @throws InvalidStringException If the string read does not match any of the expected strings
	 */
	public @NotNull String readExpected(@NotNull List<String> expected, boolean caseSensitive) {
		Objects.requireNonNull(expected, "Expected strings must not be null");
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected one of '" + expected + "' but found nothing");
		}
		if (expected.isEmpty()) {
			throw new IllegalArgumentException("Expected strings must not be empty");
		}
		int readable = this.string.length() - this.index;
		List<String> possibleExpected = expected.stream().filter(e -> e.length() <= readable).collect(Collectors.toList());
		if (possibleExpected.isEmpty()) {
			throw new InvalidStringException("Expected one of '" + expected + "' but there are not enough remaining characters " + readable + " for any expected string");
		}
		
		int start = this.index;
		int markedIndex = this.markedIndex;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < readable; i++) {
			this.mark();
			char c = this.read();
			List<String> newPossibleExpected = Lists.newArrayList();
			Iterator<String> iterator = possibleExpected.iterator();
			while (iterator.hasNext()) {
				String expectedString = iterator.next();
				if (i >= expectedString.length()) {
					iterator.remove();
					continue;
				}
				if (caseSensitive) {
					if (c != expectedString.charAt(i)) {
						iterator.remove();
						continue;
					}
				} else {
					if (Character.toLowerCase(c) != Character.toLowerCase(expectedString.charAt(i))) {
						iterator.remove();
						continue;
					}
				}
				builder.append(c);
				if (caseSensitive ? expectedString.contentEquals(builder) : expectedString.equalsIgnoreCase(builder.toString())) {
					return builder.toString();
				}
				newPossibleExpected.add(expectedString);
			}
			if (newPossibleExpected.isEmpty()) {
				this.reset();
				break;
			}
			possibleExpected = newPossibleExpected;
		}
		this.markedIndex = markedIndex;
		throw new InvalidStringException("Expected one of '" + expected + "' but found: '" + this.string.substring(start, this.index) + "'");
	}
	//endregion
	
	/**
	 * Reads a boolean.<br>
	 * The boolean is read as an unquoted string and then parsed case-insensitive.<br>
	 * @return The boolean value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a boolean
	 */
	public boolean readBoolean() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a boolean value but found nothing");
		}
		char c = Character.toLowerCase(this.peek());
		String value;
		if (c == 't') {
			value = this.readExpected("true", false);
		} else if (c == 'f') {
			value = this.readExpected("false", false);
		} else {
			throw new InvalidStringException("Expected a start character of 'true' or 'false' but found: '" + c + "'");
		}
		if ("true".equalsIgnoreCase(value)) {
			return true;
		} else if ("false".equalsIgnoreCase(value)) {
			return false;
		}
		throw new InvalidStringException("Expected a boolean value but found: '" + value + "'");
	}
	
	//region Read number helper methods
	
	/**
	 * Reads a number as a string of the given type.<br>
	 * The initial type is used to determine the type of the number,<br>
	 * it can be overridden by a larger number type (e.g. reading a byte as a long).<br>
	 * <p>
	 *     The number can be quoted or unquoted.<br>
	 *     The first character can be a minus or plus sign.<br>
	 *     The number can contain a single dot for float or double values.<br>
	 *     The number can contain a type suffix (single character, case-insensitive) at the end of the number:<br>
	 * </p>
	 * <ul>
	 *     <li>byte: 'b'</li>
	 *     <li>short: 's'</li>
	 *     <li>integer: 'i'</li>
	 *     <li>long: 'l'</li>
	 *     <li>float: 'f'</li>
	 *     <li>double: 'd'</li>
	 * </ul>
	 * <p>
	 *     The number can be specified in:<br>
	 * </p>
	 * <ul>
	 *     <li>binary by prefixing with '0b'</li>
	 *     <li>octal by prefixing with a single '0'</li>
	 *     <li>decimal by default</li>
	 *     <li>hexadecimal by prefixing with '0x'</li>
	 * </ul>
	 * <p>
	 *     The number can contain underscores to separate digits.<br>
	 *     Floating point numbers can contain an exponent with 'e' or 'p' for hexadecimal floating point numbers.<br>
	 * </p>
	 * @param initialType The initial number type to read as
	 * @return The number as a string which was read
	 * @throws InvalidStringException If the read value is not a (valid) number
	 */
	private @Nullable ParsedNumber readNumberAsString(@Nullable NumberType initialType) {
		if (!this.canRead()) {
			return null;
		}
		char next = this.peek();
		if (next == '"' || next == '\'') {
			String number = this.readQuotedString();
			return new StringReader(number).readNumberAsString(initialType);
		}
		char sign = '+';
		StringBuilder builder = new StringBuilder();
		if (next == '-' || next == '+') {
			sign = this.read();
		}
		
		boolean exponent = false;
		Radix radix = Radix.DECIMAL;
		NumberType type = initialType == null ? NumberType.LONG : initialType; // Largest none infinite integer type
		while (this.canRead()) {
			char c = Character.toLowerCase(this.peek());
			if (c == '_') {
				if (builder.isEmpty()) {
					throw new InvalidStringException("Underscores must not be at the beginning of a number");
				}
				this.skip();
				continue;
			}
			
			if (Character.isDigit(c)) {
				if (builder.length() == 1 && builder.charAt(0) == '0' && c != '0' && radix == Radix.DECIMAL) {
					radix = Radix.OCTAL;
				}
			} else if (c == '.') {
				if (type.isFloatingPoint() && builder.toString().contains(".")) {
					throw new InvalidStringException("Multiple dots in a number are not allowed: '" + sign + builder + c + "'");
				}
				if (!type.isFloatingPoint()) {
					type = NumberType.DOUBLE; // Largest none infinite floating point type
				}
				if (!type.isSupportedRadix(radix)) {
					throw new InvalidStringException("Try to read number of type '" + type + "' which does not support radix '" + radix + "': '" + sign + builder + c + "'");
				}
			} else if (c == 'x') {
				if (radix != Radix.DECIMAL) {
					throw new InvalidStringException("Found invalid 'x' in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'");
				}
				if (builder.length() != 1 || builder.charAt(0) != '0') {
					throw new InvalidStringException("Found invalid 'x' in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'");
				}
				radix = Radix.HEXADECIMAL;
				builder.setLength(0);
				this.skip();
				continue;
			} else if (c == 'b') {
				if (radix != Radix.DECIMAL) {
					throw new InvalidStringException("Found invalid 'b' in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'");
				}
				if (builder.length() == 1 && builder.charAt(0) == '0') {
					radix = Radix.BINARY;
					builder.setLength(0);
					this.skip();
					continue;
				} else {
					break; // 'b' is a valid suffix for byte
				}
			} else if (c == 'e' || c == 'p') {
				if (exponent) {
					throw new InvalidStringException("Found invalid '" + c + "' exponent in number of type '" + type + "' with radix '" + radix + "': '" + sign + builder + c + "'");
				}
				if (!type.isFloatingPoint()) {
					throw new InvalidStringException("Exponent is only allowed for floating point numbers but found in number of type '" + type + "': '" + sign + builder + c + "'");
				}
				if ('e' == c && radix != Radix.DECIMAL) {
					throw new InvalidStringException("Found invalid 'e' exponent in number of type '" + type + "' with radix '" + radix + "' expected 'decimal' radix: '" + sign + builder + c + "'");
				}
				if ('p' == c && radix != Radix.HEXADECIMAL) {
					throw new InvalidStringException("Found invalid 'p' exponent in number of type '" + type + "' with radix '" + radix + "' expected 'hexadecimal' radix: '" + sign + builder + c + "'");
				}
				exponent = true;
			} else if (c == '+' || c == '-') {
				String message = "Found invalid '" + c + "' sign in number of type '" + type + "' with radix '" + radix + "' and missing exponent: '" + sign + builder + c + "'";
				if (!exponent) {
					throw new InvalidStringException(message);
				}
				char last = Character.toLowerCase(builder.charAt(builder.length() - 1));
				if (last != 'e' && last != 'p') {
					throw new InvalidStringException(message);
				}
			} else {
				if (radix != Radix.HEXADECIMAL) {
					break;
				}
				if (c < 'a' || 'f' < c) {
					break;
				}
			}
			builder.append(this.read());
		}
		
		if (this.canRead()) {
			char c = Character.toLowerCase(this.peek());
			NumberType newType = NumberType.getBySuffix(c);
			if (newType != null) {
				this.skip();
				if (!newType.isSupportedRadix(radix)) {
					throw new InvalidStringException("Try to read number of type '" + newType + "' which does not support radix '" + radix + "': '" + sign + builder + c + "'");
				}
				if (!newType.canConvertTo(sign + builder.toString(), radix)) {
					throw new InvalidStringException("Unable to convert number of type '" + type + "' to type '" + newType + "': '" + sign + builder + c + "'");
				}
				type = newType;
			}
		}
		return new ParsedNumber(sign, builder.toString(), type, radix);
	}
	//endregion
	
	/**
	 * Reads a number.<br>
	 * If the number is specified with a type suffix, the number is parsed as the specified type.<br>
	 * By default, the number is parsed as a double if it contains a dot otherwise as a long.<br>
	 * @return The number value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) number
	 * @see #readNumberAsString(NumberType)
	 */
	public @NotNull Number readNumber() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a number but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(null);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a number but found nothing");
		}
		return number.type().parseNumber(number.getSignedValue(), number.radix());
	}
	
	//region Read integer numbers
	
	/**
	 * Reads a byte.<br>
	 * The number can be suffixed with a {@code b} (case-insensitive) to indicate that it is a byte.<br>
	 * @return The byte value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) byte
	 * @see #readNumberAsString(NumberType)
	 */
	public byte readByte() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a byte value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.BYTE);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a byte value but found nothing");
		}
		if (number.type() != NumberType.BYTE) {
			throw new InvalidStringException("Expected a byte value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse byte value: '" + number + "'", e);
		}
	}
	
	/**
	 * Reads a short.<br>
	 * The number can be suffixed with an {@code s} (case-insensitive) to indicate that it is a short.<br>
	 * @return The short value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) short
	 * @see #readNumberAsString(NumberType)
	 */
	public short readShort() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a short value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.SHORT);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a short value but found nothing");
		}
		if (number.type() != NumberType.SHORT) {
			throw new InvalidStringException("Expected a short value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse short value: '" + number + "'", e);
		}
	}
	
	/**
	 * Reads an integer.<br>
	 * The number can be suffixed with an {@code i} (case-insensitive) to indicate that it is an integer.<br>
	 * @return The integer value which was read
	 * @throws InvalidStringException If the read value is not a (valid) integer
	 * @see #readNumberAsString(NumberType)
	 */
	public int readInt() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected an integer value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.INTEGER);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a integer value but found nothing");
		}
		if (number.type() != NumberType.INTEGER) {
			throw new InvalidStringException("Expected a integer value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse integer value: '" + number + "'", e);
		}
	}
	
	/**
	 * Reads a long.<br>
	 * The number can be suffixed with an {@code l} (case-insensitive) to indicate that it is a long.<br>
	 * @return The long value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) long
	 * @see #readNumberAsString(NumberType)
	 */
	public long readLong() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a long value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.LONG);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a long value but found nothing");
		}
		if (number.type() != NumberType.LONG) {
			throw new InvalidStringException("Expected a long value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse long value: '" + number + "'", e);
		}
	}
	//endregion
	
	//region Floating point numbers
	
	/**
	 * Reads a float.<br>
	 * The number can be suffixed with an {@code f} (case-insensitive) to indicate that it is a float.<br>
	 * @return The float value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) float
	 * @see #readNumberAsString(NumberType)
	 */
	public float readFloat() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a float value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.FLOAT);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a float value but found nothing");
		}
		if (number.type() != NumberType.FLOAT) {
			throw new InvalidStringException("Expected a float value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse float value: '" + number + "'", e);
		}
	}
	
	/**
	 * Reads a double.<br>
	 * The number can be suffixed with a {@code d} (case-insensitive) to indicate that it is a double.<br>
	 * @return The double value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) double
	 * @see #readNumberAsString(NumberType)
	 */
	public double readDouble() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a double value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.DOUBLE);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a double value but found nothing");
		}
		if (number.type() != NumberType.DOUBLE) {
			throw new InvalidStringException("Expected a double value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse double value: '" + number + "'", e);
		}
	}
	//endregion
	
	//region Big numbers
	
	/**
	 * Reads a big integer.<br>
	 * @return The big integer value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) big integer
	 * @see #readNumberAsString(NumberType)
	 */
	public @NotNull BigInteger readBigInteger() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a big integer value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.BIG_INTEGER);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a big integer value but found nothing");
		}
		if (number.type() != NumberType.BIG_INTEGER) {
			throw new InvalidStringException("Expected a big integer value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse big integer value: '" + number + "'", e);
		}
	}
	
	/**
	 * Reads a big decimal.<br>
	 * @return The big decimal value which was read
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If the read value is not a (valid) big decimal
	 * @see #readNumberAsString(NumberType)
	 */
	public @NotNull BigDecimal readBigDecimal() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a big decimal value but found nothing");
		}
		ParsedNumber number = this.readNumberAsString(NumberType.BIG_DECIMAL);
		if (number == null || number.value().isEmpty()) {
			throw new InvalidStringException("Expected a big decimal value but found nothing");
		}
		if (number.type() != NumberType.BIG_DECIMAL) {
			throw new InvalidStringException("Expected a big decimal value but found: '" + number + "'");
		}
		try {
			return number.type().parseNumber(number.getSignedValue(), number.radix());
		} catch (NumberFormatException e) {
			throw new InvalidStringException("Unable to parse big decimal value: '" + number + "'", e);
		}
	}
	//endregion
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StringReader that)) return false;
		
		if (this.index != that.index) return false;
		if (this.markedIndex != that.markedIndex) return false;
		return this.string.equals(that.string);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.string);
	}
	//endregion
	
	//region Internal
	
	/**
	 * Internal to represent a parsed number.<br>
	 * @param sign The sign of the number
	 * @param value The value of the number
	 * @param type The type of the number
	 * @param radix The radix of the number
	 */
	private record ParsedNumber(char sign, @NotNull String value, @NotNull NumberType type, @NotNull Radix radix) {
		
		/**
		 * Constructs a new parsed number.<br>
		 * @param sign The sign of the number
		 * @param value The value of the number
		 * @param type The type of the number
		 * @param radix The radix of the number
		 * @throws NullPointerException If the value, type or radix is null
		 */
		private ParsedNumber {
			Objects.requireNonNull(value, "Value must not be null");
			Objects.requireNonNull(type, "Type must not be null");
			Objects.requireNonNull(radix, "Radix must not be null");
		}
		
		/**
		 * Returns the signed value of the number.<br>
		 * The value is prefixed with the sign.<br>
		 * @return The signed value
		 */
		public @NotNull String getSignedValue() {
			return this.sign + this.value;
		}
		
		//region Object overrides
		@Override
		public @NotNull String toString() {
			return this.sign + this.value + " of type " + this.type + " with radix " + this.radix;
		}
		//endregion
	}
	//endregion
}
