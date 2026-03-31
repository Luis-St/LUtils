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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.toml.exception.TomlSyntaxException;
import net.luis.utils.io.reader.StringReader;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.InputStreamReader;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents a reader for toml files.<br>
 * This reader reads the toml content from a defined input and returns it as a toml table.<br>
 * <p>
 *     Supports toml v1.0 specification including:
 * </p>
 * <ul>
 *     <li>Basic and literal strings</li>
 *     <li>Multi-line strings</li>
 *     <li>Integers: decimal, hex, octal, binary</li>
 *     <li>Floats: standard, exponent, inf, nan</li>
 *     <li>Booleans</li>
 *     <li>Date/time values (RFC 3339)</li>
 *     <li>Arrays and inline arrays</li>
 *     <li>Tables and inline tables</li>
 *     <li>Array of tables</li>
 *     <li>Dotted keys</li>
 * </ul>
 *
 * @author Luis-St
 */
public class TomlReader implements AutoCloseable {
	
	/**
	 * Pattern for RFC 3339 offset date-time.<br>
	 */
	private static final Pattern OFFSET_DATETIME_PATTERN = Pattern.compile(
		"\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})"
	);
	
	/**
	 * Pattern for local date-time.<br>
	 */
	private static final Pattern LOCAL_DATETIME_PATTERN = Pattern.compile(
		"\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?"
	);
	
	/**
	 * Pattern for local date.<br>
	 */
	private static final Pattern LOCAL_DATE_PATTERN = Pattern.compile(
		"\\d{4}-\\d{2}-\\d{2}"
	);
	
	/**
	 * Pattern for local time.<br>
	 */
	private static final Pattern LOCAL_TIME_PATTERN = Pattern.compile(
		"\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?"
	);
	
	/**
	 * The configuration for this reader.<br>
	 */
	private final TomlConfig config;
	
	/**
	 * The internal io reader to read the toml content.<br>
	 */
	private final StringReader reader;
	
	/**
	 * The current line number for error messages.<br>
	 */
	private int lineNumber = 1;
	
	/**
	 * Constructs a new toml reader with the given string and the default configuration.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public TomlReader(@NonNull String string) {
		this(string, TomlConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new toml reader with the given string and configuration.<br>
	 *
	 * @param string The string to read from
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the string or the configuration is null
	 */
	public TomlReader(@NonNull String string, @NonNull TomlConfig config) {
		this.config = Objects.requireNonNull(config, "Toml config must not be null");
		this.reader = new StringReader(Objects.requireNonNull(string, "String must not be null"));
	}
	
