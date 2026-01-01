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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.ini.exception.IniSyntaxException;
import net.luis.utils.io.reader.StringReader;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.Objects;

/**
 * Represents a reader for ini files.<br>
 * This reader reads the ini content from a defined input and returns it as an ini document.<br>
 * <p>
 * ini format supports:
 * <ul>
 *     <li>Global properties (before any section)</li>
 *     <li>Sections denoted by [section_name]</li>
 *     <li>Key-value pairs: key = value</li>
 *     <li>Comments starting with ; or #</li>
 *     <li>Quoted string values</li>
 * </ul>
 *
 * @author Luis-St
 */
public class IniReader implements AutoCloseable {
	
	/**
	 * The configuration for this reader.<br>
	 */
	private final IniConfig config;
	
	/**
	 * The internal io reader to read the ini content.<br>
	 */
	private final BufferedReader reader;
	
	/**
	 * The current line number for error messages.<br>
	 */
	private int lineNumber;
	
	/**
	 * Constructs a new ini reader with the given string and the default configuration.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public IniReader(@NonNull String string) {
		this(string, IniConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new ini reader with the given string and configuration.<br>
	 *
	 * @param string The string to read from
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the string or the configuration is null
	 */
	public IniReader(@NonNull String string, @NonNull IniConfig config) {
		this.config = Objects.requireNonNull(config, "Ini config must not be null");
		this.reader = new BufferedReader(new java.io.StringReader(Objects.requireNonNull(string, "String must not be null")));
	}
	
	/**
	 * Constructs a new ini reader with the given input and the default configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public IniReader(@NonNull InputProvider input) {
		this(input, IniConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new ini reader with the given input and configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the input or the configuration is null
	 */
	public IniReader(@NonNull InputProvider input, @NonNull IniConfig config) {
		this.config = Objects.requireNonNull(config, "Ini config must not be null");
		this.reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Reads the ini content and returns it as an ini document.<br>
	 *
	 * @return The parsed ini document
	 * @throws IniSyntaxException If the ini content has syntax errors
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public @NonNull IniDocument readIni() {
		try {
			IniDocument document = new IniDocument();
			IniSection currentSection = null;
			String line;
			
			while ((line = this.reader.readLine()) != null) {
				this.lineNumber++;
				String trimmedLine = line.trim();
				
				if (trimmedLine.isEmpty()) {
					continue;
				}
				
				if (this.isComment(trimmedLine)) {
					continue;
				}
				
				if (trimmedLine.startsWith("[")) {
					currentSection = this.parseSection(trimmedLine, document);
					continue;
				}
				
				this.parseKeyValue(trimmedLine, document, currentSection);
			}
			return document;
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read ini content", e);
		}
	}
	
	/**
	 * Checks if the line is a comment.<br>
	 *
	 * @param line The line to check
	 * @return True if the line is a comment, false otherwise
	 */
	private boolean isComment(@NonNull String line) {
		if (line.isEmpty()) {
			return false;
		}
		
		char firstChar = line.charAt(0);
		return this.config.commentCharacters().contains(firstChar);
	}
	
	/**
	 * Parses a section header and returns the section.<br>
	 *
	 * @param line The line containing the section header
	 * @param document The document to add the section to
	 * @return The parsed or existing section
	 * @throws IniSyntaxException If the section header is invalid
	 */
	private @Nullable IniSection parseSection(@NonNull String line, @NonNull IniDocument document) {
		int closeBracket = line.indexOf(']');
		if (closeBracket == -1) {
			throw new IniSyntaxException("Missing closing bracket ']' for section at line " + this.lineNumber);
		}
		
		String sectionName = line.substring(1, closeBracket).trim();
		if (sectionName.isEmpty()) {
			throw new IniSyntaxException("Empty section name at line " + this.lineNumber);
		}
		
		if (this.config.strict()) {
			this.config.ensureSectionMatches(sectionName);
		}
		
		String afterBracket = line.substring(closeBracket + 1).trim();
		if (!afterBracket.isEmpty() && !this.isComment(afterBracket)) {
			if (this.config.strict()) {
				throw new IniSyntaxException("Unexpected content after section header at line " + this.lineNumber);
			}
		}
		
		if (document.containsSection(sectionName)) {
			if (!this.config.allowDuplicateSections()) {
				throw new IniSyntaxException("Duplicate section '" + sectionName + "' at line " + this.lineNumber);
			}
			return document.getSection(sectionName);
		}
		return document.createSection(sectionName);
	}
	
	/**
	 * Parses a key-value pair and adds it to the appropriate container.<br>
	 *
	 * @param line The line containing the key-value pair
	 * @param document The document for global properties
	 * @param currentSection The current section, or null for global properties
	 * @throws IniSyntaxException If the key-value pair is invalid
	 */
	private void parseKeyValue(@NonNull String line, @NonNull IniDocument document, IniSection currentSection) {
		int separatorIndex = line.indexOf(this.config.separator());
		if (separatorIndex == -1) {
			if (this.config.strict()) {
				throw new IniSyntaxException("Missing separator '" + this.config.separator() + "' at line " + this.lineNumber);
			}
			
			String key = line.trim();
			this.addProperty(document, currentSection, key, IniNull.INSTANCE);
			return;
		}
		
		String key = line.substring(0, separatorIndex).trim();
		String valueStr = line.substring(separatorIndex + 1).trim();
		if (key.isEmpty()) {
			throw new IniSyntaxException("Empty key at line " + this.lineNumber);
		}
		
		if (this.config.strict()) {
			this.config.ensureKeyMatches(key);
		}
		
		valueStr = this.removeInlineComment(valueStr);
		IniElement value = this.parseValue(valueStr);
		
		boolean isDuplicate = currentSection != null ? currentSection.containsKey(key) : document.containsGlobalKey(key);
		if (isDuplicate && !this.config.allowDuplicateKeys()) {
			throw new IniSyntaxException("Duplicate key '" + key + "' at line " + this.lineNumber);
		}
		this.addProperty(document, currentSection, key, value);
	}
	
	/**
	 * Removes inline comments from a value string.<br>
	 *
	 * @param value The value string
	 * @return The value without inline comments
	 */
	private @NonNull String removeInlineComment(@NonNull String value) {
		boolean inQuotes = false;
		char quoteChar = 0;
		
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			
			if (!inQuotes && (c == '"' || c == '\'')) {
				inQuotes = true;
				quoteChar = c;
			} else if (inQuotes && c == quoteChar) {
				if (value.charAt(i - 1) != '\\') {
					inQuotes = false;
				}
			} else if (!inQuotes && this.config.commentCharacters().contains(c)) {
				return value.substring(0, i).trim();
			}
		}
		
		return value;
	}
	
