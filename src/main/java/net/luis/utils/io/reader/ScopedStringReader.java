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

import com.google.common.collect.*;
import net.luis.utils.exception.ConcurrentInteractionException;
import net.luis.utils.exception.InvalidStringException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.luis.utils.io.reader.StringScope.*;

/**
 * A string reader which handles scopes and reads collections.<br>
 * A scope is defined by two characters, an opening and a closing character.<br>
 * <p>
 *     The handled scopes are defined on construction.<br>
 *     Methods which read until a terminator ignore terminators inside quoted parts and inside handled scopes.
 * </p>
 * <p>
 *     The elements of collections are read by sub readers of the same string,<br>
 *     so the positions of the elements are the positions in the original string.<br>
 *     The type of the element readers can be configured for each collection.
 * </p>
 *
 * @author Luis-St
 */
public class ScopedStringReader extends ValueStringReader {
	
	/**
	 * The scopes which are handled by default.<br>
	 */
	public static final Set<StringScope> DEFAULT_SCOPES = Set.of(PARENTHESES, CURLY_BRACKETS, SQUARE_BRACKETS, ANGLE_BRACKETS);
	
	/**
	 * The scopes handled by this reader, mapped by their opening character.<br>
	 */
	private final Map<Character, StringScope> scopes;
	
	/**
	 * Constructs a new scoped string reader with the given string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character}, the {@link StringQuoting#DEFAULT default quoting} and the {@link #DEFAULT_SCOPES default scopes}.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public ScopedStringReader(@NonNull String string) {
		this(string, DEFAULT_ESCAPE_CHARACTER, StringQuoting.DEFAULT);
	}
	
	/**
	 * Constructs a new scoped string reader with the given string, escape character and quoting.<br>
	 * The reader uses the {@link #DEFAULT_SCOPES default scopes}.<br>
	 *
	 * @param string The string to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @throws NullPointerException If the string or quoting is null
	 */
	public ScopedStringReader(@NonNull String string, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting) {
		this(string, escapeCharacter, quoting, DEFAULT_SCOPES);
	}
	
	/**
	 * Constructs a new scoped string reader with the given string, escape character, quoting and scopes.<br>
	 *
	 * @param string The string to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @param scopes The scopes handled by the reader
	 * @throws NullPointerException If the string, quoting, the set of scopes or any scope is null
	 * @throws IllegalArgumentException If two scopes have the same opening character
	 */
	public ScopedStringReader(@NonNull String string, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting, @NonNull Set<StringScope> scopes) {
		super(string, escapeCharacter, quoting);
		this.scopes = createScopes(scopes);
	}
	
	/**
	 * Constructs a new scoped string reader from the given reader.<br>
	 * The reader is closed after reading the string.<br>
	 * The reader uses the {@link #DEFAULT_ESCAPE_CHARACTER default escape character}, the {@link StringQuoting#DEFAULT default quoting} and the {@link #DEFAULT_SCOPES default scopes}.<br>
	 *
	 * @param reader The reader to read from
	 * @throws NullPointerException If the reader is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ScopedStringReader(@NonNull Reader reader) {
		this(reader, DEFAULT_ESCAPE_CHARACTER, StringQuoting.DEFAULT);
	}
	
	/**
	 * Constructs a new scoped string reader from the given reader with the given escape character and quoting.<br>
	 * The reader is closed after reading the string.<br>
	 * The reader uses the {@link #DEFAULT_SCOPES default scopes}.<br>
	 *
	 * @param reader The reader to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @throws NullPointerException If the reader or quoting is null
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ScopedStringReader(@NonNull Reader reader, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting) {
		this(reader, escapeCharacter, quoting, DEFAULT_SCOPES);
	}
	
	/**
	 * Constructs a new scoped string reader from the given reader with the given escape character, quoting and scopes.<br>
	 * The reader is closed after reading the string.<br>
	 *
	 * @param reader The reader to read from
	 * @param escapeCharacter The escape character, or null to disable escaping
	 * @param quoting The quotes handled by the reader
	 * @param scopes The scopes handled by the reader
	 * @throws NullPointerException If the reader, quoting, the set of scopes or any scope is null
	 * @throws IllegalArgumentException If two scopes have the same opening character
	 * @throws UncheckedIOException If an I/O error occurs while reading the string
	 */
	public ScopedStringReader(@NonNull Reader reader, @Nullable Character escapeCharacter, @NonNull StringQuoting quoting, @NonNull Set<StringScope> scopes) {
		super(reader, escapeCharacter, quoting);
		this.scopes = createScopes(scopes);
	}
	
