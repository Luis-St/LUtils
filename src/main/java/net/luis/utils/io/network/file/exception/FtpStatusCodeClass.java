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

package net.luis.utils.io.network.file.exception;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public enum FtpStatusCodeClass {
	
	/**
	 * 1xx: Positive Preliminary reply
	 */
	_1XX("Positive Preliminary Reply", 1),
	/**
	 * 2xx: Positive Completion reply
	 */
	_2XX("Positive Completion Reply", 2),
	/**
	 * 3xx: Positive Intermediate reply
	 */
	_3XX("Positive Intermediate Reply", 3),
	/**
	 * 4xx: Transient Negative Completion reply
	 */
	_4XX("Transient Negative Completion Reply", 4),
	/**
	 * 5xx: Permanent Negative Completion reply
	 */
	_5XX("Permanent Negative Completion Reply", 5),
	/**
	 * 6xx: Protected reply
	 */
	_6XX("Protected Reply", 6);
	
	private final String name;
	private final int code;
	
	FtpStatusCodeClass(@NotNull String name, int code) {
		this.name = Objects.requireNonNull(name, "Name must not be null");
		this.code = code;
	}
	
	public @NotNull String getName() {
		return this.name;
	}
	
	public int getCode() {
		return this.code;
	}
}
