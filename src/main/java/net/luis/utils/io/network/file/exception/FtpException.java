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

import org.jspecify.annotations.Nullable;

import java.io.IOException;

/**
 *
 * @author Luis-St
 *
 */

public class FtpException extends IOException {
	
	public FtpException() {}
	
	public FtpException(@Nullable String message) {
		super(message);
	}
	
	public FtpException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public FtpException(@Nullable Throwable cause) {
		super(cause);
	}
}
