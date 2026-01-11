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

package net.luis.utils.io.network.address.exception;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when an IP address cannot be parsed from a string representation.<br>
 * This exception provides detailed information about the parsing failure including the type of error encountered and the original input string.<br>
 * <p>
 *     The {@link IpParseErrorType} enum categorizes the specific parsing error, allowing
 *     callers to handle different error conditions appropriately. For example:
 * </p>
 * <pre>{@code
 * try {
 *     Ipv4Address address = IpAddresses.parseIpv4("256.0.0.1");
 * } catch (IpParseException e) {
 *     if (e.errorType() == ErrorType.INVALID_OCTET_VALUE) {
 *         System.out.println("Octet value out of range: " + e.input());
 *     }
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public class IpParseException extends IllegalArgumentException {
	
	/**
	 * The type of error that caused this exception.<br>
	 */
	private final @NonNull IpParseErrorType errorType;

	/**
	 * The original input string that failed to parse, or null if not available.<br>
	 */
	private final @Nullable String input;

	/**
	 * Constructs a new IP parse exception with no details.<br>
	 * The error type defaults to {@link IpParseErrorType#UNKNOWN} and the input is null.
	 */
	public IpParseException() {
		this.errorType = IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified message.<br>
	 * The error type defaults to {@link IpParseErrorType#UNKNOWN} and the input is null.<br>
	 *
	 * @param message The message of the exception
	 */
	public IpParseException(@Nullable String message) {
		super(message);
		this.errorType = IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified message and cause.<br>
	 * The error type defaults to {@link IpParseErrorType#UNKNOWN} and the input is null.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public IpParseException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
		this.errorType = IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified cause.<br>
	 * The error type defaults to {@link IpParseErrorType#UNKNOWN} and the input is null.<br>
	 *
	 * @param cause The cause of the exception
	 */
	public IpParseException(@Nullable Throwable cause) {
		super(cause);
		this.errorType = IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified error type.<br>
	 * The input is null and no message is set.<br>
	 * If the provided error type is null, it defaults to {@link IpParseErrorType#UNKNOWN}.<br>
	 *
	 * @param errorType The type of error that caused this exception
	 */
	public IpParseException(@Nullable IpParseErrorType errorType) {
		this.errorType = errorType != null ? errorType : IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified error type and input.<br>
	 * No message is set, if the provided error type is null, it defaults to {@link IpParseErrorType#UNKNOWN}.<br>
	 *
	 * @param errorType The type of error that caused this exception
	 * @param input The original input string that failed to parse
	 */
	public IpParseException(@Nullable IpParseErrorType errorType, @Nullable String input) {
		this.errorType = errorType != null ? errorType : IpParseErrorType.UNKNOWN;
		this.input = input;
	}

	/**
	 * Constructs a new IP parse exception with the specified message and error type.<br>
	 * The input is null, if the provided error type is null, it defaults to {@link IpParseErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param errorType The type of error that caused this exception
	 */
	public IpParseException(@Nullable String message, @Nullable IpParseErrorType errorType) {
		super(message);
		this.errorType = errorType != null ? errorType : IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified message, error type, and input.<br>
	 * If the provided error type is null, it defaults to {@link IpParseErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param errorType The type of error that caused this exception
	 * @param input The original input string that failed to parse
	 */
	public IpParseException(@Nullable String message, @Nullable IpParseErrorType errorType, @Nullable String input) {
		super(message);
		this.errorType = errorType != null ? errorType : IpParseErrorType.UNKNOWN;
		this.input = input;
	}

	/**
	 * Constructs a new IP parse exception with the specified message, cause, and error type.<br>
	 * The input is null, if the provided error type is null, it defaults to {@link IpParseErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 * @param errorType The type of error that caused this exception
	 */
	public IpParseException(@Nullable String message, @Nullable Throwable cause, @Nullable IpParseErrorType errorType) {
		super(message, cause);
		this.errorType = errorType != null ? errorType : IpParseErrorType.UNKNOWN;
		this.input = null;
	}

	/**
	 * Constructs a new IP parse exception with the specified message, cause, error type, and input.<br>
	 * If the provided error type is null, it defaults to {@link IpParseErrorType#UNKNOWN}.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 * @param errorType The type of error that caused this exception
	 * @param input The original input string that failed to parse
	 */
	public IpParseException(@Nullable String message, @Nullable Throwable cause, @Nullable IpParseErrorType errorType, @Nullable String input) {
		super(message, cause);
		this.errorType = errorType != null ? errorType : IpParseErrorType.UNKNOWN;
		this.input = input;
	}

	/**
	 * Returns the type of error that caused this exception.<br>
	 * @return The error type, never null
	 */
	public @NonNull IpParseErrorType errorType() {
		return this.errorType;
	}

	/**
	 * Returns the original input string that failed to parse.<br>
	 * @return The input string, or null if not available
	 */
	public @Nullable String input() {
		return this.input;
	}
}
