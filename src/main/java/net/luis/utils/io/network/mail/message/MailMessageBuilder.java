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
import java.util.*;

/**
 * Builder for constructing {@link MailMessage} instances with a fluent API.<br>
 * The sender, subject, and content are required; the date and message identifier are filled in automatically when not set.<br>
 * Adding one or more attachments wraps the content in a {@code multipart/mixed} container at build time.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * MailMessage message = MailMessage.builder()
 *     .from(Mailbox.parse("alice@example.com"))
 *     .to(Mailbox.parse("bob@example.com"))
 *     .cc(Mailbox.parse("carol@example.com"))
 *     .subject("Report")
 *     .content(TextContent.of("See attached."))
 *     .attach(Attachment.of("report.pdf", "application/pdf", pdfBytes))
 *     .build();
 * }</pre>
 *
 * @see MailMessage
 *
 * @author Luis-St
 */
public final class MailMessageBuilder {
	
	/**
	 * The recipients added so far.<br>
	 */
	private final List<MailRecipient> recipients = new ArrayList<>();
	/**
	 * The additional headers added so far.<br>
	 */
	private final List<MailHeader> headers = new ArrayList<>();
	/**
	 * The attachments added so far.<br>
	 */
	private final List<MailAttachment> attachments = new ArrayList<>();
	/**
	 * The sender mailbox.<br>
	 */
	private @Nullable Mailbox from;
	/**
	 * The reply-to mailbox, or null if none.<br>
	 */
	private @Nullable Mailbox replyTo;
	/**
	 * The subject line.<br>
	 */
	private @Nullable String subject;
	/**
	 * The MIME body content.<br>
	 */
	private @Nullable MailContent content;
	/**
	 * The message date, or null to use the current time at build.<br>
	 */
	private @Nullable Instant date;
	/**
	 * The message identifier, or null to generate one at build.<br>
	 */
	private @Nullable String messageId;
	
	/**
	 * Constructs a new builder with no values set.<br>
	 */
	MailMessageBuilder() {}
	
	/**
	 * Sets the sender mailbox.<br>
	 *
	 * @param from The sender mailbox
	 * @return This builder for method chaining
	 * @throws NullPointerException If the sender mailbox is null
	 */
	public @NonNull MailMessageBuilder from(@NonNull Mailbox from) {
		this.from = Objects.requireNonNull(from, "From must not be null");
		return this;
	}
	
	/**
	 * Sets the reply-to mailbox.<br>
	 *
	 * @param replyTo The reply-to mailbox, or null to clear
	 * @return This builder for method chaining
	 */
	public @NonNull MailMessageBuilder replyTo(@Nullable Mailbox replyTo) {
		this.replyTo = replyTo;
		return this;
	}
	
	/**
	 * Sets the subject line.<br>
	 *
	 * @param subject The subject line
	 * @return This builder for method chaining
	 * @throws NullPointerException If the subject is null
	 */
	public @NonNull MailMessageBuilder subject(@NonNull String subject) {
		this.subject = Objects.requireNonNull(subject, "Subject must not be null");
		return this;
	}
	
	/**
	 * Sets the MIME body content.<br>
	 *
	 * @param content The MIME body content
	 * @return This builder for method chaining
	 * @throws NullPointerException If the content is null
	 */
	public @NonNull MailMessageBuilder content(@NonNull MailContent content) {
		this.content = Objects.requireNonNull(content, "Content must not be null");
		return this;
	}
	
	/**
	 * Sets the message date.<br>
	 * If not set, the current time is used when the message is built.<br>
	 *
	 * @param date The message date
	 * @return This builder for method chaining
	 * @throws NullPointerException If the date is null
	 */
	public @NonNull MailMessageBuilder date(@NonNull Instant date) {
		this.date = Objects.requireNonNull(date, "Date must not be null");
		return this;
	}
	
	/**
	 * Sets the message identifier.<br>
	 * If not set, a unique identifier is generated when the message is built.<br>
	 *
	 * @param messageId The message identifier
	 * @return This builder for method chaining
	 * @throws NullPointerException If the message identifier is null
	 */
	public @NonNull MailMessageBuilder messageId(@NonNull String messageId) {
		this.messageId = Objects.requireNonNull(messageId, "Message id must not be null");
		return this;
	}
	
