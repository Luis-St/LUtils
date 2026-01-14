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

package net.luis.utils.io.network.connection.exception;

/**
 * Enumeration of possible error types that can occur during network operations.<br>
 * Each error type represents a specific category of network failure.<br>
 * @author Luis-St
 */
public enum NetworkErrorType {
	
	/**
	 * Connection attempt failed for an unspecified reason.<br>
	 */
	CONNECTION_FAILED,
	
	/**
	 * Connection was refused by the remote host.<br>
	 * This typically occurs when no server is listening on the target port.<br>
	 */
	CONNECTION_REFUSED,
	
	/**
	 * Connection attempt timed out.<br>
	 * The remote host did not respond within the configured timeout period.<br>
	 */
	CONNECTION_TIMEOUT,
	
	/**
	 * Read operation timed out.<br>
	 * No data was received within the configured read timeout period.<br>
	 */
	READ_TIMEOUT,
	
	/**
	 * Write operation timed out.<br>
	 * Data could not be sent within the configured write timeout period.<br>
	 */
	WRITE_TIMEOUT,
	
	/**
	 * Connection was reset by the remote host.<br>
	 * The connection was forcibly closed by the remote peer.<br>
	 */
	CONNECTION_RESET,
	
	/**
	 * The target network is unreachable.<br>
	 * There is no route to the destination network.<br>
	 */
	NETWORK_UNREACHABLE,
	
	/**
	 * The target host is unreachable.<br>
	 * The host exists but cannot be reached.<br>
	 */
	HOST_UNREACHABLE,
	
	/**
	 * The address is already in use.<br>
	 * Another socket is already bound to the requested address and port.<br>
	 */
	ADDRESS_IN_USE,
	
	/**
	 * The socket has been closed.<br>
	 * An operation was attempted on a closed socket.<br>
	 */
	SOCKET_CLOSED,
	
	/**
	 * The client is not connected.<br>
	 * An operation requiring a connection was attempted before connecting.<br>
	 */
	NOT_CONNECTED,
	
	/**
	 * The client is already connected.<br>
	 * A connection was attempted on an already connected socket.<br>
	 */
	ALREADY_CONNECTED,
	
	/**
	 * A general I/O error occurred.<br>
	 * An unspecified I/O error occurred during the operation.<br>
	 */
	IO_ERROR,
	
	/**
	 * An unknown or unclassified error occurred.<br>
	 * This is used when the error does not fit any other category.<br>
	 */
	UNKNOWN
}
