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

package net.luis.utils.crypto;

import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * The length-prefixed encoding of a hybrid signature.<br>
 * <p>
 *     This is not the IETF LAMPS composite encoding.<br>
 *     It is fine for artifacts this library produces and consumes.<br>
 *     It is not interoperable with third parties expecting the standardised form, which is what the composite constants on {@link net.luis.utils.crypto.algorithm.SignatureAlgorithm} are for.
 * </p>
 *
 * @author Luis-St
 *
 * @param classical The classical component's signature
 * @param postQuantum The post-quantum component's signature
 */
record HybridSignature(
	byte @NonNull [] classical,
	byte @NonNull [] postQuantum
) {
	
	/**
	 * Constructs a new hybrid signature.<br>
	 * @throws NullPointerException If either component is null
	 */
	HybridSignature {
		Objects.requireNonNull(classical, "Classical signature must not be null");
		Objects.requireNonNull(postQuantum, "Post-quantum signature must not be null");
	}
	
	/**
	 * Parses a hybrid signature from its encoded form.<br>
	 * <p>
	 *     Each length is checked against what is actually left in the buffer before anything is allocated.<br>
	 *     A hostile length prefix would otherwise turn a few bytes into an allocation large enough to exhaust the heap,<br>
	 *     which no catch of a runtime exception would cover.
	 * </p>
	 *
	 * @param signature The encoded hybrid signature
	 * @return The parsed components
	 * @throws NullPointerException If the signature is null
	 * @throws MalformedDataException If the encoding is malformed or truncated
	 */
	static @NonNull HybridSignature parse(byte @NonNull [] signature) {
		Objects.requireNonNull(signature, "Signature must not be null");
		
		ByteBuffer buffer = ByteBuffer.wrap(signature);
		try {
			byte[] classical = new byte[bounded(buffer)];
			buffer.get(classical);
			byte[] postQuantum = new byte[bounded(buffer)];
			buffer.get(postQuantum);
			return new HybridSignature(classical, postQuantum);
		} catch (RuntimeException e) {
			throw new MalformedDataException("Malformed hybrid signature", e);
		}
	}
	
	/**
	 * Encodes the given components into their hybrid form.<br>
	 *
	 * @param classical The classical component's signature
	 * @param postQuantum The post-quantum component's signature
	 * @return The encoded hybrid signature
	 * @throws NullPointerException If either component is null
	 */
	static byte @NonNull [] encode(byte @NonNull [] classical, byte @NonNull [] postQuantum) {
		Objects.requireNonNull(classical, "Classical signature must not be null");
		Objects.requireNonNull(postQuantum, "Post-quantum signature must not be null");
		
		return CryptoBytes.concat(CryptoBytes.of(classical.length), classical, CryptoBytes.of(postQuantum.length), postQuantum);
	}
	
	/**
	 * Reads a length prefix and checks it against the bytes remaining in the buffer.<br>
	 *
	 * @param buffer The buffer to read from
	 * @return The read length
	 * @throws NullPointerException If the buffer is null
	 * @throws BufferUnderflowException If the buffer holds no length prefix
	 * @throws MalformedDataException If the length is negative or longer than the remaining bytes
	 */
	private static int bounded(@NonNull ByteBuffer buffer) {
		Objects.requireNonNull(buffer, "Buffer must not be null");
		
		int length = buffer.getInt();
		if (length < 0 || length > buffer.remaining()) {
			throw new MalformedDataException("Hybrid signature component length " + length + " does not fit into the remaining " + buffer.remaining() + " bytes");
		}
		return length;
	}
}
