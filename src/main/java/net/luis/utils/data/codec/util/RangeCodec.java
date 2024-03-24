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

import net.luis.utils.data.codec.*;
import net.luis.utils.data.codec.exception.DecoderException;
import net.luis.utils.util.Range;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public class RangeCodec implements StringableCodec<Range> {
	
	private static final Pattern PATTERN = Pattern.compile("^\\[\\s*(-?(\\d+[.,])?\\d+)\\s*\\.\\.\\s*(-?(\\d+[.,])?\\d+)\\s*]$");
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		if (StringUtils.isEmpty(value)) {
			return ParserCache.invalid("Unable to decode a null, empty or blank value");
		}
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.matches()) {
			return ParserCache.invalid("Invalid Range format: '" + value + "'");
		}
		return ParserCache.cast(matcher);
	}
	
	@Override
	public @NotNull <X> Range decode(@NotNull ParserCache<X> cache) {
		if (!cache.isValid()) {
			throw new DecoderException("Invalid cache provided: " + cache);
		}
		Matcher matcher = cache.cast();
		double min = Codec.DOUBLE.decodeDirect(matcher.group(1));
		double max = Codec.DOUBLE.decodeDirect(matcher.group(3));
		return Range.of(min, max);
	}
	
	@Override
	public @NotNull Range getDefaultValue() {
		return Range.EMPTY;
	}
}
