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

package net.luis.utils.io.data.binary;

import net.luis.utils.io.data.OutputProvider;
import net.luis.utils.io.data.binary.exception.BinaryTypeException;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.util.Objects;

/**
 * A binary writer for writing binary elements to an output.<br>
 * The writer expects only one binary element per output.<br>
 * <p>
 *     The field names of a {@link BinaryStruct struct} are not written to the output,<br>
 *     the fields are identified by their position instead.
 * </p>
 *
 * @author Luis-St
 */
public class BinaryWriter implements AutoCloseable {
	
	/**
	 * The binary config used by the writer.<br>
	 */
	private final BinaryConfig config;
	/**
	 * The internal stream used to write the binary elements.<br>
	 */
	private final DataOutputStream stream;
	
	/**
	 * Constructs a new binary writer with the default configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @throws NullPointerException If the output is null
	 */
	public BinaryWriter(@NonNull OutputProvider output) {
		this(output, BinaryConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new binary writer with the given configuration.<br>
	 *
	 * @param output The output to create the writer for
	 * @param config The configuration to use for the writer
	 * @throws NullPointerException If the output or the configuration is null
	 */
	public BinaryWriter(@NonNull OutputProvider output, @NonNull BinaryConfig config) {
		this.config = Objects.requireNonNull(config, "Binary config must not be null");
		this.stream = new DataOutputStream(new BufferedOutputStream(Objects.requireNonNull(output, "Output must not be null").getStream()));
	}
	
	/**
	 * Writes the given binary element to a byte array using the default configuration.<br>
	 *
	 * @param binary The binary element to write
	 * @return The byte array with the encoded element
	 * @throws NullPointerException If the binary element is null
	 * @throws BinaryTypeException If the binary element can not be written
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public static byte @NonNull [] toByteArray(@NonNull BinaryElement binary) {
		return toByteArray(binary, BinaryConfig.DEFAULT);
	}
	
	/**
	 * Writes the given binary element to a byte array using the given configuration.<br>
	 *
	 * @param binary The binary element to write
	 * @param config The configuration to use for the writer
	 * @return The byte array with the encoded element
	 * @throws NullPointerException If the binary element or the configuration is null
	 * @throws BinaryTypeException If the binary element can not be written
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public static byte @NonNull [] toByteArray(@NonNull BinaryElement binary, @NonNull BinaryConfig config) {
		Objects.requireNonNull(binary, "Binary element must not be null");
		Objects.requireNonNull(config, "Binary config must not be null");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (BinaryWriter writer = new BinaryWriter(new OutputProvider(output), config)) {
			writer.writeBinary(binary);
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while closing the binary writer", e);
		}
		return output.toByteArray();
	}
	
	/**
	 * Encodes the given signed value as an unsigned value using the zigzag encoding.<br>
	 * The encoding maps small negative values to small unsigned values.<br>
	 *
	 * @param value The value to encode
	 * @return The zigzag encoded value
	 */
	private static long zigzag(long value) {
		return (value << 1) ^ (value >> 63);
	}
	
	/**
	 * Writes the given binary element to the output.<br>
	 *
	 * @param binary The binary element to write
	 * @throws NullPointerException If the binary element is null
	 * @throws BinaryTypeException If the binary element is an absent value or can not be written
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public void writeBinary(@NonNull BinaryElement binary) {
		Objects.requireNonNull(binary, "Binary element must not be null");
		if (binary.isBinaryAbsent()) {
			throw new BinaryTypeException("An absent value can only be written as a field of a struct");
		}
		
		try {
			if (this.config.writeHeader()) {
				this.stream.writeShort(BinaryConfig.MAGIC);
				this.stream.writeByte(BinaryConfig.VERSION);
			}
			
			this.writeElement(binary);
			this.stream.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the binary element", e);
		}
	}
	
	/**
	 * Writes the given binary element including its type tag to the output.<br>
	 *
	 * @param binary The binary element to write
	 * @throws NullPointerException If the binary element is null
	 * @throws BinaryTypeException If the value of the element does not match its type
	 * @throws IOException If an I/O error occurs
	 */
	private void writeElement(@NonNull BinaryElement binary) throws IOException {
		Objects.requireNonNull(binary, "Binary element must not be null");
		
		switch (binary.getType()) {
			case NULL, ABSENT -> this.stream.writeByte(binary.getType().getId());
			case BOOLEAN -> this.stream.writeByte(binary.getAsBoolean() ? BinaryType.BOOLEAN_TRUE_ID : BinaryType.BOOLEAN.getId());
			case BYTE -> {
				this.stream.writeByte(BinaryType.BYTE.getId());
				this.stream.writeByte(binary.getAsByte());
			}
			case SHORT -> {
				this.stream.writeByte(BinaryType.SHORT.getId());
				this.writeVarLong(zigzag(binary.getAsShort()));
			}
			case INTEGER -> {
				this.stream.writeByte(BinaryType.INTEGER.getId());
				this.writeVarLong(zigzag(binary.getAsInteger()));
			}
			case LONG -> {
				this.stream.writeByte(BinaryType.LONG.getId());
				this.writeVarLong(zigzag(binary.getAsLong()));
			}
			case FLOAT -> {
				this.stream.writeByte(BinaryType.FLOAT.getId());
				this.stream.writeFloat(binary.getAsFloat());
			}
			case DOUBLE -> {
				this.stream.writeByte(BinaryType.DOUBLE.getId());
				this.stream.writeDouble(binary.getAsDouble());
			}
			case STRING -> {
				this.stream.writeByte(BinaryType.STRING.getId());
				this.writeString(binary.getAsString());
			}
			case LIST -> {
				BinaryArray array = binary.getAsBinaryArray();
				this.stream.writeByte(BinaryType.LIST.getId());
				this.writeVarLong(array.size());
				for (BinaryElement element : array) {
					this.writeElement(element);
				}
			}
			case STRUCT -> {
				BinaryStruct struct = binary.getAsBinaryStruct();
				this.stream.writeByte(BinaryType.STRUCT.getId());
				this.writeVarLong(struct.size());
				for (int i = 0; i < struct.size(); i++) {
					this.writeElement(struct.get(i));
				}
			}
			case MAP -> {
				BinaryMap map = binary.getAsBinaryMap();
				this.stream.writeByte(BinaryType.MAP.getId());
				this.writeVarLong(map.size());
				for (var entry : map.getElements().entrySet()) {
					this.writeString(entry.getKey());
					this.writeElement(entry.getValue());
				}
			}
		}
	}
	
	/**
	 * Writes the given string as its length in bytes followed by the encoded characters.<br>
	 *
	 * @param value The string to write
	 * @throws NullPointerException If the value is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeString(@NonNull String value) throws IOException {
		Objects.requireNonNull(value, "Value must not be null");
		
		byte[] bytes = value.getBytes(this.config.charset());
		this.writeVarLong(bytes.length);
		this.stream.write(bytes);
	}
	
	/**
	 * Writes the given value as an unsigned variable-length integer.<br>
	 * Each written byte holds seven bits of the value, the highest bit indicates whether another byte follows.<br>
	 *
	 * @param value The value to write
	 * @throws IOException If an I/O error occurs
	 */
	private void writeVarLong(long value) throws IOException {
		long remaining = value;
		while ((remaining & ~0x7FL) != 0) {
			this.stream.writeByte((int) (remaining & 0x7F) | 0x80);
			remaining >>>= 7;
		}
		this.stream.writeByte((int) remaining);
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
