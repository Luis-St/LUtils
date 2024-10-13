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

package net.luis.utils.io.data.properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.exception.IllegalLineReadException;
import net.luis.utils.io.data.properties.exception.IllegalPropertyKeyPartException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.data.DataInput;
import net.luis.utils.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Represents a reader for properties.<br>
 * This reader reads the properties from a defined input and returns them as a properties object.<br>
 * The base format of the properties is a key-value pair separated by a separator:<br>
 * <pre>{@code
 * <key><separator><value>
 * }</pre>
 * The line as a whole can contain multiple separators, but the first one will be used to split the key and value.<br>
 *
 *
 *
 *
 * @author Luis-St
 */
public class PropertyReader implements AutoCloseable {
	
	/**
	 * The pattern to check if a key is a compacted key.<br>
	 */
	private static final Pattern COMPACTED_KEY_PATTERN = Pattern.compile("^(.+?)\\.(\\[(.*)])\\.?(.*)$");
	/**
	 * The pattern to check if a key is a variable key.<br>
	 */
	private static final Pattern VARIABLE_KEY_PATTERN = Pattern.compile("^(.+?)\\.(\\$\\{(.*)})\\.?(.*)$");
	
	/**
	 * A cache for all properties that have been read.<br>
	 */
	private final Map<String, String> properties = Maps.newLinkedHashMap();
	/**
	 * The configuration for this reader.<br>
	 */
	private final PropertyConfig config;
	/**
	 * The internal io reader to read the properties.<br>
	 */
	private final BufferedReader reader;
	
