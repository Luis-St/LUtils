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

import net.luis.utils.exception.InvalidStringException;
import org.jspecify.annotations.NonNull;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.luis.utils.io.reader.StringScope.*;

/**
 * A utility class to read strings with scopes.<br>
 * A scope is defined by two characters, an opening and a closing character.<br>
 *
 * @author Luis-St
 */
public class ScopedStringReader extends StringReader {
	
	/**
	 * Constructs a new scoped string reader with the given string.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public ScopedStringReader(@NonNull String string) {
		super(string);
	}
	
	/**
	 * Constructs a new scoped string reader with the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 *
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ScopedStringReader(@NonNull Reader reader) {
		super(reader);
	}
	
	/**
	 * Reads a string with the given scope.<br>
	 * <p>
	 *     The scope is defined by an opening and a closing character.<br>
	 *     The method will read until the opened scope is closed.<br>
	 *     If there are nested scopes, the method will read until all scopes are closed.
	 * </p>
	 * <p>
	 *     The opening and closing character are included in the returned string.<br>
	 *     If there are no more characters to read, an empty string is returned.
	 * </p>
	 *
	 * @param scope The scope to use
	 * @return The read string
	 * @throws NullPointerException If the scope is null
	 * @throws IllegalArgumentException If the next character is not the opening character of the scope
	 * @throws InvalidStringException If the scope is invalid
	 */
	public @NonNull String readScope(@NonNull StringScope scope) {
		Objects.requireNonNull(scope, "Scope must not be null");
		if (!this.canRead()) {
			return "";
		}
		
		char next = this.peek();
		if (next != scope.open()) {
			throw new IllegalArgumentException("Expected '" + scope.open() + "' but got '" + next + "'");
		}
		
		StringBuilder builder = new StringBuilder();
		int depth = 0;
		boolean escaped = false;
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		while (this.canRead()) {
			char c = this.read();
			
			if (c == '\\' && !escaped) {
				builder.append(c);
				escaped = true;
				continue;
			}
			
			if (c == '\'' && !escaped) {
				inSingleQuotes = !inSingleQuotes;
			} else if (c == '\"' && !escaped) {
				inDoubleQuotes = !inDoubleQuotes;
			}
			
			if (!inSingleQuotes && !inDoubleQuotes && !escaped) {
				if (c == scope.open()) {
					depth++;
				} else if (c == scope.close()) {
					depth--;
					if (depth == 0) {
						builder.append(c);
						break;
					}
				}
			}
			
			builder.append(c);
			escaped = false;
		}
		
		if (depth > 0) {
			throw new InvalidStringException("Invalid scope, " + depth + " scope" + (depth > 1 ? "s" : "") + " are not closed, expected '" + scope.close() + "'");
		} else if (depth < 0) {
			throw new InvalidStringException("Invalid scope, " + -depth + " scope" + (-depth > 1 ? "s" : "") + " are not opened, expected '" + scope.open() + "'");
		}
		return builder.toString();
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
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
	 * @throws IllegalArgumentException If the terminator is a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntil(char terminator) {
		return super.readUntil(terminator);
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
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
	 * @throws IllegalArgumentException If the terminator is a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilInclusive(char terminator) {
		return super.readUntilInclusive(terminator);
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The terminator and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
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
	 * @throws IllegalArgumentException If the terminator is a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilSkip(char terminator) {
		return super.readUntilSkip(terminator);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
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
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntil(char @NonNull ... terminators) {
		return super.readUntil(terminators);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
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
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilInclusive(char @NonNull ... terminators) {
		return super.readUntilInclusive(terminators);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The terminators and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
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
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilSkip(char @NonNull ... terminators) {
		return super.readUntilSkip(terminators);
	}
	
	/**
	 * Reads the string until the given predicate is true.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the predicate is true at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the predicate is true in a quoted part or in a scope, the predicate is ignored.<br>
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
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntil(@NonNull Predicate<Character> predicate) {
		return super.readUntil(predicate);
	}
	
	/**
	 * Reads the string until the given predicate is true.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the predicate is true at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the predicate is true in a quoted part or in a scope, the predicate is ignored.<br>
	 *     If the predicate is true for an escaped character, it is ignored and the next character is read.<br>
	 *     If the predicate is not true, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating character after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating character.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param predicate The predicate to match the characters is found, inclusive of the terminator
	 * @return The string which was read until the predicate is true
	 * @throws NullPointerException If the predicate is null
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilInclusive(@NonNull Predicate<Character> predicate) {
		return super.readUntilInclusive(predicate);
	}
	
	/**
	 * Reads the string until the given predicate is true.<br>
	 * The predicate and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the predicate is true at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the predicate is true in a quoted part or in a scope, the predicate is ignored.<br>
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
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilSkip(@NonNull Predicate<Character> predicate) {
		return super.readUntilSkip(predicate);
	}
	
	/**
	 * Internal method to read the string until the given predicate is true.<br>
	 *
	 * @param predicate The predicate to match the characters
	 * @param inclusive Whether the character which matches the predicate should be included in the result or not
	 * @return The string which was read until the predicate is true
	 * @throws NullPointerException If the predicate is null
	 * @see #readUntil(char)
	 * @see #readUntilInclusive(char)
	 * @see #readUntilSkip(char)
	 * @see #readUntil(char...)
	 * @see #readUntilInclusive(char...)
	 * @see #readUntilSkip(char...)
	 * @see #readUntil(Predicate)
	 * @see #readUntilInclusive(Predicate)
	 * @see #readUntilSkip(Predicate)
	 */
	@Override
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull String readUntil(@NonNull Predicate<Character> predicate, boolean inclusive) {
		Objects.requireNonNull(predicate, "Predicate must not be null");
		
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		Deque<Character> stack = new ArrayDeque<>();
		
		while (this.canRead()) {
			char c = this.peek();
			if (escaped) {
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
				this.skip();
				continue;
			} else if (!inSingleQuotes && !inDoubleQuotes && predicate.test(c) && stack.isEmpty()) {
				if (inclusive) {
					builder.append(this.read());
				}
				break;
			} else if (c == '\'') {
				inSingleQuotes = !inSingleQuotes;
			} else if (c == '\"') {
				inDoubleQuotes = !inDoubleQuotes;
			} else if (SCOPE_REGISTRY.containsKey(c)) {
				stack.push(c);
			} else if (!stack.isEmpty() && SCOPE_REGISTRY.get(stack.peek()) == c) {
				stack.pop();
			}
			builder.append(this.read());
		}
		
		if (!stack.isEmpty()) {
			String scopes = stack.reversed().stream().map(SCOPE_REGISTRY::get).map(String::valueOf).collect(Collectors.joining("', '", "'", "'"));
			throw new InvalidStringException("Invalid scope, " + stack.size() + " scope" + (stack.size() > 1 ? "s" : "") + " are not closed, expected closing characters in order: " + scopes);
		}
		return builder.toString();
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned on the first character of the terminating string after this method is called.<br>
	 *     The next call to {@link #read()} will return the first character of the terminating string.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntil(@NonNull String terminator, boolean caseSensitive) {
		return super.readUntil(terminator, caseSensitive);
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating string after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating string.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator is found, inclusive of the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilInclusive(@NonNull String terminator, boolean caseSensitive) {
		return super.readUntilInclusive(terminator, caseSensitive);
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The terminator string and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.
	 * </p>
	 * <p>
	 *     The cursor will be positioned after the terminating string after this method is called.<br>
	 *     The next call to {@link #read()} will return the character after the terminating string.<br>
	 *     If the terminating character is not found, the cursor will be positioned at the end of the string.
	 * </p>
	 *
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NonNull String readUntilSkip(@NonNull String terminator, boolean caseSensitive) {
		return super.readUntilSkip(terminator, caseSensitive);
	}
	
	/**
	 * Internal method to read the string until the terminating string is found.<br>
	 *
	 * @param terminator The terminator string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @param inclusive Whether the terminator string should be included in the result or not
	 * @return The string which was read until the equals predicate is true
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 * @throws InvalidStringException If the scope is invalid
	 * @see #readUntil(String, boolean)
	 * @see #readUntilInclusive(String, boolean)
	 * @see #readUntilSkip(String, boolean)
	 */
	@Override
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull String readUntil(@NonNull String terminator, boolean caseSensitive, boolean inclusive) {
		Objects.requireNonNull(terminator, "Terminator string must not be null");
		if (terminator.isEmpty()) {
			throw new IllegalArgumentException("Terminators string must not be empty");
		}
		if (terminator.contains("\\")) {
			throw new IllegalArgumentException("Terminator string must not contain a backslash");
		}
		
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		Deque<Character> stack = new ArrayDeque<>();
		
		while (this.canRead()) {
			if (!inSingleQuotes && !inDoubleQuotes && stack.isEmpty()) {
				String lookahead = this.peek(terminator.length());
				boolean matches = caseSensitive ? terminator.equals(lookahead) : terminator.equalsIgnoreCase(lookahead);
				if (matches) {
					if (inclusive) {
						builder.append(lookahead);
						this.skip(terminator.length());
					}
					break;
				}
			}
			
			char c = this.peek();
			if (escaped) {
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
				this.skip();
				continue;
			} else if (c == '\'') {
				inSingleQuotes = !inSingleQuotes;
			} else if (c == '"') {
				inDoubleQuotes = !inDoubleQuotes;
			} else if (SCOPE_REGISTRY.containsKey(c)) {
				stack.push(c);
			} else if (!stack.isEmpty() && SCOPE_REGISTRY.get(stack.peek()) == c) {
				stack.pop();
			}
			
			builder.append(this.read());
		}
		
		if (!stack.isEmpty()) {
			String scopes = stack.reversed().stream().map(SCOPE_REGISTRY::get).map(String::valueOf).collect(Collectors.joining("', '", "'", "'"));
			throw new InvalidStringException("Invalid scope, " + stack.size() + " scope" + (stack.size() > 1 ? "s" : "") + " are not closed, expected closing characters in order: " + scopes);
		}
		return builder.toString();
	}
	
	/**
	 * Reads a collection like object from the string.<br>
	 * <p>
	 *     The collection is defined by an opening and a closing character.<br>
	 *     If there are no more characters to read, an empty collection is returned.<br>
	 *     Whitespace characters are ignored.
	 * </p>
	 * <p>
	 *     The elements of the collection are separated by a comma.<br>
	 *     The elements are parsed by the given parser.
	 * </p>
	 *
	 * @param scope The scope with the opening and closing character
	 * @param parser The parser to parse the elements
	 * @return The read collection
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the scope or parser is null
	 * @throws IllegalArgumentException If an error occurs while reading the collection
	 */
	private <T> @NonNull Collection<T> readCollection(@NonNull StringScope scope, @NonNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(scope, "Scope must not be null");
		Objects.requireNonNull(parser, "Parser must not be null");
		
		ScopedStringReader reader = new ScopedStringReader(this.readScope(scope));
		char next = reader.peek();
		if (next != scope.open()) {
			throw new InvalidStringException("Expected '" + scope.open() + "' but got '" + next + "'");
		}
		
		reader.skip();
		reader.skipWhitespaces();
		List<T> collection = new ArrayList<>();
		while (reader.canRead()) {
			if (reader.peek() == scope.close()) {
				reader.skip();
				break;
			}
			
			String value = reader.readUntilSkip(',').strip();
			if (value.isEmpty()) {
				continue;
			}
			
			if (value.endsWith("" + scope.close())) {
				int remaining = reader.getString().length() - reader.getIndex();
				if (0 >= remaining || reader.canRead(remaining, Character::isWhitespace)) {
					value = value.substring(0, value.length() - 1);
				}
			}
			collection.add(parser.apply(new ScopedStringReader(value)));
			reader.skipWhitespaces();
		}
		return List.copyOf(collection);
	}
	
	/**
	 * Reads a list from the string.<br>
	 * The list is defined by square brackets.<br>
	 *
	 * @param parser The parser to parse the elements
	 * @return The read list as an {@link ArrayList}
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the parser is null
	 * @throws InvalidStringException If an error occurs while reading the list
	 * @see #readCollection(StringScope, Function)
	 */
	public <T> @NonNull List<T> readList(@NonNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(parser, "List element parser must not be null");
		if (!this.canRead()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(this.readCollection(SQUARE_BRACKETS, parser));
	}
	
	/**
	 * Reads a set from the string.<br>
	 * The set is defined by parentheses.<br>
	 * <p>
	 *     The order of the elements is not guaranteed.<br>
	 *     Duplicates are ignored.
	 * </p>
	 *
	 * @param parser The parser to parse the elements
	 * @return The read set as an {@link HashSet}
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the parser is null
	 * @throws InvalidStringException If an error occurs while reading the set
	 * @see #readCollection(StringScope, Function)
	 */
	public <T> @NonNull Set<T> readSet(@NonNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(parser, "Set element parser must not be null");
		if (!this.canRead()) {
			return new HashSet<>();
		}
		return new HashSet<>(this.readCollection(PARENTHESES, parser));
	}
	
	/**
	 * Reads a map from the string.<br>
	 * The map is defined by curly brackets.<br>
	 * <p>
	 *     The key and value are separated by an equal sign ('=').<br>
	 *     The entries are separated by a comma.
	 * </p>
	 * <p>
	 *     If a key is defined multiple times, the last value is used.
	 * </p>
	 *
	 * @param keyParser The parser to parse the keys
	 * @param valueParser The parser to parse the values
	 * @return The read map as a {@link HashMap}
	 * @param <K> The type of the keys
	 * @param <V> The type of the values
	 * @throws NullPointerException If the key or value parser is null
	 * @throws InvalidStringException If an error occurs while reading the map
	 * @see #readCollection(StringScope, Function)
	 */
	public <K, V> @NonNull Map<K, V> readMap(@NonNull Function<ScopedStringReader, K> keyParser, @NonNull Function<ScopedStringReader, V> valueParser) {
		Objects.requireNonNull(keyParser, "Map key parser must not be null");
		Objects.requireNonNull(valueParser, "Map value parser must not be null");
		if (!this.canRead()) {
			return new HashMap<>();
		}
		
		Map<K, V> map = new HashMap<>();
		Collection<String> entries = this.readCollection(CURLY_BRACKETS, reader -> reader.readUntilSkip(',').strip());
		for (String entry : entries) {
			if (!entry.contains("=")) {
				throw new InvalidStringException("Invalid map entry, expected 'key=value' but got: '" + entry + "'");
			}
			
			ScopedStringReader reader = new ScopedStringReader(entry);
			K key = keyParser.apply(new ScopedStringReader(reader.readUntilSkip('=').strip()));
			V value = valueParser.apply(new ScopedStringReader(reader.readRemaining().strip()));
			map.put(key, value);
		}
		return map;
	}
}
