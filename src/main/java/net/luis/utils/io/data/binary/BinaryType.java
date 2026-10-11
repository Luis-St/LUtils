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

import net.luis.utils.io.data.binary.exception.BinarySyntaxException;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * The types of the binary format.<br>
 * Each type is identified by a single tag byte which is written in front of the payload of a value.<br>
 * <p>
 *     Booleans are the only type which is identified by two tag ids.<br>
 *     The id {@code 0x02} represents the value {@code false} and the id {@code 0x03} represents the value {@code true},<br>
 *     therefore a boolean does not need any payload.
 * </p>
 * <p>
 *     The payloads of the types are defined as follows:
 * </p>
 * <ul>
 *     <li>Null, absent and boolean: No payload</li>
 *     <li>Byte: A single byte</li>
 *     <li>Short, integer and long: A zigzag encoded variable-length integer</li>
 *     <li>Float and double: 4 or 8 bytes in big-endian ieee-754 format</li>
 *     <li>String: The length in bytes as a variable-length integer followed by the encoded characters</li>
 *     <li>List: The number of elements as a variable-length integer followed by the elements</li>
 *     <li>Struct: The number of fields as a variable-length integer followed by the fields, the field names are not written</li>
 *     <li>Map: The number of entries as a variable-length integer followed by the key-value pairs</li>
 * </ul>
 * <p>
 *     A list whose elements all have the same type is written in a compacted form which does not repeat the tag of every element.<br>
 *     The compacted forms are identified by their own tag ids which are all mapped back to {@link #LIST},<br>
 *     therefore they are an encoding detail of a list and not a type of their own:
 * </p>
 * <ul>
 *     <li>{@link #LIST_BYTE_ID}: The number of elements as a variable-length integer followed by the raw bytes</li>
 *     <li>{@link #LIST_BOOLEAN_ID}: The number of elements as a variable-length integer followed by the bits of the values, eight values per byte, the lowest bit first</li>
 *     <li>{@link #LIST_TYPED_ID}: The tag id of the elements, the number of elements as a variable-length integer and the payloads of the elements</li>
 * </ul>
 *
 * @author Luis-St
 */
public enum BinaryType {
	
	/**
	 * The type of a binary null value.<br>
	 */
	NULL((byte) 0x00, "binary null"),
	/**
	 * The type of absent value.<br>
	 * Absent values are used for fields of a struct which have not been set.<br>
	 */
	ABSENT((byte) 0x01, "binary absent"),
	/**
	 * The type of a binary boolean value.<br>
	 * The tag id {@code 0x02} represents {@code false}, the tag id {@code 0x03} represents {@code true}.<br>
	 */
	BOOLEAN((byte) 0x02, "binary boolean"),
	/**
	 * The type of a binary byte value.<br>
	 */
	BYTE((byte) 0x04, "binary byte"),
	/**
	 * The type of a binary short value.<br>
	 */
	SHORT((byte) 0x05, "binary short"),
	/**
	 * The type of a binary integer value.<br>
	 */
	INTEGER((byte) 0x06, "binary integer"),
	/**
	 * The type of a binary long value.<br>
	 */
	LONG((byte) 0x07, "binary long"),
	/**
	 * The type of a binary float value.<br>
	 */
	FLOAT((byte) 0x08, "binary float"),
	/**
	 * The type of a binary double value.<br>
	 */
	DOUBLE((byte) 0x09, "binary double"),
	/**
	 * The type of a binary string value.<br>
	 */
	STRING((byte) 0x0A, "binary string"),
	/**
	 * The type of a binary list value.<br>
	 */
	LIST((byte) 0x0B, "binary list"),
	/**
	 * The type of a binary struct value.<br>
	 * A struct holds a fixed number of fields which are identified by their position.<br>
	 */
	STRUCT((byte) 0x0C, "binary struct"),
	/**
	 * The type of a binary map value.<br>
	 * In contrast to a struct, the keys of a map are written to the output.<br>
	 */
	MAP((byte) 0x0D, "binary map");
	
	/**
	 * The tag id of a boolean value which is {@code true}.<br>
	 */
	public static final byte BOOLEAN_TRUE_ID = 0x03;
	/**
	 * The tag id of a list which holds only byte values.<br>
	 * The values are written as raw bytes without a tag per element.<br>
	 */
	public static final byte LIST_BYTE_ID = 0x0E;
	/**
	 * The tag id of a list which holds only boolean values.<br>
	 * The values are written as bits, eight values per byte, the lowest bit first.<br>
	 */
	public static final byte LIST_BOOLEAN_ID = 0x0F;
	/**
	 * The tag id of a list whose elements all have the same type.<br>
	 * The tag id of the elements is written once, the payloads of the elements follow without a tag per element.<br>
	 */
	public static final byte LIST_TYPED_ID = 0x10;
	
	/**
	 * The tag id of this type.<br>
	 */
	private final byte id;
	/**
	 * The human-readable name of this type.<br>
	 */
	private final String name;
	
	/**
	 * Constructs a new binary type with the given tag id and name.<br>
	 *
	 * @param id The tag id of the type
	 * @param name The human-readable name of the type
	 * @throws NullPointerException If the name is null
	 */
	BinaryType(byte id, @NonNull String name) {
		this.id = id;
		this.name = Objects.requireNonNull(name, "Name must not be null");
	}
	
	/**
	 * Returns the binary type which is identified by the given tag id.<br>
	 * The tag ids {@code 0x02} and {@code 0x03} are both mapped to {@link #BOOLEAN}.<br>
	 * The tag ids of the compacted list forms are all mapped to {@link #LIST}.<br>
	 *
	 * @param id The tag id to get the type for
	 * @return The type of the tag id
	 * @throws BinarySyntaxException If no type is identified by the given tag id
	 */
	public static @NonNull BinaryType fromId(byte id) {
		if (id == BOOLEAN_TRUE_ID) {
			return BOOLEAN;
		}
		if (id == LIST_BYTE_ID || id == LIST_BOOLEAN_ID || id == LIST_TYPED_ID) {
			return LIST;
		}
		
		for (BinaryType type : values()) {
			if (type.id == id) {
				return type;
			}
		}
		throw new BinarySyntaxException("Unknown binary type id: 0x" + String.format("%02X", id));
	}
	
	/**
	 * Returns the tag id of this type.<br>
	 * @return The tag id
	 */
	public byte getId() {
		return this.id;
	}
	
	/**
	 * Returns the human-readable name of this type.<br>
	 * Used for debugging and error messages.<br>
	 *
	 * @return The name of this type
	 */
	public @NonNull String getName() {
		return this.name;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.name;
	}
	//endregion
}