	/**
	 * Constructs a new toml reader with the given input and the default configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public TomlReader(@NonNull InputProvider input) {
		this(input, TomlConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new toml reader with the given input and configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the input or the configuration is null
	 */
	public TomlReader(@NonNull InputProvider input, @NonNull TomlConfig config) {
		this.config = Objects.requireNonNull(config, "Toml config must not be null");
		this.reader = new StringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Reads the toml content and returns it as a toml table.<br>
	 *
	 * @return The parsed toml table (root document)
	 * @throws TomlSyntaxException If the toml content has syntax errors
	 */
	public @NonNull TomlTable readToml() {
		TomlTable root = new TomlTable();
		TomlTable currentTable = root;
		
		while (this.reader.canRead()) {
			this.skipWhitespace();
			
			if (!this.reader.canRead() || this.isEndOfLine()) {
				this.skipToNextLine();
				continue;
			}
			
			if (this.peek() == '#') {
				this.skipToNextLine();
				continue;
			}
			
			if (this.peek() == '[') {
				this.advance();
				
				if (this.peek() == '[') {
					this.advance();
					
					String tablePath = this.parseTablePath();
					
					this.expectChar(']');
					this.expectChar(']');
					this.skipWhitespaceAndComment();
					
					currentTable = this.getOrCreateArrayOfTables(root, tablePath);
				} else {
					String tablePath = this.parseTablePath();
					this.expectChar(']');
					this.skipWhitespaceAndComment();
					
					currentTable = this.getOrCreateTable(root, tablePath);
				}
				continue;
			}
			
			this.parseKeyValue(currentTable);
		}
		
		return root;
	}
	
	/**
	 * Checks if the current position is at end of line (newline character).<br>
	 * @return True if at end of line, false otherwise
	 */
	private boolean isEndOfLine() {
		if (!this.reader.canRead()) {
			return true;
		}
		char c = this.reader.peek();
		return c == '\n' || c == '\r';
	}
	
	/**
	 * Skips to the next line, consuming the newline character(s).<br>
	 */
	private void skipToNextLine() {
		while (this.reader.canRead()) {
			char c = this.reader.read();
			if (c == '\n') {
				this.lineNumber++;
				return;
			} else if (c == '\r') {
				this.lineNumber++;
				if (this.reader.canRead() && this.reader.peek() == '\n') {
					this.reader.skip();
				}
				return;
			}
		}
	}
	
	/**
	 * Peeks at the current character without advancing.<br>
	 * @return The current character, or '\0' if at end of input or end of line
	 */
	private char peek() {
		if (!this.reader.canRead()) {
			return '\0';
		}
		char c = this.reader.peek();
		if (c == '\n' || c == '\r') {
			return '\0';
		}
		return c;
	}
	
	/**
	 * Peeks at a character ahead without advancing.<br>
	 *
	 * @param offset The offset from current position
	 * @return The character at the offset, or '\0' if past end or at newline
	 */
	private char peek(int offset) {
		if (!this.reader.canRead(offset + 1)) {
			return '\0';
		}
		this.reader.mark();
		for (int i = 0; i < offset; i++) {
			char c = this.reader.read();
			if (c == '\n' || c == '\r') {
				this.reader.reset();
				return '\0';
			}
		}
		char result = this.reader.peek();
		this.reader.reset();
		if (result == '\n' || result == '\r') {
			return '\0';
		}
		return result;
	}
	
	/**
	 * Advances to the next character.<br>
	 * @return The character that was at the current position
	 */
	private char advance() {
		if (!this.reader.canRead()) {
			return '\0';
		}
		
		char c = this.reader.read();
		if (c == '\n') {
			this.lineNumber++;
		} else if (c == '\r') {
			this.lineNumber++;
			
			if (this.reader.canRead() && this.reader.peek() == '\n') {
				this.reader.skip();
			}
		}
		return c;
	}
	
	/**
	 * Skips whitespace characters (spaces and tabs only, not newlines).<br>
	 */
	private void skipWhitespace() {
		while (this.reader.canRead()) {
			char c = this.reader.peek();
			
			if (c == ' ' || c == '\t') {
				this.reader.skip();
			} else {
				break;
			}
		}
	}
	
	/**
	 * Skips whitespace and comments to end of line.<br>
	 */
	private void skipWhitespaceAndComment() {
		this.skipWhitespace();
		
		if (this.peek() == '#') {
			this.skipToNextLine();
		} else if (!this.isEndOfLine() && this.reader.canRead()) {
			throw new TomlSyntaxException("Unexpected character '" + this.peek() + "' at line " + this.lineNumber);
		} else {
			this.skipToNextLine();
		}
	}
	
	/**
	 * Peeks at the current character without advancing (raw - includes newlines).<br>
	 * @return The current character, or '\0' if at end of input
	 */
	private char peekRaw() {
		if (!this.reader.canRead()) {
			return '\0';
		}
		return this.reader.peek();
	}
	
	/**
	 * Peeks at a character ahead without advancing (raw - includes newlines).<br>
	 *
	 * @param offset The offset from current position
	 * @return The character at the offset, or '\0' if past end
	 */
	private char peekRaw(int offset) {
		if (!this.reader.canRead(offset + 1)) {
			return '\0';
		}
		
		this.reader.mark();
		for (int i = 0; i < offset; i++) {
			this.reader.read();
		}
		
		char result = this.reader.peek();
		this.reader.reset();
		return result;
	}
	
	/**
	 * Expects and consumes a specific character (raw - allows newlines).<br>
	 *
	 * @param expected The expected character
	 * @throws TomlSyntaxException If the character doesn't match
	 */
	private void expectCharRaw(char expected) {
		char actual = this.peekRaw();
		if (actual != expected) {
			throw new TomlSyntaxException("Expected '" + expected + "' but found '" + actual + "' at line " + this.lineNumber);
		}
		this.advance();
	}
	
	/**
	 * Skips whitespace characters including newlines (for multi-line strings).<br>
	 */
	private void skipWhitespaceRaw() {
		while (this.reader.canRead()) {
			char c = this.reader.peek();
			
			if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
				this.advance();
			} else {
				break;
			}
		}
	}
	
