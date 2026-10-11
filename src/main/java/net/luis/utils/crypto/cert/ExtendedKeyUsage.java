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

import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * The extended key usages a certificate can restrict its key to.<br>
 * <p>
 *     A certificate without the extension may be used for any purpose.<br>
 *     A certificate with it may only be used for the purposes it lists, which is why a server certificate should list {@link #SERVER_AUTH} and nothing else.
 * </p>
 *
 * @author Luis-St
 */
public enum ExtendedKeyUsage {
	
	/**
	 * TLS server authentication.<br>
	 */
	SERVER_AUTH("1.3.6.1.5.5.7.3.1"),
	/**
	 * TLS client authentication.<br>
	 */
	CLIENT_AUTH("1.3.6.1.5.5.7.3.2"),
	/**
	 * Signing of executable code.<br>
	 */
	CODE_SIGNING("1.3.6.1.5.5.7.3.3"),
	/**
	 * Email protection through S/MIME.<br>
	 */
	EMAIL_PROTECTION("1.3.6.1.5.5.7.3.4"),
	/**
	 * Binding the hash of an object to a time, as a timestamping authority does.<br>
	 */
	TIME_STAMPING("1.3.6.1.5.5.7.3.8"),
	/**
	 * Signing of OCSP responses on behalf of a certificate authority.<br>
	 */
	OCSP_SIGNING("1.3.6.1.5.5.7.3.9"),
	/**
	 * Authentication of an IKE peer in IPsec, as defined by RFC 4945.<br>
	 */
	IPSEC_IKE("1.3.6.1.5.5.7.3.17");
	
	/**
	 * The object identifier of this usage in dotted form.<br>
	 */
	private final String oid;
	
	/**
	 * Constructs a new extended key usage constant.<br>
	 * @param oid The object identifier of the usage
	 */
	ExtendedKeyUsage(@NonNull String oid) {
		this.oid = Objects.requireNonNull(oid, "Oid must not be null");
	}
	
	/**
	 * Looks up an extended key usage by its object identifier.<br>
	 *
	 * @param oid The object identifier in dotted form
	 * @return The usage with the given object identifier, or empty if there is none
	 * @throws NullPointerException If the object identifier is null
	 */
	public static @NonNull Optional<ExtendedKeyUsage> byOid(@NonNull String oid) {
		Objects.requireNonNull(oid, "Oid must not be null");
		return Arrays.stream(values()).filter(usage -> usage.oid.equals(oid)).findFirst();
	}
	
	/**
	 * Returns the object identifier of this usage in dotted form.<br>
	 * @return The object identifier
	 */
	public @NonNull String oid() {
		return this.oid;
	}
}
