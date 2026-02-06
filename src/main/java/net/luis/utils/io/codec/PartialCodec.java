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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.util.Either;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Interface for codecs that support partial encoding and decoding.<br>
 * A partial codec allows for encoding and decoding operations where some elements may fail to encode or decode,<br>
 * but instead of throwing an exception for the entire operation, it discards the errors and returns the successfully encoded or decoded elements.<br>
 * <p>
 *     This is useful in scenarios where you want to process a collection of elements and want to continue processing even if some elements fail,<br>
 *     rather than stopping the entire operation due to an error.
 * </p>
 *
 * @author Luis-St
 *
 * @param <V> The type of the codec that implements this interface
 */
public interface PartialCodec<V> {
	
	/**
	 * Returns a codec that allows partial encoding and decoding,<br>
	 * where errors are discarded and the successfully encoded or decoded elements are returned instead of throwing an exception.<br>
	 *
	 * @return A codec that allows partial encoding and decoding
	 */
	@NonNull V partial();
	
	/**
	 * Returns a codec that does not allow partial encoding and decoding,<br>
	 * where any error during encoding or decoding will result in an exception being thrown and no elements being returned.<br>
	 *
	 * @return A codec that does not allow partial encoding and decoding
	 */
	@NonNull V strict();
	
	/**
	 * Returns whether this codec allows partial encoding and decoding.<br>
	 * @return True if this codec allows partial encoding and decoding, false otherwise
	 */
	boolean isPartial();
	
	/**
	 * Encodes a list of elements, where each element is either a successfully encoded value or an encoding error.<br>
	 * <p>
	 *     If this codec does not allow partial encoding, the first encoding error will cause an exception to be thrown and no elements will be returned.<br>
	 *     If this codec allows partial encoding, all successfully encoded values will be returned and any encoding errors will be discarded.
	 * </p>
	 *
	 * @param elements A list of elements to encode, where each element is either a successfully encoded value or an encoding error
	 * @param <R> The type of the successfully encoded values
	 * @return A list of successfully encoded values, with any encoding errors discarded if this codec allows partial encoding
	 * @throws NullPointerException If the elements list is null
	 * @throws EncoderException If this codec does not allow partial encoding and an encoding error is encountered
	 */
	default <R> @NonNull List<R> encode(@NonNull List<Either<R, EncoderException>> elements) throws EncoderException {
		Objects.requireNonNull(elements, "Elements list must not be null");
		
		List<R> result = new ArrayList<>(elements.size());
		for (Either<R, EncoderException> element : elements) {
			if (element.isLeft()) {
				result.add(element.leftOrThrow());
			} else if (!this.isPartial()) {
				throw element.rightOrThrow();
			}
		}
		
		return result;
	}
	
	/**
	 * Decodes a list of elements, where each element is either a successfully decoded value or a decoding error.<br>
	 * <p>
	 *     If this codec does not allow partial decoding, the first decoding error will cause an exception to be thrown and no elements will be returned.<br>
	 *     If this codec allows partial decoding, all successfully decoded values will be returned and any decoding errors will be discarded.
	 * </p>
	 *
	 * @param elements A list of elements to decode, where each element is either a successfully decoded value or a decoding error
	 * @param <R> The type of the successfully decoded values
	 * @return A list of successfully decoded values, with any decoding errors discarded if this codec allows partial decoding
	 * @throws NullPointerException If the elements list is null
	 * @throws DecoderException If this codec does not allow partial decoding and a decoding error is encountered
	 */
	default <R> @NonNull List<R> decode(@NonNull List<Either<R, DecoderException>> elements) throws DecoderException {
		Objects.requireNonNull(elements, "Elements list must not be null");
		
		List<R> result = new ArrayList<>(elements.size());
		for (Either<R, DecoderException> element : elements) {
			if (element.isLeft()) {
				result.add(element.leftOrThrow());
			} else if (!this.isPartial()) {
				throw element.rightOrThrow();
			}
		}
		
		return result;
	}
}
