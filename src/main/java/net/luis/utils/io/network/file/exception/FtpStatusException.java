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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class FtpStatusException extends FtpException {
	
	private final FtpStatusCode statusCode;
	
	public FtpStatusException(@NonNull FtpStatusCode statusCode) {
		Objects.requireNonNull(statusCode, "Status code must not be null");
		
		super(statusCode.formatMessage());
		this.statusCode = statusCode;
	}
	
	public FtpStatusException(@NonNull FtpStatusCode statusCode, Object @NonNull ... parameter) {
		Objects.requireNonNull(statusCode, "Status code must not be null");
		
		super(statusCode.formatMessage(parameter));
		this.statusCode = statusCode;
	}
	
	public FtpStatusException(@NonNull FtpStatusCode statusCode, @Nullable Throwable cause) {
		Objects.requireNonNull(statusCode, "Status code must not be null");
		
		super(statusCode.formatMessage(), cause);
		this.statusCode = statusCode;
	}
	
	public FtpStatusException(@NonNull FtpStatusCode statusCode, @Nullable Throwable cause, Object @NonNull ... parameter) {
		Objects.requireNonNull(statusCode, "Status code must not be null");
		
		super(statusCode.formatMessage(parameter), cause);
		this.statusCode = statusCode;
	}
	
	public @NonNull FtpStatusCode getStatusCode() {
		return this.statusCode;
	}
}
