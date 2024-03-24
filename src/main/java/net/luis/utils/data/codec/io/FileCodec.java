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
import net.luis.utils.data.codec.exception.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public class FileCodec implements Codec<File> {
	
	private static final Pattern FILE_PATTERN = Pattern.compile(
		"^(?<drive>[a-z]:)?(?<path>(?:/?(?:[\\w §$%&=~+;,!#(){}\\[\\]-]+|[\\w §$%&=~+;,!#(){}\\[\\]-]+[.][\\w §$%&=~+;,!#(){}\\[\\]-]+|[.]{1,2})/)*)?(?<file>[\\w §$%&=~+;,.!#(){}\\[\\]-]+)?$", Pattern.CASE_INSENSITIVE
	);
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		if (StringUtils.isEmpty(value)) {
			return ParserCache.invalid("Unable to decode a null, empty or blank value");
		}
		value = value.replace("\\", "/");
		Matcher matcher = FILE_PATTERN.matcher(value);
		if (!matcher.matches()) {
			return ParserCache.invalid("Invalid file path: '" + value + "'");
		}
		if (value.contains("/./")) {
			return ParserCache.invalid("Invalid file path: '" + value + "'");
		}
		return ParserCache.cast(matcher);
	}
	
	@Override
	public @NotNull <X> File decode(@NotNull ParserCache<X> cache) {
		Objects.requireNonNull(cache, "Parser cache must not be null");
		if (!cache.isValid()) {
			throw new DecoderException("Invalid cache provided: " + cache);
		}
		Matcher matcher = cache.cast();
		return new File(matcher.group());
	}
	
	@Override
	public @NotNull String encode(@NotNull File value) {
		return String.valueOf(value).replace("\\", "/");
	}
	
	@Override
	public @NotNull File getDefaultValue() {
		return null;
	}
}
