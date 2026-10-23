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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.exception.ConcurrentInteractionException;
import net.luis.utils.exception.InvalidStringException;
import net.luis.utils.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a reader to read characters and strings from a string.<br>
 * <p>
 *     This reader treats quotes as normal characters.<br>
 *     Methods which read until a terminator stop at every occurrence of the terminator,<br>
 *     unless the terminator is escaped by the escape character of this reader.<br>
 *     The escape character can be configured on construction or disabled by passing null.
 * </p>
 * <p>
 *     Subclasses add further functionality on top of this reader:<br>
 * </p>
 * <ul>
 *     <li>{@link PositionedStringReader} tracks the line and column of the reader</li>
 *     <li>{@link QuotedStringReader} handles quoted parts</li>
 *     <li>{@link ValueStringReader} reads booleans, numbers and objects</li>
 *     <li>{@link ScopedStringReader} handles scopes and reads collections</li>
 * </ul>
 * <p>
 *     A reader can create sub readers which read a part of the same string.<br>
 *     A sub reader can be of any reader type and may use a different configuration than its parent.
 * </p>
 * <p>
 *     This reader implementation is not performance optimized,<br>
 *     it reads the string character by character in {@code O(n)}.
 * </p>
 *
 * @author Luis-St
 */
public class StringReader implements AutoCloseable {
	
	/**
	 * A tracker which does not track any nested parts.<br>
	 */
	private static final NestingTracker NO_NESTING = new NestingTracker() {
		@Override
		public boolean isNested() {
			return false;
		}
		
		@Override
		public void accept(char c) {}
	};
	/**
	 * The default escape character of a reader.<br>
	 */
	public static final char DEFAULT_ESCAPE_CHARACTER = '\\';
	/**
	 * The string to read from.<br>
	 */
	private final String string;
	/**
	 * The escape character of the reader, or null if escaping is disabled.<br>
	 */
	private final @Nullable Character escapeCharacter;
	/**
	 * The current index of the reader.<br>
	 */
	private int index;
	/**
	 * The marked index of the reader.<br>
	 */
	private int markedIndex = -1;
	/**
	 * The index to which the reader is reset if no index is marked.<br>
	 * The start index is zero for a root reader and the index of the parent at creation for a sub reader.<br>
	 */
	private int startIndex;
	/**
	 * The exclusive index at which the reader stops reading.<br>
	 * The end index is the length of the string for a root reader and the end of the readable part for a sub reader.<br>
	 */
	private int endIndex;
	/**
	 * The reader this reader was created from, or null if this reader is a root reader.<br>
	 */
	private @Nullable StringReader parent;
	/**
	 * The currently active sub reader of this reader, or null if no sub reader is active.<br>
	 */
	private @Nullable StringReader child;
	/**
	 * Whether this reader is a sub reader which has been closed.<br>
	 */
	private boolean closed;
	
	/**
	 * Constructs a new string reader with the given string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character}.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public StringReader(@NonNull String string) {
		this(string, DEFAULT_ESCAPE_CHARACTER);
	}
	
	/**
	 * Constructs a new string reader with the given string and escape character.<br>
	 *
	 * @param string The string to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @throws NullPointerException If the string is null
	 */
	public StringReader(@NonNull String string, @Nullable Character escapeCharacter) {
		this.string = Objects.requireNonNull(string, "String must not be null");
		this.escapeCharacter = escapeCharacter;
		this.endIndex = this.string.length();
	}
	
	/**
	 * Constructs a new string reader from the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character}.<br>
	 *
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public StringReader(@NonNull Reader reader) {
		this(reader, DEFAULT_ESCAPE_CHARACTER);
	}
	
	/**
	 * Constructs a new string reader from the given reader with the given escape character.<br>
	 * The reader is closed after reading the string.<br>
	 *
	 * @param reader The reader to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public StringReader(@NonNull Reader reader, @Nullable Character escapeCharacter) {
		this(readContent(reader), escapeCharacter);
	}
	
	/**
	 * Reads the content of the given reader as a string.<br>
	 *
	 * @param reader The reader to read from
	 * @return The content of the reader
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	private static @NonNull String readContent(@NonNull Reader reader) {
		Objects.requireNonNull(reader, "Reader must not be null");
		try {
			return FileUtils.readString(reader);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read string from reader", e);
		}
	}
	
	/**
	 * Checks whether the given characters match.<br>
	 *
	 * @param actual The actual character
	 * @param expected The expected character
	 * @param caseSensitive Whether the characters should be compared case-sensitive or not
	 * @return True if the characters match, otherwise false
	 */
	private static boolean matches(char actual, char expected, boolean caseSensitive) {
		return caseSensitive ? actual == expected : Character.toLowerCase(actual) == Character.toLowerCase(expected);
	}
	