	/**
	 * Maps the given scopes by their opening character.<br>
	 *
	 * @param scopes The scopes
	 * @return An unmodifiable map of the scopes by their opening character
	 * @throws NullPointerException If the set of scopes or any scope is null
	 * @throws IllegalArgumentException If two scopes have the same opening character
	 */
	private static @NonNull Map<Character, StringScope> createScopes(@NonNull Set<StringScope> scopes) {
		Objects.requireNonNull(scopes, "Scopes must not be null");
		
		Map<Character, StringScope> map = Maps.newLinkedHashMap();
		for (StringScope scope : scopes) {
			Objects.requireNonNull(scope, "Scope must not be null");
			if (map.put(scope.open(), scope) != null) {
				throw new IllegalArgumentException("Scopes must have unique opening characters, but found '" + scope.open() + "' multiple times");
			}
		}
		return Collections.unmodifiableMap(map);
	}
	
	/**
	 * Returns the scopes handled by this reader.<br>
	 * @return An unmodifiable set of the scopes
	 */
	public @NonNull Set<StringScope> getScopes() {
		return Set.copyOf(this.scopes.values());
	}
	
	@Override
	protected @NonNull NestingTracker createTracker() {
		return new ScopeTracker(this.getQuoting(), this.scopes);
	}
	
	/**
	 * Creates the reader which reads an element of a collection.<br>
	 * The created reader uses the same escape character, quoting and scopes as this reader.<br>
	 * Subclasses can override this method to change the default element reader.<br>
	 *
	 * @param string The string to read from
	 * @return The created reader
	 * @throws NullPointerException If the string is null
	 */
	protected @NonNull ScopedStringReader createElementReader(@NonNull String string) {
		return new ScopedStringReader(string, this.getEscapeCharacter(), this.getQuoting(), this.getScopes());
	}
	
	/**
	 * Reads a string with the given scope.<br>
	 * <p>
	 *     The scope is defined by an opening and a closing character.<br>
	 *     The method will read until the opened scope is closed.<br>
	 *     If there are nested scopes of the same type, the method will read until all of them are closed.<br>
	 *     The scope does not need to be handled by this reader.
	 * </p>
	 * <p>
	 *     Scope characters in quoted parts and escaped scope characters are ignored.<br>
	 *     The opening and closing character and escape characters are included in the returned string.<br>
	 *     If there are no more characters to read, an empty string is returned.
	 * </p>
	 *
	 * @param scope The scope to use
	 * @return The read string
	 * @throws NullPointerException If the scope is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not the opening character of the scope
	 * @throws InvalidStringException If the scope is not closed
	 */
	public @NonNull String readScope(@NonNull StringScope scope) {
		Objects.requireNonNull(scope, "Scope must not be null");
		this.ensureUsable();
		if (!this.canRead()) {
			return "";
		}
		
		char next = this.peek();
		if (next != scope.open()) {
			throw new IllegalArgumentException("Expected '" + scope.open() + "' but got '" + next + "'");
		}
		
		NestingTracker quotes = new QuoteTracker(this.getQuoting());
		StringBuilder builder = new StringBuilder();
		int depth = 0;
		boolean escaped = false;
		while (this.canRead()) {
			char c = this.read();
			builder.append(c);
			if (escaped) {
				escaped = false;
				continue;
			}
			if (this.isEscapeCharacter(c)) {
				escaped = true;
				continue;
			}
			
			if (!quotes.isNested()) {
				if (c == scope.open()) {
					depth++;
				} else if (c == scope.close() && --depth == 0) {
					return builder.toString();
				}
			}
			quotes.accept(c);
		}
		throw new InvalidStringException(this.withLocation("Invalid scope, " + depth + " scope" + (depth > 1 ? "s are" : " is") + " not closed, expected '" + scope.close() + "'"));
	}
	
