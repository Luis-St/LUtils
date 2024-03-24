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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

public class BigDecimalCodec extends AbstractNumberCodec<BigDecimal> {
	
	@Override
	public @NotNull <X> ParserCache<X> isValid(@Nullable String value) {
		return this.isValid(value, Function.identity());
	}
	
	@Override
	public @NotNull BigDecimal getDefaultValue() {
		return BigDecimal.ZERO;
	}
}
