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

package net.luis.utils.crypto.util;

import net.luis.utils.annotation.type.Facade;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

/**
 * Byte utilities for cryptographic code.<br>
 * <p>
 *     This class is deliberately narrow: it holds only what is specific to cryptography, such as the constant-time comparison and the wiping helpers.<br>
 *     Hex and Base64 conversion are not part of it, because they already exist in several places in this library and a further copy would help nobody.<br>
 *     The few crypto call sites that need text encoding use {@link HexFormat} and {@link Base64} directly.
 * </p>
 *
 * @author Luis-St
 */
@Facade
public final class CryptoBytes {
	
	/**
	 * A shared empty byte array, used wherever an absent value is encoded as no bytes.<br>
	 */
	public static final byte[] EMPTY = new byte[0];
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CryptoBytes() {}
	
	/**
	 * Compares two byte arrays without leaking their contents through timing.<br>
	 * <p>
	 *     The comparison takes the same time for all inputs of the same length.<br>
	 *     The length itself is not treated as secret.<br>
	 *     Both arrays may be null, in which case they are equal only if both are null.
	 * </p>
	 *
	 * @param first The first array to compare
	 * @param second The second array to compare
	 * @return True if both arrays are null or hold the same bytes
	 */
	public static boolean equalsConstantTime(byte @Nullable [] first, byte @Nullable [] second) {
		return MessageDigest.isEqual(first, second);
	}
	
	/**
	 * Concatenates the given byte arrays in order.<br>
	 * Concatenating no parts at all returns an empty array.<br>
	 *
	 * @param parts The arrays to concatenate
	 * @return A new array holding every part in order
	 * @throws NullPointerException If the parts array or any part is null
	 */
	public static byte @NonNull [] concat(byte @NonNull []... parts) {
		Objects.requireNonNull(parts, "Parts must not be null");
		
		int length = 0;
		for (byte[] part : parts) {
			Objects.requireNonNull(part, "Part must not be null");
			length += part.length;
		}
		
		byte[] result = new byte[length];
		int offset = 0;
		for (byte[] part : parts) {
			System.arraycopy(part, 0, result, offset, part.length);
			offset += part.length;
		}
		return result;
	}
	
	/**
	 * Combines two equally long byte arrays with a bitwise exclusive or.<br>
	 *
	 * @param first The first array
	 * @param second The second array
	 * @return A new array holding the exclusive or of both inputs
	 * @throws NullPointerException If any of the arrays is null
	 * @throws IllegalArgumentException If the arrays do not have the same length
	 */
	public static byte @NonNull [] xor(byte @NonNull [] first, byte @NonNull [] second) {
		Objects.requireNonNull(first, "First array must not be null");
		Objects.requireNonNull(second, "Second array must not be null");
		if (first.length != second.length) {
			throw new IllegalArgumentException("Cannot xor arrays of different length: " + first.length + " != " + second.length);
		}
		
		byte[] result = new byte[first.length];
		for (int i = 0; i < first.length; i++) {
			result[i] = (byte) (first[i] ^ second[i]);
		}
		return result;
	}
	
	/**
	 * Copies a section out of the given array.<br>
	 * <p>
	 *     An out of range section is a programming error rather than malformed input, so this throws an unchecked bounds exception and not a crypto exception.<br>
	 *     Callers parsing untrusted data are expected to validate the length before slicing.
	 * </p>
	 *
	 * @param data The array to copy from
	 * @param offset The index of the first byte to copy
	 * @param length The number of bytes to copy
	 * @return A new array holding the requested section
	 * @throws NullPointerException If the data is null
	 * @throws IndexOutOfBoundsException If the section is not fully inside the array
	 */
	public static byte @NonNull [] slice(byte @NonNull [] data, int offset, int length) {
		Objects.requireNonNull(data, "Data must not be null");
		Objects.checkFromIndexSize(offset, length, data.length);
		return Arrays.copyOfRange(data, offset, offset + length);
	}
	
	/**
	 * Overwrites the given array with zeros.<br>
	 * <p>
	 *     Wiping heap memory is best effort: the garbage collector may already have copied the array elsewhere.<br>
	 *     It closes the window, it does not eliminate it.
	 * </p>
	 *
	 * @param data The array to wipe, may be null
	 */
	public static void wipe(byte @Nullable [] data) {
		if (data != null) {
			Arrays.fill(data, (byte) 0);
		}
	}
	
	/**
	 * Overwrites the given array with null characters.<br>
	 * The same best effort caveat as for {@link #wipe(byte[])} applies.<br>
	 *
	 * @param data The array to wipe, may be null
	 */
	public static void wipe(char @Nullable [] data) {
		if (data != null) {
			Arrays.fill(data, '\0');
		}
	}
	
	/**
	 * Converts characters to bytes without ever creating a {@link String}.<br>
	 * <p>
	 *     A string would keep the secret alive in the string pool and out of reach of any wipe, which is why passwords travel as character arrays throughout this package.
	 * </p>
	 * <p>
	 *     The intermediate buffer is cleared before returning.<br>
	 *     The caller's array is left untouched and remains the caller's responsibility to wipe.
	 * </p>
	 *
	 * @param chars The characters to convert
	 * @param charset The charset to encode with
	 * @return The encoded bytes
	 * @throws NullPointerException If the characters or the charset are null
	 */
	public static byte @NonNull [] toBytes(char @NonNull [] chars, @NonNull Charset charset) {
		Objects.requireNonNull(chars, "Chars must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		ByteBuffer byteBuffer = charset.encode(CharBuffer.wrap(chars));
		byte[] result = new byte[byteBuffer.remaining()];
		byteBuffer.get(result);
		if (byteBuffer.hasArray()) {
			Arrays.fill(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.arrayOffset() + byteBuffer.capacity(), (byte) 0);
		}
		return result;
	}
	
	/**
	 * Converts the given value into its two byte big-endian representation.<br>
	 *
	 * @param value The value to convert
	 * @return The two bytes of the value
	 */
	public static byte @NonNull [] of(short value) {
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}
	
	/**
	 * Converts the given value into its four byte big-endian representation.<br>
	 *
	 * @param value The value to convert
	 * @return The four bytes of the value
	 */
	public static byte @NonNull [] of(int value) {
		return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
	}
	
	/**
	 * Converts the given value into its eight byte big-endian representation.<br>
	 *
	 * @param value The value to convert
	 * @return The eight bytes of the value
	 */
	public static byte @NonNull [] of(long value) {
		return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
	}
}
