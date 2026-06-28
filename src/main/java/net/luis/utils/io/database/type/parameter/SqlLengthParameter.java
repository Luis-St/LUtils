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

package net.luis.utils.io.database.type.parameter;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A {@link SqlParameter} that holds a single length value.<br>
 * It is used to configure parameterized sql types that take a single length argument such as the {@code n} in a {@code VARCHAR(n)}.<br>
 *
 * @see SqlParameter
 *
 * @author Luis-St
 */
public final class SqlLengthParameter implements SqlParameter {
	
	/**
	 * The length of the type.<br>
	 */
	private final int length;
	
	/**
	 * Constructs a new length parameter with the given length.<br>
	 *
	 * @param length The length of the type
	 * @throws IllegalArgumentException If the length is not positive
	 */
	SqlLengthParameter(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length must be positive");
		}
		this.length = length;
	}
	
	/**
	 * Returns the length of this parameter.<br>
	 * @return The length
	 */
	public int length() {
		return this.length;
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SqlLengthParameter that)) return false;
		
		return this.length == that.length;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.length);
	}
	
	@Override
	public @NonNull String toString() {
		return "SqlLengthParameter[length=" + this.length + "]";
	}
	//endregion
}
