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

package net.luis.utils.io.network.file;

/**
 *
 * @author Luis-St
 *
 */

public enum FtpAuthenticationMethod {
	
	/**
	 * Default authentication method, which uses the standard FTP protocol without any encryption.<br>
	 * Either no authentication is required or the server requires a username and password to be provided in plain text.<br>
	 */
	DEFAULT,
	/**
	 * Deprecated authentication method, which uses the SSL protocol to encrypt the connection.<br>
	 * This methods gets automatically upgraded to TLS.<br>
	 */
	SSL,
	/**
	 * Authentication method, which uses the TLS protocol to encrypt the connection.<br>
	 * This method is the recommended one, as it provides a secure connection and is widely supported by FTP servers.<br>
	 */
	TLS;
	
	private FtpAuthenticationMethod() {}
}
