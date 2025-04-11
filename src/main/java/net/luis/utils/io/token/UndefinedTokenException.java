/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.token;

import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class UndefinedTokenException extends RuntimeException {
	
	public UndefinedTokenException() {}
	
	public UndefinedTokenException(@Nullable String message) {
		super(message);
	}
	
	public UndefinedTokenException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	public UndefinedTokenException(@Nullable Throwable cause) {
		super(cause);
	}
}
