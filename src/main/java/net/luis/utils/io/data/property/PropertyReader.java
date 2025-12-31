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

package net.luis.utils.io.data.property;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.property.exception.PropertySyntaxException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringScope;
import net.luis.utils.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Represents a reader for properties.<br>
 * This reader reads the properties from a defined input and returns them as a property object.<br>
 * The base format of the properties is a key-value pair separated by a separator:<br>
 * <pre>{@code
 * <key><separator><value>
 * }</pre>
 * The line as a whole can contain multiple separators, but the first one will be used to split the key and value.<br>
 * <br>
 *
 * <h2><a href="compactedKeys">Compacted keys</a></h2>
 * A compacted key is a key that contains multiple keys separated by a pipe.<br>
 * The compacted part of a key is enclosed in square brackets {@code [}, {@code ]} and the keys are separated by {@code |}.<br>
 * <pre>{@code
 * <key_part>.[<compacted_key_part>|<compacted_key_part>|...].<key_part><separator><value>
 * }</pre>
 * All compacted keys will be resolved to multiple keys with the same value.<br>
 * A compacted key part can contain a variable key part that will be resolved to a value.<br>
 * <br>
 *
 * <h2><a href="variableKeys">Variable keys</a></h2>
 * A variable key is a key that contains a variable part that will be resolved to a value.<br>
 * The variable part of a key is enclosed in curly brackets with a dollar sign prefix {@code ${...}}.<br>
 * <p>
 *     The format is {@code ${type:key}} or {@code ${type:key:-default}} where:
 * </p>
 * <ul>
 *     <li>{@code type} - The source type: {@code prop} (property), {@code sys} (system), or {@code env} (environment)</li>
 *     <li>{@code key} - The key to look up in the specified source</li>
 *     <li>{@code default} - Optional default value if the key is not found (preceded by {@code :-})</li>
 * </ul>
 * <pre>{@code
 * <key_part>.${<type>:<variable_key>:-<default_value>}.<key_part><separator><value>
 * }</pre>
 *
 * <h2><a href="arrays">Arrays</a></h2>
 * Arrays can be specified in two formats:
 * <ul>
 *     <li>Inline: {@code key = [value1, value2, value3]}</li>
 *     <li>Multi-line: {@code key[] = value1} on separate lines</li>
 * </ul>
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
	private final Map<String, String> propertyCache = Maps.newLinkedHashMap();
	/**
	 * A cache for multi-line arrays.<br>
	 */
	private final Map<String, PropertyArray> arrayCache = Maps.newLinkedHashMap();
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
	 *
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public PropertyReader(@NonNull InputProvider input) {
		this(input, PropertyConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new property reader with the given input and configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @param config The configuration for this reader
	 * @throws NullPointerException If the input or the configuration is null
	 */
	public PropertyReader(@NonNull InputProvider input, @NonNull PropertyConfig config) {
		this.config = Objects.requireNonNull(config, "Property config must not be null");
		this.reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(input, "Input must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Reads the content of the give scoped string reader and returns it without the scope.<br>
	 *
	 * @param reader The reader to read the content from
	 * @param scope The scope which is expected
	 * @return The content of the reader without the scope
	 */
	private static @NonNull String readScopeExclude(@NonNull ScopedStringReader reader, @NonNull StringScope scope) {
		Objects.requireNonNull(reader, "Reader must not be null");
		Objects.requireNonNull(scope, "Scope must not be null");
		String result = reader.readScope(scope);
		return result.substring(1, result.length() - 1);
	}
	
	/**
	 * Splits array elements by the separator, respecting nested structures.<br>
	 *
	 * @param content The content to split
	 * @param separator The separator character
	 * @return The list of elements
	 */
	private static @NonNull List<String> splitArrayElements(@NonNull String content, char separator) {
		List<String> elements = Lists.newArrayList();
		StringBuilder current = new StringBuilder();
		int depth = 0;
		boolean inQuotes = false;
		char quoteChar = 0;
		
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			
			if (!inQuotes && (c == '"' || c == '\'')) {
				inQuotes = true;
				quoteChar = c;
				current.append(c);
			} else if (inQuotes && c == quoteChar) {
				inQuotes = false;
				quoteChar = 0;
				current.append(c);
			} else if (!inQuotes && (c == '[' || c == '{' || c == '(')) {
				depth++;
				current.append(c);
			} else if (!inQuotes && (c == ']' || c == '}' || c == ')')) {
				depth--;
				current.append(c);
			} else if (!inQuotes && depth == 0 && c == separator) {
				elements.add(current.toString());
				current = new StringBuilder();
			} else {
				current.append(c);
			}
		}
		
		if (!current.isEmpty()) {
			elements.add(current.toString());
		}
		
		return elements;
	}
	
	/**
	 * Reads the properties from the input and returns them as a property object.<br>
	 *
	 * @return The property object that has been read
	 * @throws PropertySyntaxException If an error occurs while reading the properties
	 */
	public @NonNull PropertyObject readProperties() {
		PropertyObject properties = new PropertyObject();
		while (true) {
			String line;
			try {
				line = this.reader.readLine();
			} catch (Exception e) {
				throw new PropertySyntaxException("Unable to read line from input", e);
			}
			if (line == null) {
				break;
			}
			List<ParsedProperty> parsedProperties = this.parseLine(line);
			if (parsedProperties.isEmpty()) {
				continue;
			}
			for (ParsedProperty prop : parsedProperties) {
				if (prop.isArrayAppend()) {
					// Multi-line array syntax: key[] = value
					String baseKey = prop.key();
					PropertyArray array = this.arrayCache.computeIfAbsent(baseKey, k -> new PropertyArray());
					array.add(prop.element());
					properties.add(baseKey, array);
				} else {
					properties.add(prop.key(), prop.element());
				}
				// Cache string values for variable resolution
				if (prop.element() instanceof PropertyValue value && value.isString()) {
					this.propertyCache.put(prop.key(), value.getAsString());
				}
			}
		}
		return properties;
	}
	
	/**
	 * Parses the given line and returns the properties that have been read.<br>
	 * If the line is empty or a comment, an empty list will be returned.<br>
	 *
	 * @param rawLine The line to parse
	 * @return The properties that have been read
	 * @throws NullPointerException If the line is null
	 * @throws PropertySyntaxException If the line is not parsable
	 */
	private @NonNull @Unmodifiable List<ParsedProperty> parseLine(@NonNull String rawLine) {
		Objects.requireNonNull(rawLine, "Line must not be null");
		String line = rawLine.stripLeading();
		if (line.isBlank() || this.config.commentCharacters().contains(line.charAt(0))) {
			return List.of();
		}
		char separator = this.config.separator();
		if (!StringUtils.containsNotEscaped(line, separator)) {
			throw new PropertySyntaxException("No separator (" + separator + ") found in line: " + line);
		}
		String[] parts = StringUtils.splitNotEscaped(line, separator);
		if (parts.length == 0) {
			throw new PropertySyntaxException("Unable to split line at separator (" + separator + "): " + line);
		}
		return this.parseProperty(parts[0], this.getValuePart(parts));
	}
	
	/**
	 * Returns the value part of the given parts.<br>
	 * If the line contains multiple separators, the value part will be concatenated with the separator.<br>
	 *
	 * @param valueParts The parts of the line that were split at the separator
	 * @return The value part of the line
	 * @throws NullPointerException If the value parts are null
	 */
	private @NonNull String getValuePart(String @NonNull [] valueParts) {
		Objects.requireNonNull(valueParts, "Value parts must not be null");
		if (valueParts.length == 1) {
			return "";
		} else if (valueParts.length == 2) {
			return valueParts[1];
		}
		StringBuilder builder = new StringBuilder(valueParts[1]);
		for (int i = 2; i < valueParts.length; i++) {
			builder.append(this.config.separator()).append(valueParts[i]);
		}
		return builder.toString();
	}
	
	/**
	 * Parses the given key and value and returns the properties that have been read.<br>
	 * If advanced parsing is enabled, the key can be a compacted or variable key.<br>
	 *
	 * @param rawKey The key to parse
	 * @param rawValue The value to parse
	 * @return The properties that have been read
	 * @throws PropertySyntaxException If an error occurs while parsing the key or value
	 */
	private @NonNull @Unmodifiable List<ParsedProperty> parseProperty(@NonNull String rawKey, @NonNull String rawValue) {
		Objects.requireNonNull(rawKey, "Key must not be null");
		Objects.requireNonNull(rawValue, "Value must not be null");
		int alignment = this.getWhitespaceAlignmentCount(rawKey);
		String key = this.removeAlignment(rawKey, alignment, true);
		String value = this.removeAlignment(rawValue, alignment, false);
		
		// Check for multi-line array syntax: key[] = value
		boolean isArrayAppend = key.endsWith("[]") && this.config.allowMultiLineArrays();
		if (isArrayAppend) {
			key = key.substring(0, key.length() - 2);
		}
		
		if (this.isAdvancedKey(key) && !this.config.advancedParsing()) {
			throw new PropertySyntaxException("Advanced key '" + key + "' is not allowed: '" + rawKey + "'");
		}
		if (this.config.advancedParsing()) {
			List<ParsedProperty> properties = Lists.newArrayList();
			for (String resolvedKey : this.resolveAdvancedKeys(key)) {
				this.config.ensureKeyMatches(resolvedKey);
				this.config.ensureValueMatches(value);
				PropertyElement element = this.parseValue(value);
				properties.add(new ParsedProperty(resolvedKey, element, isArrayAppend));
			}
			return properties;
		}
		this.config.ensureKeyMatches(key);
		this.config.ensureValueMatches(value);
		PropertyElement element = this.parseValue(value);
		return List.of(new ParsedProperty(key, element, isArrayAppend));
	}
	
	/**
	 * Parses the value string into a PropertyElement.<br>
	 * Supports inline arrays and typed value parsing.<br>
	 *
	 * @param value The value string to parse
	 * @return The parsed property element
	 */
	private @NonNull PropertyElement parseValue(@NonNull String value) {
		String trimmed = value.strip();
		
		// Check for null values
		if (trimmed.isEmpty()) {
			return PropertyNull.INSTANCE;
		}
		if ("null".equalsIgnoreCase(trimmed) || "~".equals(trimmed)) {
			return PropertyNull.INSTANCE;
		}
		
		// Check for inline array: [value1, value2, value3]
		if (trimmed.startsWith(String.valueOf(this.config.arrayOpenChar())) &&
			trimmed.endsWith(String.valueOf(this.config.arrayCloseChar()))) {
			return this.parseInlineArray(trimmed);
		}
		
		// Parse as simple value
		PropertyValue propValue = new PropertyValue(value);
		
		// Auto-parse typed values if enabled
		if (this.config.parseTypedValues()) {
			return propValue.getAsParsedPropertyValue();
		}
		
		return propValue;
	}
	
	/**
	 * Parses an inline array string like [value1, value2, value3].<br>
	 *
	 * @param arrayString The array string to parse
	 * @return The parsed property array
	 */
	private @NonNull PropertyArray parseInlineArray(@NonNull String arrayString) {
		PropertyArray array = new PropertyArray();
		
		// Remove brackets
		String content = arrayString.substring(1, arrayString.length() - 1).strip();
		if (content.isEmpty()) {
			return array;
		}
		
		// Split by array separator, respecting quotes and nested structures
		List<String> elements = splitArrayElements(content, this.config.arraySeparator());
		for (String element : elements) {
			String trimmed = element.strip();
			if (trimmed.isEmpty()) {
				array.add(PropertyNull.INSTANCE);
			} else {
				PropertyValue value = new PropertyValue(trimmed);
				if (this.config.parseTypedValues()) {
					array.add(value.getAsParsedPropertyValue());
				} else {
					array.add(value);
				}
			}
		}
		
		return array;
	}
	
	/**
	 * Checks if the given key is either a compacted or variable key.<br>
	 *
	 * @param key The key to check
	 * @return True if the key is advanced, otherwise false
	 */
	private boolean isAdvancedKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		return COMPACTED_KEY_PATTERN.matcher(key).matches() || VARIABLE_KEY_PATTERN.matcher(key).matches();
	}
	
	/**
	 * Returns the alignment count of the given key.<br>
	 * The alignment count is the count of whitespaces at the end of the key.<br>
	 *
	 * @param key The key to get the alignment count of
	 * @return The alignment count of the key
	 * @throws NullPointerException If the key is null
	 */
	private int getWhitespaceAlignmentCount(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		int count = 0;
		for (int i = key.length() - 1; i >= 0; i--) {
			if (Character.isWhitespace(key.charAt(i))) {
				count++;
			} else {
				return count;
			}
		}
		return count;
	}
	
	/**
	 * Removes the alignment from the given string.<br>
	 *
	 * @param str The string to remove the alignment from
	 * @param alignment The alignment count to remove
	 * @param isKey True if the string is a key, otherwise false
	 * @return The string without the alignment
	 * @throws NullPointerException If the string is null
	 */
	private @NonNull String removeAlignment(@NonNull String str, int alignment, boolean isKey) {
		Objects.requireNonNull(str, "String must not be null");
		if (0 >= alignment) {
			return str;
		}
		if (isKey) {
			return str.substring(0, str.length() - alignment);
		}
		return str.substring(alignment);
	}
	
	/**
	 * Validates and resolves the given advanced key and returns the resolved keys.<br>
	 * The key can be a compacted or variable key.<br>
	 *
	 * @param key The key to resolve
	 * @return The resolved keys
	 * @throws NullPointerException If the key is null
	 * @throws PropertySyntaxException If the key does not match the expected format
	 */
	private @NonNull @Unmodifiable List<String> resolveAdvancedKeys(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		List<String> resolvedKeys = Lists.newArrayList();
		ScopedStringReader reader = new ScopedStringReader(key);
		while (reader.canRead()) {
			int position = reader.getIndex();
			String part = reader.readUntilInclusive('.');
			
			if (part.charAt(part.length() - 1) == '.') {
				if (!reader.canRead()) {
					throw new PropertySyntaxException("Key part '" + part + "' at position " + position + " end must not end with a dot: '" + key + "'");
				}
				part = part.substring(0, part.length() - 1);
			}
			if (part.isEmpty()) {
				throw new PropertySyntaxException("Key part '" + part + "' at position " + position + " must not be empty: '" + key + "'");
			} else if (part.isBlank()) {
				throw new PropertySyntaxException("Key part '" + part + "' at position " + position + " must not be blank: '" + key + "'");
			}
			
			ScopedStringReader partReader = new ScopedStringReader(part);
			if (partReader.peek() == '$') {
				partReader.skip(); // skip $
				position += partReader.getIndex();
				String variable = readScopeExclude(partReader, StringScope.CURLY_BRACKETS);
				if (variable.isEmpty()) {
					throw new PropertySyntaxException("Variable key part '${" + variable + "}' at position " + position + " must not be empty: '" + key + "'");
				} else if (variable.isBlank()) {
					throw new PropertySyntaxException("Variable key part '${" + variable + "}' at position " + position + " must not be blank: '" + key + "'");
				}
				resolvedKeys = this.extendResolvedKeys(resolvedKeys, this.resolveVariableKeyPart(key, variable));
			} else if (partReader.peek() == '[') {
				position += partReader.getIndex();
				String compacted = readScopeExclude(partReader, StringScope.SQUARE_BRACKETS);
				if (compacted.isEmpty()) {
					throw new PropertySyntaxException("Compacted key part '[" + compacted + "]' at position " + position + " must not be empty: '" + key + "'");
				} else if (compacted.isBlank()) {
					throw new PropertySyntaxException("Compacted key part '[" + compacted + "]' at position " + position + " must not be blank: '" + key + "'");
				} else if (compacted.indexOf('|') == -1) {
					resolvedKeys = this.extendResolvedKeys(resolvedKeys, compacted);
				} else {
					resolvedKeys = this.extendResolvedKeys(resolvedKeys, this.resolveCompactedKeyPart(key, compacted));
				}
			} else {
				resolvedKeys = this.extendResolvedKeys(resolvedKeys, part);
			}
		}
		return resolvedKeys;
	}
	
	/**
	 * Validates and resolves the given compacted key part against the expected format.<br>
	 *
	 * @param key The key to resolve
	 * @param compacted The compacted key part to resolve
	 * @return The resolved keys
	 * @throws NullPointerException If the key or compacted key part is null
	 * @throws PropertySyntaxException If the compacted key part does not match the expected format
	 */
	private String @NonNull [] resolveCompactedKeyPart(@NonNull String key, @NonNull String compacted) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(compacted, "Compacted key part must not be null");
		String[] compactedValues = compacted.split("\\|");
		for (int i = 0; i < compactedValues.length; i++) {
			String compactedValue = compactedValues[i];
			if (compactedValue.isBlank()) {
				throw new PropertySyntaxException("Value '" + compactedValue + "' in compacted key part '[" + compacted + "]' must not be blank: '" + key + "'");
			} else if (containsWhitespace(compactedValue)) {
				throw new PropertySyntaxException("Value '" + compactedValue + "' in compacted key part '[" + compacted + "]' must not contain whitespaces: '" + key + "'");
			} else if (compactedValue.indexOf('[') != -1) {
				throw new PropertySyntaxException("Value '" + compactedValue + "' in compacted key part '[" + compacted + "]' must not be nested: '" + key + "'");
			} else if (compactedValue.charAt(0) == '$') {
				String variable = readScopeExclude(new ScopedStringReader(compactedValue.substring(1)), StringScope.CURLY_BRACKETS);
				compactedValues[i] = this.resolveVariableKeyPart(key, variable);
			}
		}
		return compactedValues;
	}
	
	/**
	 * Validates and resolves the given variable key part using the new colon-based syntax.<br>
	 * <p>
	 *     Format: {@code type:key} or {@code type:key:-default}
	 * </p>
	 * <ul>
	 *     <li>{@code prop:} - Resolve from previously read properties</li>
	 *     <li>{@code sys:} - Resolve from system properties</li>
	 *     <li>{@code env:} - Resolve from environment variables</li>
	 * </ul>
	 *
	 * @param key The key being parsed (for error messages)
	 * @param variable The variable content (without ${})
	 * @return The resolved value
	 * @throws NullPointerException If the key or variable is null
	 * @throws PropertySyntaxException If the variable format is invalid or value cannot be resolved
	 */
	private @NonNull String resolveVariableKeyPart(@NonNull String key, @NonNull String variable) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(variable, "Variable key part must not be null");
		
		char typeSeparator = this.config.variableTypeSeparator();
		String defaultMarker = this.config.defaultValueMarker();
		
		// Find the type separator
		int typeEnd = variable.indexOf(typeSeparator);
		if (typeEnd == -1) {
			throw new PropertySyntaxException("Variable '${" + variable + "}' must contain type separator '" + typeSeparator + "': '" + key + "'");
		}
		
		String type = variable.substring(0, typeEnd).strip();
		String remainder = variable.substring(typeEnd + 1);
		
		// Check for default value marker
		String targetKey;
		String defaultValue = null;
		int defaultIndex = remainder.indexOf(defaultMarker);
		if (defaultIndex != -1) {
			targetKey = remainder.substring(0, defaultIndex).strip();
			defaultValue = remainder.substring(defaultIndex + defaultMarker.length());
		} else {
			targetKey = remainder.strip();
		}
		
		// Validate target key
		if (targetKey.isEmpty()) {
			throw new PropertySyntaxException("Target key in variable '${" + variable + "}' must not be empty: '" + key + "'");
		}
		
		// Resolve the value based on type
		String value = this.resolveVariableValue(type, targetKey, key, variable);
		
		// Apply default if needed
		if (value == null && defaultValue != null) {
			value = defaultValue;
		}
		
		if (value == null) {
			throw new PropertySyntaxException("Could not resolve variable '${" + variable + "}' and no default was provided: '" + key + "'");
		}
		
		return value;
	}
	
	/**
	 * Resolves the actual value of the given variable based on its type.<br>
	 *
	 * @param type The variable type (prop, sys, env)
	 * @param targetKey The key to look up
	 * @param key The original key (for error messages)
	 * @param variable The original variable (for error messages)
	 * @return The resolved value, or null if not found
	 * @throws PropertySyntaxException If the type is not recognized
	 */
	private @Nullable String resolveVariableValue(@NonNull String type, @NonNull String targetKey, @NonNull String key, @NonNull String variable) {
		// Check custom variables first
		Map<String, String> customVars = this.config.getCustomVariables();
		if (!customVars.isEmpty() && customVars.containsKey(targetKey)) {
			return customVars.get(targetKey);
		}
		
		return switch (type.toLowerCase()) {
			case "prop", "property", "" -> this.propertyCache.get(targetKey);
			case "sys", "system" -> System.getProperty(targetKey);
			case "env", "environment" -> System.getenv(targetKey);
			default -> throw new PropertySyntaxException("Unknown variable type '" + type + "' in '${" + variable + "}'. " +
				"Valid types are: prop, sys, env: '" + key + "'");
		};
	}
	
	/**
	 * Extends the given list of resolved keys with the given new key parts.<br>
	 *
	 * @param resolvedKeys The resolved keys to extend
	 * @param keyParts The new key parts to append
	 * @return The extended resolved keys
	 * @throws NullPointerException If the resolved keys are null
	 */
	private @NonNull List<String> extendResolvedKeys(@NonNull List<String> resolvedKeys, String @Nullable ... keyParts) {
		Objects.requireNonNull(resolvedKeys, "Resolved keys must not be null");
		List<String> result = Lists.newArrayList();
		if (resolvedKeys.isEmpty() && keyParts != null) {
			result.addAll(Arrays.asList(keyParts));
		}
		for (String existingKey : resolvedKeys) {
			for (String part : ArrayUtils.nullToEmpty(keyParts)) {
				result.add(existingKey + "." + part);
			}
		}
		return result;
	}
	
	@Override
	public void close() throws IOException {
		this.reader.close();
	}
	
	/**
	 * Internal record for holding a parsed property with its key, element, and array append flag.<br>
	 *
	 * @param key The property key
	 * @param element The property element
	 * @param isArrayAppend Whether this is a multi-line array append (key[] = value)
	 */
	private record ParsedProperty(@NonNull String key, @NonNull PropertyElement element, boolean isArrayAppend) {}
}
