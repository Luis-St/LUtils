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

package net.luis.utils.math;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public enum Radix {
	
	BINARY(2, "0b"),
	OCTAL(8, "0"),
	DECIMAL(10, ""),
	HEXADECIMAL(16, "0x");
	
	private final int radix;
	private final String prefix;
	
	Radix(int radix, @NotNull String prefix) {
		if (0 >= radix) {
			throw new IllegalArgumentException("Radix must be greater than 0 but found: " + radix);
		}
		this.radix = radix;
		this.prefix = Objects.requireNonNull(prefix, "Prefix must not be null");
	}
	
	public int getRadix() {
		return this.radix;
	}
	
	public @NotNull String getPrefix() {
		return this.prefix;
	}
	
	//region Object overrides
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	//endregion
}