	/**
	 * Expects and consumes a specific character.<br>
	 *
	 * @param expected The expected character
	 * @throws TomlSyntaxException If the character doesn't match
	 */
	private void expectChar(char expected) {
		char actual = this.peek();
		if (actual != expected) {
			throw new TomlSyntaxException("Expected '" + expected + "' but found '" + actual + "' at line " + this.lineNumber);
		}
		this.advance();
	}
	
	/**
	 * Parses a table path (e.g., "server.database").<br>
	 * @return The table path
	 */
	private @NonNull String parseTablePath() {
		StringBuilder path = new StringBuilder();
		boolean first = true;
		
		while (true) {
			this.skipWhitespace();
			
			if (!first) {
				if (this.peek() != '.') {
					break;
				}
				this.advance();
				path.append('.');
				this.skipWhitespace();
			}
			first = false;
			
			String key = this.parseKey();
			path.append(key);
			
			this.skipWhitespace();
		}
		return path.toString();
	}
	
	/**
	 * Parses a key (bare or quoted).<br>
	 * @return The parsed key
	 */
	private @NonNull String parseKey() {
		char c = this.peek();
		
		if (c == '"') {
			return this.parseBasicString();
		} else if (c == '\'') {
			return this.parseLiteralString();
		} else {
			return this.parseBareKey();
		}
	}
	
	/**
	 * Parses a bare key.<br>
	 * @return The bare key
	 */
	private @NonNull String parseBareKey() {
		StringBuilder key = new StringBuilder();
		
		while (true) {
			char c = this.peek();
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-') {
				key.append(this.advance());
			} else {
				break;
			}
		}
		
