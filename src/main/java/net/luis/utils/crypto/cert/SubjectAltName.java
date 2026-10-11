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

import net.luis.utils.io.network.address.IpAddress;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A subject alternative name, one of the identities a certificate is valid for.<br>
 * <p>
 *     Hostname verification reads these names and ignores the common name, so a server certificate needs at least one of them.<br>
 *     Only the four kinds that name an endpoint are modeled.<br>
 *     Directory names, other names and registered identifiers are skipped when the names of a certificate are read.
 * </p>
 * <p>
 *     Example:
 * </p>
 * <pre>{@code
 * List<SubjectAltName> names = List.of(
 *     new SubjectAltName.Dns("api.example.com"),
 *     new SubjectAltName.Ip(IpAddresses.parse("192.0.2.1"))
 * );
 * }</pre>
 *
 * @author Luis-St
 */
public sealed interface SubjectAltName permits SubjectAltName.Dns, SubjectAltName.Ip, SubjectAltName.Uri, SubjectAltName.Email {
	
	/**
	 * Requires that the given value can be written as an IA5 string.<br>
	 *
	 * @param value The value to check
	 * @param description The description of the value, used in the exception message
	 * @throws NullPointerException If the value or the description is null
	 * @throws IllegalArgumentException If the value is blank or not ascii
	 */
	private static void requireAscii(@NonNull String value, @NonNull String description) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(description, "Description must not be null");
		
		if (value.isBlank()) {
			throw new IllegalArgumentException(description + " must not be blank");
		}
		if (!StandardCharsets.US_ASCII.newEncoder().canEncode(value)) {
			throw new IllegalArgumentException(description + " must be ascii, was '" + value + "'");
		}
	}
	
	/**
	 * A domain name, which may start with a single wildcard label.<br>
	 *
	 * @author Luis-St
	 *
	 * @param name The domain name, for example {@code api.example.com} or {@code *.example.com}
	 */
	record Dns(@NonNull String name) implements SubjectAltName {
		
		/**
		 * Constructs a new domain name.<br>
		 * @throws NullPointerException If the name is null
		 * @throws IllegalArgumentException If the name is blank or not ascii
		 */
		public Dns {
			Objects.requireNonNull(name, "Name must not be null");
			requireAscii(name, "Domain name");
		}
	}
	
	/**
	 * An internet protocol address.<br>
	 *
	 * @author Luis-St
	 *
	 * @param address The address
	 */
	record Ip(@NonNull IpAddress<?> address) implements SubjectAltName {
		
		/**
		 * Constructs a new address name.<br>
		 * @throws NullPointerException If the address is null
		 */
		public Ip {
			Objects.requireNonNull(address, "Address must not be null");
		}
	}
	
	/**
	 * A uniform resource identifier, for example a SPIFFE identity.<br>
	 *
	 * @author Luis-St
	 *
	 * @param uri The uniform resource identifier
	 */
	record Uri(@NonNull URI uri) implements SubjectAltName {
		
		/**
		 * Constructs a new uniform resource identifier name.<br>
		 * @throws NullPointerException If the uri is null
		 * @throws IllegalArgumentException If the uri is not absolute
		 */
		public Uri {
			Objects.requireNonNull(uri, "Uri must not be null");
			
			if (!uri.isAbsolute()) {
				throw new IllegalArgumentException("Uri must be absolute, was '" + uri + "'");
			}
		}
	}
	
	/**
	 * An email address.<br>
	 *
	 * @author Luis-St
	 *
	 * @param address The email address
	 */
	record Email(@NonNull String address) implements SubjectAltName {
		
		/**
		 * Constructs a new email name.<br>
		 * @throws NullPointerException If the address is null
		 * @throws IllegalArgumentException If the address is blank, not ascii, or holds no at sign
		 */
		public Email {
			Objects.requireNonNull(address, "Address must not be null");
			requireAscii(address, "Email address");
			
			if (address.indexOf('@') <= 0) {
				throw new IllegalArgumentException("Email address must hold a local part and a domain, was '" + address + "'");
			}
		}
	}
}