	/**
	 * Adds a recipient of an explicit type.<br>
	 *
	 * @param recipient The recipient to add
	 * @return This builder for method chaining
	 * @throws NullPointerException If the recipient is null
	 */
	public @NonNull MailMessageBuilder recipient(@NonNull MailRecipient recipient) {
		Objects.requireNonNull(recipient, "Recipient must not be null");
		
		this.recipients.add(recipient);
		return this;
	}
	
	/**
	 * Adds a primary ({@link MailRecipientType#TO}) recipient.<br>
	 *
	 * @param mailbox The recipient mailbox
	 * @return This builder for method chaining
	 * @throws NullPointerException If the mailbox is null
	 */
	public @NonNull MailMessageBuilder to(@NonNull Mailbox mailbox) {
		return this.recipient(MailRecipient.to(mailbox));
	}
	
	/**
	 * Adds a carbon-copy ({@link MailRecipientType#CC}) recipient.<br>
	 *
	 * @param mailbox The recipient mailbox
	 * @return This builder for method chaining
	 * @throws NullPointerException If the mailbox is null
	 */
	public @NonNull MailMessageBuilder cc(@NonNull Mailbox mailbox) {
		return this.recipient(MailRecipient.cc(mailbox));
	}
	
	/**
	 * Adds a blind carbon-copy ({@link MailRecipientType#BCC}) recipient.<br>
	 *
	 * @param mailbox The recipient mailbox
	 * @return This builder for method chaining
	 * @throws NullPointerException If the mailbox is null
	 */
	public @NonNull MailMessageBuilder bcc(@NonNull Mailbox mailbox) {
		return this.recipient(MailRecipient.bcc(mailbox));
	}
	
	/**
	 * Adds an additional header.<br>
	 *
	 * @param header The header to add
	 * @return This builder for method chaining
	 * @throws NullPointerException If the header is null
	 */
	public @NonNull MailMessageBuilder header(@NonNull MailHeader header) {
		Objects.requireNonNull(header, "Header must not be null");
		
		this.headers.add(header);
		return this;
	}
	
	/**
	 * Adds an additional header from a name and value.<br>
	 *
	 * @param name The header field name
	 * @param value The header field value
	 * @return This builder for method chaining
	 * @throws NullPointerException If the name or value is null
	 * @throws IllegalArgumentException If the name is empty or invalid, or the value contains a line break
	 */
	public @NonNull MailMessageBuilder header(@NonNull String name, @NonNull String value) {
		return this.header(new MailHeader(name, value));
	}
	
	/**
	 * Adds an attachment.<br>
	 * When the message is built, the presence of any attachment wraps the content in a
	 * {@code multipart/mixed} container.<br>
	 *
	 * @param attachment The attachment to add
	 * @return This builder for method chaining
	 * @throws NullPointerException If the attachment is null
	 */
	public @NonNull MailMessageBuilder attach(@NonNull MailAttachment attachment) {
		Objects.requireNonNull(attachment, "Attachment must not be null");
		
		this.attachments.add(attachment);
		return this;
	}
	
	/**
	 * Builds a new mail message from the configured values.<br>
	 * The date defaults to the current time and the message identifier is generated if not set.<br>
	 *
	 * @return A new mail message
	 * @throws NullPointerException If the sender, subject, or content has not been set
	 * @throws IllegalArgumentException If no recipient has been added
	 */
	public @NonNull MailMessage build() {
		Mailbox from = Objects.requireNonNull(this.from, "From must be set");
		String subject = Objects.requireNonNull(this.subject, "Subject must be set");
		MailContent content = Objects.requireNonNull(this.content, "Content must be set");
		
		MailContent resolvedContent = content;
		if (!this.attachments.isEmpty()) {
			MailContent[] parts = new MailContent[1 + this.attachments.size()];
			parts[0] = content;
			
			for (int i = 0; i < this.attachments.size(); i++) {
				parts[i + 1] = this.attachments.get(i);
			}
			
			resolvedContent = MultipartContent.mixed(parts);
		}
		
		Instant resolvedDate = this.date != null ? this.date : Instant.now();
		String resolvedMessageId = this.messageId != null ? this.messageId : "<" + UUID.randomUUID() + "@" + from.address().domain() + ">";
		return new MailMessage(from, this.replyTo, this.recipients, subject, resolvedContent, resolvedDate, resolvedMessageId, this.headers);
	}
}
