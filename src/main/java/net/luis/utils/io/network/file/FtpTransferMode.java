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

public enum FtpTransferMode {
	
	/**
	 * Stream mode ({@code MODE S}), the data is sent as a plain byte stream terminated by closing the connection.<br>
	 */
	STREAM("Stream", 'S'),
	/**
	 * Block mode ({@code MODE B}), the data is sent as a series of blocks, each preceded by a descriptor header.<br>
	 */
	BLOCK("Block", 'B'),
	/**
	 * Compressed mode ({@code MODE C}), the data is sent run-length encoded to compress repeated bytes and filler.<br>
	 */
	COMPRESSED("Compressed", 'C');
	
	private final String name;
	private final char code;
	
	private FtpTransferMode(@NonNull String name, char code) {
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
