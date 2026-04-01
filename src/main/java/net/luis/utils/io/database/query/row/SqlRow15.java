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

package net.luis.utils.io.database.query.row;

import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public record SqlRow15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>(
	@NonNull T1 first,
	@NonNull T2 second,
	@NonNull T3 third,
	@NonNull T4 fourth,
	@NonNull T5 fifth,
	@NonNull T6 sixth,
	@NonNull T7 seventh,
	@NonNull T8 eighth,
	@NonNull T9 ninth,
	@NonNull T10 tenth,
	@NonNull T11 eleventh,
	@NonNull T12 twelfth,
	@NonNull T13 thirteenth,
	@NonNull T14 fourteenth,
	@NonNull T15 fifteenth
) {}
