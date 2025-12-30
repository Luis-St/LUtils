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

package net.luis.utils.io.data.yaml.exception;

/**
 * Thrown when an index is out of bounds for a yaml sequence.<br>
 * The exception message will contain the details about the index and bounds.<br>
 *
 * @author Luis-St
 */
public class YamlSequenceIndexOutOfBoundsException extends IndexOutOfBoundsException {
	
	/**
	 * Constructs a new yaml sequence index out of bounds exception with no details.<br>
	 */
	public YamlSequenceIndexOutOfBoundsException() {}
	
	/**
	 * Constructs a new yaml sequence index out of bounds exception with the specified index.<br>
	 * @param index The index that was out of bounds
	 */
	public YamlSequenceIndexOutOfBoundsException(int index) {
		super("Yaml sequence index out of bounds: " + index);
	}
	
	/**
	 * Constructs a new yaml sequence index out of bounds exception with the specified index and size.<br>
	 * @param index The index that was out of bounds
	 * @param size The size of the yaml sequence
	 */
	public YamlSequenceIndexOutOfBoundsException(int index, int size) {
		super("Yaml sequence index out of bounds: " + index + ", size: " + size);
	}
	
	/**
	 * Constructs a new yaml sequence index out of bounds exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public YamlSequenceIndexOutOfBoundsException(String message) {
		super(message);
	}
}
