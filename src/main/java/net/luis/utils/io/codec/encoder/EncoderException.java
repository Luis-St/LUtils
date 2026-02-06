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

package net.luis.utils.io.codec.encoder;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when an error occurs during encoding an object.<br>
 *
 * @see Encoder
 *
 * @author Luis-St
 */
public class EncoderException extends Exception {
	
	/**
	 * The encoder that caused the exception.<br>
	 */
	private final Encoder<?> encoder;
	
	/**
	 * Constructs a new encoder exception with the specified encoder.<br>
	 * @param encoder The encoder that caused the exception
	 */
	public EncoderException(@Nullable Encoder<?> encoder) {
		this.encoder = encoder;
	}
	
	/**
	 * Constructs a new encoder exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public EncoderException(@Nullable String message) {
		this(message, (Encoder<?>) null);
	}
	
	/**
	 * Constructs a new encoder exception with the specified message and encoder.<br>
	 *
	 * @param message The message of the exception
	 * @param encoder The encoder that caused the exception
	 */
	public EncoderException(@Nullable String message, @Nullable Encoder<?> encoder) {
		super(message);
		this.encoder = encoder;
	}
	
	/**
	 * Constructs a new encoder exception with the specified message and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public EncoderException(@Nullable String message, @Nullable Throwable cause) {
		this(message, null, cause);
	}
	
	/**
	 * Constructs a new encoder exception with the specified message, encoder and cause.<br>
	 *
	 * @param message The message of the exception
	 * @param encoder The encoder that caused the exception
	 * @param cause The cause of the exception
	 */
	public EncoderException(@Nullable String message, @Nullable Encoder<?> encoder, @Nullable Throwable cause) {
		super(message, cause);
		this.encoder = encoder;
	}
	
	/**
	 * Constructs a new encoder exception with the specified encoder and cause.<br>
	 *
	 * @param encoder The encoder that caused the exception
	 * @param cause The cause of the exception
	 */
	public EncoderException(@Nullable Encoder<?> encoder, @Nullable Throwable cause) {
		super(cause);
		this.encoder = encoder;
	}
	
	/**
	 * Gets the encoder that caused the exception.<br>
	 * @return The encoder, or null if no encoder is associated with the exception
	 */
	public @Nullable Encoder<?> getCodec() {
		return this.encoder;
	}
}
