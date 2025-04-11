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

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Luis-St
 *
 */

public class StringTokenDefinition extends TokenDefinition {
	
	private final String fixedString;
	private final boolean ignoreCase;
	
	public StringTokenDefinition(@NotNull String fixedString, boolean ignoreCase) {
		super(fixedString);
		this.fixedString = fixedString;
		this.ignoreCase = ignoreCase;
	}
	
	@Override
	public boolean isSingleChar() {
		return false;
	}
	
	@Override
	public char getSingleChar() {
		throw new UnsupportedOperationException("This token definition is not a single character token.");
	}
	
	@Override
	public boolean matches(@NotNull String word) {
		if (this.ignoreCase) {
			return word.equalsIgnoreCase(this.fixedString);
		} else {
			return word.equals(this.fixedString);
		}
	}
}
