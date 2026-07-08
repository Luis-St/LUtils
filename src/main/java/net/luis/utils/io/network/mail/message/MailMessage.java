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

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * An immutable, fully modelled email message ready for RFC 5322 serialization.<br>
 * A message bundles the sender, recipients, subject, MIME body content, and headers.<br>
 * It is serialized to wire format by {@link #toRfc5322()}.<br>
 * <p>
 *     Blind carbon-copy ({@link MailRecipientType#BCC}) recipients are part of the recipient list for<br>
 *     envelope delivery but are never written to the serialized headers.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * MailMessage message = MailMessage.builder()
 *     .from(Mailbox.parse("alice@example.com"))
 *     .to(Mailbox.parse("bob@example.com"))
 *     .subject("Hello")
 *     .content(TextContent.of("Hi Bob!"))
 *     .build();
 * String wire = message.toRfc5322();
 * }</pre>
 *
 * @see MailMessageBuilder
 * @see MailContent
 *
 * @author Luis-St
 *
 * @param from The sender mailbox
 * @param replyTo The reply-to mailbox, or null if none
 * @param recipients The recipients, including blind carbon-copy recipients
 * @param subject The subject line
 * @param content The MIME body content
 * @param date The message date
 * @param messageId The message identifier, or null if none
 * @param headers Additional user-defined headers
 */
public record MailMessage(
	@NonNull Mailbox from,
	@Nullable Mailbox replyTo,
	@NonNull List<MailRecipient> recipients,
	@NonNull String subject,
	@NonNull MailContent content,
	@NonNull Instant date,
	@Nullable String messageId,
	@NonNull List<MailHeader> headers
) {
	
	/**
	 * Constructs a new mail message.<br>
	 * The recipient and header lists are copied defensively into immutable lists.<br>
	 *
	 * @param from The sender mailbox
	 * @param replyTo The reply-to mailbox, or null if none
	 * @param recipients The recipients, including blind carbon-copy recipients
	 * @param subject The subject line
	 * @param content The MIME body content
	 * @param date The message date
	 * @param messageId The message identifier, or null if none
	 * @param headers Additional user-defined headers
	 * @throws NullPointerException If from, recipients, subject, content, date, or headers is null
	 * @throws IllegalArgumentException If there are no recipients or the subject contains a line break
	 */
	public MailMessage {
		Objects.requireNonNull(from, "From must not be null");
		Objects.requireNonNull(recipients, "Recipients must not be null");
		Objects.requireNonNull(subject, "Subject must not be null");
		Objects.requireNonNull(content, "Content must not be null");
		Objects.requireNonNull(date, "Date must not be null");
		Objects.requireNonNull(headers, "Headers must not be null");
		
		if (recipients.isEmpty()) {
			throw new IllegalArgumentException("Message must have at least one recipient");
		}
		if (subject.indexOf('\r') >= 0 || subject.indexOf('\n') >= 0) {
			throw new IllegalArgumentException("Subject must not contain line breaks: " + subject);
		}
		
		recipients = List.copyOf(recipients);
		headers = List.copyOf(headers);
	}
	
	/**
	 * Creates a new builder for constructing a mail message.<br>
	 * @return A new builder
	 */
	public static @NonNull MailMessageBuilder builder() {
		return new MailMessageBuilder();
	}
	
	/**
	 * Serializes this message to its RFC 5322 wire representation using CRLF line endings.<br>
	 * The output contains the message headers followed by the MIME encoded body, but does not apply SMTP dot-stuffing,<br>
	 * which is the responsibility of the transport layer.<br>
	 *
	 * @return The serialized message
	 */
	public @NonNull String toRfc5322() {
		return MailMessageSerializer.serialize(this);
	}
}