	/**
	 * Reads the next value as the best matching object.<br>
	 * In addition to the types supported by ,<br>
	 * this method can read collections, which are determined by their opening character:<br>
	 * <ul>
	 *     <li>
	 *         <b>Lists</b>:<br>
	 *         If the next character is {@code '['}, a {@link List} is returned (see {@link #readList(Function)}).
	 *     </li>
	 *     <li>
	 *         <b>Sets</b>:<br>
	 *         If the next character is {@code '('}, a {@link Set} is returned (see {@link #readSet(Function)}).
	 *     </li>
	 *     <li>
	 *         <b>Maps</b>:<br>
	 *         If the next character is <code>'{'</code>, a {@link Map} is returned (see {@link #readMap(Function, Function)}).
	 *     </li>
	 * </ul>
	 * <p>
	 *     The elements of lists and sets as well as the keys and values of maps are read recursively using this method.<br>
	 *     Therefore, collections can be nested at any depth (e.g., a list of maps containing sets).
	 * </p>
	 *
	 * @return The object which was read
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws StringIndexOutOfBoundsException If there are no more characters to read
	 * @throws InvalidStringException If a collection is invalid or its scope is not closed
	 * @see #readList(Function)
	 * @see #readSet(Function)
	 * @see #readMap(Function, Function)
	 */
	@Override
	public @NonNull Object readObject() {
		this.ensureUsable();
		if (!this.canRead()) {
			throw new StringIndexOutOfBoundsException(this.withLocation("Expected a value but found nothing"));
		}
		
		char next = this.peek();
		if (next == SQUARE_BRACKETS.open()) {
			return this.readList(ScopedStringReader::readObject);
		}
		if (next == PARENTHESES.open()) {
			return this.readSet(ScopedStringReader::readObject);
		}
		if (next == CURLY_BRACKETS.open()) {
			return this.readMap(ScopedStringReader::readObject, ScopedStringReader::readObject);
		}
		return super.readObject();
	}
	
	/**
	 * Reads a list from the string.<br>
	 * The elements are read by readers created by {@link #createElementReader(String)}.<br>
	 *
	 * @param parser The parser to parse the elements
	 * @return The read list as an {@link ArrayList}
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not an opening square bracket
	 * @throws InvalidStringException If an error occurs while reading the list
	 * @see #readList(Function, Function)
	 */
	public <T> @NonNull List<T> readList(@NonNull Function<ScopedStringReader, T> parser) {
		return this.readList(this::createElementReader, parser);
	}
	
	/**
	 * Reads a list from the string.<br>
	 * <p>
	 *     The list is defined by square brackets.<br>
	 *     The elements are separated by a comma, empty elements and surrounding whitespaces are ignored.
	 * </p>
	 * <p>
	 *     Each element is read by a sub reader created by the given factory.<br>
	 *     The sub reader can only read the characters of its element.
	 * </p>
	 *
	 * @param factory The factory which creates the element readers from the string of this reader
	 * @param parser The parser to parse the elements
	 * @return The read list as an {@link ArrayList}
	 * @param <R> The type of the element readers
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the factory or parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not an opening square bracket or the factory does not create a new reader of the same string
	 * @throws InvalidStringException If an error occurs while reading the list
	 */
	public <R extends StringReader, T> @NonNull List<T> readList(@NonNull Function<String, R> factory, @NonNull Function<? super R, T> parser) {
		return Lists.newArrayList(this.readCollection(SQUARE_BRACKETS, factory, parser));
	}
	
