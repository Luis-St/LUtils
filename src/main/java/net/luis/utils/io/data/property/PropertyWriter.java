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
import net.luis.utils.io.data.OutputProvider;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a writer for properties.<br>
 * Supports writing PropertyObject instances with optional key compaction
 * and various output formatting options.<br>
 *
 * @author Luis-St
 */
public class PropertyWriter implements AutoCloseable {
	
	/**
	 * The configuration for the property writer.<br>
	 */
	private final PropertyConfig config;
	/**
	 * The internal io writer for writing the property.<br>
	 */
	private final BufferedWriter writer;
	
	/**
	 * Constructs a new property writer for the given output with the default property configuration.<br>
	 *
	 * @param output The output provider to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public PropertyWriter(@NonNull OutputProvider output) {
		this(output, PropertyConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new property writer for the given output with the given property configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @param config The configuration for the properties writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public PropertyWriter(@NonNull OutputProvider output, @NonNull PropertyConfig config) {
		this.config = Objects.requireNonNull(config, "Property config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Writes the given property object to the underlying output.<br>
	 * Uses the configured output format options including optional key compaction.<br>
	 *
	 * @param propertyObject The property object to write
	 * @throws NullPointerException If the property object is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void write(@NonNull PropertyObject propertyObject) {
		Objects.requireNonNull(propertyObject, "Property object must not be null");
		try {
			if (this.config.enableWriteCompaction()) {
				this.writeWithCompaction(propertyObject);
			} else {
				this.writeSimple(propertyObject);
			}
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the property object", e);
		}
	}
	
	/**
	 * Writes the property object without key compaction.<br>
	 * Each property is written on its own line.<br>
	 *
	 * @param propertyObject The property object to write
	 * @throws IOException If an I/O error occurs
	 */
	private void writeSimple(@NonNull PropertyObject propertyObject) throws IOException {
		boolean first = true;
		for (Map.Entry<String, PropertyElement> entry : propertyObject.entrySet()) {
			if (!first) {
				this.writer.newLine();
			}
			first = false;
			
			String key = entry.getKey();
			PropertyElement value = entry.getValue();
			
			this.config.ensureKeyMatches(key);
			this.writeProperty(key, value);
		}
	}
	
	/**
	 * Writes a single property (key-value pair) to the output.<br>
	 *
	 * @param key The property key
	 * @param value The property value
	 * @throws IOException If an I/O error occurs
	 */
	private void writeProperty(@NonNull String key, @NonNull PropertyElement value) throws IOException {
		String alignment = " ".repeat(this.config.alignment());
		String valueString = this.formatValue(value);
		
		this.config.ensureValueMatches(valueString);
		
		this.writer.write(key);
		this.writer.write(alignment);
		this.writer.write(this.config.separator());
		this.writer.write(alignment);
		this.writer.write(valueString);
	}
	
	/**
	 * Formats a property element to its string representation.<br>
	 *
	 * @param element The property element to format
	 * @return The formatted string representation
	 */
	private @NonNull String formatValue(@NonNull PropertyElement element) {
		if (element.isPropertyNull()) {
			return switch (this.config.nullStyle()) {
				case EMPTY -> "";
				case NULL_STRING -> "null";
				case TILDE -> "~";
			};
		} else if (element instanceof PropertyArray array) {
			return this.formatArray(array);
		} else if (element instanceof PropertyValue value) {
			return value.toString(this.config);
		} else if (element instanceof PropertyObject object) {
			return object.toString(this.config);
		}
		return element.toString(this.config);
	}
	
	/**
	 * Formats a property array to its string representation.<br>
	 *
	 * @param array The property array to format
	 * @return The formatted string representation
	 */
	private @NonNull String formatArray(@NonNull PropertyArray array) {
		StringBuilder builder = new StringBuilder();
		builder.append(this.config.arrayOpenChar());
		
		boolean first = true;
		for (PropertyElement element : array) {
			if (!first) {
				builder.append(this.config.arraySeparator()).append(" ");
			}
			first = false;
			builder.append(this.formatValue(element));
		}
		
		builder.append(this.config.arrayCloseChar());
		return builder.toString();
	}
	
