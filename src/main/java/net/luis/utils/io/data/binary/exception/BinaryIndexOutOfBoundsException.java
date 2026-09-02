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

package net.luis.utils.io.data.binary.exception;

import org.jspecify.annotations.Nullable;

/**
 * Thrown to indicate that an index of a binary array or a binary struct is out of bounds.<br>
 * This exception is thrown when an index is either negative or greater than or equal to the size of the element.<br>
 * The exception message will contain the index and may the size of the element.<br>
 *
 * @author Luis-St
 */
public class BinaryIndexOutOfBoundsException extends ArrayIndexOutOfBoundsException {
	
	/**
	 * Constructs a new binary index out-of-bounds exception with no details.<br>
	 */
	public BinaryIndexOutOfBoundsException() {}
	
	/**
	 * Constructs a new binary index out-of-bounds exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public BinaryIndexOutOfBoundsException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new binary index out-of-bounds exception with the specified index.<br>
	 * @param index The index that is out of bounds
	 */
	public BinaryIndexOutOfBoundsException(int index) {
		super("Binary index out of bounds: " + index);
	}
	
	/**
	 * Constructs a new binary index out-of-bounds exception with the specified index and size.<br>
	 *
	 * @param index The index that is out of bounds
	 * @param size The size of the element
	 */
	public BinaryIndexOutOfBoundsException(int index, int size) {
		super("Binary index out of bounds: " + index + " of size " + size);
	}
}
