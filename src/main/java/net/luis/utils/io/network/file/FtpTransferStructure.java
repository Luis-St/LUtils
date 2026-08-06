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

package net.luis.utils.io.network.file;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public enum FtpTransferStructure {
	
	/**
	 * File structure ({@code STRU F}), the file is treated as a continuous sequence of bytes without internal structure.<br>
	 * <p>
	 *     No structural markers are placed on the data connection.<br>
	 *     In stream mode the end of the file is signaled solely by closing the data connection,<br>
	 *     in block and compressed mode by the {@code EOF} bit of the block descriptor.
	 * </p>
	 * This is the only structure implemented by virtually all modern servers and the default if {@code STRU} is never issued.<br>
	 */
	FILE("File", 'F'),
	/**
	 * Record structure ({@code STRU R}), the file is treated as a sequence of sequential records.<br>
	 * <p>
	 *     The record boundaries are transmitted in-band on the data connection,<br>
	 *     their encoding depends on the negotiated {@link FtpTransferMode}.
	 * </p>
	 * In stream mode the boundaries are inline escape sequences introduced by the escape byte {@code 0xFF}:
	 * <ul>
	 *     <li>{@code FF 01} end of record</li>
	 *     <li>{@code FF 02} end of file</li>
	 *     <li>{@code FF 03} end of record and end of file</li>
	 *     <li>{@code FF FF} a literal {@code 0xFF} data byte</li>
	 * </ul>
	 * Consequently every {@code 0xFF} contained in the payload must be doubled by the sender and collapsed again by the receiver.<br>
	 * <p>
	 *     In block and compressed mode the boundaries are moved out of the payload into the block descriptor,<br>
	 *     so no byte stuffing is required.
	 * </p>
	 * This structure is largely limited to record-oriented mainframe hosts, most servers reject it with a {@code 504} reply.<br>
	 */
	RECORD("Record", 'R'),
	/**
	 * Page structure ({@code STRU P}), the file is treated as a set of independently indexed pages.<br>
	 * <p>
	 *     Every page is preceded by its own header on the data connection,<br>
	 *     counted in logical bytes and consisting of the header length including itself, the logical page index,<br>
	 *     the data length, the page type and any type-specific fields:
	 * </p>
	 * <table>
	 *     <caption>Page header structure</caption>
	 *     <tr>
	 *         <th>Byte index</th>
	 *         <td>0</td>
	 *         <td>1</td>
	 *         <td>2</td>
	 *         <td>3</td>
	 *         <td>1</td>
	 *     </tr>
	 *     <tr>
	 *         <th>Field</th>
	 *         <td>Header length</td>
	 *         <td>Page index</td>
	 *         <td>Data length</td>
	 *         <td>Page type</td>
	 *         <td>Type-specific fields (only for type 0x03)</td>
	 *     </tr>
	 * </table>
	 * <p>
	 *     Simple page 4 (Type: {@code 0x01}), followed by 8 logical bytes of data:
	 * </p>
	 * <pre>{@code
	 * 0x04 0x04 0x08 0x01 0x..
	 * }</pre>
	 * <p>
	 *     	Descriptor page 0 ({Type: @code 0x02}), followed by 12 logical bytes of descriptive data (e.g. file size, timestamp, etc.):
	 * </p>
	 * <pre>{@code
	 * 0x04 0x00 0x0C 0x02 0x..
	 * }</pre>
	 * <p>
	 *     	Access controlled page 1 (Type: {@code 0x03}), followed by 16 logical bytes of access-controlled data (e.g permissions, encryption, etc.):
	 * </p>
	 * <pre>{@code
	 * 0x05 0x01 0x0F 0x03 0x02 0x..
	 * }</pre>
	 * <p>
	 *     Last page (Type: {@code 0x00}), carries no data:
	 * </p>
	 * <pre>{@code
	 *  0x04 0x03 0x00 0x00 0x..
	 * }</pre>
	 * <p>
	 *     The defined page types are {@code 0} for the last page, {@code 1} for a simple page,<br>
	 *     {@code 2} for a descriptor page and {@code 3} for an access-controlled page,<br>
	 *     the latter carries an additional access control field and therefore uses a header length of {@code 5} instead of {@code 4}.
	 * </p>
	 * <p>
	 *     Since the pages are indexed, they may be sent out of order or with gaps.<br>
	 *     This structure originates from TENEX hosts, is discouraged by RFC 1123 and is practically never supported.
	 * </p>
	 */
	PAGE("Page", 'P');
	
	private final String name;
	private final char code;
	
	private FtpTransferStructure(@NonNull String name, char code) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.code = code;
	}
	
	public @NonNull String getName() {
		return this.name;
	}
	
	public char getCode() {
		return this.code;
	}
}
