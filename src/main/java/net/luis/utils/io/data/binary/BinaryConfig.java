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

import net.luis.utils.io.data.config.ReadOnly;
import org.jspecify.annotations.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration for reading and writing binary elements.<br>
 * <p>
 *     The limits of this configuration are only used when reading binary data.<br>
 *     They protect the reader against malformed or malicious input which would otherwise<br>
 *     allocate an excessive amount of memory, this is important when the data is read from a network connection.
 * </p>
 *
 * @author Luis-St
 *
 * @param writeHeader Whether the magic number and the format version are written in front of the data
 * @param maxDepth The maximum nesting depth of the data which is read (read-only)
 * @param maxCollectionSize The maximum number of elements of a list, struct or map which is read (read-only)
 * @param maxStringLength The maximum length in bytes of a string which is read (read-only)
 * @param charset The charset to use for reading and writing strings
 */
public record BinaryConfig(
	boolean writeHeader,
	@ReadOnly int maxDepth,
	@ReadOnly int maxCollectionSize,
	@ReadOnly int maxStringLength,
	@NonNull Charset charset
) {
	
	/**
	 * The magic number which is written in front of the data if the header is enabled.<br>
	 */
	public static final int MAGIC = 0x4C42;
	/**
	 * The version of the binary format which is written after the magic number if the header is enabled.<br>
	 */
	public static final byte VERSION = 1;
	
	/**
	 * The default binary configuration.<br>
	 * <ul>
	 *     <li>Write header: false</li>
	 *     <li>Max depth: 64</li>
	 *     <li>Max collection size: 65536</li>
	 *     <li>Max string length: 1048576</li>
	 *     <li>Charset: UTF-8</li>
	 * </ul>
	 */
	public static final BinaryConfig DEFAULT = new BinaryConfig(
		false,
		64,
		65536,
		1048576,
		StandardCharsets.UTF_8
	);
	
	/**
	 * Constructs a new binary configuration.<br>
	 *
	 * @param writeHeader Whether the magic number and the format version are written in front of the data
	 * @param maxDepth The maximum nesting depth of the data which is read
	 * @param maxCollectionSize The maximum number of elements of a list, struct or map which is read
	 * @param maxStringLength The maximum length in bytes of a string which is read
	 * @param charset The charset to use for reading and writing strings
	 * @throws NullPointerException If the charset is null
	 * @throws IllegalArgumentException If the max depth, max collection size or max string length is not positive
	 */
	public BinaryConfig {
		Objects.requireNonNull(charset, "Charset must not be null");
		
		if (0 >= maxDepth) {
			throw new IllegalArgumentException("Max depth must be positive, but was " + maxDepth);
		}
		if (0 >= maxCollectionSize) {
			throw new IllegalArgumentException("Max collection size must be positive, but was " + maxCollectionSize);
		}
		if (0 >= maxStringLength) {
			throw new IllegalArgumentException("Max string length must be positive, but was " + maxStringLength);
		}
	}
}
