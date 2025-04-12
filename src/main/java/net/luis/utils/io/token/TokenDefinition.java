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

@FunctionalInterface
public interface TokenDefinition {
	
	TokenDefinition WORD = new TokenDefinition() {
		@Override
		public boolean matches(@NotNull String word) {
			return word.length() > 1;
		}
		
		@Override
		public String toString() {
			return "WORD";
		}
	};
	
	static @NotNull TokenDefinition of(char token) {
		return new TokenDefinition() {
			@Override
			public boolean matches(@NotNull String word) {
				return word.length() == 1 && word.charAt(0) == token;
			}
			
			@Override
			public String toString() {
				return String.valueOf(token);
			}
		};
	}
	
	static @NotNull TokenDefinition of(@NotNull String token, boolean equalsIgnoreCase) {
		return new TokenDefinition() {
			@Override
			public boolean matches(@NotNull String word) {
				return equalsIgnoreCase ? word.equalsIgnoreCase(token) : word.equals(token);
			}
			
			@Override
			public String toString() {
				return token;
			}
		};
	}
	
	boolean matches(@NotNull String word);
	
	
}
