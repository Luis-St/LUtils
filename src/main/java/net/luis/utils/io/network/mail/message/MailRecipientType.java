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

/**
 * The type of an email recipient, determining how the recipient appears in the message headers.<br>
 * All types are used as envelope recipients ({@code RCPT TO}),<br>
 * but only {@link #TO} and {@link #CC} are written to the message headers, {@link #BCC} recipients are deliberately omitted.
 *
 * @author Luis-St
 */
public enum MailRecipientType {
	
	/**
	 * A primary recipient, written to the {@code To} header.<br>
	 */
	TO,
	
	/**
	 * A carbon-copy recipient, written to the {@code Cc} header.<br>
	 */
	CC,
	
	/**
	 * A blind carbon-copy recipient, used for delivery but never written to any header.<br>
	 */
	BCC
}
