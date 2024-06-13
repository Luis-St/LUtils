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
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
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
	 * @throws IllegalStateException If the scope is invalid
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
			throw new IllegalStateException("Invalid scope, " + depth + " scope" + (depth > 1 ? "s" : "") + " are not closed, expected '" + scope.close + "'");
		} else if (depth < 0) {
			throw new IllegalStateException("Invalid scope, " + -depth + " scope" + (-depth > 1 ? "s" : "") + " are not opened, expected '" + scope.open + "'");
		}
		return builder.toString();
	}
	
	@Override
	public @NotNull String readUntil(char terminator) {
		if (terminator == '\\') {
			throw new IllegalArgumentException("Terminator cannot be a backslash");
		}
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
			} else if (!inSingleQuotes && !inDoubleQuotes && c == terminator && stack.isEmpty()) {
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
			throw new IllegalStateException("Invalid scope, " + stack.size() + " scope" + (stack.size() > 1 ? "s" : "") + " are not closed, expected closing characters in order: " + scopes);
		}
		return builder.toString();
	}
	
	@Unmodifiable
	private <T> @NotNull Collection<T> readCollection(@NotNull StringScope scope, @NotNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(parser, "Parser must not be null");
		ScopedStringReader reader = new ScopedStringReader(this.readScope(scope));
		char next = reader.peek();
		if (next != scope.open) {
			throw new IllegalArgumentException("Expected '" + scope.open + "' but got '" + next + "'");
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
	
	public <T> @NotNull List<T> readList(@NotNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(parser, "List element parser must not be null");
		if (!this.canRead()) {
			return new ArrayList<>();
		}
		return new ArrayList<>(this.readCollection(SQUARE_BRACKETS, parser));
	}
	
	public <T> @NotNull Set<T> readSet(@NotNull Function<ScopedStringReader, T> parser) {
		Objects.requireNonNull(parser, "Set element parser must not be null");
		if (!this.canRead()) {
			return new HashSet<>();
		}
		return new HashSet<>(this.readCollection(PARENTHESES, parser));
	}
	
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
				throw new IllegalStateException("Invalid map entry, expected key=value but got: '" + entry + "'");
			}
			ScopedStringReader reader = new ScopedStringReader(entry);
			K key = keyParser.apply(new ScopedStringReader(reader.readUntil('=').strip()));
			V value = valueParser.apply(new ScopedStringReader(reader.readRemaining().strip()));
			map.put(key, value);
		}
		return map;
	}
	
	/**
	 * A record to define a string scope with an opening and a closing character.<br>
	 *
	 * @author Luis-St
	 *
	 * @param open The opening character of the scope
	 * @param close The closing character of the scope
	 */
	public static record StringScope(char open, char close) {
		
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
