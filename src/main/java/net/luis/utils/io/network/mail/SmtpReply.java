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

package net.luis.utils.io.network.mail;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * A parsed SMTP server reply consisting of a three-digit status code and one or more text lines.<br>
 * A multiline reply (for example the capability list returned by {@code EHLO}) carries the text of each line,<br>
 * with the leading code and separator already stripped.<br>
 * <p>
 *     The reply code classifies the outcome by its leading digit:
 * </p>
 * <ul>
 *     <li>{@code 2xx} positive completion ({@link #isPositiveCompletion()})</li>
 *     <li>{@code 3xx} positive intermediate, more input expected ({@link #isIntermediate()})</li>
 *     <li>{@code 4xx}/{@code 5xx} transient or permanent error ({@link #isError()})</li>
 * </ul>
 *
 * @author Luis-St
 *
 * @param code The three-digit SMTP reply code
 * @param lines The text lines of the reply, without their leading code
 */
public record SmtpReply(int code, @NonNull List<String> lines) {
	
	/**
	 * The minimum valid SMTP reply code.<br>
	 */
	public static final int MIN_CODE = 100;
	/**
	 * The maximum valid SMTP reply code.<br>
	 */
	public static final int MAX_CODE = 599;
	
	/**
	 * Constructs a new SMTP reply.<br>
	 * The lines list is copied defensively into an immutable list.<br>
	 *
	 * @param code The three-digit SMTP reply code
	 * @param lines The text lines of the reply
	 * @throws NullPointerException If the lines list is null
	 * @throws IllegalArgumentException If the code is not between 100 and 599
	 */
	public SmtpReply {
		Objects.requireNonNull(lines, "Lines must not be null");
		if (code < MIN_CODE || code > MAX_CODE) {
			throw new IllegalArgumentException("Reply code must be between " + MIN_CODE + " and " + MAX_CODE + ": " + code);
		}
		
		lines = List.copyOf(lines);
	}
	
	/**
	 * Returns whether this reply is a positive completion reply ({@code 2xx}).<br>
	 * @return True if the code is in the 200-299 range
	 */
	public boolean isPositiveCompletion() {
		return this.code / 100 == 2;
	}
	
	/**
	 * Returns whether this reply is a positive intermediate reply ({@code 3xx}).<br>
	 * @return True if the code is in the 300-399 range
	 */
	public boolean isIntermediate() {
		return this.code / 100 == 3;
	}
	
	/**
	 * Returns whether this reply is an error reply ({@code 4xx} or {@code 5xx}).<br>
	 * @return True if the code is 400 or greater
	 */
	public boolean isError() {
		return this.code >= 400;
	}
	
	/**
	 * Returns the reply text as a single string with the lines joined by line feeds.<br>
	 * @return The joined reply message
	 */
	public @NonNull String message() {
		return String.join("\n", this.lines);
	}
}
