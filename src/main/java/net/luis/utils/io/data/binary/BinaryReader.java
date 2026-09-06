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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.binary.exception.BinarySyntaxException;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * A binary reader for reading binary elements from an input.<br>
 * The reader expects only one binary element per input.<br>
 * <p>
 *     A {@link BinaryStruct struct} which is read from an input does not know the names of its fields,<br>
 *     the fields must be accessed by their position.
 * </p>
 * <p>
 *     The compacted forms of a list are read transparently, they yield the same elements as the plain form.<br>
 *     A compressed document is recognized by the flag in its header,<br>
 *     therefore the compression mode of the configuration is not used while reading.
 * </p>
 *
 * @author Luis-St
 */
public class BinaryReader implements AutoCloseable {
	
	/**
	 * The binary config used by the reader.<br>
	 */
	private final BinaryConfig config;
	/**
	 * The internal stream used to read the binary elements.<br>
	 */
	private final DataInputStream stream;
	
	/**
	 * Constructs a new binary reader with the default configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @throws NullPointerException If the input is null
	 */
	public BinaryReader(@NonNull InputProvider input) {
		this(input, BinaryConfig.DEFAULT);
	}
	
	/**
	 * Constructs a new binary reader with the given configuration.<br>
	 *
	 * @param input The input to create the reader for
	 * @param config The configuration to use for the reader
	 * @throws NullPointerException If the input or the configuration is null
	 */
	public BinaryReader(@NonNull InputProvider input, @NonNull BinaryConfig config) {
		this.config = Objects.requireNonNull(config, "Binary config must not be null");
		this.stream = new DataInputStream(new BufferedInputStream(Objects.requireNonNull(input, "Input must not be null").getStream()));
	}
	
	/**
	 * Reads a binary element from the given byte array using the default configuration.<br>
	 *
	 * @param data The byte array to read the element from
	 * @return The binary element which was read
	 * @throws NullPointerException If the data is null
	 * @throws BinarySyntaxException If the data is invalid or truncated
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public static @NonNull BinaryElement fromByteArray(byte @NonNull [] data) {
		return fromByteArray(data, BinaryConfig.DEFAULT);
	}
	
	/**
	 * Reads a binary element from the given byte array using the given configuration.<br>
	 *
	 * @param data The byte array to read the element from
	 * @param config The configuration to use for the reader
	 * @return The binary element which was read
	 * @throws NullPointerException If the data or the configuration is null
	 * @throws BinarySyntaxException If the data is invalid or truncated
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public static @NonNull BinaryElement fromByteArray(byte @NonNull [] data, @NonNull BinaryConfig config) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.requireNonNull(config, "Binary config must not be null");
		
		try (BinaryReader reader = new BinaryReader(new InputProvider(data), config)) {
			return reader.readBinary();
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while closing the binary reader", e);
		}
	}
	
	/**
	 * Decodes the given zigzag encoded value back to a signed value.<br>
	 *
	 * @param value The value to decode
	 * @return The decoded value
	 */
	private static long zagzig(long value) {
		return (value >>> 1) ^ -(value & 1);
	}
	
	/**
	 * Reads a binary element from the input.<br>
	 *
	 * @return The binary element which was read
	 * @throws BinarySyntaxException If the data is invalid or truncated
	 * @throws UncheckedIOException If an I/O error occurs
	 */
	public @NonNull BinaryElement readBinary() {
		try {
			if (this.config.writeHeader()) {
				int magic = this.stream.readUnsignedShort();
				if (magic != BinaryConfig.MAGIC) {
					throw new BinarySyntaxException("Invalid magic number: 0x" + String.format("%04X", magic));
				}
				
				byte version = this.stream.readByte();
				if (version != BinaryConfig.VERSION) {
					throw new BinarySyntaxException("Unsupported binary format version: " + version);
				}
				
				byte flags = this.stream.readByte();
				if ((flags & ~BinaryConfig.FLAG_COMPRESSED) != 0) {
					throw new BinarySyntaxException("Unknown binary header flags: 0x" + String.format("%02X", flags));
				}
				if ((flags & BinaryConfig.FLAG_COMPRESSED) != 0) {
					return this.readCompressed();
				}
			}
			return this.readElement(0);
		} catch (EOFException e) {
			throw new BinarySyntaxException("Unable to read binary element, the input is truncated", e);
		} catch (IOException e) {
			throw new UncheckedIOException("An I/O error occurred while reading the binary element", e);
		}
	}
	