	/**
	 * Reads a set from the string.<br>
	 * The elements are read by readers created by {@link #createElementReader(String)}.<br>
	 *
	 * @param parser The parser to parse the elements
	 * @return The read set as a {@link LinkedHashSet}
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not an opening parenthesis
	 * @throws InvalidStringException If an error occurs while reading the set
	 * @see #readSet(Function, Function)
	 */
	public <T> @NonNull Set<T> readSet(@NonNull Function<ScopedStringReader, T> parser) {
		return this.readSet(this::createElementReader, parser);
	}
	
	/**
	 * Reads a set from the string.<br>
	 * <p>
	 *     The set is defined by parentheses.<br>
	 *     The elements are separated by a comma, empty elements and surrounding whitespaces are ignored.<br>
	 *     Duplicates are ignored, the order of the elements is kept.
	 * </p>
	 * <p>
	 *     Each element is read by a sub reader created by the given factory.<br>
	 *     The sub reader can only read the characters of its element.
	 * </p>
	 *
	 * @param factory The factory which creates the element readers from the string of this reader
	 * @param parser The parser to parse the elements
	 * @return The read set as a {@link LinkedHashSet}
	 * @param <R> The type of the element readers
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the factory or parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not an opening parenthesis or the factory does not create a new reader of the same string
	 * @throws InvalidStringException If an error occurs while reading the set
	 */
	public <R extends StringReader, T> @NonNull Set<T> readSet(@NonNull Function<String, R> factory, @NonNull Function<? super R, T> parser) {
		return Sets.newLinkedHashSet(this.readCollection(PARENTHESES, factory, parser));
	}
	
	/**
	 * Reads a map from the string.<br>
	 * The keys and values are read by readers created by {@link #createElementReader(String)}.<br>
	 *
	 * @param keyParser The parser to parse the keys
	 * @param valueParser The parser to parse the values
	 * @return The read map as a {@link LinkedHashMap}
	 * @param <K> The type of the keys
	 * @param <V> The type of the values
	 * @throws NullPointerException If the key or value parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not an opening curly bracket
	 * @throws InvalidStringException If an error occurs while reading the map
	 * @see #readMap(Function, Function, Function)
	 */
	public <K, V> @NonNull Map<K, V> readMap(@NonNull Function<ScopedStringReader, K> keyParser, @NonNull Function<ScopedStringReader, V> valueParser) {
		return this.readMap(this::createElementReader, keyParser, valueParser);
	}
	
	/**
	 * Reads a map from the string.<br>
	 * <p>
	 *     The map is defined by curly brackets.<br>
	 *     The entries are separated by a comma, empty entries and surrounding whitespaces are ignored.<br>
	 *     The key and value of an entry are separated by the first equal sign ('=') outside of nested parts.<br>
	 *     If a key is defined multiple times, the last value is used.
	 * </p>
	 * <p>
	 *     Each key and value is read by a sub reader created by the given factory.<br>
	 *     The sub reader can only read the characters of its key or value.
	 * </p>
	 *
	 * @param factory The factory which creates the key and value readers from the string of this reader
	 * @param keyParser The parser to parse the keys
	 * @param valueParser The parser to parse the values
	 * @return The read map as a {@link LinkedHashMap}
	 * @param <R> The type of the key and value readers
	 * @param <K> The type of the keys
	 * @param <V> The type of the values
	 * @throws NullPointerException If the factory, key or value parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not an opening curly bracket or the factory does not create a new reader of the same string
	 * @throws InvalidStringException If an error occurs while reading the map or an entry has no equal sign
	 */
	public <R extends StringReader, K, V> @NonNull Map<K, V> readMap(@NonNull Function<String, R> factory, @NonNull Function<? super R, K> keyParser, @NonNull Function<? super R, V> valueParser) {
		Objects.requireNonNull(factory, "Map reader factory must not be null");
		Objects.requireNonNull(keyParser, "Map key parser must not be null");
		Objects.requireNonNull(valueParser, "Map value parser must not be null");
		this.ensureUsable();
		
		List<Range> entries = this.readElementRanges(CURLY_BRACKETS);
		int end = this.getIndex();
		
		Map<K, V> map = Maps.newLinkedHashMap();
		for (Range entry : entries) {
			int separator = this.indexOf(entry.start(), entry.end(), '=');
			if (separator == -1) {
				this.setIndex(entry.start());
				throw new InvalidStringException(this.withLocation("Invalid map entry, expected 'key=value' but got: '" + entry.of(this.getString()) + "'"));
			}
			
			K key = this.readRange(this.trim(entry.start(), separator), factory, keyParser);
			V value = this.readRange(this.trim(separator + 1, entry.end()), factory, valueParser);
			map.put(key, value);
		}
		this.setIndex(end);
		return map;
	}
	
