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
 * A {@code text/plain} email body with an associated character set.<br>
 * When serialized, the text is encoded using quoted-printable transfer encoding.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * TextContent body = TextContent.of("Hello, world!");
 * TextContent latin = TextContent.of("Best regards", StandardCharsets.ISO_8859_1);
 * }</pre>
 *
 * @see MailContent
 *
 * @author Luis-St
 *
 * @param text The plain text body
 * @param charset The character set used to encode the text
 */
public record TextContent(@NonNull String text, @NonNull Charset charset) implements MailContent {
	
	/**
	 * Constructs a new plain text content with the given text and character set.<br>
	 *
	 * @param text The plain text body
	 * @param charset The character set used to encode the text
	 * @throws NullPointerException If the text or charset is null
	 */
	public TextContent {
		Objects.requireNonNull(text, "Text must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
	}
	
	/**
	 * Creates a new plain text content using the UTF-8 character set.<br>
	 *
	 * @param text The plain text body
	 * @return A new plain text content
	 * @throws NullPointerException If the text is null
	 */
	public static @NonNull TextContent of(@NonNull String text) {
		return new TextContent(text, StandardCharsets.UTF_8);
	}
	
	/**
	 * Creates a new plain text content with the given text and character set.<br>
	 *
	 * @param text The plain text body
	 * @param charset The character set used to encode the text
	 * @return A new plain text content
	 * @throws NullPointerException If the text or charset is null
	 */
	public static @NonNull TextContent of(@NonNull String text, @NonNull Charset charset) {
		return new TextContent(text, charset);
	}
	
	@Override
	public @NonNull String contentType() {
		return "text/plain";
	}
}
