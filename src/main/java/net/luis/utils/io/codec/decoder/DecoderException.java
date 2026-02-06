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

package net.luis.utils.io.codec.decoder;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when an error occurs during decoding an object.<br>
 *
 * @see Decoder
 *
 * @author Luis-St
 */
public class DecoderException extends Exception {
	
	/**
	 * The decoder that caused the exception.<br>
	 */
	private final Decoder<?> decoder;
	
	/**
	 * Constructs a new decoder exception with the specified decoder.<br>
	 * @param decoder The decoder that caused the exception
	 */
	public DecoderException(@Nullable Decoder<?> decoder) {
		this.decoder = decoder;
	}
	
	/**
	 * Constructs a new decoder exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public DecoderException(@Nullable String message) {
		this(message, (Decoder<?>) null);
	}
	
	/**
	 * Constructs a new decoder exception with the specified message and decoder.<br>
	 *
	 * @param message The message of the exception
	 * @param decoder The decoder that caused the exception
	 */
	public DecoderException(@Nullable String message, @Nullable Decoder<?> decoder) {
		super(message);
		this.decoder = decoder;
	}
	
	/**
	 * Constructs a new decoder exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public DecoderException(@Nullable String message, @Nullable Throwable cause) {
		this(message, null, cause);
	}
	
	/**
	 * Constructs a new decoder exception with the specified message, decoder and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param decoder The decoder that caused the exception
	 * @param cause The cause of the exception
	 */
	public DecoderException(@Nullable String message, @Nullable Decoder<?> decoder, @Nullable Throwable cause) {
		super(message, cause);
		this.decoder = decoder;
	}
	
	/**
	 * Constructs a new decoder exception with the specified cause.<br>
	 *
	 * @param decoder The decoder that caused the exception
	 * @param cause The cause of the exception
	 */
	public DecoderException(@Nullable Decoder<?> decoder, @Nullable Throwable cause) {
		super(cause);
		this.decoder = decoder;
	}
	
	/**
	 * Gets the decoder that caused the exception.<br>
	 * @return The decoder, or null if no decoder is associated with this exception
	 */
	public @Nullable Decoder<?> getDecoder() {
		return this.decoder;
	}
}
