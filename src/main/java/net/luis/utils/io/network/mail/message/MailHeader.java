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
 * Represents a single RFC 5322 message header field: a name and its value.<br>
 * The field name must be a printable ASCII token without a colon,<br>
 * and neither the name nor the value may contain a carriage return or line feed,<br>
 * which prevents header injection.<br>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * MailHeader header = new MailHeader("X-Priority", "1");
 * }</pre>
 *
 * @author Luis-St
 *
 * @param name The field name
 * @param value The field value
 */
public record MailHeader(@NonNull String name, @NonNull String value) {
	
	/**
	 * Constructs a new mail header from the given name and value.<br>
	 *
	 * @param name The field name
	 * @param value The field value
	 * @throws NullPointerException If the name or value is null
	 * @throws IllegalArgumentException If the name is empty or invalid, or the value contains a line break
	 */
	public MailHeader {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name must not be empty");
		}
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c <= ' ' || c >= 0x7F || c == ':') {
				throw new IllegalArgumentException("Name contains an illegal character: " + name);
			}
		}
		if (value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0) {
			throw new IllegalArgumentException("Value must not contain line breaks: " + value);
		}
	}
}
