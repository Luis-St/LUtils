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
import net.luis.utils.data.codec.exception.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class BooleanCodec implements StringableCodec<Boolean> {
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		if (StringUtils.isBlank(value)) {
			return ParserCache.invalid("Unable to decode a null, empty or blank value");
		}
		if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
			return ParserCache.invalid("Value is not a boolean: " + value);
		}
		return ParserCache.cast(Boolean.parseBoolean(value.toLowerCase()));
	}
	
	@Override
	public @NotNull <X> Boolean decode(@NotNull ParserCache<X> cache) {
		if (!cache.isValid()) {
			throw new DecoderException("Invalid cache provided: " + cache);
		}
		return cache.cast();
	}
	
	@Override
	public @NotNull Boolean getDefaultValue() {
		return false;
	}
}
