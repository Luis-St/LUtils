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

/**
 * The transport security mode used by an {@link SmtpClient} when connecting to a mail server.<br>
 * Each mode corresponds to a standard SMTP submission port and a different TLS behavior.<br>
 * <ul>
 *     <li>{@link #PLAINTEXT} no encryption, default port 25</li>
 *     <li>{@link #IMPLICIT_TLS} TLS negotiated immediately (SMTPS), default port 465</li>
 *     <li>{@link #STARTTLS} plain connection upgraded to TLS via {@code STARTTLS}, default port 587</li>
 * </ul>
 *
 * @see SmtpClient
 *
 * @author Luis-St
 */
public enum SmtpSecurity {
	
	/**
	 * An unencrypted plaintext connection.<br>
	 * No TLS is used at all; the standard port is 25.<br>
	 */
	PLAINTEXT(25),
	
	/**
	 * An implicit TLS (SMTPS) connection.<br>
	 * The TLS handshake is performed immediately after the socket connects, before any SMTP command, the standard port is 465.<br>
	 */
	IMPLICIT_TLS(465),
	
	/**
	 * An opportunistic TLS connection using {@code STARTTLS}.<br>
	 * The connection starts in plaintext, and after the initial {@code EHLO} the client issues<br>
	 * {@code STARTTLS} to upgrade the existing socket to TLS, the standard port is 587.<br>
	 */
	STARTTLS(587);
	
	/**
	 * The standard SMTP port associated with this security mode.<br>
	 */
	private final int defaultPort;
	
	/**
	 * Constructs a new security mode with the given default port.<br>
	 * @param defaultPort The standard SMTP port for this mode
	 */
	SmtpSecurity(int defaultPort) {
		this.defaultPort = defaultPort;
	}
	
	/**
	 * Returns the standard SMTP port associated with this security mode.<br>
	 * @return The default port
	 */
	public int defaultPort() {
		return this.defaultPort;
	}
}