		if (key.isEmpty()) {
			throw new TomlSyntaxException("Empty key at line " + this.lineNumber);
		}
		return key.toString();
	}
	
	/**
	 * Reads a dotted key path until the '=' character.<br>
	 * @return The list of keys in the path
	 */
	private @NonNull List<String> readKeyPath() {
		List<String> keyPath = new ArrayList<>();
		keyPath.add(this.parseKey());
		
		this.skipWhitespace();
		while (this.peek() == '.') {
			this.advance();
			this.skipWhitespace();
			keyPath.add(this.parseKey());
			this.skipWhitespace();
		}
		
		this.expectChar('=');
		this.skipWhitespace();
		
		return keyPath;
	}
	
	/**
	 * Parses a key-value pair and adds it to the table.<br>
	 *
	 * @param table The table to add the key-value to
	 * @throws NullPointerException If the table is null
	 * @throws TomlSyntaxException If there are syntax errors
	 */
	private void parseKeyValue(@NonNull TomlTable table) {
		Objects.requireNonNull(table, "Table must not be null");
		
		List<String> keyPath = this.readKeyPath();
		TomlElement value = this.parseValue();
		this.skipWhitespaceAndComment();
		
		TomlTable target = table;
		for (int i = 0; i < keyPath.size() - 1; i++) {
			String key = keyPath.get(i);
			TomlElement existing = target.get(key);
			
			if (existing == null) {
				TomlTable newTable = new TomlTable();
				target.add(key, newTable);
				target = newTable;
			} else if (existing instanceof TomlTable t) {
				target = t;
			} else {
				throw new TomlSyntaxException("Cannot use dotted key '" + key + "' - already exists as non-table at line " + this.lineNumber);
			}
		}
		
		String finalKey = keyPath.getLast();
		if (target.containsKey(finalKey) && !this.config.allowDuplicateKeys()) {
			throw new TomlSyntaxException("Duplicate key '" + finalKey + "' at line " + this.lineNumber);
		}
		target.add(finalKey, value);
	}
	
	/**
	 * Parses a value.<br>
	 *
	 * @return The parsed value
	 * @throws TomlSyntaxException If the value has syntax errors
	 */
	private @NonNull TomlElement parseValue() {
		char c = this.peek();
		
		if (c == '"') {
			if (this.peek(1) == '"' && this.peek(2) == '"') {
				return new TomlValue(this.parseMultiLineBasicString());
			}
			return new TomlValue(this.parseBasicString());
		} else if (c == '\'') {
			if (this.peek(1) == '\'' && this.peek(2) == '\'') {
				return new TomlValue(this.parseMultiLineLiteralString());
			}
			return new TomlValue(this.parseLiteralString());
		} else if (c == '[') {
			return this.parseArray();
		} else if (c == '{') {
			return this.parseInlineTable();
		} else if (c == 't' || c == 'f') {
			return this.parseBoolean();
		} else if (c == '-' || c == '+' || (c >= '0' && c <= '9') || c == 'i' || c == 'n') {
			return this.parseNumberOrDateTime();
		}
		throw new TomlSyntaxException("Unexpected character '" + c + "' at line " + this.lineNumber);
	}
	
	/**
	 * Parses a basic string (double-quoted).<br>
	 *
	 * @return The parsed string
	 * @throws TomlSyntaxException If the string is unterminated
	 */
	private @NonNull String parseBasicString() {
		this.expectChar('"');
		StringBuilder result = new StringBuilder();
		
		while (true) {
			char c = this.peek();
			if (c == '\0') {
				throw new TomlSyntaxException("Unterminated string at line " + this.lineNumber);
			}
			
			if (c == '"') {
				this.advance();
				break;
			}
			
			if (c == '\\') {
				this.advance();
				result.append(this.parseEscapeSequence());
			} else {
				result.append(this.advance());
			}
		}
		return result.toString();
	}
	
	/**
	 * Parses a literal string (single-quoted).<br>
	 *
	 * @return The parsed string
	 * @throws TomlSyntaxException If the string is unterminated
	 */
	private @NonNull String parseLiteralString() {
		this.expectChar('\'');
		StringBuilder result = new StringBuilder();
		
		while (true) {
			char c = this.peek();
			if (c == '\0') {
				throw new TomlSyntaxException("Unterminated literal string at line " + this.lineNumber);
			}
			
			if (c == '\'') {
				this.advance();
				break;
			}
			result.append(this.advance());
		}
		return result.toString();
	}
	
	/**
	 * Parses a multi-line basic string.<br>
	 *
	 * @return The parsed string
	 * @throws TomlSyntaxException If the string is unterminated
	 */
	private @NonNull String parseMultiLineBasicString() {
		this.expectCharRaw('"');
		this.expectCharRaw('"');
		this.expectCharRaw('"');
		
		StringBuilder result = new StringBuilder();
		boolean firstLine = true;
		
		while (this.reader.canRead()) {
			if (this.peekRaw() == '"' && this.peekRaw(1) == '"' && this.peekRaw(2) == '"') {
				this.advance();
				this.advance();
				this.advance();
				return result.toString();
			}
			
			char c = this.peekRaw();
			if (c == '\n' || c == '\r') {
				if (!firstLine) {
					result.append('\n');
				}
				this.advance();
				firstLine = false;
				continue;
			}
			
			if (c == '\\') {
				this.advance();
				char next = this.peekRaw();
				if (next == '\n' || next == '\r') {
					this.advance();
					this.skipWhitespaceRaw();
				} else {
					result.append(this.parseEscapeSequence());
				}
			} else {
				result.append(this.advance());
			}
			
			firstLine = false;
		}
		throw new TomlSyntaxException("Unterminated multi-line string starting at line " + this.lineNumber);
	}
	
	/**
	 * Parses a multi-line literal string.<br>
	 *
	 * @return The parsed string
	 * @throws TomlSyntaxException If the string is unterminated
	 */
	private @NonNull String parseMultiLineLiteralString() {
		this.expectCharRaw('\'');
		this.expectCharRaw('\'');
		this.expectCharRaw('\'');
		
		StringBuilder result = new StringBuilder();
		boolean firstLine = true;
		
		while (this.reader.canRead()) {
			if (this.peekRaw() == '\'' && this.peekRaw(1) == '\'' && this.peekRaw(2) == '\'') {
				this.advance();
				this.advance();
				this.advance();
				return result.toString();
			}
			
			char c = this.peekRaw();
			if (c == '\n' || c == '\r') {
				if (!firstLine) {
					result.append('\n');
				}
				this.advance();
				firstLine = false;
				continue;
			}
			
			result.append(this.advance());
			firstLine = false;
		}
		throw new TomlSyntaxException("Unterminated multi-line literal string starting at line " + this.lineNumber);
	}
	
	/**
	 * Parses an escape sequence.<br>
	 *
	 * @return The escaped character(s)
	 * @throws TomlSyntaxException If the escape sequence is invalid
	 */
	private @NonNull String parseEscapeSequence() {
		char c = this.advance();
		return switch (c) {
			case 'b' -> "\b";
			case 't' -> "\t";
			case 'n' -> "\n";
			case 'f' -> "\f";
			case 'r' -> "\r";
			case '"' -> "\"";
			case '\\' -> "\\";
			case 'u' -> {
				String hex = this.readHex(4);
				yield Character.toString(Integer.parseInt(hex, 16));
			}
			case 'U' -> {
				String hex = this.readHex(8);
				yield Character.toString(Integer.parseInt(hex, 16));
			}
			default -> throw new TomlSyntaxException("Invalid escape sequence '\\" + c + "' at line " + this.lineNumber);
		};
	}
	
	/**
	 * Reads a hex sequence of the given length.<br>
	 *
	 * @param length The number of hex characters to read
	 * @return The hex string
	 * @throws TomlSyntaxException If an invalid hex character is encountered
	 */
	private @NonNull String readHex(int length) {
		StringBuilder hex = new StringBuilder();
		
		for (int i = 0; i < length; i++) {
			char c = this.advance();
			
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
				throw new TomlSyntaxException("Invalid hex character '" + c + "' in escape sequence at line " + this.lineNumber);
			}
			hex.append(c);
		}
		return hex.toString();
	}
	
	/**
	 * Parses an array.<br>
	 *
	 * @return The parsed array
	 * @throws TomlSyntaxException If the array has syntax errors
	 */
	private @NonNull TomlArray parseArray() {
		this.expectChar('[');
		TomlArray array = new TomlArray();
		
		while (true) {
			this.skipWhitespaceAndNewlines();
			
			if (this.peekRaw() == ']') {
				this.advance();
				return array;
			}
			
			TomlElement element = this.parseValue();
			array.add(element);
			
			this.skipWhitespaceAndNewlines();
			if (this.peekRaw() == ',') {
				this.advance();
			} else if (this.peekRaw() != ']') {
				throw new TomlSyntaxException("Expected ',' or ']' in array at line " + this.lineNumber);
			}
		}
	}
	
	/**
	 * Skips whitespace, newlines, and comments.<br>
	 */
	private void skipWhitespaceAndNewlines() {
		while (this.reader.canRead()) {
			char c = this.reader.peek();
			if (c == ' ' || c == '\t') {
				this.reader.skip();
			} else if (c == '#') {
				while (this.reader.canRead() && this.reader.peek() != '\n' && this.reader.peek() != '\r') {
					this.reader.skip();
				}
			} else if (c == '\n' || c == '\r') {
				this.advance();
			} else {
				break;
			}
		}
	}
	
	/**
	 * Parses an inline table.<br>
	 * @return The parsed table
	 * @throws TomlSyntaxException If the table has syntax errors
	 */
	private @NonNull TomlTable parseInlineTable() {
		this.expectChar('{');
		TomlTable table = new TomlTable();
		table.setInline(true);
		
		this.skipWhitespace();
		
		if (this.peek() == '}') {
			this.advance();
			return table;
		}
		
		while (true) {
			this.skipWhitespace();
			
			List<String> keyPath = this.readKeyPath();
			TomlElement value = this.parseValue();
			
			TomlTable target = table;
			for (int i = 0; i < keyPath.size() - 1; i++) {
				String key = keyPath.get(i);
				TomlElement existing = target.get(key);
				if (existing == null) {
					TomlTable newTable = new TomlTable();
					newTable.setInline(true);
					target.add(key, newTable);
					target = newTable;
				} else if (existing instanceof TomlTable t) {
					target = t;
				} else {
					throw new TomlSyntaxException("Cannot use dotted key in inline table at line " + this.lineNumber);
				}
			}
			
			target.add(keyPath.getLast(), value);
			
			this.skipWhitespace();
			
			if (this.peek() == '}') {
				this.advance();
				return table;
			}
			
			if (this.peek() == ',') {
				this.advance();
			} else {
				throw new TomlSyntaxException("Expected ',' or '}' in inline table at line " + this.lineNumber);
			}
		}
	}
	
	/**
	 * Parses a boolean value.<br>
	 * @return The parsed boolean value
	 * @throws TomlSyntaxException If the boolean is invalid
	 */
	private @NonNull TomlValue parseBoolean() {
		if (this.match("true")) {
			return new TomlValue(true);
		} else if (this.match("false")) {
			return new TomlValue(false);
		}
		throw new TomlSyntaxException("Invalid boolean at line " + this.lineNumber);
	}
	
	/**
	 * Tries to match and consume a string.<br>
	 *
	 * @param expected The expected string
	 * @return True if matched, false otherwise
	 */
	private boolean match(@NonNull String expected) {
		if (!this.reader.canRead()) {
			return false;
		}
		
		for (int i = 0; i < expected.length(); i++) {
			if (this.peek(i) != expected.charAt(i)) {
				return false;
			}
		}
		
		char next = this.peek(expected.length());
		if ((next >= 'a' && next <= 'z') || (next >= 'A' && next <= 'Z') || (next >= '0' && next <= '9') || next == '_') {
			return false;
		}
		
		this.reader.skip(expected.length());
		return true;
	}
	
	/**
	 * Parses a number or date/time value.<br>
	 * @return The parsed value
	 */
	private @NonNull TomlValue parseNumberOrDateTime() {
		StringBuilder raw = new StringBuilder();
		
		while (true) {
			char c = this.peek();
			if (c == '\0' || c == ' ' || c == '\t' || c == ',' || c == ']' || c == '}' || c == '#') {
				break;
			}
			raw.append(this.advance());
		}
		
		String value = raw.toString();
		
		switch (value) {
			case "inf", "+inf" -> {
				return new TomlValue(Double.POSITIVE_INFINITY);
			}
			case "-inf" -> {
				return new TomlValue(Double.NEGATIVE_INFINITY);
			}
			case "nan", "+nan", "-nan" -> {
				return new TomlValue(Double.NaN);
			}
		}
		
		TomlValue dateTime = this.tryParseDateTime(value);
		if (dateTime != null) {
			return dateTime;
		}
		return this.parseNumber(value);
	}
	
	/**
	 * Tries to parse a date/time value.<br>
	 *
	 * @param value The value string
	 * @return The parsed date/time, or null if not a date/time
	 * @throws NullPointerException If the value is null
	 */
	private @Nullable TomlValue tryParseDateTime(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		try {
			if (OFFSET_DATETIME_PATTERN.matcher(value).matches()) {
				String normalized = value.replace(' ', 'T');
				return new TomlValue(OffsetDateTime.parse(normalized));
			}
			
			if (LOCAL_DATETIME_PATTERN.matcher(value).matches()) {
				String normalized = value.replace(' ', 'T');
				return new TomlValue(LocalDateTime.parse(normalized));
			}
			
			if (LOCAL_DATE_PATTERN.matcher(value).matches()) {
				return new TomlValue(LocalDate.parse(value));
			}
			
			if (LOCAL_TIME_PATTERN.matcher(value).matches()) {
				return new TomlValue(LocalTime.parse(value));
			}
		} catch (DateTimeParseException _) {}
		
		return null;
	}
	
	/**
	 * Parses a number value.<br>
	 *
	 * @param value The value string
	 * @return The parsed number value
	 * @throws NullPointerException If the value is null
	 * @throws TomlSyntaxException If the number is invalid
	 */
	private @NonNull TomlValue parseNumber(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		String cleanValue = value.replace("_", "");
		
		try {
			if (cleanValue.startsWith("0x") || cleanValue.startsWith("0X")) {
				return new TomlValue(Long.parseLong(cleanValue.substring(2), 16));
			} else if (cleanValue.startsWith("0o") || cleanValue.startsWith("0O")) {
				return new TomlValue(Long.parseLong(cleanValue.substring(2), 8));
			} else if (cleanValue.startsWith("0b") || cleanValue.startsWith("0B")) {
				return new TomlValue(Long.parseLong(cleanValue.substring(2), 2));
			}
			
			if (cleanValue.contains(".") || cleanValue.contains("e") || cleanValue.contains("E")) {
				return new TomlValue(Double.parseDouble(cleanValue));
			}
			return new TomlValue(Long.parseLong(cleanValue));
		} catch (NumberFormatException e) {
			throw new TomlSyntaxException("Invalid number '" + value + "' at line " + this.lineNumber);
		}
	}
	
	/**
	 * Gets or creates a table at the given path.<br>
	 *
	 * @param root The root table
	 * @param path The table path (e.g., "server.database")
	 * @return The table at the path
	 * @throws NullPointerException If the root or path is null
	 * @throws TomlSyntaxException If the table cannot be created due to existing non-table elements
	 */
	@SuppressWarnings("DuplicatedCode")
	private @NonNull TomlTable getOrCreateTable(@NonNull TomlTable root, @NonNull String path) {
		Objects.requireNonNull(root, "Root table must not be null");
		Objects.requireNonNull(path, "Table path must not be null");
		String[] parts = path.split("\\.");
		TomlTable current = root;
		
		for (String part : parts) {
			TomlElement existing = current.get(part);
			
			switch (existing) {
				case null -> {
					TomlTable newTable = new TomlTable();
					current.add(part, newTable);
					current = newTable;
				}
				case TomlTable table -> current = table;
				case TomlArray array when array.isArrayOfTables() -> current = array.getAsTomlTable(array.size() - 1);
				default -> throw new TomlSyntaxException("Cannot redefine '" + part + "' as table at line " + this.lineNumber);
			}
		}
		return current;
	}
	
	/**
	 * Gets or creates an array of tables at the given path.<br>
	 *
	 * @param root The root table
	 * @param path The table path (e.g., "products")
	 * @return A new table added to the array
	 * @throws NullPointerException If the root or path is null
	 * @throws TomlSyntaxException If the array of tables cannot be created due to existing non-array elements
	 */
	@SuppressWarnings("DuplicatedCode")
	private @NonNull TomlTable getOrCreateArrayOfTables(@NonNull TomlTable root, @NonNull String path) {
		Objects.requireNonNull(root, "Root table must not be null");
		Objects.requireNonNull(path, "Table path must not be null");
		String[] parts = path.split("\\.");
		TomlTable current = root;
		
		for (int i = 0; i < parts.length - 1; i++) {
			String part = parts[i];
			TomlElement existing = current.get(part);
			
			switch (existing) {
				case null -> {
					TomlTable newTable = new TomlTable();
					current.add(part, newTable);
					current = newTable;
				}
				case TomlTable table -> current = table;
				case TomlArray array when array.isArrayOfTables() -> current = array.getAsTomlTable(array.size() - 1);
				default -> throw new TomlSyntaxException("Cannot redefine '" + part + "' as table at line " + this.lineNumber);
			}
		}
		
		String finalPart = parts[parts.length - 1];
		TomlElement existing = current.get(finalPart);
		TomlArray array;
		if (existing == null) {
			array = new TomlArray();
			array.setArrayOfTables(true);
			current.add(finalPart, array);
		} else if (existing instanceof TomlArray a && a.isArrayOfTables()) {
			array = a;
		} else {
			throw new TomlSyntaxException("Cannot redefine '" + finalPart + "' as array of tables at line " + this.lineNumber);
		}
		
		TomlTable newTable = new TomlTable();
		array.add(newTable);
		return newTable;
	}
	
	@Override
	public void close() {
		this.reader.readRemaining();
	}
}