	/**
	 * Constructs a new property reader with the given input and the default configuration.<br>
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public PropertyReader(@NotNull InputProvider input) {
		this(input, PropertyConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new property reader with the given input and configuration.<br>
	 * @param input The input to create the reader for
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the input or the configuration is null
	 */
	public PropertyReader(@NotNull InputProvider input, @NotNull PropertyConfig config) {
		this.config = Objects.requireNonNull(config, "Property config must not be null");
		this.reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	//region Static helper methods
	
	/**
	 * Reads the content of the give scoped string reader and returns it without the scope.<br>
	 * @param reader The reader to read the content from
	 * @param scope The scope which is expected
	 * @return The content of the reader without the scope
	 */
	private static @NotNull String readScopeExclude(@NotNull ScopedStringReader reader, ScopedStringReader.@NotNull StringScope scope) {
		Objects.requireNonNull(reader, "Reader must not be null");
		Objects.requireNonNull(scope, "Scope must not be null");
		String result = reader.readScope(scope);
		return result.substring(1, result.length() - 1);
	}
	//endregion
	
	/**
	 * Reads the properties from the input and returns them as a properties object.<br>
	 * @return The properties that have been read
	 */
	public @NotNull Properties readProperties() {
		List<Property> properties = Lists.newArrayList();
		try {
			while (true) {
				String line = this.reader.readLine();
				if (line == null) {
					break;
				}
				List<Property> parsedProperties = this.parseLine(line);
				if (parsedProperties.isEmpty()) {
					continue;
				}
				properties.addAll(parsedProperties);
				properties.forEach(property -> this.properties.put(property.getKey(), property.getRawValue()));
			}
		} catch (IOException e) {
			this.config.errorAction().handle(e);
		}
		return new Properties(properties);
	}
	
	/**
	 * Parses the given line and returns the properties that have been read.<br>
	 * If the line is empty or a comment, an empty list will be returned.<br>
	 * In the case of an error, the error action of the configuration will be executed.<br>
	 * @param rawLine The line to parse
	 * @return The properties that have been read
	 * @throws NullPointerException If the line is null
	 * @throws IllegalLineReadException If the line is not parsable (depends on the configuration)
	 */
	private @NotNull @Unmodifiable List<Property> parseLine(@NotNull String rawLine) {
		Objects.requireNonNull(rawLine, "Line must not be null");
		String line = rawLine.stripLeading();
		if (line.isBlank() || this.config.commentCharacters().contains(line.charAt(0))) {
			return List.of();
		}
		char separator = this.config.separator();
		if (!StringUtils.containsNotEscaped(line, separator)) {
			this.config.errorAction().handle(new IllegalLineReadException("No separator (" + separator + ") found in line: " + line));
		}
		String[] parts = StringUtils.splitNotEscaped(line, separator);
		if (parts.length == 0) {
			this.config.errorAction().handle(new IllegalLineReadException("Unable to split line at separator (" + separator + "): " + line));
		}
		try {
			return this.parseProperty(parts[0], this.getValuePart(parts));
		} catch (IOException e) {
			this.config.errorAction().handle(e);
			return List.of();
		}
	}
	
	/**
	 * Returns the value part of the given parts.<br>
	 * If the line contains multiple separators, the value part will be concatenated with the separator.<br>
	 * @param valueParts The parts of the line that were split at the separator
	 * @return The value part of the line
	 * @throws NullPointerException If the value parts are null
	 */
	private @NotNull String getValuePart(String @NotNull [] valueParts) {
		Objects.requireNonNull(valueParts, "Value parts must not be null");
		if (valueParts.length == 1) {
			return "";
		} else if (valueParts.length == 2) {
			return valueParts[1];
		} else {
			StringBuilder builder = new StringBuilder(valueParts[1]);
			for (int i = 2; i < valueParts.length; i++) {
				builder.append(this.config.separator()).append(valueParts[i]);
			}
			return builder.toString();
		}
	}
	
	/**
	 * Parses the given key and value and returns the properties that have been read.<br>
	 * If advanced parsing is enabled, the key can be a compacted or variable key.<br>
	 * @param rawKey
	 * @param rawValue
	 * @return
	 * @throws IOException
	 */
	private @NotNull @Unmodifiable List<Property> parseProperty(@NotNull String rawKey, @NotNull String rawValue) throws IOException {
		Objects.requireNonNull(rawKey, "Key must not be null");
		Objects.requireNonNull(rawValue, "Value must not be null");
		int alignment = this.getWhitespaceAlignmentCount(rawKey);
		String key = this.removeAlignment(rawKey, alignment, true);
		String value = this.removeAlignment(rawValue, alignment, false);
		if (this.isAdvancedKey(key) && !this.config.advancedParsing()) {
			this.config.errorAction().handle(new IllegalPropertyKeyPartException("Advanced key '" + key + "' is not allowed: '" + rawKey + "'"));
		}
		if (this.isAdvancedKey(key) && this.config.advancedParsing()) {
			List<Property> properties = this.parsePropertyAdvanced(key, value);
			this.config.ensureValueMatches(value);
			return properties;
		}
		return List.of(this.parsePropertySimple(key, value));
	}
	
	//region Helper methods
	private boolean isAdvancedKey(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return COMPACTED_KEY_PATTERN.matcher(key).matches() || VARIABLE_KEY_PATTERN.matcher(key).matches();
	}
	
	private int getWhitespaceAlignmentCount(@NotNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		int count = 0;
		for (int i = key.length() - 1; i >= 0; i--) {
			if (Character.isWhitespace(key.charAt(i))) {
				count++;
			} else {
				break;
			}
		}
		return count;
	}
	
	private @NotNull String removeAlignment(@NotNull String str, int alignment, boolean isKey) {
		Objects.requireNonNull(str, "String must not be null");
		if (0 >= alignment) {
			return str;
		}
		if (isKey) {
			return str.substring(0, str.length() - alignment);
		} else {
			return str.substring(alignment);
		}
	}
	//endregion
	
	//region Simple parsing
	private @NotNull Property parsePropertySimple(@NotNull String key, @NotNull String value) throws IOException {
		this.config.ensureKeyMatches(key);
		this.config.ensureValueMatches(value);
		return Property.of(key, value);
	}
	//endregion
	
	//region Advanced parsing
	private @NotNull @Unmodifiable List<Property> parsePropertyAdvanced(@NotNull String key, @NotNull String value) throws IOException {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		List<String> resolvedKeys = Lists.newArrayList();
		ScopedStringReader reader = new ScopedStringReader(key);
		while (reader.canRead()) {
			int position = reader.getIndex();
			String part = reader.readUntil('.');
			if (part.isEmpty()) {
				continue; // empty key part -> maybe ignoring it since [].[] is a valid key
			} else if (part.isBlank()) {
				throw new IllegalPropertyKeyPartException("Key part at position " + position + " must not be blank: '" + key + "'");
			}
			if (!reader.canRead()) {
				throw new IllegalPropertyKeyPartException("Key part ends with illegal character: '" + part.charAt(part.length() - 1) + "'");
			}
			char next = reader.peek();
			if (next == '.') {
				throw new IllegalPropertyKeyPartException("Key part at position " + position + " must not be empty: '" + key + "'");
			} else if (next == '$') {
				reader.skip(); // skip $
				position = reader.getIndex();
				String variable = readScopeExclude(reader, ScopedStringReader.CURLY_BRACKETS);
				if (variable.isEmpty()) {
					throw new IllegalPropertyKeyPartException("Variable key part '${" + variable + "}' at position " + position + " must not be empty: '" + key + "'");
				} else if (variable.isBlank()) {
					throw new IllegalPropertyKeyPartException("Variable key part '${" + variable + "}' at position " + position + " must not be blank: '" + key + "'");
				}
				resolvedKeys = this.extendResolvedKeys(resolvedKeys, this.parseVariableKeyPart(key, variable));
			} else if (next == '[') {
				position = reader.getIndex();
				String compacted = readScopeExclude(reader, ScopedStringReader.SQUARE_BRACKETS);
				if (compacted.isEmpty()) {
					throw new IllegalPropertyKeyPartException("Compacted key part '[" + compacted + "]' at position " + position + " must not be empty: '" + key + "'");
				} else if (compacted.isBlank()) {
					throw new IllegalPropertyKeyPartException("Compacted key part '[" + compacted + "]' at position " + position + " must not be blank: '" + key + "'");
				} else if (compacted.indexOf('|') == -1) {
					resolvedKeys = this.extendResolvedKeys(resolvedKeys, compacted);
				} else {
					resolvedKeys = this.extendResolvedKeys(resolvedKeys, this.parseCompactedKeyPart(key, compacted));
				}
			} else {
				resolvedKeys = this.extendResolvedKeys(resolvedKeys, part);
			}
		}
		
		List<Property> properties = Lists.newArrayList();
		for (String resolvedKey : resolvedKeys) {
			this.config.ensureKeyMatches(resolvedKey);
			properties.add(Property.of(resolvedKey, value));
		}
		return properties;
	}
	
	//region Compacted key parsing
	private String @NotNull [] parseCompactedKeyPart(@NotNull String key, @NotNull String compacted) throws IOException {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(compacted, "Compacted key part must not be null");
		String[] compactedValues = compacted.split("\\|");
		for (int i = 0; i < compactedValues.length; i++) {
			String compactedValue = compactedValues[i];
			if (compactedValue.isBlank()) {
				throw new IllegalPropertyKeyPartException("Value '" + compactedValue + "' in compacted key part '[" + compacted + "]' must not be blank: '" + key + "'");
			} else if (containsWhitespace(compactedValue)) {
				// illegal whitespace in compacted part
				throw new IllegalPropertyKeyPartException("Value '" + compactedValue + "' in compacted key part '[" + compacted + "]' must not contain whitespaces: '" + key + "'");
			} else if (compactedValue.indexOf('[') != -1) {
				// illegal nested compacted part
				throw new IllegalPropertyKeyPartException("Value '" + compactedValue + "' in compacted key part '[" + compacted + "]' must not be nested: '" + key + "'");
			} else if (compactedValue.charAt(0) == '$') {
				String variable = readScopeExclude(new ScopedStringReader(compactedValue.substring(1)), ScopedStringReader.CURLY_BRACKETS);
				compactedValues[i] = this.parseVariableKeyPart(key, variable);
			}
		}
		return compactedValues;
	}
	//endregion
	
	//region Variable key parsing
	private @NotNull String parseVariableKeyPart(@NotNull String key, @NotNull String variable) throws IOException {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(variable, "Variable key part must not be null");
		int questionMarkCount = countMatches(variable, '?');
		if (questionMarkCount == 0) {
			throw new IllegalPropertyKeyPartException("Variable key part '${" + variable + "}' must contain at least one question mark: '" + key + "'");
		} else if (questionMarkCount == 1 || questionMarkCount == 2) {
			return this.resolveVariableValue(key, variable, variable.split("\\?"));
		} else {
			throw new IllegalPropertyKeyPartException("Variable key part '${" + variable + "}' must contain at most two question marks: '" + key + "'");
		}
	}
	
	private @NotNull String resolveVariableValue(@NotNull String key, @NotNull String variable, String @NotNull [] parts) throws IOException {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(variable, "Variable key part must not be null");
		Objects.requireNonNull(parts, "Parts must not be null");
		String targetKey = this.getTargetKey(key, variable, parts);
		String targetType = parts[0];
		String value;
		if (isBlank(targetType) || equalsAnyIgnoreCase(targetType, "prop", "property")) {
			value = this.properties.get(targetKey);
		} else if (equalsAnyIgnoreCase(targetType, "sys", "system")) {
			value = System.getProperty(targetKey);
		} else if (equalsAnyIgnoreCase(targetType, "env", "environment")) {
			value = System.getenv(targetKey);
		} else {
			throw new IllegalPropertyKeyPartException("Target type '" + targetType + "' of variable key part '${" + variable + "}' must be 'prop', 'sys' or 'env': '" + key + "'");
		}
		if (value == null && parts.length == 3) {
			value = parts[2];
		}
		if (value == null) {
			throw new IllegalPropertyKeyPartException("Resolved value of variable key part '${" + variable + "}' must not be null: '" + key + "'");
		}
		return value;
	}
	
	private @NotNull String getTargetKey(@NotNull String key, @NotNull String variable, String @NotNull [] parts) throws IllegalPropertyKeyPartException {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(variable, "Variable key part must not be null");
		Objects.requireNonNull(parts, "Parts must not be null");
		String targetKey = parts[1].strip();
		if (targetKey.isEmpty()) {
			throw new IllegalPropertyKeyPartException("Target key of variable key part '${" + variable + "}' must not be empty or blank: '" + key + "'");
		} else if (targetKey.indexOf('$') != -1) {
			throw new IllegalPropertyKeyPartException("Target key of variable key part '${" + variable + "}' must not contain nested variable parts: '" + key + "'");
		} else if (targetKey.indexOf('[') != -1) {
			throw new IllegalPropertyKeyPartException("Target key of variable key part '${" + variable + "}' must not contain nested compacted parts: '" + key + "'");
		}
		return targetKey;
	}
	//endregion
	
	private @NotNull List<String> extendResolvedKeys(@NotNull List<String> keys, String @Nullable ... keyParts) {
		Objects.requireNonNull(keys, "Keys must not be null");
		List<String> result = Lists.newArrayList();
		for (String key : keys) {
			for (String part : ArrayUtils.nullToEmpty(keyParts)) {
				result.add(key + part);
			}
		}
		return result;
	}
	//endregion
	
	@Override
	public void close() throws IOException {
		this.reader.close();
	}
}
