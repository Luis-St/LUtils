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

package net.luis.utils.data.codec.number;

import net.luis.utils.data.codec.ParserCache;
import net.luis.utils.data.codec.StringableCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

abstract class AbstractNumberCodec<T extends Number> implements StringableCodec<T> {
	
	protected @NotNull <X> ParserCache<X> isValid(@Nullable String value, @NotNull Function<BigDecimal, T> decoder) {
		Objects.requireNonNull(decoder, "Decoder function must not be null");
		if (StringUtils.isBlank(value)) {
			return ParserCache.invalid("Unable to decode a null, empty or blank value");
		}
		if (!NumberUtils.isCreatable(value)) {
			return ParserCache.invalid("Value '" + value + "' is not a valid number");
		}
		try {
			return ParserCache.cast(decoder.apply(NumberUtils.createBigDecimal(value)));
		} catch (Exception e) {
			return ParserCache.invalid("Decoding of value ' " + value + "' failed: " + e.getMessage());
		}
	}
}
