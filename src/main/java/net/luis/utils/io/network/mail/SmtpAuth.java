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

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * The authentication strategy an {@link SmtpClient} uses after connecting.<br>
 * This sealed interface is implemented by inner records, one per supported SASL mechanism.<br>
 * <ul>
 *     <li>{@link None} no authentication</li>
 *     <li>{@link Plain} SASL {@code PLAIN} (RFC 4616)</li>
 *     <li>{@link Login} the legacy {@code AUTH LOGIN} exchange</li>
 *     <li>{@link OAuth} {@code XOAUTH2} bearer-token authentication</li>
 * </ul>
 * <p>
 *     Secret material (passwords and tokens) is modelled as {@code char[]} rather than {@link String} so callers can zero it after use.<br>
 *     The records store and return the secret by reference without copying, and their {@code toString()} implementations redact the secret and never expose it.
 * </p>
 *
 * @see SmtpClient
 *
 * @author Luis-St
 */
@SuppressWarnings("InnerClassOfInterface")
public sealed interface SmtpAuth permits SmtpAuth.None, SmtpAuth.Plain, SmtpAuth.Login, SmtpAuth.OAuth {
	
	/**
	 * Returns the SMTP {@code AUTH} mechanism name of this strategy.<br>
	 * For {@link None} this is the empty string, otherwise {@code PLAIN}, {@code LOGIN}, or {@code XOAUTH2}.<br>
	 *
	 * @return The mechanism name
	 */
	@NonNull String mechanism();
	
	/**
	 * No authentication.<br>
	 * The client skips the {@code AUTH} exchange entirely.<br>
	 *
	 * @author Luis-St
	 */
	record None() implements SmtpAuth {
		
		@Override
		public @NonNull String mechanism() {
			return "";
		}
	}
	
	/**
	 * SASL {@code PLAIN} authentication (RFC 4616).<br>
	 * The credentials are sent as a single Base64 encoded token.<br>
	 *
	 * @author Luis-St
	 *
	 * @param username The user name
	 * @param password The password (stored by reference; redacted, never keyed on)
	 */
	record Plain(@NonNull String username, char @NonNull [] password) implements SmtpAuth {
		
		/**
		 * Constructs a new PLAIN authentication strategy.<br>
		 *
		 * @param username The user name
		 * @param password The password
		 * @throws NullPointerException If the username or password is null
		 * @throws IllegalArgumentException If the username or password is empty
		 */
		public Plain {
			Objects.requireNonNull(username, "Username must not be null");
			Objects.requireNonNull(password, "Password must not be null");
			
			if (username.isEmpty()) {
				throw new IllegalArgumentException("Username must not be empty");
			}
			if (password.length == 0) {
				throw new IllegalArgumentException("Password must not be empty");
			}
		}
		
		@Override
		public @NonNull String mechanism() {
			return "PLAIN";
		}
		
		@Override
		public @NonNull String toString() {
			return "Plain[username=" + this.username + ", password=***]";
		}
	}
	
	/**
	 * Legacy {@code AUTH LOGIN} authentication.<br>
	 * The user name and password are sent as separate Base64 encoded responses to server challenges.<br>
	 *
	 * @author Luis-St
	 *
	 * @param username The user name
	 * @param password The password (stored by reference; redacted, never keyed on)
	 */
	record Login(@NonNull String username, char @NonNull [] password) implements SmtpAuth {
		
		/**
		 * Constructs a new LOGIN authentication strategy.<br>
		 *
		 * @param username The user name
		 * @param password The password
		 * @throws NullPointerException If the username or password is null
		 * @throws IllegalArgumentException If the username or password is empty
		 */
		public Login {
			Objects.requireNonNull(username, "Username must not be null");
			Objects.requireNonNull(password, "Password must not be null");
			
			if (username.isEmpty()) {
				throw new IllegalArgumentException("Username must not be empty");
			}
			if (password.length == 0) {
				throw new IllegalArgumentException("Password must not be empty");
			}
		}
		
		@Override
		public @NonNull String mechanism() {
			return "LOGIN";
		}
		
		@Override
		public @NonNull String toString() {
			return "Login[username=" + this.username + ", password=***]";
		}
	}
	
	/**
	 * {@code XOAUTH2} bearer-token authentication (RFC 7628 style).<br>
	 * The user name and OAuth 2.0 access token are combined into a single Base64 encoded token.<br>
	 *
	 * @author Luis-St
	 *
	 * @param username The user name
	 * @param token The OAuth 2.0 bearer token (stored by reference; redacted, never keyed on)
	 */
	record OAuth(@NonNull String username, char @NonNull [] token) implements SmtpAuth {
		
		/**
		 * Constructs a new XOAUTH2 authentication strategy.<br>
		 *
		 * @param username The user name
		 * @param token The OAuth 2.0 bearer token
		 * @throws NullPointerException If the username or token is null
		 * @throws IllegalArgumentException If the username or token is empty
		 */
		public OAuth {
			Objects.requireNonNull(username, "Username must not be null");
			Objects.requireNonNull(token, "Token must not be null");
			
			if (username.isEmpty()) {
				throw new IllegalArgumentException("Username must not be empty");
			}
			if (token.length == 0) {
				throw new IllegalArgumentException("Token must not be empty");
			}
		}
		
		@Override
		public @NonNull String mechanism() {
			return "XOAUTH2";
		}
		
		@Override
		public @NonNull String toString() {
			return "OAuth[username=" + this.username + ", token=***]";
		}
	}
}
