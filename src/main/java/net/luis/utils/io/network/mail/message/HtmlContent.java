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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A {@code text/html} email body with an associated character set.<br>
 * When serialized, the markup is encoded using quoted-printable transfer encoding.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * HtmlContent body = HtmlContent.of("<h1>Hello</h1>");
 * }</pre>
 *
 * @see MailContent
 *
 * @author Luis-St
 *
 * @param html The HTML markup body
 * @param charset The character set used to encode the markup
 */
public record HtmlContent(@NonNull String html, @NonNull Charset charset) implements MailContent {
	
	/**
	 * Constructs a new HTML content with the given markup and character set.<br>
	 *
	 * @param html The HTML markup body
	 * @param charset The character set used to encode the markup
	 * @throws NullPointerException If the markup or charset is null
	 */
	public HtmlContent {
		Objects.requireNonNull(html, "Html must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
	}
	
	/**
	 * Creates a new HTML content using the UTF-8 character set.<br>
	 *
	 * @param html The HTML markup body
	 * @return A new HTML content
	 * @throws NullPointerException If the markup is null
	 */
	public static @NonNull HtmlContent of(@NonNull String html) {
		return new HtmlContent(html, StandardCharsets.UTF_8);
	}
	
	/**
	 * Creates a new HTML content with the given markup and character set.<br>
	 *
	 * @param html The HTML markup body
	 * @param charset The character set used to encode the markup
	 * @return A new HTML content
	 * @throws NullPointerException If the markup or charset is null
	 */
	public static @NonNull HtmlContent of(@NonNull String html, @NonNull Charset charset) {
		return new HtmlContent(html, charset);
	}
	
	@Override
	public @NonNull String contentType() {
		return "text/html";
	}
}
