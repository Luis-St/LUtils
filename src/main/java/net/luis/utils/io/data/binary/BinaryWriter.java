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
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * A binary writer for writing binary elements to an output.<br>
 * The writer expects only one binary element per output.<br>
 * <p>
 *     The field names of a {@link BinaryStruct struct} are not written to the output,<br>
 *     the fields are identified by their position instead.
 * </p>
 * <p>
 *     A list whose elements all have the same type is written in a compacted form which does not repeat the tag of every element.<br>
 *     The compaction is applied automatically and is transparent, a reader restores the same elements from both forms.
 * </p>
 * <p>
 *     The encoded data is compressed if the {@link BinaryConfig#compression() compression mode} of the configuration requires it.<br>
 *     Compression is applied to the document as a whole and requires the header, because the header holds the flag which marks the data as compressed.
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
	 * Compresses the given data with the deflate algorithm.<br>
	 *
	 * @param data The data to compress
	 * @return The compressed data
	 * @throws NullPointerException If the data is null
	 * @throws IOException If an I/O error occurs
	 */
	private static byte @NonNull [] deflate(byte @NonNull [] data) throws IOException {
		Objects.requireNonNull(data, "Data must not be null");
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
		try (DeflaterOutputStream out = new DeflaterOutputStream(buffer, deflater)) {
			out.write(data);
		} finally {
			deflater.end();
		}
		return buffer.toByteArray();
	}
	
	/**
	 * Returns the type which all elements of the given array have.<br>
	 *
	 * @param array The array to get the uniform type of
	 * @return The type of all elements or null if the array is empty, holds an element which is not a primitive or holds elements of different types
	 * @throws NullPointerException If the array is null
	 */
	private static @Nullable BinaryType uniformType(@NonNull BinaryArray array) {
		Objects.requireNonNull(array, "Array must not be null");
		
		BinaryType type = null;
		for (BinaryElement element : array) {
			if (!element.isBinaryPrimitive()) {
				return null;
			}
			
			if (type == null) {
				type = element.getType();
			} else if (type != element.getType()) {
				return null;
			}
		}
		return type;
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
			if (this.config.compression() == BinaryCompression.NONE) {
				this.writeHeader(this.stream, false);
				this.writeElement(this.stream, binary);
			} else {
				this.writeCompressed(binary);
			}
			this.stream.flush();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while writing the binary element", e);
		}
	}
	
	/**
	 * Writes the header to the given output if the header is enabled in the configuration.<br>
	 * The header holds the magic number, the format version and the flags of the data.<br>
	 *
	 * @param out The output to write the header to
	 * @param compressed Whether the data behind the header is compressed
	 * @throws NullPointerException If the output is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeHeader(@NonNull DataOutputStream out, boolean compressed) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		if (!this.config.writeHeader()) {
			return;
		}
		
		out.writeShort(BinaryConfig.MAGIC);
		out.writeByte(BinaryConfig.VERSION);
		out.writeByte(compressed ? BinaryConfig.FLAG_COMPRESSED : 0);
	}
	
	/**
	 * Writes the given binary element as a compressed document to the output.<br>
	 * <p>
	 *     The element is encoded into memory and compressed afterwards.<br>
	 *     In the {@link BinaryCompression#AUTO auto} mode the compressed data is only used if it is smaller than the encoded data,<br>
	 *     therefore the output of this method is never larger than the output of an uncompressed document.
	 * </p>
	 * <p>
	 *     The length of the compressed data is written in front of it,<br>
	 *     so that a reader knows how many bytes belong to the document.
	 * </p>
	 *
	 * @param binary The binary element to write
	 * @throws NullPointerException If the binary element is null
	 * @throws BinaryTypeException If the value of an element does not match its type
	 * @throws IOException If an I/O error occurs
	 */
	private void writeCompressed(@NonNull BinaryElement binary) throws IOException {
		Objects.requireNonNull(binary, "Binary element must not be null");
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(buffer)) {
			this.writeElement(out, binary);
		}
		
		byte[] encoded = buffer.toByteArray();
		byte[] compressed = deflate(encoded);
		if (this.config.compression() == BinaryCompression.AUTO && compressed.length >= encoded.length) {
			this.writeHeader(this.stream, false);
			this.stream.write(encoded);
			return;
		}
		
		this.writeHeader(this.stream, true);
		this.writeVarLong(this.stream, compressed.length);
		this.stream.write(compressed);
	}
	
	/**
	 * Writes the given binary element including its type tag to the given output.<br>
	 *
	 * @param out The output to write the element to
	 * @param binary The binary element to write
	 * @throws NullPointerException If the output or the binary element is null
	 * @throws BinaryTypeException If the value of the element does not match its type
	 * @throws IOException If an I/O error occurs
	 */
	private void writeElement(@NonNull DataOutputStream out, @NonNull BinaryElement binary) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		Objects.requireNonNull(binary, "Binary element must not be null");
		
		switch (binary.getType()) {
			case NULL, ABSENT -> out.writeByte(binary.getType().getId());
			case BOOLEAN -> out.writeByte(binary.getAsBoolean() ? BinaryType.BOOLEAN_TRUE_ID : BinaryType.BOOLEAN.getId());
			case BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING -> {
				out.writeByte(binary.getType().getId());
				this.writePayload(out, binary);
			}
			case LIST -> this.writeList(out, binary.getAsBinaryArray());
			case STRUCT -> {
				BinaryStruct struct = binary.getAsBinaryStruct();
				out.writeByte(BinaryType.STRUCT.getId());
				this.writeVarLong(out, struct.size());
				for (int i = 0; i < struct.size(); i++) {
					this.writeElement(out, struct.get(i));
				}
			}
			case MAP -> {
				BinaryMap map = binary.getAsBinaryMap();
				out.writeByte(BinaryType.MAP.getId());
				this.writeVarLong(out, map.size());
				for (var entry : map.getElements().entrySet()) {
					this.writeString(out, entry.getKey());
					this.writeElement(out, entry.getValue());
				}
			}
		}
	}
	
	/**
	 * Writes the payload of the given binary element without its type tag to the given output.<br>
	 *
	 * @param out The output to write the payload to
	 * @param binary The binary element to write the payload of
	 * @throws NullPointerException If the output or the binary element is null
	 * @throws BinaryTypeException If the element has no payload or the value of the element does not match its type
	 * @throws IOException If an I/O error occurs
	 */
	private void writePayload(@NonNull DataOutputStream out, @NonNull BinaryElement binary) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		Objects.requireNonNull(binary, "Binary element must not be null");
		
		switch (binary.getType()) {
			case BYTE -> out.writeByte(binary.getAsByte());
			case SHORT -> this.writeVarLong(out, zigzag(binary.getAsShort()));
			case INTEGER -> this.writeVarLong(out, zigzag(binary.getAsInteger()));
			case LONG -> this.writeVarLong(out, zigzag(binary.getAsLong()));
			case FLOAT -> out.writeFloat(binary.getAsFloat());
			case DOUBLE -> out.writeDouble(binary.getAsDouble());
			case STRING -> this.writeString(out, binary.getAsString());
			default -> throw new BinaryTypeException("The type " + binary.getType() + " has no payload which can be written without its tag");
		}
	}
	
	/**
	 * Writes the given binary array to the given output.<br>
	 * <p>
	 *     A list whose elements all have the same type is written in a compacted form which does not repeat the tag of every element.<br>
	 *     The compacted forms are only used if they are smaller than the plain form,<br>
	 *     a list of a single boolean is therefore written in the plain form.
	 * </p>
	 *
	 * @param out The output to write the array to
	 * @param array The array to write
	 * @throws NullPointerException If the output or the array is null
	 * @throws BinaryTypeException If the value of an element does not match its type
	 * @throws IOException If an I/O error occurs
	 */
	private void writeList(@NonNull DataOutputStream out, @NonNull BinaryArray array) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		Objects.requireNonNull(array, "Array must not be null");
		
		BinaryType elementType = uniformType(array);
		if (elementType == BinaryType.BYTE) {
			this.writeByteList(out, array);
			return;
		}
		if (elementType == BinaryType.BOOLEAN && array.size() > 1) {
			this.writeBooleanList(out, array);
			return;
		}
		if (elementType != null && array.size() > 1 && elementType != BinaryType.BOOLEAN) {
			out.writeByte(BinaryType.LIST_TYPED_ID);
			out.writeByte(elementType.getId());
			this.writeVarLong(out, array.size());
			for (BinaryElement element : array) {
				this.writePayload(out, element);
			}
			return;
		}
		
		out.writeByte(BinaryType.LIST.getId());
		this.writeVarLong(out, array.size());
		for (BinaryElement element : array) {
			this.writeElement(out, element);
		}
	}
	
	/**
	 * Writes the given binary array which holds only byte values as raw bytes to the given output.<br>
	 *
	 * @param out The output to write the array to
	 * @param array The array to write
	 * @throws NullPointerException If the output or the array is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeByteList(@NonNull DataOutputStream out, @NonNull BinaryArray array) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		Objects.requireNonNull(array, "Array must not be null");
		
		out.writeByte(BinaryType.LIST_BYTE_ID);
		this.writeVarLong(out, array.size());
		
		byte[] bytes = new byte[array.size()];
		for (int i = 0; i < array.size(); i++) {
			bytes[i] = array.get(i).getAsByte();
		}
		out.write(bytes);
	}
	
	/**
	 * Writes the given binary array which holds only boolean values as packed bits to the given output.<br>
	 * Eight values are packed into a single byte, the value of the first element is stored in the lowest bit.<br>
	 *
	 * @param out The output to write the array to
	 * @param array The array to write
	 * @throws NullPointerException If the output or the array is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeBooleanList(@NonNull DataOutputStream out, @NonNull BinaryArray array) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		Objects.requireNonNull(array, "Array must not be null");
		
		out.writeByte(BinaryType.LIST_BOOLEAN_ID);
		this.writeVarLong(out, array.size());
		
		int bits = 0;
		int count = 0;
		for (BinaryElement element : array) {
			if (element.getAsBoolean()) {
				bits |= 1 << count;
			}
			
			if (++count == Byte.SIZE) {
				out.writeByte(bits);
				bits = 0;
				count = 0;
			}
		}
		if (count > 0) {
			out.writeByte(bits);
		}
	}
	
	/**
	 * Writes the given string as its length in bytes followed by the encoded characters.<br>
	 *
	 * @param out The output to write the string to
	 * @param value The string to write
	 * @throws NullPointerException If the output or the value is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeString(@NonNull DataOutputStream out, @NonNull String value) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		byte[] bytes = value.getBytes(this.config.charset());
		this.writeVarLong(out, bytes.length);
		out.write(bytes);
	}
	
	/**
	 * Writes the given value as an unsigned variable-length integer.<br>
	 * Each written byte holds seven bits of the value, the highest bit indicates whether another byte follows.<br>
	 *
	 * @param out The output to write the value to
	 * @param value The value to write
	 * @throws NullPointerException If the output is null
	 * @throws IOException If an I/O error occurs
	 */
	private void writeVarLong(@NonNull DataOutputStream out, long value) throws IOException {
		Objects.requireNonNull(out, "Output must not be null");
		
		long remaining = value;
		while ((remaining & ~0x7FL) != 0) {
			out.writeByte((int) (remaining & 0x7F) | 0x80);
			remaining >>>= 7;
		}
		out.writeByte((int) remaining);
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
