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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.util.Version;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

/**
 * A xml writer for writing xml elements to an output.<br>
 * The writer expects only one xml element per output.<br>
 *
 * @author Luis-St
 */
public class XmlWriter implements AutoCloseable {
	
	/**
	 * The xml config used by the writer.<br>
	 */
	private final XmlConfig config;
	/**
	 * The internal writer used to write the xml elements.<br>
	 */
	private final BufferedWriter writer;
	/**
	 * A flag indicating whether the xml declaration was already written.<br>
	 */
	private boolean wroteDeclaration;
	
	/**
	 * Constructs a new xml writer with the default configuration.<br>
	 * @param output The output to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public XmlWriter(@NotNull OutputProvider output) {
		this(output, XmlConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new xml writer with the given configuration.<br>
	 * @param output The output to create the writer for
	 * @param config The configuration to use for the writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public XmlWriter(@NotNull OutputProvider output, @NotNull XmlConfig config) {
		this.config = Objects.requireNonNull(config, "Xml config must not be null");
		this.writer = new BufferedWriter(new OutputStreamWriter(Objects.requireNonNull(output, "Output must not be null").getStream(), config.charset()));
	}
	
	/**
	 * Writes the given xml declaration to the output.<br>
	 * @param declaration The xml declaration to write
	 * @throws NullPointerException If the xml declaration is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeDeclaration(@NotNull XmlDeclaration declaration) {
		Objects.requireNonNull(declaration, "Xml declaration must not be null");
		if (!this.wroteDeclaration) {
			try {
				this.writer.write(declaration.toString());
				this.writer.flush();
				this.wroteDeclaration = true;
			} catch (IOException e) {
				throw new UncheckedIOException("An I/O error occurred while writing the xml declaration", e);
			}
		}
	}
	
	/**
	 * Writes the given xml element to the output.<br>
	 * @param element The xml element to write
	 * @throws NullPointerException If the xml element is null
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeXml(@NotNull XmlElement element) {
		Objects.requireNonNull(element, "Xml element must not be null");
		if (!this.wroteDeclaration) {
			this.writeDeclaration(new XmlDeclaration(Version.of(1, 0)));
		}
		try {
			this.writer.write(element.toString(this.config));
			this.writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the xml element", e);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.writer.close();
	}
}
