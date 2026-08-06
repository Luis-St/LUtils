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

public enum FtpDataChannelProtection {
	
	/**
	 * Data channel is not protected.<br>
	 */
	CLEAR("Clear", 'C'),
	/**
	 * Data channel is unprotected, only the integrity of the data channel is protected.<br>
	 */
	SAFE("Safe", 'S'),
	/**
	 * Fully protected data channel, both the integrity and the privacy of the data channel are protected.<br>
	 */
	PRIVATE("Private", 'P');
	
	
	private final String name;
	private final char code;
	
	private FtpDataChannelProtection(@NonNull String name, char code) {
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