	/**
	 * Reads a compressed document from the input and decodes the element it holds.<br>
	 * The compressed data is preceded by its length, therefore the input may hold more data than the document.<br>
	 *
	 * @return The binary element which was read
	 * @throws BinarySyntaxException If the data is invalid or a limit of the configuration is exceeded
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull BinaryElement readCompressed() throws IOException {
		long length = this.readVarLong();
		if (0 > length || length > this.config.maxDocumentSize()) {
			throw new BinarySyntaxException("The length of the compressed document (" + length + ") exceeds the maximum document size of " + this.config.maxDocumentSize());
		}
		
		byte[] compressed = new byte[(int) length];
		this.stream.readFully(compressed);
		
		BinaryConfig config = new BinaryConfig(
			false, this.config.maxDepth(), this.config.maxCollectionSize(), this.config.maxStringLength(), this.config.maxDocumentSize(), this.config.charset(), BinaryCompression.NONE
		);
		try (BinaryReader reader = new BinaryReader(new InputProvider(this.inflate(compressed)), config)) {
			return reader.readBinary();
		}
	}
	
	/**
	 * Decompresses the given data with the deflate algorithm.<br>
	 * The size of the decompressed data is limited by the maximum document size of the configuration.<br>
	 *
	 * @param data The data to decompress
	 * @return The decompressed data
	 * @throws NullPointerException If the data is null
	 * @throws BinarySyntaxException If the data is not valid deflate data or the decompressed data exceeds the maximum document size
	 */
	private byte @NonNull [] inflate(byte @NonNull [] data) {
		Objects.requireNonNull(data, "Data must not be null");
		
		Inflater inflater = new Inflater();
		inflater.setInput(data);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		try {
			while (!inflater.finished()) {
				int read = inflater.inflate(buffer);
				if (read == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
					throw new BinarySyntaxException("Unable to decompress the binary document, the compressed data is truncated");
				}
				
				output.write(buffer, 0, read);
				if (output.size() > this.config.maxDocumentSize()) {
					throw new BinarySyntaxException("The size of the decompressed document exceeds the maximum document size of " + this.config.maxDocumentSize());
				}
			}
		} catch (DataFormatException e) {
			throw new BinarySyntaxException("Unable to decompress the binary document, the compressed data is invalid", e);
		} finally {
			inflater.end();
		}
		return output.toByteArray();
	}
	
