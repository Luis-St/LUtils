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

public enum FtpTransferType {
	
	/**
	 * ASCII with Telnet format control ({@code TYPE A T}), the default transfer type.<br>
	 */
	ASCII("ASCII", 'A', "T"),
	/**
	 * ASCII without format control ({@code TYPE A N}), the receiver performs no vertical formatting.<br>
	 */
	ASCII_NON_PRINT("ASCII Non-print", 'A', "N"),
	/**
	 * ASCII with ASA carriage control ({@code TYPE A A}), the first column of each line controls vertical spacing.<br>
	 */
	ASCII_ASA("ASCII Carriage Control", 'A', "A"),
	/**
	 * Binary image transfer ({@code TYPE I}), the data is sent as a contiguous stream of bytes without conversion.<br>
	 */
	BINARY("Binary", 'I'),
	/**
	 * EBCDIC text transfer ({@code TYPE E}), intended for hosts that use EBCDIC as their internal character encoding.<br>
	 */
	EBCDIC("EBCDIC", 'E'),
	/**
	 * Unicode text transfer ({@code TYPE U}), the data is transmitted as UTF-8 encoded text.<br>
	 */
	UNICODE("Unicode", 'U'),
	/**
	 * Local byte transfer with a logical byte size of 4 bits ({@code TYPE L 4}).<br>
	 */
	LOCAL_4_BIT("Local 4-bit", 'L', "4"),
	/**
	 * Local byte transfer with a logical byte size of 8 bits ({@code TYPE L 8}).<br>
	 */
	LOCAL_8_BIT("Local 8-bit", 'L', "8"),
	/**
	 * Local byte transfer with a logical byte size of 9 bits ({@code TYPE L 9}).<br>
	 */
	LOCAL_9_BIT("Local 9-bit", 'L', "9"),
	/**
	 * Local byte transfer with a logical byte size of 16 bits ({@code TYPE L 16}).<br>
	 */
	LOCAL_16_BIT("Local 16-bit", 'L', "16"),
	/**
	 * Local byte transfer with a logical byte size of 32 bits ({@code TYPE L 32}).<br>
	 */
	LOCAL_32_BIT("Local 32-bit", 'L', "32"),
	/**
	 * Local byte transfer with a logical byte size of 36 bits ({@code TYPE L 36}).<br>
	 */
	LOCAL_36_BIT("Local 36-bit", 'L', "36");
	
	private final String name;
	private final char code;
	private final String parameter;
	
	private FtpTransferType(@NonNull String name, char code) {
		this(name, code, "");
	}
	
	private FtpTransferType(@NonNull String name, char code, @NonNull String parameter) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.code = code;
		this.parameter = Objects.requireNonNull(parameter, "Parameter must not be null");
	}
	
	public @NonNull String getName() {
		return this.name;
	}
	
	public char getCode() {
		return this.code;
	}
	
	public @NonNull String getParameter() {
		return this.parameter;
	}
}