	/**
	 * Creates a sub reader which reads the remaining part of this reader.<br>
	 *
	 * @param creator The function which creates the sub reader from the string of this reader
	 * @return The created sub reader
	 * @param <R> The type of the sub reader
	 * @throws NullPointerException If the creator is null or returns null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the created reader is not a new reader of the same string
	 * @see #createSubReader(int, Function)
	 */
	public <R extends StringReader> @NonNull R createSubReader(@NonNull Function<String, R> creator) {
		this.ensureUsable();
		return this.createSubReader(this.remaining(), creator);
	}
	
	/**
	 * Creates a sub reader which continues reading at the current index of this reader.<br>
	 * The sub reader can read the given number of characters and stops reading afterward.<br>
	 * <p>
	 *     The creator is called with the string of this reader and must return a new reader created from it.<br>
	 *     The sub reader can be of any reader type and may use a different configuration than this reader.
	 * </p>
	 * <p>
	 *     While the sub reader is active, all interactions with this reader are rejected.<br>
	 *     Closing the sub reader transfers its index back to this reader and makes this reader usable again.<br>
	 *     The sub reader can not be reset to an index before the index at which it was created.
	 * </p>
	 *
	 * @param length The number of characters the sub reader can read
	 * @param creator The function which creates the sub reader from the string of this reader
	 * @return The created sub reader
	 * @param <R> The type of the sub reader
	 * @throws NullPointerException If the creator is null or returns null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the length is negative or exceeds the remaining characters, or the created reader is not a new reader of the same string
	 */
	public <R extends StringReader> @NonNull R createSubReader(int length, @NonNull Function<String, R> creator) {
		Objects.requireNonNull(creator, "Creator must not be null");
		this.ensureUsable();
		if (0 > length) {
			throw new IllegalArgumentException("Length must not be negative");
		}
		if (length > this.remaining()) {
			throw new IllegalArgumentException("Length " + length + " exceeds the " + this.remaining() + " remaining characters");
		}
		
		R reader = Objects.requireNonNull(creator.apply(this.string), "Created sub reader must not be null");
		StringReader subReader = reader;
		if (subReader == this) {
			throw new IllegalArgumentException("Sub reader must not be the reader it is created from");
		}
		if (!this.string.equals(subReader.string)) {
			throw new IllegalArgumentException("Sub reader must be created from the same string as its parent reader");
		}
		if (subReader.parent != null || subReader.child != null || subReader.closed) {
			throw new IllegalArgumentException("Sub reader must be a new reader which is not linked to another reader");
		}
		
		subReader.index = this.index;
		subReader.startIndex = this.index;
		subReader.endIndex = this.index + length;
		subReader.parent = this;
		this.child = reader;
		return reader;
	}
	
	/**
	 * Ensures that this reader can be interacted with.<br>
	 *
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws ConcurrentInteractionException If a child reader is active
	 */
	protected void ensureUsable() {
		if (this.closed) {
			throw new IllegalStateException("Interactions on a closed sub reader are not allowed");
		}
		if (this.child != null) {
			throw new ConcurrentInteractionException("Interactions on the parent reader are not allowed while a child reader is active");
		}
	}
	
	/**
	 * Checks whether the given character is the escape character of this reader.<br>
	 * If escaping is disabled, no character is an escape character.<br>
	 *
	 * @param c The character to check
	 * @return True if the character is the escape character, otherwise false
	 */
	protected boolean isEscapeCharacter(char c) {
		return this.escapeCharacter != null && c == this.escapeCharacter;
	}
	
