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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.yaml.exception.YamlSyntaxException;
import net.luis.utils.io.reader.StringReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * A yaml reader for reading yaml elements from a {@link String string} or {@link InputProvider input provider}.<br>
 * The reader can be used to read yaml mappings, sequences, scalars and null values.<br>
 * The reader expects only one yaml document per input.<br>
 *
 * @author Luis-St
 */
public class YamlReader implements AutoCloseable {

	/**
	 * The yaml config used by this reader.<br>
	 */
	private final YamlConfig config;
	/**
	 * The internal reader used to read the yaml content.<br>
	 */
	private final StringReader reader;
	/**
	 * Map of anchor names to their resolved elements.<br>
	 */
	private final Map<String, YamlElement> anchors = new HashMap<>();
	/**
	 * The lines of the yaml content split by newline.<br>
	 */
	private final List<String> lines = new ArrayList<>();
	/**
	 * The current line index.<br>
	 */
	private int lineIndex = 0;

	/**
	 * Constructs a new yaml reader with the given string and the default configuration.<br>
	 *
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public YamlReader(@NotNull String string) {
		this(string, YamlConfig.DEFAULT);
	}

	/**
	 * Constructs a new yaml reader with the given string and configuration.<br>
	 *
	 * @param string The string to read from
	 * @param config The configuration to use
	 * @throws NullPointerException If the string or configuration is null
	 */
	public YamlReader(@NotNull String string, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Yaml config must not be null");
		this.reader = new StringReader(Objects.requireNonNull(string, "String must not be null"));
		this.splitLines(string);
	}

	/**
	 * Constructs a new yaml reader with the given input and the default configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public YamlReader(@NotNull InputProvider input) {
		this(input, YamlConfig.DEFAULT);
	}

	/**
	 * Constructs a new yaml reader with the given input and configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @param config The configuration to use
	 * @throws NullPointerException If the input or configuration is null
	 */
	public YamlReader(@NotNull InputProvider input, @NotNull YamlConfig config) {
		this.config = Objects.requireNonNull(config, "Yaml config must not be null");
		StringReader tempReader = new StringReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
		String content = tempReader.readRemaining();
		this.reader = new StringReader(content);
		this.splitLines(content);
	}

	/**
	 * Splits the content into lines for indentation-based parsing.<br>
	 *
	 * @param content The content to split
	 */
	private void splitLines(@NotNull String content) {
		String[] split = content.split("\r\n|\r|\n", -1);
		this.lines.addAll(Arrays.asList(split));
	}

	/**
	 * Reads the next yaml element from the input.<br>
	 * <p>
	 *     In strict mode, this reader only accepts one yaml document per input.
	 * </p>
	 *
	 * @return The next yaml element
	 * @throws YamlSyntaxException If the yaml is invalid
	 */
	public @NotNull YamlElement readYaml() {
		this.anchors.clear();
		this.lineIndex = 0;

		// Skip document start marker if present
		this.skipEmptyLinesAndComments();
		if (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex).trim();
			if (line.equals("---")) {
				this.lineIndex++;
			}
		}

		this.skipEmptyLinesAndComments();
		if (this.lineIndex >= this.lines.size()) {
			return YamlNull.INSTANCE;
		}

		YamlElement element = this.readElement(0);

