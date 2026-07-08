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

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

/**
 * Renders a {@link MailMessage} into its RFC 5322 wire representation with CRLF line endings.<br>
 * The serializer writes the message headers, encodes non-ASCII header text as RFC 2047 encoded words,<br>
 * and renders the MIME body with quoted-printable text parts and Base64 attachments.<br>
 * <p>
 *     Blind carbon-copy recipients are never written to the headers, and SMTP dot-stuffing is not applied here,<br>
 *     this is handled by the transport layer.
 * </p>
 *
 * @see MailMessage
 *
 * @author Luis-St
 */
final class MailMessageSerializer {
	
	/**
	 * The CRLF line separator required by RFC 5322.<br>
	 */
	private static final String CRLF = "\r\n";
	/**
	 * The uppercase hexadecimal digits used for quoted-printable encoding.<br>
	 */
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();
	/**
	 * The RFC 5322 date/time formatter.<br>
	 */
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	/**
	 * The characters that force a display name to be rendered as a quoted string.<br>
	 */
	private static final String SPECIALS = "()<>@,;:\\\".[]";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private MailMessageSerializer() {}
	
	/**
	 * Serializes the given message to its RFC 5322 wire representation.<br>
	 *
	 * @param message The message to serialize
	 * @return The serialized message using CRLF line endings
	 */
	static @NonNull String serialize(@NonNull MailMessage message) {
		StringBuilder sb = new StringBuilder();
		sb.append("From: ").append(message.from()).append(CRLF);
		if (message.replyTo() != null) {
			sb.append("Reply-To: ").append(message.replyTo()).append(CRLF);
		}
		
		appendRecipientHeader(sb, message, MailRecipientType.TO, "To");
		appendRecipientHeader(sb, message, MailRecipientType.CC, "Cc");
		sb.append("Subject: ").append(encodeHeaderText(message.subject())).append(CRLF);
		sb.append("Date: ").append(DATE_FORMAT.format(OffsetDateTime.ofInstant(message.date(), ZoneOffset.UTC))).append(CRLF);
		
		if (message.messageId() != null) {
			sb.append("Message-ID: ").append(ensureBrackets(message.messageId())).append(CRLF);
		}
		
		sb.append("MIME-Version: 1.0").append(CRLF);
		for (MailHeader header : message.headers()) {
			sb.append(header.name()).append(": ").append(header.value()).append(CRLF);
		}
		
		appendEntity(sb, message.content(), new int[] { 0 });
		return sb.toString();
	}
	
	/**
	 * Encodes header text as an RFC 2047 encoded word if it contains non-ASCII characters.<br>
	 *
	 * @param text The header text to encode
	 * @return The header text, RFC 2047 encoded if necessary
	 */
	static @NonNull String encodeHeaderText(@NonNull String text) {
		if (isAscii(text)) {
			return text;
		}
		return encodeWord(text);
	}
	
	/**
	 * Encodes a display name for use in a name-addr header, quoting or RFC 2047 encoding it as needed.<br>
	 *
	 * @param displayName The display name to encode
	 * @return The encoded display name
	 */
	static @NonNull String encodeDisplayName(@NonNull String displayName) {
		if (isAscii(displayName)) {
			if (needsQuoting(displayName)) {
				return "\"" + displayName.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
			}
			return displayName;
		}
		return encodeWord(displayName);
	}
	
	//region Helper methods
	
	/**
	 * Appends a recipient header for all recipients of the given type, if any exist.<br>
	 *
	 * @param sb The builder to append to
	 * @param message The message whose recipients are read
	 * @param type The recipient type to collect
	 * @param name The header field name to write
	 */
	private static void appendRecipientHeader(@NonNull StringBuilder sb, @NonNull MailMessage message, @NonNull MailRecipientType type, @NonNull String name) {
		StringBuilder value = new StringBuilder();
		
		for (MailRecipient recipient : message.recipients()) {
			if (recipient.type() == type) {
				if (!value.isEmpty()) {
					value.append(", ");
				}
				
				value.append(recipient.mailbox());
			}
		}
		
		if (!value.isEmpty()) {
			sb.append(name).append(": ").append(value).append(CRLF);
		}
	}
	