	/**
	 * Creates a new tracker for nested parts of the string.<br>
	 * The tracker is used by the methods which read until a terminator.<br>
	 * While the tracker reports a nested part, terminators are ignored.<br>
	 * This reader has no nested parts, subclasses override this method to add them.<br>
	 *
	 * @return A new tracker
	 */
	protected @NonNull NestingTracker createTracker() {
		return NO_NESTING;
	}
	
	/**
	 * Returns a description of the current location of the reader.<br>
	 * The description is appended to the messages of exceptions thrown while reading.<br>
	 *
	 * @return The description of the location
	 */
	protected @NonNull String describeLocation() {
		return "index " + this.index;
	}
	
	/**
	 * Appends the current location of the reader to the given message.<br>
	 *
	 * @param message The message
	 * @return The message with the location
	 * @throws NullPointerException If the message is null
	 * @see #describeLocation()
	 */
	protected @NonNull String withLocation(@NonNull String message) {
		Objects.requireNonNull(message, "Message must not be null");
		return message + " at " + this.describeLocation();
	}
	
	/**
	 * Returns the string to read from.<br>
	 * @return The string
	 */
	public @NonNull String getString() {
		return this.string;
	}
	
	/**
	 * Returns the escape character of this reader.<br>
	 * @return The escape character, or null if escaping is disabled
	 */
	public @Nullable Character getEscapeCharacter() {
		return this.escapeCharacter;
	}
	
	/**
	 * Returns the current character index of the reader.<br>
	 * @return The current index
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Sets the current index of the reader.<br>
	 * The index must be between the start index and the end index of this reader.<br>
	 *
	 * @param index The new index
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IndexOutOfBoundsException If the index is outside the readable part of this reader
	 */
	protected void setIndex(int index) {
		this.ensureUsable();
		if (index < this.startIndex || index > this.endIndex) {
			throw new IndexOutOfBoundsException("Index " + index + " is outside the readable part from " + this.startIndex + " to " + this.endIndex);
		}
		this.index = index;
	}
	
	/**
	 * Returns the exclusive index at which the reader stops reading.<br>
	 * @return The end index
	 */
	public int getEndIndex() {
		return this.endIndex;
	}
	
	/**
	 * Returns the number of remaining characters which can be read.<br>
	 * @return The number of remaining characters
	 */
	protected int remaining() {
		return Math.max(0, this.endIndex - this.index);
	}
	
	/**
	 * Reads the next character from the string.<br>
	 *
	 * @return The next character
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IndexOutOfBoundsException If there are no more characters to read ({@link #canRead()} returns false)
	 */
	public char read() {
		char c = this.peek();
		this.index++;
		return c;
	}
	
	/**
	 * Reads the given number of characters from the string.<br>
	 *
	 * @param amount The number of characters to read
	 * @return The read characters as a string
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 * @throws IndexOutOfBoundsException If there are not enough characters to read ({@link #canRead(int)} returns false)
	 */
	public @NonNull String read(int amount) {
		this.ensureUsable();
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		if (!this.canRead(amount)) {
			throw new IndexOutOfBoundsException("Expected " + amount + " characters but found only " + this.remaining() + " remaining characters");
		}
		
		String str = this.string.substring(this.index, this.index + amount);
		this.index += amount;
		return str;
	}
	
	/**
	 * Checks whether there is at least the given number of characters to read.<br>
	 *
	 * @param amount The number of characters to read
	 * @return True if there is at least a given number of characters to read, otherwise false
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 */
	public boolean canRead(int amount) {
		this.ensureUsable();
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		
		return this.index + amount <= this.endIndex;
	}
	
