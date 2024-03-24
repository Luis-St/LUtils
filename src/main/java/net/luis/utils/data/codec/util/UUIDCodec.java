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

import net.luis.utils.data.codec.ParserCache;
import net.luis.utils.data.codec.StringableCodec;
import net.luis.utils.data.codec.exception.DecoderException;
import net.luis.utils.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public class UUIDCodec implements StringableCodec<UUID> {
	
	private static final Pattern PATTERN = Pattern.compile("^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})$", Pattern.CASE_INSENSITIVE);
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		if (StringUtils.isEmpty(value)) {
			return ParserCache.invalid("Unable to decode a null, empty or blank value");
		}
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.matches()) {
			return ParserCache.invalid("Invalid UUID format: '" + value + "'");
		}
		return ParserCache.cast(matcher.group(1));
	}
	
	@Override
	public @NotNull <X> UUID decode(@NotNull ParserCache<X> cache) {
		Objects.requireNonNull(cache, "Parser cache must not be null");
		if (!cache.isValid()) {
			throw new DecoderException("Invalid cache provided: " + cache);
		}
		return java.util.UUID.fromString(cache.cast());
	}
	
	@Override
	public @NotNull UUID getDefaultValue() {
		return Utils.EMPTY_UUID;
	}
}
