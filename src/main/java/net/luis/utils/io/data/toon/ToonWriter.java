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

import net.luis.utils.io.data.OutputProvider;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a writer for TOON files.<br>
 * This writer writes TOON elements to a defined output.<br>
 * <p>
 * Supports writing:
 * <ul>
 *     <li>Key-value pairs with type-aware formatting</li>
 *     <li>Nested objects via indentation</li>
 *     <li>Inline primitive arrays</li>
 *     <li>Tabular arrays</li>
 *     <li>Expanded list arrays</li>
 *     <li>Key folding for single-child chains</li>
 * </ul>
 *
 * @author Luis-St
 */
public class ToonWriter implements AutoCloseable {
	
	/**
	 * The configuration for the TOON writer.<br>
	 */
	private final ToonConfig config;
	
	/**
	 * The internal io writer for writing the TOON content.<br>
	 */
	private final BufferedWriter writer;
	
	/**
	 * Constructs a new TOON writer for the given output with the default TOON configuration.<br>
	 *
	 * @param output The output provider to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public ToonWriter(@NonNull OutputProvider output) {
		this(output, ToonConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new TOON writer for the given output with the given TOON configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @param config The configuration for the TOON writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public ToonWriter(@NonNull OutputProvider output, @NonNull ToonConfig config) {
		this.config = Objects.requireNonNull(config, "TOON config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Writes the given TOON element to the underlying output.<br>
	 *
	 * @param element The TOON element to write
	 * @throws NullPointerException If the element is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeToon(@NonNull ToonElement element) {
		Objects.requireNonNull(element, "TOON element must not be null");
		try {
			if (element instanceof ToonObject object) {
				this.writeObject(object, 0, "");
			} else if (element instanceof ToonArray array) {
				this.writeRootArray(array);
			} else {
				this.writer.write(element.toString(this.config));
			}
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the TOON document", e);
		}
	}
	
	/**
	 * Writes an object with the given indentation depth and optional key prefix.<br>
	 *
	 * @param object The object to write
	 * @param depth The current indentation depth
	 * @param keyPrefix The key prefix from key folding
	 * @throws NullPointerException If the object or key prefix is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeObject(@NonNull ToonObject object, int depth, @NonNull String keyPrefix) throws IOException {
		Objects.requireNonNull(object, "Object must not be null");
		Objects.requireNonNull(keyPrefix, "Key prefix must not be null");
		
		String indentStr = " ".repeat(this.config.indent() * depth);
		boolean first = true;
		
		for (Map.Entry<String, ToonElement> entry : object) {
			if (!first) {
				this.writer.newLine();
			}
			first = false;
			
			String formattedKey = ToonHelper.formatKey(entry.getKey());
			String fullKey = keyPrefix.isEmpty() ? formattedKey : keyPrefix + "." + formattedKey;
			ToonElement value = entry.getValue();
			
			switch (value) {
				case ToonObject nested when this.config.keyFolding() == ToonConfig.KeyFolding.SAFE && this.shouldFold(entry.getKey(), nested, depth) -> this.writeObject(nested, depth, fullKey);
				case ToonObject nested -> {
					this.writer.write(indentStr + fullKey + ":");
					if (!nested.isEmpty()) {
						this.writer.newLine();
						this.writeObject(nested, depth + 1, "");
					}
				}
				case ToonArray array -> this.writeArrayWithKey(fullKey, array, depth, indentStr);
				default -> this.writer.write(indentStr + fullKey + ": " + value.toString(this.config));
			}
		}
	}
	
	/**
	 * Checks if a key-object pair should be folded into a dotted key path.<br>
	 *
	 * @param key The key name
	 * @param nested The nested object
	 * @param depth The current depth
	 * @return True if the key should be folded, false otherwise
	 * @throws NullPointerException If any parameter is null
	 */
	private boolean shouldFold(@NonNull String key, @NonNull ToonObject nested, int depth) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(nested, "Nested must not be null");
		
		if (!ToonHelper.canFoldKey(key)) {
			return false;
		}
		if (nested.size() != 1) {
			return false;
		}
		return depth < this.config.flattenDepth();
	}
	
	/**
	 * Writes an array with a key prefix.<br>
	 *
	 * @param key The formatted key
	 * @param array The array to write
	 * @param depth The current indentation depth
	 * @param indentStr The current indentation string
	 * @throws NullPointerException If any parameter is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeArrayWithKey(@NonNull String key, @NonNull ToonArray array, int depth, @NonNull String indentStr) throws IOException {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(array, "Array must not be null");
		Objects.requireNonNull(indentStr, "Indent string must not be null");
		
		this.writer.write(array.toStringWithKey(key, this.config, depth, indentStr));
	}
	
	/**
	 * Writes a root-level array.<br>
	 *
	 * @param array The array to write
	 * @throws NullPointerException If the array is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeRootArray(@NonNull ToonArray array) throws IOException {
		Objects.requireNonNull(array, "Array must not be null");
		this.writer.write(array.toString(this.config));
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
