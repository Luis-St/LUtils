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

package net.luis.utils.io.network.connection.ssl;

import javax.net.ssl.SSLServerSocket;

/**
 * Defines how an {@link SSLServer} handles client certificate authentication during the TLS handshake.<br>
 * This controls mutual TLS (mTLS) behavior, where the server may request or require a certificate from the client.<br>
 *
 * @see SSLServer
 * @see SSLServerConfig
 *
 * @author Luis-St
 */
public enum SSLClientAuth {
	
	/**
	 * The server does not request a client certificate.<br>
	 * This is the default and corresponds to standard one-way TLS where only the server is authenticated.<br>
	 */
	NONE,
	
	/**
	 * The server requests a client certificate but does not require one.<br>
	 * If the client does not present a certificate, the handshake still succeeds.<br>
	 * Maps to {@link SSLServerSocket#setWantClientAuth(boolean)}.<br>
	 */
	REQUESTED,
	
	/**
	 * The server requires a client certificate.<br>
	 * If the client does not present a valid certificate, the handshake fails.<br>
	 * Maps to {@link SSLServerSocket#setNeedClientAuth(boolean)}.<br>
	 */
	REQUIRED
}