	/**
	 * Reads a collection like object from the string.<br>
	 *
	 * @param scope The scope with the opening and closing character
	 * @param factory The factory which creates the element readers
	 * @param parser The parser to parse the elements
	 * @return The read elements in order
	 * @param <R> The type of the element readers
	 * @param <T> The type of the elements
	 * @throws NullPointerException If the scope, factory or parser is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not the opening character of the scope
	 * @throws InvalidStringException If an error occurs while reading the collection
	 */
	private <R extends StringReader, T> @NonNull List<T> readCollection(@NonNull StringScope scope, @NonNull Function<String, R> factory, @NonNull Function<? super R, T> parser) {
		Objects.requireNonNull(scope, "Scope must not be null");
		Objects.requireNonNull(factory, "Element reader factory must not be null");
		Objects.requireNonNull(parser, "Element parser must not be null");
		this.ensureUsable();
		
		List<Range> elements = this.readElementRanges(scope);
		int end = this.getIndex();
		
		List<T> collection = new ArrayList<>();
		for (Range element : elements) {
			collection.add(this.readRange(element, factory, parser));
		}
		this.setIndex(end);
		return collection;
	}
	
	/**
	 * Reads the given scope and splits its content into the ranges of its comma separated elements.<br>
	 * The reader is positioned after the closing character of the scope.<br>
	 *
	 * @param scope The scope of the collection
	 * @return The trimmed ranges of the non-empty elements
	 * @throws NullPointerException If the scope is null
	 * @throws ConcurrentInteractionException If a child reader is active
	 * @throws IllegalStateException If this reader is a closed sub reader
	 * @throws IllegalArgumentException If the next character is not the opening character of the scope
	 * @throws InvalidStringException If there are no more characters to read or the scope is invalid
	 */
	private @NonNull List<Range> readElementRanges(@NonNull StringScope scope) {
		Objects.requireNonNull(scope, "Scope must not be null");
		if (!this.canRead()) {
			throw new InvalidStringException(this.withLocation("Expected '" + scope.open() + "' but found nothing"));
		}
		
		int start = this.getIndex();
		this.readScope(scope);
		int end = this.getIndex() - 1;
		
		List<Range> ranges = new ArrayList<>();
		int partStart = start + 1;
		while (partStart <= end) {
			int separator = this.indexOf(partStart, end, ',');
			int partEnd = separator == -1 ? end : separator;
			
			Range range = this.trim(partStart, partEnd);
			if (range.end() > range.start()) {
				ranges.add(range);
			}
			partStart = partEnd + 1;
		}
		return ranges;
	}
	
	/**
	 * Finds the first occurrence of the given separator in the given range of the string.<br>
	 * Escaped separators and separators in nested parts are ignored.<br>
	 *
	 * @param from The inclusive start index of the range
	 * @param to The exclusive end index of the range
	 * @param separator The separator to find
	 * @return The index of the separator, or -1 if the separator is not found
	 * @throws InvalidStringException If a nested part in the range is not closed
	 */
	private int indexOf(int from, int to, char separator) {
		NestingTracker tracker = this.createTracker();
		boolean escaped = false;
		for (int i = from; i < to; i++) {
			char c = this.getString().charAt(i);
			if (escaped) {
				escaped = false;
			} else if (this.isEscapeCharacter(c)) {
				escaped = true;
			} else if (!tracker.isNested() && c == separator) {
				return i;
			} else {
				tracker.accept(c);
			}
		}
		tracker.finish();
		return -1;
	}
	
