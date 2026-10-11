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

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.security.cert.X509CRL;
import java.util.*;

/**
 * How {@link CertificateChains#validate} checks whether the certificates of a chain are revoked.<br>
 * <p>
 *     Every certificate below the trust anchor is checked, the leaf and every intermediate.<br>
 *     A certificate whose status cannot be determined fails validation unless the policy explicitly allows a soft failure.
 * </p>
 *
 * @author Luis-St
 */
public sealed interface RevocationPolicy permits RevocationPolicy.None, RevocationPolicy.Crl, RevocationPolicy.Ocsp {
	
	/**
	 * No revocation checking.<br>
	 * Appropriate for a private infrastructure with short-lived certificates, where expiry takes the place of revocation.<br>
	 *
	 * @author Luis-St
	 */
	record None() implements RevocationPolicy {}
	
	/**
	 * Revocation checking against the given revocation lists only.<br>
	 * Nothing is fetched from the network, so a certificate whose issuer has no list among these fails validation.<br>
	 *
	 * @author Luis-St
	 *
	 * @param crls The revocation lists to check against
	 */
	record Crl(@NonNull @Unmodifiable List<X509CRL> crls) implements RevocationPolicy {
		
		/**
		 * Constructs a new revocation list policy.<br>
		 * @throws NullPointerException If the revocation lists are null or hold null
		 */
		public Crl {
			Objects.requireNonNull(crls, "Revocation lists must not be null");
			crls = List.copyOf(crls);
		}
		
		/**
		 * Constructs a new revocation list policy from the given revocation lists.<br>
		 *
		 * @param crls The revocation lists to check against
		 * @throws NullPointerException If the revocation lists are null or hold null
		 */
		public Crl(@NonNull Collection<X509CRL> crls) {
			this(List.copyOf(Objects.requireNonNull(crls, "Revocation lists must not be null")));
		}
	}
	
	/**
	 * Revocation checking through the online certificate status protocol.<br>
	 * <p>
	 *     The responder is asked over the network during validation.<br>
	 *     A soft failure accepts a certificate whose responder cannot be reached, which keeps a service up during a responder outage at the price of letting a revoked certificate through while it lasts.
	 * </p>
	 *
	 * @author Luis-St
	 *
	 * @param responder The responder to ask, or null to ask the responder each certificate names in its authority information access extension
	 * @param softFail Whether a certificate whose status cannot be determined is accepted
	 */
	record Ocsp(@Nullable URI responder, boolean softFail) implements RevocationPolicy {}
}