	/**
	 * Writes the property object with key compaction enabled.<br>
	 * Groups keys with common prefixes and writes them using compacted syntax.<br>
	 * <p>
	 *     Example: app.dev.url, app.prod.url becomes app.[dev|prod].url
	 * </p>
	 *
	 * @param propertyObject The property object to write
	 * @throws IOException If an I/O error occurs
	 */
	private void writeWithCompaction(@NonNull PropertyObject propertyObject) throws IOException {
		List<CompactedEntry> entries = this.compactEntries(propertyObject);
		
		boolean first = true;
		for (CompactedEntry entry : entries) {
			if (!first) {
				this.writer.newLine();
			}
			first = false;
			
			this.config.ensureKeyMatches(entry.key());
			this.writeProperty(entry.key(), entry.value());
		}
	}
	
	/**
	 * Analyzes property entries and creates compacted entries where possible.<br>
	 *
	 * @param propertyObject The property object to analyze
	 * @return A list of compacted entries
	 */
	private @NonNull List<CompactedEntry> compactEntries(@NonNull PropertyObject propertyObject) {
		List<CompactedEntry> result = Lists.newArrayList();
		Set<String> processedKeys = new HashSet<>();
		
		Map<String, Map<String, Map<String, PropertyElement>>> compactionGroups = this.findCompactionGroups(propertyObject);
		
		for (Map.Entry<String, Map<String, Map<String, PropertyElement>>> prefixEntry : compactionGroups.entrySet()) {
			String prefix = prefixEntry.getKey();
			Map<String, Map<String, PropertyElement>> suffixGroups = prefixEntry.getValue();
			
			for (Map.Entry<String, Map<String, PropertyElement>> suffixEntry : suffixGroups.entrySet()) {
				String suffix = suffixEntry.getKey();
				Map<String, PropertyElement> variants = suffixEntry.getValue();
				
				if (variants.size() >= this.config.minCompactionGroupSize()) {
					if (this.canCompact(variants)) {
						String compactedKey = this.buildCompactedKey(prefix, variants.keySet(), suffix);
						PropertyElement commonValue = variants.values().iterator().next();
						result.add(new CompactedEntry(compactedKey, commonValue));
						
						for (String variant : variants.keySet()) {
							String fullKey = this.buildFullKey(prefix, variant, suffix);
							processedKeys.add(fullKey);
						}
					}
				}
			}
		}
		
		for (Map.Entry<String, PropertyElement> entry : propertyObject.entrySet()) {
			if (!processedKeys.contains(entry.getKey())) {
				result.add(new CompactedEntry(entry.getKey(), entry.getValue()));
			}
		}
		
		return result;
	}
	
	/**
	 * Finds groups of keys that can potentially be compacted.<br>
	 * Groups are organized by prefix, suffix, and variants.<br>
	 *
	 * @param propertyObject The property object to analyze
	 * @return A map of prefix -> suffix -> variant -> value
	 */
	private @NonNull Map<String, Map<String, Map<String, PropertyElement>>> findCompactionGroups(@NonNull PropertyObject propertyObject) {
		Map<String, Map<String, Map<String, PropertyElement>>> groups = Maps.newLinkedHashMap();
		
		for (Map.Entry<String, PropertyElement> entry : propertyObject.entrySet()) {
			String key = entry.getKey();
			String[] parts = key.split("\\.");
			
			if (parts.length >= 2) {
				for (int variantIndex = 0; variantIndex < parts.length; variantIndex++) {
					String prefix = String.join(".", Arrays.copyOfRange(parts, 0, variantIndex));
					String variant = parts[variantIndex];
					String suffix = String.join(".", Arrays.copyOfRange(parts, variantIndex + 1, parts.length));
					
					String groupKey = prefix + "|" + suffix;
					
					groups.computeIfAbsent(prefix, k -> Maps.newLinkedHashMap())
						.computeIfAbsent(suffix, k -> Maps.newLinkedHashMap())
						.put(variant, entry.getValue());
				}
			}
		}
		
		return groups;
	}
	