	/**
	 * Appends a MIME entity: its content headers, a blank line, and the encoded body.<br>
	 *
	 * @param sb The builder to append to
	 * @param content The content to render
	 * @param counter A single-element holder used to allocate unique multipart boundaries
	 */
	private static void appendEntity(@NonNull StringBuilder sb, @NonNull MailContent content, int @NonNull [] counter) {
		switch (content) {
			case TextContent text -> {
				sb.append("Content-Type: text/plain; charset=\"").append(text.charset().name()).append("\"").append(CRLF);
				sb.append("Content-Transfer-Encoding: quoted-printable").append(CRLF);
				sb.append(CRLF);
				sb.append(quotedPrintable(text.text().getBytes(text.charset()))).append(CRLF);
			}
			case HtmlContent html -> {
				sb.append("Content-Type: text/html; charset=\"").append(html.charset().name()).append("\"").append(CRLF);
				sb.append("Content-Transfer-Encoding: quoted-printable").append(CRLF);
				sb.append(CRLF);
				sb.append(quotedPrintable(html.html().getBytes(html.charset()))).append(CRLF);
			}
			case MailAttachment attachment -> {
				sb.append("Content-Type: ").append(attachment.contentType()).append("; name=\"").append(attachment.fileName()).append("\"").append(CRLF);
				sb.append("Content-Transfer-Encoding: base64").append(CRLF);
				sb.append("Content-Disposition: attachment; filename=\"").append(attachment.fileName()).append("\"").append(CRLF);
				sb.append(CRLF);
				sb.append(Base64.getMimeEncoder().encodeToString(attachment.data())).append(CRLF);
			}
			case MultipartContent multipart -> {
				String boundary = "=_Part_" + counter[0]++;
				sb.append("Content-Type: multipart/").append(multipart.subtype()).append("; boundary=\"").append(boundary).append("\"").append(CRLF);
				sb.append(CRLF);
				
				for (MailContent part : multipart.parts()) {
					sb.append("--").append(boundary).append(CRLF);
					appendEntity(sb, part, counter);
				}
				
				sb.append("--").append(boundary).append("--").append(CRLF);
			}
		}
	}
	
	/**
	 * Encodes the given bytes using quoted-printable transfer encoding with soft line wrapping.<br>
	 *
	 * @param bytes The bytes to encode
	 * @return The quoted-printable encoded text
	 */
	private static @NonNull String quotedPrintable(byte @NonNull [] bytes) {
		StringBuilder sb = new StringBuilder();
		int lineLength = 0;
		for (int i = 0; i < bytes.length; i++) {
			int b = bytes[i] & 0xFF;
			if (b == '\r') {
				continue;
			}
			
			if (b == '\n') {
				sb.append(CRLF);
				lineLength = 0;
				continue;
			}
			
			String token;
			if (b == ' ' || b == '\t') {
				boolean lineEnd = i + 1 >= bytes.length || bytes[i + 1] == '\n' || bytes[i + 1] == '\r';
				token = lineEnd ? encodeByte(b) : String.valueOf((char) b);
			} else if (b >= 33 && b <= 126 && b != '=') {
				token = String.valueOf((char) b);
			} else {
				token = encodeByte(b);
			}
			
			if (lineLength + token.length() > 75) {
				sb.append('=').append(CRLF);
				lineLength = 0;
			}
			
			sb.append(token);
			lineLength += token.length();
		}
		return sb.toString();
	}
	
	/**
	 * Encodes a single byte as a quoted-printable escape ({@code =XX}).<br>
	 *
	 * @param b The byte value (0-255)
	 * @return The quoted-printable escape
	 */
	private static @NonNull String encodeByte(int b) {
		return "=" + HEX[(b >> 4) & 0xF] + HEX[b & 0xF];
	}
	
	/**
	 * Encodes the given text as an RFC 2047 Base64 encoded word.<br>
	 *
	 * @param text The text to encode
	 * @return The RFC 2047 encoded word
	 */
	private static @NonNull String encodeWord(@NonNull String text) {
		return "=?" + StandardCharsets.UTF_8.name() + "?B?" + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)) + "?=";
	}
	
	/**
	 * Ensures the given message identifier is wrapped in angle brackets.<br>
	 *
	 * @param messageId The message identifier
	 * @return The message identifier wrapped in angle brackets
	 */
	private static @NonNull String ensureBrackets(@NonNull String messageId) {
		if (messageId.startsWith("<") && messageId.endsWith(">")) {
			return messageId;
		}
		return "<" + messageId + ">";
	}
	
	/**
	 * Returns whether every character of the given text is a 7-bit ASCII character.<br>
	 *
	 * @param text The text to check
	 * @return True if the text is pure ASCII
	 */
	private static boolean isAscii(@NonNull String text) {
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) > 0x7F) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns whether the given ASCII display name contains characters that require quoting.<br>
	 *
	 * @param text The display name to check
	 * @return True if the display name must be rendered as a quoted string
	 */
	private static boolean needsQuoting(@NonNull String text) {
		for (int i = 0; i < text.length(); i++) {
			if (SPECIALS.indexOf(text.charAt(i)) >= 0) {
				return true;
			}
		}
		return false;
	}
	//endregion
}
