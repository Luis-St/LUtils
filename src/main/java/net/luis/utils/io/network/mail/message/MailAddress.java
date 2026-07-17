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

package net.luis.utils.io.network.mail.message;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents an email address (RFC 5321 addr-spec) consisting of a local part and a domain.<br>
 * The address is stored as its two components and rendered as {@code localPart@domain}.<br>
 * <p>
 *     Validation is pragmatic rather than a full RFC 5321 parser:<br>
 *     both parts must be non-empty and must not contain whitespace, control characters, or a second {@code @} sign,<br>
 *     which is enough to prevent SMTP command and header injection.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * MailAddress address = MailAddress.of("john.doe", "example.com");
 * MailAddress parsed = MailAddress.parse("john.doe@example.com");
 * System.out.println(address); // john.doe@example.com
 * }</pre>
 *
 * @author Luis-St
 *
 * @param localPart The local part of the address (before the {@code @})
 * @param domain The domain part of the address (after the {@code @})
 */
public record MailAddress(@NonNull String localPart, @NonNull String domain) {
	
	/**
	 * Constructs a new mail address from the given local part and domain.<br>
	 *
	 * @param localPart The local part of the address
	 * @param domain The domain part of the address
	 * @throws NullPointerException If the local part or domain is null
	 * @throws IllegalArgumentException If the local part or domain is empty or contains an illegal character
	 */
	public MailAddress {
		Objects.requireNonNull(localPart, "Local part must not be null");
		Objects.requireNonNull(domain, "Domain must not be null");
		validatePart(localPart, "Local part");
		validatePart(domain, "Domain");
	}
	
	/**
	 * Creates a new mail address from the given local part and domain.<br>
	 *
	 * @param localPart The local part of the address
	 * @param domain The domain part of the address
	 * @return A new mail address
	 * @throws NullPointerException If the local part or domain is null
	 * @throws IllegalArgumentException If the local part or domain is empty or contains an illegal character
	 */
	public static @NonNull MailAddress of(@NonNull String localPart, @NonNull String domain) {
		return new MailAddress(localPart, domain);
	}
	
	/**
	 * Parses a mail address from its {@code localPart@domain} string representation.<br>
	 *
	 * @param address The address to parse
	 * @return The parsed mail address
	 * @throws NullPointerException If the address is null
	 * @throws IllegalArgumentException If the address does not contain exactly one {@code @}, or a part is invalid
	 */
	public static @NonNull MailAddress parse(@NonNull String address) {
		Objects.requireNonNull(address, "Address must not be null");
		
		int at = address.indexOf('@');
		if (at < 0 || at != address.lastIndexOf('@')) {
			throw new IllegalArgumentException("Address must contain exactly one '@': " + address);
		}
		return new MailAddress(address.substring(0, at), address.substring(at + 1));
	}
	
	/**
	 * Validates that the given address part is non-empty and free of illegal characters.<br>
	 * Illegal characters are whitespace, control characters, {@code 0x7F}, and the {@code @} sign.<br>
	 *
	 * @param part The address part to validate
	 * @param name The human-readable name of the part for error messages
	 * @throws IllegalArgumentException If the part is empty or contains an illegal character
	 */
	private static void validatePart(@NonNull String part, @NonNull String name) {
		if (part.isEmpty()) {
			throw new IllegalArgumentException(name + " must not be empty");
		}
		
		for (int i = 0; i < part.length(); i++) {
			char c = part.charAt(i);
			if (c <= ' ' || c == 0x7F || c == '@') {
				throw new IllegalArgumentException(name + " contains an illegal character: " + part);
			}
		}
	}
	
	//region Object overrides
	
	/**
	 * Returns the {@code localPart@domain} string representation of this address.<br>
	 * @return The address as a string
	 */
	@Override
	public @NonNull String toString() {
		return this.localPart + "@" + this.domain;
	}
	//endregion
}