	/**
	 * Checks if all values in the variant map are equal and can be compacted.<br>
	 *
	 * @param variants The map of variant -> value
	 * @return True if all values are equal, false otherwise
	 */
	private boolean canCompact(@NonNull Map<String, PropertyElement> variants) {
		if (variants.isEmpty()) {
			return false;
		}
		PropertyElement first = variants.values().iterator().next();
		for (PropertyElement value : variants.values()) {
			if (!Objects.equals(first, value)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Builds a compacted key from prefix, variants, and suffix.<br>
	 * <p>
	 *     Example: prefix="app", variants=["dev", "prod"], suffix="url"
	 *     Result: "app.[dev|prod].url"
	 * </p>
	 *
	 * @param prefix The key prefix
	 * @param variants The set of variant names
	 * @param suffix The key suffix
	 * @return The compacted key
	 */
	private @NonNull String buildCompactedKey(@NonNull String prefix, @NonNull Set<String> variants, @NonNull String suffix) {
		String variantPart = "[" + variants.stream().sorted().collect(Collectors.joining("|")) + "]";
		
		StringBuilder builder = new StringBuilder();
		if (!prefix.isEmpty()) {
			builder.append(prefix).append(".");
		}
		builder.append(variantPart);
		if (!suffix.isEmpty()) {
			builder.append(".").append(suffix);
		}
		return builder.toString();
	}
	
	/**
	 * Builds a full key from prefix, variant, and suffix.<br>
	 *
	 * @param prefix The key prefix
	 * @param variant The variant name
	 * @param suffix The key suffix
	 * @return The full key
	 */
	private @NonNull String buildFullKey(@NonNull String prefix, @NonNull String variant, @NonNull String suffix) {
		StringBuilder builder = new StringBuilder();
		if (!prefix.isEmpty()) {
			builder.append(prefix).append(".");
		}
		builder.append(variant);
		if (!suffix.isEmpty()) {
			builder.append(".").append(suffix);
		}
		return builder.toString();
	}
	
	/**
	 * Writes a single property with the given key and string value.<br>
	 *
	 * @param key The property key
	 * @param value The property value
	 * @throws NullPointerException If the key or value is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeSingleProperty(@NonNull String key, @NonNull String value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		this.writeSingleProperty(key, new PropertyValue(value));
	}
	
	/**
	 * Writes a single property with the given key and boolean value.<br>
	 *
	 * @param key The property key
	 * @param value The property value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeSingleProperty(@NonNull String key, boolean value) {
		Objects.requireNonNull(key, "Key must not be null");
		this.writeSingleProperty(key, new PropertyValue(value));
	}
	
	/**
	 * Writes a single property with the given key and number value.<br>
	 *
	 * @param key The property key
	 * @param value The property value
	 * @throws NullPointerException If the key or value is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeSingleProperty(@NonNull String key, @NonNull Number value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		this.writeSingleProperty(key, new PropertyValue(value));
	}
	
	/**
	 * Writes a single property with the given key and element value.<br>
	 *
	 * @param key The property key
	 * @param value The property element value
	 * @throws NullPointerException If the key or value is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeSingleProperty(@NonNull String key, @NonNull PropertyElement value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		try {
			this.config.ensureKeyMatches(key);
			this.writeProperty(key, value);
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the property", e);
		}
	}
	
	/**
	 * Writes a multi-line array property.<br>
	 * Each array element is written on its own line with the key[] = value syntax.<br>
	 *
	 * @param key The property key
	 * @param array The property array to write
	 * @throws NullPointerException If the key or array is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeMultiLineArray(@NonNull String key, @NonNull PropertyArray array) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(array, "Array must not be null");
		try {
			String alignment = " ".repeat(this.config.alignment());
			String arrayKey = key + "[]";
			
			this.config.ensureKeyMatches(arrayKey);
			
			boolean first = true;
			for (PropertyElement element : array) {
				if (!first) {
					this.writer.newLine();
				}
				first = false;
				
				String valueString = this.formatValue(element);
				this.config.ensureValueMatches(valueString);
				
				this.writer.write(arrayKey);
				this.writer.write(alignment);
				this.writer.write(this.config.separator());
				this.writer.write(alignment);
				this.writer.write(valueString);
			}
			
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the multi-line array", e);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
	
	/**
	 * Represents a compacted entry with a potentially compacted key and value.<br>
	 *
	 * @param key The property key (may be compacted)
	 * @param value The property value
	 */
	private record CompactedEntry(@NonNull String key, @NonNull PropertyElement value) {}
}
