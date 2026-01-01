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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.OutputProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.Objects;

/**
 * Represents a writer for ini files.<br>
 * This writer writes ini documents to a defined output.<br>
 *
 * @author Luis-St
 */
public class IniWriter implements AutoCloseable {
	
	/**
	 * The configuration for the ini writer.<br>
	 */
	private final IniConfig config;
	
	/**
	 * The internal io writer for writing the ini content.<br>
	 */
	private final BufferedWriter writer;
	
	/**
	 * Constructs a new ini writer for the given output with the default ini configuration.<br>
	 *
	 * @param output The output provider to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public IniWriter(@NonNull OutputProvider output) {
		this(output, IniConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new ini writer for the given output with the given ini configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @param config The configuration for the ini writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public IniWriter(@NonNull OutputProvider output, @NonNull IniConfig config) {
		this.config = Objects.requireNonNull(config, "Ini config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Writes the given ini document to the underlying output.<br>
	 *
	 * @param document The ini document to write
	 * @throws NullPointerException If the document is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeIni(@NonNull IniDocument document) {
		Objects.requireNonNull(document, "Ini document must not be null");
		
		try {
			this.writer.write(document.toString(this.config));
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the ini document", e);
		}
	}
	
	/**
	 * Writes a single section to the underlying output.<br>
	 *
	 * @param section The section to write
	 * @throws NullPointerException If the section is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeSection(@NonNull IniSection section) {
		Objects.requireNonNull(section, "Section must not be null");
		
		try {
			this.writer.write(section.toString(this.config));
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the ini section", e);
		}
	}
	
	/**
	 * Writes a single property (key-value pair) to the underlying output.<br>
	 * This writes a global property (not within any section).<br>
	 *
	 * @param key The property key
	 * @param value The property value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, @NonNull IniElement value) {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		try {
			if (value.isIniNull() && this.config.nullStyle() == IniConfig.NullStyle.SKIP) {
				return;
			}
			
			this.config.ensureKeyMatches(key);
			
			String alignment = " ".repeat(this.config.alignment());
			this.writer.write(key);
			this.writer.write(alignment);
			this.writer.write(this.config.separator());
			this.writer.write(alignment);
			this.writer.write(value.toString(this.config));
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the ini property", e);
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
	public void writeProperty(@NonNull String key, @Nullable String value) {
		this.writeProperty(key, value == null ? IniNull.INSTANCE : new IniValue(value));
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
		this.writeProperty(key, new IniValue(value));
	}
	
	/**
	 * Writes a single property with a number value.<br>
	 *
	 * @param key The property key
	 * @param value The number value
	 * @throws NullPointerException If the key is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeProperty(@NonNull String key, @Nullable Number value) {
		this.writeProperty(key, value == null ? IniNull.INSTANCE : new IniValue(value));
	}
	
	/**
	 * Writes a section header to the underlying output.<br>
	 *
	 * @param sectionName The name of the section
	 * @throws NullPointerException If the section name is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeSectionHeader(@NonNull String sectionName) {
		Objects.requireNonNull(sectionName, "Section name must not be null");
		
		try {
			this.config.ensureSectionMatches(sectionName);
			
			if (this.config.prettyPrint()) {
				this.writer.newLine();
			}
			
			this.writer.write("[");
			this.writer.write(sectionName);
			this.writer.write("]");
			this.writer.newLine();
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the section header", e);
		}
	}
	
	/**
	 * Writes a comment to the underlying output.<br>
	 *
	 * @param comment The comment text (without the comment character)
	 * @throws NullPointerException If the comment is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeComment(@NonNull String comment) {
		Objects.requireNonNull(comment, "Comment must not be null");
		
		try {
			char commentChar = this.config.commentCharacters().iterator().next();
			this.writer.write(commentChar);
			this.writer.write(" ");
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
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
