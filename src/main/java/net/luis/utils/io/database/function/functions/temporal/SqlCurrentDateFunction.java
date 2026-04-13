/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.database.function.functions.temporal;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.query.SqlAlias;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

/**
 *
 * @author Luis-St
 *
 */

public record SqlCurrentDateFunction() implements SqlTemporalFunction<LocalDate> {
	
	@Override
	public @NonNull SqlExpression<LocalDate> as(@NonNull SqlAlias alias) {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<LocalDate> ascending() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<LocalDate> descending() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<LocalDate> nullsFirst() {
		return null;
	}
	
	@Override
	public @NonNull SqlFunction<LocalDate> nullsLast() {
		return null;
	}
}
