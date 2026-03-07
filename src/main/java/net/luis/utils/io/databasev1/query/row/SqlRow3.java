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

package net.luis.utils.io.databasev1.query.row;

import org.jspecify.annotations.NonNull;

/**
 * A record representing a SQL row with 3 columns.<br>
 *
 * @author Luis-St
 *
 * @param first The value of the first column
 * @param second The value of the second column
 * @param third The value of the third column
 * @param <T1> The type of the first column
 * @param <T2> The type of the second column
 * @param <T3> The type of the third column
 */
public record SqlRow3<T1, T2, T3>(
	@NonNull T1 first,
	@NonNull T2 second,
	@NonNull T3 third
) {}
