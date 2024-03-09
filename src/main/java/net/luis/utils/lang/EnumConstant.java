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

package net.luis.utils.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A record that represents an enum constant.<br>
 *
 * @author Luis-St
 *
 * @param name The name of the enum constant
 * @param ordinal The ordinal of the enum constant
 * @param value The value of the enum constant
 * @param <T> The type of the enum constant
 */
public record EnumConstant<T>(@NotNull String name, int ordinal, @NotNull T value) {
	
	/**
	 * Constructs a new enum constant with the given name, ordinal and value.<br>
	 * @param name The name of the enum constant
	 * @param ordinal The ordinal of the enum constant
	 * @param value The value of the enum constant
	 * @throws NullPointerException If the given name or value is null
	 * @throws IllegalArgumentException If the given ordinal is negative
	 */
	public EnumConstant {
		Objects.requireNonNull(name, "Name must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		if (ordinal < 0) {
			throw new IllegalArgumentException("Ordinal must not be negative");
		}
	}
}
