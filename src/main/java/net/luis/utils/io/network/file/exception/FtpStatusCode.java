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

package net.luis.utils.io.network.file.exception;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.luis.utils.io.network.file.exception.FtpStatusCodeClass.*;

/**
 *
 * @author Luis-St
 *
 */

public enum FtpStatusCode {
	
	RESTART_MARKER_REPLY(_1XX, 1, 0, "Restart marker reply."),
	SERVICE_READY_SOON(_1XX, 2, 0, "Service ready in {Time} minutes."),
	DATA_CONNECTION_ALREADY_OPEN(_1XX, 2, 5, "Data connection already open; transfer starting."),
	FILE_STATUS_OKAY(_1XX, 5, 0, "File status okay; about to open data connection."),
	
	COMMAND_OKAY(_2XX, 0, 0, "Command okay."),
	COMMAND_NOT_IMPLEMENTED_SUPERFLUOUS(_2XX, 0, 2, "Command not implemented, superfluous at this site."),
	SYSTEM_STATUS(_2XX, 1, 1, "System status, or system help reply."),
	DIRECTORY_STATUS(_2XX, 1, 2, "Directory status."),
	FILE_STATUS(_2XX, 1, 3, "File status."),
	HELP_MESSAGE(_2XX, 1, 4, "Help message: {Message}"),
	SYSTEM_TYPE(_2XX, 1, 5, "{SystemName} system type."),
	SERVICE_READY(_2XX, 2, 0, "Service ready for new user."),
	SERVICE_CLOSING_CONTROL_CONNECTION(_2XX, 2, 1, "Service closing control connection."),
	DATA_CONNECTION_OPEN(_2XX, 2, 5, "Data connection open; no transfer in progress."),
	CLOSING_DATA_CONNECTION(_2XX, 2, 6, "Closing data connection; requested file action successful."),
	ENTERING_PASSIVE_MODE(_2XX, 2, 7, "Entering Passive Mode ({Host},{Port})."),
	ENTERING_LONG_PASSIVE_MODE(_2XX, 2, 8, "Entering Long Passive Mode ({Address})."),
	ENTERING_EXTENDED_PASSIVE_MODE(_2XX, 2, 9, "Entering Extended Passive Mode (|||{Port}|)."),
	USER_LOGGED_IN(_2XX, 3, 0, "User logged in, proceed."),
	USER_LOGGED_OUT(_2XX, 3, 1, "User logged out; service terminated."),
	LOGOUT_NOTED(_2XX, 3, 2, "Logout command noted, will complete when transfer is done."),
	SECURITY_DATA_EXCHANGE_COMPLETE(_2XX, 3, 4, "Security data exchange complete."),
	SECURITY_DATA_EXCHANGE_SUCCESSFUL(_2XX, 3, 5, "Security data exchange successful; ADAT={Data}"),
	FILE_ACTION_COMPLETED(_2XX, 5, 0, "Requested file action okay, completed."),
	PATHNAME_CREATED(_2XX, 5, 7, "\"{Pathname}\" created."),
	
	USER_NAME_OKAY(_3XX, 3, 1, "User name okay, need password."),
	NEED_ACCOUNT_FOR_LOGIN(_3XX, 3, 2, "Need account for login."),
	SECURITY_MECHANISM_ACCEPTED(_3XX, 3, 4, "Security mechanism accepted; ADAT={Data}"),
	SECURITY_DATA_ACCEPTABLE(_3XX, 3, 5, "Security data acceptable; ADAT={Data}"),
	USERNAME_OKAY_NEED_PASSWORD_CHALLENGE(_3XX, 3, 6, "Username okay, need password. Challenge is \"{Challenge}\"."),
	FILE_ACTION_PENDING_INFORMATION(_3XX, 5, 0, "Requested file action pending further information."),
	
	SERVICE_NOT_AVAILABLE(_4XX, 2, 1, "Service not available, closing control connection."),
	CANNOT_OPEN_DATA_CONNECTION(_4XX, 2, 5, "Can't open data connection."),
	CONNECTION_CLOSED_TRANSFER_ABORTED(_4XX, 2, 6, "Connection closed; transfer aborted."),
	INVALID_USERNAME_OR_PASSWORD(_4XX, 3, 0, "Invalid username or password."),
	RESOURCE_UNAVAILABLE_FOR_SECURITY(_4XX, 3, 1, "Need some unavailable resource to process security."),
	REQUESTED_HOST_UNAVAILABLE(_4XX, 3, 4, "Requested host unavailable."),
	FILE_ACTION_NOT_TAKEN(_4XX, 5, 0, "Requested file action not taken; file unavailable."),
	ACTION_ABORTED_LOCAL_ERROR(_4XX, 5, 1, "Requested action aborted: local error in processing."),
	INSUFFICIENT_STORAGE_SPACE(_4XX, 5, 2, "Requested action not taken; insufficient storage space in system."),
	
