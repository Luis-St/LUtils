/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.exception;

import net.luis.utils.util.LazyInitialization;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown when an access or action is attempted on an object that has not been initialized<br>
 * but requires initialization before it can be used.<br>
 *
 * @see LazyInitialization
 *
 * @author Luis-St
 */
public class NotInitializedException extends RuntimeException {
	
	/**
	 * Constructs a new not initialized exception with no details.<br>
	 */
	public NotInitializedException() {}
	
	/**
	 * Constructs a new not initialized exception with the specified message.<br>
	 * @param message The message of the exception
	 */
	public NotInitializedException(@Nullable String message) {
		super(message);
	}
	
	/**
	 * Constructs a new not initialized exception with the specified message and cause.<br>
	 * @param message The message of the exception
	 * @param cause The cause of the exception
	 */
	public NotInitializedException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs a new not initialized exception with the specified cause.<br>
	 * @param cause The cause of the exception
	 */
	public NotInitializedException(@Nullable Throwable cause) {
		super(cause);
	}
}
