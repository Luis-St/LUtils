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

package net.luis.utils.data.codec.util;

import net.luis.utils.data.codec.Codec;
import net.luis.utils.data.codec.ParserCache;
import net.luis.utils.util.Chance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class ChanceCodec implements Codec<Chance> {
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull Chance value) {
		return null;
	}
	
	@Override
	public @NotNull Chance getDefaultValue() {
		return null;
	}
}
