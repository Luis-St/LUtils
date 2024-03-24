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

package net.luis.utils.data.codec.io;

import net.luis.utils.data.codec.Codec;
import net.luis.utils.data.codec.ParserCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 *
 * @author Luis-St
 *
 */

public class URLCodec implements Codec<URL> {
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		return null;
	}
	
	@Override
	public @NotNull String encode(@NotNull URL value) {
		return null;
	}
	
	@Override
	public @NotNull URL getDefaultValue() {
		return null;
	}
}
