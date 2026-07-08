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
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents an RFC 5322 mailbox: an optional display name paired with a {@link MailAddress}.<br>
 * When rendered, the mailbox produces either a bare addr-spec ({@code local@domain}) or a name-addr ({@code Display Name <local@domain>})<br>
 * with the display name RFC 2047 encoded if it contains non-ASCII characters.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * Mailbox mailbox = Mailbox.of("John Doe", MailAddress.parse("john@example.com"));
 * Mailbox parsed = Mailbox.parse("John Doe <john@example.com>");
 * System.out.println(mailbox); // John Doe <john@example.com>
 * }</pre>
 *
 * @see MailAddress
 *
 * @author Luis-St
 *
 * @param displayName The optional display name, or null for a bare address
 * @param address The mail address of this mailbox
 */
public record Mailbox(@Nullable String displayName, @NonNull MailAddress address) {
	
	/**
	 * Constructs a new mailbox with the given display name and address.<br>
	 *
	 * @param displayName The optional display name, or null for a bare address
	 * @param address The mail address of this mailbox
	 * @throws NullPointerException If the address is null
	 * @throws IllegalArgumentException If the display name contains a line break
	 */
	public Mailbox {
		Objects.requireNonNull(address, "Address must not be null");
		if (displayName != null && (displayName.indexOf('\r') >= 0 || displayName.indexOf('\n') >= 0)) {
			throw new IllegalArgumentException("Display name must not contain line breaks: " + displayName);
		}
	}
	
	/**
	 * Creates a new mailbox with no display name.<br>
	 *
	 * @param address The mail address of this mailbox
	 * @return A new mailbox wrapping the given address
	 * @throws NullPointerException If the address is null
	 */
	public static @NonNull Mailbox of(@NonNull MailAddress address) {
		return new Mailbox(null, address);
	}
	
	/**
	 * Creates a new mailbox with the given display name and address.<br>
	 *
	 * @param displayName The display name of the mailbox
	 * @param address The mail address of this mailbox
	 * @return A new mailbox
	 * @throws NullPointerException If the display name or address is null
	 * @throws IllegalArgumentException If the display name contains a line break
	 */
	public static @NonNull Mailbox of(@NonNull String displayName, @NonNull MailAddress address) {
		Objects.requireNonNull(displayName, "Display name must not be null");
		return new Mailbox(displayName, address);
	}
	
	/**
	 * Parses a mailbox from an RFC 5322 name-addr or bare addr-spec string.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * parse("john@example.com")            -> Mailbox(null, john@example.com)
	 * parse("John Doe <john@example.com>") -> Mailbox("John Doe", john@example.com)
	 * parse("\"Doe, John\" <john@x.com>")  -> Mailbox("Doe, John", john@x.com)
	 * }</pre>
	 *
	 * @param mailbox The mailbox string to parse
	 * @return The parsed mailbox
	 * @throws NullPointerException If the mailbox string is null
	 * @throws IllegalArgumentException If the mailbox string is malformed or the address is invalid
	 */
	public static @NonNull Mailbox parse(@NonNull String mailbox) {
		Objects.requireNonNull(mailbox, "Mailbox must not be null");
		
		String trimmed = mailbox.trim();
		int lt = trimmed.indexOf('<');
		if (lt >= 0) {
			int gt = trimmed.indexOf('>', lt);
			if (gt < 0) {
				throw new IllegalArgumentException("Mailbox is missing the closing '>': " + mailbox);
			}
			
			String name = trimmed.substring(0, lt).trim();
			String addr = trimmed.substring(lt + 1, gt).trim();
			String displayName = name.isEmpty() ? null : unquote(name);
			return new Mailbox(displayName, MailAddress.parse(addr));
		}
		return new Mailbox(null, MailAddress.parse(trimmed));
	}
	
	/**
	 * Removes surrounding double quotes from a quoted-string display name and unescapes it.<br>
	 *
	 * @param name The raw display name text
	 * @return The unquoted and unescaped display name
	 */
	private static @NonNull String unquote(@NonNull String name) {
		if (name.length() >= 2 && name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"') {
			return name.substring(1, name.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
		}
		return name;
	}
	
	//region Object overrides
	
	/**
	 * Returns the RFC 5322 string representation of this mailbox.<br>
	 * A mailbox without a display name renders as a bare addr-spec, otherwise as a name-addr with
	 * the display name quoted or RFC 2047 encoded as needed.<br>
	 *
	 * @return The mailbox as a string
	 */
	@Override
	public @NonNull String toString() {
		if (this.displayName == null) {
			return this.address.toString();
		}
		return MailMessageSerializer.encodeDisplayName(this.displayName) + " <" + this.address + ">";
	}
	//endregion
}
