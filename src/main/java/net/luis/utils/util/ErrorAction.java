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

package net.luis.utils.util;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an action to be taken when an error or exception occurs.<br>
 * Each action defined in this enum has a different method to handle the error.<br>
 *
 * @author Luis-St
 */
public enum ErrorAction {
	
	/**
	 * Ignores the error or exception.<br>
	 */
	IGNORE,
	/**
	 * Throws the error or exception using {@link Utils#throwSneaky(Throwable)}.<br>
	 */
	THROW {
		@Override
		public void handle(@NotNull Throwable e) {
			Utils.throwSneaky(e);
		}
	};
	
	/**
	 * Handles the error or exception.<br>
	 * @param e The throwable to handle
	 * @throws NullPointerException If the throwable is null (optional)
	 */
	public void handle(@NotNull Throwable e) {}
}
