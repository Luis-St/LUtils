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

import net.luis.utils.exception.InvalidStringException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A utility class to read strings with scopes.<br>
 * A scope is defined by two characters, an opening and a closing character.<br>
 *
 * @author Luis-St
 */
public class ScopedStringReader extends StringReader {
	
	/**
	 * A map that stores all created scopes.<br>
	 * The key is the opening character and the value is the closing character.<br>
	 */
	private static final Map<Character, Character> SCOPE_REGISTRY = new HashMap<>();
	
	/**
	 * Constant string scope for parentheses.<br>
	 */
	public static final StringScope PARENTHESES = new StringScope('(', ')');
	/**
	 * Constant string scope for curly brackets.<br>
	 */
	public static final StringScope CURLY_BRACKETS = new StringScope('{', '}');
	/**
	 * Constant string scope for square brackets.<br>
	 */
	public static final StringScope SQUARE_BRACKETS = new StringScope('[', ']');
	/**
	 * Constant string scope for angle brackets.<br>
	 */
	public static final StringScope ANGLE_BRACKETS = new StringScope('<', '>');
	
	/**
	 * Constructs a new scoped string reader with the given string.<br>
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public ScopedStringReader(@NotNull String string) {
		super(string);
	}
	
	/**
	 * Constructs a new scoped string reader with the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ScopedStringReader(@NotNull Reader reader) {
		super(reader);
	}
	
	/**
	 * Reads a string with the given scope.<br>
	 * <p>
	 *     The scope is defined by an opening and a closing character.<br>
	 *     The method will read until the opened scope is closed.<br>
	 *     If there are nested scopes, the method will read until all scopes are closed.<br>
	 * </p>
	 * <p>
	 *     The opening and closing character are included in the returned string.<br>
	 *     If there are no more characters to read, an empty string is returned.<br>
	 * </p>
	 * @param scope The scope to use
	 * @return The read string
	 * @throws NullPointerException If the scope is null
	 * @throws IllegalArgumentException If the next character is not the opening character of the scope
	 * @throws InvalidStringException If the scope is invalid
	 */
	public @NotNull String readScope(@NotNull StringScope scope) {
		Objects.requireNonNull(scope, "Scope must not be null");
		if (!this.canRead()) {
			return "";
		}
		char next = this.peek();
		if (next != scope.open) {
			throw new IllegalArgumentException("Expected '" + scope.open + "' but got '" + next + "'");
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
				if (c == scope.open) {
					depth++;
				} else if (c == scope.close) {
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
			throw new InvalidStringException("Invalid scope, " + depth + " scope" + (depth > 1 ? "s" : "") + " are not closed, expected '" + scope.close + "'");
		} else if (depth < 0) {
			throw new InvalidStringException("Invalid scope, " + -depth + " scope" + (-depth > 1 ? "s" : "") + " are not opened, expected '" + scope.open + "'");
		}
		return builder.toString();
	}
	
	//region Read until
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The terminator and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The read string
	 * @throws IllegalArgumentException If the terminator is a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NotNull String readUntil(char terminator) {
		return super.readUntil(terminator);
	}
	
	/**
	 * Reads the string until the given terminator is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminator to read until
	 * @return The read string
	 * @throws IllegalArgumentException If the terminator is a backslash
	 * @throws InvalidStringException If the scope is invalid
	 */
	@Override
	public @NotNull String readUntilInclusive(char terminator) {
		return super.readUntilInclusive(terminator);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The terminators and escape character ('\\') are read but not included in the result.
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If none terminator is found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 */
	@Override
	public @NotNull String readUntil(char @NotNull ... terminators) {
		return super.readUntil(terminators);
	}
	
	/**
	 * Reads the string until any of the given terminators is found.<br>
	 * The escape character ('\\') is read but not included in the result.
	 * <p>
	 *     If any terminator is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If any terminator is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If none terminator is found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminators The terminators to read until
	 * @return The string which was read until the terminator
	 * @throws IllegalArgumentException If the terminators are empty or contain a backslash
	 */
	@Override
	public @NotNull String readUntilInclusive(char @NotNull ... terminators) {
		return super.readUntilInclusive(terminators);
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
	@Override
	@ApiStatus.Internal
	@SuppressWarnings("DuplicatedCode")
	protected @NotNull String readUntil(@NotNull Predicate<Character> predicate, boolean inclusive) {
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		Deque<Character> stack = new ArrayDeque<>();
		while (this.canRead()) {
			char c = this.read();
			if (escaped) {
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
				continue;
			} else if (!inSingleQuotes && !inDoubleQuotes && predicate.test(c) && stack.isEmpty()) {
				if (inclusive) {
					builder.append(c);
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
			builder.append(c);
		}
		if (!stack.isEmpty()) {
			String scopes = stack.reversed().stream().map(SCOPE_REGISTRY::get).map(String::valueOf).collect(Collectors.joining("', '", "'", "'"));
			throw new InvalidStringException("Invalid scope, " + stack.size() + " scope" + (stack.size() > 1 ? "s" : "") + " are not closed, expected closing characters in order: " + scopes);
		}
		return builder.toString();
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The terminator string and escape character ('\\') are read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 */
	@Override
	public @NotNull String readUntil(@NotNull String terminator, boolean caseSensitive) {
		return super.readUntil(terminator, caseSensitive);
	}
	
	/**
	 * Reads the string until the given terminator string is found.<br>
	 * The escape character ('\\') is read but not included in the result.<br>
	 * <p>
	 *     If the terminator string is found at the beginning or at the end of the string, an empty string is returned.<br>
	 *     If the terminator string is found in a quoted part or in a scope, the terminator is ignored.<br>
	 *     If the terminator string is not found, the rest of the string is returned.<br>
	 * </p>
	 * @param terminator The terminating string to read until
	 * @param caseSensitive Whether the terminator string should be case-sensitive or not
	 * @return The string which was read until the terminator
	 * @throws NullPointerException If the terminator string is null
	 * @throws IllegalArgumentException If the terminator string is empty or contains a backslash
	 */
	@Override
	public @NotNull String readUntilInclusive(@NotNull String terminator, boolean caseSensitive) {
		return super.readUntilInclusive(terminator, caseSensitive);
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
	@Override
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
		Deque<Character> stack = new ArrayDeque<>();
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
			} else if (SCOPE_REGISTRY.containsKey(c)) {
				stack.push(c);
			} else if (!stack.isEmpty() && SCOPE_REGISTRY.get(stack.peek()) == c) {
				stack.pop();
			}
			if (!inSingleQuotes && !inDoubleQuotes && stack.isEmpty()) {
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
		if (!stack.isEmpty()) {
			String scopes = stack.reversed().stream().map(SCOPE_REGISTRY::get).map(String::valueOf).collect(Collectors.joining("', '", "'", "'"));
			throw new InvalidStringException("Invalid scope, " + stack.size() + " scope" + (stack.size() > 1 ? "s" : "") + " are not closed, expected closing characters in order: " + scopes);
		}
		return builder.toString();
	}
	//endregion
	
	//region Read collections
	
	/**
	 * Reads a collection like object from the string.<br>
	 * <p>
	 *     The collection is defined by an opening and a closing character.<br>
	 *     If there are no more characters to read, an empty collection is returned.<br>
	 *     Whitespace characters are ignored.<br>
	 * </p>
	 * <p>
	 *     The elements of the collection are separated by a comma.<br>
	 *     The elements are parsed by the given parser.<br>
	 * </p>
	 * @param scope The scope with the opening and closing character
	 * @param parser The parser to parse the elements
	 * @return The read collection
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the scope or parser is null
	 * @throws IllegalArgumentException If an error occurs while reading the collection
	 */
	private <T> @NotNull Collection<T> readCollection(@NotNull StringScope scope, @NotNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(scope, "Scope must not be null");
		Objects.requireNonNull(parser, "Parser must not be null");
		ScopedStringReader reader = new ScopedStringReader(this.readScope(scope));
		char next = reader.peek();
		if (next != scope.open) {
			throw new InvalidStringException("Expected '" + scope.open + "' but got '" + next + "'");
		}
		reader.skip();
		reader.skipWhitespaces();
		List<T> collection = new ArrayList<>();
		while (reader.canRead()) {
			String value = reader.readUntil(',').strip();
			if (value.isEmpty()) {
				continue;
			}
			if (value.endsWith("" + scope.close)) {
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
	 * @param parser The parser to parse the elements
	 * @return The read list as an {@link ArrayList}
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the parser is null
	 * @throws InvalidStringException If an error occurs while reading the list
	 * @see #readCollection(StringScope, Function)
	 */
	public <T> @NotNull List<T> readList(@NotNull Function<ScopedStringReader, T> parser) {
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
	 *     Duplicates are ignored.<br>
	 * </p>
	 * @param parser The parser to parse the elements
	 * @return The read set as an {@link HashSet}
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the parser is null
	 * @throws InvalidStringException If an error occurs while reading the set
	 * @see #readCollection(StringScope, Function)
	 */
	public <T> @NotNull Set<T> readSet(@NotNull Function<ScopedStringReader, T> parser) {
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
	 *     The entries are separated by a comma.<br>
	 * </p>
	 * <p>
	 *     If a key is defined multiple times, the last value is used.<br>
	 * </p>
	 * @param keyParser The parser to parse the keys
	 * @param valueParser The parser to parse the values
	 * @return The read map as a {@link HashMap}
	 * @param <K> The type of the keys
	 * @param <V> The type of the values
	 * @throws NullPointerException If the key or value parser is null
	 * @throws InvalidStringException If an error occurs while reading the map
	 * @see #readCollection(StringScope, Function)
	 */
	public <K, V> @NotNull Map<K, V> readMap(@NotNull Function<ScopedStringReader, K> keyParser, @NotNull Function<ScopedStringReader, V> valueParser) {
		Objects.requireNonNull(keyParser, "Map key parser must not be null");
		Objects.requireNonNull(valueParser, "Map value parser must not be null");
		if (!this.canRead()) {
			return new HashMap<>();
		}
		Map<K, V> map = new HashMap<>();
		Collection<String> entries = this.readCollection(CURLY_BRACKETS, reader -> reader.readUntil(',').strip());
		for (String entry : entries) {
			if (!entry.contains("=")) {
				throw new InvalidStringException("Invalid map entry, expected 'key=value' but got: '" + entry + "'");
			}
			ScopedStringReader reader = new ScopedStringReader(entry);
			K key = keyParser.apply(new ScopedStringReader(reader.readUntil('=').strip()));
			V value = valueParser.apply(new ScopedStringReader(reader.readRemaining().strip()));
			map.put(key, value);
		}
		return map;
	}
	//endregion
	
	/**
	 * A record to define a string scope with an opening and a closing character.<br>
	 *
	 * @author Luis-St
	 *
	 * @param open The opening character of the scope
	 * @param close The closing character of the scope
	 */
	public record StringScope(char open, char close) {
		
		/**
		 * Constructs a new string scope with the given opening and closing character.<br>
		 * @param open The opening character
		 * @param close The closing character
		 * @throws IllegalArgumentException If the opening and closing character are the same
		 */
		public StringScope {
			if (open == close) {
				throw new IllegalArgumentException("Opening and closing character must not be the same");
			}
			if (open == '\0' || close == '\0') {
				throw new IllegalArgumentException("Opening and closing character must not be the null character");
			}
			SCOPE_REGISTRY.put(open, close);
		}
	}
}
