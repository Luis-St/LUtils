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
 * Represents an email recipient: a {@link Mailbox} paired with its {@link MailRecipientType}.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * MailRecipient to = MailRecipient.to(Mailbox.parse("john@example.com"));
 * MailRecipient bcc = MailRecipient.bcc(Mailbox.parse("audit@example.com"));
 * }</pre>
 *
 * @see Mailbox
 * @see MailRecipientType
 *
 * @author Luis-St
 *
 * @param mailbox The mailbox of the recipient
 * @param type The type of the recipient
 */
public record MailRecipient(@NonNull Mailbox mailbox, @NonNull MailRecipientType type) {
	
	/**
	 * Constructs a new recipient from the given mailbox and type.<br>
	 *
	 * @param mailbox The mailbox of the recipient
	 * @param type The type of the recipient
	 * @throws NullPointerException If the mailbox or type is null
	 */
	public MailRecipient {
		Objects.requireNonNull(mailbox, "Mailbox must not be null");
		Objects.requireNonNull(type, "Type must not be null");
	}
	
	/**
	 * Creates a new primary ({@link MailRecipientType#TO}) recipient.<br>
	 *
	 * @param mailbox The mailbox of the recipient
	 * @return A new recipient
	 * @throws NullPointerException If the mailbox is null
	 */
	public static @NonNull MailRecipient to(@NonNull Mailbox mailbox) {
		return new MailRecipient(mailbox, MailRecipientType.TO);
	}
	
	/**
	 * Creates a new carbon-copy ({@link MailRecipientType#CC}) recipient.<br>
	 *
	 * @param mailbox The mailbox of the recipient
	 * @return A new recipient
	 * @throws NullPointerException If the mailbox is null
	 */
	public static @NonNull MailRecipient cc(@NonNull Mailbox mailbox) {
		return new MailRecipient(mailbox, MailRecipientType.CC);
	}
	
	/**
	 * Creates a new blind carbon-copy ({@link MailRecipientType#BCC}) recipient.<br>
	 *
	 * @param mailbox The mailbox of the recipient
	 * @return A new recipient
	 * @throws NullPointerException If the mailbox is null
	 */
	public static @NonNull MailRecipient bcc(@NonNull Mailbox mailbox) {
		return new MailRecipient(mailbox, MailRecipientType.BCC);
	}
}