	SYNTAX_ERROR(_5XX, 0, 0, "Syntax error, command unrecognized."),
	SYNTAX_ERROR_IN_PARAMETERS(_5XX, 0, 1, "Syntax error in parameters or arguments."),
	COMMAND_NOT_IMPLEMENTED(_5XX, 0, 2, "Command not implemented."),
	BAD_SEQUENCE_OF_COMMANDS(_5XX, 0, 3, "Bad sequence of commands."),
	COMMAND_NOT_IMPLEMENTED_FOR_PARAMETER(_5XX, 0, 4, "Command not implemented for that parameter."),
	NOT_LOGGED_IN(_5XX, 3, 0, "Not logged in."),
	NEED_ACCOUNT_FOR_STORING_FILES(_5XX, 3, 2, "Need account for storing files."),
	COMMAND_PROTECTION_LEVEL_DENIED(_5XX, 3, 3, "Command protection level denied for policy reasons."),
	REQUEST_DENIED(_5XX, 3, 4, "Request denied for policy reasons."),
	FAILED_SECURITY_CHECK(_5XX, 3, 5, "Failed security check."),
	PROTECTION_LEVEL_NOT_SUPPORTED(_5XX, 3, 6, "Requested PROT level not supported by mechanism."),
	COMMAND_PROTECTION_LEVEL_NOT_SUPPORTED(_5XX, 3, 7, "Command protection level not supported by security mechanism."),
	FILE_UNAVAILABLE(_5XX, 5, 0, "Requested action not taken; file unavailable."),
	PAGE_TYPE_UNKNOWN(_5XX, 5, 1, "Requested action aborted: page type unknown."),
	EXCEEDED_STORAGE_ALLOCATION(_5XX, 5, 2, "Requested file action aborted; exceeded storage allocation."),
	FILE_NAME_NOT_ALLOWED(_5XX, 5, 3, "Requested action not taken; file name not allowed."),
	
	INTEGRITY_PROTECTED_REPLY(_6XX, 3, 1, "Integrity protected reply."),
	CONFIDENTIALITY_AND_INTEGRITY_PROTECTED_REPLY(_6XX, 3, 2, "Confidentiality and integrity protected reply."),
	CONFIDENTIALITY_PROTECTED_REPLY(_6XX, 3, 3, "Confidentiality protected reply.");
	
	private final FtpStatusCodeClass codeClass;
	private final int errorKind;
	private final int detailCode;
	private final String messagePattern;
	
	private FtpStatusCode(@NonNull FtpStatusCodeClass codeClass, int errorKind, int detailCode, @NonNull String messagePattern) {
		this.codeClass = Objects.requireNonNull(codeClass, "Code class must not be null");
		this.errorKind = errorKind;
		this.detailCode = detailCode;
		this.messagePattern = Objects.requireNonNull(messagePattern, "Message pattern must not be null");
	}
	
	public @NonNull FtpStatusCodeClass getCodeClass() {
		return this.codeClass;
	}
	
	public int getCode() {
		return this.codeClass.getCode() * 100 + this.errorKind * 10 + this.detailCode;
	}
	
	public boolean is(@Nullable FtpStatusCodeClass codeClass) {
		return this.codeClass == codeClass;
	}
	
	public boolean is1xx() {
		return this.is(_1XX);
	}
	
	public boolean is2xx() {
		return this.is(_2XX);
	}
	
	public boolean is3xx() {
		return this.is(_3XX);
	}
	
	public boolean is4xx() {
		return this.is(_4XX);
	}
	
	public boolean is5xx() {
		return this.is(_5XX);
	}
	
	public boolean is6xx() {
		return this.is(_6XX);
	}
	
	public @NonNull String getMessagePattern() {
		return this.messagePattern;
	}
	
	public @NonNull @Unmodifiable List<String> getMessageParameters() {
		return List.of(this.messagePattern.split("\\{[^}]*}"));
	}
	
	public @NonNull String formatMessage(@NonNull Object @NonNull ... parameters) {
		Objects.requireNonNull(parameters, "Parameters must not be null");
		
		String message = this.messagePattern;
		for (Object parameter : parameters) {
			Objects.requireNonNull(parameter, "Parameter must not be null");
			
			message = message.replaceFirst("\\{[^}]*}", Objects.toString(parameter));
		}
		return message;
	}
}