	/**
	 * Removes the leading and trailing whitespaces from the given range of the string.<br>
	 *
	 * @param from The inclusive start index of the range
	 * @param to The exclusive end index of the range
	 * @return The trimmed range
	 */
	private @NonNull Range trim(int from, int to) {
		String string = this.getString();
		while (from < to && Character.isWhitespace(string.charAt(from))) {
			from++;
		}
		while (to > from && Character.isWhitespace(string.charAt(to - 1))) {
			to--;
		}
		return new Range(from, to);
	}
	
	/**
	 * Parses the given range of the string with a sub reader created by the given factory.<br>
	 *
	 * @param range The range to parse
	 * @param factory The factory which creates the sub reader
	 * @param parser The parser
	 * @return The parsed value
	 * @param <R> The type of the sub reader
	 * @param <T> The type of the value
	 * @throws NullPointerException If the range, factory or parser is null
	 * @throws IllegalArgumentException If the factory does not create a new reader of the same string
	 */
	private <R extends StringReader, T> T readRange(@NonNull Range range, @NonNull Function<String, R> factory, @NonNull Function<? super R, T> parser) {
		Objects.requireNonNull(range, "Range must not be null");
		Objects.requireNonNull(factory, "Factory must not be null");
		Objects.requireNonNull(parser, "Parser must not be null");
		
		this.setIndex(range.start());
		try (R reader = this.createSubReader(range.end() - range.start(), factory)) {
			return parser.apply(reader);
		}
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) return false;
		ScopedStringReader that = (ScopedStringReader) o;
		
		return this.scopes.equals(that.scopes);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), this.scopes);
	}
	//endregion
	
	/**
	 * A tracker for quoted parts and scopes of a string.<br>
	 * Scope characters inside quoted parts are ignored.<br>
	 *
	 * @author Luis-St
	 */
	protected static class ScopeTracker extends QuoteTracker {
		
		/**
		 * The tracked scopes, mapped by their opening character.<br>
		 */
		private final Map<Character, StringScope> scopes;
		/**
		 * The currently open scopes, the innermost scope is on top.<br>
		 */
		private final Deque<StringScope> openScopes = new ArrayDeque<>();
		
		/**
		 * Constructs a new scope tracker for the given quotes and scopes.<br>
		 *
		 * @param quoting The quotes to track
		 * @param scopes The scopes to track, mapped by their opening character
		 * @throws NullPointerException If the quoting or the map of scopes is null
		 */
		protected ScopeTracker(@NonNull StringQuoting quoting, @NonNull Map<Character, StringScope> scopes) {
			super(quoting);
			this.scopes = Objects.requireNonNull(scopes, "Scopes must not be null");
		}
		
		@Override
		public boolean isNested() {
			return this.isInQuote() || !this.openScopes.isEmpty();
		}
		
		@Override
		public void accept(char c) {
			if (this.isInQuote() || this.opensQuote(c)) {
				super.accept(c);
				return;
			}
			
			StringScope scope = this.scopes.get(c);
			if (scope != null) {
				this.openScopes.push(scope);
			} else if (!this.openScopes.isEmpty() && this.openScopes.peek().close() == c) {
				this.openScopes.pop();
			}
		}
		
		@Override
		public void finish() {
			if (!this.openScopes.isEmpty()) {
				int count = this.openScopes.size();
				String closing = this.openScopes.stream().map(scope -> String.valueOf(scope.close())).collect(Collectors.joining("', '", "'", "'"));
				throw new InvalidStringException("Invalid scope, " + count + " scope" + (count > 1 ? "s are" : " is") + " not closed, expected closing characters in order: " + closing);
			}
		}
	}
	
	//region Internal
	
	/**
	 * Internal record to represent a range of the string.<br>
	 *
	 * @param start The inclusive start index
	 * @param end The exclusive end index
	 */
	private record Range(int start, int end) {
		
		/**
		 * Returns the part of the given string which is covered by this range.<br>
		 *
		 * @param string The string
		 * @return The covered part of the string
		 */
		public @NonNull String of(@NonNull String string) {
			return string.substring(this.start, this.end);
		}
	}
}
