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

package net.luis.utils.io.data.toon;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.toon.exception.ToonSyntaxException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a reader for toon files.<br>
 * This reader reads the toon content from a defined input and returns it as a toon element.<br>
 * <p>
 *     Supports TOON format including:
 * </p>
 * <ul>
 *     <li>Key-value pairs with type inference</li>
 *     <li>Nested objects via indentation</li>
 *     <li>Inline primitive arrays</li>
 *     <li>Tabular arrays</li>
 *     <li>Expanded list arrays</li>
 *     <li>Quoted and unquoted strings</li>
 *     <li>Path expansion for dotted keys</li>
 * </ul>
 *
 * @author Luis-St
 */
public class ToonReader implements AutoCloseable {
	
	/**
	 * Pattern for inline array header: key[N,]: or key[N\t]: or key[N|]:<br>
	 */
	private static final Pattern INLINE_ARRAY_PATTERN = Pattern.compile("^\\[(\\d+)([,\\t|])]$");
	
	/**
	 * Pattern for tabular array header: key[N,]{field1, field2}: or similar.<br>
	 */
	private static final Pattern TABULAR_ARRAY_PATTERN = Pattern.compile("^\\[(\\d+)([,\\t|])]\\{(.+)}$");
	
	/**
	 * Pattern for expanded list array header: key[N]:<br>
	 */
	private static final Pattern EXPANDED_ARRAY_PATTERN = Pattern.compile("^\\[(\\d+)]$");
	
	/**
	 * The configuration for this reader.<br>
	 */
	private final ToonConfig config;
	
	/**
	 * The lines of the toon content.<br>
	 */
	private final List<String> lines;
	
	/**
	 * The current line index.<br>
	 */
	private int lineIndex;
	
	/**
	 * Constructs a new toon reader with the given string and the default configuration.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public ToonReader(@NonNull String string) {
		this(string, ToonConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new toon reader with the given string and configuration.<br>
	 *
	 * @param string The string to read from
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the string or the configuration is null
	 */
	public ToonReader(@NonNull String string, @NonNull ToonConfig config) {
		this.config = Objects.requireNonNull(config, "Toon config must not be null");
		this.lines = splitLines(Objects.requireNonNull(string, "String must not be null"));
		this.lineIndex = 0;
	}
	
	/**
	 * Constructs a new toon reader with the given input and the default configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public ToonReader(@NonNull InputProvider input) {
		this(input, ToonConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new toon reader with the given input and configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the input or the configuration is null
	 */
	public ToonReader(@NonNull InputProvider input, @NonNull ToonConfig config) {
		this.config = Objects.requireNonNull(config, "Toon config must not be null");
		Objects.requireNonNull(input, "Input must not be null");
		
		try (InputStreamReader reader = new InputStreamReader(input.getStream(), config.charset())) {
			this.lines = splitLines(readAll(reader));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read toon content", e);
		}
		this.lineIndex = 0;
	}
	
	/**
	 * Reads all content from a reader into a string.<br>
	 *
	 * @param reader The reader to read from
	 * @return The content as a string
	 * @throws NullPointerException If the reader is null
	 */
	@SuppressWarnings("NestedAssignment")
	private static @NonNull String readAll(@NonNull Reader reader) {
		Objects.requireNonNull(reader, "Reader must not be null");
		
		try {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[4096];
			
			int read;
			while ((read = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, read);
			}
			
			return sb.toString();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read toon content", e);
		}
	}
	
	/**
	 * Splits content into lines, preserving empty lines.<br>
	 *
	 * @param content The content to split
	 * @return The list of lines
	 * @throws NullPointerException If the content is null
	 */
	private static @NonNull List<String> splitLines(@NonNull String content) {
		Objects.requireNonNull(content, "Content must not be null");
		if (content.isEmpty()) {
			return new ArrayList<>();
		}
		
		int start = 0;
		List<String> result = new ArrayList<>();
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			
			if (c == '\n') {
				result.add(content.substring(start, i));
				start = i + 1;
			} else if (c == '\r') {
				result.add(content.substring(start, i));
				if (i + 1 < content.length() && content.charAt(i + 1) == '\n') {
					i++;
				}
				start = i + 1;
			}
		}
		
