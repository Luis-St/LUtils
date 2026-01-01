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

package net.luis.utils.io.data.xml.exception;

import org.jspecify.annotations.Nullable;

import java.util.NoSuchElementException;

/**
 * Thrown to indicate that a xml element does not exist.<br>
 * The exception message will contain the details of the missing element.<br>
 *
 * @author Luis-St
 */
public class NoSuchXmlElementException extends NoSuchElementException {
	
	/**
	 * Constructs a new no such xml element exception with no details.<br>
	 */
	public NoSuchXmlElementException() {}
	
	/**
	 * Constructs a new no such xml element exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public NoSuchXmlElementException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new no such xml element exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NoSuchXmlElementException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new no such xml element exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public NoSuchXmlElementException(@Nullable Throwable cause) {
		super(cause);
	}
}
