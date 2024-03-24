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

package net.luis.utils.data.codec.primitive;

import net.luis.utils.data.codec.ParserCache;
import net.luis.utils.data.codec.StringableCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class CharacterCodec implements StringableCodec<Character> {
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		if (value == null) {
			return ParserCache.invalid("Unable to decode a null value");
		}
		if (value.length() != 1) {
			return ParserCache.invalid("Unable to decode value '" + value + "' to a character, not a single character");
		}
		return ParserCache.cast(value.charAt(0));
	}
	
	@Override
	public @NotNull Character getDefaultValue() {
		return ' ';
	}
}