	/**
	 * Checks whether there are more characters to read.<br>
	 *
	 * @return True if there are more characters to read, otherwise false
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public boolean canRead() {
		return this.canRead(1);
	}
	
	/**
	 * Checks whether the number of characters to read matches the given predicate.<br>
	 *
	 * @param amount The number of characters to read
	 * @param predicate The predicate to match the characters
	 * @return True if the number of characters to read matches the predicate, otherwise false
	 * @throws NullPointerException If the predicate is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 */
	public boolean canRead(int amount, @NonNull Predicate<Character> predicate) {
		Objects.requireNonNull(predicate, "Predicate must not be null");
		if (!this.canRead(amount)) {
			return false;
		}
		
		for (int i = 0; i < amount; i++) {
			if (!predicate.test(this.string.charAt(this.index + i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Peeks the current character without incrementing the index.<br>
	 *
	 * @return The current character
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IndexOutOfBoundsException If there are no more characters to read ({@link #canRead()} returns false)
	 */
	public char peek() {
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException("Expected a character but reached the end of the readable part at index " + this.index);
		}
		return this.string.charAt(this.index);
	}
	
	/**
	 * Peeks the given number of characters without incrementing the index.<br>
	 * If the amount is greater than the remaining characters, the remaining characters are returned.<br>
	 *
	 * @param amount The number of characters to peek
	 * @return The peeked characters as a string
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 */
	public @NonNull String peek(int amount) {
		this.ensureUsable();
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		
		return this.string.substring(this.index, this.index + Math.min(amount, this.remaining()));
	}
	
	/**
	 * Skips the next character.<br>
	 * Increments the index by one.<br>
	 *
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public void skip() {
		this.ensureUsable();
		this.index++;
	}
	
	/**
	 * Skips the given number of characters.<br>
	 *
	 * @param amount The number of characters to skip
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the amount is less than or equal to zero
	 */
	public void skip(int amount) {
		this.ensureUsable();
		if (0 >= amount) {
			throw new IllegalArgumentException("Amount must be greater than zero");
		}
		
		this.index += amount;
	}
	
	/**
	 * Skips all characters that are equal to the given character.<br>
	 * If the next character is not equal to the given character<br>
	 * or there are no more characters to read, nothing happens.<br>
	 *
	 * @param c The character to skip
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public void skip(char c) {
		this.skip(next -> next == c);
	}
	
	/**
	 * Skips all characters that match the given predicate.<br>
	 * If the next character does not match the predicate<br>
	 * or there are no more characters to read, nothing happens.<br>
	 *
	 * @param predicate The predicate to match
	 * @throws NullPointerException If the predicate is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public void skip(@NonNull Predicate<Character> predicate) {
		Objects.requireNonNull(predicate, "Predicate must not be null");
		this.ensureUsable();
		
		while (this.canRead() && predicate.test(this.peek())) {
			this.skip();
		}
	}
	
	/**
	 * Skips all whitespaces.<br>
	 * A whitespace is a character that returns true when calling {@link Character#isWhitespace(char)}.<br>
	 *
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public void skipWhitespaces() {
		this.skip(Character::isWhitespace);
	}
	
	/**
	 * Marks the current index of the reader.<br>
	 * The marked index can be reset to by calling {@link #reset()}.<br>
	 * After calling {@link #reset()}, the marked index will be reset.<br>
	 *
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public void mark() {
		this.ensureUsable();
		this.markedIndex = this.index;
	}
	
	/**
	 * Resets the index of the reader to the marked index if set, otherwise to the start index.<br>
	 * The start index is zero, or the index at which the reader was created if it is a sub reader.<br>
	 * If the marked index is set, it will be reset after calling this method.<br>
	 *
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public void reset() {
		this.ensureUsable();
		
		if (this.markedIndex == -1) {
			this.index = this.startIndex;
		} else {
			this.index = this.markedIndex;
			this.markedIndex = -1;
		}
	}
	
	/**
	 * Reads all characters until the next line break is found.<br>
	 * Supported line breaks are: LF ('\n'), CR ('\r'), CRLF ('\r\n').<br>
	 *
	 * @param includeLineBreak Whether the line break should be included in the result or not
	 * @return The line which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public @NonNull String readLine(boolean includeLineBreak) {
		this.ensureUsable();
		
		StringBuilder builder = new StringBuilder();
		while (this.canRead()) {
			char c = this.read();
			if (c != '\n' && c != '\r') {
				builder.append(c);
				continue;
			}
			
			// Types of line breaks:
			//  - LF: Line Feed, '\n' (Unix, Linux, macOS)
			//  - CR: Carriage Return, '\r' (Old Mac OS)
			//  - CRLF: CR followed by LF, '\r\n' (Windows)
			if (includeLineBreak) {
				builder.append(c);
			}
			if (c == '\r' && this.canRead() && this.peek() == '\n') {
				char lineFeed = this.read();
				if (includeLineBreak) {
					builder.append(lineFeed);
				}
			}
			break;
		}
		return builder.toString();
	}
	
	/**
	 * Reads the remaining string.<br>
	 *
	 * @return The remaining string
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 */
	public @NonNull String readRemaining() {
		this.ensureUsable();
		if (!this.canRead()) {
			return "";
		}
		
		String remaining = this.string.substring(this.index, this.endIndex);
		this.index = this.endIndex;
		return remaining;
	}
	
	/**
	 * Reads the string until the next whitespace (' ') is found.<br>
	 * The whitespace is read but not included in the result.<br>
	 *
	 * @return The string which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @see #readUnquotedString(char)
	 */
	public @NonNull String readUnquotedString() {
		return this.readUnquotedString(' ');
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The terminator is read but not included in the result.<br>
	 * Escape characters and nested parts are not handled by this method.<br>
	 *
	 * @param terminator The terminator to read until
	 * @return The string which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 */
	public @NonNull String readUnquotedString(char terminator) {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected an unquoted string but found nothing"));
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
	 * Validates the given terminator character.<br>
	 *
	 * @param terminator The terminator to validate
	 * @throws IllegalArgumentException If the terminator is the escape character
	 */
	protected void validateTerminator(char terminator) {
		if (this.isEscapeCharacter(terminator)) {
			throw new IllegalArgumentException("Terminator must not be the escape character");
		}
	}
	
	/**
	 * Validates the given terminator characters and returns them as a set without duplicates.<br>
	 *
	 * @param terminators The terminators to validate
	 * @return The unique terminators
	 * @throws NullPointerException If the terminator array is null
	 * @throws IllegalArgumentException If the terminators are empty or contain the escape character
	 */
	protected @NonNull Set<Character> validateTerminators(char @NonNull [] terminators) {
		Objects.requireNonNull(terminators, "Terminators must not be null");
		
		Set<Character> uniqueTerminators = Sets.newHashSet(ArrayUtils.toObject(terminators));
		if (uniqueTerminators.isEmpty()) {
			throw new IllegalArgumentException("Terminators must not be empty");
		}
		if (uniqueTerminators.stream().anyMatch(this::isEscapeCharacter)) {
			throw new IllegalArgumentException("Terminators must not contain the escape character");
		}
		return uniqueTerminators;
	}
	
	/**
	 * Validates the given terminator string.<br>
	 *
	 * @param terminator The terminator string to validate
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains the escape character
	 */
	protected void validateTerminator(@NonNull String terminator) {
		Objects.requireNonNull(terminator, "Terminator string must not be null");
		if (terminator.isEmpty()) {
			throw new IllegalArgumentException("Terminator string must not be empty");
		}
		if (terminator.chars().anyMatch(c -> this.isEscapeCharacter((char) c))) {
			throw new IllegalArgumentException("Terminator string must not contain the escape character");
		}
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator is escaped, it is ignored and the next character is read.<br>
	 *     If the terminator is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned on the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator is the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntil(char terminator) {
		this.ensureUsable();
		this.validateTerminator(terminator);
		
		return this.readUntil(c -> c == terminator, false);
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator is escaped, it is ignored and the next character is read.<br>
	 *     If the terminator is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator is found, inclusive of the terminator
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator is the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilInclusive(char terminator) {
		this.ensureUsable();
		this.validateTerminator(terminator);
		
		return this.readUntil(c -> c == terminator, true);
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The terminator and escape character are read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator is escaped, it is ignored and the next character is read.<br>
	 *     If the terminator is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminator to read until
	 * @return The string which was read until the terminator
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator is the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilSkip(char terminator) {
		this.ensureUsable();
		this.validateTerminator(terminator);
		
		return this.readUntilSkip(c -> c == terminator);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator is escaped, it is ignored and the next character is read.<br>
	 *     If none terminator is found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned on the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator array is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminators are empty or contain the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntil(char @NonNull ... terminators) {
		this.ensureUsable();
		
		return this.readUntil(this.validateTerminators(terminators)::contains, false);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator is escaped, it is ignored and the next character is read.<br>
	 *     If none terminator is found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator is found, inclusive of the terminator
	 * @throws NullPointerException If the terminator array is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminators are empty or contain the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilInclusive(char @NonNull ... terminators) {
		this.ensureUsable();
		
		return this.readUntil(this.validateTerminators(terminators)::contains, true);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The terminators and escape character are read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator is escaped, it is ignored and the next character is read.<br>
	 *     If none terminator is found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator array is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminators are empty or contain the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilSkip(char @NonNull ... terminators) {
		this.ensureUsable();
		
		return this.readUntilSkip(this.validateTerminators(terminators)::contains);
	}
	
	/**
	 * Reads the string until the given predicate is true.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If the predicate is true at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the predicate is true in a nested part of the string, the predicate is ignored.<br>
	 *     If the predicate is true for an escaped character, it is ignored and the next character is read.<br>
	 *     If the predicate is not true, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned on the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param predicate The predicate to match the characters
	 * @return The string which was read until the predicate is true
	 * @throws NullPointerException If the predicate is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntil(@NonNull Predicate<Character> predicate) {
		return this.readUntil(predicate, false);
	}
	
	/**
	 * Reads the string until the given predicate is true.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If the predicate is true at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the predicate is true in a nested part of the string, the predicate is ignored.<br>
	 *     If the predicate is true for an escaped character, it is ignored and the next character is read.<br>
	 *     If the predicate is not true, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param predicate The predicate to match the characters
	 * @return The string which was read until the predicate is true, inclusive of the matching character
	 * @throws NullPointerException If the predicate is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilInclusive(@NonNull Predicate<Character> predicate) {
		return this.readUntil(predicate, true);
	}
	
	/**
	 * Reads the string until the given predicate is true.<br>
	 * The matching character and escape character are read but not included in the result.<br>
	 * <p>
	 *     If the predicate is true at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the predicate is true in a nested part of the string, the predicate is ignored.<br>
	 *     If the predicate is true for an escaped character, it is ignored and the next character is read.<br>
	 *     If the predicate is not true, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param predicate The predicate to match the characters
	 * @return The string which was read until the predicate is true
	 * @throws NullPointerException If the predicate is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilSkip(@NonNull Predicate<Character> predicate) {
		String str = this.readUntil(predicate, false);
		if (this.canRead()) {
			this.skip();
		}
		return str;
	}
	
	/**
	 * Internal method to read the string until the given predicate is true.<br>
	 * Characters in nested parts reported by the {@link #createTracker() tracker} are never tested.<br>
	 *
	 * @param predicate The predicate to match the characters
	 * @param inclusive Whether the character which matches the predicate should be included in the result or not
	 * @return The string which was read until the predicate is true
	 * @throws NullPointerException If the predicate is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #readUntil(Predicate)
	 * @see #readUntilInclusive(Predicate)
	 * @see #readUntilSkip(Predicate)
	 */
	protected @NonNull String readUntil(@NonNull Predicate<Character> predicate, boolean inclusive) {
		Objects.requireNonNull(predicate, "Predicate must not be null");
		this.ensureUsable();
		
		NestingTracker tracker = this.createTracker();
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		while (this.canRead()) {
			char c = this.peek();
			if (escaped) {
				escaped = false;
			} else if (this.isEscapeCharacter(c)) {
				escaped = true;
				this.skip();
				continue;
			} else if (!tracker.isNested() && predicate.test(c)) {
				if (inclusive) {
					builder.append(this.read());
				}
				return builder.toString();
			} else {
				tracker.accept(c);
			}
			builder.append(this.read());
		}
		tracker.finish();
		return builder.toString();
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned on the first character of the terminating string after this method is called.<br>
	 *     The next call to {@link #read()} will return the first character of the terminating string.<br>
	 *     If the terminating string is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator string is empty or contains the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntil(@NonNull String terminator, boolean caseSensitive) {
		return this.readUntil(terminator, caseSensitive, false);
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The escape character is read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating string after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating string.<br>
	 *     If the terminating string is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator is found, inclusive of the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator string is empty or contains the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilInclusive(@NonNull String terminator, boolean caseSensitive) {
		return this.readUntil(terminator, caseSensitive, true);
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The terminator string and escape character are read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a nested part of the string, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating string after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating string.<br>
	 *     If the terminating string is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator string is empty or contains the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #createTracker()
	 */
	public @NonNull String readUntilSkip(@NonNull String terminator, boolean caseSensitive) {
		String str = this.readUntil(terminator, caseSensitive, false);
		if (this.canRead(terminator.length())) {
			this.skip(terminator.length());
		}
		return str;
	}
	
	/**
	 * Internal method to read the string until the terminating string is found.<br>
	 * The terminator string is never matched in nested parts reported by the {@link #createTracker() tracker}.<br>
	 *
	 * @param terminator The terminator string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @param inclusive Whether the terminator string should be included in the result or not
	 * @return The string which was read until the terminator string is found
	 * @throws NullPointerException If the terminator string is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the terminator string is empty or contains the escape character
	 * @throws InvalidStringException If a nested part is invalid
	 * @see #readUntil(String, boolean)
	 * @see #readUntilInclusive(String, boolean)
	 * @see #readUntilSkip(String, boolean)
	 */
	protected @NonNull String readUntil(@NonNull String terminator, boolean caseSensitive, boolean inclusive) {
		Objects.requireNonNull(terminator, "Terminator string must not be null");
		this.ensureUsable();
		this.validateTerminator(terminator);
		
		NestingTracker tracker = this.createTracker();
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		while (this.canRead()) {
			if (!escaped && !tracker.isNested()) {
				String lookahead = this.peek(terminator.length());
				if (caseSensitive ? terminator.equals(lookahead) : terminator.equalsIgnoreCase(lookahead)) {
					if (inclusive) {
						builder.append(lookahead);
						this.skip(terminator.length());
					}
					return builder.toString();
				}
			}
			
			char c = this.peek();
			if (escaped) {
				escaped = false;
			} else if (this.isEscapeCharacter(c)) {
				escaped = true;
				this.skip();
				continue;
			} else {
				tracker.accept(c);
			}
			builder.append(this.read());
		}
		tracker.finish();
		return builder.toString();
	}
	
	/**
	 * Reads the given expected string from the reader.<br>
	 * If any read character does not match the expected character, an exception is thrown.<br>
	 * <p>
	 *     If the expected string could not be read, an exception is thrown.<br>
	 *     The reader index is set to the first character which does not match the expected string.
	 * </p>
	 *
	 * @param expected The expected string
	 * @param caseSensitive Whether the expected string should be case-sensitive or not
	 * @return The expected string which was read
	 * @throws NullPointerException  If the expected string is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws IllegalArgumentException If the expected string is empty
	 * @throws InvalidStringException If the string read does not match the expected string
	 */
	public @NonNull String readExpected(@NonNull String expected, boolean caseSensitive) {
		Objects.requireNonNull(expected, "Expected string must not be null");
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected '" + expected + "' but found nothing"));
		}
		if (expected.isEmpty()) {
			throw new IllegalArgumentException("Expected string must not be empty");
		}
		
		int length = expected.length();
		if (!this.canRead(length)) {
			throw new InvalidStringException(this.withLocation("Expected '" + expected + "' requires " + length + " characters but found " + this.remaining() + " remaining characters"));
		}
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = this.peek();
			if (!matches(c, expected.charAt(i), caseSensitive)) {
				throw new InvalidStringException(this.withLocation("Expected '" + expected + "' but found: '" + builder + c + "'"));
			}
			builder.append(this.read());
		}
		return builder.toString();
	}
	
	/**
	 * Reads the first matching expected string from the reader.<br>
	 * <p>
	 *     If any read character does not match the expected character of a given string,<br>
	 *     the string is removed from the list.<br>
	 *     If no expected string matches the read string, an exception is thrown.
	 * </p>
	 * <p>
	 *     If none of the expected strings could be read, an exception is thrown.<br>
	 *     The reader index is set to the first character which does not match any of the expected strings.
	 * </p>
	 *
	 * @param expected The list of expected strings
	 * @param caseSensitive Whether the expected strings should be case-sensitive or not
	 * @return The expected string which was read
	 * @throws NullPointerException  If the list of expected strings is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws IllegalArgumentException If the list of expected strings is empty
	 * @throws InvalidStringException If the string read does not match any of the expected strings
	 */
	public @NonNull String readExpected(@NonNull List<String> expected, boolean caseSensitive) {
		Objects.requireNonNull(expected, "Expected strings must not be null");
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected one of '" + expected + "' but found nothing"));
		}
		if (expected.isEmpty()) {
			throw new IllegalArgumentException("Expected strings must not be empty");
		}
		
		int readable = this.remaining();
		List<String> possibleExpected = expected.stream().filter(e -> e.length() <= readable).collect(Collectors.toList());
		if (possibleExpected.isEmpty()) {
			throw new InvalidStringException(this.withLocation("Expected one of '" + expected + "' but there are not enough remaining characters " + readable + " for any expected string"));
		}
		
		int start = this.index;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < readable; i++) {
			char c = this.peek();
			List<String> newPossibleExpected = Lists.newArrayList();
			for (String expectedString : possibleExpected) {
				if (i < expectedString.length() && matches(c, expectedString.charAt(i), caseSensitive)) {
					newPossibleExpected.add(expectedString);
				}
			}
			if (newPossibleExpected.isEmpty()) {
				break;
			}
			
			builder.append(this.read());
			for (String expectedString : newPossibleExpected) {
				if (caseSensitive ? expectedString.contentEquals(builder) : expectedString.equalsIgnoreCase(builder.toString())) {
					return builder.toString();
				}
			}
			possibleExpected = newPossibleExpected;
		}
		throw new InvalidStringException(this.withLocation("Expected one of '" + expected + "' but found: '" + this.string.substring(start, this.index) + "'"));
	}
	
	/**
	 * Closes this reader if it is a sub reader.<br>
	 * <p>
	 *     The index of this reader is transferred back to its parent reader,<br>
	 *     which makes the parent reader usable again.<br>
	 *     After closing, all interactions with this reader are rejected.
	 * </p>
	 * <p>
	 *     Closing a root reader or an already closed sub reader has no effect.
	 * </p>
	 *
	 * @throws ConcurrentInteractionException If a child reader of this reader is active
	 */
	@Override
	public void close() {
		if (this.parent == null || this.closed) {
			return;
		}
		if (this.child != null) {
			throw new ConcurrentInteractionException("Cannot close a sub reader while its own child reader is active");
		}
		
		this.parent.index = this.index;
		this.parent.child = null;
		this.closed = true;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (o == null || this.getClass() != o.getClass()) return false;
		StringReader that = (StringReader) o;
		
		if (this.index != that.index) return false;
		if (this.markedIndex != that.markedIndex) return false;
		if (this.endIndex != that.endIndex) return false;
		if (!Objects.equals(this.escapeCharacter, that.escapeCharacter)) return false;
		return this.string.equals(that.string);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.string, this.escapeCharacter);
	}
	//endregion
	
	/**
	 * A tracker for nested parts of a string, such as quoted parts or scopes.<br>
	 * <p>
	 *     The tracker is fed with every unescaped character read by the methods which read until a terminator.<br>
	 *     While the tracker reports a nested part, terminators are ignored.<br>
	 *     After the end of the readable part is reached, {@link #finish()} is called.
	 * </p>
	 *
	 * @author Luis-St
	 */
	protected interface NestingTracker {
		
		/**
		 * Checks whether the tracker is currently inside a nested part.<br>
		 * @return True if the tracker is inside a nested part, otherwise false
		 */
		boolean isNested();
		
		/**
		 * Accepts the next unescaped character which is not a terminator.<br>
		 * @param c The character
		 */
		void accept(char c);
		
		/**
		 * Called after the end of the readable part is reached without finding a terminator.<br>
		 * The default implementation does nothing.<br>
		 *
		 * @throws InvalidStringException If an unclosed nested part is not allowed
		 */
		default void finish() {}
	}
}
