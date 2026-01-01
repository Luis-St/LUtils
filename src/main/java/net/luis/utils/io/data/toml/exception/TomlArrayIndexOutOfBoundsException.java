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

package net.luis.utils.io.data.toml.exception;

import org.jspecify.annotations.Nullable;

/**
 * Thrown when an index is out of bounds for a TOML array.<br>
 * The exception message will contain the details about the invalid index.<br>
 *
 * @author Luis-St
 */
public class TomlArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
	
	/**
	 * Constructs a new TOML array index out of bounds exception with no details.<br>
	 */
	public TomlArrayIndexOutOfBoundsException() {}
	
	/**
	 * Constructs a new TOML array index out of bounds exception with the specified index.<br>
	 * @param index The invalid index
	 */
	public TomlArrayIndexOutOfBoundsException(int index) {
		super(index);
	}
	
	/**
	 * Constructs a new TOML array index out of bounds exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public TomlArrayIndexOutOfBoundsException(@Nullable String message) {
		super(message);
	}
}
