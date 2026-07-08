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

import java.util.List;
import java.util.Objects;

/**
 * A {@code multipart/*} MIME container grouping several {@link MailContent} parts under a subtype.<br>
 * The two common subtypes are {@code alternative} (for example a text and an HTML rendering of the same message) and {@code mixed} (a body together with attachments).<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * MultipartContent body = MultipartContent.alternative(
 *     TextContent.of("Plain version"),
 *     HtmlContent.of("<p>HTML version</p>")
 * );
 * MultipartContent full = MultipartContent.mixed(body, attachment);
 * }</pre>
 *
 * @see MailContent
 *
 * @author Luis-St
 *
 * @param subtype The multipart subtype, e.g. {@code alternative} or {@code mixed}
 * @param parts The contained content parts
 */
public record MultipartContent(@NonNull String subtype, @NonNull List<MailContent> parts) implements MailContent {
	
	/**
	 * Constructs a new multipart content with the given subtype and parts.<br>
	 * The parts list is copied defensively into an immutable list.<br>
	 *
	 * @param subtype The multipart subtype
	 * @param parts The contained content parts
	 * @throws NullPointerException If the subtype or parts list is null
	 * @throws IllegalArgumentException If the subtype is empty or the parts list is empty
	 */
	public MultipartContent {
		Objects.requireNonNull(subtype, "Subtype must not be null");
		Objects.requireNonNull(parts, "Parts must not be null");
		
		if (subtype.isEmpty()) {
			throw new IllegalArgumentException("Subtype must not be empty");
		}
		if (parts.isEmpty()) {
			throw new IllegalArgumentException("Parts must not be empty");
		}
		
		parts = List.copyOf(parts);
	}
	
	/**
	 * Creates a new {@code multipart/alternative} content from the given parts.<br>
	 * The parts should represent the same message in different formats,<br>
	 * ordered from least to most preferred (for example plain text first, then HTML).<br>
	 *
	 * @param parts The alternative content parts
	 * @return A new multipart/alternative content
	 * @throws NullPointerException If the parts array is null
	 * @throws IllegalArgumentException If no parts are provided
	 */
	public static @NonNull MultipartContent alternative(@NonNull MailContent @NonNull ... parts) {
		Objects.requireNonNull(parts, "Parts must not be null");
		return new MultipartContent("alternative", List.of(parts));
	}
	
	/**
	 * Creates a new {@code multipart/mixed} content from the given parts.<br>
	 * The first part is typically the message body and the remaining parts are attachments.<br>
	 *
	 * @param parts The mixed content parts
	 * @return A new multipart/mixed content
	 * @throws NullPointerException If the parts array is null
	 * @throws IllegalArgumentException If no parts are provided
	 */
	public static @NonNull MultipartContent mixed(@NonNull MailContent @NonNull ... parts) {
		Objects.requireNonNull(parts, "Parts must not be null");
		return new MultipartContent("mixed", List.of(parts));
	}
	
	@Override
	public @NonNull String contentType() {
		return "multipart/" + this.subtype;
	}
}