		if (start <= content.length()) {
			String lastLine = content.substring(start);
			if (!lastLine.isEmpty()) {
				result.add(lastLine);
			}
		}
		return result;
	}
	
	/**
	 * Extracts the array header part (before the colon) from a line.<br>
	 *
	 * @param trimmed The trimmed line content
	 * @return The array header part, or empty string if not an array header
	 * @throws NullPointerException If the trimmed string is null
	 */
	private static @NonNull String extractArrayHeader(@NonNull String trimmed) {
		Objects.requireNonNull(trimmed, "Trimmed must not be null");
		if (!trimmed.startsWith("[")) {
			return "";
		}
		
		int headerEnd;
		if (trimmed.contains("}:")) {
			headerEnd = trimmed.indexOf("}:");
			return trimmed.substring(0, headerEnd + 1);
		}
		if (trimmed.contains("]:")) {
			headerEnd = trimmed.indexOf("]:");
			return trimmed.substring(0, headerEnd + 1);
		}
		if (trimmed.endsWith(":")) {
			return trimmed.substring(0, trimmed.length() - 1);
		}
		return "";
	}
	
	/**
	 * Parses a token value, handling quoted strings and type inference.<br>
	 *
	 * @param token The raw token string
	 * @return The parsed toon element
	 * @throws NullPointerException If the token is null
	 */
	private static @NonNull ToonElement parseTokenValue(@NonNull String token) {
		Objects.requireNonNull(token, "Token must not be null");
		
		if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
			String inner = token.substring(1, token.length() - 1);
			return new ToonValue(unescapeString(inner));
		}
		return ToonHelper.inferType(token);
	}
	
	/**
	 * Unescapes a quoted string, processing escape sequences.<br>
	 *
	 * @param str The string to unescape
	 * @return The unescaped string
	 * @throws NullPointerException If the string is null
	 * @throws ToonSyntaxException If an invalid escape sequence is found
	 */
	private static @NonNull String unescapeString(@NonNull String str) {
		Objects.requireNonNull(str, "String must not be null");
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\\' && i + 1 < str.length()) {
				char next = str.charAt(i + 1);
				switch (next) {
					case '\\' -> result.append('\\');
					case '"' -> result.append('"');
					case 'n' -> result.append('\n');
					case 'r' -> result.append('\r');
					case 't' -> result.append('\t');
					default -> throw new ToonSyntaxException("Invalid escape sequence '\\" + next + "'");
				}
				i++;
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}
	
	/**
	 * Finds the index of the colon separator in a key-value line,<br>
	 * skipping colons inside quoted strings and array headers.<br>
	 *
	 * @param content The line content
	 * @return The index of the colon, or -1 if not found
	 * @throws NullPointerException If the content is null
	 */
	private static int findColonIndex(@NonNull String content) {
		Objects.requireNonNull(content, "Content must not be null");
		boolean inQuote = false;
		int bracketDepth = 0;
		int braceDepth = 0;
		
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			
			if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
				inQuote = !inQuote;
			} else if (!inQuote) {
				if (c == '[') {
					bracketDepth++;
				} else if (c == ']') {
					bracketDepth--;
				} else if (c == '{') {
					braceDepth++;
				} else if (c == '}') {
					braceDepth--;
				} else if (c == ':' && bracketDepth == 0 && braceDepth == 0) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Finds the position of the first unquoted '[' in a raw key string.<br>
	 * This is used to detect array headers embedded in key names (e.g., "tags[3,]").<br>
	 *
	 * @param rawKey The raw key string
	 * @return The index of the first unquoted '[', or -1 if not found
	 * @throws NullPointerException If the raw key is null
	 */
	private static int findArrayBracketInKey(@NonNull String rawKey) {
		Objects.requireNonNull(rawKey, "Raw key must not be null");
		
		boolean inQuote = false;
		for (int i = 0; i < rawKey.length(); i++) {
			char c = rawKey.charAt(i);
			
			if (c == '"' && (i == 0 || rawKey.charAt(i - 1) != '\\')) {
				inQuote = !inQuote;
			} else if (c == '[' && !inQuote) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Unquotes a key, removing surrounding quotes if present.<br>
	 *
	 * @param key The raw key string
	 * @return The unquoted key
	 * @throws NullPointerException If the key is null
	 */
	private static @NonNull String unquoteKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		
		if (key.startsWith("\"") && key.endsWith("\"") && key.length() >= 2) {
			return unescapeString(key.substring(1, key.length() - 1));
		}
		return key;
	}
	
	/**
	 * Expands a dotted key into nested objects.<br>
	 *
	 * @param target The target object to add to
	 * @param dottedKey The dotted key path
	 * @param value The value to add
	 * @throws NullPointerException If any parameter is null
	 */
	private static void expandDottedKey(@NonNull ToonObject target, @NonNull String dottedKey, @NonNull ToonElement value) {
		Objects.requireNonNull(target, "Target must not be null");
		Objects.requireNonNull(dottedKey, "Dotted key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		String[] parts = dottedKey.split("\\.");
		if (parts.length == 1) {
			target.add(dottedKey, value);
			return;
		}
		
		ToonObject current = target;
		for (int i = 0; i < parts.length - 1; i++) {
			ToonElement existing = current.get(parts[i]);
			
			if (existing instanceof ToonObject obj) {
				current = obj;
			} else {
				ToonObject newObj = new ToonObject();
				current.add(parts[i], newObj);
				current = newObj;
			}
		}
		current.add(parts[parts.length - 1], value);
	}
	
	/**
	 * Parses a delimiter character from its string representation.<br>
	 *
	 * @param delimStr The delimiter string
	 * @return The delimiter character
	 * @throws NullPointerException If the delimiter string is null
	 * @throws ToonSyntaxException If the delimiter is unknown
	 */
	private static char parseDelimiterChar(@NonNull String delimStr) {
		Objects.requireNonNull(delimStr, "Delimiter string must not be null");
		
		return switch (delimStr) {
			case "," -> ',';
			case "\t" -> '\t';
			case "|" -> '|';
			default -> throw new ToonSyntaxException("Unknown delimiter: " + delimStr);
		};
	}
	
	/**
	 * Splits a string by a delimiter, respecting quoted strings.<br>
	 *
	 * @param str The string to split
	 * @param delim The delimiter character
	 * @return The list of split tokens
	 * @throws NullPointerException If the string is null
	 */
	private static @NonNull List<String> splitByDelimiter(@NonNull String str, char delim) {
		Objects.requireNonNull(str, "String must not be null");
		List<String> result = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean inQuote = false;
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			if (c == '"' && (i == 0 || str.charAt(i - 1) != '\\')) {
				inQuote = !inQuote;
				current.append(c);
			} else if (c == delim && !inQuote) {
				result.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		
		if (!current.isEmpty() || !str.isEmpty()) {
			result.add(current.toString());
		}
		return result;
	}
	
	/**
	 * Gets the indentation level (number of leading spaces) of a line.<br>
	 *
	 * @param line The line to check
	 * @return The number of leading spaces
	 * @throws NullPointerException If the line is null
	 */
	private static int getIndent(@NonNull String line) {
		Objects.requireNonNull(line, "Line must not be null");
		
		int count = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == ' ') {
				count++;
			} else {
				return count;
			}
		}
		return count;
	}
	
	/**
	 * Reads the toon content and returns it as a toon element.<br>
	 *
	 * @return The parsed toon element
	 * @throws ToonSyntaxException If the toon content has syntax errors
	 */
	public @NonNull ToonElement readToon() {
		if (this.lines.isEmpty()) {
			return new ToonObject();
		}
		
		String firstLine = this.findFirstNonBlankLine();
		if (firstLine == null) {
			return new ToonObject();
		}
		
		String trimmed = firstLine.trim();
		if (trimmed.startsWith("[")) {
			this.lineIndex = 0;
			return this.parseRootArray();
		}
		
		this.lineIndex = 0;
		return this.parseObject(0);
	}
	
	/**
	 * Finds the first non-blank line in the content.<br>
	 *
	 * @return The first non-blank line, or null if all lines are blank
	 */
	private @Nullable String findFirstNonBlankLine() {
		for (String line : this.lines) {
			if (!line.trim().isEmpty()) {
				return line;
			}
		}
		return null;
	}
	
	/**
	 * Parses an object from lines at the given indentation depth.<br>
	 *
	 * @param expectedIndent The expected indentation in spaces
	 * @return The parsed toon object
	 * @throws ToonSyntaxException If there are syntax errors
	 */
	private @NonNull ToonObject parseObject(int expectedIndent) {
		ToonObject object = new ToonObject();
		
		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex);
			
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}
			
			int indent = getIndent(line);
			if (indent < expectedIndent) {
				return object;
			}
			
			if (indent > expectedIndent) {
				return object;
			}
			
			this.lineIndex++;
			String content = line.substring(indent);
			
			if (content.startsWith("- ") || "-".equals(content)) {
				this.lineIndex--;
				return object;
			}
			
			int colonIndex = findColonIndex(content);
			if (colonIndex == -1) {
				throw new ToonSyntaxException("Expected key-value pair at line " + (this.lineIndex));
			}
			
			String rawKey = content.substring(0, colonIndex).trim();
			String afterColon = content.substring(colonIndex + 1);
			
			int arrayStart = findArrayBracketInKey(rawKey);
			String key;
			if (arrayStart >= 0) {
				String arrayHeader = rawKey.substring(arrayStart);
				afterColon = " " + arrayHeader + ":" + afterColon;
				key = unquoteKey(rawKey.substring(0, arrayStart).trim());
			} else {
				key = unquoteKey(rawKey);
			}
			
			ToonElement value = this.parseValueAfterColon(afterColon, indent);
			if (this.config.expandPaths() == ToonConfig.PathExpansion.SAFE && key.contains(".")) {
				expandDottedKey(object, key, value);
			} else {
				object.add(key, value);
			}
		}
		return object;
	}
	
	/**
	 * Parses the value part after the colon separator.<br>
	 *
	 * @param afterColon The string after the colon
	 * @param currentIndent The current line indentation
	 * @return The parsed toon element
	 * @throws ToonSyntaxException If there are syntax errors
	 */
	private @NonNull ToonElement parseValueAfterColon(@NonNull String afterColon, int currentIndent) {
		Objects.requireNonNull(afterColon, "After colon must not be null");
		
		String trimmed = afterColon.trim();
		
		if (trimmed.isEmpty()) {
			return this.parseObject(currentIndent + this.config.indent());
		}
		
		Matcher inlineMatcher = INLINE_ARRAY_PATTERN.matcher(extractArrayHeader(trimmed));
		if (inlineMatcher.matches()) {
			int count = Integer.parseInt(inlineMatcher.group(1));
			char delim = parseDelimiterChar(inlineMatcher.group(2));
			String valuesPart = trimmed.substring(trimmed.indexOf("]:") + 2).trim();
			return this.parseInlineArray(count, delim, valuesPart);
		}
		
		Matcher tabularMatcher = TABULAR_ARRAY_PATTERN.matcher(extractArrayHeader(trimmed));
		if (tabularMatcher.matches()) {
			int count = Integer.parseInt(tabularMatcher.group(1));
			char delim = parseDelimiterChar(tabularMatcher.group(2));
			String fieldsStr = tabularMatcher.group(3);
			List<String> fields = splitByDelimiter(fieldsStr, delim);
			return this.parseTabularArray(count, delim, fields, currentIndent);
		}
		
		Matcher expandedMatcher = EXPANDED_ARRAY_PATTERN.matcher(extractArrayHeader(trimmed));
		if (expandedMatcher.matches()) {
			int count = Integer.parseInt(expandedMatcher.group(1));
			return this.parseExpandedArray(count, currentIndent);
		}
		return parseTokenValue(trimmed);
	}
	
	/**
	 * Parses an inline primitive array from a values string.<br>
	 *
	 * @param count The expected element count
	 * @param delim The delimiter character
	 * @param valuesPart The string containing the values
	 * @return The parsed toon array
	 * @throws ToonSyntaxException If the count doesn't match in strict mode
	 */
	private @NonNull ToonArray parseInlineArray(int count, char delim, @NonNull String valuesPart) {
		Objects.requireNonNull(valuesPart, "Values part must not be null");
		ToonArray array = new ToonArray();
		
		if (valuesPart.isEmpty()) {
			if (this.config.strict() && count != 0) {
				throw new ToonSyntaxException("Expected " + count + " elements but found 0 at line " + this.lineIndex);
			}
			return array;
		}
		
		List<String> tokens = splitByDelimiter(valuesPart, delim);
		for (String token : tokens) {
			array.add(parseTokenValue(token.trim()));
		}
		
		if (this.config.strict() && array.size() != count) {
			throw new ToonSyntaxException("Expected " + count + " elements but found " + array.size() + " at line " + this.lineIndex);
		}
		return array;
	}
	
	/**
	 * Parses a tabular array from subsequent row lines.<br>
	 *
	 * @param count The expected row count
	 * @param delim The delimiter character
	 * @param fields The field names
	 * @param currentIndent The current indentation level
	 * @return The parsed toon array of objects
	 * @throws ToonSyntaxException If the count doesn't match in strict mode
	 */
	@SuppressWarnings("DuplicatedCode")
	private @NonNull ToonArray parseTabularArray(int count, char delim, @NonNull List<String> fields, int currentIndent) {
		Objects.requireNonNull(fields, "Fields must not be null");
		ToonArray array = new ToonArray();
		
		int rowIndent = currentIndent + this.config.indent();
		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex);
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}
			
			int indent = getIndent(line);
			if (indent < rowIndent) {
				break;
			}
			
			this.lineIndex++;
			String content = line.trim();
			List<String> values = splitByDelimiter(content, delim);
			if (values.size() != fields.size()) {
				throw new ToonSyntaxException("Expected " + fields.size() + " values but found " + values.size() + " at line " + this.lineIndex);
			}
			
			ToonObject row = new ToonObject();
			for (int i = 0; i < fields.size(); i++) {
				row.add(fields.get(i).trim(), parseTokenValue(values.get(i).trim()));
			}
			array.add(row);
		}
		
		if (this.config.strict() && array.size() != count) {
			throw new ToonSyntaxException("Expected " + count + " rows but found " + array.size() + " at line " + this.lineIndex);
		}
		return array;
	}
	
	/**
	 * Parses an expanded list array from subsequent dash-prefixed lines.<br>
	 *
	 * @param count The expected element count
	 * @param currentIndent The current indentation level
	 * @return The parsed toon array
	 * @throws ToonSyntaxException If the count doesn't match in strict mode
	 */
	@SuppressWarnings("DuplicatedCode")
	private @NonNull ToonArray parseExpandedArray(int count, int currentIndent) {
		ToonArray array = new ToonArray();
		
		int itemIndent = currentIndent + this.config.indent();
		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex);
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}
			
			int indent = getIndent(line);
			if (indent < itemIndent) {
				break;
			}
			
			String content = line.substring(indent);
			if (!content.startsWith("- ") && !"-".equals(content)) {
				break;
			}
			
			this.lineIndex++;
			String itemContent = content.substring(1).trim();
			
			if (itemContent.isEmpty()) {
				array.add(this.parseObject(itemIndent + this.config.indent()));
			} else if (itemContent.startsWith("[")) {
				String headerPart = extractArrayHeader(itemContent);
				
				Matcher inlineMatcher = INLINE_ARRAY_PATTERN.matcher(headerPart);
				if (inlineMatcher.matches()) {
					int innerCount = Integer.parseInt(inlineMatcher.group(1));
					char innerDelim = parseDelimiterChar(inlineMatcher.group(2));
					String valuesPart = itemContent.substring(itemContent.indexOf("]:") + 2).trim();
					array.add(this.parseInlineArray(innerCount, innerDelim, valuesPart));
					continue;
				}
				
				Matcher expandedMatcher = EXPANDED_ARRAY_PATTERN.matcher(headerPart);
				if (expandedMatcher.matches()) {
					int innerCount = Integer.parseInt(expandedMatcher.group(1));
					array.add(this.parseExpandedArray(innerCount, itemIndent));
					continue;
				}
				
				array.add(parseTokenValue(itemContent));
			} else {
				array.add(parseTokenValue(itemContent));
			}
		}
		
		if (this.config.strict() && array.size() != count) {
			throw new ToonSyntaxException("Expected " + count + " elements but found " + array.size() + " at line " + this.lineIndex);
		}
		return array;
	}
	
	/**
	 * Parses a root-level array document.<br>
	 *
	 * @return The parsed toon array
	 * @throws ToonSyntaxException If there are syntax errors
	 */
	private @NonNull ToonArray parseRootArray() {
		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex);
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}
			
			String trimmed = line.trim();
			String headerPart = extractArrayHeader(trimmed);
			
			Matcher inlineMatcher = INLINE_ARRAY_PATTERN.matcher(headerPart);
			if (inlineMatcher.matches()) {
				this.lineIndex++;
				int count = Integer.parseInt(inlineMatcher.group(1));
				char delim = parseDelimiterChar(inlineMatcher.group(2));
				String valuesPart = trimmed.substring(trimmed.indexOf("]:") + 2).trim();
				return this.parseInlineArray(count, delim, valuesPart);
			}
			
			Matcher tabularMatcher = TABULAR_ARRAY_PATTERN.matcher(headerPart);
			if (tabularMatcher.matches()) {
				this.lineIndex++;
				int count = Integer.parseInt(tabularMatcher.group(1));
				char delim = parseDelimiterChar(tabularMatcher.group(2));
				String fieldsStr = tabularMatcher.group(3);
				List<String> fields = splitByDelimiter(fieldsStr, delim);
				return this.parseTabularArray(count, delim, fields, -this.config.indent());
			}
			
			Matcher expandedMatcher = EXPANDED_ARRAY_PATTERN.matcher(headerPart);
			if (expandedMatcher.matches()) {
				this.lineIndex++;
				int count = Integer.parseInt(expandedMatcher.group(1));
				return this.parseExpandedArray(count, -this.config.indent());
			}
			
			throw new ToonSyntaxException("Invalid root array syntax at line " + (this.lineIndex + 1));
		}
		return new ToonArray();
	}
	
	@Override
	public void close() {}
}
