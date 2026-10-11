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
 * The key usages a certificate can restrict its key to, as defined by RFC 5280.<br>
 * <p>
 *     The constants are declared in the order of their bits, so {@link #bit()} is the index into {@link java.security.cert.X509Certificate#getKeyUsage()}.
 * </p>
 *
 * @author Luis-St
 */
public enum KeyUsage {
	
	/**
	 * The key verifies digital signatures other than those on certificates and revocation lists.<br>
	 * A TLS server or client key with an elliptic curve or Edwards curve needs this usage.<br>
	 */
	DIGITAL_SIGNATURE(0),
	/**
	 * The key verifies signatures that commit the signer to the signed content, also known as non-repudiation.<br>
	 */
	CONTENT_COMMITMENT(1),
	/**
	 * The key encrypts other keys for transport.<br>
	 */
	KEY_ENCIPHERMENT(2),
	/**
	 * The key encrypts user data directly.<br>
	 */
	DATA_ENCIPHERMENT(3),
	/**
	 * The key takes part in a key agreement.<br>
	 */
	KEY_AGREEMENT(4),
	/**
	 * The key verifies signatures on certificates, which every certificate authority needs.<br>
	 */
	KEY_CERT_SIGN(5),
	/**
	 * The key verifies signatures on certificate revocation lists.<br>
	 */
	CRL_SIGN(6),
	/**
	 * Together with {@link #KEY_AGREEMENT}, the key may only be used to encrypt during a key agreement.<br>
	 */
	ENCIPHER_ONLY(7),
	/**
	 * Together with {@link #KEY_AGREEMENT}, the key may only be used to decrypt during a key agreement.<br>
	 */
	DECIPHER_ONLY(8);
	
	/**
	 * The index of the bit of this usage in the encoded bit string.<br>
	 */
	private final int bit;
	
	/**
	 * Constructs a new key usage constant.<br>
	 * @param bit The index of the bit of this usage
	 */
	KeyUsage(int bit) {
		this.bit = bit;
	}
	
	/**
	 * Returns the index of the bit of this usage in the encoded bit string.<br>
	 * @return The bit index
	 */
	public int bit() {
		return this.bit;
	}
}
