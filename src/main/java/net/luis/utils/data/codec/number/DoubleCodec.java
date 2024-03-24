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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Luis-St
 *
 */

public class DoubleCodec extends AbstractNumberCodec<Double> {
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		if (StringUtils.isBlank(value)) {
			return ParserCache.invalid("Unable to decode a null, empty or blank value");
		}
		if (!NumberUtils.isCreatable(value)) {
			return ParserCache.invalid("Unable to decode value ' " + value + "' because it's not a number");
		}
		double f = NumberUtils.createBigDecimal(value).doubleValue();
		if (Double.isInfinite(f) || Double.isNaN(f)) {
			return ParserCache.invalid("Unable to convert value ' " + value + "' to a double because it's out of range");
		} else {
			return ParserCache.cast(value);
		}
	}
	
	@Override
	public @NotNull Double getDefaultValue() {
		return 0.0;
	}
}
