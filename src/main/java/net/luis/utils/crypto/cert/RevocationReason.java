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

package net.luis.utils.crypto.cert;

/**
 * The reasons a certificate can be revoked for, as defined by RFC 5280.<br>
 * <p>
 *     Code 7 is unused by the standard and has no constant.<br>
 *     {@link #UNSPECIFIED} is never written into a revocation list.<br>
 *     RFC 5280 recommends leaving the reason out instead.
 * </p>
 *
 * @author Luis-St
 */
public enum RevocationReason {
	
	/**
	 * No reason is given.<br>
	 */
	UNSPECIFIED(0),
	/**
	 * The private key of the certificate is known or suspected to be compromised.<br>
	 */
	KEY_COMPROMISE(1),
	/**
	 * The private key of the issuing authority is known or suspected to be compromised.<br>
	 */
	CA_COMPROMISE(2),
	/**
	 * The subject's name or other information in the certificate has changed.<br>
	 */
	AFFILIATION_CHANGED(3),
	/**
	 * The certificate has been replaced by a new one.<br>
	 */
	SUPERSEDED(4),
	/**
	 * The certificate is no longer needed for the purpose it was issued for.<br>
	 */
	CESSATION_OF_OPERATION(5),
	/**
	 * The certificate is temporarily suspended.<br>
	 */
	CERTIFICATE_HOLD(6),
	/**
	 * A previously held certificate is released again, only meaningful in a delta revocation list.<br>
	 */
	REMOVE_FROM_CRL(8),
	/**
	 * A privilege granted by the certificate has been withdrawn.<br>
	 */
	PRIVILEGE_WITHDRAWN(9),
	/**
	 * The private key of an attribute authority is known or suspected to be compromised.<br>
	 */
	AA_COMPROMISE(10);
	
	/**
	 * The reason code of this reason as written into a revocation list.<br>
	 */
	private final int code;
	
	/**
	 * Constructs a new revocation reason constant.<br>
	 * @param code The reason code
	 */
	RevocationReason(int code) {
		this.code = code;
	}
	
	/**
	 * Returns the reason code of this reason as written into a revocation list.<br>
	 * @return The reason code
	 */
	public int code() {
		return this.code;
	}
}
