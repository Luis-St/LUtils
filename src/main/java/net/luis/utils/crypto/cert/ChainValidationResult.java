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

import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * The outcome of validating a certificate chain with {@link CertificateChains#validate}.<br>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * switch (CertificateChains.validate(chain, trust, Instant.now(), new RevocationPolicy.None())) {
 *     case ChainValidationResult.Valid valid -> accept(chain.getFirst());
 *     case ChainValidationResult.Invalid invalid -> reject(invalid.reason());
 * }
 * }</pre>
 *
 * @author Luis-St
 */
public sealed interface ChainValidationResult permits ChainValidationResult.Valid, ChainValidationResult.Invalid {
	
	/**
	 * Returns whether the chain is valid.<br>
	 * @return True if this is a {@link Valid} result
	 */
	default boolean isValid() {
		return this instanceof Valid;
	}
	
	/**
	 * The chain is valid.<br>
	 *
	 * @author Luis-St
	 *
	 * @param anchor The trust anchor the chain ends in
	 */
	record Valid(@NonNull X509Certificate anchor) implements ChainValidationResult {
		
		/**
		 * Constructs a new valid result.<br>
		 * @throws NullPointerException If the anchor is null
		 */
		public Valid {
			Objects.requireNonNull(anchor, "Anchor must not be null");
		}
	}
	
	/**
	 * The chain is not valid.<br>
	 *
	 * @author Luis-St
	 *
	 * @param reason Why the chain is not valid, meant for a log rather than for a user
	 * @param index The index of the certificate in the chain that failed, or -1 if the failure concerns no single certificate
	 */
	record Invalid(@NonNull String reason, int index) implements ChainValidationResult {
		
		/**
		 * Constructs a new invalid result.<br>
		 * @throws NullPointerException If the reason is null
		 * @throws IllegalArgumentException If the index is less than -1
		 */
		public Invalid {
			Objects.requireNonNull(reason, "Reason must not be null");
			
			if (index < -1) {
				throw new IllegalArgumentException("Index must be -1 or a chain index, was " + index);
			}
		}
	}
}
