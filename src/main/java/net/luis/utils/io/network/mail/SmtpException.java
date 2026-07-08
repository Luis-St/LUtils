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

package net.luis.utils.io.network.mail;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Thrown when an SMTP conversation fails because the server returned an unexpected reply.<br>
 * This exception extends {@link NetworkConnectionException} and additionally carries the offending {@link SmtpReply},<br>
 * so callers can inspect the reply code and message that caused the failure.<br>
 * <p>
 *     It is used both for unexpected reply codes during the protocol flow and for authentication
 *     failures. The default error type is {@link NetworkErrorType#PROTOCOL_ERROR}.
 * </p>
 * <pre>{@code
 * try {
 *     client.send(message);
 * } catch (SmtpException e) {
 *     System.out.println("Server rejected the message with code " + e.replyCode());
 * }
 * }</pre>
 *
 * @see NetworkConnectionException
 * @see SmtpReply
 *
 * @author Luis-St
 */
public class SmtpException extends NetworkConnectionException {
	
	/**
	 * The server reply that caused this exception.<br>
	 */
	private final @NonNull SmtpReply reply;
	
	/**
	 * Constructs a new SMTP exception with the specified message and reply.<br>
	 * The error type defaults to {@link NetworkErrorType#PROTOCOL_ERROR}.<br>
	 *
	 * @param message The message of the exception
	 * @param reply The server reply that caused this exception
	 * @throws NullPointerException If the reply is null
	 */
	public SmtpException(@Nullable String message, @NonNull SmtpReply reply) {
		super(message, NetworkErrorType.PROTOCOL_ERROR);
		this.reply = Objects.requireNonNull(reply, "Reply must not be null");
	}
	
	/**
	 * Constructs a new SMTP exception with the specified message, reply, and endpoint.<br>
	 * The error type defaults to {@link NetworkErrorType#PROTOCOL_ERROR}.<br>
	 *
	 * @param message The message of the exception
	 * @param reply The server reply that caused this exception
	 * @param endpoint The endpoint involved in the failed operation
	 * @throws NullPointerException If the reply is null
	 */
	public SmtpException(@Nullable String message, @NonNull SmtpReply reply, @Nullable IpEndpoint endpoint) {
		super(message, NetworkErrorType.PROTOCOL_ERROR, endpoint);
		this.reply = Objects.requireNonNull(reply, "Reply must not be null");
	}
	
	/**
	 * Constructs a new SMTP exception with the specified message, reply, error type, and endpoint.<br>
	 *
	 * @param message The message of the exception
	 * @param reply The server reply that caused this exception
	 * @param errorType The type of error that caused this exception
	 * @param endpoint The endpoint involved in the failed operation
	 * @throws NullPointerException If the reply is null
	 */
	public SmtpException(@Nullable String message, @NonNull SmtpReply reply, @Nullable NetworkErrorType errorType, @Nullable IpEndpoint endpoint) {
		super(message, errorType, endpoint);
		this.reply = Objects.requireNonNull(reply, "Reply must not be null");
	}
	
	/**
	 * Constructs a new SMTP exception with all details.<br>
	 *
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 * @param reply The server reply that caused this exception
	 * @param errorType The type of error that caused this exception
	 * @param endpoint The endpoint involved in the failed operation
	 * @throws NullPointerException If the reply is null
	 */
	public SmtpException(@Nullable String message, @Nullable Throwable cause, @NonNull SmtpReply reply, @Nullable NetworkErrorType errorType, @Nullable IpEndpoint endpoint) {
		super(message, cause, errorType, endpoint);
		this.reply = Objects.requireNonNull(reply, "Reply must not be null");
	}
	
	/**
	 * Returns the reply code of the server reply that caused this exception.<br>
	 * @return The SMTP reply code
	 */
	public int replyCode() {
		return this.reply.code();
	}
	
	/**
	 * Returns the server reply that caused this exception.<br>
	 * @return The server reply, never null
	 */
	public @NonNull SmtpReply reply() {
		return this.reply;
	}
}
