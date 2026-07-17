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

/**
 * The body content of an email message, modelled as a MIME entity.<br>
 * This sealed interface is implemented by the concrete content types used to build simple and multipart messages.<br>
 * <ul>
 *     <li>{@link TextContent} a {@code text/plain} body</li>
 *     <li>{@link HtmlContent} a {@code text/html} body</li>
 *     <li>{@link MailAttachment} a Base64 encoded file or binary part</li>
 *     <li>{@link MultipartContent} a {@code multipart/*} container of other content</li>
 * </ul>
 *
 * @see TextContent
 * @see HtmlContent
 * @see MailAttachment
 * @see MultipartContent
 *
 * @author Luis-St
 */
public sealed interface MailContent permits TextContent, HtmlContent, MailAttachment, MultipartContent {
	
	/**
	 * Returns the MIME media type of this content, without any parameters.<br>
	 * For example {@code text/plain}, {@code text/html}, or {@code multipart/mixed}.<br>
	 *
	 * @return The MIME content type
	 */
	@NonNull String contentType();
}
