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

import net.luis.utils.io.data.OutputProvider;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.time.*;
import java.util.*;

/**
 * Represents a writer for TOML files.<br>
 * This writer writes TOML tables to a defined output.<br>
 * <p>
 * Supports writing:
 * <ul>
 *     <li>Key-value pairs</li>
 *     <li>Tables with section headers</li>
 *     <li>Inline tables</li>
 *     <li>Arrays and array of tables</li>
 *     <li>All TOML value types including date/time</li>
 * </ul>
 *
 * @author Luis-St
 */
public class TomlWriter implements AutoCloseable {

	/**
	 * The configuration for the TOML writer.<br>
	 */
	private final TomlConfig config;

	/**
	 * The internal io writer for writing the TOML content.<br>
	 */
	private final BufferedWriter writer;

	/**
	 * Constructs a new TOML writer for the given output with the default TOML configuration.<br>
	 *
	 * @param output The output provider to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public TomlWriter(@NonNull OutputProvider output) {
		this(output, TomlConfig.DEFAULT);
	}

	/**
	 * Constructs a new TOML writer for the given output with the given TOML configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @param config The configuration for the TOML writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public TomlWriter(@NonNull OutputProvider output, @NonNull TomlConfig config) {
		this.config = Objects.requireNonNull(config, "TOML config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}

	/**
	 * Writes the given TOML table to the underlying output.<br>
	 * This writes the table as a complete TOML document with proper section headers.<br>
	 *
	 * @param table The TOML table to write
	 * @throws NullPointerException If the table is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeToml(@NonNull TomlTable table) {
		Objects.requireNonNull(table, "TOML table must not be null");
		try {
			this.writeTable(table, "");
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the TOML document", e);
		}
	}

	/**
	 * Writes a table and its contents.<br>
	 *
	 * @param table The table to write
	 * @param path The current table path (empty for root)
	 * @throws IOException If an I/O error occurs
	 */
	private void writeTable(@NonNull TomlTable table, @NonNull String path) throws IOException {
		List<Map.Entry<String, TomlElement>> simpleEntries = new ArrayList<>();
		List<Map.Entry<String, TomlElement>> nestedTables = new ArrayList<>();
		List<Map.Entry<String, TomlElement>> arrayOfTables = new ArrayList<>();

		for (Map.Entry<String, TomlElement> entry : table) {
			TomlElement value = entry.getValue();
			if (value instanceof TomlTable nested && !nested.isInline()) {
				nestedTables.add(entry);
			} else if (value instanceof TomlArray array && array.isArrayOfTables()) {
				arrayOfTables.add(entry);
			} else {
				simpleEntries.add(entry);
			}
		}

		if (!path.isEmpty() && (!simpleEntries.isEmpty() || (nestedTables.isEmpty() && arrayOfTables.isEmpty()))) {
			this.writer.write("[");
			this.writer.write(path);
			this.writer.write("]");
			this.writer.newLine();
		}

		for (Map.Entry<String, TomlElement> entry : simpleEntries) {
			this.writeKeyValue(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, TomlElement> entry : nestedTables) {
			if (this.config.prettyPrint() && !simpleEntries.isEmpty()) {
				this.writer.newLine();
			}
			String nestedPath = path.isEmpty() ? this.formatKey(entry.getKey()) : path + "." + this.formatKey(entry.getKey());
			this.writeTable((TomlTable) entry.getValue(), nestedPath);
		}

		for (Map.Entry<String, TomlElement> entry : arrayOfTables) {
			TomlArray array = (TomlArray) entry.getValue();
			String arrayPath = path.isEmpty() ? this.formatKey(entry.getKey()) : path + "." + this.formatKey(entry.getKey());

			for (TomlElement element : array) {
				if (this.config.prettyPrint()) {
					this.writer.newLine();
				}
				this.writer.write("[[");
				this.writer.write(arrayPath);
				this.writer.write("]]");
				this.writer.newLine();

				if (element instanceof TomlTable arrayTable) {
					for (Map.Entry<String, TomlElement> tableEntry : arrayTable) {
						TomlElement value = tableEntry.getValue();
						if (value instanceof TomlTable nested && !nested.isInline()) {
							String nestedPath = arrayPath + "." + this.formatKey(tableEntry.getKey());
							this.writeTable(nested, nestedPath);
						} else if (!(value instanceof TomlArray a && a.isArrayOfTables())) {
							this.writeKeyValue(tableEntry.getKey(), value);
						}
					}
				}
			}
		}
	}

	/**
	 * Writes a key-value pair to the underlying output.<br>
	 *
	 * @param key The key
	 * @param value The value
	 * @throws IOException If an I/O error occurs
	 */
	private void writeKeyValue(@NonNull String key, @NonNull TomlElement value) throws IOException {
		this.writer.write(this.formatKey(key));
		this.writer.write(" = ");
		this.writer.write(value.toString(this.config));
		this.writer.newLine();
	}

	/**
	 * Writes a single key-value pair to the underlying output.<br>
	 * This is a convenience method for writing standalone properties.<br>
	 *
	 * @param key The key
	 * @param value The value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, @NonNull TomlElement value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		try {
			this.writeKeyValue(key, value);
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the TOML property", e);
		}
	}

	/**
	 * Writes a single property with a string value.<br>
	 *
	 * @param key The property key
	 * @param value The string value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, String value) {
		this.writeProperty(key, value == null ? TomlNull.INSTANCE : new TomlValue(value));
	}

	/**
	 * Writes a single property with a boolean value.<br>
	 *
	 * @param key The property key
	 * @param value The boolean value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, boolean value) {
		this.writeProperty(key, new TomlValue(value));
	}

	/**
	 * Writes a single property with a number value.<br>
	 *
	 * @param key The property key
	 * @param value The number value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, Number value) {
		this.writeProperty(key, value == null ? TomlNull.INSTANCE : new TomlValue(value));
	}

	/**
	 * Writes a single property with a local date value.<br>
	 *
	 * @param key The property key
	 * @param value The local date value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, LocalDate value) {
		this.writeProperty(key, value == null ? TomlNull.INSTANCE : new TomlValue(value));
	}

	/**
	 * Writes a single property with a local time value.<br>
	 *
	 * @param key The property key
	 * @param value The local time value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, LocalTime value) {
		this.writeProperty(key, value == null ? TomlNull.INSTANCE : new TomlValue(value));
	}

	/**
	 * Writes a single property with a local date-time value.<br>
	 *
	 * @param key The property key
	 * @param value The local date-time value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, LocalDateTime value) {
		this.writeProperty(key, value == null ? TomlNull.INSTANCE : new TomlValue(value));
	}

	/**
	 * Writes a single property with an offset date-time value.<br>
	 *
	 * @param key The property key
	 * @param value The offset date-time value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, OffsetDateTime value) {
		this.writeProperty(key, value == null ? TomlNull.INSTANCE : new TomlValue(value));
	}

	/**
	 * Writes a table header to the underlying output.<br>
	 *
	 * @param tablePath The path of the table (e.g., "server.database")
	 * @throws NullPointerException If the table path is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeTableHeader(@NonNull String tablePath) {
		Objects.requireNonNull(tablePath, "Table path must not be null");
		
		try {
			if (this.config.prettyPrint()) {
				this.writer.newLine();
			}
			
			this.writer.write("[");
			this.writer.write(tablePath);
			this.writer.write("]");
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the table header", e);
		}
	}

	/**
	 * Writes an array of tables header to the underlying output.<br>
	 *
	 * @param tablePath The path of the array of tables (e.g., "products")
	 * @throws NullPointerException If the table path is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeArrayOfTablesHeader(@NonNull String tablePath) {
		Objects.requireNonNull(tablePath, "Table path must not be null");
		
		try {
			if (this.config.prettyPrint()) {
				this.writer.newLine();
			}
			
			this.writer.write("[[");
			this.writer.write(tablePath);
			this.writer.write("]]");
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the array of tables header", e);
		}
	}

	/**
	 * Writes a comment to the underlying output.<br>
	 *
	 * @param comment The comment text (without the # character)
	 * @throws NullPointerException If the comment is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeComment(@NonNull String comment) {
		Objects.requireNonNull(comment, "Comment must not be null");
		
		try {
			this.writer.write("# ");
			this.writer.write(comment);
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the comment", e);
		}
	}

	/**
	 * Writes a blank line to the underlying output.<br>
	 *
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeBlankLine() {
		try {
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing a blank line", e);
		}
	}

	/**
	 * Checks if the given key is a valid bare key in TOML.<br>
	 *
	 * @param key The key to check
	 * @return True if the key is a valid bare key, false otherwise
	 */
	private boolean isBareKey(@NonNull String key) {
		if (key.isEmpty()) {
			return false;
		}
		
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Formats a key for TOML output, quoting if necessary.<br>
	 *
	 * @param key The key to format
	 * @return The formatted key
	 */
	private @NonNull String formatKey(@NonNull String key) {
		if (this.isBareKey(key)) {
			return key;
		}
		return "\"" + this.escapeString(key) + "\"";
	}

	/**
	 * Escapes special characters in a string for TOML output.<br>
	 *
	 * @param str The string to escape
	 * @return The escaped string
	 */
	private @NonNull String escapeString(@NonNull String str) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
				case '"' -> result.append("\\\"");
				case '\\' -> result.append("\\\\");
				case '\b' -> result.append("\\b");
				case '\f' -> result.append("\\f");
				case '\n' -> result.append("\\n");
				case '\r' -> result.append("\\r");
				case '\t' -> result.append("\\t");
				default -> {
					if (c < 0x20) {
						result.append(String.format("\\u%04X", (int) c));
					} else {
						result.append(c);
					}
				}
			}
		}
		return result.toString();
	}

	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
