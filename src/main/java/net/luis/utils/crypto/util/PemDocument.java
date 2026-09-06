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

package net.luis.utils.crypto.util;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A decoded PEM document, its label and its DER encoded content.<br>
 * <p>
 *     The label is the text between the BEGIN and END markers, for example {@code PUBLIC KEY}.<br>
 *     It says what the content is, so a caller that asked for a certificate cannot silently be handed a private key.
 * </p>
 *
 * @author Luis-St
 *
 * @param label The label between the BEGIN and END markers
 * @param content The decoded DER encoded content
 */
public record PemDocument(
	@NonNull String label,
	byte @NonNull [] content
) {
	
	/**
	 * Constructs a new pem document.<br>
	 * @throws NullPointerException If the label or the content is null
	 */
	public PemDocument {
		Objects.requireNonNull(label, "Label must not be null");
		Objects.requireNonNull(content, "Content must not be null");
	}
}