		// Skip document end marker if present
		this.skipEmptyLinesAndComments();
		if (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex).trim();
			if (line.equals("...")) {
				this.lineIndex++;
			}
		}

		this.skipEmptyLinesAndComments();
		if (this.config.strict() && this.lineIndex < this.lines.size()) {
			String remaining = this.lines.get(this.lineIndex).trim();
			if (!remaining.isEmpty() && !remaining.startsWith("#")) {
				throw new YamlSyntaxException("Invalid yaml, expected end of input but got: '" + remaining + "'");
			}
		}

		return element;
	}

	/**
	 * Skips empty lines and comment-only lines.<br>
	 */
	private void skipEmptyLinesAndComments() {
		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex).trim();
			if (line.isEmpty() || line.startsWith("#")) {
				this.lineIndex++;
			} else {
				break;
			}
		}
	}

	/**
	 * Gets the indentation level of a line.<br>
	 *
	 * @param line The line to check
	 * @return The number of leading spaces
	 */
	private int getIndent(@NotNull String line) {
		int indent = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == ' ') {
				indent++;
			} else if (line.charAt(i) == '\t') {
				throw new YamlSyntaxException("Tabs are not allowed for indentation in yaml");
			} else {
				break;
			}
		}
		return indent;
	}

	/**
	 * Reads a yaml element at the given indentation level.<br>
	 *
	 * @param expectedIndent The expected minimum indentation level
	 * @return The parsed yaml element
	 */
	private @NotNull YamlElement readElement(int expectedIndent) {
		this.skipEmptyLinesAndComments();
		if (this.lineIndex >= this.lines.size()) {
			return YamlNull.INSTANCE;
		}

		String line = this.lines.get(this.lineIndex);
		String trimmed = line.trim();

		// Check for flow style collections
		if (trimmed.startsWith("{")) {
			return this.readFlowMapping();
		}
		if (trimmed.startsWith("[")) {
			return this.readFlowSequence();
		}

		// Check for alias
		if (trimmed.startsWith("*")) {
			this.lineIndex++;
			String anchorName = this.extractAnchorName(trimmed.substring(1));
			YamlAlias alias = new YamlAlias(anchorName);
			if (this.config.resolveAnchors()) {
				YamlElement resolved = this.anchors.get(anchorName);
				if (resolved == null) {
					throw new YamlSyntaxException("Undefined anchor: '" + anchorName + "'");
				}
				return resolved;
			}
			return alias;
		}

		// Check for anchor definition
		String anchorName = null;
		if (trimmed.startsWith("&")) {
			int spaceIndex = trimmed.indexOf(' ');
			if (spaceIndex == -1) {
				// Anchor on its own line - element follows
				anchorName = this.extractAnchorName(trimmed.substring(1));
				this.lineIndex++;
				YamlElement element = this.readElement(expectedIndent);
				YamlAnchor anchor = new YamlAnchor(anchorName, element);
				this.anchors.put(anchorName, element);
				return this.config.resolveAnchors() ? element : anchor;
			} else {
				anchorName = this.extractAnchorName(trimmed.substring(1, spaceIndex));
				trimmed = trimmed.substring(spaceIndex + 1).trim();
				// Update the line for further processing
				line = " ".repeat(this.getIndent(line)) + trimmed;
			}
		}

		// Check for block sequence (- item)
		if (trimmed.startsWith("- ") || trimmed.equals("-")) {
			YamlElement element = this.readBlockSequence(expectedIndent);
			if (anchorName != null) {
				this.anchors.put(anchorName, element);
				return this.config.resolveAnchors() ? element : new YamlAnchor(anchorName, element);
			}
			return element;
		}

		// Check for mapping (key: value)
		int colonIndex = this.findKeyColonIndex(trimmed);
		if (colonIndex > 0) {
			YamlElement element = this.readBlockMapping(expectedIndent);
			if (anchorName != null) {
				this.anchors.put(anchorName, element);
				return this.config.resolveAnchors() ? element : new YamlAnchor(anchorName, element);
			}
			return element;
		}

		// It's a scalar value
		this.lineIndex++;
		YamlElement element = this.parseScalar(trimmed);
		if (anchorName != null) {
			this.anchors.put(anchorName, element);
			return this.config.resolveAnchors() ? element : new YamlAnchor(anchorName, element);
		}
		return element;
	}

	/**
	 * Extracts a valid anchor name, stopping at whitespace or invalid characters.<br>
	 *
	 * @param text The text potentially containing an anchor name
	 * @return The extracted anchor name
	 */
	private @NotNull String extractAnchorName(@NotNull String text) {
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
				name.append(c);
			} else {
				break;
			}
		}
		if (name.isEmpty()) {
			throw new YamlSyntaxException("Invalid anchor name in: '" + text + "'");
		}
		return name.toString();
	}

	/**
	 * Finds the index of the colon that separates key from value.<br>
	 * Handles quoted keys properly.<br>
	 *
	 * @param line The line to search
	 * @return The index of the colon, or -1 if not found
	 */
	private int findKeyColonIndex(@NotNull String line) {
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		boolean escaped = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (escaped) {
				escaped = false;
				continue;
			}

			if (c == '\\') {
				escaped = true;
				continue;
			}

			if (c == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
			} else if (c == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
			} else if (c == ':' && !inSingleQuote && !inDoubleQuote) {
				// Check if followed by space, newline, or end of line
				if (i + 1 >= line.length() || Character.isWhitespace(line.charAt(i + 1))) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Reads a block-style mapping.<br>
	 *
	 * @param baseIndent The base indentation level
	 * @return The parsed yaml mapping
	 */
	private @NotNull YamlMapping readBlockMapping(int baseIndent) {
		YamlMapping mapping = new YamlMapping();
		int mappingIndent = -1;

		while (this.lineIndex < this.lines.size()) {
			this.skipEmptyLinesAndComments();
			if (this.lineIndex >= this.lines.size()) {
				break;
			}

			String line = this.lines.get(this.lineIndex);
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}

			int currentIndent = this.getIndent(line);

			// First entry establishes the mapping indent
			if (mappingIndent == -1) {
				if (currentIndent < baseIndent) {
					break;
				}
				mappingIndent = currentIndent;
			}

			// Check if we've moved out of this mapping
			if (currentIndent < mappingIndent) {
				break;
			}

			// Check for document markers
			String trimmed = line.trim();
			if (trimmed.equals("---") || trimmed.equals("...")) {
				break;
			}

			// Must be at the same indentation level for this mapping
			if (currentIndent != mappingIndent) {
				throw new YamlSyntaxException("Inconsistent indentation in mapping at line " + (this.lineIndex + 1));
			}

			// Parse key-value pair
			int colonIndex = this.findKeyColonIndex(trimmed);
			if (colonIndex == -1) {
				throw new YamlSyntaxException("Expected key-value pair but found: '" + trimmed + "' at line " + (this.lineIndex + 1));
			}

			String keyStr = trimmed.substring(0, colonIndex).trim();
			String valueStr = trimmed.substring(colonIndex + 1).trim();

			// Handle anchor on key
			String keyAnchor = null;
			if (keyStr.startsWith("&")) {
				int spaceIdx = keyStr.indexOf(' ');
				if (spaceIdx > 0) {
					keyAnchor = this.extractAnchorName(keyStr.substring(1, spaceIdx));
					keyStr = keyStr.substring(spaceIdx + 1).trim();
				}
			}

			// Parse the key
			String key = this.parseKeyString(keyStr);

			// Check for duplicate keys
			if (!this.config.allowDuplicateKeys() && mapping.containsKey(key)) {
				throw new YamlSyntaxException("Duplicate key '" + key + "' found at line " + (this.lineIndex + 1));
			}

			this.lineIndex++;

			// Parse the value
			YamlElement value;
			if (valueStr.isEmpty()) {
				// Value is on next line(s)
				this.skipEmptyLinesAndComments();
				if (this.lineIndex < this.lines.size()) {
					String nextLine = this.lines.get(this.lineIndex);
					int nextIndent = this.getIndent(nextLine);
					if (nextIndent > mappingIndent) {
						value = this.readElement(nextIndent);
					} else {
						value = YamlNull.INSTANCE;
					}
				} else {
					value = YamlNull.INSTANCE;
				}
			} else {
				// Value is inline
				value = this.parseInlineValue(valueStr, mappingIndent);
			}

			if (keyAnchor != null) {
				this.anchors.put(keyAnchor, new YamlScalar(key));
			}

			mapping.add(key, value);
		}

		return mapping;
	}

	/**
	 * Parses a key string, removing quotes if present.<br>
	 *
	 * @param keyStr The key string
	 * @return The parsed key
	 */
	private @NotNull String parseKeyString(@NotNull String keyStr) {
		if (keyStr.isEmpty()) {
			throw new YamlSyntaxException("Empty key is not allowed");
		}

		// Handle quoted keys
		if ((keyStr.startsWith("\"") && keyStr.endsWith("\"")) ||
			(keyStr.startsWith("'") && keyStr.endsWith("'"))) {
			return keyStr.substring(1, keyStr.length() - 1);
		}

		return keyStr;
	}

	/**
	 * Parses an inline value (value on the same line as the key).<br>
	 *
	 * @param valueStr The value string
	 * @param currentIndent The current indentation level
	 * @return The parsed yaml element
	 */
	private @NotNull YamlElement parseInlineValue(@NotNull String valueStr, int currentIndent) {
		// Remove trailing comment
		valueStr = this.removeTrailingComment(valueStr);

		// Check for anchor
		String anchorName = null;
		if (valueStr.startsWith("&")) {
			int spaceIndex = valueStr.indexOf(' ');
			if (spaceIndex > 0) {
				anchorName = this.extractAnchorName(valueStr.substring(1, spaceIndex));
				valueStr = valueStr.substring(spaceIndex + 1).trim();
			}
		}

		// Check for alias
		if (valueStr.startsWith("*")) {
			String aliasName = this.extractAnchorName(valueStr.substring(1));
			if (this.config.resolveAnchors()) {
				YamlElement resolved = this.anchors.get(aliasName);
				if (resolved == null) {
					throw new YamlSyntaxException("Undefined anchor: '" + aliasName + "'");
				}
				return resolved;
			}
			return new YamlAlias(aliasName);
		}

		// Check for flow collections
		if (valueStr.startsWith("{")) {
			YamlElement element = this.parseFlowMappingFromString(valueStr);
			if (anchorName != null) {
				this.anchors.put(anchorName, element);
				return this.config.resolveAnchors() ? element : new YamlAnchor(anchorName, element);
			}
			return element;
		}
		if (valueStr.startsWith("[")) {
			YamlElement element = this.parseFlowSequenceFromString(valueStr);
			if (anchorName != null) {
				this.anchors.put(anchorName, element);
				return this.config.resolveAnchors() ? element : new YamlAnchor(anchorName, element);
			}
			return element;
		}

		// Check for multi-line string indicators
		if (valueStr.equals("|") || valueStr.startsWith("| ") || valueStr.equals("|+") || valueStr.equals("|-")) {
			return this.readLiteralBlockScalar(currentIndent, valueStr);
		}
		if (valueStr.equals(">") || valueStr.startsWith("> ") || valueStr.equals(">+") || valueStr.equals(">-")) {
			return this.readFoldedBlockScalar(currentIndent, valueStr);
		}

		// Parse as scalar
		YamlElement element = this.parseScalar(valueStr);
		if (anchorName != null) {
			this.anchors.put(anchorName, element);
			return this.config.resolveAnchors() ? element : new YamlAnchor(anchorName, element);
		}
		return element;
	}

	/**
	 * Removes trailing comments from a value string.<br>
	 *
	 * @param value The value string
	 * @return The value without trailing comments
	 */
	private @NotNull String removeTrailingComment(@NotNull String value) {
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		boolean escaped = false;

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (escaped) {
				escaped = false;
				continue;
			}

			if (c == '\\') {
				escaped = true;
				continue;
			}

			if (c == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
			} else if (c == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
			} else if (c == '#' && !inSingleQuote && !inDoubleQuote) {
				// Found a comment
				return value.substring(0, i).trim();
			}
		}
		return value;
	}

	/**
	 * Reads a block-style sequence.<br>
	 *
	 * @param baseIndent The base indentation level
	 * @return The parsed yaml sequence
	 */
	private @NotNull YamlSequence readBlockSequence(int baseIndent) {
		YamlSequence sequence = new YamlSequence();
		int sequenceIndent = -1;

		while (this.lineIndex < this.lines.size()) {
			this.skipEmptyLinesAndComments();
			if (this.lineIndex >= this.lines.size()) {
				break;
			}

			String line = this.lines.get(this.lineIndex);
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}

			int currentIndent = this.getIndent(line);
			String trimmed = line.trim();

			// First entry establishes the sequence indent
			if (sequenceIndent == -1) {
				if (currentIndent < baseIndent) {
					break;
				}
				sequenceIndent = currentIndent;
			}

			// Check if we've moved out of this sequence
			if (currentIndent < sequenceIndent) {
				break;
			}

			// Must start with - at this level
			if (!trimmed.startsWith("-")) {
				break;
			}

			if (currentIndent != sequenceIndent) {
				throw new YamlSyntaxException("Inconsistent indentation in sequence at line " + (this.lineIndex + 1));
			}

			// Parse the sequence item
			String itemValue;
			if (trimmed.equals("-")) {
				itemValue = "";
			} else {
				itemValue = trimmed.substring(1).trim();
			}

			this.lineIndex++;

			YamlElement element;
			if (itemValue.isEmpty()) {
				// Value is on next line(s)
				this.skipEmptyLinesAndComments();
				if (this.lineIndex < this.lines.size()) {
					String nextLine = this.lines.get(this.lineIndex);
					int nextIndent = this.getIndent(nextLine);
					if (nextIndent > sequenceIndent) {
						element = this.readElement(nextIndent);
					} else {
						element = YamlNull.INSTANCE;
					}
				} else {
					element = YamlNull.INSTANCE;
				}
			} else {
				// Check if it's an inline mapping (key: value after -)
				int colonIndex = this.findKeyColonIndex(itemValue);
				if (colonIndex > 0) {
					// It's a mapping - we need to go back and read it properly
					this.lineIndex--;
					// Create a pseudo-line at the item's position
					element = this.readNestedMappingInSequence(sequenceIndent + 2, itemValue);
				} else {
					element = this.parseInlineValue(itemValue, sequenceIndent);
				}
			}

			sequence.add(element);
		}

		return sequence;
	}

	/**
	 * Reads a nested mapping that appears inline after a sequence dash.<br>
	 *
	 * @param expectedIndent The expected indentation
	 * @param firstPair The first key-value pair string
	 * @return The parsed mapping
	 */
	private @NotNull YamlElement readNestedMappingInSequence(int expectedIndent, @NotNull String firstPair) {
		YamlMapping mapping = new YamlMapping();

		// Parse the first pair
		int colonIndex = this.findKeyColonIndex(firstPair);
		String keyStr = firstPair.substring(0, colonIndex).trim();
		String valueStr = firstPair.substring(colonIndex + 1).trim();

		String key = this.parseKeyString(keyStr);
		this.lineIndex++;

		YamlElement value;
		if (valueStr.isEmpty()) {
			this.skipEmptyLinesAndComments();
			if (this.lineIndex < this.lines.size()) {
				String nextLine = this.lines.get(this.lineIndex);
				int nextIndent = this.getIndent(nextLine);
				if (nextIndent >= expectedIndent) {
					value = this.readElement(nextIndent);
				} else {
					value = YamlNull.INSTANCE;
				}
			} else {
				value = YamlNull.INSTANCE;
			}
		} else {
			value = this.parseInlineValue(valueStr, expectedIndent);
		}

		mapping.add(key, value);

		// Read any additional pairs at the same or greater indentation
		while (this.lineIndex < this.lines.size()) {
			this.skipEmptyLinesAndComments();
			if (this.lineIndex >= this.lines.size()) {
				break;
			}

			String line = this.lines.get(this.lineIndex);
			if (line.trim().isEmpty()) {
				this.lineIndex++;
				continue;
			}

			int currentIndent = this.getIndent(line);
			String trimmed = line.trim();

			// Check if we're still in the nested mapping
			if (currentIndent < expectedIndent || trimmed.startsWith("-")) {
				break;
			}

			colonIndex = this.findKeyColonIndex(trimmed);
			if (colonIndex == -1) {
				break;
			}

			keyStr = trimmed.substring(0, colonIndex).trim();
			valueStr = trimmed.substring(colonIndex + 1).trim();
			key = this.parseKeyString(keyStr);

			if (!this.config.allowDuplicateKeys() && mapping.containsKey(key)) {
				throw new YamlSyntaxException("Duplicate key '" + key + "' found at line " + (this.lineIndex + 1));
			}

			this.lineIndex++;

			if (valueStr.isEmpty()) {
				this.skipEmptyLinesAndComments();
				if (this.lineIndex < this.lines.size()) {
					String nextLine = this.lines.get(this.lineIndex);
					int nextIndent = this.getIndent(nextLine);
					if (nextIndent > currentIndent) {
						value = this.readElement(nextIndent);
					} else {
						value = YamlNull.INSTANCE;
					}
				} else {
					value = YamlNull.INSTANCE;
				}
			} else {
				value = this.parseInlineValue(valueStr, currentIndent);
			}

			mapping.add(key, value);
		}

		return mapping;
	}

	/**
	 * Reads a flow-style mapping from the current line position.<br>
	 *
	 * @return The parsed yaml mapping
	 */
	private @NotNull YamlMapping readFlowMapping() {
		StringBuilder sb = new StringBuilder();
		int braceCount = 0;

		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex).trim();
			this.lineIndex++;

			for (char c : line.toCharArray()) {
				sb.append(c);
				if (c == '{') {
					braceCount++;
				} else if (c == '}') {
					braceCount--;
					if (braceCount == 0) {
						return this.parseFlowMappingFromString(sb.toString());
					}
				}
			}
			sb.append(' ');
		}

		throw new YamlSyntaxException("Unclosed flow mapping");
	}

	/**
	 * Parses a flow-style mapping from a string.<br>
	 *
	 * @param str The string containing the flow mapping
	 * @return The parsed yaml mapping
	 */
	private @NotNull YamlMapping parseFlowMappingFromString(@NotNull String str) {
		str = str.trim();
		if (!str.startsWith("{") || !str.endsWith("}")) {
			throw new YamlSyntaxException("Invalid flow mapping: " + str);
		}

		YamlMapping mapping = new YamlMapping();
		String content = str.substring(1, str.length() - 1).trim();

		if (content.isEmpty()) {
			return mapping;
		}

		List<String> pairs = this.splitFlowItems(content);
		for (String pair : pairs) {
			pair = pair.trim();
			if (pair.isEmpty()) {
				continue;
			}

			int colonIndex = this.findKeyColonIndex(pair);
			if (colonIndex == -1) {
				throw new YamlSyntaxException("Invalid key-value pair in flow mapping: " + pair);
			}

			String key = this.parseKeyString(pair.substring(0, colonIndex).trim());
			String valueStr = pair.substring(colonIndex + 1).trim();

			if (!this.config.allowDuplicateKeys() && mapping.containsKey(key)) {
				throw new YamlSyntaxException("Duplicate key '" + key + "' in flow mapping");
			}

			YamlElement value = this.parseFlowValue(valueStr);
			mapping.add(key, value);
		}

		return mapping;
	}

	/**
	 * Reads a flow-style sequence from the current line position.<br>
	 *
	 * @return The parsed yaml sequence
	 */
	private @NotNull YamlSequence readFlowSequence() {
		StringBuilder sb = new StringBuilder();
		int bracketCount = 0;

		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex).trim();
			this.lineIndex++;

			for (char c : line.toCharArray()) {
				sb.append(c);
				if (c == '[') {
					bracketCount++;
				} else if (c == ']') {
					bracketCount--;
					if (bracketCount == 0) {
						return this.parseFlowSequenceFromString(sb.toString());
					}
				}
			}
			sb.append(' ');
		}

		throw new YamlSyntaxException("Unclosed flow sequence");
	}

	/**
	 * Parses a flow-style sequence from a string.<br>
	 *
	 * @param str The string containing the flow sequence
	 * @return The parsed yaml sequence
	 */
	private @NotNull YamlSequence parseFlowSequenceFromString(@NotNull String str) {
		str = str.trim();
		if (!str.startsWith("[") || !str.endsWith("]")) {
			throw new YamlSyntaxException("Invalid flow sequence: " + str);
		}

		YamlSequence sequence = new YamlSequence();
		String content = str.substring(1, str.length() - 1).trim();

		if (content.isEmpty()) {
			return sequence;
		}

		List<String> items = this.splitFlowItems(content);
		for (String item : items) {
			item = item.trim();
			if (item.isEmpty()) {
				continue;
			}
			sequence.add(this.parseFlowValue(item));
		}

		return sequence;
	}

	/**
	 * Splits flow collection content by commas, respecting nesting and quotes.<br>
	 *
	 * @param content The content to split
	 * @return List of items
	 */
	private @NotNull List<String> splitFlowItems(@NotNull String content) {
		List<String> items = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		int braceCount = 0;
		int bracketCount = 0;
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		boolean escaped = false;

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);

			if (escaped) {
				current.append(c);
				escaped = false;
				continue;
			}

			if (c == '\\') {
				current.append(c);
				escaped = true;
				continue;
			}

			if (c == '\'' && !inDoubleQuote) {
				inSingleQuote = !inSingleQuote;
			} else if (c == '"' && !inSingleQuote) {
				inDoubleQuote = !inDoubleQuote;
			}

			if (!inSingleQuote && !inDoubleQuote) {
				if (c == '{') {
					braceCount++;
				} else if (c == '}') {
					braceCount--;
				} else if (c == '[') {
					bracketCount++;
				} else if (c == ']') {
					bracketCount--;
				} else if (c == ',' && braceCount == 0 && bracketCount == 0) {
					items.add(current.toString());
					current = new StringBuilder();
					continue;
				}
			}

			current.append(c);
		}

		if (!current.isEmpty()) {
			items.add(current.toString());
		}

		return items;
	}

	/**
	 * Parses a value in flow context.<br>
	 *
	 * @param valueStr The value string
	 * @return The parsed yaml element
	 */
	private @NotNull YamlElement parseFlowValue(@NotNull String valueStr) {
		valueStr = valueStr.trim();

		// Check for alias
		if (valueStr.startsWith("*")) {
			String aliasName = this.extractAnchorName(valueStr.substring(1));
			if (this.config.resolveAnchors()) {
				YamlElement resolved = this.anchors.get(aliasName);
				if (resolved == null) {
					throw new YamlSyntaxException("Undefined anchor: '" + aliasName + "'");
				}
				return resolved;
			}
			return new YamlAlias(aliasName);
		}

		// Check for nested flow collections
		if (valueStr.startsWith("{")) {
			return this.parseFlowMappingFromString(valueStr);
		}
		if (valueStr.startsWith("[")) {
			return this.parseFlowSequenceFromString(valueStr);
		}

		return this.parseScalar(valueStr);
	}

	/**
	 * Reads a literal block scalar (|).<br>
	 *
	 * @param baseIndent The base indentation level
	 * @param indicator The block indicator (|, |+, |-)
	 * @return The parsed scalar
	 */
	private @NotNull YamlScalar readLiteralBlockScalar(int baseIndent, @NotNull String indicator) {
		boolean keepTrailing = indicator.contains("+");
		boolean stripTrailing = indicator.contains("-");

		StringBuilder content = new StringBuilder();
		int contentIndent = -1;

		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex);

			// Empty lines are preserved in literal blocks
			if (line.trim().isEmpty()) {
				content.append(System.lineSeparator());
				this.lineIndex++;
				continue;
			}

			int currentIndent = this.getIndent(line);

			// First content line establishes the indent
			if (contentIndent == -1) {
				if (currentIndent <= baseIndent) {
					break;
				}
				contentIndent = currentIndent;
			}

			// Check if we've moved out of the block
			if (currentIndent < contentIndent && !line.trim().isEmpty()) {
				break;
			}

			// Add the line content (minus the indent)
			if (currentIndent >= contentIndent) {
				if (!content.isEmpty()) {
					content.append(System.lineSeparator());
				}
				content.append(line.substring(contentIndent));
			}

			this.lineIndex++;
		}

		String result = content.toString();

		// Handle trailing newlines based on chomping indicator
		if (stripTrailing) {
			result = result.replaceAll("[\r\n]+$", "");
		} else if (!keepTrailing) {
			// Default: single trailing newline
			result = result.replaceAll("[\r\n]+$", "") + System.lineSeparator();
			if (result.equals(System.lineSeparator())) {
				result = "";
			}
		}

		return new YamlScalar(result);
	}

	/**
	 * Reads a folded block scalar (>).<br>
	 *
	 * @param baseIndent The base indentation level
	 * @param indicator The block indicator (>, >+, >-)
	 * @return The parsed scalar
	 */
	private @NotNull YamlScalar readFoldedBlockScalar(int baseIndent, @NotNull String indicator) {
		boolean keepTrailing = indicator.contains("+");
		boolean stripTrailing = indicator.contains("-");

		StringBuilder content = new StringBuilder();
		int contentIndent = -1;
		boolean lastWasEmpty = false;

		while (this.lineIndex < this.lines.size()) {
			String line = this.lines.get(this.lineIndex);

			// Handle empty lines
			if (line.trim().isEmpty()) {
				if (!content.isEmpty()) {
					content.append(System.lineSeparator());
				}
				lastWasEmpty = true;
				this.lineIndex++;
				continue;
			}

			int currentIndent = this.getIndent(line);

			// First content line establishes the indent
			if (contentIndent == -1) {
				if (currentIndent <= baseIndent) {
					break;
				}
				contentIndent = currentIndent;
			}

			// Check if we've moved out of the block
			if (currentIndent < contentIndent) {
				break;
			}

			// Add space or newline based on context
			if (!content.isEmpty() && !lastWasEmpty) {
				content.append(" ");
			}

			content.append(line.substring(contentIndent));
			lastWasEmpty = false;
			this.lineIndex++;
		}

		String result = content.toString();

		// Handle trailing newlines based on chomping indicator
		if (stripTrailing) {
			result = result.replaceAll("[\r\n\\s]+$", "");
		} else if (!keepTrailing) {
			result = result.stripTrailing();
			if (!result.isEmpty()) {
				result = result + System.lineSeparator();
			}
		}

		return new YamlScalar(result);
	}

	/**
	 * Parses a scalar value from a string.<br>
	 *
	 * @param value The value string
	 * @return The parsed yaml element
	 */
	private @NotNull YamlElement parseScalar(@NotNull String value) {
		value = value.trim();

		if (value.isEmpty()) {
			return YamlNull.INSTANCE;
		}

		// Handle quoted strings
		if ((value.startsWith("\"") && value.endsWith("\"")) ||
			(value.startsWith("'") && value.endsWith("'"))) {
			String unquoted = value.substring(1, value.length() - 1);
			return new YamlScalar(this.processEscapeSequences(unquoted, value.charAt(0)));
		}

		// Handle null values
		String lower = value.toLowerCase();
		if (lower.equals("null") || lower.equals("~") || lower.equals("")) {
			return YamlNull.INSTANCE;
		}

		// Handle boolean values
		if (lower.equals("true") || lower.equals("yes") || lower.equals("on")) {
			return new YamlScalar(true);
		}
		if (lower.equals("false") || lower.equals("no") || lower.equals("off")) {
			return new YamlScalar(false);
		}

		// In strict mode, only accept true/false for booleans
		if (this.config.strict()) {
			if (lower.equals("yes") || lower.equals("no") || lower.equals("on") || lower.equals("off")) {
				return new YamlScalar(value);
			}
		}

		// Try to parse as number
		Number number = this.tryParseNumber(value);
		if (number != null) {
			return new YamlScalar(number);
		}

		// Default to string
		return new YamlScalar(value);
	}

	/**
	 * Processes escape sequences in a quoted string.<br>
	 *
	 * @param value The string value
	 * @param quoteChar The quote character used
	 * @return The processed string
	 */
	private @NotNull String processEscapeSequences(@NotNull String value, char quoteChar) {
		if (quoteChar == '\'') {
			// Single-quoted strings only escape ''
			return value.replace("''", "'");
		}

		// Double-quoted strings support escape sequences
		StringBuilder result = new StringBuilder();
		boolean escaped = false;

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (escaped) {
				switch (c) {
					case 'n' -> result.append('\n');
					case 'r' -> result.append('\r');
					case 't' -> result.append('\t');
					case '\\' -> result.append('\\');
					case '"' -> result.append('"');
					case '0' -> result.append('\0');
					case 'a' -> result.append('\u0007');
					case 'b' -> result.append('\b');
					case 'f' -> result.append('\f');
					case 'v' -> result.append('\u000B');
					case 'e' -> result.append('\u001B');
					case ' ' -> result.append(' ');
					case 'N' -> result.append('\u0085');
					case '_' -> result.append('\u00A0');
					case 'L' -> result.append('\u2028');
					case 'P' -> result.append('\u2029');
					case 'x' -> {
						// \xNN - 2 hex digits
						if (i + 2 < value.length()) {
							String hex = value.substring(i + 1, i + 3);
							try {
								result.append((char) Integer.parseInt(hex, 16));
								i += 2;
							} catch (NumberFormatException e) {
								result.append('x');
							}
						} else {
							result.append('x');
						}
					}
					case 'u' -> {
						// \\uNNNN - 4 hex digits
						if (i + 4 < value.length()) {
							String hex = value.substring(i + 1, i + 5);
							try {
								result.append((char) Integer.parseInt(hex, 16));
								i += 4;
							} catch (NumberFormatException e) {
								result.append('u');
							}
						} else {
							result.append('u');
						}
					}
					default -> result.append(c);
				}
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
			} else {
				result.append(c);
			}
		}

		return result.toString();
	}

	/**
	 * Tries to parse a value as a number.<br>
	 *
	 * @param value The value to parse
	 * @return The parsed number, or null if not a valid number
	 */
	private @Nullable Number tryParseNumber(@NotNull String value) {
		if (value.isEmpty()) {
			return null;
		}

		// Handle special float values
		String lower = value.toLowerCase();
		if (lower.equals(".inf") || lower.equals("+.inf")) {
			return Double.POSITIVE_INFINITY;
		}
		if (lower.equals("-.inf")) {
			return Double.NEGATIVE_INFINITY;
		}
		if (lower.equals(".nan")) {
			return Double.NaN;
		}

		// Handle hexadecimal
		if (value.startsWith("0x") || value.startsWith("0X")) {
			try {
				return Long.parseLong(value.substring(2), 16);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		// Handle octal
		if (value.startsWith("0o") || value.startsWith("0O")) {
			try {
				return Long.parseLong(value.substring(2), 8);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		// Handle binary
		if (value.startsWith("0b") || value.startsWith("0B")) {
			try {
				return Long.parseLong(value.substring(2), 2);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		// Try parsing as integer first
		try {
			if (!value.contains(".") && !value.toLowerCase().contains("e")) {
				long longValue = Long.parseLong(value);
				if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
					return (int) longValue;
				}
				return longValue;
			}
		} catch (NumberFormatException ignored) {
		}

		// Try parsing as floating point
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		// StringReader doesn't need closing, but we implement the interface for consistency
	}
}