	/**
	 * Reads a single binary element including its type tag from the input.<br>
	 *
	 * @param depth The current nesting depth
	 * @return The binary element which was read
	 * @throws IllegalArgumentException If the depth is negative
	 * @throws BinarySyntaxException If the data is invalid or a limit of the configuration is exceeded
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull BinaryElement readElement(int depth) throws IOException {
		if (0 > depth) {
			throw new IllegalArgumentException("Depth must not be negative, but was " + depth);
		}
		if (depth > this.config.maxDepth()) {
			throw new BinarySyntaxException("The nesting depth of the binary data exceeds the maximum depth of " + this.config.maxDepth());
		}
		
		byte id = this.stream.readByte();
		if (id == BinaryType.LIST_BYTE_ID) {
			return this.readByteList();
		}
		if (id == BinaryType.LIST_BOOLEAN_ID) {
			return this.readBooleanList();
		}
		if (id == BinaryType.LIST_TYPED_ID) {
			return this.readTypedList(depth);
		}
		return this.readPayload(BinaryType.fromId(id), id, depth);
	}
	
	/**
	 * Reads the payload of a binary element of the given type from the input.<br>
	 *
	 * @param type The type of the element
	 * @param id The tag id which was read for the element, it is used to distinguish the two boolean values
	 * @param depth The current nesting depth
	 * @return The binary element which was read
	 * @throws NullPointerException If the type is null
	 * @throws BinarySyntaxException If the data is invalid or a limit of the configuration is exceeded
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull BinaryElement readPayload(@NonNull BinaryType type, byte id, int depth) throws IOException {
		Objects.requireNonNull(type, "Type must not be null");
		
		return switch (type) {
			case NULL -> BinaryNull.INSTANCE;
			case ABSENT -> BinaryAbsent.INSTANCE;
			case BOOLEAN -> new BinaryPrimitive(id == BinaryType.BOOLEAN_TRUE_ID);
			case BYTE -> new BinaryPrimitive(this.stream.readByte());
			case SHORT -> new BinaryPrimitive((short) zagzig(this.readVarLong()));
			case INTEGER -> new BinaryPrimitive((int) zagzig(this.readVarLong()));
			case LONG -> new BinaryPrimitive(zagzig(this.readVarLong()));
			case FLOAT -> new BinaryPrimitive(this.stream.readFloat());
			case DOUBLE -> new BinaryPrimitive(this.stream.readDouble());
			case STRING -> new BinaryPrimitive(this.readString());
			case LIST -> {
				int size = this.readSize("list");
				List<BinaryElement> elements = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					elements.add(this.readElement(depth + 1));
				}
				yield new BinaryArray(elements);
			}
			case STRUCT -> {
				int size = this.readSize("struct");
				List<BinaryElement> values = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					values.add(this.readElement(depth + 1));
				}
				yield new BinaryStruct(values);
			}
			case MAP -> {
				int size = this.readSize("map");
				BinaryMap map = new BinaryMap();
				for (int i = 0; i < size; i++) {
					map.add(this.readString(), this.readElement(depth + 1));
				}
				yield map;
			}
		};
	}
	
	/**
	 * Reads a list which holds only byte values from the input.<br>
	 * The values are stored as raw bytes without a tag per element.<br>
	 *
	 * @return The list which was read
	 * @throws BinarySyntaxException If the size exceeds the maximum collection size
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull BinaryArray readByteList() throws IOException {
		int size = this.readSize("list");
		byte[] bytes = new byte[size];
		this.stream.readFully(bytes);
		
		List<BinaryElement> elements = new ArrayList<>(size);
		for (byte value : bytes) {
			elements.add(new BinaryPrimitive(value));
		}
		return new BinaryArray(elements);
	}
	
	/**
	 * Reads a list which holds only boolean values from the input.<br>
	 * The values are stored as bits, eight values per byte, the value of the first element is stored in the lowest bit.<br>
	 *
	 * @return The list which was read
	 * @throws BinarySyntaxException If the size exceeds the maximum collection size
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull BinaryArray readBooleanList() throws IOException {
		int size = this.readSize("list");
		byte[] bits = new byte[(size + Byte.SIZE - 1) / Byte.SIZE];
		this.stream.readFully(bits);
		
		List<BinaryElement> elements = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			elements.add(new BinaryPrimitive((bits[i / Byte.SIZE] & 1 << i % Byte.SIZE) != 0));
		}
		return new BinaryArray(elements);
	}
	
	/**
	 * Reads a list whose elements all have the same type from the input.<br>
	 * The tag id of the elements is stored once, the payloads of the elements follow without a tag per element.<br>
	 *
	 * @param depth The current nesting depth
	 * @return The list which was read
	 * @throws BinarySyntaxException If the element type has no payload, the element type is not a primitive or the size exceeds the maximum collection size
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull BinaryArray readTypedList(int depth) throws IOException {
		byte elementId = this.stream.readByte();
		BinaryType elementType = BinaryType.fromId(elementId);
		if (elementType == BinaryType.NULL || elementType == BinaryType.ABSENT || elementType == BinaryType.BOOLEAN) {
			throw new BinarySyntaxException("A typed list can not hold elements of the type " + elementType + ", the type has no payload");
		}
		if (elementType == BinaryType.LIST || elementType == BinaryType.STRUCT || elementType == BinaryType.MAP) {
			throw new BinarySyntaxException("A typed list can not hold elements of the type " + elementType + ", the type is not a primitive");
		}
		
		int size = this.readSize("list");
		List<BinaryElement> elements = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			elements.add(this.readPayload(elementType, elementId, depth + 1));
		}
		return new BinaryArray(elements);
	}
	
	/**
	 * Reads the number of elements of a list, struct or map from the input.<br>
	 *
	 * @param name The name of the collection type, used for error messages
	 * @return The number of elements
	 * @throws NullPointerException If the name is null
	 * @throws BinarySyntaxException If the size is negative or exceeds the maximum collection size
	 * @throws IOException If an I/O error occurs
	 */
	private int readSize(@NonNull String name) throws IOException {
		Objects.requireNonNull(name, "Name must not be null");
		
		long size = this.readVarLong();
		if (0 > size || size > this.config.maxCollectionSize()) {
			throw new BinarySyntaxException("The size of the " + name + " (" + size + ") exceeds the maximum collection size of " + this.config.maxCollectionSize());
		}
		return (int) size;
	}
	
	/**
	 * Reads a string as its length in bytes followed by the encoded characters from the input.<br>
	 *
	 * @return The string which was read
	 * @throws BinarySyntaxException If the length is negative or exceeds the maximum string length
	 * @throws IOException If an I/O error occurs
	 */
	private @NonNull String readString() throws IOException {
		long length = this.readVarLong();
		if (0 > length || length > this.config.maxStringLength()) {
			throw new BinarySyntaxException("The length of the string (" + length + ") exceeds the maximum string length of " + this.config.maxStringLength());
		}
		
		byte[] bytes = new byte[(int) length];
		this.stream.readFully(bytes);
		return new String(bytes, this.config.charset());
	}
	
	/**
	 * Reads an unsigned variable-length integer from the input.<br>
	 *
	 * @return The value which was read
	 * @throws BinarySyntaxException If the value is longer than ten bytes
	 * @throws IOException If an I/O error occurs
	 */
	private long readVarLong() throws IOException {
		long value = 0;
		for (int shift = 0; shift < 70; shift += 7) {
			byte current = this.stream.readByte();
			value |= (long) (current & 0x7F) << shift;
			
			if ((current & 0x80) == 0) {
				return value;
			}
		}
		throw new BinarySyntaxException("The variable-length integer is longer than ten bytes");
	}
	
	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}