	/**
	 * Parses a value string and returns the appropriate ini element.<br>
	 *
	 * @param valueStr The value string
	 * @return The parsed ini element
	 */
	private @NonNull IniElement parseValue(@NonNull String valueStr) {
		if (valueStr.isEmpty()) {
			return IniNull.INSTANCE;
		}
		
		if ((valueStr.startsWith("\"") && valueStr.endsWith("\"")) ||
			(valueStr.startsWith("'") && valueStr.endsWith("'"))) {
			String unquoted = valueStr.substring(1, valueStr.length() - 1);
			return new IniValue(this.processEscapes(unquoted));
		}
		
		if (this.config.parseTypedValues()) {
			if ("true".equalsIgnoreCase(valueStr) || "false".equalsIgnoreCase(valueStr)) {
				return new IniValue(Boolean.parseBoolean(valueStr));
			}
			
			try {
				StringReader reader = new StringReader(valueStr);
				Number number = reader.readNumber();
				reader.skipWhitespaces();
				if (!reader.canRead()) {
					return new IniValue(number);
				}
			} catch (Exception _) {}
		}
		return new IniValue(valueStr);
	}
	
	/**
	 * Processes escape sequences in a string.<br>
	 *
	 * @param value The value to process
	 * @return The processed value
	 */
	private @NonNull String processEscapes(@NonNull String value) {
		StringBuilder result = new StringBuilder();
		boolean escape = false;
		
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			
			if (escape) {
				switch (c) {
					case 'n' -> result.append('\n');
					case 'r' -> result.append('\r');
					case 't' -> result.append('\t');
					case '\\' -> result.append('\\');
					case '"' -> result.append('"');
					case '\'' -> result.append('\'');
					default -> {
						result.append('\\');
						result.append(c);
					}
				}
				escape = false;
			} else if (c == '\\') {
				escape = true;
			} else {
				result.append(c);
			}
		}
		
		if (escape) {
			result.append('\\');
		}
		return result.toString();
	}
	
	/**
	 * Adds a property to the appropriate container.<br>
	 *
	 * @param document The document for global properties
	 * @param section The section, or null for global properties
	 * @param key The property key
	 * @param value The property value
	 */
	private void addProperty(@NonNull IniDocument document, IniSection section, @NonNull String key, @NonNull IniElement value) {
		if (section != null) {
			section.add(key, value);
		} else {
			document.addGlobal(key, value);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.reader.close();
	}
}
